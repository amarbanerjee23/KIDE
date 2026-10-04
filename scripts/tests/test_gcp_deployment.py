from pathlib import Path
import re
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

    def test_cloud_run_entrypoint_binds_public_port_before_readiness_waits(self):
        entrypoint = self.read("deploy/gcp/entrypoint.sh")

        nginx_start = entrypoint.index("nginx -c /tmp/nginx.conf -g 'daemon off;' &")
        api_wait = entrypoint.index("for _ in $(seq 1 60); do")
        lsp_wait = entrypoint.index("wait_listener lsp")
        ready_log = entrypoint.index("KIDE CLOUD RUN READY")

        self.assertLess(nginx_start, api_wait)
        self.assertLess(nginx_start, lsp_wait)
        self.assertLess(nginx_start, ready_log)
        self.assertIn("Cloud Run requires the container to bind to $PORT promptly", entrypoint)
    def test_container_qualification_waits_for_full_kide_readiness(self):
        qualifier = self.read("scripts/qualify-cloud-run-backend.sh")
        self.assertIn("fully_ready=0", qualifier)
        self.assertIn("KIDE CLOUD RUN READY", qualifier)
        self.assertIn(
            "Cloud Run image became HTTP healthy but did not reach full KIDE readiness",
            qualifier,
        )
        self.assertNotIn(
            "docker logs \"$container_id\" 2>&1 | grep -q 'KIDE CLOUD RUN READY'",
            qualifier,
        )
    def test_cloud_run_deployment_matches_gcp_runtime_contract(self):
        nginx = self.read("deploy/gcp/nginx.conf.template")
        build = self.read("cloudbuild.yaml")
        deploy = self.read("deploy/gcp/cloudbuild-deploy.yaml")

        self.assertIn("listen 0.0.0.0:${PORT};", nginx)
        self.assertIn("storage.googleapis.com", build)
        self.assertIn("gcloud storage buckets create", build)
        self.assertIn("roles/storage.objectUser", build)
        self.assertIn("_KIDE_DATA_BUCKET", build)
        self.assertIn("httpGet.path=/health", build)
        self.assertIn("httpGet.port=8080", build)
        self.assertIn("periodSeconds=10", build)
        self.assertIn("failureThreshold=60", build)
        self.assertIn("--cpu-boost", build)
        self.assertIn("httpGet.path=/health", deploy)
    def test_cloud_run_health_endpoint_avoids_reserved_z_paths(self):
        deployment_files = (
            "deploy/gcp/nginx.conf.template",
            "deploy/web/nginx.conf.template",
            "scripts/qualify-cloud-run-backend.sh",
            "deploy/gcp/entrypoint.sh",
            "deploy/gcp/deployment-status.sh",
            "deploy-kide-gcp.sh",
            "deploy/gcp/bootstrap-first-deployment.sh",
            "deploy/web/deploy-web.sh",
            ".github/workflows/cloud-run.yml",
            "cloudbuild.yaml",
            "deploy/gcp/cloudbuild.yaml",
            "deploy/gcp/cloudbuild-release.yaml",
            "deploy/gcp/cloudbuild-deploy.yaml",
        )
        for relative in deployment_files:
            content = self.read(relative)
            self.assertNotIn(
                "/healthz",
                content,
                msg=f"{relative} uses a Cloud Run reserved-style health path",
            )

        backend_nginx = self.read("deploy/gcp/nginx.conf.template")
        web_nginx = self.read("deploy/web/nginx.conf.template")
        self.assertIn("location = /health {", backend_nginx)
        self.assertIn("location = /health {", web_nginx)
        self.assertIn('"health":"/health"', backend_nginx)
        self.assertIn("default_type application/json;", backend_nginx)
        self.assertIn("default_type text/plain;", web_nginx)
    def test_cloud_run_bootstraps_starter_knowledge_for_first_use(self):
        entrypoint = self.read("deploy/gcp/entrypoint.sh")
        application = self.read(
            "com.kide.enterprise.server/src/com/kide/enterprise/server/"
            "EnterpriseApiApplication.java"
        )

        self.assertIn(
            'KIDE_API_BOOTSTRAP_STARTER_KNOWLEDGE="${'
            'KIDE_API_BOOTSTRAP_STARTER_KNOWLEDGE:-true}"',
            entrypoint,
        )
        self.assertIn("KIDE_API_BOOTSTRAP_STARTER_KNOWLEDGE", application)
        self.assertIn("knowledgeRepository.snapshot().isEmpty()", application)
        self.assertIn("starterKnowledge(context)", application)
        self.assertIn("KnowledgeRepository.MISSING_ETAG", application)

    def test_cloud_run_services_are_explicitly_public(self):
        for relative in (
            "cloudbuild.yaml",
            "deploy/gcp/cloudbuild.yaml",
            "deploy/gcp/cloudbuild-release.yaml",
            "deploy/gcp/cloudbuild-deploy.yaml",
        ):
            build = self.read(relative)
            self.assertGreaterEqual(build.count("--ingress all"), 2)
            self.assertGreaterEqual(build.count("--allow-unauthenticated"), 2)
    def test_cloud_run_uses_reported_service_urls_and_informative_backend_root(self):
        nginx = self.read("deploy/gcp/nginx.conf.template")
        for relative in (
            "cloudbuild.yaml",
            "deploy/gcp/cloudbuild.yaml",
            "deploy/gcp/cloudbuild-release.yaml",
            "deploy/gcp/cloudbuild-deploy.yaml",
        ):
            build = self.read(relative)
            self.assertGreaterEqual(build.count("value(status.url)"), 3)
            self.assertNotIn(".${_REGION}.run.app", build)
            self.assertNotIn("value(projectNumber)", build)
            self.assertNotIn("gcloud projects describe", build)
            self.assertGreaterEqual(build.count("--default-url"), 2)

        self.assertIn('location = / {', nginx)
        self.assertIn('"service":"kide-backend"', nginx)
        self.assertIn('"health":"/health"', nginx)
        self.assertIn("default_type application/json;", nginx)
    def test_image_qualification_uses_one_robust_backend_contract(self):
        qualifier = self.read("scripts/qualify-cloud-run-backend.sh")

        for relative in (
            ".github/workflows/cloud-run.yml",
            ".github/workflows/publish-build-artifacts.yml",
        ):
            workflow = self.read(relative)
            self.assertIn("scripts/qualify-cloud-run-backend.sh", workflow)
            self.assertNotIn(
                "Backend image unexpectedly serves the browser application",
                workflow,
            )

        self.assertIn('"service": "kide-backend"', qualifier)
        self.assertIn('"status": "UP"', qualifier)
        self.assertIn('"health": "/health"', qualifier)
        self.assertIn('"api": "/api/v1"', qualifier)
        self.assertIn("/api/v1/health", qualifier)
        self.assertIn("json.loads", qualifier)
        self.assertIn('payload.get("version") == "v1"', qualifier)
        self.assertIn("definitely-not-a-kide-route", qualifier)
        self.assertIn("expected 404", qualifier)
        self.assertIn("expected application/json", qualifier)
    def test_deploy_contract_is_single_writer_and_websocket_ready(self):
        deploy = self.read("deploy/gcp/deploy-cloud-run.sh")
        cloudbuild = self.read("deploy/gcp/cloudbuild-deploy.yaml")
        self.assertIn("--timeout 3600s", cloudbuild)
        self.assertIn("--session-affinity", cloudbuild)
        self.assertIn("--max 1", cloudbuild)
        self.assertIn("type=cloud-storage", cloudbuild)
        self.assertIn("KIDE_FIREBASE_PROJECT_ID=", cloudbuild)
        self.assertIn("KIDE_FIREBASE_ADMIN_UID=", cloudbuild)
        self.assertNotIn("KIDE_OIDC_", cloudbuild)
        self.assertIn("gcloud builds submit", deploy)
        self.assertIn("--config deploy/gcp/cloudbuild-release.yaml", deploy)
        self.assertIn("COMMIT_SHA=", deploy)
        self.assertIn("SHORT_SHA=", deploy)
        self.assertIn("_KIDE_GITHUB_TOKEN_SECRET=", deploy)
        self.assertIn("deployment-status.sh", deploy)
        self.assertNotIn("KIDE_OIDC_", deploy)

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

    def test_cloud_build_substitutions_are_valid_and_complete(self):
        builtins = {
            "PROJECT_ID",
            "PROJECT_NUMBER",
            "BUILD_ID",
            "LOCATION",
            "TRIGGER_NAME",
            "COMMIT_SHA",
            "REVISION_ID",
            "SHORT_SHA",
            "REPO_NAME",
            "REPO_FULL_NAME",
            "BRANCH_NAME",
            "TAG_NAME",
            "REF_NAME",
            "TRIGGER_BUILD_CONFIG_PATH",
            "SERVICE_ACCOUNT_EMAIL",
            "SERVICE_ACCOUNT",
        }
        token_pattern = re.compile(
            r"(?<!\$)\$(?:\{([A-Z_][A-Z0-9_]*)\}|([A-Z_][A-Z0-9_]*))"
        )
        trigger_builtins = {
            "_HEAD_BRANCH",
            "_BASE_BRANCH",
            "_HEAD_REPO_URL",
            "_PR_NUMBER",
        }
        definition_pattern = re.compile(r"^  (_[A-Z0-9_]+):", re.MULTILINE)

        for relative in (
            "cloudbuild.yaml",
            "deploy/gcp/cloudbuild.yaml",
            "deploy/gcp/cloudbuild-release.yaml",
            "deploy/gcp/cloudbuild-deploy.yaml",
        ):
            cloudbuild = self.read(relative)
            tokens = {
                braced or plain
                for braced, plain in token_pattern.findall(cloudbuild)
            }
            invalid = sorted(
                token
                for token in tokens
                if not token.startswith("_") and token not in builtins
            )
            self.assertEqual(
                [],
                invalid,
                msg=f"{relative} has illegal unescaped Cloud Build substitutions",
            )

            custom_refs = {
                token
                for token in tokens
                if token.startswith("_") and token not in trigger_builtins
            }
            definitions = set(definition_pattern.findall(cloudbuild))
            self.assertEqual(
                [],
                sorted(custom_refs - definitions),
                msg=f"{relative} references undefined custom substitutions",
            )

            self.assertIn("${BACKEND_URL}", cloudbuild)
            self.assertIn("${WEB_URL}", cloudbuild)
            self.assertIn("value(status.url)", cloudbuild)
            self.assertNotIn(
                'gcloud projects describe "${PROJECT_ID}" --format=\'value(projectNumber)\'',
                cloudbuild,
            )
    def test_live_deployment_verification_is_diagnostic_and_complete(self):
        for relative in (
            "cloudbuild.yaml",
            "deploy/gcp/cloudbuild.yaml",
            "deploy/gcp/cloudbuild-release.yaml",
            "deploy/gcp/cloudbuild-deploy.yaml",
        ):
            build = self.read(relative)
            self.assertIn('check_url "KIDE backend root"', build)
            self.assertIn('check_url "KIDE backend health"', build)
            self.assertIn('check_url "KIDE API health"', build)
            self.assertIn('check_url "KIDE web health"', build)
            self.assertIn('check_url "KIDE web root"', build)
            self.assertIn("Last HTTP status:", build)
            self.assertIn("Response body (first 2048 bytes):", build)
            self.assertIn("status.latestReadyRevisionName", build)
            self.assertIn("status.traffic", build)
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

    def test_cloud_build_uses_firebase_without_oidc_introspection(self):
        build = self.read("cloudbuild.yaml")
        self.assertIn("Bootstrap Firebase hosted deployment", build)
        self.assertIn("_KIDE_FIREBASE_PROJECT_ID", build)
        self.assertIn("KIDE_FIREBASE_API_KEY", build)
        self.assertIn("_KIDE_FIREBASE_ADMIN_UID", build)
        self.assertIn("VITE_FIREBASE_API_KEY", build)
        self.assertIn("VITE_FIREBASE_PROJECT_ID", build)
        self.assertIn(
            '--set-env-vars "^|^KIDE_FIREBASE_PROJECT_ID=',
            build,
        )
        self.assertNotIn("KIDE_OIDC_", build)
        self.assertNotIn("OIDC_CLIENT_SECRET", build)

    def test_canonical_cloud_build_self_bootstraps_and_deploys(self):
        build = self.read("cloudbuild.yaml")

        self.assertIn("Bootstrap Firebase hosted deployment", build)
        self.assertIn("scripts/bootstrap_firebase_auth.py", build)
        self.assertIn("kide-firebase-admin-password", build)
        self.assertIn("gcloud secrets create", build)
        self.assertIn("/workspace/.kide-firebase.env", build)
        self.assertIn('source /workspace/.kide-firebase.env', build)
        self.assertIn('. /workspace/.kide-firebase.env', build)
        self.assertIn("VITE_FIREBASE_API_KEY=", build)
        self.assertIn("KIDE_FIREBASE_API_KEY", build)
        self.assertIn("KIDE_FIREBASE_ADMIN_UID=", build)
        self.assertIn('gcloud run deploy "${_KIDE_SERVICE_NAME}"', build)
        self.assertIn('gcloud run deploy "${_KIDE_WEB_SERVICE_NAME}"', build)
        self.assertIn("KIDE DEPLOYMENT COMPLETE", build)
        self.assertIn('echo "Web: ', build)
        self.assertNotIn(
            "Skipping backend deployment: KIDE hosted release is not enabled.",
            build,
        )
        self.assertNotIn(
            "Skipping web deployment: KIDE hosted release is not enabled.",
            build,
        )
        self.assertNotIn(
            "Skipping hosted deployment metadata: KIDE hosted release is not enabled.",
            build,
        )
    def test_cloud_build_bootstraps_runtime_service_account_iam(self):
        build = self.read("cloudbuild.yaml")

        self.assertIn("gcloud auth list --filter=status:ACTIVE", build)
        self.assertIn("ensure_runtime_service_account", build)
        self.assertIn("gcloud iam service-accounts create", build)
        self.assertIn("gcloud iam service-accounts add-iam-policy-binding", build)
        self.assertIn("roles/iam.serviceAccountUser", build)
        self.assertIn("_KIDE_RUNTIME_SA", build)
        self.assertIn("_KIDE_WEB_RUNTIME_SA", build)
        self.assertIn("Cloud Build runtime identity bindings are ready.", build)
    def test_cloud_build_sh_steps_use_posix_condition_syntax(self):
        for relative in (
            "cloudbuild.yaml",
            "deploy/gcp/cloudbuild.yaml",
            "deploy/gcp/cloudbuild-release.yaml",
        ):
            lines = self.read(relative).splitlines()
            for index, line in enumerate(lines):
                if line.strip() != "entrypoint: sh":
                    continue
                block = "\n".join(lines[index : index + 45])
                self.assertNotIn(
                    "[[",
                    block,
                    msg=f"{relative} uses Bash-only [[ in an sh step",
                )

    def test_release_publisher_requires_hosted_metadata(self):
        release = self.read("deploy/gcp/cloudbuild-release.yaml")
        self.assertIn("test -s dist/gcp-release-notes.md", release)
        self.assertIn("test -s dist/KIDE-HOSTED-URL.txt", release)
        self.assertIn("test -s dist/gcp-deployment-manifest.json", release)
        self.assertIn("test -s /workspace/.kide-github-token", release)

    def test_deployment_shell_scripts_parse(self):
        for relative in (
            "deploy/gcp/entrypoint.sh",
            "deploy/gcp/deploy-cloud-run.sh",
            "deploy/gcp/repair-cloud-build-trigger.sh",
            "deploy/gcp/bootstrap-cloud-build.sh",
            "deploy/gcp/configure-auto-deploy.sh",
            "deploy/gcp/verify-firebase-trigger.sh",
            "deploy/gcp/deployment-status.sh",
            "deploy/gcp/bootstrap-first-deployment.sh",
            "deploy-kide-gcp.sh",
            "scripts/qualify-cloud-run-backend.sh",
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
        self.assertIn("scripts/bootstrap_firebase_auth.py", bootstrap)
        self.assertIn("KIDE_FIREBASE_API_KEY", bootstrap)
        self.assertIn("KIDE_FIREBASE_ADMIN_UID", bootstrap)
        self.assertIn('source "${FIREBASE_ENV_TEMP_FILE}"', bootstrap)
        self.assertNotIn('prompt_value KIDE_FIREBASE_API_KEY', bootstrap)
        self.assertNotIn('prompt_value KIDE_FIREBASE_ADMIN_UID', bootstrap)
        self.assertIn("gcloud secrets versions add", bootstrap)
        self.assertIn("gcloud secrets create", bootstrap)
        self.assertIn("configure-auto-deploy.sh", bootstrap)
        self.assertIn("wait_for_live_services", bootstrap)
        self.assertIn('"${backend_url}/health"', bootstrap)
        self.assertIn('"${web_url}/health"', bootstrap)
        self.assertIn("KIDE FIRST DEPLOYMENT COMPLETE", bootstrap)
        self.assertIn("KIDE_GITHUB_TOKEN_SECRET", bootstrap)
        self.assertIn("KIDE_GITHUB_RELEASE_TOKEN_FILE", bootstrap)
        self.assertIn("GitHub release token (hidden)", bootstrap)
        self.assertIn("gcloud secrets versions add", bootstrap)
        self.assertNotIn("KIDE_OIDC_", bootstrap)
        self.assertNotIn("OIDC client secret", bootstrap)

        self.assertIn("bootstrap_first_deployment", entrypoint)
        self.assertIn("bootstrap-first-deployment.sh", entrypoint)
        self.assertIn("bootstrap) bootstrap_first_deployment", entrypoint)

    def test_firebase_bootstrap_provisions_web_app_and_admin(self):
        helper = self.read("scripts/bootstrap_firebase_auth.py")
        self.assertIn("projects/{project_id}:addFirebase", helper)
        self.assertIn("/webApps", helper)
        self.assertIn("/config", helper)
        self.assertIn("identityPlatform:initializeAuth", helper)
        self.assertIn("ensure_auth_configuration", helper)
        self.assertIn("allow_404=True", helper)
        self.assertIn("updateMask", helper)
        self.assertIn("signIn.email", helper)
        self.assertIn("signInWithPassword", helper)
        self.assertIn("signUp", helper)
        self.assertIn("KIDE_FIREBASE_ADMIN_PASSWORD_FILE", helper)
        self.assertIn("serviceusage.services.enable", helper)
        self.assertNotIn("KIDE_OIDC_", helper)

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
        self.assertIn('"$${BACKEND_URL}/health"', cloudbuild)
        self.assertIn('"$${WEB_URL}/health"', cloudbuild)
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
        self.assertIn("scripts/publish_github_release.py", release)
        self.assertIn("python:3.12-slim", release)
        self.assertIn("--asset-glob", release)
        self.assertIn("_KIDE_GITHUB_TOKEN_SECRET", release)
        self.assertIn("Resolve GitHub release token", release)
        self.assertIn("gcloud secrets versions access latest", release)
        self.assertIn("/workspace/.kide-github-token", release)
        self.assertNotIn("availableSecrets:", release)
        self.assertNotIn("secretEnv:", release)
        self.assertNotIn("ghcr.io/cli/cli:", release)

    def test_auto_deploy_bootstrap_uses_separate_runtime_identities(self):
        configure = self.read("deploy/gcp/configure-auto-deploy.sh")

        self.assertIn("roles/run.admin", configure)
        self.assertIn("roles/iam.serviceAccountUser", configure)
        self.assertIn("roles/storage.objectUser", configure)
        self.assertIn("roles/secretmanager.secretAccessor", configure)
        self.assertIn("kide-runtime", configure)
        self.assertIn("kide-web-runtime", configure)
        self.assertIn("--update-substitutions", configure)
        self.assertIn("_KIDE_FIREBASE_PROJECT_ID=", configure)
        self.assertIn("_KIDE_FIREBASE_API_KEY=", configure)
        self.assertIn("_KIDE_FIREBASE_ADMIN_UID=", configure)
        self.assertNotIn("KIDE_OIDC_", configure)
        self.assertIn("_KIDE_GITHUB_REPOSITORY=", configure)
        self.assertIn("_KIDE_GITHUB_TOKEN_SECRET=", configure)
        self.assertIn("roles/secretmanager.secretAccessor", configure)
        self.assertIn("_KIDE_RELEASE_ENABLED=true", configure)
        self.assertIn("--update-substitutions", configure)
        self.assertIn("verify-firebase-trigger.sh", configure)
        verifier = self.read("deploy/gcp/verify-firebase-trigger.sh")
        self.assertIn("read_trigger_substitution", verifier)
        self.assertIn("Cloud Build trigger Firebase configuration verification failed.", verifier)
        self.assertIn("The deployment build will not be started with incomplete Firebase substitutions.", verifier)
        self.assertIn("Substitution ${key} was not persisted as expected.", verifier)
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
        self.assertIn("Bootstrap Firebase hosted deployment", build)
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
