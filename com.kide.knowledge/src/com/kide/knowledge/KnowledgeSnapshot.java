package com.kide.knowledge;

import java.util.Objects;

public record KnowledgeSnapshot(long revision, String etag, KnowledgeDataset dataset) {
    public KnowledgeSnapshot {
        if (revision <= 0L) throw new IllegalArgumentException("revision must be positive");
        etag = Objects.requireNonNull(etag, "etag");
        if (!etag.matches("[0-9a-f]{64}")) throw new IllegalArgumentException("etag must be SHA-256");
        dataset = Objects.requireNonNull(dataset, "dataset");
    }
}
