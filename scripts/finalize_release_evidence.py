#!/usr/bin/env python3
"""Generate release evidence from the final publishable KIDE archives.

Run this only after native signing/notarisation. It intentionally recomputes all
artifact digests so release evidence can never describe pre-signing bytes.
"""

from __future__ import annotations

import argparse
import hashlib
import json
import re
import sys
import tarfile
import zipfile
from pathlib import Path, PurePosixPath
from typing import BinaryIO, Iterable, Iterator


PLATFORMS = (
    ("windows-x86_64", ".zip"),
    ("linux-x86_64", ".tar.gz"),
    ("macos-x86_64", ".tar.gz"),
    ("macos-aarch64", ".tar.gz"),
)
PLUGIN_FILE = re.compile(r"^(?P<name>.+)_(?P<version>[0-9][^/]*)\.jar$")


class EvidenceError(RuntimeError):
    pass


def sha256_stream(stream: BinaryIO) -> str:
    digest = hashlib.sha256()
    for chunk in iter(lambda: stream.read(1024 * 1024), b""):
        digest.update(chunk)
    return digest.hexdigest()


def sha256_file(path: Path) -> str:
    with path.open("rb") as stream:
        return sha256_stream(stream)


def plugin_identity(path: str) -> tuple[str, str]:
    filename = PurePosixPath(path).name
    match = PLUGIN_FILE.match(filename)
    if match:
        return match.group("name"), match.group("version")
    return filename.removesuffix(".jar"), "unknown"


def iter_plugins(path: Path) -> Iterator[dict[str, object]]:
    def component(member_name: str, stream: BinaryIO, size: int) -> dict[str, object]:
        name, version = plugin_identity(member_name)
        digest = sha256_stream(stream)
        return {
            "type": "library",
            "bom-ref": f"urn:kide:eclipse-plugin:{name}:{version}:sha256:{digest}",
            "name": name,
            "version": version,
            "hashes": [{"alg": "SHA-256", "content": digest}],
            "properties": [
                {"name": "kide:archiveMember", "value": member_name},
                {"name": "kide:size", "value": str(size)},
            ],
        }

    if path.name.endswith(".zip"):
        with zipfile.ZipFile(path) as archive:
            for info in archive.infolist():
                normalized = info.filename.replace("\\", "/")
                if info.is_dir() or "/plugins/" not in f"/{normalized}" or not normalized.endswith(".jar"):
                    continue
                with archive.open(info) as stream:
                    yield component(normalized, stream, info.file_size)
        return

    if path.name.endswith(".tar.gz"):
        with tarfile.open(path, mode="r:gz") as archive:
            for info in archive.getmembers():
                normalized = info.name.replace("\\", "/")
                if not info.isfile() or "/plugins/" not in f"/{normalized}" or not normalized.endswith(".jar"):
                    continue
                stream = archive.extractfile(info)
                if stream is None:
                    raise EvidenceError(f"Could not read {normalized} from {path.name}")
                with stream:
                    yield component(normalized, stream, info.size)
        return

    raise EvidenceError(f"Unsupported product archive: {path}")


def stable_product_files(dist: Path, version: str) -> list[tuple[str, Path]]:
    result: list[tuple[str, Path]] = []
    for platform, extension in PLATFORMS:
        path = dist / f"KIDE-{version}-{platform}{extension}"
        if not path.is_file():
            raise EvidenceError(f"Missing final release archive: {path.name}")
        result.append((platform, path))
    return result


def write_checksums(dist: Path, files: Iterable[Path]) -> None:
    lines = [f"{sha256_file(path)}  {path.name}" for path in sorted(files, key=lambda item: item.name)]
    (dist / "SHA256SUMS.txt").write_text("\n".join(lines) + "\n", encoding="utf-8")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--dist", type=Path, required=True)
    parser.add_argument("--version", required=True)
    parser.add_argument("--windows-authenticode-verified", action="store_true")
    parser.add_argument("--macos-notarization-verified", action="store_true")
    args = parser.parse_args()

    dist = args.dist.resolve()
    if not dist.is_dir():
        raise EvidenceError(f"Release directory does not exist: {dist}")
    version = args.version.removeprefix("v").strip()
    if not version or "/" in version or "\\" in version:
        raise EvidenceError(f"Invalid version: {args.version!r}")

    products = stable_product_files(dist, version)
    p2_archive = dist / "kide-p2-repository.zip"
    if not p2_archive.is_file():
        raise EvidenceError("Missing kide-p2-repository.zip")

    manifest_path = dist / "release-manifest.json"
    manifest: dict[str, object]
    if manifest_path.is_file():
        manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
    else:
        manifest = {"schemaVersion": 2, "product": "KIDE Modelling Studio", "version": version}

    previous = {
        item.get("platform"): item
        for item in manifest.get("artifacts", [])
        if isinstance(item, dict) and isinstance(item.get("platform"), str)
    }
    artifacts: list[dict[str, object]] = []
    for platform, path in products:
        entry: dict[str, object] = dict(previous.get(platform, {}))
        entry.update({"platform": platform, "file": path.name, "sha256": sha256_file(path)})
        artifacts.append(entry)

    unique_components: dict[tuple[str, str, str], dict[str, object]] = {}
    for _, archive in products:
        for item in iter_plugins(archive):
            digest = str(item["hashes"][0]["content"])  # type: ignore[index]
            key = (str(item["name"]), str(item["version"]), digest)
            if key not in unique_components:
                unique_components[key] = item

    sbom_name = f"KIDE-{version}-sbom.cdx.json"
    sbom_path = dist / sbom_name
    sbom = {
        "bomFormat": "CycloneDX",
        "specVersion": "1.5",
        "version": 1,
        "metadata": {
            "component": {
                "type": "application",
                "name": "KIDE Modelling Studio",
                "version": version,
            },
            "properties": [
                {"name": "kide:evidenceSource", "value": "final-publishable-archives"},
            ],
        },
        "components": sorted(
            unique_components.values(),
            key=lambda item: (str(item["name"]), str(item["version"]), str(item["bom-ref"])),
        ),
    }
    sbom_path.write_text(json.dumps(sbom, indent=2, sort_keys=True) + "\n", encoding="utf-8")

    manifest.update(
        {
            "schemaVersion": 2,
            "product": "KIDE Modelling Studio",
            "version": version,
            "artifacts": artifacts,
            "supplyChain": {
                "sbom": sbom_name,
                "sbomFormat": "CycloneDX 1.5",
                "windowsAuthenticodeVerified": bool(args.windows_authenticode_verified),
                "macOSNotarizationVerified": bool(args.macos_notarization_verified),
                "provenance": "GitHub artifact attestation emitted by release workflow",
            },
        }
    )
    manifest_path.write_text(json.dumps(manifest, indent=2, sort_keys=True) + "\n", encoding="utf-8")

    checksum_inputs = [path for _, path in products] + [p2_archive, manifest_path, sbom_path]
    write_checksums(dist, checksum_inputs)
    print(
        f"Finalized {len(products)} product archives and {len(unique_components)} unique Eclipse plug-ins; "
        f"SBOM={sbom_path.name}"
    )
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except EvidenceError as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        raise SystemExit(2)
