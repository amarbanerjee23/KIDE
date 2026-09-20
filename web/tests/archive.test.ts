import { describe, expect, it } from "vitest";
import { strToU8, zipSync } from "fflate";
import {
  editableText,
  exportProjectArchive,
  importProjectArchive,
  normalizeProjectPath
} from "../src/archive";

describe("project archive boundary", () => {
  it("preserves canonical project files across import/export", () => {
    const original = zipSync({
      ".kide/schema.properties": strToU8("schema.version=1\n"),
      "model.dml": strToU8("domain Golden")
    });
    const imported = importProjectArchive(original);
    expect(imported.map((entry) => entry.path)).toEqual([
      ".kide/schema.properties",
      "model.dml"
    ]);
    expect(editableText(imported[1])).toBe("domain Golden");

    const roundTrip = importProjectArchive(exportProjectArchive(imported));
    expect(roundTrip.map((entry) => entry.path)).toEqual(
      imported.map((entry) => entry.path)
    );
    expect(editableText(roundTrip[1])).toBe("domain Golden");
  });

  it("rejects path traversal and Eclipse workspace metadata", () => {
    expect(() => normalizeProjectPath("../escape.dml")).toThrow(/traversal/i);
    expect(() =>
      normalizeProjectPath(".metadata/workspace.properties")
    ).toThrow(/workspace metadata/i);
    expect(() => normalizeProjectPath("C:/escape.dml")).toThrow(/absolute/i);
  });
});
