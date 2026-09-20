package com.kide.languageserver.gateway;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public final class BrowserWebSocketCredential {
    public static final String LSP_PROTOCOL = "kide.lsp.v1";
    private static final String BEARER_PREFIX = "kide.bearer.";
    private static final int MAX_PROTOCOL_CREDENTIAL_CHARS = 24_000;

    private BrowserWebSocketCredential() { }

    public static String authorizationHeader(
            String explicitAuthorizationHeader,
            List<String> requestedSubProtocols) {
        if (explicitAuthorizationHeader != null && !explicitAuthorizationHeader.isBlank()) {
            return explicitAuthorizationHeader;
        }
        if (requestedSubProtocols == null || !requestedSubProtocols.contains(LSP_PROTOCOL)) {
            return null;
        }

        String encoded = null;
        for (String protocol : requestedSubProtocols) {
            if (protocol == null || !protocol.startsWith(BEARER_PREFIX)) continue;
            if (encoded != null) {
                throw new IllegalArgumentException("multiple browser bearer credentials are not allowed");
            }
            encoded = protocol.substring(BEARER_PREFIX.length());
        }
        if (encoded == null || encoded.isBlank()
                || encoded.length() > MAX_PROTOCOL_CREDENTIAL_CHARS) {
            return null;
        }

        try {
            byte[] decoded = Base64.getUrlDecoder().decode(encoded);
            if (decoded.length == 0 || decoded.length > 16_384) {
                throw new IllegalArgumentException("browser bearer credential length is invalid");
            }
            String token = new String(decoded, StandardCharsets.UTF_8);
            java.util.Arrays.fill(decoded, (byte) 0);
            if (token.isBlank() || containsControl(token)) {
                throw new IllegalArgumentException("browser bearer credential is invalid");
            }
            return "Bearer " + token;
        } catch (IllegalArgumentException failure) {
            throw new IllegalArgumentException("browser bearer credential is invalid");
        }
    }

    public static String encodeBearerProtocol(String accessToken) {
        if (accessToken == null || accessToken.isBlank()
                || accessToken.length() > 16_384 || containsControl(accessToken)) {
            throw new IllegalArgumentException("access token is invalid");
        }
        return BEARER_PREFIX + Base64.getUrlEncoder().withoutPadding()
                .encodeToString(accessToken.getBytes(StandardCharsets.UTF_8));
    }

    private static boolean containsControl(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isISOControl(value.charAt(i))) return true;
        }
        return false;
    }
}
