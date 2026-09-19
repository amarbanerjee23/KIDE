package com.kide.languageserver.gateway;

import java.time.Clock;
import java.util.List;
import java.util.Objects;

import org.eclipse.jetty.server.ContextHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeHandler;

import com.kide.enterprise.authorization.ServerAuthorizationGate;
import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.identity.AuthenticatedSession;

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
                        if (!originAllowed(request.getOrigin())) {
                            response.setStatus(403);
                            callback.succeeded();
                            return null;
                        }
                        if (!secureEnough(request.isSecure(), request.getHeader("X-Forwarded-Proto"))) {
                            response.setStatus(400);
                            callback.succeeded();
                            return null;
                        }

                        AuthenticatedSession session = null;
                        try {
                            session = authenticator.authenticateAuthorizationHeader(
                                    request.getHeader("Authorization"));
                            String workspaceId = first(request.getParameterMap().get("workspaceId"));
                            EnterpriseContext enterpriseContext = workspaces.resolve(workspaceId)
                                    .orElseThrow(() -> new IllegalArgumentException("workspace not found"));
                            if (!enterpriseContext.workspace().id().value().equals(workspaceId)) {
                                throw new IllegalArgumentException("workspace identity mismatch");
                            }
                            authorization.requireWebSocketWorkspaceAccess(session, enterpriseContext);
                            authorization.requireLspWorkspaceAccess(session, enterpriseContext);
                            return new GatewayWebSocketEndpoint(config, session, clock);
                        } catch (RuntimeException failure) {
                            if (session != null) session.close();
                            response.setStatus(401);
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

    private static String first(List<String> values) {
        if (values == null || values.size() != 1) {
            throw new IllegalArgumentException("exactly one workspaceId is required");
        }
        String value = values.get(0);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("workspaceId is required");
        }
        return value.trim();
    }
}
