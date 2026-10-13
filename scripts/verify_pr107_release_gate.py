#!/usr/bin/env python3
"""PR107: require a real same-commit staging and reviewed release process in CI."""
from __future__ import annotations

from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]


def read(name: str) -> str:
    return (ROOT / name).read_text(encoding="utf-8")


def verify() -> list[str]:
    paths = [
        ".github/workflows/release.yml",
        ".github/workflows/release-candidate-qualification.yml",
        ".github/workflows/live-staging-acceptance.yml",
        "scripts/qualify_release_candidate.py",
        "scripts/verify_stable_build.py",
        "scripts/retain_latest_stable_release.py",
        "scripts/tests/test_pr107_release_candidate.py",
        "docs/pr107-release-candidate-handoff.md",
        ".github/workflows/build.yml",
    ]
    errors = [f"release candidate gate file missing: {name}" for name in paths
              if not (ROOT / name).is_file()]
    if errors:
        return errors

    release = read(paths[0])
    for name in (
        "verify-live-qualification:",
        "needs: verify-quality-gates",
        "needs: verify-live-qualification",
        "python3 scripts/qualify_release_candidate.py publication",
        "python3 scripts/verify_stable_build.py",
        "RELEASE_TAG: ${{ steps.version.outputs.tag }}",
        "python3 scripts/check_stable_release_version.py",
        "KIDE_JAR_KEYSTORE_BASE64",
        "WINDOWS_CERTIFICATE_BASE64",
        "APPLE_SIGNING_CERT_BASE64",
        "Sign, notarize, staple and verify macOS application",
        "Attest final release artifacts",
        "Finalize checksums, manifest and CycloneDX SBOM",
        "--latest",
        "retain_latest_stable_release.py",
        "--apply",
        "group: kide-single-stable-release",
    ):
        if name not in release:
            errors.append("signed stable publication depends on unchecked gate: " + name)
    # Must not reference build-release's output within the job itself.
    if "RELEASE_TAG: ${{ needs.build-release.outputs.tag }}" in release:
        errors.append("build-release cannot read its own yet-unavailable version output")
    if release.index("verify-live-qualification:") > release.index("validate-release:"):
        errors.append("staging approval runs too late, after signing or production credentials")
    if release.index("gh release create") > release.index("retain_latest_stable_release.py"):
        errors.append("older releases could be deleted before a new stable publication")

    ops = read(paths[1])
    for name in (
        "workflow_dispatch:",
        "github.ref == 'refs/heads/main'",
        "environment: release-qualification",
        "actions: read",
        "Verify reviewed recovery, rollback and desktop acceptance",
        "qualify_release_candidate.py operations",
        "KIDE_APPROVED_STAGING_RUN",
        "KIDE_RESTORED_SNAPSHOT_SHA256",
        "KIDE_RESTORE_RECORD",
        "KIDE_ROLLBACK_RECORD",
        "KIDE_PRODUCTION_APPROVAL",
        "windows_x86_64",
        "linux_x86_64",
        "macos_x86_64",
        "macos_aarch64",
    ):
        if name not in ops:
            errors.append("protected operator acceptance missing: " + name)

    stage = read(paths[2])
    for token in (
        "workflow_dispatch:", "environment: staging",
        "Run unmocked hosted engineering acceptance", "npm run test:live",
        "KIDE_LIVE_OTHER_ENGINEER_EMAIL",
        "KIDE_LIVE_ALLOW_WRITES",
    ):
        if token not in stage:
            errors.append("real isolated staging acceptance missing: " + token)

    verifier = read(paths[3])
    for name in (
        "latest_qualified_run", "require_job", "validate_operations",
        "qualify_publication", 'head_sha") == source_sha',
        'head_branch") == "main"',
        'event") == "workflow_dispatch"',
        'get("status") != "completed"',
        'get("conclusion") != "success"',
        'require_job(jobs_for(api, staging["id"]), STAGING_JOB)',
        'require_job(jobs_for(api, operations["id"]), OPERATIONS_JOB)',
        'operations.get("created_at", "") < staging.get("created_at", "")',
        "SHA256", "REST", "Xtext", "GLSP", "generation",
        'DESKTOP_OS = ("windows-x86_64", "linux-x86_64", "macos-x86_64", "macos-aarch64")',
        "I_APPROVE_THIS_SHA_FOR_PRODUCTION_RELEASE",
    ):
        if name not in verifier:
            errors.append("strict release candidate identity/approval check missing: " + name)

    tests = read(paths[6])
    for token in ("test_publication_requires_exact_main_sha",
                  "test_missing_real_stage_or_operations",
                  "test_failed_latest_attempt",
                  "test_skipped_or_failed_real_job",
                  "test_signoff_must_follow",
                  "test_operational_signoff_requires"):
        if token not in tests:
            errors.append("release candidate adversarial test missing: " + token)

    doc = read(paths[7])
    for name in ("release-qualification", "Live Staging Engineering Acceptance",
                 "Cloud Storage", "Windows", "macOS", "Linux",
                 "rollback", "restore", "signed", "main", "Latest"):
        if name.lower() not in doc.lower():
            errors.append("release operator drill/runbook incomplete: " + name)
    if "python3 scripts/verify_pr107_release_gate.py" not in read(paths[8]):
        errors.append("PR107 gate must run in normal Build KIDE CI")
    return errors


def main() -> int:
    errors = verify()
    for error in errors:
        print("ERROR:", error, file=sys.stderr)
    if errors:
        return 2
    print("PR107 FINAL STAGING/OPERATOR/SIGNED-STABLE RELEASE GATES VERIFIED; ACTUAL APPROVAL PENDING")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
