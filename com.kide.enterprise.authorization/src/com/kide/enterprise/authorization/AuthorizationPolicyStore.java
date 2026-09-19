package com.kide.enterprise.authorization;

import java.util.List;

public interface AuthorizationPolicyStore {
    AuthorizationPolicySnapshot snapshot();

    AuthorizationPolicySnapshot replace(long expectedRevision, List<RoleBinding> bindings);
}
