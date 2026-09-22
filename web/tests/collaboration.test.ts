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
