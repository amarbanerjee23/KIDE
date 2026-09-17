package com.kide.enterprise.context;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/** Headless packaged-product self-check used by CI to catch runtime bundle/startup defects. */
public final class EnterpriseContextSelfCheckApplication implements IApplication {
    @Override
    public Object start(IApplicationContext context) {
        Path root = null;
        try {
            root = Files.createTempDirectory("kide-e04-selfcheck-");
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project"));
            EnterpriseContextStore store = new EnterpriseContextStore();

            EnterpriseContextResult initial = store.load(workspace, project);
            if (initial.status() != ContextStatus.UNINITIALIZED) {
                return fail("new context did not report UNINITIALIZED");
            }

            EnterpriseContextResult provisioned = store.provision(workspace, project,
                    "Self Check Organization", "Self Check Portfolio", "Self Check Project", "Self Check Workspace");
            if (!provisioned.isReady()) {
                return fail("context provisioning failed: " + provisioned.summary());
            }

            EnterpriseContext ids = provisioned.context().get();
            EnterpriseContextResult metadata = store.updateMetadata(workspace, project, EnterpriseScope.PROJECT,
                    Map.of("classification", "internal", "selfcheck", "true"));
            if (!metadata.isReady()) {
                return fail("metadata update failed: " + metadata.summary());
            }

            EnterpriseContextResult reloaded = store.load(workspace, project);
            if (!reloaded.isReady()
                    || !ids.organization().id().equals(reloaded.context().get().organization().id())
                    || !ids.portfolio().id().equals(reloaded.context().get().portfolio().id())
                    || !ids.project().id().equals(reloaded.context().get().project().id())
                    || !ids.workspace().id().equals(reloaded.context().get().workspace().id())) {
                return fail("stable identities changed after persistence round-trip");
            }

            Path projectFile = project.resolve(EnterpriseContextStore.PROJECT_DESCRIPTOR);
            Files.writeString(projectFile, "schema.version=999\n", StandardCharsets.UTF_8);
            EnterpriseContextResult corrupted = store.load(workspace, project);
            if (corrupted.status() != ContextStatus.INVALID) {
                return fail("malformed metadata did not fail safely");
            }

            System.out.println("KIDE E04 SELF-CHECK OK");
            return IApplication.EXIT_OK;
        } catch (Exception e) {
            return fail("guarded runtime failure: " + e.getClass().getSimpleName());
        } finally {
            deleteTree(root);
        }
    }

    @Override
    public void stop() {
        // No background threads or resources are retained by the self-check.
    }

    private static Integer fail(String message) {
        System.err.println("KIDE E04 SELF-CHECK FAILED: " + message);
        return Integer.valueOf(2);
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
                    // Temporary CI cleanup must never turn a successful runtime check into an application exception.
                }
            });
        } catch (IOException | RuntimeException ignored) {
            // Best-effort temporary cleanup only.
        }
    }
}
