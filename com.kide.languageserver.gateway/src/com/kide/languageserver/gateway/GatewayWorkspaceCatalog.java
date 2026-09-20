package com.kide.languageserver.gateway;

import java.util.Optional;

public interface GatewayWorkspaceCatalog {
    Optional<GatewayWorkspaceBinding> resolve(String workspaceId);
}
