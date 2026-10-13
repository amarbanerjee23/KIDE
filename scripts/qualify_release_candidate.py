#!/usr/bin/env python3
"""Fail-closed PR107 gates for the actual staging and operational release decision.

A green pull request, mock WebSocket tests or anonymous /health cannot
substitute for an operator-approved, unmocked staging engineering run.
The operational signoff is a second protected-environment workflow, tied to
the identical full commit SHA and to a successful staging run.
"""
from __future__ import annotations

import argparse
import os
import re
import sys

try:
    from scripts.retain_latest_stable_release import COMMIT, GitHubAPI, RetentionError
except ModuleNotFoundError:  # direct execution as scripts/qualify_release_candidate.py
    from retain_latest_stable_release import COMMIT, GitHubAPI, RetentionError

STAGING_WORKFLOW = "Live Staging Engineering Acceptance"
STAGING_JOB = "Real Firebase / REST / Xtext / GLSP / generation staging acceptance"
OPERATIONS_WORKFLOW = "KIDE Release Candidate Operational Qualification"
OPERATIONS_JOB = "Verify reviewed recovery, rollback and desktop acceptance"
DIGEST = re.compile(r"^[0-9a-f]{64}$")
EVIDENCE_ID = re.compile(r"^[A-Za-z0-9][A-Za-z0-9._:/-]{7,159}$")
ACKNOWLEDGEMENT = "I_APPROVE_THIS_SHA_FOR_PRODUCTION_RELEASE"
DESKTOP_OS = ("windows-x86_64", "linux-x86_64", "macos-x86_64", "macos-aarch64")


def latest_qualified_run(runs: list[dict], name: str, source_sha: str) -> dict:
    """A recent failed or running attempt must not be hidden by an old success."""
    if not COMMIT.fullmatch(source_sha):
        raise RetentionError("A full source SHA is required")
    candidates = [run for run in runs if isinstance(run, dict)
                  and run.get("name") == name
                  and run.get("head_sha") == source_sha
                  and run.get("head_branch") == "main"
                  and run.get("event") == "workflow_dispatch"]
    if not candidates:
        raise RetentionError(name + ": no operator-dispatched main acceptance for this exact SHA")
    candidates.sort(key=lambda run: (run.get("created_at", ""),
                                      run.get("run_attempt", 0), run.get("id", 0)))
    latest = candidates[-1]
    if latest.get("status") != "completed" or latest.get("conclusion") != "success":
        raise RetentionError(name + ": latest main acceptance is not successful")
    if not isinstance(latest.get("id"), int):
        raise RetentionError(name + ": missing verifiable workflow run ID")
    return latest


def require_job(jobs: list[dict], name: str) -> None:
    matched = [job for job in jobs if isinstance(job, dict) and job.get("name") == name]
    if len(matched) != 1 or matched[0].get("status") != "completed" or matched[0].get("conclusion") != "success":
        raise RetentionError("Required acceptance job was not successful: " + name)


def validate_operations(
    source_sha: str, staging_run: dict, staging_jobs: list[dict],
    restore_digest: str, restore_record: str, rollback_record: str,
    desktop_results: dict[str, str], approval: str
) -> None:
    if not COMMIT.fullmatch(source_sha):
        raise RetentionError("Operations signoff requires a full release SHA")
    if (staging_run.get("name") != STAGING_WORKFLOW
            or staging_run.get("head_sha") != source_sha
            or staging_run.get("head_branch") != "main"
            or staging_run.get("event") != "workflow_dispatch"
            or staging_run.get("status") != "completed"
            or staging_run.get("conclusion") != "success"):
        raise RetentionError("Live staging acceptance must have passed for the exact release commit")
    require_job(staging_jobs, STAGING_JOB)
    if not DIGEST.fullmatch(restore_digest):
        raise RetentionError("Provide verified restored-snapshot SHA256 (64 lowercase hex chars)")
    if not EVIDENCE_ID.fullmatch(restore_record):
        raise RetentionError("Provide the operator's traceable NFS/backup restore evidence ID")
    if not EVIDENCE_ID.fullmatch(rollback_record):
        raise RetentionError("Provide the operator's traceable hosted rollout/rollback evidence ID")
    if set(desktop_results) != set(DESKTOP_OS) or any(
            desktop_results[platform] != "verified" for platform in DESKTOP_OS):
        raise RetentionError("Windows, Linux and both macOS desktop platforms require real acceptance")
    if approval != ACKNOWLEDGEMENT:
        raise RetentionError("Operator has not acknowledged production release qualification")


def inventory(api: GitHubAPI, sha: str) -> list[dict]:
    result: list[dict] = []
    for page in range(1, 21):
        payload = api.request("/actions/runs?head_sha=" + sha + "&per_page=100&page=" + str(page))
        if not isinstance(payload, dict) or not isinstance(payload.get("workflow_runs"), list):
            raise RetentionError("GitHub Actions acceptance inventory is incomplete")
        items = payload["workflow_runs"]
        result.extend(items)
        if len(items) < 100:
            return result
    raise RetentionError("Actions inventory exceeded pagination bound")


def jobs_for(api: GitHubAPI, run_id: int) -> list[dict]:
    jobs: list[dict] = []
    for page in range(1, 11):
        result = api.request(f"/actions/runs/{run_id}/jobs?per_page=100&page={page}")
        if not isinstance(result, dict) or not isinstance(result.get("jobs"), list):
            raise RetentionError("GitHub acceptance job inventory is malformed")
        items = result["jobs"]
        jobs.extend(items)
        if len(items) < 100:
            return jobs
    raise RetentionError("Actions job inventory exceeded pagination bound")


def qualify_publication(api: GitHubAPI, source_sha: str) -> None:
    runs = inventory(api, source_sha)
    # Both independent protected workflows are mandatory, not optional.
    staging = latest_qualified_run(runs, STAGING_WORKFLOW, source_sha)
    operations = latest_qualified_run(runs, OPERATIONS_WORKFLOW, source_sha)
    require_job(jobs_for(api, staging["id"]), STAGING_JOB)
    require_job(jobs_for(api, operations["id"]), OPERATIONS_JOB)
    if operations.get("created_at", "") < staging.get("created_at", ""):
        raise RetentionError("Operational review must follow the latest live staging acceptance")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("mode", choices=("publication", "operations"))
    parser.add_argument("--repository", default=os.environ.get("GITHUB_REPOSITORY", ""))
    parser.add_argument("--sha", default=os.environ.get("GITHUB_SHA", ""))
    parser.add_argument("--staging-run-id", type=int)
    parser.add_argument("--restore-sha256", default="")
    parser.add_argument("--restore-record", default="")
    parser.add_argument("--rollback-record", default="")
    for platform in DESKTOP_OS:
        parser.add_argument("--" + platform, default="")
    parser.add_argument("--acknowledgement", default="")
    args = parser.parse_args()
    try:
        api = GitHubAPI(args.repository, os.environ.get("GH_TOKEN", ""))
        if args.mode == "publication":
            qualify_publication(api, args.sha)
        else:
            if not args.staging_run_id or args.staging_run_id < 1:
                raise RetentionError("Staging workflow run ID is required")
            candidate = api.request("/actions/runs/" + str(args.staging_run_id))
            if not isinstance(candidate, dict):
                raise RetentionError("Staging acceptance run cannot be verified")
            validate_operations(
                args.sha, candidate, jobs_for(api, args.staging_run_id),
                args.restore_sha256, args.restore_record, args.rollback_record,
                {platform: getattr(args, platform.replace("-", "_"))
                 for platform in DESKTOP_OS},
                args.acknowledgement,
            )
        print("PR107 RELEASE CANDIDATE ACCEPTED: exact main SHA, staging and operator evidence")
        return 0
    except RetentionError as exc:
        print("RELEASE CANDIDATE BLOCKED:", exc, file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
