package com.kide.enterprise.modelrepo;

public final class RevisionConflictException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RevisionConflictException(String message) {
        super(message);
    }
}
