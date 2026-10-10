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

    # PR93: CI may retain Actions artifacts but must never publish GitHub Releases.
    if job(workflow, "publish-desktop"):
        failures.append("unsigned desktop GitHub Release publisher is forbidden")
    for marker in ("gh release create ", "gh release upload ", "gh release delete "):
        if marker in workflow:
            failures.append("GitHub Release mutation is forbidden outside trusted release workflow")
    if "contents: write" in workflow:
        failures.append("artifact qualification workflow needs no GitHub content-write token")

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
    gate = job(release, "verify-quality-gates")
    for marker in (
        "needs: verify-release-source",
        "actions: read",
        "python3 scripts/verify_stable_build.py",
    ):
        if marker not in gate:
            failures.append("stable release must depend on green exact-main CI: " + marker)
    if "needs: verify-quality-gates" not in job(release, "validate-release"):
        failures.append("release secrets can be accessed before quality verification")
    if "git rev-parse origin/main" not in source:
        failures.append("release source must be the current main HEAD")
    if "group: kide-single-stable-release" not in release:
        failures.append("stable release publication/cleanup must be serialized")
    publish = job(release, "publish")
    for marker in (
        "needs: [build-release, sign-windows, sign-macos]",
        "python3 scripts/finalize_release_evidence.py",
        "actions/attest-build-provenance@",
        "gh release create ",
        "--latest",
        "python3 scripts/retain_latest_stable_release.py",
        "--keep-tag",
        "--expected-sha",
        "--apply",
    ):
        if marker not in publish:
            failures.append("trusted stable publisher is missing: " + marker)
    if "--prerelease" in publish:
        failures.append("stable publisher must not mark output as prerelease")
    if publish.find("gh release create ") >= publish.find("python3 scripts/retain_latest_stable_release.py"):
        failures.append("cleanup must occur only after successful stable publication")
    if "python3 scripts/check_stable_release_version.py" not in job(release, "build-release"):
        failures.append("stable release must reject stale or downgraded versions")

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
