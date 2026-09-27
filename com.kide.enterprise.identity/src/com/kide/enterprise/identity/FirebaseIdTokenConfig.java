package com.kide.enterprise.identity;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;

/** Server-side Firebase ID-token verification configuration. */
public final class FirebaseIdTokenConfig {
    public static final URI GOOGLE_CERTIFICATES_URI = URI.create(
            "https://www.googleapis.com/robot/v1/metadata/x509/"
                    + "securetoken@system.gserviceaccount.com");

    private final String projectId;
    private final Duration requestTimeout;

    public FirebaseIdTokenConfig(String projectId, Duration requestTimeout) {
        this.projectId = required(projectId, "projectId");
        this.requestTimeout = Objects.requireNonNull(requestTimeout, "requestTimeout");
        if (requestTimeout.isZero() || requestTimeout.isNegative()) {
            throw new IllegalArgumentException("requestTimeout must be positive");
        }
    }

    public String projectId() { return projectId; }

    public String expectedIssuer() {
        return "https://securetoken.google.com/" + projectId;
    }

    public URI certificatesUri() { return GOOGLE_CERTIFICATES_URI; }

    public Duration requestTimeout() { return requestTimeout; }

    private static String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        String trimmed = value.trim();
        if (trimmed.length() > 256 || trimmed.indexOf('/') >= 0
                || trimmed.indexOf(' ') >= 0) {
            throw new IllegalArgumentException(field + " is invalid");
        }
        return trimmed;
    }
}
