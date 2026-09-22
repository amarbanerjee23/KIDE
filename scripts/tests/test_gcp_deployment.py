from pathlib import Path
import subprocess
import unittest

ROOT = Path(__file__).resolve().parents[2]


class GoogleCloudDeploymentContractTest(unittest.TestCase):
    def read(self, relative: str) -> str:
        return (ROOT / relative).read_text(encoding="utf-8")

    def test_glsp_has_production_application(self):
        plugin = self.read("com.kide.glsp/plugin.xml")
        source = self.read(
            "com.kide.glsp/src/com/kide/glsp/GlspGatewayApplication.java"
        )
        self.assertIn('id="application"', plugin)
        self.assertIn("GlspGatewayApplication", plugin)
        self.assertIn("KIDE_GLSP_TRUST_FORWARDED_PROTO", source)
        self.assertIn("KIDE_GLSP_ROLE_BINDINGS", source)

    def test_cloud_run_container_keeps_runtime_boundaries_internal(self):
        dockerfile = self.read("deploy/gcp/Dockerfile")
        entrypoint = self.read("deploy/gcp/entrypoint.sh")
        nginx = self.read("deploy/gcp/nginx.conf.template")

        self.assertIn("USER 10001:10001", dockerfile)
        self.assertIn("com.kide.enterprise.server.application", entrypoint)
        self.assertIn("com.kide.languageserver.gateway.application", entrypoint)
        self.assertIn("com.kide.glsp.application", entrypoint)
        self.assertIn("KIDE_API_BIND=127.0.0.1", entrypoint)
        self.assertIn("KIDE_GATEWAY_BIND=127.0.0.1", entrypoint)
        self.assertIn("KIDE_GLSP_BIND=127.0.0.1", entrypoint)
        self.assertIn("location /api/", nginx)
        self.assertIn("location = /lsp", nginx)
        self.assertIn("location = /glsp", nginx)
        self.assertIn("try_files $uri $uri/ /index.html", nginx)

    def test_deploy_contract_is_single_writer_and_websocket_ready(self):
        deploy = self.read("deploy/gcp/deploy-cloud-run.sh")
        self.assertIn("--timeout 3600s", deploy)
        self.assertIn("--session-affinity", deploy)
        self.assertIn("--max 1", deploy)
        self.assertIn("type=cloud-storage", deploy)
        self.assertIn("--set-secrets", deploy)
        self.assertNotIn("YOUR_OIDC_CLIENT_SECRET", deploy)

    def test_cloud_build_uses_dedicated_dockerfile(self):
        cloudbuild = self.read("deploy/gcp/cloudbuild.yaml")
        root_cloudbuild = self.read("cloudbuild.yaml")
        self.assertEqual(root_cloudbuild, cloudbuild)
        self.assertIn("deploy/gcp/Dockerfile", cloudbuild)
        self.assertIn("${_IMAGE}", cloudbuild)
        self.assertIn("logging: CLOUD_LOGGING_ONLY", cloudbuild)

    def test_deployment_shell_scripts_parse(self):
        for relative in (
            "deploy/gcp/entrypoint.sh",
            "deploy/gcp/deploy-cloud-run.sh",
            "deploy/gcp/repair-cloud-build-trigger.sh",
        ):
            result = subprocess.run(
                ["bash", "-n", str(ROOT / relative)],
                check=False,
                capture_output=True,
                text=True,
            )
            self.assertEqual(
                result.returncode,
                0,
                msg=f"{relative} failed bash -n: {result.stderr}",
            )

    def test_trigger_repair_forces_cloudbuild_config(self):
        repair = self.read("deploy/gcp/repair-cloud-build-trigger.sh")
        self.assertIn("--build-config", repair)
        self.assertIn('BUILD_CONFIG="${BUILD_CONFIG:-cloudbuild.yaml}"', repair)
        self.assertIn("--update-substitutions", repair)
        self.assertIn("gcloud builds triggers update github", repair)


if __name__ == "__main__":
    unittest.main()
