package com.kide.enterprise.identity.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.kide.enterprise.identity.AuthTokens;
import com.kide.enterprise.identity.AuthenticatedSession;
import com.kide.enterprise.identity.AuthenticationException;
import com.kide.enterprise.identity.AuthenticationMethod;
import com.kide.enterprise.identity.DeviceAuthorization;
import com.kide.enterprise.identity.OidcAuthenticationService;
import com.kide.enterprise.identity.OidcClientConfig;
import com.kide.enterprise.identity.OidcTokenGrant;
import com.kide.enterprise.identity.OidcTransport;
import com.kide.enterprise.identity.PendingAuthorization;
import com.kide.enterprise.identity.PrincipalIdentity;
import com.kide.enterprise.identity.PrincipalKind;
import com.kide.enterprise.identity.ValidatedOidcIdentity;

public class IdentityAuthenticationTest {
    private static final Instant NOW = Instant.parse("2026-09-19T00:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final OidcClientConfig CONFIG = new OidcClientConfig(
            URI.create("https://id.example.test"),
            "kide-desktop",
            List.of("openid", "profile"),
            URI.create("http://127.0.0.1:8765/callback"));

    @Test
    public void offlinePrincipalIsActiveAndTokenFree() {
        OidcAuthenticationService service = new OidcAuthenticationService(new MockTransport(), CLOCK);
        try (AuthenticatedSession session = service.offline("amar", "Amar")) {
            session.requireActive();
            assertEquals(PrincipalKind.LOCAL_OFFLINE, session.principal().kind());
            assertEquals(AuthenticationMethod.LOCAL_OFFLINE, session.principal().method());
            assertTrue(session.tokens().isEmpty());
        }
    }

    @Test
    public void expiredTokenFailsClosed() {
        PrincipalIdentity principal = principal(PrincipalKind.HUMAN, AuthenticationMethod.OIDC_PKCE);
        AuthTokens tokens = new AuthTokens(chars("a"), chars("r"), NOW.minusSeconds(1));
        try (AuthenticatedSession session = new AuthenticatedSession(principal, tokens, CLOCK)) {
            assertFalse(session.isActive());
            assertThrows(AuthenticationException.class, session::requireActive);
        }
    }

    @Test
    public void browserPkceUsesS256AndRejectsWrongStateBeforeTransport() {
        MockTransport transport = new MockTransport();
        OidcAuthenticationService service = new OidcAuthenticationService(transport, CLOCK);
        try (PendingAuthorization pending = service.beginBrowserPkce(CONFIG)) {
            String uri = pending.authorizationUri().toString();
            assertTrue(uri.contains("code_challenge_method=S256"));
            assertTrue(uri.contains("state="));
            assertTrue(uri.contains("nonce="));
            AuthenticationException error = assertThrows(AuthenticationException.class,
                    () -> service.completeBrowserPkce(CONFIG, pending, "code", "wrong-state"));
            assertEquals("OIDC state validation failed", error.getMessage());
            assertEquals(0, transport.authorizationCodeCalls);
        }
    }

    @Test
    public void browserPkceMapsOnlyValidatedClaims() {
        MockTransport transport = new MockTransport();
        OidcAuthenticationService service = new OidcAuthenticationService(transport, CLOCK);
        PendingAuthorization pending = service.beginBrowserPkce(CONFIG);
        String state = queryValue(pending.authorizationUri(), "state");
        try (AuthenticatedSession session =
                service.completeBrowserPkce(CONFIG, pending, "code", state)) {
            session.requireActive();
            assertEquals(PrincipalKind.HUMAN, session.principal().kind());
            assertEquals("https://id.example.test#user-1", session.principal().id());
            assertEquals(1, transport.authorizationCodeCalls);
        }
    }

    @Test
    public void serviceAccountUsesDistinctPrincipalKind() {
        MockTransport transport = new MockTransport();
        OidcAuthenticationService service = new OidcAuthenticationService(transport, CLOCK);
        char[] supplied = chars("client-value");
        try (AuthenticatedSession session = service.authenticateServiceAccount(CONFIG, supplied)) {
            assertEquals(PrincipalKind.SERVICE_ACCOUNT, session.principal().kind());
            assertEquals(AuthenticationMethod.OIDC_CLIENT_CREDENTIALS, session.principal().method());
            assertEquals("client-value", new String(supplied));
        }
    }

    @Test
    public void refreshFailureClosesSessionAndSanitizesTransportError() {
        MockTransport transport = new MockTransport();
        transport.failRefresh = true;
        OidcAuthenticationService service = new OidcAuthenticationService(transport, CLOCK);
        AuthTokens tokens = new AuthTokens(chars("access-value"), chars("refresh-value"), NOW.plusSeconds(60));
        AuthenticatedSession session = new AuthenticatedSession(
                principal(PrincipalKind.HUMAN, AuthenticationMethod.OIDC_PKCE), tokens, CLOCK);

        AuthenticationException error = assertThrows(AuthenticationException.class,
                () -> service.refresh(CONFIG, session));
        assertEquals("OIDC token refresh failed", error.getMessage());
        assertTrue(error.getCause() == null);
        assertFalse(session.isActive());
        assertTrue(tokens.isClosed());
        assertFalse(error.toString().contains("refresh-value"));
    }

    @Test
    public void logoutAlwaysClosesLocalSessionEvenWhenRevocationFails() {
        MockTransport transport = new MockTransport();
        transport.failRevoke = true;
        OidcAuthenticationService service = new OidcAuthenticationService(transport, CLOCK);
        AuthTokens tokens = new AuthTokens(chars("access-value"), chars("refresh-value"), NOW.plusSeconds(60));
        AuthenticatedSession session = new AuthenticatedSession(
                principal(PrincipalKind.HUMAN, AuthenticationMethod.OIDC_PKCE), tokens, CLOCK);

        AuthenticationException error = assertThrows(AuthenticationException.class,
                () -> service.logout(CONFIG, session));
        assertEquals("OIDC logout/revocation failed", error.getMessage());
        assertTrue(error.getCause() == null);
        assertFalse(session.isActive());
        assertTrue(tokens.isClosed());
    }

    @Test
    public void sensitiveClaimsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new PrincipalIdentity(
                "id", "name", PrincipalKind.HUMAN, AuthenticationMethod.OIDC_PKCE,
                "https://id.example.test", "subject", Map.of("access_token", "forbidden")));
    }

    @Test
    public void expiredDeviceAuthorizationFailsBeforeExchange() {
        MockTransport transport = new MockTransport();
        OidcAuthenticationService service = new OidcAuthenticationService(transport, CLOCK);
        DeviceAuthorization device = new DeviceAuthorization(
                chars("device-value"), "ABCD",
                URI.create("https://id.example.test/device"), NOW.minusSeconds(1), 5);
        assertThrows(AuthenticationException.class, () -> service.completeDeviceFlow(CONFIG, device));
        assertEquals(0, transport.deviceCodeCalls);
    }

    @Test
    public void tokenAndSessionRenderingIsRedacted() {
        AuthTokens tokens = new AuthTokens(chars("access-value"), chars("refresh-value"), NOW.plusSeconds(60));
        try (AuthenticatedSession session = new AuthenticatedSession(
                principal(PrincipalKind.HUMAN, AuthenticationMethod.OIDC_PKCE), tokens, CLOCK)) {
            String rendered = tokens.toString() + session.toString();
            assertFalse(rendered.contains("access-value"));
            assertFalse(rendered.contains("refresh-value"));
            assertTrue(rendered.contains("[REDACTED]"));
        }
    }

    private static PrincipalIdentity principal(PrincipalKind kind, AuthenticationMethod method) {
        return new ValidatedOidcIdentity(
                "https://id.example.test", "user-1", "User One", Map.of("tenant", "engineering"))
                .toPrincipal(kind, method);
    }

    private static char[] chars(String value) {
        return value.toCharArray();
    }

    private static String queryValue(URI uri, String name) {
        for (String pair : uri.getRawQuery().split("&")) {
            String[] pieces = pair.split("=", 2);
            if (pieces[0].equals(name)) {
                return java.net.URLDecoder.decode(pieces[1], java.nio.charset.StandardCharsets.UTF_8);
            }
        }
        throw new AssertionError("missing query parameter " + name);
    }

    private static final class MockTransport implements OidcTransport {
        int authorizationCodeCalls;
        int deviceCodeCalls;
        boolean failRefresh;
        boolean failRevoke;

        public OidcTokenGrant exchangeAuthorizationCode(
                OidcClientConfig config, String authorizationCode, char[] pkceVerifier,
                URI redirectUri, String expectedNonce) {
            authorizationCodeCalls++;
            assertNotNull(expectedNonce);
            assertTrue(pkceVerifier.length >= 43);
            return grant();
        }

        public DeviceAuthorization beginDeviceAuthorization(OidcClientConfig config) {
            return new DeviceAuthorization(chars("device-value"), "ABCD",
                    URI.create("https://id.example.test/device"), NOW.plusSeconds(60), 5);
        }

        public OidcTokenGrant exchangeDeviceCode(OidcClientConfig config, char[] deviceCode) {
            deviceCodeCalls++;
            return grant();
        }

        public OidcTokenGrant clientCredentials(OidcClientConfig config, char[] clientSecret) {
            return grant();
        }

        public OidcTokenGrant refresh(OidcClientConfig config, char[] refreshToken) {
            if (failRefresh) throw new IllegalStateException("transport diagnostic must not escape");
            return grant();
        }

        public void revoke(OidcClientConfig config, char[] accessToken) {
            if (failRevoke) throw new IllegalStateException("transport diagnostic must not escape");
        }

        private OidcTokenGrant grant() {
            return new OidcTokenGrant(
                    new ValidatedOidcIdentity(
                            "https://id.example.test", "user-1", "User One",
                            Map.of("tenant", "engineering")),
                    new AuthTokens(chars("issued-access"), chars("issued-refresh"), NOW.plusSeconds(3600)));
        }
    }
}
