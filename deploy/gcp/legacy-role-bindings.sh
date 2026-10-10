#!/usr/bin/env bash
# Operator-controlled Firebase project grants, never browser-supplied UID.
set -euo pipefail
project_id="${1:?usage: legacy-role-bindings.sh PROJECT_ID}"
if [[ ! "$project_id" =~ ^kide:project:[0-9a-f]{8}-([0-9a-f]{4}-){3}[0-9a-f]{12}$ ]]; then
  echo "Invalid canonical project ID" >&2
  exit 2
fi

if [[ -n "${KIDE_ROLE_BINDINGS:-}" ]]; then
  role_bindings="${KIDE_ROLE_BINDINGS}"
else
  primary_principal="${KIDE_PRIMARY_PRINCIPAL:-}"
  if [[ -z "${primary_principal}" ]]; then
    if [[ -z "${KIDE_FIREBASE_PROJECT_ID:-}" || -z "${KIDE_FIREBASE_ADMIN_UID:-}" ]]; then
      echo "KIDE_FIREBASE_PROJECT_ID and KIDE_FIREBASE_ADMIN_UID (or KIDE_ROLE_BINDINGS) are required" >&2
      exit 2
    fi
    primary_principal="firebase:${KIDE_FIREBASE_PROJECT_ID}#${KIDE_FIREBASE_ADMIN_UID}"
  fi
  role_bindings="${primary_principal}|ADMINISTRATOR|${project_id}"
fi

# Operator-approved Firebase engineer UIDs. These are explicit allowlist entries,
# never wildcard grants, and only apply to the legacy single-project runtime.
# A newly registered Firebase account is NOT automatically authorized.
engineer_uids="${KIDE_FIREBASE_ENGINEER_UIDS:-}"
if [[ -n "${engineer_uids}" ]]; then
  if [[ -n "${KIDE_HOSTED_PROJECTS_ROOT:-}" ]]; then
    echo "KIDE_FIREBASE_ENGINEER_UIDS cannot grant registry-backed project access" >&2
    exit 2
  fi
  if [[ -n "${KIDE_ROLE_BINDINGS:-}" ]]; then
    echo "Use KIDE_ROLE_BINDINGS alone when supplying explicit role grants" >&2
    exit 2
  fi
  if [[ -z "${KIDE_FIREBASE_PROJECT_ID:-}" ]]; then
    echo "Firebase project ID is required to resolve engineer identities" >&2
    exit 2
  fi
  IFS=':' read -r -a approved_uids <<< "${engineer_uids}"
  # Reject invalid/empty segments, duplicate entries and overlarge grant sets.
  if [[ "${#approved_uids[@]}" -gt 32 || "${engineer_uids}" == :* || "${engineer_uids}" == *: || "${engineer_uids}" == *::*
     ]]; then
    echo "Invalid KIDE_FIREBASE_ENGINEER_UIDS allowlist" >&2
    exit 2
  fi
  declare -A seen_engineers=()
  for uid in "${approved_uids[@]}"; do
    if [[ ! "${uid}" =~ ^[A-Za-z0-9_-]{1,128}$ || -n "${seen_engineers[${uid}]:-}" ]]; then
      echo "Invalid or duplicate Firebase engineer UID" >&2
      exit 2
    fi
    seen_engineers["${uid}"]=1
    principal="firebase:${KIDE_FIREBASE_PROJECT_ID}#${uid}"
    if [[ "${principal}" != "${primary_principal}" ]]; then
      role_bindings+=";${principal}|ENGINEER|${project_id}"
    fi
  done
fi


printf '%s\n' "$role_bindings"
