package com.kide.enterprise.api;

import java.util.Objects;

public record ApiOperation(
        String operationId,
        HttpMethod method,
        String path,
        String requestSchema,
        String responseSchema) {

    public ApiOperation {
        operationId = required(operationId, "operationId");
        Objects.requireNonNull(method, "method");
        path = required(path, "path");
        if (!path.startsWith(ApiVersion.V1.basePath())) {
            throw new IllegalArgumentException("API path must be under " + ApiVersion.V1.basePath());
        }
        requestSchema = requestSchema == null ? "" : requestSchema.trim();
        responseSchema = required(responseSchema, "responseSchema");
    }

    private static String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim();
    }
}
