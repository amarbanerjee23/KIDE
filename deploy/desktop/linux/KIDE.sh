#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
NATIVE_LAUNCHER="${SCRIPT_DIR}/kide"

if [[ ! -x "${NATIVE_LAUNCHER}" ]]; then
  echo "KIDE native Linux launcher is missing or not executable: ${NATIVE_LAUNCHER}" >&2
  exit 2
fi

exec "${NATIVE_LAUNCHER}" "$@"
