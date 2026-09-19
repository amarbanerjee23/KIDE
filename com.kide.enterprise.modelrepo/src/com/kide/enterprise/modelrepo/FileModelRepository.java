package com.kide.enterprise.modelrepo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

/**
 * Crash-recoverable local repository whose canonical state remains the project files.
 * Transaction journals and revision metadata live only under .kide/model-repository.
 */
public final class FileModelRepository implements ModelRepository {
    private static final String INTERNAL_DIR = ".kide/model-repository";
    private static final String REVISIONS_FILE = "revisions.properties";
    private static final String TX_DIR = "transactions";
    private static final String STATE_FILE = "state";
    private static final String STATE_COMMITTING = "COMMITTING";
    private static final String STATE_COMMITTED = "COMMITTED";

    private final Path root;
    private final Path metadataRoot;
    private final Path revisionsFile;
    private final Path transactionsRoot;

    public FileModelRepository(Path projectRoot) {
        try {
            if (projectRoot == null) throw new IllegalArgumentException("projectRoot is required");
            root = projectRoot.toAbsolutePath().normalize();
            if (Files.exists(root, LinkOption.NOFOLLOW_LINKS) && Files.isSymbolicLink(root)) {
                throw new IllegalArgumentException("project root must not be a symbolic link");
            }
            Files.createDirectories(root);
            if (!Files.isDirectory(root, LinkOption.NOFOLLOW_LINKS)) {
                throw new IllegalArgumentException("project root is not a directory");
            }
            metadataRoot = root.resolve(INTERNAL_DIR);
            revisionsFile = metadataRoot.resolve(REVISIONS_FILE);
            transactionsRoot = metadataRoot.resolve(TX_DIR);
            Files.createDirectories(transactionsRoot);
            recoverTransactions();
        } catch (IOException e) {
            throw new ModelRepositoryException("model repository initialization failed", e);
        }
    }

    @Override
    public synchronized Optional<ModelSnapshot> read(ModelPath path) {
        try {
            Path target = resolveSafe(path, false);
            if (!Files.exists(target, LinkOption.NOFOLLOW_LINKS)) return Optional.empty();
            if (!Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS) || Files.isSymbolicLink(target)) {
                throw new ModelRepositoryException("model path is not a safe regular file");
            }
            long size = Files.size(target);
            if (size > RepositoryLimits.MAX_MODEL_BYTES) {
                throw new ModelRepositoryException("model exceeds supported size limit");
            }
            byte[] content = Files.readAllBytes(target);
            String etag = ModelHashing.sha256(content);
            Properties revisions = loadRevisions();
            long storedVersion = parseVersion(revisions.getProperty(key(path, "version")));
            String storedEtag = revisions.getProperty(key(path, "etag"), "");
            long version = etag.equals(storedEtag) ? storedVersion : storedVersion + 1L;
            return Optional.of(new ModelSnapshot(path, new ModelRevision(version, etag), content));
        } catch (IOException e) {
            throw new ModelRepositoryException("model read failed", e);
        }
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
                Optional<ModelSnapshot> current = FileModelRepository.this.read(path);
                long version = current.map(s -> s.revision().version() + 1L).orElse(1L);
                return Optional.of(new ModelSnapshot(
                        path,
                        new ModelRevision(version, ModelHashing.sha256(staged.content)),
                        staged.content));
            }
            return FileModelRepository.this.read(path);
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
            synchronized (FileModelRepository.this) {
                Path journal = null;
                try {
                    for (Map.Entry<ModelPath, Mutation> item : mutations.entrySet()) {
                        String current = FileModelRepository.this.read(item.getKey())
                                .map(s -> s.revision().etag())
                                .orElse(ModelHashing.MISSING_ETAG);
                        if (!current.equals(item.getValue().expectedEtag)) {
                            throw new RevisionConflictException(
                                    "stale model revision for " + item.getKey().value());
                        }
                    }
                    if (mutations.isEmpty()) {
                        finished = true;
                        return;
                    }

                    journal = prepareJournal(mutations);
                    writeState(journal, STATE_COMMITTING);

                    Properties revisions = loadRevisions();
                    int index = 0;
                    for (Map.Entry<ModelPath, Mutation> item : mutations.entrySet()) {
                        ModelPath modelPath = item.getKey();
                        Mutation mutation = item.getValue();
                        Path target = resolveSafe(modelPath, true);
                        long previousVersion = parseVersion(revisions.getProperty(key(modelPath, "version")));
                        if (mutation.delete) {
                            Files.deleteIfExists(target);
                            revisions.remove(key(modelPath, "version"));
                            revisions.remove(key(modelPath, "etag"));
                        } else {
                            Path staged = journal.resolve("staged").resolve(Integer.toString(index));
                            Files.createDirectories(target.getParent());
                            atomicReplace(staged, target);
                            String etag = ModelHashing.sha256(mutation.content);
                            revisions.setProperty(key(modelPath, "version"),
                                    Long.toString(previousVersion + 1L));
                            revisions.setProperty(key(modelPath, "etag"), etag);
                        }
                        index++;
                    }
                    storeRevisions(revisions);
                    writeState(journal, STATE_COMMITTED);
                    deleteTree(journal);
                    finished = true;
                    mutations.clear();
                } catch (RevisionConflictException e) {
                    throw e;
                } catch (IOException | RuntimeException e) {
                    if (journal != null) {
                        try {
                            rollbackJournal(journal);
                        } catch (IOException rollbackFailure) {
                            e.addSuppressed(rollbackFailure);
                        }
                    }
                    throw new ModelRepositoryException("model transaction commit failed", e);
                }
            }
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

    private Path prepareJournal(Map<ModelPath, Mutation> mutations) throws IOException {
        Path journal = transactionsRoot.resolve(UUID.randomUUID().toString());
        Path stagedDir = journal.resolve("staged");
        Path backupDir = journal.resolve("backup");
        Files.createDirectories(stagedDir);
        Files.createDirectories(backupDir);

        Properties manifest = new Properties();
        int index = 0;
        for (Map.Entry<ModelPath, Mutation> item : mutations.entrySet()) {
            Path target = resolveSafe(item.getKey(), false);
            boolean existed = Files.exists(target, LinkOption.NOFOLLOW_LINKS);
            manifest.setProperty("entry." + index + ".path", item.getKey().value());
            manifest.setProperty("entry." + index + ".existed", Boolean.toString(existed));
            manifest.setProperty("entry." + index + ".delete", Boolean.toString(item.getValue().delete));
            if (existed) {
                if (!Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS)
                        || Files.isSymbolicLink(target)) {
                    throw new ModelRepositoryException("unsafe model target");
                }
                Files.copy(target, backupDir.resolve(Integer.toString(index)),
                        StandardCopyOption.REPLACE_EXISTING);
            }
            if (!item.getValue().delete) {
                Files.write(stagedDir.resolve(Integer.toString(index)), item.getValue().content);
            }
            index++;
        }
        manifest.setProperty("count", Integer.toString(index));
        if (Files.exists(revisionsFile, LinkOption.NOFOLLOW_LINKS)) {
            Files.copy(revisionsFile, journal.resolve("revisions.backup"),
                    StandardCopyOption.REPLACE_EXISTING);
            manifest.setProperty("revisions.existed", "true");
        } else {
            manifest.setProperty("revisions.existed", "false");
        }
        try (OutputStream out = Files.newOutputStream(journal.resolve("manifest.properties"))) {
            manifest.store(out, "KIDE model transaction");
        }
        return journal;
    }

    private void recoverTransactions() throws IOException {
        if (!Files.isDirectory(transactionsRoot, LinkOption.NOFOLLOW_LINKS)) return;
        try (java.util.stream.Stream<Path> stream = Files.list(transactionsRoot)) {
            for (Path journal : stream.filter(Files::isDirectory).toList()) {
                String state = Files.exists(journal.resolve(STATE_FILE))
                        ? Files.readString(journal.resolve(STATE_FILE)).trim() : "";
                if (STATE_COMMITTED.equals(state)) {
                    deleteTree(journal);
                } else {
                    rollbackJournal(journal);
                }
            }
        }
    }

    private void rollbackJournal(Path journal) throws IOException {
        Path manifestFile = journal.resolve("manifest.properties");
        if (!Files.exists(manifestFile, LinkOption.NOFOLLOW_LINKS)) {
            deleteTree(journal);
            return;
        }
        Properties manifest = new Properties();
        try (InputStream in = Files.newInputStream(manifestFile)) {
            manifest.load(in);
        }
        int count = Integer.parseInt(manifest.getProperty("count", "0"));
        for (int index = 0; index < count; index++) {
            ModelPath path = new ModelPath(manifest.getProperty("entry." + index + ".path"));
            Path target = resolveSafe(path, true);
            boolean existed = Boolean.parseBoolean(
                    manifest.getProperty("entry." + index + ".existed", "false"));
            if (existed) {
                Path backup = journal.resolve("backup").resolve(Integer.toString(index));
                if (Files.exists(backup, LinkOption.NOFOLLOW_LINKS)) {
                    Files.createDirectories(target.getParent());
                    atomicReplace(backup, target);
                }
            } else {
                Files.deleteIfExists(target);
            }
        }

        boolean revisionsExisted = Boolean.parseBoolean(
                manifest.getProperty("revisions.existed", "false"));
        Path revisionBackup = journal.resolve("revisions.backup");
        if (revisionsExisted && Files.exists(revisionBackup, LinkOption.NOFOLLOW_LINKS)) {
            Files.createDirectories(revisionsFile.getParent());
            atomicReplace(revisionBackup, revisionsFile);
        } else {
            Files.deleteIfExists(revisionsFile);
        }
        deleteTree(journal);
    }

    private Properties loadRevisions() throws IOException {
        Properties properties = new Properties();
        if (!Files.exists(revisionsFile, LinkOption.NOFOLLOW_LINKS)) return properties;
        if (!Files.isRegularFile(revisionsFile, LinkOption.NOFOLLOW_LINKS)
                || Files.isSymbolicLink(revisionsFile)) {
            throw new ModelRepositoryException("revision metadata is unsafe");
        }
        try (InputStream in = Files.newInputStream(revisionsFile)) {
            properties.load(in);
        }
        return properties;
    }

    private void storeRevisions(Properties properties) throws IOException {
        Files.createDirectories(revisionsFile.getParent());
        Path temp = revisionsFile.resolveSibling(REVISIONS_FILE + ".tmp-" + UUID.randomUUID());
        try (OutputStream out = Files.newOutputStream(temp)) {
            properties.store(out, "KIDE model repository revisions");
        }
        atomicReplace(temp, revisionsFile);
    }

    private Path resolveSafe(ModelPath path, boolean createParents) throws IOException {
        Path target = root.resolve(path.value()).normalize();
        if (!target.startsWith(root) || target.startsWith(metadataRoot)) {
            throw new ModelRepositoryException("model path escapes project content boundary");
        }
        Path parent = target.getParent();
        Path current = root;
        if (parent != null) {
            for (Path component : root.relativize(parent)) {
                current = current.resolve(component);
                if (Files.exists(current, LinkOption.NOFOLLOW_LINKS)
                        && Files.isSymbolicLink(current)) {
                    throw new ModelRepositoryException("symbolic links are not allowed in model paths");
                }
            }
            if (createParents) Files.createDirectories(parent);
        }
        if (Files.exists(target, LinkOption.NOFOLLOW_LINKS) && Files.isSymbolicLink(target)) {
            throw new ModelRepositoryException("symbolic-link model targets are not allowed");
        }
        return target;
    }

    private static long parseVersion(String value) {
        if (value == null || value.isBlank()) return 0L;
        try {
            long parsed = Long.parseLong(value);
            return Math.max(parsed, 0L);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private static String key(ModelPath path, String suffix) {
        return "model." + java.util.Base64.getUrlEncoder().withoutPadding()
                .encodeToString(path.value().getBytes(java.nio.charset.StandardCharsets.UTF_8))
                + "." + suffix;
    }

    private static String normalizeExpected(String expectedEtag) {
        if (expectedEtag == null || expectedEtag.isBlank()) return ModelHashing.MISSING_ETAG;
        if (!expectedEtag.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("expectedEtag must be lowercase SHA-256");
        }
        return expectedEtag;
    }

    private static void writeState(Path journal, String state) throws IOException {
        Path temp = journal.resolve(STATE_FILE + ".tmp");
        Files.writeString(temp, state);
        atomicReplace(temp, journal.resolve(STATE_FILE));
    }

    private static void atomicReplace(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void deleteTree(Path root) throws IOException {
        if (root == null || !Files.exists(root, LinkOption.NOFOLLOW_LINKS)) return;
        try (java.util.stream.Stream<Path> stream = Files.walk(root)) {
            for (Path path : stream.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
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
