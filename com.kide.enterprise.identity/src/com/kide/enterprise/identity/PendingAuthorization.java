package com.kide.enterprise.identity;

import java.net.URI;
import java.util.Arrays;
import java.util.Objects;

public final class PendingAuthorization implements AutoCloseable {
    private final URI authorizationUri;
    private char[] verifier;
    private final String state;
    private final String nonce;
    private boolean closed;

    PendingAuthorization(URI authorizationUri, char[] verifier, String state, String nonce) {
        this.authorizationUri = Objects.requireNonNull(authorizationUri, "authorizationUri");
        this.verifier = verifier.clone();
        this.state = state;
        this.nonce = nonce;
    }

    public URI authorizationUri() { return authorizationUri; }
    String state() { return state; }
    String nonce() { return nonce; }

    synchronized char[] verifierCopy() {
        if (closed) throw new IllegalStateException("authorization request is closed");
        return verifier.clone();
    }

    @Override
    public synchronized void close() {
        if (closed) return;
        Arrays.fill(verifier, '\0');
        verifier = new char[0];
        closed = true;
    }

    @Override
    public String toString() {
        return "PendingAuthorization[authorizationUri=" + authorizationUri + ", verifier=[REDACTED]]";
    }
}
