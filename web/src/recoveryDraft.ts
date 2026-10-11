/**
 * Tab-session-only recovery for interrupted hosted-model autosaves.
 * A draft is NOT a server write and is never restored automatically.
 * Firebase tokens/passwords are never persisted.
 */
export interface RecoveryDraft {
  uid: string;
  projectId: string;
  path: string;
  baseEtag: string;
  text: string;
  savedAt: number;
}

type DraftStorage = Pick<Storage, "getItem" | "setItem" | "removeItem">;

const PREFIX = "kide:recovery:v1:";
export const MAX_RECOVERY_DRAFT_BYTES = 2 * 1024 * 1024;

export function recoveryDraftKey(uid: string, projectId: string, path: string): string {
  return PREFIX + [uid, projectId, path].map(encodeURIComponent).join(":");
}

export function saveRecoveryDraft(storage: DraftStorage, draft: RecoveryDraft): boolean {
  if (!validDraft(draft)) return false;
  try {
    storage.setItem(recoveryDraftKey(draft.uid, draft.projectId, draft.path),
      JSON.stringify({ ...draft, version: 1 }));
    return true;
  } catch {
    // Quota or disabled session storage cannot be silently described as saved.
    return false;
  }
}

export function loadRecoveryDraft(
  storage: DraftStorage, uid: string, projectId: string, path: string
): RecoveryDraft | undefined {
  try {
    const raw = storage.getItem(recoveryDraftKey(uid, projectId, path));
    if (!raw) return undefined;
    const parsed: unknown = JSON.parse(raw);
    if (!parsed || typeof parsed !== "object") return undefined;
    const item = parsed as Partial<RecoveryDraft> & { version?: number };
    if (item.version !== 1 || !validDraft(item)) return undefined;
    if (item.uid !== uid || item.projectId !== projectId || item.path !== path) {
      return undefined;
    }
    return {
      uid, projectId, path, baseEtag: item.baseEtag,
      text: item.text, savedAt: item.savedAt
    };
  } catch {
    return undefined;
  }
}

export function clearRecoveryDraft(
  storage: DraftStorage, uid: string, projectId: string, path: string
): void {
  try {
    storage.removeItem(recoveryDraftKey(uid, projectId, path));
  } catch {
    // Safe degradation when session storage is unavailable.
  }
}

function validDraft(
  item: Partial<RecoveryDraft>
): item is RecoveryDraft {
  return typeof item.uid === "string" && item.uid.length > 0 &&
    typeof item.projectId === "string" && item.projectId.length > 0 &&
    typeof item.path === "string" && item.path.length > 0 &&
    typeof item.baseEtag === "string" && item.baseEtag.length > 0 &&
    typeof item.text === "string" &&
    new TextEncoder().encode(item.text).byteLength <= MAX_RECOVERY_DRAFT_BYTES &&
    typeof item.savedAt === "number" && Number.isFinite(item.savedAt) &&
    item.savedAt > 0;
}
