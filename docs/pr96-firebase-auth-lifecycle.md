# PR96 — Firebase authentication session-race hardening

## Problem and threat boundary

KIDE Web stores Firebase ID/refresh tokens only in memory. Before PR96, multiple API calls could simultaneously refresh an expiring ID token using the same refresh token, which Firebase rotates. A failed second refresh could sign out an otherwise healthy engineering session. Worse, a delayed successful response after sign-out, registration, or switching accounts could restore an obsolete account or overwrite the newer identity.

The React shell also cleared its editor contents on sign-out without immediately removing its selected Project state, and a delayed API handshake could repopulate stale projects after logout.

## Implementation

- **Single-flight refresh:** simultaneous calls to `idToken()` share one HTTP request. Only a complete and consistent refresh response atomically changes the in-memory ID token, refresh token and expiry.
- **Monotonic session versions:** signing in, registering or signing out increments a generation. Every pending password-auth or refresh response checks that generation before changing the identity. Stale responses reject as superseded without logging out a newer session.
- **Identity binding:** refresh success must return the same Firebase UID; unexpected identity or invalid response fails closed. A real provider rejection clears that session; a transient network/transport failure, HTTP 429 or provider HTTP 5xx retains its identity but never supplies an expired token.
- **UI generation checks:** delayed sign-in/register continuations and API health/version/project discovery cannot update the UI after logout or a different account has been established. Sign-out clears active project state and authorizations, not just Monaco editor resources.
- **Refresh timer:** ignores results from older operations, retries transient failures, and clears active workspace when Firebase itself invalidates the current session. Switching accounts closes the prior workspace before the new sign-in begins.

## Verification

`npm test` from `web/` exercises concurrent refresh, delayed refresh after sign-out, account switch with late failed refresh, delayed password sign-in, out-of-order password sign-ins, foreign-UID refresh rejection and retry after temporary provider outage. The full KIDE browser/desktop/Cloud Run workflows must pass before merge.

The change does not persist tokens in browser storage, weaken Firebase authorization, bypass KIDE ENGINEER role grants, or claim external production sign-in testing. Live Firebase/Cloud Run acceptance is a separate deployment gate.
