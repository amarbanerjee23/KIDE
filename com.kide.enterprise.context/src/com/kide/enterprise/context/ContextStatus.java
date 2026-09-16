package com.kide.enterprise.context;

/** Result state for enterprise context loading/provisioning. */
public enum ContextStatus {
    READY,
    UNINITIALIZED,
    INVALID,
    IO_ERROR
}
