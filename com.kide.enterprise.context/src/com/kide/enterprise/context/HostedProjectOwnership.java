package com.kide.enterprise.context;

/**
 * Durable project-scoped creator binding. Stored in the E04 project descriptor,
 * not in browser state or in a process-local authorization map.
 */
public final class HostedProjectOwnership {
    public static final String OWNER_KEY = "hosted.ownerPrincipalId";

    private HostedProjectOwnership() { }

    public static String principalId(HostedProjectRegistration registration) {
        String owner = registration.context().project().metadata().get(OWNER_KEY);
        if (owner == null || owner.isBlank() || owner.length() > 512
                || owner.chars().anyMatch(ch -> ch < 32 || ch == 127)) {
            return null;
        }
        return owner;
    }
}
