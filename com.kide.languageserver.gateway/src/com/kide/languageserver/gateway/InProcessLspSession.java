package com.kide.languageserver.gateway;

import java.io.Closeable;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.xtext.ide.server.ILanguageServerShutdownAndExitHandler;
import org.eclipse.xtext.ide.server.LaunchArgs;
import org.eclipse.xtext.ide.server.ServerLauncher;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.kide.languageserver.KideServerModules;

final class InProcessLspSession implements Closeable {
    interface Outbound {
        void send(String json) throws Exception;
        void failed(Throwable failure);
    }

    private static final int PIPE_BUFFER_BYTES = 1024 * 1024;

    private final int maxMessageBytes;
    private final Outbound outbound;
    private final PipedInputStream serverInput;
    private final PipedOutputStream clientToServer;
    private final PipedInputStream serverToClient;
    private final PipedOutputStream serverOutput;
    private final AtomicBoolean closed = new AtomicBoolean();
    private Thread serverThread;
    private Thread readerThread;

    InProcessLspSession(int maxMessageBytes, Outbound outbound) throws IOException {
        this.maxMessageBytes = maxMessageBytes;
        this.outbound = Objects.requireNonNull(outbound, "outbound");
        this.serverInput = new PipedInputStream(PIPE_BUFFER_BYTES);
        this.clientToServer = new PipedOutputStream(serverInput);
        this.serverToClient = new PipedInputStream(PIPE_BUFFER_BYTES);
        this.serverOutput = new PipedOutputStream(serverToClient);
    }

    void start() {
        if (serverThread != null) throw new IllegalStateException("LSP session already started");

        serverThread = new Thread(() -> {
            try {
                Module inProcessModule = Modules.override(KideServerModules.create())
                        .with(new AbstractModule() {
                            @Override
                            protected void configure() {
                                bind(ILanguageServerShutdownAndExitHandler.class)
                                        .to(ILanguageServerShutdownAndExitHandler.NullImpl.class);
                            }
                        });
                ServerLauncher launcher = Guice.createInjector(inProcessModule)
                        .getInstance(ServerLauncher.class);
                LaunchArgs args = new LaunchArgs();
                args.setIn(serverInput);
                args.setOut(serverOutput);
                args.setValidate(true);
                args.setTrace(null);
                launcher.start(args);
            } catch (Throwable failure) {
                if (!closed.get()) outbound.failed(failure);
            } finally {
                closeQuietly(serverOutput);
            }
        }, "kide-lsp-session");
        serverThread.setDaemon(true);

        readerThread = new Thread(() -> {
            try {
                while (!closed.get()) {
                    String message = LspMessageFraming.readJson(serverToClient, maxMessageBytes);
                    outbound.send(message);
                }
            } catch (Throwable failure) {
                if (!closed.get()) outbound.failed(failure);
            }
        }, "kide-lsp-websocket-outbound");
        readerThread.setDaemon(true);

        serverThread.start();
        readerThread.start();
    }

    void receive(String json) throws IOException {
        if (closed.get()) throw new IOException("LSP session is closed");
        if (json.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > maxMessageBytes) {
            throw new IOException("LSP request exceeds configured size limit");
        }
        LspMessageFraming.writeJson(clientToServer, json);
    }

    boolean isClosed() {
        return closed.get();
    }

    @Override
    public void close() {
        if (!closed.compareAndSet(false, true)) return;
        closeQuietly(clientToServer);
        closeQuietly(serverInput);
        closeQuietly(serverOutput);
        closeQuietly(serverToClient);
        if (serverThread != null) serverThread.interrupt();
        if (readerThread != null) readerThread.interrupt();
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException ignored) {
            // Cleanup path.
        }
    }
}
