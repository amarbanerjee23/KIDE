package com.kide.knowledge;

import java.net.URI;
import java.util.Objects;

public record KnowledgeTerm(String value, boolean literal) {
    public KnowledgeTerm {
        value = Objects.requireNonNull(value, "value").trim();
        if (value.isEmpty()) throw new IllegalArgumentException("knowledge term is required");
        if (!literal && !value.startsWith("_:")) {
            URI uri = URI.create(value);
            if (!uri.isAbsolute()) throw new IllegalArgumentException("knowledge IRI must be absolute");
        }
    }

    public static KnowledgeTerm iri(String value) { return new KnowledgeTerm(value, false); }
    public static KnowledgeTerm literal(String value) { return new KnowledgeTerm(value, true); }
}
