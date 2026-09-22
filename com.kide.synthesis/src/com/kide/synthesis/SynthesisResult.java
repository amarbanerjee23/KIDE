package com.kide.synthesis;

import java.util.List;
import java.util.Objects;

import mncModel.Model;

public record SynthesisResult(
        String serviceVersion,
        SynthesisStatus status,
        String fingerprint,
        SynthesisPlan plan,
        List<SynthesisDiagnostic> diagnostics,
        Model controllerModel) {
    public static final String VERSION = "1";

    public SynthesisResult {
        serviceVersion = Objects.requireNonNull(serviceVersion, "serviceVersion");
        status = Objects.requireNonNull(status, "status");
        fingerprint = Objects.requireNonNull(fingerprint, "fingerprint");
        plan = Objects.requireNonNull(plan, "plan");
        diagnostics = List.copyOf(diagnostics == null ? List.of() : diagnostics);
        if (status == SynthesisStatus.SUCCESS && controllerModel == null) {
            throw new IllegalArgumentException("successful synthesis requires a controller model");
        }
    }
}
