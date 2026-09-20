export type ApiErrorCode =
  | "BAD_REQUEST"
  | "UNAUTHENTICATED"
  | "FORBIDDEN"
  | "NOT_FOUND"
  | "CONFLICT"
  | "TOO_LARGE"
  | "RATE_LIMITED"
  | "SERVICE_UNAVAILABLE"
  | "INTERNAL_ERROR";

export interface ApiErrorEnvelope {
  apiVersion: string;
  requestId: string;
  code: ApiErrorCode;
  message: string;
  details?: Record<string, string>;
}

export interface Health {
  status: string;
  version: string;
  dependencies?: Record<string, unknown>;
}

export interface Project {
  id: string;
  displayName: string;
  revision: string;
  portfolioId?: string;
}

export interface ProjectList {
  items: Project[];
  nextCursor?: string;
}

export interface Model {
  id: string;
  content: string;
  revision: string;
  etag: string;
  mediaType?: string;
}

export type SaveState = "clean" | "pending" | "saving" | "saved" | "conflict" | "error";

export interface WorkspaceEntry {
  path: string;
  bytes: Uint8Array;
  mediaType: string;
  source: "archive" | "remote";
  projectId?: string;
  etag?: string;
  revision?: string;
  dirty: boolean;
}
