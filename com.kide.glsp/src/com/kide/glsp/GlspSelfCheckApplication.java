package com.kide.glsp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class GlspSelfCheckApplication implements IApplication {
    private volatile InProcessGlspSession session;

    @Override
    public Object start(IApplicationContext context) {
        Path project = null;
        try {
            project = Files.createTempDirectory("kide-pr32-packaged-glsp-");
            Path source = project.resolve("selfcheck.mncspec");
            Files.writeString(
                    source,
                    "Model Golden\n"
                    + "InterfaceDescription Device {\n"
                    + "  commands { Start[] }\n"
                    + "  events { Publish Ready[] }\n"
                    + "}\n");

            BlockingQueue<JsonObject> outbound = new LinkedBlockingQueue<>();
            session = new InProcessGlspSession(
                    1024 * 1024,
                    new KideGlspWorkspace(project),
                    new InProcessGlspSession.Outbound() {
                        @Override
                        public void send(String json) {
                            outbound.add(JsonParser.parseString(json).getAsJsonObject());
                        }

                        @Override
                        public void failed(Throwable failure) {
                            JsonObject error = new JsonObject();
                            error.addProperty("selfcheckFailure", failure.toString());
                            outbound.add(error);
                        }
                    });
            session.start();

            session.receive(
                    "{\"jsonrpc\":\"2.0\",\"id\":1,"
                    + "\"method\":\"initialize\",\"params\":{"
                    + "\"applicationId\":\"KIDE Packaged GLSP Self Check\","
                    + "\"protocolVersion\":\"1.0.0\"}}");
            JsonObject initialized = await(
                    outbound,
                    message -> message.has("id")
                            && message.get("id").getAsInt() == 1);
            if (!"1.0.0".equals(
                    initialized.getAsJsonObject("result")
                            .get("protocolVersion").getAsString())) {
                throw new IllegalStateException("Packaged GLSP protocol version mismatch");
            }

            session.receive(
                    "{\"jsonrpc\":\"2.0\",\"id\":2,"
                    + "\"method\":\"initializeClientSession\",\"params\":{"
                    + "\"clientSessionId\":\"packaged-selfcheck\","
                    + "\"diagramType\":\"kide-mnc-diagram\","
                    + "\"clientActionKinds\":[\"setModel\",\"setTypeHints\"]}}");
            await(outbound, message -> message.has("id")
                    && message.get("id").getAsInt() == 2);

            String escapedPath = source.toAbsolutePath().normalize().toString()
                    .replace("\\", "\\\\");
            session.receive(
                    "{\"jsonrpc\":\"2.0\",\"method\":\"process\","
                    + "\"params\":{\"clientId\":\"packaged-selfcheck\","
                    + "\"action\":{\"kind\":\"requestModel\","
                    + "\"requestId\":\"model-1\",\"options\":{"
                    + "\"sourceUri\":\"" + escapedPath + "\","
                    + "\"diagramType\":\"kide-mnc-diagram\"}}}}");
            JsonObject model = await(
                    outbound,
                    message -> actionKind(message, "setModel"));
            if (!model.toString().contains("kide:mnc-interface")) {
                throw new IllegalStateException(
                        "Packaged GLSP model did not contain the shared MNC interface concept");
            }

            session.receive(
                    "{\"jsonrpc\":\"2.0\",\"method\":\"process\","
                    + "\"params\":{\"clientId\":\"packaged-selfcheck\","
                    + "\"action\":{\"kind\":\"requestTypeHints\","
                    + "\"requestId\":\"hints-1\"}}}");
            JsonObject hints = await(
                    outbound,
                    message -> actionKind(message, "setTypeHints"));
            if (!hints.toString().contains("kide:mnc-control-node")) {
                throw new IllegalStateException(
                        "Packaged GLSP type hints are incomplete");
            }

            System.out.println("KIDE PR32 GLSP SELF-CHECK OK");
            return IApplication.EXIT_OK;
        } catch (Exception failure) {
            System.err.println(
                    "KIDE packaged GLSP self-check failed: " + failure.getMessage());
            return Integer.valueOf(2);
        } finally {
            stop();
            deleteTree(project);
        }
    }

    @Override
    public void stop() {
        InProcessGlspSession current = session;
        session = null;
        if (current != null) current.close();
    }

    private static JsonObject await(
            BlockingQueue<JsonObject> messages,
            Predicate<JsonObject> predicate) throws Exception {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(20);
        while (System.nanoTime() < deadline) {
            JsonObject message = messages.poll(1, TimeUnit.SECONDS);
            if (message == null) continue;
            if (message.has("selfcheckFailure")) {
                throw new IllegalStateException(
                        message.get("selfcheckFailure").getAsString());
            }
            if (predicate.test(message)) return message;
        }
        throw new IllegalStateException("Timed out waiting for packaged GLSP response");
    }

    private static boolean actionKind(JsonObject message, String expected) {
        try {
            return "process".equals(message.get("method").getAsString())
                    && expected.equals(
                            message.getAsJsonObject("params")
                                    .getAsJsonObject("action")
                                    .get("kind").getAsString());
        } catch (RuntimeException failure) {
            return false;
        }
    }

    private static void deleteTree(Path root) {
        if (root == null || !Files.exists(root)) return;
        try (java.util.stream.Stream<Path> stream = Files.walk(root)) {
            for (Path path : stream.sorted(Comparator.reverseOrder()).toList()) {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) { }
            }
        } catch (IOException ignored) { }
    }
}
