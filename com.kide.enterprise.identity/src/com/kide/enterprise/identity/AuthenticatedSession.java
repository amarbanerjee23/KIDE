package com.kide.enterprise.identity;

import java.time.Clock;
import java.util.Objects;
import java.util.Optional;

public final class AuthenticatedSession implements AutoCloseable {
    private final PrincipalIdentity principal;
    private final AuthTokens tokens;
    private final Clock clock;
    private boolean loggedOut;

    public AuthenticatedSession(PrincipalIdentity principal, AuthTokens tokens, Clock clock) {
        this.principal = Objects.requireNonNull(principal, "principal");
        this.tokens = tokens;
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PrincipalIdentity principal() { return principal; }
    public Optional<AuthTokens> tokens() { return Optional.ofNullable(tokens); }

    public synchronized boolean isActive() {
        return !loggedOut && (tokens == null || !tokens.isExpired(clock));
    }

    public synchronized void requireActive() {
        if (loggedOut) throw new AuthenticationException("Authentication session is logged out");
        if (tokens != null && tokens.isExpired(clock)) {
            throw new AuthenticationException("Authentication session has expired");
        }
    }

    public synchronized void logoutLocal() {
        if (loggedOut) return;
        loggedOut = true;
        if (tokens != null) tokens.close();
    }

    @Override
    public void close() {
        logoutLocal();
    }

    @Override
    public String toString() {
        return "AuthenticatedSession[principal=" + principal + ", active=" + isActive()
                + ", tokens=[REDACTED]]";
    }
}
