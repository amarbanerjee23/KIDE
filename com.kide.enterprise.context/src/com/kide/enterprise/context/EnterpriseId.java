package com.kide.enterprise.context;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Path-independent, display-name-independent stable identifier.
 * Canonical form: kide:&lt;scope&gt;:&lt;uuid&gt;.
 */
public final class EnterpriseId {
    private static final String PREFIX = "kide:";

    private final EnterpriseScope scope;
    private final UUID uuid;

    private EnterpriseId(EnterpriseScope scope, UUID uuid) {
        this.scope = Objects.requireNonNull(scope, "scope");
        this.uuid = Objects.requireNonNull(uuid, "uuid");
    }

    public static EnterpriseId create(EnterpriseScope scope) {
        return new EnterpriseId(Objects.requireNonNull(scope, "scope"), UUID.randomUUID());
    }

    /** Safe parser for persisted/untrusted metadata; malformed values return empty. */
    public static Optional<EnterpriseId> tryParse(EnterpriseScope expectedScope, String raw) {
        if (expectedScope == null || raw == null) {
            return Optional.empty();
        }
        String value = raw.trim();
        String expectedPrefix = PREFIX + expectedScope.token() + ":";
        if (!value.startsWith(expectedPrefix)) {
            return Optional.empty();
        }
        String uuidPart = value.substring(expectedPrefix.length());
        try {
            UUID uuid = UUID.fromString(uuidPart);
            if (!uuid.toString().equals(uuidPart.toLowerCase())) {
                return Optional.empty();
            }
            return Optional.of(new EnterpriseId(expectedScope, uuid));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public EnterpriseScope scope() {
        return scope;
    }

    public UUID uuid() {
        return uuid;
    }

    public String value() {
        return PREFIX + scope.token() + ":" + uuid;
    }

    @Override
    public String toString() {
        return value();
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, uuid);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EnterpriseId)) {
            return false;
        }
        EnterpriseId other = (EnterpriseId) obj;
        return scope == other.scope && uuid.equals(other.uuid);
    }
}
