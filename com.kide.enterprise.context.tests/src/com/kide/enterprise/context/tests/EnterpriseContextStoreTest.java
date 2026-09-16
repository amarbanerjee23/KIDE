package com.kide.enterprise.context.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;

import org.junit.Test;

import com.kide.enterprise.context.ContextStatus;
import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.context.EnterpriseContextResult;
import com.kide.enterprise.context.EnterpriseContextStore;
import com.kide.enterprise.context.EnterpriseScope;

public class EnterpriseContextStoreTest {
    @Test
    public void missingContextIsUninitializedWithoutException() throws Exception {
        Path root = Files.createTempDirectory("kide-e04-missing-");
        try {
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project"));
            EnterpriseContextResult result = new EnterpriseContextStore().load(workspace, project);
            assertEquals(ContextStatus.UNINITIALIZED, result.status());
            assertFalse(result.isReady());
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void nullPathsReturnDiagnosticInsteadOfThrowing() {
        EnterpriseContextStore store = new EnterpriseContextStore();
        assertEquals(ContextStatus.INVALID, store.load(null, null).status());
        assertEquals(ContextStatus.INVALID,
                store.provision(null, null, "Org", "Portfolio", "Project", "Workspace").status());
    }

    @Test
    public void stableIdsSurviveRenamesAndProjectPathMoves() throws Exception {
        Path root = Files.createTempDirectory("kide-e04-stable-");
        try {
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project-a"));
            EnterpriseContextStore store = new EnterpriseContextStore();

            EnterpriseContext first = ready(store.provision(workspace, project,
                    "Org A", "Portfolio A", "Project A", "Workspace A"));
            EnterpriseContext renamed = ready(store.provision(workspace, project,
                    "Org Renamed", "Portfolio Renamed", "Project Renamed", "Workspace Renamed"));

            assertEquals(first.organization().id(), renamed.organization().id());
            assertEquals(first.portfolio().id(), renamed.portfolio().id());
            assertEquals(first.project().id(), renamed.project().id());
            assertEquals(first.workspace().id(), renamed.workspace().id());

            Path moved = root.resolve("project-moved");
            // This test validates identity independence from filesystem paths, not
            // whether the host filesystem implements atomic directory renames.
            Files.move(project, moved);
            EnterpriseContext afterMove = ready(store.load(workspace, moved));
            assertEquals(first.organization().id(), afterMove.organization().id());
            assertEquals(first.portfolio().id(), afterMove.portfolio().id());
            assertEquals(first.project().id(), afterMove.project().id());
            assertEquals(first.workspace().id(), afterMove.workspace().id());
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void projectIdentitySurvivesBindingIntoAnotherWorkspace() throws Exception {
        Path root = Files.createTempDirectory("kide-e04-rebind-");
        try {
            Path workspaceA = Files.createDirectories(root.resolve("workspace-a"));
            Path workspaceB = Files.createDirectories(root.resolve("workspace-b"));
            Path project = Files.createDirectories(root.resolve("project"));
            EnterpriseContextStore store = new EnterpriseContextStore();

            EnterpriseContext first = ready(store.provision(workspaceA, project,
                    "Org", "Portfolio", "Project", "Workspace A"));
            EnterpriseContextResult beforeBinding = store.load(workspaceB, project);
            assertEquals(ContextStatus.UNINITIALIZED, beforeBinding.status());

            EnterpriseContext rebound = ready(store.provision(workspaceB, project,
                    "Org", "Portfolio", "Project", "Workspace B"));
            assertEquals(first.organization().id(), rebound.organization().id());
            assertEquals(first.portfolio().id(), rebound.portfolio().id());
            assertEquals(first.project().id(), rebound.project().id());
            assertNotEquals(first.workspace().id(), rebound.workspace().id());
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void malformedAndUnsupportedMetadataFailSafely() throws Exception {
        Path root = Files.createTempDirectory("kide-e04-invalid-");
        try {
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project"));
            EnterpriseContextStore store = new EnterpriseContextStore();
            ready(store.provision(workspace, project, "Org", "Portfolio", "Project", "Workspace"));

            Path descriptor = project.resolve(EnterpriseContextStore.PROJECT_DESCRIPTOR);
            Files.writeString(descriptor, "schema.version=999\norganization.id=not-an-id\n", StandardCharsets.UTF_8);
            EnterpriseContextResult invalid = store.load(workspace, project);
            assertEquals(ContextStatus.INVALID, invalid.status());
            assertFalse(invalid.isReady());
            assertTrue(invalid.summary().toLowerCase().contains("schema"));
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void workspaceBindingMismatchFailsWithoutRegeneratingIds() throws Exception {
        Path root = Files.createTempDirectory("kide-e04-binding-");
        try {
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project"));
            EnterpriseContextStore store = new EnterpriseContextStore();
            EnterpriseContext original = ready(store.provision(workspace, project,
                    "Org", "Portfolio", "Project", "Workspace"));

            Path binding = workspace.resolve(EnterpriseContextStore.WORKSPACE_DESCRIPTOR);
            String text = Files.readString(binding, StandardCharsets.UTF_8);
            String wrongProject = "kide:project:00000000-0000-0000-0000-000000000001";
            text = text.replace(original.project().id().value(), wrongProject);
            Files.writeString(binding, text, StandardCharsets.UTF_8);

            EnterpriseContextResult invalid = store.load(workspace, project);
            assertEquals(ContextStatus.INVALID, invalid.status());
            assertTrue(invalid.summary().toLowerCase().contains("different"));
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void metadataRoundTripsAndInvalidMetadataDoesNotReplaceContext() throws Exception {
        Path root = Files.createTempDirectory("kide-e04-metadata-");
        try {
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project"));
            EnterpriseContextStore store = new EnterpriseContextStore();
            EnterpriseContext original = ready(store.provision(workspace, project,
                    "Org", "Portfolio", "Project", "Workspace"));

            EnterpriseContext updated = ready(store.updateMetadata(workspace, project, EnterpriseScope.PROJECT,
                    Map.of("classification", "restricted", "system", "payments")));
            assertEquals("restricted", updated.project().metadata().get("classification"));
            assertEquals(original.project().id(), updated.project().id());

            EnterpriseContextResult rejected = store.updateMetadata(workspace, project, EnterpriseScope.PROJECT,
                    Map.of("bad key!", "value"));
            assertEquals(ContextStatus.INVALID, rejected.status());
            EnterpriseContext reloaded = ready(store.load(workspace, project));
            assertEquals("restricted", reloaded.project().metadata().get("classification"));
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void invalidNamesAreRejectedWithoutCreatingDescriptors() throws Exception {
        Path root = Files.createTempDirectory("kide-e04-name-");
        try {
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project"));
            EnterpriseContextStore store = new EnterpriseContextStore();
            EnterpriseContextResult result = store.provision(workspace, project,
                    "", "Portfolio", "Project", "Workspace");
            assertEquals(ContextStatus.INVALID, result.status());
            assertFalse(Files.exists(project.resolve(EnterpriseContextStore.PROJECT_DESCRIPTOR)));
            assertFalse(Files.exists(workspace.resolve(EnterpriseContextStore.WORKSPACE_DESCRIPTOR)));
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void oversizedDescriptorIsRejectedBeforeParsing() throws Exception {
        Path root = Files.createTempDirectory("kide-e04-size-");
        try {
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project/.kide"));
            Path descriptor = project.resolve("enterprise-context.properties");
            byte[] oversized = new byte[70 * 1024];
            Files.write(descriptor, oversized);
            EnterpriseContextResult result = new EnterpriseContextStore().load(workspace, root.resolve("project"));
            assertEquals(ContextStatus.INVALID, result.status());
            assertTrue(result.summary().toLowerCase().contains("size"));
        } finally {
            deleteTree(root);
        }
    }

    private static EnterpriseContext ready(EnterpriseContextResult result) {
        assertTrue("Expected READY but was " + result.status() + ": " + result.summary(), result.isReady());
        return result.context().get();
    }

    private static void deleteTree(Path root) {
        if (root == null || !Files.exists(root)) {
            return;
        }
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
