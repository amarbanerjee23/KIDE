#!/usr/bin/env python3
"""Run the E04 enterprise-context self-check from the packaged Linux KIDE product."""

from __future__ import annotations

import argparse
import os
import re
import shutil
import subprocess
import sys
import tarfile
import tempfile
from pathlib import Path


class SmokeError(RuntimeError):
    pass


def find_linux_archive(products: Path) -> Path:
    matches = sorted(
        p for p in products.iterdir()
        if p.is_file() and "linux.gtk.x86_64" in p.name and p.name.endswith(".tar.gz")
    )
    if len(matches) != 1:
        raise SmokeError(f"expected one Linux desktop archive, found {len(matches)}")
    return matches[0]


def safe_extract(archive: Path, destination: Path) -> None:
    root = destination.resolve()
    with tarfile.open(archive, "r:gz") as tar:
        members = tar.getmembers()
        for member in members:
            target = (destination / member.name).resolve()
            if target != root and root not in target.parents:
                raise SmokeError(f"unsafe archive member: {member.name}")
        tar.extractall(destination)


def find_launcher(root: Path) -> Path:
    candidates: list[Path] = []
    for path in root.rglob("kide"):
        if not path.is_file():
            continue
        lowered = {part.lower() for part in path.parts}
        if {"plugins", "jre", "jdk"} & lowered:
            continue
        candidates.append(path)
    if not candidates:
        raise SmokeError("packaged Linux KIDE launcher was not found")
    candidates.sort(key=lambda path: (len(path.parts), str(path)))
    return candidates[0]


def suspicious_output(text: str) -> list[str]:
    patterns = (
        re.compile(r"\bexception\b", re.IGNORECASE),
        re.compile(r"\berror\b", re.IGNORECASE),
        re.compile(r"!ENTRY\s+\S+\s+4\s+", re.IGNORECASE),
        re.compile(r"unresolved requirement", re.IGNORECASE),
    )
    lines: list[str] = []
    for line in text.splitlines():
        if any(pattern.search(line) for pattern in patterns):
            lines.append(line)
    return lines


def run_selfcheck(archive: Path) -> None:
    with tempfile.TemporaryDirectory(prefix="kide-e04-runtime-") as temp_dir:
        root = Path(temp_dir)
        safe_extract(archive, root)
        launcher = find_launcher(root)
        mode = launcher.stat().st_mode
        launcher.chmod(mode | 0o111)
        workspace = root / "selfcheck-workspace"
        workspace.mkdir()

        command = [
            str(launcher),
            "-nosplash",
            "-consoleLog",
            "-application", "com.kide.enterprise.context.selfcheck",
            "-data", str(workspace),
        ]
        env = os.environ.copy()
        result = subprocess.run(
            command,
            cwd=launcher.parent,
            env=env,
            text=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            timeout=90,
            check=False,
        )
        output = result.stdout or ""
        if result.returncode != 0:
            raise SmokeError(f"packaged E04 self-check exited {result.returncode}\n{output[-6000:]}")
        if "KIDE E04 SELF-CHECK OK" not in output:
            raise SmokeError(f"packaged E04 self-check success marker missing\n{output[-6000:]}")
        suspicious = suspicious_output(output.replace("KIDE E04 SELF-CHECK OK", ""))
        if suspicious:
            detail = "\n".join(suspicious[-20:])
            raise SmokeError(f"packaged E04 self-check emitted error/exception signatures:\n{detail}")
        print(f"E04 PACKAGED RUNTIME QUALIFIED: {archive.name}")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--products", required=True, type=Path)
    args = parser.parse_args()
    products = args.products
    if not products.is_dir():
        raise SmokeError(f"products directory does not exist: {products}")
    run_selfcheck(find_linux_archive(products))
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (SmokeError, subprocess.TimeoutExpired) as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        raise SystemExit(2)
