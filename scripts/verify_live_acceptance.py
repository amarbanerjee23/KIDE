#!/usr/bin/env python3
"""PR89 governance: CI must distinguish mocked PR tests from real staging acceptance.

This script performs repository-only checks, never calls Firebase or Cloud Run.
It deliberately does NOT claim that deployed acceptance has passed.
"""
from __future__ import annotations

import pathlib
import sys

ROOT = pathlib.Path(__file__).resolve().parents[1]


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


def verify() -> list[str]:
    errors: list[str] = []
    files = [
        "web/e2e/live-hosted.spec.ts",
        "web/playwright.live.config.ts",
        ".github/workflows/live-staging-acceptance.yml",
        "web/playwright.config.ts",
        "web/package.json",
        ".github/workflows/build.yml",
    ]
    for file in files:
        if not (ROOT / file).is_file():
            errors.append(f"required deployed acceptance file is missing: {file}")
    if errors:
        return errors

    live = read(files[0])
    for term in (
        'firebaseSignIn(request, cfg.apiKey',
        'page.getByLabel("Firebase email")',
        'page.getByLabel("Firebase password")',
        'projectCreationEnabled',
        'assertGateway(page, cfg.api, engineer.token, created.workspaceId, "lsp")',
        'assertGateway(page, cfg.api, engineer.token, created.workspaceId, "glsp")',
        'synthesis.fingerprint',
        'assertGeneratedArtifacts(generated, source, krl, synthesis.fingerprint)',
        'other.token, "GET", projectPath, 403',
        'MISSING_ETAG',
        'apiOrigin + "/api/v1"',
        "createHash(",
        'KIDE_LIVE_ALLOW_WRITES',
        'KIDE_LIVE_APPROVED_API_HOST',
        'KIDE_LIVE_APPROVED_WEB_HOST',
        'expect(frontend.buildId).toBe(backend.buildId)',
    ):
        if term not in live:
            errors.append(f"unmocked engineering journey missing: {term}")
    for forbidden in (
        "await page.route(", "await page.routeWebSocket(", "test.skip(",
        "test.fixme(", "mockProjectServices(", "route.fulfill(",
    ):
        if forbidden in live:
            errors.append(f"unmocked test must not use {forbidden}")

    config = read(files[1])
    for marker in (
        'testMatch: "live-hosted.spec.ts"',
        'trace: "off"',
        'screenshot: "off"',
        'video: "off"',
    ):
        if marker not in config:
            errors.append(f"live Playwright security or selection invariant missing: {marker}")
    if "webServer:" in config:
        errors.append("live acceptance must never use an auto-started Vite mock server")

    default = read(files[3])
    if 'testIgnore: "**/live-hosted.spec.ts"' not in default:
        errors.append("regular browser PR tests must explicitly exclude the live suite")

    workflow = read(files[2])
    if "workflow_dispatch:" not in workflow or "environment: staging" not in workflow:
        errors.append("real acceptance must be manually gated by staging environment")
    if "\n  pull_request:" in workflow or "\n  push:" in workflow:
        errors.append("real staging acceptance must not auto-run on push or PR")
    for marker in (
        "writes_approved:",
        'test "$KIDE_LIVE_ALLOW_WRITES" = "staging-only"',
        "KIDE_STAGING_ENGINEER_PASSWORD",
        "KIDE_STAGING_OTHER_ENGINEER_PASSWORD",
        "npm run test:live",
        "actions/checkout@3d3c42e5aac5ba805825da76410c181273ba90b1",
    ):
        if marker not in workflow:
            errors.append(f"staging workflow missing: {marker}")

    package = read(files[4])
    if '"test:live": "playwright test --config playwright.live.config.ts"' not in package:
        errors.append("separate live acceptance npm command is missing")

    main_ci = read(files[5])
    if "python3 scripts/verify_live_acceptance.py" not in main_ci:
        errors.append("PR CI must preserve the live qualification contract")
    if "npx playwright test --config playwright.live.config.ts --list" not in main_ci:
        errors.append("PR CI must at least parse/discover the live Playwright suite")

    return errors


def main() -> int:
    failures = verify()
    for message in failures:
        print(f"ERROR: {message}", file=sys.stderr)
    if failures:
        return 2
    print("PR89 LIVE STAGING ACCEPTANCE CONTRACT VERIFIED (live execution is separate)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
