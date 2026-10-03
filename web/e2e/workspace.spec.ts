import { expect, test } from "@playwright/test";

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


test("opens Activity through the secure GLSP browser boundary", async ({ page }) => {
  let requestedSourceUri = "";
  let requestedDiagramType = "";
  let glspSessionId = "";
  let reconfigurationRequest: unknown;

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
                  id: "//@activities.0",
                  type: "kide:activity",
                  position: { x: 80, y: 80 },
                  size: { width: 180, height: 72 },
                  children: [{
                    id: "//@activities.0_label",
                    type: "label",
                    text: "ObserveStep"
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
                elementTypeId: "kide:activity",
                repositionable: true,
                deletable: true,
                resizable: true,
                reparentable: false,
                containableElementTypeIds: []
              }],
              edgeHints: [{
                elementTypeId: "kide:activity-next",
                repositionable: false,
                deletable: true,
                routable: false,
                sourceElementTypeIds: ["kide:activity"],
                targetElementTypeIds: ["kide:activity"]
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
          manifestJson: "{\"schemaVersion\":\"1\",\"toolchainVersion\":\"1\"}",
          artifacts: [{
            path: "generated/Out.java",
            mediaType: "text/x-java-source",
            contentBase64: "cHVibGljIGNsYXNzIE91dCB7fQ==",
            sha256: "a".repeat(64),
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
      reconfigurationRequest = route.request().postDataJSON();
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
  await expect(page.getByTestId("monaco-editor")).toBeVisible();
  await expect(page.locator(".status-bar")).toContainText("Collaboration Online");
  await expect(page.getByLabel("Engineering workflow").getByRole("button", { name: "Synthesize", exact: true })).toBeVisible();

  await page.getByLabel("Engineering workflow").getByRole("button", { name: "Synthesize", exact: true }).click();
  await expect(page.getByLabel("Synthesis result")).toContainText("SUCCESS");
  await expect(page.getByLabel("Synthesis result")).toContainText(
    "urn:kide:device:camera"
  );
  await expect(page.getByText("Generated MNC")).toBeVisible();

  await expect(page.getByLabel("KRL model ID")).toHaveValue("bindings.krl");
  await page.getByRole("button", { name: "Generate code", exact: true }).first().click();
  await expect(page.getByLabel("Generation result")).toContainText(
    "Generated 1 artifact"
  );
  await expect(page.getByLabel("Generation result")).toContainText(
    "generated/Out.java"
  );
  await expect(page.getByText("Generation manifest")).toBeVisible();

  await page.getByLabel("Reconfiguration cause").selectOption("RESOURCE_LOSS");
  await expect(page.getByLabel("Reconfiguration cause")).toHaveValue("RESOURCE_LOSS");
  await page.getByLabel("Engineering workflow").getByRole("button", { name: "Reconfigure", exact: true }).click();
  await expect(page.getByLabel("Reconfiguration result")).toContainText("RECONFIGURED");
  await expect(page.getByLabel("Reconfiguration result")).toContainText(
    "urn:kide:device:camera-b"
  );
  await expect(page.getByLabel("Reconfiguration result")).toContainText("MIGRATE");
  expect(reconfigurationRequest).toEqual({
    modelId: "flow.activity",
    modelRevision: "etag-4",
    cause: "RESOURCE_LOSS",
    previousBindings: [{
      requirementId: "activity:ObserveStep",
      resourceId: "urn:kide:device:camera"
    }]
  });

  await page.getByRole("button", { name: "Diagram" }).click();

  await expect(page.getByTestId("glsp-editor")).toBeVisible();
  await expect(page.getByTestId("glsp-editor").getByText("ObserveStep", { exact: true })).toBeVisible();
  await expect(page.getByRole("button", { name: "Activity", exact: true })).toBeVisible();
  await expect(page.getByText("Connected · Eclipse GLSP graphical model")).toBeVisible();

  expect(glspSessionId).not.toBe("");
  expect(requestedSourceUri).toBe("kide-workspace:/flow.activity");
  expect(requestedDiagramType).toBe("kide-activity-diagram");
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


async function signInFirebase(page: import("@playwright/test").Page) {
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
