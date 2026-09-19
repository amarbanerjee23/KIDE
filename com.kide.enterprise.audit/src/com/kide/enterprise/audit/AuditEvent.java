package com.kide.enterprise.audit;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record AuditEvent(
        int schemaVersion,
        long sequence,
        UUID eventId,
        Instant timestamp,
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
        Map<String, String> attributes,
        String previousHash,
        String hash) {

    public AuditEvent {
        if (!AuditSchema.isReadable(schemaVersion)) {
            throw new IllegalArgumentException("unsupported audit schema version");
        }
        if (sequence < 1) throw new IllegalArgumentException("sequence must be positive");
        Objects.requireNonNull(eventId, "eventId");
        Objects.requireNonNull(timestamp, "timestamp");
        actorPrincipalId = AuditRedactor.sanitize(actorPrincipalId, "actorPrincipalId", 256);
        clientId = AuditRedactor.sanitize(clientId, "clientId", 128);
        action = AuditRedactor.sanitize(action, "action", 128);
        resourceType = AuditRedactor.sanitize(resourceType, "resourceType", 128);
        resourceId = AuditRedactor.sanitize(resourceId, "resourceId", 512);
        Objects.requireNonNull(outcome, "outcome");
        Objects.requireNonNull(correlationId, "correlationId");
        projectId = AuditRedactor.sanitize(projectId, "projectId", 128);
        projectRevision = AuditRedactor.sanitize(projectRevision, "projectRevision", 128);
        attributes = Map.copyOf(attributes == null ? Map.of() : attributes);
        previousHash = requiredHash(previousHash, "previousHash");
        hash = requiredHash(hash, "hash");
    }

    private static String requiredHash(String value, String field) {
        if (value == null || !value.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException(field + " must be a lowercase SHA-256 hex value");
        }
        return value;
    }
}
