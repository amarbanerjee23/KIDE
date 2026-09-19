package com.kide.enterprise.audit.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

import com.kide.enterprise.audit.AuditEvent;
import com.kide.enterprise.audit.AuditEventDraft;
import com.kide.enterprise.audit.AuditOutcome;
import com.kide.enterprise.audit.AuditRedactor;
import com.kide.enterprise.audit.AuditSchema;
import com.kide.enterprise.audit.InMemoryAuditLedger;
import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.context.EnterpriseContextResult;
import com.kide.enterprise.context.EnterpriseContextStore;
import com.kide.enterprise.identity.AuthenticationMethod;
import com.kide.enterprise.identity.PrincipalIdentity;
import com.kide.enterprise.identity.PrincipalKind;

public class AuditLedgerTest {
    private static final Clock CLOCK =
            Clock.fixed(Instant.parse("2026-09-19T00:00:00Z"), ZoneOffset.UTC);

    @Test
    public void schemaV1IsExplicitAndFutureVersionsFailClosed() {
        assertEquals(1, AuditSchema.CURRENT_VERSION);
        assertTrue(AuditSchema.isReadable(1));
        assertFalse(AuditSchema.isReadable(2));
    }

    @Test
    public void appendCreatesHashChainWithActorClientAndRevision() throws Exception {
        try (Fixture fixture = Fixture.create()) {
            InMemoryAuditLedger ledger = new InMemoryAuditLedger(CLOCK);
            UUID correlation = UUID.randomUUID();

            AuditEvent first = ledger.append(fixture.draft(
                    "model.validate", "model", "model-a", AuditOutcome.SUCCESS,
                    correlation, null, "rev-41", Map.of("validator", "semantic")));
            AuditEvent second = ledger.append(fixture.draft(
                    "model.synthesize", "model", "model-a", AuditOutcome.SUCCESS,
                    correlation, first.eventId(), "rev-42", Map.of("engine", "deterministic")));

            assertEquals(1L, first.sequence());
            assertEquals(2L, second.sequence());
            assertEquals(first.hash(), second.previousHash());
            assertEquals(correlation, second.correlationId());
            assertEquals(first.eventId(), second.causationId());
            assertEquals("desktop:eclipse", second.clientId());
            assertEquals("rev-42", second.projectRevision());
            assertEquals(fixture.context.project().id().value(), second.projectId());
            assertTrue(ledger.verify());
        }
    }

    @Test
    public void sensitiveAttributesAreRedactedBeforeCommit() throws Exception {
        try (Fixture fixture = Fixture.create()) {
            InMemoryAuditLedger ledger = new InMemoryAuditLedger(CLOCK);
            AuditEvent event = ledger.append(fixture.draft(
                    "provider.connect", "provider", "primary", AuditOutcome.SUCCESS,
                    UUID.randomUUID(), null, "rev-7",
                    Map.of(
                            "access_token", "top-secret-token",
                            "clientSecret", "another-secret",
                            "endpoint", "https://provider.example.test")));

            assertEquals(AuditRedactor.REDACTED, event.attributes().get("access_token"));
            assertEquals(AuditRedactor.REDACTED, event.attributes().get("clientSecret"));
            assertEquals("https://provider.example.test", event.attributes().get("endpoint"));
            assertFalse(event.toString().contains("top-secret-token"));
            assertFalse(event.toString().contains("another-secret"));
        }
    }

    @Test
    public void tamperingBreaksLedgerVerification() throws Exception {
        try (Fixture fixture = Fixture.create()) {
            InMemoryAuditLedger ledger = new InMemoryAuditLedger(CLOCK);
            ledger.append(fixture.draft(
                    "model.read", "model", "model-a", AuditOutcome.SUCCESS,
                    UUID.randomUUID(), null, "rev-1", Map.of()));

            AuditEvent original = ledger.snapshot().get(0);
            AuditEvent tampered = new AuditEvent(
                    original.schemaVersion(),
                    original.sequence(),
                    original.eventId(),
                    original.timestamp(),
                    original.actorPrincipalId(),
                    original.clientId(),
                    "model.delete",
                    original.resourceType(),
                    original.resourceId(),
                    original.outcome(),
                    original.correlationId(),
                    original.causationId(),
                    original.projectId(),
                    original.projectRevision(),
                    original.attributes(),
                    original.previousHash(),
                    original.hash());

            assertFalse(InMemoryAuditLedger.verify(List.of(tampered)));
            assertTrue(ledger.verify());
        }
    }

    @Test
    public void concurrentAppendsHaveOneTotalOrderAndPreserveCorrelation() throws Exception {
        try (Fixture fixture = Fixture.create()) {
            InMemoryAuditLedger ledger = new InMemoryAuditLedger(CLOCK);
            UUID correlation = UUID.randomUUID();
            int workers = 8;
            int perWorker = 25;
            ExecutorService pool = Executors.newFixedThreadPool(workers);
            CountDownLatch start = new CountDownLatch(1);
            List<Future<?>> futures = new ArrayList<>();

            try {
                for (int worker = 0; worker < workers; worker++) {
                    final int workerId = worker;
                    futures.add(pool.submit(() -> {
                        start.await();
                        for (int index = 0; index < perWorker; index++) {
                            ledger.append(fixture.draft(
                                    "model.read", "model", "w" + workerId + "-" + index,
                                    AuditOutcome.SUCCESS, correlation, null, "rev-9",
                                    Map.of("worker", Integer.toString(workerId))));
                        }
                        return null;
                    }));
                }
                start.countDown();
                for (Future<?> future : futures) future.get();
            } finally {
                pool.shutdownNow();
            }

            List<AuditEvent> events = ledger.snapshot();
            assertEquals(workers * perWorker, events.size());
            for (int i = 0; i < events.size(); i++) {
                assertEquals(i + 1L, events.get(i).sequence());
                assertEquals(correlation, events.get(i).correlationId());
            }
            assertTrue(ledger.verify());
        }
    }

    @Test
    public void failureEventUsesReasonCodeWithoutExceptionText() throws Exception {
        try (Fixture fixture = Fixture.create()) {
            InMemoryAuditLedger ledger = new InMemoryAuditLedger(CLOCK);
            AuditEventDraft draft = AuditEventDraft.failure(
                    fixture.principal,
                    "desktop:eclipse",
                    fixture.context,
                    "rev-11",
                    "model.write",
                    "model",
                    "model-a",
                    UUID.randomUUID(),
                    null,
                    "AUTHZ_DENIED");
            AuditEvent event = ledger.append(draft);

            assertEquals(AuditOutcome.FAILURE, event.outcome());
            assertEquals("AUTHZ_DENIED", event.attributes().get("reasonCode"));
            assertFalse(event.attributes().containsKey("exception"));
            assertTrue(ledger.verify());
        }
    }

    @Test
    public void malformedOrOversizedFieldsAreRejected() throws Exception {
        try (Fixture fixture = Fixture.create()) {
            String oversized = "x".repeat(513);
            assertThrows(IllegalArgumentException.class, () -> fixture.draft(
                    "model.read", "model", oversized, AuditOutcome.SUCCESS,
                    UUID.randomUUID(), null, "rev-1", Map.of()));
        }
    }

    private static final class Fixture implements AutoCloseable {
        final Path root;
        final EnterpriseContext context;
        final PrincipalIdentity principal;

        private Fixture(Path root, EnterpriseContext context, PrincipalIdentity principal) {
            this.root = root;
            this.context = context;
            this.principal = principal;
        }

        static Fixture create() throws IOException {
            Path root = Files.createTempDirectory("kide-pr19-audit-");
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project"));
            EnterpriseContextResult result = new EnterpriseContextStore().provision(
                    workspace, project, "Audit Org", "Audit Portfolio", "Audit Project", "Audit Workspace");
            if (!result.isReady()) throw new AssertionError(result.summary());
            PrincipalIdentity principal = new PrincipalIdentity(
                    "offline:audit-user",
                    "Audit User",
                    PrincipalKind.LOCAL_OFFLINE,
                    AuthenticationMethod.LOCAL_OFFLINE,
                    "",
                    "audit-user",
                    Map.of("offline", "true"));
            return new Fixture(root, result.context().get(), principal);
        }

        AuditEventDraft draft(
                String action,
                String resourceType,
                String resourceId,
                AuditOutcome outcome,
                UUID correlationId,
                UUID causationId,
                String revision,
                Map<String, String> attributes) {
            return AuditEventDraft.of(
                    principal,
                    "desktop:eclipse",
                    context,
                    revision,
                    action,
                    resourceType,
                    resourceId,
                    outcome,
                    correlationId,
                    causationId,
                    attributes);
        }

        @Override
        public void close() {
            try (java.util.stream.Stream<Path> stream = Files.walk(root)) {
                stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException ignored) {
                        // Test cleanup only.
                    }
                });
            } catch (IOException ignored) {
                // Test cleanup only.
            }
        }
    }
}
