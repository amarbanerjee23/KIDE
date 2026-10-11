#!/usr/bin/env bash
# Run only by an approved GCP deployment operator (not from a user browser).
# Adds one ENGINEER UID to the legacy single project without replacing grants.
set -euo pipefail

uid="${1:-}"
: "${PROJECT_ID:?Set the GCP project ID}"
: "${TRIGGER_NAME:?Set the Cloud Build deployment trigger name}"
REGION="${REGION:-asia-south1}"
TRIGGER_REGION="${TRIGGER_REGION:-${REGION}}"
TRIGGER_KIND="${TRIGGER_KIND:-github}"
KIDE_SERVICE_NAME="${KIDE_SERVICE_NAME:-kide}"
KIDE_FIREBASE_PROJECT_ID="${KIDE_FIREBASE_PROJECT_ID:-${PROJECT_ID}}"
if [[ -z "${uid}" ]]; then
  echo "Usage: PROJECT_ID=... TRIGGER_NAME=... bash deploy/gcp/grant-firebase-engineer.sh FIREBASE_UID" >&2
  exit 2
fi

live_json="$(gcloud run services describe "${KIDE_SERVICE_NAME}" --project "${PROJECT_ID}" --region "${REGION}" --format=json)"
live_value() {
  printf '%s' "${live_json}" | python3 deploy/gcp/firebase-access.py extract-env --key "$1"
}
if [[ "$(live_value KIDE_FIREBASE_PROJECT_ID)" != "${KIDE_FIREBASE_PROJECT_ID}" ]]; then
  echo "Refusing grant: deployed service has a different Firebase project." >&2
  exit 2
fi
if [[ -n "$(live_value KIDE_HOSTED_PROJECTS_ROOT)" ]]; then
  echo "Registry mode requires a project-scoped role binding; legacy allowlist refused." >&2
  exit 2
fi
trigger_value() {
  gcloud builds triggers describe "${TRIGGER_NAME}" --project "${PROJECT_ID}" \
    --region "${TRIGGER_REGION}" --format="value(substitutions.$1)"
}
if [[ "$(trigger_value _KIDE_FIREBASE_PROJECT_ID)" != "${KIDE_FIREBASE_PROJECT_ID}" ]]; then
  echo "Refusing grant: Cloud Build trigger uses a different Firebase project." >&2
  exit 2
fi
approved="$(python3 deploy/gcp/firebase-access.py merge \
  --live "$(live_value KIDE_FIREBASE_ENGINEER_UIDS)" \
  --trigger "$(trigger_value _KIDE_FIREBASE_ENGINEER_UIDS)" --uid "${uid}")"

case "${TRIGGER_KIND}" in
  github|manual) ;;
  *) echo "TRIGGER_KIND must be github or manual" >&2; exit 2 ;;
esac
# Update the trigger first so the next deployment never silently removes access.
gcloud builds triggers update "${TRIGGER_KIND}" "${TRIGGER_NAME}" \
  --project "${PROJECT_ID}" --region "${TRIGGER_REGION}" \
  --update-substitutions "_KIDE_FIREBASE_ENGINEER_UIDS=${approved}"
if [[ "$(trigger_value _KIDE_FIREBASE_ENGINEER_UIDS)" != "${approved}" ]]; then
  echo "Role grant was not retained by the deployment trigger; backend unchanged." >&2
  exit 2
fi

gcloud run services update "${KIDE_SERVICE_NAME}" \
  --project "${PROJECT_ID}" --region "${REGION}" \
  --update-env-vars "KIDE_FIREBASE_ENGINEER_UIDS=${approved}" --quiet

deployed="$(gcloud run services describe "${KIDE_SERVICE_NAME}" --project "${PROJECT_ID}" --region "${REGION}" --format=json)"
actual="$(printf '%s' "${deployed}" | python3 deploy/gcp/firebase-access.py extract-env --key KIDE_FIREBASE_ENGINEER_UIDS)"
if [[ "${actual}" != "${approved}" ]]; then
  echo "Backend role binding verification failed; inspect Cloud Run rollout." >&2
  exit 2
fi
echo "Approved engineer grant persisted in Cloud Build and Cloud Run."
echo "The Web IDE will re-check project authorization automatically after the backend rollout."
