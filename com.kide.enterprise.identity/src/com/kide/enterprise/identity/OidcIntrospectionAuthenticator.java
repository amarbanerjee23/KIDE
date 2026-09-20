package com.kide.enterprise.identity;

import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/** Shared bearer-token authenticator for KIDE server transports. */
public final class OidcIntrospectionAuthenticator {
    private final OidcIntrospectionConfig config;
    private final HttpClient client;
    private final Clock clock;

    public OidcIntrospectionAuthenticator(
            OidcIntrospectionConfig config,
            HttpClient client,
            Clock clock) {
        this.config = Objects.requireNonNull(config, "config");
        this.client = Objects.requireNonNull(client, "client");
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
            JsonObject response = introspect(token);
            if (!booleanField(response, "active")) {
                throw new AuthenticationException("Bearer token is inactive");
            }
            String issuer = stringField(response, "iss");
            String subject = stringField(response, "sub");
            if (!config.expectedIssuer().equals(issuer)) {
                throw new AuthenticationException("Bearer token issuer is not trusted");
            }
            if (!audienceContains(response.get("aud"), config.requiredAudience())) {
                throw new AuthenticationException("Bearer token audience is not accepted");
            }
            Instant expiresAt = Instant.ofEpochSecond(longField(response, "exp"));
            if (!expiresAt.isAfter(Instant.now(clock))) {
                throw new AuthenticationException("Bearer token has expired");
            }

            Map<String, String> claims = new LinkedHashMap<>();
            copySafeString(response, claims, "scope");
            copySafeString(response, claims, "preferred_username");
            claims.put("audience", config.requiredAudience());

            String principalId = "oidc:" + issuer + "#" + subject;
            String displayName = optionalString(response, "name",
                    optionalString(response, "preferred_username", subject));
            PrincipalIdentity principal = new PrincipalIdentity(
                    principalId,
                    displayName,
                    PrincipalKind.HUMAN,
                    AuthenticationMethod.OIDC_PKCE,
                    issuer,
                    subject,
                    claims);
            AuthTokens tokens = new AuthTokens(tokenChars, null, expiresAt);
            return new AuthenticatedSession(principal, tokens, clock);
        } finally {
            Arrays.fill(tokenChars, '\0');
        }
    }

    private JsonObject introspect(String token) {
        char[] secret = config.clientSecretCopy();
        byte[] rawBasic = null;
        try {
            String form = "token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
            rawBasic = (config.clientId() + ":" + new String(secret)).getBytes(StandardCharsets.UTF_8);
            String basic = Base64.getEncoder().encodeToString(rawBasic);
            HttpRequest request = HttpRequest.newBuilder(config.endpoint())
                    .timeout(config.requestTimeout())
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", "Basic " + basic)
                    .POST(HttpRequest.BodyPublishers.ofString(form, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                throw new AuthenticationException("OIDC token introspection failed");
            }
            JsonElement parsed = JsonParser.parseString(response.body());
            if (!parsed.isJsonObject()) {
                throw new AuthenticationException("OIDC token introspection returned invalid JSON");
            }
            return parsed.getAsJsonObject();
        } catch (IOException e) {
            throw new AuthenticationException("OIDC token introspection failed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AuthenticationException("OIDC token introspection was interrupted");
        } finally {
            Arrays.fill(secret, '\0');
            if (rawBasic != null) Arrays.fill(rawBasic, (byte) 0);
        }
    }

    private static boolean audienceContains(JsonElement value, String expected) {
        if (value == null || value.isJsonNull()) return false;
        if (value.isJsonPrimitive()) return expected.equals(value.getAsString());
        if (!value.isJsonArray()) return false;
        JsonArray array = value.getAsJsonArray();
        for (JsonElement element : array) {
            if (element.isJsonPrimitive() && expected.equals(element.getAsString())) return true;
        }
        return false;
    }

    private static boolean booleanField(JsonObject object, String key) {
        JsonElement value = object.get(key);
        return value != null && value.isJsonPrimitive() && value.getAsBoolean();
    }

    private static long longField(JsonObject object, String key) {
        JsonElement value = object.get(key);
        if (value == null || !value.isJsonPrimitive()) {
            throw new AuthenticationException("OIDC token introspection is missing " + key);
        }
        try {
            return value.getAsLong();
        } catch (RuntimeException e) {
            throw new AuthenticationException("OIDC token introspection has invalid " + key);
        }
    }

    private static String stringField(JsonObject object, String key) {
        String value = optionalString(object, key, null);
        if (value == null || value.isBlank()) {
            throw new AuthenticationException("OIDC token introspection is missing " + key);
        }
        return value;
    }

    private static String optionalString(JsonObject object, String key, String fallback) {
        JsonElement value = object.get(key);
        if (value == null || !value.isJsonPrimitive()) return fallback;
        String text = value.getAsString();
        return text == null || text.isBlank() ? fallback : text;
    }

    private static void copySafeString(JsonObject source, Map<String, String> target, String key) {
        String value = optionalString(source, key, null);
        if (value != null && value.length() <= 2048) target.put(key, value);
    }
}
