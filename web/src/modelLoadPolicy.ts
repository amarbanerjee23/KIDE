/**
 * Protect a Monaco draft from asynchronous server-model hydration.
 *
 * The caller captures this snapshot before issuing GET /models/{path}.
 * A different text value or ETag on completion proves another operation or
 * local edit won the race; even an explicit reload may not overwrite it.
 */
export interface ModelLoadSnapshot {
  text: string | undefined;
  etag: string | undefined;
}

export interface ModelLoadCurrent extends ModelLoadSnapshot {
  dirty: boolean;
  autosaveActive: boolean;
}

export type ModelLoadBlockReason = "changed-while-loading" | "unsaved-draft";

export function modelLoadBlockReason(
  initial: ModelLoadSnapshot,
  current: ModelLoadCurrent,
  allowDiscard: boolean
): ModelLoadBlockReason | undefined {
  if (initial.text !== current.text || initial.etag !== current.etag) {
    return "changed-while-loading";
  }
  if (!allowDiscard && (current.dirty || current.autosaveActive)) {
    return "unsaved-draft";
  }
  return undefined;
}
