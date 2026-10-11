import { defineConfig } from "@playwright/test";

/** Strictly read-only service connectivity, separated from mocked E2E and the
 * write-enabled full live staging acceptance. No local Vite or CI credentials. */
export default defineConfig({
  testDir: "./e2e",
  testMatch: "live-connectivity.spec.ts",
  timeout: 100_000,
  expect: { timeout: 15_000 },
  retries: 0,
  reporter: "line",
  use: {
    baseURL: process.env.KIDE_MONITOR_WEB_ORIGIN,
    trace: "off",
    screenshot: "off",
    video: "off",
    actionTimeout: 15_000
  }
});
