#!/usr/bin/env python3
"""Generate the KIDE Web API transport from the packaged OpenAPI v1 document."""

from __future__ import annotations

import argparse
import json
import pathlib
import re
import sys
from typing import Any

API_PREFIX = "/api/v1"
PATH_PARAMETER = re.compile(r"\{([A-Za-z][A-Za-z0-9]*)\}")
METHODS = ("delete", "get", "post", "put")


class GenerationError(RuntimeError):
    pass


def load_openapi(path: pathlib.Path) -> dict[str, Any]:
    try:
        document = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        raise GenerationError(f"could not read OpenAPI document: {exc}") from exc
    if document.get("openapi") != "3.1.0":
        raise GenerationError("expected OpenAPI 3.1.0")
    if document.get("info", {}).get("version") != "v1":
        raise GenerationError("expected KIDE API version v1")
    if not isinstance(document.get("paths"), dict):
        raise GenerationError("OpenAPI paths object is missing")
    return document


def ts_string(value: str) -> str:
    return json.dumps(value, ensure_ascii=False)


def schema_ref(value: object) -> str | None:
    if not isinstance(value, dict):
        return None
    ref = value.get("$ref")
    prefix = "#/components/schemas/"
    if not isinstance(ref, str) or not ref.startswith(prefix):
        return None
    name = ref[len(prefix):]
    if not re.fullmatch(r"[A-Za-z][A-Za-z0-9]*", name):
        raise GenerationError(f"invalid schema reference: {ref}")
    return name


def operation_rows(document: dict[str, Any]) -> list[dict[str, Any]]:
    rows: list[dict[str, Any]] = []
    seen: set[str] = set()
    for path, path_item in document["paths"].items():
        if not isinstance(path, str) or not path.startswith(API_PREFIX):
            raise GenerationError(f"operation path is outside {API_PREFIX}: {path!r}")
        if not isinstance(path_item, dict):
            raise GenerationError(f"path item is not an object: {path}")
        for method in METHODS:
            raw = path_item.get(method)
            if raw is None:
                continue
            if not isinstance(raw, dict):
                raise GenerationError(f"{method.upper()} {path} is not an object")
            operation_id = raw.get("operationId")
            if not isinstance(operation_id, str) or not re.fullmatch(r"[A-Za-z][A-Za-z0-9]*", operation_id):
                raise GenerationError(f"{method.upper()} {path} has invalid operationId")
            if operation_id in seen:
                raise GenerationError(f"duplicate operationId: {operation_id}")
            seen.add(operation_id)

            parameters = PATH_PARAMETER.findall(path)
            declared = {
                item.get("name")
                for item in raw.get("parameters", [])
                if isinstance(item, dict) and item.get("in") == "path"
            }
            if set(parameters) != declared:
                raise GenerationError(
                    f"{operation_id} path parameters do not match template: "
                    f"{parameters!r} vs {sorted(declared)!r}"
                )

            request_schema: str | None = None
            request_body = raw.get("requestBody")
            if isinstance(request_body, dict):
                request_schema = schema_ref(
                    request_body.get("content", {})
                    .get("application/json", {})
                    .get("schema")
                )

            responses = raw.get("responses")
            if not isinstance(responses, dict) or "200" not in responses:
                raise GenerationError(f"{operation_id} is missing a 200 response")
            response_schema = schema_ref(
                responses["200"]
                .get("content", {})
                .get("application/json", {})
                .get("schema")
            )
            if response_schema is None:
                raise GenerationError(f"{operation_id} is missing its response schema")

            rows.append(
                {
                    "operation_id": operation_id,
                    "method": method.upper(),
                    "path": path[len(API_PREFIX):] or "/",
                    "parameters": parameters,
                    "request_schema": request_schema,
                    "response_schema": response_schema,
                }
            )
    return sorted(rows, key=lambda row: row["operation_id"])


def render_path(template: str, parameters: list[str]) -> str:
    if not parameters:
        return ts_string(template)
    cursor = 0
    pieces: list[str] = []
    marker_prefix = "$" + "{"
    tick = chr(96)
    for match in PATH_PARAMETER.finditer(template):
        literal = template[cursor:match.start()]
        pieces.append(
            literal.replace("\\", "\\\\")
            .replace(tick, "\\" + tick)
            .replace(marker_prefix, "\\" + marker_prefix)
        )
        parameter = match.group(1)
        encoder = "modelPath" if parameter == "modelId" else "segment"
        pieces.append(marker_prefix + encoder + "(" + parameter + ")}")
        cursor = match.end()
    literal = template[cursor:]
    pieces.append(
        literal.replace("\\", "\\\\")
        .replace(tick, "\\" + tick)
        .replace(marker_prefix, "\\" + marker_prefix)
    )
    return tick + "".join(pieces) + tick


def generate(document: dict[str, Any]) -> str:
    rows = operation_rows(document)
    lines = [
        "/*",
        " * GENERATED FILE. DO NOT EDIT.",
        " * Source: packaged KIDE OpenAPI v1 (OpenApiV1.generateJson()).",
        " * Regenerate with scripts/generate_web_api_client.py.",
        " */",
        "",
        'export type GeneratedHttpMethod = "DELETE" | "GET" | "POST" | "PUT";',
        "",
        "export interface GeneratedApiCall {",
        "  operationId: string;",
        "  method: GeneratedHttpMethod;",
        "  path: string;",
        "  requestSchema?: string;",
        "  responseSchema: string;",
        "}",
        "",
        "function segment(value: string): string {",
        "  return encodeURIComponent(value);",
        "}",
        "",
        "function modelPath(value: string): string {",
        "  const parts = value.split(\"/\");",
        "  if (parts.some((part) => !part || part === \".\" || part === \"..\")) {",
        "    throw new Error(\"Invalid model path.\");",
        "  }",
        "  return parts.map((part) => encodeURIComponent(part)).join(\"/\");",
        "}",
        "",
        "export const apiV1 = {",
    ]
    for row in rows:
        args = ", ".join(f"{name}: string" for name in row["parameters"])
        lines.append(f"  {row['operation_id']}({args}): GeneratedApiCall {{")
        lines.append("    return {")
        lines.append(f"      operationId: {ts_string(row['operation_id'])},")
        lines.append(f"      method: {ts_string(row['method'])},")
        lines.append(f"      path: {render_path(row['path'], row['parameters'])},")
        if row["request_schema"]:
            lines.append(f"      requestSchema: {ts_string(row['request_schema'])},")
        lines.append(f"      responseSchema: {ts_string(row['response_schema'])}")
        lines.append("    };")
        lines.append("  },")
    lines.extend(
        [
            "} as const;",
            "",
            "export type ApiV1OperationId = keyof typeof apiV1;",
            "",
        ]
    )
    return "\n".join(lines)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--openapi", required=True, type=pathlib.Path)
    parser.add_argument("--output", required=True, type=pathlib.Path)
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()

    generated = generate(load_openapi(args.openapi))
    if args.check:
        try:
            existing = args.output.read_text(encoding="utf-8")
        except OSError as exc:
            raise GenerationError(f"generated client is missing: {args.output}") from exc
        if existing != generated:
            raise GenerationError(
                "generated Web API client is stale; regenerate "
                f"{args.output} from {args.openapi}"
            )
        print(f"Generated Web API client is current: {args.output}")
        return 0

    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(generated, encoding="utf-8")
    print(f"Generated Web API client: {args.output}")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except GenerationError as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        raise SystemExit(2)
