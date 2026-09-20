import { expect, test } from "@playwright/test";

test("opens an authorized project and loads a model through the shared API", async ({ page }) => {
  await page.route("**/api/v1/health", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        status: "UP",
        version: "v1",
        dependencies: {}
      })
    });
  });

  await page.route("**/api/v1/projects", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        items: [
          {
            id: "P04-001",
            displayName: "Golden Project",
            revision: "1",
            portfolioId: "PF04-1"
          }
        ]
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
        portfolioId: "PF04-1"
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
          content: "domain SelfCheck",
          revision: "7",
          etag: "etag-7",
          mediaType: "text/x-kide-dml"
        })
      });
    }
  );

  await page.goto("/");
  await page.getByLabel("Access token").fill("browser-test-token");
  await page.getByRole("button", { name: "Connect" }).click();
  await expect(page.getByText("UP · API v1")).toBeVisible();
  await expect(page.getByText("Golden Project")).toBeVisible();

  await page.getByRole("button", { name: "Open" }).click();
  await page.getByLabel("Model ID").fill("selfcheck.dml");
  await page.getByRole("button", { name: "Load model" }).click();

  await expect(
    page.getByText("Loaded selfcheck.dml at revision 7.")
  ).toBeVisible();
  await expect(page.getByTestId("monaco-editor")).toBeVisible();
});
