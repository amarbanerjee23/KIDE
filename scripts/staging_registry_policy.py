#!/usr/bin/env python3
"""PR102: validate a *candidate* Cloud Run NFS registry staging deployment.

This validates operator inputs only. It cannot prove actual NFS atomicity,
exclusive writers, firewall reachability, Cloud Run identity or rollback.
"""
from __future__ import annotations

import argparse
import ipaddress
import re
import sys


class StagingRefusal(ValueError):
    pass


def validate(service: str, ip: str, path: str, image: str,
             network: str, subnet: str, firebase: str) -> None:
    if not re.fullmatch(r"[a-z][a-z0-9-]{1,44}-registry-staging", service):
        raise StagingRefusal("A separate *-registry-staging service is required")
    try:
        addr = ipaddress.ip_address(ip)
    except ValueError as exc:
        raise StagingRefusal("NFS server must be an explicit private IPv4 address") from exc
    if addr.version != 4 or not addr.is_private or addr.is_loopback or addr.is_link_local:
        raise StagingRefusal("NFS server must use a private, non-loopback IPv4 address")
    # Paths are literal mount paths, never shell- or gcloud-option segments.
    if not re.fullmatch(r"/[A-Za-z0-9_-]+(?:/[A-Za-z0-9_-]+)*", path):
        raise StagingRefusal("NFS export path must be an absolute simple directory path")
    if not re.fullmatch(r"[a-z][a-z0-9-]{1,61}", network) or not re.fullmatch(
        r"[a-z][a-z0-9-]{1,61}", subnet
    ):
        raise StagingRefusal("A valid private network and subnet must be explicitly supplied")
    if not re.fullmatch(r"[a-z][a-z0-9-]{4,60}", firebase):
        raise StagingRefusal("An explicit dedicated staging Firebase project is required")
    if not re.fullmatch(
        r"[a-z0-9.-]+/[a-zA-Z0-9_./-]+@sha256:[0-9a-f]{64}", image
    ):
        raise StagingRefusal("Candidate backend must reference a pinned image digest")



def verify_existing(service: dict, ip: str, path: str) -> None:
    spec = service.get("spec", {}).get("template", {}).get("spec", {})
    containers = spec.get("containers") or service.get("template", {}).get("containers") or []
    if not containers:
        raise StagingRefusal("Existing staging backend lacks runtime configuration")
    env = {item.get("name"): item.get("value", "") for item in containers[0].get("env", [])
           if isinstance(item, dict) and item.get("name")}
    if env.get("KIDE_STORAGE_MODE") != "registry-posix" or (
        env.get("KIDE_HOSTED_PROJECTS_ROOT") != "/data/hosted-projects"
    ):
        raise StagingRefusal("Existing staging service has a different storage mode")
    volumes = spec.get("volumes") or service.get("template", {}).get("volumes") or []
    if len(volumes) != 1 or volumes[0].get("nfs", {}).get("server") != ip or (
        volumes[0].get("nfs", {}).get("path") != path
    ):
        raise StagingRefusal("Existing staging NFS endpoint differs; no implicit migration")
    mounts = containers[0].get("volumeMounts") or []
    if not any(item.get("mountPath") == "/data" and
               item.get("name") == volumes[0].get("name") for item in mounts):
        raise StagingRefusal("Existing staging backend does not mount the approved NFS volume")


def verify_main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser(description="Validate existing staging registry mount")
    parser.add_argument("--nfs-ip", required=True)
    parser.add_argument("--nfs-export", required=True)
    args = parser.parse_args(argv)
    try:
        import json
        verify_existing(json.load(sys.stdin), args.nfs_ip, args.nfs_export)
        print("Existing staging mode and NFS identity verified")
        return 0
    except (StagingRefusal, ValueError) as exc:
        print("Registry staging update refused: " + str(exc), file=sys.stderr)
        return 2


def main(argv: list[str] | None = None) -> int:
    p = argparse.ArgumentParser(description=__doc__)
    for name in ("service", "nfs-ip", "nfs-export", "image", "network",
                 "subnet", "firebase-project"):
        p.add_argument("--" + name, required=True)
    a = p.parse_args(argv)
    try:
        validate(a.service, a.nfs_ip, a.nfs_export, a.image,
                 a.network, a.subnet, a.firebase_project)
        print("Isolated registry candidate configuration validated (not deployed)")
        return 0
    except StagingRefusal as exc:
        print("Registry deployment refused: " + str(exc), file=sys.stderr)
        return 2


if __name__ == "__main__":
    if len(sys.argv) > 1 and sys.argv[1] == "verify-existing":
        raise SystemExit(verify_main(sys.argv[2:]))
    raise SystemExit(main())
