package com.kide.enterprise.authorization;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.kide.enterprise.context.HostedProjectOwnership;
import com.kide.enterprise.context.HostedProjectRegistry;

/**
 * Immutable configured grants plus dynamically discovered, persisted creator
 * grants. REST/LSP/GLSP all evaluate the same policy on every authorization.
 * A malformed registry fails closed rather than silently dropping a tenant.
 */
public final class HostedRegistryAuthorizationPolicyStore implements AuthorizationPolicyStore {
    private final HostedProjectRegistry registry;
    private final List<RoleBinding> configuredBindings;

    public HostedRegistryAuthorizationPolicyStore(
            HostedProjectRegistry registry, List<RoleBinding> configuredBindings) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.configuredBindings = List.copyOf(
                Objects.requireNonNull(configuredBindings, "configuredBindings"));
    }

    @Override
    public AuthorizationPolicySnapshot snapshot() {
        List<RoleBinding> bindings = new ArrayList<>(configuredBindings);
        for (var project : registry.list()) {
            String owner = HostedProjectOwnership.principalId(project);
            if (owner != null) {
                bindings.add(RoleBinding.allow(
                        owner, Role.ADMINISTRATOR, project.context().project().id()));
            }
        }
        return new AuthorizationPolicySnapshot(0, bindings);
    }

    @Override
    public AuthorizationPolicySnapshot replace(long expectedRevision, List<RoleBinding> bindings) {
        throw new UnsupportedOperationException(
                "Hosted creator grants are managed through atomic project provisioning");
    }
}
