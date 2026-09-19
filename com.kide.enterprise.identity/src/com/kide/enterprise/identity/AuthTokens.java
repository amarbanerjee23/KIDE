package com.kide.enterprise.identity;

import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

public final class AuthTokens implements AutoCloseable {
    private char[] accessToken;
    private char[] refreshToken;
    private final Instant expiresAt;
    private boolean closed;

    public AuthTokens(char[] accessToken, char[] refreshToken, Instant expiresAt) {
        if (accessToken == null || accessToken.length == 0) {
            throw new IllegalArgumentException("access token is required");
        }
        this.accessToken = accessToken.clone();
        this.refreshToken = refreshToken == null ? new char[0] : refreshToken.clone();
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
    }

    public synchronized boolean isExpired(Clock clock) {
        return !expiresAt.isAfter(Instant.now(Objects.requireNonNull(clock, "clock")));
    }

    public synchronized boolean hasRefreshToken() {
        requireOpen();
        return refreshToken.length > 0;
    }

    public synchronized char[] accessTokenCopy() {
        requireOpen();
        return accessToken.clone();
    }

    public synchronized char[] refreshTokenCopy() {
        requireOpen();
        return refreshToken.clone();
    }

    public Instant expiresAt() { return expiresAt; }
    public synchronized boolean isClosed() { return closed; }

    @Override
    public synchronized void close() {
        if (closed) return;
        Arrays.fill(accessToken, '\0');
        Arrays.fill(refreshToken, '\0');
        accessToken = new char[0];
        refreshToken = new char[0];
        closed = true;
    }

    @Override
    public String toString() {
        return "AuthTokens[access=[REDACTED], refresh=[REDACTED], expiresAt=" + expiresAt + "]";
    }

    private void requireOpen() {
        if (closed) throw new IllegalStateException("authentication tokens are closed");
    }
}
