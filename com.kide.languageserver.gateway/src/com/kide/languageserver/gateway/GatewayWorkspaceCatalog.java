package com.kide.languageserver.gateway;

import java.util.Optional;

import com.kide.enterprise.context.EnterpriseContext;

public interface GatewayWorkspaceCatalog {
    Optional<EnterpriseContext> resolve(String workspaceId);
}
