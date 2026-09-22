import { afterEach, describe, expect, it, vi } from "vitest";
import { ApiClientError, KideApiClient } from "../src/api";

afterEach(() => {
  vi.unstubAllGlobals();
});

describe("KideApiClient", () => {
  it("uses the versioned API boundary and keeps bearer credentials in headers", async () => {
    const fetchMock = vi.fn(async (input: RequestInfo | URL, init?: RequestInit) => {
      expect(String(input)).toBe("https://kide.example/api/v1/projects");
      const headers = new Headers(init?.headers);
      expect(headers.get("Authorization")).toBe("Bearer token-value");
      expect(headers.get("X-Request-Id")).toBeTruthy();
      return new Response(JSON.stringify({ items: [] }), {
        status: 200,
        headers: { "Content-Type": "application/json" }
      });
    });
    vi.stubGlobal("fetch", fetchMock);

    const client = new KideApiClient(
      "https://kide.example",
      () => "token-value"
    );
    await expect(client.listProjects()).resolves.toEqual({ items: [] });
  });

  it("surfaces typed revision conflicts without rewriting them", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn(async () =>
        new Response(
          JSON.stringify({
            apiVersion: "v1",
            requestId: "3b0f8593-dd99-4ad1-aec2-5eea1f1b62fd",
            code: "CONFLICT",
            message: "The model revision is stale.",
            details: {}
          }),
          {
            status: 409,
            headers: { "Content-Type": "application/json" }
          }
        )
      )
    );

    const client = new KideApiClient(
      "https://kide.example/api/v1",
      () => "token-value"
    );
    await expect(
      client.writeModel(
        "p1",
        "model.dml",
        "domain Local",
        "old-etag",
        "text/x-kide-dml"
      )
    ).rejects.toMatchObject({
      status: 409,
      code: "CONFLICT"
    } satisfies Partial<ApiClientError>);
  });

  it("uses revision-bound collaboration review endpoints", async () => {
    const fetchMock = vi.fn(async (input: RequestInfo | URL, init?: RequestInit) => {
      expect(String(input)).toBe(
        "https://kide.example/api/v1/projects/p1/reviews/changesets"
      );
      expect(init?.method).toBe("POST");
      const body = JSON.parse(String(init?.body));
      expect(body).toEqual({
        modelId: "model.dml",
        baseEtag: "a".repeat(64),
        proposedContent: "domain Proposed",
        mediaType: "text/x-kide-dml"
      });
      return new Response(JSON.stringify({
        id: "review-1",
        modelId: "model.dml",
        baseEtag: "a".repeat(64),
        baseRevision: "4",
        proposedContent: "domain Proposed",
        mediaType: "text/x-kide-dml",
        authorId: "u1",
        authorName: "User",
        status: "DRAFT",
        createdAt: "2026-09-22T00:00:00Z",
        updatedAt: "2026-09-22T00:00:00Z",
        reviewRevision: 1
      }), {
        status: 200,
        headers: { "Content-Type": "application/json" }
      });
    });
    vi.stubGlobal("fetch", fetchMock);

    const client = new KideApiClient("https://kide.example", () => "token-value");
    const result = await client.createReviewChangeSet(
      "p1",
      "model.dml",
      "a".repeat(64),
      "domain Proposed",
      "text/x-kide-dml"
    );

    expect(result.status).toBe("DRAFT");
  });
});
