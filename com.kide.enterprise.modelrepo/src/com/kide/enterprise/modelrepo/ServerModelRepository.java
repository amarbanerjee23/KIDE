package com.kide.enterprise.modelrepo;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Thread-safe server reference implementation. Production persistence can replace
 * its backing map without changing transaction/revision semantics.
 */
public final class ServerModelRepository implements ModelRepository {
    private final Map<ModelPath, Entry> entries = new LinkedHashMap<>();

    @Override
    public synchronized Optional<ModelSnapshot> read(ModelPath path) {
        Entry entry = entries.get(path);
        return entry == null ? Optional.empty() : Optional.of(entry.snapshot(path));
    }

    @Override
    public ModelTransaction beginTransaction() {
        return new Tx();
    }

    private final class Tx implements ModelTransaction {
        private final Map<ModelPath, Mutation> mutations = new LinkedHashMap<>();
        private boolean finished;

        @Override
        public Optional<ModelSnapshot> read(ModelPath path) {
            ensureOpen();
            Mutation staged = mutations.get(path);
            if (staged != null) {
                if (staged.delete) return Optional.empty();
                long version;
                synchronized (ServerModelRepository.this) {
                    Entry current = entries.get(path);
                    version = current == null ? 1L : current.version + 1L;
                }
                byte[] content = staged.content.clone();
                return Optional.of(new ModelSnapshot(
                        path, new ModelRevision(version, ModelHashing.sha256(content)), content));
            }
            return ServerModelRepository.this.read(path);
        }

        @Override
        public void write(ModelPath path, byte[] content, String expectedEtag) {
            ensureOpen();
            checkCapacity(path);
            mutations.put(path, Mutation.write(
                    RepositoryLimits.checkedCopy(content), normalizeExpected(expectedEtag)));
        }

        @Override
        public void delete(ModelPath path, String expectedEtag) {
            ensureOpen();
            checkCapacity(path);
            mutations.put(path, Mutation.delete(normalizeExpected(expectedEtag)));
        }

        @Override
        public void commit() {
            ensureOpen();
            synchronized (ServerModelRepository.this) {
                for (Map.Entry<ModelPath, Mutation> item : mutations.entrySet()) {
                    Entry current = entries.get(item.getKey());
                    String currentEtag = current == null
                            ? ModelHashing.MISSING_ETAG : current.etag;
                    if (!currentEtag.equals(item.getValue().expectedEtag)) {
                        throw new RevisionConflictException(
                                "stale model revision for " + item.getKey().value());
                    }
                }
                for (Map.Entry<ModelPath, Mutation> item : mutations.entrySet()) {
                    Entry current = entries.get(item.getKey());
                    Mutation mutation = item.getValue();
                    if (mutation.delete) {
                        entries.remove(item.getKey());
                    } else {
                        long version = current == null ? 1L : current.version + 1L;
                        entries.put(item.getKey(), new Entry(version, mutation.content));
                    }
                }
            }
            finished = true;
            mutations.clear();
        }

        @Override
        public void rollback() {
            if (finished) return;
            finished = true;
            mutations.clear();
        }

        private void checkCapacity(ModelPath path) {
            if (!mutations.containsKey(path)
                    && mutations.size() >= RepositoryLimits.MAX_TRANSACTION_MUTATIONS) {
                throw new IllegalStateException("transaction mutation limit exceeded");
            }
        }

        private void ensureOpen() {
            if (finished) throw new IllegalStateException("transaction is closed");
        }
    }

    private static String normalizeExpected(String expectedEtag) {
        if (expectedEtag == null || expectedEtag.isBlank()) return ModelHashing.MISSING_ETAG;
        if (!expectedEtag.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("expectedEtag must be lowercase SHA-256");
        }
        return expectedEtag;
    }

    private static final class Entry {
        final long version;
        final byte[] content;
        final String etag;

        Entry(long version, byte[] content) {
            this.version = version;
            this.content = content.clone();
            this.etag = ModelHashing.sha256(content);
        }

        ModelSnapshot snapshot(ModelPath path) {
            return new ModelSnapshot(path, new ModelRevision(version, etag), content);
        }
    }

    private static final class Mutation {
        final byte[] content;
        final String expectedEtag;
        final boolean delete;

        private Mutation(byte[] content, String expectedEtag, boolean delete) {
            this.content = content == null ? null : content.clone();
            this.expectedEtag = expectedEtag;
            this.delete = delete;
        }

        static Mutation write(byte[] content, String expectedEtag) {
            return new Mutation(content, expectedEtag, false);
        }

        static Mutation delete(String expectedEtag) {
            return new Mutation(null, expectedEtag, true);
        }
    }
}
