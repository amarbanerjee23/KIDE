package com.kide.enterprise.server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Clock;
import java.time.Instant;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kide.enterprise.authorization.AccessDeniedException;
import com.kide.enterprise.identity.PrincipalIdentity;
import com.kide.enterprise.modelrepo.ModelPath;
import com.kide.enterprise.modelrepo.ModelRepository;
import com.kide.enterprise.modelrepo.ModelSnapshot;
import com.kide.enterprise.modelrepo.ModelTransaction;
import com.kide.enterprise.modelrepo.RepositoryLimits;
import com.kide.enterprise.modelrepo.RevisionConflictException;

/**
 * Project-scoped collaboration state.
 *
 * Canonical engineering models remain in ModelRepository/project files. Presence
 * is deliberately ephemeral; review change sets and comments are auxiliary
 * metadata persisted under .kide/collaboration so browser disconnect/rejoin does
 * not lose review work.
 */
public final class ProjectCollaborationService {
    private static final Duration PRESENCE_TTL = Duration.ofSeconds(90);
    private static final int MAX_PRESENCE = 256;
    private static final int MAX_CHANGESETS = 1000;
    private static final int MAX_COMMENTS = 10000;
    private static final int MAX_COMMENT_CHARS = 8000;
    private static final int MAX_ANCHOR_CHARS = 512;
    private static final long MAX_STATE_BYTES = 32L * 1024L * 1024L;
    private static final String MISSING_ETAG = "0".repeat(64);

    public enum ChangeSetStatus {
        DRAFT,
        CONFLICT,
        READY,
        APPROVED,
        APPLIED
    }

    public record PresenceSession(
            String id,
            String principalId,
            String displayName,
            String modelId,
            String joinedAt,
            String lastSeenAt) { }

    public record ChangeSet(
            String id,
            String modelId,
            String baseEtag,
            String baseRevision,
            String proposedContent,
            String mediaType,
            String authorId,
            String authorName,
            ChangeSetStatus status,
            String createdAt,
            String updatedAt,
            long reviewRevision,
            String approvedBy,
            String approvedAt,
            String appliedEtag,
            String appliedRevision) { }

    public record ReviewComment(
            String id,
            String changeSetId,
            String authorId,
            String authorName,
            String body,
            String anchor,
            String createdAt,
            boolean resolved,
            String resolvedBy,
            String resolvedAt) { }

    public record AppliedChange(ChangeSet changeSet, ModelSnapshot model) { }

    private record PersistedState(List<ChangeSet> changeSets, List<ReviewComment> comments) { }

    private final Path projectRoot;
    private final Path projectRootReal;
    private final Path stateFile;
    private final ModelRepository models;
    private final Clock clock;
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private final Map<String, PresenceSession> presence = new LinkedHashMap<>();
    private final Map<String, ChangeSet> changeSets = new LinkedHashMap<>();
    private final Map<String, ReviewComment> comments = new LinkedHashMap<>();

    public ProjectCollaborationService(Path projectRoot, ModelRepository models, Clock clock) {
        try {
            this.projectRoot = Objects.requireNonNull(projectRoot, "projectRoot")
                    .toAbsolutePath().normalize();
            this.models = Objects.requireNonNull(models, "models");
            this.clock = Objects.requireNonNull(clock, "clock");
            if (!Files.isDirectory(this.projectRoot, LinkOption.NOFOLLOW_LINKS)
                    || Files.isSymbolicLink(this.projectRoot)) {
                throw new IllegalArgumentException(
                        "collaboration project root must be a safe directory");
            }
            this.projectRootReal = this.projectRoot.toRealPath();
            Path collaborationRoot = this.projectRoot.resolve(".kide/collaboration").normalize();
            if (!collaborationRoot.startsWith(this.projectRoot)) {
                throw new IllegalArgumentException("collaboration metadata path escapes project");
            }
            ensureNoSymlink(this.projectRoot.resolve(".kide"));
            ensureNoSymlink(collaborationRoot);
            Files.createDirectories(collaborationRoot);
            if (!collaborationRoot.toRealPath().startsWith(projectRootReal)) {
                throw new IllegalArgumentException("collaboration metadata escapes project");
            }
            this.stateFile = collaborationRoot.resolve("review-state-v1.json");
            load();
        } catch (IOException e) {
            throw new IllegalStateException("collaboration state could not be initialized", e);
        }
    }

    public synchronized PresenceSession join(
            PrincipalIdentity principal, String requestedSessionId, String modelId) {
        Objects.requireNonNull(principal, "principal");
        prunePresence();
        String id = normalizeSessionId(requestedSessionId);
        if (id == null) id = UUID.randomUUID().toString();

        PresenceSession existing = presence.get(id);
        String now = now();
        if (existing != null) {
            if (!existing.principalId().equals(principal.id())) {
                throw new AccessDeniedException("collaboration session belongs to another principal");
            }
            PresenceSession refreshed = new PresenceSession(
                    existing.id(), existing.principalId(), principal.displayName(),
                    normalizeOptionalModelId(modelId), existing.joinedAt(), now);
            presence.put(id, refreshed);
            return refreshed;
        }
        if (presence.size() >= MAX_PRESENCE) {
            throw new IllegalStateException("collaboration presence capacity reached");
        }
        PresenceSession created = new PresenceSession(
                id, principal.id(), principal.displayName(),
                normalizeOptionalModelId(modelId), now, now);
        presence.put(id, created);
        return created;
    }

    public synchronized PresenceSession heartbeat(
            PrincipalIdentity principal, String sessionId, String modelId) {
        Objects.requireNonNull(principal, "principal");
        prunePresence();
        String id = requiredUuid(sessionId, "sessionId");
        PresenceSession existing = presence.get(id);
        if (existing == null) throw new NotFoundException();
        if (!existing.principalId().equals(principal.id())) {
            throw new AccessDeniedException("collaboration session belongs to another principal");
        }
        PresenceSession refreshed = new PresenceSession(
                existing.id(), existing.principalId(), existing.displayName(),
                normalizeOptionalModelId(modelId), existing.joinedAt(), now());
        presence.put(id, refreshed);
        return refreshed;
    }

    public synchronized boolean leave(PrincipalIdentity principal, String sessionId) {
        Objects.requireNonNull(principal, "principal");
        String id = requiredUuid(sessionId, "sessionId");
        PresenceSession existing = presence.get(id);
        if (existing == null) return false;
        if (!existing.principalId().equals(principal.id())) {
            throw new AccessDeniedException("collaboration session belongs to another principal");
        }
        presence.remove(id);
        return true;
    }

    public synchronized List<PresenceSession> listPresence() {
        prunePresence();
        return presence.values().stream()
                .sorted(Comparator.comparing(PresenceSession::joinedAt)
                        .thenComparing(PresenceSession::id))
                .toList();
    }

    public synchronized ChangeSet createChangeSet(
            PrincipalIdentity principal,
            String modelId,
            String baseEtag,
            String proposedContent,
            String mediaType) {
        Objects.requireNonNull(principal, "principal");
        if (changeSets.size() >= MAX_CHANGESETS) {
            throw new IllegalStateException("collaboration change-set capacity reached");
        }
        ModelPath path = new ModelPath(required(modelId, "modelId"));
        ModelSnapshot current = models.read(path).orElseThrow(NotFoundException::new);
        String expected = etag(baseEtag);
        String proposal = content(proposedContent);
        String now = now();
        ChangeSetStatus status = expected.equals(current.revision().etag())
                ? ChangeSetStatus.DRAFT : ChangeSetStatus.CONFLICT;
        ChangeSet created = new ChangeSet(
                UUID.randomUUID().toString(),
                path.value(),
                expected,
                Long.toString(current.revision().version()),
                proposal,
                optional(mediaType, "text/plain", 128),
                principal.id(),
                principal.displayName(),
                status,
                now,
                now,
                1L,
                "",
                "",
                "",
                "");
        changeSets.put(created.id(), created);
        persist();
        return created;
    }

    public synchronized List<ChangeSet> listChangeSets() {
        return changeSets.values().stream()
                .sorted(Comparator.comparing(ChangeSet::updatedAt).reversed()
                        .thenComparing(ChangeSet::id))
                .toList();
    }

    public synchronized Optional<ChangeSet> findChangeSet(String id) {
        return Optional.ofNullable(changeSets.get(requiredUuid(id, "changeSetId")));
    }

    public synchronized ModelSnapshot currentModel(String changeSetId) {
        ChangeSet set = requireChangeSet(changeSetId);
        return models.read(new ModelPath(set.modelId())).orElseThrow(NotFoundException::new);
    }

    public synchronized ChangeSet rebase(
            PrincipalIdentity principal,
            String changeSetId,
            String expectedCurrentEtag,
            String proposedContent) {
        Objects.requireNonNull(principal, "principal");
        ChangeSet existing = requireChangeSet(changeSetId);
        requireAuthor(principal, existing);
        requireMutable(existing);
        ModelSnapshot current = models.read(new ModelPath(existing.modelId()))
                .orElseThrow(NotFoundException::new);
        String expected = etag(expectedCurrentEtag);
        if (!expected.equals(current.revision().etag())) {
            throw new RevisionConflictException(
                    "stale collaboration rebase for " + existing.modelId());
        }
        ChangeSet updated = new ChangeSet(
                existing.id(),
                existing.modelId(),
                expected,
                Long.toString(current.revision().version()),
                content(proposedContent),
                existing.mediaType(),
                existing.authorId(),
                existing.authorName(),
                ChangeSetStatus.DRAFT,
                existing.createdAt(),
                now(),
                existing.reviewRevision() + 1L,
                "",
                "",
                "",
                "");
        changeSets.put(updated.id(), updated);
        persist();
        return updated;
    }

    public synchronized ChangeSet markReady(
            PrincipalIdentity principal, String changeSetId) {
        Objects.requireNonNull(principal, "principal");
        ChangeSet existing = requireChangeSet(changeSetId);
        requireAuthor(principal, existing);
        if (existing.status() != ChangeSetStatus.DRAFT) {
            throw new IllegalArgumentException("only draft change sets can be marked ready");
        }
        if (commentsFor(existing.id()).stream().anyMatch(comment -> !comment.resolved())) {
            throw new IllegalArgumentException(
                    "unresolved review comments must be resolved before marking ready");
        }
        ChangeSet updated = copyStatus(
                existing, ChangeSetStatus.READY, "", "", "", "");
        changeSets.put(updated.id(), updated);
        persist();
        return updated;
    }

    public synchronized ChangeSet approve(
            PrincipalIdentity principal, String changeSetId) {
        Objects.requireNonNull(principal, "principal");
        ChangeSet existing = requireChangeSet(changeSetId);
        if (existing.status() != ChangeSetStatus.READY) {
            throw new IllegalArgumentException("only ready change sets can be approved");
        }
        if (existing.authorId().equals(principal.id())) {
            throw new AccessDeniedException("independent review approval is required");
        }
        if (commentsFor(existing.id()).stream().anyMatch(comment -> !comment.resolved())) {
            throw new IllegalArgumentException(
                    "unresolved review comments must be resolved before approval");
        }
        ChangeSet updated = copyStatus(
                existing, ChangeSetStatus.APPROVED,
                principal.id(), now(), "", "");
        changeSets.put(updated.id(), updated);
        persist();
        return updated;
    }

    public synchronized AppliedChange apply(
            PrincipalIdentity principal, String changeSetId) {
        Objects.requireNonNull(principal, "principal");
        ChangeSet existing = requireChangeSet(changeSetId);
        if (existing.status() != ChangeSetStatus.APPROVED) {
            throw new IllegalArgumentException("change set must be approved before apply");
        }

        ModelPath path = new ModelPath(existing.modelId());
        try (ModelTransaction tx = models.beginTransaction()) {
            tx.write(path, existing.proposedContent().getBytes(StandardCharsets.UTF_8),
                    existing.baseEtag());
            tx.commit();
        }
        ModelSnapshot saved = models.read(path).orElseThrow(NotFoundException::new);
        ChangeSet updated = copyStatus(
                existing, ChangeSetStatus.APPLIED,
                existing.approvedBy(), existing.approvedAt(),
                saved.revision().etag(), Long.toString(saved.revision().version()));
        changeSets.put(updated.id(), updated);
        persist();
        return new AppliedChange(updated, saved);
    }

    public synchronized ReviewComment addComment(
            PrincipalIdentity principal,
            String changeSetId,
            String body,
            String anchor) {
        Objects.requireNonNull(principal, "principal");
        ChangeSet set = requireChangeSet(changeSetId);
        if (set.status() == ChangeSetStatus.APPLIED) {
            throw new IllegalArgumentException("applied change sets are immutable");
        }
        if (comments.size() >= MAX_COMMENTS) {
            throw new IllegalStateException("collaboration comment capacity reached");
        }
        ReviewComment comment = new ReviewComment(
                UUID.randomUUID().toString(),
                set.id(),
                principal.id(),
                principal.displayName(),
                boundedRequired(body, "body", MAX_COMMENT_CHARS),
                optional(anchor, "", MAX_ANCHOR_CHARS),
                now(),
                false,
                "",
                "");
        comments.put(comment.id(), comment);
        persist();
        return comment;
    }

    public synchronized List<ReviewComment> comments(String changeSetId) {
        ChangeSet set = requireChangeSet(changeSetId);
        return commentsFor(set.id());
    }

    public synchronized ReviewComment resolveComment(
            PrincipalIdentity principal,
            String changeSetId,
            String commentId,
            boolean resolved) {
        Objects.requireNonNull(principal, "principal");
        ChangeSet set = requireChangeSet(changeSetId);
        ReviewComment existing = comments.get(requiredUuid(commentId, "commentId"));
        if (existing == null || !existing.changeSetId().equals(set.id())) {
            throw new NotFoundException();
        }
        ReviewComment updated = new ReviewComment(
                existing.id(),
                existing.changeSetId(),
                existing.authorId(),
                existing.authorName(),
                existing.body(),
                existing.anchor(),
                existing.createdAt(),
                resolved,
                resolved ? principal.id() : "",
                resolved ? now() : "");
        comments.put(updated.id(), updated);
        persist();
        return updated;
    }

    private ChangeSet requireChangeSet(String id) {
        ChangeSet set = changeSets.get(requiredUuid(id, "changeSetId"));
        if (set == null) throw new NotFoundException();
        return set;
    }

    private List<ReviewComment> commentsFor(String changeSetId) {
        return comments.values().stream()
                .filter(comment -> comment.changeSetId().equals(changeSetId))
                .sorted(Comparator.comparing(ReviewComment::createdAt)
                        .thenComparing(ReviewComment::id))
                .toList();
    }

    private void requireAuthor(PrincipalIdentity principal, ChangeSet set) {
        if (!set.authorId().equals(principal.id())) {
            throw new AccessDeniedException("only the change-set author may modify the proposal");
        }
    }

    private static void requireMutable(ChangeSet set) {
        if (set.status() == ChangeSetStatus.APPLIED) {
            throw new IllegalArgumentException("applied change sets are immutable");
        }
    }

    private ChangeSet copyStatus(
            ChangeSet existing,
            ChangeSetStatus status,
            String approvedBy,
            String approvedAt,
            String appliedEtag,
            String appliedRevision) {
        return new ChangeSet(
                existing.id(),
                existing.modelId(),
                existing.baseEtag(),
                existing.baseRevision(),
                existing.proposedContent(),
                existing.mediaType(),
                existing.authorId(),
                existing.authorName(),
                status,
                existing.createdAt(),
                now(),
                existing.reviewRevision() + 1L,
                approvedBy,
                approvedAt,
                appliedEtag,
                appliedRevision);
    }

    private void prunePresence() {
        Instant threshold = Instant.now(clock).minus(PRESENCE_TTL);
        presence.entrySet().removeIf(entry ->
                Instant.parse(entry.getValue().lastSeenAt()).isBefore(threshold));
    }

    private void load() throws IOException {
        if (!Files.exists(stateFile, LinkOption.NOFOLLOW_LINKS)) return;
        if (!Files.isRegularFile(stateFile, LinkOption.NOFOLLOW_LINKS)
                || Files.isSymbolicLink(stateFile)
                || Files.size(stateFile) > MAX_STATE_BYTES) {
            throw new IllegalStateException("collaboration state file is unsafe");
        }
        String raw = Files.readString(stateFile, StandardCharsets.UTF_8);
        if (raw.isBlank()) return;
        PersistedState state;
        try {
            state = gson.fromJson(raw, PersistedState.class);
        } catch (RuntimeException failure) {
            throw new IllegalStateException("collaboration state is malformed", failure);
        }
        if (state == null) throw new IllegalStateException("collaboration state is malformed");
        List<ChangeSet> loadedSets = state.changeSets() == null ? List.of() : state.changeSets();
        List<ReviewComment> loadedComments = state.comments() == null ? List.of() : state.comments();
        if (loadedSets.size() > MAX_CHANGESETS || loadedComments.size() > MAX_COMMENTS) {
            throw new IllegalStateException("collaboration state exceeds supported limits");
        }
        for (ChangeSet set : loadedSets) {
            validateLoaded(set);
            if (changeSets.put(set.id(), set) != null) {
                throw new IllegalStateException("duplicate collaboration change set");
            }
        }
        for (ReviewComment comment : loadedComments) {
            validateLoaded(comment);
            if (!changeSets.containsKey(comment.changeSetId())) {
                throw new IllegalStateException("orphan collaboration comment");
            }
            if (comments.put(comment.id(), comment) != null) {
                throw new IllegalStateException("duplicate collaboration comment");
            }
        }
    }

    private void persist() {
        try {
            ensureNoSymlink(stateFile.getParent());
            PersistedState state = new PersistedState(
                    List.copyOf(changeSets.values()), List.copyOf(comments.values()));
            byte[] bytes = gson.toJson(state).getBytes(StandardCharsets.UTF_8);
            if (bytes.length > MAX_STATE_BYTES) {
                throw new IllegalStateException("collaboration state exceeds supported size");
            }
            Path temp = stateFile.resolveSibling(
                    stateFile.getFileName() + ".tmp-" + UUID.randomUUID());
            Files.write(temp, bytes);
            try {
                Files.move(temp, stateFile, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, stateFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IllegalStateException("collaboration state could not be persisted", e);
        }
    }

    private static void validateLoaded(ChangeSet set) {
        if (set == null) throw new IllegalStateException("null collaboration change set");
        requiredUuid(set.id(), "changeSetId");
        new ModelPath(required(set.modelId(), "modelId"));
        etag(set.baseEtag());
        content(set.proposedContent());
        Objects.requireNonNull(set.status(), "status");
        required(set.authorId(), "authorId");
        required(set.authorName(), "authorName");
        Instant.parse(required(set.createdAt(), "createdAt"));
        Instant.parse(required(set.updatedAt(), "updatedAt"));
        if (set.reviewRevision() < 1L) {
            throw new IllegalStateException("invalid collaboration review revision");
        }
    }

    private static void validateLoaded(ReviewComment comment) {
        if (comment == null) throw new IllegalStateException("null collaboration comment");
        requiredUuid(comment.id(), "commentId");
        requiredUuid(comment.changeSetId(), "changeSetId");
        required(comment.authorId(), "authorId");
        required(comment.authorName(), "authorName");
        boundedRequired(comment.body(), "body", MAX_COMMENT_CHARS);
        optional(comment.anchor(), "", MAX_ANCHOR_CHARS);
        Instant.parse(required(comment.createdAt(), "createdAt"));
        if (comment.resolved() && !comment.resolvedAt().isBlank()) {
            Instant.parse(comment.resolvedAt());
        }
    }

    private static void ensureNoSymlink(Path path) throws IOException {
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS) && Files.isSymbolicLink(path)) {
            throw new IllegalArgumentException("collaboration metadata must not use symbolic links");
        }
    }

    private String now() {
        return Instant.now(clock).toString();
    }

    private static String normalizeSessionId(String value) {
        if (value == null || value.isBlank()) return null;
        return requiredUuid(value, "sessionId");
    }

    private static String normalizeOptionalModelId(String value) {
        if (value == null || value.isBlank()) return "";
        return new ModelPath(value.trim()).value();
    }

    private static String requiredUuid(String value, String field) {
        String normalized = required(value, field);
        try {
            return UUID.fromString(normalized).toString();
        } catch (IllegalArgumentException failure) {
            throw new IllegalArgumentException(field + " must be a UUID");
        }
    }

    private static String etag(String value) {
        String normalized = required(value, "etag").toLowerCase(java.util.Locale.ROOT);
        if (!normalized.matches("[0-9a-f]{64}") && !MISSING_ETAG.equals(normalized)) {
            throw new IllegalArgumentException("etag must be lowercase SHA-256");
        }
        return normalized;
    }

    private static String content(String value) {
        if (value == null) throw new IllegalArgumentException("proposedContent is required");
        if (value.getBytes(StandardCharsets.UTF_8).length > RepositoryLimits.MAX_MODEL_BYTES) {
            throw new IllegalArgumentException("proposedContent exceeds supported model size");
        }
        return value;
    }

    private static String boundedRequired(String value, String field, int max) {
        String normalized = required(value, field);
        if (normalized.length() > max) {
            throw new IllegalArgumentException(field + " exceeds supported length");
        }
        return normalized;
    }

    private static String optional(String value, String fallback, int max) {
        String normalized = value == null ? fallback : value.trim();
        if (normalized.length() > max) {
            throw new IllegalArgumentException("value exceeds supported length");
        }
        return normalized;
    }

    private static String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim();
    }

    public static final class NotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
