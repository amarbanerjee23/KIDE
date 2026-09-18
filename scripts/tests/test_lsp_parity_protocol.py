import sys
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / "scripts"))

import qualify_lsp_parity  # noqa: E402


class LspParityProtocolHelpersTest(unittest.TestCase):
    def test_position_of_is_zero_based(self):
        text = "first\nDevice second\n"
        self.assertEqual(
            {"line": 1, "character": 0},
            qualify_lsp_parity.position_of(text, "Device"),
        )

    def test_completion_items_accepts_both_lsp_shapes(self):
        items = [{"label": "A"}]
        self.assertEqual(items, qualify_lsp_parity.completion_items(items))
        self.assertEqual(items, qualify_lsp_parity.completion_items({"items": items}))

    def test_symbol_names_walks_hierarchical_symbols(self):
        result = [{"name": "Root", "children": [{"name": "Child"}]}]
        self.assertEqual({"Root", "Child"}, qualify_lsp_parity.symbol_names(result))

    def test_definition_uris_supports_location_and_location_link(self):
        result = [
            {"uri": "file:///a.cap"},
            {"targetUri": "file:///b.mncspec"},
        ]
        self.assertEqual(
            {"file:///a.cap", "file:///b.mncspec"},
            qualify_lsp_parity.definition_uris(result),
        )

    def test_workspace_edit_uris_supports_changes_and_document_changes(self):
        result = {
            "changes": {"file:///a.cap": []},
            "documentChanges": [
                {"textDocument": {"uri": "file:///b.activity"}, "edits": []}
            ],
        }
        self.assertEqual(
            {"file:///a.cap", "file:///b.activity"},
            qualify_lsp_parity.workspace_edit_uris(result),
        )

    def test_capability_enabled_rejects_absent_and_false(self):
        self.assertFalse(qualify_lsp_parity.capability_enabled({}, "renameProvider"))
        self.assertFalse(
            qualify_lsp_parity.capability_enabled(
                {"renameProvider": False}, "renameProvider"
            )
        )
        self.assertTrue(
            qualify_lsp_parity.capability_enabled(
                {"renameProvider": {"prepareProvider": True}}, "renameProvider"
            )
        )


if __name__ == "__main__":
    unittest.main()
