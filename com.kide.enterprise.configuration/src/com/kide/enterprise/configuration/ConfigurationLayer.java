package com.kide.enterprise.configuration;

/** Lowest to highest precedence for KIDE enterprise configuration. */
public enum ConfigurationLayer {
    DEFAULTS,
    INSTALLATION,
    USER,
    WORKSPACE,
    PROJECT
}
