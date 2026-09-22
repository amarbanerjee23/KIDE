package com.kide.synthesis;

public final class ProjectSynthesisException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ProjectSynthesisException(String message) {
        super(message);
    }

    public ProjectSynthesisException(String message, Throwable cause) {
        super(message, cause);
    }
}
