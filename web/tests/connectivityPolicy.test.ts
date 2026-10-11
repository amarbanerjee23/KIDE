import { describe, expect, it } from "vitest";
import { readConnectivitySettings } from "../src/connectivityPolicy";

const valid = {
  KIDE_MONITOR_API_ORIGIN: "https://api-stage.example.test",
  KIDE_MONITOR_WEB_ORIGIN: "https://web-stage.example.test",
  KIDE_MONITOR_APPROVED_API_HOST: "api-stage.example.test",
  KIDE_MONITOR_APPROVED_WEB_HOST: "web-stage.example.test",
  KIDE_MONITOR_EXPECTED_PROJECT_ID: "kide:project:11111111-1111-4111-8111-111111111111",
  KIDE_MONITOR_FIREBASE_API_KEY: "test-public-key",
  KIDE_MONITOR_ENGINEER_EMAIL: "readonly@example.test",
  KIDE_MONITOR_ENGINEER_PASSWORD: "not-a-real-password"
};

describe("authenticated hosted connectivity probe policy", () => {
  it("requires independently approved HTTPS origins and a known project", () => {
    const cfg = readConnectivitySettings(valid);
    expect(cfg.api).toBe("https://api-stage.example.test");
    expect(cfg.web).toBe("https://web-stage.example.test");
    expect(cfg.projectId).toBe(valid.KIDE_MONITOR_EXPECTED_PROJECT_ID);
  });

  it("rejects unapproved or non-HTTPS origins and URL credentials", () => {
    for (const url of [
      "http://api-stage.example.test",
      "https://other.example.test",
      "https://alice:password@api-stage.example.test",
      "https://api-stage.example.test/prefix",
      "https://api-stage.example.test/?token=secret",
      "https://api-stage.example.test/#fragment",
      "file:///etc/passwd"
    ]) {
      expect(() => readConnectivitySettings({
        ...valid, KIDE_MONITOR_API_ORIGIN: url
      })).toThrow();
    }
  });

  it("requires separate Web and API origins", () => {
    expect(() => readConnectivitySettings({
      ...valid,
      KIDE_MONITOR_WEB_ORIGIN: valid.KIDE_MONITOR_API_ORIGIN,
      KIDE_MONITOR_APPROVED_WEB_HOST: valid.KIDE_MONITOR_APPROVED_API_HOST
    })).toThrow(/independently/);
  });

  it("rejects missing credentials and unscoped project discovery", () => {
    for (const key of [
      "KIDE_MONITOR_ENGINEER_EMAIL", "KIDE_MONITOR_ENGINEER_PASSWORD",
      "KIDE_MONITOR_FIREBASE_API_KEY", "KIDE_MONITOR_EXPECTED_PROJECT_ID"
    ] as const) {
      expect(() => readConnectivitySettings({ ...valid, [key]: "" }))
        .toThrow(/Missing required/);
    }
    expect(() => readConnectivitySettings({
      ...valid, KIDE_MONITOR_EXPECTED_PROJECT_ID: "*"
    })).toThrow(/canonical/);
  });

  it("does not expose secrets in origin-validation diagnostics", () => {
    expect(() => readConnectivitySettings({
      ...valid, KIDE_MONITOR_API_ORIGIN: "https://user:private@api-stage.example.test"
    })).toThrow("KIDE_MONITOR_API_ORIGIN must match its approved HTTPS staging hostname");
  });
});
