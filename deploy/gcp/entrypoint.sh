#!/usr/bin/env bash
set -euo pipefail

PORT="${PORT:-8080}"
DATA_ROOT="${KIDE_DATA_ROOT:-/data}"
WORKSPACE_ROOT="${KIDE_WORKSPACE_ROOT:-${DATA_ROOT}/workspace}"
PROJECT_ROOT="${KIDE_PROJECT_ROOT:-${DATA_ROOT}/project}"
PROJECT_DESCRIPTOR="${PROJECT_ROOT}/.kide/enterprise-context.properties"
WORKSPACE_DESCRIPTOR="${WORKSPACE_ROOT}/.metadata/.plugins/com.kide.enterprise.context/workspace.properties"

uuid() {
  cat /proc/sys/kernel/random/uuid
}

property_value() {
  local key="$1"
  local file="$2"
  sed -n "s/^${key}=//p" "$file" | head -n 1
}

mkdir -p "${WORKSPACE_ROOT}" "${PROJECT_ROOT}"

if [[ ! -f "${PROJECT_DESCRIPTOR}" ]]; then
  organization_id="kide:organization:$(uuid)"
  portfolio_id="kide:portfolio:$(uuid)"
  project_id="kide:project:$(uuid)"
  mkdir -p "$(dirname "${PROJECT_DESCRIPTOR}")"
  cat > "${PROJECT_DESCRIPTOR}" <<EOF
schema.version=1
organization.id=${organization_id}
organization.name=${KIDE_ORGANIZATION_NAME:-KIDE Cloud}
portfolio.id=${portfolio_id}
portfolio.name=${KIDE_PORTFOLIO_NAME:-Default Portfolio}
project.id=${project_id}
project.name=${KIDE_PROJECT_NAME:-KIDE Project}
EOF
fi

organization_id="$(property_value organization.id "${PROJECT_DESCRIPTOR}")"
portfolio_id="$(property_value portfolio.id "${PROJECT_DESCRIPTOR}")"
project_id="$(property_value project.id "${PROJECT_DESCRIPTOR}")"

if [[ -z "${organization_id}" || -z "${portfolio_id}" || -z "${project_id}" ]]; then
  echo "KIDE cloud bootstrap failed: enterprise project descriptor is incomplete" >&2
  exit 2
fi

if [[ ! -f "${WORKSPACE_DESCRIPTOR}" ]]; then
  workspace_id="kide:workspace:$(uuid)"
  mkdir -p "$(dirname "${WORKSPACE_DESCRIPTOR}")"
  cat > "${WORKSPACE_DESCRIPTOR}" <<EOF
schema.version=1
organization.id=${organization_id}
portfolio.id=${portfolio_id}
project.id=${project_id}
workspace.id=${workspace_id}
workspace.name=${KIDE_WORKSPACE_NAME:-Cloud Workspace}
EOF
fi

workspace_id="$(property_value workspace.id "${WORKSPACE_DESCRIPTOR}")"
if [[ -z "${workspace_id}" ]]; then
  echo "KIDE cloud bootstrap failed: enterprise workspace descriptor is incomplete" >&2
  exit 2
fi

if [[ -n "${KIDE_ROLE_BINDINGS:-}" ]]; then
  role_bindings="${KIDE_ROLE_BINDINGS}"
else
  primary_principal="${KIDE_PRIMARY_PRINCIPAL:-}"
  if [[ -z "${primary_principal}" ]]; then
    if [[ -z "${KIDE_FIREBASE_PROJECT_ID:-}" || -z "${KIDE_FIREBASE_ADMIN_UID:-}" ]]; then
      echo "KIDE_FIREBASE_PROJECT_ID and KIDE_FIREBASE_ADMIN_UID (or KIDE_ROLE_BINDINGS) are required" >&2
      exit 2
    fi
    primary_principal="firebase:${KIDE_FIREBASE_PROJECT_ID}#${KIDE_FIREBASE_ADMIN_UID}"
  fi
  role_bindings="${primary_principal}|ADMINISTRATOR|${project_id}"
fi

# Operator-approved Firebase engineer UIDs. These are explicit allowlist entries,
# never wildcard grants, and only apply to the legacy single-project runtime.
# A newly registered Firebase account is NOT automatically authorized.
engineer_uids="${KIDE_FIREBASE_ENGINEER_UIDS:-}"
if [[ -n "${engineer_uids}" ]]; then
  if [[ -n "${KIDE_HOSTED_PROJECTS_ROOT:-}" ]]; then
    echo "KIDE_FIREBASE_ENGINEER_UIDS cannot grant registry-backed project access" >&2
    exit 2
  fi
  if [[ -n "${KIDE_ROLE_BINDINGS:-}" ]]; then
    echo "Use KIDE_ROLE_BINDINGS alone when supplying explicit role grants" >&2
    exit 2
  fi
  if [[ -z "${KIDE_FIREBASE_PROJECT_ID:-}" ]]; then
    echo "Firebase project ID is required to resolve engineer identities" >&2
    exit 2
  fi
  IFS=':' read -r -a approved_uids <<< "${engineer_uids}"
  # Reject invalid/empty segments, duplicate entries and overlarge grant sets.
  if [[ "${#approved_uids[@]}" -gt 32 || "${engineer_uids}" == :* || "${engineer_uids}" == *: || "${engineer_uids}" == *::*
     ]]; then
    echo "Invalid KIDE_FIREBASE_ENGINEER_UIDS allowlist" >&2
    exit 2
  fi
  declare -A seen_engineers=()
  for uid in "${approved_uids[@]}"; do
    if [[ ! "${uid}" =~ ^[A-Za-z0-9_-]{1,128}$ || -n "${seen_engineers[${uid}]:-}" ]]; then
      echo "Invalid or duplicate Firebase engineer UID" >&2
      exit 2
    fi
    seen_engineers["${uid}"]=1
    principal="firebase:${KIDE_FIREBASE_PROJECT_ID}#${uid}"
    if [[ "${principal}" != "${primary_principal}" ]]; then
      role_bindings+=";${principal}|ENGINEER|${project_id}"
    fi
  done
fi

allowed_origins="${KIDE_ALLOWED_ORIGINS:-}"

export KIDE_API_BIND=127.0.0.1
export KIDE_API_PORT=18081
export KIDE_API_MAX_REQUEST_BYTES="${KIDE_API_MAX_REQUEST_BYTES:-16777216}"
export KIDE_API_WORKSPACE_ROOT="${WORKSPACE_ROOT}"
export KIDE_API_PROJECT_ROOT="${PROJECT_ROOT}"
export KIDE_API_ROLE_BINDINGS="${role_bindings}"
export KIDE_API_TRUST_FORWARDED_PROTO=true
export KIDE_API_TRUSTED_PROXY_ADDRESSES="127.0.0.1,::1"
export KIDE_API_ALLOWED_ORIGINS="${allowed_origins}"
export KIDE_API_BOOTSTRAP_STARTER_KNOWLEDGE="${KIDE_API_BOOTSTRAP_STARTER_KNOWLEDGE:-true}"

export KIDE_GATEWAY_BIND=127.0.0.1
export KIDE_GATEWAY_PORT=18082
export KIDE_GATEWAY_WORKSPACE_ROOT="${WORKSPACE_ROOT}"
export KIDE_GATEWAY_PROJECT_ROOT="${PROJECT_ROOT}"
if [[ -n "${KIDE_HOSTED_PROJECTS_ROOT:-}" ]]; then
  # Registry projects use persisted creator grants. Legacy bootstrap binding
  # belongs to a different context and must not be injected into the gateways.
  export KIDE_GATEWAY_ROLE_BINDINGS="${KIDE_GATEWAY_ROLE_BINDINGS:-}"
  export KIDE_GLSP_ROLE_BINDINGS="${KIDE_GLSP_ROLE_BINDINGS:-}"
else
  export KIDE_GATEWAY_ROLE_BINDINGS="${role_bindings}"
fi
export KIDE_GATEWAY_TRUST_FORWARDED_PROTO=true
export KIDE_GATEWAY_TRUSTED_PROXY_ADDRESSES="127.0.0.1,::1"
export KIDE_GATEWAY_ALLOWED_ORIGINS="${allowed_origins}"
export KIDE_GATEWAY_IDLE_SECONDS="${KIDE_GATEWAY_IDLE_SECONDS:-3300}"

export KIDE_GLSP_BIND=127.0.0.1
export KIDE_GLSP_PORT=18083
export KIDE_GLSP_WORKSPACE_ROOT="${WORKSPACE_ROOT}"
export KIDE_GLSP_PROJECT_ROOT="${PROJECT_ROOT}"
if [[ -z "${KIDE_HOSTED_PROJECTS_ROOT:-}" ]]; then
  export KIDE_GLSP_ROLE_BINDINGS="${role_bindings}"
fi
export KIDE_GLSP_TRUST_FORWARDED_PROTO=true
export KIDE_GLSP_TRUSTED_PROXY_ADDRESSES="127.0.0.1,::1"
export KIDE_GLSP_ALLOWED_ORIGINS="${allowed_origins}"
export KIDE_GLSP_IDLE_SECONDS="${KIDE_GLSP_IDLE_SECONDS:-3300}"

for name in api lsp glsp; do
  rm -rf "/tmp/kide-config-${name}" "/tmp/kide-data-${name}"
  cp -a /opt/kide/configuration "/tmp/kide-config-${name}"
  mkdir -p "/tmp/kide-data-${name}"
done

start_application() {
  local name="$1"
  local app="$2"
  /opt/kide/kide-languageserver-headless \
    -nosplash \
    -consoleLog \
    -configuration "/tmp/kide-config-${name}" \
    -data "/tmp/kide-data-${name}" \
    -application "${app}" \
    >"/tmp/kide-${name}.log" 2>&1 &
  echo $!
}

api_pid="$(start_application api com.kide.enterprise.server.application)"
lsp_pid="$(start_application lsp com.kide.languageserver.gateway.application)"
glsp_pid="$(start_application glsp com.kide.glsp.application)"

cleanup() {
  set +e
  kill "${api_pid}" "${lsp_pid}" "${glsp_pid}" "${nginx_pid:-}" 2>/dev/null
  wait "${api_pid}" "${lsp_pid}" "${glsp_pid}" "${nginx_pid:-}" 2>/dev/null
}
trap cleanup EXIT INT TERM

# Cloud Run requires the container to bind to $PORT promptly. Start nginx
# before waiting for the internal Eclipse applications so the platform's
# startup probe can establish a TCP connection while API/LSP/GLSP finish
# booting. /health will naturally return an upstream error until the API is
# ready, and the build's live verification still waits for full readiness.
mkdir -p /tmp/nginx-client /tmp/nginx-proxy /tmp/nginx-fastcgi \
  /tmp/nginx-uwsgi /tmp/nginx-scgi
envsubst '$PORT' < /opt/kide-cloud/nginx.conf.template > /tmp/nginx.conf
nginx -c /tmp/nginx.conf -g 'daemon off;' &
nginx_pid=$!

for _ in $(seq 1 60); do
  if ! kill -0 "${api_pid}" 2>/dev/null; then
    cat /tmp/kide-api.log >&2
    exit 2
  fi
  if curl --fail --silent \
      -H 'X-Forwarded-Proto: https' \
      http://127.0.0.1:18081/api/v1/health >/dev/null; then
    break
  fi
  sleep 1
done

if ! curl --fail --silent \
    -H 'X-Forwarded-Proto: https' \
    http://127.0.0.1:18081/api/v1/health >/dev/null; then
  echo "KIDE API did not become ready" >&2
  cat /tmp/kide-api.log >&2
  exit 2
fi

wait_listener() {
  local name="$1"
  local pid="$2"
  local url="$3"
  local log="$4"
  for _ in $(seq 1 60); do
    if ! kill -0 "${pid}" 2>/dev/null; then
      echo "KIDE ${name} process exited before becoming ready" >&2
      cat "${log}" >&2
      return 1
    fi
    if curl --silent --output /dev/null --max-time 1 \
        -H 'X-Forwarded-Proto: https' "${url}"; then
      return 0
    fi
    sleep 1
  done
  echo "KIDE ${name} listener did not become ready" >&2
  cat "${log}" >&2
  return 1
}

wait_listener lsp "${lsp_pid}" \
  "http://127.0.0.1:18082/lsp?workspaceId=${workspace_id}" \
  /tmp/kide-lsp.log
wait_listener glsp "${glsp_pid}" \
  "http://127.0.0.1:18083/glsp?workspaceId=${workspace_id}" \
  /tmp/kide-glsp.log

echo "KIDE CLOUD RUN READY port=${PORT} workspaceId=${workspace_id}"
set +e
wait -n "${api_pid}" "${lsp_pid}" "${glsp_pid}" "${nginx_pid}"
exit_code=$?
set -e

echo "A KIDE Cloud Run process exited unexpectedly" >&2
for log in /tmp/kide-api.log /tmp/kide-lsp.log /tmp/kide-glsp.log; do
  if [[ -f "${log}" ]]; then
    echo "----- ${log} -----" >&2
    tail -n 200 "${log}" >&2
  fi
done
exit "${exit_code}"
