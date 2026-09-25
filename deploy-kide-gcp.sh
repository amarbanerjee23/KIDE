#!/usr/bin/env bash
set -euo pipefail

COMMAND="${1:-status}"
PROJECT_ID="${PROJECT_ID:-}"
REGION="${REGION:-asia-south1}"
AR_REPOSITORY="${AR_REPOSITORY:-kide}"
KIDE_SERVICE_NAME="${KIDE_SERVICE_NAME:-kide}"
KIDE_WEB_SERVICE_NAME="${KIDE_WEB_SERVICE_NAME:-kide-web}"
KIDE_REPOSITORY="${KIDE_REPOSITORY:-https://github.com/amarbanerjee23/KIDE.git}"
KIDE_DEPLOY_CHECKOUT="${KIDE_DEPLOY_CHECKOUT:-${HOME}/.cache/kide-deploy/KIDE}"

require_gcloud() {
  if ! command -v gcloud >/dev/null 2>&1; then
    echo "gcloud is required. Run this from Google Cloud Shell or install the Google Cloud CLI." >&2
    exit 2
  fi
  if [[ -z "${PROJECT_ID}" ]]; then
    PROJECT_ID="$(gcloud config get-value project 2>/dev/null || true)"
  fi
  if [[ -z "${PROJECT_ID}" || "${PROJECT_ID}" == "(unset)" ]]; then
    echo "PROJECT_ID is not set and gcloud has no active project." >&2
    echo "Set it with: export PROJECT_ID=kide-eclipse" >&2
    exit 2
  fi
}

service_url() {
  local service="$1"
  gcloud run services describe "${service}"     --project "${PROJECT_ID}"     --region "${REGION}"     --format='value(status.url)' 2>/dev/null || true
}

show_status() {
  require_gcloud
  local backend_url web_url
  backend_url="$(service_url "${KIDE_SERVICE_NAME}")"
  web_url="$(service_url "${KIDE_WEB_SERVICE_NAME}")"

  echo "Project: ${PROJECT_ID}"
  echo "Region: ${REGION}"
  if [[ -n "${web_url}" ]]; then
    echo "KIDE web: ${web_url}"
    echo "Web health: ${web_url}/healthz"
  else
    echo "KIDE web: NOT DEPLOYED"
  fi

  if [[ -n "${backend_url}" ]]; then
    echo "KIDE backend: ${backend_url}"
    echo "Backend health: ${backend_url}/healthz"
  else
    echo "KIDE backend: NOT DEPLOYED"
  fi
}

ensure_checkout() {
  if [[ -d "${KIDE_DEPLOY_CHECKOUT}/.git" ]]; then
    git -C "${KIDE_DEPLOY_CHECKOUT}" fetch --quiet origin main
    git -C "${KIDE_DEPLOY_CHECKOUT}" checkout --quiet main
    git -C "${KIDE_DEPLOY_CHECKOUT}" reset --hard --quiet origin/main
  else
    mkdir -p "$(dirname "${KIDE_DEPLOY_CHECKOUT}")"
    git clone --quiet --depth 1 --branch main "${KIDE_REPOSITORY}" "${KIDE_DEPLOY_CHECKOUT}"
  fi
}

require_deploy_environment() {
  local required=(
    KIDE_OIDC_INTROSPECTION_URL
    KIDE_OIDC_CLIENT_ID
    KIDE_OIDC_ISSUER
    KIDE_OIDC_AUDIENCE
    KIDE_PRIMARY_PRINCIPAL
    KIDE_OIDC_SECRET_NAME
  )
  local missing=()
  local name
  for name in "${required[@]}"; do
    if [[ -z "${!name:-}" ]]; then
      missing+=("${name}")
    fi
  done

  if (( ${#missing[@]} > 0 )); then
    echo "Deployment configuration is incomplete. Set:" >&2
    printf '  export %s="..."
' "${missing[@]}" >&2
    echo >&2
    echo "The OIDC client secret value itself stays in Secret Manager; only set KIDE_OIDC_SECRET_NAME here." >&2
    exit 2
  fi
}

deploy() {
  require_gcloud
  require_deploy_environment
  ensure_checkout

  echo "Deploying KIDE from current main using the unified GCP release pipeline..."
  (
    cd "${KIDE_DEPLOY_CHECKOUT}"
    PROJECT_ID="${PROJECT_ID}"     REGION="${REGION}"     KIDE_SERVICE_NAME="${KIDE_SERVICE_NAME}"     KIDE_WEB_SERVICE_NAME="${KIDE_WEB_SERVICE_NAME}"     bash deploy/gcp/deploy-cloud-run.sh
  )

  echo
  show_status
}


bootstrap_first_deployment() {
  require_gcloud
  ensure_checkout

  echo "Starting KIDE first-deployment bootstrap..."
  (
    cd "${KIDE_DEPLOY_CHECKOUT}"
    PROJECT_ID="${PROJECT_ID}" \
    REGION="${REGION}" \
    KIDE_SERVICE_NAME="${KIDE_SERVICE_NAME}" \
    KIDE_WEB_SERVICE_NAME="${KIDE_WEB_SERVICE_NAME}" \
    TRIGGER_NAME="${TRIGGER_NAME:-}" \
    TRIGGER_REGION="${TRIGGER_REGION:-${REGION}}" \
    KIDE_OIDC_INTROSPECTION_URL="${KIDE_OIDC_INTROSPECTION_URL:-}" \
    KIDE_OIDC_CLIENT_ID="${KIDE_OIDC_CLIENT_ID:-}" \
    KIDE_OIDC_ISSUER="${KIDE_OIDC_ISSUER:-}" \
    KIDE_OIDC_AUDIENCE="${KIDE_OIDC_AUDIENCE:-}" \
    KIDE_PRIMARY_PRINCIPAL="${KIDE_PRIMARY_PRINCIPAL:-}" \
    KIDE_OIDC_SECRET_NAME="${KIDE_OIDC_SECRET_NAME:-kide-oidc-client-secret}" \
    KIDE_OIDC_CLIENT_SECRET_FILE="${KIDE_OIDC_CLIENT_SECRET_FILE:-}" \
    KIDE_GITHUB_REPOSITORY="${KIDE_GITHUB_REPOSITORY:-amarbanerjee23/KIDE}" \
    KIDE_GITHUB_TOKEN_SECRET="${KIDE_GITHUB_TOKEN_SECRET:-kide-github-release-token}" \
    KIDE_GITHUB_RELEASE_TOKEN_FILE="${KIDE_GITHUB_RELEASE_TOKEN_FILE:-}" \
    bash deploy/gcp/bootstrap-first-deployment.sh
  )
}

configure_trigger() {
  require_gcloud
  require_deploy_environment
  if [[ -z "${TRIGGER_NAME:-}" ]]; then
    echo "TRIGGER_NAME is required for configure-trigger." >&2
    exit 2
  fi
  ensure_checkout
  (
    cd "${KIDE_DEPLOY_CHECKOUT}"
    PROJECT_ID="${PROJECT_ID}"     REGION="${REGION}"     TRIGGER_NAME="${TRIGGER_NAME}"     TRIGGER_REGION="${TRIGGER_REGION:-${REGION}}"     KIDE_GITHUB_REPOSITORY="${KIDE_GITHUB_REPOSITORY:-amarbanerjee23/KIDE}"     KIDE_GITHUB_TOKEN_SECRET="${KIDE_GITHUB_TOKEN_SECRET:-kide-github-release-token}"     bash deploy/gcp/configure-auto-deploy.sh
  )
}

doctor() {
  require_gcloud
  echo "KIDE GCP deployment doctor"
  echo "Project: ${PROJECT_ID}"
  echo "Region: ${REGION}"
  echo "Account: $(gcloud config get-value account 2>/dev/null || true)"
  echo

  if gcloud artifacts repositories describe kide       --project "${PROJECT_ID}" --location "${REGION}" >/dev/null 2>&1; then
    echo "Artifact Registry: OK"
  else
    echo "Artifact Registry: MISSING"
  fi

  if [[ -n "${KIDE_OIDC_SECRET_NAME:-}" ]]; then
    if gcloud secrets describe "${KIDE_OIDC_SECRET_NAME}"         --project "${PROJECT_ID}" >/dev/null 2>&1; then
      echo "OIDC secret: OK (${KIDE_OIDC_SECRET_NAME})"
    else
      echo "OIDC secret: MISSING OR NOT ACCESSIBLE (${KIDE_OIDC_SECRET_NAME})"
    fi
  else
    echo "OIDC secret: NOT CONFIGURED (KIDE_OIDC_SECRET_NAME is unset)"
  fi

  echo
  show_status
}

usage() {
  cat <<'EOF'
Usage: deploy-kide-gcp.sh [status|doctor|bootstrap|deploy|configure-trigger]

status
  Show the current kide and kide-web Cloud Run URLs. Works from any directory.

doctor
  Check the active project, Artifact Registry, optional OIDC secret and services.

bootstrap
  First-deployment wizard. Discovers the Cloud Build trigger, prompts for
  missing OIDC settings, stores the client secret in Secret Manager, provisions
  required resources/IAM, switches the trigger to the unified GCP release
  pipeline, runs the first deployment, verifies both live services and
  publishes the hosted URL plus Eclipse bundles to GitHub Releases.

deploy
  Clone/update KIDE in a private deployment cache and run
  deploy/gcp/cloudbuild-release.yaml. It builds the Eclipse products, deploys
  backend/web and publishes the hosted URL plus desktop bundles to Releases.
  Use after bootstrap.

configure-trigger
  Configure an existing Cloud Build trigger to use the unified GCP release
  pipeline. Requires TRIGGER_NAME and pre-provisioned secrets.

Common variables:
  PROJECT_ID                default: active gcloud project
  REGION                    default: asia-south1
  KIDE_SERVICE_NAME         default: kide
  KIDE_WEB_SERVICE_NAME     default: kide-web
  AR_REPOSITORY             default: kide

Deployment variables:
  KIDE_OIDC_INTROSPECTION_URL
  KIDE_OIDC_CLIENT_ID
  KIDE_OIDC_ISSUER
  KIDE_OIDC_AUDIENCE
  KIDE_PRIMARY_PRINCIPAL
  KIDE_OIDC_SECRET_NAME

GitHub release publishing:
  KIDE_GITHUB_REPOSITORY        default: amarbanerjee23/KIDE
  KIDE_GITHUB_TOKEN_SECRET      default: kide-github-release-token
  KIDE_GITHUB_RELEASE_TOKEN_FILE
EOF
}

case "${COMMAND}" in
  status) show_status ;;
  doctor) doctor ;;
  bootstrap) bootstrap_first_deployment ;;
  deploy) deploy ;;
  configure-trigger) configure_trigger ;;
  help|-h|--help) usage ;;
  *)
    echo "Unknown command: ${COMMAND}" >&2
    usage >&2
    exit 2
    ;;
esac
