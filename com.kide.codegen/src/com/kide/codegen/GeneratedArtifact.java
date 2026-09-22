package com.kide.codegen;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public record GeneratedArtifact(
        String path,
        String mediaType,
        byte[] bytes,
        String targetId,
        String targetVersion,
        String targetName,
        String templateName) {
    public GeneratedArtifact {
        path = GenerationPaths.requireSafeRelative(path);
        mediaType = required(mediaType, "mediaType");
        bytes = Arrays.copyOf(Objects.requireNonNull(bytes, "bytes"), bytes.length);
        targetId = required(targetId, "targetId");
        targetVersion = required(targetVersion, "targetVersion");
        targetName = required(targetName, "targetName");
        templateName = required(templateName, "templateName");
        if (bytes.length > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("generated artifact exceeds 2 MiB");
        }
    }

    @Override
    public byte[] bytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    public String utf8() {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static String required(String value, String field) {
        value = Objects.requireNonNull(value, field).trim();
        if (value.isEmpty()) throw new IllegalArgumentException(field + " is required");
        return value;
    }
}
