package com.kide.knowledge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class KnowledgeCatalogueService {
    private final KnowledgeRepository repository;
    private volatile Cache cache;

    public KnowledgeCatalogueService(KnowledgeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public KnowledgeQueryResult query(KnowledgeCatalogueQuery query) {
        Objects.requireNonNull(query, "query");
        KnowledgeSnapshot snapshot = repository.snapshot().orElse(null);
        if (snapshot == null) {
            return new KnowledgeQueryResult(
                    0L, KnowledgeRepository.MISSING_ETAG, List.of(), false);
        }

        Cache current = cache;
        boolean hit = current != null && current.etag.equals(snapshot.etag());
        if (!hit) {
            current = build(snapshot);
            cache = current;
        }

        List<String> tokens = java.util.Arrays.stream(
                        query.text().toLowerCase(Locale.ROOT).split("\\s+"))
                .filter(token -> !token.isBlank())
                .toList();
        List<KnowledgeCatalogueItem> result = new ArrayList<>();
        for (KnowledgeCatalogueItem item : current.items) {
            if (!query.typeIris().isEmpty()
                    && item.types().stream().noneMatch(query.typeIris()::contains)) {
                continue;
            }
            if (!tokens.isEmpty()) {
                String haystack = searchable(item);
                if (tokens.stream().anyMatch(token -> !haystack.contains(token))) continue;
            }
            result.add(item);
            if (result.size() >= query.limit()) break;
        }
        return new KnowledgeQueryResult(snapshot.revision(), snapshot.etag(), result, hit);
    }

    private static Cache build(KnowledgeSnapshot snapshot) {
        KnowledgeDataset dataset = snapshot.dataset();
        Map<String, MutableItem> bySubject = new LinkedHashMap<>();
        for (KnowledgeTriple triple : dataset.triples()) {
            if (triple.subject().startsWith("_:")) continue;
            MutableItem item = bySubject.computeIfAbsent(
                    triple.subject(), MutableItem::new);
            if (KnowledgeVocabulary.RDF_TYPE.equals(triple.predicate())
                    && !triple.object().literal()) {
                item.types.add(triple.object().value());
            } else if (KnowledgeVocabulary.LABEL.equals(triple.predicate())
                    && triple.object().literal()) {
                if (item.label == null || triple.object().value().compareTo(item.label) < 0) {
                    item.label = triple.object().value();
                }
            } else {
                item.properties.computeIfAbsent(
                        triple.predicate(), ignored -> new LinkedHashSet<>())
                        .add(triple.object().value());
            }
        }

        List<KnowledgeCatalogueItem> items = bySubject.values().stream()
                .filter(item -> !item.types.isEmpty() || item.label != null)
                .map(item -> item.freeze(dataset.provenance()))
                .sorted(Comparator.comparing(
                                KnowledgeCatalogueItem::label,
                                String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(KnowledgeCatalogueItem::iri))
                .toList();
        return new Cache(snapshot.etag(), items);
    }

    private static String searchable(KnowledgeCatalogueItem item) {
        StringBuilder value = new StringBuilder();
        value.append(item.iri()).append(' ').append(item.label()).append(' ');
        item.types().forEach(type -> value.append(type).append(' '));
        item.properties().forEach((predicate, values) -> {
            value.append(predicate).append(' ');
            values.forEach(v -> value.append(v).append(' '));
        });
        return value.toString().toLowerCase(Locale.ROOT);
    }

    private record Cache(String etag, List<KnowledgeCatalogueItem> items) { }

    private static final class MutableItem {
        final String iri;
        String label;
        final Set<String> types = new LinkedHashSet<>();
        final Map<String, Set<String>> properties = new LinkedHashMap<>();

        MutableItem(String iri) { this.iri = iri; }

        KnowledgeCatalogueItem freeze(KnowledgeProvenance provenance) {
            Map<String, List<String>> frozen = new LinkedHashMap<>();
            properties.forEach((key, values) ->
                    frozen.put(key, values.stream().sorted().toList()));
            String display = label == null || label.isBlank() ? compact(iri) : label;
            return new KnowledgeCatalogueItem(
                    iri,
                    display,
                    types.stream().sorted().toList(),
                    frozen,
                    provenance.source(),
                    provenance.authority());
        }

        private static String compact(String iri) {
            int hash = iri.lastIndexOf('#');
            int slash = iri.lastIndexOf('/');
            int colon = iri.lastIndexOf(':');
            int index = Math.max(Math.max(hash, slash), colon);
            return index >= 0 && index + 1 < iri.length() ? iri.substring(index + 1) : iri;
        }
    }
}
