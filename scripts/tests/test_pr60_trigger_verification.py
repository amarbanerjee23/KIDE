from __future__ import annotations

import os
from pathlib import Path
import subprocess
import tempfile
import textwrap
import unittest


ROOT = Path(__file__).resolve().parents[2]
VERIFIER = ROOT / "deploy/gcp/verify-firebase-trigger.sh"


class FirebaseTriggerVerificationBehaviorTest(unittest.TestCase):
    def run_verifier(self, *, project_id="kide-project", trigger_name="kide-trigger",
                     trigger_region="asia-south1", firebase_project_id="kide-project",
                     firebase_api_key="api-key", firebase_admin_uid="admin-uid",
                     persisted_project_id=None, persisted_api_key=None,
                     persisted_admin_uid=None):
        with tempfile.TemporaryDirectory() as temp_dir:
            bin_dir = Path(temp_dir)
            fake_gcloud = bin_dir / "gcloud"
            fake_gcloud.write_text(
                textwrap.dedent(
                    """\
                    #!/usr/bin/env bash
                    set -euo pipefail
                    case "$*" in
                      *"value(substitutions._KIDE_FIREBASE_PROJECT_ID)"*)
                        printf '%s\n' "${FAKE_FIREBASE_PROJECT_ID:-}"
                        ;;
                      *"value(substitutions._KIDE_FIREBASE_API_KEY)"*)
                        printf '%s\n' "${FAKE_FIREBASE_API_KEY:-}"
                        ;;
                      *"value(substitutions._KIDE_FIREBASE_ADMIN_UID)"*)
                        printf '%s\n' "${FAKE_FIREBASE_ADMIN_UID:-}"
                        ;;
                      *)
                        echo "unexpected gcloud invocation: $*" >&2
                        exit 97
                        ;;
                    esac
                    """
                ),
                encoding="utf-8",
            )
            fake_gcloud.chmod(0o755)

            env = os.environ.copy()
            env.update(
                {
                    "PATH": f"{bin_dir}{os.pathsep}{env['PATH']}",
                    "PROJECT_ID": project_id,
                    "TRIGGER_NAME": trigger_name,
                    "TRIGGER_REGION": trigger_region,
                    "KIDE_FIREBASE_PROJECT_ID": firebase_project_id,
                    "KIDE_FIREBASE_API_KEY": firebase_api_key,
                    "KIDE_FIREBASE_ADMIN_UID": firebase_admin_uid,
                    "FAKE_FIREBASE_PROJECT_ID": (
                        firebase_project_id
                        if persisted_project_id is None
                        else persisted_project_id
                    ),
                    "FAKE_FIREBASE_API_KEY": (
                        firebase_api_key
                        if persisted_api_key is None
                        else persisted_api_key
                    ),
                    "FAKE_FIREBASE_ADMIN_UID": (
                        firebase_admin_uid
                        if persisted_admin_uid is None
                        else persisted_admin_uid
                    ),
                }
            )
            return subprocess.run(
                ["bash", str(VERIFIER)],
                cwd=ROOT,
                env=env,
                check=False,
                capture_output=True,
                text=True,
            )

    def test_exact_persisted_firebase_configuration_passes(self):
        result = self.run_verifier()

        self.assertEqual(0, result.returncode, msg=result.stderr)
        self.assertIn(
            "Verified Firebase substitutions on Cloud Build trigger 'kide-trigger'.",
            result.stdout,
        )

    def test_api_key_mismatch_fails_closed_before_deployment(self):
        result = self.run_verifier(persisted_api_key="wrong-key")

        self.assertEqual(2, result.returncode)
        self.assertIn(
            "Cloud Build trigger Firebase configuration verification failed.",
            result.stderr,
        )
        self.assertIn(
            "Substitution _KIDE_FIREBASE_API_KEY was not persisted as expected.",
            result.stderr,
        )
        self.assertIn(
            "The deployment build will not be started with incomplete Firebase substitutions.",
            result.stderr,
        )
        self.assertNotIn("api-key", result.stderr)
        self.assertNotIn("wrong-key", result.stderr)

    def test_missing_persisted_admin_uid_fails_closed_without_leaking_values(self):
        result = self.run_verifier(persisted_admin_uid="")

        self.assertEqual(2, result.returncode)
        self.assertIn("_KIDE_FIREBASE_ADMIN_UID", result.stderr)
        self.assertNotIn("admin-uid", result.stderr)

    def test_required_bootstrap_context_is_enforced(self):
        with tempfile.TemporaryDirectory() as temp_dir:
            env = os.environ.copy()
            env["PATH"] = f"{temp_dir}{os.pathsep}{env['PATH']}"
            env.pop("KIDE_FIREBASE_API_KEY", None)
            env.update(
                {
                    "PROJECT_ID": "kide-project",
                    "TRIGGER_NAME": "kide-trigger",
                    "TRIGGER_REGION": "asia-south1",
                    "KIDE_FIREBASE_PROJECT_ID": "kide-project",
                    "KIDE_FIREBASE_ADMIN_UID": "admin-uid",
                }
            )
            result = subprocess.run(
                ["bash", str(VERIFIER)],
                cwd=ROOT,
                env=env,
                check=False,
                capture_output=True,
                text=True,
            )

        self.assertEqual(2, result.returncode)
        self.assertIn("KIDE_FIREBASE_API_KEY is required", result.stderr)


if __name__ == "__main__":
    unittest.main()
