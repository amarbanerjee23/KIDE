package com.kide.enterprise.authorization;

public final class AccessDeniedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AccessDeniedException(String reasonCode) {
        super("Access denied: " + reasonCode);
    }
}
