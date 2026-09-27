from pathlib import Path
import importlib.util
import os
import stat
import tempfile
import unittest
from unittest import mock


ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts" / "bootstrap_firebase_auth.py"


def load_module():
    spec = importlib.util.spec_from_file_location("kide_firebase_bootstrap", SCRIPT)
    module = importlib.util.module_from_spec(spec)
    assert spec.loader is not None
    spec.loader.exec_module(module)
    return module


class FirebaseBootstrapTest(unittest.TestCase):
    def test_script_compiles(self):
        source = SCRIPT.read_text(encoding="utf-8")
        compile(source, str(SCRIPT), "exec")

    def test_output_contains_only_non_secret_deployment_values(self):
        module = load_module()
        with tempfile.TemporaryDirectory() as temp:
            output = Path(temp) / "firebase.env"
            module.write_output(
                output,
                "kide-eclipse",
                "public-web-api-key",
                "uid-123",
            )

            content = output.read_text(encoding="utf-8")
            self.assertIn("KIDE_FIREBASE_PROJECT_ID=kide-eclipse", content)
            self.assertIn("KIDE_FIREBASE_API_KEY=public-web-api-key", content)
            self.assertIn("KIDE_FIREBASE_ADMIN_UID=uid-123", content)
            self.assertNotIn("PASSWORD", content.upper())
            self.assertEqual(
                stat.S_IMODE(output.stat().st_mode),
                0o600,
            )

    def test_configured_admin_uid_avoids_password_prompt(self):
        module = load_module()
        with mock.patch.dict(
            os.environ,
            {"KIDE_FIREBASE_ADMIN_UID": "uid-configured"},
            clear=False,
        ):
            with mock.patch.object(module, "password_from_operator") as password:
                uid = module.ensure_admin_uid("public-api-key")
        self.assertEqual(uid, "uid-configured")
        password.assert_not_called()

    def test_existing_admin_is_signed_in_and_uid_is_reused(self):
        module = load_module()
        with mock.patch.dict(
            os.environ,
            {
                "KIDE_FIREBASE_ADMIN_UID": "",
                "KIDE_FIREBASE_ADMIN_EMAIL": "admin@example.test",
            },
            clear=False,
        ):
            with mock.patch.object(
                module,
                "password_from_operator",
                return_value="correct-password",
            ), mock.patch.object(
                module,
                "public_auth_request",
                return_value=({"localId": "uid-existing"}, None),
            ) as auth:
                uid = module.ensure_admin_uid("public-api-key")

        self.assertEqual(uid, "uid-existing")
        self.assertEqual(auth.call_args.args[0], "signInWithPassword")

    def test_missing_admin_is_created_and_uid_is_captured(self):
        module = load_module()
        with mock.patch.dict(
            os.environ,
            {
                "KIDE_FIREBASE_ADMIN_UID": "",
                "KIDE_FIREBASE_ADMIN_EMAIL": "admin@example.test",
            },
            clear=False,
        ):
            with mock.patch.object(
                module,
                "password_from_operator",
                return_value="new-password",
            ), mock.patch.object(
                module,
                "public_auth_request",
                side_effect=[
                    (None, "EMAIL_NOT_FOUND"),
                    ({"localId": "uid-created"}, None),
                ],
            ) as auth:
                uid = module.ensure_admin_uid("public-api-key")

        self.assertEqual(uid, "uid-created")
        self.assertEqual(
            [call.args[0] for call in auth.call_args_list],
            ["signInWithPassword", "signUp"],
        )

    def test_api_enable_permission_error_is_actionable(self):
        module = load_module()
        with mock.patch.object(
            module,
            "run",
            side_effect=module.BootstrapError("permission denied"),
        ):
            with self.assertRaisesRegex(
                module.BootstrapError,
                "serviceusage.services.enable",
            ):
                module.ensure_apis("kide-eclipse")


if __name__ == "__main__":
    unittest.main()
