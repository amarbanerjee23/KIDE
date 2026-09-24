#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "$0")" && pwd)"
APP="${SCRIPT_DIR}/KIDE.app"
NATIVE_LAUNCHER="${APP}/Contents/MacOS/kide"

if [[ ! -d "${APP}" || ! -x "${NATIVE_LAUNCHER}" ]]; then
  echo "KIDE.app is incomplete or its native launcher is not executable." >&2
  exit 2
fi

exec "${NATIVE_LAUNCHER}" "$@"
