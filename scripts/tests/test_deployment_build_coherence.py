import importlib.util
import pathlib
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts" / "verify_deployment_build_coherence.py"


def load_module():
    spec = importlib.util.spec_from_file_location("verify_deployment_build_coherence", SCRIPT)
    module = importlib.util.module_from_spec(spec)
    assert spec.loader is not None
    spec.loader.exec_module(module)
    return module


class DeploymentBuildCoherenceTest(unittest.TestCase):
    def test_repository_deployment_contract_is_coherent(self):
        module = load_module()
        self.assertEqual([], module.verify())

    def test_web_image_exposes_machine_readable_build_identity(self):
        docker = (ROOT / "deploy" / "web" / "Dockerfile").read_text(encoding="utf-8")
        self.assertIn("kide-version.json", docker)
        self.assertIn("VITE_KIDE_WEB_BUILD_ID", docker)

    def test_live_gcp_deployments_verify_backend_and_web_build_ids(self):
        for relative in (
            "cloudbuild.yaml",
            "deploy/gcp/cloudbuild.yaml",
            "deploy/gcp/cloudbuild-release.yaml",
            "deploy/gcp/cloudbuild-deploy.yaml",
        ):
            content = (ROOT / relative).read_text(encoding="utf-8")
            self.assertIn("/api/v1/version", content, relative)
            self.assertIn("/kide-version.json", content, relative)
            self.assertIn("kide-api-version", content, relative)
            self.assertIn("kide-web-version", content, relative)


if __name__ == "__main__":
    unittest.main()
