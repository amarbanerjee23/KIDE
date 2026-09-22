package com.kide.codegen;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public record GenerationContext(
        String sourceModelId,
        String sourceRevision,
        String sourceEtag,
        long knowledgeRevision,
        String knowledgeEtag,
        String synthesisFingerprint,
        String krlModelId,
        String krlRevision,
        String krlEtag) {
    public GenerationContext {
        sourceModelId = required(sourceModelId, "sourceModelId");
        sourceRevision = required(sourceRevision, "sourceRevision");
        sourceEtag = sha(sourceEtag, "sourceEtag");
        if (knowledgeRevision <= 0L) {
            throw new IllegalArgumentException("knowledgeRevision must be positive");
        }
        knowledgeEtag = sha(knowledgeEtag, "knowledgeEtag");
        synthesisFingerprint = sha(synthesisFingerprint, "synthesisFingerprint");
        krlModelId = required(krlModelId, "krlModelId");
        krlRevision = required(krlRevision, "krlRevision");
        krlEtag = sha(krlEtag, "krlEtag");
    }

    public Map<String, SemanticValue> reservedValues() {
        Map<String, SemanticValue> values = new LinkedHashMap<>();
        values.put("source.modelId", SemanticValue.literal(sourceModelId));
        values.put("source.revision", SemanticValue.literal(sourceRevision));
        values.put("source.etag", SemanticValue.literal(sourceEtag));
        values.put("knowledge.revision", SemanticValue.literal(Long.toString(knowledgeRevision)));
        values.put("knowledge.etag", SemanticValue.literal(knowledgeEtag));
        values.put("synthesis.fingerprint", SemanticValue.literal(synthesisFingerprint));
        values.put("krl.modelId", SemanticValue.literal(krlModelId));
        values.put("krl.revision", SemanticValue.literal(krlRevision));
        values.put("krl.etag", SemanticValue.literal(krlEtag));
        return Map.copyOf(values);
    }

    private static String sha(String value, String field) {
        value = required(value, field);
        if (!value.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException(field + " must be a lowercase SHA-256 value");
        }
        return value;
    }

    private static String required(String value, String field) {
        value = Objects.requireNonNull(value, field).trim();
        if (value.isEmpty()) throw new IllegalArgumentException(field + " is required");
        return value;
    }
}
