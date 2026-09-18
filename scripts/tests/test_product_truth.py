import sys
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / "scripts"))

import verify_product_truth  # noqa: E402


class ProductTruthTest(unittest.TestCase):
    def test_repository_product_truth_is_consistent(self):
        self.assertEqual([], verify_product_truth.verify_repository(ROOT))

    def test_registry_is_exactly_five_unique_production_languages(self):
        languages = verify_product_truth.load_registry(ROOT)
        self.assertEqual(5, len(languages))
        self.assertEqual(5, len({item["extension"] for item in languages}))
        self.assertEqual(5, len({item["language_id"] for item in languages}))

    def test_readme_block_is_deterministic(self):
        languages = verify_product_truth.load_registry(ROOT)
        block = verify_product_truth.expected_readme_block(languages)
        self.assertEqual(1, block.count("<!-- KIDE-LANGUAGES:START -->"))
        self.assertEqual(1, block.count("<!-- KIDE-LANGUAGES:END -->"))
        for language in languages:
            self.assertIn(f"`.{language['extension']}`", block)


if __name__ == "__main__":
    unittest.main()
