"""PR101 regression: readiness must not regress to anonymous-only health."""
import importlib.util
from pathlib import Path
import unittest
from unittest.mock import patch

ROOT = Path(__file__).resolve().parents[2]
spec = importlib.util.spec_from_file_location(
    "authenticated_monitor", ROOT / "scripts/verify_authenticated_monitor.py"
)
monitor = importlib.util.module_from_spec(spec)
spec.loader.exec_module(monitor)


class AuthenticatedMonitorGovernanceTest(unittest.TestCase):
    def test_full_stack_monitor_contract_is_qualifiable(self):
        self.assertEqual([], monitor.verify())

    def test_authenticated_monitor_rejects_missing_lockfile_install(self):
        original = monitor.read
        with patch.object(
            monitor, "read",
            side_effect=lambda name: original(name).replace(
                "npm install --no-audit --no-fund",
                "npm ci --no-audit --no-fund",
            ) if name == ".github/workflows/hosted-runtime-health.yml" else original(name),
        ):
            self.assertTrue(any("impossible npm prerequisite" in e for e in monitor.verify()))

    def test_unconfigured_public_monitor_cannot_appear_green(self):
        original = monitor.read
        with patch.object(
            monitor, "read",
            side_effect=lambda name: original(name).replace(
                'if [[ -z "$API_ORIGIN" || -z "$WEB_ORIGIN" ]]',
                'if [[ -n "$API_ORIGIN" && -n "$WEB_ORIGIN" ]]',
            ) if name == ".github/workflows/hosted-runtime-health.yml" else original(name),
        ):
            self.assertTrue(any("must not report success" in e for e in monitor.verify()))

    def test_deleting_real_xtext_probe_fails_governance(self):
        original = monitor.read
        with patch.object(
            monitor, "read",
            side_effect=lambda name: original(name).replace(
                'verifyGateway(page, cfg.api, auth.idToken, selected.workspaceId, "lsp")',
                '/* removed */'
            ) if name == "web/e2e/live-connectivity.spec.ts" else original(name),
        ):
            self.assertTrue(any("lsp" in error for error in monitor.verify()))

    def test_swapping_real_collaboration_probe_for_mock_is_rejected(self):
        original = monitor.read
        with patch.object(
            monitor, "read",
            side_effect=lambda name: original(name) + '\npage.route("/mock")'
            if name == "web/e2e/live-connectivity.spec.ts" else original(name),
        ):
            self.assertTrue(any("forbidden bypass" in error for error in monitor.verify()))


if __name__ == "__main__":
    unittest.main()
