import { expect, test } from "@playwright/test";

test("opens a project and connects Monaco to the shared Xtext LSP boundary", async ({ page }) => {
  let initializeRootUri = "";
  let didOpenUri = "";

  await page.routeWebSocket("**/lsp?*", (ws) => {
    ws.onMessage((message) => {
      const request = JSON.parse(String(message));
      if (request.method === "initialize") {
        initializeRootUri = request.params.rootUri;
        ws.send(JSON.stringify({
          jsonrpc: "2.0",
          id: request.id,
          result: {
            capabilities: {
              completionProvider: {},
              hoverProvider: true,
              definitionProvider: true,
              referencesProvider: true,
              documentSymbolProvider: true,
              workspaceSymbolProvider: true,
              documentFormattingProvider: true,
              renameProvider: true
            }
          }
        }));
        return;
      }
      if (request.method === "textDocument/didOpen") {
        didOpenUri = request.params.textDocument.uri;
        return;
      }
      if (request.method === "workspace/symbol") {
        ws.send(JSON.stringify({
          jsonrpc: "2.0",
          id: request.id,
          result: [{
            name: "SelfCheck",
            kind: 5,
            containerName: "DML",
            location: {
              uri: "kide-workspace:/selfcheck.dml",
              range: {
                start: { line: 0, character: 7 },
                end: { line: 0, character: 16 }
              }
            }
          }]
        }));
        return;
      }
      if (typeof request.id === "number") {
        ws.send(JSON.stringify({
          jsonrpc: "2.0",
          id: request.id,
          result: null
        }));
      }
    });
  });

  await page.route("**/api/v1/health", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({ status: "UP", version: "v1", dependencies: {} })
    });
  });
  await page.route("**/api/v1/projects", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        items: [{
          id: "P04-001",
          displayName: "Golden Project",
          revision: "1",
          portfolioId: "PF04-1",
          workspaceId: "W04-001"
        }]
      })
    });
  });
  await page.route("**/api/v1/projects/P04-001", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        id: "P04-001",
        displayName: "Golden Project",
        revision: "1",
        portfolioId: "PF04-1",
        workspaceId: "W04-001"
      })
    });
  });
  await page.route(
    "**/api/v1/projects/P04-001/models/selfcheck.dml",
    async (route) => {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          id: "selfcheck.dml",
          content: "DataModel SelfCheck { primitives { int value } }",
          revision: "7",
          etag: "etag-7",
          mediaType: "text/x-kide-dml"
        })
      });
    }
  );

  await page.goto("/");
  await page.getByLabel("Access token").fill("browser-test-token");
  await page.getByRole("button", { name: "Connect API" }).click();
  await expect(page.getByText("UP · API v1")).toBeVisible();
  await expect(page.getByText("Golden Project")).toBeVisible();

  await page.getByRole("button", { name: "Open" }).click();
  await expect(page.getByText("Connected · Xtext LSP")).toBeVisible();
  expect(initializeRootUri).toBe("kide-workspace:/");

  await page.getByLabel("Model ID").fill("selfcheck.dml");
  await page.getByRole("button", { name: "Load model" }).click();
  await expect(page.getByTestId("monaco-editor")).toBeVisible();
  await expect.poll(() => didOpenUri).toBe("kide-workspace:/selfcheck.dml");

  await page.getByLabel("Symbol query").fill("Self");
  await page.getByRole("button", { name: "Search symbols" }).click();
  await expect(page.getByRole("button", { name: /SelfCheck/ })).toBeVisible();
});
