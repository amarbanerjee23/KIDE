package com.kide.synthesis;

import java.util.List;

public record DesignContractReport(boolean valid, List<SynthesisDiagnostic> diagnostics) {
    public DesignContractReport {
        diagnostics = List.copyOf(diagnostics == null ? List.of() : diagnostics);
        boolean hasErrors = diagnostics.stream()
                .anyMatch(d -> d.severity() == SynthesisDiagnosticSeverity.ERROR);
        if (valid && hasErrors) {
            throw new IllegalArgumentException("valid contract report cannot contain errors");
        }
    }
}
