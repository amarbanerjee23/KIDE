package com.kide.languageserver.gateway.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import com.kide.enterprise.context.EnterpriseContextResult;
import com.kide.enterprise.context.EnterpriseContextStore;
import com.kide.languageserver.gateway.GatewayWorkspaceBinding;
import com.kide.languageserver.gateway.LspWorkspaceBoundary;
import com.kide.languageserver.gateway.LspWorkspaceUriMapper;

public class LspWorkspaceUriMapperTest {
    @Test
    public void browserUrisRoundTripWithoutLeakingServerProjectPath() throws Exception {
        Path root = Files.createTempDirectory("kide-pr31-uri-");
        try {
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project"));
            Files.writeString(project.resolve("model.dml"), "DataModel Golden {}");
            EnterpriseContextResult provisioned = new EnterpriseContextStore().provision(
                    workspace, project, "Org", "Portfolio", "Project", "Workspace");
            GatewayWorkspaceBinding binding =
                    new GatewayWorkspaceBinding(provisioned.context().orElseThrow(), project);
            LspWorkspaceUriMapper mapper = new LspWorkspaceUriMapper(binding);
            LspWorkspaceBoundary boundary = new LspWorkspaceBoundary(binding);

            String browser = "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/didOpen\","
                    + "\"params\":{\"textDocument\":{\"uri\":\"kide-workspace:/model.dml\"}}}";
            String server = mapper.toServer(browser);
            assertTrue(server.contains(project.resolve("model.dml").toUri().toString()));
            boundary.requireWithinProject(server);

            String rename = "{\"jsonrpc\":\"2.0\",\"id\":2,\"result\":{\"changes\":{\""
                    + project.resolve("model.dml").toUri()
                    + "\":[{\"range\":{\"start\":{\"line\":0,\"character\":0},"
                    + "\"end\":{\"line\":0,\"character\":1}},\"newText\":\"X\"}]}}}";
            String client = mapper.toClient(rename);
            assertTrue(client.contains("kide-workspace:/model.dml"));
            assertFalse(client.contains(project.toString()));
        } finally {
            try (var stream = Files.walk(root)) {
                stream.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
                    try { Files.deleteIfExists(path); } catch (java.io.IOException ignored) { }
                });
            }
        }
    }

    @Test
    public void nativeFileUriSessionsKeepNativeUris() throws Exception {
        Path root = Files.createTempDirectory("kide-pr31-uri-native-");
        try {
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project"));
            Path model = Files.writeString(project.resolve("native.dml"), "DataModel Native {}");
            EnterpriseContextResult provisioned = new EnterpriseContextStore().provision(
                    workspace, project, "Org", "Portfolio", "Project", "Workspace");
            LspWorkspaceUriMapper mapper = new LspWorkspaceUriMapper(
                    new GatewayWorkspaceBinding(provisioned.context().orElseThrow(), project));

            String inbound = "{\"jsonrpc\":\"2.0\",\"params\":{\"rootUri\":\""
                    + project.toUri() + "\"}}";
            assertTrue(mapper.toServer(inbound).contains(project.toUri().toString()));

            String outbound = "{\"jsonrpc\":\"2.0\",\"params\":{\"uri\":\""
                    + model.toUri() + "\"}}";
            String nativeResult = mapper.toClient(outbound);
            assertTrue(nativeResult.contains(model.toUri().toString()));
            assertFalse(nativeResult.contains("kide-workspace:"));
        } finally {
            try (var stream = Files.walk(root)) {
                stream.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
                    try { Files.deleteIfExists(path); } catch (java.io.IOException ignored) { }
                });
            }
        }
    }

    @Test
    public void traversalAndOutsideServerUrisFailClosed() throws Exception {
        Path root = Files.createTempDirectory("kide-pr31-uri-deny-");
        try {
            Path workspace = Files.createDirectories(root.resolve("workspace"));
            Path project = Files.createDirectories(root.resolve("project"));
            EnterpriseContextResult provisioned = new EnterpriseContextStore().provision(
                    workspace, project, "Org", "Portfolio", "Project", "Workspace");
            LspWorkspaceUriMapper mapper = new LspWorkspaceUriMapper(
                    new GatewayWorkspaceBinding(provisioned.context().orElseThrow(), project));

            assertThrows(
                    IllegalArgumentException.class,
                    () -> mapper.toServer(
                            "{\"jsonrpc\":\"2.0\",\"params\":{\"uri\":"
                            + "\"kide-workspace:/../outside.dml\"}}"));

            Path outside = root.resolve("outside.dml");
            assertThrows(
                    IllegalArgumentException.class,
                    () -> mapper.toClient(
                            "{\"jsonrpc\":\"2.0\",\"result\":{\"uri\":\""
                            + outside.toUri() + "\"}}"));
        } finally {
            try (var stream = Files.walk(root)) {
                stream.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
                    try { Files.deleteIfExists(path); } catch (java.io.IOException ignored) { }
                });
            }
        }
    }
}
