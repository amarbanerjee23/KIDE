package com.kide.languageserver.gateway;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Objects;

public final class GatewayMessagePolicy {
    public enum Decision {
        ALLOW,
        MESSAGE_TOO_LARGE,
        RATE_LIMITED
    }

    private final int maxBytes;
    private final MessageRateLimiter limiter;

    public GatewayMessagePolicy(int maxBytes, int maxMessagesPerMinute, Clock clock) {
        if (maxBytes < 1) throw new IllegalArgumentException("maxBytes must be positive");
        this.maxBytes = maxBytes;
        this.limiter = new MessageRateLimiter(maxMessagesPerMinute, Objects.requireNonNull(clock, "clock"));
    }

    public Decision evaluate(String message) {
        Objects.requireNonNull(message, "message");
        if (message.getBytes(StandardCharsets.UTF_8).length > maxBytes) {
            return Decision.MESSAGE_TOO_LARGE;
        }
        return limiter.tryAcquire() ? Decision.ALLOW : Decision.RATE_LIMITED;
    }
}
