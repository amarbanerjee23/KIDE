package com.kide.knowledge;

import java.util.List;
import java.util.Objects;

public record KnowledgeTraceSnapshot(long revision, String etag, List<KnowledgeTraceLink> links) {
    public KnowledgeTraceSnapshot {
        if (revision < 0L) throw new IllegalArgumentException("revision must not be negative");
        etag = Objects.requireNonNull(etag, "etag");
        if (!etag.matches("[0-9a-f]{64}")) throw new IllegalArgumentException("etag must be SHA-256");
        links = List.copyOf(links == null ? List.of() : links);
    }
}
