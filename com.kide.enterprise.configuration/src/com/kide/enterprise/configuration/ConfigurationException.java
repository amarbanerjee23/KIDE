package com.kide.enterprise.configuration;

/** Raised when enterprise configuration is invalid or cannot be resolved safely. */
public final class ConfigurationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
