package com.kide.enterprise.api;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class ApiSchemaCatalog {
    private static final Map<String, ApiSchema> V1 = build();

    private ApiSchemaCatalog() { }

    public static Map<String, ApiSchema> v1() {
        return V1;
    }

    private static Map<String, ApiSchema> build() {
        Map<String, ApiSchema> schemas = new LinkedHashMap<>();
        add(schemas, schema("ProjectCreateRequest",
                Map.of("displayName", s(), "portfolioId", s()), Set.of("displayName")));
        add(schemas, schema("Project",
                Map.of("id", s(), "displayName", s(), "revision", s(), "portfolioId", s(), "workspaceId", s()),
                Set.of("id", "displayName", "revision")));
        add(schemas, schema("ProjectList",
                Map.of("items", a(), "nextCursor", s()), Set.of("items")));
        add(schemas, schema("ModelWriteRequest",
                Map.of("content", s(), "expectedRevision", s(), "mediaType", s()),
                Set.of("content", "expectedRevision")));
        add(schemas, schema("Model",
                Map.of("id", s(), "content", s(), "revision", s(), "etag", s(), "mediaType", s()),
                Set.of("id", "content", "revision", "etag")));
        add(schemas, schema("ModelList",
                Map.of("items", a(), "nextCursor", s()), Set.of("items")));
        add(schemas, schema("KnowledgeQueryRequest",
                Map.of("query", s(), "scope", s(), "limit", i()), Set.of("query")));
        add(schemas, schema("KnowledgeQueryResult",
                Map.of("items", a(), "revision", s()), Set.of("items")));
        add(schemas, schema("SynthesisRequest",
                Map.of("modelRevision", s(), "objectiveId", s(), "parameters", o()),
                Set.of("modelRevision")));
        add(schemas, schema("SynthesisResult",
                Map.of("resultId", s(), "status", s(), "revision", s(), "rationale", a()),
                Set.of("resultId", "status", "revision")));
        add(schemas, schema("EvidenceList",
                Map.of("items", a(), "nextCursor", s()), Set.of("items")));
        add(schemas, schema("Health",
                Map.of("status", s(), "version", s(), "dependencies", o()),
                Set.of("status", "version")));
        add(schemas, schema("ApiError",
                Map.of("apiVersion", s(), "requestId", s(), "code", s(), "message", s(), "details", o()),
                Set.of("apiVersion", "requestId", "code", "message")));
        return Map.copyOf(schemas);
    }

    private static ApiSchema schema(String name, Map<String, ApiFieldType> fields, Set<String> required) {
        return new ApiSchema(name, fields, required);
    }

    private static ApiFieldType s() { return ApiFieldType.STRING; }
    private static ApiFieldType i() { return ApiFieldType.INTEGER; }
    private static ApiFieldType o() { return ApiFieldType.OBJECT; }
    private static ApiFieldType a() { return ApiFieldType.ARRAY; }

    private static void add(Map<String, ApiSchema> schemas, ApiSchema schema) {
        if (schemas.put(schema.name(), schema) != null) {
            throw new IllegalStateException("duplicate schema " + schema.name());
        }
    }
}
