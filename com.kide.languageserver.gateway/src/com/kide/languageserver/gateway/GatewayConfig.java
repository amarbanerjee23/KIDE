package com.kide.languageserver.gateway;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;

public record GatewayConfig(
        String bindHost,
        int port,
        Duration idleTimeout,
        int maxTextMessageBytes,
        int maxMessagesPerMinute,
        boolean requireSecureTransport,
        boolean trustForwardedProto,
        Set<String> trustedProxyAddresses,
        Set<String> allowedOrigins) {

    public GatewayConfig {
        bindHost = Objects.requireNonNull(bindHost, "bindHost").trim();
        if (bindHost.isEmpty()) throw new IllegalArgumentException("bindHost is required");
        if (port < 0 || port > 65535) throw new IllegalArgumentException("port out of range");
        idleTimeout = Objects.requireNonNull(idleTimeout, "idleTimeout");
        if (idleTimeout.isNegative() || idleTimeout.isZero()) {
            throw new IllegalArgumentException("idleTimeout must be positive");
        }
        if (maxTextMessageBytes < 1024 || maxTextMessageBytes > 16 * 1024 * 1024) {
            throw new IllegalArgumentException("maxTextMessageBytes out of supported range");
        }
        if (maxMessagesPerMinute < 1 || maxMessagesPerMinute > 100_000) {
            throw new IllegalArgumentException("maxMessagesPerMinute out of supported range");
        }
        trustedProxyAddresses = trustedProxyAddresses == null
                ? Set.of() : Set.copyOf(trustedProxyAddresses);
        if (trustForwardedProto && trustedProxyAddresses.isEmpty()) {
            throw new IllegalArgumentException(
                    "trustedProxyAddresses is required when forwarded proto is trusted");
        }
        allowedOrigins = allowedOrigins == null ? Set.of() : Set.copyOf(allowedOrigins);
    }

    public static GatewayConfig secureDefault(int port) {
        return new GatewayConfig(
                "127.0.0.1", port, Duration.ofMinutes(5),
                1024 * 1024, 2400, true, false, Set.of(), Set.of());
    }

    public boolean loopbackBind() {
        return "127.0.0.1".equals(bindHost) || "::1".equals(bindHost) || "localhost".equalsIgnoreCase(bindHost);
    }
}
