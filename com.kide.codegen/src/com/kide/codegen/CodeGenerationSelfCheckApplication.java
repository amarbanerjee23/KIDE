package com.kide.codegen;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.kide.knowledge.KnowledgeDataset;
import com.kide.knowledge.KnowledgeProvenance;
import com.kide.knowledge.KnowledgeTerm;
import com.kide.knowledge.KnowledgeTriple;

public final class CodeGenerationSelfCheckApplication implements IApplication {
    @Override
    public Object start(IApplicationContext applicationContext) {
        Path project = null;
        try {
            Path output = outputPath(applicationContext);
            project = Files.createTempDirectory("kide-pr38-codegen-");
            Files.writeString(project.resolve("bindings.krl"), source());

            KnowledgeDataset knowledge = new KnowledgeDataset(
                    "1",
                    "codegen-selfcheck",
                    "PROJECT",
                    "P38",
                    new KnowledgeProvenance(
                            "urn:kide:selfcheck:codegen",
                            "KIDE",
                            "selfcheck",
                            Clock.systemUTC().millis(),
                            "INTERNAL"),
                    List.of(new KnowledgeTriple(
                            "urn:kide:device:camera",
                            "https://kide.dev/ontology/v1#providesCapability",
                            KnowledgeTerm.iri("urn:kide:capability:Observe"))));

            GenerationContext context = new GenerationContext(
                    "workflow.activity", "7", "a".repeat(64),
                    3L, "b".repeat(64), "c".repeat(64),
                    "bindings.krl", "2", "d".repeat(64));

            ProjectKrlGenerationEngine engine = new ProjectKrlGenerationEngine();
            GenerationOutput first = engine.generate(
                    project, "bindings.krl", knowledge, context);
            GenerationOutput second = engine.generate(
                    project, "bindings.krl", knowledge, context);
            if (!first.manifest().fingerprint().equals(second.manifest().fingerprint())
                    || !first.manifest().toJson().equals(second.manifest().toJson())
                    || first.artifacts().size() != 1
                    || !java.util.Arrays.equals(
                            first.artifacts().get(0).bytes(),
                            second.artifacts().get(0).bytes())) {
                throw new AssertionError("semantic generation is not reproducible");
            }
            if (!first.artifacts().get(0).utf8().contains(
                    "urn:kide:device:camera")) {
                throw new AssertionError("KRL query-backed binding was not rendered");
            }

            new GenerationWriter().write(output, first);
            System.out.println("KIDE PR38 SEMANTIC CODEGEN SELF-CHECK OK: "
                    + output.toAbsolutePath().normalize());
            return IApplication.EXIT_OK;
        } catch (Throwable failure) {
            String detail = failure.getMessage();
            if (detail == null || detail.isBlank()) detail = "no detail";
            detail = detail.replace('\r', ' ').replace('\n', ' ');
            System.err.println("KIDE PR38 codegen self-check failed: "
                    + failure.getClass().getSimpleName() + " - " + detail);
            return Integer.valueOf(2);
        } finally {
            deleteTree(project);
        }
    }

    @Override
    public void stop() { }

    private static Path outputPath(IApplicationContext context) {
        Object raw = context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
        String[] args = raw instanceof String[] values ? values : new String[0];
        for (int i = 0; i < args.length; i++) {
            if ("--output".equals(args[i]) && i + 1 < args.length) {
                return Path.of(args[i + 1]);
            }
        }
        return Path.of("pr38-generated");
    }

    private static String source() {
        return """
                knowledge CodegenSelfCheck {
                  namespace kide = "https://kide.dev/ontology/v1#";

                  query FindObserve(capability: iri) {
                    match ?device kide:providesCapability ?capability;
                    select ?device;
                  }

                  template JavaBinding(name: string, resource: iri) for java
                    body "public final class ${name} { public static final String RESOURCE = \"${resource}\"; private ${name}() {} }";

                  target ObserveBinding type java {
                    template JavaBinding;
                    output "generated/GeneratedObserveBinding.java";
                    bind name: string = string "GeneratedObserveBinding";
                    bind resource: iri = query FindObserve(iri "urn:kide:capability:Observe").device;
                  }
                }
                """;
    }

    private static void deleteTree(Path root) {
        if (root == null || !Files.exists(root)) return;
        try (var stream = Files.walk(root)) {
            for (Path path : stream.sorted(Comparator.reverseOrder()).toList()) {
                try { Files.deleteIfExists(path); } catch (Exception ignored) { }
            }
        } catch (Exception ignored) { }
    }
}
