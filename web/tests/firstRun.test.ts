import { describe, expect, it } from "vitest";
import { firstRunProgress, firstRunSteps, type FirstRunState } from "../src/firstRun";

const empty: FirstRunState = {
  signedIn: false,
  apiCompatible: false,
  projectCount: 0,
  projectOpen: false,
  canCreateProject: false,
  hasProductionDsl: false,
  xtextConnected: false,
  collaborationConnected: false
};

describe("PR106 first-run engineering journey", () => {
  it("never treats a signed-out or unauthorized API as a completed engineering workspace", () => {
    expect(firstRunProgress(empty)).toBe(0);
    const signedIn = { ...empty, signedIn: true };
    expect(firstRunProgress(signedIn)).toBe(1);
    const denied = firstRunSteps({ ...signedIn, apiCompatible: true });
    expect(denied[1].completed).toBe(true);
    expect(denied[2].completed).toBe(false);
    expect(denied[2].detail).toMatch(/operator.*project role/i);
    expect(denied[2].actionLabel).toBe("Check project access");
    expect(denied[4].completed).toBe(false);
  });

  it("offers actual hosted project creation only when the API advertises it", () => {
    const ableToCreate = firstRunSteps({
      ...empty, signedIn: true, apiCompatible: true, canCreateProject: true
    });
    expect(ableToCreate[2].action).toBe("explorer");
    expect(ableToCreate[2].actionLabel).toBe("Create project in Explorer");

    const existing = firstRunSteps({
      ...empty, signedIn: true, apiCompatible: true, projectCount: 2
    });
    expect(existing[2].actionLabel).toBe("Open project explorer");
  });

  it("gates starter models on an opened project with no production DSL", () => {
    const project = {
      ...empty, signedIn: true, apiCompatible: true, projectCount: 1,
      projectOpen: true
    };
    const steps = firstRunSteps(project);
    expect(firstRunProgress(project)).toBe(3);
    expect(steps[3].completed).toBe(false);
    expect(steps[3].action).toBe("starter");
    expect(steps[3].actionLabel).toBe("Initialize guided starter models");

    const withDsl = firstRunSteps({ ...project, hasProductionDsl: true });
    expect(withDsl[3].completed).toBe(true);
    expect(withDsl[3].action).toBe("quick-open");
  });

  it("requires Xtext and collaboration before claiming engineering services are ready", () => {
    const models = {
      ...empty, signedIn: true, apiCompatible: true, projectCount: 1,
      projectOpen: true, hasProductionDsl: true
    };
    expect(firstRunProgress(models)).toBe(4);
    expect(firstRunProgress({ ...models, xtextConnected: true })).toBe(4);
    expect(firstRunProgress({ ...models, collaborationConnected: true })).toBe(4);
    const ready = { ...models, xtextConnected: true, collaborationConnected: true };
    expect(firstRunProgress(ready)).toBe(5);
    expect(firstRunSteps(ready).every(step => step.completed)).toBe(true);
  });

  it("never enables language readiness on an unopened project", () => {
    const stale = {
      ...empty, signedIn: true, apiCompatible: true,
      hasProductionDsl: true, xtextConnected: true, collaborationConnected: true
    };
    expect(firstRunSteps(stale)[4].completed).toBe(false);
    expect(firstRunProgress(stale)).toBe(2);
  });
});
