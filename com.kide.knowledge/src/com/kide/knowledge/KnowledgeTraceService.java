package com.kide.knowledge;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public final class KnowledgeTraceService {
    public List<KnowledgeTraceLink> impactForKnowledge(
            KnowledgeTraceSnapshot traces, String knowledgeIri) {
        return traces.links().stream()
                .filter(link -> link.knowledgeIri().equals(knowledgeIri))
                .toList();
    }

    public List<KnowledgeTraceLink> impactForModel(
            KnowledgeTraceSnapshot traces, String modelPath) {
        return traces.links().stream()
                .filter(link -> link.modelPath().equals(modelPath))
                .toList();
    }

    public List<KnowledgeTraceIssue> validate(
            KnowledgeTraceSnapshot traces,
            KnowledgeSnapshot knowledge,
            java.util.function.Predicate<String> modelExists) {
        Set<String> resources = new LinkedHashSet<>();
        for (KnowledgeTriple triple : knowledge.dataset().triples()) {
            resources.add(triple.subject());
            if (!triple.object().literal()) resources.add(triple.object().value());
        }
        List<KnowledgeTraceIssue> issues = new ArrayList<>();
        for (KnowledgeTraceLink link : traces.links()) {
            if (!resources.contains(link.knowledgeIri())) {
                issues.add(new KnowledgeTraceIssue(
                        link.id(), "BROKEN_KNOWLEDGE", "Knowledge resource no longer exists."));
            }
            if (!modelExists.test(link.modelPath())) {
                issues.add(new KnowledgeTraceIssue(
                        link.id(), "BROKEN_MODEL", "Model resource no longer exists."));
            }
            if (!knowledge.etag().equals(link.knowledgeEtagAtBind())) {
                issues.add(new KnowledgeTraceIssue(
                        link.id(), "STALE_KNOWLEDGE", "Knowledge graph changed after this trace was bound."));
            }
        }
        return List.copyOf(issues);
    }
}
