package com.kide.codegen;

import java.util.List;
import java.util.Objects;

public record GenerationOutput(
        List<GeneratedArtifact> artifacts,
        GenerationManifest manifest) {
    public GenerationOutput {
        artifacts = List.copyOf(Objects.requireNonNull(artifacts, "artifacts"));
        manifest = Objects.requireNonNull(manifest, "manifest");
    }
}
