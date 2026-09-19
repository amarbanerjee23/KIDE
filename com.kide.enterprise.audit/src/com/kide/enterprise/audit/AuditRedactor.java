package com.kide.enterprise.audit;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class AuditRedactor {
    public static final String REDACTED = "[REDACTED]";
    private static final Set<String> SENSITIVE_TOKENS = Set.of(
            "token", "secret", "password", "credential", "authorization",
            "cookie", "apikey", "api_key", "clientsecret", "privatekey",
            "accesskey", "sessionid");

    private AuditRedactor() { }

    public static Map<String, String> redact(Map<String, String> attributes) {
        if (attributes == null || attributes.isEmpty()) return Map.of();
        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            String key = sanitize(entry.getKey(), "attribute key", 128);
            String value = sanitize(Objects.requireNonNull(entry.getValue(), "attribute value"),
                    "attribute value", 2048);
            result.put(key, isSensitive(key) ? REDACTED : value);
        }
        return Collections.unmodifiableMap(result);
    }

    public static boolean isSensitive(String key) {
        String normalized = key.toLowerCase(Locale.ROOT).replace("-", "").replace(".", "");
        for (String token : SENSITIVE_TOKENS) {
            if (normalized.contains(token.replace("_", ""))) return true;
        }
        return false;
    }

    static String sanitize(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required");
        String normalized = value.replace('\r', ' ').replace('\n', ' ').trim();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(field + " exceeds " + maxLength + " characters");
        }
        return normalized;
    }
}
