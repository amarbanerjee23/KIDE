package com.kide.enterprise.identity;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

public final class DeviceAuthorization implements AutoCloseable {
    private char[] deviceCode;
    private final String userCode;
    private final URI verificationUri;
    private final Instant expiresAt;
    private final int pollingIntervalSeconds;
    private boolean closed;

    public DeviceAuthorization(char[] deviceCode, String userCode, URI verificationUri,
            Instant expiresAt, int pollingIntervalSeconds) {
        if (deviceCode == null || deviceCode.length == 0) throw new IllegalArgumentException("deviceCode is required");
        if (userCode == null || userCode.isBlank()) throw new IllegalArgumentException("userCode is required");
        if (verificationUri == null || !"https".equalsIgnoreCase(verificationUri.getScheme())) {
            throw new IllegalArgumentException("verificationUri must use HTTPS");
        }
        if (pollingIntervalSeconds < 1) throw new IllegalArgumentException("polling interval must be positive");
        this.deviceCode = deviceCode.clone();
        this.userCode = userCode;
        this.verificationUri = verificationUri;
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
        this.pollingIntervalSeconds = pollingIntervalSeconds;
    }

    public String userCode() { return userCode; }
    public URI verificationUri() { return verificationUri; }
    public Instant expiresAt() { return expiresAt; }
    public int pollingIntervalSeconds() { return pollingIntervalSeconds; }

    synchronized char[] deviceCodeCopy() {
        if (closed) throw new IllegalStateException("device authorization is closed");
        return deviceCode.clone();
    }

    @Override
    public synchronized void close() {
        if (closed) return;
        Arrays.fill(deviceCode, '\0');
        deviceCode = new char[0];
        closed = true;
    }

    @Override
    public String toString() {
        return "DeviceAuthorization[userCode=" + userCode + ", verificationUri=" + verificationUri
                + ", deviceCode=[REDACTED]]";
    }
}
