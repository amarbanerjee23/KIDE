package com.kide.enterprise.api;

import java.util.Map;
import java.util.UUID;

import com.kide.enterprise.authorization.AccessDeniedException;

public final class ApiExceptionMapper {
    private ApiExceptionMapper() { }

    public static ApiErrorEnvelope map(UUID requestId, Throwable failure) {
        if (requestId == null) requestId = UUID.randomUUID();

        if (failure instanceof AccessDeniedException) {
            return new ApiErrorEnvelope(
                    ApiVersion.V1.token(), requestId, ApiErrorCode.FORBIDDEN,
                    "The requested operation is not permitted.", Map.of());
        }
        if (failure instanceof IllegalArgumentException) {
            return new ApiErrorEnvelope(
                    ApiVersion.V1.token(), requestId, ApiErrorCode.BAD_REQUEST,
                    "The request is invalid.", Map.of());
        }
        if (failure instanceof IllegalStateException) {
            return new ApiErrorEnvelope(
                    ApiVersion.V1.token(), requestId, ApiErrorCode.CONFLICT,
                    "The request conflicts with the current resource state.", Map.of());
        }
        return new ApiErrorEnvelope(
                ApiVersion.V1.token(), requestId, ApiErrorCode.INTERNAL_ERROR,
                "The request could not be completed.", Map.of());
    }
}
