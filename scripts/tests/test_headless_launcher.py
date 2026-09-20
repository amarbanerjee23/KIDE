from __future__ import annotations

import shutil
import subprocess
import tempfile
import unittest
from pathlib import Path


ROOT = Path(__file__).resolve().parents[2]
LAUNCHER = (
    ROOT
    / "releng"
    / "com.kide.languageserver.feature"
    / "rootfiles"
    / "linux"
    / "kide-languageserver-headless"
)


class HeadlessLauncherTests(unittest.TestCase):
    def _runtime(self) -> tuple[tempfile.TemporaryDirectory[str], Path, Path]:
        temporary = tempfile.TemporaryDirectory()
        root = Path(temporary.name)
        launcher = root / "kide-languageserver-headless"
        shutil.copy2(LAUNCHER, launcher)
        launcher.chmod(0o755)

        java = (
            root
            / "plugins"
            / "org.eclipse.justj.openjdk.hotspot.jre.full.stripped.linux.x86_64_21.test"
            / "jre"
            / "bin"
            / "java"
        )
        java.parent.mkdir(parents=True)
        java.write_text(
            "#!/bin/sh\nprintf '%s\\n' \"$@\"\n",
            encoding="utf-8",
        )
        java.chmod(0o755)

        equinox = root / "plugins" / "org.eclipse.equinox.launcher_1.0.0.jar"
        equinox.write_bytes(b"test")
        (root / "configuration").mkdir()
        return temporary, launcher, java

    def _arguments(self, *arguments: str) -> list[str]:
        temporary, launcher, _ = self._runtime()
        try:
            result = subprocess.run(
                [str(launcher), *arguments],
                check=True,
                text=True,
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
            )
            return result.stdout.splitlines()
        finally:
            temporary.cleanup()

    def test_default_invocation_selects_language_server_application(self) -> None:
        arguments = self._arguments("-consoleLog")
        index = arguments.index("-application")
        self.assertEqual("com.kide.languageserver.application", arguments[index + 1])
        self.assertEqual(1, arguments.count("-application"))

    def test_explicit_application_is_not_shadowed_by_default(self) -> None:
        application = "com.kide.languageserver.gateway.selfcheck"
        arguments = self._arguments("-application", application, "-consoleLog")
        index = arguments.index("-application")
        self.assertEqual(application, arguments[index + 1])
        self.assertEqual(1, arguments.count("-application"))

    def test_enterprise_api_application_can_override_default(self) -> None:
        application = "com.kide.enterprise.server.selfcheck"
        arguments = self._arguments("-application", application)
        index = arguments.index("-application")
        self.assertEqual(application, arguments[index + 1])
        self.assertEqual(1, arguments.count("-application"))


if __name__ == "__main__":
    unittest.main()
