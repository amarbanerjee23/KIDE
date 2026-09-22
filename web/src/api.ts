import type {
  ApiErrorEnvelope,
  Health,
  KnowledgeImpactResult,
  KnowledgeQueryResult,
  KnowledgeTraceList,
  KnowledgeTraceRelation,
  Model,
  PresenceList,
  PresenceSession,
  Project,
  ProjectList,
  ReviewApplyResult,
  ReviewBundle,
  ReviewChangeSet,
  ReviewChangeSetList,
  ReviewComment,
  SynthesisResult
} from "./types";

export class ApiClientError extends Error {
  constructor(
    message: string,
    readonly status: number,
    readonly envelope?: ApiErrorEnvelope
  ) {
    super(message);
    this.name = "ApiClientError";
  }

  get code(): ApiErrorEnvelope["code"] | undefined {
    return this.envelope?.code;
  }

  get requestId(): string | undefined {
    return this.envelope?.requestId;
  }
}

export class KideApiClient {
  private readonly root: string;

  constructor(
    serviceOrigin: string,
    private readonly accessToken: () => string
  ) {
    const normalized = serviceOrigin.trim().replace(/\/+$/, "");
    if (!normalized) throw new Error("Service origin is required");
    this.root = normalized.endsWith("/api/v1") ? normalized : `${normalized}/api/v1`;
  }

  health(): Promise<Health> {
    return this.request<Health>("/health", { method: "GET" }, false);
  }

  listProjects(): Promise<ProjectList> {
    return this.request<ProjectList>("/projects", { method: "GET" });
  }

  getProject(projectId: string): Promise<Project> {
    return this.request<Project>(
      `/projects/${encodeURIComponent(projectId)}`,
      { method: "GET" }
    );
  }

  getModel(projectId: string, modelId: string): Promise<Model> {
    return this.request<Model>(
      `/projects/${encodeURIComponent(projectId)}/models/${encodeURIComponent(modelId)}`,
      { method: "GET" }
    );
  }

  writeModel(
    projectId: string,
    modelId: string,
    content: string,
    expectedRevision: string,
    mediaType: string
  ): Promise<Model> {
    return this.request<Model>(
      `/projects/${encodeURIComponent(projectId)}/models/${encodeURIComponent(modelId)}`,
      {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ content, expectedRevision, mediaType })
      }
    );
  }

  listPresence(projectId: string): Promise<PresenceList> {
    return this.request<PresenceList>(
      `/projects/${encodeURIComponent(projectId)}/collaboration/sessions`,
      { method: "GET" }
    );
  }

  joinPresence(
    projectId: string,
    sessionId?: string,
    modelId?: string
  ): Promise<PresenceSession> {
    return this.request<PresenceSession>(
      `/projects/${encodeURIComponent(projectId)}/collaboration/sessions`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          ...(sessionId ? { sessionId } : {}),
          ...(modelId ? { modelId } : {})
        })
      }
    );
  }

  heartbeatPresence(
    projectId: string,
    sessionId: string,
    modelId?: string
  ): Promise<PresenceSession> {
    return this.request<PresenceSession>(
      `/projects/${encodeURIComponent(projectId)}/collaboration/sessions/${encodeURIComponent(sessionId)}`,
      {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(modelId ? { modelId } : {})
      }
    );
  }

  leavePresence(projectId: string, sessionId: string): Promise<PresenceSession> {
    return this.request<PresenceSession>(
      `/projects/${encodeURIComponent(projectId)}/collaboration/sessions/${encodeURIComponent(sessionId)}`,
      { method: "DELETE" }
    );
  }

  listReviewChangeSets(projectId: string): Promise<ReviewChangeSetList> {
    return this.request<ReviewChangeSetList>(
      `/projects/${encodeURIComponent(projectId)}/reviews/changesets`,
      { method: "GET" }
    );
  }

  createReviewChangeSet(
    projectId: string,
    modelId: string,
    baseEtag: string,
    proposedContent: string,
    mediaType: string
  ): Promise<ReviewChangeSet> {
    return this.request<ReviewChangeSet>(
      `/projects/${encodeURIComponent(projectId)}/reviews/changesets`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          modelId,
          baseEtag,
          proposedContent,
          mediaType
        })
      }
    );
  }

  getReviewChangeSet(projectId: string, changeSetId: string): Promise<ReviewBundle> {
    return this.request<ReviewBundle>(
      `/projects/${encodeURIComponent(projectId)}/reviews/changesets/${encodeURIComponent(changeSetId)}`,
      { method: "GET" }
    );
  }

  rebaseReviewChangeSet(
    projectId: string,
    changeSetId: string,
    expectedCurrentEtag: string,
    proposedContent: string
  ): Promise<ReviewChangeSet> {
    return this.request<ReviewChangeSet>(
      `/projects/${encodeURIComponent(projectId)}/reviews/changesets/${encodeURIComponent(changeSetId)}`,
      {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ expectedCurrentEtag, proposedContent })
      }
    );
  }

  markReviewReady(projectId: string, changeSetId: string): Promise<ReviewChangeSet> {
    return this.reviewAction(projectId, changeSetId, "ready");
  }

  approveReview(projectId: string, changeSetId: string): Promise<ReviewChangeSet> {
    return this.reviewAction(projectId, changeSetId, "approve");
  }

  applyReview(projectId: string, changeSetId: string): Promise<ReviewApplyResult> {
    return this.request<ReviewApplyResult>(
      `/projects/${encodeURIComponent(projectId)}/reviews/changesets/${encodeURIComponent(changeSetId)}/apply`,
      { method: "POST" }
    );
  }

  listReviewComments(projectId: string, changeSetId: string): Promise<{ items: ReviewComment[] }> {
    return this.request<{ items: ReviewComment[] }>(
      `/projects/${encodeURIComponent(projectId)}/reviews/changesets/${encodeURIComponent(changeSetId)}/comments`,
      { method: "GET" }
    );
  }

  addReviewComment(
    projectId: string,
    changeSetId: string,
    body: string,
    anchor = ""
  ): Promise<ReviewComment> {
    return this.request<ReviewComment>(
      `/projects/${encodeURIComponent(projectId)}/reviews/changesets/${encodeURIComponent(changeSetId)}/comments`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ body, anchor })
      }
    );
  }

  updateReviewComment(
    projectId: string,
    changeSetId: string,
    commentId: string,
    resolved: boolean
  ): Promise<ReviewComment> {
    return this.request<ReviewComment>(
      `/projects/${encodeURIComponent(projectId)}/reviews/changesets/${encodeURIComponent(changeSetId)}/comments/${encodeURIComponent(commentId)}`,
      {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ resolved })
      }
    );
  }

  synthesize(
    projectId: string,
    modelId: string,
    modelRevision: string
  ): Promise<SynthesisResult> {
    return this.request<SynthesisResult>(
      `/projects/${encodeURIComponent(projectId)}/synthesis`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ modelId, modelRevision })
      }
    );
  }

  queryKnowledge(
    projectId: string,
    query = "",
    typeIri = "",
    limit = 100
  ): Promise<KnowledgeQueryResult> {
    return this.request<KnowledgeQueryResult>(
      `/projects/${encodeURIComponent(projectId)}/knowledge/query`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ query, typeIri, limit })
      }
    );
  }

  listKnowledgeTraces(projectId: string): Promise<KnowledgeTraceList> {
    return this.request<KnowledgeTraceList>(
      `/projects/${encodeURIComponent(projectId)}/knowledge/traces`,
      { method: "GET" }
    );
  }

  createKnowledgeTrace(
    projectId: string,
    knowledgeIri: string,
    modelId: string,
    semanticId: string,
    relation: KnowledgeTraceRelation,
    expectedTraceEtag: string
  ): Promise<KnowledgeTraceList> {
    return this.request<KnowledgeTraceList>(
      `/projects/${encodeURIComponent(projectId)}/knowledge/traces`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          knowledgeIri,
          modelId,
          semanticId,
          relation,
          expectedTraceEtag
        })
      }
    );
  }

  rebindKnowledgeTrace(
    projectId: string,
    traceId: string,
    modelId: string,
    semanticId: string,
    expectedTraceEtag: string
  ): Promise<KnowledgeTraceList> {
    return this.request<KnowledgeTraceList>(
      `/projects/${encodeURIComponent(projectId)}/knowledge/traces/${encodeURIComponent(traceId)}`,
      {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ modelId, semanticId, expectedTraceEtag })
      }
    );
  }

  deleteKnowledgeTrace(
    projectId: string,
    traceId: string,
    expectedTraceEtag: string
  ): Promise<KnowledgeTraceList> {
    return this.request<KnowledgeTraceList>(
      `/projects/${encodeURIComponent(projectId)}/knowledge/traces/${encodeURIComponent(traceId)}`,
      {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ expectedTraceEtag })
      }
    );
  }

  queryKnowledgeImpact(
    projectId: string,
    knowledgeIri = "",
    modelId = ""
  ): Promise<KnowledgeImpactResult> {
    return this.request<KnowledgeImpactResult>(
      `/projects/${encodeURIComponent(projectId)}/knowledge/impact`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          ...(knowledgeIri ? { knowledgeIri } : {}),
          ...(modelId ? { modelId } : {})
        })
      }
    );
  }

  private reviewAction(
    projectId: string,
    changeSetId: string,
    action: "ready" | "approve"
  ): Promise<ReviewChangeSet> {
    return this.request<ReviewChangeSet>(
      `/projects/${encodeURIComponent(projectId)}/reviews/changesets/${encodeURIComponent(changeSetId)}/${action}`,
      { method: "POST" }
    );
  }

  private async request<T>(
    path: string,
    init: RequestInit,
    requireAuthentication = true
  ): Promise<T> {
    const headers = new Headers(init.headers);
    headers.set("Accept", "application/json");
    headers.set("X-Request-Id", crypto.randomUUID());

    if (requireAuthentication) {
      const token = this.accessToken().trim();
      if (!token) throw new ApiClientError("An access token is required.", 401);
      headers.set("Authorization", `Bearer ${token}`);
    }

    let response: Response;
    try {
      response = await fetch(`${this.root}${path}`, { ...init, headers });
    } catch {
      throw new ApiClientError("The KIDE service could not be reached.", 0);
    }

    const text = await response.text();
    const body = text ? safeJson(text) : undefined;
    if (!response.ok) {
      const envelope = isApiErrorEnvelope(body) ? body : undefined;
      throw new ApiClientError(
        envelope?.message ?? `KIDE service request failed with HTTP ${response.status}.`,
        response.status,
        envelope
      );
    }
    return body as T;
  }
}

function safeJson(text: string): unknown {
  try {
    return JSON.parse(text);
  } catch {
    return undefined;
  }
}

function isApiErrorEnvelope(value: unknown): value is ApiErrorEnvelope {
  if (!value || typeof value !== "object") return false;
  const candidate = value as Partial<ApiErrorEnvelope>;
  return (
    typeof candidate.apiVersion === "string" &&
    typeof candidate.requestId === "string" &&
    typeof candidate.code === "string" &&
    typeof candidate.message === "string"
  );
}
