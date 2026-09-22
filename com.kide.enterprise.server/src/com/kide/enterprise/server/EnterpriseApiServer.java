package com.kide.enterprise.server;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.Callback;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kide.enterprise.api.ApiErrorCode;
import com.kide.enterprise.api.ApiErrorEnvelope;
import com.kide.enterprise.api.ApiExceptionMapper;
import com.kide.enterprise.api.ApiVersion;
import com.kide.enterprise.api.OpenApiV1;
import com.kide.enterprise.audit.AuditEventDraft;
import com.kide.enterprise.audit.AuditLedger;
import com.kide.enterprise.audit.AuditOutcome;
import com.kide.enterprise.authorization.AccessDeniedException;
import com.kide.enterprise.authorization.ServerAuthorizationGate;
import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.identity.AuthenticatedSession;
import com.kide.enterprise.identity.AuthenticationException;
import com.kide.enterprise.modelrepo.ModelPath;
import com.kide.enterprise.modelrepo.ModelRepository;
import com.kide.enterprise.modelrepo.ModelRepositoryException;
import com.kide.enterprise.modelrepo.ModelSnapshot;
import com.kide.enterprise.modelrepo.ModelTransaction;
import com.kide.enterprise.modelrepo.RevisionConflictException;

/**
 * Shared HTTP runtime for browser and service clients. It binds the PR20 API
 * contract to the same identity, authorization, audit and revision semantics
 * already used by desktop/headless KIDE.
 */
public final class EnterpriseApiServer implements AutoCloseable {
    private static final String API_PREFIX = "/api/v1";
    private static final String CLIENT_ID = "kide-api-v1";
    private static final String MISSING_ETAG = "0".repeat(64);

    private final EnterpriseApiConfig config;
    private final Function<String, AuthenticatedSession> authenticator;
    private final EnterpriseContext context;
    private final ServerAuthorizationGate authorization;
    private final ModelRepository models;
    private final ProjectCollaborationService collaboration;
    private final AuditLedger audit;
    private final Clock clock;
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private final Server server;
    private final ServerConnector connector;

    public EnterpriseApiServer(
            EnterpriseApiConfig config,
            Function<String, AuthenticatedSession> authenticator,
            EnterpriseContext context,
            ServerAuthorizationGate authorization,
            ModelRepository models,
            ProjectCollaborationService collaboration,
            AuditLedger audit,
            Clock clock) {
        this.config = Objects.requireNonNull(config, "config");
        this.authenticator = Objects.requireNonNull(authenticator, "authenticator");
        this.context = Objects.requireNonNull(context, "context");
        this.authorization = Objects.requireNonNull(authorization, "authorization");
        this.models = Objects.requireNonNull(models, "models");
        this.collaboration = Objects.requireNonNull(collaboration, "collaboration");
        this.audit = Objects.requireNonNull(audit, "audit");
        this.clock = Objects.requireNonNull(clock, "clock");

        server = new Server();
        connector = new ServerConnector(server);
        connector.setHost(config.bindHost());
        connector.setPort(config.port());
        connector.setIdleTimeout(config.idleTimeout().toMillis());
        server.addConnector(connector);
        server.setHandler(new ApiHandler());
    }

    public void start() throws Exception { server.start(); }

    public int localPort() {
        int local = connector.getLocalPort();
        return local >= 0 ? local : config.port();
    }

    public void join() throws InterruptedException { server.join(); }
    public boolean isStarted() { return server.isStarted(); }

    @Override
    public void close() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new IllegalStateException("Could not stop enterprise API server", e);
        }
    }

    private final class ApiHandler extends Handler.Abstract {
        @Override
        public boolean handle(Request request, Response response, Callback callback) {
            UUID requestId = requestId(request.getHeaders().get("X-Request-Id"));
            String origin = request.getHeaders().get("Origin");
            try {
                if (!secureEnough(
                        request.getConnectionMetaData().isSecure(),
                        request.getHeaders().get("X-Forwarded-Proto"),
                        request.getConnectionMetaData().getRemoteSocketAddress())) {
                    writeError(response, callback, requestId, 400, ApiErrorCode.BAD_REQUEST,
                            "Secure transport is required.");
                    return true;
                }
                if (!originAllowed(origin)) {
                    writeError(response, callback, requestId, 403, ApiErrorCode.FORBIDDEN,
                            "The request origin is not permitted.");
                    return true;
                }
                addCommonHeaders(response, requestId, origin);

                String path = request.getHttpURI().getPath();
                if (path == null || !path.startsWith(API_PREFIX)) return false;

                if ("OPTIONS".equals(request.getMethod())) {
                    response.setStatus(204);
                    response.getHeaders().put("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
                    response.getHeaders().put(
                            "Access-Control-Allow-Headers",
                            "Authorization,Content-Type,If-Match,X-Request-Id");
                    callback.succeeded();
                    return true;
                }

                if ("/api/v1/health".equals(path) && "GET".equals(request.getMethod())) {
                    JsonObject health = new JsonObject();
                    health.addProperty("status", "UP");
                    health.addProperty("version", ApiVersion.V1.token());
                    JsonObject dependencies = new JsonObject();
                    dependencies.addProperty("modelRepository", "AVAILABLE");
                    health.add("dependencies", dependencies);
                    writeJson(response, callback, 200, health);
                    return true;
                }

                if ("/api/v1/openapi.json".equals(path) && "GET".equals(request.getMethod())) {
                    writeRawJson(response, callback, 200, OpenApiV1.generateJson());
                    return true;
                }

                try (AuthenticatedSession session =
                        authenticator.apply(request.getHeaders().get("Authorization"))) {
                    session.requireActive();
                    return handleAuthenticated(request, response, callback, requestId, session, path);
                }
            } catch (AuthenticationException e) {
                writeError(response, callback, requestId, 401, ApiErrorCode.UNAUTHENTICATED,
                        "Authentication is required.");
                return true;
            } catch (AccessDeniedException e) {
                writeError(response, callback, requestId, 403, ApiErrorCode.FORBIDDEN,
                        "The requested operation is not permitted.");
                return true;
            } catch (RevisionConflictException e) {
                writeError(response, callback, requestId, 409, ApiErrorCode.CONFLICT,
                        "The model revision is stale.");
                return true;
            } catch (ResourceNotFoundException | ProjectCollaborationService.NotFoundException e) {
                writeError(response, callback, requestId, 404, ApiErrorCode.NOT_FOUND,
                        "The requested API resource was not found.");
                return true;
            } catch (RequestTooLargeException e) {
                writeError(response, callback, requestId, 413, ApiErrorCode.TOO_LARGE,
                        "The request body is too large.");
                return true;
            } catch (ModelRepositoryException e) {
                writeError(response, callback, requestId, 503, ApiErrorCode.SERVICE_UNAVAILABLE,
                        "The model repository is unavailable.");
                return true;
            } catch (IllegalArgumentException e) {
                writeError(response, callback, requestId, 400, ApiErrorCode.BAD_REQUEST,
                        "The request is invalid.");
                return true;
            } catch (RuntimeException e) {
                ApiErrorEnvelope mapped = ApiExceptionMapper.map(requestId, e);
                writeError(response, callback, requestId, statusFor(mapped.code()),
                        mapped.code(), mapped.message());
                return true;
            }
        }
    }

    private boolean handleAuthenticated(
            Request request,
            Response response,
            Callback callback,
            UUID requestId,
            AuthenticatedSession session,
            String path) {
        String relative = path.substring(API_PREFIX.length());
        String[] segments = java.util.Arrays.stream(relative.split("/"))
                .filter(value -> !value.isEmpty())
                .toArray(String[]::new);

        if (segments.length == 1 && "projects".equals(segments[0])) {
            authorization.requireApiProjectAccess(session, context);
            if ("GET".equals(request.getMethod())) {
                JsonObject body = new JsonObject();
                com.google.gson.JsonArray items = new com.google.gson.JsonArray();
                items.add(projectJson());
                body.add("items", items);
                audit(session, requestId, "project.list", "project",
                        context.project().id().value(), AuditOutcome.SUCCESS, "context-v1");
                writeJson(response, callback, 200, body);
                return true;
            }
            if ("POST".equals(request.getMethod())) {
                unavailable(response, callback, requestId,
                        "Project creation is not enabled in this runtime phase.");
                return true;
            }
            methodNotAllowed(response, callback, requestId);
            return true;
        }

        if (segments.length >= 2 && "projects".equals(segments[0])) {
            requireProject(segments[1]);
            authorization.requireApiProjectAccess(session, context);

            if (segments.length == 2) {
                if (!"GET".equals(request.getMethod())) {
                    methodNotAllowed(response, callback, requestId);
                    return true;
                }
                audit(session, requestId, "project.read", "project", segments[1],
                        AuditOutcome.SUCCESS, "context-v1");
                writeJson(response, callback, 200, projectJson());
                return true;
            }

            if (segments.length == 3 && "models".equals(segments[2])) {
                authorization.requireModelRead(session, context);
                if ("GET".equals(request.getMethod())) {
                    unavailable(response, callback, requestId,
                            "Model listing awaits the repository index phase.");
                } else {
                    methodNotAllowed(response, callback, requestId);
                }
                return true;
            }

            if (segments.length == 4 && "models".equals(segments[2])) {
                String modelId = decodeModelId(segments[3]);
                ModelPath modelPath = new ModelPath(modelId);
                if ("GET".equals(request.getMethod())) {
                    authorization.requireModelRead(session, context);
                    ModelSnapshot snapshot = models.read(modelPath).orElse(null);
                    if (snapshot == null) {
                        writeError(response, callback, requestId, 404, ApiErrorCode.NOT_FOUND,
                                "The requested model was not found.");
                        return true;
                    }
                    audit(session, requestId, "model.read", "model", modelId,
                            AuditOutcome.SUCCESS, Long.toString(snapshot.revision().version()));
                    writeJson(response, callback, 200, modelJson(snapshot, "text/plain"));
                    return true;
                }
                if ("PUT".equals(request.getMethod())) {
                    authorization.requireModelWrite(session, context);
                    JsonObject input = readJsonObject(request);
                    String content = requiredString(input, "content");
                    String expected = requiredString(input, "expectedRevision");
                    String mediaType = optionalString(input, "mediaType", "text/plain");
                    try (ModelTransaction tx = models.beginTransaction()) {
                        tx.write(modelPath, content.getBytes(StandardCharsets.UTF_8), expected);
                        tx.commit();
                    }
                    ModelSnapshot snapshot = models.read(modelPath).orElseThrow();
                    audit(session, requestId, "model.write", "model", modelId,
                            AuditOutcome.SUCCESS, Long.toString(snapshot.revision().version()));
                    writeJson(response, callback, 200, modelJson(snapshot, mediaType));
                    return true;
                }
                methodNotAllowed(response, callback, requestId);
                return true;
            }

            if (segments.length >= 4
                    && "collaboration".equals(segments[2])
                    && "sessions".equals(segments[3])) {
                return handlePresence(
                        request, response, callback, requestId, session, segments);
            }

            if (segments.length >= 4
                    && "reviews".equals(segments[2])
                    && "changesets".equals(segments[3])) {
                return handleReviews(
                        request, response, callback, requestId, session, segments);
            }

            if (segments.length == 4
                    && "knowledge".equals(segments[2])
                    && "query".equals(segments[3])) {
                if (!"POST".equals(request.getMethod())) {
                    methodNotAllowed(response, callback, requestId);
                } else {
                    unavailable(response, callback, requestId,
                            "Knowledge services are not installed yet.");
                }
                return true;
            }

            if (segments.length == 3 && "synthesis".equals(segments[2])) {
                authorization.requireModelSynthesis(session, context);
                if (!"POST".equals(request.getMethod())) {
                    methodNotAllowed(response, callback, requestId);
                } else {
                    unavailable(response, callback, requestId,
                            "Synthesis services are not installed yet.");
                }
                return true;
            }

            if (segments.length == 3 && "evidence".equals(segments[2])) {
                authorization.requireEvidenceRead(session, context);
                if (!"GET".equals(request.getMethod())) {
                    methodNotAllowed(response, callback, requestId);
                } else {
                    unavailable(response, callback, requestId,
                            "Evidence indexing is not installed yet.");
                }
                return true;
            }
        }

        writeError(response, callback, requestId, 404, ApiErrorCode.NOT_FOUND,
                "The requested API resource was not found.");
        return true;
    }

    private JsonObject projectJson() {
        JsonObject project = new JsonObject();
        project.addProperty("id", context.project().id().value());
        project.addProperty("displayName", context.project().displayName());
        project.addProperty("revision", "1");
        project.addProperty("portfolioId", context.portfolio().id().value());
        project.addProperty("workspaceId", context.workspace().id().value());
        return project;
    }

    private JsonObject modelJson(ModelSnapshot snapshot, String mediaType) {
        JsonObject model = new JsonObject();
        model.addProperty("id", snapshot.path().value());
        model.addProperty("content", new String(snapshot.content(), StandardCharsets.UTF_8));
        model.addProperty("revision", Long.toString(snapshot.revision().version()));
        model.addProperty("etag", snapshot.revision().etag());
        model.addProperty("mediaType", mediaType);
        return model;
    }

    private JsonObject readJsonObject(Request request) {
        try (InputStream input = Content.Source.asInputStream(request)) {
            byte[] body = input.readNBytes(config.maxRequestBytes() + 1);
            if (body.length > config.maxRequestBytes()) {
                throw new RequestTooLargeException();
            }
            String text = new String(body, StandardCharsets.UTF_8);
            if (text.isBlank()) throw new IllegalArgumentException("JSON body is required");
            var parsed = JsonParser.parseString(text);
            if (!parsed.isJsonObject()) throw new IllegalArgumentException("JSON object is required");
            return parsed.getAsJsonObject();
        } catch (java.io.IOException e) {
            throw new IllegalArgumentException("Request body could not be read");
        }
    }

    private void audit(
            AuthenticatedSession session,
            UUID requestId,
            String action,
            String resourceType,
            String resourceId,
            AuditOutcome outcome,
            String revision) {
        audit.append(AuditEventDraft.of(
                session.principal(), CLIENT_ID, context, revision, action,
                resourceType, resourceId, outcome, requestId, requestId, Map.of()));
    }

    private void requireProject(String projectId) {
        if (!context.project().id().value().equals(projectId)) {
            throw new ResourceNotFoundException();
        }
    }

    private static String decodeModelId(String encoded) {
        String value = URLDecoder.decode(encoded, StandardCharsets.UTF_8);
        if (value.isBlank() || value.length() > 512 || value.contains("/")) {
            throw new IllegalArgumentException("modelId is invalid");
        }
        return value;
    }

    private boolean originAllowed(String origin) {
        Set<String> allowed = config.allowedOrigins();
        return origin == null || origin.isBlank() || allowed.isEmpty() || allowed.contains(origin);
    }

    private boolean secureEnough(boolean directSecure, String forwardedProto, SocketAddress remote) {
        if (!config.requireSecureTransport()) return true;
        if (directSecure) return true;
        if (!config.trustForwardedProto()
                || forwardedProto == null
                || !"https".equalsIgnoreCase(forwardedProto.trim())) return false;
        if (!(remote instanceof InetSocketAddress inet)) return false;
        if (inet.getAddress() != null
                && config.trustedProxyAddresses().contains(inet.getAddress().getHostAddress())) {
            return true;
        }
        return config.trustedProxyAddresses().contains(inet.getHostString());
    }

    private void addCommonHeaders(Response response, UUID requestId, String origin) {
        response.getHeaders().put("X-Request-Id", requestId.toString());
        response.getHeaders().put("Cache-Control", "no-store");
        response.getHeaders().put("X-Content-Type-Options", "nosniff");
        if (origin != null && config.allowedOrigins().contains(origin)) {
            response.getHeaders().put("Access-Control-Allow-Origin", origin);
            response.getHeaders().put("Vary", "Origin");
        }
    }

    private void unavailable(
            Response response, Callback callback, UUID requestId, String message) {
        writeError(response, callback, requestId, 503, ApiErrorCode.SERVICE_UNAVAILABLE, message);
    }

    private void methodNotAllowed(Response response, Callback callback, UUID requestId) {
        writeError(response, callback, requestId, 405, ApiErrorCode.BAD_REQUEST,
                "The HTTP method is not supported for this resource.");
    }

    private void writeError(
            Response response,
            Callback callback,
            UUID requestId,
            int status,
            ApiErrorCode code,
            String message) {
        if (response.isCommitted()) return;
        ApiErrorEnvelope error = new ApiErrorEnvelope(
                ApiVersion.V1.token(), requestId, code, message, Map.of());
        writeRawJson(response, callback, status, gson.toJson(error));
    }

    private void writeJson(Response response, Callback callback, int status, JsonObject body) {
        writeRawJson(response, callback, status, gson.toJson(body));
    }

    private void writeRawJson(Response response, Callback callback, int status, String json) {
        response.setStatus(status);
        response.getHeaders().put("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        response.write(true, ByteBuffer.wrap(bytes), callback);
    }

    private static UUID requestId(String candidate) {
        if (candidate != null && candidate.length() <= 64) {
            try {
                return UUID.fromString(candidate.trim());
            } catch (IllegalArgumentException ignored) {
                // Client request IDs are advisory; generate a safe server ID instead.
            }
        }
        return UUID.randomUUID();
    }

    private static int statusFor(ApiErrorCode code) {
        return switch (code) {
            case BAD_REQUEST -> 400;
            case UNAUTHENTICATED -> 401;
            case FORBIDDEN -> 403;
            case NOT_FOUND -> 404;
            case CONFLICT -> 409;
            case TOO_LARGE -> 413;
            case RATE_LIMITED -> 429;
            case SERVICE_UNAVAILABLE -> 503;
            case INTERNAL_ERROR -> 500;
        };
    }

    private static String requiredString(JsonObject object, String key) {
        if (!object.has(key) || !object.get(key).isJsonPrimitive()) {
            throw new IllegalArgumentException(key + " is required");
        }
        String value = object.get(key).getAsString();
        if (value == null || value.isBlank()) throw new IllegalArgumentException(key + " is required");
        return value;
    }

    private static String optionalString(JsonObject object, String key, String fallback) {
        if (!object.has(key) || !object.get(key).isJsonPrimitive()) return fallback;
        String value = object.get(key).getAsString();
        return value == null || value.isBlank() ? fallback : value;
    }

    private static final class ResourceNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private static final class RequestTooLargeException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
