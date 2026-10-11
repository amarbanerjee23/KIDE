# PR95 — Idempotent collaboration presence recovery

## Observed behavior

PR94 blocked stale WebSocket/presence callbacks, but the two-project browser regression counted two **POST /collaboration/sessions** calls. PR95's request-stack trace established the precise cause: the test mocked the collection URL `/sessions` but not the generated heartbeat resource URL `/sessions/{id}`. Playwright's `*` glob does not match a slash; the unhandled PUT therefore fell through to the development server and returned 404. The existing client legitimately rejoined an apparently expired session. **The second join was caused by the test fixture, not evidence of a production duplicate-join defect.** This PR explicitly mocks session-resource PUT/DELETE and restores a strict exactly-one-join acceptance test.

Separately, recovery after genuine transient listing/heartbeat failures previously destroyed the active coordinator and issued a new POST during the periodic retry. A 503/timeout or Firebase authorization 401/403 **must not be treated as proof the server forgot the session**. Only a 404 on an actual active-session heartbeat authorizes a rejoin.

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
