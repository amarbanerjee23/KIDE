import json
import pathlib
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[2]


class WebLspContractTest(unittest.TestCase):
    def test_generated_language_assets_match_product_registry(self):
        product = json.loads(
            (ROOT / "product" / "languages.json").read_text(encoding="utf-8")
        )
        assets = json.loads(
            (ROOT / "web" / "src" / "generated" / "language-assets.json")
            .read_text(encoding="utf-8")
        )
        self.assertEqual(
            [item["language_id"] for item in product["languages"]],
            [item["language_id"] for item in assets["languages"]],
        )
        self.assertTrue(all(item["keywords"] for item in assets["languages"]))

    def test_browser_never_requires_server_file_paths(self):
        lsp = (ROOT / "web" / "src" / "lspClient.ts").read_text(encoding="utf-8")
        mapper = (
            ROOT
            / "com.kide.languageserver.gateway"
            / "src"
            / "com"
            / "kide"
            / "languageserver"
            / "gateway"
            / "LspWorkspaceUriMapper.java"
        ).read_text(encoding="utf-8")
        self.assertIn('rootUri: "kide-workspace:/"', lsp)
        self.assertNotIn("file:///", lsp)
        self.assertIn('SCHEME = "kide-workspace"', mapper)

    def test_project_contract_exposes_workspace_identity_additively(self):
        schema = (
            ROOT
            / "com.kide.enterprise.api"
            / "src"
            / "com"
            / "kide"
            / "enterprise"
            / "api"
            / "ApiSchemaCatalog.java"
        ).read_text(encoding="utf-8")
        self.assertIn('"workspaceId"', schema)
        required_fragment = 'Set.of("id", "displayName", "revision")'
        self.assertIn(required_fragment, schema)


if __name__ == "__main__":
    unittest.main()
