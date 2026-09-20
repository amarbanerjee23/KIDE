package com.kide.languageserver.gateway;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

final class MessageRateLimiter {
    private final int limit;
    private final Clock clock;
    private Instant windowStart;
    private int count;

    MessageRateLimiter(int limit, Clock clock) {
        this.limit = limit;
        this.clock = Objects.requireNonNull(clock, "clock");
        this.windowStart = Instant.now(clock);
    }

    synchronized boolean tryAcquire() {
        Instant now = Instant.now(clock);
        if (!now.isBefore(windowStart.plusSeconds(60))) {
            windowStart = now;
            count = 0;
        }
        if (count >= limit) return false;
        count++;
        return true;
    }
}
