#!/usr/bin/env bash
set -euo pipefail

MAVEN_VERSION="3.9.16"
MAVEN_SHA512="831a8591fe20c8243b1dbe7d71e3244f31d1665b0804b2e825e38cbbe5ce0cafb8338851f90780735568773e0a6cd07bbec107cda0b896b008b861075358b6f6"
MAVEN_ARCHIVE="apache-maven-${MAVEN_VERSION}-bin.tar.gz"
MAVEN_URL="https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/${MAVEN_ARCHIVE}"

CACHE_ROOT="${KIDE_MAVEN_CACHE_ROOT:-${HOME}/.cache/kide-maven}"
MAVEN_HOME="${CACHE_ROOT}/apache-maven-${MAVEN_VERSION}"
ARCHIVE_PATH="${CACHE_ROOT}/${MAVEN_ARCHIVE}"

if [[ ! -x "${MAVEN_HOME}/bin/mvn" ]]; then
  mkdir -p "${CACHE_ROOT}"
  tmp="${ARCHIVE_PATH}.tmp"
  rm -f "${tmp}"
  curl --fail --location --silent --show-error --retry 3 --output "${tmp}" "${MAVEN_URL}"
  printf '%s  %s\n' "${MAVEN_SHA512}" "${tmp}" | sha512sum --check --status
  mv "${tmp}" "${ARCHIVE_PATH}"
  rm -rf "${MAVEN_HOME}"
  tar -xzf "${ARCHIVE_PATH}" -C "${CACHE_ROOT}"
fi

actual_version="$("${MAVEN_HOME}/bin/mvn" --version | head -n 1)"
if [[ "${actual_version}" != "Apache Maven ${MAVEN_VERSION}"* ]]; then
  echo "ERROR: expected Apache Maven ${MAVEN_VERSION}, got: ${actual_version}" >&2
  exit 2
fi

exec "${MAVEN_HOME}/bin/mvn" "$@"
