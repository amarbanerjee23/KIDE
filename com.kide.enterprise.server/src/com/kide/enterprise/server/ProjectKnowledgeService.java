package com.kide.enterprise.server;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.kide.enterprise.modelrepo.ModelPath;
import com.kide.enterprise.modelrepo.ModelRepository;
import com.kide.enterprise.modelrepo.ModelSnapshot;
import com.kide.knowledge.KnowledgeCatalogueQuery;
import com.kide.knowledge.KnowledgeCatalogueService;
import com.kide.knowledge.KnowledgeQueryResult;
import com.kide.knowledge.KnowledgeRepository;
import com.kide.knowledge.KnowledgeRepositoryException;
import com.kide.knowledge.KnowledgeSnapshot;
import com.kide.knowledge.KnowledgeTraceIssue;
import com.kide.knowledge.KnowledgeTraceLink;
import com.kide.knowledge.KnowledgeTraceRelation;
import com.kide.knowledge.KnowledgeTraceService;
import com.kide.knowledge.KnowledgeTraceSnapshot;
import com.kide.knowledge.KnowledgeTraceStore;
import com.kide.knowledge.KnowledgeVocabulary;

public final class ProjectKnowledgeService {
    private final KnowledgeRepository repository;
    private final KnowledgeCatalogueService catalogue;
    private final KnowledgeTraceStore traces;
    private final KnowledgeTraceService traceService = new KnowledgeTraceService();
    private final ModelRepository models;

    public ProjectKnowledgeService(
            KnowledgeRepository repository,
            KnowledgeTraceStore traces,
            ModelRepository models) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.catalogue = new KnowledgeCatalogueService(repository);
        this.traces = Objects.requireNonNull(traces, "traces");
        this.models = Objects.requireNonNull(models, "models");
    }

    public KnowledgeQueryResult query(String text, String typeIri, int limit) {
        return catalogue.query(new KnowledgeCatalogueQuery(
                text == null ? "" : text,
                normalizeType(typeIri),
                limit));
    }

    public KnowledgeTraceSnapshot traces() {
        return traces.snapshot();
    }

    public KnowledgeTraceSnapshot createTrace(
            String knowledgeIri,
            String modelId,
            String semanticId,
            String relation,
            String actor,
            String expectedTraceEtag) {
        KnowledgeSnapshot knowledge = requireKnowledge();
        requireKnowledgeResource(knowledge, knowledgeIri);
        ModelSnapshot model = requireModel(modelId);
        return traces.create(
                knowledgeIri,
                model.path().value(),
                semanticId,
                KnowledgeTraceRelation.valueOf(relation.trim().toUpperCase(java.util.Locale.ROOT)),
                knowledge.dataset().provenance().authority(),
                knowledge.dataset().provenance().source(),
                actor,
                knowledge.etag(),
                model.revision().etag(),
                expectedTraceEtag);
    }

    public KnowledgeTraceSnapshot rebindTrace(
            String traceId,
            String modelId,
            String semanticId,
            String actor,
            String expectedTraceEtag) {
        ModelSnapshot model = requireModel(modelId);
        return traces.rebind(
                traceId,
                model.path().value(),
                semanticId,
                model.revision().etag(),
                actor,
                expectedTraceEtag);
    }

    public KnowledgeTraceSnapshot deleteTrace(String traceId, String expectedTraceEtag) {
        return traces.delete(traceId, expectedTraceEtag);
    }

    public Impact impact(String knowledgeIri, String modelId) {
        KnowledgeSnapshot knowledge = requireKnowledge();
        KnowledgeTraceSnapshot traceSnapshot = traces.snapshot();

        List<KnowledgeTraceLink> selected;
        String normalizedKnowledge = knowledgeIri == null ? "" : knowledgeIri.trim();
        String normalizedModel = modelId == null ? "" : modelId.trim();
        if (normalizedKnowledge.isEmpty() && normalizedModel.isEmpty()) {
            throw new IllegalArgumentException("knowledgeIri or modelId is required");
        }
        if (!normalizedKnowledge.isEmpty()) {
            selected = traceService.impactForKnowledge(traceSnapshot, normalizedKnowledge);
            if (!normalizedModel.isEmpty()) {
                String safeModel = new ModelPath(normalizedModel).value();
                selected = selected.stream()
                        .filter(link -> link.modelPath().equals(safeModel))
                        .toList();
            }
        } else {
            selected = traceService.impactForModel(
                    traceSnapshot, new ModelPath(normalizedModel).value());
        }

        Set<String> selectedIds = selected.stream()
                .map(KnowledgeTraceLink::id)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
        List<KnowledgeTraceIssue> issues = traceService.validate(
                        traceSnapshot,
                        knowledge,
                        path -> models.read(new ModelPath(path))
                                .map(snapshot -> snapshot.revision().etag())
                                .orElse(null))
                .stream()
                .filter(issue -> selectedIds.contains(issue.traceId()))
                .toList();

        return new Impact(
                selected,
                issues,
                knowledge.revision(),
                traceSnapshot.revision());
    }

    private KnowledgeSnapshot requireKnowledge() {
        return repository.snapshot()
                .orElseThrow(() -> new KnowledgeRepositoryException(
                        "knowledge repository is empty"));
    }

    private void requireKnowledgeResource(KnowledgeSnapshot snapshot, String iri) {
        String value = Objects.requireNonNull(iri, "knowledgeIri").trim();
        if (value.isEmpty()) throw new IllegalArgumentException("knowledgeIri is required");
        boolean exists = snapshot.dataset().triples().stream()
                .anyMatch(triple -> triple.subject().equals(value));
        if (!exists) throw new NotFoundException();
    }

    private ModelSnapshot requireModel(String modelId) {
        ModelPath path = new ModelPath(Objects.requireNonNull(modelId, "modelId"));
        return models.read(path).orElseThrow(NotFoundException::new);
    }

    private static Set<String> normalizeType(String typeIri) {
        if (typeIri == null || typeIri.isBlank()) return Set.of();
        String value = typeIri.trim();
        String resolved = switch (value.toUpperCase(java.util.Locale.ROOT)) {
            case "CAPABILITY" -> KnowledgeVocabulary.CAPABILITY;
            case "INTERFACE" -> KnowledgeVocabulary.INTERFACE;
            case "BEHAVIOR" -> KnowledgeVocabulary.BEHAVIOR;
            case "INTERACTION" -> KnowledgeVocabulary.INTERACTION;
            case "WORKFLOW" -> KnowledgeVocabulary.WORKFLOW;
            case "DEVICE" -> KnowledgeVocabulary.DEVICE;
            default -> value;
        };
        return Set.of(resolved);
    }

    public record Impact(
            List<KnowledgeTraceLink> items,
            List<KnowledgeTraceIssue> issues,
            long knowledgeRevision,
            long traceRevision) {
        public Impact {
            items = List.copyOf(items);
            issues = List.copyOf(issues);
        }
    }

    public static final class NotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
