/*
 * GENERATED FILE. DO NOT EDIT.
 * Source: packaged KIDE OpenAPI v1 (OpenApiV1.generateJson()).
 * Regenerate with scripts/generate_web_api_client.py.
 */

export type GeneratedHttpMethod = "DELETE" | "GET" | "POST" | "PUT";

export interface GeneratedApiCall {
  operationId: string;
  method: GeneratedHttpMethod;
  path: string;
  requestSchema?: string;
  responseSchema: string;
}

function segment(value: string): string {
  return encodeURIComponent(value);
}

export const apiV1 = {
  applyReview(projectId: string, changeSetId: string): GeneratedApiCall {
    return {
      operationId: "applyReview",
      method: "POST",
      path: `/projects/${segment(projectId)}/reviews/changesets/${segment(changeSetId)}/apply`,
      responseSchema: "ReviewApplyResult"
    };
  },
  approveReview(projectId: string, changeSetId: string): GeneratedApiCall {
    return {
      operationId: "approveReview",
      method: "POST",
      path: `/projects/${segment(projectId)}/reviews/changesets/${segment(changeSetId)}/approve`,
      responseSchema: "ReviewChangeSet"
    };
  },
  createKnowledgeTrace(projectId: string): GeneratedApiCall {
    return {
      operationId: "createKnowledgeTrace",
      method: "POST",
      path: `/projects/${segment(projectId)}/knowledge/traces`,
      requestSchema: "KnowledgeTraceCreateRequest",
      responseSchema: "KnowledgeTraceList"
    };
  },
  createProject(): GeneratedApiCall {
    return {
      operationId: "createProject",
      method: "POST",
      path: "/projects",
      requestSchema: "ProjectCreateRequest",
      responseSchema: "Project"
    };
  },
  createReviewChangeSet(projectId: string): GeneratedApiCall {
    return {
      operationId: "createReviewChangeSet",
      method: "POST",
      path: `/projects/${segment(projectId)}/reviews/changesets`,
      requestSchema: "ReviewChangeSetCreateRequest",
      responseSchema: "ReviewChangeSet"
    };
  },
  createReviewComment(projectId: string, changeSetId: string): GeneratedApiCall {
    return {
      operationId: "createReviewComment",
      method: "POST",
      path: `/projects/${segment(projectId)}/reviews/changesets/${segment(changeSetId)}/comments`,
      requestSchema: "ReviewCommentCreateRequest",
      responseSchema: "ReviewComment"
    };
  },
  createStarterModels(projectId: string): GeneratedApiCall {
    return {
      operationId: "createStarterModels",
      method: "POST",
      path: `/projects/${segment(projectId)}/models`,
      responseSchema: "ModelList"
    };
  },
  deleteKnowledgeTrace(projectId: string, traceId: string): GeneratedApiCall {
    return {
      operationId: "deleteKnowledgeTrace",
      method: "DELETE",
      path: `/projects/${segment(projectId)}/knowledge/traces/${segment(traceId)}`,
      requestSchema: "KnowledgeTraceDeleteRequest",
      responseSchema: "KnowledgeTraceList"
    };
  },
  generate(projectId: string): GeneratedApiCall {
    return {
      operationId: "generate",
      method: "POST",
      path: `/projects/${segment(projectId)}/generation`,
      requestSchema: "GenerationRequest",
      responseSchema: "GenerationResult"
    };
  },
  getModel(projectId: string, modelId: string): GeneratedApiCall {
    return {
      operationId: "getModel",
      method: "GET",
      path: `/projects/${segment(projectId)}/models/${segment(modelId)}`,
      responseSchema: "Model"
    };
  },
  getProject(projectId: string): GeneratedApiCall {
    return {
      operationId: "getProject",
      method: "GET",
      path: `/projects/${segment(projectId)}`,
      responseSchema: "Project"
    };
  },
  getReviewChangeSet(projectId: string, changeSetId: string): GeneratedApiCall {
    return {
      operationId: "getReviewChangeSet",
      method: "GET",
      path: `/projects/${segment(projectId)}/reviews/changesets/${segment(changeSetId)}`,
      responseSchema: "ReviewBundle"
    };
  },
  health(): GeneratedApiCall {
    return {
      operationId: "health",
      method: "GET",
      path: "/health",
      responseSchema: "Health"
    };
  },
  heartbeatPresence(projectId: string, sessionId: string): GeneratedApiCall {
    return {
      operationId: "heartbeatPresence",
      method: "PUT",
      path: `/projects/${segment(projectId)}/collaboration/sessions/${segment(sessionId)}`,
      requestSchema: "PresenceHeartbeatRequest",
      responseSchema: "PresenceSession"
    };
  },
  joinPresence(projectId: string): GeneratedApiCall {
    return {
      operationId: "joinPresence",
      method: "POST",
      path: `/projects/${segment(projectId)}/collaboration/sessions`,
      requestSchema: "PresenceJoinRequest",
      responseSchema: "PresenceSession"
    };
  },
  leavePresence(projectId: string, sessionId: string): GeneratedApiCall {
    return {
      operationId: "leavePresence",
      method: "DELETE",
      path: `/projects/${segment(projectId)}/collaboration/sessions/${segment(sessionId)}`,
      responseSchema: "PresenceSession"
    };
  },
  listEvidence(projectId: string): GeneratedApiCall {
    return {
      operationId: "listEvidence",
      method: "GET",
      path: `/projects/${segment(projectId)}/evidence`,
      responseSchema: "EvidenceList"
    };
  },
  listKnowledgeTraces(projectId: string): GeneratedApiCall {
    return {
      operationId: "listKnowledgeTraces",
      method: "GET",
      path: `/projects/${segment(projectId)}/knowledge/traces`,
      responseSchema: "KnowledgeTraceList"
    };
  },
  listModels(projectId: string): GeneratedApiCall {
    return {
      operationId: "listModels",
      method: "GET",
      path: `/projects/${segment(projectId)}/models`,
      responseSchema: "ModelList"
    };
  },
  listPresence(projectId: string): GeneratedApiCall {
    return {
      operationId: "listPresence",
      method: "GET",
      path: `/projects/${segment(projectId)}/collaboration/sessions`,
      responseSchema: "PresenceList"
    };
  },
  listProjects(): GeneratedApiCall {
    return {
      operationId: "listProjects",
      method: "GET",
      path: "/projects",
      responseSchema: "ProjectList"
    };
  },
  listReviewChangeSets(projectId: string): GeneratedApiCall {
    return {
      operationId: "listReviewChangeSets",
      method: "GET",
      path: `/projects/${segment(projectId)}/reviews/changesets`,
      responseSchema: "ReviewChangeSetList"
    };
  },
  listReviewComments(projectId: string, changeSetId: string): GeneratedApiCall {
    return {
      operationId: "listReviewComments",
      method: "GET",
      path: `/projects/${segment(projectId)}/reviews/changesets/${segment(changeSetId)}/comments`,
      responseSchema: "ReviewCommentList"
    };
  },
  markReviewReady(projectId: string, changeSetId: string): GeneratedApiCall {
    return {
      operationId: "markReviewReady",
      method: "POST",
      path: `/projects/${segment(projectId)}/reviews/changesets/${segment(changeSetId)}/ready`,
      responseSchema: "ReviewChangeSet"
    };
  },
  putModel(projectId: string, modelId: string): GeneratedApiCall {
    return {
      operationId: "putModel",
      method: "PUT",
      path: `/projects/${segment(projectId)}/models/${segment(modelId)}`,
      requestSchema: "ModelWriteRequest",
      responseSchema: "Model"
    };
  },
  queryKnowledge(projectId: string): GeneratedApiCall {
    return {
      operationId: "queryKnowledge",
      method: "POST",
      path: `/projects/${segment(projectId)}/knowledge/query`,
      requestSchema: "KnowledgeQueryRequest",
      responseSchema: "KnowledgeQueryResult"
    };
  },
  queryKnowledgeImpact(projectId: string): GeneratedApiCall {
    return {
      operationId: "queryKnowledgeImpact",
      method: "POST",
      path: `/projects/${segment(projectId)}/knowledge/impact`,
      requestSchema: "KnowledgeImpactRequest",
      responseSchema: "KnowledgeImpactResult"
    };
  },
  rebaseReviewChangeSet(projectId: string, changeSetId: string): GeneratedApiCall {
    return {
      operationId: "rebaseReviewChangeSet",
      method: "PUT",
      path: `/projects/${segment(projectId)}/reviews/changesets/${segment(changeSetId)}`,
      requestSchema: "ReviewChangeSetUpdateRequest",
      responseSchema: "ReviewChangeSet"
    };
  },
  rebindKnowledgeTrace(projectId: string, traceId: string): GeneratedApiCall {
    return {
      operationId: "rebindKnowledgeTrace",
      method: "PUT",
      path: `/projects/${segment(projectId)}/knowledge/traces/${segment(traceId)}`,
      requestSchema: "KnowledgeTraceRebindRequest",
      responseSchema: "KnowledgeTraceList"
    };
  },
  reconfigure(projectId: string): GeneratedApiCall {
    return {
      operationId: "reconfigure",
      method: "POST",
      path: `/projects/${segment(projectId)}/reconfiguration`,
      requestSchema: "ReconfigurationRequest",
      responseSchema: "ReconfigurationResult"
    };
  },
  synthesize(projectId: string): GeneratedApiCall {
    return {
      operationId: "synthesize",
      method: "POST",
      path: `/projects/${segment(projectId)}/synthesis`,
      requestSchema: "SynthesisRequest",
      responseSchema: "SynthesisResult"
    };
  },
  updateReviewComment(projectId: string, changeSetId: string, commentId: string): GeneratedApiCall {
    return {
      operationId: "updateReviewComment",
      method: "PUT",
      path: `/projects/${segment(projectId)}/reviews/changesets/${segment(changeSetId)}/comments/${segment(commentId)}`,
      requestSchema: "ReviewCommentUpdateRequest",
      responseSchema: "ReviewComment"
    };
  },
  version(): GeneratedApiCall {
    return {
      operationId: "version",
      method: "GET",
      path: "/version",
      responseSchema: "RuntimeVersion"
    };
  },
} as const;

export type ApiV1OperationId = keyof typeof apiV1;
