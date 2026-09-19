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
        add(schemas, new ApiSchema("ProjectCreateRequest", Set.of("displayName"), Set.of("portfolioId")));
        add(schemas, new ApiSchema("Project", Set.of("id", "displayName", "revision"), Set.of("portfolioId")));
        add(schemas, new ApiSchema("ProjectList", Set.of("items"), Set.of("nextCursor")));
        add(schemas, new ApiSchema("ModelWriteRequest", Set.of("content", "expectedRevision"), Set.of("mediaType")));
        add(schemas, new ApiSchema("Model", Set.of("id", "content", "revision", "etag"), Set.of("mediaType")));
        add(schemas, new ApiSchema("ModelList", Set.of("items"), Set.of("nextCursor")));
        add(schemas, new ApiSchema("KnowledgeQueryRequest", Set.of("query"), Set.of("scope", "limit")));
        add(schemas, new ApiSchema("KnowledgeQueryResult", Set.of("items"), Set.of("revision")));
        add(schemas, new ApiSchema("SynthesisRequest", Set.of("modelRevision"), Set.of("objectiveId", "parameters")));
        add(schemas, new ApiSchema("SynthesisResult", Set.of("resultId", "status", "revision"), Set.of("rationale")));
        add(schemas, new ApiSchema("EvidenceList", Set.of("items"), Set.of("nextCursor")));
        add(schemas, new ApiSchema("Health", Set.of("status", "version"), Set.of("dependencies")));
        add(schemas, new ApiSchema("ApiError", Set.of("apiVersion", "requestId", "code", "message"), Set.of("details")));
        return Map.copyOf(schemas);
    }

    private static void add(Map<String, ApiSchema> schemas, ApiSchema schema) {
        if (schemas.put(schema.name(), schema) != null) {
            throw new IllegalStateException("duplicate schema " + schema.name());
        }
    }
}
