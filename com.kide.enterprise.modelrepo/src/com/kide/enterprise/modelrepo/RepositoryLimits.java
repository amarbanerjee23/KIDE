package com.kide.enterprise.modelrepo;

public final class RepositoryLimits {
    public static final int MAX_MODEL_BYTES = 8 * 1024 * 1024;
    public static final int MAX_TRANSACTION_MUTATIONS = 256;

    private RepositoryLimits() { }

    static byte[] checkedCopy(byte[] content) {
        if (content == null) throw new IllegalArgumentException("content is required");
        if (content.length > MAX_MODEL_BYTES) {
            throw new IllegalArgumentException("model exceeds supported size limit");
        }
        return content.clone();
    }
}
