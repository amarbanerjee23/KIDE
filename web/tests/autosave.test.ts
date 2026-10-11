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
  it("reports pending versus settled writes without treating an idle coordinator as busy", async () => {
    let finish!: (model: Model) => void;
    const pendingWrite = new Promise<Model>((resolve) => { finish = resolve; });
    const coordinator = new AutosaveCoordinator(
      "etag-1", () => pendingWrite, {
        onState: () => undefined, onSaved: () => undefined,
        onConflict: () => undefined, onError: () => undefined
      }
    );
    expect(coordinator.hasUnsettledWork).toBe(false);
    coordinator.update("local");
    expect(coordinator.hasUnsettledWork).toBe(true);
    const saving = coordinator.flush();
    expect(coordinator.hasUnsettledWork).toBe(true);
    finish(model("local", "etag-2", "2"));
    await saving;
    expect(coordinator.hasUnsettledWork).toBe(false);
    coordinator.dispose();
    expect(coordinator.hasUnsettledWork).toBe(false);
  });

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
  it("ignores a successful write that finishes after project disposal", async () => {
    let complete!: (value: Model) => void;
    const deferred = new Promise<Model>((resolve) => { complete = resolve; });
    const save = vi.fn().mockReturnValue(deferred);
    const onSaved = vi.fn();
    const onState = vi.fn();
    const coordinator = new AutosaveCoordinator("etag-1", save, {
      onState, onSaved, onConflict: vi.fn(), onError: vi.fn()
    }, 100);

    coordinator.update("first change");
    const pending = coordinator.flush();
    coordinator.update("second change");
    coordinator.dispose();
    complete(model("first change", "etag-2", "2"));
    await pending;

    expect(save).toHaveBeenCalledTimes(1);
    expect(onSaved).not.toHaveBeenCalled();
    expect(onState).toHaveBeenCalledWith("saving");
    expect(onState).not.toHaveBeenCalledWith("saved");
  });

  it("ignores a late save conflict and does not revive old conflict UI", async () => {
    let fail!: (error: Error) => void;
    const deferred = new Promise<Model>((_, reject) => { fail = reject; });
    const conflict = new ApiClientError("stale etag", 409, {
      apiVersion: "v1",
      requestId: "f64c927d-436d-483b-97b7-e5a992d7e00d",
      code: "CONFLICT",
      message: "Revision changed"
    });
    const onConflict = vi.fn();
    const onState = vi.fn();
    const coordinator = new AutosaveCoordinator("etag-1", () => deferred, {
      onState, onSaved: vi.fn(), onConflict, onError: vi.fn()
    });

    coordinator.update("unsaved content");
    const pending = coordinator.flush();
    coordinator.dispose();
    fail(conflict);
    await pending;
    expect(onConflict).not.toHaveBeenCalled();
    expect(onState).not.toHaveBeenCalledWith("conflict");
  });

  it("ignores late transport failures from a disposed autosave", async () => {
    let fail!: (error: Error) => void;
    const deferred = new Promise<Model>((_, reject) => { fail = reject; });
    const onError = vi.fn();
    const coordinator = new AutosaveCoordinator("etag-1", () => deferred, {
      onState: vi.fn(), onSaved: vi.fn(), onConflict: vi.fn(), onError
    });
    coordinator.update("data");
    const pending = coordinator.flush();
    coordinator.dispose();
    fail(new Error("server unavailable"));
    await pending;
    expect(onError).not.toHaveBeenCalled();
  });

});
