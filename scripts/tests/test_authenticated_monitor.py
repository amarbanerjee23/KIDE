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
