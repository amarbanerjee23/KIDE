package com.kide.enterprise.audit;

public final class AuditSchema {
    public static final int CURRENT_VERSION = 1;

    private AuditSchema() { }

    public static boolean isReadable(int version) {
        return version == CURRENT_VERSION;
    }
}
