import { describe, expect, it } from "vitest";
import { modelLoadBlockReason, type ModelLoadSnapshot } from "../src/modelLoadPolicy";

const before: ModelLoadSnapshot = { text: "DataModel A {}", etag: "rev-1" };

describe("modelLoadBlockReason", () => {
  it("permits clean and unmodified lazy hydration", () => {
    expect(modelLoadBlockReason(before, {
      ...before, dirty: false, autosaveActive: false
    }, false)).toBeUndefined();
  });

  it("blocks a dirty draft even when its request completed successfully", () => {
    expect(modelLoadBlockReason(before, {
      ...before, dirty: true, autosaveActive: false
    }, false)).toBe("unsaved-draft");
  });

  it("blocks an active autosave from being replaced by an old model", () => {
    expect(modelLoadBlockReason(before, {
      ...before, dirty: false, autosaveActive: true
    }, false)).toBe("unsaved-draft");
  });

  it("permits an explicit server reload after the caller backs up its draft", () => {
    expect(modelLoadBlockReason(before, {
      ...before, dirty: true, autosaveActive: true
    }, true)).toBeUndefined();
  });

  it("blocks a new local edit made while even an explicit reload is in flight", () => {
    expect(modelLoadBlockReason(before, {
      text: "DataModel A { edit }", etag: "rev-1",
      dirty: true, autosaveActive: true
    }, true)).toBe("changed-while-loading");
  });

  it("rejects a late model response when the saved revision has advanced", () => {
    expect(modelLoadBlockReason(before, {
      text: before.text, etag: "rev-2",
      dirty: false, autosaveActive: false
    }, true)).toBe("changed-while-loading");
  });

  it("rejects a late hydration after another request created the model", () => {
    expect(modelLoadBlockReason({ text: undefined, etag: "rev-1" }, {
      text: "DataModel A {}", etag: "rev-1", dirty: false, autosaveActive: false
    }, false)).toBe("changed-while-loading");
  });
});
