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
SERVICE_NAME="${SERVICE_NAME:-kide}"
AR_REPOSITORY="${AR_REPOSITORY:-kide}"
DATA_BUCKET="${DATA_BUCKET:-${PROJECT_ID}-kide-data}"
IMAGE_TAG="${IMAGE_TAG:-$(git rev-parse --short=12 HEAD)}"
RUNTIME_SA_NAME="${RUNTIME_SA_NAME:-kide-runtime}"
RUNTIME_SA="${RUNTIME_SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com"
IMAGE="${REGION}-docker.pkg.dev/${PROJECT_ID}/${AR_REPOSITORY}/kide:${IMAGE_TAG}"

gcloud config set project "${PROJECT_ID}" >/dev/null

gcloud services enable \
  run.googleapis.com \
  artifactregistry.googleapis.com \
  cloudbuild.googleapis.com \
  secretmanager.googleapis.com

if ! gcloud artifacts repositories describe "${AR_REPOSITORY}" \
    --location "${REGION}" >/dev/null 2>&1; then
  gcloud artifacts repositories create "${AR_REPOSITORY}" \
    --repository-format=docker \
    --location "${REGION}" \
    --description="KIDE Cloud Run images"
fi

if ! gcloud storage buckets describe "gs://${DATA_BUCKET}" >/dev/null 2>&1; then
  gcloud storage buckets create "gs://${DATA_BUCKET}" \
    --location "${REGION}" \
    --uniform-bucket-level-access
fi

if ! gcloud iam service-accounts describe "${RUNTIME_SA}" >/dev/null 2>&1; then
  gcloud iam service-accounts create "${RUNTIME_SA_NAME}" \
    --display-name="KIDE Cloud Run runtime"
fi

gcloud storage buckets add-iam-policy-binding "gs://${DATA_BUCKET}" \
  --member="serviceAccount:${RUNTIME_SA}" \
  --role="roles/storage.objectUser" >/dev/null

gcloud secrets describe "${KIDE_OIDC_SECRET_NAME}" >/dev/null

gcloud secrets add-iam-policy-binding "${KIDE_OIDC_SECRET_NAME}" \
  --member="serviceAccount:${RUNTIME_SA}" \
  --role="roles/secretmanager.secretAccessor" >/dev/null

gcloud builds submit . \
  --config deploy/gcp/cloudbuild.yaml \
  --substitutions="_IMAGE=${IMAGE},_QUALIFIER=${IMAGE_TAG}"

gcloud run deploy "${SERVICE_NAME}" \
  --region "${REGION}" \
  --image "${IMAGE}" \
  --service-account "${RUNTIME_SA}" \
  --allow-unauthenticated \
  --port 8080 \
  --cpu 2 \
  --memory 4Gi \
  --concurrency 20 \
  --timeout 3600s \
  --min 1 \
  --max 1 \
  --session-affinity \
  --add-volume "mount-path=/data,type=cloud-storage,bucket=${DATA_BUCKET},readonly=false,mount-options=uid=10001;gid=10001" \
  --set-env-vars "KIDE_OIDC_INTROSPECTION_URL=${KIDE_OIDC_INTROSPECTION_URL}" \
  --set-env-vars "KIDE_OIDC_CLIENT_ID=${KIDE_OIDC_CLIENT_ID}" \
  --set-env-vars "KIDE_OIDC_ISSUER=${KIDE_OIDC_ISSUER}" \
  --set-env-vars "KIDE_OIDC_AUDIENCE=${KIDE_OIDC_AUDIENCE}" \
  --set-env-vars "KIDE_PRIMARY_PRINCIPAL=${KIDE_PRIMARY_PRINCIPAL}" \
  --set-secrets "KIDE_OIDC_CLIENT_SECRET=${KIDE_OIDC_SECRET_NAME}:latest"

SERVICE_URL="$(gcloud run services describe "${SERVICE_NAME}" \
  --region "${REGION}" \
  --format='value(status.url)')"

gcloud run services update "${SERVICE_NAME}" \
  --region "${REGION}" \
  --update-env-vars "KIDE_ALLOWED_ORIGINS=${SERVICE_URL}" >/dev/null

echo "KIDE deployed: ${SERVICE_URL}"
echo "Health: ${SERVICE_URL}/healthz"
