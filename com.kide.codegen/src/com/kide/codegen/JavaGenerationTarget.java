package com.kide.codegen;

import java.nio.charset.StandardCharsets;

public final class JavaGenerationTarget implements GenerationTarget {
    public static final String VERSION = "1";

    @Override
    public String id() {
        return "java";
    }

    @Override
    public String version() {
        return VERSION;
    }

    @Override
    public GeneratedArtifact generate(
            String targetName,
            String templateName,
            String outputPath,
            String renderedContent) {
        final String safe;
        try {
            safe = GenerationPaths.requireSafeRelative(outputPath);
        } catch (IllegalArgumentException e) {
            throw new GenerationException(e.getMessage(), e);
        }
        if (!safe.endsWith(".java")) {
            throw new GenerationException("java target output must end with .java");
        }
        if (renderedContent.indexOf('\0') >= 0) {
            throw new GenerationException("generated Java source contains a NUL byte");
        }
        return new GeneratedArtifact(
                safe,
                "text/x-java-source",
                renderedContent.getBytes(StandardCharsets.UTF_8),
                id(),
                version(),
                targetName,
                templateName);
    }
}
