package com.kide.enterprise.authorization;

import java.util.List;

public record AuthorizationPolicySnapshot(long revision, List<RoleBinding> bindings) {
    public AuthorizationPolicySnapshot {
        if (revision < 0) throw new IllegalArgumentException("revision must be non-negative");
        bindings = bindings == null ? List.of() : List.copyOf(bindings);
    }
}
