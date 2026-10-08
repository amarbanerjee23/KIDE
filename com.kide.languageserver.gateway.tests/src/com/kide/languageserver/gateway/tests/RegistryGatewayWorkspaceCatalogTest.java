package com.kide.languageserver.gateway.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.kide.enterprise.authorization.Role;
import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.context.EnterpriseContextResult;
import com.kide.enterprise.context.EnterpriseContextStore;
import com.kide.enterprise.context.FileHostedProjectRegistry;
import com.kide.languageserver.gateway.GatewayRoleBindings;
import com.kide.languageserver.gateway.RegistryGatewayWorkspaceCatalog;

public class RegistryGatewayWorkspaceCatalogTest {
    @Test
    public void newlyRegisteredWorkspaceResolvesWithoutCatalogRestart() throws Exception {
        Path root = Files.createTempDirectory("kide-pr87-gateway-registry-");
        try {
            RegistryGatewayWorkspaceCatalog catalog =
                    new RegistryGatewayWorkspaceCatalog(
                            new FileHostedProjectRegistry(root));
            assertTrue(catalog.bindings().isEmpty());

            EnterpriseContext context = addProject(root, "Dynamic");
            var binding = catalog.resolve(context.workspace().id().value()).orElseThrow();

            assertEquals(context.project().id(), binding.context().project().id());
            assertEquals(
                    root.resolve(context.project().id().uuid().toString())
                            .resolve(FileHostedProjectRegistry.PROJECT_DIR)
                            .toRealPath(),
                    binding.projectRoot().toRealPath());
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void roleBindingsAcceptAnyRegisteredProjectAndRejectUnknownScope() throws Exception {
        Path root = Files.createTempDirectory("kide-pr87-role-registry-");
        try {
            EnterpriseContext first = addProject(root, "First");
            EnterpriseContext second = addProject(root, "Second");
            RegistryGatewayWorkspaceCatalog catalog =
                    new RegistryGatewayWorkspaceCatalog(
                            new FileHostedProjectRegistry(root));

            var bindings = GatewayRoleBindings.parse(
                    "firebase:test#one|ENGINEER|" + first.project().id().value()
                    + ";firebase:test#two|ADMINISTRATOR|" + second.project().id().value(),
                    catalog.contexts(),
                    "KIDE_GATEWAY_ROLE_BINDINGS");
            assertEquals(2, bindings.size());
            assertEquals(Role.ENGINEER, bindings.get(0).role());
            assertEquals(Role.ADMINISTRATOR, bindings.get(1).role());

            String unknown = "kide:project:" + UUID.randomUUID();
            assertThrows(
                    IllegalArgumentException.class,
                    () -> GatewayRoleBindings.parse(
                            "firebase:test#bad|ENGINEER|" + unknown,
                            catalog.contexts(),
                            "KIDE_GATEWAY_ROLE_BINDINGS"));
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
                "Gateway Registry Org",
                "Gateway Registry Portfolio",
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
