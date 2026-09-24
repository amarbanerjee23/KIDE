#!/usr/bin/env bash
set -euo pipefail

if [[ -z "${PROJECT_ID:-}" ]]; then
  echo "PROJECT_ID is required" >&2
  exit 2
fi

REGION="${REGION:-asia-south1}"
KIDE_SERVICE_NAME="${KIDE_SERVICE_NAME:-kide}"
KIDE_WEB_SERVICE_NAME="${KIDE_WEB_SERVICE_NAME:-kide-web}"

service_url() {
  local service="$1"
  gcloud run services describe "${service}"     --project "${PROJECT_ID}"     --region "${REGION}"     --format='value(status.url)'
}

BACKEND_URL="$(service_url "${KIDE_SERVICE_NAME}")"
WEB_URL="$(service_url "${KIDE_WEB_SERVICE_NAME}")"

echo "KIDE web: ${WEB_URL}"
echo "KIDE backend: ${BACKEND_URL}"
echo "Web health: ${WEB_URL}/healthz"
echo "Backend health: ${BACKEND_URL}/healthz"
