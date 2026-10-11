#!/usr/bin/env python3
"""Validate operator-controlled Firebase engineer grants during KIDE deployments.

No grants are inferred from browser authentication: only existing Cloud Run
configuration, approved Cloud Build substitutions, and an operator-supplied UID
are used. Do not log ID tokens, passwords, or environment JSON.
"""
import argparse
import json
import re
import subprocess
import sys

UID = re.compile(r"^[A-Za-z0-9_-]{1,128}$")


def parse_uids(value):
    if not value:
        return []
    parts = value.split(":")
    if len(parts) > 32 or any(not UID.fullmatch(v) for v in parts) or len(parts) != len(set(parts)):
        raise ValueError("Invalid, duplicate or excessive engineer UID grants")
    return parts


def environment(service):
    # Cloud Run v1 API representation, with v2 representation for compatibility.
    template = service.get("spec", {}).get("template", {}).get("spec", {})
    containers = template.get("containers") or service.get("template", {}).get("containers") or []
    if not containers:
        raise ValueError("Cloud Run service has no runtime container configuration")
    result = {}
    for entry in containers[0].get("env", []):
        name = entry.get("name")
        if name:
            result[name] = entry.get("value", "")
    return result


def merge(live, trigger, uid):
    if not UID.fullmatch(uid):
        raise ValueError("Invalid Firebase UID")
    return ":".join(dict.fromkeys([*parse_uids(live), *parse_uids(trigger), uid])) if (
        len(set([*parse_uids(live), *parse_uids(trigger), uid])) <= 32
    ) else (_ for _ in ()).throw(ValueError("Too many engineer grants"))


def check_preserved(live, desired):
    previous = set(parse_uids(live))
    requested = set(parse_uids(desired))
    if not previous.issubset(requested):
        raise ValueError("Refusing deployment: engineer role grant(s) would disappear. "
                         "Preserve approved UIDs in the Cloud Build trigger; "
                         "perform an intentional audited revocation separately.")


def describe_service(project, region, service):
    command = [
        "gcloud", "run", "services", "describe", service, "--project", project,
        "--region", region, "--format=json",
    ]
    result = subprocess.run(command, capture_output=True, text=True, check=False)
    if result.returncode:
        # A brand-new service is different from an IAM or transport failure.
        if "NOT_FOUND" in result.stderr or "was not found" in result.stderr:
            return None
        raise ValueError("Cannot inspect existing Cloud Run role grants; deployment blocked")
    return json.loads(result.stdout)


def main():
    parser = argparse.ArgumentParser()
    sub = parser.add_subparsers(dest="command", required=True)
    extract = sub.add_parser("extract-env")
    extract.add_argument("--key", required=True)
    validate = sub.add_parser("validate")
    validate.add_argument("--engineers", required=True)
    combined = sub.add_parser("merge")
    combined.add_argument("--live", required=True)
    combined.add_argument("--trigger", required=True)
    combined.add_argument("--uid", required=True)
    guard = sub.add_parser("protect-deploy")
    guard.add_argument("--project", required=True)
    guard.add_argument("--region", required=True)
    guard.add_argument("--service", required=True)
    guard.add_argument("--firebase-project", required=True)
    guard.add_argument("--engineers", required=True)
    args = parser.parse_args()
    try:
        if args.command == "extract-env":
            if args.key not in ("KIDE_FIREBASE_PROJECT_ID", "KIDE_FIREBASE_ENGINEER_UIDS",
                                "KIDE_HOSTED_PROJECTS_ROOT"):
                raise ValueError("Unsupported environment key")
            print(environment(json.load(sys.stdin)).get(args.key, ""))
        elif args.command == "validate":
            parse_uids(args.engineers)
            print("Firebase engineer allowlist validated.")
        elif args.command == "merge":
            print(merge(args.live, args.trigger, args.uid))
        else:
            parse_uids(args.engineers)
            service = describe_service(args.project, args.region, args.service)
            if service is None:
                print("First deployment: no existing engineer grants to preserve.")
                return
            deployed = environment(service)
            if deployed.get("KIDE_FIREBASE_PROJECT_ID") != args.firebase_project:
                raise ValueError("Deployed Firebase project differs from incoming configuration")
            if deployed.get("KIDE_HOSTED_PROJECTS_ROOT"):
                raise ValueError("Legacy engineer allowlist is not valid for registry-backed projects")
            check_preserved(deployed.get("KIDE_FIREBASE_ENGINEER_UIDS", ""), args.engineers)
            print("Cloud Run project identity and approved engineer grants preserved.")
    except (ValueError, json.JSONDecodeError) as exc:
        print(f"Firebase role grant validation failed: {exc}", file=sys.stderr)
        sys.exit(2)


if __name__ == "__main__":
    main()
