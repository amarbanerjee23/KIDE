# Secure LSP WebSocket gateway

PR22 exposes KIDE's existing five-language Xtext LSP runtime to browser/server
clients through an authenticated WebSocket boundary. It does not duplicate DSL
semantics in the gateway.

## Endpoint and session model

The gateway maps:

`/lsp?workspaceId=<stable E04 workspace ID>`

Each accepted WebSocket receives exactly one:

- authenticated principal session;
- E04 organization/portfolio/project/workspace lineage;
- PR18 authorization decision;
- bounded message/rate/idle policy; and
- isolated Xtext language-server runtime.

Workspace names and filesystem paths are not authorization identifiers. The
`workspaceId` must resolve to the exact stable E04 workspace registered by the
server.

WebSocket TEXT payloads contain normal JSON-RPC/LSP JSON. The gateway translates
between WebSocket messages and the `Content-Length` framing expected by Xtext's
existing LSP runtime. No parser, validator, completion, navigation or other DSL
semantics are reimplemented in the transport.

### Browser workspace URIs

PR31 adds a browser-safe URI virtualization boundary. Browser clients use
`kide-workspace:/<project-relative-path>` and never need the service host's
filesystem path. Before forwarding a message to Xtext, the gateway maps the
virtual URI onto the already-authorized project root and then runs the same
canonical path/symlink checks described below. Responses, diagnostics, locations
and workspace-edit URI keys are mapped back to `kide-workspace:` before they
cross the WebSocket.

Native clients may continue to use authorized `file:` URIs. Virtual URI
translation does not broaden the workspace boundary.

## Authentication

Production gateway startup uses OAuth 2/OIDC token introspection over HTTPS.

Required environment settings:

- `KIDE_OIDC_INTROSPECTION_URL`
- `KIDE_OIDC_CLIENT_ID`
- `KIDE_OIDC_CLIENT_SECRET`
- `KIDE_OIDC_ISSUER`
- `KIDE_OIDC_AUDIENCE`

Native/service clients may authenticate with `Authorization: Bearer ...`.

Browser WebSocket APIs cannot set arbitrary Authorization headers. Direct browser
clients therefore request the public subprotocol `kide.lsp.v1` plus a credential
subprotocol `kide.bearer.<base64url(access-token)>`. The server decodes the
credential for the same introspection path but negotiates only `kide.lsp.v1`
back to the browser; the credential value is never placed in the WebSocket URL or
echoed as the negotiated protocol. Reverse proxies must treat
`Sec-WebSocket-Protocol` as sensitive request metadata and must not log its raw
value.

Introspection must return an active token, the configured issuer and audience, a
subject, and a future expiry. The access token exists only in the authentication
session and is zeroized through the PR17 token container when the WebSocket closes.

KIDE does not implement custom JWT signature parsing in this gateway. Provider-side
introspection remains the trust boundary.

## Authorization

The server loads an existing E04 context from:

- `KIDE_GATEWAY_WORKSPACE_ROOT`
- `KIDE_GATEWAY_PROJECT_ROOT`

Role bindings are explicit:

`KIDE_GATEWAY_ROLE_BINDINGS=principal|ROLE|scopeId;principal|ROLE|scopeId`

Every scope ID must belong to the configured E04 lineage. Before the HTTP upgrade
is accepted, the server applies both PR18 WebSocket-workspace and LSP-workspace
authorization gates. The same authorization gates are re-evaluated for every
inbound WebSocket/LSP message, so a policy revision or role revocation takes
effect on an already-open session rather than waiting for reconnect/token expiry.
Missing authentication, unknown workspace, cross-context scope IDs and missing
permissions fail closed.

## Transport security and reverse proxies

Secure transport is required by default.

For TLS terminated at a trusted reverse proxy, set
`KIDE_GATEWAY_TRUST_FORWARDED_PROTO=true` and configure
`KIDE_GATEWAY_TRUSTED_PROXY_ADDRESSES` as a comma-separated list of the proxy
peer IP addresses permitted to assert transport security. Forwarded-proto trust
is rejected at startup if this allowlist is empty. The proxy must overwrite, not
append or pass through, `X-Forwarded-Proto`. The request is accepted only when
the immediate peer is allowlisted and the trusted value is `https`.

For local development/self-check only,
`KIDE_GATEWAY_ALLOW_INSECURE_LOOPBACK=true` is accepted only when the gateway
bind is loopback. It is rejected on non-loopback binds.

`KIDE_GATEWAY_ALLOWED_ORIGINS` may contain a comma-separated origin allowlist.
An empty allowlist does not add an origin restriction; production browser
deployments should set it explicitly.

## Project filesystem boundary

The selected E04 workspace is bound at gateway startup to one canonical project
root. Before any JSON-RPC payload is forwarded to Xtext, KIDE recursively checks
LSP URI/path fields such as `rootUri`, `rootPath`, `textDocument.uri`,
workspace-folder URIs and rename target URIs.

Only `file:` URIs resolving inside the authorized project are accepted. The
boundary normalizes paths and resolves the nearest existing ancestor with
`toRealPath()`, so lexical `..` traversal, sibling projects, external paths,
non-file URI schemes and symlink escapes are rejected before the language server
can access them.

## Resource controls

Defaults:

- maximum WebSocket text message: 1 MiB;
- idle timeout: 5 minutes;
- message rate: 2400/minute per session; and
- concurrent authenticated LSP sessions: 128 (`KIDE_GATEWAY_MAX_SESSIONS`).

All are bounded and configurable. A connection above the server-wide session
quota is rejected during the HTTP upgrade with 429 before an Xtext runtime is
created. Oversized messages close with WebSocket 1009; rate violations close with
1008. The LSP transport is text-only; binary WebSocket
messages are explicitly completed and closed with RFC 6455 code 1003 rather than
being left unread. The gateway uses Jetty's own message-size and idle controls in
addition to KIDE's explicit policy.

The LSP runtime is in-process and per-session; no operating-system child language
server process is spawned, so closing a WebSocket cannot leave an orphan LS
process. Session streams and threads are explicitly closed/interrupted.

## Packaged qualification

The headless KIDE product executes
`com.kide.languageserver.gateway.selfcheck` in CI. It:

1. provisions a real E04 context and PR18 role binding;
2. proves an unauthenticated WebSocket upgrade is denied;
3. opens an authenticated browser-style WebSocket using the credential subprotocol;
4. proves revoking the live role binding closes the already-open socket;
5. proves an LSP initialize rooted outside the authorized project is rejected;
6. proves binary WebSocket payloads are rejected with code 1003;
7. performs LSP initialize;
8. opens and changes documents for DML, Operation, MNC, Capability and Activity;
9. requires diagnostics for every opened language;
10. forwards a `$/cancelRequest` notification;
11. performs LSP shutdown/exit;
12. reconnects and repeats initialize/shutdown; and
13. shuts down the Jetty gateway and all in-process LSP sessions.

This packaged test runs from the customer-consumable headless product, not from a
source-only test harness.
