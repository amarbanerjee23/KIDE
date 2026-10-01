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

    def test_browser_text_extensions_match_product_language_registry(self):
        languages = json.loads(
            (ROOT / "product" / "languages.json").read_text(encoding="utf-8")
        )["languages"]
        archive = (ROOT / "web" / "src" / "archive.ts").read_text(
            encoding="utf-8"
        )

        for language in languages:
            extension = language["extension"]
            self.assertIn(
                f'"{extension}"',
                archive,
                f"browser workspace must recognize {language['id']} (*.{extension})",
            )

        self.assertNotIn('"operation"', archive)

    def test_hosted_workspace_exposes_discovery_and_engineering_actions(self):
        app = (ROOT / "web" / "src" / "App.tsx").read_text(encoding="utf-8")
        api = (ROOT / "web" / "src" / "api.ts").read_text(encoding="utf-8")
        editor = (ROOT / "web" / "src" / "MonacoEditor.tsx").read_text(
            encoding="utf-8"
        )
        server = (
            ROOT
            / "com.kide.enterprise.server"
            / "src"
            / "com"
            / "kide"
            / "enterprise"
            / "server"
            / "EnterpriseApiServer.java"
        ).read_text(encoding="utf-8")

        self.assertIn("listModels(projectId", api)
        self.assertIn("createStarterModels(projectId", api)
        self.assertIn("Create starter engineering models", app)
        self.assertIn("Refresh project files", app)
        self.assertIn("Synthesize", app)
        self.assertIn("Diagram", app)
        self.assertIn("Command Palette", app)
        self.assertIn("Quick Open", app)
        self.assertIn("status-bar", app)
        self.assertIn("editor-tabs", app)
        self.assertIn("breadcrumbs", app)
        self.assertIn("bottom-panel", app)
        self.assertNotIn("status-stack", app)

        for command in (
            "Editor: Trigger Completion",
            "Editor: Go to Definition",
            "Editor: Find All References",
            "Editor: Rename Symbol",
            "Editor: Quick Fix",
            "Editor: Format Document",
        ):
            self.assertIn(command, app)

        for option in (
            "minimap",
            "stickyScroll",
            "bracketPairColorization",
            "glyphMargin",
            "codeLens",
            "inlineSuggest",
            "formatOnPaste",
            "formatOnType",
            "mouseWheelZoom",
        ):
            self.assertIn(option, editor)

        self.assertIn('"model.list"', server)
        self.assertIn('"model.starter.create"', server)
        self.assertNotIn(
            "Model listing awaits the repository index phase.",
            server,
        )

    def test_landing_authentication_is_separate_from_engineering_workspace(self):
        app = (ROOT / "web" / "src" / "App.tsx").read_text(encoding="utf-8")
        landing = (ROOT / "web" / "src" / "LandingPage.tsx").read_text(
            encoding="utf-8"
        )

        self.assertIn('window.location.pathname.startsWith("/workspace")', app)
        self.assertIn('"/workspace"', app)
        self.assertIn('if (route === "home")', app)
        self.assertIn("<LandingPage", app)
        self.assertIn("Open Engineering Workspace", landing)
        self.assertIn("Sign in with Firebase", landing)
        self.assertIn("Engineer control software from models to generated artifacts.", landing)

        self.assertNotIn('aria-label="Firebase email"', app)
        self.assertNotIn('aria-label="Firebase password"', app)
        self.assertIn("Authentication required for server engineering", app)
        self.assertIn("Go to sign in", app)
        self.assertIn('onClick={() => navigate("home")}', app)
        self.assertIn('href="/workspace"', landing)

    def test_browser_workbench_uses_standard_ide_chrome(self):
        app = (ROOT / "web" / "src" / "App.tsx").read_text(encoding="utf-8")
        tree = (ROOT / "web" / "src" / "FileTree.tsx").read_text(
            encoding="utf-8"
        )
        picker = (ROOT / "web" / "src" / "QuickPick.tsx").read_text(
            encoding="utf-8"
        )

        for view in ("explorer", "search", "engineering", "collaboration", "settings"):
            self.assertIn(f'"{view}"', app)

        self.assertIn("Ctrl/⌘+Shift+P", app)
        self.assertIn("Ctrl/⌘+P", app)
        self.assertIn("Ctrl/⌘+Shift+F", app)
        self.assertIn("Ctrl/⌘+Shift+M", app)
        self.assertIn("Ctrl/⌘+B", app)
        self.assertIn("Ctrl/⌘+J", app)
        self.assertIn("file-tree-folder", tree)
        self.assertIn("aria-expanded", tree)
        self.assertIn("role=\"dialog\"", picker)
        self.assertIn("role=\"listbox\"", picker)

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
