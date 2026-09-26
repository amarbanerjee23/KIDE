#!/usr/bin/env python3
from __future__ import annotations

import argparse
import glob
import http.client
import json
import mimetypes
import os
from pathlib import Path
import sys
from typing import Any
from urllib.error import HTTPError, URLError
from urllib.parse import quote, urlencode
from urllib.request import Request, urlopen

API_VERSION = "2022-11-28"
USER_AGENT = "kide-cloud-build-release-publisher"


class GitHubApiError(RuntimeError):
    pass


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Create or update a GitHub pre-release and stream build assets."
    )
    parser.add_argument("--repository", required=True, help="owner/repository")
    parser.add_argument("--tag", required=True)
    parser.add_argument("--title", required=True)
    parser.add_argument("--target", required=True)
    parser.add_argument("--notes", required=True, type=Path)
    parser.add_argument("--token-file", required=True, type=Path)
    parser.add_argument(
        "--asset-glob",
        action="append",
        default=[],
        help="Asset glob. May be supplied more than once.",
    )
    return parser.parse_args()


def api_headers(token: str) -> dict[str, str]:
    return {
        "Accept": "application/vnd.github+json",
        "Authorization": f"Bearer {token}",
        "User-Agent": USER_AGENT,
        "X-GitHub-Api-Version": API_VERSION,
    }


def api_request(
    token: str,
    method: str,
    url: str,
    payload: dict[str, Any] | None = None,
    *,
    allow_404: bool = False,
) -> Any:
    body = None
    headers = api_headers(token)
    if payload is not None:
        body = json.dumps(payload).encode("utf-8")
        headers["Content-Type"] = "application/json"

    request = Request(url=url, data=body, method=method, headers=headers)
    try:
        with urlopen(request, timeout=120) as response:
            raw = response.read()
            if not raw:
                return None
            return json.loads(raw.decode("utf-8"))
    except HTTPError as exc:
        raw = exc.read().decode("utf-8", errors="replace")
        if allow_404 and exc.code == 404:
            return None
        raise GitHubApiError(
            f"GitHub API {method} {url} failed with HTTP {exc.code}: {raw}"
        ) from exc
    except URLError as exc:
        raise GitHubApiError(f"GitHub API {method} {url} failed: {exc}") from exc


def collect_assets(patterns: list[str]) -> list[Path]:
    assets: list[Path] = []
    seen: set[Path] = set()
    for pattern in patterns:
        matches = sorted(Path(path) for path in glob.glob(pattern))
        if not matches:
            raise FileNotFoundError(f"Asset glob matched no files: {pattern}")
        for path in matches:
            resolved = path.resolve()
            if resolved in seen:
                continue
            if not path.is_file():
                raise FileNotFoundError(f"Asset is not a file: {path}")
            seen.add(resolved)
            assets.append(path)
    if not assets:
        raise FileNotFoundError("No release assets were provided.")
    return assets


def load_token_and_remove(path: Path) -> str:
    token = path.read_text(encoding="utf-8").strip()
    try:
        path.unlink(missing_ok=True)
    finally:
        if not token:
            raise RuntimeError("GitHub token file is empty.")
    return token


def ensure_release(
    token: str,
    repository: str,
    tag: str,
    title: str,
    target: str,
    notes: str,
) -> dict[str, Any]:
    base = f"https://api.github.com/repos/{repository}"
    encoded_tag = quote(tag, safe="")
    release = api_request(
        token,
        "GET",
        f"{base}/releases/tags/{encoded_tag}",
        allow_404=True,
    )

    payload = {
        "tag_name": tag,
        "target_commitish": target,
        "name": title,
        "body": notes,
        "draft": False,
        "prerelease": True,
        "make_latest": "false",
    }

    if release is None:
        release = api_request(token, "POST", f"{base}/releases", payload)
        print(f"Created GitHub pre-release {tag}.")
    else:
        release_id = int(release["id"])
        release = api_request(
            token,
            "PATCH",
            f"{base}/releases/{release_id}",
            payload,
        )
        print(f"Updated GitHub pre-release {tag}.")

    if not isinstance(release, dict) or "id" not in release:
        raise GitHubApiError("GitHub release response did not contain an id.")
    return release


def list_release_assets(
    token: str,
    repository: str,
    release_id: int,
) -> list[dict[str, Any]]:
    url = (
        f"https://api.github.com/repos/{repository}/releases/"
        f"{release_id}/assets?per_page=100"
    )
    assets = api_request(token, "GET", url)
    if not isinstance(assets, list):
        raise GitHubApiError("GitHub release assets response was not a list.")
    return assets


def delete_asset(
    token: str,
    repository: str,
    asset_id: int,
    asset_name: str,
) -> None:
    url = f"https://api.github.com/repos/{repository}/releases/assets/{asset_id}"
    api_request(token, "DELETE", url)
    print(f"Removed existing release asset {asset_name}.")


def upload_asset(
    token: str,
    repository: str,
    release_id: int,
    path: Path,
) -> None:
    content_type = mimetypes.guess_type(path.name)[0] or "application/octet-stream"
    query = urlencode({"name": path.name})
    request_path = (
        f"/repos/{repository}/releases/{release_id}/assets?{query}"
    )

    headers = api_headers(token)
    headers.update(
        {
            "Content-Type": content_type,
            "Content-Length": str(path.stat().st_size),
        }
    )

    connection = http.client.HTTPSConnection(
        "uploads.github.com",
        timeout=900,
    )
    try:
        with path.open("rb") as handle:
            connection.request(
                "POST",
                request_path,
                body=handle,
                headers=headers,
            )
            response = connection.getresponse()
            raw = response.read()
        if response.status not in (200, 201):
            raise GitHubApiError(
                f"Upload of {path.name} failed with HTTP "
                f"{response.status}: {raw.decode('utf-8', errors='replace')}"
            )
        result = json.loads(raw.decode("utf-8"))
        print(
            f"Uploaded {path.name} "
            f"({path.stat().st_size} bytes) as asset {result.get('id')}."
        )
    finally:
        connection.close()


def main() -> int:
    args = parse_args()

    if "/" not in args.repository:
        raise ValueError("--repository must use owner/repository format.")
    if not args.notes.is_file():
        raise FileNotFoundError(f"Release notes file not found: {args.notes}")
    if not args.token_file.is_file():
        raise FileNotFoundError(f"GitHub token file not found: {args.token_file}")

    assets = collect_assets(args.asset_glob)
    notes = args.notes.read_text(encoding="utf-8")
    token = load_token_and_remove(args.token_file)

    release = ensure_release(
        token=token,
        repository=args.repository,
        tag=args.tag,
        title=args.title,
        target=args.target,
        notes=notes,
    )
    release_id = int(release["id"])

    existing = {
        str(asset["name"]): asset
        for asset in list_release_assets(token, args.repository, release_id)
    }
    for path in assets:
        old = existing.get(path.name)
        if old is not None:
            delete_asset(
                token,
                args.repository,
                int(old["id"]),
                path.name,
            )
        upload_asset(
            token,
            args.repository,
            release_id,
            path,
        )

    html_url = release.get("html_url")
    print(
        json.dumps(
            {
                "releaseId": release_id,
                "tag": args.tag,
                "releaseUrl": html_url,
                "assets": [path.name for path in assets],
            },
            indent=2,
        )
    )
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except Exception as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        raise
