# Enterprise HTTP service runtime

PR26 turns the PR20 `/api/v1` contract into a real server boundary that browser
and service clients can consume without moving domain semantics into TypeScript.

## Packaging boundary

The server bundle is included only in the headless KIDE service product. It is
not added to the Eclipse desktop feature. Desktop therefore remains a standalone
client while the browser uses a separately deployable service runtime.

## Security

Production startup requires OIDC bearer-token introspection. The introspection
implementation now lives in `com.kide.enterprise.identity` and is shared by the
HTTP API and the LSP WebSocket gateway.

The API:

- requires secure transport by default;
- accepts trusted forwarded HTTPS only from an explicit proxy allowlist;
- supports an explicit browser-origin allowlist;
- enforces PR18 server-side authorization after authentication;
- emits bounded PR20 error envelopes rather than provider/stack diagnostics;
- applies a bounded request-body limit; and
- propagates a UUID request ID in every handled response.

The loopback insecure mode exists only for local qualification and is rejected on
non-loopback production binds.

## Implemented PR26 surfaces

PR26 intentionally implements only semantics that already exist in the shared
core:

- `GET /api/v1/health`;
- deterministic OpenAPI publication at `GET /api/v1/openapi.json`;
- project listing and project read for the configured E04 context, including the
  stable E04 workspace ID needed to select the authorized LSP workspace;
- revision-safe model read/write through PR21 `ModelRepository`; and
- PR19 audit events for successful project/model operations.

Project creation, model indexing, knowledge, synthesis and evidence indexing are
not mocked. Until their owning PRs land, those contract surfaces return a typed
`SERVICE_UNAVAILABLE` response. This keeps the web client honest and prevents a
second implementation of KIDE semantics.

## Production configuration

Required context/authentication settings:

- `KIDE_API_WORKSPACE_ROOT`
- `KIDE_API_PROJECT_ROOT`
- `KIDE_API_ROLE_BINDINGS=principal|ROLE|scopeId;...`
- `KIDE_OIDC_INTROSPECTION_URL`
- `KIDE_OIDC_CLIENT_ID`
- `KIDE_OIDC_CLIENT_SECRET`
- `KIDE_OIDC_ISSUER`
- `KIDE_OIDC_AUDIENCE`

Transport controls include `KIDE_API_BIND`, `KIDE_API_PORT`,
`KIDE_API_ALLOWED_ORIGINS`, `KIDE_API_TRUST_FORWARDED_PROTO` and
`KIDE_API_TRUSTED_PROXY_ADDRESSES`.

## Qualification

CI executes `com.kide.enterprise.server.selfcheck` from the packaged headless
product. The self-check proves health, unauthenticated denial, authorized project
read, revision-safe model write/read, stale-write conflict, typed unavailable
future services and audit-chain integrity.
