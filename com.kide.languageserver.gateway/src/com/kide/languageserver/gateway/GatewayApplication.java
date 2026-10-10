package com.kide.languageserver.gateway;

import java.net.http.HttpClient;
import java.time.Clock;
import java.time.Duration;
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
import com.kide.enterprise.authorization.AuthorizationPolicyStore;
import com.kide.enterprise.authorization.HostedRegistryAuthorizationPolicyStore;
import com.kide.enterprise.context.FileHostedProjectRegistry;
import java.nio.file.Path;
import com.kide.enterprise.authorization.RoleBinding;
import com.kide.enterprise.authorization.ServerAuthorizationGate;
import com.kide.enterprise.identity.FirebaseIdTokenAuthenticator;
import com.kide.enterprise.identity.FirebaseIdTokenConfig;
import com.kide.enterprise.identity.GoogleFirebaseKeyProvider;

public final class GatewayApplication implements IApplication {
    private volatile SecureLspWebSocketGateway gateway;
    private volatile FirebaseIdTokenConfig firebaseConfig;

    @Override
    public Object start(IApplicationContext context) {
        try {
            Map<String, String> env = System.getenv();
            String bind = env.getOrDefault("KIDE_GATEWAY_BIND", "127.0.0.1");
            int port = integer(env, "KIDE_GATEWAY_PORT", 8080);
            boolean trustForwarded = bool(env, "KIDE_GATEWAY_TRUST_FORWARDED_PROTO", false);
            boolean allowInsecureLoopback = bool(env, "KIDE_GATEWAY_ALLOW_INSECURE_LOOPBACK", false);
            if (allowInsecureLoopback && !isLoopback(bind)) {
                throw new IllegalArgumentException(
                        "Insecure gateway transport is permitted only on a loopback bind");
            }

            GatewayConfig config = new GatewayConfig(
                    bind,
                    port,
                    Duration.ofSeconds(integer(env, "KIDE_GATEWAY_IDLE_SECONDS", 300)),
                    integer(env, "KIDE_GATEWAY_MAX_MESSAGE_BYTES", 1024 * 1024),
                    integer(env, "KIDE_GATEWAY_MAX_MESSAGES_PER_MINUTE", 2400),
                    integer(env, "KIDE_GATEWAY_MAX_SESSIONS", 128),
                    !allowInsecureLoopback,
                    trustForwarded,
                    csv(env.get("KIDE_GATEWAY_TRUSTED_PROXY_ADDRESSES")),
                    csv(env.get("KIDE_GATEWAY_ALLOWED_ORIGINS")));

            GatewayWorkspaceRuntime workspaceRuntime =
                    GatewayWorkspaceRuntime.load(
                            env,
                            "KIDE_GATEWAY_WORKSPACE_ROOT",
                            "KIDE_GATEWAY_PROJECT_ROOT");
            GatewayWorkspaceCatalog catalog = workspaceRuntime.catalog();

            // An empty registry has no configured tenants at first boot.
            // Only persisted creator grants can authorize a newly created project.
            List<RoleBinding> bindings =
                    !env.getOrDefault(GatewayWorkspaceRuntime.HOSTED_PROJECTS_ROOT, "").isBlank()
                            && env.getOrDefault("KIDE_GATEWAY_ROLE_BINDINGS", "").isBlank()
                            ? List.of()
                            : GatewayRoleBindings.parse(
                                    required(env, "KIDE_GATEWAY_ROLE_BINDINGS"),
                                    workspaceRuntime.contexts(),
                                    "KIDE_GATEWAY_ROLE_BINDINGS");
            ServerAuthorizationGate authorization = new ServerAuthorizationGate(
                    new AuthorizationEnforcer(
                            new AuthorizationService(
                                    authorizationPolicy(env, bindings))));

            firebaseConfig = new FirebaseIdTokenConfig(
                    required(env, "KIDE_FIREBASE_PROJECT_ID"),
                    Duration.ofSeconds(integer(env, "KIDE_FIREBASE_KEYS_TIMEOUT_SECONDS", 10)));
            HttpClient firebaseHttpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(
                            integer(env, "KIDE_FIREBASE_CONNECT_TIMEOUT_SECONDS", 10)))
                    .build();
            FirebaseIdTokenAuthenticator firebaseAuthenticator =
                    new FirebaseIdTokenAuthenticator(
                            firebaseConfig,
                            new GoogleFirebaseKeyProvider(
                                    firebaseConfig,
                                    firebaseHttpClient,
                                    Clock.systemUTC()),
                            Clock.systemUTC());
            GatewayAuthenticator authenticator =
                    firebaseAuthenticator::authenticateAuthorizationHeader;

            gateway = new SecureLspWebSocketGateway(
                    config, authenticator, catalog, authorization, Clock.systemUTC());
            gateway.start();
            System.out.println("KIDE SECURE LSP WEBSOCKET GATEWAY READY port=" + gateway.localPort());
            gateway.join();
            return IApplication.EXIT_OK;
        } catch (RuntimeException e) {
            System.err.println("KIDE secure LSP gateway startup failed: " + e.getMessage());
            return Integer.valueOf(2);
        } catch (Exception e) {
            System.err.println("KIDE secure LSP gateway startup failed");
            return Integer.valueOf(2);
        } finally {
            closeConfig();
        }
    }

    @Override
    public void stop() {
        SecureLspWebSocketGateway current = gateway;
        if (current != null) {
            try {
                current.close();
            } catch (RuntimeException ignored) {
                // Shutdown path must not expose provider or token diagnostics.
            }
        }
        closeConfig();
    }

    private static AuthorizationPolicyStore authorizationPolicy(
            Map<String, String> env, List<RoleBinding> configured) {
        String root = env.get(GatewayWorkspaceRuntime.HOSTED_PROJECTS_ROOT);
        if (root == null || root.isBlank()) {
            return new InMemoryAuthorizationPolicyStore(configured);
        }
        return new HostedRegistryAuthorizationPolicyStore(
                new FileHostedProjectRegistry(Path.of(root.trim())), configured);
    }

    private static String required(Map<String, String> env, String key) {
        String value = env.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(key + " is required");
        }
        return value.trim();
    }

    private static int integer(Map<String, String> env, String key, int fallback) {
        String raw = env.get(key);
        if (raw == null || raw.isBlank()) return fallback;
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(key + " must be an integer");
        }
    }

    private static boolean bool(Map<String, String> env, String key, boolean fallback) {
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
        return "127.0.0.1".equals(host) || "::1".equals(host) || "localhost".equalsIgnoreCase(host);
    }

    private synchronized void closeConfig() {
        firebaseConfig = null;
    }
}
