from pathlib import Path
import subprocess
import unittest

ROOT = Path(__file__).resolve().parents[2]


class PinnedMavenContractTest(unittest.TestCase):
    def read(self, relative: str) -> str:
        return (ROOT / relative).read_text(encoding="utf-8")

    def test_launcher_pins_known_green_maven_with_digest(self):
        launcher = self.read("scripts/run_maven.sh")
        self.assertIn('MAVEN_VERSION="3.9.16"', launcher)
        self.assertIn(
            'MAVEN_SHA512="831a8591fe20c8243b1dbe7d71e3244f31d1665b0804b2e825e38cbbe5ce0cafb8338851f90780735568773e0a6cd07bbec107cda0b896b008b861075358b6f6"',
            launcher,
        )
        self.assertIn("sha512sum --check --status", launcher)
        self.assertIn("archive.apache.org/dist/maven", launcher)
        result = subprocess.run(
            ["bash", "-n", str(ROOT / "scripts" / "run_maven.sh")],
            check=False,
            capture_output=True,
            text=True,
        )
        self.assertEqual(result.returncode, 0, result.stderr)

    def test_github_tycho_workflows_do_not_use_runner_maven(self):
        workflows = (
            ".github/workflows/build.yml",
            ".github/workflows/publish-build-artifacts.yml",
            ".github/workflows/release.yml",
        )
        for relative in workflows:
            content = self.read(relative)
            self.assertIn("bash scripts/run_maven.sh", content, relative)
            self.assertNotIn("run: mvn -B -ntp", content, relative)
            self.assertNotIn("\n          mvn -B -ntp", content, relative)


if __name__ == "__main__":
    unittest.main()
