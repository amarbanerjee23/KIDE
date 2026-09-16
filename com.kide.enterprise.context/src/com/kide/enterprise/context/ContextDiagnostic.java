package com.kide.enterprise.context;

import java.util.Objects;

/** Sanitized diagnostic returned instead of throwing metadata failures into the workbench. */
public final class ContextDiagnostic {
    private final String code;
    private final String message;

    public ContextDiagnostic(String code, String message) {
        this.code = Objects.requireNonNull(code, "code");
        this.message = Objects.requireNonNull(message, "message");
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    @Override
    public String toString() {
        return code + ": " + message;
    }
}
