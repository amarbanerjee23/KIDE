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
  workspaceId?: string;
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


export interface PresenceSession {
  id: string;
  principalId: string;
  displayName: string;
  modelId?: string;
  joinedAt: string;
  lastSeenAt: string;
}

export interface PresenceList {
  items: PresenceSession[];
}

export type ReviewChangeSetStatus =
  | "DRAFT"
  | "CONFLICT"
  | "READY"
  | "APPROVED"
  | "APPLIED";

export interface ReviewChangeSet {
  id: string;
  modelId: string;
  baseEtag: string;
  baseRevision: string;
  proposedContent?: string;
  mediaType: string;
  authorId: string;
  authorName: string;
  status: ReviewChangeSetStatus;
  createdAt: string;
  updatedAt: string;
  reviewRevision: number;
  approvedBy?: string;
  approvedAt?: string;
  appliedEtag?: string;
  appliedRevision?: string;
}

export interface ReviewChangeSetList {
  items: ReviewChangeSet[];
}

export interface ReviewComment {
  id: string;
  changeSetId: string;
  authorId: string;
  authorName: string;
  body: string;
  anchor?: string;
  createdAt: string;
  resolved: boolean;
  resolvedBy?: string;
  resolvedAt?: string;
}

export interface ReviewBundle {
  changeSet: ReviewChangeSet & { proposedContent: string };
  currentModel: Model;
  comments: ReviewComment[];
  conflicted: boolean;
}

export interface ReviewApplyResult {
  changeSet: ReviewChangeSet & { proposedContent: string };
  model: Model;
}


export interface KnowledgeCatalogueItem {
  iri: string;
  label: string;
  types: string[];
  properties: Record<string, string[]>;
  provenanceSource: string;
  authority: string;
}

export interface KnowledgeQueryResult {
  revision: number;
  etag: string;
  items: KnowledgeCatalogueItem[];
  cached: boolean;
}

export type KnowledgeTraceRelation =
  | "SATISFIES"
  | "DERIVED_FROM"
  | "SELECTS"
  | "DECLARES"
  | "REALIZES"
  | "DEPENDS_ON";

export interface KnowledgeTraceLink {
  id: string;
  knowledgeIri: string;
  modelPath: string;
  semanticId: string;
  relation: KnowledgeTraceRelation;
  sourceAuthority: string;
  provenanceSource: string;
  createdBy: string;
  updatedBy: string;
  createdAtEpochMillis: number;
  updatedAtEpochMillis: number;
  knowledgeEtagAtBind: string;
  modelEtagAtBind: string;
}

export interface KnowledgeTraceList {
  revision: number;
  etag: string;
  links: KnowledgeTraceLink[];
}

export interface KnowledgeTraceIssue {
  traceId: string;
  code: "BROKEN_KNOWLEDGE" | "BROKEN_MODEL" | "STALE_KNOWLEDGE" | "STALE_MODEL" | string;
  message: string;
}

export interface KnowledgeImpactResult {
  items: KnowledgeTraceLink[];
  issues: KnowledgeTraceIssue[];
  knowledgeRevision: number;
  traceRevision: number;
}


export type SynthesisStatus = "SUCCESS" | "NO_SOLUTION" | "CONTRACT_VIOLATION";

export interface SynthesisSelection {
  requirementId: string;
  activityName: string;
  capabilityName: string;
  resourceId: string;
  rationale: string;
}

export interface SynthesisDiagnostic {
  severity: "INFO" | "WARNING" | "ERROR";
  code: string;
  message: string;
  subjectId: string;
  relatedIds: string[];
}

export interface SynthesisResult {
  resultId: string;
  serviceVersion: string;
  status: SynthesisStatus;
  modelId: string;
  modelVersion: string;
  revision: string;
  knowledgeRevision: number;
  knowledgeEtag: string;
  fingerprint: string;
  selections: SynthesisSelection[];
  diagnostics: SynthesisDiagnostic[];
  rationale: string[];
  generatedMnc: string;
}


export type ReconfigurationCause =
  | "RESOURCE_LOSS"
  | "RESOURCE_REPLACEMENT"
  | "CAPABILITY_CHANGE"
  | "REQUIREMENT_CHANGE"
  | "AVAILABILITY_CHANGE"
  | "MANUAL_REPLAN";

export type StateMigrationPolicy =
  | "PRESERVE"
  | "MIGRATE"
  | "RESET"
  | "INITIALIZE"
  | "SAFE_FALLBACK"
  | "RETIRE";

export interface StateMigrationInstruction {
  requirementId: string;
  policy: StateMigrationPolicy;
  fromResourceId: string;
  toResourceId: string;
  reason: string;
}

export interface ReconfigurationResult {
  resultId: string;
  serviceVersion: string;
  status: "UNCHANGED" | "RECONFIGURED" | "NO_SOLUTION";
  cause: ReconfigurationCause;
  modelId: string;
  modelVersion: string;
  revision: string;
  knowledgeRevision: number;
  knowledgeEtag: string;
  fingerprint: string;
  selections: SynthesisSelection[];
  migrations: StateMigrationInstruction[];
  diagnostics: SynthesisDiagnostic[];
}
