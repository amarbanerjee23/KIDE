package com.kide.glsp;

import java.io.Closeable;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.glsp.server.gson.ServerGsonConfigurator;
import org.eclipse.glsp.server.protocol.GLSPClient;
import org.eclipse.glsp.server.protocol.GLSPServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.kide.languageserver.gateway.LspMessageFraming;

final class InProcessGlspSession implements Closeable {
    interface Outbound {
        void send(String json) throws Exception;
        void failed(Throwable failure);
    }

    private static final int PIPE_BUFFER_BYTES = 1024 * 1024;

    private final int maxMessageBytes;
    private final Outbound outbound;
    private final KideGlspWorkspace workspace;
    private final PipedInputStream serverInput;
    private final PipedOutputStream clientToServer;
    private final PipedInputStream serverToClient;
    private final PipedOutputStream serverOutput;
    private final AtomicBoolean closed = new AtomicBoolean();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private volatile GLSPServer glspServer;
    private Thread serverThread;
    private Thread readerThread;

    InProcessGlspSession(
            int maxMessageBytes, KideGlspWorkspace workspace, Outbound outbound)
            throws IOException {
        this.maxMessageBytes = maxMessageBytes;
        this.workspace = Objects.requireNonNull(workspace, "workspace");
        this.outbound = Objects.requireNonNull(outbound, "outbound");
        this.serverInput = new PipedInputStream(PIPE_BUFFER_BYTES);
        this.clientToServer = new PipedOutputStream(serverInput);
        this.serverToClient = new PipedInputStream(PIPE_BUFFER_BYTES);
        this.serverOutput = new PipedOutputStream(serverToClient);
    }

    void start() {
        if (serverThread != null) throw new IllegalStateException("GLSP session already started");

        serverThread = new Thread(() -> {
            try {
                Injector injector = Guice.createInjector(
                        KideGlspServerModules.create(),
                        new AbstractModule() {
                            @Override
                            protected void configure() {
                                bind(KideGlspWorkspace.class).toInstance(workspace);
                            }
                        });
                GLSPServer server = injector.getInstance(GLSPServer.class);
                glspServer = server;
                ServerGsonConfigurator gson =
                        injector.getInstance(ServerGsonConfigurator.class);
                Launcher<GLSPClient> launcher = Launcher.createIoLauncher(
                        server,
                        GLSPClient.class,
                        serverInput,
                        serverOutput,
                        executor,
                        java.util.function.Function.identity(),
                        builder -> gson.configureGsonBuilder(builder));
                server.connect(launcher.getRemoteProxy());
                launcher.startListening().get();
            } catch (Throwable failure) {
                if (!closed.get()) outbound.failed(failure);
            } finally {
                closeQuietly(serverOutput);
            }
        }, "kide-glsp-session");
        serverThread.setDaemon(true);

        readerThread = new Thread(() -> {
            try {
                while (!closed.get()) {
                    outbound.send(LspMessageFraming.readJson(
                            serverToClient, maxMessageBytes));
                }
            } catch (Throwable failure) {
                if (!closed.get()) outbound.failed(failure);
            }
        }, "kide-glsp-websocket-outbound");
        readerThread.setDaemon(true);

        serverThread.start();
        readerThread.start();
    }

    void receive(String json) throws IOException {
        if (closed.get()) throw new IOException("GLSP session is closed");
        if (json.getBytes(java.nio.charset.StandardCharsets.UTF_8).length
                > maxMessageBytes) {
            throw new IOException("GLSP request exceeds configured size limit");
        }
        LspMessageFraming.writeJson(clientToServer, json);
    }

    boolean isClosed() { return closed.get(); }

    @Override
    public void close() {
        if (!closed.compareAndSet(false, true)) return;
        GLSPServer current = glspServer;
        if (current != null) {
            try { current.shutdown(); } catch (RuntimeException ignored) { }
        }
        closeQuietly(clientToServer);
        closeQuietly(serverInput);
        closeQuietly(serverOutput);
        closeQuietly(serverToClient);
        executor.shutdownNow();
        if (serverThread != null) serverThread.interrupt();
        if (readerThread != null) readerThread.interrupt();
    }

    private static void closeQuietly(Closeable closeable) {
        try { closeable.close(); } catch (IOException ignored) { }
    }
}
