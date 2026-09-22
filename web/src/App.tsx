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
import { CollaborationCoordinator } from "./collaboration";
import { pathFromWorkspaceUri } from "./languageAssets";
import { KideLspClient, type SymbolInformation } from "./lspClient";
import { MonacoEditor } from "./MonacoEditor";
import { MonacoLspController } from "./monacoLsp";
import { MonacoWorkspace } from "./monacoWorkspace";
import { GraphicalEditor } from "./GraphicalEditor";
import { diagramTypeFor } from "./glspClient";
import { ensureTextMateLanguageSupport } from "./textmate";
import type {
  GenerationResult,
  KnowledgeCatalogueItem,
  KnowledgeImpactResult,
  KnowledgeTraceList,
  Model,
  PresenceSession,
  Project,
  ReviewBundle,
  ReviewChangeSet,
  ReconfigurationCause,
  ReconfigurationResult,
  SaveState,
  SynthesisResult,
  WorkspaceEntry
} from "./types";

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
  const [viewMode, setViewMode] = useState<"text" | "diagram">("text");
  const [collaborationStatus, setCollaborationStatus] = useState("Not connected");
  const [presence, setPresence] = useState<PresenceSession[]>([]);
  const [reviews, setReviews] = useState<ReviewChangeSet[]>([]);
  const [activeReview, setActiveReview] = useState<ReviewBundle>();
  const [reviewProposal, setReviewProposal] = useState("");
  const [reviewComment, setReviewComment] = useState("");
  const [knowledgeQuery, setKnowledgeQuery] = useState("");
  const [knowledgeType, setKnowledgeType] = useState("");
  const [knowledgeItems, setKnowledgeItems] = useState<KnowledgeCatalogueItem[]>([]);
  const [selectedKnowledge, setSelectedKnowledge] = useState<KnowledgeCatalogueItem>();
  const [knowledgeTraces, setKnowledgeTraces] = useState<KnowledgeTraceList>();
  const [knowledgeImpact, setKnowledgeImpact] = useState<KnowledgeImpactResult>();
  const [traceSemanticId, setTraceSemanticId] = useState("");
  const [synthesisResult, setSynthesisResult] = useState<SynthesisResult>();
  const [reconfigurationCause, setReconfigurationCause] =
    useState<ReconfigurationCause>("AVAILABILITY_CHANGE");
  const [reconfigurationResult, setReconfigurationResult] =
    useState<ReconfigurationResult>();
  const [generationKrlModelId, setGenerationKrlModelId] = useState("bindings.krl");
  const [generationResult, setGenerationResult] = useState<GenerationResult>();

  const autosaves = useRef(new Map<string, AutosaveCoordinator>());
  const collaboration = useRef<CollaborationCoordinator | undefined>(undefined);
  const lspClient = useRef<KideLspClient | undefined>(undefined);
  const lspController = useRef<MonacoLspController | undefined>(undefined);
  const workspaceChange = useRef<(path: string, value: string) => void>(() => {});
  const workspace = useMemo(
    () => new MonacoWorkspace((path, value) => workspaceChange.current(path, value)),
    []
  );

  const selected = entries.find((entry) => entry.path === selectedPathRef.current);
  const editorText = selected ? editableText(selected) : null;
  const selectedDiagramType = selected ? diagramTypeFor(selected.path) : undefined;
  const diagramAvailable = Boolean(
    selected?.source === "remote" &&
    selectedDiagramType &&
    project?.workspaceId
  );

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
      void disposeCollaboration();
      disposeAutosaves();
      workspace.dispose();
    };
  }, [workspace]);

  useEffect(() => {
    setViewMode("text");
    setSynthesisResult(undefined);
    setReconfigurationResult(undefined);
    setGenerationResult(undefined);
    collaboration.current?.setModel(selectedPath);
  }, [selectedPath]);

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
      await connectCollaboration(opened);
      await refreshKnowledgeTraces(opened.id);
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

  async function connectCollaboration(opened = projectRef.current) {
    if (!opened) return;
    await disposeCollaboration();
    setCollaborationStatus("Connecting…");

    const coordinator = new CollaborationCoordinator(
      clientRef.current,
      opened.id,
      {
        onPresence(items) {
          setPresence(items);
        },
        onSession(session) {
          setCollaborationStatus(`Connected · ${session.displayName}`);
          try {
            sessionStorage.setItem(
              `kide:collaboration-session:${opened.id}`,
              session.id
            );
          } catch {
            // Presence remains functional without browser session persistence.
          }
        },
        onError(error) {
          setCollaborationStatus("Connection degraded");
          setNotice(error.message);
        }
      }
    );
    collaboration.current = coordinator;

    let existingSessionId: string | undefined;
    try {
      existingSessionId =
        sessionStorage.getItem(`kide:collaboration-session:${opened.id}`) ?? undefined;
    } catch {
      existingSessionId = undefined;
    }
    try {
      await coordinator.connect(existingSessionId, selectedPathRef.current);
      await refreshReviews(opened.id);
    } catch (error) {
      collaboration.current = undefined;
      setCollaborationStatus("Connection failed");
      showError(error);
    }
  }

  async function disposeCollaboration() {
    const current = collaboration.current;
    collaboration.current = undefined;
    if (current) await current.disconnect();
    setPresence([]);
    setCollaborationStatus("Not connected");
  }

  async function refreshReviews(projectId = projectRef.current?.id) {
    if (!projectId) return;
    try {
      const list = await clientRef.current.listReviewChangeSets(projectId);
      setReviews(list.items);
    } catch (error) {
      showError(error);
    }
  }

  async function openReview(changeSetId: string) {
    const currentProject = projectRef.current;
    if (!currentProject) return;
    try {
      const bundle = await clientRef.current.getReviewChangeSet(
        currentProject.id,
        changeSetId
      );
      setActiveReview(bundle);
      setReviewProposal(bundle.changeSet.proposedContent);
    } catch (error) {
      showError(error);
    }
  }

  async function runSynthesis() {
    const currentProject = projectRef.current;
    const current = entriesRef.current.find(
      (entry) => entry.path === selectedPathRef.current
    );
    if (
      !currentProject ||
      !current ||
      current.source !== "remote" ||
      !current.path.endsWith(".activity") ||
      !current.etag
    ) {
      return;
    }
    if (current.dirty || saveState === "pending" || saveState === "saving") {
      setNotice("Save the Activity model before running deterministic synthesis.");
      return;
    }
    try {
      const result = await clientRef.current.synthesize(
        currentProject.id,
        current.path,
        current.etag
      );
      setSynthesisResult(result);
      setReconfigurationResult(undefined);
      setGenerationResult(undefined);
      if (result.status === "SUCCESS") {
        setNotice(
          `Synthesis selected ${result.selections.length} resource binding(s) without modifying the source model.`
        );
      } else {
        setNotice(
          `Synthesis completed with ${result.status.toLowerCase().replaceAll("_", " ")}.`
        );
      }
    } catch (error) {
      if (error instanceof ApiClientError && error.code === "CONFLICT") {
        setConflict("The Activity model changed on the server before synthesis completed.");
        setNotice("Reload the current server revision before synthesizing again.");
        return;
      }
      showError(error);
    }
  }

  async function runReconfiguration() {
    const currentProject = projectRef.current;
    const current = entriesRef.current.find(
      (entry) => entry.path === selectedPathRef.current
    );
    if (
      !currentProject ||
      !current ||
      current.source !== "remote" ||
      !current.path.endsWith(".activity") ||
      !current.etag ||
      !synthesisResult ||
      synthesisResult.status !== "SUCCESS"
    ) {
      return;
    }
    if (current.dirty || saveState === "pending" || saveState === "saving") {
      setNotice("Save the Activity model before running reconfiguration.");
      return;
    }
    try {
      const result = await clientRef.current.reconfigure(
        currentProject.id,
        current.path,
        current.etag,
        reconfigurationCause,
        synthesisResult.selections.map((selection) => ({
          requirementId: selection.requirementId,
          resourceId: selection.resourceId
        }))
      );
      setReconfigurationResult(result);
      setNotice(
        result.status === "NO_SOLUTION"
          ? "Reconfiguration found no safe solution; follow the returned fallback instructions."
          : `Reconfiguration completed with ${result.migrations.length} state migration instruction(s).`
      );
    } catch (error) {
      if (error instanceof ApiClientError && error.code === "CONFLICT") {
        setConflict("The Activity model changed on the server before reconfiguration completed.");
        setNotice("Reload the current server revision before replanning.");
        return;
      }
      showError(error);
    }
  }

  async function runGeneration() {
    const currentProject = projectRef.current;
    const current = entriesRef.current.find(
      (entry) => entry.path === selectedPathRef.current
    );
    const krlModelId = generationKrlModelId.trim();
    if (
      !currentProject ||
      !current ||
      current.source !== "remote" ||
      !current.path.endsWith(".activity") ||
      !current.etag ||
      !synthesisResult ||
      synthesisResult.status !== "SUCCESS" ||
      !krlModelId.endsWith(".krl")
    ) {
      return;
    }
    if (current.dirty || saveState === "pending" || saveState === "saving") {
      setNotice("Save the Activity model before generating target artifacts.");
      return;
    }
    try {
      const krl = await clientRef.current.getModel(currentProject.id, krlModelId);
      const result = await clientRef.current.generate(
        currentProject.id,
        current.path,
        current.etag,
        krl.id,
        krl.etag,
        synthesisResult.fingerprint
      );
      setGenerationResult(result);
      setNotice(
        `Generated ${result.artifacts.length} deterministic artifact(s) with toolchain v${result.toolchainVersion}.`
      );
    } catch (error) {
      if (error instanceof ApiClientError && error.code === "CONFLICT") {
        setConflict("The Activity, KRL, knowledge, or synthesis evidence changed before generation completed.");
        setNotice("Reload current model revisions and synthesize again before generating.");
        return;
      }
      showError(error);
    }
  }

  async function refreshKnowledgeTraces(projectId = projectRef.current?.id) {
    if (!projectId) return;
    try {
      setKnowledgeTraces(await clientRef.current.listKnowledgeTraces(projectId));
    } catch (error) {
      showError(error);
    }
  }

  async function searchKnowledge() {
    const currentProject = projectRef.current;
    if (!currentProject) return;
    try {
      const result = await clientRef.current.queryKnowledge(
        currentProject.id,
        knowledgeQuery,
        knowledgeType,
        100
      );
      setKnowledgeItems(result.items);
      if (
        selectedKnowledge &&
        !result.items.some((item) => item.iri === selectedKnowledge.iri)
      ) {
        setSelectedKnowledge(undefined);
        setKnowledgeImpact(undefined);
      }
      await refreshKnowledgeTraces(currentProject.id);
    } catch (error) {
      showError(error);
    }
  }

  async function chooseKnowledge(item: KnowledgeCatalogueItem) {
    const currentProject = projectRef.current;
    setSelectedKnowledge(item);
    if (!currentProject) return;
    try {
      setKnowledgeImpact(
        await clientRef.current.queryKnowledgeImpact(currentProject.id, item.iri)
      );
    } catch (error) {
      showError(error);
    }
  }

  async function createKnowledgeTrace() {
    const currentProject = projectRef.current;
    if (
      !currentProject ||
      !selectedKnowledge ||
      !selected ||
      selected.source !== "remote"
    ) {
      return;
    }
    try {
      const currentTraces =
        knowledgeTraces ??
        (await clientRef.current.listKnowledgeTraces(currentProject.id));
      const updated = await clientRef.current.createKnowledgeTrace(
        currentProject.id,
        selectedKnowledge.iri,
        selected.path,
        traceSemanticId.trim(),
        "REALIZES",
        currentTraces.etag
      );
      setKnowledgeTraces(updated);
      setKnowledgeImpact(
        await clientRef.current.queryKnowledgeImpact(
          currentProject.id,
          selectedKnowledge.iri
        )
      );
      setNotice(`Traced ${selectedKnowledge.label} to ${selected.path}.`);
    } catch (error) {
      if (error instanceof ApiClientError && error.code === "CONFLICT") {
        await refreshKnowledgeTraces(currentProject.id);
        setNotice("Knowledge traces changed on the server. Refreshed without overwriting them.");
        return;
      }
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
        onConflict(error, localContent, expectedEtag) {
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

  async function createConflictReview(
    entry: WorkspaceEntry,
    localContent: string,
    baseEtag: string
  ) {
    if (!entry.projectId) return;
    try {
      const set = await clientRef.current.createReviewChangeSet(
        entry.projectId,
        entry.path,
        baseEtag,
        localContent,
        entry.mediaType
      );
      await refreshReviews(entry.projectId);
      await openReview(set.id);
      if (entry.path === selectedPathRef.current) {
        setConflict(
          `Server revision changed. Local edits were captured as review change set ${set.id.slice(0, 8)}; no server content was overwritten.`
        );
      }
    } catch (error) {
      showError(error);
    }
  }

  async function createReviewFromEditor() {
    const currentProject = projectRef.current;
    if (!currentProject || !selected || selected.source !== "remote" || !selected.etag) return;
    const proposed = workspace.text(selected.path) ?? editableText(selected);
    if (proposed === null || proposed === undefined) return;
    try {
      const set = await clientRef.current.createReviewChangeSet(
        currentProject.id,
        selected.path,
        selected.etag,
        proposed,
        selected.mediaType
      );
      await refreshReviews(currentProject.id);
      await openReview(set.id);
      setNotice(`Created review change set ${set.id.slice(0, 8)}.`);
    } catch (error) {
      showError(error);
    }
  }

  async function rebaseActiveReview() {
    const currentProject = projectRef.current;
    if (!currentProject || !activeReview) return;
    try {
      const updated = await clientRef.current.rebaseReviewChangeSet(
        currentProject.id,
        activeReview.changeSet.id,
        activeReview.currentModel.etag,
        reviewProposal
      );
      await refreshReviews(currentProject.id);
      await openReview(updated.id);
      setNotice("Review proposal rebased explicitly on the current server revision.");
    } catch (error) {
      showError(error);
    }
  }

  async function markActiveReviewReady() {
    const currentProject = projectRef.current;
    if (!currentProject || !activeReview) return;
    try {
      const updated = await clientRef.current.markReviewReady(
        currentProject.id,
        activeReview.changeSet.id
      );
      await refreshReviews(currentProject.id);
      await openReview(updated.id);
      setNotice("Change set is ready for independent review.");
    } catch (error) {
      showError(error);
    }
  }

  async function approveActiveReview() {
    const currentProject = projectRef.current;
    if (!currentProject || !activeReview) return;
    try {
      const updated = await clientRef.current.approveReview(
        currentProject.id,
        activeReview.changeSet.id
      );
      await refreshReviews(currentProject.id);
      await openReview(updated.id);
      setNotice("Change set approved.");
    } catch (error) {
      showError(error);
    }
  }

  async function applyActiveReview() {
    const currentProject = projectRef.current;
    if (!currentProject || !activeReview) return;
    try {
      const applied = await clientRef.current.applyReview(
        currentProject.id,
        activeReview.changeSet.id
      );
      const entry = remoteEntry(currentProject.id, applied.model);
      replaceRemoteEntry(entry);
      workspace.sync(entry.path, applied.model.content);
      await refreshReviews(currentProject.id);
      await openReview(applied.changeSet.id);
      setSaveState("clean");
      setConflict(undefined);
      setNotice("Approved change set applied to the canonical model.");
    } catch (error) {
      showError(error);
    }
  }

  async function addActiveReviewComment() {
    const currentProject = projectRef.current;
    if (!currentProject || !activeReview || !reviewComment.trim()) return;
    try {
      await clientRef.current.addReviewComment(
        currentProject.id,
        activeReview.changeSet.id,
        reviewComment.trim(),
        activeReview.changeSet.modelId
      );
      setReviewComment("");
      await openReview(activeReview.changeSet.id);
    } catch (error) {
      showError(error);
    }
  }

  async function setCommentResolved(commentId: string, resolved: boolean) {
    const currentProject = projectRef.current;
    if (!currentProject || !activeReview) return;
    try {
      await clientRef.current.updateReviewComment(
        currentProject.id,
        activeReview.changeSet.id,
        commentId,
        resolved
      );
      await openReview(activeReview.changeSet.id);
    } catch (error) {
      showError(error);
    }
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

  function openDiagram() {
    if (!diagramAvailable || !selected) return;
    if (!tokenRef.current.trim()) {
      setNotice("An access token is required for the graphical service.");
      return;
    }
    if (
      saveState === "pending" ||
      saveState === "saving" ||
      saveState === "conflict" ||
      saveState === "error"
    ) {
      setNotice("Resolve or finish textual saves before opening the shared graphical model.");
      return;
    }
    setViewMode("diagram");
  }

  async function refreshAfterGraphicalSave(path: string) {
    await loadSpecificModel(path);
    setNotice("Graphical save completed. The Monaco model was refreshed from the same canonical project file.");
  }

  async function resetProjectWorkspace() {
    await disposeLanguageServices();
    await disposeCollaboration();
    disposeAutosaves();
    workspace.dispose();
    updateEntries(() => []);
    setSelectedPath(undefined);
    setRevealRange(undefined);
    setSymbols([]);
    setReviews([]);
    setActiveReview(undefined);
    setReviewProposal("");
    setReviewComment("");
    setKnowledgeItems([]);
    setSelectedKnowledge(undefined);
    setKnowledgeTraces(undefined);
    setKnowledgeImpact(undefined);
    setTraceSemanticId("");
    setSynthesisResult(undefined);
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
          <div className="service-state collaboration-state" aria-live="polite">
            {collaborationStatus}
          </div>
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

          {project && (
            <div className="knowledge-panel">
              <div className="panel-heading">
                <h2>Knowledge catalogue</h2>
                <button onClick={() => void searchKnowledge()}>Search</button>
              </div>
              <label>
                Knowledge query
                <input
                  aria-label="Knowledge query"
                  value={knowledgeQuery}
                  onChange={(event) => setKnowledgeQuery(event.target.value)}
                  placeholder="Capability, device, role, property…"
                />
              </label>
              <label>
                Concept type
                <select
                  aria-label="Knowledge concept type"
                  value={knowledgeType}
                  onChange={(event) => setKnowledgeType(event.target.value)}
                >
                  <option value="">All concepts</option>
                  <option value="CAPABILITY">Capability</option>
                  <option value="DEVICE">Device</option>
                  <option value="WORKFLOW">Workflow</option>
                  <option value="INTERFACE">Interface</option>
                  <option value="BEHAVIOR">Behavior</option>
                  <option value="INTERACTION">Interaction</option>
                </select>
              </label>
              {knowledgeItems.length ? (
                <ul className="knowledge-list">
                  {knowledgeItems.map((item) => (
                    <li key={item.iri}>
                      <button
                        className={selectedKnowledge?.iri === item.iri ? "selected" : ""}
                        onClick={() => void chooseKnowledge(item)}
                      >
                        <strong>{item.label}</strong>
                        <span>{item.types.map(compactKnowledgeIri).join(", ")}</span>
                      </button>
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="muted">Search the server-owned knowledge catalogue.</p>
              )}

              {selectedKnowledge && (
                <div className="knowledge-detail">
                  <strong>{selectedKnowledge.label}</strong>
                  <span className="muted">{selectedKnowledge.authority}</span>
                  <span className="knowledge-iri">{selectedKnowledge.iri}</span>
                  {Object.entries(selectedKnowledge.properties).map(([key, values]) => (
                    <div className="knowledge-property" key={key}>
                      <span>{compactKnowledgeIri(key)}</span>
                      <strong>{values.join(", ")}</strong>
                    </div>
                  ))}
                  <label>
                    Semantic ID (optional)
                    <input
                      aria-label="Knowledge trace semantic ID"
                      value={traceSemanticId}
                      onChange={(event) => setTraceSemanticId(event.target.value)}
                      placeholder="e.g. //@activities.0"
                    />
                  </label>
                  <button
                    disabled={!selected || selected.source !== "remote"}
                    onClick={() => void createKnowledgeTrace()}
                  >
                    Trace to current model
                  </button>
                  {knowledgeImpact && (
                    <p className="muted">
                      {knowledgeImpact.items.length} linked model location(s)
                      {knowledgeImpact.issues.length
                        ? ` · ${knowledgeImpact.issues.length} stale/broken`
                        : " · all links healthy"}
                    </p>
                  )}
                </div>
              )}
            </div>
          )}

          {project && selected?.source === "remote" && selected.path.endsWith(".activity") && (
            <div className="synthesis-panel">
              <div className="panel-heading">
                <h2>Deterministic synthesis</h2>
                <button
                  disabled={!selected.etag || selected.dirty}
                  onClick={() => void runSynthesis()}
                >
                  Synthesize
                </button>
              </div>
              <p className="muted">
                Server-owned capability matching, design contracts and Activity→MNC composition.
              </p>
              <div className="reconfiguration-controls">
                <label>
                  Reconfiguration cause
                  <select
                    aria-label="Reconfiguration cause"
                    value={reconfigurationCause}
                    onChange={(event) =>
                      setReconfigurationCause(event.target.value as ReconfigurationCause)
                    }
                  >
                    <option value="AVAILABILITY_CHANGE">Availability change</option>
                    <option value="RESOURCE_LOSS">Resource loss</option>
                    <option value="RESOURCE_REPLACEMENT">Resource replacement</option>
                    <option value="CAPABILITY_CHANGE">Capability change</option>
                    <option value="REQUIREMENT_CHANGE">Requirement change</option>
                    <option value="MANUAL_REPLAN">Manual re-plan</option>
                  </select>
                </label>
                <button
                  disabled={
                    !selected.etag ||
                    selected.dirty ||
                    !synthesisResult ||
                    synthesisResult.status !== "SUCCESS"
                  }
                  onClick={() => void runReconfiguration()}
                >
                  Reconfigure
                </button>
              </div>
              {synthesisResult && (
                <div className="synthesis-result" aria-label="Synthesis result">
                  <strong>{synthesisResult.status}</strong>
                  <span className="muted">
                    fingerprint {synthesisResult.fingerprint.slice(0, 12)} · knowledge revision {synthesisResult.knowledgeRevision}
                  </span>
                  {synthesisResult.selections.map((selection) => (
                    <div className="synthesis-selection" key={selection.requirementId}>
                      <span>{selection.activityName}</span>
                      <strong>{selection.resourceId}</strong>
                    </div>
                  ))}
                  {synthesisResult.diagnostics.map((diagnostic, index) => (
                    <div
                      className={`synthesis-diagnostic diagnostic-${diagnostic.severity.toLowerCase()}`}
                      key={diagnostic.code + index}
                    >
                      <strong>{diagnostic.code}</strong>
                      <span>{diagnostic.message}</span>
                    </div>
                  ))}
                  {synthesisResult.generatedMnc && (
                    <details>
                      <summary>Generated MNC</summary>
                      <pre className="generated-mnc">{synthesisResult.generatedMnc}</pre>
                    </details>
                  )}
                </div>
              )}
              <div className="reconfiguration-controls">
                <label>
                  KRL model ID
                  <input
                    aria-label="KRL model ID"
                    value={generationKrlModelId}
                    onChange={(event) => setGenerationKrlModelId(event.target.value)}
                    placeholder="bindings.krl"
                  />
                </label>
                <button
                  disabled={
                    !selected.etag ||
                    selected.dirty ||
                    !synthesisResult ||
                    synthesisResult.status !== "SUCCESS" ||
                    !generationKrlModelId.trim().endsWith(".krl")
                  }
                  onClick={() => void runGeneration()}
                >
                  Generate
                </button>
              </div>
              {generationResult && (
                <div className="synthesis-result" aria-label="Generation result">
                  <strong>Generated {generationResult.artifacts.length} artifact(s)</strong>
                  <span className="muted">
                    toolchain v{generationResult.toolchainVersion} · fingerprint {generationResult.fingerprint.slice(0, 12)}
                  </span>
                  {generationResult.artifacts.map((artifact) => (
                    <div className="synthesis-selection" key={artifact.path}>
                      <span>{artifact.path}</span>
                      <strong>{artifact.targetId}@{artifact.targetVersion}</strong>
                    </div>
                  ))}
                  <details>
                    <summary>Generation manifest</summary>
                    <pre className="generated-mnc">{generationResult.manifestJson}</pre>
                  </details>
                </div>
              )}
              {reconfigurationResult && (
                <div className="synthesis-result" aria-label="Reconfiguration result">
                  <strong>{reconfigurationResult.status}</strong>
                  <span className="muted">
                    {reconfigurationResult.cause.toLowerCase().replaceAll("_", " ")} · fingerprint {reconfigurationResult.fingerprint.slice(0, 12)}
                  </span>
                  {reconfigurationResult.selections.map((selection) => (
                    <div className="synthesis-selection" key={selection.requirementId}>
                      <span>{selection.activityName}</span>
                      <strong>{selection.resourceId}</strong>
                    </div>
                  ))}
                  {reconfigurationResult.migrations.map((migration) => (
                    <div className="synthesis-diagnostic" key={migration.requirementId}>
                      <strong>{migration.policy}</strong>
                      <span>{migration.reason}</span>
                    </div>
                  ))}
                  {reconfigurationResult.diagnostics.map((diagnostic, index) => (
                    <div
                      className={`synthesis-diagnostic diagnostic-${diagnostic.severity.toLowerCase()}`}
                      key={diagnostic.code + index}
                    >
                      <strong>{diagnostic.code}</strong>
                      <span>{diagnostic.message}</span>
                    </div>
                  ))}
                </div>
              )}
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

          {project && (
            <div className="collaboration-panel">
              <div className="panel-heading">
                <h2>Collaboration</h2>
                <button onClick={() => {
                  void collaboration.current?.refresh();
                  void refreshReviews();
                }}>
                  Refresh
                </button>
              </div>
              <p className="muted">{collaborationStatus}</p>
              <h3>Presence</h3>
              {presence.length ? (
                <ul className="presence-list">
                  {presence.map((item) => (
                    <li key={item.id}>
                      <strong>{item.displayName}</strong>
                      <span>{item.modelId || "Project"}</span>
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="muted">No active collaborators reported.</p>
              )}

              <div className="panel-heading">
                <h3>Reviews</h3>
                <button
                  disabled={!selected || selected.source !== "remote" || !selected.etag}
                  onClick={() => void createReviewFromEditor()}
                >
                  Create review
                </button>
              </div>
              {reviews.length ? (
                <ul className="review-list">
                  {reviews.map((review) => (
                    <li key={review.id}>
                      <button onClick={() => void openReview(review.id)}>
                        <strong>{review.modelId}</strong>
                        <span>{review.status} · {review.authorName}</span>
                      </button>
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="muted">No review change sets yet.</p>
              )}
            </div>
          )}
        </aside>

        <section className="editor-panel">
          <div className="editor-toolbar">
            <div className="editor-identity">
              <strong>{selected?.path ?? "No file selected"}</strong>
              {selected?.revision && <span>revision {selected.revision}</span>}
            </div>
            <div className="editor-actions">
              {diagramAvailable && (
                <div className="view-toggle" aria-label="Editor view">
                  <button
                    className={viewMode === "text" ? "active-view" : ""}
                    aria-pressed={viewMode === "text"}
                    onClick={() => setViewMode("text")}
                  >
                    Text
                  </button>
                  <button
                    className={viewMode === "diagram" ? "active-view" : ""}
                    aria-pressed={viewMode === "diagram"}
                    onClick={openDiagram}
                  >
                    Diagram
                  </button>
                </div>
              )}
              <span className={`save-state state-${saveState}`}>{saveState}</span>
            </div>
          </div>
          {viewMode === "diagram" &&
          selected &&
          project?.workspaceId &&
          selectedDiagramType ? (
            <GraphicalEditor
              gatewayOrigin={gatewayOrigin}
              accessToken={tokenRef.current}
              workspaceId={project.workspaceId}
              path={selected.path}
              onStatus={setNotice}
              onSaved={() => void refreshAfterGraphicalSave(selected.path)}
            />
          ) : !selected ? (
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

          {activeReview && (
            <section className="review-workbench" aria-label="Change review">
              <div className="review-header">
                <div>
                  <p className="eyebrow">REVIEW CHANGE SET</p>
                  <h2>{activeReview.changeSet.modelId}</h2>
                  <p className="muted">
                    {activeReview.changeSet.status} · revision {activeReview.changeSet.reviewRevision}
                    {activeReview.conflicted ? " · server revision changed" : ""}
                  </p>
                </div>
                <button onClick={() => setActiveReview(undefined)}>Close review</button>
              </div>

              <div className="review-compare">
                <label>
                  Current server revision
                  <textarea
                    aria-label="Current server revision"
                    readOnly
                    value={activeReview.currentModel.content}
                  />
                </label>
                <label>
                  Proposed / resolved revision
                  <textarea
                    aria-label="Resolved review proposal"
                    value={reviewProposal}
                    readOnly={
                      activeReview.changeSet.status === "READY" ||
                      activeReview.changeSet.status === "APPROVED" ||
                      activeReview.changeSet.status === "APPLIED"
                    }
                    onChange={(event) => setReviewProposal(event.target.value)}
                  />
                </label>
              </div>

              <div className="review-actions">
                {(activeReview.conflicted || activeReview.changeSet.status === "CONFLICT") && (
                  <button onClick={() => void rebaseActiveReview()}>
                    Rebase resolved proposal
                  </button>
                )}
                {activeReview.changeSet.status === "DRAFT" && (
                  <button onClick={() => void markActiveReviewReady()}>
                    Mark ready for review
                  </button>
                )}
                {activeReview.changeSet.status === "READY" && (
                  <button onClick={() => void approveActiveReview()}>
                    Approve as reviewer
                  </button>
                )}
                {activeReview.changeSet.status === "APPROVED" && (
                  <button onClick={() => void applyActiveReview()}>
                    Apply approved change
                  </button>
                )}
              </div>

              <div className="review-comments">
                <h3>Review comments</h3>
                {activeReview.changeSet.status !== "APPLIED" && (
                  <div className="comment-composer">
                    <input
                      aria-label="Review comment"
                      value={reviewComment}
                      onChange={(event) => setReviewComment(event.target.value)}
                      placeholder="Add an anchored review comment"
                    />
                    <button
                      disabled={!reviewComment.trim()}
                      onClick={() => void addActiveReviewComment()}
                    >
                      Comment
                    </button>
                  </div>
                )}
                {activeReview.comments.length ? (
                  <ul>
                    {activeReview.comments.map((comment) => (
                      <li key={comment.id} className={comment.resolved ? "resolved-comment" : ""}>
                        <div>
                          <strong>{comment.authorName}</strong>
                          <span>{comment.anchor ?? ""}</span>
                        </div>
                        <p>{comment.body}</p>
                        {activeReview.changeSet.status !== "APPLIED" && (
                          <button
                            onClick={() => void setCommentResolved(comment.id, !comment.resolved)}
                          >
                            {comment.resolved ? "Reopen" : "Resolve"}
                          </button>
                        )}
                      </li>
                    ))}
                  </ul>
                ) : (
                  <p className="muted">No review comments.</p>
                )}
              </div>
            </section>
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

function compactKnowledgeIri(value: string): string {
  const split = Math.max(
    value.lastIndexOf("#"),
    value.lastIndexOf("/"),
    value.lastIndexOf(":")
  );
  return split >= 0 && split + 1 < value.length
    ? value.slice(split + 1)
    : value;
}

function safeName(value: string): string {
  const normalized = value
    .trim()
    .replace(/[^A-Za-z0-9._-]+/g, "-")
    .replace(/^-+|-+$/g, "");
  return normalized || "kide-project";
}
