package com.kide.enterprise.context;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/** Immutable organization -> portfolio -> project -> workspace context. */
public final class EnterpriseContext {
    public static final int SCHEMA_VERSION = 1;

    private final EnterpriseNode organization;
    private final EnterpriseNode portfolio;
    private final EnterpriseNode project;
    private final EnterpriseNode workspace;

    EnterpriseContext(EnterpriseNode organization, EnterpriseNode portfolio,
            EnterpriseNode project, EnterpriseNode workspace) {
        this.organization = requireScope(organization, EnterpriseScope.ORGANIZATION);
        this.portfolio = requireScope(portfolio, EnterpriseScope.PORTFOLIO);
        this.project = requireScope(project, EnterpriseScope.PROJECT);
        this.workspace = requireScope(workspace, EnterpriseScope.WORKSPACE);
    }

    private static EnterpriseNode requireScope(EnterpriseNode node, EnterpriseScope expected) {
        Objects.requireNonNull(node, expected.token());
        if (node.scope() != expected) {
            throw new IllegalArgumentException("Enterprise node scope mismatch");
        }
        return node;
    }

    public EnterpriseNode organization() {
        return organization;
    }

    public EnterpriseNode portfolio() {
        return portfolio;
    }

    public EnterpriseNode project() {
        return project;
    }

    public EnterpriseNode workspace() {
        return workspace;
    }

    public EnterpriseNode node(EnterpriseScope scope) {
        Objects.requireNonNull(scope, "scope");
        switch (scope) {
        case ORGANIZATION:
            return organization;
        case PORTFOLIO:
            return portfolio;
        case PROJECT:
            return project;
        case WORKSPACE:
            return workspace;
        default:
            throw new IllegalStateException("Unsupported enterprise scope");
        }
    }

    /** Safe identity-only snapshot suitable for later authorization/audit layers. */
    public Map<EnterpriseScope, EnterpriseId> identitySnapshot() {
        Map<EnterpriseScope, EnterpriseId> result = new EnumMap<>(EnterpriseScope.class);
        result.put(EnterpriseScope.ORGANIZATION, organization.id());
        result.put(EnterpriseScope.PORTFOLIO, portfolio.id());
        result.put(EnterpriseScope.PROJECT, project.id());
        result.put(EnterpriseScope.WORKSPACE, workspace.id());
        return java.util.Collections.unmodifiableMap(result);
    }
}
