package com.kide.enterprise.audit;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class InMemoryAuditLedger implements AuditLedger {
    private final Clock clock;
    private final List<AuditEvent> events = new ArrayList<>();

    public InMemoryAuditLedger(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    @Override
    public synchronized AuditEvent append(AuditEventDraft draft) {
        Objects.requireNonNull(draft, "draft");
        long sequence = events.size() + 1L;
        String previousHash = events.isEmpty()
                ? AuditHashing.GENESIS_HASH
                : events.get(events.size() - 1).hash();

        AuditEvent provisional = new AuditEvent(
                AuditSchema.CURRENT_VERSION,
                sequence,
                UUID.randomUUID(),
                Instant.now(clock),
                draft.actorPrincipalId(),
                draft.clientId(),
                draft.action(),
                draft.resourceType(),
                draft.resourceId(),
                draft.outcome(),
                draft.correlationId(),
                draft.causationId(),
                draft.projectId(),
                draft.projectRevision(),
                draft.attributes(),
                previousHash,
                AuditHashing.GENESIS_HASH);

        String hash = AuditHashing.hash(previousHash, provisional);
        AuditEvent committed = new AuditEvent(
                provisional.schemaVersion(),
                provisional.sequence(),
                provisional.eventId(),
                provisional.timestamp(),
                provisional.actorPrincipalId(),
                provisional.clientId(),
                provisional.action(),
                provisional.resourceType(),
                provisional.resourceId(),
                provisional.outcome(),
                provisional.correlationId(),
                provisional.causationId(),
                provisional.projectId(),
                provisional.projectRevision(),
                provisional.attributes(),
                previousHash,
                hash);
        events.add(committed);
        return committed;
    }

    @Override
    public synchronized List<AuditEvent> snapshot() {
        return List.copyOf(events);
    }

    @Override
    public synchronized boolean verify() {
        return verify(events);
    }

    public static boolean verify(List<AuditEvent> candidate) {
        if (candidate == null) return false;
        String previousHash = AuditHashing.GENESIS_HASH;
        long expectedSequence = 1L;
        for (AuditEvent event : candidate) {
            if (event == null
                    || event.sequence() != expectedSequence
                    || !previousHash.equals(event.previousHash())) {
                return false;
            }
            String calculated = AuditHashing.hash(previousHash, event);
            if (!calculated.equals(event.hash())) return false;
            previousHash = event.hash();
            expectedSequence++;
        }
        return true;
    }
}
