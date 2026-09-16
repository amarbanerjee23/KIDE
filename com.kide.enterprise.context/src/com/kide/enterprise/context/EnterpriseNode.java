package com.kide.enterprise.context;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** One immutable node in the organization/portfolio/project/workspace hierarchy. */
public final class EnterpriseNode {
    private final EnterpriseId id;
    private final String displayName;
    private final Map<String, String> metadata;

    EnterpriseNode(EnterpriseId id, String displayName, Map<String, String> metadata) {
        this.id = Objects.requireNonNull(id, "id");
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.metadata = Collections.unmodifiableMap(new LinkedHashMap<>(metadata));
    }

    public EnterpriseScope scope() {
        return id.scope();
    }

    public EnterpriseId id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public Map<String, String> metadata() {
        return metadata;
    }
}
