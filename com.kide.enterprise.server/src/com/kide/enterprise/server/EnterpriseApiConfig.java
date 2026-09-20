package com.kide.enterprise.server;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;

public record EnterpriseApiConfig(
        String bindHost,
        int port,
        int maxRequestBytes,
        Duration idleTimeout,
        boolean requireSecureTransport,
        boolean trustForwardedProto,
        Set<String> trustedProxyAddresses,
        Set<String> allowedOrigins) {

    public EnterpriseApiConfig {
        bindHost = Objects.requireNonNull(bindHost, "bindHost").trim();
        if (bindHost.isEmpty()) throw new IllegalArgumentException("bindHost is required");
        if (port < 0 || port > 65535) throw new IllegalArgumentException("port is out of range");
        if (maxRequestBytes < 1024 || maxRequestBytes > 16 * 1024 * 1024) {
            throw new IllegalArgumentException("maxRequestBytes is out of range");
        }
        idleTimeout = Objects.requireNonNull(idleTimeout, "idleTimeout");
        if (idleTimeout.isZero() || idleTimeout.isNegative()) {
            throw new IllegalArgumentException("idleTimeout must be positive");
        }
        trustedProxyAddresses = Set.copyOf(
                trustedProxyAddresses == null ? Set.of() : trustedProxyAddresses);
        allowedOrigins = Set.copyOf(allowedOrigins == null ? Set.of() : allowedOrigins);
        if (trustForwardedProto && trustedProxyAddresses.isEmpty()) {
            throw new IllegalArgumentException(
                    "trusted proxy addresses are required when forwarded HTTPS is trusted");
        }
    }

    public static EnterpriseApiConfig secureDefault(int port) {
        return new EnterpriseApiConfig(
                "127.0.0.1", port, 1024 * 1024, Duration.ofSeconds(30),
                true, false, Set.of(), Set.of());
    }
}
