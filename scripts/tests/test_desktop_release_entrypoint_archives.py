from __future__ import annotations

import importlib.util
import io
from pathlib import Path
import tarfile
import tempfile
import unittest
import zipfile

ROOT = Path(__file__).resolve().parents[2]
MODULE_PATH = ROOT / "scripts" / "prepare_desktop_release.py"
SPEC = importlib.util.spec_from_file_location("prepare_desktop_release", MODULE_PATH)
assert SPEC and SPEC.loader
release = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(release)


class DesktopReleaseEntrypointArchiveTest(unittest.TestCase):
    def test_windows_native_launcher_is_staged_as_kide_exe(self):
        platform = release.Platform(
            "windows-x86_64", "win32.win32.x86_64", ".zip", "windows-x86_64"
        )
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            source = root / "source.zip"
            destination = root / "destination.zip"
            with zipfile.ZipFile(source, "w") as archive:
                archive.writestr("KIDE/kide.exe", b"native-exe")
                archive.writestr("KIDE/kide.ini", b"-startup\n")

            entry = release.stage_release_archive(
                source, destination, platform, {"launcher": "KIDE/kide.exe"}
            )

            self.assertEqual(entry, "KIDE/KIDE.exe")
            with zipfile.ZipFile(destination) as archive:
                self.assertIn("KIDE/KIDE.exe", archive.namelist())
                self.assertNotIn("KIDE/kide.exe", archive.namelist())
                self.assertEqual(archive.read("KIDE/KIDE.exe"), b"native-exe")

    def test_linux_release_gets_executable_shell_entrypoint(self):
        platform = release.Platform(
            "linux-x86_64", "linux.gtk.x86_64", ".tar.gz", "linux-x86_64"
        )
        self._assert_tar_wrapper(
            platform,
            native_path="KIDE/kide",
            expected_wrapper="KIDE/KIDE.sh",
        )

    def test_macos_release_gets_executable_command_entrypoint(self):
        platform = release.Platform(
            "macos-aarch64", "macosx.cocoa.aarch64", ".tar.gz", "macos-aarch64"
        )
        self._assert_tar_wrapper(
            platform,
            native_path="KIDE/KIDE.app/Contents/MacOS/kide",
            expected_wrapper="KIDE/KIDE.command",
        )

    def _assert_tar_wrapper(self, platform, native_path: str, expected_wrapper: str):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            source = root / "source.tar.gz"
            destination = root / "destination.tar.gz"

            payload = b"native-launcher"
            with tarfile.open(source, "w:gz") as archive:
                native = tarfile.TarInfo(native_path)
                native.size = len(payload)
                native.mode = 0o755
                archive.addfile(native, io.BytesIO(payload))

            entry = release.stage_release_archive(
                source, destination, platform, {"launcher": native_path}
            )
            self.assertEqual(entry, expected_wrapper)

            with tarfile.open(destination, "r:gz") as archive:
                wrapper = archive.getmember(expected_wrapper)
                self.assertEqual(wrapper.mode & 0o777, 0o755)
                stream = archive.extractfile(wrapper)
                self.assertIsNotNone(stream)
                self.assertTrue(stream.read().startswith(b"#!"))


if __name__ == "__main__":
    unittest.main()
