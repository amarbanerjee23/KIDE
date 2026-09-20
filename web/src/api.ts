import type { ApiErrorEnvelope, Health, Model, Project, ProjectList } from "./types";

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
