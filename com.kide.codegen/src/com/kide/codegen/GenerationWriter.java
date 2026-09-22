package com.kide.codegen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class GenerationWriter {
    public void write(Path stagingRoot, GenerationOutput output) {
        Path root = prepareRoot(stagingRoot);
        try {
            for (GeneratedArtifact artifact : output.artifacts()) {
                writeOne(root, artifact.path(), artifact.bytes());
            }
            writeOne(root, "generation-manifest.json", output.manifest().jsonBytes());
        } catch (IOException e) {
            throw new GenerationException("generated artifacts could not be written", e);
        }
    }

    private static Path prepareRoot(Path stagingRoot) {
        if (stagingRoot == null) throw new IllegalArgumentException("stagingRoot is required");
        Path root = stagingRoot.toAbsolutePath().normalize();
        try {
            if (Files.exists(root, LinkOption.NOFOLLOW_LINKS)) {
                if (Files.isSymbolicLink(root)
                        || !Files.isDirectory(root, LinkOption.NOFOLLOW_LINKS)) {
                    throw new GenerationException("stagingRoot must be a real directory");
                }
            } else {
                Files.createDirectories(root);
            }
            return root;
        } catch (IOException e) {
            throw new GenerationException("stagingRoot could not be prepared", e);
        }
    }

    private static void writeOne(Path root, String relative, byte[] bytes) throws IOException {
        String safe = GenerationPaths.requireSafeRelative(relative);
        Path target = root.resolve(safe).normalize();
        if (!target.startsWith(root)) {
            throw new GenerationException("generated path escapes stagingRoot");
        }
        Path parent = target.getParent();
        if (parent != null) {
            verifyNoSymlinkParents(root, parent);
            Files.createDirectories(parent);
            verifyNoSymlinkParents(root, parent);
        }
        if (Files.exists(target, LinkOption.NOFOLLOW_LINKS) && Files.isSymbolicLink(target)) {
            throw new GenerationException("generated target is a symbolic link: " + safe);
        }
        Files.write(
                target,
                bytes,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

    private static void verifyNoSymlinkParents(Path root, Path parent) {
        Path current = root;
        Path relative = root.relativize(parent);
        for (Path segment : relative) {
            current = current.resolve(segment);
            if (Files.exists(current, LinkOption.NOFOLLOW_LINKS)
                    && Files.isSymbolicLink(current)) {
                throw new GenerationException(
                        "generated path crosses symbolic link: " + root.relativize(current));
            }
        }
    }
}
