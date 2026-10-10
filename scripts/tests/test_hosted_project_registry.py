import importlib.util
import json
import pathlib
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts" / "verify_hosted_project_registry.py"


def load_module():
    spec = importlib.util.spec_from_file_location(
        "verify_hosted_project_registry", SCRIPT
    )
    module = importlib.util.module_from_spec(spec)
    assert spec.loader is not None
    spec.loader.exec_module(module)
    return module


class HostedProjectRegistryContractTest(unittest.TestCase):
    def test_repository_contract_is_aligned(self):
        module = load_module()
        self.assertEqual([], module.verify())

    def test_pr87_keeps_creation_disabled_until_api_is_registry_aware(self):
        contract = json.loads(
            (ROOT / "product" / "hosted-project-registry.json")
            .read_text(encoding="utf-8")
        )
        self.assertFalse(contract["project_creation_enabled"])
        self.assertTrue(contract["legacy_single_project_fallback"])
        self.assertEqual(
            {"xtext-lsp", "glsp"},
            set(contract["dynamic_consumers"]),
        )

    def test_registry_security_requirements_are_explicit(self):
        contract = json.loads(
            (ROOT / "product" / "hosted-project-registry.json")
            .read_text(encoding="utf-8")
        )
        self.assertEqual(
            {
                "no-symbolic-link-registry-entries",
                "slot-name-matches-project-uuid",
                "unique-project-id",
                "unique-workspace-id",
                "registry-rescanned-on-workspace-resolution",
                "role-binding-scope-must-exist-in-registry",
            },
            set(contract["requirements"]),
        )


if __name__ == "__main__":
    unittest.main()
