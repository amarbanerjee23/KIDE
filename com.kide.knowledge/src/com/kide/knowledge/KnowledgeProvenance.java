package com.kide.knowledge;

import java.util.Objects;

public record KnowledgeProvenance(
        String source,
        String authority,
        String importedBy,
        long importedAtEpochMillis,
        String format) {
    public KnowledgeProvenance {
        source = required(source, "source");
        authority = required(authority, "authority");
        importedBy = required(importedBy, "importedBy");
        format = required(format, "format");
        if (importedAtEpochMillis <= 0L) {
            throw new IllegalArgumentException("importedAtEpochMillis must be positive");
        }
    }

    private static String required(String value, String field) {
        value = Objects.requireNonNull(value, field).trim();
        if (value.isEmpty()) throw new IllegalArgumentException(field + " is required");
        return value;
    }
}
