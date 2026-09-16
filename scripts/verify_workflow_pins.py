#!/usr/bin/env python3
"""Fail when a GitHub Actions workflow uses a mutable external action reference."""

from __future__ import annotations

import re
import sys
from pathlib import Path

USES = re.compile(r"^\s*-?\s*uses:\s*([^\s#]+)")
PINNED = re.compile(r"^[^@\s]+@[0-9a-fA-F]{40}$")


def verify(root: Path) -> list[str]:
    errors: list[str] = []
    workflows = root / ".github" / "workflows"
    for path in sorted(workflows.glob("*.y*ml")):
        for number, line in enumerate(path.read_text(encoding="utf-8").splitlines(), start=1):
            match = USES.match(line)
            if not match:
                continue
            target = match.group(1).strip("'\"")
            if target.startswith("./") or target.startswith("docker://"):
                continue
            if not PINNED.fullmatch(target):
                errors.append(
                    f"{path.relative_to(root)}:{number}: external action must be pinned "
                    f"to a full 40-character commit SHA: {target}"
                )
    return errors


def main() -> int:
    root = Path(__file__).resolve().parents[1]
    errors = verify(root)
    if errors:
        print("GitHub Actions pinning check failed:", file=sys.stderr)
        for error in errors:
            print(f"  - {error}", file=sys.stderr)
        return 2
    print("GitHub Actions pinning check passed: every external action uses an immutable commit SHA.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
