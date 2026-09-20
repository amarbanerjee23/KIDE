package com.kide.glsp;

import java.nio.file.Path;
import java.util.Objects;

import com.kide.enterprise.modelrepo.FileModelRepository;

public final class KideGlspWorkspace {
    private final Path projectRoot;
    private final Path projectRootReal;
    private final FileModelRepository repository;

    public KideGlspWorkspace(Path projectRoot) {
        try {
            this.projectRoot = Objects.requireNonNull(projectRoot, "projectRoot")
                    .toAbsolutePath().normalize();
            this.projectRootReal = this.projectRoot.toRealPath();
            this.repository = new FileModelRepository(this.projectRoot);
        } catch (java.io.IOException e) {
            throw new IllegalArgumentException("GLSP project root cannot be canonicalized", e);
        }
    }

    public Path projectRoot() { return projectRoot; }
    public Path projectRootReal() { return projectRootReal; }
    public FileModelRepository repository() { return repository; }

    public Path requireProjectPath(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("GLSP source path is required");
        }
        Path candidate = Path.of(raw).toAbsolutePath().normalize();
        if (!candidate.startsWith(projectRoot)) {
            throw new IllegalArgumentException("GLSP path is outside the authorized project");
        }
        Path existing = candidate;
        while (existing != null && !java.nio.file.Files.exists(existing)) {
            existing = existing.getParent();
        }
        if (existing == null) throw new IllegalArgumentException("GLSP path has no authorized ancestor");
        try {
            if (!existing.toRealPath().startsWith(projectRootReal)) {
                throw new IllegalArgumentException("GLSP path escapes the authorized project");
            }
            if (java.nio.file.Files.exists(candidate)
                    && !candidate.toRealPath().startsWith(projectRootReal)) {
                throw new IllegalArgumentException("GLSP path escapes the authorized project");
            }
        } catch (java.io.IOException e) {
            throw new IllegalArgumentException("GLSP path cannot be canonicalized", e);
        }
        return candidate;
    }

    public com.kide.enterprise.modelrepo.ModelPath modelPath(Path path) {
        Path safe = requireProjectPath(path.toString());
        return new com.kide.enterprise.modelrepo.ModelPath(
                projectRoot.relativize(safe).toString().replace('\\', '/'));
    }
}
