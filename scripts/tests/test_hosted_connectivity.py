"""PR92: executable deployment grant policy and fail-closed UID tests."""
import os
import pathlib
import subprocess
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[2]
BINDING_SCRIPT = ROOT / "deploy/gcp/legacy-role-bindings.sh"
PROJECT = "kide:project:11111111-1111-4111-8111-111111111111"
ADMIN = "firebase:kide-eclipse#bootstrap-admin"


def policy(engineers="", **values):
    env = os.environ.copy()
    for key in (
        "KIDE_ROLE_BINDINGS", "KIDE_HOSTED_PROJECTS_ROOT", "KIDE_PRIMARY_PRINCIPAL",
        "KIDE_FIREBASE_PROJECT_ID", "KIDE_FIREBASE_ADMIN_UID", "KIDE_FIREBASE_ENGINEER_UIDS",
    ):
        env.pop(key, None)
    env.update(
        KIDE_FIREBASE_PROJECT_ID="kide-eclipse",
        KIDE_FIREBASE_ADMIN_UID="bootstrap-admin",
        KIDE_FIREBASE_ENGINEER_UIDS=engineers,
        **values,
    )
    return subprocess.run(
        ["bash", str(BINDING_SCRIPT), PROJECT],
        env=env, text=True, capture_output=True, check=False,
    )


class HostedConnectivityTest(unittest.TestCase):
    def test_explicit_engineer_binding_adds_only_approved_uid(self):
        result = policy("engineer-one:engineer_two")
        self.assertEqual(0, result.returncode, result.stderr)
        self.assertEqual(
            [
                f"{ADMIN}|ADMINISTRATOR|{PROJECT}",
                f"firebase:kide-eclipse#engineer-one|ENGINEER|{PROJECT}",
                f"firebase:kide-eclipse#engineer_two|ENGINEER|{PROJECT}",
            ],
            result.stdout.strip().split(";"),
        )
        self.assertNotIn("viewer|", result.stdout)
        self.assertNotIn("anonymous", result.stdout)

    def test_unchanged_legacy_bootstrap_default(self):
        self.assertEqual(f"{ADMIN}|ADMINISTRATOR|{PROJECT}\n", policy().stdout)

    def test_duplicate_admin_does_not_add_extra_binding(self):
        self.assertEqual(f"{ADMIN}|ADMINISTRATOR|{PROJECT}\n",
                         policy("bootstrap-admin").stdout)

    def test_invalid_and_ambiguous_allowlists_fail_closed(self):
        for bad in (
            ":engineer", "engineer:", "engineer::other", "one:one",
            "*", "email@example.com", "space user", "foo|ADMINISTRATOR",
            "foo;bar", "engineering/other", "x" * 129,
            ":".join("uid" + str(i) for i in range(33)),
        ):
            with self.subTest(bad=bad):
                self.assertNotEqual(0, policy(bad).returncode)

    def test_explicit_role_bindings_cannot_be_silently_extended(self):
        custom = f"firebase:kide-eclipse#alice|ENGINEER|{PROJECT}"
        result = policy("bob", KIDE_ROLE_BINDINGS=custom)
        self.assertNotEqual(0, result.returncode)
        self.assertIn("Use KIDE_ROLE_BINDINGS alone", result.stderr)
        result = policy("", KIDE_ROLE_BINDINGS=custom)
        self.assertEqual(custom + "\n", result.stdout)

    def test_registry_mode_refuses_legacy_owner_allowlist(self):
        denied = policy("engineer-one", KIDE_HOSTED_PROJECTS_ROOT="/tmp/hosted")
        self.assertNotEqual(0, denied.returncode)
        self.assertIn("registry-backed", denied.stderr)

    def test_deployment_propagates_explicit_role_allowlist_in_every_build_path(self):
        for file in (
            "cloudbuild.yaml", "deploy/gcp/cloudbuild.yaml",
            "deploy/gcp/cloudbuild-release.yaml",
            "deploy/gcp/cloudbuild-deploy.yaml",
        ):
            with self.subTest(file=file):
                text = (ROOT / file).read_text()
                self.assertIn("_KIDE_FIREBASE_ENGINEER_UIDS:", text)
                self.assertIn("KIDE_FIREBASE_ENGINEER_UIDS=${_KIDE_FIREBASE_ENGINEER_UIDS}", text)
        for file in (
            "deploy/gcp/configure-auto-deploy.sh",
            "deploy/gcp/deploy-cloud-run.sh",
            "deploy/gcp/bootstrap-first-deployment.sh",
        ):
            with self.subTest(file=file):
                self.assertIn("KIDE_FIREBASE_ENGINEER_UIDS", (ROOT / file).read_text())
        self.assertIn("legacy-role-bindings.sh",
                      (ROOT / "deploy/gcp/Dockerfile").read_text())
        self.assertIn("legacy-role-bindings.sh",
                      (ROOT / "deploy/gcp/entrypoint.sh").read_text())


if __name__ == "__main__":
    unittest.main()
