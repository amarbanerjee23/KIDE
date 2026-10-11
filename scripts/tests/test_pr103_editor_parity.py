import importlib.util
from pathlib import Path
import unittest
from unittest.mock import patch

ROOT = Path(__file__).resolve().parents[2]
SPEC = importlib.util.spec_from_file_location(
    "pr103_editor_gate", ROOT / "scripts/verify_pr103_live_editor_parity.py"
)
gate = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(gate)


class RealEditorParityGovernanceTest(unittest.TestCase):
    def test_all_six_languages_and_two_diagrams_are_mandatory(self):
        self.assertEqual([], gate.verify())

    def test_removing_real_semantic_completion_is_rejected(self):
        original = gate.read

        def modified(path):
            content = original(path)
            if path == "web/e2e/live-editor-parity.ts":
                return content.replace('"textDocument/completion"', '"not-real-completion"')
            return content

        with patch.object(gate, "read", side_effect=modified):
            self.assertTrue(any("completion" in e for e in gate.verify()))

    def test_removing_real_diagram_request_is_rejected(self):
        original = gate.read

        def modified(path):
            content = original(path)
            if path == "web/e2e/live-editor-parity.ts":
                return content.replace('"requestModel"', '"not-model-request"')
            return content

        with patch.object(gate, "read", side_effect=modified):
            self.assertTrue(any("requestModel" in e for e in gate.verify()))

    def test_mocks_are_rejected_even_if_protocol_names_remain(self):
        original = gate.read

        def modified(path):
            content = original(path)
            return content + '\npage.route("**/lsp", mockHandler);' if path == (
                "web/e2e/live-editor-parity.ts"
            ) else content

        with patch.object(gate, "read", side_effect=modified):
            self.assertTrue(any("mock" in e for e in gate.verify()))

    def test_hiding_an_existing_language_from_live_import_is_rejected(self):
        original = gate.read

        def modified(path):
            content = original(path)
            if path == "web/e2e/live-hosted.spec.ts":
                return content.replace("expect(imported.importedCount).toBe(6)",
                                       "expect(imported.importedCount).toBe(4)")
            return content

        with patch.object(gate, "read", side_effect=modified):
            self.assertTrue(any("importedCount" in e for e in gate.verify()))


if __name__ == "__main__":
    unittest.main()
