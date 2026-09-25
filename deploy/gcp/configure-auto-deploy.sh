#!/usr/bin/env bash
set -euo pipefail

required=(
  PROJECT_ID
  TRIGGER_NAME
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
TRIGGER_REGION="${TRIGGER_REGION:-${REGION}}"
TRIGGER_KIND="${TRIGGER_KIND:-github}"
AR_REPOSITORY="${AR_REPOSITORY:-kide}"
BUILD_CONFIG="${BUILD_CONFIG:-cloudbuild.yaml}"
BUILD_SERVICE_ACCOUNT="${BUILD_SERVICE_ACCOUNT:-}"
KIDE_SERVICE_NAME="${KIDE_SERVICE_NAME:-kide}"
KIDE_WEB_SERVICE_NAME="${KIDE_WEB_SERVICE_NAME:-kide-web}"
KIDE_GITHUB_REPOSITORY="${KIDE_GITHUB_REPOSITORY:-amarbanerjee23/KIDE}"
KIDE_GITHUB_TOKEN_SECRET="${KIDE_GITHUB_TOKEN_SECRET:-kide-github-release-token}"
DATA_BUCKET="${DATA_BUCKET:-${PROJECT_ID}-kide-data}"
RUNTIME_SA_NAME="${RUNTIME_SA_NAME:-kide-runtime}"
WEB_RUNTIME_SA_NAME="${WEB_RUNTIME_SA_NAME:-kide-web-runtime}"
RUNTIME_SA="${RUNTIME_SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com"
WEB_RUNTIME_SA="${WEB_RUNTIME_SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com"

gcloud config set project "${PROJECT_ID}" >/dev/null
gcloud services enable run.googleapis.com artifactregistry.googleapis.com cloudbuild.googleapis.com secretmanager.googleapis.com

if [[ -z "${BUILD_SERVICE_ACCOUNT}" ]]; then
  BUILD_SERVICE_ACCOUNT="$(gcloud builds triggers describe "${TRIGGER_NAME}" --project "${PROJECT_ID}" --region "${TRIGGER_REGION}" --format='value(serviceAccount)')"
fi
if [[ -z "${BUILD_SERVICE_ACCOUNT}" ]]; then
  BUILD_SERVICE_ACCOUNT="$(gcloud builds get-default-service-account --project "${PROJECT_ID}" 2>/dev/null || true)"
fi
if [[ -z "${BUILD_SERVICE_ACCOUNT}" ]]; then
  echo "Could not determine the Cloud Build service account." >&2
  echo "Set BUILD_SERVICE_ACCOUNT and run this script again." >&2
  exit 2
fi

if [[ "${BUILD_SERVICE_ACCOUNT}" == projects/*/serviceAccounts/* ]]; then
  BUILD_SERVICE_ACCOUNT_RESOURCE="${BUILD_SERVICE_ACCOUNT}"
  BUILD_SERVICE_ACCOUNT_EMAIL="${BUILD_SERVICE_ACCOUNT##*/}"
else
  BUILD_SERVICE_ACCOUNT_EMAIL="${BUILD_SERVICE_ACCOUNT}"
  BUILD_SERVICE_ACCOUNT_RESOURCE="projects/${PROJECT_ID}/serviceAccounts/${BUILD_SERVICE_ACCOUNT_EMAIL}"
fi

BUILD_SERVICE_ACCOUNT="${BUILD_SERVICE_ACCOUNT_RESOURCE}"
export PROJECT_ID REGION AR_REPOSITORY TRIGGER_NAME TRIGGER_REGION BUILD_SERVICE_ACCOUNT
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

gcloud secrets describe "${KIDE_GITHUB_TOKEN_SECRET}" --project "${PROJECT_ID}" >/dev/null
gcloud secrets add-iam-policy-binding "${KIDE_GITHUB_TOKEN_SECRET}" --project "${PROJECT_ID}" --member="serviceAccount:${BUILD_SERVICE_ACCOUNT_EMAIL}" --role="roles/secretmanager.secretAccessor" >/dev/null

gcloud projects add-iam-policy-binding "${PROJECT_ID}" --member="serviceAccount:${BUILD_SERVICE_ACCOUNT_EMAIL}" --role="roles/run.admin" >/dev/null

for service_account in "${RUNTIME_SA}" "${WEB_RUNTIME_SA}"; do
  gcloud iam service-accounts add-iam-policy-binding "${service_account}" --project "${PROJECT_ID}" --member="serviceAccount:${BUILD_SERVICE_ACCOUNT_EMAIL}" --role="roles/iam.serviceAccountUser" >/dev/null
done

substitutions="_KIDE_RELEASE_ENABLED=true,_REGION=${REGION},_AR_REPOSITORY=${AR_REPOSITORY},_KIDE_SERVICE_NAME=${KIDE_SERVICE_NAME},_KIDE_WEB_SERVICE_NAME=${KIDE_WEB_SERVICE_NAME},_KIDE_DATA_BUCKET=${DATA_BUCKET},_KIDE_RUNTIME_SA=${RUNTIME_SA},_KIDE_WEB_RUNTIME_SA=${WEB_RUNTIME_SA},_KIDE_OIDC_INTROSPECTION_URL=${KIDE_OIDC_INTROSPECTION_URL},_KIDE_OIDC_CLIENT_ID=${KIDE_OIDC_CLIENT_ID},_KIDE_OIDC_ISSUER=${KIDE_OIDC_ISSUER},_KIDE_OIDC_AUDIENCE=${KIDE_OIDC_AUDIENCE},_KIDE_PRIMARY_PRINCIPAL=${KIDE_PRIMARY_PRINCIPAL},_KIDE_OIDC_SECRET_NAME=${KIDE_OIDC_SECRET_NAME},_KIDE_GITHUB_REPOSITORY=${KIDE_GITHUB_REPOSITORY},_KIDE_GITHUB_TOKEN_SECRET=${KIDE_GITHUB_TOKEN_SECRET}"

common=(
  "${TRIGGER_NAME}"
  --project "${PROJECT_ID}"
  --region "${TRIGGER_REGION}"
  --build-config "${BUILD_CONFIG}"
  --update-substitutions "${substitutions}"
  --service-account "${BUILD_SERVICE_ACCOUNT_RESOURCE}"
)

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
echo "KIDE continuous deployment configured."
echo "Project: ${PROJECT_ID}"
echo "Region: ${REGION}"
echo "Backend service: ${KIDE_SERVICE_NAME}"
echo "Web service: ${KIDE_WEB_SERVICE_NAME}"
echo "GitHub release repository: ${KIDE_GITHUB_REPOSITORY}"
echo "GitHub token secret: ${KIDE_GITHUB_TOKEN_SECRET}"
echo "Build service account: ${BUILD_SERVICE_ACCOUNT_EMAIL}"
echo "Backend runtime service account: ${RUNTIME_SA}"
echo "Web runtime service account: ${WEB_RUNTIME_SA}"
echo
echo "The next successful trigger build will deploy both Cloud Run services."
