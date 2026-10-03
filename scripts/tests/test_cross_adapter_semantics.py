import copy
import importlib.util
import pathlib
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts" / "verify_cross_adapter_semantics.py"


def load_module():
    spec = importlib.util.spec_from_file_location("verify_cross_adapter_semantics", SCRIPT)
    module = importlib.util.module_from_spec(spec)
    assert spec.loader is not None
    spec.loader.exec_module(module)
    return module


class CrossAdapterSemanticQualificationTest(unittest.TestCase):
    def test_repository_contract_is_complete(self):
        module = load_module()
        self.assertEqual([], module.verify(ROOT))

    def test_semantic_fields_fail_closed(self):
        module = load_module()
        contract = module.load_contract(ROOT)
        mutated = copy.deepcopy(contract)
        mutated["runtime_equality"]["compared_semantics"] = [
            value
            for value in mutated["runtime_equality"]["compared_semantics"]
            if "synthesis" not in value.lower()
        ]
        errors = module.verify(ROOT, mutated)
        self.assertTrue(
            any("synthesis" in error.lower() for error in errors),
            errors,
        )

    def test_adapter_gate_removal_fails_closed(self):
        module = load_module()
        contract = module.load_contract(ROOT)
        mutated = copy.deepcopy(contract)
        mutated["adapter_gates"] = mutated["adapter_gates"][:1]
        errors = module.verify(ROOT, mutated)
        self.assertTrue(
            any("adapter gates" in error.lower() for error in errors),
            errors,
        )

    def test_transport_noise_is_not_semantic_parity(self):
        module = load_module()
        contract = module.load_contract(ROOT)
        mutated = copy.deepcopy(contract)
        mutated["runtime_equality"]["excluded_presentation_or_transport"] = [
            value
            for value in mutated["runtime_equality"]["excluded_presentation_or_transport"]
            if value != "request ids"
        ]
        errors = module.verify(ROOT, mutated)
        self.assertTrue(
            any("exclusions" in error.lower() for error in errors),
            errors,
        )


if __name__ == "__main__":
    unittest.main()
