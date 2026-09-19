package com.kide.enterprise.modelrepo;

public final class ModelRepositoryException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ModelRepositoryException(String message) {
        super(message);
    }

    public ModelRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
