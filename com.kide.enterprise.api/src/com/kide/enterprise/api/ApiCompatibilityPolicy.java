package com.kide.enterprise.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ApiCompatibilityPolicy {
    private ApiCompatibilityPolicy() { }

    public static List<String> compareOperations(
            Map<String, ApiOperation> previous,
            Map<String, ApiOperation> candidate) {
        List<String> issues = new ArrayList<>();
        for (Map.Entry<String, ApiOperation> entry : previous.entrySet()) {
            ApiOperation next = candidate.get(entry.getKey());
            if (next == null) {
                issues.add("removed operation: " + entry.getKey());
                continue;
            }
            ApiOperation old = entry.getValue();
            if (old.method() != next.method()) issues.add("changed method: " + entry.getKey());
            if (!old.path().equals(next.path())) issues.add("changed path: " + entry.getKey());
            if (!old.requestSchema().equals(next.requestSchema())) issues.add("changed request schema: " + entry.getKey());
            if (!old.responseSchema().equals(next.responseSchema())) issues.add("changed response schema: " + entry.getKey());
        }
        return List.copyOf(issues);
    }

    public static List<String> compareSchemas(
            Map<String, ApiSchema> previous,
            Map<String, ApiSchema> candidate) {
        List<String> issues = new ArrayList<>();
        for (Map.Entry<String, ApiSchema> entry : previous.entrySet()) {
            ApiSchema next = candidate.get(entry.getKey());
            if (next == null) {
                issues.add("removed schema: " + entry.getKey());
                continue;
            }

            ApiSchema old = entry.getValue();
            Set<String> oldFields = new HashSet<>(old.requiredFields());
            oldFields.addAll(old.optionalFields());
            Set<String> nextFields = new HashSet<>(next.requiredFields());
            nextFields.addAll(next.optionalFields());

            Set<String> removedFields = new HashSet<>(oldFields);
            removedFields.removeAll(nextFields);
            if (!removedFields.isEmpty()) {
                issues.add("removed fields from " + entry.getKey() + ": " + removedFields);
            }

            Set<String> newlyRequired = new HashSet<>(next.requiredFields());
            newlyRequired.removeAll(old.requiredFields());
            if (!newlyRequired.isEmpty()) {
                issues.add("new required fields in " + entry.getKey() + ": " + newlyRequired);
            }
        }
        return List.copyOf(issues);
    }
}
