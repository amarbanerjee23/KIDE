#!/usr/bin/env bash
# Opt-in SINGLE-TENANT shared-project engineering for Firebase authenticated users.
# This is a deliberate broadening of access to EVERY registered KIDE project.
set -euo pipefail

: "${PROJECT_ID:?Set PROJECT_ID to the GCP project}"
: "${TRIGGER_NAME:?Set TRIGGER_NAME to the reviewed deployment trigger}"
: "${KIDE_SHARED_ACCESS_ACK:?Explicit security approval is required}"
if [[ "${KIDE_SHARED_ACCESS_ACK}" != I_UNDERSTAND_ALL_FIREBASE_USERS_CAN_EDIT_ALL_PROJECTS ]]; then
  echo "Refusing access expansion without explicit all-project data-sharing approval" >&2
  exit 2
fi

REGION="${REGION:-asia-south1}"
TRIGGER_REGION="${TRIGGER_REGION:-${REGION}}"
TRIGGER_KIND="${TRIGGER_KIND:-github}"
KIDE_SERVICE_NAME="${KIDE_SERVICE_NAME:-kide}"
KIDE_FIREBASE_PROJECT_ID="${KIDE_FIREBASE_PROJECT_ID:-${PROJECT_ID}}"
if [[ "${TRIGGER_KIND}" != github && "${TRIGGER_KIND}" != manual ]]; then
  echo "TRIGGER_KIND must be github or manual" >&2
  exit 2
fi

# Verify the Firebase trust boundary before any side effect.
live="$(gcloud run services describe "${KIDE_SERVICE_NAME}" \
  --project "${PROJECT_ID}" --region "${REGION}" --format=json)"
live_firebase="$(printf '%s' "${live}" | \
  python3 deploy/gcp/firebase-access.py extract-env --key KIDE_FIREBASE_PROJECT_ID)"
if [[ "${live_firebase}" != "${KIDE_FIREBASE_PROJECT_ID}" ]]; then
  echo "Refusing shared access: deployed Firebase project differs from approval" >&2
  exit 2
fi
trigger_firebase="$(gcloud builds triggers describe "${TRIGGER_NAME}" \
  --project "${PROJECT_ID}" --region "${TRIGGER_REGION}" \
  --format='value(substitutions._KIDE_FIREBASE_PROJECT_ID)')"
if [[ "${trigger_firebase}" != "${KIDE_FIREBASE_PROJECT_ID}" ]]; then
  echo "Refusing shared access: deployment trigger uses another Firebase project" >&2
  exit 2
fi

# Update the trigger first, so the next automatic release preserves access.
gcloud builds triggers update "${TRIGGER_KIND}" "${TRIGGER_NAME}" \
  --project "${PROJECT_ID}" --region "${TRIGGER_REGION}" \
  --update-substitutions "_KIDE_SHARED_FIREBASE_ENGINEERS=true"
actual_trigger="$(gcloud builds triggers describe "${TRIGGER_NAME}" \
  --project "${PROJECT_ID}" --region "${TRIGGER_REGION}" \
  --format='value(substitutions._KIDE_SHARED_FIREBASE_ENGINEERS)')"
if [[ "${actual_trigger}" != true ]]; then
  echo "Deployment trigger did not retain the shared Firebase engineering policy" >&2
  exit 2
fi

gcloud run services update "${KIDE_SERVICE_NAME}" \
  --project "${PROJECT_ID}" --region "${REGION}" \
  --update-env-vars KIDE_SHARED_FIREBASE_ENGINEERS=true --quiet
deployed="$(gcloud run services describe "${KIDE_SERVICE_NAME}" \
  --project "${PROJECT_ID}" --region "${REGION}" --format=json)"
actual="$(printf '%s' "${deployed}" | \
  python3 deploy/gcp/firebase-access.py extract-env --key KIDE_SHARED_FIREBASE_ENGINEERS)"
if [[ "${actual}" != true ]]; then
  echo "Cloud Run did not retain the shared engineering policy" >&2
  exit 2
fi
echo "Shared Firebase ENGINEER access approved for ALL KIDE projects."
echo "IMPORTANT: This is intended only for a trusted single-tenant Firebase user directory."
echo "Verify the deployed backend image supports PR105 before expecting authorization changes."
echo "Re-open the Web Engineering Workspace and use Reconnect API if necessary."
