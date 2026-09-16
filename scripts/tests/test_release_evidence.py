from __future__ import annotations

import io
import json
import sys
import tarfile
import tempfile
import unittest
import zipfile
from pathlib import Path
from unittest.mock import patch

from scripts import finalize_release_evidence


class ReleaseEvidenceTests(unittest.TestCase):
    def _write_tar(self, path: Path) -> None:
        payload = b"fake-eclipse-plugin"
        with tarfile.open(path, "w:gz") as archive:
            info = tarfile.TarInfo("KIDE/plugins/org.example.bundle_1.2.3.jar")
            info.size = len(payload)
            archive.addfile(info, io.BytesIO(payload))

    def test_final_evidence_uses_publishable_archives(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            dist = Path(directory)
            version = "1.2.3"
            with zipfile.ZipFile(dist / f"KIDE-{version}-windows-x86_64.zip", "w") as archive:
                archive.writestr("KIDE/plugins/org.example.bundle_1.2.3.jar", b"fake-eclipse-plugin")
            self._write_tar(dist / f"KIDE-{version}-linux-x86_64.tar.gz")
            self._write_tar(dist / f"KIDE-{version}-macos-x86_64.tar.gz")
            self._write_tar(dist / f"KIDE-{version}-macos-aarch64.tar.gz")
            with zipfile.ZipFile(dist / "kide-p2-repository.zip", "w") as archive:
                archive.writestr("content.jar", b"p2")

            with patch.object(
                sys,
                "argv",
                [
                    "finalize_release_evidence.py",
                    "--dist",
                    str(dist),
                    "--version",
                    version,
                    "--windows-authenticode-verified",
                    "--macos-notarization-verified",
                ],
            ):
                self.assertEqual(0, finalize_release_evidence.main())

            manifest = json.loads((dist / "release-manifest.json").read_text(encoding="utf-8"))
            self.assertEqual(4, len(manifest["artifacts"]))
            self.assertTrue(manifest["supplyChain"]["windowsAuthenticodeVerified"])
            self.assertTrue(manifest["supplyChain"]["macOSNotarizationVerified"])

            sbom = json.loads((dist / f"KIDE-{version}-sbom.cdx.json").read_text(encoding="utf-8"))
            self.assertEqual("CycloneDX", sbom["bomFormat"])
            self.assertEqual(1, len(sbom["components"]))
            self.assertEqual("org.example.bundle", sbom["components"][0]["name"])
            checksums = (dist / "SHA256SUMS.txt").read_text(encoding="utf-8")
            self.assertIn(f"KIDE-{version}-windows-x86_64.zip", checksums)
            self.assertIn(f"KIDE-{version}-sbom.cdx.json", checksums)


if __name__ == "__main__":
    unittest.main()
