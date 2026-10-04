import importlib.util
import pathlib
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts" / "verify_runtime_compatibility.py"


def load_module():
    spec = importlib.util.spec_from_file_location("verify_runtime_compatibility", SCRIPT)
    module = importlib.util.module_from_spec(spec)
    assert spec.loader is not None
    spec.loader.exec_module(module)
    return module


class RuntimeCompatibilityContractTest(unittest.TestCase):
    def test_repository_contract_is_aligned(self):
        module = load_module()
        self.assertEqual([], module.verify())

    def test_contract_requires_all_fail_closed_boundaries(self):
        contract = __import__("json").loads(
            (ROOT / "product" / "runtime-compatibility.json").read_text(encoding="utf-8")
        )
        self.assertEqual(
            {
                "api_version",
                "engineering_compatibility_level",
                "project_schema_version",
                "shared_kernel_schema_version",
            },
            set(contract["policy"]["required_exact_match"]),
        )
        self.assertEqual(
            {"product_version", "build_id"},
            set(contract["policy"]["diagnostic_only"]),
        )


if __name__ == "__main__":
    unittest.main()
