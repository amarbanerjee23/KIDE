package com.kide.enterprise.modelrepo;

import java.nio.file.Path;
import java.util.Objects;

public record ModelPath(String value) {
    public ModelPath {
        Objects.requireNonNull(value, "value");
        String normalized = value.replace('\\', '/').trim();
        if (normalized.isEmpty() || normalized.startsWith("/") || normalized.startsWith(".kide/")
                || normalized.equals(".kide") || normalized.contains("../") || normalized.equals("..")
                || normalized.contains("/./") || normalized.endsWith("/..")) {
            throw new IllegalArgumentException("model path must be a safe project-relative path");
        }
        Path candidate = Path.of(normalized).normalize();
        if (candidate.isAbsolute() || candidate.startsWith("..")) {
            throw new IllegalArgumentException("model path escapes project root");
        }
        value = candidate.toString().replace('\\', '/');
    }
}
