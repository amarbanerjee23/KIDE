package com.kide.enterprise.authorization;

import java.util.Objects;

import com.kide.enterprise.context.EnterpriseId;

public record RoleBinding(
        String principalId,
        Role role,
        EnterpriseId scopeId,
        BindingEffect effect) {

    public RoleBinding {
        if (principalId == null || principalId.isBlank()) {
            throw new IllegalArgumentException("principalId is required");
        }
        Objects.requireNonNull(role, "role");
        Objects.requireNonNull(scopeId, "scopeId");
        Objects.requireNonNull(effect, "effect");
    }

    public static RoleBinding allow(String principalId, Role role, EnterpriseId scopeId) {
        return new RoleBinding(principalId, role, scopeId, BindingEffect.ALLOW);
    }

    public static RoleBinding deny(String principalId, Role role, EnterpriseId scopeId) {
        return new RoleBinding(principalId, role, scopeId, BindingEffect.DENY);
    }
}
