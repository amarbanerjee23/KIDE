package com.kide.enterprise.configuration;

import java.util.Arrays;
import java.util.Objects;

/** Short-lived secret material. Close promptly to overwrite the in-memory copy. */
public final class SecretValue implements AutoCloseable {
    private char[] value;

    public SecretValue(char[] value) {
        Objects.requireNonNull(value, "value");
        this.value = Arrays.copyOf(value, value.length);
    }

    public synchronized char[] copy() {
        ensureOpen();
        return Arrays.copyOf(value, value.length);
    }

    public synchronized int length() {
        ensureOpen();
        return value.length;
    }

    @Override
    public synchronized void close() {
        if (value != null) {
            Arrays.fill(value, '\0');
            value = null;
        }
    }

    private void ensureOpen() {
        if (value == null) {
            throw new IllegalStateException("Secret value has been closed");
        }
    }

    @Override
    public String toString() {
        return "[REDACTED]";
    }
}
