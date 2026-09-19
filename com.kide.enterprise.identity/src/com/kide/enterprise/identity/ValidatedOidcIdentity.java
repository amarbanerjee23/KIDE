package com.kide.enterprise.identity;

import java.net.URI;
import java.util.Map;

public record ValidatedOidcIdentity(
        String issuer,
        String subject,
        String displayName,
        Map<String, String> claims) {

    public ValidatedOidcIdentity {
        if (issuer == null || subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Validated OIDC identity requires issuer and subject");
        }
        URI parsed = URI.create(issuer);
        if (!"https".equalsIgnoreCase(parsed.getScheme()) || parsed.getHost() == null) {
            throw new IllegalArgumentException("OIDC issuer must be an HTTPS origin");
        }
        displayName = (displayName == null || displayName.isBlank()) ? subject : displayName;
        claims = claims == null ? Map.of() : Map.copyOf(claims);
    }

    public PrincipalIdentity toPrincipal(PrincipalKind kind, AuthenticationMethod method) {
        return new PrincipalIdentity(
                issuer + "#" + subject, displayName, kind, method, issuer, subject, claims);
    }
}
