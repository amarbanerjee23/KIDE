#!/usr/bin/env python3
"""PR91 security contract (static regression gate; not a penetration test)."""
from __future__ import annotations

import pathlib
import re
import sys

ROOT = pathlib.Path(__file__).resolve().parents[1]
EXPR = "$" + "{{"


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


def job(workflow: str, name: str) -> str:
    match = re.search(
        r"(?ms)^  " + re.escape(name) + r":\n(.*?)(?=^  [a-z][a-z0-9-]*:\n|\Z)",
        workflow
    )
    return match.group(1) if match else ""


def verify() -> list[str]:
    failures: list[str] = []
    paths = [
        ".github/workflows/publish-build-artifacts.yml",
        ".github/workflows/release.yml",
        ".github/workflows/cloud-run.yml",
        ".github/workflows/build.yml",
        "deploy/gcp/nginx.conf.template",
        "deploy/web/nginx.conf.template",
        "scripts/qualify-cloud-run-backend.sh",
        "web/vite.config.ts",
        "docs/release-security-hardening.md",
    ]
    for path in paths:
        if not (ROOT / path).is_file():
            failures.append("required security contract file missing: " + path)
    if failures:
        return failures

    workflow = read(paths[0])
    top = workflow.split("\njobs:", 1)[0]
    if "permissions:\n  contents: read" not in top:
        failures.append("artifact workflow must default to read-only token")
    if "packages: write" in top or "contents: write" in top:
        failures.append("artifact workflow must not have a top-level write token")
    for name in ("desktop", "cloud-run"):
        section = job(workflow, name)
        if not section or "permissions:\n      contents: read" not in section:
            failures.append("PR qualification needs explicit read-only permissions: " + name)
        for dangerous in ("packages: write", "contents: write",
                          "docker push ", "gh release create ", "gh release upload ", "GHCR_TOKEN:"):
            if dangerous in section:
                failures.append("PR qualification includes write or publication: " + name)

    desktop = job(workflow, "publish-desktop")
    for marker in (
        "needs: desktop",
        "github.event_name == 'workflow_dispatch' && github.ref_name == 'main'",
        "permissions:\n      contents: write",
        "Existing KIDE preview release is immutable",
        "actions/download-artifact@3e5f45b2cfb9172054b4087a40e8e0b5a5461e7c",
    ):
        if marker not in desktop:
            failures.append("trusted desktop publisher missing: " + marker)
    if "--clobber" in desktop:
        failures.append("desktop releases must not be overwritten")

    image = job(workflow, "publish-cloud-run")
    for marker in (
        "needs: cloud-run", "github.ref_name == 'main'",
        "github.event_name == 'push'",
        "permissions:\n      contents: read\n      packages: write",
        "docker push ", "docker login ghcr.io",
        "ref: " + EXPR + " env.SOURCE_SHA }}",
    ):
        if marker not in image:
            failures.append("trusted image publisher missing: " + marker)
    if "pull_request" in image:
        failures.append("privileged image publisher must not accept pull requests")

    release = read(paths[1])
    source = job(release, "verify-release-source")
    for marker in (
        'test "$KIDE_REF" = "refs/heads/main"',
        "git merge-base --is-ancestor",
        "refs/tags/v",
    ):
        if marker not in source:
            failures.append("trusted release source validation missing: " + marker)
    if "secrets." in source or "contents: write" in source:
        failures.append("release source check must run without secrets or write token")
    for name in ("validate-release", "build-release", "sign-windows",
                 "sign-macos", "publish"):
        if "environment: trusted-release" not in job(release, name):
            failures.append("trusted-release environment missing from: " + name)
    if "needs: verify-release-source" not in job(release, "validate-release"):
        failures.append("release secrets can be accessed before source verification")
    if "KIDE_REQUESTED_VERSION: " + EXPR + " inputs.version }}" not in release:
        failures.append("manual release input must use an environment variable")
    if '"' + EXPR + " github.event.inputs.version }}" + '"' in release:
        failures.append("manual version is directly interpolated into shell code")

    for path in (paths[4], paths[5]):
        nginx = read(path)
        for header in (
            'X-Content-Type-Options "nosniff" always',
            'X-Frame-Options "DENY" always',
            'Referrer-Policy "no-referrer" always',
            'Permissions-Policy "camera=(), microphone=(), geolocation=(), payment=()" always',
        ):
            if header not in nginx:
                failures.append(path + " missing security header: " + header)
    if 'Cache-Control "no-store" always' not in read(paths[4]):
        failures.append("backend must prohibit response caching")
    web = read(paths[5])
    if "location = /kide-version.json" not in web or "expires -1" not in web:
        failures.append("frontend build identity must be revalidated")
    if "sourcemap: false" not in read(paths[7]):
        failures.append("public production Vite must not emit source maps")

    for marker in ("security_headers=", "X-Content-Type-Options: nosniff",
                   "X-Frame-Options: DENY", "Cache-Control: no-store"):
        if marker not in read(paths[6]):
            failures.append("backend packaged security probe missing: " + marker)
    for marker in ("Cross-Origin-Opener-Policy: same-origin",
                   "Cache-Control: no-cache",
                   'find /usr/share/nginx/html -type f -name "*.map"'):
        if marker not in read(paths[2]):
            failures.append("Web container security qualification missing: " + marker)
    if "python3 scripts/verify_release_security.py" not in read(paths[3]):
        failures.append("mandatory security contract is absent from Build KIDE")

    runbook = read(paths[8])
    for marker in ("trusted-release", "main", "staging", "tenant isolation",
                   "Xtext", "GLSP", "Firebase", "Cloud Storage FUSE"):
        if marker not in runbook:
            failures.append("security review runbook missing: " + marker)
    return failures


def main() -> int:
    failures = verify()
    for failure in failures:
        print("ERROR:", failure, file=sys.stderr)
    if failures:
        return 2
    print("PR91 TRUSTED RELEASE SECURITY CONTRACT VERIFIED")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
