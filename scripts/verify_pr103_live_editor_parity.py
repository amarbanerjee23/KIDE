#!/usr/bin/env python3
"""PR103: enforce non-mocked six-DSL hosted editor parity on the real staging gate.

This script does not contact staging. Its job is to make omission of required
desktop/Web semantic service coverage fail ordinary pull-request CI.
"""
from __future__ import annotations

import json
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]
EXPECTED_LANGUAGES = {
    "dml": "dml",
    "operation": "op",
    "mnc": "mncspec",
    "capability": "cap",
    "activity": "activity",
    "krl": "krl",
}
REQUIRED_LSP_METHODS = (
    '"initialize"', '"textDocument/didOpen"', '"textDocument/documentSymbol"',
    '"textDocument/hover"', '"textDocument/references"',
    '"textDocument/documentHighlight"', '"textDocument/foldingRange"',
    '"textDocument/formatting"', '"textDocument/semanticTokens/full"',
    '"textDocument/didChange"', '"textDocument/completion"',
    '"textDocument/didClose"',
)
REQUIRED_GLSP_ACTIONS = (
    '"initializeClientSession"', '"requestModel"', '"setModel"',
    '"requestTypeHints"', '"setTypeHints"',
)


def read(relative: str) -> str:
    return (ROOT / relative).read_text(encoding="utf-8")


def verify() -> list[str]:
    errors: list[str] = []
    required = [
        "product/languages.json",
        "product/lsp-capabilities.json",
        "product/diagram-semantics.json",
        "web/e2e/live-hosted.spec.ts",
        "web/e2e/live-editor-parity.ts",
        "web/playwright.live.config.ts",
        ".github/workflows/live-staging-acceptance.yml",
        ".github/workflows/build.yml",
        "docs/pr103-editor-semantic-parity.md",
    ]
    for path in required:
        if not (ROOT / path).is_file():
            errors.append("PR103 required parity file absent: " + path)
    if errors:
        return errors

    registry = json.loads(read(required[0]))
    matrix = json.loads(read(required[1]))
    diagrams = json.loads(read(required[2]))
    actual = {item["id"]: item["extension"] for item in registry["languages"]}
    if actual != EXPECTED_LANGUAGES:
        errors.append("PR103 live editor coverage no longer matches the shipped DSL registry")
    if set(matrix["languages"]) != set(EXPECTED_LANGUAGES):
        errors.append("live parity matrix must cover every shipped DSL")
    if {item["id"] for item in diagrams["diagrams"]} != {
        "kide-activity-diagram", "kide-mnc-diagram"
    }:
        errors.append("live parity requires both shipped Sirius/GLSP diagrams")

    helper = read(required[4])
    for marker in (
        '"../../product/languages.json"',
        '"../../product/lsp-capabilities.json"',
        'const modelByExtension',
        'languages.length !== 6',
        'matrix.languages[language.id]',
        'mandatoryCapabilities',
        'import { clientCapabilities } from "../src/lspClient"',
        'const productionClientCapabilities = clientCapabilities()',
        'capabilities: productionClientCapabilities',
        'new WebSocket(',
        'kide.bearer.',
        'socket.protocol !== "kide.lsp.v1"',
        'init.capabilities[name]',
        'completionItems(result).length === 0',
        'symbolNames(symbols).includes(item.symbol)',
        'item.references_min',
        'item.folding_min',
        'item.document_highlights_min',
        'item.semantic_tokens_min',
        'item.completion.text',
        'sourceUri: "kide-workspace:/"',
        'root.children.length === 0',
        'hints.kind !== "setTypeHints"',
        'socket.protocol !== "kide.glsp.v1"',
    ):
        if marker not in helper:
            errors.append("real editor/diagram semantic parity check missing: " + marker)
    for method in REQUIRED_LSP_METHODS + REQUIRED_GLSP_ACTIONS:
        if method not in helper:
            errors.append("real editor parity protocol operation missing: " + method)
    for ext, model in (
        ("dml", "golden.dml"), ("op", "golden.op"),
        ("mncspec", "device.mncspec"), ("cap", "observe.cap"),
        ("activity", "workflow.activity"), ("krl", "bindings.krl")
    ):
        if f'{ext}: "{model}"' not in helper:
            errors.append("missing real editor model mapping for " + ext)
    for forbidden in (
        "page.route(", "page.routeWebSocket(", "route.fulfill(", "test.skip(",
        "test.fixme(", "new DOMParser(", "customGrammar", "mockXtext"
    ):
        if forbidden in helper:
            errors.append("real editor semantic suite introduced a mock or duplicate parser: " + forbidden)

    live = read(required[3])
    for marker in (
        'assertLiveXtextParity(page, cfg.api, engineer.token, created.workspaceId, fixture)',
        'assertLiveGlspModels(page, cfg.api, engineer.token, created.workspaceId,',
        'expect(imported.importedCount).toBe(6)',
        '"golden.dml": readFileSync(',
        '"golden.op": readFileSync(',
        'Object.entries(fixture)',
        'model.content).toBe(sourceBytes)',
        'expect(validSha(model.etag)).toBe(true)',
        'const projectFiles = page.getByRole("list", { name: "Project files" })',
        'for (const modelId of Object.keys(fixture))',
        'projectFiles.locator("button[title=',
        'await expect(page.locator(".editor-tabs .active")).toContainText(modelId)',
        'await expect(page.locator(".monaco-editor")).toBeVisible()',
        'assertGeneratedArtifacts(generated, source, krl, synthesis.fingerprint)',
        'other.token, "GET", projectPath, 403',
    ):
        if marker not in live:
            errors.append("real hosted acceptance is missing six-DSL journey assertion: " + marker)
    for forbidden in ("await page.route(", "await page.routeWebSocket(", "route.fulfill(", "test.skip("):
        if forbidden in live:
            errors.append("real hosted acceptance may not mock network/protocol: " + forbidden)

    runner = read(required[5])
    for marker in ('testMatch: "live-hosted.spec.ts"', 'trace: "off"', 'video: "off"',
                   'screenshot: "off"', 'retries: 0'):
        if marker not in runner:
            errors.append("real acceptance secret safety changed: " + marker)
    workflow = read(required[6])
    if "workflow_dispatch:" not in workflow or "environment: staging" not in workflow:
        errors.append("real six-DSL acceptance requires explicit protected staging approval")
    ci = read(required[7])
    for command in (
        "python3 scripts/verify_pr103_live_editor_parity.py",
        "python3 scripts/verify_lsp_parity_matrix.py",
        "python3 scripts/verify_diagram_parity.py",
        "python3 scripts/verify_cross_adapter_semantics.py",
        "npx playwright test --config playwright.live.config.ts --list",
    ):
        if command not in ci:
            errors.append("PR CI lost an Eclipse/Web semantic parity gate: " + command)
    return errors


def main() -> int:
    errors = verify()
    for error in errors:
        print("ERROR:", error, file=sys.stderr)
    if errors:
        return 2
    print("PR103 SIX-DSL + ACTIVITY/MNC REAL STAGING PARITY CONTRACT VERIFIED; LIVE RUN SEPARATE")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
