package com.kide.enterprise.api;

import java.util.List;
import java.util.Set;

public record ApiSchema(String name, Set<String> requiredFields, Set<String> optionalFields) {
    public ApiSchema {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("schema name is required");
        requiredFields = Set.copyOf(requiredFields == null ? Set.of() : requiredFields);
        optionalFields = Set.copyOf(optionalFields == null ? Set.of() : optionalFields);
        for (String field : requiredFields) {
            if (optionalFields.contains(field)) throw new IllegalArgumentException("field cannot be both required and optional");
        }
    }

    public List<String> allFieldsSorted() {
        return java.util.stream.Stream.concat(requiredFields.stream(), optionalFields.stream())
                .sorted()
                .toList();
    }
}
