package com.kide.synthesis;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public record CapabilityRequirement(
        String id,
        String activityName,
        String capabilityName,
        Set<String> requiredInterfaces,
        Set<String> requiredOperations,
        boolean boundToCapabilityModel) {
    public CapabilityRequirement {
        id = required(id, "id");
        activityName = required(activityName, "activityName");
        capabilityName = required(capabilityName, "capabilityName");
        requiredInterfaces = sorted(requiredInterfaces);
        requiredOperations = sorted(requiredOperations);
    }

    private static Set<String> sorted(Set<String> values) {
        TreeSet<String> result = new TreeSet<>();
        if (values != null) {
            values.stream().map(String::trim).filter(v -> !v.isEmpty()).forEach(result::add);
        }
        return java.util.Collections.unmodifiableSet(result);
    }

    private static String required(String value, String field) {
        value = Objects.requireNonNull(value, field).trim();
        if (value.isEmpty()) throw new IllegalArgumentException(field + " is required");
        return value;
    }
}
