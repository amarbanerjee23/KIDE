import { useEffect, useMemo, useRef, useState } from "react";
import type { ChangeEvent } from "react";
import { KideApiClient, ApiClientError } from "./api";
import {
  editableText,
  exportProjectArchive,
  importProjectArchive,
  mediaTypeFor,
  withText
} from "./archive";
import { AutosaveCoordinator } from "./autosave";
import { MonacoEditor } from "./MonacoEditor";
import type { Model, Project, SaveState, WorkspaceEntry } from "./types";

const SERVICE_ORIGIN =
  import.meta.env.VITE_KIDE_API_ORIGIN ?? window.location.origin;

export default function App() {
  const [serviceOrigin, setServiceOrigin] = useState(SERVICE_ORIGIN);
  const [token, setToken] = useState("");
  const tokenRef = useRef("");
  tokenRef.current = token;

  const client = useMemo(
    () => new KideApiClient(serviceOrigin, () => tokenRef.current),
    [serviceOrigin]
  );

  const [serviceStatus, setServiceStatus] = useState("Not connected");
  const [projects, setProjects] = useState<Project[]>([]);
  const [project, setProject] = useState<Project>();
  const [entries, setEntries] = useState<WorkspaceEntry[]>([]);
  const [selectedPath, setSelectedPath] = useState<string>();
  const [modelId, setModelId] = useState("selfcheck.dml");
  const [saveState, setSaveState] = useState<SaveState>("clean");
  const [notice, setNotice] = useState("");
  const [conflict, setConflict] = useState<string>();
  const autosave = useRef<AutosaveCoordinator | undefined>(undefined);

  const selected = entries.find((entry) => entry.path === selectedPath);
  const editorText = selected ? editableText(selected) : null;

  useEffect(() => () => autosave.current?.dispose(), []);

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
      autosave.current?.dispose();
      autosave.current = undefined;
      setProject(opened);
      setEntries([]);
      setSelectedPath(undefined);
      setSaveState("clean");
      setNotice(`Opened server project ${opened.displayName}.`);
    } catch (error) {
      showError(error);
    }
  }

  async function loadModel() {
    if (!project) return;
    await loadSpecificModel(modelId.trim(), true);
  }

  async function loadSpecificModel(id: string, announce = false) {
    if (!project || !id) return;
    setConflict(undefined);
    try {
      const model = await client.getModel(project.id, id);
      const entry = remoteEntry(project.id, model);
      setEntries((current) => upsert(current, entry));
      selectRemote(entry);
      if (announce) {
        setNotice(`Loaded ${model.id} at revision ${model.revision}.`);
      }
    } catch (error) {
      showError(error);
    }
  }

  function selectRemote(entry: WorkspaceEntry) {
    setSelectedPath(entry.path);
    setSaveState("clean");
    setConflict(undefined);
    autosave.current?.dispose();
    if (entry.source !== "remote" || !entry.projectId || !entry.etag) return;

    autosave.current = new AutosaveCoordinator(
      entry.etag,
      (content, expectedEtag) =>
        client.writeModel(
          entry.projectId!,
          entry.path,
          content,
          expectedEtag,
          entry.mediaType
        ),
      {
        onState: setSaveState,
        onSaved(model) {
          setEntries((current) =>
            current.map((candidate) => {
              if (candidate.path !== entry.path) return candidate;
              const currentText = editableText(candidate);
              return {
                ...candidate,
                etag: model.etag,
                revision: model.revision,
                dirty: currentText !== model.content
              };
            })
          );
        },
        onConflict(error) {
          setConflict(
            `Server revision changed. Autosave stopped without overwriting it. Request ${error.requestId ?? "unknown"}.`
          );
        },
        onError(error) {
          setNotice(error.message);
        }
      }
    );
  }

  function selectEntry(entry: WorkspaceEntry) {
    autosave.current?.dispose();
    autosave.current = undefined;
    if (entry.source === "remote") {
      selectRemote(entry);
      return;
    }
    setSelectedPath(entry.path);
    setSaveState("clean");
    setConflict(undefined);
  }

  function editSelected(value: string) {
    if (!selected) return;
    setEntries((current) =>
      current.map((entry) =>
        entry.path === selected.path ? withText(entry, value) : entry
      )
    );

    if (selected.source === "remote") {
      autosave.current?.update(value);
    } else {
      try {
        sessionStorage.setItem(`kide:draft:${selected.path}`, value);
        setSaveState("saved");
      } catch {
        setSaveState("error");
        setNotice("Browser draft storage is unavailable; export the project to retain this edit.");
      }
    }
  }

  async function reloadConflict() {
    if (!project || !selected || selected.source !== "remote") return;
    const localDraft = editableText(selected);
    if (localDraft !== null) {
      try {
        sessionStorage.setItem(
          `kide:conflict:${project.id}:${selected.path}`,
          localDraft
        );
      } catch {
        // Reload stays safe even when browser draft storage is unavailable.
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
      autosave.current?.dispose();
      autosave.current = undefined;
      setProject(undefined);
      setProjects([]);
      setEntries(imported);
      const firstEditable = imported.find(
        (entry) => editableText(entry) !== null
      );
      setSelectedPath(firstEditable?.path);
      setSaveState("clean");
      setNotice(
        `Imported ${imported.length} canonical project files locally. No server project was created.`
      );
    } catch (error) {
      showError(error);
    }
  }

  function exportArchive() {
    if (!entries.length) return;
    const bytes = exportProjectArchive(entries);
    const blob = new Blob([bytes as BlobPart], { type: "application/zip" });
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement("a");
    anchor.href = url;
    anchor.download = `${safeName(project?.displayName ?? "kide-project")}.zip`;
    anchor.click();
    URL.revokeObjectURL(url);
    setNotice("Exported current project files as a ZIP archive.");
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
        <div className="service-state" aria-live="polite">{serviceStatus}</div>
      </header>

      <section className="connection-panel" aria-label="Service connection">
        <label>
          Service origin
          <input
            aria-label="Service origin"
            value={serviceOrigin}
            onChange={(event) => setServiceOrigin(event.target.value)}
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
        <button onClick={() => void connect()}>Connect</button>
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
                Model indexing arrives in a later server phase. Open a known model ID without duplicating repository semantics in the browser.
              </p>
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

          <h2>Files</h2>
          <ul className="file-list">
            {entries.map((entry) => (
              <li key={entry.path}>
                <button
                  className={entry.path === selectedPath ? "selected" : ""}
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
              onChange={editSelected}
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
