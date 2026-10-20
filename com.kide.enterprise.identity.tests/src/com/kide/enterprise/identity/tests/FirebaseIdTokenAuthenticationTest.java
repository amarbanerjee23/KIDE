package com.kide.enterprise.identity.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;

import org.junit.BeforeClass;
import org.junit.Test;

import com.kide.enterprise.identity.AuthenticatedSession;
import com.kide.enterprise.identity.AuthenticationException;
import com.kide.enterprise.identity.AuthenticationMethod;
import com.kide.enterprise.identity.FirebaseIdTokenAuthenticator;
import com.kide.enterprise.identity.FirebaseIdTokenConfig;
import com.kide.enterprise.identity.PrincipalKind;

public class FirebaseIdTokenAuthenticationTest {
    private static final String PROJECT_ID = "kide-eclipse";
    private static final String KID = "test-key";
    private static final Instant NOW = Instant.parse("2026-09-27T00:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static KeyPair keyPair;

    @BeforeClass
    public static void createKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        keyPair = generator.generateKeyPair();
    }

    @Test
    public void validFirebaseTokenMapsStableUidPrincipal() throws Exception {
        FirebaseIdTokenAuthenticator authenticator = authenticator();
        String token = token(
                PROJECT_ID,
                "https://securetoken.google.com/" + PROJECT_ID,
                "user-123",
                NOW.plusSeconds(3600).getEpochSecond(),
                NOW.minusSeconds(5).getEpochSecond(),
                NOW.minusSeconds(10).getEpochSecond(),
                keyPair);

        try (AuthenticatedSession session =
                authenticator.authenticateAuthorizationHeader("Bearer " + token)) {
            assertEquals(
                    "firebase:" + PROJECT_ID + "#user-123",
                    session.principal().id());
            assertEquals(PrincipalKind.HUMAN, session.principal().kind());
            assertEquals(
                    AuthenticationMethod.FIREBASE_ID_TOKEN,
                    session.principal().method());
            assertEquals("user@example.test", session.principal().claims().get("email"));
            assertEquals("password", session.principal().claims().get("sign_in_provider"));
        }
    }

    @Test
    public void wrongAudienceFailsClosed() throws Exception {
        String token = token(
                "different-project",
                "https://securetoken.google.com/" + PROJECT_ID,
                "user-123",
                NOW.plusSeconds(3600).getEpochSecond(),
                NOW.getEpochSecond(),
                NOW.getEpochSecond(),
                keyPair);
        assertThrows(
                AuthenticationException.class,
                () -> authenticator().authenticateAuthorizationHeader("Bearer " + token));
    }

    @Test
    public void expiredTokenFailsClosed() throws Exception {
        String token = token(
                PROJECT_ID,
                "https://securetoken.google.com/" + PROJECT_ID,
                "user-123",
                NOW.minusSeconds(1).getEpochSecond(),
                NOW.minusSeconds(60).getEpochSecond(),
                NOW.minusSeconds(60).getEpochSecond(),
                keyPair);
        assertThrows(
                AuthenticationException.class,
                () -> authenticator().authenticateAuthorizationHeader("Bearer " + token));
    }

    @Test
    public void futureAuthenticationTimeFailsClosed() throws Exception {
        String token = token(
                PROJECT_ID,
                "https://securetoken.google.com/" + PROJECT_ID,
                "user-123",
                NOW.plusSeconds(3600).getEpochSecond(),
                NOW.getEpochSecond(),
                NOW.plusSeconds(120).getEpochSecond(),
                keyPair);
        assertThrows(
                AuthenticationException.class,
                () -> authenticator().authenticateAuthorizationHeader("Bearer " + token));
    }

    @Test
    public void invalidSignatureFailsClosed() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair attacker = generator.generateKeyPair();
        String token = token(
                PROJECT_ID,
                "https://securetoken.google.com/" + PROJECT_ID,
                "user-123",
                NOW.plusSeconds(3600).getEpochSecond(),
                NOW.getEpochSecond(),
                NOW.getEpochSecond(),
                attacker);
        assertThrows(
                AuthenticationException.class,
                () -> authenticator().authenticateAuthorizationHeader("Bearer " + token));
    }

    @Test
    public void verifiedEmailRequiredOnlyInOwnerOnlyMode() throws Exception {
        String verified = token(PROJECT_ID,
                "https://securetoken.google.com/" + PROJECT_ID, "owner-1",
                NOW.plusSeconds(3600).getEpochSecond(), NOW.getEpochSecond(),
                NOW.getEpochSecond(), keyPair);
        try (AuthenticatedSession session = authenticator(true)
                .authenticateAuthorizationHeader("Bearer " + verified)) {
            assertEquals("firebase:" + PROJECT_ID + "#owner-1", session.principal().id());
        }
        for (Boolean state : new Boolean[] {false, null}) {
            String unverified = token(PROJECT_ID,
                    "https://securetoken.google.com/" + PROJECT_ID, "owner-1",
                    NOW.plusSeconds(3600).getEpochSecond(), NOW.getEpochSecond(),
                    NOW.getEpochSecond(), keyPair, "owner@example.test", state);
            assertThrows(AuthenticationException.class,
                    () -> authenticator(true).authenticateAuthorizationHeader(
                            "Bearer " + unverified));
            // Safe default retains backwards compatible private projects.
            try (AuthenticatedSession legacy = authenticator(false)
                    .authenticateAuthorizationHeader("Bearer " + unverified)) {
                assertEquals("owner-1", legacy.principal().subject());
            }
        }
        String phoneOnly = token(PROJECT_ID,
                "https://securetoken.google.com/" + PROJECT_ID, "owner-1",
                NOW.plusSeconds(3600).getEpochSecond(), NOW.getEpochSecond(),
                NOW.getEpochSecond(), keyPair, null, true);
        assertThrows(AuthenticationException.class,
                () -> authenticator(true).authenticateAuthorizationHeader(
                        "Bearer " + phoneOnly));
    }

    @Test
    public void ownerOnlyModeFailsClosedOnSharedOrUnqualifiedStorage() {
        java.time.Duration timeout = java.time.Duration.ofSeconds(5);
        java.util.Map<String, String> correct = java.util.Map.of(
                "KIDE_FIREBASE_OWNER_ONLY_ENGINEERS", "true",
                "KIDE_STORAGE_MODE", "registry-posix",
                "KIDE_HOSTED_PROJECTS_ROOT", "/data/hosted-projects",
                "KIDE_SHARED_FIREBASE_ENGINEERS", "false");
        org.junit.Assert.assertTrue(FirebaseIdTokenConfig.fromEnvironment(
                PROJECT_ID, timeout, correct).requireVerifiedEmail());
        for (java.util.Map<String, String> wrong : java.util.List.of(
                java.util.Map.of("KIDE_FIREBASE_OWNER_ONLY_ENGINEERS", "true"),
                java.util.Map.of("KIDE_FIREBASE_OWNER_ONLY_ENGINEERS", "true",
                        "KIDE_STORAGE_MODE", "legacy-gcs",
                        "KIDE_HOSTED_PROJECTS_ROOT", "/data/hosted-projects"),
                java.util.Map.of("KIDE_FIREBASE_OWNER_ONLY_ENGINEERS", "true",
                        "KIDE_STORAGE_MODE", "registry-posix",
                        "KIDE_HOSTED_PROJECTS_ROOT", "/data/hosted-projects",
                        "KIDE_SHARED_FIREBASE_ENGINEERS", "true"),
                java.util.Map.of("KIDE_FIREBASE_OWNER_ONLY_ENGINEERS", "INVALID"))) {
            assertThrows(IllegalArgumentException.class,
                    () -> FirebaseIdTokenConfig.fromEnvironment(PROJECT_ID, timeout, wrong));
        }
    }

    private static FirebaseIdTokenAuthenticator authenticator() {
        return authenticator(false);
    }

    private static FirebaseIdTokenAuthenticator authenticator(boolean verifiedRequired) {
        FirebaseIdTokenConfig config =
                new FirebaseIdTokenConfig(PROJECT_ID, java.time.Duration.ofSeconds(5),
                        verifiedRequired);
        return new FirebaseIdTokenAuthenticator(
                config,
                keyId -> {
                    if (!KID.equals(keyId)) {
                        throw new AuthenticationException(
                                "Firebase token signing key is not trusted");
                    }
                    return keyPair.getPublic();
                },
                CLOCK);
    }

    private static String token(
            String audience, String issuer, String subject, long expiration,
            long issuedAt, long authTime, KeyPair signingKey) throws Exception {
        return token(audience, issuer, subject, expiration, issuedAt, authTime,
                signingKey, "user@example.test", true);
    }

    private static String token(
            String audience,
            String issuer,
            String subject,
            long expiration,
            long issuedAt,
            long authTime,
            KeyPair signingKey,
            String email,
            Boolean emailVerified) throws Exception {
        String header = "{\"alg\":\"RS256\",\"kid\":\"" + KID
                + "\",\"typ\":\"JWT\"}";
        String payload = "{"
                + "\"aud\":\"" + audience + "\","
                + "\"iss\":\"" + issuer + "\","
                + "\"sub\":\"" + subject + "\","
                + "\"exp\":" + expiration + ","
                + "\"iat\":" + issuedAt + ","
                + "\"auth_time\":" + authTime + ","
                + (email == null ? "" : "\"email\":\"" + email + "\",")
                + (emailVerified == null ? "" : "\"email_verified\":" + emailVerified + ",")
                + "\"firebase\":{\"sign_in_provider\":\"password\"}"
                + "}";

        String encodedHeader = base64Url(header.getBytes(StandardCharsets.UTF_8));
        String encodedPayload = base64Url(payload.getBytes(StandardCharsets.UTF_8));
        String signed = encodedHeader + "." + encodedPayload;

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(signingKey.getPrivate());
        signature.update(signed.getBytes(StandardCharsets.US_ASCII));

        return signed + "." + base64Url(signature.sign());
    }

    private static String base64Url(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }
}
