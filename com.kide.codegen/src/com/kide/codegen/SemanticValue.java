package com.kide.codegen;

import java.math.BigDecimal;
import java.util.Objects;

public record SemanticValue(String value, boolean iri, Kind kind) {
    public enum Kind { STRING, INTEGER, DECIMAL, BOOLEAN, IRI }

    public SemanticValue {
        value = Objects.requireNonNull(value, "value").trim();
        kind = Objects.requireNonNull(kind, "kind");
        if (value.isEmpty() && kind != Kind.STRING) {
            throw new IllegalArgumentException("non-string semantic value cannot be empty");
        }
        if (iri != (kind == Kind.IRI)) {
            throw new IllegalArgumentException("IRI flag and kind disagree");
        }
        switch (kind) {
            case INTEGER -> Long.parseLong(value);
            case DECIMAL -> new BigDecimal(value);
            case BOOLEAN -> {
                if (!"true".equals(value) && !"false".equals(value)) {
                    throw new IllegalArgumentException("boolean value must be true or false");
                }
            }
            default -> { }
        }
    }

    public static SemanticValue literal(String value) {
        return new SemanticValue(Objects.requireNonNull(value), false, Kind.STRING);
    }

    public static SemanticValue iri(String value) {
        return new SemanticValue(Objects.requireNonNull(value), true, Kind.IRI);
    }

    public String rendered() {
        return value;
    }
}
