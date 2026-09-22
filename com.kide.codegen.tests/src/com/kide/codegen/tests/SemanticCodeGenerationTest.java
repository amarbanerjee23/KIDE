package com.kide.codegen.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import com.kide.codegen.GeneratedArtifact;
import com.kide.codegen.GenerationContext;
import com.kide.codegen.GenerationException;
import com.kide.codegen.GenerationOutput;
import com.kide.codegen.GenerationTargetRegistry;
import com.kide.codegen.GenerationWriter;
import com.kide.codegen.JavaGenerationTarget;
import com.kide.codegen.KrlGenerationService;
import com.kide.codegen.KrlTemplateRenderer;
import com.kide.codegen.ProjectKrlGenerationEngine;
import com.kide.codegen.SemanticValue;
import com.kide.knowledge.KnowledgeDataset;
import com.kide.knowledge.KnowledgeProvenance;
import com.kide.knowledge.KnowledgeTerm;
import com.kide.knowledge.KnowledgeTriple;

public class SemanticCodeGenerationTest {
    @Test
    public void repeatedProjectGenerationIsByteIdentical() throws Exception {
        Path project = Files.createTempDirectory("kide-pr38-repeat-");
        try {
            Files.writeString(project.resolve("bindings.krl"), validSource());
            var engine = new ProjectKrlGenerationEngine();
            var first = engine.generate(project, "bindings.krl", knowledge(false), context());
            var second = engine.generate(project, "bindings.krl", knowledge(false), context());

            assertEquals(first.manifest().fingerprint(), second.manifest().fingerprint());
            assertEquals(first.manifest().toJson(), second.manifest().toJson());
            assertEquals(1, first.artifacts().size());
            assertEquals(first.artifacts().get(0).utf8(), second.artifacts().get(0).utf8());
            assertTrue(first.artifacts().get(0).utf8().contains("urn:kide:device:camera-a"));
        } finally {
            deleteTree(project);
        }
    }

    @Test
    public void ambiguousQueryBackedBindingFailsClosed() throws Exception {
        Path project = Files.createTempDirectory("kide-pr38-ambiguous-");
        try {
            Files.writeString(project.resolve("bindings.krl"), validSource());
            GenerationException failure = assertThrows(
                    GenerationException.class,
                    () -> new ProjectKrlGenerationEngine().generate(
                            project, "bindings.krl", knowledge(true), context()));
            assertTrue(failure.getMessage().contains("exactly one row"));
        } finally {
            deleteTree(project);
        }
    }

    @Test
    public void malformedPlaceholderCannotEscapeRendererContract() {
        GenerationException failure = assertThrows(
                GenerationException.class,
                () -> new KrlTemplateRenderer().render(
                        "class ${name} { String x = \"${missing}\"; }",
                        java.util.Map.of("name", SemanticValue.literal("Safe"))));
        assertTrue(failure.getMessage().contains("unresolved template placeholder"));
    }

    @Test
    public void javaTargetRejectsTraversalBeforeWriting() {
        var target = new JavaGenerationTarget();
        assertThrows(
                IllegalArgumentException.class,
                () -> target.generate("Bad", "T", "../Bad.java", "class Bad {}"));
    }

    @Test
    public void writerRejectsSymlinkParent() throws Exception {
        Path root = Files.createTempDirectory("kide-pr38-writer-");
        Path outside = Files.createTempDirectory("kide-pr38-outside-");
        try {
            Path link = root.resolve("generated");
            try {
                Files.createSymbolicLink(link, outside);
            } catch (UnsupportedOperationException | java.nio.file.FileSystemException e) {
                return;
            }
            GeneratedArtifact artifact = new JavaGenerationTarget().generate(
                    "Safe", "T", "generated/Safe.java", "public final class Safe {}");
            var context = context();
            var manifest = com.kide.codegen.GenerationManifest.create(
                    context,
                    "Test",
                    GenerationTargetRegistry.defaults().versions(),
                    List.of(artifact));
            GenerationOutput output = new GenerationOutput(List.of(artifact), manifest);

            assertThrows(
                    GenerationException.class,
                    () -> new GenerationWriter().write(root, output));
            assertTrue(Files.list(outside).findAny().isEmpty());
        } finally {
            deleteTree(root);
            deleteTree(outside);
        }
    }

    @Test
    public void invalidKrlOutputPathFailsValidation() throws Exception {
        Path project = Files.createTempDirectory("kide-pr38-invalid-");
        try {
            Files.writeString(project.resolve("bad.krl"), """
                    knowledge Bad {
                      template T(name: string) for java
                        body "class ${name} {}";
                      target Bad type java {
                        template T;
                        output "../Bad.java";
                        bind name: string = string "Bad";
                      }
                    }
                    """);
            GenerationException failure = assertThrows(
                    GenerationException.class,
                    () -> new ProjectKrlGenerationEngine().generate(
                            project, "bad.krl", knowledge(false), context()));
            assertTrue(failure.getMessage().contains("validation failed")
                    || failure.getMessage().contains("sandbox"));
        } finally {
            deleteTree(project);
        }
    }

    private static String validSource() {
        return """
                knowledge Test {
                  namespace kide = "https://kide.dev/ontology/v1#";

                  query FindObserve(capability: iri) {
                    match ?device kide:providesCapability ?capability;
                    select ?device;
                  }

                  template Binding(name: string, resource: iri) for java
                    body "public final class ${name} { public static final String RESOURCE = \\\"${resource}\\\"; private ${name}() {} }";

                  target Observe type java {
                    template Binding;
                    output "generated/GeneratedObserve.java";
                    bind name: string = string "GeneratedObserve";
                    bind resource: iri = query FindObserve(iri "urn:kide:capability:Observe").device;
                  }
                }
                """;
    }

    private static KnowledgeDataset knowledge(boolean ambiguous) {
        java.util.ArrayList<KnowledgeTriple> triples = new java.util.ArrayList<>();
        triples.add(new KnowledgeTriple(
                "urn:kide:device:camera-a",
                "https://kide.dev/ontology/v1#providesCapability",
                KnowledgeTerm.iri("urn:kide:capability:Observe")));
        if (ambiguous) {
            triples.add(new KnowledgeTriple(
                    "urn:kide:device:camera-b",
                    "https://kide.dev/ontology/v1#providesCapability",
                    KnowledgeTerm.iri("urn:kide:capability:Observe")));
        }
        return new KnowledgeDataset(
                "1", "codegen-test", "PROJECT", "P38",
                new KnowledgeProvenance(
                        "urn:test:codegen",
                        "test",
                        "tester",
                        Clock.systemUTC().millis(),
                        "INTERNAL"),
                triples);
    }

    private static GenerationContext context() {
        return new GenerationContext(
                "workflow.activity", "7", "a".repeat(64),
                3L, "b".repeat(64), "c".repeat(64),
                "bindings.krl", "2", "d".repeat(64));
    }

    private static void deleteTree(Path root) {
        if (root == null || !Files.exists(root)) return;
        try (var stream = Files.walk(root)) {
            for (Path path : stream.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        } catch (Exception ignored) { }
    }
}
