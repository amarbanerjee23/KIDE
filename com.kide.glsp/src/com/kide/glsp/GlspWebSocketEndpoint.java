package com.kide.glsp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Clock;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;

import com.kide.enterprise.authorization.ServerAuthorizationGate;
import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.identity.AuthenticatedSession;
import com.kide.languageserver.gateway.GatewayConfig;
import com.kide.languageserver.gateway.GatewayMessagePolicy;
import com.kide.languageserver.gateway.GatewaySessionQuota;
import com.kide.languageserver.gateway.GatewayWorkspaceBinding;

final class GlspWebSocketEndpoint implements Session.Listener {
    private final GatewayConfig config;
    private final AuthenticatedSession authenticatedSession;
    private final ServerAuthorizationGate authorization;
    private final EnterpriseContext enterpriseContext;
    private final KideGlspWorkspace workspace;
    private final GlspWorkspaceUriMapper workspaceUris;
    private final GatewaySessionQuota.Lease sessionLease;
    private final GatewayMessagePolicy messagePolicy;
    private final AtomicBoolean closed = new AtomicBoolean();

    private volatile Session webSocket;
    private volatile InProcessGlspSession glsp;

    GlspWebSocketEndpoint(
            GatewayConfig config,
            AuthenticatedSession authenticatedSession,
            ServerAuthorizationGate authorization,
            GatewayWorkspaceBinding workspaceBinding,
            GatewaySessionQuota.Lease sessionLease,
            Clock clock) {
        this.config = Objects.requireNonNull(config, "config");
        this.authenticatedSession =
                Objects.requireNonNull(authenticatedSession, "authenticatedSession");
        this.authorization = Objects.requireNonNull(authorization, "authorization");
        GatewayWorkspaceBinding binding =
                Objects.requireNonNull(workspaceBinding, "workspaceBinding");
        this.enterpriseContext = binding.context();
        this.workspace = new KideGlspWorkspace(binding.projectRoot());
        this.workspaceUris = new GlspWorkspaceUriMapper(workspace);
        this.sessionLease = Objects.requireNonNull(sessionLease, "sessionLease");
        this.messagePolicy = new GatewayMessagePolicy(
                config.maxTextMessageBytes(), config.maxMessagesPerMinute(), clock);
    }

    @Override
    public void onWebSocketOpen(Session session) {
        this.webSocket = session;
        session.setIdleTimeout(config.idleTimeout());
        session.setMaxTextMessageSize(config.maxTextMessageBytes());
        try {
            InProcessGlspSession created = new InProcessGlspSession(
                    config.maxTextMessageBytes(),
                    workspace,
                    new InProcessGlspSession.Outbound() {
                        @Override
                        public void send(String json) {
                            Session socket = webSocket;
                            if (socket == null || !socket.isOpen() || closed.get()) return;
                            Callback.Completable callback = new Callback.Completable();
                            socket.sendText(workspaceUris.toClient(json), callback);
                            callback.join();
                        }

                        @Override
                        public void failed(Throwable failure) {
                            closeSocket(StatusCode.SERVER_ERROR,
                                    "graphical server session failed");
                        }
                    });
            this.glsp = created;
            created.start();
            session.demand();
        } catch (IOException failure) {
            closeSocket(StatusCode.SERVER_ERROR,
                    "graphical server session could not start");
        }
    }

    @Override
    public void onWebSocketText(String message) {
        if (closed.get()) return;
        Session socket = webSocket;
        if (socket == null) return;

        GatewayMessagePolicy.Decision decision = messagePolicy.evaluate(message);
        if (decision == GatewayMessagePolicy.Decision.MESSAGE_TOO_LARGE) {
            closeSocket(StatusCode.MESSAGE_TOO_LARGE,
                    "message exceeds configured limit");
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
            authorization.requireModelRead(authenticatedSession, enterpriseContext);
            authorization.requireWorkspaceWrite(
                    authenticatedSession, enterpriseContext);

            String translated = workspaceUris.toServer(message);
            InProcessGlspSession current = glsp;
            if (current == null || current.isClosed()) {
                closeSocket(StatusCode.SERVER_ERROR,
                        "graphical server session is unavailable");
                return;
            }
            current.receive(translated);
            socket.demand();
        } catch (RuntimeException | IOException failure) {
            closeSocket(StatusCode.POLICY_VIOLATION,
                    "graphical session is no longer authorized");
        }
    }

    @Override
    public void onWebSocketBinary(ByteBuffer payload, Callback callback) {
        callback.succeed();
        closeSocket(StatusCode.BAD_DATA,
                "binary WebSocket messages are not supported");
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
        InProcessGlspSession current = glsp;
        if (current != null) current.close();
        authenticatedSession.close();
        sessionLease.close();
        Session socket = webSocket;
        if (socket != null && socket.isOpen()) {
            socket.close(statusCode, reason, Callback.NOOP);
        }
    }

    private void closeResources() {
        if (!closed.compareAndSet(false, true)) return;
        InProcessGlspSession current = glsp;
        if (current != null) current.close();
        authenticatedSession.close();
        sessionLease.close();
    }
}
