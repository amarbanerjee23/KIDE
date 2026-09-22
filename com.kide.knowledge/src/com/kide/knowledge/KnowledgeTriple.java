package com.kide.knowledge;

import java.net.URI;
import java.util.Objects;

public record KnowledgeTriple(String subject, String predicate, KnowledgeTerm object)
        implements Comparable<KnowledgeTriple> {
    public KnowledgeTriple {
        subject = Objects.requireNonNull(subject, "subject").trim();
        predicate = Objects.requireNonNull(predicate, "predicate").trim();
        object = Objects.requireNonNull(object, "object");
        requireResource(subject, "subject");
        requireResource(predicate, "predicate");
    }

    private static void requireResource(String value, String field) {
        if (value.isBlank()) throw new IllegalArgumentException(field + " is required");
        if (value.startsWith("_:") && "subject".equals(field)) return;
        URI uri = URI.create(value);
        if (!uri.isAbsolute()) throw new IllegalArgumentException(field + " must be an absolute IRI");
    }

    @Override
    public int compareTo(KnowledgeTriple other) {
        int c = subject.compareTo(other.subject);
        if (c != 0) return c;
        c = predicate.compareTo(other.predicate);
        if (c != 0) return c;
        c = Boolean.compare(object.literal(), other.object.literal());
        if (c != 0) return c;
        return object.value().compareTo(other.object.value());
    }
}
