package com.kide.enterprise.identity;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Objects;

public final class OidcAuthenticationService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final OidcTransport transport;
    private final Clock clock;

    public OidcAuthenticationService(OidcTransport transport, Clock clock) {
        this.transport = Objects.requireNonNull(transport, "transport");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PendingAuthorization beginBrowserPkce(OidcClientConfig config) {
        if (config.redirectUri() == null) throw new AuthenticationException("Browser PKCE requires a redirect URI");
        String state = randomHex(24);
        String nonce = randomHex(24);
        char[] verifier = base64Url(randomBytes(48)).toCharArray();
        byte[] verifierBytes = new String(verifier).getBytes(StandardCharsets.US_ASCII);
        String challenge;
        try {
            challenge = base64Url(sha256(verifierBytes));
        } finally {
            Arrays.fill(verifierBytes, (byte) 0);
        }
        String uri = config.issuer().toString().replaceAll("/+$", "") + "/authorize"
                + "?response_type=code"
                + "&client_id=" + enc(config.clientId())
                + "&redirect_uri=" + enc(config.redirectUri().toString())
                + "&scope=" + enc(String.join(" ", config.scopes()))
                + "&state=" + enc(state)
                + "&nonce=" + enc(nonce)
                + "&code_challenge=" + enc(challenge)
                + "&code_challenge_method=S256";
        return new PendingAuthorization(URI.create(uri), verifier, state, nonce);
    }

    public AuthenticatedSession completeBrowserPkce(OidcClientConfig config,
            PendingAuthorization pending, String authorizationCode, String returnedState) {
        Objects.requireNonNull(pending, "pending");
        if (authorizationCode == null || authorizationCode.isBlank()) {
            throw new AuthenticationException("Authorization code is required");
        }
        if (!constantTimeEquals(pending.state(), returnedState)) {
            pending.close();
            throw new AuthenticationException("OIDC state validation failed");
        }
        char[] verifier = pending.verifierCopy();
        try {
            OidcTokenGrant grant = transport.exchangeAuthorizationCode(
                    config, authorizationCode, verifier, config.redirectUri(), pending.nonce());
            return session(grant, PrincipalKind.HUMAN, AuthenticationMethod.OIDC_PKCE);
        } catch (RuntimeException e) {
            throw safe("OIDC authorization-code exchange failed", e);
        } finally {
            Arrays.fill(verifier, '\0');
            pending.close();
        }
    }

    public DeviceAuthorization beginDeviceFlow(OidcClientConfig config) {
        try {
            return transport.beginDeviceAuthorization(config);
        } catch (RuntimeException e) {
            throw safe("OIDC device authorization failed", e);
        }
    }

    public AuthenticatedSession completeDeviceFlow(OidcClientConfig config, DeviceAuthorization device) {
        Objects.requireNonNull(device, "device");
        if (!device.expiresAt().isAfter(clock.instant())) {
            device.close();
            throw new AuthenticationException("OIDC device authorization expired");
        }
        char[] code = device.deviceCodeCopy();
        try {
            OidcTokenGrant grant = transport.exchangeDeviceCode(config, code);
            return session(grant, PrincipalKind.HUMAN, AuthenticationMethod.OIDC_DEVICE);
        } catch (RuntimeException e) {
            throw safe("OIDC device token exchange failed", e);
        } finally {
            Arrays.fill(code, '\0');
            device.close();
        }
    }

    public AuthenticatedSession authenticateServiceAccount(OidcClientConfig config, char[] clientSecret) {
        if (clientSecret == null || clientSecret.length == 0) {
            throw new AuthenticationException("Service-account credential is required");
        }
        char[] copy = clientSecret.clone();
        try {
            OidcTokenGrant grant = transport.clientCredentials(config, copy);
            return session(grant, PrincipalKind.SERVICE_ACCOUNT, AuthenticationMethod.OIDC_CLIENT_CREDENTIALS);
        } catch (RuntimeException e) {
            throw safe("OIDC service-account authentication failed", e);
        } finally {
            Arrays.fill(copy, '\0');
        }
    }

    public AuthenticatedSession refresh(OidcClientConfig config, AuthenticatedSession currentSession) {
        Objects.requireNonNull(currentSession, "currentSession");
        AuthTokens current = currentSession.tokens()
                .orElseThrow(() -> new AuthenticationException("Session has no refreshable tokens"));
        if (!current.hasRefreshToken()) throw new AuthenticationException("Session has no refresh token");
        char[] refresh = current.refreshTokenCopy();
        try {
            OidcTokenGrant grant = transport.refresh(config, refresh);
            AuthenticatedSession replacement = session(
                    grant, currentSession.principal().kind(), currentSession.principal().method());
            currentSession.logoutLocal();
            return replacement;
        } catch (RuntimeException e) {
            currentSession.logoutLocal();
            throw safe("OIDC token refresh failed", e);
        } finally {
            Arrays.fill(refresh, '\0');
        }
    }

    public void logout(OidcClientConfig config, AuthenticatedSession session) {
        Objects.requireNonNull(session, "session");
        try {
            if (session.tokens().isPresent()) {
                char[] access = session.tokens().get().accessTokenCopy();
                try {
                    transport.revoke(config, access);
                } finally {
                    Arrays.fill(access, '\0');
                }
            }
        } catch (RuntimeException e) {
            throw safe("OIDC logout/revocation failed", e);
        } finally {
            session.logoutLocal();
        }
    }

    public AuthenticatedSession offline(String localId, String displayName) {
        String id = required(localId);
        PrincipalIdentity principal = new PrincipalIdentity(
                "offline:" + id, required(displayName),
                PrincipalKind.LOCAL_OFFLINE, AuthenticationMethod.LOCAL_OFFLINE,
                "", id, java.util.Map.of("offline", "true"));
        return new AuthenticatedSession(principal, null, clock);
    }

    private AuthenticatedSession session(OidcTokenGrant grant, PrincipalKind kind, AuthenticationMethod method) {
        PrincipalIdentity principal = grant.identity().toPrincipal(kind, method);
        AuthenticatedSession session = new AuthenticatedSession(principal, grant.tokens(), clock);
        session.requireActive();
        return session;
    }

    private static AuthenticationException safe(String message, RuntimeException cause) {
        return new AuthenticationException(message, cause);
    }

    private static String required(String value) {
        if (value == null || value.isBlank()) throw new AuthenticationException("Principal value is required");
        return value;
    }

    private static byte[] randomBytes(int count) {
        byte[] bytes = new byte[count];
        RANDOM.nextBytes(bytes);
        return bytes;
    }

    private static String randomHex(int count) {
        return HexFormat.of().formatHex(randomBytes(count));
    }

    private static byte[] sha256(byte[] value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    private static String base64Url(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private static String enc(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static boolean constantTimeEquals(String expected, String actual) {
        if (expected == null || actual == null) return false;
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }
}
