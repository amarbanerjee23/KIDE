package com.kide.enterprise.authorization;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.context.EnterpriseScope;
import com.kide.enterprise.identity.AuthenticatedSession;

/**
 * UI-facing projection of server authorization. Hiding a control is never an
 * authorization decision; the matching server operation must still use an enforcer.
 */
public final class AuthorizationAffordances {
    private final AuthorizationEnforcer enforcer;

    public AuthorizationAffordances(AuthorizationEnforcer enforcer) {
        this.enforcer = java.util.Objects.requireNonNull(enforcer, "enforcer");
    }

    public Set<Permission> visibleActions(
            AuthenticatedSession session,
            EnterpriseContext context,
            EnterpriseScope scope) {
        EnumSet<Permission> visible = EnumSet.noneOf(Permission.class);
        for (Permission permission : Permission.values()) {
            if (enforcer.isAllowed(session, context, scope, permission)) {
                visible.add(permission);
            }
        }
        return Collections.unmodifiableSet(visible);
    }
}
