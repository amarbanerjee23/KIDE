package com.kide.enterprise.authorization;

import java.util.Map;
import java.util.Objects;

import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.context.EnterpriseId;
import com.kide.enterprise.context.EnterpriseScope;
import com.kide.enterprise.identity.PrincipalIdentity;

public final class AuthorizationService {
    private final AuthorizationPolicyStore store;

    public AuthorizationService(AuthorizationPolicyStore store) {
        this.store = Objects.requireNonNull(store, "store");
    }

    public AuthorizationDecision decide(
            PrincipalIdentity principal,
            EnterpriseContext context,
            EnterpriseScope targetScope,
            Permission permission) {
        Objects.requireNonNull(principal, "principal");
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(targetScope, "targetScope");
        Objects.requireNonNull(permission, "permission");

        Map<EnterpriseScope, EnterpriseId> lineage = context.identitySnapshot();
        EnterpriseId target = lineage.get(targetScope);
        if (target == null) return AuthorizationDecision.deny("DENY_UNKNOWN_TARGET");

        boolean allowed = false;
        for (RoleBinding binding : store.snapshot().bindings()) {
            if (!principal.id().equals(binding.principalId())) continue;
            if (!binding.role().grants(permission)) continue;
            if (!applies(binding.scopeId(), lineage, targetScope)) continue;

            if (binding.effect() == BindingEffect.DENY) {
                return AuthorizationDecision.deny("DENY_EXPLICIT_BINDING");
            }
            allowed = true;
        }
        return allowed
                ? AuthorizationDecision.allow()
                : AuthorizationDecision.deny("DENY_NO_MATCHING_GRANT");
    }

    private static boolean applies(
            EnterpriseId bindingScope,
            Map<EnterpriseScope, EnterpriseId> lineage,
            EnterpriseScope targetScope) {
        EnterpriseScope bindingLevel = bindingScope.scope();
        if (ordinal(bindingLevel) > ordinal(targetScope)) return false;
        EnterpriseId lineageId = lineage.get(bindingLevel);
        return bindingScope.equals(lineageId);
    }

    private static int ordinal(EnterpriseScope scope) {
        return switch (scope) {
            case ORGANIZATION -> 0;
            case PORTFOLIO -> 1;
            case PROJECT -> 2;
            case WORKSPACE -> 3;
        };
    }
}
