package com.kide.codegen;

import java.nio.file.Path;
import java.util.Objects;

public final class GenerationPaths {
    private GenerationPaths() { }

    public static String requireSafeRelative(String raw) {
        raw = Objects.requireNonNull(raw, "path").trim();
        if (raw.isEmpty() || raw.length() > 512 || raw.indexOf('\0') >= 0
                || raw.indexOf('\\') >= 0 || raw.startsWith("/")
                || raw.matches("^[A-Za-z]:.*")) {
            throw new IllegalArgumentException("generated path must be a portable relative path");
        }
        Path path = Path.of(raw).normalize();
        if (path.isAbsolute() || path.startsWith("..") || path.toString().equals("..")
                || raw.startsWith(".kide/") || raw.equals(".kide")
                || raw.contains("/./") || raw.endsWith("/..")) {
            throw new IllegalArgumentException("generated path escapes the generation sandbox");
        }
        return path.toString().replace('\\', '/');
    }
}
