# Collaborative Engineering and Review

PR33 adds project-scoped collaboration without changing KIDE's canonical model semantics.

## Boundaries

- `.dml`, `.capability`, `.mncspec`, `.op` and `.activity` project files remain the canonical engineering truth.
- Presence is ephemeral and expires automatically.
- Review change sets and comments are auxiliary project metadata under `.kide/collaboration/review-state-v1.json`.
- A review proposal never overwrites a model merely because a browser has a newer local buffer.
- Every apply still commits through the existing revision-aware `ModelRepository`.
- Text and diagram clients continue to use the same Xtext/EMF semantic core.

## Presence

Authenticated project users can join a project collaboration session. The browser heartbeats the session and reports the currently selected model. Sessions expire after inactivity and a browser can rejoin the same opaque UUID after a transient disconnect.

Presence does not grant editing rights. Authorization is evaluated independently for every model and review operation.

## Conflict review

Browser autosave remains optimistic:

1. the browser sends the exact ETag from which the edit began;
2. a stale ETag returns HTTP 409 and canonical content is not changed;
3. the local buffer is captured as a review change set with status `CONFLICT`;
4. the review workbench shows the current server revision next to the proposal;
5. the author explicitly edits a resolved proposal and rebases it against the current ETag;
6. the change set returns to `DRAFT`.

There is deliberately no generic character-level merge for semantic model files.

## Review lifecycle

The lifecycle is `DRAFT -> READY -> APPROVED -> APPLIED`. A stale proposal starts as `CONFLICT` and must be explicitly rebased to `DRAFT`.

- Engineers author/rebase change sets and mark them ready.
- Reviewers can comment and independently approve but do not gain model-write permission.
- Unresolved comments block readiness/approval.
- The author cannot approve their own change set.
- Applying an approved change set still requires model-write permission and the exact current ETag. A concurrent change therefore fails with HTTP 409 instead of overwriting it.

## Persistence and safety

Review state uses bounded JSON metadata with atomic replacement. The collaboration store rejects symbolic-link metadata locations, malformed state, duplicate/orphan records, oversized proposed models and oversized comments.

Presence is not persisted because it represents live activity rather than project history. Review state is persisted so browser disconnect/rejoin or service reconstruction does not discard comments or proposals.

## Audit

The enterprise audit ledger records presence join/leave and review change-set create, rebase, ready, approve, apply, comment-create and comment-update events. Heartbeat/list polling is intentionally not written to the audit ledger to avoid high-volume noise.

## Qualification

The packaged enterprise API self-check exercises two separately authorized principals through a concurrent-edit scenario:

- two-user presence;
- disconnect and deterministic rejoin;
- stale autosave conflict;
- side-by-side conflict review;
- explicit rebase;
- reviewer comment;
- unresolved-comment approval rejection;
- comment resolution;
- author approval denial;
- independent approval;
- revision-safe apply;
- canonical model verification;
- persisted review-state reload;
- audit-chain verification.

The web tests additionally qualify the collaboration client boundary and presence rejoin behavior.
