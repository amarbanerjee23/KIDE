import importlib.util
import pathlib
import unittest
from unittest.mock import patch

ROOT = pathlib.Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts" / "verify_live_acceptance.py"


def load():
    spec = importlib.util.spec_from_file_location("verify_live_acceptance", SCRIPT)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


class LiveAcceptanceGovernanceTest(unittest.TestCase):
    def test_live_suite_is_present_and_isolated(self):
        self.assertEqual([], load().verify())

    def test_mocks_or_skipped_tests_are_rejected(self):
        mod = load()
        original = mod.read

        def mocked_read(path):
            value = original(path)
            if path == "web/e2e/live-hosted.spec.ts":
                return value + "\nawait page.route('**/api/v1/projects', handler);\n"
            return value

        with patch.object(mod, "read", side_effect=mocked_read):
            self.assertTrue(any("must not use" in error for error in mod.verify()))

    def test_automatic_staging_execution_is_rejected(self):
        mod = load()
        original = mod.read

        def mocked_read(path):
            value = original(path)
            if path == ".github/workflows/live-staging-acceptance.yml":
                return value + "\n  pull_request:\n    branches: [main]\n"
            return value

        with patch.object(mod, "read", side_effect=mocked_read):
            self.assertTrue(any("must not auto-run" in error for error in mod.verify()))

    def test_live_suite_cannot_become_default_browser_test(self):
        mod = load()
        original = mod.read

        def mocked_read(path):
            value = original(path)
            if path == "web/playwright.config.ts":
                return value.replace('testIgnore: "**/live-hosted.spec.ts",', "")
            return value

        with patch.object(mod, "read", side_effect=mocked_read):
            self.assertTrue(any("explicitly exclude" in error for error in mod.verify()))


if __name__ == "__main__":
    unittest.main()
