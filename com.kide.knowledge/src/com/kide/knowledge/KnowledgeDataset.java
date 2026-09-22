package com.kide.knowledge;

import java.util.List;
import java.util.Objects;

public record KnowledgeDataset(
        String schemaVersion,
        String datasetId,
        String scopeType,
        String scopeId,
        KnowledgeProvenance provenance,
        List<KnowledgeTriple> triples) {
    public static final String CURRENT_SCHEMA = "1";

    public KnowledgeDataset {
        schemaVersion = required(schemaVersion, "schemaVersion");
        if (!CURRENT_SCHEMA.equals(schemaVersion)) {
            throw new IllegalArgumentException("unsupported knowledge schema version " + schemaVersion);
        }
        datasetId = required(datasetId, "datasetId");
        scopeType = required(scopeType, "scopeType").toUpperCase(java.util.Locale.ROOT);
        if (!"PROJECT".equals(scopeType) && !"ORGANIZATION".equals(scopeType)) {
            throw new IllegalArgumentException("scopeType must be PROJECT or ORGANIZATION");
        }
        scopeId = required(scopeId, "scopeId");
        provenance = Objects.requireNonNull(provenance, "provenance");
        triples = List.copyOf(Objects.requireNonNull(triples, "triples").stream()
                .distinct().sorted().toList());
        if (triples.size() > 250_000) {
            throw new IllegalArgumentException("knowledge dataset exceeds triple limit");
        }
    }

    private static String required(String value, String field) {
        value = Objects.requireNonNull(value, field).trim();
        if (value.isEmpty()) throw new IllegalArgumentException(field + " is required");
        return value;
    }
}
