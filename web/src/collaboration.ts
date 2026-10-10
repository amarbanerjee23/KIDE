import { ApiClientError, KideApiClient } from "./api";
import type { PresenceSession } from "./types";

export interface CollaborationEvents {
  onPresence(items: PresenceSession[]): void;
  onSession(session: PresenceSession): void;
  onError(error: Error): void;
}

export class CollaborationCoordinator {
  private sessionId: string | undefined;
  private modelId = "";
  private timer: ReturnType<typeof setInterval> | undefined;
  private disposed = false;

  constructor(
    private readonly client: Pick<
      KideApiClient,
      "joinPresence" | "heartbeatPresence" | "leavePresence" | "listPresence"
    >,
    private readonly projectId: string,
    private readonly events: CollaborationEvents,
    private readonly heartbeatMs = 20_000
  ) {}

  async connect(existingSessionId?: string, modelId?: string): Promise<PresenceSession> {
    if (this.disposed) throw new Error("Collaboration session is disposed.");
    this.modelId = modelId ?? "";
    const joined = await this.client.joinPresence(
      this.projectId,
      existingSessionId,
      this.modelId || undefined
    );
    if (this.disposed) {
      // An in-flight join can finish after project change or sign-out.
      // Do not resurrect presence or start a heartbeat for the old account.
      try {
        await this.client.leavePresence(this.projectId, joined.id);
      } catch {
        // The server also expires abandoned sessions.
      }
      throw new Error("Collaboration connection was superseded.");
    }
    this.sessionId = joined.id;
    this.events.onSession(joined);
    await this.refresh();
    if (this.disposed) throw new Error("Collaboration connection was superseded.");
    this.schedule();
    return joined;
  }

  setModel(modelId?: string): void {
    this.modelId = modelId ?? "";
    if (this.sessionId) void this.pulse();
  }

  async refresh(): Promise<void> {
    if (this.disposed) return;
    try {
      const presence = await this.client.listPresence(this.projectId);
      if (!this.disposed) this.events.onPresence(presence.items);
    } catch (error) {
      if (!this.disposed) this.events.onError(normalize(error));
    }
  }

  async disconnect(): Promise<void> {
    if (this.timer) clearInterval(this.timer);
    this.timer = undefined;
    const id = this.sessionId;
    this.sessionId = undefined;
    this.disposed = true;
    if (!id) return;
    try {
      await this.client.leavePresence(this.projectId, id);
    } catch {
      // Presence expires server-side; disconnect remains best effort.
    }
  }

  private schedule(): void {
    if (this.timer) clearInterval(this.timer);
    this.timer = setInterval(() => void this.pulse(), this.heartbeatMs);
  }

  private async pulse(): Promise<void> {
    if (this.disposed || !this.sessionId) return;
    const currentId = this.sessionId;
    try {
      const session = await this.client.heartbeatPresence(
        this.projectId,
        currentId,
        this.modelId || undefined
      );
      if (this.disposed) return;
      this.events.onSession(session);
      await this.refresh();
    } catch (error) {
      if (this.disposed) return;
      if (error instanceof ApiClientError && error.status === 404) {
        try {
          const rejoined = await this.client.joinPresence(
            this.projectId,
            currentId,
            this.modelId || undefined
          );
          if (this.disposed) {
            try {
              await this.client.leavePresence(this.projectId, rejoined.id);
            } catch {
              // Stale presence expires server-side.
            }
            return;
          }
          this.sessionId = rejoined.id;
          this.events.onSession(rejoined);
          await this.refresh();
          return;
        } catch (rejoinError) {
          if (!this.disposed) this.events.onError(normalize(rejoinError));
          return;
        }
      }
      this.events.onError(normalize(error));
    }
  }
}

function normalize(error: unknown): Error {
  return error instanceof Error ? error : new Error("Collaboration request failed.");
}
