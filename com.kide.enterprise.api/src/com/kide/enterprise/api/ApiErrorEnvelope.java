package com.kide.enterprise.api;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record ApiErrorEnvelope(
        String apiVersion,
        UUID requestId,
        ApiErrorCode code,
        String message,
        Map<String, String> details) {

    public ApiErrorEnvelope {
        apiVersion = required(apiVersion, "apiVersion", 16);
        Objects.requireNonNull(requestId, "requestId");
        Objects.requireNonNull(code, "code");
        message = required(message, "message", 512);
        details = safeDetails(details);
    }

    public static ApiErrorEnvelope fromMap(Map<String, ?> values) {
        Objects.requireNonNull(values, "values");
        Object apiVersion = values.get("apiVersion");
        Object requestId = values.get("requestId");
        Object code = values.get("code");
        Object message = values.get("message");
        Object details = values.get("details");

        Map<String, String> parsedDetails = new LinkedHashMap<>();
        if (details instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() instanceof String key && entry.getValue() instanceof String value) {
                    parsedDetails.put(key, value);
                }
            }
        }
        return new ApiErrorEnvelope(
                String.valueOf(apiVersion),
                UUID.fromString(String.valueOf(requestId)),
                ApiErrorCode.valueOf(String.valueOf(code)),
                String.valueOf(message),
                parsedDetails);
    }

    private static Map<String, String> safeDetails(Map<String, String> input) {
        if (input == null || input.isEmpty()) return Map.of();
        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : input.entrySet()) {
            String key = required(entry.getKey(), "detail key", 128);
            String value = required(entry.getValue(), "detail value", 512);
            String normalized = key.toLowerCase(java.util.Locale.ROOT);
            if (normalized.contains("stack")
                    || normalized.contains("exception")
                    || normalized.contains("token")
                    || normalized.contains("secret")
                    || normalized.contains("password")
                    || normalized.contains("credential")) {
                throw new IllegalArgumentException("unsafe error detail key");
            }
            result.put(key, value);
        }
        return Map.copyOf(result);
    }

    private static String required(String value, String field, int max) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required");
        String normalized = value.replace('\r', ' ').replace('\n', ' ').trim();
        if (normalized.length() > max) throw new IllegalArgumentException(field + " is too long");
        return normalized;
    }
}
