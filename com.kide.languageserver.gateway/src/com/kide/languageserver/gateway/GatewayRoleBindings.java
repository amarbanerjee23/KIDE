package com.kide.languageserver.gateway;

import java.util.ArrayList;
import java.util.List;

import com.kide.enterprise.authorization.Role;
import com.kide.enterprise.authorization.RoleBinding;
import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.context.EnterpriseId;
import com.kide.enterprise.context.EnterpriseScope;

/** Shared fail-closed role-binding parser for LSP and GLSP gateway runtimes. */
public final class GatewayRoleBindings {
    private GatewayRoleBindings() {
    }

    public static List<RoleBinding> parse(
            String raw,
            List<EnterpriseContext> contexts,
            String configKey) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException(configKey + " is required");
        }
        if (contexts == null || contexts.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one enterprise context is required for gateway authorization");
        }

        List<RoleBinding> result = new ArrayList<>();
        for (String entry : raw.split(";")) {
            if (entry.isBlank()) continue;
            String[] parts = entry.split("\\|", -1);
            if (parts.length != 3) {
                throw new IllegalArgumentException(
                        "Each " + configKey
                                + " entry must be principal|ROLE|scopeId");
            }
            String principal = parts[0].trim();
            if (principal.isEmpty()) {
                throw new IllegalArgumentException(
                        configKey + " contains a blank principal");
            }
            Role role;
            try {
                role = Role.valueOf(parts[1].trim());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        configKey + " contains an invalid role");
            }
            EnterpriseId id = parseContextId(parts[2].trim(), contexts, configKey);
            result.add(RoleBinding.allow(principal, role, id));
        }
        if (result.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one gateway role binding is required");
        }
        return List.copyOf(result);
    }

    private static EnterpriseId parseContextId(
            String raw,
            List<EnterpriseContext> contexts,
            String configKey) {
        for (EnterpriseScope scope : EnterpriseScope.values()) {
            EnterpriseId candidate =
                    EnterpriseId.tryParse(scope, raw).orElse(null);
            if (candidate == null) continue;
            boolean known = contexts.stream()
                    .anyMatch(context -> candidate.equals(context.node(scope).id()));
            if (!known) {
                throw new IllegalArgumentException(
                        configKey + " scope is outside the hosted project registry");
            }
            return candidate;
        }
        throw new IllegalArgumentException(
                configKey + " contains an invalid E04 scope ID");
    }
}
