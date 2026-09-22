package com.kide.synthesis;

import java.util.Objects;

public record ResourceSelection(
        String requirementId,
        String activityName,
        String capabilityName,
        String resourceId,
        String rationale) {
    public ResourceSelection {
        requirementId = required(requirementId, "requirementId");
        activityName = required(activityName, "activityName");
        capabilityName = required(capabilityName, "capabilityName");
        resourceId = required(resourceId, "resourceId");
        rationale = required(rationale, "rationale");
    }

    private static String required(String value, String field) {
        value = Objects.requireNonNull(value, field).trim();
        if (value.isEmpty()) throw new IllegalArgumentException(field + " is required");
        return value;
    }
}
