package com.kide.languageserver.gateway;

import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

public final class OidcIntrospectionConfig implements AutoCloseable {
    private final URI endpoint;
    private final String clientId;
    private char[] clientSecret;
    private final String expectedIssuer;
    private final String requiredAudience;
    private final Duration requestTimeout;
    private boolean closed;

    public OidcIntrospectionConfig(
            URI endpoint,
            String clientId,
            char[] clientSecret,
            String expectedIssuer,
            String requiredAudience,
            Duration requestTimeout) {
        this.endpoint = requireHttps(endpoint);
        this.clientId = required(clientId, "clientId");
        if (clientSecret == null || clientSecret.length == 0) {
            throw new IllegalArgumentException("clientSecret is required");
        }
        this.clientSecret = clientSecret.clone();
        this.expectedIssuer = required(expectedIssuer, "expectedIssuer");
        this.requiredAudience = required(requiredAudience, "requiredAudience");
        this.requestTimeout = Objects.requireNonNull(requestTimeout, "requestTimeout");
        if (requestTimeout.isZero() || requestTimeout.isNegative()) {
            throw new IllegalArgumentException("requestTimeout must be positive");
        }
    }

    public URI endpoint() { return endpoint; }
    public String clientId() { return clientId; }
    public String expectedIssuer() { return expectedIssuer; }
    public String requiredAudience() { return requiredAudience; }
    public Duration requestTimeout() { return requestTimeout; }

    public synchronized char[] clientSecretCopy() {
        if (closed) throw new IllegalStateException("OIDC introspection config is closed");
        return clientSecret.clone();
    }

    @Override
    public synchronized void close() {
        if (closed) return;
        Arrays.fill(clientSecret, '\0');
        clientSecret = new char[0];
        closed = true;
    }

    @Override
    public String toString() {
        return "OidcIntrospectionConfig[endpoint=" + endpoint
                + ", clientId=" + clientId + ", clientSecret=[REDACTED]]";
    }

    private static URI requireHttps(URI uri) {
        Objects.requireNonNull(uri, "endpoint");
        if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null) {
            throw new IllegalArgumentException("OIDC introspection endpoint must use HTTPS");
        }
        return uri;
    }

    private static String required(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required");
        return value;
    }
}
