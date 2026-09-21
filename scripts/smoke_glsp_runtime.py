#!/usr/bin/env python3
from __future__ import annotations

import argparse
import os
import subprocess
import sys
import tempfile
from pathlib import Path

HEADLESS_LINUX_LAUNCHER = "kide-languageserver-headless"
MARKER = "KIDE PR32 GLSP SELF-CHECK OK"


class SmokeError(RuntimeError):
    pass


def find_linux_launcher(products: Path) -> Path:
    candidates = [
        path
        for path in products.rglob(HEADLESS_LINUX_LAUNCHER)
        if path.is_file()
        and "linux" in {part.lower() for part in path.parts}
        and "plugins" not in {part.lower() for part in path.parts}
    ]
    if len(candidates) != 1:
        rendered = ", ".join(str(path) for path in candidates) or "none"
        raise SmokeError(
            f"expected one Linux language-server launcher, found: {rendered}"
        )
    launcher = candidates[0]
    launcher.chmod(launcher.stat().st_mode | 0o111)
    return launcher


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--products", required=True, type=Path)
    args = parser.parse_args()
    if not args.products.is_dir():
        raise SmokeError(f"products directory does not exist: {args.products}")

    launcher = find_linux_launcher(args.products.resolve())
    with tempfile.TemporaryDirectory(prefix="kide-pr32-packaged-glsp-") as td:
        workspace = Path(td) / "workspace"
        workspace.mkdir()
        environment = os.environ.copy()
        environment.pop("DISPLAY", None)
        environment.pop("WAYLAND_DISPLAY", None)
        result = subprocess.run(
            [
                str(launcher),
                "-nosplash",
                "-consoleLog",
                "-application",
                "com.kide.glsp.selfcheck",
                "-data",
                str(workspace),
            ],
            cwd=launcher.parent,
            env=environment,
            text=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            timeout=90,
            check=False,
        )
        output = result.stdout or ""
        if result.returncode != 0 or MARKER not in output:
            raise SmokeError(
                "packaged PR32 GLSP self-check failed "
                f"({result.returncode})\n{output[-8000:]}"
            )
    print("PR32 PACKAGED GLSP RUNTIME QUALIFIED")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (SmokeError, subprocess.TimeoutExpired) as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        raise SystemExit(2)
