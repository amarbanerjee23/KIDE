import { apiV1, type GeneratedApiCall } from "./generated/api-v1";
import { validateGenerationResult } from "./generationIntegrity";
import type {
  ApiErrorEnvelope,
  GenerationResult,
  Health,
  KnowledgeImpactResult,
  KnowledgeQueryResult,
  KnowledgeTraceList,
  KnowledgeTraceRelation,
  Model,
  ModelList,
  PresenceList,
  PresenceSession,
  Project,
  ProjectArchiveImportEntry,
  ProjectArchiveImportResult,
  ProjectList,
  ReviewApplyResult,
  ReviewBundle,
  ReviewChangeSet,
  ReviewChangeSetList,
  ReviewComment,
  ReconfigurationCause,
  ReconfigurationResult,
  RuntimeVersion,
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
    private readonly accessToken: () => string | Promise<string>
  ) {
    const normalized = serviceOrigin.trim().replace(/\/+$/, "");
    if (!normalized) throw new Error("Service origin is required");
    this.root = normalized.endsWith("/api/v1") ? normalized : `${normalized}/api/v1`;
  }

  health(): Promise<Health> {
    return this.request<Health>(apiV1.health(), {}, false);
  }

  version(): Promise<RuntimeVersion> {
    return this.request<RuntimeVersion>(apiV1.version(), {}, false);
  }

  listProjects(): Promise<ProjectList> {
    return this.request<ProjectList>(apiV1.listProjects(), {});
  }

  getProject(projectId: string): Promise<Project> {
    return this.request<Project>(
      apiV1.getProject(projectId),
      {}
    );
  }

  listModels(projectId: string): Promise<ModelList> {
    return this.request<ModelList>(
      apiV1.listModels(projectId),
      {}
    );
  }

  createStarterModels(projectId: string): Promise<ModelList> {
    return this.request<ModelList>(
      apiV1.createStarterModels(projectId),
      {}
    );
  }

  importProjectArchive(
    projectId: string,
    items: ProjectArchiveImportEntry[],
    archiveName?: string
  ): Promise<ProjectArchiveImportResult> {
    return this.request<ProjectArchiveImportResult>(
      apiV1.importProjectArchive(projectId),
      {
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          items,
          ...(archiveName ? { archiveName } : {})
        })
      }
    );
  }

  getModel(projectId: string, modelId: string): Promise<Model> {
    return this.request<Model>(
      apiV1.getModel(projectId, modelId),
      {}
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
      apiV1.putModel(projectId, modelId),
      {
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ content, expectedRevision, mediaType })
      }
    );
  }

  listPresence(projectId: string): Promise<PresenceList> {
    return this.request<PresenceList>(
      apiV1.listPresence(projectId),
      {}
    );
  }

  joinPresence(
    projectId: string,
    sessionId?: string,
    modelId?: string
  ): Promise<PresenceSession> {
    return this.request<PresenceSession>(
      apiV1.joinPresence(projectId),
      {
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
      apiV1.heartbeatPresence(projectId, sessionId),
      {
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(modelId ? { modelId } : {})
      }
    );
  }

  leavePresence(projectId: string, sessionId: string): Promise<PresenceSession> {
    return this.request<PresenceSession>(
      apiV1.leavePresence(projectId, sessionId),
      {}
    );
  }

  listReviewChangeSets(projectId: string): Promise<ReviewChangeSetList> {
    return this.request<ReviewChangeSetList>(
      apiV1.listReviewChangeSets(projectId),
      {}
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
      apiV1.createReviewChangeSet(projectId),
      {
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
      apiV1.getReviewChangeSet(projectId, changeSetId),
      {}
    );
  }

  rebaseReviewChangeSet(
    projectId: string,
    changeSetId: string,
    expectedCurrentEtag: string,
    proposedContent: string
  ): Promise<ReviewChangeSet> {
    return this.request<ReviewChangeSet>(
      apiV1.rebaseReviewChangeSet(projectId, changeSetId),
      {
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
      apiV1.applyReview(projectId, changeSetId),
      {}
    );
  }

  listReviewComments(projectId: string, changeSetId: string): Promise<{ items: ReviewComment[] }> {
    return this.request<{ items: ReviewComment[] }>(
      apiV1.listReviewComments(projectId, changeSetId),
      {}
    );
  }

  addReviewComment(
    projectId: string,
    changeSetId: string,
    body: string,
    anchor = ""
  ): Promise<ReviewComment> {
    return this.request<ReviewComment>(
      apiV1.createReviewComment(projectId, changeSetId),
      {
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
      apiV1.updateReviewComment(projectId, changeSetId, commentId),
      {
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
      apiV1.synthesize(projectId),
      {
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ modelId, modelRevision })
      }
    );
  }

  reconfigure(
    projectId: string,
    modelId: string,
    modelRevision: string,
    cause: ReconfigurationCause,
    previousBindings: Array<Pick<
      SynthesisResult["selections"][number],
      "requirementId" | "resourceId"
    >>
  ): Promise<ReconfigurationResult> {
    return this.request<ReconfigurationResult>(
      apiV1.reconfigure(projectId),
      {
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          modelId,
          modelRevision,
          cause,
          previousBindings
        })
      }
    );
  }

  async generate(
    projectId: string,
    sourceModelId: string,
    sourceRevision: string,
    krlModelId: string,
    krlRevision: string,
    synthesisFingerprint: string
  ): Promise<GenerationResult> {
    const result = await this.request<GenerationResult>(
      apiV1.generate(projectId),
      {
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          sourceModelId,
          sourceRevision,
          krlModelId,
          krlRevision,
          synthesisFingerprint
        })
      }
    );
    return validateGenerationResult(result, {
      sourceModelId,
      sourceRevision,
      krlModelId,
      krlRevision,
      synthesisFingerprint
    });
  }

  queryKnowledge(
    projectId: string,
    query = "",
    typeIri = "",
    limit = 100
  ): Promise<KnowledgeQueryResult> {
    return this.request<KnowledgeQueryResult>(
      apiV1.queryKnowledge(projectId),
      {
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ query, typeIri, limit })
      }
    );
  }

  listKnowledgeTraces(projectId: string): Promise<KnowledgeTraceList> {
    return this.request<KnowledgeTraceList>(
      apiV1.listKnowledgeTraces(projectId),
      {}
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
      apiV1.createKnowledgeTrace(projectId),
      {
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
      apiV1.rebindKnowledgeTrace(projectId, traceId),
      {
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
      apiV1.deleteKnowledgeTrace(projectId, traceId),
      {
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
      apiV1.queryKnowledgeImpact(projectId),
      {
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
    const call =
      action === "ready"
        ? apiV1.markReviewReady(projectId, changeSetId)
        : apiV1.approveReview(projectId, changeSetId);
    return this.request<ReviewChangeSet>(call, {});
  }

  private async request<T>(
    call: GeneratedApiCall,
    init: Omit<RequestInit, "method">,
    requireAuthentication = true
  ): Promise<T> {
    const headers = new Headers(init.headers);
    headers.set("Accept", "application/json");
    headers.set("X-Request-Id", crypto.randomUUID());

    if (requireAuthentication) {
      const token = (await this.accessToken()).trim();
      if (!token) throw new ApiClientError("An access token is required.", 401);
      headers.set("Authorization", `Bearer ${token}`);
    }

    if (call.requestSchema && init.body === undefined) {
      throw new ApiClientError(
        `Generated operation ${call.operationId} requires request schema ${call.requestSchema}.`,
        0
      );
    }
    if (!call.requestSchema && init.body !== undefined) {
      throw new ApiClientError(
        `Generated operation ${call.operationId} does not accept a request body.`,
        0
      );
    }

    let response: Response;
    try {
      response = await fetch(`${this.root}${call.path}`, {
        ...init,
        method: call.method,
        headers
      });
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
    if (body === undefined) {
      throw new ApiClientError(
        `Generated operation ${call.operationId} expected response schema ${call.responseSchema}, but the response was empty or invalid JSON.`,
        response.status
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
