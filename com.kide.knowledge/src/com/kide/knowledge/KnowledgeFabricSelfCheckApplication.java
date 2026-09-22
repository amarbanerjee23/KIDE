package com.kide.knowledge;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public final class KnowledgeFabricSelfCheckApplication implements IApplication {
    @Override
    public Object start(IApplicationContext context) {
        Path root = null;
        try {
            root = Files.createTempDirectory("kide-pr34-knowledge-");
            Clock clock = Clock.systemUTC();
            EmbeddedKnowledgeRepository repository = new EmbeddedKnowledgeRepository(root);
            KnowledgeDataset shapes = DefaultKnowledgeShapes.create("selfcheck-project", clock);
            KnowledgeShaclValidator validator = new KnowledgeShaclValidator();
            KnowledgeInterchange interchange = new KnowledgeInterchange();

            KnowledgeDataset seed = sample(clock);
            KnowledgeValidationReport valid = validator.validate(seed, shapes);
            if (!valid.conforms()) throw new AssertionError("valid knowledge failed SHACL");

            KnowledgeSnapshot first = repository.replace(seed, KnowledgeRepository.MISSING_ETAG);
            EmbeddedKnowledgeRepository restarted = new EmbeddedKnowledgeRepository(root);
            KnowledgeSnapshot reopened = restarted.snapshot().orElseThrow();
            if (!first.etag().equals(reopened.etag()) || first.revision() != reopened.revision()) {
                throw new AssertionError("embedded knowledge did not survive restart");
            }

            for (KnowledgeFormat format : KnowledgeFormat.values()) {
                String encoded = interchange.write(seed, format);
                KnowledgeDataset parsed = interchange.parse(
                        encoded, format, "roundtrip-" + format.name(), "PROJECT",
                        "selfcheck-project",
                        new KnowledgeProvenance(
                                "urn:selfcheck:" + format.name(),
                                "KIDE",
                                "selfcheck",
                                clock.millis(),
                                format.name()));
                if (!parsed.triples().equals(seed.triples())) {
                    throw new AssertionError(format + " round-trip changed RDF triples");
                }
            }

            try {
                restarted.replace(seed, KnowledgeRepository.MISSING_ETAG);
                throw new AssertionError("stale knowledge write was accepted");
            } catch (KnowledgeRevisionConflictException expected) {
                // strict optimistic concurrency confirmed
            }

            KnowledgeDataset invalid = new KnowledgeDataset(
                    KnowledgeDataset.CURRENT_SCHEMA,
                    "invalid",
                    "PROJECT",
                    "selfcheck-project",
                    seed.provenance(),
                    List.of(new KnowledgeTriple(
                            "urn:kide:selfcheck:bad",
                            KnowledgeVocabulary.RDF_TYPE,
                            KnowledgeTerm.iri(KnowledgeVocabulary.CAPABILITY))));
            if (validator.validate(invalid, shapes).conforms()) {
                throw new AssertionError("SHACL accepted capability without label");
            }

            String legacy = "<root>"
                    + "<capability name=\"Observe\"/>"
                    + "<interface name=\"Sensor\"/>"
                    + "<behavior name=\"Publish\"/>"
                    + "<interaction name=\"ObserveFlow\"/>"
                    + "<workflow name=\"Inspect\"/>"
                    + "<device name=\"Camera\"/>"
                    + "</root>";
            KnowledgeDataset migrated = new LegacyKnowledgeMigrator().migrateXmi(
                    legacy, "legacy", "PROJECT", "selfcheck-project",
                    "legacy:sample.xmi", "research-fixture", "selfcheck", clock);
            if (migrated.triples().size() != 12 || !validator.validate(migrated, shapes).conforms()) {
                throw new AssertionError("legacy concept migration failed");
            }

            new SparqlEndpointAdapter(
                    URI.create("https://example.invalid/sparql"),
                    HttpClient.newHttpClient(),
                    () -> "",
                    Duration.ofSeconds(2));
            try {
                new SparqlEndpointAdapter(
                        URI.create("http://example.invalid/sparql"),
                        HttpClient.newHttpClient(),
                        () -> "",
                        Duration.ofSeconds(2));
                throw new AssertionError("insecure remote SPARQL endpoint was accepted");
            } catch (IllegalArgumentException expected) {
                // fail-closed transport policy confirmed
            }

            KnowledgeFabricService service = new KnowledgeFabricService(
                    restarted, interchange, validator, shapes, clock);
            if (!service.validateCurrent().conforms()) {
                throw new AssertionError("service validation failed after restart");
            }

            System.out.println("KIDE PR34 KNOWLEDGE FABRIC SELF-CHECK OK");
            return IApplication.EXIT_OK;
        } catch (Throwable failure) {
            System.err.println("KIDE PR34 knowledge fabric self-check failed: "
                    + failure.getClass().getSimpleName());
            return Integer.valueOf(2);
        } finally {
            deleteTree(root);
        }
    }

    @Override
    public void stop() { }

    static KnowledgeDataset sample(Clock clock) {
        List<KnowledgeTriple> triples = new ArrayList<>();
        add(triples, "urn:kide:capability:Observe", KnowledgeVocabulary.CAPABILITY, "Observe");
        add(triples, "urn:kide:interface:Sensor", KnowledgeVocabulary.INTERFACE, "Sensor");
        add(triples, "urn:kide:behavior:Publish", KnowledgeVocabulary.BEHAVIOR, "Publish");
        add(triples, "urn:kide:interaction:ObserveFlow", KnowledgeVocabulary.INTERACTION, "ObserveFlow");
        add(triples, "urn:kide:workflow:Inspect", KnowledgeVocabulary.WORKFLOW, "Inspect");
        add(triples, "urn:kide:device:Camera", KnowledgeVocabulary.DEVICE, "Camera");
        return new KnowledgeDataset(
                KnowledgeDataset.CURRENT_SCHEMA,
                "selfcheck",
                "PROJECT",
                "selfcheck-project",
                new KnowledgeProvenance(
                        "urn:kide:selfcheck",
                        "KIDE",
                        "selfcheck",
                        clock.millis(),
                        "INTERNAL"),
                triples);
    }

    private static void add(List<KnowledgeTriple> triples, String subject, String type, String label) {
        triples.add(new KnowledgeTriple(
                subject, KnowledgeVocabulary.RDF_TYPE, KnowledgeTerm.iri(type)));
        triples.add(new KnowledgeTriple(
                subject, KnowledgeVocabulary.LABEL, KnowledgeTerm.literal(label)));
    }

    private static void deleteTree(Path root) {
        if (root == null || !Files.exists(root)) return;
        try (var stream = Files.walk(root)) {
            for (Path path : stream.sorted(Comparator.reverseOrder()).toList()) {
                try { Files.deleteIfExists(path); } catch (IOException ignored) { }
            }
        } catch (IOException ignored) { }
    }
}
