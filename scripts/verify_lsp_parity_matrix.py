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
    "type_definition", "implementation", "document_highlights",
    "range_formatting", "signature_help",
}
QUALIFICATIONS = {
    "required",
    "required_where_applicable",
    "not_applicable",
}


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

    baseline = matrix.get("baseline")
    if not isinstance(baseline, dict) or baseline.get("xtext") != "2.44.0 current platform":
        errors.append("LSP parity baseline must be Xtext 2.44.0 current platform")

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
        qualification = feature.get("qualification")
        if qualification == "not_applicable" and not feature.get("reason"):
            errors.append(f"not-applicable LSP feature {feature_id} requires a reason")
        if qualification == "deferred":
            errors.append(
                f"LSP feature {feature_id} may not remain deferred on the complete-parity baseline"
            )
    if set(feature_ids) != REQUIRED_FEATURES or len(feature_ids) != len(REQUIRED_FEATURES):
        errors.append("LSP feature matrix must contain the complete PR13 feature set exactly once")

    for language_id in ids:
        probe = language_matrix.get(language_id)
        if not isinstance(probe, dict):
            errors.append(f"missing LSP probes for {language_id}")
            continue
        for field in (
            "symbol",
            "hover_token",
            "references_min",
            "folding_min",
            "semantic_tokens_min",
            "document_highlights_min",
            "document_highlight_token",
            "rename_targets",
        ):
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

        highlight_token = probe.get("document_highlight_token")
        if not isinstance(highlight_token, str) or not highlight_token:
            errors.append(f"{language_id} document_highlight_token must be non-empty")

        semantic_min = probe.get("semantic_tokens_min")
        if not isinstance(semantic_min, int) or isinstance(semantic_min, bool) or semantic_min < 0:
            errors.append(f"{language_id} semantic_tokens_min must be a non-negative integer")

        highlight_min = probe.get("document_highlights_min")
        if not isinstance(highlight_min, int) or isinstance(highlight_min, bool) or highlight_min < 0:
            errors.append(f"{language_id} document_highlights_min must be a non-negative integer")

        semantic_completion = probe.get("semantic_completion")
        if semantic_completion is not None:
            if language_id not in {"mnc", "capability", "activity"}:
                errors.append(
                    f"{language_id} declares semantic completion without an Eclipse custom proposal provider"
                )
            elif not isinstance(semantic_completion, dict):
                errors.append(f"{language_id} semantic_completion must be an object")
            else:
                text_value = semantic_completion.get("text")
                expected = semantic_completion.get("expected")
                forbidden = semantic_completion.get("forbidden")
                if not isinstance(text_value, str) or not text_value:
                    errors.append(f"{language_id} semantic_completion text must be non-empty")
                if not isinstance(expected, list) or not expected or not all(
                    isinstance(item, str) and item for item in expected
                ):
                    errors.append(
                        f"{language_id} semantic_completion expected must be a non-empty string list"
                    )
                if not isinstance(forbidden, list) or not forbidden or not all(
                    isinstance(item, str) and item for item in forbidden
                ):
                    errors.append(
                        f"{language_id} semantic_completion forbidden must be a non-empty string list"
                    )

        action = probe.get("code_action")
        if action is not None:
            if language_id not in {"mnc", "capability", "activity"}:
                errors.append(f"{language_id} declares a custom code action without an Eclipse quick fix")
            elif not isinstance(action, dict):
                errors.append(f"{language_id} code_action must be an object")
            else:
                for field in ("diagnostic_code", "title", "replacement"):
                    if not isinstance(action.get(field), str):
                        errors.append(f"{language_id} code_action {field} must be a string")

        definition = probe.get("definition")
        if definition is not None:
            occurrence = definition.get("occurrence", 1) if isinstance(definition, dict) else 1
            if not isinstance(occurrence, int) or isinstance(occurrence, bool) or occurrence < 1:
                errors.append(f"{language_id} definition occurrence must be a positive integer")
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

    semantic_completion_languages = {
        language_id
        for language_id, probe in language_matrix.items()
        if isinstance(probe, dict)
        and isinstance(probe.get("semantic_completion"), dict)
    }
    if semantic_completion_languages != {"mnc", "capability", "activity"}:
        errors.append(
            "semantic Eclipse content-assist parity must be defined exactly for "
            "mnc, capability and activity"
        )

    action_languages = {
        language_id
        for language_id, probe in language_matrix.items()
        if isinstance(probe, dict) and isinstance(probe.get("code_action"), dict)
    }
    if action_languages != {"mnc", "capability", "activity"}:
        errors.append(
            "custom Eclipse quick-fix parity must be defined exactly for "
            "mnc, capability and activity"
        )

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
