import { expect, test } from "@playwright/test";

// Regression: project listing is a tenant boundary. A reachable API does not
// authorize a project; operator role grants take effect after reconnect.
test("recovers from missing role binding and starts real UI service sessions", async ({ page }) => {
  let engineerGranted = false;
  let lspInitialize = 0;
  let collaborationJoins = 0;
  const project = {
    id: "P04-001",
    displayName: "Engineering Project",
    revision: "1",
    portfolioId: "PF04-1",
    workspaceId: "W04-001"
  };

  await page.routeWebSocket("**/lsp?*", (ws) => {
    ws.onMessage((message) => {
      const parsed = JSON.parse(String(message));
      if (parsed.method === "initialize") {
        lspInitialize += 1;
        ws.send(JSON.stringify({
          jsonrpc: "2.0", id: parsed.id,
          result: { capabilities: { completionProvider: {}, documentSymbolProvider: true } }
        }));
      } else if (typeof parsed.id === "number") {
        ws.send(JSON.stringify({ jsonrpc: "2.0", id: parsed.id, result: null }));
      }
    });
  });

  await page.route("**/v1/accounts:signInWithPassword?*", (route) => route.fulfill({
    status: 200, contentType: "application/json",
    body: JSON.stringify({
      localId: "explicit-engineer-uid", email: "engineer@example.test",
      idToken: "test-firebase-jwt", refreshToken: "test-refresh-token", expiresIn: "3600"
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
      productLine: "1.0", productVersion: "1.0.0.test", buildId: "pr92-connect"
    })
  }));
  await page.route("**/api/v1/projects", (route) => route.fulfill({
    status: engineerGranted ? 200 : 403,
    contentType: "application/json",
    body: engineerGranted ? JSON.stringify({ items: [project] }) : JSON.stringify({
      error: {
        code: "FORBIDDEN", message: "The requested operation is not permitted.",
        requestId: "27f6de90-a786-4db2-86d0-b8fa5fd8e580"
      }
    })
  }));
  await page.route("**/api/v1/projects/P04-001", (route) => route.fulfill({
    status: 200, contentType: "application/json", body: JSON.stringify(project)
  }));
  await page.route("**/api/v1/projects/P04-001/models", (route) => route.fulfill({
    status: 200, contentType: "application/json",
    body: JSON.stringify({ items: [{
      id: "workflow.activity", revision: "1", etag: "a".repeat(64),
      mediaType: "text/x-kide-activity"
    }] })
  }));
  await page.route("**/api/v1/projects/P04-001/models/workflow.activity", (route) => route.fulfill({
    status: 200, contentType: "application/json",
    body: JSON.stringify({
      id: "workflow.activity", revision: "1", etag: "a".repeat(64),
      mediaType: "text/x-kide-activity",
      content: "Workflow Engineering { activity Observe requires Detect }"
    })
  }));
  await page.route("**/api/v1/projects/P04-001/collaboration/sessions*", async (route) => {
    if (route.request().method() === "POST") collaborationJoins += 1;
    const session = {
      id: "11111111-1111-4111-8111-111111111111",
      principalId: "firebase:kide-playwright#explicit-engineer-uid",
      displayName: "Engineer", modelId: "", joinedAt: "2026-10-10T00:00:00Z",
      lastSeenAt: "2026-10-10T00:00:00Z"
    };
    await route.fulfill({
      status: 200, contentType: "application/json",
      body: JSON.stringify(route.request().method() === "GET" ? { items: [session] } : session)
    });
  });
  await page.route("**/api/v1/projects/P04-001/reviews/changesets*", (route) => route.fulfill({
    status: 200, contentType: "application/json", body: JSON.stringify({ items: [] })
  }));
  await page.route("**/api/v1/projects/P04-001/knowledge/traces*", (route) => route.fulfill({
    status: 200, contentType: "application/json",
    body: JSON.stringify({ revision: 0, etag: "0".repeat(64), links: [] })
  }));

  await page.goto("/");
  await page.getByLabel("Firebase email").fill("engineer@example.test");
  await page.getByLabel("Firebase password").fill("password");
  await page.getByRole("button", { name: "Sign in with Firebase" }).click();
  await expect(page.getByText("engineer@example.test", { exact: true })).toBeVisible();
  await page.getByRole("link", { name: "Open Engineering Workspace" }).click();

  await expect(page.getByLabel("Workbench status")).toContainText("API Unauthorized");
  await expect(page.getByLabel("Workbench status")).toContainText("Xtext Waiting");
  expect(lspInitialize).toBe(0);
  expect(collaborationJoins).toBe(0);

  // Represents a real operator-controlled grant. Never grant on sign-in.
  engineerGranted = true;
  await page.locator(".activity-bar").getByRole("button", { name: "Settings", exact: true }).click();
  await page.getByRole("button", { name: "Reconnect API" }).click();

  await expect(page.getByLabel("Workbench status")).toContainText("API Online");
  await expect(page.getByLabel("Workbench status")).toContainText("Xtext Online");
  await expect(page.getByLabel("Workbench status")).toContainText("Collaboration Online");
  await expect(page.locator(".editor-tabs")).toContainText("workflow.activity");
  await expect.poll(() => lspInitialize).toBeGreaterThan(0);
  await expect.poll(() => collaborationJoins).toBeGreaterThan(0);
});
