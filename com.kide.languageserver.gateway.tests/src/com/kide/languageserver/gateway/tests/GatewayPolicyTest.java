package com.kide.languageserver.gateway.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;

import org.junit.Test;

import com.kide.languageserver.gateway.BrowserWebSocketCredential;
import com.kide.languageserver.gateway.GatewayConfig;
import com.kide.languageserver.gateway.GatewayMessagePolicy;
import com.kide.languageserver.gateway.GatewaySessionQuota;
import com.kide.languageserver.gateway.LspMessageFraming;
import com.kide.languageserver.gateway.LspWorkspaceBoundary;
import com.kide.languageserver.gateway.OidcIntrospectionConfig;

public class GatewayPolicyTest {
    private static final Clock CLOCK =
            Clock.fixed(Instant.parse("2026-09-19T00:00:00Z"), ZoneOffset.UTC);

    @Test
    public void messagePolicyEnforcesSizeAndRateLimits() {
        GatewayMessagePolicy policy = new GatewayMessagePolicy(16, 2, CLOCK);
        assertEquals(GatewayMessagePolicy.Decision.ALLOW, policy.evaluate("{}"));
        assertEquals(GatewayMessagePolicy.Decision.ALLOW, policy.evaluate("{\"a\":1}"));
        assertEquals(GatewayMessagePolicy.Decision.RATE_LIMITED, policy.evaluate("{}"));
        assertEquals(
                GatewayMessagePolicy.Decision.MESSAGE_TOO_LARGE,
                policy.evaluate("x".repeat(17)));
    }

    @Test
    public void lspFramingPreservesCancellationNotificationExactly() throws Exception {
        String cancel = "{\"jsonrpc\":\"2.0\",\"method\":\"$/cancelRequest\",\"params\":{\"id\":42}}";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        LspMessageFraming.writeJson(out, cancel);
        String roundTrip = LspMessageFraming.readJson(
                new ByteArrayInputStream(out.toByteArray()), 4096);
        assertEquals(cancel, roundTrip);
    }

    @Test
    public void lspFramingRejectsOversizedBodyBeforeAllocation() {
        byte[] framed = "Content-Length: 999999\r\n\r\n".getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        assertThrows(
                java.io.IOException.class,
                () -> LspMessageFraming.readJson(new ByteArrayInputStream(framed), 1024));
    }

    @Test
    public void oidcIntrospectionRequiresHttpsAndRedactsSecretRendering() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new OidcIntrospectionConfig(
                        URI.create("http://id.example.test/introspect"),
                        "client",
                        "secret".toCharArray(),
                        "https://id.example.test",
                        "kide",
                        Duration.ofSeconds(5)));

        try (OidcIntrospectionConfig config = new OidcIntrospectionConfig(
                URI.create("https://id.example.test/introspect"),
                "client",
                "secret".toCharArray(),
                "https://id.example.test",
                "kide",
                Duration.ofSeconds(5))) {
            assertTrue(config.toString().contains("[REDACTED]"));
            assertTrue(!config.toString().contains("secret"));
        }
    }

    @Test
    public void browserSubprotocolCredentialRoundTripsWithoutEchoProtocol() {
        String encoded = BrowserWebSocketCredential.encodeBearerProtocol("opaque-token.value");
        assertTrue(encoded.startsWith("kide.bearer."));
        assertEquals(
                "Bearer opaque-token.value",
                BrowserWebSocketCredential.authorizationHeader(
                        null,
                        java.util.List.of(BrowserWebSocketCredential.LSP_PROTOCOL, encoded)));
        assertEquals(
                "Bearer header-token",
                BrowserWebSocketCredential.authorizationHeader(
                        "Bearer header-token",
                        java.util.List.of(BrowserWebSocketCredential.LSP_PROTOCOL, encoded)));
        assertThrows(
                IllegalArgumentException.class,
                () -> BrowserWebSocketCredential.authorizationHeader(
                        null,
                        java.util.List.of(
                                BrowserWebSocketCredential.LSP_PROTOCOL,
                                encoded,
                                BrowserWebSocketCredential.encodeBearerProtocol("second"))));
    }

    @Test
    public void workspaceBoundaryRejectsOutsideAndSymlinkEscapes() throws Exception {
        Path root = Files.createTempDirectory("kide-pr22-boundary-");
        try {
            Path project = Files.createDirectories(root.resolve("project"));
            Path outside = Files.createDirectories(root.resolve("outside"));
            Path valid = project.resolve("new-model.dml");
            LspWorkspaceBoundary boundary = new LspWorkspaceBoundary(project);

            boundary.requireWithinProject(
                    "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/didOpen\","
                    + "\"params\":{\"textDocument\":{\"uri\":\""
                    + valid.toUri() + "\"}}}");

            assertThrows(
                    IllegalArgumentException.class,
                    () -> boundary.requireWithinProject(
                            "{\"jsonrpc\":\"2.0\",\"method\":\"initialize\","
                            + "\"params\":{\"rootUri\":\""
                            + outside.toUri() + "\"}}}"));

            assertThrows(
                    IllegalArgumentException.class,
                    () -> boundary.requireWithinProject(
                            "{\"jsonrpc\":\"2.0\",\"method\":\"initialize\","
                            + "\"params\":{\"rootUri\":\"untitled:outside\"}}"));

            Path link = project.resolve("escape");
            Files.createSymbolicLink(link, outside);
            assertThrows(
                    IllegalArgumentException.class,
                    () -> boundary.requireWithinProject(
                            "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/didOpen\","
                            + "\"params\":{\"textDocument\":{\"uri\":\""
                            + link.resolve("secret.dml").toUri() + "\"}}}"));
        } finally {
            try (java.util.stream.Stream<Path> stream = Files.walk(root)) {
                stream.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (java.io.IOException ignored) {
                        // Test cleanup only.
                    }
                });
            }
        }
    }

    @Test
    public void forwardedProtoTrustRequiresExplicitProxyAddresses() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new GatewayConfig(
                        "127.0.0.1",
                        8443,
                        Duration.ofMinutes(5),
                        1024 * 1024,
                        2400,
                        128,
                        true,
                        true,
                        Set.of(),
                        Set.of()));

        GatewayConfig trusted = new GatewayConfig(
                "127.0.0.1",
                8443,
                Duration.ofMinutes(5),
                1024 * 1024,
                2400,
                128,
                true,
                true,
                Set.of("127.0.0.1"),
                Set.of());
        assertEquals(Set.of("127.0.0.1"), trusted.trustedProxyAddresses());
    }

    @Test
    public void sessionQuotaIsBoundedAndLeaseReleaseIsIdempotent() {
        GatewaySessionQuota quota = new GatewaySessionQuota(2);
        GatewaySessionQuota.Lease first = quota.tryAcquire();
        GatewaySessionQuota.Lease second = quota.tryAcquire();
        assertTrue(first != null);
        assertTrue(second != null);
        assertEquals(2, quota.activeSessions());
        assertTrue(quota.tryAcquire() == null);

        first.close();
        first.close();
        assertEquals(1, quota.activeSessions());

        GatewaySessionQuota.Lease replacement = quota.tryAcquire();
        assertTrue(replacement != null);
        assertEquals(2, quota.activeSessions());

        second.close();
        replacement.close();
        assertEquals(0, quota.activeSessions());
    }

    @Test
    public void secureGatewayDefaultsAreBounded() {
        GatewayConfig config = GatewayConfig.secureDefault(8443);
        assertTrue(config.requireSecureTransport());
        assertTrue(config.loopbackBind());
        assertEquals(1024 * 1024, config.maxTextMessageBytes());
        assertEquals(128, config.maxConcurrentSessions());
        assertEquals(Set.of(), config.allowedOrigins());
    }
}
