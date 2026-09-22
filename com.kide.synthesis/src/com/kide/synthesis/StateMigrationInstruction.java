package com.kide.synthesis;

import java.util.Objects;

public record StateMigrationInstruction(
        String requirementId,
        StateMigrationPolicy policy,
        String fromResourceId,
        String toResourceId,
        String reason) {
    public StateMigrationInstruction {
        requirementId = required(requirementId, "requirementId");
        policy = Objects.requireNonNull(policy, "policy");
        fromResourceId = normalize(fromResourceId);
        toResourceId = normalize(toResourceId);
        reason = required(reason, "reason");
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private static String required(String value, String field) {
        value = Objects.requireNonNull(value, field).trim();
        if (value.isEmpty()) throw new IllegalArgumentException(field + " is required");
        return value;
    }
}
