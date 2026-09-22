package com.kide.knowledge;

import java.net.URI;
import java.util.Set;

public record KnowledgeCatalogueQuery(String text, Set<String> typeIris, int limit) {
    public KnowledgeCatalogueQuery {
        text = text == null ? "" : text.trim();
        if (text.length() > 512) throw new IllegalArgumentException("knowledge query is too long");
        typeIris = Set.copyOf(typeIris == null ? Set.of() : typeIris);
        for (String type : typeIris) {
            if (type == null || type.isBlank() || !URI.create(type).isAbsolute()) {
                throw new IllegalArgumentException("knowledge type filters must be absolute IRIs");
            }
        }
        if (limit < 1 || limit > 500) throw new IllegalArgumentException("knowledge query limit is out of range");
    }

    public static KnowledgeCatalogueQuery of(String text, String typeIri, int limit) {
        return new KnowledgeCatalogueQuery(
                text,
                typeIri == null || typeIri.isBlank() ? Set.of() : Set.of(typeIri.trim()),
                limit);
    }
}
