package com.kide.enterprise.identity;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.Signature;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/** Verifies Firebase Authentication ID tokens for KIDE hosted transports. */
public final class FirebaseIdTokenAuthenticator {
    private static final long CLOCK_SKEW_SECONDS = 60L;

    private final FirebaseIdTokenConfig config;
    private final FirebaseKeyProvider keys;
    private final Clock clock;

    public FirebaseIdTokenAuthenticator(
            FirebaseIdTokenConfig config,
            FirebaseKeyProvider keys,
            Clock clock) {
        this.config = Objects.requireNonNull(config, "config");
        this.keys = Objects.requireNonNull(keys, "keys");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AuthenticatedSession authenticateAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AuthenticationException("Bearer authentication is required");
        }
        String token = authorizationHeader.substring("Bearer ".length()).trim();
        if (token.isEmpty() || token.length() > 16_384) {
            throw new AuthenticationException("Bearer authentication is invalid");
        }

        char[] tokenChars = token.toCharArray();
        try {
            String[] parts = token.split("\\.", -1);
            if (parts.length != 3
                    || parts[0].isEmpty() || parts[1].isEmpty() || parts[2].isEmpty()) {
                throw new AuthenticationException("Firebase ID token is malformed");
            }

            JsonObject header = json(base64Url(parts[0]), "Firebase ID token header");
            if (!"RS256".equals(stringField(header, "alg"))) {
                throw new AuthenticationException("Firebase ID token algorithm is not accepted");
            }
            String keyId = stringField(header, "kid");

            byte[] signature = base64Url(parts[2]);
            verifySignature(
                    keys.key(keyId),
                    (parts[0] + "." + parts[1]).getBytes(StandardCharsets.US_ASCII),
                    signature);

            JsonObject claims = json(base64Url(parts[1]), "Firebase ID token claims");
            String issuer = stringField(claims, "iss");
            if (!config.expectedIssuer().equals(issuer)) {
                throw new AuthenticationException("Firebase ID token issuer is not trusted");
            }
            if (!config.projectId().equals(stringField(claims, "aud"))) {
                throw new AuthenticationException("Firebase ID token audience is not accepted");
            }

            String subject = stringField(claims, "sub");
            if (subject.length() > 128) {
                throw new AuthenticationException("Firebase ID token subject is invalid");
            }

            Instant now = Instant.now(clock);
            long nowEpoch = now.getEpochSecond();
            long expires = longField(claims, "exp");
            long issuedAt = longField(claims, "iat");
            long authenticatedAt = longField(claims, "auth_time");
            if (expires <= nowEpoch) {
                throw new AuthenticationException("Firebase ID token has expired");
            }
            if (issuedAt > nowEpoch + CLOCK_SKEW_SECONDS
                    || authenticatedAt > nowEpoch + CLOCK_SKEW_SECONDS) {
                throw new AuthenticationException("Firebase ID token time claims are invalid");
            }

            Map<String, String> safeClaims = new LinkedHashMap<>();
            copySafeString(claims, safeClaims, "email");
            copySafeBoolean(claims, safeClaims, "email_verified");
            JsonElement firebase = claims.get("firebase");
            if (firebase != null && firebase.isJsonObject()) {
                copySafeString(firebase.getAsJsonObject(), safeClaims, "sign_in_provider");
            }

            String displayName = optionalString(
                    claims,
                    "name",
                    optionalString(claims, "email", subject));
            PrincipalIdentity principal = new PrincipalIdentity(
                    "firebase:" + config.projectId() + "#" + subject,
                    displayName,
                    PrincipalKind.HUMAN,
                    AuthenticationMethod.FIREBASE_ID_TOKEN,
                    issuer,
                    subject,
                    safeClaims);
            return new AuthenticatedSession(
                    principal,
                    new AuthTokens(tokenChars, null, Instant.ofEpochSecond(expires)),
                    clock);
        } finally {
            Arrays.fill(tokenChars, '\0');
        }
    }

    private static void verifySignature(
            PublicKey key,
            byte[] signedBytes,
            byte[] signatureBytes) {
        try {
            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(key);
            verifier.update(signedBytes);
            if (!verifier.verify(signatureBytes)) {
                throw new AuthenticationException(
                        "Firebase ID token signature is invalid");
            }
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException(
                    "Firebase ID token signature could not be verified");
        }
    }

    private static JsonObject json(byte[] bytes, String label) {
        try {
            JsonElement parsed = JsonParser.parseString(
                    new String(bytes, StandardCharsets.UTF_8));
            if (!parsed.isJsonObject()) {
                throw new AuthenticationException(label + " is invalid");
            }
            return parsed.getAsJsonObject();
        } catch (AuthenticationException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new AuthenticationException(label + " is invalid");
        }
    }

    private static byte[] base64Url(String value) {
        try {
            return Base64.getUrlDecoder().decode(value);
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException("Firebase ID token encoding is invalid");
        }
    }

    private static long longField(JsonObject object, String key) {
        JsonElement value = object.get(key);
        if (value == null || !value.isJsonPrimitive()) {
            throw new AuthenticationException("Firebase ID token is missing " + key);
        }
        try {
            return value.getAsLong();
        } catch (RuntimeException e) {
            throw new AuthenticationException("Firebase ID token has invalid " + key);
        }
    }

    private static String stringField(JsonObject object, String key) {
        String value = optionalString(object, key, null);
        if (value == null || value.isBlank()) {
            throw new AuthenticationException("Firebase ID token is missing " + key);
        }
        return value;
    }

    private static String optionalString(
            JsonObject object,
            String key,
            String fallback) {
        JsonElement value = object.get(key);
        if (value == null || !value.isJsonPrimitive()) return fallback;
        String result = value.getAsString();
        return result == null || result.isBlank() ? fallback : result;
    }

    private static void copySafeString(
            JsonObject source,
            Map<String, String> target,
            String key) {
        String value = optionalString(source, key, null);
        if (value != null && value.length() <= 2048) {
            target.put(key, value);
        }
    }

    private static void copySafeBoolean(
            JsonObject source,
            Map<String, String> target,
            String key) {
        JsonElement value = source.get(key);
        if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
            target.put(key, Boolean.toString(value.getAsBoolean()));
        }
    }
}
