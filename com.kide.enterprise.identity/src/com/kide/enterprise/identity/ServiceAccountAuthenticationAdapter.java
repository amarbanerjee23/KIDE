package com.kide.enterprise.identity;

import java.util.Objects;

public final class ServiceAccountAuthenticationAdapter {
    private final OidcAuthenticationService service;
    private final OidcClientConfig config;

    public ServiceAccountAuthenticationAdapter(OidcAuthenticationService service, OidcClientConfig config) {
        this.service = Objects.requireNonNull(service, "service");
        this.config = Objects.requireNonNull(config, "config");
    }

    public AuthenticatedSession authenticate(char[] credential) {
        return service.authenticateServiceAccount(config, credential);
    }

    public void logout(AuthenticatedSession session) {
        service.logout(config, session);
    }
}
