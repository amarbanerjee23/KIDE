package com.kide.enterprise.modelrepo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public final class ModelRepositorySelfCheckApplication implements IApplication {
    private static final String MISSING = "0".repeat(64);

    @Override
    public Object start(IApplicationContext applicationContext) {
        Path root = null;
        try {
            root = Files.createTempDirectory("kide-pr21-modelrepo-");
            ModelPath path = new ModelPath("models/selfcheck.dml");
            FileModelRepository repository = new FileModelRepository(root);

            try (ModelTransaction tx = repository.beginTransaction()) {
                tx.write(path, "DataModel SelfCheck {}".getBytes(StandardCharsets.UTF_8), MISSING);
                tx.commit();
            }
            ModelSnapshot first = repository.read(path).orElseThrow();
            if (first.revision().version() != 1L) return fail("unexpected first revision");

            try (ModelTransaction tx = repository.beginTransaction()) {
                tx.write(path, "DataModel SelfCheck { primitives {} }".getBytes(StandardCharsets.UTF_8),
                        first.revision().etag());
                tx.commit();
            }
            ModelSnapshot second = repository.read(path).orElseThrow();
            if (second.revision().version() != 2L) return fail("revision did not advance");
            if (first.revision().etag().equals(second.revision().etag())) return fail("etag did not change");

            try (ModelTransaction stale = repository.beginTransaction()) {
                stale.write(path, "stale".getBytes(StandardCharsets.UTF_8), first.revision().etag());
                try {
                    stale.commit();
                    return fail("stale write was accepted");
                } catch (RevisionConflictException expected) {
                    // strict optimistic concurrency confirmed
                }
            }

            FileModelRepository restarted = new FileModelRepository(root);
            if (!restarted.read(path).orElseThrow().revision().etag().equals(second.revision().etag())) {
                return fail("restart changed committed revision");
            }

            System.out.println("KIDE PR21 MODEL REPOSITORY SELF-CHECK OK");
            return IApplication.EXIT_OK;
        } catch (Exception e) {
            return fail("guarded runtime failure: " + e.getClass().getSimpleName());
        } finally {
            deleteTree(root);
        }
    }

    @Override
    public void stop() { }

    private static Integer fail(String message) {
        System.err.println("KIDE PR21 MODEL REPOSITORY SELF-CHECK FAILED: " + message);
        return Integer.valueOf(2);
    }

    private static void deleteTree(Path root) {
        if (root == null || !Files.exists(root)) return;
        try (java.util.stream.Stream<Path> stream = Files.walk(root)) {
            stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // Temporary self-check cleanup only.
                }
            });
        } catch (IOException ignored) {
            // Temporary self-check cleanup only.
        }
    }
}
