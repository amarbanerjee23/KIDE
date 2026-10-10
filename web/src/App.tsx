import { useEffect, useMemo, useRef, useState } from "react";
import type { ChangeEvent } from "react";
import * as monaco from "monaco-editor";
import { KideApiClient, ApiClientError } from "./api";
import { FirebaseAuthClient, FirebaseAuthError } from "./firebaseAuth";
import {
  bytesFromBase64,
  bytesToBase64,
  editableText,
  exportProjectArchive,
  importProjectArchive,
  mediaTypeFor,
  withText
} from "./archive";
import { AutosaveCoordinator } from "./autosave";
import { CollaborationCoordinator } from "./collaboration";
import { pathFromWorkspaceUri } from "./languageAssets";
import { KideLspClient, type DocumentSymbol, type SymbolInformation } from "./lspClient";
import { MonacoEditor } from "./MonacoEditor";
import { MonacoLspController } from "./monacoLsp";
import { MonacoWorkspace } from "./monacoWorkspace";
import { GraphicalEditor } from "./GraphicalEditor";
import { EngineeringCockpit } from "./EngineeringCockpit";
import { QuickPick, type QuickPickItem } from "./QuickPick";
import { LandingPage } from "./LandingPage";
import { RegisterPage } from "./RegisterPage";
import { FileTree } from "./FileTree";
import { diagramTypeFor } from "./glspClient";
import { ensureTextMateLanguageSupport } from "./textmate";
import { runtimeCompatibilityIssues } from "./runtimeCompatibility";
import type {
  GenerationResult,
  Health,
  KnowledgeCatalogueItem,
  KnowledgeImpactResult,
  KnowledgeTraceList,
  Model,
  ModelSummary,
  PresenceSession,
  Project,
  ReviewBundle,
  ReviewChangeSet,
  ReconfigurationCause,
  ReconfigurationResult,
  RuntimeVersion,
  SaveState,
  SynthesisResult,
  WorkspaceEntry
} from "./types";

const SERVICE_ORIGIN =
  import.meta.env.VITE_KIDE_API_ORIGIN ?? window.location.origin;
const GATEWAY_ORIGIN =
  import.meta.env.VITE_KIDE_LSP_ORIGIN ?? SERVICE_ORIGIN;
const FIREBASE_API_KEY = import.meta.env.VITE_FIREBASE_API_KEY ?? "";
const FIREBASE_PROJECT_ID = import.meta.env.VITE_FIREBASE_PROJECT_ID ?? "";
const WEB_BUILD_ID = import.meta.env.VITE_KIDE_WEB_BUILD_ID ?? "dev";
const MAX_HOSTED_IMPORT_FILES = 256;
const MAX_HOSTED_IMPORT_BYTES = 10 * 1024 * 1024;

type SidebarView =
  | "explorer"
  | "search"
  | "engineering"
  | "collaboration"
  | "settings";

type QuickPickMode = "commands" | "files" | "operationScripts" | null;
type BottomPanel = "problems" | "output" | null;

interface WorkspaceSearchMatch {
  path: string;
  line: number;
  column: number;
  preview: string;
}

interface ProblemItem {
  path: string;
  message: string;
  severity: monaco.MarkerSeverity;
  line: number;
  column: number;
}

interface OutlineItem {
  name: string;
  detail?: string;
  kind: number;
  line: number;
  column: number;
  depth: number;
}

export default function App() {
  const [serviceOrigin, setServiceOrigin] = useState(SERVICE_ORIGIN);
  const [gatewayOrigin, setGatewayOrigin] = useState(GATEWAY_ORIGIN);
  const firebaseAuth = useMemo(
    () => new FirebaseAuthClient(FIREBASE_API_KEY),
    []
  );
  const [firebaseEmail, setFirebaseEmail] = useState("");
  const [firebasePassword, setFirebasePassword] = useState("");
  const [firebaseConfirmPassword, setFirebaseConfirmPassword] = useState("");
  const [authStatus, setAuthStatus] = useState(
    FIREBASE_PROJECT_ID ? "Not signed in" : "Firebase configuration missing"
  );
  const [token, setToken] = useState("");
  const tokenRef = useRef("");
  tokenRef.current = token;

  const client = useMemo(
    () => new KideApiClient(serviceOrigin, () => firebaseAuth.idToken()),
    [firebaseAuth, serviceOrigin]
  );
  const clientRef = useRef(client);
  clientRef.current = client;

  const [serviceStatus, setServiceStatus] = useState("Not connected");
  const [runtimeVersion, setRuntimeVersion] = useState<RuntimeVersion>();
  const [lspStatus, setLspStatus] = useState("Not connected");
  const [projects, setProjects] = useState<Project[]>([]);
  const [newProjectName, setNewProjectName] = useState("");
  const [creatingProject, setCreatingProject] = useState(false);
  const [canCreateProjects, setCanCreateProjects] = useState(false);
  const [project, setProject] = useState<Project>();
  const projectRef = useRef<Project | undefined>(undefined);
  projectRef.current = project;

  const [entries, setEntries] = useState<WorkspaceEntry[]>([]);
  const entriesRef = useRef<WorkspaceEntry[]>([]);
  entriesRef.current = entries;
  const [selectedPath, setSelectedPath] = useState<string>();
  const selectedPathRef = useRef<string | undefined>(undefined);
  selectedPathRef.current = selectedPath;
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

  const [activeSidebar, setActiveSidebar] =
    useState<SidebarView>("explorer");
  const [sidebarVisible, setSidebarVisible] = useState(true);
  const [bottomPanel, setBottomPanel] = useState<BottomPanel>(null);
  const [quickPickMode, setQuickPickMode] = useState<QuickPickMode>(null);
  const [openPaths, setOpenPaths] = useState<string[]>([]);
  const [workspaceSearch, setWorkspaceSearch] = useState("");
  const [workspaceMatches, setWorkspaceMatches] =
    useState<WorkspaceSearchMatch[]>([]);
  const [problems, setProblems] = useState<ProblemItem[]>([]);
  const [outlineItems, setOutlineItems] = useState<OutlineItem[]>([]);
  const [outputLog, setOutputLog] = useState<string[]>([]);
  const [cursorPosition, setCursorPosition] = useState({ line: 1, column: 1 });
  const [theme, setTheme] = useState<"dark" | "light">(() => {
    try {
      return localStorage.getItem("kide:web-theme") === "light"
        ? "light"
        : "dark";
    } catch {
      return "dark";
    }
  });
  const [route, setRoute] = useState<"home" | "register" | "workspace">(() => {
    if (window.location.pathname.startsWith("/workspace")) return "workspace";
    if (window.location.pathname.startsWith("/register")) return "register";
    return "home";
  });
  const [authBusy, setAuthBusy] = useState(false);
  const [engineeringCockpitOpen, setEngineeringCockpitOpen] = useState(false);
  const [localArchiveName, setLocalArchiveName] = useState<string>();

  const autosaves = useRef(new Map<string, AutosaveCoordinator>());
  const pendingDiagramPath = useRef<string | undefined>(undefined);
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
  const activityModels = entries
    .filter((entry) => entry.source === "remote" && entry.path.toLowerCase().endsWith(".activity"))
    .map((entry) => entry.path);
  const mncModels = entries
    .filter((entry) => entry.source === "remote" && entry.path.toLowerCase().endsWith(".mncspec"))
    .map((entry) => entry.path);
  const canSynthesize = Boolean(
    selected?.source === "remote" &&
    selected.path.toLowerCase().endsWith(".activity") &&
    selected.etag &&
    !selected.dirty &&
    saveState !== "pending" &&
    saveState !== "saving"
  );
  const canReconfigure = Boolean(
    canSynthesize && synthesisResult?.status === "SUCCESS"
  );
  const canGenerate = Boolean(
    canReconfigure && generationKrlModelId.trim().toLowerCase().endsWith(".krl")
  );
  const localArchiveOpen =
    !project && entries.some((entry) => entry.source === "archive");

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
    const timer = globalThis.setInterval(() => {
      if (!firebaseAuth.user) return;
      void firebaseAuth.idToken()
        .then((nextToken) => {
          if (nextToken !== tokenRef.current) setToken(nextToken);
        })
        .catch((error) => {
          firebaseAuth.signOut();
          setToken("");
          setAuthStatus("Session expired");
          setNotice(error instanceof Error ? error.message : "Firebase session expired.");
        });
    }, 5 * 60 * 1000);
    return () => globalThis.clearInterval(timer);
  }, [firebaseAuth]);

  useEffect(() => {
    if (!token || !projectRef.current) return;
    void connectLanguageServices(projectRef.current);
  }, [token]);

  useEffect(() => {
    const openRequestedDiagram =
      Boolean(selectedPath) && pendingDiagramPath.current === selectedPath;
    if (openRequestedDiagram) pendingDiagramPath.current = undefined;
    setViewMode(openRequestedDiagram ? "diagram" : "text");
    setSynthesisResult(undefined);
    setReconfigurationResult(undefined);
    setGenerationResult(undefined);
    collaboration.current?.setModel(selectedPath);
    setCursorPosition({ line: 1, column: 1 });
    if (selectedPath) {
      setOpenPaths((current) =>
        current.includes(selectedPath) ? current : [...current, selectedPath]
      );
      void refreshOutline(selectedPath);
    } else {
      setOutlineItems([]);
    }
  }, [selectedPath]);

  useEffect(() => {
    if (!notice) return;
    const stamp = new Date().toLocaleTimeString([], {
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit"
    });
    setOutputLog((current) => [...current.slice(-199), `[${stamp}] ${notice}`]);
  }, [notice]);

  useEffect(() => {
    const refresh = () => {
      const next = monaco.editor
        .getModelMarkers({})
        .map((marker) => {
          let path = marker.resource.path.replace(/^\/+/, "");
          try {
            path = pathFromWorkspaceUri(marker.resource.toString());
          } catch {
            // Non-workspace Monaco models keep their URI path.
          }
          return {
            path,
            message: marker.message,
            severity: marker.severity,
            line: marker.startLineNumber,
            column: marker.startColumn
          } satisfies ProblemItem;
        })
        .sort((a, b) =>
          b.severity - a.severity ||
          a.path.localeCompare(b.path) ||
          a.line - b.line ||
          a.column - b.column
        );
      setProblems(next);
    };
    refresh();
    const subscription = monaco.editor.onDidChangeMarkers(refresh);
    return () => subscription.dispose();
  }, []);

  useEffect(() => {
    try {
      localStorage.setItem("kide:web-theme", theme);
    } catch {
      // Theme persistence is optional.
    }
  }, [theme]);

  useEffect(() => {
    const onPopState = () => {
      if (window.location.pathname.startsWith("/workspace")) {
        setRoute("workspace");
      } else if (window.location.pathname.startsWith("/register")) {
        setRoute("register");
      } else {
        setRoute("home");
      }
    };
    window.addEventListener("popstate", onPopState);
    return () => window.removeEventListener("popstate", onPopState);
  }, []);

  useEffect(() => {
    const onKeyDown = (event: KeyboardEvent) => {
      const command = event.ctrlKey || event.metaKey;
      if (event.key === "F1" || (command && event.shiftKey && event.key.toLowerCase() === "p")) {
        event.preventDefault();
        setQuickPickMode("commands");
        return;
      }
      if (command && !event.shiftKey && event.key.toLowerCase() === "p") {
        event.preventDefault();
        setQuickPickMode("files");
        return;
      }
      if (command && event.key.toLowerCase() === "b") {
        event.preventDefault();
        setSidebarVisible((visible) => !visible);
        return;
      }
      if (command && event.key.toLowerCase() === "j") {
        event.preventDefault();
        setBottomPanel((panel) => (panel ? null : "problems"));
        return;
      }
      if (command && event.shiftKey && event.key.toLowerCase() === "e") {
        event.preventDefault();
        setActiveSidebar("explorer");
        setSidebarVisible(true);
        return;
      }
      if (command && event.shiftKey && event.key.toLowerCase() === "f") {
        event.preventDefault();
        setActiveSidebar("search");
        setSidebarVisible(true);
        return;
      }
      if (command && event.shiftKey && event.key.toLowerCase() === "m") {
        event.preventDefault();
        setBottomPanel("problems");
      }
    };
    window.addEventListener("keydown", onKeyDown, true);
    return () => window.removeEventListener("keydown", onKeyDown, true);
  }, []);

  async function signInFirebase() {
    setNotice("");
    setAuthBusy(true);
    try {
      const user = await firebaseAuth.signInWithEmailPassword(
        firebaseEmail,
        firebasePassword
      );
      const nextToken = await firebaseAuth.idToken();
      setToken(nextToken);
      setFirebasePassword("");
      setAuthStatus(`Signed in · ${user.email}`);
      await connect();
      setActiveSidebar("explorer");
      setSidebarVisible(true);
    } catch (error) {
      setToken("");
      setAuthStatus("Sign-in failed");
      setNotice(
        error instanceof FirebaseAuthError || error instanceof Error
          ? error.message
          : "Firebase sign-in failed."
      );
    } finally {
      setAuthBusy(false);
    }
  }

  async function registerFirebase() {
    setNotice("");
    if (firebasePassword !== firebaseConfirmPassword) {
      setAuthStatus("Registration failed");
      setNotice("Passwords do not match.");
      return;
    }
    setAuthBusy(true);
    try {
      const user = await firebaseAuth.registerWithEmailPassword(
        firebaseEmail,
        firebasePassword
      );
      const nextToken = await firebaseAuth.idToken();
      setToken(nextToken);
      setFirebasePassword("");
      setFirebaseConfirmPassword("");
      setAuthStatus(`Account created · ${user.email}`);
      setNotice(
        "Account created and signed in. Engineering access is granted through KIDE role bindings."
      );
      navigate("home");
    } catch (error) {
      setToken("");
      setAuthStatus("Registration failed");
      setNotice(
        error instanceof FirebaseAuthError || error instanceof Error
          ? error.message
          : "Firebase registration failed."
      );
    } finally {
      setAuthBusy(false);
    }
  }

  async function signOutFirebase() {
    firebaseAuth.signOut();
    setToken("");
    setProjects([]);
    await resetProjectWorkspace();
    setServiceStatus("Not connected");
    setRuntimeVersion(undefined);
    setLspStatus("Not connected");
    setAuthStatus("Not signed in");
    setNotice("Signed out.");
  }

  async function createHostedProject() {
    const name = newProjectName.trim();
    if (!name || creatingProject || !firebaseAuth.user || !runtimeVersion) return;
    setCreatingProject(true);
    try {
      const created = await client.createProject(name);
      setProjects((current) => [...current, created].sort(
        (a, b) => a.displayName.localeCompare(b.displayName)
      ));
      setNewProjectName("");
      setNotice(`Hosted project "${created.displayName}" created. Open it to begin engineering.`);
    } catch (error) {
      showError(error);
    } finally {
      setCreatingProject(false);
    }
  }

  async function connect() {
    setNotice("");
    setConflict(undefined);

    let health: Health;
    try {
      health = await client.health();
      setCanCreateProjects(health.projectCreationEnabled === true);
      setServiceStatus(`Connected · ${health.status} · API ${health.version}`);
    } catch (error) {
      setRuntimeVersion(undefined);
      setCanCreateProjects(false);
      setServiceStatus("Not connected");
      showError(error);
      return;
    }

    let version: RuntimeVersion;
    try {
      version = await client.version();
      setRuntimeVersion(version);
    } catch (error) {
      setProjects([]);
      setRuntimeVersion(undefined);
      setServiceStatus("Incompatible · version handshake unavailable");
      setNotice(
        "The KIDE API is reachable but did not provide the required runtime compatibility handshake. Engineering services are disabled."
      );
      return;
    }

    const compatibilityIssues = runtimeCompatibilityIssues(version);
    if (compatibilityIssues.length > 0) {
      setProjects([]);
      if (projectRef.current) await resetProjectWorkspace();
      setServiceStatus(`Incompatible · backend ${version.buildId}`);
      setNotice(
        `KIDE runtime compatibility check failed: ${compatibilityIssues.join("; ")}. ` +
        "Project discovery and hosted engineering services are disabled."
      );
      return;
    }

    setServiceStatus(
      `Connected · API ${version.apiVersion} · compatible · build ${version.buildId}`
    );

    try {
      const list = await client.listProjects();
      setProjects(list.items);
      if (list.items.length === 0) {
        setNotice("API connected. No authorized server projects are available for this account.");
      }
    } catch (error) {
      setProjects([]);
      if (error instanceof ApiClientError && error.status === 403) {
        setServiceStatus(`Connected · API ${version.apiVersion} · authorization required`);
        const principal = firebaseAuth.user
          ? `firebase:${FIREBASE_PROJECT_ID}#${firebaseAuth.user.uid}`
          : "the signed-in Firebase principal";
        const suffix = error.requestId ? ` Request ${error.requestId}.` : "";
        setNotice(
          `API is reachable and compatible, but ${principal} has no KIDE project access. ` +
          `Grant an explicit ENGINEER or ADMINISTRATOR role binding on the hosted project.${suffix}`
        );
        return;
      }
      setServiceStatus(`Connected · API ${version.apiVersion} · project listing failed`);
      showError(error);
    }
  }

  async function openProject(item: Project): Promise<boolean> {
    setNotice("");
    setConflict(undefined);
    try {
      const opened = await client.getProject(item.id);
      await resetProjectWorkspace();
      setProjectState(opened);
      setLocalArchiveName(undefined);

      const modelList = await client.listModels(opened.id);
      updateEntries(() =>
        modelList.items.map((model) => remoteSummaryEntry(opened.id, model))
      );
      const krl = modelList.items.find((model) =>
        model.id.toLowerCase().endsWith(".krl")
      );
      if (krl) setGenerationKrlModelId(krl.id);

      await connectLanguageServices(opened);
      await connectCollaboration(opened);
      await refreshKnowledgeTraces(opened.id);

      const preferred = preferredModel(modelList.items);
      if (preferred) {
        const loaded = await loadSpecificModel(preferred.id);
        if (!loaded) return false;
        setNotice(
          `Opened ${opened.displayName} · ${modelList.items.length} project file(s) · ${preferred.id} ready.`
        );
      } else {
        setNotice(
          `Opened ${opened.displayName}, but no editable DSL models are present yet.`
        );
      }
      return true;
    } catch (error) {
      showError(error);
      return false;
    }
  }

  async function createStarterEngineeringModels() {
    const currentProject = projectRef.current;
    if (!currentProject) return;
    setNotice("");
    setConflict(undefined);
    try {
      const modelList = await clientRef.current.createStarterModels(currentProject.id);
      updateEntries(() =>
        modelList.items.map((model) => remoteSummaryEntry(currentProject.id, model))
      );
      const krl = modelList.items.find((model) =>
        model.id.toLowerCase().endsWith(".krl")
      );
      if (krl) setGenerationKrlModelId(krl.id);

      const preferred = preferredModel(modelList.items);
      if (preferred) await loadSpecificModel(preferred.id);
      setNotice(
        `Created ${modelList.items.length} canonical starter model(s) atomically.`
      );
    } catch (error) {
      showError(error);
    }
  }

  async function refreshProjectModels() {
    const currentProject = projectRef.current;
    if (!currentProject) return;
    setNotice("");
    try {
      const modelList = await clientRef.current.listModels(currentProject.id);
      updateEntries((current) =>
        mergeRemoteModelListing(currentProject.id, current, modelList.items)
      );
      const krl = modelList.items.find((model) =>
        model.id.toLowerCase().endsWith(".krl")
      );
      if (krl) setGenerationKrlModelId(krl.id);
      if (!selectedPathRef.current) {
        const preferred = preferredModel(modelList.items);
        if (preferred) await loadSpecificModel(preferred.id);
      }
      setNotice(`Refreshed ${modelList.items.length} project file(s).`);
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
    let accessToken = "";
    try {
      accessToken = (await firebaseAuth.idToken()).trim();
      if (accessToken !== tokenRef.current) setToken(accessToken);
    } catch {
      setLspStatus("Unavailable · Firebase sign-in required");
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
      if (selectedPathRef.current) {
        void refreshOutline(selectedPathRef.current);
      }
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

  async function loadSpecificModel(id: string, announce = false): Promise<boolean> {
    const currentProject = projectRef.current;
    if (!currentProject || !id) return false;
    setConflict(undefined);
    try {
      const model = await clientRef.current.getModel(currentProject.id, id);
      const entry = remoteEntry(currentProject.id, model);
      replaceRemoteEntry(entry);
      workspace.ensure(entry.path, model.content);
      setOpenPaths((current) =>
        current.includes(entry.path) ? current : [...current, entry.path]
      );
      setSelectedPath(entry.path);
      setRevealRange(undefined);
      setSaveState("clean");
      if (announce) {
        setNotice(`Loaded ${model.id} at revision ${model.revision}.`);
      }
      return true;
    } catch (error) {
      showError(error);
      return false;
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
    if (entry.source === "remote" && entry.loaded === false) {
      void loadSpecificModel(entry.path, true);
      return;
    }
    setOpenPaths((current) =>
      current.includes(entry.path) ? current : [...current, entry.path]
    );
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
      setLocalArchiveName(file.name);
      updateEntries(() => imported);
      const firstEditable = imported.find(
        (entry) => editableText(entry) !== null
      );
      if (firstEditable) {
        setOpenPaths([firstEditable.path]);
        setSelectedPath(firstEditable.path);
      } else {
        setOpenPaths([]);
        setSelectedPath(undefined);
      }
      setSaveState("clean");
      setLspStatus("Unavailable · local archive");
      setNotice(
        `Imported ${imported.length} canonical project files locally. Choose an empty authorized hosted project and select Promote here to attach engineering services.`
      );
    } catch (error) {
      showError(error);
    }
  }

  async function promoteArchiveToProject(target: Project) {
    const localEntries = entriesRef.current.filter(
      (entry) => entry.source === "archive"
    );
    if (!localEntries.length) {
      setNotice("Import a local project ZIP before promoting it.");
      return;
    }
    if (!firebaseAuth.user) {
      setNotice("Sign in before promoting a local archive to a hosted project.");
      return;
    }

    const materialized = localEntries.map((entry) => {
      const text = workspace.text(entry.path);
      return text === undefined
        ? entry
        : { ...entry, bytes: new TextEncoder().encode(text) };
    });
    const internalMetadata = materialized.find(
      (entry) => entry.path === ".kide" || entry.path.startsWith(".kide/")
    );
    if (internalMetadata) {
      setNotice(
        `Hosted promotion refuses KIDE internal metadata (${internalMetadata.path}). Remove .kide/** from the archive before promotion; the hosted runtime owns its own project metadata.`
      );
      return;
    }

    const oversized = materialized.find(
      (entry) => entry.bytes.byteLength > 2 * 1024 * 1024
    );
    if (oversized) {
      setNotice(
        `Hosted promotion supports at most 2 MiB per file; ${oversized.path} is larger.`
      );
      return;
    }

    const totalBytes = materialized.reduce(
      (sum, entry) => sum + entry.bytes.byteLength,
      0
    );
    if (materialized.length > MAX_HOSTED_IMPORT_FILES) {
      setNotice(
        `Hosted promotion supports at most ${MAX_HOSTED_IMPORT_FILES} files atomically; this archive contains ${materialized.length}.`
      );
      return;
    }
    if (totalBytes > MAX_HOSTED_IMPORT_BYTES) {
      setNotice(
        "Hosted promotion supports at most 10 MiB of expanded project content in one atomic import."
      );
      return;
    }

    setNotice(`Checking ${target.displayName} before promotion…`);
    try {
      const existing = await clientRef.current.listModels(target.id);
      if (existing.items.length > 0) {
        setNotice(
          `${target.displayName} already contains ${existing.items.length} project file(s). PR84 promotion is non-destructive and requires an empty hosted target.`
        );
        return;
      }

      const result = await clientRef.current.importProjectArchive(
        target.id,
        materialized.map((entry) => ({
          path: entry.path,
          contentBase64: bytesToBase64(entry.bytes),
          mediaType: entry.mediaType
        })),
        localArchiveName
      );

      const opened = await openProject(target);
      if (!opened) return;
      setNotice(
        `Promoted ${result.importedCount} file(s) from ${localArchiveName ?? "the local archive"} to ${target.displayName} atomically. Xtext LSP, GLSP, collaboration and engineering services now use the hosted project.`
      );
    } catch (error) {
      showError(error);
    }
  }

  async function exportArchive() {
    if (!entriesRef.current.length) return;
    setNotice("");

    try {
      const currentProject = projectRef.current;
      const complete: WorkspaceEntry[] = [];
      for (const entry of entriesRef.current) {
        if (
          entry.source === "remote" &&
          entry.loaded === false &&
          currentProject
        ) {
          const model = await clientRef.current.getModel(
            currentProject.id,
            entry.path
          );
          complete.push(remoteEntry(currentProject.id, model));
        } else {
          complete.push(entry);
        }
      }

      if (complete.some((entry) => entry.source === "remote" && entry.loaded === false)) {
        throw new Error("One or more remote project files could not be materialized for export.");
      }

      updateEntries(() => complete);
      const materialized = complete.map((entry) => {
        const current = workspace.text(entry.path);
        return current === undefined ? entry : withText(entry, current);
      });
      const bytes = exportProjectArchive(materialized);
      const blob = new Blob([bytes as BlobPart], { type: "application/zip" });
      const url = URL.createObjectURL(blob);
      const anchor = document.createElement("a");
      anchor.href = url;
      anchor.download = `${safeName(currentProject?.displayName ?? "kide-project")}.zip`;
      anchor.click();
      URL.revokeObjectURL(url);
      setNotice("Exported the complete current project as a ZIP archive.");
    } catch (error) {
      showError(error);
    }
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

  async function openGraphicalModel(path: string) {
    const entry = entriesRef.current.find((candidate) => candidate.path === path);
    if (!entry || entry.source !== "remote" || !diagramTypeFor(path)) return;
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

    setEngineeringCockpitOpen(false);
    if (selectedPathRef.current === path) {
      pendingDiagramPath.current = undefined;
      setViewMode("diagram");
      return;
    }

    pendingDiagramPath.current = path;
    if (entry.loaded === false) {
      await loadSpecificModel(path);
    } else {
      selectEntry(entry);
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
    setGenerationResult(undefined);
    setReconfigurationResult(undefined);
    setEngineeringCockpitOpen(false);
    setSaveState("clean");
    setOpenPaths([]);
    setWorkspaceMatches([]);
    setProblems([]);
    setOutlineItems([]);
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

  function navigate(next: "home" | "register" | "workspace") {
    const path =
      next === "workspace" ? "/workspace" : next === "register" ? "/register" : "/";
    if (window.location.pathname !== path) {
      window.history.pushState({}, "", path);
    }
    setRoute(next);
  }

  async function signOutAndGoHome() {
    await signOutFirebase();
    navigate("home");
  }

  function showError(error: unknown) {
    if (error instanceof ApiClientError) {
      const suffix = error.requestId ? ` Request ${error.requestId}.` : "";
      setNotice(`${error.message}${suffix}`);
      return;
    }
    setNotice(error instanceof Error ? error.message : "Unexpected error.");
  }

  function activateSidebar(view: SidebarView) {
    if (activeSidebar === view && sidebarVisible) {
      setSidebarVisible(false);
      return;
    }
    setActiveSidebar(view);
    setSidebarVisible(true);
  }

  function runEditorAction(actionId: string) {
    window.dispatchEvent(
      new CustomEvent("kide:editor-action", { detail: { actionId } })
    );
  }

  function chooseOperationExecutableScript() {
    const path = selectedPathRef.current;
    if (!path?.toLowerCase().endsWith(".op")) {
      setNotice("Open an Operation DSL file before choosing an executable script.");
      return;
    }
    setQuickPickMode("operationScripts");
  }

  function insertOperationExecutablePath(scriptPath: string) {
    const path = selectedPathRef.current;
    if (!path?.toLowerCase().endsWith(".op")) return;
    const model = workspace.get(path);
    if (!model) return;
    const position = new monaco.Position(
      cursorPosition.line,
      cursorPosition.column
    );
    const quotedPath = JSON.stringify(scriptPath);
    model.pushEditOperations(
      null,
      [{
        range: new monaco.Range(
          position.lineNumber,
          position.column,
          position.lineNumber,
          position.column
        ),
        text: quotedPath,
        forceMoveMarkers: true
      }],
      () => null
    );
    setNotice(`Inserted executable script path ${scriptPath} into ${path}.`);
  }

  function runSynthesisInWorkbench() {
    setEngineeringCockpitOpen(true);
    void runSynthesis();
  }

  function closeEditor(path: string) {
    const index = openPaths.indexOf(path);
    const next = openPaths.filter((candidate) => candidate !== path);
    setOpenPaths(next);
    if (selectedPathRef.current === path) {
      const fallback = next[Math.min(index, Math.max(0, next.length - 1))];
      setSelectedPath(fallback);
      setRevealRange(undefined);
    }
  }

  async function refreshOutline(path = selectedPathRef.current) {
    const controller = lspController.current;
    if (!controller || !path || !controller.client.hasCapability("documentSymbolProvider")) {
      setOutlineItems([]);
      return;
    }
    const model = workspace.get(path);
    if (!model) {
      setOutlineItems([]);
      return;
    }
    try {
      const symbols = await controller.client.documentSymbols(model.uri.toString());
      setOutlineItems(flattenDocumentSymbols(symbols ?? []));
    } catch {
      setOutlineItems([]);
    }
  }

  function openOutlineItem(item: OutlineItem) {
    if (!selectedPathRef.current) return;
    setRevealRange(
      new monaco.Range(
        item.line,
        item.column,
        item.line,
        item.column + Math.max(1, item.name.length)
      )
    );
  }

  async function runWorkspaceSearch() {
    const query = workspaceSearch.trim();
    if (!query) {
      setWorkspaceMatches([]);
      return;
    }

    const needle = query.toLocaleLowerCase();
    const matches: WorkspaceSearchMatch[] = [];
    const hydrated: WorkspaceEntry[] = [];
    const currentProject = projectRef.current;

    try {
      for (const candidate of entriesRef.current) {
        let entry = candidate;
        if (
          entry.source === "remote" &&
          entry.loaded === false &&
          currentProject &&
          isTextProjectPath(entry.path)
        ) {
          const model = await clientRef.current.getModel(
            currentProject.id,
            entry.path
          );
          entry = remoteEntry(currentProject.id, model);
          hydrated.push(entry);
        }

        const text = editableText(entry);
        if (text === null) continue;
        const lines = text.split(/\r?\n/);
        for (let lineIndex = 0; lineIndex < lines.length; lineIndex += 1) {
          const source = lines[lineIndex];
          let from = 0;
          while (from <= source.length) {
            const column = source.toLocaleLowerCase().indexOf(needle, from);
            if (column < 0) break;
            matches.push({
              path: entry.path,
              line: lineIndex + 1,
              column: column + 1,
              preview: source.trim() || source
            });
            if (matches.length >= 500) break;
            from = column + Math.max(1, query.length);
          }
          if (matches.length >= 500) break;
        }
        if (matches.length >= 500) break;
      }

      if (hydrated.length) {
        updateEntries((current) => {
          let next = current;
          for (const entry of hydrated) next = upsert(next, entry);
          return next;
        });
      }

      setWorkspaceMatches(matches);
      if (matches.length >= 500) {
        setNotice("Search stopped after 500 matches. Refine the query for a smaller result set.");
      }
    } catch (error) {
      showError(error);
    }
  }

  async function openWorkspaceSearchMatch(match: WorkspaceSearchMatch) {
    const entry = entriesRef.current.find((candidate) => candidate.path === match.path);
    if (!entry) return;
    if (entry.source === "remote" && entry.loaded === false) {
      await loadSpecificModel(entry.path);
    } else {
      selectEntry(entry);
    }
    setRevealRange(
      new monaco.Range(
        match.line,
        match.column,
        match.line,
        match.column + Math.max(1, workspaceSearch.length)
      )
    );
  }

  async function openProblem(problem: ProblemItem) {
    const entry = entriesRef.current.find((candidate) => candidate.path === problem.path);
    if (!entry) return;
    if (entry.source === "remote" && entry.loaded === false) {
      await loadSpecificModel(entry.path);
    } else {
      selectEntry(entry);
    }
    setRevealRange(
      new monaco.Range(
        problem.line,
        problem.column,
        problem.line,
        problem.column + 1
      )
    );
  }

  function toggleTheme() {
    setTheme((current) => (current === "dark" ? "light" : "dark"));
  }

  const workbenchCommands: Array<QuickPickItem & { run: () => void }> = [
    {
      id: "view.explorer",
      label: "View: Explorer",
      description: "Projects and project files",
      shortcut: "Ctrl/⌘+Shift+E",
      run: () => {
        setActiveSidebar("explorer");
        setSidebarVisible(true);
      }
    },
    {
      id: "view.search",
      label: "View: Search",
      description: "Search text and workspace symbols",
      shortcut: "Ctrl/⌘+Shift+F",
      run: () => {
        setActiveSidebar("search");
        setSidebarVisible(true);
      }
    },
    {
      id: "view.engineering",
      label: "View: Engineering",
      description: "Knowledge, synthesis, reconfiguration and generation",
      run: () => {
        setActiveSidebar("engineering");
        setSidebarVisible(true);
      }
    },
    {
      id: "view.collaboration",
      label: "View: Collaboration",
      description: "Presence and engineering reviews",
      run: () => {
        setActiveSidebar("collaboration");
        setSidebarVisible(true);
      }
    },
    {
      id: "view.settings",
      label: "View: Settings & Connection",
      description: "Authentication and service endpoints",
      run: () => {
        setActiveSidebar("settings");
        setSidebarVisible(true);
      }
    },
    {
      id: "view.sidebar",
      label: sidebarVisible ? "View: Hide Primary Side Bar" : "View: Show Primary Side Bar",
      shortcut: "Ctrl/⌘+B",
      run: () => setSidebarVisible((visible) => !visible)
    },
    {
      id: "view.panel",
      label: bottomPanel ? "View: Hide Panel" : "View: Show Panel",
      description: "Problems and Output",
      shortcut: "Ctrl/⌘+J",
      run: () => setBottomPanel((panel) => (panel ? null : "problems"))
    },
    {
      id: "view.problems",
      label: "View: Problems",
      description: `${problems.length} current diagnostic(s)`,
      shortcut: "Ctrl/⌘+Shift+M",
      run: () => setBottomPanel("problems")
    },
    {
      id: "view.output",
      label: "View: Output",
      description: "KIDE workspace activity and service messages",
      run: () => setBottomPanel("output")
    },
    {
      id: "editor.completion",
      label: "Editor: Trigger Completion",
      shortcut: "Ctrl/⌘+Space",
      run: () => runEditorAction("editor.action.triggerSuggest")
    },
    {
      id: "editor.definition",
      label: "Editor: Go to Definition",
      shortcut: "F12",
      run: () => runEditorAction("editor.action.revealDefinition")
    },
    {
      id: "editor.references",
      label: "Editor: Find All References",
      shortcut: "Shift+F12",
      run: () => runEditorAction("editor.action.referenceSearch.trigger")
    },
    {
      id: "editor.rename",
      label: "Editor: Rename Symbol",
      shortcut: "F2",
      run: () => runEditorAction("editor.action.rename")
    },
    {
      id: "editor.quickfix",
      label: "Editor: Quick Fix",
      shortcut: "Ctrl/⌘+.",
      run: () => runEditorAction("editor.action.quickFix")
    },
    {
      id: "editor.format",
      label: "Editor: Format Document",
      shortcut: "Shift+Alt+F",
      run: () => runEditorAction("editor.action.formatDocument")
    },
    ...(selected?.path.toLowerCase().endsWith(".op")
      ? [{
          id: "operation.executableScript",
          label: "Operation: Choose Executable Script",
          description: "Insert a workspace file path for the execute field",
          run: chooseOperationExecutableScript
        }]
      : []),
    {
      id: "editor.find",
      label: "Editor: Find / Replace",
      shortcut: "Ctrl/⌘+F",
      run: () => runEditorAction("actions.find")
    },
    {
      id: "project.refresh",
      label: "Project: Refresh Files",
      description: project ? project.displayName : "No server project open",
      run: () => void refreshProjectModels()
    },
    {
      id: "engineering.cockpit",
      label: "Engineering: Open Flow & Generation Cockpit",
      description: "Synthesis, state machines, reconfiguration and generated code",
      run: () => setEngineeringCockpitOpen(true)
    },
    {
      id: "engineering.synthesize",
      label: "Engineering: Run Deterministic Synthesis",
      description: selected?.path.endsWith(".activity")
        ? selected.path
        : "Open an Activity model first",
      run: runSynthesisInWorkbench
    },
    {
      id: "engineering.reconfigure",
      label: "Engineering: Reconfigure Control Plan",
      description: synthesisResult?.status === "SUCCESS"
        ? reconfigurationCause
        : "Run synthesis first",
      run: () => {
        setEngineeringCockpitOpen(true);
        void runReconfiguration();
      }
    },
    {
      id: "engineering.generate",
      label: "Engineering: Generate Code",
      description: canGenerate ? generationKrlModelId : "Run synthesis first",
      run: () => {
        setEngineeringCockpitOpen(true);
        void runGeneration();
      }
    },
    {
      id: "engineering.activityDiagram",
      label: "Engineering: Visualize Activity Flow",
      description: activityModels[0] ?? "No Activity model",
      run: () => {
        const path =
          selected?.path.toLowerCase().endsWith(".activity")
            ? selected.path
            : activityModels[0];
        if (path) void openGraphicalModel(path);
      }
    },
    {
      id: "engineering.stateMachine",
      label: "Engineering: Visualize MNC State Machine",
      description: mncModels[0] ?? "No MNC model",
      run: () => mncModels[0] && void openGraphicalModel(mncModels[0])
    },
    {
      id: "engineering.diagram",
      label: viewMode === "diagram"
        ? "Engineering: Switch to Text Editor"
        : "Engineering: Open Graphical Editor",
      run: () => {
        if (viewMode === "diagram") setViewMode("text");
        else openDiagram();
      }
    },
    {
      id: "workbench.theme",
      label: `Preferences: Use ${theme === "dark" ? "Light" : "Dark"} Theme`,
      run: toggleTheme
    }
  ];

  const quickPickItems: QuickPickItem[] =
    quickPickMode === "files"
      ? entries.map((entry) => ({
          id: `file:${entry.path}`,
          label: basename(entry.path),
          description: entry.path,
          keywords: [entry.path]
        }))
      : quickPickMode === "operationScripts"
        ? entries
            .filter((entry) => entry.path !== selectedPathRef.current)
            .map((entry) => ({
              id: `operation-script:${entry.path}`,
              label: basename(entry.path),
              description: entry.path,
              keywords: [entry.path]
            }))
        : workbenchCommands.map(({ run: _run, ...item }) => item);

  function chooseQuickPick(item: QuickPickItem) {
    if (quickPickMode === "files") {
      const path = item.id.startsWith("file:") ? item.id.slice(5) : item.id;
      const entry = entriesRef.current.find((candidate) => candidate.path === path);
      if (entry) selectEntry(entry);
      return;
    }
    if (quickPickMode === "operationScripts") {
      const path = item.id.startsWith("operation-script:")
        ? item.id.slice("operation-script:".length)
        : item.id;
      insertOperationExecutablePath(path);
      return;
    }
    workbenchCommands.find((command) => command.id === item.id)?.run();
  }

  if (route === "register") {
    return (
      <RegisterPage
        email={firebaseEmail}
        password={firebasePassword}
        confirmPassword={firebaseConfirmPassword}
        authStatus={authStatus}
        configured={Boolean(FIREBASE_PROJECT_ID && FIREBASE_API_KEY)}
        busy={authBusy}
        notice={notice}
        onEmail={setFirebaseEmail}
        onPassword={setFirebasePassword}
        onConfirmPassword={setFirebaseConfirmPassword}
        onRegister={() => void registerFirebase()}
        onOpenSignIn={() => navigate("home")}
      />
    );
  }

  if (route === "home") {
    return (
      <LandingPage
        email={firebaseEmail}
        password={firebasePassword}
        authStatus={authStatus}
        signedInEmail={firebaseAuth.user?.email}
        configured={Boolean(FIREBASE_PROJECT_ID && FIREBASE_API_KEY)}
        busy={authBusy}
        notice={notice}
        onEmail={setFirebaseEmail}
        onPassword={setFirebasePassword}
        onSignIn={() => void signInFirebase()}
        onSignOut={() => void signOutFirebase()}
        onOpenRegister={() => navigate("register")}
        onOpenWorkspace={() => navigate("workspace")}
      />
    );
  }

  return (
    <main className="app-shell" data-theme={theme}>
      <header className="ide-titlebar">
        <button
          className="ide-brand"
          type="button"
          onClick={() => activateSidebar("explorer")}
          title="KIDE Explorer"
        >
          <span className="ide-brand-mark">K</span>
          <span>KIDE</span>
          <small>WEB</small>
        </button>

        <button
          className="command-center"
          type="button"
          onClick={() => setQuickPickMode("commands")}
          aria-label="Open command palette"
        >
          <span className="command-center-icon">⌕</span>
          <span className="command-center-copy">
            {project?.displayName ?? "KIDE Engineering Workspace"}
            {selected ? ` · ${basename(selected.path)}` : ""}
          </span>
          <kbd>Ctrl/⌘+Shift+P</kbd>
        </button>

        <div className="titlebar-actions">
          <button
            type="button"
            className="titlebar-home-button"
            onClick={() => navigate("home")}
          >
            Home
          </button>
          <button
            type="button"
            className="titlebar-icon-button"
            onClick={toggleTheme}
            title={theme === "dark" ? "Use light theme" : "Use dark theme"}
            aria-label={theme === "dark" ? "Use light theme" : "Use dark theme"}
          >
            {theme === "dark" ? "☀" : "◐"}
          </button>
          <button
            type="button"
            className="account-button"
            onClick={() => {
              if (!firebaseAuth.user) {
                navigate("home");
                return;
              }
              setActiveSidebar("settings");
              setSidebarVisible(true);
            }}
          >
            <span className="account-dot" />
            {firebaseAuth.user?.email ?? "Sign in"}
          </button>
        </div>
      </header>

      {notice && <div className="notice" role="status">{notice}</div>}
      {conflict && (
        <div className="conflict" role="alert">
          <strong>Revision conflict</strong>
          <span>{conflict}</span>
          <button onClick={() => void reloadConflict()}>Reload server revision</button>
        </div>
      )}

      <section className={`workspace ${sidebarVisible ? "" : "sidebar-hidden"}`}>
        <nav className="activity-bar" aria-label="Workbench views">
          <button
            type="button"
            className={activeSidebar === "explorer" && sidebarVisible ? "active" : ""}
            aria-pressed={activeSidebar === "explorer" && sidebarVisible}
            title="Explorer (Ctrl/⌘+Shift+E)"
            onClick={() => activateSidebar("explorer")}
          >
            <span aria-hidden="true">▤</span>
            <span className="sr-only">Explorer</span>
          </button>
          <button
            type="button"
            className={activeSidebar === "search" && sidebarVisible ? "active" : ""}
            aria-pressed={activeSidebar === "search" && sidebarVisible}
            title="Search (Ctrl/⌘+Shift+F)"
            onClick={() => activateSidebar("search")}
          >
            <span aria-hidden="true">⌕</span>
            <span className="sr-only">Search</span>
          </button>
          <button
            type="button"
            className={activeSidebar === "engineering" && sidebarVisible ? "active" : ""}
            aria-pressed={activeSidebar === "engineering" && sidebarVisible}
            title="Engineering"
            onClick={() => activateSidebar("engineering")}
          >
            <span aria-hidden="true">◇</span>
            <span className="sr-only">Engineering</span>
          </button>
          <button
            type="button"
            className={activeSidebar === "collaboration" && sidebarVisible ? "active" : ""}
            aria-pressed={activeSidebar === "collaboration" && sidebarVisible}
            title="Collaboration"
            onClick={() => activateSidebar("collaboration")}
          >
            <span aria-hidden="true">◎</span>
            <span className="sr-only">Collaboration</span>
          </button>
          <span className="activity-spacer" />
          <button
            type="button"
            className={activeSidebar === "settings" && sidebarVisible ? "active" : ""}
            aria-pressed={activeSidebar === "settings" && sidebarVisible}
            title="Settings & Connection"
            onClick={() => activateSidebar("settings")}
          >
            <span aria-hidden="true">⚙</span>
            <span className="sr-only">Settings</span>
          </button>
        </nav>

        {sidebarVisible && (
        <aside className="sidebar" aria-label={sidebarViewLabel(activeSidebar)}>
          <div className="sidebar-view-title">
            <strong>{sidebarViewLabel(activeSidebar)}</strong>
            <button
              type="button"
              onClick={() => setSidebarVisible(false)}
              title="Hide Primary Side Bar"
              aria-label="Hide Primary Side Bar"
            >
              ×
            </button>
          </div>

          {activeSidebar === "settings" && (
            <div className="settings-view">
              <section className="settings-section">
                <h2>Account</h2>
                {firebaseAuth.user ? (
                  <>
                    <div className="signed-in-workbench-account">
                      <span className="account-avatar">
                        {firebaseAuth.user.email.slice(0, 1).toUpperCase()}
                      </span>
                      <div>
                        <small>Signed in as</small>
                        <strong>{firebaseAuth.user.email}</strong>
                      </div>
                    </div>
                    <div className="settings-actions">
                      <button type="button" onClick={() => void connect()}>
                        Reconnect API
                      </button>
                      <button type="button" onClick={() => void signOutAndGoHome()}>
                        Sign out
                      </button>
                    </div>
                    {FIREBASE_PROJECT_ID && (
                      <div className="authorization-identity" aria-label="KIDE authorization identity">
                        <small>KIDE principal</small>
                        <code>{`firebase:${FIREBASE_PROJECT_ID}#${firebaseAuth.user.uid}`}</code>
                        <p className="muted">
                          Firebase sign-in proves identity. Server-side KIDE role bindings grant
                          project access; authentication never grants engineering rights automatically.
                        </p>
                      </div>
                    )}
                  </>
                ) : (
                  <div className="workspace-auth-required">
                    <strong>Authentication required for server engineering</strong>
                    <p className="muted">
                      Sign in on the KIDE landing page to use the enterprise API,
                      Xtext LSP, GLSP, synthesis, knowledge and generation services.
                    </p>
                    <button
                      type="button"
                      className="primary-action"
                      onClick={() => navigate("home")}
                    >
                      Go to sign in
                    </button>
                  </div>
                )}
              </section>

              <section className="settings-section">
                <h2>Services</h2>
                <label>
                  API origin
                  <input
                    aria-label="API origin"
                    value={serviceOrigin}
                    onChange={(event) => setServiceOrigin(event.target.value)}
                  />
                </label>
                <label>
                  LSP / GLSP gateway origin
                  <input
                    aria-label="LSP gateway origin"
                    value={gatewayOrigin}
                    onChange={(event) => setGatewayOrigin(event.target.value)}
                  />
                </label>
                <div className="connection-summary">
                  <span><strong>API</strong>{serviceStatus}</span>
                  <span><strong>Xtext LSP</strong>{lspStatus}</span>
                  <span><strong>Collaboration</strong>{collaborationStatus}</span>
                </div>
                {runtimeVersion && (
                  <div className="runtime-version-summary" aria-label="Runtime compatibility">
                    <small>
                      Web build {WEB_BUILD_ID} · Backend {runtimeVersion.productVersion} ·
                      build {runtimeVersion.buildId} · engineering level {runtimeVersion.engineeringCompatibilityLevel} ·
                      project schema {runtimeVersion.projectSchemaVersion} ·
                      kernel schema {runtimeVersion.sharedKernelSchemaVersion}
                    </small>
                  </div>
                )}
                {!project && entries.some((entry) => entry.source !== "remote") && (
                  <div className="local-archive-service-note" role="status">
                    <strong>Local archive workspace</strong>
                    <p className="muted">
                      This ZIP is open only in the browser and has no hosted project/workspace ID.
                      Reconnecting the API checks server availability and authorization. To attach
                      Xtext LSP, GLSP, collaboration, synthesis and generation, promote this archive
                      into an empty authorized hosted project from the Explorer.
                    </p>
                  </div>
                )}
              </section>

              <section className="settings-section">
                <h2>Workspace</h2>
                <label className="import-button">
                  Import project ZIP
                  <input
                    aria-label="Import project ZIP"
                    type="file"
                    accept=".zip,application/zip"
                    onChange={(event) => void importArchive(event)}
                  />
                </label>
                <button
                  type="button"
                  disabled={!entries.length}
                  onClick={() => void exportArchive()}
                >
                  Export project ZIP
                </button>
                <button type="button" onClick={toggleTheme}>
                  Use {theme === "dark" ? "light" : "dark"} theme
                </button>
              </section>
            </div>
          )}

          {activeSidebar === "explorer" && (
            <>
              <div className="sidebar-section-heading">
                <h2>Projects</h2>
                <button
                  type="button"
                  onClick={() => setQuickPickMode("files")}
                  title="Quick Open (Ctrl/⌘+P)"
                >
                  ⌕
                </button>
              </div>
          {firebaseAuth.user && runtimeVersion && canCreateProjects && (
            <form
              aria-label="Create hosted project"
              className="project-actions"
              onSubmit={(event) => {
                event.preventDefault();
                void createHostedProject();
              }}
            >
              <input
                aria-label="New project name"
                maxLength={128}
                value={newProjectName}
                onChange={(event) => setNewProjectName(event.target.value)}
                placeholder="New engineering project"
              />
              <button type="submit" disabled={creatingProject || !newProjectName.trim()}>
                {creatingProject ? "Creating…" : "Create hosted project"}
              </button>
            </form>
          )}
          {projects.length === 0 ? (
            <div className="explorer-empty">
              <p className="muted">
                {firebaseAuth.user
                  ? "No authorized server projects are currently listed. You can also import a local ZIP."
                  : "Sign in from Home to list authorized server projects, or import a local ZIP."}
              </p>
              {!firebaseAuth.user && (
                <button type="button" onClick={() => navigate("home")}>
                  Go to sign in
                </button>
              )}
            </div>
          ) : (
            <ul className="project-list">
              {projects.map((item) => (
                <li key={item.id}>
                  <span>{item.displayName}</span>
                  <button onClick={() => void openProject(item)}>Open</button>
                  {localArchiveOpen && (
                    <button onClick={() => void promoteArchiveToProject(item)}>
                      Promote here
                    </button>
                  )}
                </li>
              ))}
            </ul>
          )}

          {project && (
            <div className="model-loader">
              <h3>{project.displayName}</h3>
              <p className="muted">
                {entries.length} project file(s) discovered. Production DSLs open with the shared Xtext language services; Activity and MNC models also expose graphical editing.
              </p>
              <div className="project-actions">
                <button
                  disabled={entries.some((entry) => isProductionDslPath(entry.path))}
                  onClick={() => void createStarterEngineeringModels()}
                >
                  Create starter engineering models
                </button>
                <button onClick={() => void refreshProjectModels()}>
                  Refresh project files
                </button>
                <button onClick={() => void connectLanguageServices()}>
                  Reconnect language services
                </button>
              </div>
            </div>
          )}
            </>
          )}

          {activeSidebar === "search" && (
            <>
              <section className="search-view">
                <h2>Search project</h2>
                <div className="search-box-row">
                  <input
                    aria-label="Search project text"
                    value={workspaceSearch}
                    onChange={(event) => setWorkspaceSearch(event.target.value)}
                    onKeyDown={(event) => {
                      if (event.key === "Enter") void runWorkspaceSearch();
                    }}
                    placeholder="Search across project files"
                  />
                  <button
                    type="button"
                    disabled={!workspaceSearch.trim()}
                    onClick={() => void runWorkspaceSearch()}
                  >
                    Search
                  </button>
                </div>
                <p className="muted">
                  Text search materializes remote text models on demand and is capped at 500 matches.
                </p>
                <ul className="search-results">
                  {workspaceMatches.map((match, index) => (
                    <li key={`${match.path}:${match.line}:${match.column}:${index}`}>
                      <button
                        type="button"
                        onClick={() => void openWorkspaceSearchMatch(match)}
                      >
                        <strong>{match.path}</strong>
                        <span>Ln {match.line}, Col {match.column}</span>
                        <small>{match.preview}</small>
                      </button>
                    </li>
                  ))}
                </ul>
              </section>

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
            </>
          )}

          {activeSidebar === "engineering" && project && (
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

          {activeSidebar === "engineering" &&
          project &&
          selected?.source === "remote" &&
          selected.path.endsWith(".activity") && (
            <div className="synthesis-panel">
              <div className="panel-heading">
                <h2>Deterministic synthesis</h2>
                <button
                  disabled={!selected.etag || selected.dirty}
                  onClick={() => void runSynthesis()}
                >
                  Run synthesis
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

          {activeSidebar === "explorer" && (
            <>
              <div className="sidebar-section-heading files-heading">
                <h2>Files</h2>
                <button
                  type="button"
                  disabled={!project}
                  onClick={() => void refreshProjectModels()}
                  title="Refresh project files"
                >
                  ↻
                </button>
              </div>
              <FileTree
                entries={entries}
                selectedPath={selectedPath}
                onSelect={selectEntry}
              />

              {outlineItems.length > 0 && (
                <section className="outline-view" aria-label="Document outline">
                  <div className="sidebar-section-heading">
                    <h2>Outline</h2>
                    <button
                      type="button"
                      onClick={() => void refreshOutline()}
                      title="Refresh outline"
                    >
                      ↻
                    </button>
                  </div>
                  <ul>
                    {outlineItems.map((item, index) => (
                      <li key={`${item.name}:${item.line}:${index}`}>
                        <button
                          type="button"
                          style={{ paddingLeft: 6 + item.depth * 12 }}
                          onClick={() => openOutlineItem(item)}
                        >
                          <span className="outline-kind" aria-hidden="true">◇</span>
                          <span className="file-path">{item.name}</span>
                          <small>{item.detail ?? ""}</small>
                        </button>
                      </li>
                    ))}
                  </ul>
                </section>
              )}
            </>
          )}

          {activeSidebar === "collaboration" && project && (
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
        )}

        <section className="editor-panel">
          <div className="editor-tabs" role="tablist" aria-label="Open editors">
            {openPaths.length ? (
              openPaths.map((path) => {
                const entry = entries.find((candidate) => candidate.path === path);
                return (
                  <button
                    type="button"
                    role="tab"
                    aria-selected={path === selectedPath}
                    className={path === selectedPath ? "active" : ""}
                    key={path}
                    onClick={() => entry && selectEntry(entry)}
                    title={path}
                  >
                    <span className="file-icon" aria-hidden="true">{fileGlyph(path)}</span>
                    <span>{basename(path)}</span>
                    {entry?.dirty && <span className="tab-dirty" aria-label="Unsaved">●</span>}
                    <span
                      className="tab-close"
                      role="button"
                      aria-label={`Close ${basename(path)}`}
                      onClick={(event) => {
                        event.stopPropagation();
                        closeEditor(path);
                      }}
                    >
                      ×
                    </span>
                  </button>
                );
              })
            ) : (
              <span className="editor-tabs-empty">No open editors</span>
            )}
          </div>

          {selected && (
            <nav className="breadcrumbs" aria-label="Editor breadcrumbs">
              {(project ? [project.displayName, ...selected.path.split("/")] : selected.path.split("/"))
                .map((part, index, parts) => (
                  <span key={`${part}:${index}`}>
                    {part}
                    {index < parts.length - 1 && <b aria-hidden="true">›</b>}
                  </span>
                ))}
            </nav>
          )}

          <div className="engineering-workflow-bar" aria-label="Engineering workflow">
            <button
              type="button"
              className={engineeringCockpitOpen ? "active" : ""}
              onClick={() => setEngineeringCockpitOpen(true)}
            >
              Engineering flow
            </button>
            <button
              type="button"
              disabled={!activityModels.length}
              onClick={() => {
                const path =
                  selected?.path.toLowerCase().endsWith(".activity")
                    ? selected.path
                    : activityModels[0];
                if (path) void openGraphicalModel(path);
              }}
            >
              Visualize flow
            </button>
            <button
              type="button"
              className="primary-action"
              disabled={!canSynthesize}
              onClick={runSynthesisInWorkbench}
            >
              Synthesize
            </button>
            <button
              type="button"
              disabled={!mncModels.length}
              onClick={() => mncModels[0] && void openGraphicalModel(mncModels[0])}
            >
              State machine
            </button>
            <button
              type="button"
              disabled={!canReconfigure}
              onClick={() => {
                setEngineeringCockpitOpen(true);
                void runReconfiguration();
              }}
            >
              Reconfigure
            </button>
            <button
              type="button"
              className="primary-action"
              disabled={!canGenerate}
              onClick={() => {
                setEngineeringCockpitOpen(true);
                void runGeneration();
              }}
            >
              Generate code
            </button>
            <span className="engineering-workflow-status">
              {synthesisResult?.status ?? "Ready"}
            </span>
          </div>
          <div className="editor-toolbar">
            <div className="editor-identity">
              <strong>{selected ? languageLabelForPath(selected.path) : "Welcome"}</strong>
              {selected?.revision && <span>revision {selected.revision}</span>}
            </div>
            <div className="editor-actions">
              {project &&
              selected?.source === "remote" &&
              selected.path.endsWith(".activity") && (
                <button
                  className="primary-action"
                  disabled={!selected.etag || selected.dirty}
                  onClick={runSynthesisInWorkbench}
                >
                  Synthesize
                </button>
              )}
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
              <button
                type="button"
                className="editor-more-actions"
                onClick={() => setQuickPickMode("commands")}
                title="More editor actions (F1)"
                aria-label="More editor actions"
              >
                ⋯
              </button>
              <span className={`save-state state-${saveState}`}>{saveState}</span>
            </div>
          </div>
          {engineeringCockpitOpen && project ? (
            <EngineeringCockpit
              projectName={project.displayName}
              selectedPath={selected?.path}
              activityModels={activityModels}
              mncModels={mncModels}
              synthesisResult={synthesisResult}
              reconfigurationResult={reconfigurationResult}
              generationResult={generationResult}
              canSynthesize={canSynthesize}
              canReconfigure={canReconfigure}
              canGenerate={canGenerate}
              reconfigurationCause={reconfigurationCause}
              generationKrlModelId={generationKrlModelId}
              onReconfigurationCause={setReconfigurationCause}
              onGenerationKrlModelId={setGenerationKrlModelId}
              onSynthesize={() => void runSynthesis()}
              onReconfigure={() => void runReconfiguration()}
              onGenerate={() => void runGeneration()}
              onOpenActivityDiagram={(path) => void openGraphicalModel(path)}
              onOpenMncDiagram={(path) => void openGraphicalModel(path)}
              onOpenEditor={() => setEngineeringCockpitOpen(false)}
            />
          ) : viewMode === "diagram" &&
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
            <div className="welcome-workbench">
              <div className="welcome-copy">
                <span className="welcome-mark">K</span>
                <p className="eyebrow">KIDE WEB</p>
                <h1>Engineering Workspace</h1>
                <p>
                  Model with the shared Xtext DSLs, edit Activity and MNC diagrams,
                  synthesize supervisory designs, and generate deterministic artifacts.
                </p>
              </div>

              {!firebaseAuth.user ? (
                <div className="welcome-actions">
                  <button
                    type="button"
                    className="primary-action"
                    onClick={() => {
                      setActiveSidebar("settings");
                      setSidebarVisible(true);
                    }}
                  >
                    Sign in to KIDE
                  </button>
                  <label className="import-button">
                    Open local project ZIP
                    <input
                      aria-label="Open local project ZIP"
                      type="file"
                      accept=".zip,application/zip"
                      onChange={(event) => void importArchive(event)}
                    />
                  </label>
                </div>
              ) : project ? (
                <div className="welcome-actions">
                  <strong>{project.displayName}</strong>
                  <p className="muted">
                    {entries.length} project file(s). Select a file in Explorer or use Quick Open.
                  </p>
                  <button type="button" onClick={() => setQuickPickMode("files")}>
                    Quick Open file
                  </button>
                  {!entries.some((entry) => isProductionDslPath(entry.path)) && (
                    <button
                      type="button"
                      className="primary-action"
                      onClick={() => void createStarterEngineeringModels()}
                    >
                      Create starter engineering models
                    </button>
                  )}
                </div>
              ) : (
                <div className="welcome-projects">
                  <h2>Open an engineering project</h2>
                  {projects.length ? (
                    projects.map((item) => (
                      <button
                        type="button"
                        key={item.id}
                        onClick={() => void openProject(item)}
                      >
                        <strong>{item.displayName}</strong>
                        <small>{item.id}</small>
                      </button>
                    ))
                  ) : (
                    <p className="muted">
                      No authorized projects were returned by the connected service.
                    </p>
                  )}
                </div>
              )}

              <div className="welcome-shortcuts">
                <span><kbd>Ctrl/⌘+P</kbd> Quick Open</span>
                <span><kbd>F1</kbd> Command Palette</span>
                <span><kbd>Ctrl/⌘+Shift+F</kbd> Search</span>
                <span><kbd>Ctrl/⌘+Shift+M</kbd> Problems</span>
              </div>
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
              theme={theme === "dark" ? "vs-dark" : "vs"}
              onCursorChange={(line, column) => setCursorPosition({ line, column })}
            />
          )}

          {bottomPanel && (
            <section className="bottom-panel" aria-label="Workbench panel">
              <div className="bottom-panel-header">
                <div className="bottom-panel-tabs">
                  <button
                    type="button"
                    className={bottomPanel === "problems" ? "active" : ""}
                    onClick={() => setBottomPanel("problems")}
                  >
                    Problems <span>{problems.length}</span>
                  </button>
                  <button
                    type="button"
                    className={bottomPanel === "output" ? "active" : ""}
                    onClick={() => setBottomPanel("output")}
                  >
                    Output
                  </button>
                </div>
                <button
                  type="button"
                  className="panel-close"
                  aria-label="Close panel"
                  onClick={() => setBottomPanel(null)}
                >
                  ×
                </button>
              </div>
              <div className="bottom-panel-body">
                {bottomPanel === "problems" ? (
                  problems.length ? (
                    <ul className="problems-list">
                      {problems.map((problem, index) => (
                        <li key={`${problem.path}:${problem.line}:${problem.column}:${index}`}>
                          <button
                            type="button"
                            onClick={() => void openProblem(problem)}
                          >
                            <span
                              className={`problem-severity ${problemSeverityClass(problem.severity)}`}
                              aria-hidden="true"
                            >
                              {problemSeverityGlyph(problem.severity)}
                            </span>
                            <span className="problem-copy">
                              <strong>{problem.message}</strong>
                              <small>
                                {problem.path} · Ln {problem.line}, Col {problem.column}
                              </small>
                            </span>
                          </button>
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <div className="panel-empty">No problems detected in loaded models.</div>
                  )
                ) : (
                  <div className="output-view">
                    <div className="output-toolbar">
                      <strong>KIDE Workspace</strong>
                      <button type="button" onClick={() => setOutputLog([])}>Clear</button>
                    </div>
                    <pre>{outputLog.length ? outputLog.join("\n") : "No workspace output yet."}</pre>
                  </div>
                )}
              </div>
            </section>
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

      <footer className="status-bar" aria-label="Workbench status">
        <div className="status-left">
          <button
            type="button"
            className={`status-item ${connectionTone(serviceStatus)}`}
            onClick={() => {
              setActiveSidebar("settings");
              setSidebarVisible(true);
            }}
            title={serviceStatus}
          >
            <span className="status-dot" /> API {connectionSummary(serviceStatus)}
          </button>
          <button
            type="button"
            className={`status-item ${connectionTone(lspStatus)}`}
            onClick={() => {
              setActiveSidebar("settings");
              setSidebarVisible(true);
            }}
            title={lspStatus}
          >
            <span className="status-dot" /> Xtext {connectionSummary(lspStatus)}
          </button>
          <button
            type="button"
            className={`status-item ${connectionTone(collaborationStatus)}`}
            onClick={() => activateSidebar("collaboration")}
            title={collaborationStatus}
          >
            <span className="status-dot" /> Collaboration {connectionSummary(collaborationStatus)}
          </button>
          {project && <span className="status-project">{project.displayName}</span>}
        </div>

        <div className="status-right">
          <button
            type="button"
            className="status-item"
            onClick={() => setBottomPanel("problems")}
            title="Show Problems"
          >
            × {problems.filter((problem) => problem.severity === monaco.MarkerSeverity.Error).length}
            <span className="status-warning">
              △ {problems.filter((problem) => problem.severity === monaco.MarkerSeverity.Warning).length}
            </span>
          </button>
          {selected && (
            <>
              <span className="status-item status-static">{languageLabelForPath(selected.path)}</span>
              <span className="status-item status-static">
                Ln {cursorPosition.line}, Col {cursorPosition.column}
              </span>
              <span className={`status-item status-static save-state state-${saveState}`}>
                {saveState}
              </span>
            </>
          )}
          <button
            type="button"
            className="status-item"
            onClick={() => setQuickPickMode("commands")}
            title="Command Palette (F1)"
          >
            {theme === "dark" ? "Dark" : "Light"} · F1
          </button>
        </div>
      </footer>

      <QuickPick
        open={quickPickMode !== null}
        title={
          quickPickMode === "files"
            ? "Quick Open"
            : quickPickMode === "operationScripts"
              ? "Choose Executable Script"
              : "Command Palette"
        }
        placeholder={
          quickPickMode === "files"
            ? "Type a file name to open…"
            : quickPickMode === "operationScripts"
              ? "Type a workspace file name…"
              : "Type a command to run…"
        }
        items={quickPickItems}
        onSelect={chooseQuickPick}
        onClose={() => setQuickPickMode(null)}
      />
    </main>
  );
}

function remoteEntry(projectId: string, model: Model): WorkspaceEntry {
  return {
    path: model.id,
    bytes: model.contentBase64
      ? bytesFromBase64(model.contentBase64)
      : new TextEncoder().encode(model.content),
    mediaType: model.mediaType ?? mediaTypeFor(model.id),
    source: "remote",
    projectId,
    etag: model.etag,
    revision: model.revision,
    dirty: false,
    loaded: true
  };
}

function remoteSummaryEntry(
  projectId: string,
  model: ModelSummary
): WorkspaceEntry {
  return {
    path: model.id,
    bytes: new Uint8Array(),
    mediaType: model.mediaType ?? mediaTypeFor(model.id),
    source: "remote",
    projectId,
    etag: model.etag,
    revision: model.revision,
    dirty: false,
    loaded: false
  };
}

function mergeRemoteModelListing(
  projectId: string,
  current: WorkspaceEntry[],
  models: ModelSummary[]
): WorkspaceEntry[] {
  return models
    .map((model) => {
      const existing = current.find(
        (entry) => entry.source === "remote" && entry.path === model.id
      );
      return existing ?? remoteSummaryEntry(projectId, model);
    })
    .sort((a, b) => a.path.localeCompare(b.path));
}

function isProductionDslPath(path: string): boolean {
  const lower = path.toLowerCase();
  return [".activity", ".dml", ".cap", ".mncspec", ".op", ".krl"].some(
    (extension) => lower.endsWith(extension)
  );
}

function isTextProjectPath(path: string): boolean {
  const lower = path.toLowerCase();
  return [
    ".activity",
    ".cap",
    ".dml",
    ".json",
    ".krl",
    ".md",
    ".mncspec",
    ".op",
    ".properties",
    ".txt",
    ".xml",
    ".yaml",
    ".yml"
  ].some((extension) => lower.endsWith(extension));
}

function basename(path: string): string {
  const slash = path.lastIndexOf("/");
  return slash < 0 ? path : path.slice(slash + 1);
}

function fileGlyph(path: string): string {
  const lower = path.toLowerCase();
  if (lower.endsWith(".activity")) return "A";
  if (lower.endsWith(".dml")) return "D";
  if (lower.endsWith(".cap")) return "C";
  if (lower.endsWith(".mncspec")) return "M";
  if (lower.endsWith(".op")) return "O";
  if (lower.endsWith(".krl")) return "K";
  if (lower.endsWith(".md")) return "#";
  if (lower.endsWith(".json")) return "{}";
  return "·";
}

function languageLabelForPath(path: string): string {
  const lower = path.toLowerCase();
  if (lower.endsWith(".activity")) return "Activity DSL";
  if (lower.endsWith(".dml")) return "Data Model DSL";
  if (lower.endsWith(".cap")) return "Capability DSL";
  if (lower.endsWith(".mncspec")) return "MNC Specification DSL";
  if (lower.endsWith(".op")) return "Operation DSL";
  if (lower.endsWith(".krl")) return "Knowledge Representation DSL";
  if (lower.endsWith(".md")) return "Markdown";
  if (lower.endsWith(".json")) return "JSON";
  if (lower.endsWith(".xml")) return "XML";
  if (lower.endsWith(".yaml") || lower.endsWith(".yml")) return "YAML";
  if (lower.endsWith(".properties")) return "Properties";
  return "Plain Text";
}

function flattenDocumentSymbols(
  symbols: Array<DocumentSymbol | SymbolInformation>,
  depth = 0
): OutlineItem[] {
  const result: OutlineItem[] = [];
  for (const symbol of symbols) {
    if ("location" in symbol) {
      result.push({
        name: symbol.name,
        kind: symbol.kind,
        line: symbol.location.range.start.line + 1,
        column: symbol.location.range.start.character + 1,
        depth
      });
      continue;
    }

    result.push({
      name: symbol.name,
      detail: symbol.detail,
      kind: symbol.kind,
      line: symbol.selectionRange.start.line + 1,
      column: symbol.selectionRange.start.character + 1,
      depth
    });
    if (symbol.children?.length) {
      result.push(...flattenDocumentSymbols(symbol.children, depth + 1));
    }
  }
  return result;
}

function sidebarViewLabel(view: SidebarView): string {
  switch (view) {
    case "explorer": return "Explorer";
    case "search": return "Search";
    case "engineering": return "Engineering";
    case "collaboration": return "Collaboration";
    case "settings": return "Settings";
  }
}

function problemSeverityClass(severity: monaco.MarkerSeverity): string {
  if (severity === monaco.MarkerSeverity.Error) return "error";
  if (severity === monaco.MarkerSeverity.Warning) return "warning";
  if (severity === monaco.MarkerSeverity.Info) return "info";
  return "hint";
}

function problemSeverityGlyph(severity: monaco.MarkerSeverity): string {
  if (severity === monaco.MarkerSeverity.Error) return "×";
  if (severity === monaco.MarkerSeverity.Warning) return "△";
  if (severity === monaco.MarkerSeverity.Info) return "i";
  return "·";
}

function connectionTone(status: string): string {
  const normalized = status.toLowerCase();
  if (normalized.includes("incompatible")) return "status-error";
  if (
    normalized.includes("connected") ||
    normalized.startsWith("up") ||
    normalized.includes("signed in")
  ) {
    if (normalized.includes("not connected")) return "status-offline";
    return "status-online";
  }
  if (normalized.includes("connecting")) return "status-connecting";
  if (normalized.includes("degraded") || normalized.includes("failed")) {
    return "status-error";
  }
  return "status-offline";
}

function connectionSummary(status: string): string {
  const normalized = status.toLowerCase();
  if (normalized.includes("incompatible")) return "Incompatible";
  if (normalized.includes("not connected")) return "Offline";
  if (normalized.includes("connecting")) return "Connecting";
  if (normalized.includes("degraded")) return "Degraded";
  if (normalized.includes("failed")) return "Failed";
  if (normalized.startsWith("up") || normalized.includes("connected")) return "Online";
  return status;
}

function preferredModel(models: ModelSummary[]): ModelSummary | undefined {
  const extensions = [".activity", ".dml", ".cap", ".mncspec", ".op", ".krl"];
  for (const extension of extensions) {
    const match = models.find((model) => model.id.toLowerCase().endsWith(extension));
    if (match) return match;
  }
  return undefined;
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
