package com.kide.enterprise.context;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * File-backed hosted-project registry.
 *
 * Layout:
 *   <root>/<project-uuid>/project/
 *   <root>/<project-uuid>/workspace/
 *
 * The enterprise descriptors inside those roots remain the source of truth.
 * Directory names are only stable lookup slots and must equal the project UUID.
 */
public final class FileHostedProjectRegistry implements HostedProjectRegistry {
    public static final String PROJECT_DIR = "project";
    public static final String WORKSPACE_DIR = "workspace";
    public static final int MAX_PROJECTS = 512;

    private final Path root;
    private final Path rootReal;
    private final EnterpriseContextStore contexts;

    public FileHostedProjectRegistry(Path root) {
        this(root, new EnterpriseContextStore());
    }

    FileHostedProjectRegistry(Path root, EnterpriseContextStore contexts) {
        this.root = requireSafeRoot(root);
        this.contexts = java.util.Objects.requireNonNull(contexts, "contexts");
        try {
            this.rootReal = this.root.toRealPath();
        } catch (IOException e) {
            throw new IllegalArgumentException("hosted project registry cannot be canonicalized", e);
        }
    }

    @Override
    public List<HostedProjectRegistration> list() {
        List<HostedProjectRegistration> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            for (Path slot : stream) {
                if (Files.isSymbolicLink(slot)
                        || !Files.isDirectory(slot, LinkOption.NOFOLLOW_LINKS)) {
                    throw new IllegalStateException(
                            "hosted project registry contains a non-directory or symbolic-link entry");
                }
                result.add(loadSlot(slot));
                if (result.size() > MAX_PROJECTS) {
                    throw new IllegalStateException(
                            "hosted project registry exceeds the supported project limit");
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("hosted project registry could not be read", e);
        }

        result.sort(Comparator.comparing(
                item -> item.context().project().id().value()));
        validateUnique(result);
        return List.copyOf(result);
    }

    @Override
    public Optional<HostedProjectRegistration> resolveProject(String projectId) {
        String id = normalizedId(projectId);
        if (id == null) return Optional.empty();
        return list().stream()
                .filter(item -> item.context().project().id().value().equals(id))
                .findFirst();
    }

    @Override
    public Optional<HostedProjectRegistration> resolveWorkspace(String workspaceId) {
        String id = normalizedId(workspaceId);
        if (id == null) return Optional.empty();
        return list().stream()
                .filter(item -> item.context().workspace().id().value().equals(id))
                .findFirst();
    }

    private HostedProjectRegistration loadSlot(Path slot) {
        String slotName = slot.getFileName().toString();
        if (!slotName.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
            throw new IllegalStateException(
                    "hosted project registry slot name is not a canonical project UUID");
        }

        Path normalizedSlot = slot.toAbsolutePath().normalize();
        Path projectRoot = normalizedSlot.resolve(PROJECT_DIR).normalize();
        Path workspaceRoot = normalizedSlot.resolve(WORKSPACE_DIR).normalize();
        requireContainedDirectory(projectRoot);
        requireContainedDirectory(workspaceRoot);

        EnterpriseContextResult loaded = contexts.load(workspaceRoot, projectRoot);
        if (!loaded.isReady()) {
            throw new IllegalStateException(
                    "hosted project registry contains an invalid enterprise context: "
                    + loaded.summary());
        }
        EnterpriseContext context = loaded.context().orElseThrow();
        if (!context.project().id().uuid().toString().equals(slotName)) {
            throw new IllegalStateException(
                    "hosted project registry slot does not match the project identity");
        }

        return new HostedProjectRegistration(context, projectRoot, workspaceRoot);
    }

    private void requireContainedDirectory(Path candidate) {
        if (Files.isSymbolicLink(candidate)
                || !Files.isDirectory(candidate, LinkOption.NOFOLLOW_LINKS)) {
            throw new IllegalStateException(
                    "hosted project registry project/workspace roots must be real directories");
        }
        try {
            Path real = candidate.toRealPath();
            if (!real.startsWith(rootReal)) {
                throw new IllegalStateException(
                        "hosted project registry root escapes the configured registry");
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "hosted project registry root cannot be canonicalized", e);
        }
    }

    private static void validateUnique(List<HostedProjectRegistration> registrations) {
        Set<String> projectIds = new HashSet<>();
        Set<String> workspaceIds = new HashSet<>();
        for (HostedProjectRegistration registration : registrations) {
            if (!projectIds.add(registration.context().project().id().value())) {
                throw new IllegalStateException("duplicate hosted project identity");
            }
            if (!workspaceIds.add(registration.context().workspace().id().value())) {
                throw new IllegalStateException("duplicate hosted workspace identity");
            }
        }
    }

    private static String normalizedId(String raw) {
        if (raw == null) return null;
        String value = raw.trim();
        return value.isEmpty() ? null : value;
    }

    private static Path requireSafeRoot(Path root) {
        if (root == null) {
            throw new IllegalArgumentException("hosted project registry root is required");
        }
        Path normalized = root.toAbsolutePath().normalize();
        try {
            Files.createDirectories(normalized);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "hosted project registry root could not be created", e);
        }
        if (Files.isSymbolicLink(normalized)
                || !Files.isDirectory(normalized, LinkOption.NOFOLLOW_LINKS)) {
            throw new IllegalArgumentException(
                    "hosted project registry root must be a real directory");
        }
        return normalized;
    }
}
