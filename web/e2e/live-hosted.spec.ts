import { createHash } from "node:crypto";
import { expect, test, type APIRequestContext, type Page } from "@playwright/test";

/**
 * PR89: strictly real, staging-only engineering acceptance.
 * No page.route(), routeWebSocket(), static fixtures substituted for API data,
 * token stubs, or test.skip() are permitted.
 *
 * Every request targets the provided deployed Cloud Run/Firebase services.
 * The test creates a uniquely named, disposable staging tenant and does NOT
 * delete it: there is intentionally no unsafe tenant deletion API.
 */

interface AcceptanceConfig {
  api: string;
  web: string;
  apiKey: string;
  email: string;
  password: string;
  otherEmail: string;
  otherPassword: string;
}

interface FirebaseIdentity {
  uid: string;
  token: string;
}

interface Project {
  id: string;
  workspaceId: string;
  displayName: string;
}

interface Model {
  id: string;
  etag: string;
  content: string;
}

const SHA256 = /^[0-9a-f]{64}$/;
const MISSING_ETAG = "0".repeat(64);
const fixture = {
  "device.mncspec": [
    "Model Golden",
    "InterfaceDescription Device {",
    "  commands { Start[] }",
    "  events { Publish Ready[] }",
    "}",
    ""
  ].join("\n"),
  "observe.cap": [
    "Capability Observe compatible component interface Device {",
    "  providesControlCapabilities {",
    "    fireable commands : Start",
    "    receivable events : Ready",
    "  }",
    "}",
    ""
  ].join("\n"),
  "workflow.activity": [
    "ActivityDiagram GoldenWorkflow",
    "has activities {",
    "  Activity ObserveStep {",
    "    requireCapability : Observe { Start, Ready }",
    "    nextActivity : ObserveStep",
    "  }",
    "}",
    ""
  ].join("\n"),
  "bindings.krl": [
    "knowledge ApiBindings {",
    '  namespace kide = "https://kide.dev/ontology/v1#";',
    "  query FindObserve(capability: iri) {",
    "    match ?device kide:providesCapability ?capability;",
    "    select ?device;",
    "  }",
    "  template Binding(name: string, resource: iri) for java",
    '    body "public final class __DOLLAR__{name} { public static final String RESOURCE = \\"__DOLLAR__{resource}\\"; private __DOLLAR__{name}() {} }";',
    "  target Observe type java {",
    "    template Binding;",
    '    output "generated/ApiObserveBinding.java";',
    '    bind name: string = string "ApiObserveBinding";',
    '    bind resource: iri = query FindObserve(iri "urn:kide:capability:Observe").device;',
    "  }",
    "}",
    ""
  ].join("\n").replaceAll("__DOLLAR__", "$")
};

function required(name: string): string {
  const value = process.env[name]?.trim();
  if (!value) throw new Error(name + " is required for live staging acceptance");
  return value;
}

function configuration(): AcceptanceConfig {
  if (required("KIDE_LIVE_ALLOW_WRITES") !== "staging-only") {
    throw new Error("Live acceptance requires explicit staging-only write approval");
  }
  const api = new URL(required("KIDE_LIVE_API_ORIGIN"));
  const web = new URL(required("KIDE_LIVE_WEB_URL"));
  if (api.protocol !== "https:" || web.protocol !== "https:"
      || api.username || api.password || web.username || web.password
      || api.pathname !== "/" || web.pathname !== "/"
      || api.search || web.search || api.hash || web.hash) {
    throw new Error("Live URLs must be credential-free HTTPS service origins");
  }
  if (api.hostname !== required("KIDE_LIVE_APPROVED_API_HOST")
      || web.hostname !== required("KIDE_LIVE_APPROVED_WEB_HOST")) {
    throw new Error("Live service host differs from approved staging host");
  }
  if (api.origin === web.origin) {
    throw new Error("Live acceptance requires independent Web and API origins");
  }
  return {
    api: api.origin,
    web: web.origin,
    apiKey: required("KIDE_LIVE_FIREBASE_API_KEY"),
    email: required("KIDE_LIVE_ENGINEER_EMAIL"),
    password: required("KIDE_LIVE_ENGINEER_PASSWORD"),
    otherEmail: required("KIDE_LIVE_OTHER_ENGINEER_EMAIL"),
    otherPassword: required("KIDE_LIVE_OTHER_ENGINEER_PASSWORD")
  };
}

async function firebaseSignIn(
  request: APIRequestContext, apiKey: string, email: string, password: string
): Promise<FirebaseIdentity> {
  const response = await request.post(
    "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key="
      + encodeURIComponent(apiKey),
    { data: { email, password, returnSecureToken: true }, timeout: 30_000 }
  );
  if (!response.ok()) throw new Error("Staging Firebase sign-in failed for an acceptance principal");
  const payload = await response.json() as { localId?: string; idToken?: string };
  if (!payload.localId || !payload.idToken) {
    throw new Error("Staging Firebase response omitted authenticated identity");
  }
  return { uid: payload.localId, token: payload.idToken };
}

async function apiCall<T>(
  request: APIRequestContext,
  apiOrigin: string, token: string, method: "GET" | "POST" | "PUT",
  route: string, expected: number, body?: unknown
): Promise<T> {
  const response = await request.fetch(apiOrigin + "/api/v1" + route, {
    method,
    headers: {
      Authorization: "Bearer " + token,
      ...(body === undefined ? {} : { "Content-Type": "application/json" })
    },
    ...(body === undefined ? {} : { data: body }),
    timeout: 30_000
  });
  if (response.status() !== expected) {
    throw new Error("Live " + method + " " + route + " returned HTTP "
      + response.status() + "; expected " + expected);
  }
  return response.json() as Promise<T>;
}

function validSha(value: unknown): value is string {
  return typeof value === "string" && SHA256.test(value);
}

function assertGeneratedArtifacts(result: Record<string, unknown>, source: Model, krl: Model,
  synthesisFingerprint: string): void {
  expect(result.sourceRevision).toBe(source.etag);
  expect(result.krlRevision).toBe(krl.etag);
  expect(result.synthesisFingerprint).toBe(synthesisFingerprint);
  expect(validSha(result.fingerprint)).toBeTruthy();
  expect(result.toolchainVersion).toBe("1");
  const artifacts = result.artifacts as Array<{
    path: string; sha256: string; contentBase64: string;
    mediaType: string; targetId: string; targetVersion: string;
  }>;
  expect(artifacts.length).toBeGreaterThan(0);
  const manifest = JSON.parse(result.manifestJson as string) as {
    schemaVersion: string; fingerprint: string;
    source: { modelId: string; revision: string };
    krl: { modelId: string; revision: string };
    synthesis: { fingerprint: string };
    artifacts: Array<{
      path: string; bytes: number; sha256: string;
      mediaType: string; targetId: string; targetVersion: string;
    }>;
    targetVersions: Record<string, string>;
  };
  expect(manifest.schemaVersion).toBe("1");
  expect(manifest.fingerprint).toBe(result.fingerprint);
  expect(manifest.source).toMatchObject({ modelId: source.id, revision: source.etag });
  expect(manifest.krl).toMatchObject({ modelId: krl.id, revision: krl.etag });
  expect(manifest.synthesis.fingerprint).toBe(synthesisFingerprint);
  const seen = new Set<string>();
  for (const artifact of artifacts) {
    expect(artifact.path).toMatch(/^[A-Za-z0-9_.-]+(?:\/[A-Za-z0-9_.-]+)*$/);
    expect(artifact.path.split("/")).not.toContain("..");
    expect(seen.has(artifact.path)).toBeFalsy();
    seen.add(artifact.path);
    expect(validSha(artifact.sha256)).toBeTruthy();
    const bytes = Buffer.from(artifact.contentBase64, "base64");
    expect(bytes.toString("base64")).toBe(artifact.contentBase64);
    expect(createHash("sha256").update(bytes).digest("hex")).toBe(artifact.sha256);
    const evidence = manifest.artifacts.find((item) => item.path === artifact.path);
    expect(evidence).toBeDefined();
    expect(evidence).toMatchObject({
      path: artifact.path, sha256: artifact.sha256, bytes: bytes.length,
      mediaType: artifact.mediaType, targetId: artifact.targetId,
      targetVersion: artifact.targetVersion
    });
    expect(manifest.targetVersions[artifact.targetId]).toBe(artifact.targetVersion);
  }
}

// A real remote WebSocket, authenticated with the same browser credential
// subprotocol used by KIDE's Monaco and GLSP clients. Never mock the transport.
async function assertGateway(
  page: Page, api: string, token: string, workspaceId: string,
  kind: "lsp" | "glsp"
): Promise<void> {
  await page.evaluate(async ({ api, token, workspaceId, kind }) => {
    const url = new URL(api);
    url.protocol = "wss:";
    url.pathname = "/" + kind;
    url.search = new URLSearchParams({ workspaceId }).toString();
    const bytes = new TextEncoder().encode(token);
    let binary = "";
    for (const value of bytes) binary += String.fromCharCode(value);
    const encoded = btoa(binary).replaceAll("+", "-").replaceAll("/", "_").replace(/=+$/, "");
    const protocol = kind === "lsp" ? "kide.lsp.v1" : "kide.glsp.v1";
    const ws = new WebSocket(url.toString(), [protocol, "kide.bearer." + encoded]);
    const queue = new Map<number, (value: unknown) => void>();
    const wait = (id: number) => new Promise<unknown>((resolve, reject) => {
      const timer = window.setTimeout(() => reject(new Error("Live " + kind + " request timed out")), 15_000);
      queue.set(id, (value) => { clearTimeout(timer); resolve(value); });
    });
    ws.addEventListener("message", (event) => {
      try {
        const response = JSON.parse(String(event.data)) as {
          id?: number; result?: unknown; error?: { message?: string };
        };
        if (typeof response.id !== "number") return;
        const complete = queue.get(response.id);
        if (!complete) return;
        queue.delete(response.id);
        complete(response.error
          ? { protocolError: response.error.message ?? "unknown JSON-RPC error" }
          : response.result);
      } catch { /* unrelated service notifications do not satisfy a request */ }
    });
    try {
      await new Promise<void>((resolve, reject) => {
        const timer = window.setTimeout(
          () => reject(new Error("Live " + kind + " handshake timed out")), 15_000
        );
        ws.addEventListener("open", () => { clearTimeout(timer); resolve(); }, { once: true });
        ws.addEventListener("error", () => {
          clearTimeout(timer); reject(new Error("Live " + kind + " handshake failed"));
        }, { once: true });
      });
      if (ws.protocol !== protocol) throw new Error("Unexpected " + kind + " WebSocket protocol");
      const response = wait(1);
      ws.send(JSON.stringify({
        jsonrpc: "2.0", id: 1, method: "initialize",
        params: kind === "lsp"
          ? {
              processId: null,
              rootUri: "kide-workspace:/",
              workspaceFolders: [{ uri: "kide-workspace:/", name: "KIDE Live Acceptance" }],
              capabilities: {}
            }
          : { applicationId: "KIDE PR89 staging", protocolVersion: "1.0.0" }
      }));
      const initialized = await response as Record<string, unknown>;
      if (!initialized || initialized.protocolError) throw new Error("Live " + kind + " initialize failed");
      if (kind === "lsp") {
        if (!initialized.capabilities || typeof initialized.capabilities !== "object") {
          throw new Error("Live LSP did not return server capabilities");
        }
        const capabilities = initialized.capabilities as Record<string, unknown>;
        if (!capabilities.completionProvider || !capabilities.documentSymbolProvider) {
          throw new Error("Live LSP is missing completion or document symbols");
        }
        ws.send(JSON.stringify({ jsonrpc: "2.0", method: "initialized", params: {} }));
      } else {
        if (initialized.protocolVersion !== "1.0.0") {
          throw new Error("Live GLSP protocol version is incompatible");
        }
        const session = wait(2);
        ws.send(JSON.stringify({
          jsonrpc: "2.0", id: 2, method: "initializeClientSession",
          params: {
            clientSessionId: "pr89-live-acceptance",
            diagramType: "kide-mnc-diagram",
            clientActionKinds: ["setModel", "updateModel", "setDirtyState", "setMarkers", "setTypeHints"]
          }
        }));
        const sessionResult = await session as Record<string, unknown> | null;
        if (sessionResult && sessionResult.protocolError) {
          throw new Error("Live GLSP client session initialization failed");
        }
      }
    } finally {
      ws.close();
    }
  }, { api, token, workspaceId, kind });
}

test("PR89 live staging engineering: Firebase, tenant isolation, REST, LSP, GLSP, synthesis, generation and Web", async ({
  page, request
}) => {
  const cfg = configuration();
  // Fail before writing to a remote environment if build IDs or capabilities
  // indicate an incomplete or mixed-version deployment.
  const healthResponse = await request.get(cfg.api + "/api/v1/health");
  const versionResponse = await request.get(cfg.api + "/api/v1/version");
  const webVersionResponse = await request.get(cfg.web + "/kide-version.json");
  expect(healthResponse.status()).toBe(200);
  expect(versionResponse.status()).toBe(200);
  expect(webVersionResponse.status()).toBe(200);
  const health = await healthResponse.json() as {
    status: string; projectCreationEnabled?: boolean;
  };
  const backend = await versionResponse.json() as {
    buildId: string; apiVersion: string;
  };
  const frontend = await webVersionResponse.json() as {
    buildId: string; component: string;
  };
  expect(health.status).toBe("UP");
  expect(health.projectCreationEnabled).toBe(true);
  expect(backend.apiVersion).toBe("v1");
  expect(backend.buildId).toBeTruthy();
  expect(frontend.component).toBe("web");
  expect(frontend.buildId).toBe(backend.buildId);

  const engineer = await firebaseSignIn(request, cfg.apiKey, cfg.email, cfg.password);
  const other = await firebaseSignIn(request, cfg.apiKey, cfg.otherEmail, cfg.otherPassword);
  expect(engineer.uid).not.toBe(other.uid);

  const unauthenticated = await request.get(cfg.api + "/api/v1/projects");
  expect(unauthenticated.status()).toBe(401);
  const uniqueName = "PR89 Acceptance " + Date.now().toString(36);
  const created = await apiCall<Project>(request, cfg.api, engineer.token, "POST", "/projects", 201,
    { displayName: uniqueName });
  expect(created.displayName).toBe(uniqueName);
  expect(created.id).toMatch(/^kide:project:/);
  expect(created.workspaceId).toMatch(/^kide:workspace:/);
  const projectPath = "/projects/" + encodeURIComponent(created.id);
  const isolatedList = await apiCall<{ items: Project[] }>(
    request, cfg.api, other.token, "GET", "/projects", 200
  );
  expect(isolatedList.items.some((item) => item.id === created.id)).toBe(false);
  await apiCall(request, cfg.api, other.token, "GET", projectPath, 403);
  await apiCall(request, cfg.api, other.token, "GET", projectPath + "/models", 403);

  // Use the exact engineering corpus qualified by the packaged Java API
  // self-check; unlike the mocked golden journey these are real source bytes.
  const imported = await apiCall<{ importedCount: number; workspaceId: string }>(
    request, cfg.api, engineer.token, "POST", projectPath + "/imports/archive", 201,
    {
      archiveName: "pr89-staging-corpus.zip",
      items: Object.entries(fixture).map(([path, content]) => ({
        path, contentBase64: Buffer.from(content, "utf8").toString("base64"),
        mediaType: path.endsWith(".activity") ? "text/x-kide-activity"
          : path.endsWith(".krl") ? "text/x-kide-krl"
          : path.endsWith(".cap") ? "text/x-kide-capability"
          : "text/x-kide-mnc"
      }))
    }
  );
  expect(imported.importedCount).toBe(4);
  expect(imported.workspaceId).toBe(created.workspaceId);

  const source = await apiCall<Model>(request, cfg.api, engineer.token,
    "GET", projectPath + "/models/workflow.activity", 200);
  const krl = await apiCall<Model>(request, cfg.api, engineer.token,
    "GET", projectPath + "/models/bindings.krl", 200);
  expect(source.content).toBe(fixture["workflow.activity"]);
  expect(krl.content).toBe(fixture["bindings.krl"]);
  expect(validSha(source.etag)).toBe(true);
  expect(validSha(krl.etag)).toBe(true);

  // Real gateway handshakes use the same authenticated Firebase JWT.
  await page.goto(cfg.web + "/");
  await assertGateway(page, cfg.api, engineer.token, created.workspaceId, "lsp");
  await assertGateway(page, cfg.api, engineer.token, created.workspaceId, "glsp");

  const synthesisPayload = { modelId: source.id, modelRevision: source.etag };
  const synthesis = await apiCall<{
    status: string; fingerprint: string; selections: unknown[];
  }>(request, cfg.api, engineer.token, "POST", projectPath + "/synthesis", 200, synthesisPayload);
  expect(synthesis.status).toBe("SUCCESS");
  expect(validSha(synthesis.fingerprint)).toBe(true);
  expect(synthesis.selections.length).toBeGreaterThan(0);
  const repeated = await apiCall<{ fingerprint: string }>(
    request, cfg.api, engineer.token, "POST", projectPath + "/synthesis", 200, synthesisPayload
  );
  expect(repeated.fingerprint).toBe(synthesis.fingerprint);
  const generatePayload = {
    sourceModelId: source.id, sourceRevision: source.etag,
    krlModelId: krl.id, krlRevision: krl.etag,
    synthesisFingerprint: synthesis.fingerprint
  };
  const generated = await apiCall<Record<string, unknown>>(
    request, cfg.api, engineer.token, "POST", projectPath + "/generation", 200, generatePayload
  );
  assertGeneratedArtifacts(generated, source, krl, synthesis.fingerprint);
  const again = await apiCall<Record<string, unknown>>(
    request, cfg.api, engineer.token, "POST", projectPath + "/generation", 200, generatePayload
  );
  expect(again.fingerprint).toBe(generated.fingerprint);
  await apiCall(request, cfg.api, engineer.token, "POST", projectPath + "/generation",
    409, { ...generatePayload, krlRevision: MISSING_ETAG });
  const unchanged = await apiCall<Model>(
    request, cfg.api, engineer.token, "GET", projectPath + "/models/workflow.activity", 200
  );
  expect(unchanged.etag).toBe(source.etag);
  expect(unchanged.content).toBe(source.content);

  // Real browser Firebase sign-in. Never substitute Firebase/REST/WS responses.
  await page.getByLabel("Firebase email").fill(cfg.email);
  await page.getByLabel("Firebase password").fill(cfg.password);
  await page.getByRole("button", { name: "Sign in with Firebase" }).click();
  await expect(page.getByText(cfg.email, { exact: true })).toBeVisible();
  await page.getByRole("button", { name: "Open Engineering Workspace" }).first().click();
  await expect(page).toHaveURL(/\/workspace$/);
  const projectRow = page.locator(".project-list li")
    .filter({ hasText: uniqueName });
  await expect(projectRow).toBeVisible();
  await projectRow.getByRole("button", { name: "Open" }).click();
  await expect(page.locator(".editor-tabs")).toContainText("workflow.activity");
  await expect(page.getByLabel("Workbench status")).toContainText("API Online");
  await expect(page.getByLabel("Workbench status")).toContainText("Xtext Online");

  const workflow = page.getByLabel("Engineering workflow");
  await expect(workflow.getByRole("button", { name: "Synthesize", exact: true })).toBeEnabled();
  await workflow.getByRole("button", { name: "Synthesize", exact: true }).click();
  await expect(page.getByLabel("Synthesis result")).toContainText("SUCCESS");
  await expect(workflow.getByRole("button", { name: "Generate code", exact: true })).toBeEnabled();
  await workflow.getByRole("button", { name: "Generate code", exact: true }).click();
  await expect(page.getByLabel("Generation result")).toContainText("generated/ApiObserveBinding.java");

  await workflow.getByRole("button", { name: "Visualize flow", exact: true }).click();
  await expect(page.getByTestId("glsp-editor")).toBeVisible();
  await expect(page.getByLabel("KIDE graphical model")).toBeVisible();
  await workflow.getByRole("button", { name: "State machine", exact: true }).click();
  await expect(page.getByTestId("glsp-editor")).toBeVisible();
});
