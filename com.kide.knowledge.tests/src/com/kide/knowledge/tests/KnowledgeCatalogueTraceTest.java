package com.kide.knowledge.tests;

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
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.kide.knowledge.EmbeddedKnowledgeRepository;
import com.kide.knowledge.KnowledgeCatalogueQuery;
import com.kide.knowledge.KnowledgeCatalogueService;
import com.kide.knowledge.KnowledgeDataset;
import com.kide.knowledge.KnowledgeProvenance;
import com.kide.knowledge.KnowledgeRepository;
import com.kide.knowledge.KnowledgeRevisionConflictException;
import com.kide.knowledge.KnowledgeTerm;
import com.kide.knowledge.KnowledgeTraceRelation;
import com.kide.knowledge.KnowledgeTraceService;
import com.kide.knowledge.KnowledgeTraceStore;
import com.kide.knowledge.KnowledgeTriple;
import com.kide.knowledge.KnowledgeVocabulary;

public class KnowledgeCatalogueTraceTest {
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-09-22T06:00:00Z"), ZoneOffset.UTC);

    @Test
    public void catalogueIsDeterministicAndCacheInvalidatesOnKnowledgeRevision() throws Exception {
        Path root = Files.createTempDirectory("kide-pr35-catalogue-");
        try {
            EmbeddedKnowledgeRepository repository = new EmbeddedKnowledgeRepository(root);
            var first = repository.replace(dataset(true), KnowledgeRepository.MISSING_ETAG);
            KnowledgeCatalogueService catalogue = new KnowledgeCatalogueService(repository);

            var capabilityQuery = new KnowledgeCatalogueQuery(
                    "observe",
                    Set.of(KnowledgeVocabulary.CAPABILITY),
                    20);
            var firstResult = catalogue.query(capabilityQuery);
            assertFalse(firstResult.cached());
            assertEquals(first.etag(), firstResult.etag());
            assertEquals(1, firstResult.items().size());
            assertEquals("Observe", firstResult.items().get(0).label());
            assertEquals(
                    List.of("Sensor"),
                    firstResult.items().get(0).properties()
                            .get("https://kide.dev/ontology/v1#role"));

            var repeated = catalogue.query(capabilityQuery);
            assertTrue(repeated.cached());
            assertEquals(firstResult.items(), repeated.items());

            var second = repository.replace(dataset(false), first.etag());
            var afterChange = catalogue.query(new KnowledgeCatalogueQuery("", Set.of(), 20));
            assertFalse(afterChange.cached());
            assertEquals(second.etag(), afterChange.etag());
            assertEquals(List.of("Camera", "Inspect"), afterChange.items().stream()
                    .map(item -> item.label()).toList());
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void traceIdentitySurvivesRebindAndImpactQueriesRemainStable() throws Exception {
        Path root = Files.createTempDirectory("kide-pr35-trace-");
        try {
            EmbeddedKnowledgeRepository repository = new EmbeddedKnowledgeRepository(root);
            var knowledge = repository.replace(dataset(true), KnowledgeRepository.MISSING_ETAG);
            KnowledgeTraceStore traces = new KnowledgeTraceStore(root, CLOCK);

            var created = traces.create(
                    "urn:kide:capability:Observe",
                    "models/flow.activity",
                    "//@activities.0",
                    KnowledgeTraceRelation.REALIZES,
                    "test-authority",
                    "urn:test:catalogue",
                    "offline:engineer",
                    knowledge.etag(),
                    "1".repeat(64),
                    KnowledgeRepository.MISSING_ETAG);
            String traceId = created.links().get(0).id();

            var rebound = traces.rebind(
                    traceId,
                    "renamed/flow.activity",
                    "//@activities.0",
                    "2".repeat(64),
                    "offline:engineer",
                    created.etag());

            assertEquals(traceId, rebound.links().get(0).id());
            assertEquals("renamed/flow.activity", rebound.links().get(0).modelPath());
            assertEquals(1, new KnowledgeTraceService()
                    .impactForKnowledge(rebound, "urn:kide:capability:Observe").size());
            assertEquals(1, new KnowledgeTraceService()
                    .impactForModel(rebound, "renamed/flow.activity").size());

            var issues = new KnowledgeTraceService().validate(
                    rebound,
                    knowledge,
                    path -> "renamed/flow.activity".equals(path));
            assertTrue(issues.isEmpty());

            assertThrows(
                    KnowledgeRevisionConflictException.class,
                    () -> traces.delete(traceId, created.etag()));
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void brokenAndStaleTraceLinksAreReportedWithoutMutation() throws Exception {
        Path root = Files.createTempDirectory("kide-pr35-broken-");
        try {
            EmbeddedKnowledgeRepository repository = new EmbeddedKnowledgeRepository(root);
            var first = repository.replace(dataset(true), KnowledgeRepository.MISSING_ETAG);
            KnowledgeTraceStore traces = new KnowledgeTraceStore(root, CLOCK);
            var traceSnapshot = traces.create(
                    "urn:kide:capability:Observe",
                    "flow.activity",
                    "",
                    KnowledgeTraceRelation.DERIVED_FROM,
                    "test-authority",
                    "urn:test:catalogue",
                    "offline:engineer",
                    first.etag(),
                    "3".repeat(64),
                    KnowledgeRepository.MISSING_ETAG);

            var second = repository.replace(dataset(false), first.etag());
            var issues = new KnowledgeTraceService().validate(
                    traceSnapshot,
                    second,
                    ignored -> false);

            assertEquals(
                    List.of("BROKEN_KNOWLEDGE", "BROKEN_MODEL", "STALE_KNOWLEDGE"),
                    issues.stream().map(issue -> issue.code()).toList());
            assertEquals(1, traces.snapshot().links().size());
        } finally {
            deleteTree(root);
        }
    }

    private static KnowledgeDataset dataset(boolean includeObserve) {
        List<KnowledgeTriple> triples = includeObserve
                ? List.of(
                        type("urn:kide:capability:Observe", KnowledgeVocabulary.CAPABILITY),
                        label("urn:kide:capability:Observe", "Observe"),
                        literal("urn:kide:capability:Observe",
                                "https://kide.dev/ontology/v1#role", "Sensor"),
                        type("urn:kide:device:Camera", KnowledgeVocabulary.DEVICE),
                        label("urn:kide:device:Camera", "Camera"))
                : List.of(
                        type("urn:kide:device:Camera", KnowledgeVocabulary.DEVICE),
                        label("urn:kide:device:Camera", "Camera"),
                        type("urn:kide:workflow:Inspect", KnowledgeVocabulary.WORKFLOW),
                        label("urn:kide:workflow:Inspect", "Inspect"));
        return new KnowledgeDataset(
                "1",
                includeObserve ? "with-observe" : "without-observe",
                "PROJECT",
                "p1",
                new KnowledgeProvenance(
                        "urn:test:catalogue",
                        "test-authority",
                        "tester",
                        CLOCK.millis(),
                        "INTERNAL"),
                triples);
    }

    private static KnowledgeTriple type(String subject, String type) {
        return new KnowledgeTriple(
                subject, KnowledgeVocabulary.RDF_TYPE, KnowledgeTerm.iri(type));
    }

    private static KnowledgeTriple label(String subject, String label) {
        return literal(subject, KnowledgeVocabulary.LABEL, label);
    }

    private static KnowledgeTriple literal(String subject, String predicate, String value) {
        return new KnowledgeTriple(subject, predicate, KnowledgeTerm.literal(value));
    }

    private static void deleteTree(Path root) {
        try (var stream = Files.walk(root)) {
            for (Path path : stream.sorted(Comparator.reverseOrder()).toList()) {
                try { Files.deleteIfExists(path); } catch (IOException ignored) { }
            }
        } catch (IOException ignored) { }
    }
}
