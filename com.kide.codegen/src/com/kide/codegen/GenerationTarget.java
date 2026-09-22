package com.kide.codegen;

public interface GenerationTarget {
    String id();
    String version();

    GeneratedArtifact generate(
            String targetName,
            String templateName,
            String outputPath,
            String renderedContent);
}
