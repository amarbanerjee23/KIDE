package com.kide.enterprise.migration.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.context.EnterpriseContextResult;
import com.kide.enterprise.context.EnterpriseContextStore;
import com.kide.enterprise.migration.ProjectSchemaReport;
import com.kide.enterprise.migration.ProjectSchemaService;
import com.kide.enterprise.migration.ProjectSchemaStatus;

public class ProjectSchemaServiceTest {

    @Test
    public void legacyProjectRemainsReadableAndDryRunDoesNotWrite() throws Exception {
        Path root = Files.createTempDirectory("kide-pr14-legacy-");
        try {
            ProjectSchemaService service = new ProjectSchemaService();
            assertEquals(ProjectSchemaStatus.LEGACY, service.inspect(root).status());

            ProjectSchemaReport dryRun = service.migrate(root, true);
            assertEquals(ProjectSchemaStatus.MIGRATION_REQUIRED, dryRun.status());
            assertFalse(Files.exists(root.resolve(ProjectSchemaService.MANIFEST)));
            assertEquals(ProjectSchemaStatus.LEGACY, service.inspect(root).status());
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void migrationIsIdempotentAndDoesNotRewriteModels() throws Exception {
        Path root = Files.createTempDirectory("kide-pr14-idempotent-");
        try {
            Path model = root.resolve("system.mncspec");
            byte[] original = "Model Stable\nInterfaceDescription Device { commands { Start[] } }\n"
                    .getBytes(StandardCharsets.UTF_8);
            Files.write(model, original);

            ProjectSchemaService service = new ProjectSchemaService();
            assertEquals(ProjectSchemaStatus.CURRENT, service.migrate(root, false).status());
            assertEquals(ProjectSchemaStatus.CURRENT, service.migrate(root, false).status());
            assertArrayEquals(original, Files.readAllBytes(model));
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void migrationBacksUpExistingOlderManifest() throws Exception {
        Path root = Files.createTempDirectory("kide-pr14-backup-");
        try {
            Path manifest = root.resolve(ProjectSchemaService.MANIFEST);
            Files.createDirectories(manifest.getParent());
            Properties old = new Properties();
            old.setProperty("schema.version", "0");
            try (BufferedWriter writer = Files.newBufferedWriter(manifest, StandardCharsets.UTF_8)) {
                old.store(writer, "old");
            }
            byte[] before = Files.readAllBytes(manifest);

            ProjectSchemaReport report = new ProjectSchemaService().migrate(root, false);
            assertEquals(ProjectSchemaStatus.CURRENT, report.status());
            assertTrue(report.backup().isPresent());
            assertArrayEquals(before, Files.readAllBytes(report.backup().get()));
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void forwardVersionIsRejectedWithoutModification() throws Exception {
        Path root = Files.createTempDirectory("kide-pr14-future-");
        try {
            Path manifest = root.resolve(ProjectSchemaService.MANIFEST);
            Files.createDirectories(manifest.getParent());
            Files.writeString(manifest, "schema.version=999\n", StandardCharsets.UTF_8);
            byte[] before = Files.readAllBytes(manifest);

            ProjectSchemaService service = new ProjectSchemaService();
            assertEquals(ProjectSchemaStatus.FORWARD_INCOMPATIBLE, service.inspect(root).status());
            assertEquals(ProjectSchemaStatus.FORWARD_INCOMPATIBLE, service.migrate(root, false).status());
            assertArrayEquals(before, Files.readAllBytes(manifest));
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void malformedAndOversizedManifestsFailSafely() throws Exception {
        Path root = Files.createTempDirectory("kide-pr14-invalid-");
        try {
            Path manifest = root.resolve(ProjectSchemaService.MANIFEST);
            Files.createDirectories(manifest.getParent());
            Files.writeString(manifest, "schema.version=not-a-number\n", StandardCharsets.UTF_8);
            ProjectSchemaService service = new ProjectSchemaService();
            assertEquals(ProjectSchemaStatus.INVALID, service.inspect(root).status());

            byte[] oversized = new byte[70 * 1024];
            Files.write(manifest, oversized);
            assertEquals(ProjectSchemaStatus.INVALID, service.inspect(root).status());
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void symlinkManifestIsRejectedWhenSupported() throws Exception {
        Path root = Files.createTempDirectory("kide-pr14-symlink-");
        try {
            Path kide = Files.createDirectories(root.resolve(".kide"));
            Path outside = root.resolve("outside.properties");
            Files.writeString(outside, "schema.version=1\n", StandardCharsets.UTF_8);
            Path manifest = kide.resolve("schema.properties");
            try {
                Files.createSymbolicLink(manifest, outside);
            } catch (UnsupportedOperationException | java.io.IOException | SecurityException e) {
                return;
            }
            assertEquals(ProjectSchemaStatus.INVALID,
                    new ProjectSchemaService().inspect(root).status());
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void enterpriseStableIdsSurviveProjectSchemaMigration() throws Exception {
        Path root = Files.createTempDirectory("kide-pr14-ids-");
        try {
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project"));
            EnterpriseContextStore contextStore = new EnterpriseContextStore();
            EnterpriseContext before = ready(contextStore.provision(
                    workspace, project, "Org", "Portfolio", "Project", "Workspace"));
            Map<?, ?> idsBefore = before.identitySnapshot();

            assertEquals(ProjectSchemaStatus.CURRENT,
                    new ProjectSchemaService().migrate(project, false).status());

            EnterpriseContext after = ready(contextStore.load(workspace, project));
            assertEquals(idsBefore, after.identitySnapshot());
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void nullProjectPathReturnsDiagnosticInsteadOfThrowing() {
        ProjectSchemaService service = new ProjectSchemaService();
        assertEquals(ProjectSchemaStatus.INVALID, service.inspect(null).status());
        assertEquals(ProjectSchemaStatus.INVALID, service.migrate(null, false).status());
    }

    private static EnterpriseContext ready(EnterpriseContextResult result) {
        assertTrue(result.isReady());
        return result.context().get();
    }

    private static void deleteTree(Path root) throws Exception {
        if (root == null || !Files.exists(root)) return;
        try (java.util.stream.Stream<Path> stream = Files.walk(root)) {
            stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                try { Files.deleteIfExists(path); } catch (Exception ignored) { }
            });
        }
    }
}
