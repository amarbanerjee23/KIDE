package com.kide.enterprise.migration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public final class ProjectSchemaService {
    public static final String MANIFEST = ".kide/schema.properties";
    public static final int CURRENT_VERSION = 1;
    private static final int MAX_BYTES = 64 * 1024;
    private static final List<String> LANGUAGE_IDS =
            Arrays.asList("dml", "operation", "mnc", "capability", "activity");

    public ProjectSchemaReport inspect(Path projectRoot) {
        try {
            Path root = normalizeRoot(projectRoot);
            if (root == null) {
                return report(ProjectSchemaStatus.INVALID, -1, "KIDE_SCHEMA_PATH_REQUIRED",
                        "Project location must be a local filesystem directory", null);
            }
            if (Files.exists(root, LinkOption.NOFOLLOW_LINKS)
                    && !Files.isDirectory(root, LinkOption.NOFOLLOW_LINKS)) {
                return report(ProjectSchemaStatus.INVALID, -1, "KIDE_SCHEMA_ROOT_INVALID",
                        "Project location is not a directory", null);
            }
            Path manifest = root.resolve(MANIFEST);
            if (!Files.exists(manifest, LinkOption.NOFOLLOW_LINKS)) {
                return report(ProjectSchemaStatus.LEGACY, 0, "KIDE_SCHEMA_LEGACY",
                        "Project predates explicit KIDE schema metadata", null);
            }
            if (!safeRegularFile(manifest)) {
                return report(ProjectSchemaStatus.INVALID, -1, "KIDE_SCHEMA_MANIFEST_UNSAFE",
                        "Project schema manifest is not a safe regular file", null);
            }
            if (Files.size(manifest) > MAX_BYTES) {
                return report(ProjectSchemaStatus.INVALID, -1, "KIDE_SCHEMA_MANIFEST_TOO_LARGE",
                        "Project schema manifest exceeds the supported size limit", null);
            }
            Properties properties = read(manifest);
            int version = parseVersion(properties.getProperty("schema.version"));
            if (version < 0) {
                return report(ProjectSchemaStatus.INVALID, -1, "KIDE_SCHEMA_VERSION_INVALID",
                        "Project schema version is missing or malformed", null);
            }
            if (version > CURRENT_VERSION) {
                return report(ProjectSchemaStatus.FORWARD_INCOMPATIBLE, version,
                        "KIDE_SCHEMA_FORWARD_VERSION",
                        "Project was created by a newer KIDE schema and will not be reinterpreted", null);
            }
            if (version < CURRENT_VERSION) {
                return report(ProjectSchemaStatus.MIGRATION_REQUIRED, version,
                        "KIDE_SCHEMA_MIGRATION_REQUIRED",
                        "Project schema requires migration before modification", null);
            }
            String error = validateCurrent(properties);
            if (error != null) {
                return report(ProjectSchemaStatus.INVALID, version,
                        "KIDE_SCHEMA_MANIFEST_INVALID", error, null);
            }
            return report(ProjectSchemaStatus.CURRENT, version, "KIDE_SCHEMA_CURRENT",
                    "Project schema is current", null);
        } catch (IOException | RuntimeException e) {
            return report(ProjectSchemaStatus.IO_ERROR, -1, "KIDE_SCHEMA_INSPECT_FAILED",
                    "Project schema could not be inspected safely", null);
        }
    }

    public ProjectSchemaReport migrate(Path projectRoot, boolean dryRun) {
        ProjectSchemaReport before = inspect(projectRoot);
        if (!before.canMigrate()) {
            return before;
        }
        if (dryRun) {
            return report(ProjectSchemaStatus.MIGRATION_REQUIRED, before.detectedVersion(),
                    "KIDE_SCHEMA_DRY_RUN",
                    "Migration can be applied without rewriting KIDE model files", null);
        }

        try {
            Path root = normalizeRoot(projectRoot);
            if (root == null) {
                return report(ProjectSchemaStatus.INVALID, -1, "KIDE_SCHEMA_PATH_REQUIRED",
                        "Project location must be a local filesystem directory", null);
            }
            Files.createDirectories(root);
            Path manifest = root.resolve(MANIFEST);
            Path parent = manifest.getParent();
            if (!safeManagedParent(root, parent)) {
                return report(ProjectSchemaStatus.INVALID, before.detectedVersion(),
                        "KIDE_SCHEMA_MANAGED_PATH_UNSAFE",
                        "Project schema storage path contains an unsafe link or non-directory", null);
            }
            Files.createDirectories(parent);

            Path backup = null;
            if (Files.exists(manifest, LinkOption.NOFOLLOW_LINKS)) {
                if (!safeRegularFile(manifest)) {
                    return report(ProjectSchemaStatus.INVALID, before.detectedVersion(),
                            "KIDE_SCHEMA_MANIFEST_UNSAFE",
                            "Existing project schema manifest is not safe to back up", null);
                }
                Path backupDir = root.resolve(".kide/backups");
                if (!safeManagedParent(root, backupDir)) {
                    return report(ProjectSchemaStatus.INVALID, before.detectedVersion(),
                            "KIDE_SCHEMA_BACKUP_PATH_UNSAFE",
                            "Project schema backup path is unsafe", null);
                }
                Files.createDirectories(backupDir);
                backup = backupDir.resolve("schema-v" + before.detectedVersion() + ".properties");
                if (!Files.exists(backup, LinkOption.NOFOLLOW_LINKS)) {
                    Files.copy(manifest, backup);
                } else if (!safeRegularFile(backup)) {
                    return report(ProjectSchemaStatus.INVALID, before.detectedVersion(),
                            "KIDE_SCHEMA_BACKUP_UNSAFE",
                            "Existing project schema backup is unsafe", null);
                }
            }

            atomicWrite(root, manifest, currentProperties());
            ProjectSchemaReport after = inspect(root);
            if (!after.isCurrent()) {
                return report(ProjectSchemaStatus.IO_ERROR, before.detectedVersion(),
                        "KIDE_SCHEMA_POST_MIGRATION_INVALID",
                        "Migrated schema failed validation; project model files were not changed", backup);
            }
            return report(ProjectSchemaStatus.CURRENT, CURRENT_VERSION,
                    "KIDE_SCHEMA_MIGRATED",
                    "Project schema migration completed without rewriting model files", backup);
        } catch (IOException | RuntimeException e) {
            return report(ProjectSchemaStatus.IO_ERROR, before.detectedVersion(),
                    "KIDE_SCHEMA_MIGRATION_FAILED",
                    "Project schema migration failed safely", null);
        }
    }

    private static Properties currentProperties() {
        Properties properties = new Properties();
        properties.setProperty("schema.version", Integer.toString(CURRENT_VERSION));
        properties.setProperty("context.schema.version", "1");
        for (String language : LANGUAGE_IDS) {
            properties.setProperty("language." + language + ".schema.version", "1");
        }
        return properties;
    }

    private static String validateCurrent(Properties properties) {
        if (!"1".equals(properties.getProperty("context.schema.version"))) {
            return "Context schema compatibility metadata is missing or unsupported";
        }
        for (String language : LANGUAGE_IDS) {
            if (!"1".equals(properties.getProperty("language." + language + ".schema.version"))) {
                return "Language schema compatibility metadata is missing or unsupported for " + language;
            }
        }
        return null;
    }

    private static int parseVersion(String value) {
        if (value == null) return -1;
        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed < 0 ? -1 : parsed;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static Properties read(Path file) throws IOException {
        Properties properties = new Properties();
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return properties;
    }

    private static void atomicWrite(Path root, Path file, Properties properties) throws IOException {
        Path parent = file.getParent();
        if (!safeManagedParent(root, parent)) {
            throw new IOException("unsafe managed schema path");
        }
        Files.createDirectories(parent);
        Path temp = Files.createTempFile(parent, "schema-", ".tmp");
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                properties.store(writer, "KIDE project schema " + CURRENT_VERSION);
            }
            try {
                Files.move(temp, file, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    private static boolean safeRegularFile(Path file) {
        return file != null
                && Files.exists(file, LinkOption.NOFOLLOW_LINKS)
                && !Files.isSymbolicLink(file)
                && Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)
                && Files.isReadable(file);
    }

    private static boolean safeManagedParent(Path root, Path parent) {
        try {
            Path normalizedRoot = root.toAbsolutePath().normalize();
            Path normalizedParent = parent.toAbsolutePath().normalize();
            if (!normalizedParent.startsWith(normalizedRoot)) return false;
            Path cursor = normalizedRoot;
            for (Path segment : normalizedRoot.relativize(normalizedParent)) {
                cursor = cursor.resolve(segment);
                if (Files.exists(cursor, LinkOption.NOFOLLOW_LINKS)
                        && (Files.isSymbolicLink(cursor)
                        || !Files.isDirectory(cursor, LinkOption.NOFOLLOW_LINKS))) {
                    return false;
                }
            }
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static Path normalizeRoot(Path root) {
        try {
            return root == null ? null : root.toAbsolutePath().normalize();
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static ProjectSchemaReport report(ProjectSchemaStatus status, int detected,
            String code, String message, Path backup) {
        return new ProjectSchemaReport(status, detected, CURRENT_VERSION, code, message, backup);
    }
}
