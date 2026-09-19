# Enterprise model repository and revision transactions

PR21 introduces the shared model repository contract used by desktop and future
server/web clients.

## Canonical state

Project model files remain canonical. Local repository metadata is stored only under
`.kide/model-repository` and never replaces the engineering source files.

Each model snapshot contains:

- a validated project-relative model path;
- a monotonically advancing local revision number; and
- a SHA-256 ETag derived from exact file bytes.

ETags provide the cross-client concurrency contract. Clients must submit the ETag
they read when writing or deleting an existing model. A stale ETag fails with
`RevisionConflictException` before any mutation is applied.

## Transaction model

All mutations occur through `ModelTransaction`.

A transaction can stage reads, writes and deletes, then either commit or roll back.
The repository enforces a bounded mutation count and a maximum individual model
size. Closing an uncommitted transaction rolls it back.

The server reference repository implements the same optimistic-concurrency
semantics in memory so service/API code can consume the exact same contract.
A future durable server store can replace its backing implementation without
changing transaction behavior.

## File-backed recovery

`FileModelRepository` stages mutations below
`.kide/model-repository/transactions`.

Before project files are touched it records:

- the intended paths;
- whether each target existed;
- backups of existing targets;
- staged replacement bytes; and
- the previous revision metadata.

The journal is marked `COMMITTING` before canonical files are changed. If the
process stops before the commit is completed, repository initialization restores
the backed-up files and metadata. A journal marked `COMMITTED` is cleanup-only.

This makes interrupted local mutations recoverable without relying on source
control or silently accepting a partially written project.

## Filesystem security

Model paths are relative, normalized and cannot address `.kide` internals or
escape with parent traversal. Existing symbolic links in a model path are rejected,
as are symbolic-link model targets. Files larger than the configured model bound
are rejected before mutation.

## Qualification

The PR21 test bundle covers:

- lost-update prevention;
- stale ETag conflicts;
- explicit rollback;
- commit/restart persistence;
- simulated interrupted-commit recovery;
- path traversal and symbolic-link rejection;
- large-file rejection;
- transactional delete; and
- desktop/server byte and ETag round-trip parity.

The packaged desktop product also runs
`com.kide.enterprise.modelrepo.selfcheck`, proving optimistic concurrency and
restart behavior from shipped product bytes.
