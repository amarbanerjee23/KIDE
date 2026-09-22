package com.kide.enterprise.authorization;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum Role {
    ADMINISTRATOR(EnumSet.allOf(Permission.class)),
    ENGINEER(EnumSet.of(
            Permission.CONTEXT_READ,
            Permission.PROJECT_READ,
            Permission.MODEL_READ,
            Permission.MODEL_WRITE,
            Permission.MODEL_VALIDATE,
            Permission.MODEL_SYNTHESIZE,
            Permission.WORKSPACE_OPEN,
            Permission.WORKSPACE_WRITE,
            Permission.LSP_CONNECT,
            Permission.API_ACCESS,
            Permission.EVIDENCE_READ,
            Permission.EVIDENCE_WRITE,
            Permission.COLLABORATION_READ,
            Permission.COLLABORATION_WRITE,
            Permission.REVIEW_COMMENT)),
    REVIEWER(EnumSet.of(
            Permission.CONTEXT_READ,
            Permission.PROJECT_READ,
            Permission.MODEL_READ,
            Permission.MODEL_VALIDATE,
            Permission.WORKSPACE_OPEN,
            Permission.LSP_CONNECT,
            Permission.API_ACCESS,
            Permission.EVIDENCE_READ,
            Permission.COLLABORATION_READ,
            Permission.REVIEW_COMMENT,
            Permission.REVIEW_APPROVE)),
    VIEWER(EnumSet.of(
            Permission.CONTEXT_READ,
            Permission.PROJECT_READ,
            Permission.MODEL_READ,
            Permission.WORKSPACE_OPEN,
            Permission.API_ACCESS,
            Permission.EVIDENCE_READ,
            Permission.COLLABORATION_READ)),
    SERVICE_OPERATOR(EnumSet.of(
            Permission.CONTEXT_READ,
            Permission.PROJECT_READ,
            Permission.MODEL_READ,
            Permission.MODEL_WRITE,
            Permission.MODEL_VALIDATE,
            Permission.MODEL_SYNTHESIZE,
            Permission.WORKSPACE_OPEN,
            Permission.WORKSPACE_WRITE,
            Permission.LSP_CONNECT,
            Permission.API_ACCESS,
            Permission.EVIDENCE_READ,
            Permission.EVIDENCE_WRITE,
            Permission.COLLABORATION_READ,
            Permission.COLLABORATION_WRITE,
            Permission.REVIEW_COMMENT,
            Permission.REVIEW_APPROVE));

    private final Set<Permission> permissions;

    Role(EnumSet<Permission> permissions) {
        this.permissions = Collections.unmodifiableSet(EnumSet.copyOf(permissions));
    }

    public boolean grants(Permission permission) {
        return permissions.contains(permission);
    }

    public Set<Permission> permissions() {
        return permissions;
    }
}
