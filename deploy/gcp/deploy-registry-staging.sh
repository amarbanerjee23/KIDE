#!/usr/bin/env bash
# PR102 — Deploy an isolated, pinned-image, *no-traffic* NFS registry candidate.
# Never run this script against the existing legacy kide production service.
# All inputs are operator-approved; this does not provision Filestore, migrate
# source models, or promote the candidate to the stable serving revision.
set -euo pipefail

: "${PROJECT_ID:?Dedicated staging GCP project required}"
: "${REGION:?Cloud Run region required}"
: "${STAGING_SERVICE:?Set a separate -registry-staging service}"
: "${STAGING_BACKEND_IMAGE:?Pinned backend image digest required}"
: "${STAGING_FIREBASE_PROJECT_ID:?Dedicated staging Firebase project required}"
: "${STAGING_RUNTIME_SA:?Dedicated staging runtime service account required}"
: "${NFS_IP:?Private Filestore/NFS address required}"
: "${NFS_EXPORT:?NFS export path required}"
: "${VPC_NETWORK:?Staging private VPC network required}"
: "${VPC_SUBNET:?Staging private VPC subnet required}"
: "${STAGING_WEB_ORIGIN:?Approved staging Web HTTPS origin required}"

if [[ "${DEPLOY_APPROVAL:-}" != "isolated-staging-only" ||
      "${STORAGE_PREFLIGHT_APPROVAL:-}" != "staging-only-offline" ]]; then
  echo "Require explicit isolated staging and offline filesystem qualification approval." >&2
  exit 2
fi

python3 scripts/staging_registry_policy.py \
  --service "${STAGING_SERVICE}" \
  --nfs-ip "${NFS_IP}" \
  --nfs-export "${NFS_EXPORT}" \
  --image "${STAGING_BACKEND_IMAGE}" \
  --network "${VPC_NETWORK}" \
  --subnet "${VPC_SUBNET}" \
  --firebase-project "${STAGING_FIREBASE_PROJECT_ID}"

if ! [[ "${STAGING_WEB_ORIGIN}" =~ ^https://[a-zA-Z0-9.-]+$ ]]; then
  echo "Staging Web origin must be a credential-free HTTPS hostname." >&2
  exit 2
fi

# An IAM denial, API failure or timeout must never be interpreted as no existing
# service. Never change the legacy release target or automatic deployment trigger.
services="$(gcloud run services list --project "${PROJECT_ID}" \
  --region "${REGION}" --format='value(metadata.name)')"
if grep -Fxq "${STAGING_SERVICE}" <<< "${services}"; then
  if [[ "${STAGING_UPDATE_APPROVAL:-}" != "reviewed-same-nfs-backend" ]]; then
    echo "Existing staging service found; explicit reviewed update approval required." >&2
    exit 2
  fi
  gcloud run services describe "${STAGING_SERVICE}" \
    --project "${PROJECT_ID}" --region "${REGION}" --format=json |
    python3 scripts/staging_registry_policy.py verify-existing \
      --nfs-ip "${NFS_IP}" --nfs-export "${NFS_EXPORT}" \
      --firebase-project "${STAGING_FIREBASE_PROJECT_ID}"
fi

# Cloud Run NFS mounts do NOT provide file locking. Max instance=1 does not
# prevent concurrent old revisions during promotion, so deployment is tagged
# and receives *zero normal traffic*. A write-frozen/manual traffic transition
# and rollback plan are separate operator actions.
gcloud run deploy "${STAGING_SERVICE}" \
  --project "${PROJECT_ID}" --region "${REGION}" \
  --image "${STAGING_BACKEND_IMAGE}" \
  --service-account "${STAGING_RUNTIME_SA}" \
  --network "${VPC_NETWORK}" --subnet "${VPC_SUBNET}" \
  --vpc-egress private-ranges-only \
  --allow-unauthenticated --ingress all \
  --port 8080 \
  --cpu 2 --memory 4Gi --concurrency 1 --timeout 3600s \
  --min 0 --max 1 \
  --startup-probe "httpGet.path=/health,httpGet.port=8080,initialDelaySeconds=10,timeoutSeconds=5,periodSeconds=10,failureThreshold=60" \
  --add-volume "mount-path=/data,type=nfs,location=${NFS_IP}:${NFS_EXPORT},readonly=false" \
  --set-env-vars "^|^KIDE_STORAGE_MODE=registry-posix|KIDE_HOSTED_PROJECTS_ROOT=/data/hosted-projects|KIDE_FIREBASE_PROJECT_ID=${STAGING_FIREBASE_PROJECT_ID}|KIDE_ALLOWED_ORIGINS=${STAGING_WEB_ORIGIN}|KIDE_FIREBASE_OWNER_ONLY_ENGINEERS=true|KIDE_SHARED_FIREBASE_ENGINEERS=false" \
  --tag registry-candidate --no-traffic \
  --labels "kide-component=registry-staging,kide-storage=posix" \
  --quiet

# Service revisions are immutable; deployment above does not direct the
# existing primary traffic to the candidate. Operators must verify tagged
# endpoint read/write isolation against authorized disposable staging data,
# then separately approve a cutover with all old writers quiesced.
gcloud run services describe "${STAGING_SERVICE}" \
  --project "${PROJECT_ID}" --region "${REGION}" \
  --format='value(status.traffic)'
echo "Staging candidate deployed without normal traffic. Real NFS restart,"
echo "owner grants, API/LSP/GLSP and application cutover are NOT yet qualified."
