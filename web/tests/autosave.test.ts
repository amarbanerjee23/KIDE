import { describe, expect, it, vi } from "vitest";
import { ApiClientError } from "../src/api";
import { AutosaveCoordinator } from "../src/autosave";
import type { Model, SaveState } from "../src/types";

function model(content: string, etag: string, revision: string): Model {
  return {
    id: "model.dml",
    content,
    etag,
    revision,
    mediaType: "text/x-kide-dml"
  };
}

describe("AutosaveCoordinator", () => {
  it("carries the server etag forward and serializes newer edits", async () => {
    vi.useFakeTimers();
    const states: SaveState[] = [];
    const save = vi
      .fn()
      .mockResolvedValueOnce(model("one", "etag-2", "2"))
      .mockResolvedValueOnce(model("two", "etag-3", "3"));

    const coordinator = new AutosaveCoordinator(
      "etag-1",
      save,
      {
        onState: (state) => states.push(state),
        onSaved: () => undefined,
        onConflict: () => undefined,
        onError: () => undefined
      },
      10
    );

    coordinator.update("one");
    await vi.advanceTimersByTimeAsync(10);
    coordinator.update("two");
    await vi.advanceTimersByTimeAsync(10);

    expect(save).toHaveBeenNthCalledWith(1, "one", "etag-1");
    expect(save).toHaveBeenNthCalledWith(2, "two", "etag-2");
    expect(states).toContain("saved");

    coordinator.dispose();
    vi.useRealTimers();
  });

  it("stops on a typed conflict instead of silently overwriting", async () => {
    const conflict = new ApiClientError("stale", 409, {
      apiVersion: "v1",
      requestId: "f64c927d-436d-483b-97b7-e5a992d7e00d",
      code: "CONFLICT",
      message: "The model revision is stale."
    });
    const onConflict = vi.fn();
    const states: SaveState[] = [];
    const coordinator = new AutosaveCoordinator(
      "etag-1",
      async () => {
        throw conflict;
      },
      {
        onState: (state) => states.push(state),
        onSaved: () => undefined,
        onConflict,
        onError: () => undefined
      },
      0
    );

    coordinator.update("local");
    await coordinator.flush();

    expect(onConflict).toHaveBeenCalledWith(conflict, "local", "etag-1");
    expect(states.at(-1)).toBe("conflict");
    coordinator.dispose();
  });
});
