package com.kide.languageserver.gateway;

import java.net.http.HttpClient;
import java.time.Clock;
import java.util.Objects;

import com.kide.enterprise.identity.AuthenticatedSession;

/** Compatibility adapter over the shared enterprise identity authenticator. */
public final class OidcIntrospectionAuthenticator implements GatewayAuthenticator {
    private final com.kide.enterprise.identity.OidcIntrospectionAuthenticator delegate;

    public OidcIntrospectionAuthenticator(
            OidcIntrospectionConfig config,
            HttpClient client,
            Clock clock) {
        Objects.requireNonNull(config, "config");
        delegate = new com.kide.enterprise.identity.OidcIntrospectionAuthenticator(
                config.delegate(), client, clock);
    }

    @Override
    public AuthenticatedSession authenticateAuthorizationHeader(String authorizationHeader) {
        return delegate.authenticateAuthorizationHeader(authorizationHeader);
    }
}
