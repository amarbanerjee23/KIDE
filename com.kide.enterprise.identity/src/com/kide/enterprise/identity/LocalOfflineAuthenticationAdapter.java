package com.kide.enterprise.identity;

import java.util.Objects;

public final class LocalOfflineAuthenticationAdapter {
    private final OidcAuthenticationService service;

    public LocalOfflineAuthenticationAdapter(OidcAuthenticationService service) {
        this.service = Objects.requireNonNull(service, "service");
    }

    public AuthenticatedSession authenticate(String localId, String displayName) {
        return service.offline(localId, displayName);
    }
}
