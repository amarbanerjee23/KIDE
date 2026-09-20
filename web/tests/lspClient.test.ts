import { afterEach, describe, expect, it, vi } from "vitest";
import { KideLspClient } from "../src/lspClient";

class FakeWebSocket {
  static CONNECTING = 0;
  static OPEN = 1;
  static CLOSING = 2;
  static CLOSED = 3;

  static last: FakeWebSocket | undefined;

  readyState = FakeWebSocket.CONNECTING;
  protocol = "kide.lsp.v1";
  readonly sent: Array<Record<string, unknown>> = [];
  private readonly listeners = new Map<string, Set<(event: any) => void>>();

  constructor(
    readonly url: string,
    readonly protocols: string[]
  ) {
    FakeWebSocket.last = this;
    queueMicrotask(() => {
      this.readyState = FakeWebSocket.OPEN;
      this.emit("open", {});
    });
  }

  addEventListener(type: string, handler: (event: any) => void): void {
    const handlers = this.listeners.get(type) ?? new Set();
    handlers.add(handler);
    this.listeners.set(type, handlers);
  }

  send(raw: string): void {
    const message = JSON.parse(raw) as Record<string, any>;
    this.sent.push(message);
    if (typeof message.id !== "number") return;

    let result: unknown = null;
    if (message.method === "initialize") {
      result = {
        capabilities: {
          completionProvider: {},
          hoverProvider: true,
          definitionProvider: true,
          referencesProvider: true,
          documentSymbolProvider: true,
          workspaceSymbolProvider: true,
          documentFormattingProvider: true,
          renameProvider: true,
          codeActionProvider: true,
          foldingRangeProvider: true,
          semanticTokensProvider: {
            legend: { tokenTypes: ["type"], tokenModifiers: [] },
            full: true
          }
        }
      };
    } else if (message.method === "textDocument/completion") {
      result = [{ label: "DataModel", kind: 14 }];
    } else if (message.method === "workspace/symbol") {
      result = [];
    }

    queueMicrotask(() => this.emit("message", {
      data: JSON.stringify({ jsonrpc: "2.0", id: message.id, result })
    }));
  }

  close(): void {
    this.readyState = FakeWebSocket.CLOSED;
    this.emit("close", {});
  }

  private emit(type: string, event: any): void {
    for (const handler of this.listeners.get(type) ?? []) handler(event);
  }
}

afterEach(() => {
  vi.unstubAllGlobals();
  FakeWebSocket.last = undefined;
});

describe("KideLspClient", () => {
  it("uses authenticated WebSocket subprotocols and project-relative virtual URIs", async () => {
    vi.stubGlobal("WebSocket", FakeWebSocket as unknown as typeof WebSocket);
    const client = new KideLspClient(
      "https://gateway.example/base",
      "opaque-token.value",
      "W04-001"
    );

    await client.connect();
    const socket = FakeWebSocket.last!;
    expect(socket.url).toBe(
      "wss://gateway.example/lsp?workspaceId=W04-001"
    );
    expect(socket.url).not.toContain("opaque-token");
    expect(socket.protocols[0]).toBe("kide.lsp.v1");
    expect(socket.protocols[1]).toMatch(/^kide\.bearer\./);

    const initialize = socket.sent.find(
      (message) => message.method === "initialize"
    ) as Record<string, any>;
    expect(initialize.params.rootUri).toBe("kide-workspace:/");
    expect(client.hasCapability("renameProvider")).toBe(true);

    client.didOpen(
      "kide-workspace:/model.dml",
      "kide-dml",
      1,
      "DataModel Golden {}"
    );
    await expect(
      client.completion("kide-workspace:/model.dml", {
        line: 0,
        character: 10
      })
    ).resolves.toEqual([{ label: "DataModel", kind: 14 }]);

    await Promise.all([
      client.hover("kide-workspace:/model.dml", { line: 0, character: 1 }),
      client.definition("kide-workspace:/model.dml", { line: 0, character: 1 }),
      client.references("kide-workspace:/model.dml", { line: 0, character: 1 }),
      client.documentSymbols("kide-workspace:/model.dml"),
      client.workspaceSymbols("Golden"),
      client.formatting("kide-workspace:/model.dml"),
      client.rename("kide-workspace:/model.dml", { line: 0, character: 1 }, "Renamed"),
      client.codeActions(
        "kide-workspace:/model.dml",
        {
          start: { line: 0, character: 0 },
          end: { line: 0, character: 1 }
        },
        []
      ),
      client.semanticTokens("kide-workspace:/model.dml"),
      client.foldingRanges("kide-workspace:/model.dml")
    ]);

    const methods = new Set(socket.sent.map((message) => message.method));
    for (const method of [
      "textDocument/didOpen",
      "textDocument/completion",
      "textDocument/hover",
      "textDocument/definition",
      "textDocument/references",
      "textDocument/documentSymbol",
      "workspace/symbol",
      "textDocument/formatting",
      "textDocument/rename",
      "textDocument/codeAction",
      "textDocument/semanticTokens/full",
      "textDocument/foldingRange"
    ]) {
      expect(methods.has(method)).toBe(true);
    }

    await client.dispose();
    expect(socket.sent.some((message) => message.method === "shutdown")).toBe(true);
    expect(socket.sent.some((message) => message.method === "exit")).toBe(true);
  });
});
