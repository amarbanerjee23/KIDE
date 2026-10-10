package com.kide.enterprise.server;

import java.nio.file.Path;
import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.kide.codegen.ProjectGenerationService;
import com.kide.enterprise.authorization.ServerAuthorizationGate;
import com.kide.enterprise.authorization.AccessDeniedException;
import com.kide.enterprise.context.FileHostedProjectRegistry;
import com.kide.enterprise.context.HostedProjectOwnership;
import com.kide.enterprise.context.HostedProjectRegistration;
import com.kide.enterprise.identity.AuthenticatedSession;
import com.kide.enterprise.modelrepo.FileModelRepository;
import com.kide.knowledge.EmbeddedKnowledgeRepository;
import com.kide.knowledge.KnowledgeRepository;
import com.kide.knowledge.KnowledgeTraceStore;
import com.kide.knowledge.ProjectKnowledgeService;
import com.kide.synthesis.ProjectSynthesisService;

/**
 * Registry-aware REST service catalog. Every request revalidates project identity
 * against the on-disk registry; repository/collaboration services are cached by
 * the immutable project identity and canonical project root.
 */
public final class HostedApiProjectCatalog {
    private static final int MAX_PROJECTS_PER_CREATOR = 10;
    private final FileHostedProjectRegistry registry;
    private final ServerAuthorizationGate authorization;
    private final Clock clock;
    private final boolean bootstrapStarterKnowledge;
    private final ConcurrentMap<String, HostedApiProjectRuntime> runtimes =
            new ConcurrentHashMap<>();

    public HostedApiProjectCatalog(
            FileHostedProjectRegistry registry,
            ServerAuthorizationGate authorization, Clock clock) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.authorization = Objects.requireNonNull(authorization, "authorization");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public List<HostedApiProjectRuntime> list(AuthenticatedSession session) {
        // Authorize against cheap context metadata before constructing any
        // per-tenant repository or seeding its knowledge data.
        session.requireActive();
        return registry.list().stream()
                .filter(registration -> canRead(session, registration))
                .map(this::runtime).toList();
    }

    public Optional<HostedApiProjectRuntime> resolve(
            String projectId, AuthenticatedSession session) {
        session.requireActive();
        return registry.resolveProject(projectId).map(registration -> {
            authorization.requireApiProjectAccess(session, registration.context());
            return runtime(registration);
        });
    }

    private boolean canRead(
            AuthenticatedSession session, HostedProjectRegistration registration) {
        try {
            authorization.requireApiProjectAccess(session, registration.context());
            return true;
        } catch (AccessDeniedException denied) {
            return false;
        }
    }

    public synchronized HostedApiProjectRuntime create(
            AuthenticatedSession session, String displayName) {
        Objects.requireNonNull(session, "session");
        session.requireActive();
        String principal = session.principal().id();
        if (registry.list().stream().filter(project ->
                principal.equals(HostedProjectOwnership.principalId(project))).count()
                >= MAX_PROJECTS_PER_CREATOR) {
            throw new QuotaExceededException();
        }
        if (displayName == null || displayName.isBlank() || displayName.length() > 128) {
            throw new IllegalArgumentException("displayName must be 1 to 128 characters");
        }
        try {
            HostedProjectRegistration created =
                    registry.createProject(displayName, principal);
            return runtime(created);
        } catch (IllegalStateException unavailable) {
            throw new ProvisioningUnavailableException();
        }
    }

    public static final class QuotaExceededException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    public static final class ProvisioningUnavailableException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private HostedApiProjectRuntime runtime(HostedProjectRegistration registration) {
        String id = registration.context().project().id().value();
        Path root = registration.projectRoot();
        HostedApiProjectRuntime existing = runtimes.get(id);
        if (existing != null) {
            // Reject replacing the project backing a live identity.
            if (!existing.context().workspace().id().equals(
                    registration.context().workspace().id())) {
                throw new IllegalStateException("Hosted project identity changed in the registry");
            }
            return existing;
        }
        return runtimes.computeIfAbsent(id, key -> {
            FileModelRepository models = new FileModelRepository(root);
            EmbeddedKnowledgeRepository knowledge = new EmbeddedKnowledgeRepository(root);
            if (bootstrapStarterKnowledge && knowledge.snapshot().isEmpty()) {
                knowledge.replace(
                        EnterpriseApiApplication.starterKnowledge(registration.context()),
                        KnowledgeRepository.MISSING_ETAG);
            }
            ProjectSynthesisService synthesis =
                    new ProjectSynthesisService(root, models, knowledge);
            return new HostedApiProjectRuntime(
                    registration.context(),
                    authorization,
                    models,
                    new ProjectCollaborationService(root, models, clock),
                    new ProjectKnowledgeService(
                            knowledge, new KnowledgeTraceStore(root, clock), models),
                    synthesis,
                    new ProjectGenerationService(root, models, knowledge, synthesis));
        });
    }
}
