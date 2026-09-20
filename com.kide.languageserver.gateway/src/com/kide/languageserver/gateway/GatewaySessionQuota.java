package com.kide.languageserver.gateway;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class GatewaySessionQuota {
    private final int maximum;
    private final AtomicInteger active = new AtomicInteger();

    public GatewaySessionQuota(int maximum) {
        if (maximum < 1) throw new IllegalArgumentException("maximum must be positive");
        this.maximum = maximum;
    }

    public Lease tryAcquire() {
        while (true) {
            int current = active.get();
            if (current >= maximum) return null;
            if (active.compareAndSet(current, current + 1)) return new Lease();
        }
    }

    public int activeSessions() {
        return active.get();
    }

    public final class Lease implements AutoCloseable {
        private final AtomicBoolean closed = new AtomicBoolean();

        private Lease() { }

        @Override
        public void close() {
            if (closed.compareAndSet(false, true)) {
                active.decrementAndGet();
            }
        }
    }
}
