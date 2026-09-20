export interface Position {
  line: number;
  character: number;
}

export interface Range {
  start: Position;
  end: Position;
}

export interface Location {
  uri: string;
  range: Range;
}

export interface Diagnostic {
  range: Range;
  severity?: number;
  code?: string | number;
  source?: string;
  message: string;
}

export interface TextEdit {
  range: Range;
  newText: string;
}

export interface WorkspaceEdit {
  changes?: Record<string, TextEdit[]>;
  documentChanges?: Array<{
    textDocument?: { uri: string; version?: number | null };
    edits?: TextEdit[];
    kind?: string;
    uri?: string;
    oldUri?: string;
    newUri?: string;
  }>;
}

export interface CompletionItem {
  label: string;
  kind?: number;
  detail?: string;
  documentation?: string | { kind: string; value: string };
  insertText?: string;
  insertTextFormat?: number;
  sortText?: string;
  filterText?: string;
  textEdit?: TextEdit;
}

export interface Hover {
  contents:
    | string
    | { kind: string; value: string }
    | Array<string | { language?: string; value: string }>;
  range?: Range;
}

export interface DocumentSymbol {
  name: string;
  detail?: string;
  kind: number;
  range: Range;
  selectionRange: Range;
  children?: DocumentSymbol[];
}

export interface SymbolInformation {
  name: string;
  kind: number;
  location: Location;
  containerName?: string;
}

export interface CodeAction {
  title: string;
  kind?: string;
  edit?: WorkspaceEdit;
  command?: {
    title: string;
    command: string;
    arguments?: unknown[];
  };
  disabled?: { reason: string };
}

export interface FoldingRange {
  startLine: number;
  startCharacter?: number;
  endLine: number;
  endCharacter?: number;
  kind?: string;
}

export interface ServerCapabilities {
  [key: string]: unknown;
  completionProvider?: unknown;
  hoverProvider?: unknown;
  definitionProvider?: unknown;
  referencesProvider?: unknown;
  documentSymbolProvider?: unknown;
  workspaceSymbolProvider?: unknown;
  documentFormattingProvider?: unknown;
  renameProvider?: unknown;
  codeActionProvider?: unknown;
  foldingRangeProvider?: unknown;
  semanticTokensProvider?: {
    legend?: { tokenTypes?: string[]; tokenModifiers?: string[] };
    full?: boolean | { delta?: boolean };
    range?: boolean;
  };
}

interface Pending {
  resolve(value: unknown): void;
  reject(error: Error): void;
  timer: ReturnType<typeof setTimeout>;
}

type NotificationHandler = (params: unknown) => void;
type ApplyEditHandler = (edit: WorkspaceEdit) => Promise<boolean>;

export class KideLspClient {
  private socket: WebSocket | undefined;
  private nextId = 1;
  private readonly pending = new Map<number, Pending>();
  private readonly notifications = new Map<string, Set<NotificationHandler>>();
  private applyEditHandler: ApplyEditHandler | undefined;
  private connected = false;

  capabilities: ServerCapabilities = {};

  constructor(
    private readonly gatewayOrigin: string,
    private readonly accessToken: string,
    private readonly workspaceId: string
  ) {}

  async connect(): Promise<void> {
    if (this.connected) return;
    const endpoint = gatewayUrl(this.gatewayOrigin, this.workspaceId);
    const credential = "kide.bearer." + base64Url(this.accessToken);
    const socket = new WebSocket(endpoint, ["kide.lsp.v1", credential]);
    this.socket = socket;

    await new Promise<void>((resolve, reject) => {
      const timer = globalThis.setTimeout(
        () => reject(new Error("Language service connection timed out.")),
        10_000
      );
      socket.addEventListener("open", () => {
        globalThis.clearTimeout(timer);
        if (socket.protocol && socket.protocol !== "kide.lsp.v1") {
          reject(new Error("Language service negotiated an unexpected WebSocket protocol."));
          return;
        }
        resolve();
      }, { once: true });
      socket.addEventListener("error", () => {
        globalThis.clearTimeout(timer);
        reject(new Error("Language service WebSocket could not be opened."));
      }, { once: true });
    });

    socket.addEventListener("message", (event) => this.onMessage(event.data));
    socket.addEventListener("close", () => {
      this.connected = false;
      this.rejectPending(new Error("Language service connection closed."));
    });
    socket.addEventListener("error", () => {
      this.rejectPending(new Error("Language service connection failed."));
    });

    const initialized = await this.request<{ capabilities?: ServerCapabilities }>(
      "initialize",
      {
        processId: null,
        clientInfo: { name: "KIDE Web", version: "0.1.0" },
        rootUri: "kide-workspace:/",
        workspaceFolders: [
          { uri: "kide-workspace:/", name: "KIDE Project" }
        ],
        capabilities: clientCapabilities()
      }
    );
    this.capabilities = initialized?.capabilities ?? {};
    this.notify("initialized", {});
    this.connected = true;
  }

  setApplyEditHandler(handler: ApplyEditHandler | undefined): void {
    this.applyEditHandler = handler;
  }

  onNotification(method: string, handler: NotificationHandler): () => void {
    const handlers = this.notifications.get(method) ?? new Set();
    handlers.add(handler);
    this.notifications.set(method, handlers);
    return () => handlers.delete(handler);
  }

  hasCapability(name: keyof ServerCapabilities): boolean {
    const value = this.capabilities[name];
    return value !== undefined && value !== null && value !== false;
  }

  didOpen(uri: string, languageId: string, version: number, text: string): void {
    this.notify("textDocument/didOpen", {
      textDocument: { uri, languageId, version, text }
    });
  }

  didChange(uri: string, version: number, text: string): void {
    this.notify("textDocument/didChange", {
      textDocument: { uri, version },
      contentChanges: [{ text }]
    });
  }

  didClose(uri: string): void {
    this.notify("textDocument/didClose", {
      textDocument: { uri }
    });
  }

  completion(uri: string, position: Position): Promise<CompletionItem[] | { items: CompletionItem[] } | null> {
    return this.request("textDocument/completion", {
      textDocument: { uri },
      position,
      context: { triggerKind: 1 }
    });
  }

  hover(uri: string, position: Position): Promise<Hover | null> {
    return this.request("textDocument/hover", {
      textDocument: { uri },
      position
    });
  }

  definition(uri: string, position: Position): Promise<Location | Location[] | null> {
    return this.request("textDocument/definition", {
      textDocument: { uri },
      position
    });
  }

  references(uri: string, position: Position): Promise<Location[] | null> {
    return this.request("textDocument/references", {
      textDocument: { uri },
      position,
      context: { includeDeclaration: true }
    });
  }

  documentSymbols(uri: string): Promise<Array<DocumentSymbol | SymbolInformation> | null> {
    return this.request("textDocument/documentSymbol", {
      textDocument: { uri }
    });
  }

  workspaceSymbols(query: string): Promise<SymbolInformation[] | null> {
    return this.request("workspace/symbol", { query });
  }

  formatting(uri: string): Promise<TextEdit[] | null> {
    return this.request("textDocument/formatting", {
      textDocument: { uri },
      options: { tabSize: 2, insertSpaces: true, trimTrailingWhitespace: true }
    });
  }

  rename(uri: string, position: Position, newName: string): Promise<WorkspaceEdit | null> {
    return this.request("textDocument/rename", {
      textDocument: { uri },
      position,
      newName
    });
  }

  codeActions(uri: string, range: Range, diagnostics: Diagnostic[]): Promise<CodeAction[] | null> {
    return this.request("textDocument/codeAction", {
      textDocument: { uri },
      range,
      context: { diagnostics }
    });
  }

  semanticTokens(uri: string): Promise<{ data: number[]; resultId?: string } | null> {
    return this.request("textDocument/semanticTokens/full", {
      textDocument: { uri }
    });
  }

  foldingRanges(uri: string): Promise<FoldingRange[] | null> {
    return this.request("textDocument/foldingRange", {
      textDocument: { uri }
    });
  }

  executeCommand(command: string, args: unknown[] = []): Promise<unknown> {
    return this.request("workspace/executeCommand", {
      command,
      arguments: args
    });
  }

  async dispose(): Promise<void> {
    const socket = this.socket;
    if (!socket) return;
    if (socket.readyState === WebSocket.OPEN) {
      try {
        await this.request("shutdown", null);
        this.notify("exit", undefined);
      } catch {
        // Closing the browser session remains safe even if the server is already gone.
      }
      socket.close(1000, "KIDE web language client closed");
    }
    this.socket = undefined;
    this.connected = false;
    this.rejectPending(new Error("Language service client disposed."));
  }

  private request<T>(method: string, params: unknown): Promise<T> {
    const socket = this.requireSocket();
    const id = this.nextId++;
    return new Promise<T>((resolve, reject) => {
      const timer = globalThis.setTimeout(() => {
        this.pending.delete(id);
        reject(new Error(`Language service request timed out: ${method}`));
      }, 15_000);
      this.pending.set(id, {
        resolve: (value) => resolve(value as T),
        reject,
        timer
      });
      socket.send(JSON.stringify({
        jsonrpc: "2.0",
        id,
        method,
        params
      }));
    });
  }

  private notify(method: string, params: unknown): void {
    const socket = this.requireSocket();
    socket.send(JSON.stringify({
      jsonrpc: "2.0",
      method,
      ...(params === undefined ? {} : { params })
    }));
  }

  private requireSocket(): WebSocket {
    if (!this.socket || this.socket.readyState !== WebSocket.OPEN) {
      throw new Error("Language service is not connected.");
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
      const pending = this.pending.get(message.id);
      if (!pending) return;
      this.pending.delete(message.id);
      globalThis.clearTimeout(pending.timer);
      if (message.error && typeof message.error === "object") {
        const error = message.error as { message?: string; code?: number };
        pending.reject(new Error(
          `Language service error ${error.code ?? "unknown"}: ${error.message ?? "request failed"}`
        ));
      } else {
        pending.resolve(message.result);
      }
      return;
    }

    if (typeof message.method === "string" && typeof message.id === "number") {
      void this.handleServerRequest(
        message.id,
        message.method,
        message.params
      );
      return;
    }

    if (typeof message.method === "string") {
      for (const handler of this.notifications.get(message.method) ?? []) {
        handler(message.params);
      }
    }
  }

  private async handleServerRequest(
    id: number,
    method: string,
    params: unknown
  ): Promise<void> {
    try {
      if (method === "workspace/configuration") {
        const items = (
          params as { items?: unknown[] } | undefined
        )?.items ?? [];
        this.respond(id, items.map(() => null));
        return;
      }
      if (method === "workspace/applyEdit") {
        const edit = (
          params as { edit?: WorkspaceEdit } | undefined
        )?.edit;
        const applied = edit && this.applyEditHandler
          ? await this.applyEditHandler(edit)
          : false;
        this.respond(id, { applied });
        return;
      }
      if (method === "window/showMessageRequest") {
        this.respond(id, null);
        return;
      }
      this.respondError(id, -32601, "Browser client method is not implemented.");
    } catch (error) {
      this.respondError(
        id,
        -32603,
        error instanceof Error ? error.message : "Browser client request failed."
      );
    }
  }

  private respond(id: number, result: unknown): void {
    this.requireSocket().send(JSON.stringify({ jsonrpc: "2.0", id, result }));
  }

  private respondError(id: number, code: number, message: string): void {
    this.requireSocket().send(JSON.stringify({
      jsonrpc: "2.0",
      id,
      error: { code, message }
    }));
  }

  private rejectPending(error: Error): void {
    for (const pending of this.pending.values()) {
      globalThis.clearTimeout(pending.timer);
      pending.reject(error);
    }
    this.pending.clear();
  }
}

function gatewayUrl(origin: string, workspaceId: string): string {
  const url = new URL(origin.trim());
  if (url.protocol === "https:") url.protocol = "wss:";
  else if (url.protocol === "http:") url.protocol = "ws:";
  else if (url.protocol !== "ws:" && url.protocol !== "wss:") {
    throw new Error("Language gateway origin must use http(s) or ws(s).");
  }
  url.pathname = "/lsp";
  url.search = "";
  url.hash = "";
  url.searchParams.set("workspaceId", workspaceId);
  return url.toString();
}

function base64Url(value: string): string {
  if (!value || value.length > 16_384 || /[\u0000-\u001f\u007f]/.test(value)) {
    throw new Error("Access token is invalid for the language gateway.");
  }
  const bytes = new TextEncoder().encode(value);
  let binary = "";
  for (const byte of bytes) binary += String.fromCharCode(byte);
  return btoa(binary)
    .replace(/\+/g, "-")
    .replace(/\//g, "_")
    .replace(/=+$/g, "");
}

function clientCapabilities(): Record<string, unknown> {
  return {
    workspace: {
      applyEdit: true,
      workspaceEdit: {
        documentChanges: true,
        resourceOperations: ["create", "rename", "delete"]
      },
      symbol: { dynamicRegistration: false }
    },
    textDocument: {
      synchronization: {
        dynamicRegistration: false,
        willSave: false,
        didSave: false
      },
      completion: {
        dynamicRegistration: false,
        completionItem: {
          snippetSupport: true,
          documentationFormat: ["markdown", "plaintext"]
        }
      },
      hover: {
        dynamicRegistration: false,
        contentFormat: ["markdown", "plaintext"]
      },
      definition: { dynamicRegistration: false, linkSupport: false },
      references: { dynamicRegistration: false },
      documentSymbol: {
        dynamicRegistration: false,
        hierarchicalDocumentSymbolSupport: true
      },
      formatting: { dynamicRegistration: false },
      rename: { dynamicRegistration: false, prepareSupport: false },
      codeAction: {
        dynamicRegistration: false,
        codeActionLiteralSupport: {
          codeActionKind: { valueSet: ["", "quickfix", "refactor", "source"] }
        }
      },
      foldingRange: { dynamicRegistration: false, lineFoldingOnly: false },
      semanticTokens: {
        dynamicRegistration: false,
        requests: { range: true, full: true },
        tokenTypes: [
          "namespace", "type", "class", "enum", "interface", "struct",
          "typeParameter", "parameter", "variable", "property", "enumMember",
          "event", "function", "method", "macro", "keyword", "modifier",
          "comment", "string", "number", "regexp", "operator", "decorator"
        ],
        tokenModifiers: [
          "declaration", "definition", "readonly", "static", "deprecated",
          "abstract", "async", "modification", "documentation", "defaultLibrary"
        ],
        formats: ["relative"],
        overlappingTokenSupport: false,
        multilineTokenSupport: false
      },
      publishDiagnostics: {
        relatedInformation: true,
        versionSupport: true
      }
    }
  };
}
