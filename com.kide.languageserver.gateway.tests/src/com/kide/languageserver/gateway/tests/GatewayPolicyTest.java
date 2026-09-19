package com.kide.languageserver.gateway.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;

import org.junit.Test;

import com.kide.languageserver.gateway.GatewayConfig;
import com.kide.languageserver.gateway.GatewayMessagePolicy;
import com.kide.languageserver.gateway.LspMessageFraming;
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
    public void secureGatewayDefaultsAreBounded() {
        GatewayConfig config = GatewayConfig.secureDefault(8443);
        assertTrue(config.requireSecureTransport());
        assertTrue(config.loopbackBind());
        assertEquals(1024 * 1024, config.maxTextMessageBytes());
        assertEquals(Set.of(), config.allowedOrigins());
    }
}
