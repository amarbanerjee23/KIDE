#!/usr/bin/env python3
"""Qualify and stage KIDE desktop product archives.

This script is deliberately dependency-free so the exact same checks run in
pull-request CI and in the release workflow.  A desktop archive is publishable
only when it contains:

* the expected native Eclipse launcher;
* an embedded Java runtime (KIDE must not depend on a machine-wide JRE);
* Eclipse configuration and plug-ins; and
* executable launcher/runtime bits on Unix archives.

When --output is supplied, qualified Tycho archives are copied to stable,
customer-facing file names and a machine-readable release manifest is emitted.
"""

from __future__ import annotations

import argparse
import hashlib
import json
import shutil
import sys
import tarfile
import zipfile
from dataclasses import dataclass
from pathlib import Path, PurePosixPath
from typing import Iterable


JUSTJ_VERSION = "17.0.20"


@dataclass(frozen=True)
class Platform:
    key: str
    tycho_token: str
    extension: str
    release_slug: str


PLATFORMS = (
    Platform("windows-x86_64", "win32.win32.x86_64", ".zip", "windows-x86_64"),
    Platform("linux-x86_64", "linux.gtk.x86_64", ".tar.gz", "linux-x86_64"),
    Platform("macos-x86_64", "macosx.cocoa.x86_64", ".tar.gz", "macos-x86_64"),
    Platform("macos-aarch64", "macosx.cocoa.aarch64", ".tar.gz", "macos-aarch64"),
)


@dataclass(frozen=True)
class Member:
    name: str
    mode: int

    @property
    def path(self) -> PurePosixPath:
        return PurePosixPath(self.name.lstrip("./"))


class QualificationError(RuntimeError):
    pass


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def archive_members(path: Path) -> list[Member]:
    if path.name.endswith(".zip"):
        with zipfile.ZipFile(path) as archive:
            return [
                Member(info.filename.replace("\\", "/"), (info.external_attr >> 16) & 0o777)
                for info in archive.infolist()
                if not info.is_dir()
            ]

    if path.name.endswith(".tar.gz"):
        with tarfile.open(path, mode="r:gz") as archive:
            return [
                Member(info.name.replace("\\", "/"), info.mode & 0o777)
                for info in archive.getmembers()
                if info.isfile()
            ]

    raise QualificationError(f"Unsupported archive type: {path}")


def find_product_archive(products_dir: Path, platform: Platform) -> Path:
    matches = sorted(
        path
        for path in products_dir.iterdir()
        if path.is_file()
        and platform.tycho_token in path.name
        and path.name.endswith(platform.extension)
    )
    if len(matches) != 1:
        names = ", ".join(path.name for path in matches) or "none"
        raise QualificationError(
            f"Expected exactly one {platform.key} archive containing "
            f"'{platform.tycho_token}', found {len(matches)}: {names}"
        )
    return matches[0]


def is_runtime_java(member: Member) -> bool:
    parts = tuple(part.lower() for part in member.path.parts)
    if "bin" not in parts or not ({"jre", "jdk"} & set(parts)):
        return False
    return member.path.name.lower() in {"java", "java.exe", "javaw.exe"}


def is_launcher(member: Member, platform: Platform) -> bool:
    parts = tuple(part.lower() for part in member.path.parts)
    if "plugins" in parts or "jre" in parts or "jdk" in parts:
        return False

    if platform.key.startswith("windows"):
        return member.path.name.lower() == "kide.exe"

    if platform.key.startswith("linux"):
        return member.path.name == "kide" and len(parts) <= 3

    return (
        member.path.name.lower() == "kide"
        and "kide.app" in parts
        and "contents" in parts
        and "macos" in parts
    )


def require_executable(member: Member, description: str) -> None:
    if member.mode & 0o111 == 0:
        raise QualificationError(
            f"{description} is present but is not executable in the archive: {member.name}"
        )


def qualify_archive(path: Path, platform: Platform) -> dict[str, str]:
    members = archive_members(path)
    if not members:
        raise QualificationError(f"Archive is empty: {path}")

    launchers = [member for member in members if is_launcher(member, platform)]
    if not launchers:
        raise QualificationError(f"{platform.key}: native KIDE launcher is missing from {path.name}")

    runtimes = [member for member in members if is_runtime_java(member)]
    if not runtimes:
        raise QualificationError(
            f"{platform.key}: embedded Java runtime is missing from {path.name}; "
            "the desktop product must run without a system JRE"
        )

    lower_names = [member.name.lower() for member in members]
    if not any(name.endswith("configuration/config.ini") for name in lower_names):
        raise QualificationError(f"{platform.key}: configuration/config.ini is missing")
    if not any("/plugins/" in f"/{name}" for name in lower_names):
        raise QualificationError(f"{platform.key}: Eclipse plug-ins are missing")

    if not platform.key.startswith("windows"):
        require_executable(launchers[0], f"{platform.key} KIDE launcher")
        executable_runtime = next((member for member in runtimes if member.mode & 0o111), None)
        if executable_runtime is None:
            raise QualificationError(
                f"{platform.key}: embedded Java runtime exists but no java binary is executable"
            )

    return {
        "launcher": launchers[0].name,
        "runtime": runtimes[0].name,
        "sha256": sha256(path),
    }


def write_checksums(output_dir: Path, filenames: Iterable[str]) -> None:
    lines = []
    for filename in sorted(filenames):
        path = output_dir / filename
        lines.append(f"{sha256(path)}  {filename}")
    (output_dir / "SHA256SUMS.txt").write_text("\n".join(lines) + "\n", encoding="utf-8")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--products", required=True, type=Path, help="Tycho target/products directory")
    parser.add_argument("--output", type=Path, help="Optional directory for staged release artifacts")
    parser.add_argument("--version", default="dev", help="Release version used in staged filenames")
    args = parser.parse_args()

    products_dir: Path = args.products
    if not products_dir.is_dir():
        raise QualificationError(f"Products directory does not exist: {products_dir}")

    version = args.version.removeprefix("v").strip()
    if not version or "/" in version or "\\" in version:
        raise QualificationError(f"Invalid release version: {args.version!r}")

    qualified: list[tuple[Platform, Path, dict[str, str]]] = []
    for platform in PLATFORMS:
        archive = find_product_archive(products_dir, platform)
        evidence = qualify_archive(archive, platform)
        qualified.append((platform, archive, evidence))
        print(
            f"QUALIFIED {platform.key}: {archive.name} "
            f"launcher={evidence['launcher']} runtime={evidence['runtime']}"
        )

    if args.output is None:
        print(f"Qualified {len(qualified)} standalone KIDE desktop archives.")
        return 0

    output_dir: Path = args.output
    output_dir.mkdir(parents=True, exist_ok=True)

    manifest = {
        "schemaVersion": 1,
        "product": "KIDE Modelling Studio",
        "version": version,
        "bundledRuntime": {
            "distribution": "Eclipse JustJ / Adoptium OpenJDK HotSpot",
            "javaVersion": JUSTJ_VERSION,
        },
        "artifacts": [],
    }
    staged_names: list[str] = []

    for platform, source, evidence in qualified:
        destination_name = f"KIDE-{version}-{platform.release_slug}{platform.extension}"
        destination = output_dir / destination_name
        shutil.copy2(source, destination)
        digest = sha256(destination)
        staged_names.append(destination_name)
        manifest["artifacts"].append(
            {
                "platform": platform.key,
                "file": destination_name,
                "sha256": digest,
                "launcher": evidence["launcher"],
                "runtime": evidence["runtime"],
            }
        )

    manifest_path = output_dir / "release-manifest.json"
    manifest_path.write_text(json.dumps(manifest, indent=2) + "\n", encoding="utf-8")
    staged_names.append(manifest_path.name)
    write_checksums(output_dir, staged_names)

    print(f"Staged qualified release in {output_dir}")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except QualificationError as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        raise SystemExit(2)
