package com.kide.languageserver.gateway;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.kide.enterprise.context.EnterpriseContext;

public final class InMemoryGatewayWorkspaceCatalog implements GatewayWorkspaceCatalog {
    private final Map<String, EnterpriseContext> contexts = new ConcurrentHashMap<>();

    public void register(EnterpriseContext context) {
        Objects.requireNonNull(context, "context");
        contexts.put(context.workspace().id().value(), context);
    }

    @Override
    public Optional<EnterpriseContext> resolve(String workspaceId) {
        if (workspaceId == null || workspaceId.isBlank()) return Optional.empty();
        return Optional.ofNullable(contexts.get(workspaceId.trim()));
    }
}
