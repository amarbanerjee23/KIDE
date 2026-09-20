package com.kide.glsp;

import java.net.URI;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public final class GlspWorkspaceUriMapper {
    public static final String SCHEME = "kide-workspace";
    public static final String ROOT_URI = SCHEME + ":/";

    private static final Set<String> URI_KEYS =
            Set.of("sourceuri", "fileuri");

    private final KideGlspWorkspace workspace;

    public GlspWorkspaceUriMapper(KideGlspWorkspace workspace) {
        this.workspace = java.util.Objects.requireNonNull(workspace, "workspace");
    }

    public String toServer(String jsonRpcMessage) {
        return rewriteMessage(jsonRpcMessage, true);
    }

    public String toClient(String jsonRpcMessage) {
        return rewriteMessage(jsonRpcMessage, false);
    }

    private String rewriteMessage(String message, boolean inbound) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("GLSP message is required");
        }
        final JsonElement parsed;
        try {
            parsed = JsonParser.parseString(message);
        } catch (RuntimeException failure) {
            throw new IllegalArgumentException("GLSP message is not valid JSON");
        }
        return rewrite(parsed, null, inbound).toString();
    }

    private JsonElement rewrite(JsonElement element, String key, boolean inbound) {
        if (element == null || element.isJsonNull()) return element;
        if (element.isJsonArray()) {
            var array = new com.google.gson.JsonArray();
            for (JsonElement item : element.getAsJsonArray()) {
                array.add(rewrite(item, key, inbound));
            }
            return array;
        }
        if (element.isJsonObject()) {
            JsonObject out = new JsonObject();
            for (var entry : element.getAsJsonObject().entrySet()) {
                out.add(entry.getKey(), rewrite(entry.getValue(), entry.getKey(), inbound));
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
        if (inbound && raw.startsWith(ROOT_URI)) {
            URI uri = URI.create(raw);
            if (uri.getAuthority() != null || uri.getQuery() != null
                    || uri.getFragment() != null) {
                throw new IllegalArgumentException(
                        "GLSP workspace URI contains unsupported components");
            }
            String relative = uri.getPath().substring(1);
            if (relative.isBlank() || relative.contains("\\")
                    || java.util.Arrays.stream(relative.split("/", -1))
                            .anyMatch(part -> part.isEmpty()
                                    || ".".equals(part) || "..".equals(part))) {
                throw new IllegalArgumentException("GLSP workspace URI path is invalid");
            }
            Path target = workspace.requireProjectPath(
                    workspace.projectRoot().resolve(relative).toString());
            return target.toString();
        }
        if (!inbound) {
            try {
                Path path;
                URI uri = URI.create(raw);
                if ("file".equalsIgnoreCase(uri.getScheme())) {
                    path = Path.of(uri);
                } else if (uri.getScheme() == null) {
                    path = Path.of(raw);
                } else {
                    return raw;
                }
                Path safe = workspace.requireProjectPath(path.toString());
                String relative = workspace.projectRoot().relativize(safe)
                        .toString().replace('\\', '/');
                return ROOT_URI + java.util.Arrays.stream(relative.split("/"))
                        .map(segment -> java.net.URLEncoder.encode(
                                segment, java.nio.charset.StandardCharsets.UTF_8)
                                .replace("+", "%20"))
                        .collect(java.util.stream.Collectors.joining("/"));
            } catch (IllegalArgumentException failure) {
                return raw;
            }
        }
        if (raw.startsWith("file:")) {
            workspace.requireProjectPath(Path.of(URI.create(raw)).toString());
        } else {
            workspace.requireProjectPath(raw);
        }
        return raw;
    }
}
