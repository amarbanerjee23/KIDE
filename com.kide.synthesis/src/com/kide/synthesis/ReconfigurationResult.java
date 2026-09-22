package com.kide.synthesis;

import java.util.List;
import java.util.Objects;

public record ReconfigurationResult(
        String serviceVersion,
        ReconfigurationStatus status,
        ReconfigurationCause cause,
        String fingerprint,
        SynthesisPlan plan,
        List<StateMigrationInstruction> migrations,
        List<SynthesisDiagnostic> diagnostics) {
    public static final String VERSION = "1";

    public ReconfigurationResult {
        serviceVersion = Objects.requireNonNull(serviceVersion, "serviceVersion");
        status = Objects.requireNonNull(status, "status");
        cause = Objects.requireNonNull(cause, "cause");
        fingerprint = Objects.requireNonNull(fingerprint, "fingerprint");
        plan = Objects.requireNonNull(plan, "plan");
        migrations = List.copyOf(migrations == null ? List.of() : migrations);
        diagnostics = List.copyOf(diagnostics == null ? List.of() : diagnostics);
        if (status == ReconfigurationStatus.NO_SOLUTION && plan.feasible()) {
            throw new IllegalArgumentException("NO_SOLUTION requires an infeasible plan");
        }
        if (status != ReconfigurationStatus.NO_SOLUTION && !plan.feasible()) {
            throw new IllegalArgumentException("feasible reconfiguration status requires feasible plan");
        }
    }
}
