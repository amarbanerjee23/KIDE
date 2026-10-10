package com.kide.enterprise.server;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Base64;
import java.util.HashSet;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kide.enterprise.api.ApiErrorCode;
import com.kide.enterprise.api.ApiErrorEnvelope;
import com.kide.enterprise.api.ApiExceptionMapper;
import com.kide.enterprise.api.ApiVersion;
import com.kide.enterprise.api.OpenApiV1;
import com.kide.enterprise.api.RuntimeCompatibility;
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
import com.kide.knowledge.KnowledgeRepositoryException;
import com.kide.knowledge.ProjectKnowledgeService;
import com.kide.knowledge.KnowledgeRevisionConflictException;
import com.kide.synthesis.ProjectSynthesisException;
import com.kide.synthesis.ProjectSynthesisService;
import com.kide.codegen.GenerationException;
import com.kide.codegen.ProjectGenerationService;

/**
 * Shared HTTP runtime for browser and service clients. It binds the PR20 API
 * contract to the same identity, authorization, audit and revision semantics
 * already used by desktop/headless KIDE.
 */
public final class EnterpriseApiServer implements AutoCloseable {
    private static final String API_PREFIX = "/api/v1";
    private static final String CLIENT_ID = "kide-api-v1";
    private static final String MISSING_ETAG = "0".repeat(64);
    private static final int MAX_IMPORT_FILES = 256;
    private static final int MAX_IMPORT_FILE_BYTES = 2 * 1024 * 1024;
    private static final int MAX_IMPORT_TOTAL_BYTES = 10 * 1024 * 1024;

    private final EnterpriseApiConfig config;
    private final Function<String, AuthenticatedSession> authenticator;
    private final EnterpriseContext context;
    private final ServerAuthorizationGate authorization;
    private final ModelRepository models;
    private final ProjectCollaborationService collaboration;
    private final ProjectKnowledgeService knowledge;
    private final ProjectSynthesisService synthesis;
    private final ProjectGenerationService generation;
    private final AuditLedger audit;
    private final Clock clock;
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private final Server server;
    private final ServerConnector connector;
    private HostedApiProjectCatalog hostedProjects;

    public EnterpriseApiServer(
            EnterpriseApiConfig config,
            Function<String, AuthenticatedSession> authenticator,
            EnterpriseContext context,
            ServerAuthorizationGate authorization,
            ModelRepository models,
            ProjectCollaborationService collaboration,
            ProjectKnowledgeService knowledge,
            ProjectSynthesisService synthesis,
            ProjectGenerationService generation,
            AuditLedger audit,
            Clock clock) {
        this.config = Objects.requireNonNull(config, "config");
        this.authenticator = Objects.requireNonNull(authenticator, "authenticator");
        this.context = Objects.requireNonNull(context, "context");
        this.authorization = Objects.requireNonNull(authorization, "authorization");
        this.models = Objects.requireNonNull(models, "models");
        this.collaboration = Objects.requireNonNull(collaboration, "collaboration");
        this.knowledge = Objects.requireNonNull(knowledge, "knowledge");
        this.synthesis = Objects.requireNonNull(synthesis, "synthesis");
        this.generation = Objects.requireNonNull(generation, "generation");
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

    public EnterpriseApiServer(
            EnterpriseApiConfig config,
            Function<String, AuthenticatedSession> authenticator,
            EnterpriseContext context,
            ServerAuthorizationGate authorization,
            ModelRepository models,
            ProjectCollaborationService collaboration,
            ProjectKnowledgeService knowledge,
            ProjectSynthesisService synthesis,
            ProjectGenerationService generation,
            AuditLedger audit,
            Clock clock,
            HostedApiProjectCatalog hostedProjects) {
        this(config, authenticator, context, authorization, models,
                collaboration, knowledge, synthesis, generation, audit, clock);
        this.hostedProjects = Objects.requireNonNull(hostedProjects, "hostedProjects");
    }

    private HostedApiProjectRuntime legacyRuntime() {
        return new HostedApiProjectRuntime(
                context, authorization, models, collaboration,
                knowledge, synthesis, generation);
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
                    health.addProperty("projectCreationEnabled", hostedProjects != null);
                    JsonObject dependencies = new JsonObject();
                    dependencies.addProperty("modelRepository", "AVAILABLE");
                    dependencies.addProperty("knowledgeRepository", "AVAILABLE");
                    health.add("dependencies", dependencies);
                    writeJson(response, callback, 200, health);
                    return true;
                }

                if ("/api/v1/version".equals(path) && "GET".equals(request.getMethod())) {
                    writeJson(
                            response,
                            callback,
                            200,
                            gson.toJsonTree(RuntimeCompatibility.current()).getAsJsonObject());
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
            } catch (HostedApiProjectCatalog.QuotaExceededException e) {
                writeError(response, callback, requestId, 429, ApiErrorCode.RATE_LIMITED,
                        "Hosted project quota has been reached.");
                return true;
            } catch (HostedApiProjectCatalog.ProvisioningUnavailableException e) {
                writeError(response, callback, requestId, 503, ApiErrorCode.SERVICE_UNAVAILABLE,
                        "Hosted project provisioning storage is unavailable.");
                return true;
            } catch (ProjectArchiveImportConflictException e) {
                writeError(response, callback, requestId, 409, ApiErrorCode.CONFLICT,
                        "Archive promotion requires an empty hosted project.");
                return true;
            } catch (RevisionConflictException | KnowledgeRevisionConflictException e) {
                writeError(response, callback, requestId, 409, ApiErrorCode.CONFLICT,
                        "The requested revision is stale.");
                return true;
            } catch (ResourceNotFoundException
                    | ProjectCollaborationService.NotFoundException
                    | ProjectKnowledgeService.NotFoundException
                    | ProjectSynthesisService.NotFoundException
                    | ProjectGenerationService.NotFoundException e) {
                writeError(response, callback, requestId, 404, ApiErrorCode.NOT_FOUND,
                        "The requested API resource was not found.");
                return true;
            } catch (RequestTooLargeException e) {
                writeError(response, callback, requestId, 413, ApiErrorCode.TOO_LARGE,
                        "The request body is too large.");
                return true;
            } catch (ModelRepositoryException | KnowledgeRepositoryException e) {
                writeError(response, callback, requestId, 503, ApiErrorCode.SERVICE_UNAVAILABLE,
                        "A project repository is unavailable.");
                return true;
            } catch (IllegalArgumentException | ProjectSynthesisException | GenerationException e) {
                writeError(response, callback, requestId, 400, ApiErrorCode.BAD_REQUEST,
                        "The request or engineering source is invalid.");
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
            if ("GET".equals(request.getMethod())) {
                JsonObject body = new JsonObject();
                JsonArray items = new JsonArray();
                java.util.List<HostedApiProjectRuntime> available =
                        hostedProjects == null
                                ? java.util.List.of(legacyRuntime()) : hostedProjects.list(session);
                for (HostedApiProjectRuntime project : available) {
                    try {
                        project.authorization().requireApiProjectAccess(session, project.context());
                    } catch (AccessDeniedException denied) {
                        continue; // Never disclose other customers' project identities.
                    }
                    items.add(projectJson(project));
                    audit(project, session, requestId, "project.list", "project",
                            project.context().project().id().value(),
                            AuditOutcome.SUCCESS, "context-v1");
                }
                body.add("items", items);
                writeJson(response, callback, 200, body);
                return true;
            }
            if ("POST".equals(request.getMethod())) {
                if (hostedProjects == null) {
                    unavailable(response, callback, requestId,
                            "Project creation is not enabled in this runtime phase.");
                    return true;
                }
                JsonObject input = readJsonObject(request);
                HostedApiProjectRuntime created = hostedProjects.create(
                        session, requiredString(input, "displayName"));
                created.authorization().requireApiProjectAccess(session, created.context());
                audit(created, session, requestId, "project.create", "project",
                        created.context().project().id().value(),
                        AuditOutcome.SUCCESS, "context-v1");
                writeJson(response, callback, 201, projectJson(created));
                return true;
            }
            methodNotAllowed(response, callback, requestId);
            return true;
        }

        if (segments.length >= 2 && "projects".equals(segments[0])) {
            HostedApiProjectRuntime p = hostedProjects == null
                    ? legacyRuntime()
                    : hostedProjects.resolve(segments[1], session)
                            .orElseThrow(ResourceNotFoundException::new);
            requireProject(p, segments[1]);
            p.authorization().requireApiProjectAccess(session, p.context());

            if (segments.length == 2) {
                if (!"GET".equals(request.getMethod())) {
                    methodNotAllowed(response, callback, requestId);
                    return true;
                }
                audit(p, session, requestId, "project.read", "project", segments[1],
                        AuditOutcome.SUCCESS, "context-v1");
                writeJson(response, callback, 200, projectJson(p));
                return true;
            }

            if (segments.length == 4
                    && "imports".equals(segments[2])
                    && "archive".equals(segments[3])) {
                p.authorization().requireModelWrite(session, p.context());
                if (!"POST".equals(request.getMethod())) {
                    methodNotAllowed(response, callback, requestId);
                    return true;
                }
                JsonObject input = readJsonObject(request);
                JsonObject body = importProjectArchive(p, input);
                audit(p, session, requestId, "project.archive.promote", "project", segments[1],
                        AuditOutcome.SUCCESS,
                        Integer.toString(body.get("importedCount").getAsInt()));
                writeJson(response, callback, 201, body);
                return true;
            }

            if (segments.length == 3 && "models".equals(segments[2])) {
                if ("GET".equals(request.getMethod())) {
                    p.authorization().requireModelRead(session, p.context());
                    JsonObject body = modelListJson(p);
                    audit(p, session, requestId, "model.list", "project", segments[1],
                            AuditOutcome.SUCCESS,
                            Integer.toString(body.getAsJsonArray("items").size()));
                    writeJson(response, callback, 200, body);
                } else if ("POST".equals(request.getMethod())) {
                    p.authorization().requireModelWrite(session, p.context());
                    createStarterModels(p);
                    JsonObject body = modelListJson(p);
                    audit(p, session, requestId, "model.starter.create", "project", segments[1],
                            AuditOutcome.SUCCESS,
                            Integer.toString(body.getAsJsonArray("items").size()));
                    writeJson(response, callback, 201, body);
                } else {
                    methodNotAllowed(response, callback, requestId);
                }
                return true;
            }

            if (segments.length >= 4 && "models".equals(segments[2])) {
                String modelId = decodeModelId(String.join(
                        "/", java.util.Arrays.copyOfRange(segments, 3, segments.length)));
                ModelPath modelPath = new ModelPath(modelId);
                if ("GET".equals(request.getMethod())) {
                    p.authorization().requireModelRead(session, p.context());
                    ModelSnapshot snapshot = p.models().read(modelPath).orElse(null);
                    if (snapshot == null) {
                        writeError(response, callback, requestId, 404, ApiErrorCode.NOT_FOUND,
                                "The requested model was not found.");
                        return true;
                    }
                    audit(p, session, requestId, "model.read", "model", modelId,
                            AuditOutcome.SUCCESS, Long.toString(snapshot.revision().version()));
                    writeJson(response, callback, 200, modelJson(snapshot, modelMediaType(modelId)));
                    return true;
                }
                if ("PUT".equals(request.getMethod())) {
                    p.authorization().requireModelWrite(session, p.context());
                    JsonObject input = readJsonObject(request);
                    String content = requiredString(input, "content");
                    String expected = requiredString(input, "expectedRevision");
                    String mediaType = optionalString(input, "mediaType", "text/plain");
                    try (ModelTransaction tx = p.models().beginTransaction()) {
                        tx.write(modelPath, content.getBytes(StandardCharsets.UTF_8), expected);
                        tx.commit();
                    }
                    ModelSnapshot snapshot = p.models().read(modelPath).orElseThrow();
                    audit(p, session, requestId, "model.write", "model", modelId,
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
                        p, request, response, callback, requestId, session, segments);
            }

            if (segments.length >= 4
                    && "reviews".equals(segments[2])
                    && "changesets".equals(segments[3])) {
                return handleReviews(
                        p, request, response, callback, requestId, session, segments);
            }

            if (segments.length >= 4 && "knowledge".equals(segments[2])) {
                return handleKnowledge(
                        p, request, response, callback, requestId, session, segments);
            }

            if (segments.length == 3 && "synthesis".equals(segments[2])) {
                p.authorization().requireModelSynthesis(session, p.context());
                if (!"POST".equals(request.getMethod())) {
                    methodNotAllowed(response, callback, requestId);
                } else {
                    JsonObject input = readJsonObject(request);
                    String modelId = requiredString(input, "modelId");
                    String modelRevision = requiredString(input, "modelRevision");
                    var result = p.synthesis().synthesize(modelId, modelRevision);
                    audit(p, session, requestId, "p.synthesis().run", "model", modelId,
                            AuditOutcome.SUCCESS, result.revision());
                    writeJson(response, callback, 200,
                            gson.toJsonTree(result).getAsJsonObject());
                }
                return true;
            }

            if (segments.length == 3 && "reconfiguration".equals(segments[2])) {
                p.authorization().requireModelSynthesis(session, p.context());
                if (!"POST".equals(request.getMethod())) {
                    methodNotAllowed(response, callback, requestId);
                } else {
                    JsonObject input = readJsonObject(request);
                    String modelId = requiredString(input, "modelId");
                    String modelRevision = requiredString(input, "modelRevision");
                    String cause = requiredString(input, "cause");
                    if (!input.has("previousBindings")
                            || !input.get("previousBindings").isJsonArray()) {
                        throw new IllegalArgumentException("previousBindings is required");
                    }
                    java.util.List<ProjectSynthesisService.PreviousBinding> bindings =
                            new java.util.ArrayList<>();
                    for (var element : input.getAsJsonArray("previousBindings")) {
                        if (!element.isJsonObject()) {
                            throw new IllegalArgumentException(
                                    "previousBindings must contain objects");
                        }
                        JsonObject binding = element.getAsJsonObject();
                        bindings.add(new ProjectSynthesisService.PreviousBinding(
                                requiredString(binding, "requirementId"),
                                requiredString(binding, "resourceId")));
                    }
                    var result = p.synthesis().reconfigure(
                            modelId, modelRevision, cause, bindings);
                    audit(p, session, requestId, "reconfiguration.run", "model", modelId,
                            AuditOutcome.SUCCESS, result.revision());
                    writeJson(response, callback, 200,
                            gson.toJsonTree(result).getAsJsonObject());
                }
                return true;
            }

            if (segments.length == 3 && "generation".equals(segments[2])) {
                p.authorization().requireModelSynthesis(session, p.context());
                if (!"POST".equals(request.getMethod())) {
                    methodNotAllowed(response, callback, requestId);
                } else {
                    JsonObject input = readJsonObject(request);
                    String sourceModelId = requiredString(input, "sourceModelId");
                    String sourceRevision = requiredString(input, "sourceRevision");
                    String krlModelId = requiredString(input, "krlModelId");
                    String krlRevision = requiredString(input, "krlRevision");
                    String synthesisFingerprint =
                            requiredString(input, "synthesisFingerprint");
                    var result = p.generation().generate(
                            sourceModelId,
                            sourceRevision,
                            krlModelId,
                            krlRevision,
                            synthesisFingerprint);
                    audit(p, session, requestId, "p.generation().run", "model", sourceModelId,
                            AuditOutcome.SUCCESS, result.fingerprint());
                    writeJson(response, callback, 200,
                            gson.toJsonTree(result).getAsJsonObject());
                }
                return true;
            }

            if (segments.length == 3 && "evidence".equals(segments[2])) {
                p.authorization().requireEvidenceRead(session, p.context());
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

    private boolean handleKnowledge(
            HostedApiProjectRuntime p,
            Request request,
            Response response,
            Callback callback,
            UUID requestId,
            AuthenticatedSession session,
            String[] segments) {
        p.authorization().requireKnowledgeRead(session, p.context());

        if (segments.length == 4 && "query".equals(segments[3])) {
            if (!"POST".equals(request.getMethod())) {
                methodNotAllowed(response, callback, requestId);
                return true;
            }
            JsonObject input = readJsonObject(request);
            String scope = optionalString(input, "scope", "PROJECT");
            if (!"PROJECT".equalsIgnoreCase(scope)) {
                throw new IllegalArgumentException(
                        "This project-scoped knowledge endpoint accepts only PROJECT scope");
            }
            String type = optionalString(input, "typeIri", "");
            int limit = optionalInteger(input, "limit", 100);
            var result = p.knowledge().query(
                    optionalString(input, "query", ""), type, limit);
            writeJson(response, callback, 200,
                    gson.toJsonTree(result).getAsJsonObject());
            return true;
        }

        if (segments.length == 4 && "traces".equals(segments[3])) {
            if ("GET".equals(request.getMethod())) {
                writeJson(response, callback, 200,
                        gson.toJsonTree(p.knowledge().traces()).getAsJsonObject());
                return true;
            }
            if ("POST".equals(request.getMethod())) {
                p.authorization().requireKnowledgeTraceWrite(session, p.context());
                JsonObject input = readJsonObject(request);
                var result = p.knowledge().createTrace(
                        requiredString(input, "knowledgeIri"),
                        requiredString(input, "modelId"),
                        optionalString(input, "semanticId", ""),
                        requiredString(input, "relation"),
                        session.principal().id(),
                        requiredString(input, "expectedTraceEtag"));
                audit(p, session, requestId, "p.knowledge().trace.create", "knowledge-traces",
                        p.context().project().id().value(), AuditOutcome.SUCCESS,
                        Long.toString(result.revision()));
                writeJson(response, callback, 200,
                        gson.toJsonTree(result).getAsJsonObject());
                return true;
            }
            methodNotAllowed(response, callback, requestId);
            return true;
        }

        if (segments.length == 5 && "traces".equals(segments[3])) {
            p.authorization().requireKnowledgeTraceWrite(session, p.context());
            String traceId = segments[4];
            if ("PUT".equals(request.getMethod())) {
                JsonObject input = readJsonObject(request);
                var result = p.knowledge().rebindTrace(
                        traceId,
                        requiredString(input, "modelId"),
                        optionalString(input, "semanticId", ""),
                        session.principal().id(),
                        requiredString(input, "expectedTraceEtag"));
                audit(p, session, requestId, "p.knowledge().trace.rebind", "knowledge-trace",
                        traceId, AuditOutcome.SUCCESS, Long.toString(result.revision()));
                writeJson(response, callback, 200,
                        gson.toJsonTree(result).getAsJsonObject());
                return true;
            }
            if ("DELETE".equals(request.getMethod())) {
                JsonObject input = readJsonObject(request);
                var result = p.knowledge().deleteTrace(
                        traceId, requiredString(input, "expectedTraceEtag"));
                audit(p, session, requestId, "p.knowledge().trace.delete", "knowledge-trace",
                        traceId, AuditOutcome.SUCCESS, Long.toString(result.revision()));
                writeJson(response, callback, 200,
                        gson.toJsonTree(result).getAsJsonObject());
                return true;
            }
            methodNotAllowed(response, callback, requestId);
            return true;
        }

        if (segments.length == 4 && "impact".equals(segments[3])) {
            if (!"POST".equals(request.getMethod())) {
                methodNotAllowed(response, callback, requestId);
                return true;
            }
            JsonObject input = readJsonObject(request);
            var result = p.knowledge().impact(
                    optionalString(input, "knowledgeIri", ""),
                    optionalString(input, "modelId", ""));
            writeJson(response, callback, 200,
                    gson.toJsonTree(result).getAsJsonObject());
            return true;
        }

        writeError(response, callback, requestId, 404, ApiErrorCode.NOT_FOUND,
                "The requested API resource was not found.");
        return true;
    }

    private boolean handlePresence(
            HostedApiProjectRuntime p,
            Request request,
            Response response,
            Callback callback,
            UUID requestId,
            AuthenticatedSession session,
            String[] segments) {
        p.authorization().requireCollaborationRead(session, p.context());

        if (segments.length == 4) {
            if ("GET".equals(request.getMethod())) {
                JsonObject body = new JsonObject();
                com.google.gson.JsonArray items = new com.google.gson.JsonArray();
                for (var presence : p.collaboration().listPresence()) {
                    items.add(gson.toJsonTree(presence));
                }
                body.add("items", items);
                writeJson(response, callback, 200, body);
                return true;
            }
            if ("POST".equals(request.getMethod())) {
                JsonObject input = readJsonObject(request);
                var joined = p.collaboration().join(
                        session.principal(),
                        optionalString(input, "sessionId", ""),
                        optionalString(input, "modelId", ""));
                audit(p, session, requestId, "p.collaboration().presence.join", "presence",
                        joined.id(), AuditOutcome.SUCCESS, "presence-v1");
                writeJson(response, callback, 200,
                        gson.toJsonTree(joined).getAsJsonObject());
                return true;
            }
            methodNotAllowed(response, callback, requestId);
            return true;
        }

        if (segments.length == 5) {
            String sessionId = segments[4];
            if ("PUT".equals(request.getMethod())) {
                JsonObject input = readJsonObject(request);
                var refreshed = p.collaboration().heartbeat(
                        session.principal(), sessionId,
                        optionalString(input, "modelId", ""));
                writeJson(response, callback, 200,
                        gson.toJsonTree(refreshed).getAsJsonObject());
                return true;
            }
            if ("DELETE".equals(request.getMethod())) {
                var departed = p.collaboration().leave(session.principal(), sessionId);
                audit(p, session, requestId, "p.collaboration().presence.leave", "presence",
                        departed.id(), AuditOutcome.SUCCESS, "presence-v1");
                writeJson(response, callback, 200,
                        gson.toJsonTree(departed).getAsJsonObject());
                return true;
            }
            methodNotAllowed(response, callback, requestId);
            return true;
        }

        throw new ResourceNotFoundException();
    }

    private boolean handleReviews(
            HostedApiProjectRuntime p,
            Request request,
            Response response,
            Callback callback,
            UUID requestId,
            AuthenticatedSession session,
            String[] segments) {
        p.authorization().requireCollaborationRead(session, p.context());

        if (segments.length == 4) {
            if ("GET".equals(request.getMethod())) {
                JsonObject body = new JsonObject();
                com.google.gson.JsonArray items = new com.google.gson.JsonArray();
                for (var set : p.collaboration().listChangeSets()) {
                    items.add(changeSetJson(set, false));
                }
                body.add("items", items);
                writeJson(response, callback, 200, body);
                return true;
            }
            if ("POST".equals(request.getMethod())) {
                p.authorization().requireCollaborationWrite(session, p.context());
                p.authorization().requireModelRead(session, p.context());
                JsonObject input = readJsonObject(request);
                var set = p.collaboration().createChangeSet(
                        session.principal(),
                        requiredString(input, "modelId"),
                        requiredString(input, "baseEtag"),
                        requiredString(input, "proposedContent"),
                        optionalString(input, "mediaType", "text/plain"));
                audit(p, session, requestId, "review.changeset.create", "reviewChangeSet",
                        set.id(), AuditOutcome.SUCCESS, Long.toString(set.reviewRevision()));
                writeJson(response, callback, 200, changeSetJson(set, true));
                return true;
            }
            methodNotAllowed(response, callback, requestId);
            return true;
        }

        String changeSetId = segments[4];
        if (segments.length == 5) {
            if ("GET".equals(request.getMethod())) {
                p.authorization().requireModelRead(session, p.context());
                var set = p.collaboration().findChangeSet(changeSetId)
                        .orElseThrow(ResourceNotFoundException::new);
                ModelSnapshot current = p.collaboration().currentModel(changeSetId);
                writeJson(response, callback, 200, reviewBundleJson(p, set, current));
                return true;
            }
            if ("PUT".equals(request.getMethod())) {
                p.authorization().requireCollaborationWrite(session, p.context());
                p.authorization().requireModelRead(session, p.context());
                JsonObject input = readJsonObject(request);
                var set = p.collaboration().rebase(
                        session.principal(),
                        changeSetId,
                        requiredString(input, "expectedCurrentEtag"),
                        requiredString(input, "proposedContent"));
                audit(p, session, requestId, "review.changeset.rebase", "reviewChangeSet",
                        set.id(), AuditOutcome.SUCCESS, Long.toString(set.reviewRevision()));
                writeJson(response, callback, 200, changeSetJson(set, true));
                return true;
            }
            methodNotAllowed(response, callback, requestId);
            return true;
        }

        if (segments.length == 6) {
            String operation = segments[5];
            if ("ready".equals(operation) && "POST".equals(request.getMethod())) {
                p.authorization().requireCollaborationWrite(session, p.context());
                var set = p.collaboration().markReady(session.principal(), changeSetId);
                audit(p, session, requestId, "review.changeset.ready", "reviewChangeSet",
                        set.id(), AuditOutcome.SUCCESS, Long.toString(set.reviewRevision()));
                writeJson(response, callback, 200, changeSetJson(set, true));
                return true;
            }
            if ("approve".equals(operation) && "POST".equals(request.getMethod())) {
                p.authorization().requireReviewApprove(session, p.context());
                var set = p.collaboration().approve(session.principal(), changeSetId);
                audit(p, session, requestId, "review.changeset.approve", "reviewChangeSet",
                        set.id(), AuditOutcome.SUCCESS, Long.toString(set.reviewRevision()));
                writeJson(response, callback, 200, changeSetJson(set, true));
                return true;
            }
            if ("apply".equals(operation) && "POST".equals(request.getMethod())) {
                p.authorization().requireCollaborationWrite(session, p.context());
                p.authorization().requireModelWrite(session, p.context());
                var applied = p.collaboration().apply(session.principal(), changeSetId);
                audit(p, session, requestId, "review.changeset.apply", "model",
                        applied.model().path().value(), AuditOutcome.SUCCESS,
                        Long.toString(applied.model().revision().version()));
                JsonObject body = new JsonObject();
                body.add("changeSet", changeSetJson(applied.changeSet(), true));
                body.add("model", modelJson(
                        applied.model(), applied.changeSet().mediaType()));
                writeJson(response, callback, 200, body);
                return true;
            }
            if ("comments".equals(operation)) {
                if ("GET".equals(request.getMethod())) {
                    JsonObject body = new JsonObject();
                    com.google.gson.JsonArray items = new com.google.gson.JsonArray();
                    for (var comment : p.collaboration().comments(changeSetId)) {
                        items.add(gson.toJsonTree(comment));
                    }
                    body.add("items", items);
                    writeJson(response, callback, 200, body);
                    return true;
                }
                if ("POST".equals(request.getMethod())) {
                    p.authorization().requireReviewComment(session, p.context());
                    JsonObject input = readJsonObject(request);
                    var comment = p.collaboration().addComment(
                            session.principal(),
                            changeSetId,
                            requiredString(input, "body"),
                            optionalString(input, "anchor", ""));
                    audit(p, session, requestId, "review.comment.create", "reviewComment",
                            comment.id(), AuditOutcome.SUCCESS, "review-v1");
                    writeJson(response, callback, 200,
                            gson.toJsonTree(comment).getAsJsonObject());
                    return true;
                }
            }
            methodNotAllowed(response, callback, requestId);
            return true;
        }

        if (segments.length == 7
                && "comments".equals(segments[5])
                && "PUT".equals(request.getMethod())) {
            p.authorization().requireReviewComment(session, p.context());
            JsonObject input = readJsonObject(request);
            var comment = p.collaboration().resolveComment(
                    session.principal(), changeSetId, segments[6],
                    requiredBoolean(input, "resolved"));
            audit(p, session, requestId, "review.comment.update", "reviewComment",
                    comment.id(), AuditOutcome.SUCCESS, "review-v1");
            writeJson(response, callback, 200,
                    gson.toJsonTree(comment).getAsJsonObject());
            return true;
        }

        throw new ResourceNotFoundException();
    }

    private JsonObject reviewBundleJson(
            HostedApiProjectRuntime p,
            ProjectCollaborationService.ChangeSet set,
            ModelSnapshot current) {
        JsonObject body = new JsonObject();
        body.add("changeSet", changeSetJson(set, true));
        body.add("currentModel", modelJson(current, set.mediaType()));
        com.google.gson.JsonArray comments = new com.google.gson.JsonArray();
        for (var comment : p.collaboration().comments(set.id())) {
            comments.add(gson.toJsonTree(comment));
        }
        body.add("comments", comments);
        boolean conflicted = set.status() != ProjectCollaborationService.ChangeSetStatus.APPLIED
                && !set.baseEtag().equals(current.revision().etag());
        body.addProperty("conflicted", conflicted);
        return body;
    }

    private JsonObject changeSetJson(
            ProjectCollaborationService.ChangeSet set, boolean includeContent) {
        JsonObject value = gson.toJsonTree(set).getAsJsonObject();
        if (!includeContent) value.remove("proposedContent");
        return value;
    }

    private JsonObject projectJson(HostedApiProjectRuntime p) {
        JsonObject project = new JsonObject();
        project.addProperty("id", p.context().project().id().value());
        project.addProperty("displayName", p.context().project().displayName());
        project.addProperty("revision", "1");
        project.addProperty("portfolioId", p.context().portfolio().id().value());
        project.addProperty("workspaceId", p.context().workspace().id().value());
        return project;
    }

    private JsonObject modelListJson(HostedApiProjectRuntime p) {
        JsonObject body = new JsonObject();
        com.google.gson.JsonArray items = new com.google.gson.JsonArray();
        for (ModelPath path : p.models().list()) {
            ModelSnapshot snapshot = p.models().read(path).orElse(null);
            if (snapshot == null) continue;
            JsonObject item = new JsonObject();
            item.addProperty("id", path.value());
            item.addProperty("revision", Long.toString(snapshot.revision().version()));
            item.addProperty("etag", snapshot.revision().etag());
            item.addProperty("mediaType", modelMediaType(path.value()));
            items.add(item);
        }
        body.add("items", items);
        return body;
    }

    private JsonObject importProjectArchive(HostedApiProjectRuntime p, JsonObject input) {
        JsonElement rawItems = input.get("items");
        if (rawItems == null || !rawItems.isJsonArray()) {
            throw new IllegalArgumentException("items array is required");
        }
        JsonArray items = rawItems.getAsJsonArray();
        if (items.size() == 0 || items.size() > MAX_IMPORT_FILES) {
            throw new IllegalArgumentException(
                    "archive promotion requires between 1 and " + MAX_IMPORT_FILES + " files");
        }

        record ImportEntry(ModelPath path, byte[] content) { }
        java.util.ArrayList<ImportEntry> decoded = new java.util.ArrayList<>(items.size());
        java.util.Set<String> seen = new HashSet<>();
        long totalBytes = 0L;

        for (JsonElement element : items) {
            if (!element.isJsonObject()) {
                throw new IllegalArgumentException("archive import item must be an object");
            }
            JsonObject item = element.getAsJsonObject();
            String rawPath = requiredString(item, "path");
            if (rawPath.length() > 512 || !seen.add(rawPath)) {
                throw new IllegalArgumentException("archive import contains an invalid or duplicate path");
            }
            ModelPath modelPath = new ModelPath(rawPath);
            byte[] content;
            try {
                content = Base64.getDecoder().decode(requiredString(item, "contentBase64"));
            } catch (IllegalArgumentException invalidBase64) {
                throw new IllegalArgumentException("archive import contains invalid base64 content");
            }
            if (content.length > MAX_IMPORT_FILE_BYTES) {
                throw new RequestTooLargeException();
            }
            totalBytes += content.length;
            if (totalBytes > MAX_IMPORT_TOTAL_BYTES) {
                throw new RequestTooLargeException();
            }
            decoded.add(new ImportEntry(modelPath, content));
        }

        if (!p.models().list().isEmpty()) {
            throw new ProjectArchiveImportConflictException();
        }

        try (ModelTransaction tx = p.models().beginTransaction()) {
            tx.requireEmpty();
            for (ImportEntry entry : decoded) {
                tx.write(entry.path(), entry.content(), MISSING_ETAG);
            }
            try {
                tx.commit();
            } catch (RevisionConflictException concurrentChange) {
                throw new ProjectArchiveImportConflictException();
            }
        }

        JsonObject result = new JsonObject();
        result.addProperty("projectId", p.context().project().id().value());
        result.addProperty("workspaceId", p.context().workspace().id().value());
        result.addProperty("importedCount", decoded.size());
        result.addProperty("totalBytes", totalBytes);
        result.add("items", modelListJson(p).getAsJsonArray("items"));
        return result;
    }

    private void createStarterModels(HostedApiProjectRuntime p) {
        java.util.LinkedHashMap<ModelPath, String> starter = new java.util.LinkedHashMap<>();
        starter.put(new ModelPath("starter.dml"),
                "Package Starter\n"
                + "DataModel Payload {\n"
                + "  primitives { int value }\n"
                + "}\n");
        starter.put(new ModelPath("inspect.op"),
                "Operation Inspect() {\n"
                + "}\n");
        starter.put(new ModelPath("device.mncspec"),
                "Model Starter\n"
                + "InterfaceDescription Device {\n"
                + "  commands { Start[] }\n"
                + "  events { Publish Ready[] }\n"
                + "}\n");
        starter.put(new ModelPath("observe.cap"),
                "Capability Observe compatible component interface Device {\n"
                + "  providesControlCapabilities {\n"
                + "    fireable commands : Start\n"
                + "    receivable events : Ready\n"
                + "  }\n"
                + "}\n");
        starter.put(new ModelPath("workflow.activity"),
                "ActivityDiagram StarterWorkflow\n"
                + "has activities {\n"
                + "  Activity ObserveStep {\n"
                + "    requireCapability : Observe { Start, Ready }\n"
                + "    nextActivity : ObserveStep\n"
                + "  }\n"
                + "}\n");
        starter.put(new ModelPath("bindings.krl"),
                "knowledge StarterKnowledge {\n"
                + "  namespace kide = \"https://kide.dev/ontology/v1#\";\n"
                + "  fact kide:Camera kide:providesCapability iri "
                + "\"urn:kide:capability:Observe\";\n"
                + "  query FindObserve(capability: iri) {\n"
                + "    match ?device kide:providesCapability ?capability;\n"
                + "    select ?device;\n"
                + "  }\n"
                + "  template Binding(name: string, resource: iri) for java\n"
                + "    body \"public final class ${name} { public static final String RESOURCE = \\\"${resource}\\\"; }\";\n"
                + "  target Observe type java {\n"
                + "    template Binding;\n"
                + "    output \"generated/ObserveBinding.java\";\n"
                + "    bind name: string = string \"ObserveBinding\";\n"
                + "    bind resource: iri = query FindObserve(iri "
                + "\"urn:kide:capability:Observe\").device;\n"
                + "  }\n"
                + "}\n");

        try (ModelTransaction tx = p.models().beginTransaction()) {
            for (Map.Entry<ModelPath, String> item : starter.entrySet()) {
                tx.write(
                        item.getKey(),
                        item.getValue().getBytes(StandardCharsets.UTF_8),
                        MISSING_ETAG);
            }
            tx.commit();
        }
    }

    private static String modelMediaType(String modelId) {
        String lower = modelId.toLowerCase(java.util.Locale.ROOT);
        if (lower.endsWith(".dml")) return "text/x-kide-dml";
        if (lower.endsWith(".op")) return "text/x-kide-operation";
        if (lower.endsWith(".mncspec")) return "text/x-kide-mnc";
        if (lower.endsWith(".cap")) return "text/x-kide-capability";
        if (lower.endsWith(".activity")) return "text/x-kide-activity";
        if (lower.endsWith(".krl")) return "text/x-kide-krl";
        if (lower.endsWith(".json")) return "application/json";
        return "text/plain";
    }

    private JsonObject modelJson(ModelSnapshot snapshot, String mediaType) {
        JsonObject model = new JsonObject();
        model.addProperty("id", snapshot.path().value());
        model.addProperty("content", new String(snapshot.content(), StandardCharsets.UTF_8));
        model.addProperty("contentBase64", Base64.getEncoder().encodeToString(snapshot.content()));
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
            HostedApiProjectRuntime p,
            AuthenticatedSession session,
            UUID requestId,
            String action,
            String resourceType,
            String resourceId,
            AuditOutcome outcome,
            String revision) {
        audit.append(AuditEventDraft.of(
                session.principal(), CLIENT_ID, p.context(), revision, action,
                resourceType, resourceId, outcome, requestId, requestId, Map.of()));
    }

    private void requireProject(HostedApiProjectRuntime p, String projectId) {
        if (!p.context().project().id().value().equals(projectId)) {
            throw new ResourceNotFoundException();
        }
    }

    private static String decodeModelId(String encoded) {
        String value = URLDecoder.decode(encoded, StandardCharsets.UTF_8);
        if (value.isBlank() || value.length() > 512) {
            throw new IllegalArgumentException("modelId is invalid");
        }
        return new ModelPath(value).value();
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

    private static boolean requiredBoolean(JsonObject object, String key) {
        if (!object.has(key) || !object.get(key).isJsonPrimitive()
                || !object.get(key).getAsJsonPrimitive().isBoolean()) {
            throw new IllegalArgumentException(key + " is required");
        }
        return object.get(key).getAsBoolean();
    }

    private static String optionalString(JsonObject object, String key, String fallback) {
        if (!object.has(key) || !object.get(key).isJsonPrimitive()) return fallback;
        String value = object.get(key).getAsString();
        return value == null || value.isBlank() ? fallback : value;
    }

    private static int optionalInteger(JsonObject object, String key, int fallback) {
        if (!object.has(key)) return fallback;
        if (!object.get(key).isJsonPrimitive()
                || !object.get(key).getAsJsonPrimitive().isNumber()) {
            throw new IllegalArgumentException(key + " must be an integer");
        }
        try {
            return object.get(key).getAsInt();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(key + " must be an integer");
        }
    }

    private static final class ResourceNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private static final class ProjectArchiveImportConflictException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private static final class RequestTooLargeException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
