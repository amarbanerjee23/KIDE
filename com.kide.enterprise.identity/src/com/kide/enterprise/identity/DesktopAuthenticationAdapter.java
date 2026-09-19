package com.kide.enterprise.identity;

import java.util.Objects;

public final class DesktopAuthenticationAdapter {
    private final OidcAuthenticationService service;
    private final OidcClientConfig config;

    public DesktopAuthenticationAdapter(OidcAuthenticationService service, OidcClientConfig config) {
        this.service = Objects.requireNonNull(service, "service");
        this.config = Objects.requireNonNull(config, "config");
    }

    public PendingAuthorization beginPkceLogin() {
        return service.beginBrowserPkce(config);
    }

    public AuthenticatedSession completePkceLogin(
            PendingAuthorization pending, String authorizationCode, String returnedState) {
        return service.completeBrowserPkce(config, pending, authorizationCode, returnedState);
    }

    public DeviceAuthorization beginDeviceLogin() {
        return service.beginDeviceFlow(config);
    }

    public AuthenticatedSession completeDeviceLogin(DeviceAuthorization device) {
        return service.completeDeviceFlow(config, device);
    }

    public AuthenticatedSession refresh(AuthenticatedSession session) {
        return service.refresh(config, session);
    }

    public void logout(AuthenticatedSession session) {
        service.logout(config, session);
    }
}
