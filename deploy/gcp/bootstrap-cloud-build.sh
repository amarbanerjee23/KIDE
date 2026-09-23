#!/usr/bin/env bash
set -euo pipefail

if [[ -z "${PROJECT_ID:-}" ]]; then
  echo "PROJECT_ID is required" >&2
  exit 2
fi

REGION="${REGION:-asia-south1}"
AR_REPOSITORY="${AR_REPOSITORY:-kide}"
TRIGGER_NAME="${TRIGGER_NAME:-}"
TRIGGER_REGION="${TRIGGER_REGION:-global}"
BUILD_SERVICE_ACCOUNT="${BUILD_SERVICE_ACCOUNT:-}"

gcloud config set project "${PROJECT_ID}" >/dev/null
gcloud services enable artifactregistry.googleapis.com cloudbuild.googleapis.com

if ! gcloud artifacts repositories describe "${AR_REPOSITORY}"     --project "${PROJECT_ID}"     --location "${REGION}" >/dev/null 2>&1; then
  gcloud artifacts repositories create "${AR_REPOSITORY}"     --project "${PROJECT_ID}"     --location "${REGION}"     --repository-format=docker     --description="KIDE Cloud Build images"
fi

if [[ -z "${BUILD_SERVICE_ACCOUNT}" && -n "${TRIGGER_NAME}" ]]; then
  BUILD_SERVICE_ACCOUNT="$(gcloud builds triggers describe "${TRIGGER_NAME}"     --project "${PROJECT_ID}"     --region "${TRIGGER_REGION}"     --format='value(serviceAccount)')"
fi

if [[ -z "${BUILD_SERVICE_ACCOUNT}" ]]; then
  BUILD_SERVICE_ACCOUNT="$(gcloud builds get-default-service-account     --project "${PROJECT_ID}" 2>/dev/null || true)"
fi

if [[ -z "${BUILD_SERVICE_ACCOUNT}" ]]; then
  echo "Could not determine the Cloud Build service account." >&2
  echo "Set BUILD_SERVICE_ACCOUNT and run this script again." >&2
  exit 2
fi

BUILD_SERVICE_ACCOUNT="${BUILD_SERVICE_ACCOUNT##*/}"

gcloud artifacts repositories add-iam-policy-binding "${AR_REPOSITORY}"   --project "${PROJECT_ID}"   --location "${REGION}"   --member="serviceAccount:${BUILD_SERVICE_ACCOUNT}"   --role="roles/artifactregistry.writer" >/dev/null

gcloud projects add-iam-policy-binding "${PROJECT_ID}"   --member="serviceAccount:${BUILD_SERVICE_ACCOUNT}"   --role="roles/logging.logWriter" >/dev/null

echo "Artifact Registry ready:"
echo "  repository: ${REGION}-docker.pkg.dev/${PROJECT_ID}/${AR_REPOSITORY}"
echo "  build service account: ${BUILD_SERVICE_ACCOUNT}"
