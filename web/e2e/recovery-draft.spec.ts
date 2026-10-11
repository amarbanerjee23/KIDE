import { expect, test } from "@playwright/test";

for (const revisionChanged of [false, true]) {
  test(
    revisionChanged
      ? "recovery never writes over a newer server model revision"
      : "explicitly restores a scoped draft and clears it after server acknowledgement",
    async ({ page }) => {
      const project = {
        id: "P99-RECOVERY", displayName: "Recovery Project",
        workspaceId: "W99", portfolioId: "PF99", revision: "1"
      };
      const originalEtag = "a".repeat(64);
      const liveEtag = revisionChanged ? "b".repeat(64) : originalEtag;
      const recoveredText = "DataModel Recovered { localDraft }";
      const draftKey = "kide:recovery:v1:engineer-99:P99-RECOVERY:main.dml";
      let writeCount = 0;

      await page.addInitScript(({ key, draft }) => {
        sessionStorage.setItem(key, JSON.stringify(draft));
      }, {
        key: draftKey,
        draft: {
          version: 1, uid: "engineer-99", projectId: project.id,
          path: "main.dml", baseEtag: originalEtag,
          text: recoveredText, savedAt: 1760000000000
        }
      });

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
          localId: "engineer-99", email: "engineer@example.test",
          idToken: "jwt-99", refreshToken: "refresh-99", expiresIn: "3600"
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
          productLine: "1.0", productVersion: "1.0.0.test", buildId: "pr99"
        })
      }));
      await page.route("**/api/v1/projects", (route) => route.fulfill({
        status: 200, contentType: "application/json",
        body: JSON.stringify({ items: [project] })
      }));
      await page.route("**/api/v1/projects/P99-RECOVERY", (route) => route.fulfill({
        status: 200, contentType: "application/json", body: JSON.stringify(project)
      }));
      await page.route("**/api/v1/projects/P99-RECOVERY/models", (route) => route.fulfill({
        status: 200, contentType: "application/json",
        body: JSON.stringify({ items: [{
          id: "main.dml", revision: "1", etag: liveEtag, mediaType: "text/x-kide-dml"
        }] })
      }));
      await page.route("**/api/v1/projects/P99-RECOVERY/models/main.dml", async (route) => {
        if (route.request().method() === "PUT") {
          writeCount++;
          const payload = route.request().postDataJSON();
          expect(payload).toMatchObject({
            content: recoveredText, expectedRevision: originalEtag
          });
          await route.fulfill({
            status: 200, contentType: "application/json",
            body: JSON.stringify({
              id: "main.dml", revision: "2", etag: "c".repeat(64),
              mediaType: "text/x-kide-dml", content: recoveredText
            })
          });
          return;
        }
        await route.fulfill({
          status: 200, contentType: "application/json",
          body: JSON.stringify({
            id: "main.dml", revision: "1", etag: liveEtag,
            mediaType: "text/x-kide-dml", content: "DataModel Main {}"
          })
        });
      });
      const session = {
        id: "11111111-1111-4111-8111-111111111111",
        principalId: "engineer-99", displayName: "Engineer",
        joinedAt: "2026-10-11T00:00:00Z", lastSeenAt: "2026-10-11T00:00:00Z"
      };
      await page.route("**/api/v1/projects/P99-RECOVERY/collaboration/sessions", (route) =>
        route.fulfill({
          status: 200, contentType: "application/json",
          body: JSON.stringify(route.request().method() === "GET" ? { items: [session] } : session)
        })
      );
      await page.route("**/api/v1/projects/P99-RECOVERY/collaboration/sessions/*", (route) =>
        route.fulfill({
          status: 200, contentType: "application/json", body: JSON.stringify(session)
        })
      );
      await page.route("**/api/v1/projects/P99-RECOVERY/reviews/changesets*", (route) =>
        route.fulfill({ status: 200, contentType: "application/json", body: JSON.stringify({ items: [] }) })
      );
      await page.route("**/api/v1/projects/P99-RECOVERY/knowledge/traces*", (route) =>
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
      await expect(page.locator(".editor-tabs")).toContainText("main.dml");

      const recovery = page.getByRole("region", { name: "Recovered unsaved draft" });
      await expect(recovery).toBeVisible();
      await recovery.getByText("Review recovered text").click();
      await expect(recovery.getByLabel("Recovered draft text")).toHaveValue(recoveredText);

      if (revisionChanged) {
        await expect(recovery.getByRole("button", { name: "Restore draft" })).toHaveCount(0);
        await expect(recovery).toContainText("server revision changed");
        expect(writeCount).toBe(0);
        return;
      }

      await recovery.getByRole("button", { name: "Restore draft" }).click();
      await expect(recovery).toHaveCount(0);
      await expect.poll(() => writeCount).toBe(1);
      await expect.poll(() =>
        page.evaluate((key) => sessionStorage.getItem(key), draftKey)
      ).toBeNull();
    }
  );
}
