from pathlib import Path
import subprocess
import unittest

ROOT = Path(__file__).resolve().parents[2]


class ReleaseDeliveryContractTest(unittest.TestCase):
    def read(self, relative: str) -> str:
        return (ROOT / relative).read_text(encoding="utf-8")

    def assert_bash_parses(self, relative: str) -> None:
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

    def test_desktop_release_has_platform_entrypoints(self):
        prepare = self.read("scripts/prepare_desktop_release.py")
        linux = self.read("deploy/desktop/linux/KIDE.sh")
        mac = self.read("deploy/desktop/macos/KIDE.command")

        self.assertIn('"kide.exe"', prepare)
        self.assertIn('"KIDE.exe"', prepare)
        self.assertIn('"KIDE.sh"', prepare)
        self.assertIn('"KIDE.command"', prepare)
        self.assertIn("stage_release_archive", prepare)
        self.assertIn('wrapper.mode = 0o755', prepare)
        self.assertIn('NATIVE_LAUNCHER="${SCRIPT_DIR}/kide"', linux)
        self.assertIn('APP="${SCRIPT_DIR}/KIDE.app"', mac)
        self.assertIn('NATIVE_LAUNCHER="${APP}/Contents/MacOS/kide"', mac)

        self.assert_bash_parses("deploy/desktop/linux/KIDE.sh")
        self.assert_bash_parses("deploy/desktop/macos/KIDE.command")

    def test_repository_publication_builds_and_pushes_outputs(self):
        workflow = self.read(".github/workflows/publish-build-artifacts.yml")

        self.assertIn("contents: write", workflow)
        self.assertIn("packages: write", workflow)
        self.assertIn("Build Eclipse desktop products", workflow)
        self.assertIn("prepare_desktop_release.py", workflow)
        self.assertIn("Upload runnable desktop bundles to repository artifacts", workflow)
        self.assertIn("dist/KIDE-*.zip", workflow)
        self.assertIn("dist/KIDE-*.tar.gz", workflow)
        self.assertIn("ghcr.io/", workflow)
        self.assertIn("kide-cloud-run", workflow)
        self.assertIn("docker login ghcr.io", workflow)
        self.assertIn("docker push", workflow)
        self.assertIn("sha-${SOURCE_SHA}", workflow)
        self.assertIn("SOURCE_SHA:", workflow)
        self.assertIn("org.opencontainers.image.source", workflow)
        self.assertIn("kide-cloud-run-image.txt", workflow)
        self.assertIn("Publish runnable desktop bundles to GitHub Releases", workflow)
        self.assertIn(
            "(github.event_name == 'push' && github.ref_name != 'main') || github.event_name == 'workflow_dispatch'",
            workflow,
        )
        self.assertIn("gh release create", workflow)
        self.assertIn("gh release upload", workflow)
        self.assertIn("--prerelease", workflow)
        self.assertIn("branch-${branch_slug}-build-${short_sha}", workflow)
        self.assertNotIn("pr45-build-", workflow)
        self.assertIn("dist/KIDE-*.zip", workflow)
        self.assertIn("dist/KIDE-*.tar.gz", workflow)

    def test_gcp_backend_image_does_not_embed_browser_bundle(self):
        dockerfile = self.read("deploy/gcp/Dockerfile")
        nginx = self.read("deploy/gcp/nginx.conf.template")

        self.assertNotIn("FROM node:", dockerfile)
        self.assertNotIn("web/dist", dockerfile)
        self.assertIn("return 404;", nginx)

    def test_web_deployer_is_independent_from_desktop_product(self):
        dockerfile = self.read("deploy/web/Dockerfile")
        cloudbuild = self.read("deploy/web/cloudbuild.yaml")
        deploy = self.read("deploy/web/deploy-web.sh")
        nginx = self.read("deploy/web/nginx.conf.template")

        self.assertIn("VITE_KIDE_API_ORIGIN", dockerfile)
        self.assertIn("VITE_KIDE_LSP_ORIGIN", dockerfile)
        self.assertNotIn("com.kide.enterprise", dockerfile)
        self.assertNotIn("mvn ", dockerfile)
        self.assertIn("deploy/web/Dockerfile", cloudbuild)
        self.assertIn("_KIDE_BACKEND_ORIGIN", cloudbuild)
        self.assertIn("logging: CLOUD_LOGGING_ONLY", cloudbuild)
        self.assertIn('SERVICE_NAME="${SERVICE_NAME:-kide-web}"', deploy)
        self.assertIn("deploy/web/cloudbuild.yaml", deploy)
        self.assertIn("gcloud run deploy", deploy)
        self.assertIn("KIDE_ALLOWED_ORIGINS", deploy)
        self.assertIn("try_files $uri $uri/ /index.html", nginx)

        self.assert_bash_parses("deploy/web/deploy-web.sh")


if __name__ == "__main__":
    unittest.main()
