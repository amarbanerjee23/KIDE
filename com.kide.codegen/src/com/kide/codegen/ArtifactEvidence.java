package com.kide.codegen;

import java.util.Objects;

public record ArtifactEvidence(
        String path,
        String mediaType,
        long bytes,
        String sha256,
        String targetId,
        String targetVersion,
        String targetName,
        String templateName) {
    public ArtifactEvidence {
        path = GenerationPaths.requireSafeRelative(path);
        mediaType = required(mediaType, "mediaType");
        if (bytes < 0L) throw new IllegalArgumentException("bytes must not be negative");
        sha256 = required(sha256, "sha256");
        if (!sha256.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("sha256 must be lowercase SHA-256");
        }
        targetId = required(targetId, "targetId");
        targetVersion = required(targetVersion, "targetVersion");
        targetName = required(targetName, "targetName");
        templateName = required(templateName, "templateName");
    }

    private static String required(String value, String field) {
        value = Objects.requireNonNull(value, field).trim();
        if (value.isEmpty()) throw new IllegalArgumentException(field + " is required");
        return value;
    }
}
