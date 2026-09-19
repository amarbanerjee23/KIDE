package com.kide.enterprise.modelrepo.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.Properties;

import org.junit.Test;

import com.kide.enterprise.modelrepo.FileModelRepository;
import com.kide.enterprise.modelrepo.ModelPath;
import com.kide.enterprise.modelrepo.ModelRepositoryException;
import com.kide.enterprise.modelrepo.ModelSnapshot;
import com.kide.enterprise.modelrepo.ModelTransaction;
import com.kide.enterprise.modelrepo.RepositoryLimits;
import com.kide.enterprise.modelrepo.RevisionConflictException;
import com.kide.enterprise.modelrepo.ServerModelRepository;

public class ModelRepositoryTest {
    private static final String MISSING = "0".repeat(64);

    @Test
    public void serverRepositoryPreventsLostUpdates() {
        ServerModelRepository repository = new ServerModelRepository();
        ModelPath path = new ModelPath("models/system.mncspec");

        ModelTransaction first = repository.beginTransaction();
        ModelTransaction second = repository.beginTransaction();
        first.write(path, bytes("first"), MISSING);
        second.write(path, bytes("second"), MISSING);

        first.commit();
        assertThrows(RevisionConflictException.class, second::commit);
        assertArrayEquals(bytes("first"), repository.read(path).orElseThrow().content());
    }

    @Test
    public void fileRepositoryCommitRollbackAndRestartPreserveCanonicalFiles() throws Exception {
        Path root = Files.createTempDirectory("kide-pr21-file-");
        try {
            FileModelRepository repository = new FileModelRepository(root);
            ModelPath path = new ModelPath("models/activity.activity");

            try (ModelTransaction tx = repository.beginTransaction()) {
                tx.write(path, bytes("v1"), MISSING);
                tx.commit();
            }
            ModelSnapshot first = repository.read(path).orElseThrow();
            assertEquals(1L, first.revision().version());

            try (ModelTransaction tx = repository.beginTransaction()) {
                tx.write(path, bytes("not-committed"), first.revision().etag());
                tx.rollback();
            }
            assertArrayEquals(bytes("v1"), repository.read(path).orElseThrow().content());

            FileModelRepository restarted = new FileModelRepository(root);
            assertArrayEquals(bytes("v1"), restarted.read(path).orElseThrow().content());
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void staleFileRevisionFailsWithoutMutation() throws Exception {
        Path root = Files.createTempDirectory("kide-pr21-stale-");
        try {
            FileModelRepository repository = new FileModelRepository(root);
            ModelPath path = new ModelPath("models/data.dml");

            try (ModelTransaction tx = repository.beginTransaction()) {
                tx.write(path, bytes("v1"), MISSING);
                tx.commit();
            }
            ModelSnapshot v1 = repository.read(path).orElseThrow();

            try (ModelTransaction tx = repository.beginTransaction()) {
                tx.write(path, bytes("v2"), v1.revision().etag());
                tx.commit();
            }

            try (ModelTransaction stale = repository.beginTransaction()) {
                stale.write(path, bytes("lost-update"), v1.revision().etag());
                assertThrows(RevisionConflictException.class, stale::commit);
            }
            assertArrayEquals(bytes("v2"), repository.read(path).orElseThrow().content());
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void interruptedCommitIsRolledBackOnRestart() throws Exception {
        Path root = Files.createTempDirectory("kide-pr21-recovery-");
        try {
            Path target = root.resolve("models/controller.mncspec");
            Files.createDirectories(target.getParent());
            Files.write(target, bytes("original"));

            Path journal = root.resolve(".kide/model-repository/transactions/crashed");
            Files.createDirectories(journal.resolve("backup"));
            Files.createDirectories(journal.resolve("staged"));
            Files.write(journal.resolve("backup/0"), bytes("original"));
            Files.write(target, bytes("partial-new-value"));

            Properties manifest = new Properties();
            manifest.setProperty("count", "1");
            manifest.setProperty("entry.0.path", "models/controller.mncspec");
            manifest.setProperty("entry.0.existed", "true");
            manifest.setProperty("entry.0.delete", "false");
            manifest.setProperty("revisions.existed", "false");
            try (OutputStream out = Files.newOutputStream(journal.resolve("manifest.properties"))) {
                manifest.store(out, "simulated interrupted commit");
            }
            Files.writeString(journal.resolve("state"), "COMMITTING", StandardCharsets.UTF_8);

            FileModelRepository recovered = new FileModelRepository(root);
            assertArrayEquals(bytes("original"),
                    recovered.read(new ModelPath("models/controller.mncspec")).orElseThrow().content());
            assertFalse(Files.exists(journal));
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void symlinkAndTraversalAreRejected() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> new ModelPath("../outside.dml"));
        assertThrows(IllegalArgumentException.class, () -> new ModelPath(".kide/secret"));

        Path root = Files.createTempDirectory("kide-pr21-symlink-");
        Path outside = Files.createTempDirectory("kide-pr21-outside-");
        try {
            Path models = Files.createDirectories(root.resolve("models"));
            Path external = Files.createDirectories(outside.resolve("external"));
            Files.write(external.resolve("secret.dml"), bytes("outside"));
            Files.createSymbolicLink(models.resolve("linked"), external);

            FileModelRepository repository = new FileModelRepository(root);
            assertThrows(ModelRepositoryException.class,
                    () -> repository.read(new ModelPath("models/linked/secret.dml")));
        } finally {
            deleteTree(root);
            deleteTree(outside);
        }
    }

    @Test
    public void largeFilesAreRejectedBeforeMutation() throws Exception {
        Path root = Files.createTempDirectory("kide-pr21-large-");
        try {
            FileModelRepository repository = new FileModelRepository(root);
            byte[] tooLarge = new byte[RepositoryLimits.MAX_MODEL_BYTES + 1];
            try (ModelTransaction tx = repository.beginTransaction()) {
                assertThrows(IllegalArgumentException.class,
                        () -> tx.write(new ModelPath("models/huge.dml"), tooLarge, MISSING));
            }
            assertFalse(Files.exists(root.resolve("models/huge.dml")));
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void desktopAndServerRepositoriesShareContentRevisionSemantics() throws Exception {
        Path root = Files.createTempDirectory("kide-pr21-roundtrip-");
        try {
            ModelPath path = new ModelPath("models/shared.capability");
            byte[] content = bytes("Capability Shared {}");
            FileModelRepository desktop = new FileModelRepository(root);
            ServerModelRepository server = new ServerModelRepository();

            try (ModelTransaction tx = desktop.beginTransaction()) {
                tx.write(path, content, MISSING);
                tx.commit();
            }
            try (ModelTransaction tx = server.beginTransaction()) {
                tx.write(path, desktop.read(path).orElseThrow().content(), MISSING);
                tx.commit();
            }

            ModelSnapshot desktopSnapshot = desktop.read(path).orElseThrow();
            ModelSnapshot serverSnapshot = server.read(path).orElseThrow();
            assertArrayEquals(desktopSnapshot.content(), serverSnapshot.content());
            assertEquals(desktopSnapshot.revision().etag(), serverSnapshot.revision().etag());
            assertEquals(1L, serverSnapshot.revision().version());
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void deleteUsesOptimisticRevisionAndIsTransactional() throws Exception {
        Path root = Files.createTempDirectory("kide-pr21-delete-");
        try {
            FileModelRepository repository = new FileModelRepository(root);
            ModelPath path = new ModelPath("models/delete.operation");
            try (ModelTransaction tx = repository.beginTransaction()) {
                tx.write(path, bytes("operation"), MISSING);
                tx.commit();
            }
            String etag = repository.read(path).orElseThrow().revision().etag();
            try (ModelTransaction tx = repository.beginTransaction()) {
                tx.delete(path, etag);
                tx.commit();
            }
            Optional<ModelSnapshot> deleted = repository.read(path);
            assertTrue(deleted.isEmpty());
        } finally {
            deleteTree(root);
        }
    }

    private static byte[] bytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    private static void deleteTree(Path root) {
        if (root == null || !Files.exists(root)) return;
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
