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
import com.kide.synthesis.ReconfigurationCause;
import com.kide.synthesis.ReconfigurationResult;
import com.kide.synthesis.ResourceSelection;
import com.kide.synthesis.SynthesisDiagnostic;
import com.kide.synthesis.SynthesisPlan;
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

    public ReconfigurationApiResult reconfigure(
            String modelId,
            String expectedRevision,
            String cause,
            List<PreviousBinding> previousBindings) {
        ModelPath path = new ModelPath(modelId);
        String expected = requiredEtag(expectedRevision);
        ModelSnapshot before = models.read(path).orElseThrow(NotFoundException::new);
        if (!before.revision().etag().equals(expected)) {
            throw new RevisionConflictException("stale reconfiguration source revision");
        }

        KnowledgeSnapshot knowledgeSnapshot = knowledge.snapshot()
                .orElseThrow(() -> new KnowledgeRepositoryException(
                        "knowledge repository is empty"));
        ReconfigurationCause parsedCause;
        try {
            parsedCause = ReconfigurationCause.valueOf(
                    Objects.requireNonNull(cause, "cause").trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("cause is invalid", e);
        }

        List<PreviousBinding> bindingsInput =
                previousBindings == null ? List.of() : List.copyOf(previousBindings);
        if (bindingsInput.size() > 10_000) {
            throw new IllegalArgumentException("previousBindings exceeds the supported limit");
        }
        java.util.Set<String> bindingIds = new java.util.HashSet<>();
        for (PreviousBinding binding : bindingsInput) {
            if (!bindingIds.add(binding.requirementId())) {
                throw new IllegalArgumentException(
                        "previousBindings contains duplicate requirementId");
            }
        }

        List<ResourceSelection> previousSelections =
                bindingsInput.stream()
                        .map(binding -> new ResourceSelection(
                                binding.requirementId(),
                                binding.activityName(),
                                binding.capabilityName(),
                                binding.resourceId(),
                                "previous binding evidence"))
                        .sorted(java.util.Comparator.comparing(ResourceSelection::requirementId))
                        .toList();
        SynthesisPlan previousPlan =
                new SynthesisPlan(true, previousSelections, List.of());

        ReconfigurationResult result = engine.reconfigure(
                projectRoot,
                path.value(),
                knowledgeSnapshot.dataset(),
                previousPlan,
                parsedCause);

        ModelSnapshot after = models.read(path).orElseThrow(NotFoundException::new);
        if (!after.revision().etag().equals(before.revision().etag())) {
            throw new RevisionConflictException(
                    "reconfiguration source changed while planning was running");
        }

        return new ReconfigurationApiResult(
                "reconf-" + result.fingerprint().substring(0, 20),
                result.serviceVersion(),
                result.status().name(),
                result.cause().name(),
                path.value(),
                Long.toString(before.revision().version()),
                before.revision().etag(),
                knowledgeSnapshot.revision(),
                knowledgeSnapshot.etag(),
                result.fingerprint(),
                result.plan().selections(),
                result.migrations(),
                result.diagnostics());
    }

    private static String requiredEtag(String value) {
        if (value == null || !value.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException(
                    "modelRevision must be a lowercase SHA-256 ETag");
        }
        return value;
    }

    public record PreviousBinding(
            String requirementId,
            String activityName,
            String capabilityName,
            String resourceId) {
        public PreviousBinding {
            requirementId = required(requirementId, "requirementId");
            activityName = required(activityName, "activityName");
            capabilityName = required(capabilityName, "capabilityName");
            resourceId = required(resourceId, "resourceId");
        }
    }

    public record ReconfigurationApiResult(
            String resultId,
            String serviceVersion,
            String status,
            String cause,
            String modelId,
            String modelVersion,
            String revision,
            long knowledgeRevision,
            String knowledgeEtag,
            String fingerprint,
            List<ResourceSelection> selections,
            List<com.kide.synthesis.StateMigrationInstruction> migrations,
            List<SynthesisDiagnostic> diagnostics) {
        public ReconfigurationApiResult {
            selections = List.copyOf(selections);
            migrations = List.copyOf(migrations);
            diagnostics = List.copyOf(diagnostics);
        }
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

    private static String required(String value, String field) {
        value = Objects.requireNonNull(value, field).trim();
        if (value.isEmpty()) throw new IllegalArgumentException(field + " is required");
        return value;
    }

    public static final class NotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
