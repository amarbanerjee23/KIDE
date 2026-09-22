#!/usr/bin/env python3
from __future__ import annotations

import argparse
import hashlib
import json
import os
import subprocess
import sys
import tempfile
from pathlib import Path

HEADLESS_LINUX_LAUNCHER = "kide-languageserver-headless"
MARKER = "KIDE PR38 SEMANTIC CODEGEN SELF-CHECK OK:"
MANIFEST = "generation-manifest.json"

class SmokeFailure(RuntimeError):
    pass

def find_linux_launcher(products: Path) -> Path:
    candidates = [
        path for path in products.rglob(HEADLESS_LINUX_LAUNCHER)
        if path.is_file()
        and "linux" in {part.lower() for part in path.parts}
        and os.access(path, os.X_OK)
        and "plugins" not in {part.lower() for part in path.parts}
    ]
    if len(candidates) != 1:
        rendered = ", ".join(str(path) for path in candidates) or "none"
        raise SmokeFailure(
            f"expected exactly one packaged Linux headless launcher, found: {rendered}"
        )
    return candidates[0].resolve()

def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()

def validate_manifest(output: Path) -> dict:
    path = output / MANIFEST
    if not path.is_file():
        raise SmokeFailure("generation manifest is missing")
    try:
        manifest = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        raise SmokeFailure(f"generation manifest is invalid: {exc}") from exc

    if manifest.get("schemaVersion") != "1":
        raise SmokeFailure("unexpected generation manifest schema")
    if manifest.get("toolchainVersion") != "1":
        raise SmokeFailure("unexpected generation toolchain version")
    fingerprint = manifest.get("fingerprint")
    if not isinstance(fingerprint, str) or len(fingerprint) != 64:
        raise SmokeFailure("manifest fingerprint is not SHA-256")
    artifacts = manifest.get("artifacts")
    if not isinstance(artifacts, list) or len(artifacts) != 1:
        raise SmokeFailure("self-check must generate exactly one reference artifact")
    artifact = artifacts[0]
    relative = artifact.get("path")
    if relative != "generated/GeneratedObserveBinding.java":
        raise SmokeFailure(f"unexpected generated path: {relative}")
    target = output / relative
    if not target.is_file():
        raise SmokeFailure("generated Java artifact is missing")
    if artifact.get("sha256") != sha256(target):
        raise SmokeFailure("generated artifact hash does not match manifest")
    if artifact.get("targetId") != "java" or artifact.get("targetVersion") != "1":
        raise SmokeFailure("reference Java target provenance is missing")
    return manifest

def compile_java(output: Path) -> None:
    source = output / "generated" / "GeneratedObserveBinding.java"
    classes = output / "compiled"
    classes.mkdir(parents=True, exist_ok=True)
    result = subprocess.run(
        ["javac", "-encoding", "UTF-8", "-d", str(classes), str(source)],
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        timeout=60,
        check=False,
    )
    if result.returncode != 0:
        raise SmokeFailure(
            f"generated Java reference target did not compile ({result.returncode})\n"
            f"{(result.stdout or '')[-8000:]}"
        )
    if not (classes / "GeneratedObserveBinding.class").is_file():
        raise SmokeFailure("compiled reference target class is missing")

def run_selfcheck(launcher: Path, output: Path) -> None:
    output.mkdir(parents=True, exist_ok=True)
    result = subprocess.run(
        [
            str(launcher),
            "-nosplash",
            "-consoleLog",
            "-application",
            "com.kide.codegen.selfcheck",
            "--output",
            str(output.resolve()),
        ],
        cwd=launcher.parent,
        env=os.environ.copy(),
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        timeout=180,
        check=False,
    )
    console = result.stdout or ""
    if result.returncode != 0 or MARKER not in console:
        raise SmokeFailure(
            f"packaged PR38 codegen self-check failed ({result.returncode})\n"
            f"{console[-12000:]}"
        )
    validate_manifest(output)
    compile_java(output)
    print(f"PR38 PACKAGED SEMANTIC CODEGEN QUALIFIED: {output}")

def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--products", required=True, type=Path)
    parser.add_argument("--output", required=True, type=Path)
    args = parser.parse_args()
    if not args.products.is_dir():
        raise SmokeFailure(f"products directory does not exist: {args.products}")
    run_selfcheck(find_linux_launcher(args.products), args.output)
    return 0

if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (SmokeFailure, subprocess.TimeoutExpired) as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        raise SystemExit(2)
