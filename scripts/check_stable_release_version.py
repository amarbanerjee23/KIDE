#!/usr/bin/env python3
"""Reject stable release versions that do not advance published stable semver."""
from __future__ import annotations

import argparse
import os
import sys

from retain_latest_stable_release import GitHubAPI, RetentionError, stable_version


def check(candidate: str, releases: list[dict]) -> list[str]:
    target = stable_version(candidate)
    if target is None:
        return ["Stable release requires vMAJOR.MINOR.PATCH (no prerelease suffix)"]
    return [
        f"Published stable release {release.get('tag_name')} is not older than {candidate}"
        for release in releases
        if not release.get("draft") and not release.get("prerelease")
        and (version := stable_version(release.get("tag_name"))) is not None
        and version >= target
    ]


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--tag", required=True)
    parser.add_argument("--repository", default=os.environ.get("GITHUB_REPOSITORY", ""))
    args = parser.parse_args()
    try:
        api = GitHubAPI(args.repository, os.environ.get("GH_TOKEN", ""))
        errors = check(args.tag, api.releases())
        if errors:
            for error in errors:
                print("STABLE RELEASE BLOCKED:", error, file=sys.stderr)
            return 2
        print("STABLE VERSION QUALIFIED:", args.tag)
        return 0
    except RetentionError as exc:
        print("STABLE RELEASE BLOCKED:", exc, file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
