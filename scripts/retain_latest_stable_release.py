#!/usr/bin/env python3
"""Keep exactly one fully published, signed stable KIDE GitHub Release.

Dry-run by default. The only mutation is DELETE /releases/{id}: version
tags, git history, workflow runs and registry packages are never deleted.
Call --apply only after trusted release publication has succeeded.
"""
from __future__ import annotations

import argparse
import json
import os
import re
import sys
import urllib.error
import urllib.parse
import urllib.request

STABLE_TAG = re.compile(r"^v(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)$")
REPOSITORY = re.compile(r"^[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+$")
COMMIT = re.compile(r"^[0-9a-f]{40}$")


class RetentionError(ValueError):
    pass


def stable_version(tag: object) -> tuple[int, int, int] | None:
    match = STABLE_TAG.fullmatch(tag) if isinstance(tag, str) else None
    return tuple(map(int, match.groups())) if match else None


def required_assets(tag: str) -> set[str]:
    version = tag.removeprefix("v")
    return {
        f"KIDE-{version}-windows-x86_64.zip",
        f"KIDE-{version}-linux-x86_64.tar.gz",
        f"KIDE-{version}-macos-x86_64.tar.gz",
        f"KIDE-{version}-macos-aarch64.tar.gz",
        f"KIDE-{version}-sbom.cdx.json",
        "release-manifest.json",
        "SHA256SUMS.txt",
        "kide-p2-repository.zip",
    }


def retention_plan(
    releases: list[dict], latest: dict, keep_tag: str, expected_sha: str
) -> list[dict]:
    """Return releasable deletions, or fail closed with no side effects."""
    keep_version = stable_version(keep_tag)
    if keep_version is None or not COMMIT.fullmatch(expected_sha):
        raise RetentionError("A stable vMAJOR.MINOR.PATCH tag and full source SHA are required")
    if latest.get("tag_name") != keep_tag:
        raise RetentionError("Requested release is not GitHub's current Latest stable release")
    if latest.get("draft") is not False or latest.get("prerelease") is not False:
        raise RetentionError("Latest release must be published, stable and not a draft")
    if latest.get("target_commitish") != expected_sha:
        raise RetentionError("Latest release does not point to the qualified source commit")
    if not isinstance(latest.get("id"), int):
        raise RetentionError("Latest release has no numeric GitHub ID")
    assets = latest.get("assets")
    if not isinstance(assets, list):
        raise RetentionError("Latest release assets are missing")
    asset_names = {item.get("name") for item in assets if isinstance(item, dict)
                   and isinstance(item.get("size"), int) and item["size"] > 0
                   and item.get("state") == "uploaded"}
    missing = required_assets(keep_tag) - asset_names
    if missing:
        raise RetentionError("Stable release lacks signed/evidence assets: " +
                             ", ".join(sorted(missing)))
    ids: set[int] = set()
    keep_count = 0
    candidates: list[dict] = []
    for release in releases:
        if not isinstance(release, dict) or not isinstance(release.get("id"), int):
            raise RetentionError("Release inventory is malformed; no deletion is safe")
        release_id = release["id"]
        if release_id in ids:
            raise RetentionError("Duplicate GitHub release ID")
        ids.add(release_id)
        if release_id == latest["id"]:
            keep_count += 1
            if release.get("tag_name") != keep_tag:
                raise RetentionError("Release inventory disagrees with Latest")
            continue
        version = stable_version(release.get("tag_name"))
        if version is not None and version >= keep_version and not release.get("draft"):
            raise RetentionError("A stable release at or above the keeper version exists")
        candidates.append(release)
    if keep_count != 1:
        raise RetentionError("Latest release was not found exactly once in inventory")
    return candidates


class GitHubAPI:
    def __init__(self, repo: str, token: str):
        if not REPOSITORY.fullmatch(repo):
            raise RetentionError("Invalid owner/repository")
        if not token:
            raise RetentionError("GH_TOKEN is required")
        self.root = "https://api.github.com/repos/" + repo
        self.token = token

    def request(self, path: str, method: str = "GET"):
        url = self.root + path
        request = urllib.request.Request(url, method=method, headers={
            "Authorization": "Bearer " + self.token,
            "Accept": "application/vnd.github+json",
            "X-GitHub-Api-Version": "2022-11-28",
            "User-Agent": "kide-stable-release-retention",
        })
        try:
            with urllib.request.urlopen(request, timeout=20) as response:
                data = response.read(4 * 1024 * 1024 + 1)
                if len(data) > 4 * 1024 * 1024:
                    raise RetentionError("GitHub API response too large")
                if method == "DELETE":
                    return None
                return json.loads(data)
        except urllib.error.HTTPError as exc:
            raise RetentionError(
                f"GitHub API {method} request failed (HTTP {exc.code}); refusing cleanup"
            ) from None

    def releases(self) -> list[dict]:
        results: list[dict] = []
        for page in range(1, 51):  # bounded; never delete against an incomplete inventory
            result = self.request(f"/releases?per_page=100&page={page}")
            if not isinstance(result, list):
                raise RetentionError("GitHub release inventory is not a list")
            results.extend(result)
            if len(result) < 100:
                return results
        raise RetentionError("Release inventory exceeded safe pagination limit")

    def latest(self) -> dict:
        response = self.request("/releases/latest")
        if not isinstance(response, dict):
            raise RetentionError("GitHub Latest is invalid")
        return response


def run(repo: str, token: str, keep_tag: str, sha: str, apply: bool = False) -> int:
    api = GitHubAPI(repo, token)
    latest = api.latest()
    candidates = retention_plan(api.releases(), latest, keep_tag, sha)
    for release in candidates:
        print(("DELETE" if apply else "WOULD DELETE") + " release=" +
              str(release["id"]) + " tag=" + str(release.get("tag_name")), flush=True)
        if apply:
            # Another publisher may have changed Latest. Never delete if that happens.
            if api.latest().get("id") != latest["id"]:
                raise RetentionError("Latest release changed mid-cleanup")
            api.request("/releases/" + str(release["id"]), "DELETE")
    print(f"KEEP {keep_tag}; obsolete releases={len(candidates)}; " +
          ("deleted" if apply else "dry-run"), flush=True)
    print("Git tags and historical source commits are intentionally retained.")
    return 0


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--repository", default=os.environ.get("GITHUB_REPOSITORY", ""))
    parser.add_argument("--keep-tag", required=True)
    parser.add_argument("--expected-sha", default=os.environ.get("GITHUB_SHA", ""))
    parser.add_argument("--apply", action="store_true")
    args = parser.parse_args()
    try:
        return run(args.repository, os.environ.get("GH_TOKEN", ""),
                   args.keep_tag, args.expected_sha, args.apply)
    except RetentionError as exc:
        print("RELEASE RETENTION BLOCKED: " + str(exc), file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
