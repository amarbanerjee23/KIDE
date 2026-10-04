#!/usr/bin/env bash
set -euo pipefail

IMAGE="${1:?usage: qualify-cloud-run-backend.sh IMAGE}"
HOST_PORT="${KIDE_CI_HOST_PORT:-18080}"
BASE_URL="http://127.0.0.1:${HOST_PORT}"

container_id="$(docker run -d \
  -p "${HOST_PORT}:8080" \
  -e PORT=8080 \
  -e KIDE_FIREBASE_PROJECT_ID=kide-ci \
  -e KIDE_FIREBASE_ADMIN_UID=ci-administrator \
  "${IMAGE}")"

cleanup() {
  docker logs "${container_id}" || true
  docker rm -f "${container_id}" >/dev/null 2>&1 || true
}
trap cleanup EXIT

container_running() {
  docker inspect --format='{{.State.Running}}' "${container_id}" \
    2>/dev/null | grep -qx true
}

wait_for_http() {
  local url="$1"
  local attempts="$2"
  for _ in $(seq 1 "${attempts}"); do
    if curl --fail --silent --show-error "${url}" >/dev/null 2>&1; then
      return 0
    fi
    if ! container_running; then
      echo "Cloud Run image exited while waiting for ${url}" >&2
      return 1
    fi
    sleep 1
  done
  echo "Timed out waiting for ${url}" >&2
  return 1
}

wait_for_http "${BASE_URL}/health" 120

health_body="$(curl --fail --silent --show-error "${BASE_URL}/health")"
api_health_body="$(curl --fail --silent --show-error "${BASE_URL}/api/v1/health")"
version_body="$(curl --fail --silent --show-error "${BASE_URL}/api/v1/version")"
root_body="$(curl --fail --silent --show-error "${BASE_URL}/")"

python3 - "${root_body}" "${health_body}" "${api_health_body}" "${version_body}" <<'PY'
import json
import sys

root = json.loads(sys.argv[1])
health = json.loads(sys.argv[2])
api_health = json.loads(sys.argv[3])

assert root == {
    "service": "kide-backend",
    "status": "UP",
    "health": "/health",
    "api": "/api/v1",
}, root

for name, payload in (("healthz", health), ("api/v1/health", api_health)):
    assert payload.get("status") == "UP", (name, payload)
    assert payload.get("version") == "v1", (name, payload)
PY

unknown_status="$(curl --silent --output /dev/null --write-out '%{http_code}' \
  "${BASE_URL}/definitely-not-a-kide-route")"
if [[ "${unknown_status}" != "404" ]]; then
  echo "Unknown backend route returned HTTP ${unknown_status}; expected 404" >&2
  exit 1
fi

root_content_type="$(curl --silent --head "${BASE_URL}/" \
  | tr -d '\r' \
  | awk 'BEGIN{IGNORECASE=1} /^Content-Type:/{print $2; exit}')"
if [[ "${root_content_type}" != application/json* ]]; then
  echo "Backend root Content-Type is '${root_content_type}', expected application/json" >&2
  exit 1
fi

fully_ready=0
for _ in $(seq 1 180); do
  container_logs="$(docker logs "${container_id}" 2>&1)"
  if grep -q 'KIDE CLOUD RUN READY' <<< "${container_logs}"; then
    fully_ready=1
    break
  fi
  if ! container_running; then
    echo "Cloud Run image exited before full KIDE readiness" >&2
    exit 1
  fi
  sleep 1
done

if [[ "${fully_ready}" -ne 1 ]]; then
  echo "Cloud Run image became HTTP healthy but did not reach full KIDE readiness" >&2
  exit 1
fi

echo "KIDE Cloud Run backend qualification passed."
