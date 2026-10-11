#!/usr/bin/env python3
"""PR104: fail closed if multi-engineer integrity and offline recovery gates drift."""
from __future__ import annotations

from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]


def read(relative: str) -> str:
    return (ROOT / relative).read_text(encoding="utf-8")


def verify() -> list[str]:
    required = [
        "web/src/autosave.ts",
        "web/tests/autosave.test.ts",
        "web/e2e/live-hosted.spec.ts",
        "com.kide.enterprise.modelrepo/src/com/kide/enterprise/modelrepo/FileModelRepository.java",
        "com.kide.enterprise.modelrepo.tests/src/com/kide/enterprise/modelrepo/tests/ModelRepositoryTest.java",
        "scripts/hosted_snapshot.py",
        "scripts/tests/test_hosted_snapshot.py",
        "docs/pr104-concurrent-engineering-recovery.md",
        ".github/workflows/build.yml",
        ".github/workflows/live-staging-acceptance.yml",
    ]
    errors = [f"PR104 durability source absent: {x}" for x in required
              if not (ROOT / x).is_file()]
    if errors:
        return errors

    autosave = read(required[0])
    for token in (
        "if (this.pending === undefined) this.pending = content;",
        "this.events.onConflict(normalized, this.pending, expectedEtag)",
        "this.events.onError(normalized)",
        "this.saving = false",
        "this.pending !== undefined",
    ):
        if token not in autosave:
            errors.append("browser autosave newer-draft protection missing: " + token)
    if "this.pending = content;\n        this.events.onState(\"conflict\")" in autosave:
        errors.append("stale autosave content can overwrite newer pending edit")
    browser_tests = read(required[1])
    for token in (
        "preserves the newest edit after an older in-flight 409 conflict",
        "never replaces a newer draft after a previous transport failure",
        'expect(coordinator.hasUnsettledWork).toBe(true)',
        'expect(save).toHaveBeenNthCalledWith(2, "newest editor text", "etag-1")',
        'expect(save).toHaveBeenNthCalledWith(2, "typed after request", "base-etag")',
    ):
        if token not in browser_tests:
            errors.append("browser autosave race test missing: " + token)

    java = read(required[3])
    for token in (
        'WRITER_GUARD_DIR = "writer.guard"',
        "Files.createDirectory(writerGuardDir)",
        "catch (FileAlreadyExistsException e)",
        "throw new RevisionConflictException(",
        "try (WriterGuard ignored = acquireWriterGuard())",
        "recoverTransactions();",
        "Path journal = null;",
        "rollbackJournal(journal)",
        "Files.delete(writerGuardDir)",
    ):
        if token not in java:
            errors.append("cross-process write fence missing: " + token)
    if "FileChannel.lock(" in java or "tryLock(" in java:
        errors.append("Cloud Run NFS writer fencing must not depend on file locking")
    java_tests = read(required[4])
    for token in (
        "writerGuardRefusesConcurrentInstanceAndDoesNotDestroyCommittedModel",
        "simultaneousEngineersCannotBothCommitSameExpectedRevision",
        "CountDownLatch(8)",
        "1, successes",
        "2L, committed.revision().version()",
    ):
        if token not in java_tests:
            errors.append("model repository concurrent writer regression removed: " + token)

    recovery = read(required[5])
    for token in (
        'components[4] == "writer.guard"',
        'components[4] == "transactions"',
        "model repository writer guard present",
        "model repository has an unresolved transaction journal",
        "validate_contexts(files, set(directories))",
    ):
        if token not in recovery:
            errors.append("snapshot writer-fencing invariant removed: " + token)
    recovery_tests = read(required[6])
    for token in (
        "test_refuses_locked_registry_snapshot_and_does_not_publish_archive",
        "test_refuses_incomplete_model_transaction_journals",
        "test_rejects_replay_of_archive_containing_a_writer_guard",
    ):
        if token not in recovery_tests:
            errors.append("offline backup negative-test removed: " + token)

    live = read(required[2])
    for token in (
        "KIDE_LIVE_DURABILITY_ITERATIONS",
        "stressIterations > 32",
        "Promise.all(candidates.map(commit))",
        "expect(statuses).toEqual([200, 409])",
        "expect(readback.content).toBe(candidates[winnerIndex])",
        "expect(readback.etag).toBe(acknowledged.etag)",
        'other.token, "GET", racePath, 403',
        'KIDE_LIVE_ALLOW_WRITES',
        'KIDE_LIVE_APPROVED_API_HOST',
    ):
        if token not in live:
            errors.append("real staging CAS soak acceptance missing: " + token)
    for forbidden in ("await page.route(", "await page.routeWebSocket(",
                      "route.fulfill(", "test.skip("):
        if forbidden in live:
            errors.append("live durability acceptance must not use mocks: " + forbidden)
    build = read(required[8])
    for token in (
        "python3 scripts/verify_pr104_durability.py",
        "python3 -m unittest discover -s scripts/tests",
        "npm run test",
        "Build Eclipse plug-ins and portable products",
        "npx playwright test --config playwright.live.config.ts --list",
    ):
        if token not in build:
            errors.append("mandatory CI durability coverage absent: " + token)
    workflow = read(required[9])
    if "workflow_dispatch:" not in workflow or "environment: staging" not in workflow:
        errors.append("staging durability load test must require protected explicit dispatch")
    if "npm run test:live" not in workflow:
        errors.append("protected staging workflow must execute actual live acceptance")

    runbook = read(required[7])
    for token in ("RPO", "RTO", "rmdir", "zero live writers", "roll",
                  "writer.guard", "two sessions", "no blind retry"):
        if token.lower() not in runbook.lower():
            errors.append("operator runbook durability qualification gap: " + token)
    return errors


def main() -> int:
    problems = verify()
    for problem in problems:
        print("ERROR:", problem, file=sys.stderr)
    if problems:
        return 2
    print("PR104 MULTI-ENGINEER FENCING + OFFLINE RESTORE BOUNDARY VERIFIED; LIVE STAGING NOT RUN")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
