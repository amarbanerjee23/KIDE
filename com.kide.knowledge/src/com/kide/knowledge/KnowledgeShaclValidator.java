package com.kide.knowledge;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class KnowledgeShaclValidator {
    public KnowledgeValidationReport validate(KnowledgeDataset data, KnowledgeDataset shapes) {
        Map<String, String> targets = singleIri(shapes, KnowledgeVocabulary.SH_TARGET_CLASS);
        Map<String, List<String>> properties = iriLists(shapes, KnowledgeVocabulary.SH_PROPERTY);
        Map<String, String> paths = singleIri(shapes, KnowledgeVocabulary.SH_PATH);
        Map<String, Integer> min = singleInt(shapes, KnowledgeVocabulary.SH_MIN_COUNT);
        Map<String, Integer> max = singleInt(shapes, KnowledgeVocabulary.SH_MAX_COUNT);

        Map<String, Set<String>> types = new LinkedHashMap<>();
        for (KnowledgeTriple triple : data.triples()) {
            if (KnowledgeVocabulary.RDF_TYPE.equals(triple.predicate()) && !triple.object().literal()) {
                types.computeIfAbsent(triple.subject(), ignored -> new LinkedHashSet<>())
                        .add(triple.object().value());
            }
        }

        List<KnowledgeValidationIssue> issues = new ArrayList<>();
        for (var target : targets.entrySet()) {
            String shape = target.getKey();
            String targetClass = target.getValue();
            for (var node : types.entrySet()) {
                if (!node.getValue().contains(targetClass)) continue;
                for (String propertyShape : properties.getOrDefault(shape, List.of())) {
                    String path = paths.get(propertyShape);
                    if (path == null) continue;
                    long count = data.triples().stream()
                            .filter(t -> node.getKey().equals(t.subject())
                                    && path.equals(t.predicate()))
                            .count();
                    int minCount = min.getOrDefault(propertyShape, 0);
                    int maxCount = max.getOrDefault(propertyShape, Integer.MAX_VALUE);
                    if (count < minCount) {
                        issues.add(new KnowledgeValidationIssue(
                                node.getKey(), path, "SHACL minCount " + minCount + " violated"));
                    }
                    if (count > maxCount) {
                        issues.add(new KnowledgeValidationIssue(
                                node.getKey(), path, "SHACL maxCount " + maxCount + " violated"));
                    }
                }
            }
        }
        return new KnowledgeValidationReport(issues.isEmpty(), issues);
    }

    private static Map<String, String> singleIri(KnowledgeDataset dataset, String predicate) {
        Map<String, String> result = new LinkedHashMap<>();
        for (KnowledgeTriple triple : dataset.triples()) {
            if (predicate.equals(triple.predicate()) && !triple.object().literal()) {
                if (result.put(triple.subject(), triple.object().value()) != null) {
                    throw new IllegalArgumentException("SHACL field must be single-valued: " + predicate);
                }
            }
        }
        return result;
    }

    private static Map<String, List<String>> iriLists(KnowledgeDataset dataset, String predicate) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (KnowledgeTriple triple : dataset.triples()) {
            if (predicate.equals(triple.predicate()) && !triple.object().literal()) {
                result.computeIfAbsent(triple.subject(), ignored -> new ArrayList<>())
                        .add(triple.object().value());
            }
        }
        return result;
    }

    private static Map<String, Integer> singleInt(KnowledgeDataset dataset, String predicate) {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (KnowledgeTriple triple : dataset.triples()) {
            if (predicate.equals(triple.predicate()) && triple.object().literal()) {
                int value;
                try {
                    value = Integer.parseInt(triple.object().value());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("SHACL count must be an integer", e);
                }
                if (value < 0 || result.put(triple.subject(), value) != null) {
                    throw new IllegalArgumentException("SHACL count is invalid");
                }
            }
        }
        return result;
    }
}
