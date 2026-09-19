package com.kide.enterprise.api;

import java.util.Objects;
import java.util.UUID;

public record ApiRequestContext(
        UUID requestId,
        String clientId) {

    public ApiRequestContext {
        Objects.requireNonNull(requestId, "requestId");
        if (clientId == null || clientId.isBlank() || clientId.length() > 128) {
            throw new IllegalArgumentException("clientId is required and must be <= 128 characters");
        }
        clientId = clientId.trim();
    }

    public static ApiRequestContext create(String clientId) {
        return new ApiRequestContext(UUID.randomUUID(), clientId);
    }
}
