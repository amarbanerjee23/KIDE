package com.kide.enterprise.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ApiContractRegistry {
    private static final List<ApiOperation> V1_OPERATIONS = List.of(
            new ApiOperation("listProjects", HttpMethod.GET, "/api/v1/projects", "", "ProjectList"),
            new ApiOperation("createProject", HttpMethod.POST, "/api/v1/projects", "ProjectCreateRequest", "Project"),
            new ApiOperation("getProject", HttpMethod.GET, "/api/v1/projects/{projectId}", "", "Project"),
            new ApiOperation("listModels", HttpMethod.GET, "/api/v1/projects/{projectId}/models", "", "ModelList"),
            new ApiOperation("getModel", HttpMethod.GET, "/api/v1/projects/{projectId}/models/{modelId}", "", "Model"),
            new ApiOperation("putModel", HttpMethod.PUT, "/api/v1/projects/{projectId}/models/{modelId}", "ModelWriteRequest", "Model"),
            new ApiOperation("listPresence", HttpMethod.GET, "/api/v1/projects/{projectId}/collaboration/sessions", "", "PresenceList"),
            new ApiOperation("joinPresence", HttpMethod.POST, "/api/v1/projects/{projectId}/collaboration/sessions", "PresenceJoinRequest", "PresenceSession"),
            new ApiOperation("heartbeatPresence", HttpMethod.PUT, "/api/v1/projects/{projectId}/collaboration/sessions/{sessionId}", "PresenceHeartbeatRequest", "PresenceSession"),
            new ApiOperation("leavePresence", HttpMethod.DELETE, "/api/v1/projects/{projectId}/collaboration/sessions/{sessionId}", "", "PresenceSession"),
            new ApiOperation("listReviewChangeSets", HttpMethod.GET, "/api/v1/projects/{projectId}/reviews/changesets", "", "ReviewChangeSetList"),
            new ApiOperation("createReviewChangeSet", HttpMethod.POST, "/api/v1/projects/{projectId}/reviews/changesets", "ReviewChangeSetCreateRequest", "ReviewChangeSet"),
            new ApiOperation("getReviewChangeSet", HttpMethod.GET, "/api/v1/projects/{projectId}/reviews/changesets/{changeSetId}", "", "ReviewBundle"),
            new ApiOperation("rebaseReviewChangeSet", HttpMethod.PUT, "/api/v1/projects/{projectId}/reviews/changesets/{changeSetId}", "ReviewChangeSetUpdateRequest", "ReviewChangeSet"),
            new ApiOperation("markReviewReady", HttpMethod.POST, "/api/v1/projects/{projectId}/reviews/changesets/{changeSetId}/ready", "", "ReviewChangeSet"),
            new ApiOperation("approveReview", HttpMethod.POST, "/api/v1/projects/{projectId}/reviews/changesets/{changeSetId}/approve", "", "ReviewChangeSet"),
            new ApiOperation("applyReview", HttpMethod.POST, "/api/v1/projects/{projectId}/reviews/changesets/{changeSetId}/apply", "", "ReviewApplyResult"),
            new ApiOperation("listReviewComments", HttpMethod.GET, "/api/v1/projects/{projectId}/reviews/changesets/{changeSetId}/comments", "", "ReviewCommentList"),
            new ApiOperation("createReviewComment", HttpMethod.POST, "/api/v1/projects/{projectId}/reviews/changesets/{changeSetId}/comments", "ReviewCommentCreateRequest", "ReviewComment"),
            new ApiOperation("updateReviewComment", HttpMethod.PUT, "/api/v1/projects/{projectId}/reviews/changesets/{changeSetId}/comments/{commentId}", "ReviewCommentUpdateRequest", "ReviewComment"),
            new ApiOperation("queryKnowledge", HttpMethod.POST, "/api/v1/projects/{projectId}/knowledge/query", "KnowledgeQueryRequest", "KnowledgeQueryResult"),
            new ApiOperation("listKnowledgeTraces", HttpMethod.GET, "/api/v1/projects/{projectId}/knowledge/traces", "", "KnowledgeTraceList"),
            new ApiOperation("createKnowledgeTrace", HttpMethod.POST, "/api/v1/projects/{projectId}/knowledge/traces", "KnowledgeTraceCreateRequest", "KnowledgeTraceList"),
            new ApiOperation("rebindKnowledgeTrace", HttpMethod.PUT, "/api/v1/projects/{projectId}/knowledge/traces/{traceId}", "KnowledgeTraceRebindRequest", "KnowledgeTraceList"),
            new ApiOperation("deleteKnowledgeTrace", HttpMethod.DELETE, "/api/v1/projects/{projectId}/knowledge/traces/{traceId}", "KnowledgeTraceDeleteRequest", "KnowledgeTraceList"),
            new ApiOperation("queryKnowledgeImpact", HttpMethod.POST, "/api/v1/projects/{projectId}/knowledge/impact", "KnowledgeImpactRequest", "KnowledgeImpactResult"),
            new ApiOperation("synthesize", HttpMethod.POST, "/api/v1/projects/{projectId}/synthesis", "SynthesisRequest", "SynthesisResult"),
            new ApiOperation("listEvidence", HttpMethod.GET, "/api/v1/projects/{projectId}/evidence", "", "EvidenceList"),
            new ApiOperation("health", HttpMethod.GET, "/api/v1/health", "", "Health"));

    private ApiContractRegistry() { }

    public static List<ApiOperation> v1() {
        return V1_OPERATIONS;
    }

    public static Map<String, ApiOperation> v1ByOperationId() {
        Map<String, ApiOperation> result = new LinkedHashMap<>();
        for (ApiOperation operation : V1_OPERATIONS) {
            if (result.put(operation.operationId(), operation) != null) {
                throw new IllegalStateException("duplicate operationId");
            }
        }
        return Map.copyOf(result);
    }
}
