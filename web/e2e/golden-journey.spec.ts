import { expect, test, type Page } from "@playwright/test";

const SOURCE_REVISION = "a".repeat(64);
const KRL_REVISION = "b".repeat(64);
const SYNTHESIS_FINGERPRINT = "c".repeat(64);
const GENERATION_FINGERPRINT = "d".repeat(64);
const ARTIFACT_HASH = "cd840d30eb4d65df2ccf2a95f610870b62f732a547140b60009f795ad1cae7ec";
const ARTIFACT_BASE64 = "cHVibGljIGNsYXNzIE91dCB7fQo=";

test("golden engineering journey produces integrity-checked generated code", async ({ page }) => {
  let synthesisCalls = 0;
  let generationCalls = 0;

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
        ws.send(JSON.stringify({ jsonrpc: "2.0", id: request.id, result: null }));
      }
    });
  });

  await mockProjectServices(page);

  await page.route("**/api/v1/projects/P04-001/synthesis", async (route) => {
    synthesisCalls += 1;
    const body = route.request().postDataJSON();
    expect(body).toEqual({
      modelId: "flow.activity",
      modelRevision: SOURCE_REVISION
    });
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        resultId: "syn-golden",
        serviceVersion: "1",
        status: "SUCCESS",
        modelId: "flow.activity",
        modelVersion: "4",
        revision: SOURCE_REVISION,
        knowledgeRevision: 3,
        knowledgeEtag: "e".repeat(64),
        fingerprint: SYNTHESIS_FINGERPRINT,
        selections: [{
          requirementId: "activity:ObserveStep",
          activityName: "ObserveStep",
          capabilityName: "Observe",
          resourceId: "urn:kide:device:camera",
          rationale: "priority=10"
        }],
        diagnostics: [],
        rationale: ["ObserveStep -> urn:kide:device:camera (priority=10)"],
        generatedMnc: "Model GoldenWorkflow\nInterfaceDescription GoldenWorkflow {}\n"
      })
    });
  });

  await page.route("**/api/v1/projects/P04-001/generation", async (route) => {
    generationCalls += 1;
    const body = route.request().postDataJSON();
    expect(body).toEqual({
      sourceModelId: "flow.activity",
      sourceRevision: SOURCE_REVISION,
      krlModelId: "bindings.krl",
      krlRevision: KRL_REVISION,
      synthesisFingerprint: SYNTHESIS_FINGERPRINT
    });

    const manifest = {
      schemaVersion: "1",
      toolchainVersion: "1",
      fingerprint: GENERATION_FINGERPRINT,
      krlModelName: "Bindings",
      source: {
        modelId: "flow.activity",
        revision: SOURCE_REVISION,
        etag: SOURCE_REVISION
      },
      knowledge: { revision: 3, etag: "e".repeat(64) },
      synthesis: { fingerprint: SYNTHESIS_FINGERPRINT },
      krl: {
        modelId: "bindings.krl",
        revision: KRL_REVISION,
        etag: KRL_REVISION
      },
      targetVersions: { java: "1" },
      artifacts: [{
        path: "generated/Out.java",
        mediaType: "text/x-java-source",
        bytes: 20,
        sha256: ARTIFACT_HASH,
        targetId: "java",
        targetVersion: "1",
        targetName: "Java",
        templateName: "reference"
      }]
    };

    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        resultId: "gen-golden",
        toolchainVersion: "1",
        fingerprint: GENERATION_FINGERPRINT,
        sourceModelId: "flow.activity",
        sourceModelVersion: "4",
        sourceRevision: SOURCE_REVISION,
        krlModelId: "bindings.krl",
        krlModelVersion: "2",
        krlRevision: KRL_REVISION,
        knowledgeRevision: 3,
        knowledgeEtag: "e".repeat(64),
        synthesisFingerprint: SYNTHESIS_FINGERPRINT,
        manifestJson: JSON.stringify(manifest),
        artifacts: [{
          path: "generated/Out.java",
          mediaType: "text/x-java-source",
          contentBase64: ARTIFACT_BASE64,
          sha256: ARTIFACT_HASH,
          targetId: "java",
          targetVersion: "1"
        }]
      })
    });
  });

  await signIn(page);

  await expect(page.locator(".project-list").getByText("Golden Project", { exact: true })).toBeVisible();
  await page.getByRole("button", { name: "Open", exact: true }).click();
  await expect(page.locator(".editor-tabs")).toContainText("flow.activity");
  await expect(page.locator(".status-bar")).toContainText("API Online");
  await expect(page.locator(".status-bar")).toContainText("Xtext Online");

  const workflow = page.getByLabel("Engineering workflow");
  await expect(workflow.getByRole("button", { name: "Synthesize", exact: true })).toBeEnabled();
  await expect(workflow.getByRole("button", { name: "Generate code", exact: true })).toBeDisabled();

  await workflow.getByRole("button", { name: "Synthesize", exact: true }).click();
  await expect.poll(() => synthesisCalls).toBe(1);
  await expect(page.getByLabel("Synthesis result")).toContainText("SUCCESS");
  await expect(page.getByLabel("Synthesis result")).toContainText("urn:kide:device:camera");
  await expect(workflow.getByRole("button", { name: "Generate code", exact: true })).toBeEnabled();

  await workflow.getByRole("button", { name: "Engineering flow", exact: true }).click();
  const cockpit = page.getByLabel("Engineering cockpit");
  await expect(cockpit).toBeVisible();
  await expect(page.getByLabel("KRL model ID")).toHaveValue("bindings.krl");

  await cockpit
    .getByLabel("Generation result")
    .getByRole("button", { name: "Generate code", exact: true })
    .click();

  await expect.poll(() => generationCalls).toBe(1);
  const generation = cockpit.getByLabel("Generation result");
  await expect(generation).toContainText("Generated 1 artifact");
  await expect(generation).toContainText("generated/Out.java");
  await expect(generation).toContainText("public class Out {}");
  await expect(generation).toContainText("java@1");
  await expect(generation.getByText("Generation manifest")).toBeVisible();
});

async function mockProjectServices(page: Page) {
  await page.route("**/api/v1/health", (route) => route.fulfill({
    status: 200,
    contentType: "application/json",
    body: JSON.stringify({ status: "UP", version: "v1", dependencies: {} })
  }));

  await page.route("**/api/v1/projects", (route) => route.fulfill({
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
  }));

  await page.route("**/api/v1/projects/P04-001", (route) => route.fulfill({
    status: 200,
    contentType: "application/json",
    body: JSON.stringify({
      id: "P04-001",
      displayName: "Golden Project",
      revision: "1",
      portfolioId: "PF04-1",
      workspaceId: "W04-001"
    })
  }));

  await page.route("**/api/v1/projects/P04-001/models", (route) => route.fulfill({
    status: 200,
    contentType: "application/json",
    body: JSON.stringify({
      items: [
        {
          id: "flow.activity",
          revision: "4",
          etag: SOURCE_REVISION,
          mediaType: "text/x-kide-activity"
        },
        {
          id: "bindings.krl",
          revision: "2",
          etag: KRL_REVISION,
          mediaType: "text/x-kide-krl"
        }
      ]
    })
  }));

  await page.route("**/api/v1/projects/P04-001/models/flow.activity", (route) => route.fulfill({
    status: 200,
    contentType: "application/json",
    body: JSON.stringify({
      id: "flow.activity",
      content: "Workflow GoldenWorkflow { activity ObserveStep requires Observe }",
      revision: "4",
      etag: SOURCE_REVISION,
      mediaType: "text/x-kide-activity"
    })
  }));

  await page.route("**/api/v1/projects/P04-001/models/bindings.krl", (route) => route.fulfill({
    status: 200,
    contentType: "application/json",
    body: JSON.stringify({
      id: "bindings.krl",
      content: "KrlModel Bindings {}",
      revision: "2",
      etag: KRL_REVISION,
      mediaType: "text/x-kide-krl"
    })
  }));

  await page.route("**/api/v1/projects/P04-001/collaboration/sessions*", async (route) => {
    const session = {
      id: "11111111-1111-4111-8111-111111111111",
      principalId: "browser-user",
      displayName: "Browser Engineer",
      modelId: "",
      joinedAt: "2026-10-04T00:00:00Z",
      lastSeenAt: "2026-10-04T00:00:00Z"
    };
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify(route.request().method() === "GET" ? { items: [session] } : session)
    });
  });

  await page.route("**/api/v1/projects/P04-001/reviews/changesets*", (route) => route.fulfill({
    status: 200,
    contentType: "application/json",
    body: JSON.stringify({ items: [] })
  }));

  await page.route("**/api/v1/projects/P04-001/knowledge/traces*", (route) => route.fulfill({
    status: 200,
    contentType: "application/json",
    body: JSON.stringify({ revision: 0, etag: "0".repeat(64), links: [] })
  }));
}

async function signIn(page: Page) {
  await page.route("**/api/v1/version", (route) => route.fulfill({
    status: 200,
    contentType: "application/json",
    body: JSON.stringify({
      apiVersion: "v1",
      engineeringCompatibilityLevel: 1,
      projectSchemaVersion: 1,
      sharedKernelSchemaVersion: 1,
      productLine: "1.0",
      productVersion: "1.0.0.test",
      buildId: "pr84-golden"
    })
  }));

  await page.route("**/v1/accounts:signInWithPassword?*", (route) => route.fulfill({
    status: 200,
    contentType: "application/json",
    body: JSON.stringify({
      localId: "firebase-browser-user",
      email: "browser@example.test",
      idToken: "browser-test-token",
      refreshToken: "browser-refresh-token",
      expiresIn: "3600"
    })
  }));

  await page.goto("/");
  await page.getByLabel("Firebase email").fill("browser@example.test");
  await page.getByLabel("Firebase password").fill("password");
  await page.getByRole("button", { name: "Sign in with Firebase" }).click();
  await expect(page.getByText("browser@example.test", { exact: true })).toBeVisible();
  await page.getByRole("button", { name: "Open Engineering Workspace" }).first().click();
  await expect(page).toHaveURL(/\/workspace$/);
}
