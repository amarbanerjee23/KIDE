package com.kide.enterprise.identity;

import java.util.Objects;

public final class BrowserAuthenticationAdapter {
    private final OidcAuthenticationService service;
    private final OidcClientConfig config;

    public BrowserAuthenticationAdapter(OidcAuthenticationService service, OidcClientConfig config) {
        this.service = Objects.requireNonNull(service, "service");
        this.config = Objects.requireNonNull(config, "config");
    }

    public PendingAuthorization beginLogin() {
        return service.beginBrowserPkce(config);
    }

    public AuthenticatedSession completeLogin(
            PendingAuthorization pending, String authorizationCode, String returnedState) {
        return service.completeBrowserPkce(config, pending, authorizationCode, returnedState);
    }

    public AuthenticatedSession refresh(AuthenticatedSession session) {
        return service.refresh(config, session);
    }

    public void logout(AuthenticatedSession session) {
        service.logout(config, session);
    }
}
