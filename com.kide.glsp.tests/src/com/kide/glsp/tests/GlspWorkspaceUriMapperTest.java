package com.kide.glsp.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.junit.Test;

import com.google.gson.JsonParser;
import com.kide.glsp.GlspWorkspaceUriMapper;
import com.kide.glsp.KideGlspWorkspace;

public class GlspWorkspaceUriMapperTest {
    @Test
    public void virtualUrisRoundTripWithoutExposingServerPaths() throws Exception {
        Path root = Files.createTempDirectory("kide-pr32-uri-");
        try {
            Path project = Files.createDirectories(root.resolve("project"));
            Path model = project.resolve("models/main.activity");
            Files.createDirectories(model.getParent());
            Files.writeString(model, "ActivityDiagram Empty\nhas activities { }\n");

            GlspWorkspaceUriMapper mapper =
                    new GlspWorkspaceUriMapper(new KideGlspWorkspace(project));

            String inbound = mapper.toServer(
                    "{\"jsonrpc\":\"2.0\",\"method\":\"process\","
                    + "\"params\":{\"action\":{\"options\":{"
                    + "\"sourceUri\":\"kide-workspace:/models/main.activity\"}}}}");
            String serverPath = JsonParser.parseString(inbound).getAsJsonObject()
                    .getAsJsonObject("params")
                    .getAsJsonObject("action")
                    .getAsJsonObject("options")
                    .get("sourceUri").getAsString();
            assertEquals(model.toAbsolutePath().normalize().toString(), serverPath);

            String outbound = mapper.toClient(
                    "{\"jsonrpc\":\"2.0\",\"method\":\"process\","
                    + "\"params\":{\"sourceUri\":"
                    + com.google.gson.JsonParser.parseString(
                            "\"" + serverPath.replace("\\", "\\\\") + "\"")
                    + "}}");
            assertTrue(outbound.contains("kide-workspace:/models/main.activity"));
            assertTrue(!outbound.contains(project.toString()));

            String rootModel = mapper.toClient(
                    "{\"params\":{\"action\":{\"kind\":\"setModel\","
                    + "\"newRoot\":{\"id\":"
                    + com.google.gson.JsonParser.parseString(
                            "\"" + serverPath.replace("\\", "\\\\") + "\"")
                    + ",\"children\":[{\"id\":\"//@systems.0\"}]}}}}");
            assertTrue(rootModel.contains(
                    "\"id\":\"kide-workspace:/models/main.activity\""));
            assertTrue(rootModel.contains("\"id\":\"//@systems.0\""));
            assertTrue(!rootModel.contains(project.toString()));

            String rootInbound = mapper.toServer(
                    "{\"params\":{\"action\":{\"id\":"
                    + "\"kide-workspace:/models/main.activity\"}}}");
            String rootServerId = JsonParser.parseString(rootInbound).getAsJsonObject()
                    .getAsJsonObject("params")
                    .getAsJsonObject("action")
                    .get("id").getAsString();
            assertEquals(serverPath, rootServerId);
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void traversalAndOutsidePathsFailClosed() throws Exception {
        Path root = Files.createTempDirectory("kide-pr32-uri-reject-");
        try {
            Path project = Files.createDirectories(root.resolve("project"));
            Path outside = Files.createDirectories(root.resolve("outside"));
            GlspWorkspaceUriMapper mapper =
                    new GlspWorkspaceUriMapper(new KideGlspWorkspace(project));

            assertThrows(
                    IllegalArgumentException.class,
                    () -> mapper.toServer(
                            "{\"params\":{\"sourceUri\":"
                            + "\"kide-workspace:/../outside/secret.mncspec\"}}"));
            assertThrows(
                    IllegalArgumentException.class,
                    () -> mapper.toServer(
                            "{\"params\":{\"sourceUri\":\""
                            + outside.resolve("secret.mncspec").toUri()
                            + "\"}}"));
        } finally {
            deleteTree(root);
        }
    }

    private static void deleteTree(Path root) throws Exception {
        if (root == null || !Files.exists(root)) return;
        try (java.util.stream.Stream<Path> stream = Files.walk(root)) {
            for (Path path : stream.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }
}
