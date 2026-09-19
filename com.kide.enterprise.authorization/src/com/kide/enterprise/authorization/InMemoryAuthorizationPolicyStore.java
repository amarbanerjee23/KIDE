package com.kide.enterprise.authorization;

import java.util.List;

public final class InMemoryAuthorizationPolicyStore implements AuthorizationPolicyStore {
    private AuthorizationPolicySnapshot snapshot;

    public InMemoryAuthorizationPolicyStore(List<RoleBinding> initialBindings) {
        this.snapshot = new AuthorizationPolicySnapshot(0, initialBindings);
    }

    @Override
    public synchronized AuthorizationPolicySnapshot snapshot() {
        return snapshot;
    }

    @Override
    public synchronized AuthorizationPolicySnapshot replace(long expectedRevision, List<RoleBinding> bindings) {
        if (snapshot.revision() != expectedRevision) {
            throw new IllegalStateException("authorization policy revision conflict");
        }
        snapshot = new AuthorizationPolicySnapshot(expectedRevision + 1, bindings);
        return snapshot;
    }
}
