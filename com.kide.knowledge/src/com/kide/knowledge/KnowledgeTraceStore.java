package com.kide.knowledge;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class KnowledgeTraceStore {
    private static final int MAX_BYTES = 4 * 1024 * 1024;
    private static final int MAX_LINKS = 20_000;
    private final Path root;
    private final Path file;
    private final Clock clock;

    public KnowledgeTraceStore(Path projectRoot, Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock");
        try {
            Path project = projectRoot.toAbsolutePath().normalize();
            Files.createDirectories(project);
            Path kide = project.resolve(".kide");
            Path knowledge = kide.resolve("knowledge");
            rejectSymlink(kide, ".kide");
            rejectSymlink(knowledge, "knowledge metadata");
            Files.createDirectories(knowledge);
            this.root = knowledge;
            this.file = knowledge.resolve("traces.json");
            rejectSymlink(file, "trace state");
        } catch (IOException e) {
            throw new KnowledgeRepositoryException("knowledge trace store initialization failed", e);
        }
    }

    public synchronized KnowledgeTraceSnapshot snapshot() {
        if (!Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
            return new KnowledgeTraceSnapshot(0L, KnowledgeRepository.MISSING_ETAG, List.of());
        }
        try {
            if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)
                    || Files.isSymbolicLink(file) || Files.size(file) > MAX_BYTES) {
                throw new KnowledgeRepositoryException("knowledge trace state is not a safe bounded file");
            }
            State state = KnowledgeCodec.GSON.fromJson(
                    Files.readString(file, StandardCharsets.UTF_8), State.class);
            if (state == null || state.revision <= 0L || state.links == null || state.etag == null) {
                throw new KnowledgeRepositoryException("knowledge trace state is malformed");
            }
            List<KnowledgeTraceLink> links = normalize(state.links);
            String expected = etag(state.revision, links);
            if (!expected.equals(state.etag)) {
                throw new KnowledgeRepositoryException("knowledge trace state checksum mismatch");
            }
            return new KnowledgeTraceSnapshot(state.revision, state.etag, links);
        } catch (KnowledgeRepositoryException e) {
            throw e;
        } catch (RuntimeException | IOException e) {
            throw new KnowledgeRepositoryException("knowledge trace state could not be read", e);
        }
    }

    public synchronized KnowledgeTraceSnapshot create(
            String knowledgeIri,
            String modelPath,
            String semanticId,
            KnowledgeTraceRelation relation,
            String sourceAuthority,
            String provenanceSource,
            String actor,
            String knowledgeEtag,
            String modelEtag,
            String expectedStoreEtag) {
        KnowledgeTraceSnapshot current = requireExpected(expectedStoreEtag);
        if (current.links().size() >= MAX_LINKS) {
            throw new KnowledgeRepositoryException("knowledge trace link limit exceeded");
        }
        long now = clock.millis();
        KnowledgeTraceLink link = new KnowledgeTraceLink(
                UUID.randomUUID().toString(), knowledgeIri, modelPath, semanticId,
                relation, sourceAuthority, provenanceSource, actor, actor,
                now, now, knowledgeEtag, modelEtag);
        List<KnowledgeTraceLink> next = new ArrayList<>(current.links());
        next.add(link);
        return persist(current.revision() + 1L, next);
    }

    public synchronized KnowledgeTraceSnapshot rebind(
            String traceId,
            String modelPath,
            String semanticId,
            String modelEtag,
            String actor,
            String expectedStoreEtag) {
        UUID.fromString(traceId);
        KnowledgeTraceSnapshot current = requireExpected(expectedStoreEtag);
        List<KnowledgeTraceLink> next = new ArrayList<>();
        boolean found = false;
        for (KnowledgeTraceLink link : current.links()) {
            if (link.id().equals(traceId)) {
                next.add(link.rebind(modelPath, semanticId, modelEtag, actor, clock.millis()));
                found = true;
            } else {
                next.add(link);
            }
        }
        if (!found) throw new IllegalArgumentException("knowledge trace link was not found");
        return persist(current.revision() + 1L, next);
    }

    public synchronized KnowledgeTraceSnapshot delete(
            String traceId, String expectedStoreEtag) {
        UUID.fromString(traceId);
        KnowledgeTraceSnapshot current = requireExpected(expectedStoreEtag);
        List<KnowledgeTraceLink> next = current.links().stream()
                .filter(link -> !link.id().equals(traceId)).toList();
        if (next.size() == current.links().size()) {
            throw new IllegalArgumentException("knowledge trace link was not found");
        }
        return persist(current.revision() + 1L, next);
    }

    private KnowledgeTraceSnapshot requireExpected(String expected) {
        if (expected == null || !expected.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("expectedStoreEtag must be SHA-256");
        }
        KnowledgeTraceSnapshot current = snapshot();
        if (!current.etag().equals(expected)) {
            throw new KnowledgeRevisionConflictException("stale knowledge trace revision");
        }
        return current;
    }

    private KnowledgeTraceSnapshot persist(long revision, List<KnowledgeTraceLink> source) {
        List<KnowledgeTraceLink> links = normalize(source);
        String hash = etag(revision, links);
        State state = new State(revision, hash, links);
        byte[] bytes = KnowledgeCodec.GSON.toJson(state).getBytes(StandardCharsets.UTF_8);
        if (bytes.length > MAX_BYTES) {
            throw new KnowledgeRepositoryException("knowledge trace state exceeds supported size");
        }
        try {
            Path temp = root.resolve("traces.json.tmp-" + UUID.randomUUID());
            Files.write(temp, bytes);
            try {
                Files.move(temp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
            }
            return new KnowledgeTraceSnapshot(revision, hash, links);
        } catch (IOException e) {
            throw new KnowledgeRepositoryException("knowledge trace state could not be persisted", e);
        }
    }

    private static List<KnowledgeTraceLink> normalize(List<KnowledgeTraceLink> source) {
        if (source.size() > MAX_LINKS) throw new KnowledgeRepositoryException("knowledge trace link limit exceeded");
        return source.stream()
                .peek(Objects::requireNonNull)
                .sorted(Comparator.comparing(KnowledgeTraceLink::id))
                .toList();
    }

    private static String etag(long revision, List<KnowledgeTraceLink> links) {
        return KnowledgeCodec.sha256Text(
                KnowledgeCodec.GSON.toJson(new HashState(revision, links)));
    }

    private static void rejectSymlink(Path path, String label) throws IOException {
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS) && Files.isSymbolicLink(path)) {
            throw new IllegalArgumentException(label + " must not be a symbolic link");
        }
    }

    private static final class State {
        long revision;
        String etag;
        List<KnowledgeTraceLink> links;

        State(long revision, String etag, List<KnowledgeTraceLink> links) {
            this.revision = revision; this.etag = etag; this.links = links;
        }
    }

    private record HashState(long revision, List<KnowledgeTraceLink> links) { }
}
