#!/usr/bin/env bash
set -euo pipefail

required=(PROJECT_ID TRIGGER_NAME)
for name in "${required[@]}"; do
  if [[ -z "${!name:-}" ]]; then
    echo "${name} is required" >&2
    exit 2
  fi
done

TRIGGER_REGION="${TRIGGER_REGION:-global}"
TRIGGER_KIND="${TRIGGER_KIND:-github}"
REGION="${REGION:-asia-south1}"
AR_REPOSITORY="${AR_REPOSITORY:-kide}"
BUILD_SERVICE_ACCOUNT="${BUILD_SERVICE_ACCOUNT:-}"
BUILD_CONFIG="${BUILD_CONFIG:-cloudbuild.yaml}"

export PROJECT_ID REGION AR_REPOSITORY TRIGGER_NAME TRIGGER_REGION BUILD_SERVICE_ACCOUNT
bash deploy/gcp/bootstrap-cloud-build.sh

substitutions="_REGION=${REGION},_AR_REPOSITORY=${AR_REPOSITORY}"

common=(
  "${TRIGGER_NAME}"
  --project "${PROJECT_ID}"
  --region "${TRIGGER_REGION}"
  --build-config "${BUILD_CONFIG}"
  --update-substitutions "${substitutions}"
)

if [[ -n "${BUILD_SERVICE_ACCOUNT}" ]]; then
  common+=(--service-account "${BUILD_SERVICE_ACCOUNT}")
fi

case "${TRIGGER_KIND}" in
  github)
    gcloud builds triggers update github "${common[@]}"
    ;;
  manual)
    gcloud builds triggers update manual "${common[@]}"
    ;;
  *)
    echo "Unsupported TRIGGER_KIND=${TRIGGER_KIND}; expected github or manual" >&2
    exit 2
    ;;
esac

echo
echo "Trigger repaired: ${TRIGGER_NAME}"
echo "Build config: ${BUILD_CONFIG}"
echo "Logging mode: CLOUD_LOGGING_ONLY"
echo
gcloud builds triggers describe "${TRIGGER_NAME}" \
  --project "${PROJECT_ID}" \
  --region "${TRIGGER_REGION}" \
  --format='yaml(name,serviceAccount,filename,build)'
