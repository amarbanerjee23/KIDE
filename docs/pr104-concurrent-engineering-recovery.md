# PR104 — Concurrent engineers, interrupted saves and durable recovery

**Implementation:** PR104 hardens the existing KIDE Web autosave coordinator,
the canonical Java `FileModelRepository`, and the offline hosted-registry
snapshot/restore tools. Production and staged service identities, roles,
Xtext/GLSP semantics and desktop/Web separation are unchanged.

## Defects fixed

1. **Newer Web text overwritten by failure of an older in-flight save.**
   Previously, after the server rejected a stale ETag or a network request
   failed, `AutosaveCoordinator.flush()` unconditionally assigned the older
   submitted content back to `pending`. An engineer's edits typed during the
   request could be lost in the unsaved buffer. PR104 preserves the newest
   pending content. A conflict callback receives that newest text and the
   actual rejected base ETag. There is **no blind retry on HTTP 409**:
   the user must reconcile against the current server revision.

2. **Cross-process lost update and unsafe journal replay.**
   `FileModelRepository` previously synchronized only on `this`.
   Separate Java repository objects and the REST/GLSP processes could
   validate the same expected revision, then both write a journal and
   clobber a model. PR104 wraps journal recovery and *the entire optimistic
   revision check through commit and rollback* in a filesystem-exclusive
   `.kide/model-repository/writer.guard` directory. Linux POSIX/NFS
   `mkdir` is used instead of unsupported Cloud Run NFS `flock`.
   A collision fails closed with a revision-conflict response, preserving
   the canonical model; it is **not** an authorization success or automatic
   retry. In-process package tests race eight independently instantiated
   repository sessions using the same expected ETag, require **exactly
   one** winner, restart and check committed bytes/revision.

3. **Backup accepting active or interrupted model transactions.**
   Both offline snapshot and archived-restore validation reject an active
   or stale `writer.guard` or an unresolved journal under
   `.kide/model-repository/transactions`. Restores cannot publish a
   filesystem tree containing an abandoned model mutation. Existing full
   SHA-256 bytes, creator grants, E04 metadata and hidden collaboration
   history remain preserved by the prior PR90 snapshot format.

## Staging-only bounded soak

The protected existing live acceptance journey now exercises *two distinct
HTTP sessions of the same authorized Firebase engineer* racing updates on
a disposable `pr104-concurrent.dml` model after completing synthesis,
generation and graphical editing acceptance. Both requests use the exact
same real server ETag. Each iteration requires HTTP statuses `200` and
`409`, read-after-write equality with the winning content/ETag, and
continued HTTP `403` for a second unauthorized Firebase principal.

`KIDE_LIVE_DURABILITY_ITERATIONS` defaults to **8** and is strictly bounded
to **1–32**. There is no parallel customer data mutation or production
traffic. Configure the protected staging workflow's original
`writes_approved=staging-only` and approved explicit Web/API hostnames;
run `npm run test:live` from `web/`. A CI test discovery pass is **not**
evidence that this real staging write test has run.

For genuine two-principal *same-project* engineering, the staging operator
must provision an explicit project-scoped ENGINEER grant to a second
principal. Creator-owned projects do **not** automatically grant access
to other Firebase users. A future signed acceptance record must include
a concurrently authorized two-person race, not just two sessions of one
principal.

## Crash / stale writer-guard recovery

The guard is intentionally **not a TTL lease**. It prevents a paused or
partitioned writer from losing ownership to another process. If a JVM
crashes while holding it, the directory remains, and new writes and
automatic journal replay fail closed. A normal Cloud Run restart alone
must never silently remove the guard or assume the NFS path is healthy.

Only an authorized maintenance operator may:

1. Stop **all** Cloud Run serving revisions and every external NFS writer,
   and verify old requests can no longer mutate the export. A max-instance
   setting or traffic=0 alone is insufficient fencing.
2. On the offline mount, inspect the exact
   `<project>/project/.kide/model-repository/writer.guard` directory and
   `transactions/` journal states; capture protected forensic evidence.
3. After confirming **zero live writers**, remove only the empty
   `writer.guard` directory using `rmdir`. Never recursively erase
   journals or restore a stale backup over acknowledged newer data.
4. Start exactly one KIDE repository process on isolated staging to let
   built-in journal recovery roll back uncommitted states and discard only
   `COMMITTED` journals. Re-run model ETag, content, authorization,
   Xtext/GLSP and synthesis read-after-restart acceptance.
5. Stop that process again; verify the writer guard is absent, journal
   directory has no unresolved entries and validate a full private backup
   via `scripts/hosted_snapshot.py snapshot/verify/restore` to a **new,
   isolated, empty** target. Resume writers only on explicit approval.

**Never delete a writer guard while any process might still own it.**
Doing so defeats exclusive writes and can lose customer revisions. Never
retry a 409 blindly using the stale ETag. Do not remove journals to make a
backup pass. Existing Git history and releases are not a backup.

## Remaining release blockers

- Real target-mounted Filestore/NFS staging verification must demonstrate
  atomic directory creation *across Cloud Run REST and GLSP JVMs* and a
  crashed-writer recovery drill. The normal CI tests run on ephemeral local
  filesystems and do not establish multi-host filesystem correctness.
- Two separately authorized principals racing and reviewing the same model;
  application UI conflict-resolution and autosave UX acceptance.
- An operator measured **RPO/RTO**, interrupted-write and restart/rollback
  timeline, consistent snapshot on frozen target storage, and external
  encrypted backup/restore. PR104 code cannot simulate these facts.
- Load/soak with real network partitions, Cloud Run simultaneous revisions,
  source durability SLOs and all-DSL artifact equivalence.

Do not mark production multi-engineer durability ready, enable automatic
restart-based lock stealing or claim HA merely because repository CI passes.
