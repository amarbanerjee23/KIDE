"""PR111: negative regression tests for verified and private Firebase onboarding."""
from __future__ import annotations
from pathlib import Path
import importlib.util
import unittest
from unittest.mock import patch

ROOT = Path(__file__).resolve().parents[2]
SPEC = importlib.util.spec_from_file_location(
    "verify_pr111_owner_only", ROOT / "scripts/verify_pr111_owner_only.py"
)
guard = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(guard)


class VerifiedOwnerOnlyGovernanceTest(unittest.TestCase):
    def test_complete_owner_only_policy_and_gcp_gates(self):
        self.assertEqual([], guard.verify())

    def test_bypass_email_verification_fails(self):
        original = guard.read
        with patch.object(guard, "read", side_effect=lambda name:
                original(name).replace('config.requireVerifiedEmail()', 'false')
                if name.endswith("FirebaseIdTokenAuthenticator.java")
                else original(name)):
            self.assertTrue(any("verified-email" in error for error in guard.verify()))

    def test_cross_project_sharing_cannot_be_silently_enabled(self):
        original = guard.read
        with patch.object(guard, "read", side_effect=lambda name:
                original(name).replace('"KIDE_SHARED_FIREBASE_ENGINEERS"', '"unsafe-shared"')
                if name.endswith("FirebaseIdTokenConfig.java")
                else original(name)):
            self.assertTrue(any("DENY invariant" in error for error in guard.verify()))

    def test_explicit_deny_wins_over_creator(self):
        original = guard.read
        with patch.object(guard, "read", side_effect=lambda name:
                original(name).replace('if (binding.effect() == BindingEffect.DENY)',
                                       'if (false)')
                if name.endswith("AuthorizationService.java")
                else original(name)):
            self.assertTrue(any("DENY" in error for error in guard.verify()))

    def test_git_push_fuse_mount_must_not_enable_owner_only(self):
        original = guard.read
        with patch.object(guard, "read", side_effect=lambda name:
                original(name).replace(
                    'if [[ "${_KIDE_FIREBASE_OWNER_ONLY_ENGINEERS}" == true ]]',
                    'if [[ false == true ]]')
                if name == "cloudbuild.yaml" else original(name)):
            self.assertTrue(any("legacy Git-push" in e for e in guard.verify()))

    def test_gateway_cannot_disable_verified_check(self):
        original = guard.read
        with patch.object(guard, "read", side_effect=lambda name:
                original(name).replace('FirebaseIdTokenConfig.fromEnvironment(',
                                       'new FirebaseIdTokenConfig(')
                if name.endswith("GlspGatewayApplication.java") else original(name)):
            self.assertTrue(any("owner-only" in e for e in guard.verify()))


if __name__ == "__main__":
    unittest.main()
