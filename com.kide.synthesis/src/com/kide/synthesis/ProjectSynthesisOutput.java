package com.kide.synthesis;

import java.util.Objects;

public record ProjectSynthesisOutput(
        SynthesisResult result,
        String generatedMnc) {
    public ProjectSynthesisOutput {
        result = Objects.requireNonNull(result, "result");
        generatedMnc = generatedMnc == null ? "" : generatedMnc;
        if (result.status() == SynthesisStatus.SUCCESS && generatedMnc.isBlank()) {
            throw new IllegalArgumentException("successful synthesis requires generated MNC");
        }
        if (result.status() != SynthesisStatus.SUCCESS && !generatedMnc.isBlank()) {
            throw new IllegalArgumentException("failed synthesis must not emit generated MNC");
        }
    }
}
