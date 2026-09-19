package com.kide.enterprise.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record ApiSchema(
        String name,
        Map<String, ApiFieldType> fields,
        Set<String> requiredFields) {

    public ApiSchema {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("schema name is required");
        fields = Map.copyOf(fields == null ? Map.of() : fields);
        requiredFields = Set.copyOf(requiredFields == null ? Set.of() : requiredFields);
        if (!fields.keySet().containsAll(requiredFields)) {
            throw new IllegalArgumentException("required fields must exist in schema fields");
        }
    }

    public Set<String> optionalFields() {
        java.util.HashSet<String> optional = new java.util.HashSet<>(fields.keySet());
        optional.removeAll(requiredFields);
        return Set.copyOf(optional);
    }

    public List<String> allFieldsSorted() {
        return fields.keySet().stream().sorted().toList();
    }
}
