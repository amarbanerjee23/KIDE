package com.kide.synthesis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kide.knowledge.KnowledgeDataset;
import com.kide.knowledge.KnowledgeTerm;
import com.kide.knowledge.KnowledgeTriple;
import com.kide.knowledge.KnowledgeVocabulary;

public final class KnowledgeResourceCatalogue {
    public List<SynthesisResource> resources(KnowledgeDataset dataset) {
        Map<String, MutableResource> resources = new LinkedHashMap<>();
        for (KnowledgeTriple triple : dataset.triples()) {
            if (KnowledgeVocabulary.RDF_TYPE.equals(triple.predicate())
                    && !triple.object().literal()
                    && KnowledgeVocabulary.DEVICE.equals(triple.object().value())) {
                resources.computeIfAbsent(triple.subject(), MutableResource::new);
            }
        }

        for (KnowledgeTriple triple : dataset.triples()) {
            MutableResource resource = resources.get(triple.subject());
            if (resource == null) continue;
            apply(resource, triple.predicate(), triple.object());
        }

        return resources.values().stream()
                .map(MutableResource::freeze)
                .sorted(java.util.Comparator.comparing(SynthesisResource::id))
                .toList();
    }

    private static void apply(MutableResource resource, String predicate, KnowledgeTerm value) {
        if (KnowledgeVocabulary.LABEL.equals(predicate) && value.literal()) {
            resource.label = value.value();
        } else if (SynthesisVocabulary.PROVIDES_CAPABILITY.equals(predicate)) {
            resource.capabilities.add(value.value());
            resource.capabilities.add(compact(value.value()));
        } else if (SynthesisVocabulary.PROVIDES_INTERFACE.equals(predicate)) {
            resource.interfaces.add(value.value());
            resource.interfaces.add(compact(value.value()));
        } else if (SynthesisVocabulary.PROVIDES_OPERATION.equals(predicate)) {
            resource.operations.add(value.value());
            resource.operations.add(compact(value.value()));
        } else if (SynthesisVocabulary.CONFLICTS_WITH.equals(predicate) && !value.literal()) {
            resource.conflictsWith.add(value.value());
        } else if (SynthesisVocabulary.PRIORITY.equals(predicate) && value.literal()) {
            resource.priority = parseInt(value.value(), "priority");
        } else if (SynthesisVocabulary.LATENCY_MILLIS.equals(predicate) && value.literal()) {
            resource.latency = parseLong(value.value(), "latencyMillis");
        } else if (SynthesisVocabulary.ENERGY_MILLIJOULES.equals(predicate) && value.literal()) {
            resource.energy = parseLong(value.value(), "energyMilliJoules");
        } else if (SynthesisVocabulary.MAX_BINDINGS.equals(predicate) && value.literal()) {
            resource.maxBindings = parseInt(value.value(), "maxBindings");
        } else if (SynthesisVocabulary.AVAILABLE.equals(predicate) && value.literal()) {
            if (!"true".equalsIgnoreCase(value.value())
                    && !"false".equalsIgnoreCase(value.value())) {
                throw new IllegalArgumentException("available must be true or false");
            }
            resource.available = Boolean.parseBoolean(value.value());
        }
    }

    private static int parseInt(String value, String field) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(field + " must be an integer", e);
        }
    }

    private static long parseLong(String value, String field) {
        try {
            long parsed = Long.parseLong(value);
            if (parsed < 0L) throw new IllegalArgumentException(field + " must not be negative");
            return parsed;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(field + " must be an integer", e);
        }
    }

    private static String compact(String value) {
        int split = Math.max(
                Math.max(value.lastIndexOf('#'), value.lastIndexOf('/')),
                value.lastIndexOf(':'));
        return split >= 0 && split + 1 < value.length()
                ? value.substring(split + 1)
                : value;
    }

    private static final class MutableResource {
        final String id;
        String label;
        final Set<String> capabilities = new LinkedHashSet<>();
        final Set<String> interfaces = new LinkedHashSet<>();
        final Set<String> operations = new LinkedHashSet<>();
        final Set<String> conflictsWith = new LinkedHashSet<>();
        boolean available = true;
        int priority;
        long latency;
        long energy;
        int maxBindings = Integer.MAX_VALUE;

        MutableResource(String id) {
            this.id = id;
        }

        SynthesisResource freeze() {
            return new SynthesisResource(
                    id,
                    label == null || label.isBlank() ? compact(id) : label,
                    capabilities,
                    interfaces,
                    operations,
                    conflictsWith,
                    available,
                    priority,
                    latency,
                    energy,
                    maxBindings);
        }
    }
}
