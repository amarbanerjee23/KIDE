package com.kide.enterprise.context.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Properties;
import java.util.UUID;

import org.junit.Test;

import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.context.EnterpriseContextResult;
import com.kide.enterprise.context.EnterpriseContextStore;
import com.kide.enterprise.context.FileHostedProjectRegistry;
import com.kide.enterprise.context.HostedProjectRegistration;
import com.kide.enterprise.context.HostedProjectOwnership;

public class HostedProjectRegistryTest {
    @Test
    public void discoversProjectsAndWorkspacesDeterministically() throws Exception {
        Path root = Files.createTempDirectory("kide-pr87-registry-");
        try {
            EnterpriseContext second = addProject(root, "Second");
            EnterpriseContext first = addProject(root, "First");

            FileHostedProjectRegistry registry = new FileHostedProjectRegistry(root);
            var listed = registry.list();
            assertEquals(2, listed.size());
            assertTrue(listed.get(0).context().project().id().value()
                    .compareTo(listed.get(1).context().project().id().value()) < 0);
            assertEquals(
                    first.project().id(),
                    registry.resolveProject(first.project().id().value())
                            .orElseThrow().context().project().id());
            assertEquals(
                    second.workspace().id(),
                    registry.resolveWorkspace(second.workspace().id().value())
                            .orElseThrow().context().workspace().id());
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void rejectsSlotWhoseDirectoryDoesNotMatchProjectIdentity() throws Exception {
        Path root = Files.createTempDirectory("kide-pr87-mismatch-");
        try {
            EnterpriseContext context = addProject(root, "Mismatch");
            Path correct = root.resolve(context.project().id().uuid().toString());
            Path wrong = root.resolve(UUID.randomUUID().toString());
            Files.move(correct, wrong);

            FileHostedProjectRegistry registry = new FileHostedProjectRegistry(root);
            assertThrows(IllegalStateException.class, registry::list);
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void rejectsDuplicateWorkspaceIdentityAcrossProjects() throws Exception {
        Path root = Files.createTempDirectory("kide-pr87-duplicate-");
        try {
            EnterpriseContext first = addProject(root, "First");
            EnterpriseContext second = addProject(root, "Second");

            Path secondWorkspaceDescriptor = root
                    .resolve(second.project().id().uuid().toString())
                    .resolve(FileHostedProjectRegistry.WORKSPACE_DIR)
                    .resolve(EnterpriseContextStore.WORKSPACE_DESCRIPTOR);
            Properties properties = new Properties();
            try (var reader = Files.newBufferedReader(
                    secondWorkspaceDescriptor, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
            assertEquals(second.workspace().id().value(),
                    properties.getProperty("workspace.id"));
            properties.setProperty("workspace.id", first.workspace().id().value());
            try (var writer = Files.newBufferedWriter(
                    secondWorkspaceDescriptor, StandardCharsets.UTF_8)) {
                properties.store(writer, "PR87 duplicate-workspace fixture");
            }

            Properties mutated = new Properties();
            try (var reader = Files.newBufferedReader(
                    secondWorkspaceDescriptor, StandardCharsets.UTF_8)) {
                mutated.load(reader);
            }
            assertEquals(first.workspace().id().value(),
                    mutated.getProperty("workspace.id"));

            FileHostedProjectRegistry registry = new FileHostedProjectRegistry(root);
            IllegalStateException duplicate = assertThrows(
                    IllegalStateException.class, registry::list);
            assertTrue(duplicate.getMessage().contains("duplicate hosted workspace identity"));
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void rejectsSymbolicLinkRegistryEntries() throws Exception {
        Path root = Files.createTempDirectory("kide-pr87-symlink-");
        Path outside = Files.createTempDirectory("kide-pr87-outside-");
        try {
            Files.createSymbolicLink(root.resolve(UUID.randomUUID().toString()), outside);
            FileHostedProjectRegistry registry = new FileHostedProjectRegistry(root);
            assertThrows(IllegalStateException.class, registry::list);
        } finally {
            deleteTree(root);
            deleteTree(outside);
        }
    }

    @Test
    public void provisionsOwnedProjectWithStableIdentityAndAtomicPublish() throws Exception {
        Path root = Files.createTempDirectory("kide-pr88-provision-");
        try {
            FileHostedProjectRegistry registry = new FileHostedProjectRegistry(root);
            String owner = "firebase:example#engineer-1";
            HostedProjectRegistration created =
                    registry.createProject("Control Engineering", owner);
            assertEquals(1, registry.list().size());
            assertEquals("Control Engineering", created.context().project().displayName());
            assertEquals(owner, HostedProjectOwnership.principalId(created));
            assertEquals(created.context().project().id(),
                    new FileHostedProjectRegistry(root)
                            .resolveWorkspace(created.context().workspace().id().value())
                            .orElseThrow().context().project().id());
            // A malformed creator or name cannot produce a discoverable slot.
            assertThrows(IllegalArgumentException.class,
                    () -> registry.createProject("", owner));
            assertThrows(IllegalArgumentException.class,
                    () -> registry.createProject("Invalid", "bad\\nowner"));
            assertEquals(1, registry.list().size());
        } finally {
            deleteTree(root);
        }
    }

    private static EnterpriseContext addProject(Path registryRoot, String name)
            throws IOException {
        Path staging = Files.createDirectory(
                registryRoot.resolve(".staging-" + UUID.randomUUID()));
        Path project = Files.createDirectories(
                staging.resolve(FileHostedProjectRegistry.PROJECT_DIR));
        Path workspace = Files.createDirectories(
                staging.resolve(FileHostedProjectRegistry.WORKSPACE_DIR));

        EnterpriseContextResult provisioned = new EnterpriseContextStore().provision(
                workspace,
                project,
                "Registry Org",
                "Registry Portfolio",
                name + " Project",
                name + " Workspace");
        if (!provisioned.isReady()) {
            throw new AssertionError(provisioned.summary());
        }
        EnterpriseContext context = provisioned.context().orElseThrow();
        Files.move(
                staging,
                registryRoot.resolve(context.project().id().uuid().toString()));
        return context;
    }

    private static void deleteTree(Path root) {
        if (root == null || !Files.exists(root)) return;
        try (var stream = Files.walk(root)) {
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
