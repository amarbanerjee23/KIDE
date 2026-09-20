import { useEffect, useMemo, useRef, useState } from "react";
import type { ChangeEvent } from "react";
import type * as monaco from "monaco-editor";
import { KideApiClient, ApiClientError } from "./api";
import {
  editableText,
  exportProjectArchive,
  importProjectArchive,
  mediaTypeFor,
  withText
} from "./archive";
import { AutosaveCoordinator } from "./autosave";
import { pathFromWorkspaceUri } from "./languageAssets";
import { KideLspClient, type SymbolInformation } from "./lspClient";
import { MonacoEditor } from "./MonacoEditor";
import { MonacoLspController } from "./monacoLsp";
import { MonacoWorkspace } from "./monacoWorkspace";
import { ensureTextMateLanguageSupport } from "./textmate";
import type { Model, Project, SaveState, WorkspaceEntry } from "./types";

const SERVICE_ORIGIN =
  import.meta.env.VITE_KIDE_API_ORIGIN ?? window.location.origin;
const GATEWAY_ORIGIN =
  import.meta.env.VITE_KIDE_LSP_ORIGIN ?? SERVICE_ORIGIN;

export default function App() {
  const [serviceOrigin, setServiceOrigin] = useState(SERVICE_ORIGIN);
  const [gatewayOrigin, setGatewayOrigin] = useState(GATEWAY_ORIGIN);
  const [token, setToken] = useState("");
  const tokenRef = useRef("");
  tokenRef.current = token;

  const client = useMemo(
    () => new KideApiClient(serviceOrigin, () => tokenRef.current),
    [serviceOrigin]
  );
  const clientRef = useRef(client);
  clientRef.current = client;

  const [serviceStatus, setServiceStatus] = useState("Not connected");
  const [lspStatus, setLspStatus] = useState("Not connected");
  const [projects, setProjects] = useState<Project[]>([]);
  const [project, setProject] = useState<Project>();
  const projectRef = useRef<Project | undefined>(undefined);
  projectRef.current = project;

  const [entries, setEntries] = useState<WorkspaceEntry[]>([]);
  const entriesRef = useRef<WorkspaceEntry[]>([]);
  entriesRef.current = entries;
  const [selectedPath, setSelectedPath] = useState<string>();
  const selectedPathRef = useRef<string | undefined>(undefined);
  selectedPathRef.current = selectedPath;
  const [modelId, setModelId] = useState("selfcheck.dml");
  const [saveState, setSaveState] = useState<SaveState>("clean");
  const [notice, setNotice] = useState("");
  const [conflict, setConflict] = useState<string>();
  const [symbolQuery, setSymbolQuery] = useState("");
  const [symbols, setSymbols] = useState<SymbolInformation[]>([]);
  const [revealRange, setRevealRange] = useState<monaco.Range>();

  const autosaves = useRef(new Map<string, AutosaveCoordinator>());
  const lspClient = useRef<KideLspClient | undefined>(undefined);
  const lspController = useRef<MonacoLspController | undefined>(undefined);
  const workspaceChange = useRef<(path: string, value: string) => void>(() => {});
  const workspace = useMemo(
    () => new MonacoWorkspace((path, value) => workspaceChange.current(path, value)),
    []
  );

  const selected = entries.find((entry) => entry.path === selectedPathRef.current);
  const editorText = selected ? editableText(selected) : null;

  workspaceChange.current = (path, value) => {
    const current = entriesRef.current.find((entry) => entry.path === path);
    if (!current) return;

    updateEntries((items) =>
      items.map((entry) =>
        entry.path === path ? withText(entry, value) : entry
      )
    );

    if (current.source === "remote") {
      ensureAutosave(current).update(value);
    } else {
      try {
        sessionStorage.setItem(`kide:draft:${path}`, value);
        if (path === selectedPath) setSaveState("saved");
      } catch {
        if (path === selectedPath) setSaveState("error");
        setNotice("Browser draft storage is unavailable; export the project to retain this edit.");
      }
    }
  };

  useEffect(() => {
    void ensureTextMateLanguageSupport().catch((error) => {
      setNotice(error instanceof Error ? error.message : "Syntax highlighting could not initialize.");
    });
    return () => {
      void disposeLanguageServices();
      disposeAutosaves();
      workspace.dispose();
    };
  }, [workspace]);

  async function connect() {
    setNotice("");
    setConflict(undefined);
    try {
      const health = await client.health();
      const list = await client.listProjects();
      setServiceStatus(`${health.status} · API ${health.version}`);
      setProjects(list.items);
    } catch (error) {
      showError(error);
    }
  }

  async function openProject(item: Project) {
    setNotice("");
    setConflict(undefined);
    try {
      const opened = await client.getProject(item.id);
      await resetProjectWorkspace();
      setProjectState(opened);
      setNotice(`Opened server project ${opened.displayName}.`);
      await connectLanguageServices(opened);
    } catch (error) {
      showError(error);
    }
  }

  async function connectLanguageServices(opened = projectRef.current) {
    if (!opened) return;
    await disposeLanguageServices();
    if (!opened.workspaceId) {
      setLspStatus("Unavailable · workspace ID missing");
      return;
    }
    const accessToken = tokenRef.current.trim();
    if (!accessToken) {
      setLspStatus("Unavailable · access token required");
      return;
    }

    setLspStatus("Connecting…");
    try {
      const connection = new KideLspClient(
        gatewayOrigin,
        accessToken,
        opened.workspaceId
      );
      await connection.connect();
      const controller = new MonacoLspController(
        connection,
        workspace,
        { ensureDocument: ensureLspDocument }
      );
      lspClient.current = connection;
      lspController.current = controller;
      setLspStatus("Connected · Xtext LSP");
      setSymbols([]);
    } catch (error) {
      setLspStatus("Connection failed");
      showError(error);
    }
  }

  async function loadModel() {
    if (!projectRef.current) return;
    await loadSpecificModel(modelId.trim(), true);
  }

  async function loadSpecificModel(id: string, announce = false) {
    const currentProject = projectRef.current;
    if (!currentProject || !id) return;
    setConflict(undefined);
    try {
      const model = await clientRef.current.getModel(currentProject.id, id);
      const entry = remoteEntry(currentProject.id, model);
      replaceRemoteEntry(entry);
      workspace.ensure(entry.path, model.content);
      setSelectedPath(entry.path);
      setRevealRange(undefined);
      setSaveState("clean");
      if (announce) {
        setNotice(`Loaded ${model.id} at revision ${model.revision}.`);
      }
    } catch (error) {
      showError(error);
    }
  }

  async function ensureLspDocument(uri: string) {
    const path = pathFromWorkspaceUri(uri);
    const existingModel = workspace.get(path);
    if (existingModel) return existingModel;

    const existingEntry = entriesRef.current.find((entry) => entry.path === path);
    if (existingEntry) {
      const text = editableText(existingEntry);
      return text === null ? undefined : workspace.ensure(path, text);
    }

    const currentProject = projectRef.current;
    if (!currentProject) return undefined;
    const model = await clientRef.current.getModel(currentProject.id, path);
    const entry = remoteEntry(currentProject.id, model);
    replaceRemoteEntry(entry);
    return workspace.ensure(path, model.content);
  }

  function replaceRemoteEntry(entry: WorkspaceEntry) {
    autosaves.current.get(entry.path)?.dispose();
    autosaves.current.delete(entry.path);
    updateEntries((current) => upsert(current, entry));
  }

  function ensureAutosave(entry: WorkspaceEntry): AutosaveCoordinator {
    const existing = autosaves.current.get(entry.path);
    if (existing) return existing;
    if (!entry.projectId || !entry.etag) {
      throw new Error("Remote model is missing revision metadata.");
    }

    const coordinator = new AutosaveCoordinator(
      entry.etag,
      (content, expectedEtag) =>
        clientRef.current.writeModel(
          entry.projectId!,
          entry.path,
          content,
          expectedEtag,
          entry.mediaType
        ),
      {
        onState(state) {
          if (entry.path === selectedPathRef.current) setSaveState(state);
        },
        onSaved(model) {
          updateEntries((current) =>
            current.map((candidate) => {
              if (candidate.path !== entry.path) return candidate;
              return {
                ...candidate,
                etag: model.etag,
                revision: model.revision,
                dirty: workspace.text(entry.path) !== model.content
              };
            })
          );
        },
        onConflict(error, localContent) {
          try {
            sessionStorage.setItem(
              `kide:conflict:${entry.projectId}:${entry.path}`,
              localContent
            );
          } catch {
            // Conflict remains fail-safe without browser draft persistence.
          }
          if (entry.path === selectedPathRef.current) {
            setSaveState("conflict");
            setConflict(
              `Server revision changed. Autosave stopped without overwriting it. Request ${error.requestId ?? "unknown"}.`
            );
          }
        },
        onError(error) {
          if (entry.path === selectedPathRef.current) setSaveState("error");
          setNotice(error.message);
        }
      }
    );
    autosaves.current.set(entry.path, coordinator);
    return coordinator;
  }

  function selectEntry(entry: WorkspaceEntry) {
    setSelectedPath(entry.path);
    setRevealRange(undefined);
    setSaveState(entry.dirty ? "pending" : "clean");
    setConflict(undefined);
  }

  async function reloadConflict() {
    const currentProject = projectRef.current;
    if (!currentProject || !selected || selected.source !== "remote") return;
    const localDraft = workspace.text(selected.path) ?? editableText(selected);
    if (localDraft !== null && localDraft !== undefined) {
      try {
        sessionStorage.setItem(
          `kide:conflict:${currentProject.id}:${selected.path}`,
          localDraft
        );
      } catch {
        // Server reload remains safe without session storage.
      }
    }
    await loadSpecificModel(selected.path);
    setConflict(undefined);
    setNotice("Reloaded the server revision. The pre-reload local draft remains in this browser session.");
  }

  async function importArchive(event: ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0];
    event.target.value = "";
    if (!file) return;
    setNotice("");
    try {
      const imported = importProjectArchive(
        new Uint8Array(await file.arrayBuffer())
      );
      await resetProjectWorkspace();
      setProjectState(undefined);
      setProjects([]);
      updateEntries(() => imported);
      const firstEditable = imported.find(
        (entry) => editableText(entry) !== null
      );
      setSelectedPath(firstEditable?.path);
      setSaveState("clean");
      setLspStatus("Unavailable · local archive");
      setNotice(
        `Imported ${imported.length} canonical project files locally. No server project was created.`
      );
    } catch (error) {
      showError(error);
    }
  }

  function exportArchive() {
    if (!entriesRef.current.length) return;
    const materialized = entriesRef.current.map((entry) => {
      const current = workspace.text(entry.path);
      return current === undefined ? entry : withText(entry, current);
    });
    const bytes = exportProjectArchive(materialized);
    const blob = new Blob([bytes as BlobPart], { type: "application/zip" });
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement("a");
    anchor.href = url;
    anchor.download = `${safeName(projectRef.current?.displayName ?? "kide-project")}.zip`;
    anchor.click();
    URL.revokeObjectURL(url);
    setNotice("Exported current project files as a ZIP archive.");
  }

  async function searchSymbols() {
    const controller = lspController.current;
    if (!controller) return;
    try {
      setSymbols(await controller.workspaceSymbols(symbolQuery));
    } catch (error) {
      showError(error);
    }
  }

  async function openSymbol(symbol: SymbolInformation) {
    const controller = lspController.current;
    if (!controller) return;
    try {
      const target = await controller.revealLocation(symbol.location);
      if (!target) return;
      setSelectedPath(target.path);
      setRevealRange(target.range);
    } catch (error) {
      showError(error);
    }
  }

  async function resetProjectWorkspace() {
    await disposeLanguageServices();
    disposeAutosaves();
    workspace.dispose();
    updateEntries(() => []);
    setSelectedPath(undefined);
    setRevealRange(undefined);
    setSymbols([]);
    setSaveState("clean");
  }

  async function disposeLanguageServices() {
    lspController.current?.dispose();
    lspController.current = undefined;
    const connection = lspClient.current;
    lspClient.current = undefined;
    if (connection) await connection.dispose();
    setLspStatus("Not connected");
  }

  function disposeAutosaves() {
    for (const coordinator of autosaves.current.values()) coordinator.dispose();
    autosaves.current.clear();
  }

  function updateEntries(
    update: (current: WorkspaceEntry[]) => WorkspaceEntry[]
  ) {
    setEntries((current) => {
      const next = update(current);
      entriesRef.current = next;
      return next;
    });
  }

  function setProjectState(next: Project | undefined) {
    projectRef.current = next;
    setProject(next);
  }

  function showError(error: unknown) {
    if (error instanceof ApiClientError) {
      const suffix = error.requestId ? ` Request ${error.requestId}.` : "";
      setNotice(`${error.message}${suffix}`);
      return;
    }
    setNotice(error instanceof Error ? error.message : "Unexpected error.");
  }

  return (
    <main className="app-shell">
      <header className="topbar">
        <div>
          <p className="eyebrow">KIDE WEB</p>
          <h1>Engineering Workspace</h1>
          <p className="subtitle">
            Separate browser client · shared enterprise API · shared Xtext semantics
          </p>
        </div>
        <div className="status-stack">
          <div className="service-state" aria-live="polite">{serviceStatus}</div>
          <div className="service-state lsp-state" aria-live="polite">{lspStatus}</div>
        </div>
      </header>

      <section className="connection-panel" aria-label="Service connection">
        <label>
          API origin
          <input
            aria-label="API origin"
            value={serviceOrigin}
            onChange={(event) => setServiceOrigin(event.target.value)}
          />
        </label>
        <label>
          LSP gateway origin
          <input
            aria-label="LSP gateway origin"
            value={gatewayOrigin}
            onChange={(event) => setGatewayOrigin(event.target.value)}
          />
        </label>
        <label>
          Access token
          <input
            aria-label="Access token"
            type="password"
            autoComplete="off"
            value={token}
            onChange={(event) => setToken(event.target.value)}
            placeholder="Held in memory only"
          />
        </label>
        <button onClick={() => void connect()}>Connect API</button>
        <label className="import-button">
          Import project ZIP
          <input
            aria-label="Import project ZIP"
            type="file"
            accept=".zip,application/zip"
            onChange={(event) => void importArchive(event)}
          />
        </label>
        <button disabled={!entries.length} onClick={exportArchive}>Export ZIP</button>
      </section>

      {notice && <div className="notice" role="status">{notice}</div>}
      {conflict && (
        <div className="conflict" role="alert">
          <strong>Revision conflict</strong>
          <span>{conflict}</span>
          <button onClick={() => void reloadConflict()}>Reload server revision</button>
        </div>
      )}

      <section className="workspace">
        <aside className="sidebar">
          <h2>Projects</h2>
          {projects.length === 0 ? (
            <p className="muted">Connect to list authorized projects, or import a local ZIP.</p>
          ) : (
            <ul className="project-list">
              {projects.map((item) => (
                <li key={item.id}>
                  <span>{item.displayName}</span>
                  <button onClick={() => void openProject(item)}>Open</button>
                </li>
              ))}
            </ul>
          )}

          {project && (
            <div className="model-loader">
              <h3>{project.displayName}</h3>
              <p className="muted">
                Textual intelligence is served by the shared Xtext LSP. Model indexing remains a later server phase, so a known model ID is still used here.
              </p>
              <button onClick={() => void connectLanguageServices()}>
                Reconnect language services
              </button>
              <label>
                Model ID
                <input
                  aria-label="Model ID"
                  value={modelId}
                  onChange={(event) => setModelId(event.target.value)}
                />
              </label>
              <button disabled={!modelId.trim()} onClick={() => void loadModel()}>
                Load model
              </button>
            </div>
          )}

          {lspController.current && (
            <div className="symbol-search">
              <h2>Workspace symbols</h2>
              <label>
                Symbol query
                <input
                  aria-label="Symbol query"
                  value={symbolQuery}
                  onChange={(event) => setSymbolQuery(event.target.value)}
                />
              </label>
              <button onClick={() => void searchSymbols()}>Search symbols</button>
              <ul className="symbol-list">
                {symbols.map((symbol, index) => (
                  <li key={symbol.name + index}>
                    <button onClick={() => void openSymbol(symbol)}>
                      <strong>{symbol.name}</strong>
                      <span>{symbol.containerName ?? ""}</span>
                    </button>
                  </li>
                ))}
              </ul>
            </div>
          )}

          <h2>Files</h2>
          <ul className="file-list">
            {entries.map((entry) => (
              <li key={entry.path}>
                <button
                  className={entry.path === selectedPathRef.current ? "selected" : ""}
                  onClick={() => selectEntry(entry)}
                >
                  <span>{entry.path}</span>
                  {entry.dirty && <span aria-label="Unsaved local change">●</span>}
                </button>
              </li>
            ))}
          </ul>
        </aside>

        <section className="editor-panel">
          <div className="editor-toolbar">
            <div>
              <strong>{selected?.path ?? "No file selected"}</strong>
              {selected?.revision && <span>revision {selected.revision}</span>}
            </div>
            <span className={`save-state state-${saveState}`}>{saveState}</span>
          </div>
          {!selected ? (
            <div className="empty-state">
              Open a server project/model or import a project ZIP.
            </div>
          ) : editorText === null ? (
            <div className="empty-state">
              Binary/non-text project content is preserved for export but is not editable in Monaco.
            </div>
          ) : (
            <MonacoEditor
              path={selected.path}
              value={editorText}
              workspace={workspace}
              lsp={lspController.current}
              revealRange={revealRange}
            />
          )}
        </section>
      </section>
    </main>
  );
}

function remoteEntry(projectId: string, model: Model): WorkspaceEntry {
  return {
    path: model.id,
    bytes: new TextEncoder().encode(model.content),
    mediaType: model.mediaType ?? mediaTypeFor(model.id),
    source: "remote",
    projectId,
    etag: model.etag,
    revision: model.revision,
    dirty: false
  };
}

function upsert(
  entries: WorkspaceEntry[],
  replacement: WorkspaceEntry
): WorkspaceEntry[] {
  const existing = entries.findIndex(
    (entry) => entry.path === replacement.path
  );
  if (existing < 0) {
    return [...entries, replacement].sort((a, b) =>
      a.path.localeCompare(b.path)
    );
  }
  return entries.map((entry, index) =>
    index === existing ? replacement : entry
  );
}

function safeName(value: string): string {
  const normalized = value
    .trim()
    .replace(/[^A-Za-z0-9._-]+/g, "-")
    .replace(/^-+|-+$/g, "");
  return normalized || "kide-project";
}
