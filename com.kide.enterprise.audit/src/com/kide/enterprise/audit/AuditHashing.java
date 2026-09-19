package com.kide.enterprise.audit;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.Map;

final class AuditHashing {
    static final String GENESIS_HASH = "0".repeat(64);

    private AuditHashing() { }

    static String hash(String previousHash, AuditEvent eventWithoutTrustedHash) {
        return sha256(previousHash + "\n" + canonicalPayload(eventWithoutTrustedHash));
    }

    static String canonicalPayload(AuditEvent event) {
        StringBuilder builder = new StringBuilder(1024);
        field(builder, Integer.toString(event.schemaVersion()));
        field(builder, Long.toString(event.sequence()));
        field(builder, event.eventId().toString());
        field(builder, event.timestamp().toString());
        field(builder, event.actorPrincipalId());
        field(builder, event.clientId());
        field(builder, event.action());
        field(builder, event.resourceType());
        field(builder, event.resourceId());
        field(builder, event.outcome().name());
        field(builder, event.correlationId().toString());
        field(builder, event.causationId() == null ? "" : event.causationId().toString());
        field(builder, event.projectId());
        field(builder, event.projectRevision());
        event.attributes().entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(entry -> {
                    field(builder, entry.getKey());
                    field(builder, entry.getValue());
                });
        return builder.toString();
    }

    private static void field(StringBuilder builder, String value) {
        String safe = value == null ? "" : value;
        builder.append(safe.length()).append(':').append(safe).append('|');
    }

    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
