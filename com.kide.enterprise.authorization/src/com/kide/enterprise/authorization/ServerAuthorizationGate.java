package com.kide.enterprise.authorization;

import java.util.Objects;

import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.context.EnterpriseScope;
import com.kide.enterprise.identity.AuthenticatedSession;

/**
 * Transport-neutral server-side gate. REST, WebSocket and LSP front ends must
 * pass through this boundary before exposing a project/workspace operation.
 */
public final class ServerAuthorizationGate {
    private final AuthorizationEnforcer enforcer;

    public ServerAuthorizationGate(AuthorizationEnforcer enforcer) {
        this.enforcer = Objects.requireNonNull(enforcer, "enforcer");
    }

    public void requireApiProjectAccess(AuthenticatedSession session, EnterpriseContext context) {
        enforcer.require(session, context, EnterpriseScope.PROJECT, Permission.API_ACCESS);
        enforcer.require(session, context, EnterpriseScope.PROJECT, Permission.PROJECT_READ);
    }

    public void requireWebSocketWorkspaceAccess(AuthenticatedSession session, EnterpriseContext context) {
        enforcer.require(session, context, EnterpriseScope.WORKSPACE, Permission.API_ACCESS);
        enforcer.require(session, context, EnterpriseScope.WORKSPACE, Permission.WORKSPACE_OPEN);
    }

    public void requireLspWorkspaceAccess(AuthenticatedSession session, EnterpriseContext context) {
        enforcer.require(session, context, EnterpriseScope.WORKSPACE, Permission.LSP_CONNECT);
        enforcer.require(session, context, EnterpriseScope.WORKSPACE, Permission.WORKSPACE_OPEN);
    }

    public void requireWorkspaceWrite(AuthenticatedSession session, EnterpriseContext context) {
        enforcer.require(session, context, EnterpriseScope.WORKSPACE, Permission.WORKSPACE_WRITE);
        enforcer.require(session, context, EnterpriseScope.PROJECT, Permission.MODEL_WRITE);
    }
}
