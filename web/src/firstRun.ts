/** Stateless first-run guidance derived from actual backend/editor state. */
export interface FirstRunState {
  signedIn: boolean;
  apiCompatible: boolean;
  projectCount: number;
  projectOpen: boolean;
  canCreateProject: boolean;
  hasProductionDsl: boolean;
  xtextConnected: boolean;
  collaborationConnected: boolean;
}

export type FirstRunAction = "sign-in" | "reconnect" | "explorer"
  | "starter" | "quick-open" | "services";

export interface FirstRunStep {
  id: string;
  label: string;
  detail: string;
  completed: boolean;
  action?: FirstRunAction;
  actionLabel?: string;
}

export function firstRunSteps(state: FirstRunState): FirstRunStep[] {
  return [
    {
      id: "sign-in", label: "Sign in",
      detail: "Authenticate with the Firebase identity configured for this KIDE deployment.",
      completed: state.signedIn,
      action: "sign-in", actionLabel: "Open sign-in page"
    },
    {
      id: "api", label: "Connect the engineering API",
      detail: "Verify backend compatibility; an API health check alone does not grant project access.",
      completed: state.apiCompatible,
      action: "reconnect", actionLabel: "Retry engineering API connection"
    },
    {
      id: "project", label: "Open an authorized project",
      detail: state.projectCount > 0
        ? "Choose a project in Explorer. Your permissions come from its server-side KIDE role policy."
        : state.canCreateProject
          ? "No projects are listed yet. Create a hosted project in Explorer, then open it."
          : "No authorized projects are listed. Ask the operator for a project role; signing in does not bypass access control.",
      completed: state.projectOpen,
      action: "explorer", actionLabel: state.projectCount > 0
        ? "Open project explorer"
        : state.canCreateProject ? "Create project in Explorer" : "Check project access"
    },
    {
      id: "model", label: "Open a DSL source model",
      detail: state.projectOpen && !state.hasProductionDsl
        ? "Create server-side starter models in the empty hosted project."
        : "Open a production DSL model from Explorer or Quick Open (Ctrl/⌘+P).",
      completed: state.projectOpen && state.hasProductionDsl,
      action: state.projectOpen && !state.hasProductionDsl ? "starter" : "quick-open",
      actionLabel: state.projectOpen && !state.hasProductionDsl
        ? "Initialize guided starter models" : "Quick Open model"
    },
    {
      id: "services", label: "Connect language and collaboration services",
      detail: "Xtext and collaboration need an open, authorized project. GLSP opens with a supported Activity or MNC diagram.",
      completed: state.projectOpen && state.xtextConnected && state.collaborationConnected,
      action: "services", actionLabel: "Reconnect editor services"
    }
  ];
}

export function firstRunProgress(state: FirstRunState): number {
  return firstRunSteps(state).filter((step) => step.completed).length;
}
