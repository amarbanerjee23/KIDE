package com.kide.enterprise.server;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import com.kide.enterprise.modelrepo.ModelPath;
import com.kide.enterprise.modelrepo.ModelRepository;
import com.kide.enterprise.modelrepo.ModelSnapshot;
import com.kide.enterprise.modelrepo.RevisionConflictException;
import com.kide.knowledge.KnowledgeRepository;
import com.kide.knowledge.KnowledgeRepositoryException;
import com.kide.knowledge.KnowledgeSnapshot;
import com.kide.synthesis.ProjectSynthesisEngine;
import com.kide.synthesis.SynthesisDiagnostic;
import com.kide.synthesis.SynthesisResult;

public final class ProjectSynthesisService {
    private final Path projectRoot;
    private final ModelRepository models;
    private final KnowledgeRepository knowledge;
    private final ProjectSynthesisEngine engine = new ProjectSynthesisEngine();

    public ProjectSynthesisService(
            Path projectRoot,
            ModelRepository models,
            KnowledgeRepository knowledge) {
        this.projectRoot = Objects.requireNonNull(projectRoot, "projectRoot");
        this.models = Objects.requireNonNull(models, "models");
        this.knowledge = Objects.requireNonNull(knowledge, "knowledge");
    }

    public ApiResult synthesize(String modelId, String expectedRevision) {
        ModelPath path = new ModelPath(modelId);
        String expected = requiredEtag(expectedRevision);
        ModelSnapshot before = models.read(path).orElseThrow(NotFoundException::new);
        if (!before.revision().etag().equals(expected)) {
            throw new RevisionConflictException("stale synthesis source revision");
        }

        KnowledgeSnapshot knowledgeSnapshot = knowledge.snapshot()
                .orElseThrow(() -> new KnowledgeRepositoryException(
                        "knowledge repository is empty"));

        var output = engine.synthesize(
                projectRoot,
                path.value(),
                knowledgeSnapshot.dataset());

        ModelSnapshot after = models.read(path).orElseThrow(NotFoundException::new);
        if (!after.revision().etag().equals(before.revision().etag())) {
            throw new RevisionConflictException(
                    "synthesis source changed while composition was running");
        }

        SynthesisResult result = output.result();
        List<String> rationale = result.plan().selections().stream()
                .map(selection -> selection.activityName() + " -> "
                        + selection.resourceId() + " (" + selection.rationale() + ")")
                .toList();

        return new ApiResult(
                "syn-" + result.fingerprint().substring(0, 20),
                result.serviceVersion(),
                result.status().name(),
                path.value(),
                Long.toString(before.revision().version()),
                before.revision().etag(),
                knowledgeSnapshot.revision(),
                knowledgeSnapshot.etag(),
                result.fingerprint(),
                result.plan().selections(),
                result.diagnostics(),
                rationale,
                output.generatedMnc());
    }

    private static String requiredEtag(String value) {
        if (value == null || !value.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException(
                    "modelRevision must be a lowercase SHA-256 ETag");
        }
        return value;
    }

    public record ApiResult(
            String resultId,
            String serviceVersion,
            String status,
            String modelId,
            String modelVersion,
            String revision,
            long knowledgeRevision,
            String knowledgeEtag,
            String fingerprint,
            List<com.kide.synthesis.ResourceSelection> selections,
            List<SynthesisDiagnostic> diagnostics,
            List<String> rationale,
            String generatedMnc) {
        public ApiResult {
            selections = List.copyOf(selections);
            diagnostics = List.copyOf(diagnostics);
            rationale = List.copyOf(rationale);
            generatedMnc = generatedMnc == null ? "" : generatedMnc;
        }
    }

    public static final class NotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
