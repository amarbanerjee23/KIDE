package com.kide.enterprise.server;

import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import com.kide.codegen.GeneratedArtifact;
import com.kide.codegen.GenerationContext;
import com.kide.codegen.GenerationException;
import com.kide.codegen.GenerationHashes;
import com.kide.codegen.GenerationOutput;
import com.kide.codegen.ProjectKrlGenerationEngine;
import com.kide.enterprise.modelrepo.ModelPath;
import com.kide.enterprise.modelrepo.ModelRepository;
import com.kide.enterprise.modelrepo.ModelSnapshot;
import com.kide.enterprise.modelrepo.RevisionConflictException;
import com.kide.knowledge.KnowledgeRepository;
import com.kide.knowledge.KnowledgeRepositoryException;
import com.kide.knowledge.KnowledgeRevisionConflictException;
import com.kide.knowledge.KnowledgeSnapshot;

public final class ProjectGenerationService {
    private static final int MAX_ARTIFACTS = 1_000;
    private static final long MAX_TOTAL_BYTES = 5L * 1024L * 1024L;

    private final Path projectRoot;
    private final ModelRepository models;
    private final KnowledgeRepository knowledge;
    private final ProjectSynthesisService synthesis;
    private final ProjectKrlGenerationEngine engine = new ProjectKrlGenerationEngine();

    public ProjectGenerationService(
            Path projectRoot,
            ModelRepository models,
            KnowledgeRepository knowledge,
            ProjectSynthesisService synthesis) {
        this.projectRoot = Objects.requireNonNull(projectRoot, "projectRoot");
        this.models = Objects.requireNonNull(models, "models");
        this.knowledge = Objects.requireNonNull(knowledge, "knowledge");
        this.synthesis = Objects.requireNonNull(synthesis, "synthesis");
    }

    public ApiResult generate(
            String sourceModelId,
            String sourceRevision,
            String krlModelId,
            String krlRevision,
            String synthesisFingerprint) {
        String expectedSynthesis = requiredSha(
                synthesisFingerprint, "synthesisFingerprint");

        ProjectSynthesisService.ApiResult synthesisResult =
                synthesis.synthesize(sourceModelId, sourceRevision);
        if (!"SUCCESS".equals(synthesisResult.status())) {
            throw new GenerationException(
                    "generation requires a successful deterministic synthesis");
        }
        if (!expectedSynthesis.equals(synthesisResult.fingerprint())) {
            throw new RevisionConflictException(
                    "synthesis fingerprint no longer matches the current source and knowledge");
        }

        ModelPath krlPath = new ModelPath(krlModelId);
        String expectedKrl = requiredSha(krlRevision, "krlRevision");
        ModelSnapshot krlBefore =
                models.read(krlPath).orElseThrow(NotFoundException::new);
        if (!krlBefore.revision().etag().equals(expectedKrl)) {
            throw new RevisionConflictException("stale KRL source revision");
        }

        KnowledgeSnapshot knowledgeBefore = knowledge.snapshot()
                .orElseThrow(() -> new KnowledgeRepositoryException(
                        "knowledge repository is empty"));
        if (knowledgeBefore.revision() != synthesisResult.knowledgeRevision()
                || !knowledgeBefore.etag().equals(synthesisResult.knowledgeEtag())) {
            throw new KnowledgeRevisionConflictException(
                    "knowledge changed after deterministic synthesis");
        }

        GenerationContext context = new GenerationContext(
                synthesisResult.modelId(),
                synthesisResult.modelVersion(),
                synthesisResult.revision(),
                knowledgeBefore.revision(),
                knowledgeBefore.etag(),
                synthesisResult.fingerprint(),
                krlPath.value(),
                Long.toString(krlBefore.revision().version()),
                krlBefore.revision().etag());

        GenerationOutput output = engine.generate(
                projectRoot,
                krlPath.value(),
                knowledgeBefore.dataset(),
                context);
        enforceApiLimits(output);

        ModelSnapshot sourceAfter = models.read(new ModelPath(sourceModelId))
                .orElseThrow(NotFoundException::new);
        ModelSnapshot krlAfter =
                models.read(krlPath).orElseThrow(NotFoundException::new);
        KnowledgeSnapshot knowledgeAfter = knowledge.snapshot()
                .orElseThrow(() -> new KnowledgeRepositoryException(
                        "knowledge repository is empty"));

        if (!sourceAfter.revision().etag().equals(synthesisResult.revision())) {
            throw new RevisionConflictException(
                    "source model changed while generation was running");
        }
        if (!krlAfter.revision().etag().equals(krlBefore.revision().etag())) {
            throw new RevisionConflictException(
                    "KRL source changed while generation was running");
        }
        if (knowledgeAfter.revision() != knowledgeBefore.revision()
                || !knowledgeAfter.etag().equals(knowledgeBefore.etag())) {
            throw new KnowledgeRevisionConflictException(
                    "knowledge changed while generation was running");
        }

        List<ArtifactPayload> artifacts = output.artifacts().stream()
                .map(ProjectGenerationService::payload)
                .toList();
        return new ApiResult(
                "gen-" + output.manifest().fingerprint().substring(0, 20),
                output.manifest().toolchainVersion(),
                output.manifest().fingerprint(),
                synthesisResult.modelId(),
                synthesisResult.modelVersion(),
                synthesisResult.revision(),
                krlPath.value(),
                Long.toString(krlBefore.revision().version()),
                krlBefore.revision().etag(),
                knowledgeBefore.revision(),
                knowledgeBefore.etag(),
                synthesisResult.fingerprint(),
                output.manifest().toJson(),
                artifacts);
    }

    private static ArtifactPayload payload(GeneratedArtifact artifact) {
        byte[] bytes = artifact.bytes();
        return new ArtifactPayload(
                artifact.path(),
                artifact.mediaType(),
                Base64.getEncoder().encodeToString(bytes),
                GenerationHashes.sha256(bytes),
                artifact.targetId(),
                artifact.targetVersion());
    }

    private static void enforceApiLimits(GenerationOutput output) {
        if (output.artifacts().size() > MAX_ARTIFACTS) {
            throw new GenerationException("generated artifact count exceeds API limit");
        }
        long total = 0L;
        for (GeneratedArtifact artifact : output.artifacts()) {
            total = Math.addExact(total, artifact.bytes().length);
            if (total > MAX_TOTAL_BYTES) {
                throw new GenerationException("generated artifact bytes exceed 5 MiB API limit");
            }
        }
    }

    private static String requiredSha(String value, String field) {
        value = Objects.requireNonNull(value, field).trim();
        if (!value.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException(
                    field + " must be a lowercase SHA-256 value");
        }
        return value;
    }

    public record ArtifactPayload(
            String path,
            String mediaType,
            String contentBase64,
            String sha256,
            String targetId,
            String targetVersion) { }

    public record ApiResult(
            String resultId,
            String toolchainVersion,
            String fingerprint,
            String sourceModelId,
            String sourceModelVersion,
            String sourceRevision,
            String krlModelId,
            String krlModelVersion,
            String krlRevision,
            long knowledgeRevision,
            String knowledgeEtag,
            String synthesisFingerprint,
            String manifestJson,
            List<ArtifactPayload> artifacts) {
        public ApiResult {
            artifacts = List.copyOf(artifacts);
        }
    }

    public static final class NotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
