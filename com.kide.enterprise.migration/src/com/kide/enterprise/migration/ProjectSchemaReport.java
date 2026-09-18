package com.kide.enterprise.migration;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public final class ProjectSchemaReport {
    private final ProjectSchemaStatus status;
    private final int detectedVersion;
    private final int targetVersion;
    private final String code;
    private final String message;
    private final Path backup;

    ProjectSchemaReport(ProjectSchemaStatus status, int detectedVersion, int targetVersion,
            String code, String message, Path backup) {
        this.status = Objects.requireNonNull(status, "status");
        this.detectedVersion = detectedVersion;
        this.targetVersion = targetVersion;
        this.code = Objects.requireNonNull(code, "code");
        this.message = Objects.requireNonNull(message, "message");
        this.backup = backup;
    }

    public ProjectSchemaStatus status() { return status; }
    public int detectedVersion() { return detectedVersion; }
    public int targetVersion() { return targetVersion; }
    public String code() { return code; }
    public String message() { return message; }
    public Optional<Path> backup() { return Optional.ofNullable(backup); }

    public boolean isCurrent() {
        return status == ProjectSchemaStatus.CURRENT;
    }

    public boolean canMigrate() {
        return status == ProjectSchemaStatus.LEGACY
                || status == ProjectSchemaStatus.MIGRATION_REQUIRED;
    }
}
