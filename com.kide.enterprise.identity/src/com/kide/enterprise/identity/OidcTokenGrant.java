package com.kide.enterprise.identity;

import java.util.Objects;

public record OidcTokenGrant(ValidatedOidcIdentity identity, AuthTokens tokens) {
    public OidcTokenGrant {
        Objects.requireNonNull(identity, "identity");
        Objects.requireNonNull(tokens, "tokens");
    }
}
