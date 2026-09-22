package com.kide.glsp;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Objects;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeHandler;

import com.kide.enterprise.authorization.AccessDeniedException;
import com.kide.enterprise.authorization.ServerAuthorizationGate;
import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.identity.AuthenticatedSession;
import com.kide.enterprise.identity.AuthenticationException;
import com.kide.languageserver.gateway.BrowserWebSocketCredential;
import com.kide.languageserver.gateway.GatewayAuthenticator;
import com.kide.languageserver.gateway.GatewayConfig;
import com.kide.languageserver.gateway.GatewaySessionQuota;
import com.kide.languageserver.gateway.GatewayWorkspaceBinding;
import com.kide.languageserver.gateway.GatewayWorkspaceCatalog;

public final class SecureGlspWebSocketGateway implements AutoCloseable {
    private final GatewayConfig config;
    private final GatewayAuthenticator authenticator;
    private final GatewayWorkspaceCatalog workspaces;
    private final ServerAuthorizationGate authorization;
    private final Clock clock;
    private final GatewaySessionQuota sessionQuota;
    private final Server server;
    private final ServerConnector connector;

    public SecureGlspWebSocketGateway(
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
        this.sessionQuota = new GatewaySessionQuota(config.maxConcurrentSessions());

        this.server = new Server();
        this.connector = new ServerConnector(server);
        connector.setHost(config.bindHost());
        connector.setPort(config.port());
        server.addConnector(connector);

        ContextHandler context = new ContextHandler("/");
        WebSocketUpgradeHandler handler =
                WebSocketUpgradeHandler.from(server, context, container -> {
                    container.setMaxTextMessageSize(config.maxTextMessageBytes());
                    container.setIdleTimeout(config.idleTimeout());
                    container.addMapping("/glsp", (request, response, callback) -> {
                        if (!originAllowed(request.getHeaders().get("Origin"))) {
                            response.setStatus(403);
                            callback.succeeded();
                            return null;
                        }
                        if (!secureEnough(
                                request.getConnectionMetaData().isSecure(),
                                request.getHeaders().get("X-Forwarded-Proto"),
                                request.getConnectionMetaData().getRemoteSocketAddress())) {
                            response.setStatus(400);
                            callback.succeeded();
                            return null;
                        }

                        AuthenticatedSession session = null;
                        try {
                            String authorizationHeader =
                                    BrowserWebSocketCredential.authorizationHeader(
                                            request.getHeaders().get("Authorization"),
                                            request.getSubProtocols(),
                                            BrowserWebSocketCredential.GLSP_PROTOCOL);
                            session = authenticator.authenticateAuthorizationHeader(
                                    authorizationHeader);
                            String workspaceId = queryParameter(
                                    request.getHttpURI().getQuery(), "workspaceId");
                            GatewayWorkspaceBinding binding = workspaces.resolve(workspaceId)
                                    .orElseThrow(() -> new IllegalArgumentException(
                                            "workspace not found"));
                            EnterpriseContext enterpriseContext = binding.context();
                            if (!enterpriseContext.workspace().id().value()
                                    .equals(workspaceId)) {
                                throw new IllegalArgumentException(
                                        "workspace identity mismatch");
                            }
                            authorization.requireWebSocketWorkspaceAccess(
                                    session, enterpriseContext);
                            authorization.requireModelRead(session, enterpriseContext);
                            authorization.requireWorkspaceWrite(
                                    session, enterpriseContext);

                            GatewaySessionQuota.Lease lease = sessionQuota.tryAcquire();
                            if (lease == null) {
                                session.close();
                                response.setStatus(429);
                                callback.succeeded();
                                return null;
                            }
                            try {
                                if (request.hasSubProtocol(
                                        BrowserWebSocketCredential.GLSP_PROTOCOL)) {
                                    response.setAcceptedSubProtocol(
                                            BrowserWebSocketCredential.GLSP_PROTOCOL);
                                }
                                return new GlspWebSocketEndpoint(
                                        config, session, authorization,
                                        binding, lease, clock);
                            } catch (RuntimeException failure) {
                                lease.close();
                                throw failure;
                            }
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
        context.setHandler(handler);
        server.setHandler(context);
    }

    public void start() throws Exception { server.start(); }

    public int localPort() {
        int port = connector.getLocalPort();
        return port >= 0 ? port : config.port();
    }

    public void join() throws InterruptedException { server.join(); }

    public boolean isStarted() { return server.isStarted(); }

    @Override
    public void close() {
        try {
            server.stop();
        } catch (Exception failure) {
            throw new IllegalStateException(
                    "Could not stop secure GLSP WebSocket gateway", failure);
        }
    }

    private boolean originAllowed(String origin) {
        if (config.allowedOrigins().isEmpty()) return true;
        return origin != null && config.allowedOrigins().contains(origin);
    }

    private boolean secureEnough(
            boolean directSecure, String forwardedProto, SocketAddress remoteAddress) {
        if (!config.requireSecureTransport()) return true;
        if (directSecure) return true;
        if (!config.trustForwardedProto()
                || forwardedProto == null
                || !"https".equalsIgnoreCase(forwardedProto.trim())) {
            return false;
        }
        return trustedProxy(remoteAddress);
    }

    private boolean trustedProxy(SocketAddress remoteAddress) {
        if (!(remoteAddress instanceof InetSocketAddress inet)) return false;
        if (inet.getAddress() != null
                && config.trustedProxyAddresses().contains(
                        inet.getAddress().getHostAddress())) {
            return true;
        }
        return config.trustedProxyAddresses().contains(inet.getHostString());
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
