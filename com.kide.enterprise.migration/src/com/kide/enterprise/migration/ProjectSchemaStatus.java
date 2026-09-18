package com.kide.enterprise.migration;

public enum ProjectSchemaStatus {
    LEGACY,
    CURRENT,
    MIGRATION_REQUIRED,
    FORWARD_INCOMPATIBLE,
    INVALID,
    IO_ERROR
}
