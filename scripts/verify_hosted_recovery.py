#!/usr/bin/env python3
"""PR90 non-removable recovery, restore and monitoring qualification gate."""
from __future__ import annotations

import pathlib
import sys

ROOT = pathlib.Path(__file__).resolve().parents[1]


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


def verify() -> list[str]:
    errors = []
    required = [
        "scripts/hosted_snapshot.py",
        "scripts/hosted_probe.py",
        "scripts/tests/test_hosted_snapshot.py",
        "scripts/tests/test_hosted_probe.py",
        ".github/workflows/hosted-runtime-health.yml",
        ".github/workflows/build.yml",
        "docs/hosted-recovery-operations.md",
    ]
    for path in required:
        if not (ROOT / path).is_file():
            errors.append("missing hosted recovery contract file: " + path)
    if errors:
        return errors

    snapshot = read("scripts/hosted_snapshot.py")
    for token in (
        "MAX_BYTES", "MAX_FILES", "MAX_FILE_BYTES",
        "project.meta.hosted.ownerPrincipalId",  # optional metadata: handled opaquely
        "validate_contexts(", "fingerprint(",
        "registry changed during snapshot",
        "O_NOFOLLOW",
        "snapshot output already exists",
        "rename_no_replace(",
        'getattr(libc, "renameat2", None)',
        "RENAME_NOREPLACE",
        "restore target already exists",
        "snapshot contains unknown entries or unsafe paths",
        "--offline-confirmed",
    ):
        # Ownership is preserved through exact byte capture; no role rewrite.
        if token not in snapshot:
            errors.append("offline recovery safety invariant absent: " + token)

    probe = read("scripts/hosted_probe.py")
    for token in (
        "check_web_health(", "/api/v1/health", "/api/v1/version",
        "/kide-version.json", "frontend.get", 'version["buildId"]',
        "max-latency-ms", "ssl.create_default_context()",
    ):
        if token not in probe:
            errors.append("live read-only monitoring capability absent: " + token)

    tests = read("scripts/tests/test_hosted_snapshot.py")
    for token in (
        "test_full_registry_roundtrip",
        "test_nonempty_restore_destination_is_never_modified",
        "test_restore_malformed_archive_fails_without_publish",
        "test_refuses_symlinks_and_hardlinks",
        "test_refuses_writer_activity_during_capture",
        "test_refuses_missing_offline_operator_confirmation",
        "test_rejects_unsafe_zip_and_unknown_payload_entries",
        "test_rejects_duplicate_archived_entries",
    ):
        if token not in tests:
            errors.append("adversarial recovery test removed: " + token)

    monitor = read(".github/workflows/hosted-runtime-health.yml")
    for token in (
        "schedule:", "workflow_dispatch:", "contents: read",
        "KIDE_MONITOR_API_ORIGIN", "KIDE_MONITOR_WEB_ORIGIN",
        "python3 scripts/hosted_probe.py",
    ):
        if token not in monitor:
            errors.append("opt-in health-watch invariant removed: " + token)

    build = read(".github/workflows/build.yml")
    if "python3 scripts/verify_hosted_recovery.py" not in build:
        errors.append("hosted recovery governance check must run on pull requests")
    if "python3 -m unittest discover -s scripts/tests" not in build:
        errors.append("hosted recovery tests must remain in the packaged CI suite")

    runbook = read("docs/hosted-recovery-operations.md")
    for token in (
        "Cloud Storage FUSE", "--offline-confirmed",
        "staging", "RPO", "RTO", "owner grants",
    ):
        if token not in runbook:
            errors.append("operational runbook missing: " + token)

    return errors


def main() -> int:
    failures = verify()
    for issue in failures:
        print("ERROR:", issue, file=sys.stderr)
    if failures:
        return 2
    print("PR90 HOSTED RECOVERY SAFETY CONTRACT VERIFIED")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
