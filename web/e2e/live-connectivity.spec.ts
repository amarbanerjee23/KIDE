import { expect, test, type Page } from "@playwright/test";
import { readConnectivitySettings } from "../src/connectivityPolicy";

/**
 * PR101: unmocked, read-only readiness for the *deployed* staging service mesh.
 * Only Firebase sign-in, GETs and ephemeral protocol initialization.
 * No project/model creation, presence join, synthesis, generation, or writes.
 * Requires a dedicated approved staging ENGINEER account and known project.
 */
test("authenticated REST, collaboration, Xtext and GLSP are reachable together", async ({
  page, request
}) => {
  const cfg = readConnectivitySettings(process.env);

  const [healthResponse, versionResponse, webVersionResponse] = await Promise.all([
    request.get(cfg.api + "/api/v1/health"),
    request.get(cfg.api + "/api/v1/version"),
    request.get(cfg.web + "/kide-version.json")
  ]);
  expect(healthResponse.status(), "backend health").toBe(200);
  expect(versionResponse.status(), "backend version").toBe(200);
  expect(webVersionResponse.status(), "web version").toBe(200);
  const health = await healthResponse.json() as { status?: string };
  const backend = await versionResponse.json() as { buildId?: string; apiVersion?: string };
  const frontend = await webVersionResponse.json() as { component?: string; buildId?: string };
  expect(health.status).toBe("UP");
  expect(backend.apiVersion).toBe("v1");
  expect(backend.buildId).toBeTruthy();
  expect(frontend).toMatchObject({ component: "web", buildId: backend.buildId });

  const firebase = await request.post(
    "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" +
      encodeURIComponent(cfg.apiKey),
    {
      data: { email: cfg.email, password: cfg.password, returnSecureToken: true },
      timeout: 20_000
    }
  );
  // Fail with redacted diagnostics: no response body, email, password or JWT.
  expect(firebase.status(), "staging Firebase sign-in").toBe(200);
  const auth = await firebase.json() as { localId?: string; idToken?: string };
  if (!auth.localId || !auth.idToken) throw new Error("Firebase monitor identity is incomplete");
  const headers = { Authorization: "Bearer " + auth.idToken };

  // A healthy API can still return 403 for the monitor identity. Such a
  // response must fail the readiness gate, never be reported as Connected.
  const unauthenticated = await request.get(cfg.api + "/api/v1/projects");
  expect(unauthenticated.status(), "anonymous projects must be denied").toBe(401);
  const projectsResponse = await request.get(cfg.api + "/api/v1/projects", { headers });
  expect(projectsResponse.status(), "authenticated project listing").toBe(200);
  const listing = await projectsResponse.json() as {
    items?: Array<{ id: string; workspaceId?: string }>;
  };
  const selected = listing.items?.find(project => project.id === cfg.projectId);
  if (!selected?.workspaceId) {
    throw new Error("Monitor account cannot discover its approved engineering project");
  }
  if (!/^kide:workspace:[0-9a-f-]{36}$/.test(selected.workspaceId)) {
    throw new Error("Authorized project has no canonical workspace ID");
  }

  const scope = "/api/v1/projects/" + encodeURIComponent(cfg.projectId);
  const [projectResponse, modelsResponse, collaborationResponse] = await Promise.all([
    request.get(cfg.api + scope, { headers }),
    request.get(cfg.api + scope + "/models", { headers }),
    request.get(cfg.api + scope + "/collaboration/sessions", { headers })
  ]);
  expect(projectResponse.status(), "authorized project metadata").toBe(200);
  expect(modelsResponse.status(), "authorized model discovery").toBe(200);
  expect(collaborationResponse.status(), "authenticated collaboration listing").toBe(200);
  const project = await projectResponse.json() as { id?: string; workspaceId?: string };
  const models = await modelsResponse.json() as { items?: unknown[] };
  const presence = await collaborationResponse.json() as { items?: unknown[] };
  expect(project.id).toBe(cfg.projectId);
  expect(project.workspaceId).toBe(selected.workspaceId);
  expect(Array.isArray(models.items)).toBe(true);
  expect(Array.isArray(presence.items)).toBe(true);

  // Web origin is the browser's actual Origin header for the authenticated WS.
  const pageResponse = await page.goto(cfg.web + "/", { waitUntil: "domcontentloaded" });
  expect(pageResponse?.status(), "deployed browser frontend").toBe(200);
  await verifyGateway(page, cfg.api, auth.idToken, selected.workspaceId, "lsp");
  await verifyGateway(page, cfg.api, auth.idToken, selected.workspaceId, "glsp");
});

async function verifyGateway(
  page: Page, api: string, token: string, workspaceId: string,
  kind: "lsp" | "glsp"
): Promise<void> {
  await page.evaluate(async ({ api, token, workspaceId, kind }) => {
    const endpoint = new URL(api);
    endpoint.protocol = "wss:";
    endpoint.pathname = "/" + kind;
    endpoint.search = new URLSearchParams({ workspaceId }).toString();
    const bytes = new TextEncoder().encode(token);
    let raw = "";
    for (const byte of bytes) raw += String.fromCharCode(byte);
    const credential = btoa(raw).replaceAll("+", "-").replaceAll("/", "_").replace(/=+$/, "");
    const protocol = kind === "lsp" ? "kide.lsp.v1" : "kide.glsp.v1";
    const socket = new WebSocket(endpoint.toString(), [protocol, "kide.bearer." + credential]);
    // Each handshake has a finite independent timeout even when the backend
    // acknowledges TCP/WebSocket but never completes JSON-RPC initialization.
    const timeout = 12_000;
    const connected = new Promise<void>((resolve, reject) => {
      const timer = window.setTimeout(() => reject(new Error(kind + " socket timed out")), timeout);
      socket.addEventListener("open", () => { clearTimeout(timer); resolve(); }, { once: true });
      socket.addEventListener("error", () => {
        clearTimeout(timer);
        reject(new Error(kind + " socket unavailable"));
      }, { once: true });
      socket.addEventListener("close", () => {
        clearTimeout(timer);
        reject(new Error(kind + " socket closed before initialization"));
      }, { once: true });
    });
    const rpc = (id: number, method: string, params: unknown): Promise<unknown> =>
      new Promise((resolve, reject) => {
        const timer = window.setTimeout(() => {
          socket.removeEventListener("message", onMessage);
          reject(new Error(kind + " " + method + " timed out"));
        }, timeout);
        const onMessage = (event: MessageEvent) => {
          let result: { id?: number; result?: unknown; error?: unknown };
          try { result = JSON.parse(String(event.data)); } catch { return; }
          if (result.id !== id) return;
          clearTimeout(timer);
          socket.removeEventListener("message", onMessage);
          if (result.error) reject(new Error(kind + " " + method + " failed"));
          else resolve(result.result);
        };
        socket.addEventListener("message", onMessage);
        socket.send(JSON.stringify({ jsonrpc: "2.0", id, method, params }));
      });

    try {
      await connected;
      if (socket.protocol !== protocol) throw new Error(kind + " subprotocol mismatch");
      const init = await rpc(1, "initialize", kind === "lsp"
        ? {
            processId: null, rootUri: "kide-workspace:/",
            workspaceFolders: [{ uri: "kide-workspace:/", name: "Readiness" }],
            capabilities: {}
          }
        : { applicationId: "KIDE read-only service readiness", protocolVersion: "1.0.0" }
      ) as Record<string, unknown> | null;
      if (!init) throw new Error(kind + " initialize returned no result");
      if (kind === "lsp") {
        const caps = init.capabilities as Record<string, unknown> | undefined;
        if (!caps?.completionProvider || !caps.documentSymbolProvider) {
          throw new Error("Xtext capabilities missing");
        }
        socket.send(JSON.stringify({ jsonrpc: "2.0", method: "initialized", params: {} }));
      } else {
        if (init.protocolVersion !== "1.0.0") throw new Error("GLSP version mismatch");
        // Ephemeral client session only; no action modifying a model is sent.
        const ready = await rpc(2, "initializeClientSession", {
          clientSessionId: "kide-readonly-monitor",
          diagramType: "kide-mnc-diagram",
          clientActionKinds: ["setModel", "updateModel", "setDirtyState", "setMarkers", "setTypeHints"]
        }) as Record<string, unknown> | null;
        if (ready && ready.error) throw new Error("GLSP client session rejected");
      }
    } finally {
      if (socket.readyState === WebSocket.OPEN ||
          socket.readyState === WebSocket.CONNECTING) socket.close();
    }
  }, { api, token, workspaceId, kind });
}
