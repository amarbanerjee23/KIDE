#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import os
import subprocess
import sys
from pathlib import Path

HEADLESS_LINUX_LAUNCHER = "kide-languageserver-headless"
MARKER = "KIDE PR37 RECONFIGURATION SCALE QUALIFIED:"
EXPECTED_COUNTS = [10, 50, 100, 500, 1000]

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

def validate_evidence(path: Path) -> None:
    if not path.is_file():
        raise SmokeFailure(f"PR37 benchmark evidence was not created: {path}")
    data = json.loads(path.read_text(encoding="utf-8"))
    if data.get("schema") != "kide-pr37-reconfiguration-benchmark-v1":
        raise SmokeFailure("unexpected PR37 benchmark schema")
    if data.get("timingPolicy") != "evidence-only-on-hosted-runner":
        raise SmokeFailure("benchmark timing policy is missing")
    scenarios = data.get("scenarios")
    if not isinstance(scenarios, list):
        raise SmokeFailure("benchmark scenarios are missing")
    counts = [row.get("deviceCount") for row in scenarios]
    if counts != EXPECTED_COUNTS:
        raise SmokeFailure(f"unexpected benchmark device counts: {counts}")
    for row in scenarios:
        requirement_count = row.get("requirementCount")
        if not isinstance(requirement_count, int) or requirement_count < 1:
            raise SmokeFailure("invalid requirement count")
        if row.get("preservedBindings") != requirement_count - 1:
            raise SmokeFailure(
                f"non-minimal preserved binding count for {row.get('deviceCount')}"
            )
        if row.get("migratedBindings") != 1:
            raise SmokeFailure(
                f"expected exactly one migrated binding for {row.get('deviceCount')}"
            )
        for metric in (
            "initialMatchNanos",
            "reconfigurationNanos",
            "threadCpuNanos",
        ):
            value = row.get(metric)
            if not isinstance(value, int) or value < 0:
                raise SmokeFailure(
                    f"invalid {metric} for {row.get('deviceCount')}: {value}"
                )
        fingerprint = row.get("fingerprint")
        if not isinstance(fingerprint, str) or len(fingerprint) != 64:
            raise SmokeFailure("invalid deterministic fingerprint")

def run_benchmark(launcher: Path, output: Path) -> None:
    output.parent.mkdir(parents=True, exist_ok=True)
    result = subprocess.run(
        [
            str(launcher),
            "-nosplash",
            "-consoleLog",
            "-application",
            "com.kide.synthesis.reconfigurationBenchmark",
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
            f"packaged PR37 benchmark failed ({result.returncode})\n{console[-12000:]}"
        )
    validate_evidence(output)
    print(f"PR37 PACKAGED RECONFIGURATION SCALE QUALIFIED: {output}")

def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--products", required=True, type=Path)
    parser.add_argument("--output", required=True, type=Path)
    args = parser.parse_args()
    if not args.products.is_dir():
        raise SmokeFailure(f"products directory does not exist: {args.products}")
    run_benchmark(find_linux_launcher(args.products), args.output)
    return 0

if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (SmokeFailure, subprocess.TimeoutExpired, json.JSONDecodeError) as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        raise SystemExit(2)
