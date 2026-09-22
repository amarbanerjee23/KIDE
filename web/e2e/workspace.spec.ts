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

  await page.getByLabel("Knowledge query").fill("Observe");
  await page.getByLabel("Knowledge concept type").selectOption("CAPABILITY");
  await page.getByRole("button", { name: "Search", exact: true }).click();
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

  await page.goto("/");
  await page.getByLabel("Access token").fill("browser-test-token");
  await page.getByRole("button", { name: "Connect API" }).click();
  await page.getByRole("button", { name: "Open" }).click();

  await page.getByLabel("Model ID").fill("flow.activity");
  await page.getByRole("button", { name: "Load model" }).click();

  await page.getByRole("button", { name: "Synthesize" }).click();
  await expect(page.getByLabel("Synthesis result")).toContainText("SUCCESS");
  await expect(page.getByLabel("Synthesis result")).toContainText(
    "urn:kide:device:camera"
  );
  await expect(page.getByText("Generated MNC")).toBeVisible();

  await expect(page.getByLabel("KRL model ID")).toHaveValue("bindings.krl");
  await page.getByRole("button", { name: "Generate" }).click();
  await expect(page.getByLabel("Generation result")).toContainText(
    "Generated 1 artifact"
  );
  await expect(page.getByLabel("Generation result")).toContainText(
    "generated/Out.java"
  );
  await expect(page.getByText("Generation manifest")).toBeVisible();

  await page.getByLabel("Reconfiguration cause").selectOption("RESOURCE_LOSS");
  await expect(page.getByLabel("Reconfiguration cause")).toHaveValue("RESOURCE_LOSS");
  await page.getByRole("button", { name: "Reconfigure" }).click();
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
