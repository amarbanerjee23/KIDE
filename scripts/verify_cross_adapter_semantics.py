#!/usr/bin/env python3
"""Verify KIDE cross-adapter semantic qualification remains complete and fail-closed."""

from __future__ import annotations

import json
import pathlib
import sys

ROOT = pathlib.Path(__file__).resolve().parents[1]
CONTRACT = pathlib.Path("product/cross-adapter-semantic-qualification.json")

EXPECTED_SERVICES = {
    "com.kide.knowledge.ProjectKnowledgeService",
    "com.kide.synthesis.ProjectSynthesisService",
    "com.kide.codegen.ProjectGenerationService",
}
EXPECTED_PAIRS = {
    "Eclipse Xtext / Web LSP",
    "Eclipse Sirius / Web GLSP",
}
REQUIRED_SEMANTIC_TERMS = {
    "knowledge",
    "synthesis",
    "fingerprint",
    "generated MNC",
    "generation",
    "artifact",
    "reconfiguration",
    "migration",
}
REQUIRED_EXCLUSIONS = {
    "request ids",
    "timestamps",
    "HTTP headers",
    "JSON object ordering",
    "UI wording and layout",
}


def load_contract(root: pathlib.Path) -> dict:
    return json.loads((root / CONTRACT).read_text(encoding="utf-8"))


def verify(root: pathlib.Path = ROOT, contract: dict | None = None) -> list[str]:
    errors: list[str] = []
    data = contract if contract is not None else load_contract(root)

    if data.get("schema_version") != 1:
        errors.append("cross-adapter contract schema_version must be 1")

    runtime = data.get("runtime_equality")
    if not isinstance(runtime, dict):
        return errors + ["runtime_equality section is required"]

    if runtime.get("application") != "com.kide.enterprise.server.selfcheck":
        errors.append("runtime equality must execute the packaged enterprise self-check")
    marker = runtime.get("marker")
    if marker != "KIDE PR80 CROSS-ADAPTER SEMANTIC PARITY OK":
        errors.append("PR80 runtime parity marker changed or is missing")

    services = set(runtime.get("shared_services") or [])
    if services != EXPECTED_SERVICES:
        errors.append(
            "runtime parity must cover exactly the project knowledge, synthesis and generation services"
        )
    if runtime.get("http_adapter") != "com.kide.enterprise.server.EnterpriseApiServer":
        errors.append("runtime parity must compare against EnterpriseApiServer")

    semantics = " ".join(runtime.get("compared_semantics") or [])
    for term in REQUIRED_SEMANTIC_TERMS:
        if term.lower() not in semantics.lower():
            errors.append(f"runtime parity contract is missing semantic evidence: {term}")

    exclusions = set(runtime.get("excluded_presentation_or_transport") or [])
    if exclusions != REQUIRED_EXCLUSIONS:
        errors.append("presentation/transport exclusions changed; semantic comparison boundary drifted")

    gates = data.get("adapter_gates")
    if not isinstance(gates, list):
        errors.append("adapter_gates must be a list")
        gates = []
    pairs = {gate.get("adapter_pair") for gate in gates if isinstance(gate, dict)}
    if pairs != EXPECTED_PAIRS:
        errors.append("adapter gates must cover exactly Eclipse Xtext/Web LSP and Sirius/Web GLSP")

    for gate in gates:
        if not isinstance(gate, dict):
            errors.append("adapter gate entries must be objects")
            continue
        for field in ("contract", "static_verifier", "runtime_qualifier"):
            value = gate.get(field)
            if not isinstance(value, str) or not value:
                errors.append(f"{gate.get('adapter_pair', 'adapter')} missing {field}")
            elif not (root / value).is_file():
                errors.append(f"{gate.get('adapter_pair', 'adapter')} references missing {field}: {value}")

    api_selfcheck = (
        root
        / "com.kide.enterprise.server/src/com/kide/enterprise/server/EnterpriseApiSelfCheckApplication.java"
    ).read_text(encoding="utf-8")
    for fragment in (
        'knowledge.query("observe", "CAPABILITY", 10)',
        "synthesis.synthesize(",
        "generation.generate(",
        "synthesis.reconfigure(",
        "requireSynthesisParity(",
        "requireGenerationParity(",
        "requireReconfigurationParity(",
        "KIDE PR80 CROSS-ADAPTER SEMANTIC PARITY OK",
    ):
        if fragment not in api_selfcheck:
            errors.append(f"packaged runtime parity lost required evidence: {fragment}")

    smoke = (root / "scripts/smoke_enterprise_api_server.py").read_text(encoding="utf-8")
    if marker and marker not in smoke:
        errors.append("packaged enterprise smoke no longer requires the PR80 parity marker")

    build = (root / ".github/workflows/build.yml").read_text(encoding="utf-8")
    required_ci = {
        "python3 scripts/verify_cross_adapter_semantics.py",
        "python3 scripts/verify_lsp_parity_matrix.py",
        "python3 scripts/verify_diagram_parity.py",
        "python3 scripts/qualify_lsp_parity.py",
        "python3 scripts/smoke_glsp_runtime.py",
        "python3 scripts/smoke_enterprise_api_server.py",
    }
    for command in sorted(required_ci):
        if command not in build:
            errors.append(f"Build KIDE no longer runs required parity gate: {command}")

    kernel = json.loads(
        (root / "product/shared-service-kernel.json").read_text(encoding="utf-8")
    )
    bundles = set(kernel.get("bundles") or [])
    for bundle in ("com.kide.knowledge", "com.kide.synthesis", "com.kide.codegen"):
        if bundle not in bundles:
            errors.append(f"shared service kernel no longer ships parity owner {bundle}")

    return errors


def main() -> int:
    errors = verify()
    if errors:
        for error in errors:
            print(f"ERROR: {error}", file=sys.stderr)
        return 2
    print("Cross-adapter semantic qualification contract verified")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
