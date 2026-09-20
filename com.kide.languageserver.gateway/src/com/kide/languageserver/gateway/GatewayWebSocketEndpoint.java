package com.kide.languageserver.gateway;

import java.io.IOException;
import java.time.Clock;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;

import com.kide.enterprise.authorization.ServerAuthorizationGate;
import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.identity.AuthenticatedSession;

final class GatewayWebSocketEndpoint implements Session.Listener {
    private final GatewayConfig config;
    private final AuthenticatedSession authenticatedSession;
    private final ServerAuthorizationGate authorization;
    private final EnterpriseContext enterpriseContext;
    private final LspWorkspaceBoundary workspaceBoundary;
    private final GatewayMessagePolicy messagePolicy;
    private final AtomicBoolean closed = new AtomicBoolean();

    private volatile Session webSocket;
    private volatile InProcessLspSession lsp;

    GatewayWebSocketEndpoint(
            GatewayConfig config,
            AuthenticatedSession authenticatedSession,
            ServerAuthorizationGate authorization,
            GatewayWorkspaceBinding workspaceBinding,
            Clock clock) {
        this.config = Objects.requireNonNull(config, "config");
        this.authenticatedSession = Objects.requireNonNull(authenticatedSession, "authenticatedSession");
        this.authorization = Objects.requireNonNull(authorization, "authorization");
        GatewayWorkspaceBinding binding =
                Objects.requireNonNull(workspaceBinding, "workspaceBinding");
        this.enterpriseContext = binding.context();
        this.workspaceBoundary = new LspWorkspaceBoundary(binding);
        this.messagePolicy = new GatewayMessagePolicy(
                config.maxTextMessageBytes(), config.maxMessagesPerMinute(), clock);
    }

    @Override
    public void onWebSocketOpen(Session session) {
        this.webSocket = session;
        session.setIdleTimeout(config.idleTimeout());
        session.setMaxTextMessageSize(config.maxTextMessageBytes());
        try {
            InProcessLspSession created = new InProcessLspSession(
                    config.maxTextMessageBytes(),
                    new InProcessLspSession.Outbound() {
                        @Override
                        public void send(String json) {
                            Session socket = webSocket;
                            if (socket == null || !socket.isOpen() || closed.get()) return;
                            Callback.Completable callback = new Callback.Completable();
                            socket.sendText(json, callback);
                            callback.join();
                        }

                        @Override
                        public void failed(Throwable failure) {
                            closeSocket(StatusCode.SERVER_ERROR, "language server session failed");
                        }
                    });
            this.lsp = created;
            created.start();
            session.demand();
        } catch (IOException e) {
            closeSocket(StatusCode.SERVER_ERROR, "language server session could not start");
        }
    }

    @Override
    public void onWebSocketText(String message) {
        if (closed.get()) return;
        Session socket = webSocket;
        if (socket == null) return;

        GatewayMessagePolicy.Decision decision = messagePolicy.evaluate(message);
        if (decision == GatewayMessagePolicy.Decision.MESSAGE_TOO_LARGE) {
            closeSocket(StatusCode.MESSAGE_TOO_LARGE, "message exceeds configured limit");
            return;
        }
        if (decision == GatewayMessagePolicy.Decision.RATE_LIMITED) {
            closeSocket(StatusCode.POLICY_VIOLATION, "message rate limit exceeded");
            return;
        }
        try {
            authenticatedSession.requireActive();
            authorization.requireWebSocketWorkspaceAccess(
                    authenticatedSession, enterpriseContext);
            authorization.requireLspWorkspaceAccess(
                    authenticatedSession, enterpriseContext);
            workspaceBoundary.requireWithinProject(message);
            InProcessLspSession current = lsp;
            if (current == null || current.isClosed()) {
                closeSocket(StatusCode.SERVER_ERROR, "language server session is unavailable");
                return;
            }
            current.receive(message);
            socket.demand();
        } catch (RuntimeException | IOException e) {
            closeSocket(StatusCode.POLICY_VIOLATION, "session is no longer authorized");
        }
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        closeResources();
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason, Callback callback) {
        closeResources();
        callback.succeed();
    }

    private void closeSocket(int statusCode, String reason) {
        if (!closed.compareAndSet(false, true)) return;
        InProcessLspSession current = lsp;
        if (current != null) current.close();
        authenticatedSession.close();
        Session socket = webSocket;
        if (socket != null && socket.isOpen()) {
            socket.close(statusCode, reason, Callback.NOOP);
        }
    }

    private void closeResources() {
        if (!closed.compareAndSet(false, true)) return;
        InProcessLspSession current = lsp;
        if (current != null) current.close();
        authenticatedSession.close();
    }
}
