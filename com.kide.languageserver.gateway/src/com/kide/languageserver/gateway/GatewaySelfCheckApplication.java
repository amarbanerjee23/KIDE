package com.kide.languageserver.gateway;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.google.gson.JsonArray;
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

public final class GatewaySelfCheckApplication implements IApplication {
    private static final Duration WAIT = Duration.ofSeconds(20);

    @Override
    public Object start(IApplicationContext applicationContext) {
        Path root = null;
        SecureLspWebSocketGateway gateway = null;
        String stage = "bootstrap";
        try {
            root = Files.createTempDirectory("kide-pr22-gateway-");
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project"));
            EnterpriseContextResult provisioned = new EnterpriseContextStore().provision(
                    workspace, project, "Gateway Org", "Gateway Portfolio",
                    "Gateway Project", "Gateway Workspace");
            if (!provisioned.isReady()) return fail("enterprise context provisioning failed");
            EnterpriseContext context = provisioned.context().orElseThrow();

            PrincipalIdentity principal = new PrincipalIdentity(
                    "selfcheck:gateway-user",
                    "Gateway Self Check",
                    PrincipalKind.LOCAL_OFFLINE,
                    AuthenticationMethod.LOCAL_OFFLINE,
                    "",
                    "gateway-user",
                    Map.of("offline", "true"));

            GatewayAuthenticator authenticator = header -> {
                if (!"Bearer pr22-self-check".equals(header)) {
                    throw new AuthenticationException("Bearer authentication is invalid");
                }
                return new AuthenticatedSession(principal, null, Clock.systemUTC());
            };

            InMemoryGatewayWorkspaceCatalog catalog = new InMemoryGatewayWorkspaceCatalog();
            catalog.register(new GatewayWorkspaceBinding(context, project));

            RoleBinding engineerBinding = RoleBinding.allow(
                    principal.id(), Role.ENGINEER, context.project().id());
            InMemoryAuthorizationPolicyStore policyStore =
                    new InMemoryAuthorizationPolicyStore(List.of(engineerBinding));
            ServerAuthorizationGate authorization = new ServerAuthorizationGate(
                    new AuthorizationEnforcer(
                            new AuthorizationService(policyStore)));

            GatewayConfig config = new GatewayConfig(
                    "127.0.0.1",
                    0,
                    Duration.ofSeconds(30),
                    1024 * 1024,
                    5000,
                    8,
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
                    + URLEncoder.encode(context.workspace().id().value(), StandardCharsets.UTF_8));

            stage = "unauthorized-handshake";
            if (!unauthorizedHandshakeIsDenied(endpoint)) {
                return fail("unauthorized WebSocket upgrade was accepted");
            }

            stage = "privilege-revocation";
            runPrivilegeRevocationQualification(
                    endpoint, project, policyStore, engineerBinding);
            Path outsideProject = Files.createDirectories(root.resolve("outside-project"));
            stage = "path-isolation";
            runPathIsolationQualification(endpoint, outsideProject);
            stage = "binary-rejection";
            runBinaryRejectionQualification(endpoint);
            stage = "five-language-lsp";
            runFullLanguageQualification(endpoint, project);
            stage = "reconnect";
            runReconnectQualification(endpoint, project);

            System.out.println("KIDE PR22 SECURE LSP WEBSOCKET SELF-CHECK OK");
            return IApplication.EXIT_OK;
        } catch (Exception e) {
            return fail("guarded self-check failure at " + stage
                    + ": " + rootCauseDetail(e));
        } finally {
            if (gateway != null) {
                try {
                    gateway.close();
                } catch (RuntimeException ignored) {
                    // Self-check cleanup only.
                }
            }
            deleteTree(root);
        }
    }

    @Override
    public void stop() { }

    private static boolean unauthorizedHandshakeIsDenied(URI endpoint) {
        ProbeListener listener = new ProbeListener();
        try {
            HttpClient.newHttpClient().newWebSocketBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .buildAsync(endpoint, listener)
                    .get(8, TimeUnit.SECONDS);
            return false;
        } catch (Exception expected) {
            return true;
        }
    }

    private static void runPrivilegeRevocationQualification(
            URI endpoint,
            Path project,
            InMemoryAuthorizationPolicyStore policyStore,
            RoleBinding engineerBinding) throws Exception {
        ProbeListener listener = new ProbeListener();
        WebSocket socket = connect(endpoint, listener);
        boolean closedByServer = false;
        boolean policyRevoked = false;
        try {
            socket.sendText(initialize(20, project), true).join();
            requireMessage(listener, message -> hasId(message, 20), "revocation initialize response");

            policyStore.replace(0, List.of());
            policyRevoked = true;
            socket.sendText(notification("initialized", new JsonObject()), true).join();
            Integer closeCode = listener.closeCodes.poll(10, TimeUnit.SECONDS);
            if (closeCode == null || closeCode.intValue() != 1008) {
                throw new IllegalStateException(
                        "privilege revocation did not close WebSocket with policy violation");
            }
            closedByServer = true;
        } finally {
            if (policyRevoked) {
                policyStore.replace(1, List.of(engineerBinding));
            }
            if (!closedByServer) {
                try {
                    socket.sendClose(WebSocket.NORMAL_CLOSURE, "revocation cleanup").join();
                } catch (RuntimeException ignored) {
                    // The server may already have closed the socket.
                }
            }
        }
    }

    private static void runPathIsolationQualification(
            URI endpoint,
            Path outsideProject) throws Exception {
        ProbeListener listener = new ProbeListener();
        WebSocket socket = connect(endpoint, listener);
        boolean closedByServer = false;
        try {
            socket.sendText(initialize(30, outsideProject), true).join();
            Integer closeCode = listener.closeCodes.poll(10, TimeUnit.SECONDS);
            if (closeCode == null || closeCode.intValue() != 1008) {
                throw new IllegalStateException(
                        "outside-project LSP root was not rejected with policy violation");
            }
            closedByServer = true;
        } finally {
            if (!closedByServer) {
                try {
                    socket.sendClose(WebSocket.NORMAL_CLOSURE, "path isolation cleanup").join();
                } catch (RuntimeException ignored) {
                    // The server may already have closed the socket.
                }
            }
        }
    }

    private static void runBinaryRejectionQualification(URI endpoint) throws Exception {
        ProbeListener listener = new ProbeListener();
        WebSocket socket = connect(endpoint, listener);
        boolean closedByServer = false;
        try {
            socket.sendBinary(ByteBuffer.wrap(new byte[] { 1, 2, 3 }), true).join();
            Integer closeCode = listener.closeCodes.poll(10, TimeUnit.SECONDS);
            if (closeCode == null || closeCode.intValue() != 1003) {
                throw new IllegalStateException(
                        "binary WebSocket message was not rejected with unsupported-data close");
            }
            closedByServer = true;
        } finally {
            if (!closedByServer) {
                try {
                    socket.sendClose(WebSocket.NORMAL_CLOSURE, "binary cleanup").join();
                } catch (RuntimeException ignored) {
                    // The server may already have closed the socket.
                }
            }
        }
    }

    private static void runFullLanguageQualification(URI endpoint, Path project) throws Exception {
        ProbeListener listener = new ProbeListener();
        WebSocket socket = connectBrowser(endpoint, listener);
        try {
            socket.sendText(initialize(1, project), true).join();
            requireMessage(listener, message -> hasId(message, 1), "initialize response");
            socket.sendText(notification("initialized", new JsonObject()), true).join();

            Map<String, String> probes = new LinkedHashMap<>();
            probes.put("dml", "DataModel CompletionProbe {\n  primitives { ");
            probes.put("op", "Operation CompletionProbe () {\n");
            probes.put("mncspec", "Model CompletionProbe\nInterfaceDescription Device {\n");
            probes.put("cap", "Capability CompletionProbe compatible component interface Device {\n");
            probes.put("activity", "ActivityDiagram CompletionProbe has activities { Activity Step {\n");

            int version = 1;
            for (Map.Entry<String, String> probe : probes.entrySet()) {
                URI uri = project.resolve("gateway-" + probe.getKey() + "." + probe.getKey()).toUri();
                String languageId = probe.getKey();
                socket.sendText(didOpen(uri, languageId, version, probe.getValue()), true).join();
                requireMessage(
                        listener,
                        message -> isDiagnosticsFor(message, uri.toString()),
                        "diagnostics for ." + probe.getKey());

                socket.sendText(didChange(uri, version + 1, probe.getValue() + "\n"), true).join();
                version += 2;
            }

            JsonObject cancelParams = new JsonObject();
            cancelParams.addProperty("id", 999);
            socket.sendText(notification("$/cancelRequest", cancelParams), true).join();

            socket.sendText(request(2, "shutdown", null), true).join();
            requireMessage(listener, message -> hasId(message, 2), "shutdown response");
            socket.sendText(notification("exit", null), true).join();
        } finally {
            socket.sendClose(WebSocket.NORMAL_CLOSURE, "qualified").join();
        }
    }

    private static void runReconnectQualification(URI endpoint, Path project) throws Exception {
        ProbeListener listener = new ProbeListener();
        WebSocket socket = connect(endpoint, listener);
        try {
            socket.sendText(initialize(10, project), true).join();
            requireMessage(listener, message -> hasId(message, 10), "reconnect initialize response");
            socket.sendText(request(11, "shutdown", null), true).join();
            requireMessage(listener, message -> hasId(message, 11), "reconnect shutdown response");
            socket.sendText(notification("exit", null), true).join();
        } finally {
            socket.sendClose(WebSocket.NORMAL_CLOSURE, "reconnect qualified").join();
        }
    }

    private static WebSocket connect(URI endpoint, ProbeListener listener) throws Exception {
        return HttpClient.newHttpClient().newWebSocketBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .header("Authorization", "Bearer pr22-self-check")
                .buildAsync(endpoint, listener)
                .get(10, TimeUnit.SECONDS);
    }

    private static WebSocket connectBrowser(URI endpoint, ProbeListener listener) throws Exception {
        String credential = BrowserWebSocketCredential.encodeBearerProtocol("pr22-self-check");
        return HttpClient.newHttpClient().newWebSocketBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .subprotocols(BrowserWebSocketCredential.LSP_PROTOCOL, credential)
                .buildAsync(endpoint, listener)
                .get(10, TimeUnit.SECONDS);
    }

    private static String initialize(int id, Path project) {
        JsonObject params = new JsonObject();
        params.addProperty("processId", ProcessHandle.current().pid());
        params.addProperty("rootUri", project.toUri().toString());
        params.add("capabilities", new JsonObject());
        return request(id, "initialize", params);
    }

    private static String didOpen(URI uri, String languageId, int version, String text) {
        JsonObject document = new JsonObject();
        document.addProperty("uri", uri.toString());
        document.addProperty("languageId", languageId);
        document.addProperty("version", version);
        document.addProperty("text", text);
        JsonObject params = new JsonObject();
        params.add("textDocument", document);
        return notification("textDocument/didOpen", params);
    }

    private static String didChange(URI uri, int version, String text) {
        JsonObject identifier = new JsonObject();
        identifier.addProperty("uri", uri.toString());
        identifier.addProperty("version", version);
        JsonObject change = new JsonObject();
        change.addProperty("text", text);
        JsonArray changes = new JsonArray();
        changes.add(change);
        JsonObject params = new JsonObject();
        params.add("textDocument", identifier);
        params.add("contentChanges", changes);
        return notification("textDocument/didChange", params);
    }

    private static String request(int id, String method, JsonObject params) {
        JsonObject message = new JsonObject();
        message.addProperty("jsonrpc", "2.0");
        message.addProperty("id", id);
        message.addProperty("method", method);
        if (params != null) message.add("params", params);
        return message.toString();
    }

    private static String notification(String method, JsonObject params) {
        JsonObject message = new JsonObject();
        message.addProperty("jsonrpc", "2.0");
        message.addProperty("method", method);
        if (params != null) message.add("params", params);
        return message.toString();
    }

    private static boolean hasId(String raw, int id) {
        try {
            JsonObject object = JsonParser.parseString(raw).getAsJsonObject();
            return object.has("id") && object.get("id").getAsInt() == id;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static boolean isDiagnosticsFor(String raw, String uri) {
        try {
            JsonObject object = JsonParser.parseString(raw).getAsJsonObject();
            if (!object.has("method")
                    || !"textDocument/publishDiagnostics".equals(object.get("method").getAsString())) {
                return false;
            }
            JsonObject params = object.getAsJsonObject("params");
            return params != null && uri.equals(params.get("uri").getAsString());
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static void requireMessage(
            ProbeListener listener,
            java.util.function.Predicate<String> predicate,
            String description) throws Exception {
        long deadline = System.nanoTime() + WAIT.toNanos();
        while (System.nanoTime() < deadline) {
            String message = listener.messages.poll(250, TimeUnit.MILLISECONDS);
            if (message != null && predicate.test(message)) return;
            if (listener.failure != null) throw new IllegalStateException(description, listener.failure);
        }
        throw new IllegalStateException("Timed out waiting for " + description);
    }

    private static String rootCauseDetail(Throwable failure) {
        Throwable current = failure;
        Throwable last = failure;
        int depth = 0;
        while (current != null && current != current.getCause() && depth++ < 12) {
            if (current instanceof java.net.http.WebSocketHandshakeException handshake) {
                return "WebSocketHandshakeException(status="
                        + handshake.getResponse().statusCode() + ")";
            }
            last = current;
            current = current.getCause();
        }
        if (current instanceof java.net.http.WebSocketHandshakeException handshake) {
            return "WebSocketHandshakeException(status="
                    + handshake.getResponse().statusCode() + ")";
        }
        if (current != null) last = current;
        String simple = last.getClass().getSimpleName();
        return simple == null || simple.isBlank() ? last.getClass().getName() : simple;
    }

    private static Integer fail(String message) {
        System.err.println("KIDE PR22 SECURE LSP WEBSOCKET SELF-CHECK FAILED: " + message);
        return Integer.valueOf(2);
    }

    private static void deleteTree(Path root) {
        if (root == null || !Files.exists(root)) return;
        try (java.util.stream.Stream<Path> stream = Files.walk(root)) {
            stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // Self-check cleanup only.
                }
            });
        } catch (IOException ignored) {
            // Self-check cleanup only.
        }
    }

    private static final class ProbeListener implements WebSocket.Listener {
        private final BlockingQueue<String> messages = new LinkedBlockingQueue<>();
        private final BlockingQueue<Integer> closeCodes = new LinkedBlockingQueue<>();
        private final StringBuilder partial = new StringBuilder();
        private volatile Throwable failure;

        @Override
        public void onOpen(WebSocket webSocket) {
            webSocket.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            partial.append(data);
            if (last) {
                messages.add(partial.toString());
                partial.setLength(0);
            }
            webSocket.request(1);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            closeCodes.offer(statusCode);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            failure = error;
        }
    }
}
