package com.kide.languageserver.gateway;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class LspWorkspaceBoundary {
    private static final Set<String> URI_KEYS = Set.of(
            "uri", "rooturi", "targeturi", "olduri", "newuri");
    private static final Set<String> PATH_KEYS = Set.of("rootpath");

    private final Path projectRoot;
    private final Path projectRootReal;

    public LspWorkspaceBoundary(Path projectRoot) {
        this.projectRoot = java.util.Objects.requireNonNull(projectRoot, "projectRoot")
                .toAbsolutePath().normalize();
        try {
            if (!Files.isDirectory(this.projectRoot)) {
                throw new IllegalArgumentException("projectRoot must be an existing directory");
            }
            this.projectRootReal = this.projectRoot.toRealPath();
        } catch (IOException e) {
            throw new IllegalArgumentException("projectRoot cannot be canonicalized", e);
        }
    }

    public LspWorkspaceBoundary(GatewayWorkspaceBinding binding) {
        this(java.util.Objects.requireNonNull(binding, "binding").projectRoot());
    }

    public void requireWithinProject(String jsonRpcMessage) {
        if (jsonRpcMessage == null || jsonRpcMessage.isBlank()) {
            throw new IllegalArgumentException("LSP message is required");
        }
        final JsonElement root;
        try {
            root = JsonParser.parseString(jsonRpcMessage);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("LSP message is not valid JSON");
        }
        inspect(root, null);
    }

    private void inspect(JsonElement element, String key) {
        if (element == null || element.isJsonNull()) return;

        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            for (var entry : object.entrySet()) {
                inspect(entry.getValue(), entry.getKey());
            }
            return;
        }

        if (element.isJsonArray()) {
            for (JsonElement item : element.getAsJsonArray()) {
                inspect(item, key);
            }
            return;
        }

        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString() || key == null) {
            return;
        }

        String normalizedKey = key.toLowerCase(Locale.ROOT);
        String value = element.getAsString();
        if (URI_KEYS.contains(normalizedKey)) {
            requireFileUriWithinProject(value);
        } else if (PATH_KEYS.contains(normalizedKey)) {
            requirePathWithinProject(value);
        }
    }

    private void requireFileUriWithinProject(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("LSP file URI is empty");
        }
        final URI uri;
        try {
            uri = URI.create(raw);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("LSP file URI is invalid");
        }
        if (!"file".equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException("Only file URIs are allowed in this LSP session");
        }
        final Path path;
        try {
            path = Path.of(uri);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("LSP file URI cannot be resolved");
        }
        requireCandidateWithinProject(path);
    }

    private void requirePathWithinProject(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("LSP rootPath is empty");
        }
        final Path path;
        try {
            path = Path.of(raw);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("LSP rootPath is invalid");
        }
        requireCandidateWithinProject(path);
    }

    private void requireCandidateWithinProject(Path rawPath) {
        Path candidate = rawPath.toAbsolutePath().normalize();
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

        final Path realExisting;
        try {
            realExisting = existing.toRealPath();
        } catch (IOException e) {
            throw new IllegalArgumentException("LSP path cannot be canonicalized");
        }
        if (!realExisting.startsWith(projectRootReal)) {
            throw new IllegalArgumentException("LSP path escapes the authorized project");
        }

        if (Files.exists(candidate)) {
            try {
                Path realCandidate = candidate.toRealPath();
                if (!realCandidate.startsWith(projectRootReal)) {
                    throw new IllegalArgumentException("LSP path escapes the authorized project");
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("LSP path cannot be canonicalized");
            }
        }
    }
}
