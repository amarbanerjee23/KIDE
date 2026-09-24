#!/usr/bin/env bash
set -euo pipefail

required=(PROJECT_ID KIDE_BACKEND_ORIGIN)
for name in "${required[@]}"; do
  if [[ -z "${!name:-}" ]]; then
    echo "${name} is required" >&2
    exit 2
  fi
done

REGION="${REGION:-asia-south1}"
SERVICE_NAME="${SERVICE_NAME:-kide-web}"
BACKEND_SERVICE_NAME="${BACKEND_SERVICE_NAME:-kide}"
AR_REPOSITORY="${AR_REPOSITORY:-kide}"
IMAGE_TAG="${IMAGE_TAG:-$(git rev-parse --short=12 HEAD)}"
IMAGE="${REGION}-docker.pkg.dev/${PROJECT_ID}/${AR_REPOSITORY}/kide-web:${IMAGE_TAG}"

case "${KIDE_BACKEND_ORIGIN}" in
  https://*) ;;
  *)
    echo "KIDE_BACKEND_ORIGIN must be an https:// origin" >&2
    exit 2
    ;;
esac

gcloud config set project "${PROJECT_ID}" >/dev/null

export PROJECT_ID REGION AR_REPOSITORY
bash deploy/gcp/bootstrap-cloud-build.sh

gcloud builds submit .   --config deploy/web/cloudbuild.yaml   --substitutions="_REGION=${REGION},_AR_REPOSITORY=${AR_REPOSITORY},_KIDE_WEB_IMAGE=${IMAGE},_KIDE_BACKEND_ORIGIN=${KIDE_BACKEND_ORIGIN}"

gcloud run deploy "${SERVICE_NAME}"   --project "${PROJECT_ID}"   --region "${REGION}"   --image "${IMAGE}"   --allow-unauthenticated   --port 8080   --cpu 1   --memory 512Mi   --concurrency 80   --timeout 300s   --min 0   --max 10

WEB_URL="$(gcloud run services describe "${SERVICE_NAME}"   --project "${PROJECT_ID}"   --region "${REGION}"   --format='value(status.url)')"

if gcloud run services describe "${BACKEND_SERVICE_NAME}"     --project "${PROJECT_ID}"     --region "${REGION}" >/dev/null 2>&1; then
  gcloud run services update "${BACKEND_SERVICE_NAME}"     --project "${PROJECT_ID}"     --region "${REGION}"     --update-env-vars "^^@^^KIDE_ALLOWED_ORIGINS=${KIDE_BACKEND_ORIGIN},${WEB_URL}"     >/dev/null
  echo "Updated backend allowed origins for ${BACKEND_SERVICE_NAME}."
else
  echo "Backend Cloud Run service '${BACKEND_SERVICE_NAME}' was not found; configure it to allow Origin: ${WEB_URL}" >&2
fi

echo "KIDE web deployed: ${WEB_URL}"
echo "Backend: ${KIDE_BACKEND_ORIGIN}"
echo "Health: ${WEB_URL}/healthz"
