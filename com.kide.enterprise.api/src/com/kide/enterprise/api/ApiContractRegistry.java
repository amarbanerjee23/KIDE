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
            new ApiOperation("queryKnowledge", HttpMethod.POST, "/api/v1/projects/{projectId}/knowledge/query", "KnowledgeQueryRequest", "KnowledgeQueryResult"),
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
