import { afterEach, describe, expect, it, vi } from "vitest";
import { KideApiClient } from "../src/api";

afterEach(() => {
  vi.unstubAllGlobals();
  it("sends only prior binding evidence for deterministic reconfiguration", async () => {
    vi.stubGlobal("fetch", vi.fn(async (input: RequestInfo | URL, init?: RequestInit) => {
      expect(String(input)).toBe(
        "https://kide.example/api/v1/projects/P04-001/reconfiguration"
      );
      expect(init?.method).toBe("POST");
      const body = JSON.parse(String(init?.body));
      expect(body).toEqual({
        modelId: "flow.activity",
        modelRevision: "a".repeat(64),
        cause: "RESOURCE_LOSS",
        previousBindings: [{
          requirementId: "activity:ObserveStep",
          activityName: "ObserveStep",
          capabilityName: "Observe",
          resourceId: "urn:kide:device:camera"
        }]
      });
      expect(body).not.toHaveProperty("resources");
      expect(body).not.toHaveProperty("ranking");

      return new Response(JSON.stringify({
        resultId: "reconf-123",
        serviceVersion: "1",
        status: "RECONFIGURED",
        cause: "RESOURCE_LOSS",
        modelId: "flow.activity",
        modelVersion: "4",
        revision: "a".repeat(64),
        knowledgeRevision: 4,
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
          reason: "Equivalent capability binding changed."
        }],
        diagnostics: []
      }), {
        status: 200,
        headers: { "Content-Type": "application/json" }
      });
    }));

    const client = new KideApiClient("https://kide.example", () => "token");
    const result = await client.reconfigure(
      "P04-001",
      "flow.activity",
      "a".repeat(64),
      "RESOURCE_LOSS",
      [{
        requirementId: "activity:ObserveStep",
        activityName: "ObserveStep",
        capabilityName: "Observe",
        resourceId: "urn:kide:device:camera"
      }]
    );

    expect(result.status).toBe("RECONFIGURED");
    expect(result.migrations[0].policy).toBe("MIGRATE");
    expect(result.selections[0].resourceId).toBe("urn:kide:device:camera-b");
  });
});

describe("KideApiClient synthesis boundary", () => {
  it("sends only the canonical Activity identity and current revision", async () => {
    vi.stubGlobal("fetch", vi.fn(async (input: RequestInfo | URL, init?: RequestInit) => {
      expect(String(input)).toBe(
        "https://kide.example/api/v1/projects/P04-001/synthesis"
      );
      expect(init?.method).toBe("POST");
      const body = JSON.parse(String(init?.body));
      expect(body).toEqual({
        modelId: "flow.activity",
        modelRevision: "a".repeat(64)
      });
      expect(body).not.toHaveProperty("resources");
      expect(body).not.toHaveProperty("ranking");
      expect(body).not.toHaveProperty("generatedMnc");

      return new Response(JSON.stringify({
        resultId: "syn-123",
        serviceVersion: "1",
        status: "SUCCESS",
        modelId: "flow.activity",
        modelVersion: "4",
        revision: "a".repeat(64),
        knowledgeRevision: 3,
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
        rationale: ["ObserveStep -> urn:kide:device:camera (priority=10)"],
        generatedMnc: "Model GoldenWorkflow\nInterfaceDescription GoldenWorkflow {}\n"
      }), {
        status: 200,
        headers: { "Content-Type": "application/json" }
      });
    }));

    const client = new KideApiClient("https://kide.example", () => "token");
    const result = await client.synthesize(
      "P04-001", "flow.activity", "a".repeat(64)
    );

    expect(result.status).toBe("SUCCESS");
    expect(result.selections[0].resourceId).toBe("urn:kide:device:camera");
    expect(result.generatedMnc).toContain("Model GoldenWorkflow");
  });
});
