import * as monaco from "monaco-editor";
import {
  KideLspClient,
  type CodeAction,
  type Diagnostic,
  type DocumentSymbol,
  type Location,
  type Range,
  type SymbolInformation,
  type TextEdit,
  type WorkspaceEdit
} from "./lspClient";
import { LANGUAGE_ASSETS, pathFromWorkspaceUri } from "./languageAssets";
import { MonacoWorkspace } from "./monacoWorkspace";

interface DocumentHost {
  ensureDocument(uri: string): Promise<monaco.editor.ITextModel | undefined>;
}

interface Attachment {
  refs: number;
  version: number;
  subscription: monaco.IDisposable;
}

export class MonacoLspController {
  private readonly disposables: monaco.IDisposable[] = [];
  private readonly attachments = new Map<string, Attachment>();

  constructor(
    readonly client: KideLspClient,
    private readonly workspace: MonacoWorkspace,
    private readonly host: DocumentHost
  ) {
    this.client.setApplyEditHandler((edit) => this.applyWorkspaceEdit(edit));
    this.registerProviders();
    const unsubscribe = this.client.onNotification(
      "textDocument/publishDiagnostics",
      (params) => this.publishDiagnostics(params)
    );
    this.disposables.push({ dispose: unsubscribe });
  }

  attachModel(model: monaco.editor.ITextModel): monaco.IDisposable {
    const uri = model.uri.toString();
    const existing = this.attachments.get(uri);
    if (existing) {
      existing.refs += 1;
      return { dispose: () => this.detach(uri) };
    }

    const attachment: Attachment = {
      refs: 1,
      version: 1,
      subscription: { dispose() {} }
    };
    this.attachments.set(uri, attachment);
    this.client.didOpen(
      uri,
      model.getLanguageId(),
      attachment.version,
      model.getValue()
    );
    attachment.subscription = model.onDidChangeContent(() => {
      attachment.version += 1;
      this.client.didChange(uri, attachment.version, model.getValue());
    });
    return { dispose: () => this.detach(uri) };
  }

  async workspaceSymbols(query: string): Promise<SymbolInformation[]> {
    if (!this.client.hasCapability("workspaceSymbolProvider")) return [];
    return (await this.client.workspaceSymbols(query)) ?? [];
  }

  async revealLocation(location: Location): Promise<{
    path: string;
    range: monaco.Range;
  } | undefined> {
    const model = await this.host.ensureDocument(location.uri);
    if (!model) return undefined;
    return {
      path: pathFromWorkspaceUri(location.uri),
      range: toRange(location.range)
    };
  }

  dispose(): void {
    for (const [uri, attachment] of this.attachments) {
      attachment.subscription.dispose();
      this.client.didClose(uri);
      const model = this.workspace.getByUri(uri);
      if (model) monaco.editor.setModelMarkers(model, "kide-lsp", []);
    }
    this.attachments.clear();
    for (const disposable of this.disposables) disposable.dispose();
    this.disposables.length = 0;
    this.client.setApplyEditHandler(undefined);
  }

  private detach(uri: string): void {
    const attachment = this.attachments.get(uri);
    if (!attachment) return;
    attachment.refs -= 1;
    if (attachment.refs > 0) return;
    attachment.subscription.dispose();
    this.attachments.delete(uri);
    this.client.didClose(uri);
  }

  private registerProviders(): void {
    for (const asset of LANGUAGE_ASSETS) {
      const languageId = asset.language_id;

      if (this.client.hasCapability("completionProvider")) {
        this.disposables.push(monaco.languages.registerCompletionItemProvider(
          languageId,
          {
            triggerCharacters: [".", ":", "(", "{", "["],
            provideCompletionItems: async (model, position) => {
              const result = await this.client.completion(
                model.uri.toString(),
                fromPosition(position)
              );
              const items = Array.isArray(result) ? result : result?.items ?? [];
              const word = model.getWordUntilPosition(position);
              const defaultRange = new monaco.Range(
                position.lineNumber,
                word.startColumn,
                position.lineNumber,
                word.endColumn
              );
              return {
                suggestions: items.map((item) => {
                  const edit = item.textEdit;
                  return {
                    label: item.label,
                    kind: completionKind(item.kind),
                    detail: item.detail,
                    documentation: markdown(item.documentation),
                    insertText: edit?.newText ?? item.insertText ?? item.label,
                    insertTextRules: item.insertTextFormat === 2
                      ? monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet
                      : undefined,
                    sortText: item.sortText,
                    filterText: item.filterText,
                    range: edit ? toRange(edit.range) : defaultRange
                  };
                })
              };
            }
          }
        ));
      }

      if (this.client.hasCapability("hoverProvider")) {
        this.disposables.push(monaco.languages.registerHoverProvider(
          languageId,
          {
            provideHover: async (model, position) => {
              const hover = await this.client.hover(
                model.uri.toString(),
                fromPosition(position)
              );
              if (!hover) return null;
              return {
                contents: hoverContents(hover.contents),
                range: hover.range ? toRange(hover.range) : undefined
              };
            }
          }
        ));
      }

      if (this.client.hasCapability("definitionProvider")) {
        this.disposables.push(monaco.languages.registerDefinitionProvider(
          languageId,
          {
            provideDefinition: async (model, position) => {
              const raw = await this.client.definition(
                model.uri.toString(),
                fromPosition(position)
              );
              const locations = raw
                ? (Array.isArray(raw) ? raw : [raw])
                : [];
              await Promise.all(
                locations.map((location) => this.host.ensureDocument(location.uri))
              );
              return locations.map(toLocation);
            }
          }
        ));
      }

      if (this.client.hasCapability("referencesProvider")) {
        this.disposables.push(monaco.languages.registerReferenceProvider(
          languageId,
          {
            provideReferences: async (model, position) => {
              const locations = (
                await this.client.references(
                  model.uri.toString(),
                  fromPosition(position)
                )
              ) ?? [];
              await Promise.all(
                locations.map((location) => this.host.ensureDocument(location.uri))
              );
              return locations.map(toLocation);
            }
          }
        ));
      }

      if (this.client.hasCapability("documentSymbolProvider")) {
        this.disposables.push(monaco.languages.registerDocumentSymbolProvider(
          languageId,
          {
            provideDocumentSymbols: async (model) => {
              const symbols = (
                await this.client.documentSymbols(model.uri.toString())
              ) ?? [];
              return symbols.map(toDocumentSymbol);
            }
          }
        ));
      }

      if (this.client.hasCapability("documentFormattingProvider")) {
        this.disposables.push(monaco.languages.registerDocumentFormattingEditProvider(
          languageId,
          {
            provideDocumentFormattingEdits: async (model) => {
              const edits = (
                await this.client.formatting(model.uri.toString())
              ) ?? [];
              return edits.map(toMonacoTextEdit);
            }
          }
        ));
      }

      if (this.client.hasCapability("renameProvider")) {
        this.disposables.push(monaco.languages.registerRenameProvider(
          languageId,
          {
            provideRenameEdits: async (model, position, newName) => {
              const edit = await this.client.rename(
                model.uri.toString(),
                fromPosition(position),
                newName
              );
              if (!edit) return { edits: [] };
              await this.ensureWorkspaceEdit(edit);
              return toMonacoWorkspaceEdit(edit);
            },
            resolveRenameLocation: async (model, position) => ({
              range: wordRange(model, position),
              text: model.getWordAtPosition(position)?.word ?? ""
            })
          }
        ));
      }

      if (this.client.hasCapability("codeActionProvider")) {
        this.disposables.push(monaco.languages.registerCodeActionProvider(
          languageId,
          {
            provideCodeActions: async (model, range, context) => {
              const diagnostics = context.markers.map(fromMarker);
              const actions = (
                await this.client.codeActions(
                  model.uri.toString(),
                  fromRange(range),
                  diagnostics
                )
              ) ?? [];
              const converted: monaco.languages.CodeAction[] = [];
              for (const action of actions) {
                if (action.disabled || !action.edit) continue;
                await this.ensureWorkspaceEdit(action.edit);
                converted.push(toMonacoCodeAction(action));
              }
              return {
                actions: converted,
                dispose() {}
              };
            }
          }
        ));
      }

      if (this.client.hasCapability("foldingRangeProvider")) {
        this.disposables.push(monaco.languages.registerFoldingRangeProvider(
          languageId,
          {
            provideFoldingRanges: async (model) => {
              const ranges = (
                await this.client.foldingRanges(model.uri.toString())
              ) ?? [];
              return ranges.map((range) => ({
                start: range.startLine + 1,
                end: range.endLine + 1
              }));
            }
          }
        ));
      }

      const semantic = this.client.capabilities.semanticTokensProvider;
      const legend = semantic?.legend;
      if (semantic && legend?.tokenTypes) {
        this.disposables.push(monaco.languages.registerDocumentSemanticTokensProvider(
          languageId,
          {
            getLegend: () => ({
              tokenTypes: legend.tokenTypes ?? [],
              tokenModifiers: legend.tokenModifiers ?? []
            }),
            provideDocumentSemanticTokens: async (model) => {
              const tokens = await this.client.semanticTokens(model.uri.toString());
              if (!tokens) return null;
              return {
                data: Uint32Array.from(tokens.data),
                resultId: tokens.resultId
              };
            },
            releaseDocumentSemanticTokens() {}
          }
        ));
      }
    }
  }

  private publishDiagnostics(params: unknown): void {
    const payload = params as {
      uri?: string;
      diagnostics?: Diagnostic[];
    } | undefined;
    if (!payload?.uri) return;
    const model = this.workspace.getByUri(payload.uri);
    if (!model) return;
    monaco.editor.setModelMarkers(
      model,
      "kide-lsp",
      (payload.diagnostics ?? []).map((diagnostic) => ({
        ...markerRange(diagnostic.range),
        message: diagnostic.message,
        severity: markerSeverity(diagnostic.severity),
        source: diagnostic.source ?? "KIDE",
        code: diagnostic.code
      }))
    );
  }

  private async ensureWorkspaceEdit(edit: WorkspaceEdit): Promise<void> {
    const uris = new Set<string>();
    for (const uri of Object.keys(edit.changes ?? {})) uris.add(uri);
    for (const change of edit.documentChanges ?? []) {
      if (change.textDocument?.uri) uris.add(change.textDocument.uri);
      if (change.kind || change.oldUri || change.newUri || change.uri) {
        throw new Error("LSP file create/rename/delete operations are not supported by this browser workspace.");
      }
    }
    await Promise.all([...uris].map((uri) => this.host.ensureDocument(uri)));
  }

  private async applyWorkspaceEdit(edit: WorkspaceEdit): Promise<boolean> {
    await this.ensureWorkspaceEdit(edit);
    for (const [uri, edits] of workspaceTextEdits(edit)) {
      const model = this.workspace.getByUri(uri);
      if (!model) return false;
      model.pushEditOperations(
        null,
        edits.map((edit) => ({
          range: toRange(edit.range),
          text: edit.newText,
          forceMoveMarkers: true
        })),
        () => null
      );
    }
    return true;
  }
}

function workspaceTextEdits(edit: WorkspaceEdit): Map<string, TextEdit[]> {
  const result = new Map<string, TextEdit[]>();
  for (const [uri, edits] of Object.entries(edit.changes ?? {})) {
    result.set(uri, [...(result.get(uri) ?? []), ...edits]);
  }
  for (const change of edit.documentChanges ?? []) {
    if (!change.textDocument?.uri || !change.edits) continue;
    const uri = change.textDocument.uri;
    result.set(uri, [...(result.get(uri) ?? []), ...change.edits]);
  }
  return result;
}

function toMonacoWorkspaceEdit(edit: WorkspaceEdit): monaco.languages.WorkspaceEdit {
  const edits: monaco.languages.IWorkspaceTextEdit[] = [];
  for (const [uri, changes] of workspaceTextEdits(edit)) {
    for (const change of changes) {
      edits.push({
        resource: monaco.Uri.parse(uri),
        textEdit: {
          range: toRange(change.range),
          text: change.newText
        }
      });
    }
  }
  return { edits };
}

function toMonacoCodeAction(action: CodeAction): monaco.languages.CodeAction {
  return {
    title: action.title,
    kind: action.kind,
    edit: action.edit ? toMonacoWorkspaceEdit(action.edit) : undefined
  };
}

function toDocumentSymbol(
  symbol: DocumentSymbol | SymbolInformation
): monaco.languages.DocumentSymbol {
  if ("location" in symbol) {
    return {
      name: symbol.name,
      detail: symbol.containerName ?? "",
      kind: symbolKind(symbol.kind),
      range: toRange(symbol.location.range),
      selectionRange: toRange(symbol.location.range)
    };
  }
  return {
    name: symbol.name,
    detail: symbol.detail ?? "",
    kind: symbolKind(symbol.kind),
    range: toRange(symbol.range),
    selectionRange: toRange(symbol.selectionRange),
    children: symbol.children?.map(toDocumentSymbol)
  };
}

function toLocation(location: Location): monaco.languages.Location {
  return {
    uri: monaco.Uri.parse(location.uri),
    range: toRange(location.range)
  };
}

function toMonacoTextEdit(edit: TextEdit): monaco.languages.TextEdit {
  return { range: toRange(edit.range), text: edit.newText };
}

function toRange(range: Range): monaco.Range {
  return new monaco.Range(
    range.start.line + 1,
    range.start.character + 1,
    range.end.line + 1,
    range.end.character + 1
  );
}

function fromRange(range: monaco.Range): Range {
  return {
    start: {
      line: range.startLineNumber - 1,
      character: range.startColumn - 1
    },
    end: {
      line: range.endLineNumber - 1,
      character: range.endColumn - 1
    }
  };
}

function fromPosition(position: monaco.Position): {
  line: number;
  character: number;
} {
  return {
    line: position.lineNumber - 1,
    character: position.column - 1
  };
}

function markerRange(range: Range) {
  return {
    startLineNumber: range.start.line + 1,
    startColumn: range.start.character + 1,
    endLineNumber: range.end.line + 1,
    endColumn: range.end.character + 1
  };
}

function fromMarker(marker: monaco.editor.IMarkerData): Diagnostic {
  return {
    range: {
      start: {
        line: marker.startLineNumber - 1,
        character: marker.startColumn - 1
      },
      end: {
        line: marker.endLineNumber - 1,
        character: marker.endColumn - 1
      }
    },
    severity: lspSeverity(marker.severity),
    code: typeof marker.code === "object" ? marker.code.value : marker.code,
    source: marker.source,
    message: marker.message
  };
}

function markerSeverity(severity?: number): monaco.MarkerSeverity {
  switch (severity) {
    case 1: return monaco.MarkerSeverity.Error;
    case 2: return monaco.MarkerSeverity.Warning;
    case 3: return monaco.MarkerSeverity.Info;
    case 4: return monaco.MarkerSeverity.Hint;
    default: return monaco.MarkerSeverity.Info;
  }
}

function lspSeverity(severity: monaco.MarkerSeverity): number {
  if (severity === monaco.MarkerSeverity.Error) return 1;
  if (severity === monaco.MarkerSeverity.Warning) return 2;
  if (severity === monaco.MarkerSeverity.Info) return 3;
  return 4;
}

function completionKind(kind?: number): monaco.languages.CompletionItemKind {
  const values = monaco.languages.CompletionItemKind;
  const map: Record<number, monaco.languages.CompletionItemKind> = {
    2: values.Method,
    3: values.Function,
    4: values.Constructor,
    5: values.Field,
    6: values.Variable,
    7: values.Class,
    8: values.Interface,
    9: values.Module,
    10: values.Property,
    11: values.Unit,
    12: values.Value,
    13: values.Enum,
    14: values.Keyword,
    15: values.Snippet,
    16: values.Color,
    17: values.File,
    18: values.Reference,
    19: values.Folder,
    20: values.EnumMember,
    21: values.Constant,
    22: values.Struct,
    23: values.Event,
    24: values.Operator,
    25: values.TypeParameter
  };
  return kind ? (map[kind] ?? values.Text) : values.Text;
}

function symbolKind(kind: number): monaco.languages.SymbolKind {
  const values = monaco.languages.SymbolKind;
  const map: Record<number, monaco.languages.SymbolKind> = {
    1: values.File,
    2: values.Module,
    3: values.Namespace,
    4: values.Package,
    5: values.Class,
    6: values.Method,
    7: values.Property,
    8: values.Field,
    9: values.Constructor,
    10: values.Enum,
    11: values.Interface,
    12: values.Function,
    13: values.Variable,
    14: values.Constant,
    15: values.String,
    16: values.Number,
    17: values.Boolean,
    18: values.Array,
    19: values.Object,
    20: values.Key,
    21: values.Null,
    22: values.EnumMember,
    23: values.Struct,
    24: values.Event,
    25: values.Operator,
    26: values.TypeParameter
  };
  return map[kind] ?? values.Object;
}

function markdown(
  value: string | { kind: string; value: string } | undefined
): monaco.IMarkdownString | undefined {
  if (!value) return undefined;
  return { value: typeof value === "string" ? value : value.value };
}

function hoverContents(
  contents: string | { kind: string; value: string } | Array<string | { language?: string; value: string }>
): monaco.IMarkdownString[] {
  const values = Array.isArray(contents) ? contents : [contents];
  return values.map((value) => {
    if (typeof value === "string") return { value };
    return { value: value.value };
  });
}

function wordRange(
  model: monaco.editor.ITextModel,
  position: monaco.Position
): monaco.Range {
  const word = model.getWordAtPosition(position);
  if (!word) {
    return new monaco.Range(
      position.lineNumber,
      position.column,
      position.lineNumber,
      position.column
    );
  }
  return new monaco.Range(
    position.lineNumber,
    word.startColumn,
    position.lineNumber,
    word.endColumn
  );
}
