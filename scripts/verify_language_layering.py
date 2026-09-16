#!/usr/bin/env python3
"""Enforce the client-neutral Xtext language-layer contract.

Runtime (``*.dsl``) and IDE (``*.dsl.ide``) bundles are shared by Eclipse and
future LSP/browser clients. They must therefore not acquire Eclipse UI/SWT/JFace
or Xtext-UI dependencies. UI bundles remain free to adapt those services to the
Eclipse workbench.
"""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path
from typing import Iterable, List, Sequence

FORBIDDEN_PACKAGE_PREFIXES: Sequence[str] = (
    "org.eclipse.ui",
    "org.eclipse.jface",
    "org.eclipse.swt",
    "org.eclipse.xtext.ui",
)

SOURCE_SUFFIXES = {".java", ".xtend"}


def is_shared_language_module(path: Path) -> bool:
    name = path.name
    return name.endswith(".dsl") or name.endswith(".dsl.ide")


def discover_shared_language_modules(root: Path) -> List[Path]:
    return sorted(
        path
        for path in root.iterdir()
        if path.is_dir()
        and is_shared_language_module(path)
        and (path / "META-INF" / "MANIFEST.MF").is_file()
    )


def _source_files(module: Path) -> Iterable[Path]:
    for source_root_name in ("src", "src-gen"):
        source_root = module / source_root_name
        if not source_root.exists():
            continue
        for path in source_root.rglob("*"):
            if path.is_file() and path.suffix in SOURCE_SUFFIXES:
                yield path


def _is_forbidden_package(value: str) -> bool:
    return any(value == prefix or value.startswith(prefix + ".") for prefix in FORBIDDEN_PACKAGE_PREFIXES)


def _source_violations(root: Path, module: Path) -> List[str]:
    violations: List[str] = []
    import_pattern = re.compile(r"^\s*import\s+([\w.]+)", re.MULTILINE)
    for path in _source_files(module):
        text = path.read_text(encoding="utf-8", errors="replace")
        for imported in import_pattern.findall(text):
            if _is_forbidden_package(imported):
                violations.append(
                    f"{path.relative_to(root)}: shared language layer imports client UI package {imported}"
                )
    return violations


def _logical_manifest_lines(text: str) -> Iterable[str]:
    current = ""
    for line in text.splitlines():
        if line.startswith(" ") and current:
            current += line[1:]
        else:
            if current:
                yield current
            current = line
    if current:
        yield current


def _required_bundles(manifest_text: str) -> List[str]:
    for line in _logical_manifest_lines(manifest_text):
        if line.startswith("Require-Bundle:"):
            raw = line.split(":", 1)[1]
            return [entry.split(";", 1)[0].strip() for entry in raw.split(",") if entry.strip()]
    return []


def _manifest_violations(root: Path, module: Path) -> List[str]:
    path = module / "META-INF" / "MANIFEST.MF"
    required = _required_bundles(path.read_text(encoding="utf-8", errors="replace"))
    violations: List[str] = []

    for bundle in required:
        if _is_forbidden_package(bundle):
            violations.append(
                f"{path.relative_to(root)}: shared language layer requires client UI bundle {bundle}"
            )
        if bundle.endswith(".ui") or ".ui." in bundle:
            violations.append(
                f"{path.relative_to(root)}: shared language layer requires UI bundle {bundle}"
            )
        if module.name.endswith(".dsl") and (bundle.endswith(".ide") or ".ide." in bundle):
            violations.append(
                f"{path.relative_to(root)}: runtime language layer must not depend on IDE bundle {bundle}"
            )
    return violations


def verify(root: Path) -> List[str]:
    root = root.resolve()
    modules = discover_shared_language_modules(root)
    if not modules:
        return ["No shared Xtext language modules (*.dsl / *.dsl.ide) were discovered"]

    violations: List[str] = []
    for module in modules:
        violations.extend(_source_violations(root, module))
        violations.extend(_manifest_violations(root, module))
    return violations


def main(argv: Sequence[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=Path(__file__).resolve().parents[1])
    args = parser.parse_args(argv)

    violations = verify(args.root)
    if violations:
        print("KIDE shared-language layering check FAILED:", file=sys.stderr)
        for violation in violations:
            print(f" - {violation}", file=sys.stderr)
        return 1

    modules = discover_shared_language_modules(args.root.resolve())
    print(f"KIDE shared-language layering check passed for {len(modules)} runtime/IDE bundles.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
