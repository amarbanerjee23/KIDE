package com.kide.knowledge;

import java.util.List;
import java.util.Objects;

public record KnowledgeQueryResult(
        long revision,
        String etag,
        List<KnowledgeCatalogueItem> items,
        boolean cached) {
    public KnowledgeQueryResult {
        if (revision < 0L) throw new IllegalArgumentException("revision must not be negative");
        etag = Objects.requireNonNull(etag, "etag");
        items = List.copyOf(items == null ? List.of() : items);
    }
}
