package com.kide.glsp;

import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.kide.enterprise.authorization.AuthorizationEnforcer;
import com.kide.enterprise.authorization.AuthorizationService;
import com.kide.enterprise.authorization.InMemoryAuthorizationPolicyStore;
import com.kide.enterprise.authorization.Role;
import com.kide.enterprise.authorization.RoleBinding;
import com.kide.enterprise.authorization.ServerAuthorizationGate;
import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.context.EnterpriseContextResult;
import com.kide.enterprise.context.EnterpriseContextStore;
import com.kide.enterprise.context.EnterpriseId;
import com.kide.enterprise.context.EnterpriseScope;
import com.kide.languageserver.gateway.GatewayAuthenticator;
import com.kide.languageserver.gateway.GatewayConfig;
import com.kide.languageserver.gateway.GatewayWorkspaceBinding;
import com.kide.languageserver.gateway.InMemoryGatewayWorkspaceCatalog;
import com.kide.languageserver.gateway.OidcIntrospectionAuthenticator;
import com.kide.languageserver.gateway.OidcIntrospectionConfig;

/**
 * Production entry point for the secure GLSP WebSocket gateway.
 *
 * The runtime intentionally mirrors the LSP gateway security boundary while
 * keeping its own bind/port settings so a reverse proxy can expose /glsp and
 * /lsp on one public origin.
 */
public final class GlspGatewayApplication implements IApplication {
    private volatile SecureGlspWebSocketGateway gateway;
    private volatile OidcIntrospectionConfig oidcConfig;

    @Override
    public Object start(IApplicationContext context) {
        try {
            Map<String, String> env = System.getenv();
            String bind = env.getOrDefault("KIDE_GLSP_BIND", "127.0.0.1");
            int port = integer(env, "KIDE_GLSP_PORT", 8082);
            boolean trustForwarded = bool(
                    env, "KIDE_GLSP_TRUST_FORWARDED_PROTO", false);
            boolean allowInsecureLoopback = bool(
                    env, "KIDE_GLSP_ALLOW_INSECURE_LOOPBACK", false);
            if (allowInsecureLoopback && !isLoopback(bind)) {
                throw new IllegalArgumentException(
                        "Insecure GLSP transport is permitted only on a loopback bind");
            }

            GatewayConfig config = new GatewayConfig(
                    bind,
                    port,
                    Duration.ofSeconds(integer(env, "KIDE_GLSP_IDLE_SECONDS", 300)),
                    integer(env, "KIDE_GLSP_MAX_MESSAGE_BYTES", 1024 * 1024),
                    integer(env, "KIDE_GLSP_MAX_MESSAGES_PER_MINUTE", 2400),
                    integer(env, "KIDE_GLSP_MAX_SESSIONS", 128),
                    !allowInsecureLoopback,
                    trustForwarded,
                    csv(env.get("KIDE_GLSP_TRUSTED_PROXY_ADDRESSES")),
                    csv(env.get("KIDE_GLSP_ALLOWED_ORIGINS")));

            GatewayWorkspaceBinding workspaceBinding = loadContext(env);
            EnterpriseContext enterpriseContext = workspaceBinding.context();
            InMemoryGatewayWorkspaceCatalog catalog =
                    new InMemoryGatewayWorkspaceCatalog();
            catalog.register(workspaceBinding);

            List<RoleBinding> bindings = parseBindings(
                    required(env, "KIDE_GLSP_ROLE_BINDINGS"),
                    enterpriseContext);
            ServerAuthorizationGate authorization = new ServerAuthorizationGate(
                    new AuthorizationEnforcer(
                            new AuthorizationService(
                                    new InMemoryAuthorizationPolicyStore(bindings))));

            char[] clientSecret =
                    required(env, "KIDE_OIDC_CLIENT_SECRET").toCharArray();
            try {
                oidcConfig = new OidcIntrospectionConfig(
                        URI.create(required(env, "KIDE_OIDC_INTROSPECTION_URL")),
                        required(env, "KIDE_OIDC_CLIENT_ID"),
                        clientSecret,
                        required(env, "KIDE_OIDC_ISSUER"),
                        required(env, "KIDE_OIDC_AUDIENCE"),
                        Duration.ofSeconds(integer(
                                env, "KIDE_OIDC_TIMEOUT_SECONDS", 10)));
            } finally {
                Arrays.fill(clientSecret, '\0');
            }

            GatewayAuthenticator authenticator =
                    new OidcIntrospectionAuthenticator(
                            oidcConfig,
                            HttpClient.newBuilder()
                                    .connectTimeout(Duration.ofSeconds(integer(
                                            env,
                                            "KIDE_OIDC_CONNECT_TIMEOUT_SECONDS",
                                            10)))
                                    .build(),
                            Clock.systemUTC());

            gateway = new SecureGlspWebSocketGateway(
                    config,
                    authenticator,
                    catalog,
                    authorization,
                    Clock.systemUTC());
            gateway.start();
            System.out.println(
                    "KIDE SECURE GLSP WEBSOCKET GATEWAY READY port="
                            + gateway.localPort());
            gateway.join();
            return IApplication.EXIT_OK;
        } catch (RuntimeException e) {
            System.err.println(
                    "KIDE secure GLSP gateway startup failed: " + safe(e.getMessage()));
            return Integer.valueOf(2);
        } catch (Exception e) {
            System.err.println("KIDE secure GLSP gateway startup failed");
            return Integer.valueOf(2);
        } finally {
            closeConfig();
        }
    }

    @Override
    public void stop() {
        SecureGlspWebSocketGateway current = gateway;
        if (current != null) {
            try {
                current.close();
            } catch (RuntimeException ignored) {
                // Shutdown must not expose provider or credential diagnostics.
            }
        }
        closeConfig();
    }

    private static GatewayWorkspaceBinding loadContext(Map<String, String> env) {
        Path workspace = Path.of(required(env, "KIDE_GLSP_WORKSPACE_ROOT"));
        Path project = Path.of(required(env, "KIDE_GLSP_PROJECT_ROOT"));
        EnterpriseContextResult result =
                new EnterpriseContextStore().load(workspace, project);
        if (!result.isReady()) {
            throw new IllegalStateException(
                    "GLSP enterprise context is not ready: " + result.summary());
        }
        return new GatewayWorkspaceBinding(
                result.context().orElseThrow(), project);
    }

    private static List<RoleBinding> parseBindings(
            String raw, EnterpriseContext context) {
        List<RoleBinding> result = new ArrayList<>();
        for (String entry : raw.split(";")) {
            if (entry.isBlank()) continue;
            String[] parts = entry.split("\\|", -1);
            if (parts.length != 3) {
                throw new IllegalArgumentException(
                        "Each KIDE_GLSP_ROLE_BINDINGS entry must be "
                                + "principal|ROLE|scopeId");
            }
            String principal = parts[0].trim();
            Role role = Role.valueOf(parts[1].trim());
            EnterpriseId id = parseContextId(parts[2].trim(), context);
            result.add(RoleBinding.allow(principal, role, id));
        }
        if (result.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one GLSP role binding is required");
        }
        return List.copyOf(result);
    }

    private static EnterpriseId parseContextId(
            String raw, EnterpriseContext context) {
        for (EnterpriseScope scope : EnterpriseScope.values()) {
            EnterpriseId id = EnterpriseId.tryParse(scope, raw).orElse(null);
            if (id != null) {
                if (!id.equals(context.node(scope).id())) {
                    throw new IllegalArgumentException(
                            "GLSP role binding scope is outside the configured "
                                    + "enterprise context");
                }
                return id;
            }
        }
        throw new IllegalArgumentException(
                "GLSP role binding contains an invalid E04 scope ID");
    }

    private static String required(Map<String, String> env, String key) {
        String value = env.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(key + " is required");
        }
        return value.trim();
    }

    private static int integer(
            Map<String, String> env, String key, int fallback) {
        String raw = env.get(key);
        if (raw == null || raw.isBlank()) return fallback;
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(key + " must be an integer");
        }
    }

    private static boolean bool(
            Map<String, String> env, String key, boolean fallback) {
        String raw = env.get(key);
        if (raw == null || raw.isBlank()) return fallback;
        if ("true".equalsIgnoreCase(raw)) return true;
        if ("false".equalsIgnoreCase(raw)) return false;
        throw new IllegalArgumentException(key + " must be true or false");
    }

    private static Set<String> csv(String raw) {
        if (raw == null || raw.isBlank()) return Set.of();
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }

    private static boolean isLoopback(String host) {
        return "127.0.0.1".equals(host)
                || "::1".equals(host)
                || "localhost".equalsIgnoreCase(host);
    }

    private static String safe(String message) {
        if (message == null || message.isBlank()) return "configuration error";
        String lower = message.toLowerCase(java.util.Locale.ROOT);
        if (lower.contains("token")
                || lower.contains("secret")
                || lower.contains("password")
                || lower.contains("credential")) {
            return "authentication configuration error";
        }
        return message.replace('\r', ' ').replace('\n', ' ');
    }

    private synchronized void closeConfig() {
        OidcIntrospectionConfig current = oidcConfig;
        oidcConfig = null;
        if (current != null) current.close();
    }
}
