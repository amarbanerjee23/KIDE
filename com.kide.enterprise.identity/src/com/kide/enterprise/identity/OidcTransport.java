package com.kide.enterprise.identity;

import java.net.URI;

public interface OidcTransport {
    OidcTokenGrant exchangeAuthorizationCode(
            OidcClientConfig config,
            String authorizationCode,
            char[] pkceVerifier,
            URI redirectUri,
            String expectedNonce);

    DeviceAuthorization beginDeviceAuthorization(OidcClientConfig config);

    OidcTokenGrant exchangeDeviceCode(OidcClientConfig config, char[] deviceCode);

    OidcTokenGrant clientCredentials(OidcClientConfig config, char[] clientSecret);

    OidcTokenGrant refresh(OidcClientConfig config, char[] refreshToken);

    void revoke(OidcClientConfig config, char[] accessToken);
}
