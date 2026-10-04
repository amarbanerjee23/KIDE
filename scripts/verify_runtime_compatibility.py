#!/usr/bin/env python3
from __future__ import annotations

import json
import pathlib
import re
import sys

ROOT = pathlib.Path(__file__).resolve().parents[1]
CONTRACT = ROOT / "product" / "runtime-compatibility.json"


class VerificationError(RuntimeError):
    pass


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


def java_constant(source: str, name: str) -> str:
    match = re.search(
        rf"public static final (?:String|int) {re.escape(name)} = (?:\"([^\"]+)\"|(\d+));",
        source,
    )
    if not match:
        raise VerificationError(f"missing Java compatibility constant {name}")
    return match.group(1) or match.group(2)


def verify() -> list[str]:
    errors: list[str] = []
    contract = json.loads(CONTRACT.read_text(encoding="utf-8"))
    if contract.get("schema_version") != 1:
        errors.append("runtime compatibility contract must use schema_version 1")

    java = read(
        "com.kide.enterprise.api/src/com/kide/enterprise/api/RuntimeCompatibility.java"
    )
    web = read("web/src/runtimeCompatibility.ts")
    schema = read(
        "com.kide.enterprise.migration/src/com/kide/enterprise/migration/ProjectSchemaService.java"
    )
    registry = read(
        "com.kide.enterprise.api/src/com/kide/enterprise/api/ApiContractRegistry.java"
    )
    generated = read("web/src/generated/api-v1.ts")

    expected = {
        "API_VERSION": str(contract["api_version"]),
        "ENGINEERING_COMPATIBILITY_LEVEL": str(
            contract["engineering_compatibility_level"]
        ),
        "PROJECT_SCHEMA_VERSION": str(contract["project_schema_version"]),
        "SHARED_KERNEL_SCHEMA_VERSION": str(
            contract["shared_kernel_schema_version"]
        ),
        "PRODUCT_LINE": str(contract["product_line"]),
    }
    for name, value in expected.items():
        try:
            actual = java_constant(java, name)
        except VerificationError as exc:
            errors.append(str(exc))
            continue
        if actual != value:
            errors.append(f"{name}={actual} does not match contract {value}")

    web_expectations = {
        "apiVersion": f'"{contract["api_version"]}"',
        "engineeringCompatibilityLevel": str(
            contract["engineering_compatibility_level"]
        ),
        "projectSchemaVersion": str(contract["project_schema_version"]),
        "sharedKernelSchemaVersion": str(contract["shared_kernel_schema_version"]),
    }
    for field, value in web_expectations.items():
        if not re.search(rf"{field}:\s*{re.escape(value)}\b?", web):
            errors.append(f"Web compatibility expectation for {field} drifted")

    project_match = re.search(r"CURRENT_VERSION\s*=\s*(\d+)", schema)
    if not project_match or int(project_match.group(1)) != contract["project_schema_version"]:
        errors.append("ProjectSchemaService CURRENT_VERSION does not match handshake")

    kernel = json.loads(read("product/shared-service-kernel.json"))
    if kernel.get("schema_version") != contract["shared_kernel_schema_version"]:
        errors.append("shared service kernel schema does not match handshake")

    if 'new ApiOperation("version", HttpMethod.GET, "/api/v1/version", "", "RuntimeVersion")' not in registry:
        errors.append("API registry does not expose the version handshake")
    if 'version(): GeneratedApiCall' not in generated or 'path: "/version"' not in generated:
        errors.append("generated Web API transport does not expose version handshake")

    policy = contract.get("policy", {})
    required = set(policy.get("required_exact_match", []))
    if required != {
        "api_version",
        "engineering_compatibility_level",
        "project_schema_version",
        "shared_kernel_schema_version",
    }:
        errors.append("required exact-match compatibility policy drifted")

    return errors


def main() -> int:
    errors = verify()
    if errors:
        for error in errors:
            print(f"ERROR: {error}", file=sys.stderr)
        return 2
    print("Runtime compatibility handshake contract verified")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
