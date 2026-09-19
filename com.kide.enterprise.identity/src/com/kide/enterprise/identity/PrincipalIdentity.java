package com.kide.enterprise.identity;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class PrincipalIdentity {
    private final String id;
    private final String displayName;
    private final PrincipalKind kind;
    private final AuthenticationMethod method;
    private final String issuer;
    private final String subject;
    private final Map<String, String> claims;

    public PrincipalIdentity(String id, String displayName, PrincipalKind kind,
            AuthenticationMethod method, String issuer, String subject,
            Map<String, String> claims) {
        this.id = required(id, "id");
        this.displayName = required(displayName, "displayName");
        this.kind = Objects.requireNonNull(kind, "kind");
        this.method = Objects.requireNonNull(method, "method");
        this.issuer = issuer == null ? "" : issuer;
        this.subject = subject == null ? "" : subject;
        this.claims = sanitizeClaims(claims);
    }

    public String id() { return id; }
    public String displayName() { return displayName; }
    public PrincipalKind kind() { return kind; }
    public AuthenticationMethod method() { return method; }
    public String issuer() { return issuer; }
    public String subject() { return subject; }
    public Map<String, String> claims() { return claims; }

    @Override
    public String toString() {
        return "PrincipalIdentity[id=" + id + ", kind=" + kind + ", method=" + method + "]";
    }

    private static Map<String, String> sanitizeClaims(Map<String, String> source) {
        if (source == null || source.isEmpty()) return Map.of();
        Map<String, String> copy = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : source.entrySet()) {
            String key = required(entry.getKey(), "claim key");
            String normalized = key.toLowerCase(Locale.ROOT);
            if (normalized.contains("token") || normalized.contains("secret")
                    || normalized.contains("password") || normalized.contains("credential")) {
                throw new IllegalArgumentException(
                        "Sensitive authentication material cannot be stored as a principal claim");
            }
            copy.put(key, Objects.requireNonNull(entry.getValue(), "claim value"));
        }
        return Collections.unmodifiableMap(copy);
    }

    private static String required(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required");
        return value;
    }
}
