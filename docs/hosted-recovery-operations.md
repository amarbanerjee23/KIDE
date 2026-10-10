# PR90 — Hosted project durability and recovery operations

**Scope:** hosted registry data, including every registered project's canonical
models, model-repository transaction journals, embedded knowledge, knowledge
traces, collaboration/review state, E04 identity, and creator authorization
metadata. The Eclipse desktop application remains separate and unchanged.

**Deployment posture:** tooling and automated synthetic drills exist, but
production durability and recovery remain **UNQUALIFIED** until an operator
performs real isolated staging recovery on the target storage class, records
RPO/RTO measurements and verifies ownership and application behavior.

## 1. Storage architecture and restrictions

- The deployed Cloud Run service currently uses a single-writer Google Cloud
  Storage volume mounted at \`/data\`. **Cloud Storage FUSE must not be assumed
  to have POSIX atomic rename, directory durability, or recovery guarantees.**
- PR88 multi-project creation uses an atomic directory rename, and this PR's
  offline restore publishes an entire registry through a Linux atomic
  no-replace directory rename. Use a POSIX filesystem supporting both operations,
  or keep project creation and restore disabled.
- Model repository transaction journals protect individual interrupted writes.
  They do **not** replace consistent full-project backups or remote copies.
- Snapshots contain project source, metadata, and potentially proprietary
  customer data. Backups are created with mode 0600, but ZIP compression is
  **not encryption**. Encrypt before off-site upload, use per-environment keys,
  private access controls, retention, an inventory of backup objects and
  immutable/object-versioned retention where available.
- The snapshot tool intentionally enforces finite resource limits (50,000 files,
  256 MiB total uncompressed contents, 32 MiB per file) so large deployments
  fail closed rather than exhausting memory. A streaming, larger-scale backup
  implementation must be qualified separately before increasing these bounds.

## 2. Offline backup (strict maintenance window)

1. Announce maintenance, refuse new writes, and stop **all** REST, LSP, GLSP
   processes and any out-of-process collaborators using the registry. A Cloud Run
   min-instances setting of 1 can restart a stopped service; disable serving
   traffic/writers through an approved maintenance procedure first. Do not
   assume that flipping the banner in the Web UI stops model writes.
2. On a Linux machine that has exclusive access to the *same POSIX registry
   volume*, verify the registry's path and that no writer is running.
3. Store the backup in a private, local directory **outside the registry**:

\`\`\`bash
python3 scripts/hosted_snapshot.py snapshot \
  --registry /srv/kide/hosted-projects \
  --archive /srv/kide-private-backups/hosted-20261010-001.zip \
  --offline-confirmed

python3 scripts/hosted_snapshot.py verify \
  --archive /srv/kide-private-backups/hosted-20261010-001.zip
\`\`\`

4. Require matching \`fingerprint\`, project/file counts and \`PASS\`. This
   checks exact bytes, SHA-256 per file, enterprise context hierarchy and
   UUID/workspace identity uniqueness. A modification detected during capture
   aborts and removes the incomplete temporary ZIP.
5. Encrypt and copy the **verified** ZIP to separately administered off-site
   storage; record object version, encryption key ID, SHA-256 of encrypted
   backup, retention expiry, project count and operator audit ticket.
6. Resume only after validating maintenance release. Without a coordinated
   writer freeze, these snapshots **cannot** be declared transaction-consistent,
   even if the double-scan check happened to pass.

Never put backups in the Web static bundle, build artifacts or public GitHub
Actions logs. Do not schedule \`snapshot\` against a live registry.

## 3. Restore and isolated drill

1. Create an **isolated Linux POSIX destination** on the same type of storage
   used for your proposed staging deployment. Verify it is **not** a path to
   live customer projects or an existing production mount.
2. Stop all readers and writers that could discover the restore target.
3. Fetch/decrypt the private backup onto a protected machine; validate it:

\`\`\`bash
python3 scripts/hosted_snapshot.py verify \
  --archive /srv/kide-private-backups/hosted-20261010-001.zip

python3 scripts/hosted_snapshot.py restore \
  --archive /srv/kide-private-backups/hosted-20261010-001.zip \
  --registry /srv/kide-restored/hosted-projects \
  --offline-confirmed
\`\`\`

The target registry **must not exist**. The tool will not overwrite a live or
empty existing directory. It validates the complete ZIP before extraction,
rejects traversal, duplicate names, symlinks, non-regular records, inconsistent
tenant identities and byte hashes, extracts into a sibling staging directory,
rechecks the restored tree, then publishes via Linux
\`renameat2(RENAME_NOREPLACE)\`. If that operation is unsupported, restore
fails closed without publishing the target.

4. Start an isolated copy of KIDE REST/LSP/GLSP configured to the restored
   registry. Validate exact project/workspace IDs, creator grant permissions,
   all intended models and ETags, knowledge/synthesis, deterministic code
   generation, GLSP and Xtext. Verify a second principal cannot see or mutate
   another owner's project.
5. Record **actual** elapsed recovery time (RTO), the backup timestamp compared
   with last committed data (RPO), checksums and owner-grant replay. Compare
   against **explicitly approved** business targets; this PR does not invent
   RTO/RPO numbers.
6. Never replace a production registry in place as a shortcut. Cutover should
   use an operator-approved endpoint/volume switch with rollback, controls to
   prevent split-brain writers and a documented incident ticket.

The Python unit suite runs a two-owner synthetic round trip and adversarial
cases (tampering, link injection, unsafe archive entries, destination refusal,
writer mutation, missing offline approval). That CI evidence does **not**
substitute for a restore exercise on the real staging storage class.

## 4. Live service monitoring

\`scripts/hosted_probe.py\` makes read-only, unauthenticated HTTPS calls to
\`/api/v1/health\`, \`/api/v1/version\`, Web \`/health\` (Nginx plain text
\`ok\`) and \`/kide-version.json\`. The CLI checks backend readiness, Web
readiness, same build ID and per-request latency, and prints a JSON result.
It never accepts user credentials in URLs, does not access customer models and
exits 2 on unhealthy/drift/timeout.

\`\`\`bash
python3 scripts/hosted_probe.py \
  --api https://staging-api.example.org \
  --web https://staging-web.example.org \
  --max-latency-ms 5000
\`\`\`

The \`Hosted Runtime Health Watch\` workflow is opt-in. Set both GitHub
repository variables \`KIDE_MONITOR_API_ORIGIN\` and
\`KIDE_MONITOR_WEB_ORIGIN\` to known HTTPS service origins. It runs at
minutes 13 and 43 of each hour on the repository default branch and fails
on unsuccessful probes. **If variables are unset, the job is skipped; that is
not successful monitoring.** GitHub Actions failure reporting is not an
incident paging/alerting integration. Set up independent Cloud Monitoring
uptime checks, logs-based error alerts, 5xx/error-rate, storage capacity,
write latency, CPU/memory, and an audited on-call response chain before GA.

## 5. Operational acceptance checklist (must be separately signed)

- [ ] A dedicated staging deployment supports atomic publish + restore;
      creation stays disabled elsewhere
- [ ] Tested maintenance/writer freeze, without split-brain or Cloud Run auto restart
- [ ] Verified encrypted, off-site, access-limited backups and retention
- [ ] Recorded successful staging restore with exact model bytes/ETags and durable owner grants
- [ ] Verified post-restore REST, LSP, GLSP, collaboration, synthesis and codegen
- [ ] Measured RPO/RTO against agreed business objectives
- [ ] Enabled independent uptime checks, response paging and storage/latency alerts
- [ ] Ran bounded workload testing for expected concurrent users and documented bottlenecks
- [ ] Assigned rollback, incident-response and restore operator responsibilities

**PR90 is a testable foundation for operations, not a claim of commercial GA
or disaster-recovery readiness.**
