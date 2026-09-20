import json
import pathlib
import re
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[2]


class WebWorkspaceContractTest(unittest.TestCase):
    def test_manifest_uses_exact_versions_and_required_gates(self):
        manifest = json.loads(
            (ROOT / "web" / "package.json").read_text(encoding="utf-8")
        )
        exact = re.compile(r"^\d+\.\d+\.\d+$")
        for group in ("dependencies", "devDependencies"):
            for name, version in manifest[group].items():
                self.assertRegex(
                    version, exact, f"{name} must use an exact version"
                )
        self.assertEqual("vitest run", manifest["scripts"]["test"])
        self.assertEqual(
            "tsc -b && vite build", manifest["scripts"]["build"]
        )
        self.assertEqual(
            "playwright test", manifest["scripts"]["test:e2e"]
        )

    def test_browser_is_a_separate_client_of_shared_semantics(self):
        app = (ROOT / "web" / "src" / "App.tsx").read_text(
            encoding="utf-8"
        )
        api = (ROOT / "web" / "src" / "api.ts").read_text(
            encoding="utf-8"
        )
        docs = (
            ROOT / "docs" / "web-workspace-foundation.md"
        ).read_text(encoding="utf-8")
        workflow = (
            ROOT / ".github" / "workflows" / "build.yml"
        ).read_text(encoding="utf-8")

        self.assertIn("/api/v1", api)
        self.assertNotIn("parser", app.lower())
        self.assertIn(
            "does **not** implement a TypeScript parser", docs
        )
        self.assertIn("npm run test", workflow)
        self.assertIn("npm run build", workflow)
        self.assertIn("npm run test:e2e", workflow)


if __name__ == "__main__":
    unittest.main()
