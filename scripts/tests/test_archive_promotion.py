import importlib.util
import json
import pathlib
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts" / "verify_archive_promotion.py"


def load_module():
    spec = importlib.util.spec_from_file_location("verify_archive_promotion", SCRIPT)
    module = importlib.util.module_from_spec(spec)
    assert spec.loader is not None
    spec.loader.exec_module(module)
    return module


class ArchivePromotionContractTest(unittest.TestCase):
    def test_repository_contract_is_aligned(self):
        module = load_module()
        self.assertEqual([], module.verify())

    def test_contract_is_atomic_non_destructive_and_binary_safe(self):
        contract = json.loads(
            (ROOT / "product" / "archive-promotion.json").read_text(encoding="utf-8")
        )
        self.assertEqual("REQUIRE_EMPTY", contract["target_policy"])
        self.assertEqual("base64", contract["content_encoding"])
        self.assertEqual(256, contract["max_files"])
        self.assertEqual(2 * 1024 * 1024, contract["max_file_bytes"])
        self.assertEqual(10 * 1024 * 1024, contract["max_total_bytes"])
        self.assertEqual([".kide/"], contract["forbidden_hosted_prefixes"])

    def test_post_promotion_services_cover_engineering_workflow(self):
        contract = json.loads(
            (ROOT / "product" / "archive-promotion.json").read_text(encoding="utf-8")
        )
        self.assertEqual(
            {
                "xtext-lsp",
                "glsp",
                "collaboration",
                "synthesis",
                "reconfiguration",
                "generation",
            },
            set(contract["post_promotion_services"]),
        )


if __name__ == "__main__":
    unittest.main()
