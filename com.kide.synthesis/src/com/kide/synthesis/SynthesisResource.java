package com.kide.synthesis;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public record SynthesisResource(
        String id,
        String displayName,
        Set<String> capabilities,
        Set<String> interfaces,
        Set<String> operations,
        Set<String> conflictsWith,
        boolean available,
        int priority,
        long latencyMillis,
        long energyMilliJoules,
        int maxBindings) {
    public SynthesisResource {
        id = required(id, "id");
        displayName = required(displayName, "displayName");
        capabilities = sorted(capabilities);
        interfaces = sorted(interfaces);
        operations = sorted(operations);
        conflictsWith = sorted(conflictsWith);
        if (latencyMillis < 0L || energyMilliJoules < 0L) {
            throw new IllegalArgumentException("latency and energy must not be negative");
        }
        if (maxBindings < 1) throw new IllegalArgumentException("maxBindings must be positive");
    }

    public boolean supports(CapabilityRequirement requirement) {
        return available
                && capabilities.contains(requirement.capabilityName())
                && interfaces.containsAll(requirement.requiredInterfaces())
                && operations.containsAll(requirement.requiredOperations());
    }

    private static Set<String> sorted(Set<String> values) {
        TreeSet<String> result = new TreeSet<>();
        if (values != null) {
            values.stream().filter(Objects::nonNull).map(String::trim)
                    .filter(v -> !v.isEmpty()).forEach(result::add);
        }
        return Collections.unmodifiableSet(result);
    }

    private static String required(String value, String field) {
        value = Objects.requireNonNull(value, field).trim();
        if (value.isEmpty()) throw new IllegalArgumentException(field + " is required");
        return value;
    }
}
