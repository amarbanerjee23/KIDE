package com.kide.enterprise.server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kide.enterprise.audit.InMemoryAuditLedger;
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
import com.kide.enterprise.modelrepo.ServerModelRepository;

public final class EnterpriseApiSelfCheckApplication implements IApplication {
    private volatile EnterpriseApiServer server;

    @Override
    public Object start(IApplicationContext applicationContext) {
        Path root = null;
        try {
            root = Files.createTempDirectory("kide-pr26-api-");
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project"));
            EnterpriseContextResult provisioned = new EnterpriseContextStore().provision(
                    workspace, project, "API Org", "API Portfolio", "API Project", "API Workspace");
            if (!provisioned.isReady()) throw new IllegalStateException(provisioned.summary());
            EnterpriseContext context = provisioned.context().orElseThrow();

            PrincipalIdentity principal = new PrincipalIdentity(
                    "selfcheck:api-engineer",
                    "API Engineer",
                    PrincipalKind.LOCAL_OFFLINE,
                    AuthenticationMethod.LOCAL_OFFLINE,
                    "",
                    "api-engineer",
                    Map.of("offline", "true"));

            InMemoryAuthorizationPolicyStore policies =
                    new InMemoryAuthorizationPolicyStore(List.of(
                            RoleBinding.allow(
                                    principal.id(), Role.ENGINEER, context.project().id())));
            ServerAuthorizationGate authorization = new ServerAuthorizationGate(
                    new AuthorizationEnforcer(new AuthorizationService(policies)));

            InMemoryAuditLedger audit = new InMemoryAuditLedger(Clock.systemUTC());
            server = new EnterpriseApiServer(
                    new EnterpriseApiConfig(
                            "127.0.0.1", 0, 1024 * 1024, Duration.ofSeconds(20),
                            false, false, Set.of(), Set.of("https://web.example.test")),
                    header -> {
                        if (!"Bearer pr26-self-check".equals(header)) {
                            throw new AuthenticationException("Bearer authentication is invalid");
                        }
                        return new AuthenticatedSession(principal, null, Clock.systemUTC());
                    },
                    context,
                    authorization,
                    new ServerModelRepository(),
                    audit,
                    Clock.systemUTC());
            server.start();

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5)).build();
            URI base = URI.create("http://127.0.0.1:" + server.localPort());

            HttpResponse<String> health = send(client, base.resolve("/api/v1/health"), "GET", null, null);
            requireStatus(health, 200);
            if (!"UP".equals(json(health).get("status").getAsString())) {
                throw new AssertionError("health status is not UP");
            }

            HttpResponse<String> denied = send(
                    client,
                    base.resolve("/api/v1/projects/" + context.project().id().value()),
                    "GET", null, null);
            requireStatus(denied, 401);

            HttpResponse<String> projectResponse = send(
                    client,
                    base.resolve("/api/v1/projects/" + context.project().id().value()),
                    "GET", "Bearer pr26-self-check", null);
            requireStatus(projectResponse, 200);
            JsonObject projectJson = json(projectResponse);
            if (!context.project().id().value().equals(projectJson.get("id").getAsString())) {
                throw new AssertionError("project identity mismatch");
            }
            if (!context.workspace().id().value().equals(projectJson.get("workspaceId").getAsString())) {
                throw new AssertionError("workspace identity missing from browser project contract");
            }

            String modelUri = "/api/v1/projects/" + context.project().id().value()
                    + "/models/selfcheck.dml";
            JsonObject create = new JsonObject();
            create.addProperty("content", "domain SelfCheck");
            create.addProperty("expectedRevision", "0".repeat(64));
            create.addProperty("mediaType", "text/x-kide-dml");
            HttpResponse<String> written = send(
                    client, base.resolve(modelUri), "PUT", "Bearer pr26-self-check", create.toString());
            requireStatus(written, 200);
            String etag = json(written).get("etag").getAsString();

            JsonObject stale = new JsonObject();
            stale.addProperty("content", "domain Stale");
            stale.addProperty("expectedRevision", "0".repeat(64));
            HttpResponse<String> conflict = send(
                    client, base.resolve(modelUri), "PUT", "Bearer pr26-self-check", stale.toString());
            requireStatus(conflict, 409);

            HttpResponse<String> read = send(
                    client, base.resolve(modelUri), "GET", "Bearer pr26-self-check", null);
            requireStatus(read, 200);
            JsonObject readJson = json(read);
            if (!etag.equals(readJson.get("etag").getAsString())
                    || !"domain SelfCheck".equals(readJson.get("content").getAsString())) {
                throw new AssertionError("model round-trip mismatch");
            }

            HttpResponse<String> future = send(
                    client,
                    base.resolve("/api/v1/projects/" + context.project().id().value()
                            + "/knowledge/query"),
                    "POST", "Bearer pr26-self-check", "{}");
            requireStatus(future, 503);
            if (!"SERVICE_UNAVAILABLE".equals(json(future).get("code").getAsString())) {
                throw new AssertionError("future service did not fail with typed unavailable state");
            }

            if (!audit.verify() || audit.snapshot().size() < 3) {
                throw new AssertionError("audit evidence missing or invalid");
            }

            System.out.println("KIDE PR26 ENTERPRISE API SELF-CHECK OK");
            return IApplication.EXIT_OK;
        } catch (Throwable failure) {
            System.err.println("KIDE PR26 enterprise API self-check failed: "
                    + failure.getClass().getSimpleName());
            return Integer.valueOf(2);
        } finally {
            if (server != null) {
                try { server.close(); } catch (RuntimeException ignored) { }
            }
            deleteTree(root);
        }
    }

    @Override
    public void stop() {
        EnterpriseApiServer current = server;
        if (current != null) {
            try { current.close(); } catch (RuntimeException ignored) { }
        }
    }

    private static HttpResponse<String> send(
            HttpClient client, URI uri, String method, String authorization, String body)
            throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(8))
                .header("Accept", "application/json")
                .header("Origin", "https://web.example.test");
        if (authorization != null) builder.header("Authorization", authorization);
        if (body == null) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder.header("Content-Type", "application/json");
            builder.method(method, HttpRequest.BodyPublishers.ofString(body));
        }
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private static JsonObject json(HttpResponse<String> response) {
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    private static void requireStatus(HttpResponse<?> response, int expected) {
        if (response.statusCode() != expected) {
            throw new AssertionError(
                    "expected HTTP " + expected + " but received " + response.statusCode());
        }
    }

    private static void deleteTree(Path root) {
        if (root == null || !Files.exists(root)) return;
        try (java.util.stream.Stream<Path> stream = Files.walk(root)) {
            stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) { }
            });
        } catch (IOException ignored) { }
    }
}
