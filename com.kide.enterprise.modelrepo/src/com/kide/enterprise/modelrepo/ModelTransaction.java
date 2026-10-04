package com.kide.enterprise.modelrepo;

import java.util.Optional;

public interface ModelTransaction extends AutoCloseable {
    Optional<ModelSnapshot> read(ModelPath path);

    /**
     * Adds a commit-time precondition that the canonical repository contains no
     * project models. The check is evaluated under the same repository lock as
     * revision preconditions so promotion into an empty project is race-safe.
     */
    void requireEmpty();

    void write(ModelPath path, byte[] content, String expectedEtag);

    void delete(ModelPath path, String expectedEtag);

    void commit();

    void rollback();

    @Override
    default void close() {
        rollback();
    }
}
