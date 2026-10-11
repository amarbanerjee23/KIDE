# PR102 — Safe hosted registry storage and deployment-mode boundaries

**Status:** staged implementation pending real GCP/NFS and application acceptance.
No automatic conversion of production Cloud Storage FUSE data is performed.

## Summary

KIDE's legacy production backend uses Cloud Storage FUSE at `/data`. The
registry implementation in PR87/88 requires atomic directory rename, a
no-replace publication boundary and durable model/workspace metadata.
Cloud Storage FUSE does not supply the documented POSIX contract; exposing
project creation on that mount could corrupt multi-project onboarding.

Cloud Run supports NFS/Filestore mounting, but **does not support NFS file
locking**; it mounts without locks. A single `--max 1` instance is
not fencing of old revisions, split-brain instances or external writers.
For that reason PR102 introduces a *separate staging candidate*, rather
than a production switch, and does not advertise multi-project GA.

Google documentation:
- [NFS volumes for Cloud Run services](https://docs.cloud.google.com/run/docs/configuring/services/nfs-volume-mounts)
- [Direct VPC egress for Cloud Run](https://docs.cloud.google.com/run/docs/configuring/vpc-direct-vpc)

## Permanent fail-closed rules

1. **Ordinary deployments remain legacy GCS FUSE.**
   `scripts/guard_hosted_storage.py` inspects the running Cloud Run backend
   before every normal Cloud Build deploy. It refuses to overwrite a service
   configured with `KIDE_STORAGE_MODE=registry-posix`,
   `KIDE_HOSTED_PROJECTS_ROOT` or an NFS/unknown storage volume.
   Permission errors in service inspection fail closed; only true absent
   services are eligible for the original bootstrap.
2. **Registry mode requires an actual approved POSIX staging mount.**
   The container startup refuses a registry unless
   `KIDE_STORAGE_MODE=registry-posix`,
   `KIDE_HOSTED_PROJECTS_ROOT=/data/hosted-projects`, the mounted directory
   exists and is writable, and the sibling
   `/data/.kide-registry-staging-qualified` contains the approved sentinel.
   A missing NFS mount must not create a new anonymous legacy project.
3. **No static legacy authorization in registry mode.**
   The entrypoint does not create a parallel project/workspace descriptor
   or inject the single-project allowlist into REST, LSP or GLSP.
   Registry identity and persisted per-project owner grants remain
   authoritative.
4. **No implicit migration or rollout.**
   The new staging deploy script rejects the legacy production name,
   unpinned images, public NFS addresses and changed NFS exports; it deploys
   a tagged, no-traffic revision.
5. Existing desktop/application semantic tests, live Firebase checks and
   stable-release policy are unchanged.

## Staging provisioning procedure (operator-only)

1. Use an isolated GCP staging project and Firebase staging project,
   with dedicated least-privilege runtime SA, private Direct VPC network,
   subnet and Filestore/NFS share. Ensure firewall allows the Cloud Run VPC
   source to access private NFS. Create an empty `hosted-projects`
   directory inside that share. Give **UID/GID 10001** read/write access to
   the share and directory. Do not reuse a production data export.
2. **Stop or block all writers**, then mount the *exact same* export on a
   staging maintenance Linux host. Qualify atomic rename and no-replace:

   ```bash
   python3 scripts/qualify_registry_storage.py \
     --root /mnt/staging-share/hosted-projects \
     --approval staging-only-offline
   ```

   Expected response: `QUALIFIED_FOR_ISOLATED_STAGING`, with filesystem
   `nfs` / `nfs4` (or an approved POSIX staging filesystem), and the
   sentinel stored in the share's parent. If the filesystem lacks required
   atomic/no-replace operations, the preflight fails and registry mode
   **must not be enabled**.

3. Build and qualify the backend image using existing KIDE CI. Resolve the
   image's immutable `@sha256:` digest. Then supply *operator-approved*
   environment variables, never from web user input:

   ```bash
   export PROJECT_ID="dedicated-staging-gcp-project"
   export REGION="asia-south1"
   export STAGING_SERVICE="kide-registry-staging"
   export STAGING_BACKEND_IMAGE="asia-south1-docker.pkg.dev/stage/kide/backend@sha256:<64-hex-digest>"
   export STAGING_FIREBASE_PROJECT_ID="kide-staging"
   export STAGING_RUNTIME_SA="kide-staging-runtime@dedicated-staging-gcp-project.iam.gserviceaccount.com"
   export NFS_IP="10.20.0.5"
   export NFS_EXPORT="/vol1"
   export VPC_NETWORK="staging-private"
   export VPC_SUBNET="staging-subnet"
   export STAGING_WEB_ORIGIN="https://staging-web.example.org"
   export DEPLOY_APPROVAL="isolated-staging-only"
   export STORAGE_PREFLIGHT_APPROVAL="staging-only-offline"

   bash deploy/gcp/deploy-registry-staging.sh
   ```

   For an already existing staging registry service, explicitly approve
   `STAGING_UPDATE_APPROVAL=reviewed-same-nfs-backend`. The script verifies
   that its NFS endpoint and registry root have not changed.

4. The candidate uses `--no-traffic --tag registry-candidate`, one maximum
   instance and one concurrent HTTP request per instance. This is **not**
   sufficient to guarantee a single writer across revisions; do not direct
   traffic to old and new revisions simultaneously. Do not promote or
   attach production users to the candidate by default.
5. Provision an isolated staging Web frontend targeted at the candidate
   revision and configure its Firebase staging credentials; its URL must
   match `STAGING_WEB_ORIGIN`. Then run PR101's authenticated connectivity
   and PR89's *real* multi-principal staging acceptance; use real fresh
   staging projects and ensure creation, REST/Xtext/GLSP, model storage,
   owner grants, synthesis, generation and stale-revision rejection all work
   without intercepted responses.

## Migration design — explicitly not automatic

**No source or role grant is migrated by this PR.** Legacy GCS FUSE and
registry POSIX paths have different storage and identity semantics. An
approved production migration must:

- Freeze writes and capture a verified, encrypted, offline backup with
  source project/workspace E04 IDs, all canonical model bytes, revisions,
  knowledge and collaboration evidence.
- Copy/migrate into an isolated POSIX target, never overwrite a registered
  destination; map each authorized legacy engineer to a specific
  project-scoped role, preserving correct least privilege.
- Verify project ownership, negative-tenant access, LSP/GLSP project
  discovery, synthesis and generation before and after a restart.
- Obtain explicit storage and security sign-off; only then cut over one
  service/traffic endpoint with old writers quiesced.
- Retain a verified read-only legacy fallback until the migration window is
  closed; restoring live customer data from an old snapshot may lose
  acknowledged new writes and requires an approved RPO decision.

## Rollback and promotion restrictions

Record before deployment: service name, current active revision,
immutable image digest, storage mode, NFS export ID, verified backup
fingerprint, authorized Firebase principal inventory, RPO/RTO limits, and
the operator incident/approval ticket.

**Before any traffic switch**, ensure no old container/writer can still
mutate the same NFS export. Traffic percentage=0 is not evidence an old
request has finished. Stop external clients and verify quiescence, then
exercise the candidate on an isolated endpoint. Rollback consists of a
deliberate Cloud Run revision/endpoint switch *only if* storage schema and
acknowledged data remain compatible; otherwise use the separately tested
offline restore and obtain data-loss approval. Do not automatically route
production traffic as a reaction to a failed health check.

## Required acceptance evidence not available to repository-only CI

- Exact NFS/Filestore export, filesystem type and offline qualification
- Real Cloud Run startup with the approved sentinel, non-root write access,
  and no ghost legacy project
- Real project creation and persisted creator authorization after container
  restart/new revision and two-principal cross-project denial
- Interrupted-write/autosave/concurrent engineer conflict behavior
- Cloud Run traffic freeze and tested rollback including role identity
- Verified backup/restore on this actual POSIX storage class
- Cloud Monitoring alert routing and measured load/RPO/RTO

No operator GCP state is modified by opening this PR. A green CI run alone is
not proof that those live acceptance conditions passed.
