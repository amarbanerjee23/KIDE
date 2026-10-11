import { ApiClientError } from "./api";
import type { Model, SaveState } from "./types";

export interface AutosaveEvents {
  onState(state: SaveState): void;
  onSaved(model: Model): void;
  onConflict(error: ApiClientError, localContent: string, expectedEtag: string): void;
  onError(error: Error): void;
}

export class AutosaveCoordinator {
  private timer: ReturnType<typeof setTimeout> | undefined;
  private pending: string | undefined;
  private saving = false;
  private disposed = false;

  constructor(
    private etag: string,
    private readonly save: (content: string, expectedEtag: string) => Promise<Model>,
    private readonly events: AutosaveEvents,
    private readonly delayMs = 800
  ) {}

  update(content: string): void {
    if (this.disposed) return;
    this.pending = content;
    this.events.onState("pending");
    this.schedule(this.delayMs);
  }

  async flush(): Promise<void> {
    if (this.disposed || this.saving || this.pending === undefined) return;
    if (this.timer) clearTimeout(this.timer);
    this.timer = undefined;

    const content = this.pending;
    const expectedEtag = this.etag;
    this.pending = undefined;
    this.saving = true;
    this.events.onState("saving");

    try {
      const saved = await this.save(content, expectedEtag);
      // A completed server write from a closed tab/project must not revive
      // stale editor entries, save state, conflict dialogs or autosave loops.
      if (this.disposed) return;
      this.etag = saved.etag;
      this.events.onSaved(saved);
      this.events.onState(this.pending === undefined ? "saved" : "pending");
    } catch (error) {
      if (this.disposed) return;
      const normalized = error instanceof Error ? error : new Error("Autosave failed.");
      if (normalized instanceof ApiClientError && normalized.code === "CONFLICT") {
        this.pending = content;
        this.events.onState("conflict");
        this.events.onConflict(normalized, content, expectedEtag);
        return;
      }
      this.pending = content;
      this.events.onState("error");
      this.events.onError(normalized);
      return;
    } finally {
      this.saving = false;
    }

    if (this.pending !== undefined) this.schedule(0);
  }

  updateRevision(etag: string): void {
    if (!this.disposed) this.etag = etag;
  }

  dispose(): void {
    this.disposed = true;
    this.pending = undefined;
    if (this.timer) clearTimeout(this.timer);
    this.timer = undefined;
  }

  private schedule(delay: number): void {
    if (this.disposed) return;
    if (this.timer) clearTimeout(this.timer);
    this.timer = setTimeout(() => void this.flush(), delay);
  }
}
