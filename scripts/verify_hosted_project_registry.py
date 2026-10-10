#!/usr/bin/env python3
from __future__ import annotations

import json
import pathlib
import sys

ROOT = pathlib.Path(__file__).resolve().parents[1]


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


def verify() -> list[str]:
    errors: list[str] = []
    contract = json.loads(read("product/hosted-project-registry.json"))

    if contract.get("schema_version") != 1:
        errors.append("hosted project registry schema_version must remain 1")
    if contract.get("registry_root_env") != "KIDE_HOSTED_PROJECTS_ROOT":
        errors.append("registry root environment variable drifted")
    if contract.get("max_projects") != 512:
        errors.append("registry project limit drifted")
    if contract.get("legacy_single_project_fallback") is not True:
        errors.append("legacy single-project fallback must remain enabled in PR87")
    if contract.get("project_creation_enabled") is not False:
        errors.append("PR87 must not silently enable project creation")

    registry = read(
        "com.kide.enterprise.context/src/com/kide/enterprise/context/"
        "FileHostedProjectRegistry.java"
    )
    for token in (
        "MAX_PROJECTS = 512",
        "Files.isSymbolicLink(slot)",
        "context.project().id().uuid().toString().equals(slotName)",
        "duplicate hosted project identity",
        "duplicate hosted workspace identity",
        "resolveWorkspace(String workspaceId)",
    ):
        if token not in registry:
            errors.append(f"file registry invariant missing: {token}")

    runtime = read(
        "com.kide.languageserver.gateway/src/com/kide/languageserver/gateway/"
        "GatewayWorkspaceRuntime.java"
    )
    for token in (
        'HOSTED_PROJECTS_ROOT = "KIDE_HOSTED_PROJECTS_ROOT"',
        "new FileHostedProjectRegistry",
        "new InMemoryGatewayWorkspaceCatalog",
    ):
        if token not in runtime:
            errors.append(f"gateway runtime compatibility path missing: {token}")

    catalog = read(
        "com.kide.languageserver.gateway/src/com/kide/languageserver/gateway/"
        "RegistryGatewayWorkspaceCatalog.java"
    )
    if "registry.resolveWorkspace(workspaceId)" not in catalog:
        errors.append("registry-backed gateway no longer resolves dynamically")

    roles = read(
        "com.kide.languageserver.gateway/src/com/kide/languageserver/gateway/"
        "GatewayRoleBindings.java"
    )
    if "scope is outside the hosted project registry" not in roles:
        errors.append("gateway role-binding validation no longer fails closed")

    lsp = read(
        "com.kide.languageserver.gateway/src/com/kide/languageserver/gateway/"
        "GatewayApplication.java"
    )
    glsp = read("com.kide.glsp/src/com/kide/glsp/GlspGatewayApplication.java")
    for name, source in (("LSP", lsp), ("GLSP", glsp)):
        if "GatewayWorkspaceRuntime.load(" not in source:
            errors.append(f"{name} gateway no longer uses shared registry bootstrap")
        if "GatewayRoleBindings.parse(" not in source:
            errors.append(f"{name} gateway no longer uses registry-aware authorization")

    lsp_test = read(
        "com.kide.languageserver.gateway.tests/src/com/kide/languageserver/"
        "gateway/tests/GatewayHandshakeIntegrationTest.java"
    )
    if "discoversRegistryWorkspaceAfterLspGatewayStartup" not in lsp_test:
        errors.append("dynamic LSP registry integration qualification is missing")

    glsp_test = read(
        "com.kide.glsp.tests/src/com/kide/glsp/tests/"
        "GlspGatewayIntegrationTest.java"
    )
    if "discoversRegistryWorkspaceAfterGatewayStartup" not in glsp_test:
        errors.append("dynamic GLSP registry integration qualification is missing")

    api = read(
        "com.kide.enterprise.server/src/com/kide/enterprise/server/"
        "EnterpriseApiServer.java"
    )
    if "Project creation is not enabled in this runtime phase." not in api:
        errors.append("PR87 must keep REST project creation disabled")

    workflow = read(".github/workflows/build.yml")
    if "python3 scripts/verify_hosted_project_registry.py" not in workflow:
        errors.append("Build KIDE no longer verifies hosted project registry governance")

    return errors


def main() -> int:
    errors = verify()
    if errors:
        for error in errors:
            print(f"ERROR: {error}", file=sys.stderr)
        return 2
    print("Hosted project registry contract verified")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
