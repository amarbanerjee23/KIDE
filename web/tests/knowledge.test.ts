import { afterEach, describe, expect, it, vi } from "vitest";
import { KideApiClient } from "../src/api";

afterEach(() => {
  vi.unstubAllGlobals();
});

describe("KideApiClient knowledge surfaces", () => {
  it("queries catalogue data and keeps ontology semantics server-side", async () => {
    vi.stubGlobal("fetch", vi.fn(async (input: RequestInfo | URL, init?: RequestInit) => {
      expect(String(input)).toBe(
        "https://kide.example/api/v1/projects/p1/knowledge/query"
      );
      const body = JSON.parse(String(init?.body));
      expect(body).toEqual({ query: "observe", typeIri: "CAPABILITY", limit: 25 });
      return new Response(JSON.stringify({
        revision: 3,
        etag: "a".repeat(64),
        cached: false,
        items: [{
          iri: "urn:kide:capability:Observe",
          label: "Observe",
          types: ["https://kide.dev/ontology/v1#Capability"],
          properties: { "https://kide.dev/ontology/v1#role": ["Sensor"] },
          provenanceSource: "urn:test",
          authority: "test"
        }]
      }), { status: 200, headers: { "Content-Type": "application/json" } });
    }));

    const client = new KideApiClient("https://kide.example", () => "token");
    const result = await client.queryKnowledge("p1", "observe", "CAPABILITY", 25);
    expect(result.items[0].label).toBe("Observe");
    expect(result.items[0].properties["https://kide.dev/ontology/v1#role"])
      .toEqual(["Sensor"]);
  });

  it("sends only the trace-store precondition while the server derives model and knowledge revisions", async () => {
    vi.stubGlobal("fetch", vi.fn(async (_input: RequestInfo | URL, init?: RequestInit) => {
      const body = JSON.parse(String(init?.body));
      expect(body).toEqual({
        knowledgeIri: "urn:kide:capability:Observe",
        modelId: "flow.activity",
        semanticId: "//@activities.0",
        relation: "REALIZES",
        expectedTraceEtag: "0".repeat(64)
      });
      expect(body).not.toHaveProperty("knowledgeEtagAtBind");
      expect(body).not.toHaveProperty("modelEtagAtBind");
      return new Response(JSON.stringify({
        revision: 1,
        etag: "b".repeat(64),
        links: []
      }), { status: 200, headers: { "Content-Type": "application/json" } });
    }));

    const client = new KideApiClient("https://kide.example", () => "token");
    await client.createKnowledgeTrace(
      "p1",
      "urn:kide:capability:Observe",
      "flow.activity",
      "//@activities.0",
      "REALIZES",
      "0".repeat(64)
    );
  });
});
