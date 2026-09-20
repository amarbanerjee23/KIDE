package com.kide.languageserver.gateway;

import com.kide.enterprise.identity.AuthenticatedSession;

public interface GatewayAuthenticator {
    AuthenticatedSession authenticateAuthorizationHeader(String authorizationHeader);
}
