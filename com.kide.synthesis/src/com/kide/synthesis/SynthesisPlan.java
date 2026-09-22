package com.kide.synthesis;

import java.util.List;

public record SynthesisPlan(
        boolean feasible,
        List<ResourceSelection> selections,
        List<SynthesisDiagnostic> diagnostics) {
    public SynthesisPlan {
        selections = List.copyOf(selections == null ? List.of() : selections);
        diagnostics = List.copyOf(diagnostics == null ? List.of() : diagnostics);
        boolean hasErrors = diagnostics.stream()
                .anyMatch(d -> d.severity() == SynthesisDiagnosticSeverity.ERROR);
        if (feasible && hasErrors) {
            throw new IllegalArgumentException("feasible plan cannot contain error diagnostics");
        }
    }
}
