#!/usr/bin/env python3
"""Require successful trusted-main CI for the exact source SHA before signing."""
from __future__ import annotations

import argparse
import os
import re
import sys

from retain_latest_stable_release import GitHubAPI, RetentionError, COMMIT

REQUIRED = ("Build KIDE", "KIDE Enterprise CI Pipeline")
OPTIONAL = ("Publish KIDE Build Artifacts", "KIDE Cloud Run Image")


def qualify(runs: list[dict], source_sha: str) -> list[str]:
    failures: list[str] = []
    if not COMMIT.fullmatch(source_sha):
        return ["Expected source commit is not a full SHA-1"]
    for name in (*REQUIRED, *OPTIONAL):
        matching = [
            run for run in runs
            if run.get("name") == name and run.get("head_sha") == source_sha
            and run.get("event") in ("push", "workflow_dispatch")
            and run.get("head_branch") == "main"
        ]
        if not matching:
            if name in REQUIRED:
                failures.append(f"{name}: no trusted main-branch CI run on source commit")
            continue
        # A failing or in-progress retry must not be hidden by an older pass.
        matching.sort(key=lambda item: (item.get("created_at", ""),
                                        item.get("run_attempt", 0), item.get("id", 0)))
        current = matching[-1]
        if current.get("status") != "completed" or current.get("conclusion") != "success":
            failures.append(name + ": latest relevant run is not successful")
    return failures


def workflow_runs(api: GitHubAPI, source_sha: str) -> list[dict]:
    results: list[dict] = []
    for page in range(1, 21):
        response = api.request(
            "/actions/runs?head_sha=" + source_sha + "&per_page=100&page=" + str(page)
        )
        if not isinstance(response, dict) or not isinstance(response.get("workflow_runs"), list):
            raise RetentionError("Incomplete GitHub Actions status response")
        items = response["workflow_runs"]
        results.extend(items)
        if len(items) < 100:
            return results
    raise RetentionError("Actions run inventory exceeded pagination limit")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--repository", default=os.environ.get("GITHUB_REPOSITORY", ""))
    parser.add_argument("--sha", default=os.environ.get("GITHUB_SHA", ""))
    args = parser.parse_args()
    try:
        api = GitHubAPI(args.repository, os.environ.get("GH_TOKEN", ""))
        failures = qualify(workflow_runs(api, args.sha), args.sha)
        if failures:
            for failure in failures:
                print("STABLE RELEASE BLOCKED:", failure, file=sys.stderr)
            return 2
        print("STABLE RELEASE QUALIFIED: Build KIDE and Enterprise CI green on exact main SHA")
        return 0
    except RetentionError as exc:
        print("STABLE RELEASE BLOCKED:", exc, file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
