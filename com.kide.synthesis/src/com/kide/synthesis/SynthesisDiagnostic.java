package com.kide.synthesis;

import java.util.List;
import java.util.Objects;

public record SynthesisDiagnostic(
        SynthesisDiagnosticSeverity severity,
        String code,
        String message,
        String subjectId,
        List<String> relatedIds) {
    public SynthesisDiagnostic {
        severity = Objects.requireNonNull(severity, "severity");
        code = required(code, "code");
        message = required(message, "message");
        subjectId = subjectId == null ? "" : subjectId.trim();
        relatedIds = List.copyOf(relatedIds == null ? List.of() : relatedIds);
    }

    public static SynthesisDiagnostic error(
            String code, String message, String subjectId, List<String> relatedIds) {
        return new SynthesisDiagnostic(
                SynthesisDiagnosticSeverity.ERROR, code, message, subjectId, relatedIds);
    }

    public static SynthesisDiagnostic warning(
            String code, String message, String subjectId, List<String> relatedIds) {
        return new SynthesisDiagnostic(
                SynthesisDiagnosticSeverity.WARNING, code, message, subjectId, relatedIds);
    }

    private static String required(String value, String field) {
        value = Objects.requireNonNull(value, field).trim();
        if (value.isEmpty()) throw new IllegalArgumentException(field + " is required");
        return value;
    }
}
