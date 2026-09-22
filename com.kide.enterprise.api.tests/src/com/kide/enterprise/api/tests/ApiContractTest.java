package com.kide.enterprise.api.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import com.kide.enterprise.api.ApiCompatibilityPolicy;
import com.kide.enterprise.api.ApiFieldType;
import com.kide.enterprise.api.ApiContractRegistry;
import com.kide.enterprise.api.ApiErrorCode;
import com.kide.enterprise.api.ApiErrorEnvelope;
import com.kide.enterprise.api.ApiExceptionMapper;
import com.kide.enterprise.api.ApiOperation;
import com.kide.enterprise.api.ApiSchema;
import com.kide.enterprise.api.ApiSchemaCatalog;
import com.kide.enterprise.api.ApiVersion;
import com.kide.enterprise.api.HttpMethod;
import com.kide.enterprise.api.OpenApiV1;
import com.kide.enterprise.authorization.AccessDeniedException;

public class ApiContractTest {

    @Test
    public void v1ContainsAllRequiredDomainSurfaces() {
        List<ApiOperation> operations = ApiContractRegistry.v1();

        assertTrue(operations.stream().anyMatch(op -> op.path().equals("/api/v1/projects")));
        assertTrue(operations.stream().anyMatch(op -> op.path().contains("/models")));
        assertTrue(operations.stream().anyMatch(op -> op.path().contains("/knowledge")));
        assertTrue(operations.stream().anyMatch(op ->
                op.path().endsWith("/knowledge/query") && op.method() == HttpMethod.POST));
        assertTrue(operations.stream().anyMatch(op ->
                op.path().endsWith("/knowledge/traces") && op.method() == HttpMethod.GET));
        assertTrue(operations.stream().anyMatch(op ->
                op.path().contains("/knowledge/traces/{traceId}") && op.method() == HttpMethod.PUT));
        assertTrue(operations.stream().anyMatch(op ->
                op.path().endsWith("/knowledge/impact") && op.method() == HttpMethod.POST));
        assertTrue(operations.stream().anyMatch(op -> op.path().contains("/synthesis")));
        assertTrue(operations.stream().anyMatch(op ->
                op.path().endsWith("/reconfiguration") && op.method() == HttpMethod.POST));
        assertTrue(operations.stream().anyMatch(op -> op.path().contains("/evidence")));
        assertTrue(operations.stream().anyMatch(op -> op.path().contains("/collaboration/sessions")));
        assertTrue(operations.stream().anyMatch(op -> op.path().contains("/reviews/changesets")));
        assertTrue(operations.stream().anyMatch(op -> op.path().equals("/api/v1/health")));
        assertTrue(operations.stream().allMatch(op -> op.path().startsWith(ApiVersion.V1.basePath())));
    }

    @Test
    public void generatedOpenApiContractValidatesAndGroupsSharedPaths() {
        assertTrue(OpenApiV1.validateContract().toString(), OpenApiV1.validateContract().isEmpty());

        String document = OpenApiV1.generateJson();
        assertEquals(1, count(document, "\"/api/v1/projects\":"));
        assertTrue(document.contains("\"get\""));
        assertTrue(document.contains("\"post\""));
        assertTrue(document.contains("\"parameters\""));
        assertTrue(document.contains("\"projectId\""));
        assertTrue(document.contains("\"#/components/schemas/ApiError\""));
        assertTrue(document.contains("\"additionalProperties\": true"));
    }

    @Test
    public void errorEnvelopePropagatesRequestIdAndHidesInternalFailures() {
        UUID requestId = UUID.randomUUID();
        RuntimeException internal = new RuntimeException(
                "database password=super-secret at com.example.Internal.stack(Internal.java:42)");

        ApiErrorEnvelope envelope = ApiExceptionMapper.map(requestId, internal);

        assertEquals(requestId, envelope.requestId());
        assertEquals(ApiErrorCode.INTERNAL_ERROR, envelope.code());
        assertFalse(envelope.toString().contains("super-secret"));
        assertFalse(envelope.toString().contains("Internal.java"));
        assertFalse(envelope.toString().contains("RuntimeException"));
    }

    @Test
    public void knownAuthorizationFailureMapsToStructuredForbiddenWithoutRawMessage() {
        UUID requestId = UUID.randomUUID();
        ApiErrorEnvelope envelope =
                ApiExceptionMapper.map(requestId, new AccessDeniedException("DENY_NO_MATCHING_GRANT"));

        assertEquals(ApiErrorCode.FORBIDDEN, envelope.code());
        assertEquals("The requested operation is not permitted.", envelope.message());
        assertFalse(envelope.toString().contains("DENY_NO_MATCHING_GRANT"));
    }

    @Test
    public void unknownResponseFieldsAreIgnoredForForwardCompatibility() {
        UUID requestId = UUID.randomUUID();
        ApiErrorEnvelope decoded = ApiErrorEnvelope.fromMap(Map.of(
                "apiVersion", "v1",
                "requestId", requestId.toString(),
                "code", "NOT_FOUND",
                "message", "Not found",
                "details", Map.of("resource", "model"),
                "futureServerField", "newer-client-safe"));

        assertEquals(requestId, decoded.requestId());
        assertEquals(ApiErrorCode.NOT_FOUND, decoded.code());
        assertEquals("model", decoded.details().get("resource"));
    }

    @Test
    public void unsafeStructuredErrorDetailsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new ApiErrorEnvelope(
                "v1",
                UUID.randomUUID(),
                ApiErrorCode.BAD_REQUEST,
                "Bad request",
                Map.of("stackTrace", "secret internal detail")));

        assertThrows(IllegalArgumentException.class, () -> new ApiErrorEnvelope(
                "v1",
                UUID.randomUUID(),
                ApiErrorCode.BAD_REQUEST,
                "Bad request",
                Map.of("accessToken", "secret")));
    }

    @Test
    public void optionalFieldAdditionIsCompatibleButRemovalOrRequiredPromotionIsBreaking() {
        Map<String, ApiSchema> previous = Map.of(
                "Project", new ApiSchema("Project",
                        Map.of("id", ApiFieldType.STRING, "description", ApiFieldType.STRING),
                        Set.of("id")));

        Map<String, ApiSchema> optionalAddition = Map.of(
                "Project", new ApiSchema("Project",
                        Map.of("id", ApiFieldType.STRING, "description", ApiFieldType.STRING,
                                "owner", ApiFieldType.STRING),
                        Set.of("id")));
        assertTrue(ApiCompatibilityPolicy.compareSchemas(previous, optionalAddition).isEmpty());

        Map<String, ApiSchema> removedField = Map.of(
                "Project", new ApiSchema("Project",
                        Map.of("id", ApiFieldType.STRING),
                        Set.of("id")));
        assertFalse(ApiCompatibilityPolicy.compareSchemas(previous, removedField).isEmpty());

        Map<String, ApiSchema> optionalBecameRequired = Map.of(
                "Project", new ApiSchema("Project",
                        Map.of("id", ApiFieldType.STRING, "description", ApiFieldType.STRING),
                        Set.of("id", "description")));
        assertFalse(ApiCompatibilityPolicy.compareSchemas(previous, optionalBecameRequired).isEmpty());
    }

    @Test
    public void routeMutationIsBreakingButNewOperationIsCompatible() {
        Map<String, ApiOperation> previous = ApiContractRegistry.v1ByOperationId();
        Map<String, ApiOperation> candidate = new LinkedHashMap<>(previous);
        candidate.put("futureOperation",
                new ApiOperation("futureOperation", HttpMethod.GET, "/api/v1/future", "", "Health"));
        assertTrue(ApiCompatibilityPolicy.compareOperations(previous, candidate).isEmpty());

        ApiOperation old = previous.get("getProject");
        candidate = new LinkedHashMap<>(previous);
        candidate.put("getProject",
                new ApiOperation("getProject", HttpMethod.GET,
                        "/api/v1/project/{projectId}", old.requestSchema(), old.responseSchema()));
        assertFalse(ApiCompatibilityPolicy.compareOperations(previous, candidate).isEmpty());
    }

    @Test
    public void allReferencedSchemasExist() {
        Map<String, ApiSchema> schemas = ApiSchemaCatalog.v1();
        for (ApiOperation operation : ApiContractRegistry.v1()) {
            if (!operation.requestSchema().isEmpty()) {
                assertTrue("missing request schema " + operation.requestSchema(),
                        schemas.containsKey(operation.requestSchema()));
            }
            assertTrue("missing response schema " + operation.responseSchema(),
                    schemas.containsKey(operation.responseSchema()));
        }
        assertTrue(schemas.containsKey("ApiError"));
    }

    private static int count(String value, String needle) {
        int total = 0;
        int offset = 0;
        while ((offset = value.indexOf(needle, offset)) >= 0) {
            total++;
            offset += needle.length();
        }
        return total;
    }
}
