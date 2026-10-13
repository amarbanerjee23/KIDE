"""Mutation tests ensuring PR107's real release gate cannot be silently weakened."""
from __future__ import annotations

import importlib.util
from pathlib import Path
import unittest
from unittest.mock import patch

ROOT = Path(__file__).resolve().parents[2]
SPEC = importlib.util.spec_from_file_location(
    "pr107_governance", ROOT / "scripts/verify_pr107_release_gate.py")
governance = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(governance)


class ReleaseGateGovernanceTest(unittest.TestCase):
    def test_integrated_release_contract(self):
        self.assertEqual([], governance.verify())

    def check_mutation(self, path: str, old: str, new: str, issue: str):
        original = governance.read
        source = original(path)
        self.assertIn(old, source)

        def alter(name: str) -> str:
            content = original(name)
            return content.replace(old, new) if name == path else content

        with patch.object(governance, "read", side_effect=alter):
            self.assertTrue(any(issue in x for x in governance.verify()))

    def test_required_live_gate_cannot_be_removed(self):
        self.check_mutation(
            ".github/workflows/release.yml",
            "needs: verify-live-qualification", "needs: verify-quality-gates",
            "signed stable publication",
        )

    def test_build_release_cannot_read_its_own_needs_output(self):
        self.check_mutation(
            ".github/workflows/release.yml",
            "RELEASE_TAG: ${{ steps.version.outputs.tag }}",
            "RELEASE_TAG: ${{ needs.build-release.outputs.tag }}",
            "build-release",
        )

    def test_operator_environment_must_require_review(self):
        self.check_mutation(
            ".github/workflows/release-candidate-qualification.yml",
            "environment: release-qualification", "environment: unprotected",
            "protected operator acceptance",
        )

    def test_prefer_recent_failure_to_stale_staging_success(self):
        self.check_mutation(
            "scripts/qualify_release_candidate.py",
            'get("conclusion") != "success"', 'get("conclusion") == "failure"',
            "strict release candidate",
        )

    def test_release_only_prunes_after_publication(self):
        self.check_mutation(
            ".github/workflows/release.yml",
            "retain_latest_stable_release.py", "unverified_delete_old_releases.py",
            "signed stable publication",
        )


if __name__ == "__main__":
    unittest.main()
