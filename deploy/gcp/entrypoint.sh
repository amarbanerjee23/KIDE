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
  if [[ -z "${KIDE_PRIMARY_PRINCIPAL:-}" ]]; then
    echo "KIDE_PRIMARY_PRINCIPAL or KIDE_ROLE_BINDINGS is required" >&2
    exit 2
  fi
  role_bindings="${KIDE_PRIMARY_PRINCIPAL}|ADMINISTRATOR|${project_id}"
fi

allowed_origins="${KIDE_ALLOWED_ORIGINS:-}"

export KIDE_API_BIND=127.0.0.1
export KIDE_API_PORT=18081
export KIDE_API_WORKSPACE_ROOT="${WORKSPACE_ROOT}"
export KIDE_API_PROJECT_ROOT="${PROJECT_ROOT}"
export KIDE_API_ROLE_BINDINGS="${role_bindings}"
export KIDE_API_TRUST_FORWARDED_PROTO=true
export KIDE_API_TRUSTED_PROXY_ADDRESSES="127.0.0.1,::1"
export KIDE_API_ALLOWED_ORIGINS="${allowed_origins}"

export KIDE_GATEWAY_BIND=127.0.0.1
export KIDE_GATEWAY_PORT=18082
export KIDE_GATEWAY_WORKSPACE_ROOT="${WORKSPACE_ROOT}"
export KIDE_GATEWAY_PROJECT_ROOT="${PROJECT_ROOT}"
export KIDE_GATEWAY_ROLE_BINDINGS="${role_bindings}"
export KIDE_GATEWAY_TRUST_FORWARDED_PROTO=true
export KIDE_GATEWAY_TRUSTED_PROXY_ADDRESSES="127.0.0.1,::1"
export KIDE_GATEWAY_ALLOWED_ORIGINS="${allowed_origins}"
export KIDE_GATEWAY_IDLE_SECONDS="${KIDE_GATEWAY_IDLE_SECONDS:-3300}"

export KIDE_GLSP_BIND=127.0.0.1
export KIDE_GLSP_PORT=18083
export KIDE_GLSP_WORKSPACE_ROOT="${WORKSPACE_ROOT}"
export KIDE_GLSP_PROJECT_ROOT="${PROJECT_ROOT}"
export KIDE_GLSP_ROLE_BINDINGS="${role_bindings}"
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

if ! kill -0 "${lsp_pid}" 2>/dev/null; then
  cat /tmp/kide-lsp.log >&2
  exit 2
fi
if ! kill -0 "${glsp_pid}" 2>/dev/null; then
  cat /tmp/kide-glsp.log >&2
  exit 2
fi

envsubst '$PORT' < /opt/kide-cloud/nginx.conf.template > /tmp/nginx.conf
nginx -c /tmp/nginx.conf -g 'daemon off;' &
nginx_pid=$!

echo "KIDE CLOUD RUN READY port=${PORT} workspaceId=${workspace_id}"
wait -n "${api_pid}" "${lsp_pid}" "${glsp_pid}" "${nginx_pid}"
exit_code=$?

echo "A KIDE Cloud Run process exited unexpectedly" >&2
for log in /tmp/kide-api.log /tmp/kide-lsp.log /tmp/kide-glsp.log; do
  if [[ -f "${log}" ]]; then
    echo "----- ${log} -----" >&2
    tail -n 200 "${log}" >&2
  fi
done
exit "${exit_code}"
