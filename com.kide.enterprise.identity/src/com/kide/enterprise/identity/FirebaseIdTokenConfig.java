package com.kide.enterprise.identity;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;
import java.util.Map;

/** Server-side Firebase ID-token verification configuration. */
public final class FirebaseIdTokenConfig {
    public static final URI GOOGLE_CERTIFICATES_URI = URI.create(
            "https://www.googleapis.com/robot/v1/metadata/x509/"
                    + "securetoken@system.gserviceaccount.com");

    private final String projectId;
    private final Duration requestTimeout;
    private final boolean requireVerifiedEmail;

    public FirebaseIdTokenConfig(String projectId, Duration requestTimeout) {
        this(projectId, requestTimeout, false);
    }

    public FirebaseIdTokenConfig(String projectId, Duration requestTimeout,
            boolean requireVerifiedEmail) {
        this.projectId = required(projectId, "projectId");
        this.requireVerifiedEmail = requireVerifiedEmail;
        this.requestTimeout = Objects.requireNonNull(requestTimeout, "requestTimeout");
        if (requestTimeout.isZero() || requestTimeout.isNegative()) {
            throw new IllegalArgumentException("requestTimeout must be positive");
        }
    }

    public String projectId() { return projectId; }

    public boolean requireVerifiedEmail() { return requireVerifiedEmail; }

    /**
     * Owner-only onboarding is a *verified-identity + durable-registry* mode,
     * never a grant to the legacy singleton project or to all Firebase users.
     * Every hosted HTTP, LSP and GLSP entrypoint uses this exact validator.
     */
    public static FirebaseIdTokenConfig fromEnvironment(
            String projectId, Duration timeout, Map<String, String> env) {
        Objects.requireNonNull(env, "env");
        String owner = env.getOrDefault("KIDE_FIREBASE_OWNER_ONLY_ENGINEERS", "false");
        if (!"true".equals(owner) && !"false".equals(owner)) {
            throw new IllegalArgumentException(
                    "KIDE_FIREBASE_OWNER_ONLY_ENGINEERS must be true or false");
        }
        if ("true".equals(owner)) {
            if (!"registry-posix".equals(env.get("KIDE_STORAGE_MODE")) ||
                    !"/data/hosted-projects".equals(env.get("KIDE_HOSTED_PROJECTS_ROOT"))) {
                throw new IllegalArgumentException(
                        "Verified owner-only Firebase access requires qualified POSIX registry storage");
            }
            if (!"false".equalsIgnoreCase(
                    env.getOrDefault("KIDE_SHARED_FIREBASE_ENGINEERS", "false"))) {
                throw new IllegalArgumentException(
                        "Owner-only Firebase access is incompatible with shared-project authorization");
            }
        }
        return new FirebaseIdTokenConfig(projectId, timeout, "true".equals(owner));
    }

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
