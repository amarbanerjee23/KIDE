package com.kide.enterprise.identity;

import java.net.URI;
import java.util.List;

public record OidcClientConfig(
        URI issuer,
        String clientId,
        List<String> scopes,
        URI redirectUri) {

    public OidcClientConfig {
        if (issuer == null || !"https".equalsIgnoreCase(issuer.getScheme()) || issuer.getHost() == null) {
            throw new IllegalArgumentException("OIDC issuer must be an HTTPS origin");
        }
        if (clientId == null || clientId.isBlank()) throw new IllegalArgumentException("clientId is required");
        scopes = scopes == null || scopes.isEmpty() ? List.of("openid") : List.copyOf(scopes);
        if (!scopes.contains("openid")) throw new IllegalArgumentException("OIDC scopes must include openid");
    }
}
