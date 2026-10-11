"""PR105: automatic shared-workspace authorization remains deliberate and testable."""
from __future__ import annotations

import importlib.util
from pathlib import Path
import unittest
from unittest.mock import patch

ROOT = Path(__file__).resolve().parents[2]
SPEC = importlib.util.spec_from_file_location(
    "shared_firebase_guard", ROOT / "scripts/verify_shared_firebase_access.py"
)
gate = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(gate)


class SharedFirebaseAccessGovernanceTest(unittest.TestCase):
    def test_all_backend_services_and_safe_operator_switch_are_present(self):
        self.assertEqual([], gate.verify())

    def test_privacy_default_cannot_be_opened_silently(self):
        original = gate.read

        def tamper(path):
            value = original(path)
            if path.endswith("SharedFirebaseEngineeringPolicy.java"):
                return value.replace('env.getOrDefault(ENV_KEY, "false")',
                                     'env.getOrDefault(ENV_KEY, "true")')
            return value

        with patch.object(gate, "read", side_effect=tamper):
            self.assertTrue(any("trust boundary" in issue for issue in gate.verify()))

    def test_glsp_cannot_use_a_different_policy(self):
        original = gate.read

        def tamper(path):
            value = original(path)
            if path.endswith("GlspGatewayApplication.java"):
                return value.replace("SharedFirebaseEngineeringPolicy.fromEnvironment(env)",
                                     "SharedFirebaseEngineeringPolicy.disabled()")
            return value

        with patch.object(gate, "read", side_effect=tamper):
            self.assertTrue(any("GLSP policy wiring" in issue for issue in gate.verify()))

    def test_cloud_build_must_not_reset_role_mode_to_private(self):
        original = gate.read

        def tamper(path):
            value = original(path)
            if path == "cloudbuild.yaml":
                return value.replace("KIDE_SHARED_FIREBASE_ENGINEERS=${_KIDE_SHARED_FIREBASE_ENGINEERS}",
                                     "KIDE_SHARED_FIREBASE_ENGINEERS=false")
            return value

        with patch.object(gate, "read", side_effect=tamper):
            self.assertTrue(any("deployment" in issue for issue in gate.verify()))

    def test_operator_consent_cannot_be_removed(self):
        original = gate.read

        def tamper(path):
            value = original(path)
            if path == "deploy/gcp/enable-shared-firebase-access.sh":
                return value.replace("I_UNDERSTAND_ALL_FIREBASE_USERS_CAN_EDIT_ALL_PROJECTS", "NO_CONFIRMATION")
            return value

        with patch.object(gate, "read", side_effect=tamper):
            self.assertTrue(any("operator" in issue for issue in gate.verify()))


if __name__ == "__main__":
    unittest.main()
