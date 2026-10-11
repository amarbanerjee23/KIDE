"""PR100: engineer authorization survives releases and requires operator approval."""
import importlib.util
import json
import pathlib
import unittest
from unittest import mock

ROOT = pathlib.Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "deploy/gcp/firebase-access.py"
spec = importlib.util.spec_from_file_location("firebase_access", SCRIPT)
access = importlib.util.module_from_spec(spec)
spec.loader.exec_module(access)


def service(project="kide-eclipse", engineers="alice:bob", hosted=""):
    env = [
        {"name": "KIDE_FIREBASE_PROJECT_ID", "value": project},
        {"name": "KIDE_FIREBASE_ENGINEER_UIDS", "value": engineers},
    ]
    if hosted:
        env.append({"name": "KIDE_HOSTED_PROJECTS_ROOT", "value": hosted})
    return {"spec": {"template": {"spec": {"containers": [{"env": env}]}}}}


class FirebaseAccessTest(unittest.TestCase):
    def test_extract_cloud_run_environment(self):
        self.assertEqual(
            "alice:bob", access.environment(service())["KIDE_FIREBASE_ENGINEER_UIDS"]
        )

    def test_merge_existing_grants_is_additive_and_idempotent(self):
        self.assertEqual("alice:bob:carol", access.merge("alice:bob", "bob:carol", "alice"))
        self.assertEqual("alice:bob:carol", access.merge("alice", "bob", "carol"))
        self.assertEqual("firebase-uid", access.merge("", "", "firebase-uid"))

    def test_invalid_grants_fail_closed(self):
        for value in (":alice", "alice:", "alice::bob", "alice:alice", "*",
                      "email@example.com", "uid/evil", "x" * 129,
                      ":".join(f"uid{i}" for i in range(33))):
            with self.subTest(value=value):
                with self.assertRaises(ValueError):
                    access.parse_uids(value)
        with self.assertRaises(ValueError):
            access.merge("alice", "", "bob|ADMINISTRATOR")

    def test_requires_all_previously_granted_uids(self):
        access.check_preserved("alice:bob", "bob:alice:carol")
        with self.assertRaisesRegex(ValueError, "Refusing deployment"):
            access.check_preserved("alice:bob", "alice")
        with self.assertRaisesRegex(ValueError, "Refusing deployment"):
            access.check_preserved("alice", "")
        access.check_preserved("", "")

    def test_inspection_failures_do_not_look_like_fresh_install(self):
        result = mock.Mock(returncode=1, stderr="PERMISSION_DENIED", stdout="")
        with mock.patch.object(access.subprocess, "run", return_value=result):
            with self.assertRaisesRegex(ValueError, "deployment blocked"):
                access.describe_service("p", "asia-south1", "kide")

        not_found = mock.Mock(returncode=1, stderr="NOT_FOUND", stdout="")
        with mock.patch.object(access.subprocess, "run", return_value=not_found):
            self.assertIsNone(access.describe_service("p", "asia-south1", "kide"))

        exists = mock.Mock(returncode=0, stderr="", stdout=json.dumps(service()))
        with mock.patch.object(access.subprocess, "run", return_value=exists):
            self.assertEqual(service(), access.describe_service("p", "asia-south1", "kide"))

    def test_all_cloud_build_paths_protect_prior_grants(self):
        for path in ("cloudbuild.yaml", "deploy/gcp/cloudbuild.yaml",
                     "deploy/gcp/cloudbuild-deploy.yaml",
                     "deploy/gcp/cloudbuild-release.yaml"):
            with self.subTest(path=path):
                data = (ROOT / path).read_text()
                self.assertIn("firebase-access.py protect-deploy", data)
                self.assertLess(data.index("firebase-access.py protect-deploy"),
                                data.index('gcloud run deploy "${_KIDE_SERVICE_NAME}"'))
        deploy_script = (ROOT / "deploy/gcp/configure-auto-deploy.sh").read_text()
        self.assertIn("! -v KIDE_FIREBASE_ENGINEER_UIDS", deploy_script)
        self.assertIn("value(substitutions._KIDE_FIREBASE_ENGINEER_UIDS)", deploy_script)
        one_time = (ROOT / "deploy/gcp/grant-firebase-engineer.sh").read_text()
        self.assertIn("gcloud builds triggers update", one_time)
        self.assertIn("gcloud run services update", one_time)

    def test_browser_retry_requires_authenticated_denied_session(self):
        app = (ROOT / "web/src/App.tsx").read_text()
        self.assertIn("authorizationRetryRef.current = true", app)
        self.assertIn("authorizationRetryRef.current = false", app)
        self.assertIn("!firebaseAuth.user || projectRef.current", app)
        self.assertIn("connectRef.current(true)", app)


if __name__ == "__main__":
    unittest.main()
