package com.kide.enterprise.configuration;

import java.nio.file.Path;
import java.util.Objects;

/** A non-secret configuration value together with the layer that supplied it. */
public final class ConfigurationValue {
    private final String key;
    private final String value;
    private final ConfigurationLayer layer;
    private final Path source;

    ConfigurationValue(String key, String value, ConfigurationLayer layer, Path source) {
        this.key = Objects.requireNonNull(key, "key");
        this.value = Objects.requireNonNull(value, "value");
        this.layer = Objects.requireNonNull(layer, "layer");
        this.source = source;
    }

    public String key() {
        return key;
    }

    /** Returns a normal value or a secret reference, never resolved secret material. */
    public String value() {
        return value;
    }

    public ConfigurationLayer layer() {
        return layer;
    }

    /** Null only for bundled defaults. */
    public Path source() {
        return source;
    }

    @Override
    public String toString() {
        return "ConfigurationValue{" + key + ", layer=" + layer + ", source=" + source + "}";
    }
}
