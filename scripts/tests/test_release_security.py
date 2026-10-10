from __future__ import annotations

import importlib.util
import pathlib
import unittest
from unittest.mock import patch

ROOT = pathlib.Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts/verify_release_security.py"


def verifier():
    spec = importlib.util.spec_from_file_location("verify_release_security", SCRIPT)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


class ReleaseSecurityTest(unittest.TestCase):
    def test_current_security_boundary_passes(self):
        self.assertEqual([], verifier().verify())

    def altered(self, path, transform):
        module = verifier()
        original = module.read

        def read(name):
            content = original(name)
            return transform(content) if name == path else content

        with patch.object(module, "read", side_effect=read):
            return module.verify()

    def test_pr_build_must_not_publish_registry_image(self):
        errors = self.altered(
            ".github/workflows/publish-build-artifacts.yml",
            lambda s: s.replace(
                "    name: Build and qualify Cloud Run image (no publication)",
                "    name: Build and qualify Cloud Run image (no publication)"
                + "\n      docker push attacker.example/image:latest"
            ),
        )
        self.assertTrue(any("PR qualification" in error for error in errors), errors)

    def test_repository_wide_write_token_is_rejected(self):
        errors = self.altered(
            ".github/workflows/publish-build-artifacts.yml",
            lambda s: s.replace("permissions:\n  contents: read",
                                "permissions:\n  contents: write\n  packages: write", 1)
        )
        self.assertTrue(any("top-level write" in error for error in errors), errors)

    def test_release_must_not_bypass_source_guard(self):
        errors = self.altered(
            ".github/workflows/release.yml",
            lambda s: s.replace("    needs: verify-release-source\n", "", 1)
        )
        self.assertTrue(any("stable release must depend" in error for error in errors), errors)

    def test_release_must_not_interpolate_manual_version_inside_shell(self):
        expr = "$" + "{{ github.event.inputs.version }}"
        errors = self.altered(
            ".github/workflows/release.yml",
            lambda s: s.replace('VERSION="$KIDE_REQUESTED_VERSION"',
                                'VERSION="' + expr + '"')
        )
        self.assertTrue(any("interpolated into shell" in error for error in errors), errors)

    def test_production_sourcemap_is_rejected(self):
        errors = self.altered(
            "web/vite.config.ts",
            lambda s: s.replace("sourcemap: false", "sourcemap: true")
        )
        self.assertTrue(any("source maps" in error for error in errors), errors)

    def test_security_header_removal_is_rejected(self):
        errors = self.altered(
            "deploy/web/nginx.conf.template",
            lambda s: s.replace('X-Frame-Options "DENY" always', "# removed")
        )
        self.assertTrue(any("X-Frame-Options" in error for error in errors), errors)

    def test_unsigned_ci_release_publication_is_rejected(self):
        errors = self.altered(
            ".github/workflows/publish-build-artifacts.yml",
            lambda s: s.replace(
                "  publish-cloud-run:",
                "  publish-desktop:\n    name: Unsigned release preview\n"
                "    steps:\n      - run: gh release create release-preview --prerelease\n\n"
                "  publish-cloud-run:",
                1,
            ),
        )
        self.assertTrue(any("unsigned desktop" in error for error in errors), errors)
        self.assertTrue(any("outside trusted release" in error for error in errors), errors)

    def test_trusted_publisher_without_retention_is_rejected(self):
        errors = self.altered(
            ".github/workflows/release.yml",
            lambda s: s.replace(
                "python3 scripts/retain_latest_stable_release.py",
                "echo skip-release-retention",
            ),
        )
        self.assertTrue(any("trusted stable publisher" in error for error in errors), errors)

    def test_signing_secrets_require_successful_quality_gate(self):
        errors = self.altered(
            ".github/workflows/release.yml",
            lambda s: s.replace("    needs: verify-quality-gates\n", "", 1),
        )
        self.assertTrue(any("before quality verification" in error for error in errors), errors)


if __name__ == "__main__":
    unittest.main()
