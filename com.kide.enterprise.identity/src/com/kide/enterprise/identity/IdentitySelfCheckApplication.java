package com.kide.enterprise.identity;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public final class IdentitySelfCheckApplication implements IApplication {
    @Override
    public Object start(IApplicationContext context) {
        try {
            Clock clock = Clock.fixed(Instant.parse("2026-09-19T00:00:00Z"), ZoneOffset.UTC);
            OidcAuthenticationService service = new OidcAuthenticationService(new RejectingTransport(), clock);
            try (AuthenticatedSession offline = service.offline("selfcheck", "Self Check")) {
                offline.requireActive();
                if (offline.principal().kind() != PrincipalKind.LOCAL_OFFLINE) return fail("offline principal kind");
            }

            AuthTokens tokens = new AuthTokens("selfcheck-a".toCharArray(),
                    "selfcheck-r".toCharArray(), Instant.parse("2026-09-19T01:00:00Z"));
            if (tokens.toString().contains("selfcheck-a") || tokens.toString().contains("selfcheck-r")) {
                return fail("token redaction");
            }
            tokens.close();
            if (!tokens.isClosed()) return fail("token close");

            System.out.println("KIDE PR17 IDENTITY SELF-CHECK OK");
            return IApplication.EXIT_OK;
        } catch (RuntimeException e) {
            return fail("guarded runtime failure: " + e.getClass().getSimpleName());
        }
    }

    @Override
    public void stop() { }

    private static Integer fail(String message) {
        System.err.println("KIDE PR17 IDENTITY SELF-CHECK FAILED: " + message);
        return Integer.valueOf(2);
    }

    private static final class RejectingTransport implements OidcTransport {
        private AuthenticationException unused() { return new AuthenticationException("network disabled in self-check"); }
        public OidcTokenGrant exchangeAuthorizationCode(OidcClientConfig c, String a, char[] p, java.net.URI r, String n) { throw unused(); }
        public DeviceAuthorization beginDeviceAuthorization(OidcClientConfig c) { throw unused(); }
        public OidcTokenGrant exchangeDeviceCode(OidcClientConfig c, char[] d) { throw unused(); }
        public OidcTokenGrant clientCredentials(OidcClientConfig c, char[] s) { throw unused(); }
        public OidcTokenGrant refresh(OidcClientConfig c, char[] r) { throw unused(); }
        public void revoke(OidcClientConfig c, char[] a) { throw unused(); }
    }
}
