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
        self.assertIn("return 404;", nginx)
        self.assertNotIn("try_files $uri $uri/ /index.html", nginx)
        self.assertNotIn("FROM node:", dockerfile)
        self.assertNotIn("/src/web/dist", dockerfile)

    def test_deploy_contract_is_single_writer_and_websocket_ready(self):
        deploy = self.read("deploy/gcp/deploy-cloud-run.sh")
        cloudbuild = self.read("deploy/gcp/cloudbuild-deploy.yaml")
        self.assertIn("--timeout 3600s", cloudbuild)
        self.assertIn("--session-affinity", cloudbuild)
        self.assertIn("--max 1", cloudbuild)
        self.assertIn("type=cloud-storage", cloudbuild)
        self.assertIn("--set-secrets", cloudbuild)
        self.assertIn("gcloud builds submit", deploy)
        self.assertIn("--config deploy/gcp/cloudbuild-release.yaml", deploy)
        self.assertIn("COMMIT_SHA=", deploy)
        self.assertIn("SHORT_SHA=", deploy)
        self.assertIn("_KIDE_GITHUB_TOKEN_SECRET=", deploy)
        self.assertIn("deployment-status.sh", deploy)
        self.assertNotIn("YOUR_OIDC_CLIENT_SECRET", deploy)

    def test_cloud_build_uses_canonical_unified_release_config(self):
        cloudbuild = self.read("deploy/gcp/cloudbuild.yaml")
        root_cloudbuild = self.read("cloudbuild.yaml")
        release = self.read("deploy/gcp/cloudbuild-release.yaml")

        self.assertEqual(root_cloudbuild, cloudbuild)
        self.assertEqual(root_cloudbuild, release)
        self.assertIn("deploy/gcp/Dockerfile", cloudbuild)
        self.assertIn("Build Eclipse desktop products", cloudbuild)
        self.assertIn("Build KIDE web image", cloudbuild)
        self.assertIn("Deploy KIDE backend", cloudbuild)
        self.assertIn("Deploy KIDE web", cloudbuild)
        self.assertIn("KIDE-HOSTED-URL.txt", cloudbuild)
        self.assertIn("_KIDE_RELEASE_ENABLED: 'false'", cloudbuild)
        self.assertIn("dynamicSubstitutions: true", cloudbuild)
        self.assertIn("logging: CLOUD_LOGGING_ONLY", cloudbuild)

    def test_cloud_build_yaml_has_no_unindented_embedded_content(self):
        allowed_top_level = (
            "steps:",
            "images:",
            "substitutions:",
            "timeout:",
            "options:",
        )
        for relative in (
            "cloudbuild.yaml",
            "deploy/gcp/cloudbuild.yaml",
            "deploy/gcp/cloudbuild-release.yaml",
        ):
            for line_number, line in enumerate(
                self.read(relative).splitlines(),
                start=1,
            ):
                if not line.strip():
                    continue
                if line.startswith(" "):
                    continue
                self.assertTrue(
                    line.startswith(allowed_top_level),
                    msg=(
                        f"{relative}:{line_number} has unexpected "
                        f"top-level content: {line!r}"
                    ),
                )

    def test_deployment_shell_scripts_parse(self):
        for relative in (
            "deploy/gcp/entrypoint.sh",
            "deploy/gcp/deploy-cloud-run.sh",
            "deploy/gcp/repair-cloud-build-trigger.sh",
            "deploy/gcp/bootstrap-cloud-build.sh",
            "deploy/gcp/configure-auto-deploy.sh",
            "deploy/gcp/deployment-status.sh",
            "deploy/gcp/bootstrap-first-deployment.sh",
            "deploy-kide-gcp.sh",
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

    def test_cloud_shell_entrypoint_supports_status_and_deploy(self):
        script = self.read("deploy-kide-gcp.sh")
        status = self.read("deploy/gcp/deployment-status.sh")

        self.assertIn('COMMAND="${1:-status}"', script)
        self.assertIn("KIDE web: NOT DEPLOYED", script)
        self.assertIn("KIDE backend: NOT DEPLOYED", script)
        self.assertIn("deploy/gcp/deploy-cloud-run.sh", script)
        self.assertIn("configure-auto-deploy.sh", script)
        self.assertIn("git clone", script)
        self.assertIn("origin/main", script)
        self.assertIn("KIDE web: NOT DEPLOYED", status)
        self.assertIn("KIDE backend: NOT DEPLOYED", status)

    def test_first_deployment_bootstrap_is_secure_and_complete(self):
        bootstrap = self.read("deploy/gcp/bootstrap-first-deployment.sh")
        entrypoint = self.read("deploy-kide-gcp.sh")

        self.assertIn("gcloud builds triggers list", bootstrap)
        self.assertIn("gcloud builds triggers describe", bootstrap)
        self.assertIn("gcloud builds triggers run", bootstrap)
        self.assertIn("--branch", bootstrap)
        self.assertIn("read -r -s -p", bootstrap)
        self.assertIn("mktemp", bootstrap)
        self.assertIn("--data-file=", bootstrap)
        self.assertIn("gcloud secrets versions add", bootstrap)
        self.assertIn("gcloud secrets create", bootstrap)
        self.assertIn("configure-auto-deploy.sh", bootstrap)
        self.assertIn("wait_for_live_services", bootstrap)
        self.assertIn('"${backend_url}/healthz"', bootstrap)
        self.assertIn('"${web_url}/healthz"', bootstrap)
        self.assertIn("KIDE FIRST DEPLOYMENT COMPLETE", bootstrap)
        self.assertIn("KIDE_GITHUB_TOKEN_SECRET", bootstrap)
        self.assertIn("KIDE_GITHUB_RELEASE_TOKEN_FILE", bootstrap)
        self.assertIn("GitHub release token (hidden)", bootstrap)
        self.assertIn("gcloud secrets versions add", bootstrap)
        self.assertNotIn("echo -n \"${secret_value}\"", bootstrap)

        self.assertIn("bootstrap_first_deployment", entrypoint)
        self.assertIn("bootstrap-first-deployment.sh", entrypoint)
        self.assertIn("bootstrap) bootstrap_first_deployment", entrypoint)

    def test_trigger_repair_uses_full_auto_deploy_configuration(self):
        repair = self.read("deploy/gcp/repair-cloud-build-trigger.sh")
        self.assertIn("configure-auto-deploy.sh", repair)

    def test_cloud_build_deploys_backend_then_web_and_checks_live_urls(self):
        cloudbuild = self.read("deploy/gcp/cloudbuild-deploy.yaml")

        self.assertIn("Deploy KIDE backend", cloudbuild)
        self.assertIn('gcloud run deploy "${_KIDE_SERVICE_NAME}"', cloudbuild)
        self.assertIn("Build KIDE web image", cloudbuild)
        self.assertIn("VITE_KIDE_API_ORIGIN=", cloudbuild)
        self.assertIn("VITE_KIDE_LSP_ORIGIN=", cloudbuild)
        self.assertIn("Deploy KIDE web", cloudbuild)
        self.assertIn('gcloud run deploy "${_KIDE_WEB_SERVICE_NAME}"', cloudbuild)
        self.assertIn("KIDE_ALLOWED_ORIGINS=", cloudbuild)
        self.assertIn("Verify live KIDE deployment", cloudbuild)
        self.assertIn('"$${BACKEND_URL}/healthz"', cloudbuild)
        self.assertIn('"$${WEB_URL}/healthz"', cloudbuild)
        self.assertIn("KIDE DEPLOYMENT COMPLETE", cloudbuild)

    def test_unified_gcp_release_pipeline_builds_deploys_and_publishes(self):
        release = self.read("deploy/gcp/cloudbuild-release.yaml")

        self.assertIn("maven:3.9.9-eclipse-temurin-21", release)
        self.assertIn("Build Eclipse desktop products", release)
        self.assertIn("mvn -B -ntp clean verify", release)
        self.assertIn("prepare_desktop_release.py", release)
        self.assertIn("KIDE.exe", release)
        self.assertIn("KIDE.command", release)
        self.assertIn("Deploy KIDE backend", release)
        self.assertIn("Build KIDE web image", release)
        self.assertIn("Deploy KIDE web", release)
        self.assertIn("Verify live KIDE deployment", release)
        self.assertIn("KIDE-HOSTED-URL.txt", release)
        self.assertIn("gcp-deployment-manifest.json", release)
        self.assertIn("gh release create", release)
        self.assertIn("gh release upload", release)
        self.assertIn("gh release edit", release)
        self.assertIn("_KIDE_GITHUB_TOKEN_SECRET", release)
        self.assertIn("Resolve GitHub release token", release)
        self.assertIn("gcloud secrets versions access latest", release)
        self.assertIn("/workspace/.kide-github-token", release)
        self.assertNotIn("availableSecrets:", release)
        self.assertNotIn("secretEnv:", release)
        self.assertIn("ghcr.io/cli/cli:2.97.0", release)

    def test_auto_deploy_bootstrap_uses_separate_runtime_identities(self):
        configure = self.read("deploy/gcp/configure-auto-deploy.sh")

        self.assertIn("roles/run.admin", configure)
        self.assertIn("roles/iam.serviceAccountUser", configure)
        self.assertIn("roles/storage.objectUser", configure)
        self.assertIn("roles/secretmanager.secretAccessor", configure)
        self.assertIn("kide-runtime", configure)
        self.assertIn("kide-web-runtime", configure)
        self.assertIn("--update-substitutions", configure)
        self.assertIn("_KIDE_OIDC_SECRET_NAME=", configure)
        self.assertIn("_KIDE_GITHUB_REPOSITORY=", configure)
        self.assertIn("_KIDE_GITHUB_TOKEN_SECRET=", configure)
        self.assertIn("roles/secretmanager.secretAccessor", configure)
        self.assertIn("_KIDE_RELEASE_ENABLED=true", configure)
        self.assertIn(
            'BUILD_CONFIG="${BUILD_CONFIG:-cloudbuild.yaml}"',
            configure,
        )

    def test_artifact_registry_bootstrap_contract(self):
        bootstrap = self.read("deploy/gcp/bootstrap-cloud-build.sh")
        build = self.read("cloudbuild.yaml")
        deploy = self.read("deploy/gcp/cloudbuild-deploy.yaml")
        configure = self.read("deploy/gcp/configure-auto-deploy.sh")

        self.assertIn("gcloud artifacts repositories create", bootstrap)
        self.assertIn("roles/artifactregistry.writer", bootstrap)
        self.assertIn("roles/logging.logWriter", bootstrap)
        self.assertIn("Verify release prerequisites", build)
        self.assertIn("_KIDE_RELEASE_ENABLED", build)
        self.assertIn("Verify deployment prerequisites", deploy)
        self.assertIn("configure-auto-deploy.sh", deploy)
        self.assertIn("bootstrap-cloud-build.sh", configure)

        # The canonical root config remains green before activation and avoids
        # resolving the GitHub token secret until hosted release mode is enabled.
        self.assertIn("Skipping GitHub Release publication", build)
        self.assertNotIn("availableSecrets:", build)

        # The legacy deployment-only preflight retains least privilege.
        self.assertNotIn("gcloud storage buckets describe", deploy)
        self.assertNotIn("gcloud secrets describe", deploy)


if __name__ == "__main__":
    unittest.main()
