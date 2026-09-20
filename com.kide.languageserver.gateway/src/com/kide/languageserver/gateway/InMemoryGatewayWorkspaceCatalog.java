package com.kide.languageserver.gateway;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryGatewayWorkspaceCatalog implements GatewayWorkspaceCatalog {
    private final Map<String, GatewayWorkspaceBinding> bindings = new ConcurrentHashMap<>();

    public void register(GatewayWorkspaceBinding binding) {
        Objects.requireNonNull(binding, "binding");
        bindings.put(binding.context().workspace().id().value(), binding);
    }

    @Override
    public Optional<GatewayWorkspaceBinding> resolve(String workspaceId) {
        if (workspaceId == null || workspaceId.isBlank()) return Optional.empty();
        return Optional.ofNullable(bindings.get(workspaceId.trim()));
    }
}
