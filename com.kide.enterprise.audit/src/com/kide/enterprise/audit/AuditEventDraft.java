package com.kide.enterprise.audit;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.identity.PrincipalIdentity;

public record AuditEventDraft(
        String actorPrincipalId,
        String clientId,
        String action,
        String resourceType,
        String resourceId,
        AuditOutcome outcome,
        UUID correlationId,
        UUID causationId,
        String projectId,
        String projectRevision,
        Map<String, String> attributes) {

    public AuditEventDraft {
        actorPrincipalId = AuditRedactor.sanitize(actorPrincipalId, "actorPrincipalId", 256);
        clientId = AuditRedactor.sanitize(clientId, "clientId", 128);
        action = AuditRedactor.sanitize(action, "action", 128);
        resourceType = AuditRedactor.sanitize(resourceType, "resourceType", 128);
        resourceId = AuditRedactor.sanitize(resourceId, "resourceId", 512);
        Objects.requireNonNull(outcome, "outcome");
        Objects.requireNonNull(correlationId, "correlationId");
        projectId = AuditRedactor.sanitize(projectId, "projectId", 128);
        projectRevision = AuditRedactor.sanitize(projectRevision, "projectRevision", 128);
        attributes = AuditRedactor.redact(attributes);
    }

    public static AuditEventDraft of(
            PrincipalIdentity principal,
            String clientId,
            EnterpriseContext context,
            String projectRevision,
            String action,
            String resourceType,
            String resourceId,
            AuditOutcome outcome,
            UUID correlationId,
            UUID causationId,
            Map<String, String> attributes) {
        Objects.requireNonNull(principal, "principal");
        Objects.requireNonNull(context, "context");
        return new AuditEventDraft(
                principal.id(), clientId, action, resourceType, resourceId, outcome,
                correlationId, causationId, context.project().id().value(),
                projectRevision, attributes);
    }

    public static AuditEventDraft failure(
            PrincipalIdentity principal,
            String clientId,
            EnterpriseContext context,
            String projectRevision,
            String action,
            String resourceType,
            String resourceId,
            UUID correlationId,
            UUID causationId,
            String reasonCode) {
        return of(principal, clientId, context, projectRevision, action, resourceType,
                resourceId, AuditOutcome.FAILURE, correlationId, causationId,
                Map.of("reasonCode", AuditRedactor.sanitize(reasonCode, "reasonCode", 128)));
    }
}
