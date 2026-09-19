package com.kide.enterprise.modelrepo;

public record ModelRevision(long version, String etag) {
    public ModelRevision {
        if (version < 0) throw new IllegalArgumentException("version must be non-negative");
        if (etag == null || !etag.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("etag must be lowercase SHA-256");
        }
    }
}
