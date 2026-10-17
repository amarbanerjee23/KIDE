"""PR109: reject falsely green Enterprise CI that silently skips real KIDE tests."""
from pathlib import Path
import unittest
from unittest.mock import patch

ROOT = Path(__file__).resolve().parents[2]
CI = ROOT / ".github/workflows/ci.yml"


def coverage_errors(content: str) -> list[str]:
    errors: list[str] = []
    for forbidden in (
        "kide-enterprise/frontend",
        "kide-enterprise/backend",
        "present == 'true'",
        "Frontend component is not present",
        "Backend component is not present",
        "pip install fastapi uvicorn",
    ):
        if forbidden in content:
            errors.append("Legacy skip-capable Enterprise CI component: " + forbidden)
    for required in (
        "name: KIDE Enterprise CI Pipeline",
        "name: Engineering Backend Policy and API Contract Tests",
        "name: Web Frontend Typecheck, Unit Tests and Production Build",
        "python3 -m unittest discover -s scripts/tests -p 'test_*.py'",
        "scripts/verify_shared_firebase_access.py",
        "scripts/verify_lsp_parity_matrix.py",
        "scripts/verify_diagram_parity.py",
        "scripts/verify_live_acceptance.py",
        "scripts/verify_pr107_release_gate.py",
        "web/src/App.tsx",
        "web/src/lspClient.ts",
        "web/src/glspClient.ts",
        "web/src/collaboration.ts",
        "npm install --no-audit --no-fund",
        "npm run test",
        "npm run build",
        "playwright.live.config.ts --list",
        "playwright.connectivity.config.ts --list",
        "persist-credentials: false",
        "permissions:\n  contents: read",
    ):
        if required not in content:
            errors.append("Actual engineering test or safe CI gate missing: " + required)
    return errors


class TruthfulEnterpriseCITest(unittest.TestCase):
    def test_real_web_and_backend_test_paths_are_exercised(self):
        self.assertEqual([], coverage_errors(CI.read_text(encoding="utf8")))

    def test_legacy_skip_reintroduction_is_rejected(self):
        ci = CI.read_text(encoding="utf8")
        errors = coverage_errors(ci + "\nif: steps.frontend.outputs.present == 'true'")
        self.assertTrue(any("skip-capable" in issue for issue in errors))

    def test_deleting_real_web_or_backend_tests_is_rejected(self):
        ci = CI.read_text(encoding="utf8")
        for command in ("npm run test", "npm run build",
                        "python3 -m unittest discover -s scripts/tests -p 'test_*.py'",
                        "playwright.live.config.ts --list"):
            with self.subTest(command=command):
                self.assertTrue(coverage_errors(ci.replace(command, "echo skipped")))

    def test_compiler_tests_are_distinct_from_live_deployment(self):
        ci = CI.read_text(encoding="utf8")
        # Source/build verification cannot be promoted to a genuine hosted
        # engineering test. The real stage requires separate credentials.
        self.assertIn("playwright.live.config.ts --list", ci)
        self.assertNotIn("npm run test:live", ci)


if __name__ == "__main__":
    unittest.main()
