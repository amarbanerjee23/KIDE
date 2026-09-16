package com.kide.enterprise.context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Resilient persistence for E04 enterprise identities and metadata.
 * Runtime boundary methods intentionally return diagnostics instead of propagating
 * malformed-file, filesystem or validation exceptions into the Eclipse workbench.
 */
public final class EnterpriseContextStore {
    public static final String PROJECT_DESCRIPTOR = ".kide/enterprise-context.properties";
    public static final String WORKSPACE_DESCRIPTOR = ".metadata/.plugins/com.kide.enterprise.context/workspace.properties";
    private static final String SCHEMA_KEY = "schema.version";
    private static final String SCHEMA_VALUE = "1";

    public EnterpriseContextResult load(Path workspaceRoot, Path projectRoot) {
        try {
            Optional<PathPair> pair = paths(workspaceRoot, projectRoot);
            if (pair.isEmpty()) {
                return issue(ContextStatus.INVALID, "E04_PATH_REQUIRED",
                        "Workspace and project locations must be local filesystem directories");
            }
            PathPair paths = pair.get();
            EnterpriseContextResult pathCheck = validateRoots(paths);
            if (pathCheck != null) {
                return pathCheck;
            }

            boolean projectExists = Files.exists(paths.projectFile, LinkOption.NOFOLLOW_LINKS);
            boolean workspaceExists = Files.exists(paths.workspaceFile, LinkOption.NOFOLLOW_LINKS);
            if (!projectExists && !workspaceExists) {
                return issue(ContextStatus.UNINITIALIZED, "E04_CONTEXT_UNINITIALIZED",
                        "Enterprise context has not been initialized for this project and workspace");
            }
            if (!projectExists && workspaceExists) {
                return issue(ContextStatus.INVALID, "E04_ORPHAN_WORKSPACE_BINDING",
                        "Workspace context exists but the project identity descriptor is missing");
            }

            ParseResult project = parseProject(paths.projectFile);
            if (!project.ok) {
                return project.result;
            }
            if (!workspaceExists) {
                return issue(ContextStatus.UNINITIALIZED, "E04_WORKSPACE_UNINITIALIZED",
                        "Project identity is valid but this Eclipse workspace has not been bound yet");
            }
            ParseResult workspace = parseWorkspace(paths.workspaceFile, project.organization, project.portfolio, project.project);
            if (!workspace.ok) {
                return workspace.result;
            }
            return ready(new EnterpriseContext(project.organization, project.portfolio, project.project, workspace.workspace));
        } catch (RuntimeException e) {
            return issue(ContextStatus.IO_ERROR, "E04_RUNTIME_GUARD",
                    "Enterprise context could not be loaded safely; no workbench state was changed");
        }
    }

    /**
     * Creates missing stable IDs or updates display names while preserving every
     * existing stable identity and metadata map.
     */
    public EnterpriseContextResult provision(Path workspaceRoot, Path projectRoot,
            String organizationName, String portfolioName, String projectName, String workspaceName) {
        try {
            Optional<PathPair> pair = paths(workspaceRoot, projectRoot);
            if (pair.isEmpty()) {
                return issue(ContextStatus.INVALID, "E04_PATH_REQUIRED",
                        "Workspace and project locations must be local filesystem directories");
            }
            EnterpriseContextResult validation = validateNames(organizationName, portfolioName, projectName, workspaceName);
            if (validation != null) {
                return validation;
            }

            EnterpriseContextResult current = load(workspaceRoot, projectRoot);
            EnterpriseNode organization;
            EnterpriseNode portfolio;
            EnterpriseNode project;
            EnterpriseNode workspace;

            if (current.isReady()) {
                EnterpriseContext existing = current.context().get();
                organization = renamed(existing.organization(), organizationName);
                portfolio = renamed(existing.portfolio(), portfolioName);
                project = renamed(existing.project(), projectName);
                workspace = renamed(existing.workspace(), workspaceName);
            } else if (current.status() == ContextStatus.UNINITIALIZED) {
                PathPair resolved = pair.get();
                if (Files.exists(resolved.projectFile, LinkOption.NOFOLLOW_LINKS)) {
                    ParseResult parsedProject = parseProject(resolved.projectFile);
                    if (!parsedProject.ok) {
                        return parsedProject.result;
                    }
                    organization = renamed(parsedProject.organization, organizationName);
                    portfolio = renamed(parsedProject.portfolio, portfolioName);
                    project = renamed(parsedProject.project, projectName);
                } else {
                    organization = new EnterpriseNode(EnterpriseId.create(EnterpriseScope.ORGANIZATION),
                            organizationName.trim(), Collections.emptyMap());
                    portfolio = new EnterpriseNode(EnterpriseId.create(EnterpriseScope.PORTFOLIO),
                            portfolioName.trim(), Collections.emptyMap());
                    project = new EnterpriseNode(EnterpriseId.create(EnterpriseScope.PROJECT),
                            projectName.trim(), Collections.emptyMap());
                }
                workspace = new EnterpriseNode(EnterpriseId.create(EnterpriseScope.WORKSPACE),
                        workspaceName.trim(), Collections.emptyMap());
            } else {
                return current;
            }

            return save(workspaceRoot, projectRoot, new EnterpriseContext(organization, portfolio, project, workspace));
        } catch (RuntimeException e) {
            return issue(ContextStatus.IO_ERROR, "E04_PROVISION_GUARD",
                    "Enterprise context could not be provisioned safely; existing identity files were not intentionally replaced");
        }
    }

    /** Updates metadata for one hierarchy level while retaining all IDs and names. */
    public EnterpriseContextResult updateMetadata(Path workspaceRoot, Path projectRoot,
            EnterpriseScope scope, Map<String, String> metadata) {
        try {
            if (scope == null) {
                return issue(ContextStatus.INVALID, "E04_SCOPE_REQUIRED", "Enterprise metadata scope must be specified");
            }
            Optional<String> invalid = EnterpriseContextRules.validateMetadata(metadata, scope);
            if (invalid.isPresent()) {
                return issue(ContextStatus.INVALID, "E04_METADATA_INVALID", invalid.get());
            }
            EnterpriseContextResult loaded = load(workspaceRoot, projectRoot);
            if (!loaded.isReady()) {
                return loaded;
            }
            EnterpriseContext current = loaded.context().get();
            EnterpriseNode replacement = new EnterpriseNode(current.node(scope).id(), current.node(scope).displayName(), metadata);
            EnterpriseContext updated = replace(current, scope, replacement);
            return save(workspaceRoot, projectRoot, updated);
        } catch (RuntimeException e) {
            return issue(ContextStatus.IO_ERROR, "E04_METADATA_GUARD",
                    "Enterprise metadata could not be updated safely");
        }
    }

    /** Persists an already validated context atomically with rollback of the project descriptor on paired-write failure. */
    public EnterpriseContextResult save(Path workspaceRoot, Path projectRoot, EnterpriseContext context) {
        try {
            if (context == null) {
                return issue(ContextStatus.INVALID, "E04_CONTEXT_REQUIRED", "Enterprise context must not be null");
            }
            Optional<PathPair> pair = paths(workspaceRoot, projectRoot);
            if (pair.isEmpty()) {
                return issue(ContextStatus.INVALID, "E04_PATH_REQUIRED",
                        "Workspace and project locations must be local filesystem directories");
            }
            EnterpriseContextResult pathCheck = validateRoots(pair.get());
            if (pathCheck != null && pathCheck.status() != ContextStatus.UNINITIALIZED) {
                return pathCheck;
            }
            EnterpriseContextResult contextCheck = validateContext(context);
            if (contextCheck != null) {
                return contextCheck;
            }

            PathPair paths = pair.get();
            byte[] previousProject = null;
            boolean hadProject = Files.exists(paths.projectFile, LinkOption.NOFOLLOW_LINKS);
            if (hadProject) {
                if (!safeRegularFile(paths.projectFile)) {
                    return issue(ContextStatus.INVALID, "E04_PROJECT_DESCRIPTOR_UNSAFE",
                            "Project identity descriptor is not a safe regular file");
                }
                if (Files.size(paths.projectFile) > EnterpriseContextRules.MAX_FILE_BYTES) {
                    return issue(ContextStatus.INVALID, "E04_PROJECT_DESCRIPTOR_TOO_LARGE",
                            "Project identity descriptor exceeds the supported size limit");
                }
                previousProject = Files.readAllBytes(paths.projectFile);
            }

            WriteResult projectWrite = atomicWrite(paths.projectRoot, paths.projectFile, projectProperties(context));
            if (!projectWrite.ok) {
                return projectWrite.result;
            }
            WriteResult workspaceWrite = atomicWrite(paths.workspaceRoot, paths.workspaceFile, workspaceProperties(context));
            if (!workspaceWrite.ok) {
                rollbackProject(paths.projectRoot, paths.projectFile, hadProject, previousProject);
                return workspaceWrite.result;
            }
            return ready(context);
        } catch (IOException | RuntimeException e) {
            return issue(ContextStatus.IO_ERROR, "E04_SAVE_FAILED",
                    "Enterprise identity metadata could not be saved safely");
        }
    }

    private static EnterpriseContextResult validateRoots(PathPair paths) {
        if (Files.exists(paths.projectRoot, LinkOption.NOFOLLOW_LINKS)
                && !Files.isDirectory(paths.projectRoot, LinkOption.NOFOLLOW_LINKS)) {
            return issue(ContextStatus.INVALID, "E04_PROJECT_ROOT_INVALID", "Project location is not a directory");
        }
        if (Files.exists(paths.workspaceRoot, LinkOption.NOFOLLOW_LINKS)
                && !Files.isDirectory(paths.workspaceRoot, LinkOption.NOFOLLOW_LINKS)) {
            return issue(ContextStatus.INVALID, "E04_WORKSPACE_ROOT_INVALID", "Workspace location is not a directory");
        }
        return null;
    }

    private static EnterpriseContextResult validateNames(String organizationName, String portfolioName,
            String projectName, String workspaceName) {
        String[] values = { organizationName, portfolioName, projectName, workspaceName };
        EnterpriseScope[] scopes = EnterpriseScope.values();
        for (int i = 0; i < scopes.length; i++) {
            Optional<String> invalid = EnterpriseContextRules.validateName(values[i], scopes[i]);
            if (invalid.isPresent()) {
                return issue(ContextStatus.INVALID, "E04_NAME_INVALID", invalid.get());
            }
        }
        return null;
    }

    private static EnterpriseContextResult validateContext(EnterpriseContext context) {
        for (EnterpriseScope scope : EnterpriseScope.values()) {
            EnterpriseNode node = context.node(scope);
            if (node.id().scope() != scope) {
                return issue(ContextStatus.INVALID, "E04_ID_SCOPE_MISMATCH", "Enterprise identity scope is inconsistent");
            }
            Optional<String> name = EnterpriseContextRules.validateName(node.displayName(), scope);
            if (name.isPresent()) {
                return issue(ContextStatus.INVALID, "E04_NAME_INVALID", name.get());
            }
            Optional<String> metadata = EnterpriseContextRules.validateMetadata(node.metadata(), scope);
            if (metadata.isPresent()) {
                return issue(ContextStatus.INVALID, "E04_METADATA_INVALID", metadata.get());
            }
        }
        return null;
    }

    private static ParseResult parseProject(Path file) {
        ReadResult read = read(file, "project");
        if (!read.ok) {
            return ParseResult.failure(read.result);
        }
        Properties properties = read.properties;
        EnterpriseContextResult schema = validateSchema(properties);
        if (schema != null) {
            return ParseResult.failure(schema);
        }
        EnterpriseNode organization = parseNode(properties, EnterpriseScope.ORGANIZATION);
        EnterpriseNode portfolio = parseNode(properties, EnterpriseScope.PORTFOLIO);
        EnterpriseNode project = parseNode(properties, EnterpriseScope.PROJECT);
        if (organization == null || portfolio == null || project == null) {
            return ParseResult.failure(issue(ContextStatus.INVALID, "E04_PROJECT_DESCRIPTOR_INVALID",
                    "Project identity descriptor contains missing or malformed IDs, names or metadata"));
        }
        return ParseResult.project(organization, portfolio, project);
    }

    private static ParseResult parseWorkspace(Path file, EnterpriseNode organization,
            EnterpriseNode portfolio, EnterpriseNode project) {
        ReadResult read = read(file, "workspace");
        if (!read.ok) {
            return ParseResult.failure(read.result);
        }
        Properties properties = read.properties;
        EnterpriseContextResult schema = validateSchema(properties);
        if (schema != null) {
            return ParseResult.failure(schema);
        }
        if (!matchesId(properties, EnterpriseScope.ORGANIZATION, organization.id())
                || !matchesId(properties, EnterpriseScope.PORTFOLIO, portfolio.id())
                || !matchesId(properties, EnterpriseScope.PROJECT, project.id())) {
            return ParseResult.failure(issue(ContextStatus.INVALID, "E04_BINDING_MISMATCH",
                    "Workspace identity is bound to a different organization, portfolio or project"));
        }
        EnterpriseNode workspace = parseNode(properties, EnterpriseScope.WORKSPACE);
        if (workspace == null) {
            return ParseResult.failure(issue(ContextStatus.INVALID, "E04_WORKSPACE_DESCRIPTOR_INVALID",
                    "Workspace identity descriptor contains missing or malformed identity metadata"));
        }
        return ParseResult.workspace(workspace);
    }

    private static ReadResult read(Path file, String label) {
        try {
            if (!safeRegularFile(file)) {
                return ReadResult.failure(issue(ContextStatus.INVALID, "E04_DESCRIPTOR_UNSAFE",
                        "Enterprise " + label + " descriptor is not a safe readable regular file"));
            }
            if (Files.size(file) > EnterpriseContextRules.MAX_FILE_BYTES) {
                return ReadResult.failure(issue(ContextStatus.INVALID, "E04_DESCRIPTOR_TOO_LARGE",
                        "Enterprise " + label + " descriptor exceeds the supported size limit"));
            }
            Properties properties = new Properties();
            try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
            return ReadResult.success(properties);
        } catch (IOException | RuntimeException e) {
            return ReadResult.failure(issue(ContextStatus.IO_ERROR, "E04_DESCRIPTOR_READ_FAILED",
                    "Enterprise " + label + " descriptor could not be read safely"));
        }
    }

    private static boolean safeRegularFile(Path file) {
        return file != null && Files.exists(file, LinkOption.NOFOLLOW_LINKS)
                && !Files.isSymbolicLink(file)
                && Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)
                && Files.isReadable(file);
    }

    private static EnterpriseContextResult validateSchema(Properties properties) {
        String schema = properties.getProperty(SCHEMA_KEY);
        if (!SCHEMA_VALUE.equals(schema == null ? null : schema.trim())) {
            return issue(ContextStatus.INVALID, "E04_SCHEMA_UNSUPPORTED",
                    "Enterprise context schema is missing or unsupported; automatic reinterpretation is refused");
        }
        return null;
    }

    private static EnterpriseNode parseNode(Properties properties, EnterpriseScope scope) {
        String prefix = scope.token() + ".";
        Optional<EnterpriseId> id = EnterpriseId.tryParse(scope, properties.getProperty(prefix + "id"));
        String name = properties.getProperty(prefix + "name");
        Map<String, String> metadata = metadata(properties, prefix + "meta.");
        if (id.isEmpty() || EnterpriseContextRules.validateName(name, scope).isPresent()
                || EnterpriseContextRules.validateMetadata(metadata, scope).isPresent()) {
            return null;
        }
        return new EnterpriseNode(id.get(), name.trim(), metadata);
    }

    private static boolean matchesId(Properties properties, EnterpriseScope scope, EnterpriseId expected) {
        return EnterpriseId.tryParse(scope, properties.getProperty(scope.token() + ".id"))
                .map(expected::equals).orElse(false);
    }

    private static Map<String, String> metadata(Properties properties, String prefix) {
        Map<String, String> metadata = new LinkedHashMap<>();
        List<String> names = new ArrayList<>(properties.stringPropertyNames());
        Collections.sort(names);
        for (String key : names) {
            if (key.startsWith(prefix)) {
                metadata.put(key.substring(prefix.length()), properties.getProperty(key));
            }
        }
        return metadata;
    }

    private static Properties projectProperties(EnterpriseContext context) {
        Properties properties = new Properties();
        properties.setProperty(SCHEMA_KEY, SCHEMA_VALUE);
        writeNode(properties, context.organization(), true);
        writeNode(properties, context.portfolio(), true);
        writeNode(properties, context.project(), true);
        return properties;
    }

    private static Properties workspaceProperties(EnterpriseContext context) {
        Properties properties = new Properties();
        properties.setProperty(SCHEMA_KEY, SCHEMA_VALUE);
        properties.setProperty("organization.id", context.organization().id().value());
        properties.setProperty("portfolio.id", context.portfolio().id().value());
        properties.setProperty("project.id", context.project().id().value());
        writeNode(properties, context.workspace(), true);
        return properties;
    }

    private static void writeNode(Properties properties, EnterpriseNode node, boolean includeName) {
        String prefix = node.scope().token() + ".";
        properties.setProperty(prefix + "id", node.id().value());
        if (includeName) {
            properties.setProperty(prefix + "name", node.displayName());
        }
        node.metadata().entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEach(entry -> properties.setProperty(prefix + "meta." + entry.getKey(), entry.getValue()));
    }

    private static WriteResult atomicWrite(Path root, Path file, Properties properties) {
        try {
            Path parent = file.getParent();
            if (!safeManagedParent(root, parent)) {
                return WriteResult.failure(issue(ContextStatus.INVALID, "E04_MANAGED_PATH_UNSAFE",
                        "Enterprise identity storage path contains an unsafe symbolic link or non-directory"));
            }
            Files.createDirectories(parent);
            if (Files.exists(file, LinkOption.NOFOLLOW_LINKS) && Files.isSymbolicLink(file)) {
                return WriteResult.failure(issue(ContextStatus.INVALID, "E04_DESCRIPTOR_SYMLINK",
                        "Enterprise identity descriptors may not be symbolic links"));
            }
            Path temp = Files.createTempFile(parent, file.getFileName().toString(), ".tmp");
            try {
                try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                    properties.store(writer, "KIDE enterprise context schema " + SCHEMA_VALUE);
                }
                moveReplace(temp, file);
            } finally {
                Files.deleteIfExists(temp);
            }
            return WriteResult.success();
        } catch (IOException | RuntimeException e) {
            return WriteResult.failure(issue(ContextStatus.IO_ERROR, "E04_DESCRIPTOR_WRITE_FAILED",
                    "Enterprise identity descriptor could not be written atomically"));
        }
    }

    private static boolean safeManagedParent(Path root, Path parent) {
        try {
            Path normalizedRoot = root.toAbsolutePath().normalize();
            Path normalizedParent = parent.toAbsolutePath().normalize();
            if (!normalizedParent.startsWith(normalizedRoot)) {
                return false;
            }
            Path cursor = normalizedRoot;
            Path relative = normalizedRoot.relativize(normalizedParent);
            for (Path segment : relative) {
                cursor = cursor.resolve(segment);
                if (Files.exists(cursor, LinkOption.NOFOLLOW_LINKS)) {
                    if (Files.isSymbolicLink(cursor) || !Files.isDirectory(cursor, LinkOption.NOFOLLOW_LINKS)) {
                        return false;
                    }
                }
            }
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static void moveReplace(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void rollbackProject(Path root, Path file, boolean existed, byte[] previous) {
        try {
            if (!existed) {
                Files.deleteIfExists(file);
                return;
            }
            Path parent = file.getParent();
            if (!safeManagedParent(root, parent)) {
                return;
            }
            Path temp = Files.createTempFile(parent, file.getFileName().toString(), ".rollback");
            try {
                Files.write(temp, previous);
                moveReplace(temp, file);
            } finally {
                Files.deleteIfExists(temp);
            }
        } catch (IOException | RuntimeException ignored) {
            // The caller already returns IO_ERROR. Never throw a secondary rollback failure into Eclipse.
        }
    }

    private static EnterpriseNode renamed(EnterpriseNode node, String newName) {
        return new EnterpriseNode(node.id(), newName.trim(), node.metadata());
    }

    private static EnterpriseContext replace(EnterpriseContext context, EnterpriseScope scope, EnterpriseNode replacement) {
        switch (scope) {
        case ORGANIZATION:
            return new EnterpriseContext(replacement, context.portfolio(), context.project(), context.workspace());
        case PORTFOLIO:
            return new EnterpriseContext(context.organization(), replacement, context.project(), context.workspace());
        case PROJECT:
            return new EnterpriseContext(context.organization(), context.portfolio(), replacement, context.workspace());
        case WORKSPACE:
            return new EnterpriseContext(context.organization(), context.portfolio(), context.project(), replacement);
        default:
            return context;
        }
    }

    private static Optional<PathPair> paths(Path workspaceRoot, Path projectRoot) {
        try {
            if (workspaceRoot == null || projectRoot == null) {
                return Optional.empty();
            }
            Path workspace = workspaceRoot.toAbsolutePath().normalize();
            Path project = projectRoot.toAbsolutePath().normalize();
            return Optional.of(new PathPair(workspace, project,
                    project.resolve(PROJECT_DESCRIPTOR), workspace.resolve(WORKSPACE_DESCRIPTOR)));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    private static EnterpriseContextResult ready(EnterpriseContext context) {
        return new EnterpriseContextResult(ContextStatus.READY, context, Collections.emptyList());
    }

    private static EnterpriseContextResult issue(ContextStatus status, String code, String message) {
        return new EnterpriseContextResult(status, null,
                Collections.singletonList(new ContextDiagnostic(code, message)));
    }

    private static final class PathPair {
        final Path workspaceRoot;
        final Path projectRoot;
        final Path projectFile;
        final Path workspaceFile;

        PathPair(Path workspaceRoot, Path projectRoot, Path projectFile, Path workspaceFile) {
            this.workspaceRoot = workspaceRoot;
            this.projectRoot = projectRoot;
            this.projectFile = projectFile;
            this.workspaceFile = workspaceFile;
        }
    }

    private static final class ReadResult {
        final boolean ok;
        final Properties properties;
        final EnterpriseContextResult result;

        private ReadResult(boolean ok, Properties properties, EnterpriseContextResult result) {
            this.ok = ok;
            this.properties = properties;
            this.result = result;
        }

        static ReadResult success(Properties properties) {
            return new ReadResult(true, properties, null);
        }

        static ReadResult failure(EnterpriseContextResult result) {
            return new ReadResult(false, null, result);
        }
    }

    private static final class ParseResult {
        final boolean ok;
        final EnterpriseNode organization;
        final EnterpriseNode portfolio;
        final EnterpriseNode project;
        final EnterpriseNode workspace;
        final EnterpriseContextResult result;

        private ParseResult(boolean ok, EnterpriseNode organization, EnterpriseNode portfolio,
                EnterpriseNode project, EnterpriseNode workspace, EnterpriseContextResult result) {
            this.ok = ok;
            this.organization = organization;
            this.portfolio = portfolio;
            this.project = project;
            this.workspace = workspace;
            this.result = result;
        }

        static ParseResult project(EnterpriseNode organization, EnterpriseNode portfolio, EnterpriseNode project) {
            return new ParseResult(true, organization, portfolio, project, null, null);
        }

        static ParseResult workspace(EnterpriseNode workspace) {
            return new ParseResult(true, null, null, null, workspace, null);
        }

        static ParseResult failure(EnterpriseContextResult result) {
            return new ParseResult(false, null, null, null, null, result);
        }
    }

    private static final class WriteResult {
        final boolean ok;
        final EnterpriseContextResult result;

        private WriteResult(boolean ok, EnterpriseContextResult result) {
            this.ok = ok;
            this.result = result;
        }

        static WriteResult success() {
            return new WriteResult(true, null);
        }

        static WriteResult failure(EnterpriseContextResult result) {
            return new WriteResult(false, result);
        }
    }
}
