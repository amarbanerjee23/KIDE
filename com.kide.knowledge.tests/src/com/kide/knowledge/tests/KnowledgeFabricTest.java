package com.kide.knowledge.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import com.kide.knowledge.DefaultKnowledgeShapes;
import com.kide.knowledge.EmbeddedKnowledgeRepository;
import com.kide.knowledge.KnowledgeDataset;
import com.kide.knowledge.KnowledgeFormat;
import com.kide.knowledge.KnowledgeInterchange;
import com.kide.knowledge.KnowledgeProvenance;
import com.kide.knowledge.KnowledgeRepository;
import com.kide.knowledge.KnowledgeRevisionConflictException;
import com.kide.knowledge.KnowledgeShaclValidator;
import com.kide.knowledge.KnowledgeTerm;
import com.kide.knowledge.KnowledgeTriple;
import com.kide.knowledge.KnowledgeVocabulary;
import com.kide.knowledge.LegacyKnowledgeMigrator;
import com.kide.knowledge.SparqlEndpointAdapter;

public class KnowledgeFabricTest {
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-09-22T00:00:00Z"), ZoneOffset.UTC);

    @Test
    public void embeddedRepositoryPersistsAndRejectsStaleRevision() throws Exception {
        Path root = Files.createTempDirectory("kide-pr34-repo-");
        try {
            KnowledgeDataset dataset = validDataset();
            EmbeddedKnowledgeRepository first = new EmbeddedKnowledgeRepository(root);
            var written = first.replace(dataset, KnowledgeRepository.MISSING_ETAG);
            var reopened = new EmbeddedKnowledgeRepository(root).snapshot().orElseThrow();
            assertEquals(written.etag(), reopened.etag());
            assertEquals(1L, reopened.revision());
            assertThrows(KnowledgeRevisionConflictException.class,
                    () -> first.replace(dataset, KnowledgeRepository.MISSING_ETAG));
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void standardsFacingFormatsRoundTripSameTriples() {
        KnowledgeInterchange interchange = new KnowledgeInterchange();
        KnowledgeDataset dataset = validDataset();
        for (KnowledgeFormat format : KnowledgeFormat.values()) {
            String encoded = interchange.write(dataset, format);
            KnowledgeDataset parsed = interchange.parse(
                    encoded, format, "roundtrip", "PROJECT", "p1",
                    provenance(format.name()));
            assertEquals(dataset.triples(), parsed.triples());
        }
    }

    @Test
    public void shaclProfileRejectsMissingRequiredLabel() {
        KnowledgeDataset shapes = DefaultKnowledgeShapes.create("p1", CLOCK);
        KnowledgeDataset invalid = new KnowledgeDataset(
                "1", "invalid", "PROJECT", "p1", provenance("INTERNAL"),
                List.of(new KnowledgeTriple(
                        "urn:kide:capability:MissingLabel",
                        KnowledgeVocabulary.RDF_TYPE,
                        KnowledgeTerm.iri(KnowledgeVocabulary.CAPABILITY))));
        var report = new KnowledgeShaclValidator().validate(invalid, shapes);
        assertFalse(report.conforms());
        assertEquals(KnowledgeVocabulary.LABEL, report.issues().get(0).path());
    }

    @Test
    public void legacyMigrationPreservesThesisConceptClasses() {
        String legacy = "<root><capability name=\"C\"/><interface name=\"I\"/>"
                + "<behavior name=\"B\"/><interaction name=\"X\"/>"
                + "<workflow name=\"W\"/><device name=\"D\"/></root>";
        KnowledgeDataset migrated = new LegacyKnowledgeMigrator().migrateXmi(
                legacy, "legacy", "PROJECT", "p1", "legacy:test.xmi",
                "research", "tester", CLOCK);
        assertEquals(12, migrated.triples().size());
        assertTrue(new KnowledgeShaclValidator()
                .validate(migrated, DefaultKnowledgeShapes.create("p1", CLOCK)).conforms());
    }

    @Test
    public void externalSparqlRequiresSecureTransportOutsideLoopback() {
        assertThrows(IllegalArgumentException.class,
                () -> new SparqlEndpointAdapter(
                        URI.create("http://example.org/sparql"),
                        HttpClient.newHttpClient(), () -> "", Duration.ofSeconds(1)));
        assertEquals("https",
                new SparqlEndpointAdapter(
                        URI.create("https://example.org/sparql"),
                        HttpClient.newHttpClient(), () -> "", Duration.ofSeconds(1))
                        .endpoint().getScheme());
    }

    private static KnowledgeDataset validDataset() {
        return new KnowledgeDataset(
                "1", "valid", "PROJECT", "p1", provenance("INTERNAL"),
                List.of(
                        new KnowledgeTriple("urn:kide:capability:Observe",
                                KnowledgeVocabulary.RDF_TYPE,
                                KnowledgeTerm.iri(KnowledgeVocabulary.CAPABILITY)),
                        new KnowledgeTriple("urn:kide:capability:Observe",
                                KnowledgeVocabulary.LABEL,
                                KnowledgeTerm.literal("Observe"))));
    }

    private static KnowledgeProvenance provenance(String format) {
        return new KnowledgeProvenance(
                "urn:test", "test-authority", "tester", CLOCK.millis(), format);
    }

    private static void deleteTree(Path root) {
        try (var stream = Files.walk(root)) {
            for (Path path : stream.sorted(Comparator.reverseOrder()).toList()) {
                try { Files.deleteIfExists(path); } catch (IOException ignored) { }
            }
        } catch (IOException ignored) { }
    }
}
