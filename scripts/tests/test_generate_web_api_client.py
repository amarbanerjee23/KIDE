import importlib.util
import json
import pathlib
import tempfile
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts" / "generate_web_api_client.py"


def load_module():
    spec = importlib.util.spec_from_file_location("generate_web_api_client", SCRIPT)
    module = importlib.util.module_from_spec(spec)
    assert spec.loader is not None
    spec.loader.exec_module(module)
    return module


class GeneratedWebApiClientTest(unittest.TestCase):
    def document(self):
        return {
            "openapi": "3.1.0",
            "info": {"title": "KIDE Enterprise API", "version": "v1"},
            "paths": {
                "/api/v1/health": {
                    "get": {
                        "operationId": "health",
                        "responses": {
                            "200": {
                                "content": {
                                    "application/json": {
                                        "schema": {"$ref": "#/components/schemas/Health"}
                                    }
                                }
                            }
                        }
                    }
                },
                "/api/v1/projects/{projectId}/synthesis": {
                    "post": {
                        "operationId": "synthesize",
                        "parameters": [
                            {
                                "name": "projectId",
                                "in": "path",
                                "required": True,
                                "schema": {"type": "string"},
                            }
                        ],
                        "requestBody": {
                            "required": True,
                            "content": {
                                "application/json": {
                                    "schema": {"$ref": "#/components/schemas/SynthesisRequest"}
                                }
                            },
                        },
                        "responses": {
                            "200": {
                                "content": {
                                    "application/json": {
                                        "schema": {"$ref": "#/components/schemas/SynthesisResult"}
                                    }
                                }
                            }
                        },
                    }
                },
            },
            "components": {"schemas": {}},
        }

    def test_generates_encoded_paths_verbs_and_schema_metadata(self):
        module = load_module()
        generated = module.generate(self.document())
        self.assertIn("health(): GeneratedApiCall", generated)
        self.assertIn('method: "GET"', generated)
        self.assertIn("synthesize(projectId: string): GeneratedApiCall", generated)
        self.assertIn("segment(projectId)", generated)
        self.assertIn("/projects/", generated)
        self.assertIn('requestSchema: "SynthesisRequest"', generated)
        self.assertIn('responseSchema: "SynthesisResult"', generated)

    def test_rejects_path_parameter_contract_drift(self):
        module = load_module()
        document = self.document()
        document["paths"]["/api/v1/projects/{projectId}/synthesis"]["post"]["parameters"] = []
        with self.assertRaises(module.GenerationError):
            module.operation_rows(document)

    def test_generated_output_differs_from_stale_file(self):
        module = load_module()
        with tempfile.TemporaryDirectory() as temp:
            root = pathlib.Path(temp)
            source = root / "openapi.json"
            output = root / "api-v1.ts"
            source.write_text(json.dumps(self.document()), encoding="utf-8")
            output.write_text("stale", encoding="utf-8")
            generated = module.generate(module.load_openapi(source))
            self.assertNotEqual(output.read_text(encoding="utf-8"), generated)


if __name__ == "__main__":
    unittest.main()
