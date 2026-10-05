#!/usr/bin/env python3
from __future__ import annotations

import json
import pathlib
import re
import sys

ROOT = pathlib.Path(__file__).resolve().parents[1]


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


def verify() -> list[str]:
    errors: list[str] = []
    contract = json.loads(read("product/archive-promotion.json"))

    if contract.get("schema_version") != 1:
        errors.append("archive promotion contract must use schema_version 1")
    if contract.get("target_policy") != "REQUIRE_EMPTY":
        errors.append("archive promotion must remain non-destructive")
    if contract.get("content_encoding") != "base64":
        errors.append("archive promotion transport must remain binary-safe base64")

    server = read(
        "com.kide.enterprise.server/src/com/kide/enterprise/server/EnterpriseApiServer.java"
    )
    constants = {
        "MAX_IMPORT_FILES": contract["max_files"],
        "MAX_IMPORT_FILE_BYTES": contract["max_file_bytes"],
        "MAX_IMPORT_TOTAL_BYTES": contract["max_total_bytes"],
    }
    for name, expected in constants.items():
        match = re.search(
            rf"private static final int {name} = ([^;]+);",
            server,
        )
        if not match:
            errors.append(f"server constant missing: {name}")
            continue
        expression = match.group(1).strip().replace(" ", "")
        expected_expressions = {str(expected)}
        if expected == 2 * 1024 * 1024:
            expected_expressions.add("2*1024*1024")
        if expected == 10 * 1024 * 1024:
            expected_expressions.add("10*1024*1024")
        if expression not in expected_expressions:
            errors.append(f"{name}={expression} does not match contract {expected}")

    registry = read(
        "com.kide.enterprise.api/src/com/kide/enterprise/api/ApiContractRegistry.java"
    )
    operation = (
        f'new ApiOperation("{contract["operation_id"]}", HttpMethod.{contract["method"]}, '
        f'"{contract["route"]}", "ProjectArchiveImportRequest", '
        '"ProjectArchiveImportResult")'
    )
    if operation not in registry:
        errors.append("archive promotion operation drifted from API registry")

    transaction = read(
        "com.kide.enterprise.modelrepo/src/com/kide/enterprise/modelrepo/ModelTransaction.java"
    )
    file_repo = read(
        "com.kide.enterprise.modelrepo/src/com/kide/enterprise/modelrepo/FileModelRepository.java"
    )
    server_repo = read(
        "com.kide.enterprise.modelrepo/src/com/kide/enterprise/modelrepo/ServerModelRepository.java"
    )
    if "void requireEmpty();" not in transaction:
        errors.append("ModelTransaction empty-project precondition is missing")
    for name, source in (("file", file_repo), ("server", server_repo)):
        if "requireEmpty &&" not in source:
            errors.append(f"{name} repository does not enforce empty-project precondition")

    generated = read("web/src/generated/api-v1.ts")
    if "importProjectArchive(projectId: string): GeneratedApiCall" not in generated:
        errors.append("generated Web API transport lacks archive promotion")
    if 'requestSchema: "ProjectArchiveImportRequest"' not in generated:
        errors.append("generated archive promotion request schema drifted")

    app = read("web/src/App.tsx")
    for token in (
        "MAX_HOSTED_IMPORT_FILES = 256",
        "MAX_HOSTED_IMPORT_BYTES = 10 * 1024 * 1024",
        "Promote here",
        'entry.path.startsWith(".kide/")',
        "bytesToBase64(entry.bytes)",
        "clientRef.current.importProjectArchive",
    ):
        if token not in app:
            errors.append(f"Web archive promotion guard missing: {token}")

    archive = read("web/src/archive.ts")
    for token in ("bytesToBase64", "bytesFromBase64"):
        if token not in archive:
            errors.append(f"binary archive transport helper missing: {token}")

    application = read(
        "com.kide.enterprise.server/src/com/kide/enterprise/server/EnterpriseApiApplication.java"
    )
    if 'integer(env, "KIDE_API_MAX_REQUEST_BYTES", 16 * 1024 * 1024)' not in application:
        errors.append("enterprise API request cap no longer accommodates bounded promotion")
    entrypoint = read("deploy/gcp/entrypoint.sh")
    if "KIDE_API_MAX_REQUEST_BYTES=" not in entrypoint or "16777216" not in entrypoint:
        errors.append("Cloud Run does not configure the archive promotion request cap")

    smoke = read("scripts/smoke_enterprise_api_server.py")
    if "KIDE PR84 HOSTED ARCHIVE PROMOTION OK" not in smoke:
        errors.append("packaged server smoke no longer requires PR84 qualification")

    workflow = read(".github/workflows/build.yml")
    if "python3 scripts/verify_archive_promotion.py" not in workflow:
        errors.append("Build KIDE no longer verifies the archive promotion contract")

    forbidden = set(contract.get("forbidden_hosted_prefixes", []))
    if forbidden != {".kide/"}:
        errors.append("hosted internal metadata policy drifted")

    return errors


def main() -> int:
    errors = verify()
    if errors:
        for error in errors:
            print(f"ERROR: {error}", file=sys.stderr)
        return 2
    print("Hosted archive promotion contract verified")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
