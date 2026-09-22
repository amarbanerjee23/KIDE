package com.kide.knowledge;

import java.net.URI;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

public record KnowledgeTraceLink(
        String id,
        String knowledgeIri,
        String modelPath,
        String semanticId,
        KnowledgeTraceRelation relation,
        String sourceAuthority,
        String provenanceSource,
        String createdBy,
        String updatedBy,
        long createdAtEpochMillis,
        long updatedAtEpochMillis,
        String knowledgeEtagAtBind,
        String modelEtagAtBind) {
    public KnowledgeTraceLink {
        UUID.fromString(required(id, "id"));
        knowledgeIri = required(knowledgeIri, "knowledgeIri");
        if (!URI.create(knowledgeIri).isAbsolute()) {
            throw new IllegalArgumentException("knowledgeIri must be absolute");
        }
        modelPath = safePath(modelPath);
        semanticId = semanticId == null ? "" : semanticId.trim();
        if (semanticId.length() > 512) throw new IllegalArgumentException("semanticId is too long");
        relation = Objects.requireNonNull(relation, "relation");
        sourceAuthority = required(sourceAuthority, "sourceAuthority");
        provenanceSource = required(provenanceSource, "provenanceSource");
        createdBy = required(createdBy, "createdBy");
        updatedBy = required(updatedBy, "updatedBy");
        if (createdAtEpochMillis <= 0L || updatedAtEpochMillis < createdAtEpochMillis) {
            throw new IllegalArgumentException("trace timestamps are invalid");
        }
        knowledgeEtagAtBind = etag(knowledgeEtagAtBind, "knowledgeEtagAtBind");
        modelEtagAtBind = etag(modelEtagAtBind, "modelEtagAtBind");
    }

    public KnowledgeTraceLink rebind(
            String nextModelPath,
            String nextSemanticId,
            String nextModelEtag,
            String actor,
            long timestamp) {
        return new KnowledgeTraceLink(
                id, knowledgeIri, nextModelPath, nextSemanticId, relation,
                sourceAuthority, provenanceSource, createdBy, actor,
                createdAtEpochMillis, timestamp, knowledgeEtagAtBind, nextModelEtag);
    }

    private static String safePath(String value) {
        value = required(value, "modelPath").replace('\\', '/');
        if (value.startsWith("/") || value.startsWith(".kide/")
                || value.equals(".kide") || value.contains("../")
                || value.equals("..") || value.contains("/./") || value.endsWith("/..")) {
            throw new IllegalArgumentException("modelPath is not project-relative");
        }
        Path normalized = Path.of(value).normalize();
        if (normalized.isAbsolute() || normalized.startsWith("..")) {
            throw new IllegalArgumentException("modelPath escapes the project");
        }
        return normalized.toString().replace('\\', '/');
    }

    private static String etag(String value, String field) {
        value = required(value, field);
        if (!value.matches("[0-9a-f]{64}")) throw new IllegalArgumentException(field + " must be SHA-256");
        return value;
    }

    private static String required(String value, String field) {
        value = Objects.requireNonNull(value, field).trim();
        if (value.isEmpty()) throw new IllegalArgumentException(field + " is required");
        return value;
    }
}
