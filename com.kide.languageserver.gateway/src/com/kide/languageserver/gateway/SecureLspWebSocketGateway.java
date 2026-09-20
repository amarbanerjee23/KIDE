package com.kide.languageserver.gateway;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Objects;

import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeHandler;

import com.kide.enterprise.authorization.AccessDeniedException;
import com.kide.enterprise.authorization.ServerAuthorizationGate;
import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.identity.AuthenticatedSession;
import com.kide.enterprise.identity.AuthenticationException;

public final class SecureLspWebSocketGateway implements AutoCloseable {
    private final GatewayConfig config;
    private final GatewayAuthenticator authenticator;
    private final GatewayWorkspaceCatalog workspaces;
    private final ServerAuthorizationGate authorization;
    private final Clock clock;
    private final Server server;
    private final ServerConnector connector;

    public SecureLspWebSocketGateway(
            GatewayConfig config,
            GatewayAuthenticator authenticator,
            GatewayWorkspaceCatalog workspaces,
            ServerAuthorizationGate authorization,
            Clock clock) {
        this.config = Objects.requireNonNull(config, "config");
        this.authenticator = Objects.requireNonNull(authenticator, "authenticator");
        this.workspaces = Objects.requireNonNull(workspaces, "workspaces");
        this.authorization = Objects.requireNonNull(authorization, "authorization");
        this.clock = Objects.requireNonNull(clock, "clock");

        this.server = new Server();
        this.connector = new ServerConnector(server);
        connector.setHost(config.bindHost());
        connector.setPort(config.port());
        server.addConnector(connector);

        ContextHandler context = new ContextHandler("/");
        WebSocketUpgradeHandler webSocketHandler =
                WebSocketUpgradeHandler.from(server, context, container -> {
                    container.setMaxTextMessageSize(config.maxTextMessageBytes());
                    container.setIdleTimeout(config.idleTimeout());
                    container.addMapping("/lsp", (request, response, callback) -> {
                        if (!originAllowed(request.getHeaders().get("Origin"))) {
                            response.setStatus(403);
                            callback.succeeded();
                            return null;
                        }
                        if (!secureEnough(
                                "https".equalsIgnoreCase(request.getHttpURI().getScheme()),
                                request.getHeaders().get("X-Forwarded-Proto"))) {
                            response.setStatus(400);
                            callback.succeeded();
                            return null;
                        }

                        AuthenticatedSession session = null;
                        try {
                            String authorizationHeader =
                                    BrowserWebSocketCredential.authorizationHeader(
                                            request.getHeaders().get("Authorization"),
                                            request.getSubProtocols());
                            session = authenticator.authenticateAuthorizationHeader(
                                    authorizationHeader);
                            String workspaceId = queryParameter(
                                    request.getHttpURI().getQuery(), "workspaceId");
                            EnterpriseContext enterpriseContext = workspaces.resolve(workspaceId)
                                    .orElseThrow(() -> new IllegalArgumentException("workspace not found"));
                            if (!enterpriseContext.workspace().id().value().equals(workspaceId)) {
                                throw new IllegalArgumentException("workspace identity mismatch");
                            }
                            authorization.requireWebSocketWorkspaceAccess(session, enterpriseContext);
                            authorization.requireLspWorkspaceAccess(session, enterpriseContext);
                            if (request.hasSubProtocol(BrowserWebSocketCredential.LSP_PROTOCOL)) {
                                response.setAcceptedSubProtocol(
                                        BrowserWebSocketCredential.LSP_PROTOCOL);
                            }
                            return new GatewayWebSocketEndpoint(
                                    config, session, authorization, enterpriseContext, clock);
                        } catch (AuthenticationException failure) {
                            if (session != null) session.close();
                            response.setStatus(401);
                            callback.succeeded();
                            return null;
                        } catch (AccessDeniedException | IllegalArgumentException failure) {
                            if (session != null) session.close();
                            response.setStatus(403);
                            callback.succeeded();
                            return null;
                        } catch (RuntimeException failure) {
                            if (session != null) session.close();
                            response.setStatus(500);
                            callback.succeeded();
                            return null;
                        }
                    });
                });
        context.setHandler(webSocketHandler);
        server.setHandler(context);
    }

    public void start() throws Exception {
        server.start();
    }

    public int localPort() {
        int port = connector.getLocalPort();
        return port >= 0 ? port : config.port();
    }

    public void join() throws InterruptedException {
        server.join();
    }

    public boolean isStarted() {
        return server.isStarted();
    }

    @Override
    public void close() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new IllegalStateException("Could not stop LSP WebSocket gateway", e);
        }
    }

    private boolean originAllowed(String origin) {
        if (config.allowedOrigins().isEmpty()) return true;
        return origin != null && config.allowedOrigins().contains(origin);
    }

    private boolean secureEnough(boolean directSecure, String forwardedProto) {
        if (!config.requireSecureTransport()) return true;
        if (directSecure) return true;
        return config.trustForwardedProto()
                && forwardedProto != null
                && "https".equalsIgnoreCase(forwardedProto.trim());
    }

    private static String queryParameter(String rawQuery, String expectedName) {
        if (rawQuery == null || rawQuery.isBlank()) {
            throw new IllegalArgumentException(expectedName + " is required");
        }
        String found = null;
        for (String pair : rawQuery.split("&")) {
            int equals = pair.indexOf('=');
            String rawName = equals < 0 ? pair : pair.substring(0, equals);
            String rawValue = equals < 0 ? "" : pair.substring(equals + 1);
            String name = URLDecoder.decode(rawName, StandardCharsets.UTF_8);
            if (!expectedName.equals(name)) continue;
            if (found != null) {
                throw new IllegalArgumentException(
                        "exactly one " + expectedName + " is required");
            }
            found = URLDecoder.decode(rawValue, StandardCharsets.UTF_8);
        }
        if (found == null || found.isBlank() || found.length() > 256) {
            throw new IllegalArgumentException(expectedName + " is required");
        }
        return found.trim();
    }
}
