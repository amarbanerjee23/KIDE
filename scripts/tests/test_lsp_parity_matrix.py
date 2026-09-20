import sys
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / "scripts"))

import verify_lsp_parity_matrix  # noqa: E402


class LspParityMatrixTest(unittest.TestCase):
    def test_matrix_matches_product_registry(self):
        self.assertEqual([], verify_lsp_parity_matrix.verify(ROOT))

    def test_required_feature_set_is_frozen(self):
        matrix = verify_lsp_parity_matrix.load_json(ROOT / "product" / "lsp-capabilities.json")
        feature_ids = {item["id"] for item in matrix["features"]}
        self.assertEqual(verify_lsp_parity_matrix.REQUIRED_FEATURES, feature_ids)

    def test_every_language_has_rename_and_symbol_probes(self):
        matrix = verify_lsp_parity_matrix.load_json(ROOT / "product" / "lsp-capabilities.json")
        for language_id, probe in matrix["languages"].items():
            self.assertTrue(probe["symbol"], language_id)
            self.assertTrue(probe["rename_targets"], language_id)

    def test_mnc_symbol_contract_uses_qualified_xtext_name(self):
        matrix = verify_lsp_parity_matrix.load_json(ROOT / "product" / "lsp-capabilities.json")
        probe = matrix["languages"]["mnc"]
        self.assertEqual("Device", probe["symbol"])
        self.assertEqual("Golden.Device", probe["document_symbol"])
        self.assertEqual("Golden.Device", probe["workspace_symbol"])


if __name__ == "__main__":
    unittest.main()
