import {
  ApplyLabelEditOperation,
  ChangeBoundsOperation,
  CreateEdgeOperation,
  CreateNodeOperation,
  DeleteElementOperation,
  GLSPClient,
  RedoAction,
  RequestMarkersAction,
  RequestModelAction,
  RequestTypeHintsAction,
  SaveModelAction,
  SetDirtyStateAction,
  SetMarkersAction,
  SetModelAction,
  SetTypeHintsAction,
  UndoAction,
  UpdateModelAction,
  type Action,
  type ActionMessage,
  type EdgeTypeHint,
  type GModelRootSchema,
  type Marker,
  type Point,
  type ShapeTypeHint
} from "@eclipse-glsp/protocol";

export interface GlspState {
  model?: GModelRootSchema;
  shapeHints: ShapeTypeHint[];
  edgeHints: EdgeTypeHint[];
  markers: Marker[];
  dirty: boolean;
}

interface PendingRequest {
  resolve(value: unknown): void;
  reject(error: Error): void;
  timer: ReturnType<typeof setTimeout>;
}

interface PendingAction {
  resolve(action: Action): void;
  reject(error: Error): void;
  timer: ReturnType<typeof setTimeout>;
}

type StateListener = (state: GlspState) => void;

export class KideGlspClient {
  private socket: WebSocket | undefined;
  private readonly sessionId = `kide-web-${crypto.randomUUID()}`;
  private nextRpcId = 1;
  private nextActionId = 1;
  private readonly pendingRequests = new Map<number, PendingRequest>();
  private readonly pendingActions = new Map<string, PendingAction>();
  private readonly listeners = new Set<StateListener>();
  private state: GlspState = {
    shapeHints: [],
    edgeHints: [],
    markers: [],
    dirty: false
  };
  private connected = false;

  constructor(
    private readonly gatewayOrigin: string,
    private readonly accessToken: string,
    private readonly workspaceId: string,
    readonly diagramType: string,
    readonly sourcePath: string
  ) {}

  subscribe(listener: StateListener): () => void {
    this.listeners.add(listener);
    listener(this.snapshot());
    return () => this.listeners.delete(listener);
  }

  snapshot(): GlspState {
    return {
      ...this.state,
      shapeHints: [...this.state.shapeHints],
      edgeHints: [...this.state.edgeHints],
      markers: [...this.state.markers]
    };
  }

  async connect(): Promise<void> {
    if (this.connected) return;
    const socket = new WebSocket(
      glspGatewayUrl(this.gatewayOrigin, this.workspaceId),
      ["kide.glsp.v1", "kide.bearer." + base64Url(this.accessToken)]
    );
    this.socket = socket;

    await new Promise<void>((resolve, reject) => {
      const timer = globalThis.setTimeout(
        () => reject(new Error("Graphical service connection timed out.")),
        10_000
      );
      socket.addEventListener("open", () => {
        globalThis.clearTimeout(timer);
        if (socket.protocol && socket.protocol !== "kide.glsp.v1") {
          reject(new Error("Graphical service negotiated an unexpected WebSocket protocol."));
          return;
        }
        resolve();
      }, { once: true });
      socket.addEventListener("error", () => {
        globalThis.clearTimeout(timer);
        reject(new Error("Graphical service WebSocket could not be opened."));
      }, { once: true });
    });

    socket.addEventListener("message", (event) => this.onMessage(event.data));
    socket.addEventListener("close", () => {
      this.connected = false;
      this.rejectAll(new Error("Graphical service connection closed."));
    });
    socket.addEventListener("error", () => {
      this.rejectAll(new Error("Graphical service connection failed."));
    });

    const initialized = await this.request<{
      protocolVersion: string;
      serverActions?: Record<string, string[]>;
    }>("initialize", {
      applicationId: "KIDE Web",
      protocolVersion: GLSPClient.protocolVersion
    });
    if (initialized.protocolVersion !== GLSPClient.protocolVersion) {
      throw new Error(
        `Unsupported GLSP protocol ${initialized.protocolVersion}; expected ${GLSPClient.protocolVersion}.`
      );
    }

    await this.request<void>("initializeClientSession", {
      clientSessionId: this.sessionId,
      diagramType: this.diagramType,
      clientActionKinds: [
        SetModelAction.KIND,
        UpdateModelAction.KIND,
        SetDirtyStateAction.KIND,
        SetMarkersAction.KIND,
        SetTypeHintsAction.KIND,
        "status"
      ]
    });
    this.connected = true;

    const model = await this.requestAction(
      RequestModelAction.create({
        options: {
          sourceUri: workspaceUri(this.sourcePath),
          diagramType: this.diagramType
        }
      })
    );
    if (!SetModelAction.is(model)) {
      throw new Error("Graphical server did not return a model.");
    }

    const hints = await this.requestAction(RequestTypeHintsAction.create());
    if (!SetTypeHintsAction.is(hints)) {
      throw new Error("Graphical server did not return type hints.");
    }

    if (this.state.model?.id) {
      void this.requestMarkers([this.state.model.id]).catch(() => {
        // Live validation remains available even if the initial batch marker request fails.
      });
    }
  }

  createNode(elementTypeId: string, location: Point): void {
    this.sendAction(CreateNodeOperation.create(elementTypeId, { location }));
  }

  createEdge(
    elementTypeId: string,
    sourceElementId: string,
    targetElementId: string
  ): void {
    this.sendAction(CreateEdgeOperation.create({
      elementTypeId,
      sourceElementId,
      targetElementId
    }));
  }

  rename(labelId: string, text: string): void {
    this.sendAction(ApplyLabelEditOperation.create({ labelId, text }));
  }

  deleteElements(elementIds: string[]): void {
    this.sendAction(DeleteElementOperation.create(elementIds));
  }

  changeBounds(
    elementId: string,
    newPosition: Point,
    newSize: { width: number; height: number }
  ): void {
    this.sendAction(ChangeBoundsOperation.create([{
      elementId,
      newPosition,
      newSize
    }]));
  }

  undo(): void {
    this.sendAction(UndoAction.create());
  }

  redo(): void {
    this.sendAction(RedoAction.create());
  }

  save(): void {
    this.sendAction(SaveModelAction.create());
  }

  async requestMarkers(elementIds: string[]): Promise<Marker[]> {
    const result = await this.requestAction(
      RequestMarkersAction.create(elementIds, { reason: "batch" })
    );
    if (!SetMarkersAction.is(result)) return [];
    return result.markers;
  }

  async dispose(): Promise<void> {
    const socket = this.socket;
    if (!socket) return;
    if (socket.readyState === WebSocket.OPEN) {
      try {
        await this.request<void>("disposeClientSession", {
          clientSessionId: this.sessionId
        });
        this.notify("shutdown");
      } catch {
        // Session shutdown remains best-effort after transport/server failure.
      }
      socket.close(1000, "KIDE graphical client closed");
    }
    this.socket = undefined;
    this.connected = false;
    this.rejectAll(new Error("Graphical service client disposed."));
  }

  private sendAction(action: Action): void {
    this.notify("process", {
      clientId: this.sessionId,
      action
    } satisfies ActionMessage);
  }

  private requestAction(action: Action & { requestId: string }): Promise<Action> {
    const requestId = action.requestId || `kide-action-${this.nextActionId++}`;
    const outbound = { ...action, requestId };
    return new Promise<Action>((resolve, reject) => {
      const timer = globalThis.setTimeout(() => {
        this.pendingActions.delete(requestId);
        reject(new Error(`Graphical action timed out: ${action.kind}`));
      }, 15_000);
      this.pendingActions.set(requestId, { resolve, reject, timer });
      this.sendAction(outbound);
    });
  }

  private request<T>(method: string, params: unknown): Promise<T> {
    const socket = this.requireSocket();
    const id = this.nextRpcId++;
    return new Promise<T>((resolve, reject) => {
      const timer = globalThis.setTimeout(() => {
        this.pendingRequests.delete(id);
        reject(new Error(`Graphical service request timed out: ${method}`));
      }, 15_000);
      this.pendingRequests.set(id, {
        resolve: (value) => resolve(value as T),
        reject,
        timer
      });
      socket.send(JSON.stringify({ jsonrpc: "2.0", id, method, params }));
    });
  }

  private notify(method: string, params?: unknown): void {
    const socket = this.requireSocket();
    socket.send(JSON.stringify({
      jsonrpc: "2.0",
      method,
      ...(params === undefined ? {} : { params })
    }));
  }

  private requireSocket(): WebSocket {
    if (!this.socket || this.socket.readyState !== WebSocket.OPEN) {
      throw new Error("Graphical service is not connected.");
    }
    return this.socket;
  }

  private onMessage(raw: unknown): void {
    if (typeof raw !== "string") return;
    let message: Record<string, unknown>;
    try {
      message = JSON.parse(raw) as Record<string, unknown>;
    } catch {
      return;
    }

    if (typeof message.id === "number" && !message.method) {
      const pending = this.pendingRequests.get(message.id);
      if (!pending) return;
      this.pendingRequests.delete(message.id);
      globalThis.clearTimeout(pending.timer);
      if (message.error && typeof message.error === "object") {
        const error = message.error as { code?: number; message?: string };
        pending.reject(new Error(
          `Graphical service error ${error.code ?? "unknown"}: ${error.message ?? "request failed"}`
        ));
      } else {
        pending.resolve(message.result);
      }
      return;
    }

    if (message.method !== "process") return;
    const envelope = message.params as ActionMessage | undefined;
    if (!envelope || envelope.clientId !== this.sessionId || !envelope.action) return;
    this.acceptAction(envelope.action);
  }

  private acceptAction(action: Action): void {
    const responseId = (
      action as Action & { responseId?: string }
    ).responseId;
    if (responseId) {
      const pending = this.pendingActions.get(responseId);
      if (pending) {
        this.pendingActions.delete(responseId);
        globalThis.clearTimeout(pending.timer);
        pending.resolve(action);
      }
    }

    if (SetModelAction.is(action) || UpdateModelAction.is(action)) {
      this.state = { ...this.state, model: action.newRoot };
    } else if (SetTypeHintsAction.is(action)) {
      this.state = {
        ...this.state,
        shapeHints: [...action.shapeHints],
        edgeHints: [...action.edgeHints]
      };
    } else if (SetMarkersAction.is(action)) {
      this.state = { ...this.state, markers: [...action.markers] };
    } else if (SetDirtyStateAction.is(action)) {
      this.state = { ...this.state, dirty: action.isDirty };
    }
    this.emit();
  }

  private emit(): void {
    const current = this.snapshot();
    for (const listener of this.listeners) listener(current);
  }

  private rejectAll(error: Error): void {
    for (const pending of this.pendingRequests.values()) {
      globalThis.clearTimeout(pending.timer);
      pending.reject(error);
    }
    this.pendingRequests.clear();
    for (const pending of this.pendingActions.values()) {
      globalThis.clearTimeout(pending.timer);
      pending.reject(error);
    }
    this.pendingActions.clear();
  }
}

export function diagramTypeFor(path: string): string | undefined {
  const lower = path.toLowerCase();
  if (lower.endsWith(".activity")) return "kide-activity-diagram";
  if (lower.endsWith(".mncspec")) return "kide-mnc-diagram";
  return undefined;
}

export function workspaceUri(path: string): string {
  const normalized = path.replace(/\\/g, "/");
  const segments = normalized.split("/");
  if (
    !normalized ||
    normalized.startsWith("/") ||
    segments.some((segment) => !segment || segment === "." || segment === "..")
  ) {
    throw new Error("Graphical model path must be a safe project-relative path.");
  }
  return "kide-workspace:/" + segments.map(encodeURIComponent).join("/");
}

export function glspGatewayUrl(origin: string, workspaceId: string): string {
  const url = new URL(origin.trim());
  if (url.protocol === "https:") url.protocol = "wss:";
  else if (url.protocol === "http:") url.protocol = "ws:";
  else if (url.protocol !== "ws:" && url.protocol !== "wss:") {
    throw new Error("Graphical gateway origin must use http(s) or ws(s).");
  }
  url.pathname = "/glsp";
  url.search = "";
  url.hash = "";
  url.searchParams.set("workspaceId", workspaceId);
  return url.toString();
}

function base64Url(value: string): string {
  if (!value || value.length > 16_384 || /[\u0000-\u001f\u007f]/.test(value)) {
    throw new Error("Access token is invalid for the graphical gateway.");
  }
  const bytes = new TextEncoder().encode(value);
  let binary = "";
  for (const byte of bytes) binary += String.fromCharCode(byte);
  return btoa(binary)
    .replace(/\+/g, "-")
    .replace(/\//g, "_")
    .replace(/=+$/g, "");
}
