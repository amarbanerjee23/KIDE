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
        add(schemas, schema("PresenceJoinRequest",
                Map.of("sessionId", s(), "modelId", s()), Set.of()));
        add(schemas, schema("PresenceHeartbeatRequest",
                Map.of("modelId", s()), Set.of()));
        add(schemas, schema("PresenceSession",
                Map.of("id", s(), "principalId", s(), "displayName", s(), "modelId", s(),
                        "joinedAt", s(), "lastSeenAt", s()),
                Set.of("id", "principalId", "displayName", "joinedAt", "lastSeenAt")));
        add(schemas, schema("PresenceList",
                Map.of("items", a()), Set.of("items")));
        add(schemas, schema("ReviewChangeSetCreateRequest",
                Map.of("modelId", s(), "baseEtag", s(), "proposedContent", s(), "mediaType", s()),
                Set.of("modelId", "baseEtag", "proposedContent")));
        add(schemas, schema("ReviewChangeSetUpdateRequest",
                Map.of("expectedCurrentEtag", s(), "proposedContent", s()),
                Set.of("expectedCurrentEtag", "proposedContent")));
        add(schemas, schema("ReviewChangeSet",
                Map.ofEntries(
                        Map.entry("id", s()), Map.entry("modelId", s()),
                        Map.entry("baseEtag", s()), Map.entry("baseRevision", s()),
                        Map.entry("proposedContent", s()), Map.entry("mediaType", s()),
                        Map.entry("authorId", s()), Map.entry("authorName", s()),
                        Map.entry("status", s()), Map.entry("createdAt", s()),
                        Map.entry("updatedAt", s()), Map.entry("reviewRevision", i()),
                        Map.entry("approvedBy", s()), Map.entry("approvedAt", s()),
                        Map.entry("appliedEtag", s()), Map.entry("appliedRevision", s())),
                Set.of("id", "modelId", "baseEtag", "authorId", "authorName", "status",
                        "createdAt", "updatedAt", "reviewRevision")));
        add(schemas, schema("ReviewChangeSetList",
                Map.of("items", a()), Set.of("items")));
        add(schemas, schema("ReviewBundle",
                Map.of("changeSet", o(), "currentModel", o(), "comments", a(), "conflicted", b()),
                Set.of("changeSet", "currentModel", "comments", "conflicted")));
        add(schemas, schema("ReviewCommentCreateRequest",
                Map.of("body", s(), "anchor", s()), Set.of("body")));
        add(schemas, schema("ReviewCommentUpdateRequest",
                Map.of("resolved", b()), Set.of("resolved")));
        add(schemas, schema("ReviewComment",
                Map.ofEntries(
                        Map.entry("id", s()), Map.entry("changeSetId", s()),
                        Map.entry("authorId", s()), Map.entry("authorName", s()),
                        Map.entry("body", s()), Map.entry("anchor", s()),
                        Map.entry("createdAt", s()), Map.entry("resolved", b()),
                        Map.entry("resolvedBy", s()), Map.entry("resolvedAt", s())),
                Set.of("id", "changeSetId", "authorId", "authorName", "body",
                        "createdAt", "resolved")));
        add(schemas, schema("ReviewCommentList",
                Map.of("items", a()), Set.of("items")));
        add(schemas, schema("ReviewApplyResult",
                Map.of("changeSet", o(), "model", o()), Set.of("changeSet", "model")));
        add(schemas, schema("KnowledgeQueryRequest",
                Map.of("query", s(), "scope", s(), "typeIri", s(), "limit", i()), Set.of()));
        add(schemas, schema("KnowledgeQueryResult",
                Map.of("items", a(), "revision", i(), "etag", s(), "cached", b()),
                Set.of("items", "revision", "etag")));
        add(schemas, schema("KnowledgeTraceCreateRequest",
                Map.of("knowledgeIri", s(), "modelId", s(), "semanticId", s(),
                        "relation", s(), "expectedTraceEtag", s()),
                Set.of("knowledgeIri", "modelId", "relation", "expectedTraceEtag")));
        add(schemas, schema("KnowledgeTraceRebindRequest",
                Map.of("modelId", s(), "semanticId", s(), "expectedTraceEtag", s()),
                Set.of("modelId", "expectedTraceEtag")));
        add(schemas, schema("KnowledgeTraceDeleteRequest",
                Map.of("expectedTraceEtag", s()), Set.of("expectedTraceEtag")));
        add(schemas, schema("KnowledgeTraceList",
                Map.of("revision", i(), "etag", s(), "links", a()),
                Set.of("revision", "etag", "links")));
        add(schemas, schema("KnowledgeImpactRequest",
                Map.of("knowledgeIri", s(), "modelId", s()), Set.of()));
        add(schemas, schema("KnowledgeImpactResult",
                Map.of("items", a(), "issues", a(), "knowledgeRevision", i(), "traceRevision", i()),
                Set.of("items", "issues", "knowledgeRevision", "traceRevision")));
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
    private static ApiFieldType b() { return ApiFieldType.BOOLEAN; }
    private static ApiFieldType o() { return ApiFieldType.OBJECT; }
    private static ApiFieldType a() { return ApiFieldType.ARRAY; }

    private static void add(Map<String, ApiSchema> schemas, ApiSchema schema) {
        if (schemas.put(schema.name(), schema) != null) {
            throw new IllegalStateException("duplicate schema " + schema.name());
        }
    }
}
