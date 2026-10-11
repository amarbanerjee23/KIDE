import { afterEach, describe, expect, it, vi } from "vitest";
import { ApiClientError, type KideApiClient } from "../src/api";
import { CollaborationCoordinator } from "../src/collaboration";
import type { PresenceSession } from "../src/types";

afterEach(() => {
  vi.useRealTimers();
});

function session(id = "11111111-1111-4111-8111-111111111111"): PresenceSession {
  return {
    id,
    principalId: "user-1",
    displayName: "Engineer",
    modelId: "model.dml",
    joinedAt: "2026-09-22T00:00:00Z",
    lastSeenAt: "2026-09-22T00:00:01Z"
  };
}

describe("CollaborationCoordinator", () => {
  it("does not revive a disconnected session when a slow join finishes after sign-out", async () => {
    const joined = session("22222222-2222-4222-8222-222222222222");
    let finishJoin!: (value: PresenceSession) => void;
    const delayedJoin = new Promise<PresenceSession>((resolve) => { finishJoin = resolve; });
    const api = {
      joinPresence: vi.fn().mockReturnValue(delayedJoin),
      heartbeatPresence: vi.fn(),
      leavePresence: vi.fn().mockResolvedValue(joined),
      listPresence: vi.fn().mockResolvedValue({ items: [joined] })
    } satisfies Pick<KideApiClient,
      "joinPresence" | "heartbeatPresence" | "leavePresence" | "listPresence">;
    const onSession = vi.fn();
    const onPresence = vi.fn();
    const coordinator = new CollaborationCoordinator(api, "project-old",
      { onSession, onPresence, onError: vi.fn() }, 10);
    const pending = coordinator.connect();
    await coordinator.disconnect();
    finishJoin(joined);
    await expect(pending).rejects.toThrow("superseded");
    expect(api.leavePresence).toHaveBeenCalledWith("project-old", joined.id);
    expect(onSession).not.toHaveBeenCalled();
    expect(onPresence).not.toHaveBeenCalled();
    expect(api.heartbeatPresence).not.toHaveBeenCalled();
  });

  it("ignores a late presence-list response after project replacement", async () => {
    const joined = session();
    let finishList!: (value: { items: PresenceSession[] }) => void;
    const delayedList = new Promise<{ items: PresenceSession[] }>((resolve) => {
      finishList = resolve;
    });
    const api = {
      joinPresence: vi.fn().mockResolvedValue(joined),
      heartbeatPresence: vi.fn().mockResolvedValue(joined),
      leavePresence: vi.fn().mockResolvedValue(joined),
      listPresence: vi.fn().mockReturnValue(delayedList)
    } satisfies Pick<KideApiClient,
      "joinPresence" | "heartbeatPresence" | "leavePresence" | "listPresence">;
    const onPresence = vi.fn();
    const coordinator = new CollaborationCoordinator(api, "project-old",
      { onSession: vi.fn(), onPresence, onError: vi.fn() }, 10);
    const connecting = coordinator.connect();
    // Connect has joined but is still waiting for the slow presence list.
    await Promise.resolve();
    await coordinator.disconnect();
    finishList({ items: [joined] });
    await expect(connecting).rejects.toThrow("superseded");
    expect(onPresence).not.toHaveBeenCalled();
  });


  it("coalesces overlapping initial connect calls into exactly one presence join", async () => {
    const joined = session();
    let finishJoin!: (value: PresenceSession) => void;
    const pendingJoin = new Promise<PresenceSession>((resolve) => { finishJoin = resolve; });
    const api = {
      joinPresence: vi.fn().mockReturnValue(pendingJoin),
      heartbeatPresence: vi.fn().mockResolvedValue(joined),
      leavePresence: vi.fn().mockResolvedValue(joined),
      listPresence: vi.fn().mockResolvedValue({ items: [joined] })
    } satisfies Pick<KideApiClient, "joinPresence" | "heartbeatPresence" |
      "leavePresence" | "listPresence">;
    const onSession = vi.fn();
    const client = new CollaborationCoordinator(api, "project-1", {
      onSession, onPresence: vi.fn(), onError: vi.fn()
    });
    const first = client.connect(undefined, "model.dml");
    const second = client.connect(undefined, "model.dml");
    expect(client.isActive).toBe(true);
    expect(api.joinPresence).toHaveBeenCalledTimes(1);
    finishJoin(joined);
    await expect(first).resolves.toEqual(joined);
    await expect(second).resolves.toEqual(joined);
    await expect(client.connect()).resolves.toEqual(joined);
    expect(api.joinPresence).toHaveBeenCalledTimes(1);
    expect(onSession).toHaveBeenCalledTimes(1);
    await client.disconnect();
  });

  it("recovers after temporary presence-list failure by heartbeating without rejoining", async () => {
    const joined = session();
    const onError = vi.fn();
    const onSession = vi.fn();
    const api = {
      joinPresence: vi.fn().mockResolvedValue(joined),
      heartbeatPresence: vi.fn().mockResolvedValue(joined),
      leavePresence: vi.fn().mockResolvedValue(joined),
      listPresence: vi.fn().mockRejectedValueOnce(new Error("transient 503"))
        .mockResolvedValue({ items: [joined] })
    } satisfies Pick<KideApiClient, "joinPresence" | "heartbeatPresence" |
      "leavePresence" | "listPresence">;
    const client = new CollaborationCoordinator(api, "project-1", {
      onSession, onPresence: vi.fn(), onError
    });
    await client.connect();
    expect(onError).toHaveBeenCalledTimes(1);
    expect(client.isActive).toBe(true);
    await client.recover();
    await client.recover();
    expect(api.joinPresence).toHaveBeenCalledTimes(1);
    expect(api.heartbeatPresence).toHaveBeenCalledTimes(2);
    expect(onSession).toHaveBeenCalledTimes(3);
    await client.disconnect();
  });

  it("serializes overlapping recover calls and rejoins only for an expired 404", async () => {
    const initial = session();
    const replacement = session("33333333-3333-4333-8333-333333333333");
    let finishHeartbeat!: (value: PresenceSession) => void;
    const slowHeartbeat = new Promise<PresenceSession>((resolve) => {
      finishHeartbeat = resolve;
    });
    const api = {
      joinPresence: vi.fn().mockResolvedValueOnce(initial).mockResolvedValue(replacement),
      heartbeatPresence: vi.fn().mockReturnValueOnce(slowHeartbeat)
        .mockRejectedValueOnce(new ApiClientError("expired", 404)),
      leavePresence: vi.fn().mockResolvedValue(replacement),
      listPresence: vi.fn().mockResolvedValue({ items: [replacement] })
    } satisfies Pick<KideApiClient, "joinPresence" | "heartbeatPresence" |
      "leavePresence" | "listPresence">;
    const client = new CollaborationCoordinator(api, "project-1", {
      onSession: vi.fn(), onPresence: vi.fn(), onError: vi.fn()
    });
    await client.connect(undefined, "model.dml");
    const first = client.recover();
    const second = client.recover();
    expect(api.heartbeatPresence).toHaveBeenCalledTimes(1);
    finishHeartbeat(initial);
    await Promise.all([first, second]);
    expect(api.joinPresence).toHaveBeenCalledTimes(1);
    await Promise.all([client.recover(), client.recover()]);
    expect(api.heartbeatPresence).toHaveBeenCalledTimes(2);
    expect(api.joinPresence).toHaveBeenCalledTimes(2);
    expect(api.joinPresence).toHaveBeenLastCalledWith(
      "project-1", initial.id, "model.dml"
    );
    await client.disconnect();
    expect(api.leavePresence).toHaveBeenCalledWith("project-1", replacement.id);
  });

  it("does not create an extra join on authorization failure", async () => {
    const joined = session();
    const onError = vi.fn();
    const api = {
      joinPresence: vi.fn().mockResolvedValue(joined),
      heartbeatPresence: vi.fn().mockRejectedValue(new ApiClientError("forbidden", 403)),
      leavePresence: vi.fn().mockResolvedValue(joined),
      listPresence: vi.fn().mockResolvedValue({ items: [joined] })
    } satisfies Pick<KideApiClient, "joinPresence" | "heartbeatPresence" |
      "leavePresence" | "listPresence">;
    const client = new CollaborationCoordinator(api, "project-1", {
      onSession: vi.fn(), onPresence: vi.fn(), onError
    });
    await client.connect();
    await client.recover();
    expect(api.joinPresence).toHaveBeenCalledTimes(1);
    expect(onError).toHaveBeenCalledTimes(1);
    await client.disconnect();
  });

  it("rejoins the same presence session after expiry and leaves cleanly", async () => {
    vi.useFakeTimers();
    const joined = session();
    const api = {
      joinPresence: vi.fn().mockResolvedValue(joined),
      heartbeatPresence: vi.fn().mockRejectedValue(
        new ApiClientError("expired", 404)
      ),
      leavePresence: vi.fn().mockResolvedValue(joined),
      listPresence: vi.fn().mockResolvedValue({ items: [joined] })
    } satisfies Pick<
      KideApiClient,
      "joinPresence" | "heartbeatPresence" | "leavePresence" | "listPresence"
    >;
    const presence = vi.fn();
    const coordinator = new CollaborationCoordinator(
      api,
      "project-1",
      {
        onPresence: presence,
        onSession: () => undefined,
        onError: (error) => {
          throw error;
        }
      },
      10
    );

    await coordinator.connect(joined.id, "model.dml");
    await vi.advanceTimersByTimeAsync(10);

    expect(api.heartbeatPresence).toHaveBeenCalledWith(
      "project-1",
      joined.id,
      "model.dml"
    );
    expect(api.joinPresence).toHaveBeenCalledTimes(2);
    expect(api.joinPresence).toHaveBeenLastCalledWith(
      "project-1",
      joined.id,
      "model.dml"
    );
    expect(presence).toHaveBeenCalled();

    await coordinator.disconnect();
    expect(api.leavePresence).toHaveBeenCalledWith("project-1", joined.id);
  });
});
