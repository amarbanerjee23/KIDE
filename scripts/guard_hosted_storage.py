#!/usr/bin/env python3
"""Protect existing Cloud Run storage identity during normal legacy releases.

A standard release must never replace a registry/NFS service with the default
GCS FUSE deployment, or silently remove a persisted project registry.
Storage mode transitions require a separate operator-reviewed staging workflow.
"""
from __future__ import annotations

import argparse
import json
import subprocess
import sys


class StorageModeError(ValueError):
    pass


def environment(service: dict) -> dict[str, str]:
    spec = service.get("spec", {}).get("template", {}).get("spec", {})
    containers = spec.get("containers") or service.get("template", {}).get("containers") or []
    if not containers:
        raise StorageModeError("Cloud Run service has no runtime container configuration")
    return {item["name"]: item.get("value", "")
            for item in containers[0].get("env", []) if isinstance(item, dict) and item.get("name")}


def protect_legacy_service(service: dict) -> None:
    env = environment(service)
    mode = env.get("KIDE_STORAGE_MODE", "")
    if mode not in ("", "legacy-gcs"):
        raise StorageModeError("Refusing legacy rollout over a non-legacy storage service")
    if env.get("KIDE_HOSTED_PROJECTS_ROOT"):
        raise StorageModeError("Refusing legacy rollout over a registered multi-project service")
    spec = service.get("spec", {}).get("template", {}).get("spec", {})
    for volume in spec.get("volumes", []) or service.get("template", {}).get("volumes", []) or []:
        if "nfs" in volume or volume.get("csi", {}).get("driver", "") not in (
            "", "gcsfuse.run.googleapis.com"
        ):
            raise StorageModeError("Refusing to replace an unknown or POSIX-backed storage volume")


def describe(project: str, region: str, service_name: str) -> dict | None:
    result = subprocess.run([
        "gcloud", "run", "services", "describe", service_name,
        "--project", project, "--region", region, "--format=json"
    ], check=False, capture_output=True, text=True)
    if result.returncode:
        # Only a genuinely missing service permits a new legacy deployment.
        if "NOT_FOUND" in result.stderr or "was not found" in result.stderr:
            return None
        raise StorageModeError("Cloud Run storage identity inspection failed; release blocked")
    try:
        return json.loads(result.stdout)
    except (ValueError, UnicodeError) as exc:
        raise StorageModeError("Cloud Run storage response is malformed") from exc


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--project", required=True)
    parser.add_argument("--region", required=True)
    parser.add_argument("--service", required=True)
    args = parser.parse_args(argv)
    try:
        service = describe(args.project, args.region, args.service)
        if service is not None:
            protect_legacy_service(service)
        print("Legacy Cloud Run rollout storage identity preserved")
        return 0
    except StorageModeError as exc:
        print("KIDE storage guard refused rollout: " + str(exc), file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
