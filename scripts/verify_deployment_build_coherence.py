#!/usr/bin/env python3
from __future__ import annotations

import pathlib
import sys

ROOT = pathlib.Path(__file__).resolve().parents[1]


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


def verify() -> list[str]:
    errors: list[str] = []

    docker = read("deploy/web/Dockerfile")
    for token in (
        "ARG VITE_KIDE_WEB_BUILD_ID=dev",
        "ENV VITE_KIDE_WEB_BUILD_ID=",
        "dist/kide-version.json",
        'component:"web"',
        "buildId:process.env.VITE_KIDE_WEB_BUILD_ID",
    ):
        if token not in docker:
            errors.append(f"web image build identity contract missing: {token}")

    app = read("web/src/App.tsx")
    if "VITE_KIDE_WEB_BUILD_ID" not in app or "Web build {WEB_BUILD_ID}" not in app:
        errors.append("Web settings no longer expose the deployed web build ID")

    canonical = (
        "cloudbuild.yaml",
        "deploy/gcp/cloudbuild.yaml",
        "deploy/gcp/cloudbuild-release.yaml",
    )
    canonical_contents = [read(path) for path in canonical]
    if len(set(canonical_contents)) != 1:
        errors.append("canonical hosted Cloud Build configs diverged")
    for path, content in zip(canonical, canonical_contents):
        for token in (
            'VITE_KIDE_WEB_BUILD_ID=${SHORT_SHA}',
            '"$${BACKEND_URL}/api/v1/version"',
            '"$${WEB_URL}/kide-version.json"',
            '\'"buildId":"${SHORT_SHA}"\' /tmp/kide-api-version',
            '\'"buildId":"${SHORT_SHA}"\' /tmp/kide-web-version',
        ):
            if token not in content:
                errors.append(f"{path} missing deployment coherence evidence: {token}")

    deploy = read("deploy/gcp/cloudbuild-deploy.yaml")
    for token in (
        'VITE_KIDE_WEB_BUILD_ID=${_KIDE_QUALIFIER}',
        '"$${BACKEND_URL}/api/v1/version"',
        '"$${WEB_URL}/kide-version.json"',
        '\'"buildId":"${_KIDE_QUALIFIER}"\' /tmp/kide-api-version',
        '\'"buildId":"${_KIDE_QUALIFIER}"\' /tmp/kide-web-version',
    ):
        if token not in deploy:
            errors.append(f"deployment-only Cloud Build missing coherence evidence: {token}")

    standalone = read("deploy/web/cloudbuild.yaml")
    if "VITE_KIDE_WEB_BUILD_ID=${_KIDE_WEB_BUILD_ID}" not in standalone:
        errors.append("standalone Web Cloud Build does not stamp a build ID")
    if "_KIDE_WEB_BUILD_ID: \'${BUILD_ID}\'" not in standalone:
        errors.append("standalone Web Cloud Build build-ID substitution is missing")

    workflow = read(".github/workflows/cloud-run.yml")
    for token in (
        'VITE_KIDE_WEB_BUILD_ID=ci${GITHUB_SHA:0:12}',
        "kide-version.json",
        '\\\"buildId\\\":\\\"ci${GITHUB_SHA:0:12}\\\"',
    ):
        if token not in workflow:
            errors.append(f"Cloud Run image workflow missing Web build qualification: {token}")

    build = read(".github/workflows/build.yml")
    if "python3 scripts/verify_deployment_build_coherence.py" not in build:
        errors.append("Build KIDE no longer runs deployment build coherence verification")

    return errors


def main() -> int:
    errors = verify()
    if errors:
        for error in errors:
            print(f"ERROR: {error}", file=sys.stderr)
        return 2
    print("Web/backend deployment build coherence verified")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
