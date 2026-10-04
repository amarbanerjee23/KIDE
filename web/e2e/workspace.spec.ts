import { expect, test } from "@playwright/test";
import { strToU8, zipSync } from "fflate";

test("keeps sign-in on the landing page and engineering actions in the workspace", async ({ page }) => {
  await page.goto("/");

  await expect(
    page.getByRole("heading", {
      name: "Engineer control software from models to generated artifacts."
    })
  ).toBeVisible();
  await expect(page.getByRole("heading", { name: "Sign in to KIDE" })).toBeVisible();
  await expect(page.getByLabel("Firebase email")).toBeVisible();
  await expect(page.getByRole("link", { name: "Open Engineering Workspace" })).toBeVisible();
  await expect(page.locator(".activity-bar")).toHaveCount(0);
  await expect(page.locator(".editor-tabs")).toHaveCount(0);

  await page.getByRole("link", { name: "Open Engineering Workspace" }).click();
  await expect(page).toHaveURL(/\/workspace$/);
  await expect(page.locator(".activity-bar")).toBeVisible();
  await expect(page.locator(".status-bar")).toBeVisible();
  await expect(page.getByLabel("Firebase email")).toHaveCount(0);

  await page
    .locator(".activity-bar")
    .getByRole("button", { name: "Settings", exact: true })
    .click();
  await expect(page.getByText("Authentication required for server engineering")).toBeVisible();
  await expect(page.getByRole("button", { name: "Go to sign in" })).toBeVisible();

  await page.getByRole("button", { name: "Home" }).click();
  await expect(page).toHaveURL(/\/$/);
  await expect(page.getByRole("heading", { name: "Sign in to KIDE" })).toBeVisible();
});


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

  await mockCollaboration(page);

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
    "**/api/v1/projects/P04-001/models",
    async (route) => {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          items: [{
            id: "selfcheck.dml",
            revision: "7",
            etag: "etag-7",
            mediaType: "text/x-kide-dml"
          }]
        })
      });
    }
  );

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

  await page.route(
    "**/api/v1/projects/P04-001/knowledge/query",
    async (route) => {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          revision: 4,
          etag: "a".repeat(64),
          cached: false,
          items: [{
            iri: "urn:kide:capability:Observe",
            label: "Observe",
            types: ["https://kide.dev/ontology/v1#Capability"],
            properties: {
              "https://kide.dev/ontology/v1#role": ["Sensor"]
            },
            provenanceSource: "urn:test:catalogue",
            authority: "KIDE"
          }]
        })
      });
    }
  );

  await signInFirebase(page);
  await expect(page.locator(".status-bar")).toContainText("API Online");
  await expect(
    page.locator(".project-list").getByText("Golden Project", { exact: true })
  ).toBeVisible();

  await page.getByRole("button", { name: "Open", exact: true }).click();
  await expect(page.locator(".status-bar")).toContainText("Xtext Online");
  await expect(page.getByText("1 project file(s) discovered.")).toBeVisible();
  expect(initializeRootUri).toBe("kide-workspace:/");

  await expect(page.getByTestId("monaco-editor")).toBeVisible();
  await expect(page.locator(".editor-tabs")).toContainText("selfcheck.dml");
  await expect(page.locator(".breadcrumbs")).toContainText("selfcheck.dml");
  await expect(page.locator(".status-bar")).toContainText("API Online");
  await expect(page.locator(".status-bar")).toContainText("Xtext Online");

  await page.keyboard.press("F1");
  const palette = page.getByRole("dialog", { name: "Command Palette" });
  await expect(palette).toBeVisible();
  for (const command of [
    "Editor: Trigger Completion",
    "Editor: Go to Definition",
    "Editor: Find All References",
    "Editor: Rename Symbol",
    "Editor: Quick Fix",
    "Editor: Format Document"
  ]) {
    await expect(palette.getByText(command, { exact: true })).toBeVisible();
  }
  await page.keyboard.press("Escape");
  await expect.poll(() => didOpenUri).toBe("kide-workspace:/selfcheck.dml");

  await page
    .locator(".activity-bar")
    .getByRole("button", { name: "Search", exact: true })
    .click();
  await page.getByLabel("Symbol query").fill("Self");
  await page.getByRole("button", { name: "Search symbols" }).click();
  await expect(page.getByRole("button", { name: /SelfCheck/ })).toBeVisible();

  await page
    .locator(".activity-bar")
    .getByRole("button", { name: "Engineering", exact: true })
    .click();
  await page.getByLabel("Knowledge query").fill("Observe");
  await page.getByLabel("Knowledge concept type").selectOption("CAPABILITY");
  await page
    .locator(".knowledge-panel")
    .getByRole("button", { name: "Search", exact: true })
    .click();
  await expect(page.getByRole("button", { name: /Observe/ })).toBeVisible();
});


test("promotes a local ZIP atomically into an empty hosted project and attaches engineering services", async ({ page }) => {
  let promoted = false;
  let importCalls = 0;
  let importPayload: any;
  let initializeRootUri = "";

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
              documentSymbolProvider: true,
              completionProvider: {},
              hoverProvider: true,
              definitionProvider: true,
              referencesProvider: true,
              documentFormattingProvider: true,
              renameProvider: true
            }
          }
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

  await mockCollaboration(page);

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
          displayName: "Empty Hosted Target",
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
        displayName: "Empty Hosted Target",
        revision: "1",
        portfolioId: "PF04-1",
        workspaceId: "W04-001"
      })
    });
  });

  await page.route("**/api/v1/projects/P04-001/models", async (route) => {
    const items = promoted ? [{
      id: "models/main.activity",
      revision: "1",
      etag: "a".repeat(64),
      mediaType: "text/x-kide-activity"
    }, {
      id: "bindings.krl",
      revision: "1",
      etag: "b".repeat(64),
      mediaType: "text/x-kide-krl"
    }] : [];
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({ items })
    });
  });

  await page.route(
    "**/api/v1/projects/P04-001/imports/archive",
    async (route) => {
      importCalls += 1;
      importPayload = JSON.parse(route.request().postData() ?? "{}");
      promoted = true;
      await route.fulfill({
        status: 201,
        contentType: "application/json",
        body: JSON.stringify({
          projectId: "P04-001",
          workspaceId: "W04-001",
          importedCount: 2,
          totalBytes: 170,
          items: [{
            id: "models/main.activity",
            revision: "1",
            etag: "a".repeat(64),
            mediaType: "text/x-kide-activity"
          }, {
            id: "bindings.krl",
            revision: "1",
            etag: "b".repeat(64),
            mediaType: "text/x-kide-krl"
          }]
        })
      });
    }
  );

  await page.route(
    "**/api/v1/projects/P04-001/models/*",
    async (route) => {
      const url = decodeURIComponent(route.request().url());
      const activity = url.endsWith("/models/models/main.activity");
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify(activity ? {
          id: "models/main.activity",
          content: "ActivityDiagram Promoted\nhas activities { Activity ObserveStep { nextActivity : ObserveStep } }\n",
          contentBase64: Buffer.from(
            "ActivityDiagram Promoted\nhas activities { Activity ObserveStep { nextActivity : ObserveStep } }\n"
          ).toString("base64"),
          revision: "1",
          etag: "a".repeat(64),
          mediaType: "text/x-kide-activity"
        } : {
          id: "bindings.krl",
          content: "knowledge Promoted {}\n",
          contentBase64: Buffer.from("knowledge Promoted {}\n").toString("base64"),
          revision: "1",
          etag: "b".repeat(64),
          mediaType: "text/x-kide-krl"
        })
      });
    }
  );

  await signInFirebase(page);
  await expect(
    page.locator(".project-list").getByText("Empty Hosted Target", { exact: true })
  ).toBeVisible();

  const archive = zipSync({
    "models/main.activity": strToU8(
      "ActivityDiagram Promoted\nhas activities { Activity ObserveStep { nextActivity : ObserveStep } }\n"
    ),
    "bindings.krl": strToU8("knowledge Promoted {}\n")
  });

  await page
    .locator(".activity-bar")
    .getByRole("button", { name: "Settings", exact: true })
    .click();
  await page.getByLabel("Import project ZIP").setInputFiles({
    name: "promote-me.zip",
    mimeType: "application/zip",
    buffer: Buffer.from(archive)
  });

  await expect(
    page.getByText(/Choose an empty authorized hosted project and select Promote here/i)
  ).toBeVisible();

  await page
    .locator(".activity-bar")
    .getByRole("button", { name: "Explorer", exact: true })
    .click();

  const target = page.locator(".project-list").filter({ hasText: "Empty Hosted Target" });
  await expect(target.getByRole("button", { name: "Promote here", exact: true })).toBeVisible();
  await target.getByRole("button", { name: "Promote here", exact: true }).click();

  await expect.poll(() => importCalls).toBe(1);
  expect(importPayload.archiveName).toBe("promote-me.zip");
  expect(importPayload.items.map((item: any) => item.path).sort()).toEqual([
    "bindings.krl",
    "models/main.activity"
  ]);
  expect(importPayload.items.every((item: any) => typeof item.contentBase64 === "string")).toBe(true);

  await expect(page.locator(".status-bar")).toContainText("Xtext Online");
  await expect.poll(() => initializeRootUri).toBe("kide-workspace:/");
  await expect(page.locator(".file-list")).toContainText("main.activity");
  await expect(page.locator(".file-list")).toContainText("bindings.krl");
  await expect(page.locator(".editor-tabs")).toContainText("main.activity");
  await expect(
    page.getByText(/Promoted 2 file\(s\).*atomically.*engineering services now use the hosted project/i)
  ).toBeVisible();
});


test("refuses local ZIP promotion when the hosted target is not empty", async ({ page }) => {
  let importCalls = 0;

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
          displayName: "Occupied Hosted Target",
          revision: "1",
          workspaceId: "W04-001"
        }]
      })
    });
  });

  await page.route("**/api/v1/projects/P04-001/models", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        items: [{
          id: "existing.dml",
          revision: "4",
          etag: "c".repeat(64),
          mediaType: "text/x-kide-dml"
        }]
      })
    });
  });

  await page.route(
    "**/api/v1/projects/P04-001/imports/archive",
    async (route) => {
      importCalls += 1;
      await route.fulfill({ status: 500, body: "must not be called" });
    }
  );

  await signInFirebase(page);

  const archive = zipSync({
    "model.dml": strToU8("DataModel Local { primitives { int value } }")
  });
  await page
    .locator(".activity-bar")
    .getByRole("button", { name: "Settings", exact: true })
    .click();
  await page.getByLabel("Import project ZIP").setInputFiles({
    name: "occupied.zip",
    mimeType: "application/zip",
    buffer: Buffer.from(archive)
  });
  await page
    .locator(".activity-bar")
    .getByRole("button", { name: "Explorer", exact: true })
    .click();

  const target = page.locator(".project-list").filter({ hasText: "Occupied Hosted Target" });
  await target.getByRole("button", { name: "Promote here", exact: true }).click();

  await expect(page.getByText(/already contains 1 project file\(s\)/i)).toBeVisible();
  await expect.poll(() => importCalls).toBe(0);
});


test("keeps API reachability distinct from denied project authorization", async ({ page }) => {
  let healthCalls = 0;
  let projectCalls = 0;

  await page.route("**/api/v1/health", async (route) => {
    healthCalls += 1;
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({ status: "UP", version: "v1", dependencies: {} })
    });
  });

  await page.route("**/api/v1/projects", async (route) => {
    projectCalls += 1;
    await route.fulfill({
      status: 403,
      contentType: "application/json",
      body: JSON.stringify({
        apiVersion: "v1",
        requestId: "24f8a8d8-739c-41ec-b291-1299550f4ba9",
        code: "FORBIDDEN",
        message: "The requested operation is not permitted.",
        details: {}
      })
    });
  });

  await signInFirebase(page);
  await expect.poll(() => healthCalls).toBe(1);
  await expect.poll(() => projectCalls).toBe(1);

  await page
    .locator(".activity-bar")
    .getByRole("button", { name: "Settings", exact: true })
    .click();

  await expect(page.locator(".connection-summary")).toContainText(
    "Connected · API v1 · authorization required"
  );
  await expect(page.getByLabel("KIDE authorization identity")).toContainText(
    "firebase:kide-playwright#firebase-browser-user"
  );
  await expect(page.getByText(/API is reachable and compatible, but firebase:kide-playwright#firebase-browser-user/))
    .toBeVisible();
  await expect(page.getByText(/Grant an explicit ENGINEER or ADMINISTRATOR role binding/))
    .toBeVisible();
  await expect(page.getByText(/24f8a8d8-739c-41ec-b291-1299550f4ba9/))
    .toBeVisible();

  await page.getByRole("button", { name: "Reconnect API", exact: true }).click();
  await expect.poll(() => healthCalls).toBe(2);
  await expect.poll(() => projectCalls).toBe(2);
  await expect(page.locator(".connection-summary")).toContainText(
    "Connected · API v1 · authorization required"
  );
});


test("blocks hosted engineering when runtime compatibility does not match", async ({ page }) => {
  let projectCalls = 0;

  await page.route("**/api/v1/health", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({ status: "UP", version: "v1", dependencies: {} })
    });
  });

  await page.route("**/api/v1/projects", async (route) => {
    projectCalls += 1;
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({ items: [] })
    });
  });

  await signInFirebase(page, {
    apiVersion: "v1",
    engineeringCompatibilityLevel: 2,
    projectSchemaVersion: 1,
    sharedKernelSchemaVersion: 1,
    productLine: "1.0",
    productVersion: "1.1.0.test",
    buildId: "incompatible-backend"
  });

  await expect.poll(() => projectCalls).toBe(0);
  await expect(page.locator(".status-bar")).toContainText("API Incompatible");

  await page
    .locator(".activity-bar")
    .getByRole("button", { name: "Settings", exact: true })
    .click();

  await expect(page.getByLabel("Runtime compatibility")).toContainText(
    "incompatible-backend"
  );
  await expect(
    page.getByText(/runtime compatibility check failed/i)
  ).toBeVisible();
  await expect(page.getByText(/engineering level 2 != 1/)).toBeVisible();
  await expect(
    page.locator(".project-list").getByRole("button", { name: "Open", exact: true })
  ).toHaveCount(0);
});


test("opens Activity through the secure GLSP browser boundary", async ({ page }) => {
  let requestedSourceUri = "";
  let requestedDiagramType = "";
  let glspSessionId = "";
  const glspRequests: Array<{ sourceUri: string; diagramType: string }> = [];
  let synthesisCalls = 0;
  let generationCalls = 0;
  let reconfigurationCalls = 0;
  const reconfigurationRequests: unknown[] = [];

  await page.routeWebSocket("**/lsp?*", (ws) => {
    ws.onMessage((message) => {
      const request = JSON.parse(String(message));
      if (request.method === "initialize") {
        ws.send(JSON.stringify({
          jsonrpc: "2.0",
          id: request.id,
          result: { capabilities: {} }
        }));
      } else if (typeof request.id === "number") {
        ws.send(JSON.stringify({
          jsonrpc: "2.0",
          id: request.id,
          result: null
        }));
      }
    });
  });

  await page.routeWebSocket("**/glsp?*", (ws) => {
    ws.onMessage((message) => {
      const request = JSON.parse(String(message));
      if (request.method === "initialize") {
        ws.send(JSON.stringify({
          jsonrpc: "2.0",
          id: request.id,
          result: {
            protocolVersion: "1.0.0",
            serverActions: {
              "kide-activity-diagram": [
                "requestModel",
                "requestTypeHints",
                "createNode",
                "createEdge",
                "applyLabelEdit",
                "deleteElement",
                "changeBounds",
                "glspUndo",
                "glspRedo",
                "saveModel"
              ],
              "kide-mnc-diagram": [
                "requestModel",
                "requestTypeHints",
                "createNode",
                "createEdge",
                "applyLabelEdit",
                "deleteElement",
                "changeBounds",
                "glspUndo",
                "glspRedo",
                "saveModel"
              ]
            }
          }
        }));
        return;
      }
      if (request.method === "initializeClientSession") {
        glspSessionId = request.params.clientSessionId;
        ws.send(JSON.stringify({
          jsonrpc: "2.0",
          id: request.id,
          result: null
        }));
        return;
      }
      if (request.method === "disposeClientSession") {
        ws.send(JSON.stringify({
          jsonrpc: "2.0",
          id: request.id,
          result: null
        }));
        return;
      }
      if (request.method !== "process") return;

      const action = request.params.action;
      const clientId = request.params.clientId;
      if (action.kind === "requestModel") {
        requestedSourceUri = action.options.sourceUri;
        requestedDiagramType = action.options.diagramType;
        glspRequests.push({
          sourceUri: requestedSourceUri,
          diagramType: requestedDiagramType
        });
        const mnc = requestedDiagramType === "kide-mnc-diagram";
        ws.send(JSON.stringify({
          jsonrpc: "2.0",
          method: "process",
          params: {
            clientId,
            action: {
              kind: "setModel",
              responseId: action.requestId,
              newRoot: {
                id: "root",
                type: "graph",
                children: [{
                  id: mnc ? "//@states.0" : "//@activities.0",
                  type: mnc ? "kide:mnc-operating-state" : "kide:activity",
                  position: { x: 80, y: 80 },
                  size: { width: 180, height: 72 },
                  children: [{
                    id: mnc ? "//@states.0_label" : "//@activities.0_label",
                    type: "label",
                    text: mnc ? "ReadyState" : "ObserveStep"
                  }]
                }]
              }
            }
          }
        }));
        return;
      }
      if (action.kind === "requestTypeHints") {
        ws.send(JSON.stringify({
          jsonrpc: "2.0",
          method: "process",
          params: {
            clientId,
            action: {
              kind: "setTypeHints",
              responseId: action.requestId,
              shapeHints: [{
                elementTypeId: requestedDiagramType === "kide-mnc-diagram" ? "kide:mnc-operating-state" : "kide:activity",
                repositionable: true,
                deletable: true,
                resizable: true,
                reparentable: false,
                containableElementTypeIds: []
              }],
              edgeHints: [{
                elementTypeId: requestedDiagramType === "kide-mnc-diagram" ? "kide:mnc-state-transition" : "kide:activity-next",
                repositionable: false,
                deletable: true,
                routable: false,
                sourceElementTypeIds: [requestedDiagramType === "kide-mnc-diagram" ? "kide:mnc-operating-state" : "kide:activity"],
                targetElementTypeIds: [requestedDiagramType === "kide-mnc-diagram" ? "kide:mnc-operating-state" : "kide:activity"]
              }]
            }
          }
        }));
        return;
      }
      if (action.kind === "requestMarkers") {
        ws.send(JSON.stringify({
          jsonrpc: "2.0",
          method: "process",
          params: {
            clientId,
            action: {
              kind: "setMarkers",
              responseId: action.requestId,
              reason: "batch",
              markers: []
            }
          }
        }));
      }
    });
  });

  await mockCollaboration(page);

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
        workspaceId: "W04-001"
      })
    });
  });
  await page.route(
    "**/api/v1/projects/P04-001/models",
    async (route) => {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          items: [
            {
              id: "flow.activity",
              revision: "4",
              etag: "etag-4",
              mediaType: "text/x-kide-activity"
            },
            {
              id: "controller.mncspec",
              revision: "3",
              etag: "m".repeat(64),
              mediaType: "text/x-kide-mnc"
            },
            {
              id: "bindings.krl",
              revision: "2",
              etag: "d".repeat(64),
              mediaType: "text/x-kide-krl"
            }
          ]
        })
      });
    }
  );

  await page.route(
    "**/api/v1/projects/P04-001/models/flow.activity",
    async (route) => {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          id: "flow.activity",
          content: "ActivityDiagram GoldenWorkflow\nhas activities {\n  Activity ObserveStep {\n    requireCapability : Observe { Start, Ready }\n    nextActivity : ObserveStep\n  }\n}\n",
          revision: "4",
          etag: "etag-4",
          mediaType: "text/x-kide-activity"
        })
      });
    }
  );

  await page.route(
    "**/api/v1/projects/P04-001/models/controller.mncspec",
    async (route) => {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          id: "controller.mncspec",
          content: "Model GoldenController\nState ReadyState {}\n",
          revision: "3",
          etag: "m".repeat(64),
          mediaType: "text/x-kide-mnc"
        })
      });
    }
  );

  await page.route(
    "**/api/v1/projects/P04-001/models/bindings.krl",
    async (route) => {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          id: "bindings.krl",
          content: "knowledge BrowserBindings { template T(name: string) for java body \"class ${name} {}\"; target Out type java { template T; output \"generated/Out.java\"; bind name: string = string \"Out\"; } }",
          revision: "2",
          etag: "d".repeat(64),
          mediaType: "text/x-kide-krl"
        })
      });
    }
  );

  await page.route(
    "**/api/v1/projects/P04-001/synthesis",
    async (route) => {
      synthesisCalls += 1;
      const request = route.request();
      const body = request.postDataJSON();
      expect(body).toEqual({
        modelId: "flow.activity",
        modelRevision: "etag-4"
      });
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          resultId: "syn-browser",
          serviceVersion: "1",
          status: "SUCCESS",
          modelId: "flow.activity",
          modelVersion: "4",
          revision: "etag-4",
          knowledgeRevision: 2,
          knowledgeEtag: "b".repeat(64),
          fingerprint: "c".repeat(64),
          selections: [{
            requirementId: "activity:ObserveStep",
            activityName: "ObserveStep",
            capabilityName: "Observe",
            resourceId: "urn:kide:device:camera",
            rationale: "priority=10"
          }],
          diagnostics: [],
          rationale: ["ObserveStep -> urn:kide:device:camera"],
          generatedMnc: "Model GoldenWorkflow\nInterfaceDescription GoldenWorkflow {}\n"
        })
      });
    }
  );

  await page.route(
    "**/api/v1/projects/P04-001/generation",
    async (route) => {
      generationCalls += 1;
      const body = route.request().postDataJSON();
      expect(body).toEqual({
        sourceModelId: "flow.activity",
        sourceRevision: "etag-4",
        krlModelId: "bindings.krl",
        krlRevision: "d".repeat(64),
        synthesisFingerprint: "c".repeat(64)
      });
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          resultId: "gen-browser",
          toolchainVersion: "1",
          fingerprint: "f".repeat(64),
          sourceModelId: "flow.activity",
          sourceModelVersion: "4",
          sourceRevision: "etag-4",
          krlModelId: "bindings.krl",
          krlModelVersion: "2",
          krlRevision: "d".repeat(64),
          knowledgeRevision: 2,
          knowledgeEtag: "b".repeat(64),
          synthesisFingerprint: "c".repeat(64),
          manifestJson: JSON.stringify({
            schemaVersion: "1",
            toolchainVersion: "1",
            fingerprint: "f".repeat(64),
            source: {
              modelId: "flow.activity",
              revision: "etag-4",
              etag: "etag-4"
            },
            knowledge: { revision: 2, etag: "b".repeat(64) },
            synthesis: { fingerprint: "c".repeat(64) },
            krl: {
              modelId: "bindings.krl",
              revision: "d".repeat(64),
              etag: "d".repeat(64)
            },
            targetVersions: { java: "1" },
            artifacts: [{
              path: "generated/Out.java",
              mediaType: "text/x-java-source",
              bytes: 19,
              sha256: "2cb77ee9942215f8c3ec544036efa5f9cf5dd214b2f3678c549bafa4f70b4b31",
              targetId: "java",
              targetVersion: "1",
              targetName: "Java",
              templateName: "reference"
            }]
          }),
          artifacts: [{
            path: "generated/Out.java",
            mediaType: "text/x-java-source",
            contentBase64: "cHVibGljIGNsYXNzIE91dCB7fQ==",
            sha256: "2cb77ee9942215f8c3ec544036efa5f9cf5dd214b2f3678c549bafa4f70b4b31",
            targetId: "java",
            targetVersion: "1"
          }]
        })
      });
    }
  );

  await page.route(
    "**/api/v1/projects/P04-001/reconfiguration",
    async (route) => {
      reconfigurationCalls += 1;
      reconfigurationRequests.push(route.request().postDataJSON());
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          resultId: "reconf-browser",
          serviceVersion: "1",
          status: "RECONFIGURED",
          cause: "RESOURCE_LOSS",
          modelId: "flow.activity",
          modelVersion: "4",
          revision: "etag-4",
          knowledgeRevision: 3,
          knowledgeEtag: "d".repeat(64),
          fingerprint: "e".repeat(64),
          selections: [{
            requirementId: "activity:ObserveStep",
            activityName: "ObserveStep",
            capabilityName: "Observe",
            resourceId: "urn:kide:device:camera-b",
            rationale: "priority=5"
          }],
          migrations: [{
            requirementId: "activity:ObserveStep",
            policy: "MIGRATE",
            fromResourceId: "urn:kide:device:camera",
            toResourceId: "urn:kide:device:camera-b",
            reason: "Equivalent capability binding changed; migrate supervisory state."
          }],
          diagnostics: []
        })
      });
    }
  );

  await signInFirebase(page);
  await page.getByRole("button", { name: "Open", exact: true }).click();
  await expect(page.locator(".file-list").getByText("flow.activity", { exact: true })).toBeVisible();
  await expect(page.locator(".file-list").getByText("controller.mncspec", { exact: true })).toBeVisible();
  await expect(page.getByTestId("monaco-editor")).toBeVisible();
  await expect(page.locator(".status-bar")).toContainText("Collaboration Online");

  const workflow = page.getByLabel("Engineering workflow");
  await expect(workflow.getByRole("button", { name: "Visualize flow", exact: true })).toBeEnabled();
  await expect(workflow.getByRole("button", { name: "Synthesize", exact: true })).toBeEnabled();
  await expect(workflow.getByRole("button", { name: "State machine", exact: true })).toBeEnabled();
  await expect(workflow.getByRole("button", { name: "Reconfigure", exact: true })).toBeDisabled();
  await expect(workflow.getByRole("button", { name: "Generate code", exact: true })).toBeDisabled();

  // Persistent workflow surface: Activity GLSP visualization.
  await workflow.getByRole("button", { name: "Visualize flow", exact: true }).click();
  await expect(page.getByTestId("glsp-editor")).toBeVisible();
  await expect(page.getByTestId("glsp-editor").getByText("ObserveStep", { exact: true })).toBeVisible();
  await expect(page.getByText("Connected · Eclipse GLSP graphical model")).toBeVisible();
  expect(glspRequests.at(-1)).toEqual({
    sourceUri: "kide-workspace:/flow.activity",
    diagramType: "kide-activity-diagram"
  });

  // Return to text and open the engineering cockpit.
  await page.getByRole("button", { name: "Text", exact: true }).click();
  await workflow.getByRole("button", { name: "Engineering flow", exact: true }).click();
  const cockpit = page.getByLabel("Engineering cockpit");
  await expect(cockpit).toBeVisible();
  await expect(cockpit.getByRole("button", { name: "Synthesize", exact: true })).toBeEnabled();

  // Cockpit surface must invoke the exact same synthesis service contract.
  await cockpit.getByRole("button", { name: "Synthesize", exact: true }).click();
  await expect.poll(() => synthesisCalls).toBe(1);
  await expect(page.getByLabel("Synthesis result")).toContainText("SUCCESS");
  await expect(page.getByLabel("Synthesis result")).toContainText("urn:kide:device:camera");
  await expect(page.getByText("Generated MNC / synthesized control model")).toBeVisible();

  // The persistent workflow remains wired to the same service and can repeat deterministically.
  await workflow.getByRole("button", { name: "Synthesize", exact: true }).click();
  await expect.poll(() => synthesisCalls).toBe(2);
  await expect(page.getByLabel("Synthesis result")).toContainText("SUCCESS");

  await expect(workflow.getByRole("button", { name: "Reconfigure", exact: true })).toBeEnabled();
  await expect(workflow.getByRole("button", { name: "Generate code", exact: true })).toBeEnabled();

  // Cockpit generation surface.
  await expect(page.getByLabel("KRL model ID")).toHaveValue("bindings.krl");
  await page.getByLabel("Generation result").getByRole("button", { name: "Generate code", exact: true }).click();
  await expect.poll(() => generationCalls).toBe(1);
  await expect(page.getByLabel("Generation result")).toContainText("Generated 1 artifact");
  await expect(page.getByLabel("Generation result")).toContainText("generated/Out.java");
  await expect(page.getByLabel("Generation result")).toContainText("public class Out {}");
  await expect(page.getByText("Generation manifest")).toBeVisible();

  // Persistent generation surface must hit the same contract as the cockpit.
  await workflow.getByRole("button", { name: "Generate code", exact: true }).click();
  await expect.poll(() => generationCalls).toBe(2);

  // Cockpit reconfiguration surface.
  await page.getByLabel("Reconfiguration cause").selectOption("RESOURCE_LOSS");
  await expect(page.getByLabel("Reconfiguration cause")).toHaveValue("RESOURCE_LOSS");
  await page.getByLabel("Synthesis result").getByRole("button", { name: "Reconfigure", exact: true }).click();
  await expect.poll(() => reconfigurationCalls).toBe(1);
  await expect(page.getByLabel("Reconfiguration result")).toContainText("RECONFIGURED");
  await expect(page.getByLabel("Reconfiguration result")).toContainText("urn:kide:device:camera-b");
  await expect(page.getByLabel("Reconfiguration result")).toContainText("MIGRATE");

  // Persistent reconfiguration surface must submit the identical previous binding contract.
  await workflow.getByRole("button", { name: "Reconfigure", exact: true }).click();
  await expect.poll(() => reconfigurationCalls).toBe(2);
  expect(reconfigurationRequests).toEqual([
    {
      modelId: "flow.activity",
      modelRevision: "etag-4",
      cause: "RESOURCE_LOSS",
      previousBindings: [{
        requirementId: "activity:ObserveStep",
        resourceId: "urn:kide:device:camera"
      }]
    },
    {
      modelId: "flow.activity",
      modelRevision: "etag-4",
      cause: "RESOURCE_LOSS",
      previousBindings: [{
        requirementId: "activity:ObserveStep",
        resourceId: "urn:kide:device:camera"
      }]
    }
  ]);

  // State-machine visualization must use the MNC GLSP diagram type and canonical project URI.
  await cockpit.getByRole("button", { name: "Visualize state machine", exact: true }).click();
  await expect(page.getByTestId("glsp-editor")).toBeVisible();
  await expect(page.getByTestId("glsp-editor").getByText("ReadyState", { exact: true })).toBeVisible();
  expect(glspRequests.at(-1)).toEqual({
    sourceUri: "kide-workspace:/controller.mncspec",
    diagramType: "kide-mnc-diagram"
  });

  expect(glspSessionId).not.toBe("");
  expect(requestedSourceUri).toBe("kide-workspace:/controller.mncspec");
  expect(requestedDiagramType).toBe("kide-mnc-diagram");
});


test("creates starter engineering models for an empty hosted project", async ({ page }) => {
  await page.routeWebSocket("**/lsp?*", (ws) => {
    ws.onMessage((message) => {
      const request = JSON.parse(String(message));
      if (request.method === "initialize") {
        ws.send(JSON.stringify({
          jsonrpc: "2.0",
          id: request.id,
          result: {
            capabilities: {
              completionProvider: {},
              hoverProvider: true,
              definitionProvider: true,
              referencesProvider: true,
              documentFormattingProvider: true,
              renameProvider: true
            }
          }
        }));
      } else if (typeof request.id === "number") {
        ws.send(JSON.stringify({
          jsonrpc: "2.0",
          id: request.id,
          result: null
        }));
      }
    });
  });

  await mockCollaboration(page);

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
          displayName: "Empty Cloud Project",
          revision: "1",
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
        displayName: "Empty Cloud Project",
        revision: "1",
        workspaceId: "W04-001"
      })
    });
  });

  const starterModels = [
    { id: "starter.dml", mediaType: "text/x-kide-dml" },
    { id: "inspect.op", mediaType: "text/x-kide-operation" },
    { id: "device.mncspec", mediaType: "text/x-kide-mnc" },
    { id: "observe.cap", mediaType: "text/x-kide-capability" },
    { id: "workflow.activity", mediaType: "text/x-kide-activity" },
    { id: "bindings.krl", mediaType: "text/x-kide-krl" }
  ].map((model, index) => ({
    ...model,
    revision: "1",
    etag: String(index + 1).repeat(64).slice(0, 64)
  }));

  let starterCreated = false;
  await page.route("**/api/v1/projects/P04-001/models", async (route) => {
    if (route.request().method() === "POST") {
      starterCreated = true;
      await route.fulfill({
        status: 201,
        contentType: "application/json",
        body: JSON.stringify({ items: starterModels })
      });
      return;
    }
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({ items: [] })
    });
  });

  await page.route(
    "**/api/v1/projects/P04-001/models/workflow.activity",
    async (route) => {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          id: "workflow.activity",
          content: "ActivityDiagram StarterWorkflow\nhas activities {\n  Activity ObserveStep {\n    requireCapability : Observe { Start, Ready }\n    nextActivity : ObserveStep\n  }\n}\n",
          revision: "1",
          etag: starterModels.find((model) => model.id === "workflow.activity")!.etag,
          mediaType: "text/x-kide-activity"
        })
      });
    }
  );

  await signInFirebase(page);
  await page.getByRole("button", { name: "Open", exact: true }).click();

  const createStarter = page
    .locator(".welcome-workbench")
    .getByRole("button", { name: "Create starter engineering models" });
  await expect(createStarter).toBeEnabled();
  await expect(page.getByText(/no editable DSL models are present yet/i)).toBeVisible();

  await createStarter.click();

  await expect.poll(() => starterCreated).toBe(true);
  await expect(
    page.locator(".file-list").getByText("workflow.activity", { exact: true })
  ).toBeVisible();
  await expect(
    page.locator(".file-list").getByText("inspect.op", { exact: true })
  ).toBeVisible();
  await expect(page.getByTestId("monaco-editor")).toBeVisible();
  await expect(
    page.getByLabel("Engineering workflow").getByRole("button", { name: "Synthesize", exact: true })
  ).toBeVisible();
  await expect(page.locator(".welcome-workbench")).toHaveCount(0);
  await expect(page.locator(".editor-tabs")).toContainText("workflow.activity");
});


async function signInFirebase(
  page: import("@playwright/test").Page,
  runtimeVersion: Record<string, unknown> = {
    apiVersion: "v1",
    engineeringCompatibilityLevel: 1,
    projectSchemaVersion: 1,
    sharedKernelSchemaVersion: 1,
    productLine: "1.0",
    productVersion: "1.0.0.test",
    buildId: "playwright-compatible"
  }
) {
  await page.route("**/api/v1/version", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify(runtimeVersion)
    });
  });

  await page.route(
    "**/v1/accounts:signInWithPassword?*",
    async (route) => {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          localId: "firebase-browser-user",
          email: "browser@example.test",
          idToken: "browser-test-token",
          refreshToken: "browser-refresh-token",
          expiresIn: "3600"
        })
      });
    }
  );

  await page.goto("/");
  await expect(page.getByRole("heading", { name: "Sign in to KIDE" })).toBeVisible();
  await page.getByLabel("Firebase email").fill("browser@example.test");
  await page.getByLabel("Firebase password").fill("password");
  await page.getByRole("button", { name: "Sign in with Firebase" }).click();
  await expect(page.getByText("browser@example.test", { exact: true })).toBeVisible();

  await page
    .getByRole("button", { name: "Open Engineering Workspace" })
    .first()
    .click();
  await expect(page).toHaveURL(/\/workspace$/);
  await expect(
    page.getByRole("button", { name: "browser@example.test" })
  ).toBeVisible();
}


async function mockCollaboration(page: import("@playwright/test").Page) {
  const presence = {
    id: "11111111-1111-4111-8111-111111111111",
    principalId: "browser-user",
    displayName: "Browser Engineer",
    modelId: "",
    joinedAt: "2026-09-22T00:00:00Z",
    lastSeenAt: "2026-09-22T00:00:00Z"
  };

  await page.route(
    "**/api/v1/projects/P04-001/collaboration/sessions*",
    async (route) => {
      const request = route.request();
      if (request.method() === "GET") {
        await route.fulfill({
          status: 200,
          contentType: "application/json",
          body: JSON.stringify({ items: [presence] })
        });
        return;
      }
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify(presence)
      });
    }
  );

  await page.route(
    "**/api/v1/projects/P04-001/reviews/changesets*",
    async (route) => {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({ items: [] })
      });
    }
  );

  await page.route(
    "**/api/v1/projects/P04-001/knowledge/traces*",
    async (route) => {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          revision: 0,
          etag: "0".repeat(64),
          links: []
        })
      });
    }
  );
}
