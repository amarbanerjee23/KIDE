#!/usr/bin/env python3
"""Run PR26 enterprise HTTP service qualification from packaged headless bytes."""

from __future__ import annotations

import argparse
import os
import subprocess
import sys
from pathlib import Path


HEADLESS_LINUX_LAUNCHER = "kide-languageserver-headless"
MARKERS = (
    "KIDE PR26 ENTERPRISE API SELF-CHECK OK",
    "KIDE PR33 ENTERPRISE API COLLABORATION SELF-CHECK OK",
    "KIDE PR35 ENTERPRISE KNOWLEDGE CATALOGUE SELF-CHECK OK",
    "KIDE PR36 ENTERPRISE SYNTHESIS SELF-CHECK OK",
)


class SmokeFailure(RuntimeError):
    pass


def find_linux_launcher(products: Path) -> Path:
    candidates = [
        path
        for path in products.rglob(HEADLESS_LINUX_LAUNCHER)
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


def run_selfcheck(launcher: Path) -> None:
    result = subprocess.run(
        [
            str(launcher),
            "-nosplash",
            "-consoleLog",
            "-application",
            "com.kide.enterprise.server.selfcheck",
        ],
        cwd=launcher.parent,
        env=os.environ.copy(),
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        timeout=180,
        check=False,
    )
    output = result.stdout or ""
    if result.returncode != 0:
        raise SmokeFailure(
            f"packaged PR26 API self-check exited {result.returncode}\n{output[-10000:]}"
        )
    missing = [marker for marker in MARKERS if marker not in output]
    if missing:
        raise SmokeFailure(
            f"packaged enterprise API success markers missing: {missing}\n{output[-10000:]}"
        )
    print(f"PR35 PACKAGED ENTERPRISE API + KNOWLEDGE QUALIFIED: {launcher}")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--products", required=True, type=Path)
    args = parser.parse_args()
    if not args.products.is_dir():
        raise SmokeFailure(f"products directory does not exist: {args.products}")
    run_selfcheck(find_linux_launcher(args.products))
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (SmokeFailure, subprocess.TimeoutExpired) as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        raise SystemExit(2)
