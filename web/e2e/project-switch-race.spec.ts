import { expect, test } from "@playwright/test";

test("switching projects cancels a stalled Xtext handshake without reviving the old workspace", async ({ page }) => {
  let oldInitialize = 0;
  let newInitialize = 0;
  let oldPresenceJoins = 0;
  let newPresenceJoins = 0;
  const first = {
    id: "P04-FIRST", displayName: "First Engineering Project",
    portfolioId: "PF04", workspaceId: "W04-FIRST", revision: "1"
  };
  const second = {
    id: "P04-SECOND", displayName: "Second Engineering Project",
    portfolioId: "PF04", workspaceId: "W04-SECOND", revision: "1"
  };

  await page.routeWebSocket("**/lsp?*", (ws) => {
    const isFirst = ws.url().includes("W04-FIRST");
    ws.onMessage((message) => {
      const request = JSON.parse(String(message));
      if (request.method === "initialize") {
        if (isFirst) {
          oldInitialize += 1;
          // Old service is alive but never answers; a project change MUST
          // cancel its handshake without waiting for the LSP timeout.
          return;
        }
        newInitialize += 1;
        ws.send(JSON.stringify({
          jsonrpc: "2.0", id: request.id,
          result: { capabilities: { completionProvider: {}, documentSymbolProvider: true } }
        }));
      } else if (typeof request.id === "number") {
        ws.send(JSON.stringify({ jsonrpc: "2.0", id: request.id, result: null }));
      }
    });
  });

  await page.route("**/v1/accounts:signInWithPassword?*", (route) => route.fulfill({
    status: 200, contentType: "application/json",
    body: JSON.stringify({
      localId: "approved-engineer", email: "engineer@example.test",
      idToken: "jwt-for-two-projects", refreshToken: "refresh", expiresIn: "3600"
    })
  }));
  await page.route("**/api/v1/health", (route) => route.fulfill({
    status: 200, contentType: "application/json",
    body: JSON.stringify({ status: "UP", version: "v1", projectCreationEnabled: false, dependencies: {} })
  }));
  await page.route("**/api/v1/version", (route) => route.fulfill({
    status: 200, contentType: "application/json",
    body: JSON.stringify({
      apiVersion: "v1", engineeringCompatibilityLevel: 1,
      projectSchemaVersion: 1, sharedKernelSchemaVersion: 1,
      productLine: "1.0", productVersion: "1.0.0.test", buildId: "pr94-race"
    })
  }));
  await page.route("**/api/v1/projects", (route) => route.fulfill({
    status: 200, contentType: "application/json",
    body: JSON.stringify({ items: [first, second] })
  }));
  for (const [project, filename] of [
    [first, "first.dml"], [second, "second.dml"]
  ] as const) {
    await page.route(`**/api/v1/projects/${project.id}`, (route) => route.fulfill({
      status: 200, contentType: "application/json", body: JSON.stringify(project)
    }));
    await page.route(`**/api/v1/projects/${project.id}/models`, (route) => route.fulfill({
      status: 200, contentType: "application/json",
      body: JSON.stringify({ items: [{
        id: filename, revision: "1", etag: "a".repeat(64), mediaType: "text/x-kide-dml"
      }] })
    }));
    await page.route(`**/api/v1/projects/${project.id}/models/${filename}`, (route) => route.fulfill({
      status: 200, contentType: "application/json",
      body: JSON.stringify({
        id: filename, revision: "1", etag: "a".repeat(64),
        mediaType: "text/x-kide-dml", content: "DataModel Engineering {}"
      })
    }));
    const session = {
      id: "11111111-1111-4111-8111-111111111111",
      principalId: "approved-engineer", displayName: "Engineer",
      modelId: filename, joinedAt: "2026-10-10T00:00:00Z",
      lastSeenAt: "2026-10-10T00:00:00Z"
    };
    // The generated API uses POST/GET on the collection and PUT/DELETE on
    // /sessions/{id}. Playwright '*' does not cross '/', so mock BOTH routes.
    // An unmocked PUT would 404 and falsely trigger an expired-session rejoin.
    await page.route(`**/api/v1/projects/${project.id}/collaboration/sessions`, (route) => {
      if (route.request().method() === "POST") {
        if (project.id === first.id) oldPresenceJoins += 1;
        else newPresenceJoins += 1;
      }
      return route.fulfill({
        status: 200, contentType: "application/json",
        body: JSON.stringify(route.request().method() === "GET" ? { items: [session] } : session)
      });
    });
    await page.route(`**/api/v1/projects/${project.id}/collaboration/sessions/*`, (route) =>
      route.fulfill({
        status: 200, contentType: "application/json", body: JSON.stringify(session)
      })
    );
    await page.route(`**/api/v1/projects/${project.id}/reviews/changesets*`, (route) => route.fulfill({
      status: 200, contentType: "application/json", body: JSON.stringify({ items: [] })
    }));
    await page.route(`**/api/v1/projects/${project.id}/knowledge/traces*`, (route) => route.fulfill({
      status: 200, contentType: "application/json",
      body: JSON.stringify({ revision: 0, etag: "0".repeat(64), links: [] })
    }));
  }

  await page.goto("/");
  await page.getByLabel("Firebase email").fill("engineer@example.test");
  await page.getByLabel("Firebase password").fill("password");
  await page.getByRole("button", { name: "Sign in with Firebase" }).click();
  await page.getByRole("link", { name: "Open Engineering Workspace" }).click();

  const projects = page.locator(".project-list");
  await expect(projects).toContainText("First Engineering Project");
  await expect(projects).toContainText("Second Engineering Project");
  await projects.locator("li").filter({ hasText: "First Engineering Project" })
    .getByRole("button", { name: "Open" }).click();
  await expect.poll(() => oldInitialize).toBe(1);

  await projects.locator("li").filter({ hasText: "Second Engineering Project" })
    .getByRole("button", { name: "Open" }).click();
  await expect(page.locator(".editor-tabs")).toContainText("second.dml");
  await expect(page.getByLabel("Workbench status")).toContainText("Xtext Online");
  await expect(page.getByLabel("Workbench status")).toContainText("Collaboration Online");
  await expect.poll(() => newInitialize).toBe(1);
  // PR95: one authorized presence join for the active project.
  // Heartbeat/recovery must not issue duplicate POST /sessions calls.
  await expect.poll(() => newPresenceJoins).toBe(1);
  expect(oldPresenceJoins).toBe(0);
  await expect(page.locator(".editor-tabs")).not.toContainText("first.dml");

  // PR97: a review list response for the previously active project must
  // never reappear under a newer project after the user switches workspaces.
  await page.locator(".activity-bar")
    .getByRole("button", { name: "Collaboration", exact: true }).click();
  let releaseReviews!: () => void;
  let reviewsStarted!: () => void;
  const blockedReviews = new Promise<void>((resolve) => { releaseReviews = resolve; });
  const requestStarted = new Promise<void>((resolve) => { reviewsStarted = resolve; });
  await page.route("**/api/v1/projects/P04-SECOND/reviews/changesets*", async (route) => {
    reviewsStarted();
    await blockedReviews;
    await route.fulfill({
      status: 200, contentType: "application/json",
      body: JSON.stringify({ items: [{
        id: "stale-review", modelId: "old-project-review.dml",
        status: "OPEN", authorName: "Old project collaborator"
      }] })
    });
  });
  await page.locator(".collaboration-panel")
    .getByRole("button", { name: "Refresh", exact: true }).click();
  await requestStarted;

  // Project selection belongs to Explorer, not the Collaboration sidebar.
  await page.locator(".activity-bar")
    .getByRole("button", { name: "Explorer", exact: true }).click();
  await projects.locator("li").filter({ hasText: "First Engineering Project" })
    .getByRole("button", { name: "Open" }).click();
  await expect(page.locator(".model-loader")).toContainText("First Engineering Project");
  await page.locator(".activity-bar")
    .getByRole("button", { name: "Collaboration", exact: true }).click();
  releaseReviews();
  await expect(page.locator(".collaboration-panel")).toContainText("No review change sets yet.");
  await expect(page.locator(".collaboration-panel")).not.toContainText("old-project-review.dml");
});
