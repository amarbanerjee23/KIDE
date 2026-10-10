import { defineConfig } from "@playwright/test";

/**
 * Live acceptance is deliberately isolated from playwright.config.ts.
 * No local Vite server, mocked routes, replay, trace, screenshot or video.
 * The CI staging environment provides the deployed Cloud Run URL.
 */
export default defineConfig({
  testDir: "./e2e",
  testMatch: "live-hosted.spec.ts",
  timeout: 180_000,
  expect: { timeout: 20_000 },
  retries: 0,
  reporter: "line",
  use: {
    baseURL: process.env.KIDE_LIVE_WEB_URL,
    trace: "off",
    screenshot: "off",
    video: "off",
    actionTimeout: 20_000
  }
});
