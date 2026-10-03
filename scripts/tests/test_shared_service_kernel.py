import importlib.util
import pathlib
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts" / "verify_shared_service_kernel.py"


def load_module():
    spec = importlib.util.spec_from_file_location("verify_shared_service_kernel", SCRIPT)
    module = importlib.util.module_from_spec(spec)
    assert spec.loader is not None
    spec.loader.exec_module(module)
    return module


class SharedServiceKernelTest(unittest.TestCase):
    def test_desktop_and_headless_ship_same_kernel(self):
        module = load_module()
        contract = __import__("json").loads(
            (ROOT / "product" / "shared-service-kernel.json").read_text(encoding="utf-8")
        )
        required = set(contract["bundles"])
        self.assertTrue(required <= module.feature_plugins(module.DESKTOP_FEATURE))
        self.assertTrue(required <= module.feature_plugins(module.HEADLESS_FEATURE))

    def test_project_synthesis_is_owned_by_shared_kernel(self):
        module = load_module()
        self.assertTrue(module.SHARED_SYNTHESIS.is_file())
        self.assertFalse(module.SERVER_SYNTHESIS.exists())
        source = module.SHARED_SYNTHESIS.read_text(encoding="utf-8")
        self.assertIn("package com.kide.synthesis;", source)
        self.assertIn("ProjectSynthesisEngine", source)

    def test_server_does_not_instantiate_synthesis_engines(self):
        module = load_module()
        forbidden = ("new ProjectSynthesisEngine(", "new DeterministicSynthesisService(")
        violations = []
        for java in module.SERVER_ROOT.rglob("*.java"):
            source = java.read_text(encoding="utf-8")
            for token in forbidden:
                if token in source:
                    violations.append(f"{java.relative_to(ROOT)}: {token}")
        self.assertEqual([], violations)


if __name__ == "__main__":
    unittest.main()
