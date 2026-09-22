package com.kide.knowledge;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

public final class EmbeddedKnowledgeRepository implements KnowledgeRepository {
    private static final int MAX_STATE_BYTES = 16 * 1024 * 1024;
    private final Path projectRoot;
    private final Path knowledgeRoot;
    private final Path stateFile;

    public EmbeddedKnowledgeRepository(Path projectRoot) {
        try {
            this.projectRoot = projectRoot.toAbsolutePath().normalize();
            if (Files.exists(this.projectRoot, LinkOption.NOFOLLOW_LINKS)
                    && Files.isSymbolicLink(this.projectRoot)) {
                throw new IllegalArgumentException("project root must not be a symbolic link");
            }
            Files.createDirectories(this.projectRoot);
            Path kide = this.projectRoot.resolve(".kide");
            if (Files.exists(kide, LinkOption.NOFOLLOW_LINKS) && Files.isSymbolicLink(kide)) {
                throw new IllegalArgumentException(".kide must not be a symbolic link");
            }
            this.knowledgeRoot = kide.resolve("knowledge");
            if (Files.exists(knowledgeRoot, LinkOption.NOFOLLOW_LINKS)
                    && Files.isSymbolicLink(knowledgeRoot)) {
                throw new IllegalArgumentException("knowledge metadata must not be a symbolic link");
            }
            Files.createDirectories(knowledgeRoot);
            this.stateFile = knowledgeRoot.resolve("repository.json");
            if (Files.exists(stateFile, LinkOption.NOFOLLOW_LINKS) && Files.isSymbolicLink(stateFile)) {
                throw new IllegalArgumentException("knowledge state must not be a symbolic link");
            }
        } catch (IOException e) {
            throw new KnowledgeRepositoryException("knowledge repository initialization failed", e);
        }
    }

    @Override
    public synchronized Optional<KnowledgeSnapshot> snapshot() {
        if (!Files.exists(stateFile, LinkOption.NOFOLLOW_LINKS)) return Optional.empty();
        try {
            if (!Files.isRegularFile(stateFile, LinkOption.NOFOLLOW_LINKS)
                    || Files.isSymbolicLink(stateFile)
                    || Files.size(stateFile) > MAX_STATE_BYTES) {
                throw new KnowledgeRepositoryException("knowledge state is not a safe bounded file");
            }
            State state = KnowledgeCodec.GSON.fromJson(
                    Files.readString(stateFile, StandardCharsets.UTF_8), State.class);
            if (state == null || state.dataset == null || state.revision <= 0L) {
                throw new KnowledgeRepositoryException("knowledge state is malformed");
            }
            String actual = KnowledgeCodec.sha256(state.dataset);
            if (!actual.equals(state.etag)) {
                throw new KnowledgeRepositoryException("knowledge state checksum mismatch");
            }
            return Optional.of(new KnowledgeSnapshot(state.revision, state.etag, state.dataset));
        } catch (KnowledgeRepositoryException e) {
            throw e;
        } catch (RuntimeException | IOException e) {
            throw new KnowledgeRepositoryException("knowledge state could not be read", e);
        }
    }

    @Override
    public synchronized KnowledgeSnapshot replace(KnowledgeDataset dataset, String expectedEtag) {
        java.util.Objects.requireNonNull(dataset, "dataset");
        String expected = normalizeExpected(expectedEtag);
        Optional<KnowledgeSnapshot> current = snapshot();
        String actual = current.map(KnowledgeSnapshot::etag).orElse(MISSING_ETAG);
        if (!actual.equals(expected)) {
            throw new KnowledgeRevisionConflictException("stale knowledge repository revision");
        }
        long revision = current.map(value -> value.revision() + 1L).orElse(1L);
        String etag = KnowledgeCodec.sha256(dataset);
        State state = new State(revision, etag, dataset);
        byte[] bytes = KnowledgeCodec.GSON.toJson(state).getBytes(StandardCharsets.UTF_8);
        if (bytes.length > MAX_STATE_BYTES) {
            throw new KnowledgeRepositoryException("knowledge state exceeds supported size");
        }
        try {
            Path temp = knowledgeRoot.resolve("repository.json.tmp-" + UUID.randomUUID());
            Files.write(temp, bytes);
            try {
                Files.move(temp, stateFile, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, stateFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return new KnowledgeSnapshot(revision, etag, dataset);
        } catch (IOException e) {
            throw new KnowledgeRepositoryException("knowledge state could not be persisted", e);
        }
    }

    public Path stateFile() { return stateFile; }

    private static String normalizeExpected(String value) {
        if (value == null || value.isBlank()) return MISSING_ETAG;
        value = value.trim();
        if (!value.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("expectedEtag must be lowercase SHA-256");
        }
        return value;
    }

    static final class State {
        long revision;
        String etag;
        KnowledgeDataset dataset;

        State(long revision, String etag, KnowledgeDataset dataset) {
            this.revision = revision;
            this.etag = etag;
            this.dataset = dataset;
        }
    }
}
