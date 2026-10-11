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
    raise SystemExit(main())
