import pathlib
import re
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[2]


class GeneratedWebApiWiringTest(unittest.TestCase):
    def test_web_client_does_not_own_routes_or_verbs(self):
        source = (ROOT / "web" / "src" / "api.ts").read_text(encoding="utf-8")
        self.assertIn('from "./generated/api-v1"', source)
        self.assertNotIn('"/projects', source)
        self.assertNotIn(chr(96) + "/projects", source)
        self.assertNotRegex(source, r'method:\s*"(?:GET|POST|PUT|DELETE)"')
        self.assertIn("method: call.method", source)

    def test_generated_operations_cover_java_contract_registry(self):
        registry = (
            ROOT
            / "com.kide.enterprise.api"
            / "src"
            / "com"
            / "kide"
            / "enterprise"
            / "api"
            / "ApiContractRegistry.java"
        ).read_text(encoding="utf-8")
        generated = (
            ROOT / "web" / "src" / "generated" / "api-v1.ts"
        ).read_text(encoding="utf-8")

        expected = set(
            re.findall(r'new ApiOperation\("([A-Za-z][A-Za-z0-9]*)"', registry)
        )
        actual = set(
            re.findall(
                r"^\s{2}([A-Za-z][A-Za-z0-9]*)\([^)]*\): GeneratedApiCall \{",
                generated,
                re.MULTILINE,
            )
        )
        self.assertEqual(expected, actual)
        self.assertGreaterEqual(len(actual), 30)

    def test_packaged_openapi_is_the_ci_drift_source(self):
        selfcheck = (
            ROOT
            / "com.kide.enterprise.api"
            / "src"
            / "com"
            / "kide"
            / "enterprise"
            / "api"
            / "ApiContractSelfCheckApplication.java"
        ).read_text(encoding="utf-8")
        smoke = (ROOT / "scripts" / "smoke_api_contract.py").read_text(encoding="utf-8")
        workflow = (ROOT / ".github" / "workflows" / "build.yml").read_text(encoding="utf-8")

        self.assertIn("--openapi-output", selfcheck)
        self.assertIn("OpenApiV1.generateJson()", selfcheck)
        self.assertIn("--openapi-output", smoke)
        self.assertIn("qualification/openapi-v1.json", workflow)
        self.assertIn("scripts/generate_web_api_client.py", workflow)
        self.assertIn("--check", workflow)


if __name__ == "__main__":
    unittest.main()
