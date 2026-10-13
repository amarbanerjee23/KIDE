"""PR106 governance mutation probes: false service readiness must fail CI."""
import importlib.util
from pathlib import Path
import unittest
from unittest.mock import patch

ROOT = Path(__file__).resolve().parents[2]
SPEC = importlib.util.spec_from_file_location(
    "pr106_gate", ROOT / "scripts/verify_pr106_first_run.py"
)
gate = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(gate)


class FirstRunGovernanceTest(unittest.TestCase):
    def test_integrated_web_desktop_gate_is_present(self):
        self.assertEqual([], gate.verify())

    def test_cannot_claim_editor_ready_without_project(self):
        original = gate.read

        def tamper(path):
            source = original(path)
            if path == "web/src/firstRun.ts":
                return source.replace(
                    "state.projectOpen && state.xtextConnected && state.collaborationConnected",
                    "state.xtextConnected || state.collaborationConnected"
                )
            return source

        with patch.object(gate, "read", side_effect=tamper):
            self.assertTrue(any("reactive first-run" in issue for issue in gate.verify()))

    def test_skip_to_editor_accessibility_cannot_be_removed(self):
        original = gate.read

        def tamper(path):
            source = original(path)
            if path == "web/src/App.tsx":
                return source.replace('href="#kide-workbench-editor"', 'href="#"')
            return source

        with patch.object(gate, "read", side_effect=tamper):
            self.assertTrue(any("keyboard skip" in issue for issue in gate.verify()))

    def test_starter_models_must_be_hosted_not_local(self):
        original = gate.read

        def tamper(path):
            source = original(path)
            if path == "web/src/App.tsx":
                return source.replace('source === "remote" && isProductionDslPath(entry.path)',
                                      'source === "archive" && isProductionDslPath(entry.path)')
            return source

        with patch.object(gate, "read", side_effect=tamper):
            self.assertTrue(any("live Web" in issue for issue in gate.verify()))


if __name__ == "__main__":
    unittest.main()
