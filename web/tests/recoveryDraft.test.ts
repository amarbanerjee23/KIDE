import { describe, expect, it, vi } from "vitest";
import {
  clearRecoveryDraft,
  loadRecoveryDraft,
  MAX_RECOVERY_DRAFT_BYTES,
  recoveryDraftKey,
  saveRecoveryDraft
} from "../src/recoveryDraft";

function store() {
  const map = new Map<string, string>();
  return {
    getItem: (key: string) => map.get(key) ?? null,
    setItem: (key: string, value: string) => { map.set(key, value); },
    removeItem: (key: string) => { map.delete(key); }
  };
}

const base = {
  uid: "firebase-uid-A", projectId: "engineering-A",
  path: "activity/main.activity", baseEtag: "sha-a",
  text: "Activity Main { local change }", savedAt: 1_760_000_000_000
};

describe("session-scoped remote recovery drafts", () => {
  it("stores and loads the exact draft for the same Firebase user/project/file", () => {
    const s = store();
    expect(saveRecoveryDraft(s, base)).toBe(true);
    expect(loadRecoveryDraft(s, base.uid, base.projectId, base.path)).toEqual(base);
  });

  it("never reads a draft from a different Firebase principal", () => {
    const s = store();
    saveRecoveryDraft(s, base);
    expect(loadRecoveryDraft(s, "firebase-uid-B", base.projectId, base.path)).toBeUndefined();
  });

  it("does not cross project or file boundaries and encodes delimiters", () => {
    const s = store();
    saveRecoveryDraft(s, base);
    expect(loadRecoveryDraft(s, base.uid, "engineering-B", base.path)).toBeUndefined();
    expect(loadRecoveryDraft(s, base.uid, base.projectId, "other.activity")).toBeUndefined();
    expect(recoveryDraftKey("id:one", "proj/one", "a:b.activity"))
      .not.toBe(recoveryDraftKey("id", "one:proj/one", "a:b.activity"));
  });

  it("refuses overlarge model text rather than persisting unbounded data", () => {
    const s = store();
    expect(saveRecoveryDraft(s, { ...base, text: "x".repeat(MAX_RECOVERY_DRAFT_BYTES + 1) })).toBe(false);
    expect(loadRecoveryDraft(s, base.uid, base.projectId, base.path)).toBeUndefined();
  });

  it("does not claim protection when browser storage quota is exhausted", () => {
    const s = {
      getItem: vi.fn(() => null),
      setItem: vi.fn(() => { throw new Error("QuotaExceededError"); }),
      removeItem: vi.fn()
    };
    expect(saveRecoveryDraft(s, base)).toBe(false);
  });

  it("fails safely when browser security policy blocks storage reads or removal", () => {
    const inaccessible = {
      getItem: () => { throw new Error("SecurityError"); },
      setItem: () => { throw new Error("SecurityError"); },
      removeItem: () => { throw new Error("SecurityError"); }
    };
    expect(saveRecoveryDraft(inaccessible, base)).toBe(false);
    expect(loadRecoveryDraft(inaccessible, base.uid, base.projectId, base.path))
      .toBeUndefined();
    expect(() => clearRecoveryDraft(
      inaccessible, base.uid, base.projectId, base.path
    )).not.toThrow();
  });

  it("rejects corrupt and cross-scoped payloads even if stored under a matching key", () => {
    const s = store();
    const key = recoveryDraftKey(base.uid, base.projectId, base.path);
    s.setItem(key, "{not-json");
    expect(loadRecoveryDraft(s, base.uid, base.projectId, base.path)).toBeUndefined();
    s.setItem(key, JSON.stringify({ ...base, uid: "firebase-uid-B", version: 1 }));
    expect(loadRecoveryDraft(s, base.uid, base.projectId, base.path)).toBeUndefined();
    s.setItem(key, JSON.stringify({ ...base, version: 9 }));
    expect(loadRecoveryDraft(s, base.uid, base.projectId, base.path)).toBeUndefined();
  });

  it("clears only the specified user/project draft after a confirmed save", () => {
    const s = store();
    saveRecoveryDraft(s, base);
    saveRecoveryDraft(s, { ...base, uid: "firebase-uid-B", text: "other user" });
    clearRecoveryDraft(s, base.uid, base.projectId, base.path);
    expect(loadRecoveryDraft(s, base.uid, base.projectId, base.path)).toBeUndefined();
    expect(loadRecoveryDraft(s, "firebase-uid-B", base.projectId, base.path)?.text)
      .toBe("other user");
  });
});
