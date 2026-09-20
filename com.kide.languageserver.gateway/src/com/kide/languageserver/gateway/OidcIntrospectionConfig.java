package com.kide.languageserver.gateway;

import java.net.URI;
import java.time.Duration;

/**
 * Compatibility facade. Authentication semantics live in the shared enterprise
 * identity bundle so REST and WebSocket transports use one implementation.
 */
public final class OidcIntrospectionConfig implements AutoCloseable {
    private final com.kide.enterprise.identity.OidcIntrospectionConfig delegate;

    public OidcIntrospectionConfig(
            URI endpoint,
            String clientId,
            char[] clientSecret,
            String expectedIssuer,
            String requiredAudience,
            Duration requestTimeout) {
        delegate = new com.kide.enterprise.identity.OidcIntrospectionConfig(
                endpoint, clientId, clientSecret, expectedIssuer, requiredAudience, requestTimeout);
    }

    com.kide.enterprise.identity.OidcIntrospectionConfig delegate() { return delegate; }
    public URI endpoint() { return delegate.endpoint(); }
    public String clientId() { return delegate.clientId(); }
    public String expectedIssuer() { return delegate.expectedIssuer(); }
    public String requiredAudience() { return delegate.requiredAudience(); }
    public Duration requestTimeout() { return delegate.requestTimeout(); }
    public char[] clientSecretCopy() { return delegate.clientSecretCopy(); }

    @Override
    public void close() { delegate.close(); }

    @Override
    public String toString() { return delegate.toString(); }
}
