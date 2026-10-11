import { expect, test } from "@playwright/test";

test("a delayed model GET cannot steal the selected editor tab", async ({ page }) => {
  const project = {
    id: "P98-MODEL", displayName: "Model Intent Project",
    workspaceId: "W98-MODEL", portfolioId: "PF98", revision: "1"
  };
  const models = ["alpha.dml", "beta.dml", "gamma.dml"];
  let releaseBeta!: () => void;
  let signalBetaRequest!: () => void;
  const betaGate = new Promise<void>((resolve) => { releaseBeta = resolve; });
  const betaRequested = new Promise<void>((resolve) => { signalBetaRequest = resolve; });

  await page.routeWebSocket("**/lsp?*", (ws) => {
    ws.onMessage((message) => {
      const request = JSON.parse(String(message));
      if (request.method === "initialize") {
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
      localId: "engineer-98", email: "engineer@example.test",
      idToken: "jwt-98", refreshToken: "refresh-98", expiresIn: "3600"
    })
  }));
  await page.route("**/api/v1/health", (route) => route.fulfill({
    status: 200, contentType: "application/json",
    body: JSON.stringify({ status: "UP", version: "v1", projectCreationEnabled: false })
  }));
  await page.route("**/api/v1/version", (route) => route.fulfill({
    status: 200, contentType: "application/json",
    body: JSON.stringify({
      apiVersion: "v1", engineeringCompatibilityLevel: 1,
      projectSchemaVersion: 1, sharedKernelSchemaVersion: 1,
      productLine: "1.0", productVersion: "1.0.0.test", buildId: "pr98"
    })
  }));
  await page.route("**/api/v1/projects", (route) => route.fulfill({
    status: 200, contentType: "application/json",
    body: JSON.stringify({ items: [project] })
  }));
  await page.route("**/api/v1/projects/P98-MODEL", (route) => route.fulfill({
    status: 200, contentType: "application/json", body: JSON.stringify(project)
  }));
  await page.route("**/api/v1/projects/P98-MODEL/models", (route) => route.fulfill({
    status: 200, contentType: "application/json",
    body: JSON.stringify({ items: models.map((id) => ({
      id, revision: "1", etag: "a".repeat(64), mediaType: "text/x-kide-dml"
    })) })
  }));
  for (const id of models) {
    await page.route(`**/api/v1/projects/P98-MODEL/models/${id}`, async (route) => {
      if (id === "beta.dml") {
        signalBetaRequest();
        await betaGate;
      }
      await route.fulfill({
        status: 200, contentType: "application/json",
        body: JSON.stringify({
          id, revision: "1", etag: "a".repeat(64),
          mediaType: "text/x-kide-dml",
          content: `DataModel ${id.split(".")[0]} {}`
        })
      });
    });
  }
  const session = {
    id: "11111111-1111-4111-8111-111111111111",
    principalId: "engineer-98", displayName: "Engineer",
    joinedAt: "2026-10-11T00:00:00Z", lastSeenAt: "2026-10-11T00:00:00Z"
  };
  await page.route("**/api/v1/projects/P98-MODEL/collaboration/sessions", (route) =>
    route.fulfill({
      status: 200, contentType: "application/json",
      body: JSON.stringify(route.request().method() === "GET" ? { items: [session] } : session)
    })
  );
  await page.route("**/api/v1/projects/P98-MODEL/collaboration/sessions/*", (route) =>
    route.fulfill({
      status: 200, contentType: "application/json", body: JSON.stringify(session)
    })
  );
  await page.route("**/api/v1/projects/P98-MODEL/reviews/changesets*", (route) =>
    route.fulfill({ status: 200, contentType: "application/json", body: JSON.stringify({ items: [] }) })
  );
  await page.route("**/api/v1/projects/P98-MODEL/knowledge/traces*", (route) =>
    route.fulfill({
      status: 200, contentType: "application/json",
      body: JSON.stringify({ revision: 0, etag: "0".repeat(64), links: [] })
    })
  );

  await page.goto("/");
  await page.getByLabel("Firebase email").fill("engineer@example.test");
  await page.getByLabel("Firebase password").fill("password");
  await page.getByRole("button", { name: "Sign in with Firebase" }).click();
  await page.getByRole("link", { name: "Open Engineering Workspace" }).click();
  await expect(page.locator(".editor-tabs")).toContainText("alpha.dml");

  const files = page.getByRole("list", { name: "Project files" });
  await files.getByRole("button", { name: "beta.dml" }).click();
  await betaRequested;
  await files.getByRole("button", { name: "gamma.dml" }).click();
  await expect(page.getByRole("tab", { name: /gamma.dml/ })).toHaveAttribute("aria-selected", "true");

  const betaResponse = page.waitForResponse((response) =>
    response.url().endsWith("/api/v1/projects/P98-MODEL/models/beta.dml") &&
    response.status() === 200
  );
  releaseBeta();
  await betaResponse;
  await expect(page.getByRole("tab", { name: /gamma.dml/ })).toHaveAttribute("aria-selected", "true");
  await expect(page.locator(".editor-tabs")).not.toContainText("beta.dml");
});
