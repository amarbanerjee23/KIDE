package com.kide.enterprise.modelrepo;

import java.util.Arrays;
import java.util.Objects;

public final class ModelSnapshot {
    private final ModelPath path;
    private final ModelRevision revision;
    private final byte[] content;

    public ModelSnapshot(ModelPath path, ModelRevision revision, byte[] content) {
        this.path = Objects.requireNonNull(path, "path");
        this.revision = Objects.requireNonNull(revision, "revision");
        this.content = Objects.requireNonNull(content, "content").clone();
    }

    public ModelPath path() { return path; }
    public ModelRevision revision() { return revision; }
    public byte[] content() { return content.clone(); }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ModelSnapshot other)) return false;
        return path.equals(other.path) && revision.equals(other.revision)
                && Arrays.equals(content, other.content);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(path, revision) + Arrays.hashCode(content);
    }
}
