package com.kide.languageserver.gateway;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.kide.enterprise.context.EnterpriseContext;

public final class GatewayWorkspaceBinding {
    private final EnterpriseContext context;
    private final Path projectRoot;
    private final Path projectRootReal;

    public GatewayWorkspaceBinding(EnterpriseContext context, Path projectRoot) {
        this.context = Objects.requireNonNull(context, "context");
        this.projectRoot = Objects.requireNonNull(projectRoot, "projectRoot")
                .toAbsolutePath().normalize();
        try {
            if (!Files.isDirectory(this.projectRoot)) {
                throw new IllegalArgumentException("projectRoot must be an existing directory");
            }
            this.projectRootReal = this.projectRoot.toRealPath();
        } catch (IOException e) {
            throw new IllegalArgumentException("projectRoot cannot be canonicalized", e);
        }
    }

    public EnterpriseContext context() {
        return context;
    }

    public Path projectRoot() {
        return projectRoot;
    }

    Path projectRootReal() {
        return projectRootReal;
    }
}
