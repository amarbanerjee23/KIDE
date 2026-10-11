import { readFileSync } from "node:fs";
import type { Page } from "@playwright/test";
import { clientCapabilities } from "../src/lspClient";

/**
 * PR103: real deployed Xtext + GLSP semantic qualification.
 * The fixtures and required editor services come from the same product
 * manifests used to qualify the packaged Eclipse language server.
 * No browser parser, route interception or fabricated protocol result.
 */
interface Language {
  id: string;
  extension: string;
  language_id: string;
}
interface Feature {
  id: string;
  qualification: string;
  capability: string | null;
}
interface Probe {
  symbol: string;
  hover_token: string;
  references_min: number;
  folding_min: number;
  semantic_tokens_min: number;
  document_highlights_min: number;
  document_highlight_token: string;
  completion: { text: string; position: { line: number; character: number } };
  definition?: { token: string; target_extension: string; occurrence?: number };
  rename_targets: string[];
  code_action?: { diagnostic_code: string; title: string; replacement: string };
  workspace_symbol?: string;
}
interface Case extends Probe {
  id: string;
  path: string;
  content: string;
  languageId: string;
}

const registry = JSON.parse(readFileSync(
  new URL("../../product/languages.json", import.meta.url), "utf8"
)) as { languages: Language[] };
const matrix = JSON.parse(readFileSync(
  new URL("../../product/lsp-capabilities.json", import.meta.url), "utf8"
)) as { features: Feature[]; languages: Record<string, Probe> };

const modelByExtension: Record<string, string> = {
  dml: "golden.dml",
  op: "golden.op",
  mncspec: "device.mncspec",
  cap: "observe.cap",
  activity: "workflow.activity",
  krl: "golden.krl"
};

export async function assertLiveXtextParity(
  page: Page, api: string, token: string, workspaceId: string,
  imported: Record<string, string>
): Promise<void> {
  const languages = registry.languages;
  if (languages.length !== 6 || Object.keys(modelByExtension).length !== 6 ||
      new Set(languages.map(x => x.extension)).size !== 6) {
    throw new Error("Live editor acceptance no longer covers every production KIDE DSL");
  }
  const cases: Case[] = languages.map(language => {
    const path = modelByExtension[language.extension];
    const probe = matrix.languages[language.id];
    const content = imported[path];
    if (!path || !probe || !content || !probe.completion?.text ||
        !probe.hover_token || !probe.document_highlight_token ||
        !probe.symbol || !language.language_id) {
      throw new Error("Incomplete production language acceptance fixture: " + language.id);
    }
    return {
      ...probe, id: language.id, path, content, languageId: language.language_id
    };
  });
  const productionClientCapabilities = clientCapabilities();
  const mandatoryCapabilities = matrix.features
    .filter(f => f.qualification === "required" && f.capability)
    .map(f => f.capability as string);

  await page.evaluate(async ({ api, token, workspaceId, cases, mandatoryCapabilities, productionClientCapabilities }) => {
    const wsOrigin = new URL(api);
    wsOrigin.protocol = "wss:";
    wsOrigin.pathname = "/lsp";
    wsOrigin.search = new URLSearchParams({ workspaceId }).toString();
    const bytes = new TextEncoder().encode(token);
    let binary = "";
    for (const byte of bytes) binary += String.fromCharCode(byte);
    const bearer = btoa(binary).replaceAll("+", "-").replaceAll("/", "_").replace(/=+$/, "");
    const socket = new WebSocket(wsOrigin.toString(), ["kide.lsp.v1", "kide.bearer." + bearer]);
    const pending = new Map<number, {
      resolve: (result: unknown) => void;
      reject: (reason: Error) => void;
      timer: number;
    }>();
    let nextId = 1;
    const send = (method: string, params: unknown): void => {
      if (socket.readyState !== WebSocket.OPEN) {
        throw new Error("Xtext disconnected while testing " + method);
      }
      socket.send(JSON.stringify({ jsonrpc: "2.0", method, params }));
    };
    const request = (method: string, params: unknown): Promise<unknown> => {
      const id = nextId++;
      return new Promise<unknown>((resolve, reject) => {
        const timer = window.setTimeout(() => {
          pending.delete(id);
          reject(new Error("Xtext request timed out: " + method));
        }, 20_000);
        pending.set(id, { resolve, reject, timer });
        socket.send(JSON.stringify({ jsonrpc: "2.0", id, method, params }));
      });
    };
    socket.addEventListener("message", event => {
      let reply: { id?: number; error?: unknown; result?: unknown };
      try { reply = JSON.parse(String(event.data)); } catch { return; }
      if (typeof reply.id !== "number") return;
      const waiter = pending.get(reply.id);
      if (!waiter) return;
      pending.delete(reply.id);
      clearTimeout(waiter.timer);
      if (reply.error) waiter.reject(new Error("Xtext semantic RPC returned an error"));
      else waiter.resolve(reply.result);
    });

    const locate = (text: string, token: string): { line: number; character: number } => {
      const index = text.indexOf(token);
      if (index < 0) throw new Error("LSP parity token is missing from staging model");
      const before = text.slice(0, index);
      const pieces = before.split("\n");
      return { line: pieces.length - 1, character: pieces[pieces.length - 1].length + 1 };
    };
    const locateOccurrence = (text: string, token: string, occurrence: number) => {
      if (!Number.isInteger(occurrence) || occurrence < 1) {
        throw new Error("Invalid canonical navigation occurrence");
      }
      let from = 0;
      let index = -1;
      for (let count = 0; count < occurrence; count += 1) {
        index = text.indexOf(token, from);
        if (index < 0) throw new Error("Canonical navigation target is missing");
        from = index + token.length;
      }
      const before = text.slice(0, index);
      const lines = before.split("\n");
      return { line: lines.length - 1, character: lines[lines.length - 1].length + 1 };
    };
    const symbolNames = (symbols: unknown): string[] => {
      if (!Array.isArray(symbols)) return [];
      const result: string[] = [];
      for (const symbol of symbols) {
        if (!symbol || typeof symbol !== "object") continue;
        const item = symbol as { name?: unknown; children?: unknown };
        if (typeof item.name === "string") result.push(item.name);
        result.push(...symbolNames(item.children));
      }
      return result;
    };
    const completionItems = (value: unknown): unknown[] => {
      if (Array.isArray(value)) return value;
      if (value && typeof value === "object" && "items" in value) {
        const items = (value as { items?: unknown }).items;
        if (Array.isArray(items)) return items;
      }
      return [];
    };

    const opened: string[] = [];
    const close = new Promise<never>((_, reject) => {
      socket.addEventListener("close", () => {
        for (const [id, waiter] of pending) {
          clearTimeout(waiter.timer);
          waiter.reject(new Error("Xtext connection closed during semantic qualification"));
          pending.delete(id);
        }
        reject(new Error("Xtext connection closed during semantic qualification"));
      }, { once: true });
    });
    // Avoid an unhandled rejection after intentional cleanup.
    close.catch(() => {});

    try {
      await new Promise<void>((resolve, reject) => {
        const timer = window.setTimeout(() => reject(new Error("Xtext connect timed out")), 15_000);
        socket.addEventListener("open", () => { clearTimeout(timer); resolve(); }, { once: true });
        socket.addEventListener("error", () => {
          clearTimeout(timer);
          reject(new Error("Xtext service connection failed"));
        }, { once: true });
      });
      if (socket.protocol !== "kide.lsp.v1") {
        throw new Error("Xtext negotiated an unexpected protocol");
      }
      const init = await request("initialize", {
        processId: null,
        rootUri: "kide-workspace:/",
        workspaceFolders: [{ uri: "kide-workspace:/", name: "KIDE PR103" }],
        capabilities: productionClientCapabilities
      }) as { capabilities?: Record<string, unknown> } | null;
      if (!init?.capabilities) throw new Error("Real Xtext initialize omitted capabilities");
      for (const name of mandatoryCapabilities) {
        if (init.capabilities[name] === false || init.capabilities[name] == null) {
          throw new Error("Real Xtext capability missing: " + name);
        }
      }
      send("initialized", {});

      // All six source documents are opened through a single real workspace.
      for (const item of cases) {
        const uri = "kide-workspace:/" + encodeURIComponent(item.path);
        send("textDocument/didOpen", {
          textDocument: { uri, languageId: item.languageId, version: 1, text: item.content }
        });
        opened.push(uri);
      }

      for (const item of cases) {
        const uri = "kide-workspace:/" + encodeURIComponent(item.path);
        const textDocument = { uri };
        const symbols = await request("textDocument/documentSymbol", { textDocument });
        if (!symbolNames(symbols).includes(item.symbol)) {
          throw new Error("Real " + item.id + " document symbols missing " + item.symbol);
        }
        const hover = await request("textDocument/hover", {
          textDocument, position: locate(item.content, item.hover_token)
        });
        if (!hover || typeof hover !== "object") {
          throw new Error("Real " + item.id + " hover response is missing");
        }
        const references = await request("textDocument/references", {
          textDocument, position: locate(item.content, item.hover_token),
          context: { includeDeclaration: true }
        });
        if (!Array.isArray(references) || references.length < item.references_min) {
          throw new Error("Real " + item.id + " references are below Eclipse minimum");
        }
        const folds = await request("textDocument/foldingRange", { textDocument });
        if (!Array.isArray(folds) || folds.length < item.folding_min) {
          throw new Error("Real " + item.id + " folding is below Eclipse minimum");
        }
        const highlights = await request("textDocument/documentHighlight", {
          textDocument, position: locate(item.content, item.document_highlight_token)
        });
        if (!Array.isArray(highlights) ||
            highlights.length < item.document_highlights_min) {
          throw new Error("Real " + item.id + " highlights are below Eclipse minimum");
        }
        const formatted = await request("textDocument/formatting", {
          textDocument, options: { tabSize: 2, insertSpaces: true }
        });
        if (formatted !== null && !Array.isArray(formatted)) {
          throw new Error("Real " + item.id + " formatting returned an invalid edit shape");
        }
        const rangeEdits = await request("textDocument/rangeFormatting", {
          textDocument,
          range: {
            start: { line: 0, character: 0 },
            end: { line: item.content.split("\n").length - 1, character: 0 }
          },
          options: { tabSize: 2, insertSpaces: true }
        });
        if (rangeEdits !== null && !Array.isArray(rangeEdits)) {
          throw new Error("Real " + item.id + " range formatting is incompatible");
        }
        if (item.definition) {
          const destination = await request("textDocument/definition", {
            textDocument,
            position: locateOccurrence(item.content, item.definition.token,
              item.definition.occurrence ?? 1)
          });
          const locations = Array.isArray(destination)
            ? destination : destination ? [destination] : [];
          if (!locations.some(location =>
            location && typeof location === "object" &&
            typeof (location as { uri?: unknown }).uri === "string" &&
            ((location as { uri: string }).uri).endsWith("." + item.definition!.target_extension)
          )) {
            throw new Error("Real " + item.id + " cross-DSL definition navigation failed");
          }
        }
        const rename = await request("textDocument/rename", {
          textDocument, position: locate(item.content, item.symbol),
          newName: "KideParityRenamed"
        }) as { changes?: Record<string, unknown>; documentChanges?: unknown[] } | null;
        const targets = [
          ...Object.keys(rename?.changes ?? {}),
          ...(rename?.documentChanges ?? []).flatMap(change => {
            if (!change || typeof change !== "object") return [];
            const info = change as { textDocument?: { uri?: string } };
            return info.textDocument?.uri ? [info.textDocument.uri] : [];
          })
        ];
        if (!targets.length ||
            !item.rename_targets.every(originalPath => targets.some(target =>
              target.endsWith("." + originalPath.split(".").pop())
            ))) {
          throw new Error("Real " + item.id + " rename omitted Eclipse target documents");
        }
        if (item.workspace_symbol) {
          const workspaceSymbols = await request("workspace/symbol", {
            query: item.workspace_symbol
          });
          if (!symbolNames(workspaceSymbols).includes(item.workspace_symbol)) {
            throw new Error("Real " + item.id + " workspace symbols are missing");
          }
        }
        if (item.code_action) {
          const actions = await request("textDocument/codeAction", {
            textDocument,
            range: { start: { line: 0, character: 0 },
              end: { line: 0, character: 1 } },
            context: { diagnostics: [] }
          });
          if (actions !== null && !Array.isArray(actions)) {
            throw new Error("Real " + item.id + " code action protocol is incompatible");
          }
        }
        const semantic = await request("textDocument/semanticTokens/full", { textDocument })
          as { data?: unknown } | null;
        if (item.semantic_tokens_min > 0) {
          if (!semantic || !Array.isArray(semantic.data) ||
              semantic.data.length < item.semantic_tokens_min * 5 ||
              semantic.data.length % 5 !== 0) {
            throw new Error("Real " + item.id + " semantic tokens are below Eclipse minimum");
          }
        }

        // A virtual document change is NOT persisted to the canonical model.
        // Exercise language-specific Xtext completion using the exact probes
        // also used by the packaged Eclipse/LSP qualification suite.
        send("textDocument/didChange", {
          textDocument: { uri, version: 2 },
          contentChanges: [{ text: item.completion.text }]
        });
        const result = await request("textDocument/completion", {
          textDocument, position: item.completion.position,
          context: { triggerKind: 1 }
        });
        if (completionItems(result).length === 0) {
          throw new Error("Real " + item.id + " completion produced no proposals");
        }
        send("textDocument/didChange", {
          textDocument: { uri, version: 3 },
          contentChanges: [{ text: item.content }]
        });
      }
    } finally {
      if (socket.readyState === WebSocket.OPEN) {
        for (const uri of opened) send("textDocument/didClose", { textDocument: { uri } });
      }
      socket.close();
    }
  }, { api, token, workspaceId, cases, mandatoryCapabilities, productionClientCapabilities });
}

export async function assertLiveGlspModels(
  page: Page, api: string, token: string, workspaceId: string,
  paths: [string, string] | string[]
): Promise<void> {
  const sourceTypes: Record<string, string> = {
    "workflow.activity": "kide-activity-diagram",
    "device.mncspec": "kide-mnc-diagram"
  };
  if (paths.length !== 2 || paths.some(path => !sourceTypes[path])) {
    throw new Error("GLSP live parity requires both Activity and MNC diagrams");
  }
  for (const path of paths) {
    await page.evaluate(async ({ api, token, workspaceId, path, diagramType }) => {
      const url = new URL(api);
      url.protocol = "wss:";
      url.pathname = "/glsp";
      url.search = new URLSearchParams({ workspaceId }).toString();
      let raw = "";
      for (const byte of new TextEncoder().encode(token)) raw += String.fromCharCode(byte);
      const bearer = btoa(raw).replaceAll("+", "-").replaceAll("/", "_").replace(/=+$/, "");
      const socket = new WebSocket(url.toString(), ["kide.glsp.v1", "kide.bearer." + bearer]);
      const clientId = "pr103-" + crypto.randomUUID();
      let id = 0;
      let actionId = 0;
      const requests = new Map<number, (response: unknown, error?: unknown) => void>();
      const actions = new Map<string, (response: unknown) => void>();
      socket.addEventListener("message", event => {
        let msg: {
          id?: number; result?: unknown; error?: unknown;
          method?: string;
          params?: { clientId?: string; action?: { responseId?: string } & Record<string, unknown> };
        };
        try { msg = JSON.parse(String(event.data)); } catch { return; }
        if (typeof msg.id === "number") {
          const finish = requests.get(msg.id);
          if (finish) { requests.delete(msg.id); finish(msg.result, msg.error); }
        }
        const action = msg.params?.action;
        if (msg.method === "process" && msg.params?.clientId === clientId &&
            action?.responseId) {
          const finish = actions.get(action.responseId);
          if (finish) { actions.delete(action.responseId); finish(action); }
        }
      });
      const rpc = (method: string, params: unknown) => {
        const requestId = ++id;
        return new Promise<unknown>((resolve, reject) => {
          const timer = window.setTimeout(() => reject(new Error("GLSP RPC timed out: " + method)), 20_000);
          requests.set(requestId, (result, error) => {
            clearTimeout(timer);
            if (error) reject(new Error("GLSP RPC failed: " + method));
            else resolve(result);
          });
          socket.send(JSON.stringify({ jsonrpc: "2.0", id: requestId, method, params }));
        });
      };
      const sendAction = (kind: string, fields: Record<string, unknown> = {}) => {
        const requestId = "pr103-action-" + (++actionId);
        return new Promise<Record<string, unknown>>((resolve, reject) => {
          const timer = window.setTimeout(() => reject(new Error("GLSP semantic action timed out: " + kind)), 20_000);
          actions.set(requestId, action => {
            clearTimeout(timer);
            resolve(action as Record<string, unknown>);
          });
          socket.send(JSON.stringify({
            jsonrpc: "2.0", method: "process",
            params: { clientId, action: { kind, requestId, ...fields } }
          }));
        });
      };
      try {
        await new Promise<void>((resolve, reject) => {
          const timer = window.setTimeout(() => reject(new Error("GLSP handshake timed out")), 15_000);
          socket.addEventListener("open", () => { clearTimeout(timer); resolve(); }, { once: true });
          socket.addEventListener("error", () => {
            clearTimeout(timer);
            reject(new Error("Real GLSP gateway failed"));
          }, { once: true });
        });
        if (socket.protocol !== "kide.glsp.v1") throw new Error("GLSP subprotocol mismatch");
        const init = await rpc("initialize", {
          applicationId: "KIDE PR103 live semantic parity", protocolVersion: "1.0.0"
        }) as { protocolVersion?: string } | null;
        if (init?.protocolVersion !== "1.0.0") throw new Error("GLSP version mismatch");
        await rpc("initializeClientSession", {
          clientSessionId: clientId, diagramType,
          clientActionKinds: [
            "setModel", "updateModel", "setDirtyState", "setMarkers", "setTypeHints", "status"
          ]
        });
        const model = await sendAction("requestModel", {
          options: { sourceUri: "kide-workspace:/" + encodeURIComponent(path), diagramType }
        });
        const root = model.newRoot as { id?: string; children?: unknown[] } | undefined;
        if (model.kind !== "setModel" || !root?.id || !Array.isArray(root.children) ||
            root.children.length === 0) {
          throw new Error("Real " + diagramType + " failed to return a populated EMF GLSP model");
        }
        const hints = await sendAction("requestTypeHints");
        if (hints.kind !== "setTypeHints" || !Array.isArray(hints.shapeHints) ||
            !Array.isArray(hints.edgeHints)) {
          throw new Error("Real " + diagramType + " omitted graphical edit type hints");
        }
      } finally {
        socket.close();
      }
    }, { api, token, workspaceId, path, diagramType: sourceTypes[path] });
  }
}
