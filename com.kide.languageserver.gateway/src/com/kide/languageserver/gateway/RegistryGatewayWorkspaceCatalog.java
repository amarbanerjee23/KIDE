package com.kide.languageserver.gateway;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.context.HostedProjectRegistration;
import com.kide.enterprise.context.HostedProjectRegistry;

/**
 * Dynamic gateway catalog backed by the shared hosted-project registry.
 * Resolution intentionally re-reads the registry so newly registered projects
 * become available without restarting the LSP/GLSP processes.
 */
public final class RegistryGatewayWorkspaceCatalog implements GatewayWorkspaceCatalog {
    private final HostedProjectRegistry registry;

    public RegistryGatewayWorkspaceCatalog(HostedProjectRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    @Override
    public Optional<GatewayWorkspaceBinding> resolve(String workspaceId) {
        return registry.resolveWorkspace(workspaceId)
                .map(this::binding);
    }

    public List<GatewayWorkspaceBinding> bindings() {
        return registry.list().stream()
                .map(this::binding)
                .toList();
    }

    public List<EnterpriseContext> contexts() {
        return registry.list().stream()
                .map(HostedProjectRegistration::context)
                .toList();
    }

    private GatewayWorkspaceBinding binding(HostedProjectRegistration registration) {
        return new GatewayWorkspaceBinding(
                registration.context(),
                registration.projectRoot());
    }
}
