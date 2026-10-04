#!/usr/bin/env bash
set -euo pipefail

required=(
  PROJECT_ID
  TRIGGER_NAME
  TRIGGER_REGION
  KIDE_FIREBASE_PROJECT_ID
  KIDE_FIREBASE_API_KEY
  KIDE_FIREBASE_ADMIN_UID
)
for name in "${required[@]}"; do
  if [[ -z "${!name:-}" ]]; then
    echo "${name} is required" >&2
    exit 2
  fi
done

read_trigger_substitution() {
  local key="$1"
  gcloud builds triggers describe "${TRIGGER_NAME}" \
    --project "${PROJECT_ID}" \
    --region "${TRIGGER_REGION}" \
    --format="value(substitutions.${key})"
}

verify_value() {
  local key="$1"
  local expected="$2"
  local actual
  actual="$(read_trigger_substitution "${key}")"

  if [[ "${actual}" != "${expected}" ]]; then
    echo "Cloud Build trigger Firebase configuration verification failed." >&2
    echo "Trigger: ${TRIGGER_NAME} (region: ${TRIGGER_REGION})" >&2
    echo "Substitution ${key} was not persisted as expected." >&2
    echo "The deployment build will not be started with incomplete Firebase substitutions." >&2
    echo "Run ./deploy-kide-gcp.sh bootstrap again after confirming you selected the intended trigger." >&2
    exit 2
  fi
}

verify_value _KIDE_FIREBASE_PROJECT_ID "${KIDE_FIREBASE_PROJECT_ID}"
verify_value _KIDE_FIREBASE_API_KEY "${KIDE_FIREBASE_API_KEY}"
verify_value _KIDE_FIREBASE_ADMIN_UID "${KIDE_FIREBASE_ADMIN_UID}"

echo "Verified Firebase substitutions on Cloud Build trigger '${TRIGGER_NAME}'."
