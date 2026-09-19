package com.kide.enterprise.modelrepo;

import java.util.Optional;

public interface ModelTransaction extends AutoCloseable {
    Optional<ModelSnapshot> read(ModelPath path);

    void write(ModelPath path, byte[] content, String expectedEtag);

    void delete(ModelPath path, String expectedEtag);

    void commit();

    void rollback();

    @Override
    default void close() {
        rollback();
    }
}
