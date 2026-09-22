package com.kide.synthesis;

import java.util.Objects;

public record HistoricalResourceBinding(String requirementId, String resourceId) {
    public HistoricalResourceBinding {
        requirementId = required(requirementId, "requirementId");
        resourceId = required(resourceId, "resourceId");
    }

    private static String required(String value, String field) {
        value = Objects.requireNonNull(value, field).trim();
        if (value.isEmpty()) throw new IllegalArgumentException(field + " is required");
        return value;
    }
}
