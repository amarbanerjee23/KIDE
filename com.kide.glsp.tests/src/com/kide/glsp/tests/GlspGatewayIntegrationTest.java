package com.kide.glsp.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import com.kide.glsp.SecureGlspWebSocketGateway;
import com.kide.languageserver.gateway.BrowserWebSocketCredential;
import com.kide.languageserver.gateway.GatewayAuthenticator;
import com.kide.languageserver.gateway.GatewayConfig;
import com.kide.languageserver.gateway.GatewayWorkspaceBinding;
import com.kide.languageserver.gateway.InMemoryGatewayWorkspaceCatalog;

public class GlspGatewayIntegrationTest {
    @Test
    public void browserLoadsEditsAndSavesCanonicalMncThroughGlsp() throws Exception {
        Path root = Files.createTempDirectory("kide-pr32-glsp-");
        SecureGlspWebSocketGateway gateway = null;
        WebSocket socket = null;
        try {
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project"));
            EnterpriseContextResult provisioned = new EnterpriseContextStore().provision(
                    workspace, project, "GLSP Org", "GLSP Portfolio",
                    "GLSP Project", "GLSP Workspace");
            if (!provisioned.isReady()) throw new AssertionError(provisioned.summary());
            EnterpriseContext context = provisioned.context().orElseThrow();

            Path source = project.resolve("selfcheck.mncspec");
            Files.writeString(
                    source,
                    "Model Golden\n"
                    + "InterfaceDescription Device {\n"
                    + "  commands { Start[] }\n"
                    + "  events { Publish Ready[] }\n"
                    + "}\n");

            PrincipalIdentity principal = new PrincipalIdentity(
                    "selfcheck:glsp-user",
                    "GLSP User",
                    PrincipalKind.LOCAL_OFFLINE,
                    AuthenticationMethod.LOCAL_OFFLINE,
                    "",
                    "glsp-user",
                    Map.of("offline", "true"));

            GatewayAuthenticator authenticator = header -> {
                if (!"Bearer pr32-self-check".equals(header)) {
                    throw new AuthenticationException("Bearer authentication is invalid");
                }
                return new AuthenticatedSession(principal, null, Clock.systemUTC());
            };

            InMemoryGatewayWorkspaceCatalog catalog = new InMemoryGatewayWorkspaceCatalog();
            catalog.register(new GatewayWorkspaceBinding(context, project));

            ServerAuthorizationGate authorization = new ServerAuthorizationGate(
                    new AuthorizationEnforcer(new AuthorizationService(
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

            gateway = new SecureGlspWebSocketGateway(
                    config, authenticator, catalog, authorization, Clock.systemUTC());
            gateway.start();

            URI endpoint = URI.create(
                    "ws://127.0.0.1:" + gateway.localPort()
                    + "/glsp?workspaceId="
                    + URLEncoder.encode(
                            context.workspace().id().value(),
                            StandardCharsets.UTF_8));

            MessageListener listener = new MessageListener();
            String credential =
                    BrowserWebSocketCredential.encodeBearerProtocol("pr32-self-check");
            socket = HttpClient.newHttpClient().newWebSocketBuilder()
                    .connectTimeout(Duration.ofSeconds(8))
                    .subprotocols(BrowserWebSocketCredential.GLSP_PROTOCOL, credential)
                    .buildAsync(endpoint, listener)
                    .get(10, TimeUnit.SECONDS);
            assertEquals(
                    BrowserWebSocketCredential.GLSP_PROTOCOL,
                    socket.getSubprotocol());

            send(socket,
                    "{\"jsonrpc\":\"2.0\",\"id\":1,"
                    + "\"method\":\"initialize\",\"params\":{"
                    + "\"applicationId\":\"KIDE GLSP Test\","
                    + "\"protocolVersion\":\"1.0.0\"}}");
            JsonObject initialize = listener.await(
                    message -> responseId(message, 1));
            assertEquals(
                    "1.0.0",
                    initialize.getAsJsonObject("result")
                            .get("protocolVersion").getAsString());

            send(socket,
                    "{\"jsonrpc\":\"2.0\",\"id\":2,"
                    + "\"method\":\"initializeClientSession\",\"params\":{"
                    + "\"clientSessionId\":\"pr32-session\","
                    + "\"diagramType\":\"kide-mnc-diagram\","
                    + "\"clientActionKinds\":[\"setModel\",\"updateModel\","
                    + "\"setDirtyState\",\"setMarkers\",\"setTypeHints\"]}}");
            listener.await(message -> responseId(message, 2));

            sendProcess(socket,
                    "{\"kind\":\"requestModel\",\"requestId\":\"model-1\","
                    + "\"options\":{\"sourceUri\":\"kide-workspace:/selfcheck.mncspec\","
                    + "\"diagramType\":\"kide-mnc-diagram\"}}");
            JsonObject setModel = listener.await(
                    message -> actionKind(message, "setModel")
                            && actionResponseId(message, "model-1"));
            assertTrue(setModel.toString().contains("kide:mnc-interface"));
            assertTrue(!setModel.toString().contains(project.toString()));

            sendProcess(socket,
                    "{\"kind\":\"requestTypeHints\","
                    + "\"requestId\":\"hints-1\"}");
            JsonObject hints = listener.await(
                    message -> actionKind(message, "setTypeHints")
                            && actionResponseId(message, "hints-1"));
            assertTrue(hints.toString().contains("kide:mnc-control-node"));

            sendProcess(socket,
                    "{\"kind\":\"createNode\",\"isOperation\":true,"
                    + "\"elementTypeId\":\"kide:mnc-control-node\","
                    + "\"location\":{\"x\":320,\"y\":160}}");
            JsonObject update = listener.await(
                    message -> actionKind(message, "updateModel"));
            assertTrue(update.toString().contains("ControlNode2"));

            sendProcess(socket, "{\"kind\":\"saveModel\"}");
            listener.await(
                    message -> actionKind(message, "setDirtyState")
                            && !message.getAsJsonObject("params")
                                    .getAsJsonObject("action")
                                    .get("isDirty").getAsBoolean());

            String persisted = Files.readString(source);
            assertTrue(persisted.contains("ControlNode2"));
            assertTrue(persisted.contains("implements interface Device"));
            assertTrue(Files.exists(project.resolve("selfcheck.notation")));
        } finally {
            if (socket != null) {
                try {
                    socket.sendClose(
                            WebSocket.NORMAL_CLOSURE, "qualified").join();
                } catch (RuntimeException ignored) { }
            }
            if (gateway != null) gateway.close();
            deleteTree(root);
        }
    }

    private static void sendProcess(WebSocket socket, String action) {
        send(socket,
                "{\"jsonrpc\":\"2.0\",\"method\":\"process\","
                + "\"params\":{\"clientId\":\"pr32-session\","
                + "\"action\":" + action + "}}");
    }

    private static void send(WebSocket socket, String message) {
        socket.sendText(message, true).join();
    }

    private static boolean responseId(JsonObject message, int expected) {
        return message.has("id") && message.get("id").getAsInt() == expected;
    }

    private static boolean actionKind(JsonObject message, String expected) {
        try {
            return "process".equals(message.get("method").getAsString())
                    && expected.equals(
                            message.getAsJsonObject("params")
                                    .getAsJsonObject("action")
                                    .get("kind").getAsString());
        } catch (RuntimeException failure) {
            return false;
        }
    }

    private static boolean actionResponseId(JsonObject message, String expected) {
        try {
            return expected.equals(
                    message.getAsJsonObject("params")
                            .getAsJsonObject("action")
                            .get("responseId").getAsString());
        } catch (RuntimeException failure) {
            return false;
        }
    }

    private static void deleteTree(Path root) {
        if (root == null || !Files.exists(root)) return;
        try (java.util.stream.Stream<Path> stream = Files.walk(root)) {
            for (Path path : stream.sorted(Comparator.reverseOrder()).toList()) {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) { }
            }
        } catch (IOException ignored) { }
    }

    private static final class MessageListener implements WebSocket.Listener {
        private final StringBuilder partial = new StringBuilder();
        private final BlockingQueue<JsonObject> messages = new LinkedBlockingQueue<>();

        @Override
        public void onOpen(WebSocket webSocket) {
            webSocket.request(1);
        }

        @Override
        public CompletionStage<?> onText(
                WebSocket webSocket, CharSequence data, boolean last) {
            partial.append(data);
            if (last) {
                try {
                    messages.add(
                            JsonParser.parseString(partial.toString())
                                    .getAsJsonObject());
                } finally {
                    partial.setLength(0);
                }
            }
            webSocket.request(1);
            return java.util.concurrent.CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletionStage<?> onClose(
                WebSocket webSocket, int statusCode, String reason) {
            return java.util.concurrent.CompletableFuture.completedFuture(null);
        }

        JsonObject await(Predicate<JsonObject> predicate) throws Exception {
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(15);
            while (System.nanoTime() < deadline) {
                JsonObject message = messages.poll(1, TimeUnit.SECONDS);
                if (message != null && predicate.test(message)) return message;
            }
            throw new AssertionError("Timed out waiting for GLSP message");
        }
    }
}
