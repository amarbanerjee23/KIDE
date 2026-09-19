package com.kide.enterprise.api;

import java.util.ArrayList;
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
            if (!next.requiredFields().containsAll(old.requiredFields())) {
                Set<String> removed = new java.util.HashSet<>(old.requiredFields());
                removed.removeAll(next.requiredFields());
                issues.add("removed required fields from " + entry.getKey() + ": " + removed);
            }
            Set<String> newlyRequired = new java.util.HashSet<>(next.requiredFields());
            newlyRequired.removeAll(old.requiredFields());
            newlyRequired.removeAll(old.optionalFields());
            if (!newlyRequired.isEmpty()) {
                issues.add("added required fields to " + entry.getKey() + ": " + newlyRequired);
            }
        }
        return List.copyOf(issues);
    }
}
