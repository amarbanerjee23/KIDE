# PR95 — Idempotent collaboration presence recovery

## Observed behavior

PR94 blocked stale project WebSocket/presence callbacks but its two-project browser regression showed more than one **actual POST /collaboration/sessions** on the current project. The active project became Online, but a degraded presence listing led the Web IDE to dispose the current coordinator and POST another join on the next periodic recovery tick.

A presence list or heartbeat failure can be transient. A 503/timeout or a Firebase authorization 401/403 **must not be interpreted as proof that the server forgot the session**. Only a 404 for the active session on a heartbeat is evidence that a rejoin is needed.

## Recovery semantics

- The client retains ownership of a single presence session per browser coordinator. Concurrent calls to `connect()` use one pending join; an already active session is reused rather than rejoined.
- Overlapping `setModel`, timer heartbeat and manual recovery calls share one in-flight heartbeat. Failed presence list reads retain the current session and can be retried without creating another server session.
- `recover()` first heartbeats the current session. Success restores Connected and refreshes peer presence. HTTP 404 on heartbeat permits one rejoin with the prior session ID; HTTP 401/403 and transient errors report Degraded without POSTing another session.
- The App's periodic recovery first tries its current project's coordinator. It replaces a coordinator only if no active/pending session for that project exists.
- Existing PR94 generation fences, sign-out cleanup, and project ownership checks remain intact. A disposed join cannot recreate stale presence.

## Qualification

- `web/tests/collaboration.test.ts` exercises duplicate in-flight connect calls, failure/recovery of presence reads, overlapping recover heartbeats, HTTP 404 rejoin, forbidden heartbeat handling, late joins after disconnect and late presence reads.
- `web/e2e/project-switch-race.spec.ts` expects **exactly one** POST for the active project and zero joins to the superseded project during a stalled Xtext handshake.
- Build KIDE runs Web TypeScript checks, Vitest, and Playwright browser qualification. The Eclipse plugin and Cloud Run workflows guard shared-service packaging.

Only project-authorized users can connect. None of these changes changes or weakens RBAC, grants Firebase accounts additional project access, or claims live Cloud Run connections were validated.
