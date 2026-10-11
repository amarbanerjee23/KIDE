#!/usr/bin/env python3
"""PR102: nonremovable hosted registry/storage safety boundary in CI."""
from __future__ import annotations

from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


def verify() -> list[str]:
    files = (
        "deploy/gcp/entrypoint.sh",
        "scripts/qualify_registry_storage.py",
        "scripts/guard_hosted_storage.py",
        "scripts/staging_registry_policy.py",
        "deploy/gcp/deploy-registry-staging.sh",
        "docs/pr102-storage-rollout.md",
        "scripts/tests/test_pr102_storage_rollout.py",
    )
    errors = [f"missing PR102 safety file: {x}" for x in files if not (ROOT / x).is_file()]
    if errors:
        return errors

    entrypoint = read(files[0])
    for token in (
        'storage_mode="${KIDE_STORAGE_MODE:-legacy-gcs}"',
        'KIDE_STORAGE_MODE',
        '"legacy-gcs"',
        'registry-posix)',
        'registry_root',
        'KIDE-POSIX-REGISTRY-STAGING-v1',
        'KIDE_GATEWAY_ROLE_BINDINGS',
        'KIDE_GLSP_ROLE_BINDINGS',
        'KIDE_API_ROLE_BINDINGS',
        'if [[ "${storage_mode}" == "legacy-gcs" ]]; then',
        'role_bindings=""',
    ):
        if token not in entrypoint:
            errors.append("runtime storage/role boundary missing: " + token)

    qualifier = read(files[1])
    for token in (
        'ALLOWED_FSTYPES', 'RENAME_NOREPLACE', 'renameat2',
        'os.fsync(', 'os.replace(', 'marker.is_symlink()',
        '"staging-only-offline"', "tempfile.TemporaryDirectory",
    ):
        if token not in qualifier:
            errors.append("POSIX offline qualification gate removed: " + token)

    guard = read(files[2])
    for token in (
        '"KIDE_HOSTED_PROJECTS_ROOT"', '"KIDE_STORAGE_MODE"',
        '"nfs"', '"gcsfuse.run.googleapis.com"', 'protect_legacy_service(',
        'raise StorageModeError', 'Cloud Run storage identity inspection failed',
    ):
        if token not in guard:
            errors.append("legacy rollout storage protection removed: " + token)

    canonical = [read(path) for path in (
        "cloudbuild.yaml",
        "deploy/gcp/cloudbuild.yaml",
        "deploy/gcp/cloudbuild-release.yaml",
    )]
    if len(set(canonical)) != 1:
        errors.append("canonical Cloud Build configs diverged")
    for path in (
        "cloudbuild.yaml", "deploy/gcp/cloudbuild.yaml",
        "deploy/gcp/cloudbuild-release.yaml", "deploy/gcp/cloudbuild-deploy.yaml"
    ):
        content = read(path)
        if 'scripts/guard_hosted_storage.py' not in content:
            errors.append("ordinary Cloud Build lacks storage guard: " + path)
        elif content.index('scripts/guard_hosted_storage.py') > content.index(
            'gcloud run deploy "${_KIDE_SERVICE_NAME}"'
        ):
            errors.append("storage guard must execute before backend deploy: " + path)

    staged = read(files[4])
    for token in (
        'DEPLOY_APPROVAL', 'STORAGE_PREFLIGHT_APPROVAL',
        'scripts/staging_registry_policy.py', 'verify-existing',
        '--no-traffic', '--tag registry-candidate',
        '--max 1', '--concurrency 1', 'type=nfs',
        'KIDE_STORAGE_MODE=registry-posix',
        'KIDE_HOSTED_PROJECTS_ROOT=/data/hosted-projects',
        '--vpc-egress private-ranges-only', 'STAGING_BACKEND_IMAGE',
        '--allow-unauthenticated', 'STAGING_WEB_ORIGIN',
    ):
        if token not in staged:
            errors.append("isolated staging deploy fence removed: " + token)
    if 'gcloud run services update-traffic' in staged:
        errors.append("staging script must never auto-promote production traffic")

    policy = read(files[3])
    for token in ('ipaddress.ip_network', 'verify_existing(',
                  'sha256:', 'registry-staging', 'nfs'):
        if token not in policy:
            errors.append("staging config safety policy missing: " + token)

    build = read(".github/workflows/build.yml")
    if 'python3 scripts/verify_registry_storage_rollout.py' not in build:
        errors.append("packaged CI must verify PR102 runtime storage safety")
    runbook = read(files[5])
    for token in ('Cloud Storage FUSE', 'NFS', 'rollback', 'legacy',
                  'staging', 'No source or role grant is migrated'):
        if token.lower() not in runbook.lower():
            errors.append("storage transition runbook lacks " + token)
    return errors


def main() -> int:
    errors = verify()
    for error in errors:
        print("ERROR:", error, file=sys.stderr)
    if errors:
        return 2
    print("PR102 STAGING POSIX REGISTRY + LEGACY ROLLOUT SAFETY VERIFIED (LIVE GCP UNQUALIFIED)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
