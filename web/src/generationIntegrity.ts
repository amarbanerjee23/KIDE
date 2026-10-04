import type { GenerationArtifact, GenerationResult } from "./types";

export interface GenerationExpectation {
  sourceModelId: string;
  sourceRevision: string;
  krlModelId: string;
  krlRevision: string;
  synthesisFingerprint: string;
}

export class GenerationIntegrityError extends Error {
  constructor(message: string) {
    super(message);
    this.name = "GenerationIntegrityError";
  }
}

interface ManifestArtifact {
  path?: unknown;
  mediaType?: unknown;
  bytes?: unknown;
  sha256?: unknown;
  targetId?: unknown;
  targetVersion?: unknown;
}

interface GenerationManifest {
  schemaVersion?: unknown;
  toolchainVersion?: unknown;
  fingerprint?: unknown;
  source?: { modelId?: unknown; revision?: unknown };
  synthesis?: { fingerprint?: unknown };
  krl?: { modelId?: unknown; revision?: unknown };
  targetVersions?: Record<string, unknown>;
  artifacts?: ManifestArtifact[];
}

const SHA256 = /^[0-9a-f]{64}$/;
const BASE64 = /^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$/;

export async function validateGenerationResult(
  result: GenerationResult,
  expected: GenerationExpectation
): Promise<GenerationResult> {
  assertEqual("source model", result.sourceModelId, expected.sourceModelId);
  assertEqual("source revision", result.sourceRevision, expected.sourceRevision);
  assertEqual("KRL model", result.krlModelId, expected.krlModelId);
  assertEqual("KRL revision", result.krlRevision, expected.krlRevision);
  assertEqual(
    "synthesis fingerprint",
    result.synthesisFingerprint,
    expected.synthesisFingerprint
  );

  requireSha("generation fingerprint", result.fingerprint);
  if (result.toolchainVersion !== "1") {
    fail(`unsupported generation toolchain version: ${result.toolchainVersion}`);
  }
  if (!Array.isArray(result.artifacts) || result.artifacts.length === 0) {
    fail("generation returned no artifacts");
  }

  const manifest = parseManifest(result.manifestJson);
  assertEqual("manifest schema", manifest.schemaVersion, "1");
  assertEqual("manifest toolchain", manifest.toolchainVersion, result.toolchainVersion);
  assertEqual("manifest fingerprint", manifest.fingerprint, result.fingerprint);
  assertEqual("manifest source model", manifest.source?.modelId, expected.sourceModelId);
  assertEqual("manifest source revision", manifest.source?.revision, expected.sourceRevision);
  assertEqual("manifest KRL model", manifest.krl?.modelId, expected.krlModelId);
  assertEqual("manifest KRL revision", manifest.krl?.revision, expected.krlRevision);
  assertEqual(
    "manifest synthesis fingerprint",
    manifest.synthesis?.fingerprint,
    expected.synthesisFingerprint
  );

  if (!Array.isArray(manifest.artifacts)) {
    fail("generation manifest artifacts are missing");
  }
  if (manifest.artifacts.length !== result.artifacts.length) {
    fail("generation manifest artifact count does not match response");
  }

  const seen = new Set<string>();
  for (const artifact of result.artifacts) {
    validateArtifactPath(artifact.path);
    if (seen.has(artifact.path)) fail(`duplicate generated artifact path: ${artifact.path}`);
    seen.add(artifact.path);

    requireSha(`artifact hash for ${artifact.path}`, artifact.sha256);
    const bytes = decodeBase64(artifact);
    const actualHash = await sha256(bytes);
    if (actualHash !== artifact.sha256) {
      fail(`generated artifact hash mismatch: ${artifact.path}`);
    }

    const evidence = manifest.artifacts.find((candidate) => candidate.path === artifact.path);
    if (!evidence) fail(`manifest evidence missing for artifact: ${artifact.path}`);
    assertEqual(`manifest media type for ${artifact.path}`, evidence.mediaType, artifact.mediaType);
    assertEqual(`manifest byte count for ${artifact.path}`, evidence.bytes, bytes.byteLength);
    assertEqual(`manifest hash for ${artifact.path}`, evidence.sha256, artifact.sha256);
    assertEqual(`manifest target for ${artifact.path}`, evidence.targetId, artifact.targetId);
    assertEqual(
      `manifest target version for ${artifact.path}`,
      evidence.targetVersion,
      artifact.targetVersion
    );

    const targetVersion = manifest.targetVersions?.[artifact.targetId];
    assertEqual(
      `manifest declared target version for ${artifact.targetId}`,
      targetVersion,
      artifact.targetVersion
    );
  }

  return result;
}

function parseManifest(value: string): GenerationManifest {
  try {
    const parsed = JSON.parse(value) as GenerationManifest;
    if (!parsed || typeof parsed !== "object" || Array.isArray(parsed)) {
      fail("generation manifest must be a JSON object");
    }
    return parsed;
  } catch (error) {
    if (error instanceof GenerationIntegrityError) throw error;
    fail("generation manifest is not valid JSON");
  }
}

function validateArtifactPath(path: string): void {
  if (!path || path.startsWith("/") || path.includes("\\") || /^[A-Za-z]:/.test(path)) {
    fail(`unsafe generated artifact path: ${path || "<empty>"}`);
  }
  const parts = path.split("/");
  if (parts.some((part) => !part || part === "." || part === "..")) {
    fail(`unsafe generated artifact path: ${path}`);
  }
}

function decodeBase64(artifact: GenerationArtifact): Uint8Array {
  const value = artifact.contentBase64;
  if (!value || value.length % 4 !== 0 || !BASE64.test(value)) {
    fail(`invalid base64 generated artifact: ${artifact.path}`);
  }
  try {
    const binary = atob(value);
    return Uint8Array.from(binary, (character) => character.charCodeAt(0));
  } catch {
    fail(`invalid base64 generated artifact: ${artifact.path}`);
  }
}

async function sha256(bytes: Uint8Array): Promise<string> {
  const input = new ArrayBuffer(bytes.byteLength);
  new Uint8Array(input).set(bytes);
  const digest = await crypto.subtle.digest("SHA-256", input);
  return Array.from(new Uint8Array(digest))
    .map((value) => value.toString(16).padStart(2, "0"))
    .join("");
}

function requireSha(label: string, value: unknown): asserts value is string {
  if (typeof value !== "string" || !SHA256.test(value)) {
    fail(`${label} is not a lowercase SHA-256 value`);
  }
}

function assertEqual(label: string, actual: unknown, expected: unknown): void {
  if (actual !== expected) {
    fail(`${label} mismatch: expected ${String(expected)}, got ${String(actual)}`);
  }
}

function fail(message: string): never {
  throw new GenerationIntegrityError(message);
}
