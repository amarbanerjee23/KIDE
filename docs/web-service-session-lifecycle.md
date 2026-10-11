# PR94 — Web engineering service session lifecycle

## Failure mode

Previously the Web IDE could issue concurrent Xtext connections when opening a project, refreshing Firebase credentials or retrying a Cloud Run gateway. A slow previous LSP initialize could complete after a newer project connected and overwrite the active Monaco controller, producing incorrect Online states or wrong-workspace operations. A collaboration join or heartbeat could similarly complete after disconnect and resurrect stale presence. A signed-in API health response is not sufficient evidence of a working Xtext/editor connection.

## Corrected lifecycle

- Every Xtext, collaboration and project-open attempt receives a monotonically increasing generation ID. A superseded request may finish on the network, but it must not register a controller, update active presence, overwrite project models/knowledge traces, or display a stale status.
- A project switch, sign-out or failed session invalidates previous generations. Existing Xtext WebSockets are aborted immediately, including CONNECTING handshakes and pending JSON-RPC requests; the app no longer waits up to the RPC shutdown timeout to open the next project.
- The Xtext transport emits a disconnect signal on real WebSocket close/error; only the active workspace can change the status to Degraded. Existing periodic retries reconnect using the current authorized Firebase identity and workspace ID, not stale credentials.
- A Firebase token refresh reconnects Xtext only if the active connection is using the old token. Sign-out invalidates pending refresh completions.
- A collaboration session that finishes joining after disposal is explicitly left and does not start a timer. In-flight presence reads/heartbeats are ignored if the session was disposed; the active project keeps the only live presence connection.
- A late project model read or knowledge trace response is discarded when the project was replaced.

## Regression qualification

Run npm test from web for unit tests covering cancelled handshakes, rejected pending JSON-RPC calls, delayed collaboration joins, presence-list disposal and existing RPC capabilities. Run npm run test:e2e for browser tests covering a stalled Xtext handshake while switching between two distinct authorized projects; only the final project's editor, LSP and presence should become Online.

The existing PR92 authorization boundary remains: ungranted Firebase users cannot open hosted projects, and a healthy REST API alone does not authorize Xtext/GLSP. The browser integration test uses service mocks; live Cloud Run dual-user/staging acceptance is still required separately to claim production reliability.
