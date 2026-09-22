package com.kide.knowledge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record KnowledgeCatalogueItem(
        String iri,
        String label,
        List<String> types,
        Map<String, List<String>> properties,
        String provenanceSource,
        String authority) {
    public KnowledgeCatalogueItem {
        iri = required(iri, "iri");
        label = required(label, "label");
        types = List.copyOf(types == null ? List.of() : types);
        Map<String, List<String>> copy = new LinkedHashMap<>();
        if (properties != null) {
            properties.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
                List<String> values = new ArrayList<>(entry.getValue() == null ? List.of() : entry.getValue());
                Collections.sort(values);
                copy.put(entry.getKey(), List.copyOf(values));
            });
        }
        properties = Collections.unmodifiableMap(copy);
        provenanceSource = required(provenanceSource, "provenanceSource");
        authority = required(authority, "authority");
    }

    private static String required(String value, String field) {
        value = Objects.requireNonNull(value, field).trim();
        if (value.isEmpty()) throw new IllegalArgumentException(field + " is required");
        return value;
    }
}
