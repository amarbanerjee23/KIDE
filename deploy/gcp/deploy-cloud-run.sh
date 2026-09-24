#!/usr/bin/env bash
set -euo pipefail

required=(
  PROJECT_ID
  KIDE_OIDC_INTROSPECTION_URL
  KIDE_OIDC_CLIENT_ID
  KIDE_OIDC_ISSUER
  KIDE_OIDC_AUDIENCE
  KIDE_PRIMARY_PRINCIPAL
  KIDE_OIDC_SECRET_NAME
)
for name in "${required[@]}"; do
  if [[ -z "${!name:-}" ]]; then
    echo "${name} is required" >&2
    exit 2
  fi
done

REGION="${REGION:-asia-south1}"
KIDE_SERVICE_NAME="${KIDE_SERVICE_NAME:-kide}"
KIDE_WEB_SERVICE_NAME="${KIDE_WEB_SERVICE_NAME:-kide-web}"
AR_REPOSITORY="${AR_REPOSITORY:-kide}"
DATA_BUCKET="${DATA_BUCKET:-${PROJECT_ID}-kide-data}"
IMAGE_TAG="${IMAGE_TAG:-$(git rev-parse --short=12 HEAD)}"
RUNTIME_SA_NAME="${RUNTIME_SA_NAME:-kide-runtime}"
WEB_RUNTIME_SA_NAME="${WEB_RUNTIME_SA_NAME:-kide-web-runtime}"
RUNTIME_SA="${RUNTIME_SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com"
WEB_RUNTIME_SA="${WEB_RUNTIME_SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com"
BACKEND_IMAGE="${REGION}-docker.pkg.dev/${PROJECT_ID}/${AR_REPOSITORY}/kide:${IMAGE_TAG}"
WEB_IMAGE="${REGION}-docker.pkg.dev/${PROJECT_ID}/${AR_REPOSITORY}/kide-web:${IMAGE_TAG}"

gcloud config set project "${PROJECT_ID}" >/dev/null
gcloud services enable run.googleapis.com artifactregistry.googleapis.com cloudbuild.googleapis.com secretmanager.googleapis.com

BUILD_SERVICE_ACCOUNT="$(gcloud builds get-default-service-account --project "${PROJECT_ID}" 2>/dev/null || true)"
if [[ -z "${BUILD_SERVICE_ACCOUNT}" ]]; then
  echo "Could not determine the Cloud Build service account." >&2
  exit 2
fi
BUILD_SERVICE_ACCOUNT_EMAIL="${BUILD_SERVICE_ACCOUNT##*/}"

export PROJECT_ID REGION AR_REPOSITORY BUILD_SERVICE_ACCOUNT
bash deploy/gcp/bootstrap-cloud-build.sh

if ! gcloud storage buckets describe "gs://${DATA_BUCKET}" --project "${PROJECT_ID}" >/dev/null 2>&1; then
  gcloud storage buckets create "gs://${DATA_BUCKET}" --project "${PROJECT_ID}" --location "${REGION}" --uniform-bucket-level-access
fi

ensure_service_account() {
  local email="$1"
  local name="$2"
  local display_name="$3"
  if ! gcloud iam service-accounts describe "${email}" --project "${PROJECT_ID}" >/dev/null 2>&1; then
    gcloud iam service-accounts create "${name}" --project "${PROJECT_ID}" --display-name "${display_name}"
  fi
}

ensure_service_account "${RUNTIME_SA}" "${RUNTIME_SA_NAME}" "KIDE backend runtime"
ensure_service_account "${WEB_RUNTIME_SA}" "${WEB_RUNTIME_SA_NAME}" "KIDE web runtime"

gcloud storage buckets add-iam-policy-binding "gs://${DATA_BUCKET}" --project "${PROJECT_ID}" --member="serviceAccount:${RUNTIME_SA}" --role="roles/storage.objectUser" >/dev/null
gcloud secrets describe "${KIDE_OIDC_SECRET_NAME}" --project "${PROJECT_ID}" >/dev/null
gcloud secrets add-iam-policy-binding "${KIDE_OIDC_SECRET_NAME}" --project "${PROJECT_ID}" --member="serviceAccount:${RUNTIME_SA}" --role="roles/secretmanager.secretAccessor" >/dev/null

gcloud projects add-iam-policy-binding "${PROJECT_ID}" --member="serviceAccount:${BUILD_SERVICE_ACCOUNT_EMAIL}" --role="roles/run.admin" >/dev/null
for service_account in "${RUNTIME_SA}" "${WEB_RUNTIME_SA}"; do
  gcloud iam service-accounts add-iam-policy-binding "${service_account}" --project "${PROJECT_ID}" --member="serviceAccount:${BUILD_SERVICE_ACCOUNT_EMAIL}" --role="roles/iam.serviceAccountUser" >/dev/null
done

gcloud builds submit .   --config deploy/gcp/cloudbuild-deploy.yaml   --substitutions="_REGION=${REGION},_AR_REPOSITORY=${AR_REPOSITORY},_KIDE_SERVICE_NAME=${KIDE_SERVICE_NAME},_KIDE_WEB_SERVICE_NAME=${KIDE_WEB_SERVICE_NAME},_KIDE_IMAGE=${BACKEND_IMAGE},_KIDE_WEB_IMAGE=${WEB_IMAGE},_KIDE_QUALIFIER=${IMAGE_TAG},_KIDE_DATA_BUCKET=${DATA_BUCKET},_KIDE_RUNTIME_SA=${RUNTIME_SA},_KIDE_WEB_RUNTIME_SA=${WEB_RUNTIME_SA},_KIDE_OIDC_INTROSPECTION_URL=${KIDE_OIDC_INTROSPECTION_URL},_KIDE_OIDC_CLIENT_ID=${KIDE_OIDC_CLIENT_ID},_KIDE_OIDC_ISSUER=${KIDE_OIDC_ISSUER},_KIDE_OIDC_AUDIENCE=${KIDE_OIDC_AUDIENCE},_KIDE_PRIMARY_PRINCIPAL=${KIDE_PRIMARY_PRINCIPAL},_KIDE_OIDC_SECRET_NAME=${KIDE_OIDC_SECRET_NAME}"

PROJECT_ID="${PROJECT_ID}" REGION="${REGION}" KIDE_SERVICE_NAME="${KIDE_SERVICE_NAME}" KIDE_WEB_SERVICE_NAME="${KIDE_WEB_SERVICE_NAME}"   bash deploy/gcp/deployment-status.sh
