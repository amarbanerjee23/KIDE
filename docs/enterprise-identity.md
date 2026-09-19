# Enterprise principal identity and authentication

PR17 introduces the client-neutral identity boundary used by KIDE desktop, browser and future shared services.

## Principal model

`PrincipalIdentity` represents one authenticated actor. Principals are explicitly typed as:

- human;
- service account; or
- local/offline.

The principal contains stable identity metadata only. Access tokens, refresh tokens, client secrets and similar credentials are rejected from the claims map.

## OIDC boundary

KIDE does not parse SAML or implement its own JWT signature validation. Enterprise SAML remains behind the organization's OIDC/SAML identity-provider boundary.

`OidcTransport` is the security boundary between KIDE and an OIDC implementation. A transport may use an enterprise OIDC SDK, reverse proxy or browser/server integration, but it must return a `ValidatedOidcIdentity` only after the provider response has passed issuer, audience, signature, nonce and protocol validation appropriate to the flow.

The KIDE core supplies:

- browser authorization-code + PKCE adapter;
- desktop authorization-code + PKCE adapter;
- desktop device-flow adapter;
- service-account/client-credentials adapter;
- local/offline principal adapter.

## Credential handling

`AuthTokens`, `PendingAuthorization` and `DeviceAuthorization` own mutable credential buffers and are `AutoCloseable`. Their string representations are redacted. Closing them overwrites their internal arrays.

Provider/transport exception chains are not propagated through the public authentication API because provider diagnostics can contain credential fragments. Callers receive typed, sanitized `AuthenticationException` messages.

Authentication material is not persisted by this bundle and no project/workspace file API is used. Long-lived desktop secret material should continue to be provisioned through the enterprise configuration/secure-storage boundary rather than copied into project metadata.

## Session behavior

A session fails closed when:

- its token expiry time has passed;
- PKCE state does not match;
- device authorization has expired;
- refresh fails;
- logout/revocation fails; or
- validated identity data is missing/malformed.

Refresh replaces the session only after a valid refreshed grant is returned. A failed refresh closes the old local session. Logout always closes local credentials even when remote revocation fails.

Offline mode creates an explicit `LOCAL_OFFLINE` principal with no tokens. Offline identity is not equivalent to an enterprise-authenticated human and is intended for standalone/local workflows only.

## Qualification

`com.kide.enterprise.identity.tests` covers expiry, PKCE state validation, service-account separation, offline mode, refresh/logout failure, sensitive-claim rejection and credential redaction.

The packaged desktop product also runs `com.kide.enterprise.identity.selfcheck` in CI, proving that the shipped identity bundle resolves and starts from customer-consumable product bytes.
