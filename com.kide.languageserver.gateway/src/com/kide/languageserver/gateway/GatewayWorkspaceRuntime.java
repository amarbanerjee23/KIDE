package com.kide.languageserver.gateway;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.context.EnterpriseContextResult;
import com.kide.enterprise.context.EnterpriseContextStore;
import com.kide.enterprise.context.FileHostedProjectRegistry;

/**
 * Shared LSP/GLSP workspace-catalog bootstrap.
 */
public final class GatewayWorkspaceRuntime {
    public static final String HOSTED_PROJECTS_ROOT = "KIDE_HOSTED_PROJECTS_ROOT";

    private final GatewayWorkspaceCatalog catalog;
    private final List<EnterpriseContext> contexts;

    private GatewayWorkspaceRuntime(
            GatewayWorkspaceCatalog catalog,
            List<EnterpriseContext> contexts) {
        this.catalog = catalog;
        this.contexts = List.copyOf(contexts);
        if (this.contexts.isEmpty() && !(catalog instanceof RegistryGatewayWorkspaceCatalog)) {
            throw new IllegalArgumentException(
                    "At least one hosted enterprise context is required");
        }
    }

    public static GatewayWorkspaceRuntime load(
            Map<String, String> env,
            String workspaceRootKey,
            String projectRootKey) {
        String registryRoot = trim(env.get(HOSTED_PROJECTS_ROOT));
        if (registryRoot != null) {
            RegistryGatewayWorkspaceCatalog catalog =
                    new RegistryGatewayWorkspaceCatalog(
                            new FileHostedProjectRegistry(Path.of(registryRoot)));
            return new GatewayWorkspaceRuntime(catalog, catalog.contexts());
        }

        Path workspace = Path.of(required(env, workspaceRootKey));
        Path project = Path.of(required(env, projectRootKey));
        EnterpriseContextResult result =
                new EnterpriseContextStore().load(workspace, project);
        if (!result.isReady()) {
            throw new IllegalStateException(
                    "Gateway enterprise context is not ready: " + result.summary());
        }
        EnterpriseContext context = result.context().orElseThrow();
        InMemoryGatewayWorkspaceCatalog catalog =
                new InMemoryGatewayWorkspaceCatalog();
        catalog.register(new GatewayWorkspaceBinding(context, project));
        return new GatewayWorkspaceRuntime(catalog, List.of(context));
    }

    public GatewayWorkspaceCatalog catalog() {
        return catalog;
    }

    public List<EnterpriseContext> contexts() {
        return contexts;
    }

    private static String required(
            Map<String, String> env, String key) {
        String value = trim(env.get(key));
        if (value == null) {
            throw new IllegalArgumentException(key + " is required");
        }
        return value;
    }

    private static String trim(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
