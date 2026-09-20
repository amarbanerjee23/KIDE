#!/usr/bin/env python3
"""Validate PR13's LSP capability matrix against the production language registry."""

from __future__ import annotations

import argparse
import json
from pathlib import Path
from typing import Any

REQUIRED_FEATURES = {
    "diagnostics", "completion", "hover", "definition", "references",
    "document_symbols", "workspace_symbols", "formatting", "rename", "folding",
    "snippets", "code_actions", "semantic_tokens", "declaration",
    "type_definition", "implementation",
}
QUALIFICATIONS = {"required", "required_where_applicable", "deferred"}


def load_json(path: Path) -> dict[str, Any]:
    value = json.loads(path.read_text(encoding="utf-8"))
    if not isinstance(value, dict):
        raise ValueError(f"{path} must contain a JSON object")
    return value


def verify(root: Path) -> list[str]:
    errors: list[str] = []
    try:
        registry = load_json(root / "product" / "languages.json")
        matrix = load_json(root / "product" / "lsp-capabilities.json")
    except (OSError, ValueError, json.JSONDecodeError) as exc:
        return [str(exc)]

    if matrix.get("schema_version") != 1:
        errors.append("product/lsp-capabilities.json must use schema_version 1")

    registry_languages = registry.get("languages")
    if not isinstance(registry_languages, list):
        return errors + ["product/languages.json languages must be a list"]
    ids = [item.get("id") for item in registry_languages if isinstance(item, dict)]
    extensions = {
        item.get("id"): item.get("extension")
        for item in registry_languages if isinstance(item, dict)
    }

    language_matrix = matrix.get("languages")
    if not isinstance(language_matrix, dict):
        errors.append("lsp capability matrix languages must be an object")
        language_matrix = {}
    if set(language_matrix) != set(ids):
        errors.append(
            "lsp capability matrix language IDs must exactly match product/languages.json"
        )

    features = matrix.get("features")
    if not isinstance(features, list):
        errors.append("lsp capability matrix features must be a list")
        features = []
    feature_ids: list[str] = []
    for feature in features:
        if not isinstance(feature, dict):
            errors.append("every LSP feature entry must be an object")
            continue
        feature_id = feature.get("id")
        if not isinstance(feature_id, str) or not feature_id:
            errors.append("every LSP feature requires a non-empty id")
            continue
        feature_ids.append(feature_id)
        if feature.get("qualification") not in QUALIFICATIONS:
            errors.append(f"invalid qualification for LSP feature {feature_id}")
        if feature.get("qualification") == "deferred" and not feature.get("reason"):
            errors.append(f"deferred LSP feature {feature_id} requires a reason")
    if set(feature_ids) != REQUIRED_FEATURES or len(feature_ids) != len(REQUIRED_FEATURES):
        errors.append("LSP feature matrix must contain the complete PR13 feature set exactly once")

    for language_id in ids:
        probe = language_matrix.get(language_id)
        if not isinstance(probe, dict):
            errors.append(f"missing LSP probes for {language_id}")
            continue
        for field in ("symbol", "hover_token", "references_min", "folding_min", "rename_targets"):
            if field not in probe:
                errors.append(f"{language_id} LSP probe missing {field}")
        for field in ("document_symbol", "workspace_symbol"):
            value = probe.get(field)
            if value is not None and (not isinstance(value, str) or not value):
                errors.append(f"{language_id} {field} must be a non-empty string when present")

        rename_targets = probe.get("rename_targets")
        if not isinstance(rename_targets, list) or not rename_targets:
            errors.append(f"{language_id} rename_targets must be a non-empty list")
        else:
            extension = extensions.get(language_id)
            own_name = f"parity.{extension}"
            if own_name not in rename_targets:
                errors.append(f"{language_id} rename must include its own document {own_name}")

        definition = probe.get("definition")
        if definition is not None:
            if not isinstance(definition, dict):
                errors.append(f"{language_id} definition probe must be an object")
            else:
                target_extension = definition.get("target_extension")
                if target_extension not in set(extensions.values()):
                    errors.append(
                        f"{language_id} definition target extension is not a production DSL: {target_extension}"
                    )
                if not isinstance(definition.get("token"), str) or not definition.get("token"):
                    errors.append(f"{language_id} definition probe requires token")

    return errors


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", type=Path, default=Path(__file__).resolve().parents[1])
    args = parser.parse_args()
    errors = verify(args.root.resolve())
    if errors:
        for error in errors:
            print(f"LSP PARITY MATRIX ERROR: {error}")
        return 1
    print("KIDE LSP parity matrix verified.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
