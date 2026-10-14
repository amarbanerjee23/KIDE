"""PR107: real evidence is mandatory before trusted publication or pruning."""
from __future__ import annotations

import unittest

from scripts.qualify_release_candidate import (
    ACKNOWLEDGEMENT, DESKTOP_OS, OPERATIONS_JOB, OPERATIONS_WORKFLOW,
    STAGING_JOB, STAGING_WORKFLOW, latest_qualified_run, require_job,
    validate_operations, qualify_publication,
)
from scripts.retain_latest_stable_release import RetentionError

SHA = "a" * 40
OTHER_SHA = "b" * 40


def run(name: str, run_id: int, *, sha: str = SHA, created: str | None = None,
        conclusion: str = "success", event: str = "workflow_dispatch",
        branch: str = "main", status: str = "completed") -> dict:
    return {
        "id": run_id, "name": name,
        "path": (".github/workflows/live-staging-acceptance.yml"
                 if name == STAGING_WORKFLOW else
                 ".github/workflows/release-candidate-qualification.yml"),
        "head_sha": sha,
        "head_branch": branch, "event": event, "status": status,
        "conclusion": conclusion,
        "created_at": created or f"2026-10-13T12:00:{run_id % 60:02d}Z",
        "run_attempt": 1,
    }


def verified_desktops() -> dict[str, str]:
    return {platform: "verified" for platform in DESKTOP_OS}


def job(name: str, *, conclusion: str = "success", status: str = "completed") -> dict:
    return {"name": name, "status": status, "conclusion": conclusion}


class FakeAPI:
    def __init__(self, runs: list[dict], stage_jobs=None, ops_jobs=None):
        self.runs = runs
        self.stage_jobs = stage_jobs if stage_jobs is not None else [job(STAGING_JOB)]
        self.ops_jobs = ops_jobs if ops_jobs is not None else [job(OPERATIONS_JOB)]
        self.calls = []

    def request(self, path: str, method: str = "GET") -> dict:
        self.calls.append(path)
        if path.startswith("/actions/runs?"):
            return {"workflow_runs": self.runs}
        if path.startswith("/actions/runs/"):
            rid = int(path.split("/")[3])
            if rid == 101:
                return {"jobs": self.stage_jobs}
            if rid == 102:
                return {"jobs": self.ops_jobs}
        raise AssertionError("Unexpected GitHub API path: " + path)


class CandidateQualificationTest(unittest.TestCase):
    def setUp(self):
        self.stage = run(STAGING_WORKFLOW, 101, created="2026-10-13T10:00:00Z")
        self.ops = run(OPERATIONS_WORKFLOW, 102, created="2026-10-13T12:00:00Z")

    def test_publication_requires_exact_main_sha_and_both_distinct_successful_runs(self):
        api = FakeAPI([self.stage, self.ops])
        qualify_publication(api, SHA)
        self.assertTrue(any("/actions/runs/101/jobs" in x for x in api.calls))
        self.assertTrue(any("/actions/runs/102/jobs" in x for x in api.calls))

    def test_missing_real_stage_or_operations_never_degrades_to_mocked_ci(self):
        for runs in ([self.ops], [self.stage], [], [
            run(STAGING_WORKFLOW, 101, event="pull_request"), self.ops
        ], [run(STAGING_WORKFLOW, 101, sha=OTHER_SHA), self.ops]):
            with self.subTest(runs=runs), self.assertRaises(RetentionError):
                qualify_publication(FakeAPI(runs), SHA)

    def test_workflow_with_spoofed_name_from_another_path_is_rejected(self):
        impersonation = {**self.stage, "path": ".github/workflows/unapproved-staging.yml"}
        with self.assertRaises(RetentionError):
            qualify_publication(FakeAPI([impersonation, self.ops]), SHA)
        with self.assertRaises(RetentionError):
            validate_operations(
                SHA, impersonation, [job(STAGING_JOB)], "f" * 64,
                "ops/restore/2026-10-13", "ops/rollback/2026-10-13",
                verified_desktops(), ACKNOWLEDGEMENT,
            )

    def test_failed_latest_attempt_overrides_earlier_success(self):
        attempts = [self.stage, self.ops,
                    run(STAGING_WORKFLOW, 103, created="2026-10-13T13:00:00Z", conclusion="failure")]
        with self.assertRaisesRegex(RetentionError, "latest main acceptance"):
            qualify_publication(FakeAPI(attempts), SHA)
        with self.assertRaisesRegex(RetentionError, "latest main acceptance"):
            latest_qualified_run([self.stage, run(STAGING_WORKFLOW, 104,
                    created="2026-10-13T15:00:00Z", status="in_progress",
                    conclusion=None)], STAGING_WORKFLOW, SHA)

    def test_skipped_or_failed_real_job_blocks_successful_workflow_wrapper(self):
        for stage_jobs in ([], [job(STAGING_JOB, conclusion="skipped")],
                           [job(STAGING_JOB, conclusion="failure")],
                           [job(STAGING_JOB), job(STAGING_JOB)]):
            with self.subTest(stage_jobs=stage_jobs), self.assertRaises(RetentionError):
                qualify_publication(FakeAPI([self.stage, self.ops], stage_jobs=stage_jobs), SHA)
        with self.assertRaises(RetentionError):
            qualify_publication(FakeAPI([self.stage, self.ops], ops_jobs=[]), SHA)

    def test_signoff_must_follow_latest_live_stage(self):
        late_stage = run(STAGING_WORKFLOW, 101, created="2026-10-13T14:00:00Z")
        with self.assertRaisesRegex(RetentionError, "follow"):
            qualify_publication(FakeAPI([late_stage, self.ops]), SHA)

    def test_operational_signoff_requires_documented_recovery_and_four_desktops(self):
        validate_operations(
            SHA, self.stage, [job(STAGING_JOB)], "f" * 64,
            "ops/restore/2026-10-13", "ops/rollback/2026-10-13",
            verified_desktops(), ACKNOWLEDGEMENT,
        )
        args = [SHA, self.stage, [job(STAGING_JOB)], "f" * 64,
                "ops/restore/2026-10-13", "ops/rollback/2026-10-13",
                verified_desktops(), ACKNOWLEDGEMENT]
        mutations = [
            (0, OTHER_SHA + "bad"),
            (1, run(STAGING_WORKFLOW, 101, sha=OTHER_SHA)),
            (1, run(STAGING_WORKFLOW, 101, conclusion="failure")),
            (1, run(STAGING_WORKFLOW, 101, event="pull_request")),
            (2, []),
            (3, "invalid"),
            (4, "bad"),
            (5, ""),
            (6, {**verified_desktops(), "macos-aarch64": "skipped"}),
            (6, {"windows-x86_64": "verified"}),
            (7, "approved"),
        ]
        for index, altered in mutations:
            with self.subTest(index=index, altered=altered):
                case = args[:]
                case[index] = altered
                with self.assertRaises(RetentionError):
                    validate_operations(*case)

    def test_release_qa_does_not_accept_forged_issuer_or_non_main(self):
        for bad in (run(STAGING_WORKFLOW, 101, branch="feature"),
                    run(STAGING_WORKFLOW, 101, event="pull_request")):
            with self.assertRaises(RetentionError):
                latest_qualified_run([bad], STAGING_WORKFLOW, SHA)

    def test_expected_staging_job_identity_is_verifiable(self):
        require_job([job(STAGING_JOB)], STAGING_JOB)
        with self.assertRaises(RetentionError):
            require_job([job("anonymous health only")], STAGING_JOB)


if __name__ == "__main__":
    unittest.main()
