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
authorization gates. Missing authentication, unknown workspace, cross-context
scope IDs and missing permissions fail closed.

## Transport security and reverse proxies

Secure transport is required by default.

For TLS terminated at a trusted reverse proxy, set
`KIDE_GATEWAY_TRUST_FORWARDED_PROTO=true` and configure the proxy to overwrite,
not append or pass through, `X-Forwarded-Proto`. The request is accepted only
when the trusted value is `https`.

For local development/self-check only,
`KIDE_GATEWAY_ALLOW_INSECURE_LOOPBACK=true` is accepted only when the gateway
bind is loopback. It is rejected on non-loopback binds.

`KIDE_GATEWAY_ALLOWED_ORIGINS` may contain a comma-separated origin allowlist.
An empty allowlist does not add an origin restriction; production browser
deployments should set it explicitly.

## Resource controls

Defaults:

- maximum WebSocket text message: 1 MiB;
- idle timeout: 5 minutes;
- message rate: 2400/minute per session.

All are bounded and configurable. Oversized messages close with WebSocket 1009;
rate violations close with 1008. The gateway uses Jetty's own message-size and
idle controls in addition to KIDE's explicit policy.

The LSP runtime is in-process and per-session; no operating-system child language
server process is spawned, so closing a WebSocket cannot leave an orphan LS
process. Session streams and threads are explicitly closed/interrupted.

## Packaged qualification

The headless KIDE product executes
`com.kide.languageserver.gateway.selfcheck` in CI. It:

1. provisions a real E04 context and PR18 role binding;
2. proves an unauthenticated WebSocket upgrade is denied;
3. opens an authenticated browser-style WebSocket using the credential subprotocol;
4. performs LSP initialize;
5. opens and changes documents for DML, Operation, MNC, Capability and Activity;
6. requires diagnostics for every opened language;
7. forwards a `$/cancelRequest` notification;
8. performs LSP shutdown/exit;
9. reconnects and repeats initialize/shutdown; and
10. shuts down the Jetty gateway and all in-process LSP sessions.

This packaged test runs from the customer-consumable headless product, not from a
source-only test harness.
