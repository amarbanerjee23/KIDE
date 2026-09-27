package com.kide.enterprise.identity;

public enum AuthenticationMethod {
    OIDC_PKCE,
    OIDC_DEVICE,
    OIDC_CLIENT_CREDENTIALS,
    FIREBASE_ID_TOKEN,
    LOCAL_OFFLINE
}
