package com.kide.enterprise.identity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Retrieves and caches Google's Firebase signing certificates using the
 * provider Cache-Control max-age.
 */
public final class GoogleFirebaseKeyProvider implements FirebaseKeyProvider {
    private static final Pattern MAX_AGE =
            Pattern.compile("(?:^|,)\\s*max-age=(\\d+)(?:,|$)");

    private final FirebaseIdTokenConfig config;
    private final HttpClient client;
    private final Clock clock;

    private Map<String, PublicKey> keys = Map.of();
    private Instant expiresAt = Instant.EPOCH;

    public GoogleFirebaseKeyProvider(
            FirebaseIdTokenConfig config,
            HttpClient client,
            Clock clock) {
        this.config = Objects.requireNonNull(config, "config");
        this.client = Objects.requireNonNull(client, "client");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    @Override
    public synchronized PublicKey key(String keyId) {
        if (keyId == null || keyId.isBlank() || keyId.length() > 512) {
            throw new AuthenticationException("Firebase token signing key is invalid");
        }

        Instant now = Instant.now(clock);
        if (keys.isEmpty() || !expiresAt.isAfter(now)) {
            refresh();
        }

        PublicKey key = keys.get(keyId);
        if (key == null) {
            // A key rotation can occur before a cached max-age expires.
            refresh();
            key = keys.get(keyId);
        }
        if (key == null) {
            throw new AuthenticationException("Firebase token signing key is not trusted");
        }
        return key;
    }

    private void refresh() {
        try {
            HttpRequest request = HttpRequest.newBuilder(config.certificatesUri())
                    .timeout(config.requestTimeout())
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                throw new AuthenticationException(
                        "Firebase signing certificates could not be loaded");
            }

            JsonElement parsed = JsonParser.parseString(response.body());
            if (!parsed.isJsonObject()) {
                throw new AuthenticationException(
                        "Firebase signing certificates are invalid");
            }

            Map<String, PublicKey> loaded = new LinkedHashMap<>();
            JsonObject object = parsed.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                if (!entry.getValue().isJsonPrimitive()) continue;
                String pem = entry.getValue().getAsString();
                if (pem == null || pem.isBlank()) continue;
                loaded.put(entry.getKey(), certificatePublicKey(pem));
            }
            if (loaded.isEmpty()) {
                throw new AuthenticationException(
                        "Firebase signing certificates are empty");
            }

            long maxAge = cacheMaxAgeSeconds(
                    response.headers().firstValue("Cache-Control").orElse(""));
            keys = Map.copyOf(loaded);
            expiresAt = Instant.now(clock).plusSeconds(Math.max(60L, maxAge));
        } catch (IOException e) {
            throw new AuthenticationException(
                    "Firebase signing certificates could not be loaded");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AuthenticationException(
                    "Firebase signing certificate loading was interrupted");
        }
    }

    private static PublicKey certificatePublicKey(String pem) {
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) factory.generateCertificate(
                    new ByteArrayInputStream(pem.getBytes(StandardCharsets.US_ASCII)));
            return certificate.getPublicKey();
        } catch (Exception e) {
            throw new AuthenticationException(
                    "Firebase signing certificate is invalid");
        }
    }

    static long cacheMaxAgeSeconds(String cacheControl) {
        Matcher matcher = MAX_AGE.matcher(cacheControl == null ? "" : cacheControl);
        if (!matcher.find()) return 300L;
        try {
            return Long.parseLong(matcher.group(1));
        } catch (NumberFormatException e) {
            return 300L;
        }
    }
}
