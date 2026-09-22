package com.kide.codegen;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record GenerationManifest(
        String schemaVersion,
        String toolchainVersion,
        GenerationContext context,
        String krlModelName,
        Map<String, String> targetVersions,
        List<ArtifactEvidence> artifacts,
        String fingerprint) {
    public static final String SCHEMA_VERSION = "1";
    public static final String TOOLCHAIN_VERSION = "1";

    public GenerationManifest {
        schemaVersion = required(schemaVersion, "schemaVersion");
        toolchainVersion = required(toolchainVersion, "toolchainVersion");
        context = Objects.requireNonNull(context, "context");
        krlModelName = required(krlModelName, "krlModelName");
        targetVersions = Map.copyOf(new java.util.TreeMap<>(
                Objects.requireNonNull(targetVersions, "targetVersions")));
        artifacts = List.copyOf(Objects.requireNonNull(artifacts, "artifacts").stream()
                .sorted(Comparator.comparing(ArtifactEvidence::path)).toList());
        fingerprint = required(fingerprint, "fingerprint");
        if (!fingerprint.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("fingerprint must be lowercase SHA-256");
        }
    }

    public static GenerationManifest create(
            GenerationContext context,
            String krlModelName,
            Map<String, String> targetVersions,
            List<GeneratedArtifact> generated) {
        List<ArtifactEvidence> evidence = generated.stream()
                .sorted(Comparator.comparing(GeneratedArtifact::path))
                .map(artifact -> new ArtifactEvidence(
                        artifact.path(),
                        artifact.mediaType(),
                        artifact.bytes().length,
                        GenerationHashes.sha256(artifact.bytes()),
                        artifact.targetId(),
                        artifact.targetVersion(),
                        artifact.targetName(),
                        artifact.templateName()))
                .toList();
        String canonical = canonical(
                context, krlModelName, targetVersions, evidence);
        return new GenerationManifest(
                SCHEMA_VERSION,
                TOOLCHAIN_VERSION,
                context,
                krlModelName,
                targetVersions,
                evidence,
                GenerationHashes.sha256(canonical));
    }

    public byte[] jsonBytes() {
        return toJson().getBytes(StandardCharsets.UTF_8);
    }

    public String toJson() {
        StringBuilder out = new StringBuilder();
        out.append("{\n")
                .append("  \"schemaVersion\": \"").append(escape(schemaVersion)).append("\",\n")
                .append("  \"toolchainVersion\": \"").append(escape(toolchainVersion)).append("\",\n")
                .append("  \"fingerprint\": \"").append(fingerprint).append("\",\n")
                .append("  \"krlModelName\": \"").append(escape(krlModelName)).append("\",\n")
                .append("  \"source\": {")
                .append("\"modelId\":\"").append(escape(context.sourceModelId())).append("\",")
                .append("\"revision\":\"").append(escape(context.sourceRevision())).append("\",")
                .append("\"etag\":\"").append(context.sourceEtag()).append("\"},\n")
                .append("  \"knowledge\": {")
                .append("\"revision\":").append(context.knowledgeRevision()).append(',')
                .append("\"etag\":\"").append(context.knowledgeEtag()).append("\"},\n")
                .append("  \"synthesis\": {\"fingerprint\":\"")
                .append(context.synthesisFingerprint()).append("\"},\n")
                .append("  \"krl\": {")
                .append("\"modelId\":\"").append(escape(context.krlModelId())).append("\",")
                .append("\"revision\":\"").append(escape(context.krlRevision())).append("\",")
                .append("\"etag\":\"").append(context.krlEtag()).append("\"},\n")
                .append("  \"targetVersions\": {");
        int index = 0;
        for (var entry : new java.util.TreeMap<>(targetVersions).entrySet()) {
            if (index++ > 0) out.append(',');
            out.append("\"").append(escape(entry.getKey())).append("\":\"")
                    .append(escape(entry.getValue())).append("\"");
        }
        out.append("},\n  \"artifacts\": [\n");
        for (int i = 0; i < artifacts.size(); i++) {
            ArtifactEvidence a = artifacts.get(i);
            out.append("    {")
                    .append("\"path\":\"").append(escape(a.path())).append("\",")
                    .append("\"mediaType\":\"").append(escape(a.mediaType())).append("\",")
                    .append("\"bytes\":").append(a.bytes()).append(',')
                    .append("\"sha256\":\"").append(a.sha256()).append("\",")
                    .append("\"targetId\":\"").append(escape(a.targetId())).append("\",")
                    .append("\"targetVersion\":\"").append(escape(a.targetVersion())).append("\",")
                    .append("\"targetName\":\"").append(escape(a.targetName())).append("\",")
                    .append("\"templateName\":\"").append(escape(a.templateName())).append("\"}");
            if (i + 1 < artifacts.size()) out.append(',');
            out.append('\n');
        }
        out.append("  ]\n}\n");
        return out.toString();
    }

    private static String canonical(
            GenerationContext context,
            String krlModelName,
            Map<String, String> targetVersions,
            List<ArtifactEvidence> artifacts) {
        StringBuilder value = new StringBuilder("kide-codegen-v1\n")
                .append(context.sourceModelId()).append('|')
                .append(context.sourceRevision()).append('|')
                .append(context.sourceEtag()).append('\n')
                .append(context.knowledgeRevision()).append('|')
                .append(context.knowledgeEtag()).append('\n')
                .append(context.synthesisFingerprint()).append('\n')
                .append(context.krlModelId()).append('|')
                .append(context.krlRevision()).append('|')
                .append(context.krlEtag()).append('\n')
                .append(krlModelName).append('\n');
        new java.util.TreeMap<>(targetVersions).forEach((id, version) ->
                value.append("target|").append(id).append('|').append(version).append('\n'));
        artifacts.forEach(a -> value.append("artifact|").append(a.path()).append('|')
                .append(a.sha256()).append('|').append(a.targetId()).append('|')
                .append(a.targetVersion()).append('|').append(a.targetName()).append('|')
                .append(a.templateName()).append('\n'));
        return value.toString();
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static String required(String value, String field) {
        value = Objects.requireNonNull(value, field).trim();
        if (value.isEmpty()) throw new IllegalArgumentException(field + " is required");
        return value;
    }
}
