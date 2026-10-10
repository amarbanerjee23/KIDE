package com.kide.enterprise.context;

import java.nio.file.Path;
import java.util.Objects;

/**
 * One hosted project registration backed by canonical project/workspace roots.
 */
public final class HostedProjectRegistration {
    private final EnterpriseContext context;
    private final Path projectRoot;
    private final Path workspaceRoot;

    public HostedProjectRegistration(
            EnterpriseContext context, Path projectRoot, Path workspaceRoot) {
        this.context = Objects.requireNonNull(context, "context");
        this.projectRoot = Objects.requireNonNull(projectRoot, "projectRoot")
                .toAbsolutePath().normalize();
        this.workspaceRoot = Objects.requireNonNull(workspaceRoot, "workspaceRoot")
                .toAbsolutePath().normalize();
    }

    public EnterpriseContext context() {
        return context;
    }

    public Path projectRoot() {
        return projectRoot;
    }

    public Path workspaceRoot() {
        return workspaceRoot;
    }
}
