#!/usr/bin/env python3
"""Read-only hosted Web/API health and build-coherence probe for periodic alerts."""
from __future__ import annotations

import argparse
import json
import ssl
import sys
import time
import urllib.error
import urllib.parse
import urllib.request


class ProbeFailure(ValueError):
    pass


def origin(url: str, local_http: bool = False) -> str:
    parsed = urllib.parse.urlsplit(url)
    allowed = parsed.scheme == "https" or (
        local_http and parsed.scheme == "http"
        and parsed.hostname in ("127.0.0.1", "localhost", "::1")
    )
    if (not allowed or not parsed.hostname or parsed.username or parsed.password
            or parsed.path not in ("", "/") or parsed.query or parsed.fragment):
        raise ProbeFailure("probe target must be a credential-free HTTPS origin")
    return url.rstrip("/")


def get_json(url: str, timeout: float) -> tuple[dict, int]:
    request = urllib.request.Request(
        url, headers={"Accept": "application/json", "User-Agent": "KIDE-PR90-HealthProbe/1"},
        method="GET"
    )
    started = time.monotonic()
    with urllib.request.urlopen(request, timeout=timeout,
                                context=ssl.create_default_context()) as response:
        status = response.status
        data = response.read(65537)
    elapsed = int((time.monotonic() - started) * 1000)
    if status != 200 or len(data) > 65536:
        raise ProbeFailure("health endpoint failed or exceeded bounded response size")
    try:
        body = json.loads(data)
    except (UnicodeDecodeError, ValueError) as exc:
        raise ProbeFailure("health endpoint did not return JSON") from exc
    if not isinstance(body, dict):
        raise ProbeFailure("health endpoint response is not a JSON object")
    return body, elapsed



def check_web_health(url: str, timeout: float) -> int:
    """Static Nginx /health returns plain text 'ok', not JSON."""
    request = urllib.request.Request(url, method="GET")
    started = time.monotonic()
    with urllib.request.urlopen(request, timeout=timeout,
                                context=ssl.create_default_context()) as response:
        code = response.status
        body = response.read(17)
    if code != 200 or body.strip() != b"ok":
        raise ProbeFailure("static Web health endpoint failed")
    return int((time.monotonic() - started) * 1000)


def check(api_url: str, web_url: str, timeout: float = 10.0,
          maximum_ms: int = 5000) -> dict:
    api, web = origin(api_url), origin(web_url)
    if api == web:
        raise ProbeFailure("Web and API origins must be separate services")
    health, api_ms = get_json(api + "/api/v1/health", timeout)
    version, version_ms = get_json(api + "/api/v1/version", timeout)
    web_ms = check_web_health(web + "/health", timeout)
    frontend, frontend_ms = get_json(web + "/kide-version.json", timeout)
    checks = {
        "backendHealth": api_ms, "backendVersion": version_ms,
        "webHealth": web_ms, "webVersion": frontend_ms
    }
    if health.get("status") != "UP":
        raise ProbeFailure("backend reported unhealthy")
    # The Web /health body is nginx-owned; a successful 200 response is enough.
    if frontend.get("component") != "web":
        raise ProbeFailure("Web deployment identity is missing")
    if not isinstance(version.get("buildId"), str) or not version["buildId"]:
        raise ProbeFailure("backend build identity is missing")
    if frontend.get("buildId") != version["buildId"]:
        raise ProbeFailure("Web and backend build identities disagree")
    if any(latency > maximum_ms for latency in checks.values()):
        raise ProbeFailure("hosted endpoint exceeded latency threshold")
    return {
        "status": "PASS", "apiStatus": health["status"],
        "buildId": version["buildId"], "latencyMs": checks,
        "maxLatencyMs": max(checks.values()),
        "limitMs": maximum_ms,
    }


def main(argv: list[str] | None = None) -> int:
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--api", required=True)
    p.add_argument("--web", required=True)
    p.add_argument("--timeout", type=float, default=10.0)
    p.add_argument("--max-latency-ms", type=int, default=5000)
    args = p.parse_args(argv)
    try:
        if not 1 <= args.timeout <= 30 or not 100 <= args.max_latency_ms <= 30000:
            raise ProbeFailure("probe thresholds are outside safe range")
        result = check(args.api, args.web, args.timeout, args.max_latency_ms)
        print(json.dumps(result, sort_keys=True))
        return 0
    except (ProbeFailure, urllib.error.URLError, TimeoutError, OSError) as exc:
        # Deliberately do not echo URLs, credentials or fetched service bodies.
        print(json.dumps({"status": "FAIL", "reason": str(exc)}, sort_keys=True),
              file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
