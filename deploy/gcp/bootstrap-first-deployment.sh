#!/usr/bin/env bash
set -euo pipefail

PROJECT_ID="${PROJECT_ID:-}"
REGION="${REGION:-asia-south1}"
TRIGGER_REGION="${TRIGGER_REGION:-${REGION}}"
DEPLOY_BRANCH="${DEPLOY_BRANCH:-main}"
TRIGGER_NAME="${TRIGGER_NAME:-}"
KIDE_OIDC_SECRET_NAME="${KIDE_OIDC_SECRET_NAME:-kide-oidc-client-secret}"
KIDE_SERVICE_NAME="${KIDE_SERVICE_NAME:-kide}"
KIDE_WEB_SERVICE_NAME="${KIDE_WEB_SERVICE_NAME:-kide-web}"
WAIT_SECONDS="${WAIT_SECONDS:-1800}"
WAIT_INTERVAL="${WAIT_INTERVAL:-10}"
SECRET_TEMP_FILE=""

cleanup_secret_temp_file() {
  if [[ -n "${SECRET_TEMP_FILE:-}" && -f "${SECRET_TEMP_FILE}" ]]; then
    rm -f "${SECRET_TEMP_FILE}"
  fi
}
trap cleanup_secret_temp_file EXIT

require_command() {
  local command_name="$1"
  if ! command -v "${command_name}" >/dev/null 2>&1; then
    echo "${command_name} is required." >&2
    exit 2
  fi
}

require_command gcloud
require_command git
require_command curl

if [[ -z "${PROJECT_ID}" ]]; then
  PROJECT_ID="$(gcloud config get-value project 2>/dev/null || true)"
fi
if [[ -z "${PROJECT_ID}" || "${PROJECT_ID}" == "(unset)" ]]; then
  echo "PROJECT_ID is required and no active gcloud project is configured." >&2
  exit 2
fi

gcloud config set project "${PROJECT_ID}" >/dev/null

prompt_value() {
  local variable="$1"
  local label="$2"
  local default_value="${3:-}"
  local current_value="${!variable:-}"
  local entered=""

  if [[ -n "${current_value}" ]]; then
    return
  fi

  if [[ ! -t 0 ]]; then
    echo "${variable} is required in non-interactive mode." >&2
    exit 2
  fi

  if [[ -n "${default_value}" ]]; then
    read -r -p "${label} [${default_value}]: " entered
    entered="${entered:-${default_value}}"
  else
    read -r -p "${label}: " entered
  fi

  if [[ -z "${entered}" ]]; then
    echo "${variable} cannot be empty." >&2
    exit 2
  fi

  printf -v "${variable}" '%s' "${entered}"
  export "${variable}"
}

prompt_value KIDE_OIDC_INTROSPECTION_URL "OIDC introspection URL"
prompt_value KIDE_OIDC_CLIENT_ID "OIDC client ID"
prompt_value KIDE_OIDC_ISSUER "OIDC issuer URL"
prompt_value KIDE_OIDC_AUDIENCE "OIDC audience"
prompt_value KIDE_PRIMARY_PRINCIPAL "KIDE primary principal"
export KIDE_OIDC_SECRET_NAME

discover_trigger() {
  local requested="${TRIGGER_NAME}"
  local -a search_regions=()
  local -a all_candidates=()
  local -a preferred_candidates=()
  local region trigger_id trigger_name filename line

  if [[ -n "${requested}" ]]; then
    for region in "${TRIGGER_REGION}" global; do
      [[ "${region}" == "global" && "${TRIGGER_REGION}" == "global" && "${#search_regions[@]}" -gt 0 ]] && continue
      if gcloud builds triggers describe "${requested}"           --project "${PROJECT_ID}"           --region "${region}" >/dev/null 2>&1; then
        TRIGGER_REGION="${region}"
        export TRIGGER_REGION TRIGGER_NAME
        return
      fi
    done
    echo "Configured TRIGGER_NAME '${requested}' was not found in ${TRIGGER_REGION} or global." >&2
    exit 2
  fi

  search_regions+=("${TRIGGER_REGION}")
  if [[ "${TRIGGER_REGION}" != "global" ]]; then
    search_regions+=("global")
  fi

  for region in "${search_regions[@]}"; do
    while IFS= read -r trigger_id; do
      [[ -z "${trigger_id}" ]] && continue
      trigger_name="$(gcloud builds triggers describe "${trigger_id}"         --project "${PROJECT_ID}"         --region "${region}"         --format='value(name)' 2>/dev/null || true)"
      filename="$(gcloud builds triggers describe "${trigger_id}"         --project "${PROJECT_ID}"         --region "${region}"         --format='value(filename)' 2>/dev/null || true)"
      line="${region}|${trigger_id}|${trigger_name}|${filename}"
      all_candidates+=("${line}")
      if [[ "${filename}" == "cloudbuild.yaml" || "${filename}" == "deploy/gcp/cloudbuild-deploy.yaml" ]]; then
        preferred_candidates+=("${line}")
      fi
    done < <(
      gcloud builds triggers list         --project "${PROJECT_ID}"         --region "${region}"         --format='value(id)' 2>/dev/null || true
    )
  done

  local -a candidates=()
  if (( ${#preferred_candidates[@]} > 0 )); then
    candidates=("${preferred_candidates[@]}")
  else
    candidates=("${all_candidates[@]}")
  fi

  if (( ${#candidates[@]} == 0 )); then
    echo "No Cloud Build trigger was found in project ${PROJECT_ID}." >&2
    echo "Create a GitHub trigger first, or set TRIGGER_NAME and TRIGGER_REGION explicitly." >&2
    exit 2
  fi

  if (( ${#candidates[@]} == 1 )); then
    IFS='|' read -r TRIGGER_REGION TRIGGER_NAME _ _ <<< "${candidates[0]}"
    export TRIGGER_REGION TRIGGER_NAME
    return
  fi

  echo "Multiple Cloud Build triggers were found:"
  local index=1
  for line in "${candidates[@]}"; do
    IFS='|' read -r region trigger_id trigger_name filename <<< "${line}"
    printf '  %d) %s [%s] region=%s config=%s\n'       "${index}" "${trigger_name:-${trigger_id}}" "${trigger_id}" "${region}" "${filename:-unknown}"
    ((index+=1))
  done

  if [[ ! -t 0 ]]; then
    echo "Set TRIGGER_NAME and TRIGGER_REGION to select one in non-interactive mode." >&2
    exit 2
  fi

  local selection
  read -r -p "Select the KIDE deployment trigger [1-${#candidates[@]}]: " selection
  if ! [[ "${selection}" =~ ^[0-9]+$ ]] || (( selection < 1 || selection > ${#candidates[@]} )); then
    echo "Invalid trigger selection." >&2
    exit 2
  fi

  IFS='|' read -r TRIGGER_REGION TRIGGER_NAME _ _ <<< "${candidates[selection-1]}"
  export TRIGGER_REGION TRIGGER_NAME
}

store_client_secret() {
  local temp_file=""
  local secret_value=""
  local secret_source_file="${KIDE_OIDC_CLIENT_SECRET_FILE:-}"

  if [[ -n "${secret_source_file}" ]]; then
    if [[ ! -f "${secret_source_file}" ]]; then
      echo "KIDE_OIDC_CLIENT_SECRET_FILE does not exist: ${secret_source_file}" >&2
      exit 2
    fi
    temp_file="${secret_source_file}"
  else
    if [[ ! -t 0 ]]; then
      echo "KIDE_OIDC_CLIENT_SECRET_FILE is required in non-interactive mode." >&2
      exit 2
    fi

    read -r -s -p "OIDC client secret (hidden): " secret_value
    echo
    if [[ -z "${secret_value}" ]]; then
      echo "OIDC client secret cannot be empty." >&2
      exit 2
    fi

    umask 077
    temp_file="$(mktemp)"
    SECRET_TEMP_FILE="${temp_file}"
    printf '%s' "${secret_value}" > "${temp_file}"
    unset secret_value
  fi

  gcloud services enable secretmanager.googleapis.com --project "${PROJECT_ID}" >/dev/null

  if gcloud secrets describe "${KIDE_OIDC_SECRET_NAME}"       --project "${PROJECT_ID}" >/dev/null 2>&1; then
    echo "Adding a new version to Secret Manager secret '${KIDE_OIDC_SECRET_NAME}'..."
    gcloud secrets versions add "${KIDE_OIDC_SECRET_NAME}"       --project "${PROJECT_ID}"       --data-file="${temp_file}" >/dev/null
  else
    echo "Creating Secret Manager secret '${KIDE_OIDC_SECRET_NAME}'..."
    gcloud secrets create "${KIDE_OIDC_SECRET_NAME}"       --project "${PROJECT_ID}"       --replication-policy=automatic       --data-file="${temp_file}" >/dev/null
  fi

  if [[ -n "${SECRET_TEMP_FILE}" ]]; then
    cleanup_secret_temp_file
    SECRET_TEMP_FILE=""
  fi

  echo "OIDC client secret stored in Secret Manager."
}

service_url() {
  local service="$1"
  gcloud run services describe "${service}"     --project "${PROJECT_ID}"     --region "${REGION}"     --format='value(status.url)' 2>/dev/null || true
}

wait_for_live_services() {
  local started now elapsed
  local backend_url="" web_url=""
  started="$(date +%s)"

  echo "Waiting for KIDE Cloud Run services to become healthy..."
  while true; do
    backend_url="$(service_url "${KIDE_SERVICE_NAME}")"
    web_url="$(service_url "${KIDE_WEB_SERVICE_NAME}")"

    if [[ -n "${backend_url}" && -n "${web_url}" ]]; then
      if curl --fail --silent --show-error "${backend_url}/healthz" >/dev/null 2>&1           && curl --fail --silent --show-error "${web_url}/healthz" >/dev/null 2>&1           && curl --fail --silent --show-error "${web_url}/" | grep -q '<div id="root"></div>'; then
        echo
        echo "KIDE FIRST DEPLOYMENT COMPLETE"
        echo "Web: ${web_url}"
        echo "Backend: ${backend_url}"
        echo "Web health: ${web_url}/healthz"
        echo "Backend health: ${backend_url}/healthz"
        return
      fi
    fi

    now="$(date +%s)"
    elapsed=$((now - started))
    if (( elapsed >= WAIT_SECONDS )); then
      echo "Timed out after ${WAIT_SECONDS}s waiting for both KIDE services to become healthy." >&2
      echo "Current backend: ${backend_url:-NOT DEPLOYED}" >&2
      echo "Current web: ${web_url:-NOT DEPLOYED}" >&2
      exit 1
    fi

    printf '.'
    sleep "${WAIT_INTERVAL}"
  done
}

echo "KIDE first-deployment bootstrap"
echo "Project: ${PROJECT_ID}"
echo "Region: ${REGION}"
echo

discover_trigger
echo "Cloud Build trigger: ${TRIGGER_NAME}"
echo "Trigger region: ${TRIGGER_REGION}"

store_client_secret

echo
echo "Provisioning GCP resources/IAM and switching the trigger to the deployment pipeline..."
PROJECT_ID="${PROJECT_ID}" REGION="${REGION}" TRIGGER_NAME="${TRIGGER_NAME}" TRIGGER_REGION="${TRIGGER_REGION}" KIDE_OIDC_INTROSPECTION_URL="${KIDE_OIDC_INTROSPECTION_URL}" KIDE_OIDC_CLIENT_ID="${KIDE_OIDC_CLIENT_ID}" KIDE_OIDC_ISSUER="${KIDE_OIDC_ISSUER}" KIDE_OIDC_AUDIENCE="${KIDE_OIDC_AUDIENCE}" KIDE_PRIMARY_PRINCIPAL="${KIDE_PRIMARY_PRINCIPAL}" KIDE_OIDC_SECRET_NAME="${KIDE_OIDC_SECRET_NAME}" bash deploy/gcp/configure-auto-deploy.sh

echo
echo "Starting the first deployment from branch '${DEPLOY_BRANCH}'..."
gcloud builds triggers run "${TRIGGER_NAME}"   --project "${PROJECT_ID}"   --region "${TRIGGER_REGION}"   --branch "${DEPLOY_BRANCH}"

wait_for_live_services
