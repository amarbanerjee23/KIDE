#!/usr/bin/env python3
"""Fail closed when KIDE's documented, registered and shipped language sets drift."""

from __future__ import annotations

import argparse
import json
import re
import xml.etree.ElementTree as ET
from pathlib import Path
from typing import Any

REQUIRED_FIELDS = {
    "id", "display_name", "extension", "language_id", "description", "grammar",
    "runtime_bundle", "ide_bundle", "ui_bundle", "setup_class",
    "valid_fixture", "invalid_fixture",
}
README_START = "<!-- KIDE-LANGUAGES:START -->"
README_END = "<!-- KIDE-LANGUAGES:END -->"


def load_registry(root: Path) -> list[dict[str, Any]]:
    path = root / "product" / "languages.json"
    data = json.loads(path.read_text(encoding="utf-8"))
    if data.get("schema_version") != 1:
        raise ValueError("product/languages.json must use schema_version 1")
    languages = data.get("languages")
    if not isinstance(languages, list):
        raise ValueError("product/languages.json languages must be a list")
    return languages


def expected_readme_block(languages: list[dict[str, Any]]) -> str:
    lines = [
        README_START,
        "| Extension | Language | What you describe |",
        "| --- | --- | --- |",
    ]
    for language in languages:
        lines.append(
            f"| `.{language['extension']}` | {language['display_name']} | {language['description']} |"
        )
    lines.append(README_END)
    return "\n".join(lines)


def _safe_repo_path(root: Path, value: str) -> Path:
    candidate = Path(value)
    if candidate.is_absolute() or ".." in candidate.parts:
        raise ValueError(f"unsafe repository path in language registry: {value}")
    return root / candidate


def verify_repository(root: Path) -> list[str]:
    errors: list[str] = []
    try:
        languages = load_registry(root)
    except (OSError, ValueError, json.JSONDecodeError) as exc:
        return [str(exc)]

    if len(languages) != 5:
        errors.append(f"expected exactly five production DSLs, found {len(languages)}")

    seen_ids: set[str] = set()
    seen_extensions: set[str] = set()
    seen_language_ids: set[str] = set()
    tuples: list[tuple[str, str, str]] = []

    for index, language in enumerate(languages):
        if not isinstance(language, dict):
            errors.append(f"language entry {index} must be an object")
            continue
        missing = sorted(REQUIRED_FIELDS - set(language))
        if missing:
            errors.append(f"language entry {index} missing fields: {', '.join(missing)}")
            continue

        for field, seen in (
            ("id", seen_ids),
            ("extension", seen_extensions),
            ("language_id", seen_language_ids),
        ):
            value = language[field]
            if not isinstance(value, str) or not value.strip():
                errors.append(f"language entry {index} has invalid {field}")
                continue
            if value in seen:
                errors.append(f"duplicate {field}: {value}")
            seen.add(value)

        extension = language["extension"]
        if not re.fullmatch(r"[a-z][a-z0-9]*", extension):
            errors.append(f"invalid file extension: {extension!r}")

        for field in ("grammar", "valid_fixture", "invalid_fixture"):
            try:
                path = _safe_repo_path(root, language[field])
            except ValueError as exc:
                errors.append(str(exc))
                continue
            if not path.is_file():
                errors.append(f"{field} does not exist for .{extension}: {language[field]}")
            if field.endswith("fixture") and path.suffix != f".{extension}":
                errors.append(
                    f"{field} extension mismatch for .{extension}: {language[field]}"
                )

        for field in ("runtime_bundle", "ide_bundle", "ui_bundle"):
            bundle = root / language[field]
            if not bundle.is_dir():
                errors.append(f"{field} directory does not exist for .{extension}: {language[field]}")

        tuples.append(
            (language["ide_bundle"], language["setup_class"], language["extension"])
        )

    registry_source = (
        root / "com.kide.languageserver" / "src" / "com" / "kide" /
        "languageserver" / "KideResourceServiceProviderRegistryProvider.java"
    )
    try:
        source = registry_source.read_text(encoding="utf-8")
        registered = re.findall(
            r'new\s+LanguageSetup\(\s*"([^"]+)"\s*,\s*"([^"]+)"\s*,\s*"([^"]+)"\s*\)',
            source,
        )
        if len(registered) != len(tuples) or set(registered) != set(tuples):
            errors.append(
                "product/languages.json does not exactly match the packaged LSP LanguageSetup registry"
            )
    except OSError as exc:
        errors.append(f"cannot read packaged LSP registry: {exc}")

    feature_path = root / "releng" / "com.kide.feature" / "feature.xml"
    try:
        feature = ET.parse(feature_path).getroot()
        plugin_ids = {node.attrib.get("id") for node in feature.findall("plugin")}
        for language in languages:
            for field in ("runtime_bundle", "ide_bundle", "ui_bundle"):
                if language[field] not in plugin_ids:
                    errors.append(
                        f"desktop feature does not ship {field} {language[field]} for .{language['extension']}"
                    )
    except (OSError, ET.ParseError) as exc:
        errors.append(f"cannot parse desktop feature.xml: {exc}")

    readme_path = root / "Readme.md"
    try:
        readme = readme_path.read_text(encoding="utf-8")
        expected = expected_readme_block(languages)
        start = readme.find(README_START)
        end = readme.find(README_END)
        actual = ""
        if start >= 0 and end >= start:
            actual = readme[start:end + len(README_END)]
        if actual != expected:
            errors.append("Readme.md language table is not generated from product/languages.json")
        stale_fragments = (
            "Java 11 or newer is the only prerequisite",
            ".mncspec` has no grammar",
            "those files open as plain text",
        )
        for fragment in stale_fragments:
            if fragment in readme:
                errors.append(f"Readme.md contains stale product claim: {fragment!r}")
    except OSError as exc:
        errors.append(f"cannot read Readme.md: {exc}")

    return errors


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", type=Path, default=Path(__file__).resolve().parents[1])
    args = parser.parse_args()
    errors = verify_repository(args.root.resolve())
    if errors:
        for error in errors:
            print(f"PRODUCT TRUTH ERROR: {error}")
        return 1
    languages = load_registry(args.root.resolve())
    print(
        "KIDE product truth verified for: "
        + ", ".join(f".{language['extension']}" for language in languages)
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
