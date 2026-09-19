package com.kide.enterprise.authorization;

import java.util.Objects;

import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.context.EnterpriseScope;
import com.kide.enterprise.identity.AuthenticatedSession;

public final class AuthorizationEnforcer {
    private final AuthorizationService service;

    public AuthorizationEnforcer(AuthorizationService service) {
        this.service = Objects.requireNonNull(service, "service");
    }

    public void require(
            AuthenticatedSession session,
            EnterpriseContext context,
            EnterpriseScope targetScope,
            Permission permission) {
        Objects.requireNonNull(session, "session");
        session.requireActive();
        AuthorizationDecision decision =
                service.decide(session.principal(), context, targetScope, permission);
        if (!decision.allowed()) {
            throw new AccessDeniedException(decision.reasonCode());
        }
    }

    public boolean isAllowed(
            AuthenticatedSession session,
            EnterpriseContext context,
            EnterpriseScope targetScope,
            Permission permission) {
        if (session == null || !session.isActive()) return false;
        return service.decide(session.principal(), context, targetScope, permission).allowed();
    }
}
