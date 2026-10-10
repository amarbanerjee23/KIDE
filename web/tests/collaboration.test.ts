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
