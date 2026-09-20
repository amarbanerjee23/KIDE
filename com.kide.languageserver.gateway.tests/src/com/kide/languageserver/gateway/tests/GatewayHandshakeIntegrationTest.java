package com.kide.languageserver.gateway.tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.kide.enterprise.authorization.AuthorizationEnforcer;
import com.kide.enterprise.authorization.AuthorizationService;
import com.kide.enterprise.authorization.InMemoryAuthorizationPolicyStore;
import com.kide.enterprise.authorization.Role;
import com.kide.enterprise.authorization.RoleBinding;
import com.kide.enterprise.authorization.ServerAuthorizationGate;
import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.context.EnterpriseContextResult;
import com.kide.enterprise.context.EnterpriseContextStore;
import com.kide.enterprise.identity.AuthenticatedSession;
import com.kide.enterprise.identity.AuthenticationException;
import com.kide.enterprise.identity.AuthenticationMethod;
import com.kide.enterprise.identity.PrincipalIdentity;
import com.kide.enterprise.identity.PrincipalKind;
import com.kide.languageserver.gateway.BrowserWebSocketCredential;
import com.kide.languageserver.gateway.GatewayAuthenticator;
import com.kide.languageserver.gateway.GatewayConfig;
import com.kide.languageserver.gateway.GatewayWorkspaceBinding;
import com.kide.languageserver.gateway.InMemoryGatewayWorkspaceCatalog;
import com.kide.languageserver.gateway.SecureLspWebSocketGateway;

public class GatewayHandshakeIntegrationTest {
    @Test
    public void nativeAndBrowserCredentialsReachAuthorizedGateway() throws Exception {
        Path root = Files.createTempDirectory("kide-pr22-handshake-");
        SecureLspWebSocketGateway gateway = null;
        try {
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project"));
            EnterpriseContextResult provisioned = new EnterpriseContextStore().provision(
                    workspace, project, "Handshake Org", "Handshake Portfolio",
                    "Handshake Project", "Handshake Workspace");
            if (!provisioned.isReady()) {
                throw new AssertionError(provisioned.summary());
            }
            EnterpriseContext context = provisioned.context().orElseThrow();

            PrincipalIdentity principal = new PrincipalIdentity(
                    "selfcheck:handshake-user",
                    "Handshake User",
                    PrincipalKind.LOCAL_OFFLINE,
                    AuthenticationMethod.LOCAL_OFFLINE,
                    "",
                    "handshake-user",
                    Map.of("offline", "true"));

            GatewayAuthenticator authenticator = header -> {
                if (!"Bearer pr22-self-check".equals(header)) {
                    throw new AuthenticationException("Bearer authentication is invalid");
                }
                return new AuthenticatedSession(principal, null, Clock.systemUTC());
            };

            InMemoryGatewayWorkspaceCatalog catalog = new InMemoryGatewayWorkspaceCatalog();
            catalog.register(new GatewayWorkspaceBinding(context, project));

            ServerAuthorizationGate authorization = new ServerAuthorizationGate(
                    new AuthorizationEnforcer(
                            new AuthorizationService(
                                    new InMemoryAuthorizationPolicyStore(List.of(
                                            RoleBinding.allow(
                                                    principal.id(),
                                                    Role.ENGINEER,
                                                    context.project().id()))))));

            GatewayConfig config = new GatewayConfig(
                    "127.0.0.1",
                    0,
                    Duration.ofSeconds(30),
                    1024 * 1024,
                    5000,
                    4,
                    false,
                    false,
                    Set.of(),
                    Set.of());

            gateway = new SecureLspWebSocketGateway(
                    config, authenticator, catalog, authorization, Clock.systemUTC());
            gateway.start();

            URI endpoint = URI.create(
                    "ws://127.0.0.1:" + gateway.localPort()
                    + "/lsp?workspaceId="
                    + URLEncoder.encode(
                            context.workspace().id().value(), StandardCharsets.UTF_8));

            HttpClient client = HttpClient.newHttpClient();

            WebSocket nativeSocket = client.newWebSocketBuilder()
                    .connectTimeout(Duration.ofSeconds(8))
                    .header("Authorization", "Bearer pr22-self-check")
                    .buildAsync(endpoint, new PassiveListener())
                    .get(10, TimeUnit.SECONDS);
            nativeSocket.sendClose(WebSocket.NORMAL_CLOSURE, "native qualified").join();

            String credential =
                    BrowserWebSocketCredential.encodeBearerProtocol("pr22-self-check");
            WebSocket browserSocket = client.newWebSocketBuilder()
                    .connectTimeout(Duration.ofSeconds(8))
                    .subprotocols(BrowserWebSocketCredential.LSP_PROTOCOL, credential)
                    .buildAsync(endpoint, new PassiveListener())
                    .get(10, TimeUnit.SECONDS);
            assertEquals(BrowserWebSocketCredential.LSP_PROTOCOL, browserSocket.getSubprotocol());
            browserSocket.sendClose(WebSocket.NORMAL_CLOSURE, "browser qualified").join();
        } finally {
            if (gateway != null) gateway.close();
            deleteTree(root);
        }
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

    private static final class PassiveListener implements WebSocket.Listener {
        @Override
        public void onOpen(WebSocket webSocket) {
            webSocket.request(1);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            return java.util.concurrent.CompletableFuture.completedFuture(null);
        }
    }
}
