#!/usr/bin/env python3
"""PR101: prevent healthy anonymous endpoints from masquerading as full service readiness.

Repository-only contract. Actual Firebase/Cloud Run/WebSocket checks run only
in separately configured staging, never with credentials in pull-request CI.
"""
from __future__ import annotations

import pathlib
import sys

ROOT = pathlib.Path(__file__).resolve().parents[1]


def read(name: str) -> str:
    return (ROOT / name).read_text(encoding="utf-8")


def verify() -> list[str]:
    files = [
        "web/src/connectivityPolicy.ts",
        "web/e2e/live-connectivity.spec.ts",
        "web/playwright.connectivity.config.ts",
        "web/tests/connectivityPolicy.test.ts",
        ".github/workflows/hosted-runtime-health.yml",
        ".github/workflows/build.yml",
        "web/playwright.config.ts",
        "web/package.json",
        "docs/pr101-full-stack-readiness.md",
    ]
    missing = [name for name in files if not (ROOT / name).is_file()]
    if missing:
        return ["required authenticated readiness file missing: " + name for name in missing]

    errors = []
    probe = read(files[1])
    for name in (
        'readConnectivitySettings(process.env)',
        'identitytoolkit.googleapis.com',
        '"/api/v1/projects"',
        '"/collaboration/sessions"',
        'unauthenticated.status()',
        'projectsResponse.status()',
        'collaborationResponse.status()',
        'verifyGateway(page, cfg.api, auth.idToken, selected.workspaceId, "lsp")',
        'verifyGateway(page, cfg.api, auth.idToken, selected.workspaceId, "glsp")',
        '"initializeClientSession"',
        'caps?.completionProvider',
        'socket.protocol !== protocol',
    ):
        if name not in probe:
            errors.append("authenticated probe lost required service assertion: " + name)

    for forbidden in (
        "page.route(", "page.routeWebSocket(", "route.fulfill(",
        "test.skip(", "test.fixme(", '"/imports/archive"',
        '"/synthesis"', '"/generation"', '"joinPresence"',
        '"/models", { data:', 'test.only(',
    ):
        if forbidden in probe:
            errors.append("read-only hosted probe contains forbidden bypass/action: " + forbidden)

    policy = read(files[0])
    for key in (
        'KIDE_MONITOR_APPROVED_API_HOST', 'KIDE_MONITOR_APPROVED_WEB_HOST',
        'KIDE_MONITOR_EXPECTED_PROJECT_ID', 'KIDE_MONITOR_ENGINEER_PASSWORD',
        'url.protocol !== "https:"', 'url.username || url.password',
        "api === web",
    ):
        if key not in policy:
            errors.append("staging connection boundary missing: " + key)

    config = read(files[2])
    for token in (
        'testMatch: "live-connectivity.spec.ts"',
        'trace: "off"', 'screenshot: "off"', 'video: "off"',
        'retries: 0',
    ):
        if token not in config:
            errors.append("live connectivity runner invariant missing: " + token)
    if "webServer:" in config:
        errors.append("live authenticated connectivity must not start a mock Vite server")

    default = read(files[6])
    for excluded in ('"**/live-hosted.spec.ts"', '"**/live-connectivity.spec.ts"'):
        if excluded not in default:
            errors.append("default mocked browser suite must exclude " + excluded)

    workflow = read(files[4])
    for token in (
        'schedule:', 'workflow_dispatch:', 'contents: read',
        "authenticated-services:", "monitoring-coverage:",
        "vars.KIDE_MONITOR_AUTH_ENABLED == 'true'",
        "environment: staging-monitor",
        "KIDE_MONITOR_ENGINEER_PASSWORD: ${{ secrets.KIDE_MONITOR_ENGINEER_PASSWORD }}",
        "KIDE_MONITOR_EXPECTED_PROJECT_ID:",
        "npm run test:connectivity",
        "NOT CONFIGURED / NOT VERIFIED",
    ):
        if token not in workflow:
            errors.append("hosted staging monitor workflow lost safety/coverage: " + token)

    build = read(files[5])
    for token in (
        "python3 scripts/verify_authenticated_monitor.py",
        "npx playwright test --config playwright.connectivity.config.ts --list",
    ):
        if token not in build:
            errors.append("required PR CI connectivity governance/discovery missing: " + token)

    package = read(files[7])
    if '"test:connectivity": "playwright test --config playwright.connectivity.config.ts"' not in package:
        errors.append("dedicated read-only staging test command missing")
    return errors


def main() -> int:
    errors = verify()
    for error in errors:
        print("ERROR:", error, file=sys.stderr)
    if errors:
        return 2
    print("PR101 AUTHENTICATED READ-ONLY SERVICE MONITOR CONTRACT VERIFIED (live deployment not exercised)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
