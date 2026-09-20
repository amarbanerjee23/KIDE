package com.kide.languageserver.gateway;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * Maps browser-safe project-relative URIs to the authorized server project root
 * and maps server file URIs back before data crosses the WebSocket boundary.
 */
public final class LspWorkspaceUriMapper {
    public static final String SCHEME = "kide-workspace";
    public static final String ROOT_URI = SCHEME + ":/";

    private static final Set<String> URI_KEYS = Set.of(
            "uri", "rooturi", "targeturi", "olduri", "newuri");

    private final Path projectRoot;
    private final Path projectRootReal;

    public LspWorkspaceUriMapper(GatewayWorkspaceBinding binding) {
        GatewayWorkspaceBinding required =
                java.util.Objects.requireNonNull(binding, "binding");
        this.projectRoot = required.projectRoot();
        this.projectRootReal = required.projectRootReal();
    }

    public String toServer(String jsonRpcMessage) {
        return rewriteMessage(jsonRpcMessage, true);
    }

    public String toClient(String jsonRpcMessage) {
        return rewriteMessage(jsonRpcMessage, false);
    }

    private String rewriteMessage(String jsonRpcMessage, boolean inbound) {
        if (jsonRpcMessage == null || jsonRpcMessage.isBlank()) {
            throw new IllegalArgumentException("LSP message is required");
        }
        try {
            JsonElement parsed = JsonParser.parseString(jsonRpcMessage);
            return rewrite(parsed, null, inbound).toString();
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("LSP message is not valid JSON");
        }
    }

    private JsonElement rewrite(JsonElement element, String key, boolean inbound) {
        if (element == null || element.isJsonNull()) return element;

        if (element.isJsonArray()) {
            var out = new com.google.gson.JsonArray();
            for (JsonElement item : element.getAsJsonArray()) {
                out.add(rewrite(item, key, inbound));
            }
            return out;
        }

        if (element.isJsonObject()) {
            JsonObject source = element.getAsJsonObject();
            JsonObject out = new JsonObject();
            boolean changeMap = key != null && "changes".equalsIgnoreCase(key);
            for (var entry : source.entrySet()) {
                String outputKey = changeMap
                        ? rewriteUri(entry.getKey(), inbound)
                        : entry.getKey();
                out.add(outputKey, rewrite(entry.getValue(), entry.getKey(), inbound));
            }
            return out;
        }

        if (element.isJsonPrimitive()
                && element.getAsJsonPrimitive().isString()
                && key != null
                && URI_KEYS.contains(key.toLowerCase(Locale.ROOT))) {
            return new JsonPrimitive(rewriteUri(element.getAsString(), inbound));
        }
        return element.deepCopy();
    }

    private String rewriteUri(String raw, boolean inbound) {
        if (raw == null || raw.isBlank()) return raw;
        final URI uri;
        try {
            uri = URI.create(raw);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("LSP URI is invalid");
        }

        if (inbound) {
            if (SCHEME.equalsIgnoreCase(uri.getScheme())) {
                return browserUriToFile(uri).toUri().toString();
            }
            return raw;
        }

        if ("file".equalsIgnoreCase(uri.getScheme())) {
            return fileUriToBrowser(uri);
        }
        return raw;
    }

    private Path browserUriToFile(URI uri) {
        if (uri.getAuthority() != null
                || uri.getQuery() != null
                || uri.getFragment() != null) {
            throw new IllegalArgumentException("browser workspace URI contains unsupported components");
        }
        String path = uri.getPath();
        if (path == null || !path.startsWith("/") || path.contains("\\")) {
            throw new IllegalArgumentException("browser workspace URI path is invalid");
        }
        String relativeText = path.substring(1);
        if (relativeText.isEmpty()) return projectRoot;

        String[] segments = relativeText.split("/", -1);
        for (String segment : segments) {
            if (segment.isEmpty() || ".".equals(segment) || "..".equals(segment)) {
                throw new IllegalArgumentException("browser workspace URI contains unsafe path segments");
            }
        }
        Path candidate = projectRoot.resolve(relativeText).normalize();
        requireInside(candidate);
        return candidate;
    }

    private String fileUriToBrowser(URI uri) {
        final Path candidate;
        try {
            candidate = Path.of(uri).toAbsolutePath().normalize();
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("server LSP file URI cannot be resolved");
        }
        requireInside(candidate);
        Path relative = projectRoot.relativize(candidate);
        String portable = relative.toString().replace('\\', '/');
        try {
            return new URI(SCHEME, null, "/" + portable, null).toASCIIString();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("server LSP file URI cannot be virtualized");
        }
    }

    private void requireInside(Path candidate) {
        if (!candidate.startsWith(projectRoot)) {
            throw new IllegalArgumentException("LSP path is outside the authorized project");
        }

        Path existing = candidate;
        while (existing != null && !Files.exists(existing)) {
            existing = existing.getParent();
        }
        if (existing == null) {
            throw new IllegalArgumentException("LSP path has no existing authorized ancestor");
        }
        try {
            if (!existing.toRealPath().startsWith(projectRootReal)) {
                throw new IllegalArgumentException("LSP path escapes the authorized project");
            }
            if (Files.exists(candidate)
                    && !candidate.toRealPath().startsWith(projectRootReal)) {
                throw new IllegalArgumentException("LSP path escapes the authorized project");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("LSP path cannot be canonicalized");
        }
    }
}
