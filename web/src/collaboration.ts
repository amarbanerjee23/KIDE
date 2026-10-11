import { ApiClientError, KideApiClient } from "./api";
import type { PresenceSession } from "./types";

export interface CollaborationEvents {
  onPresence(items: PresenceSession[]): void;
  onSession(session: PresenceSession): void;
  onError(error: Error): void;
}

/**
 * One browser editor owns one server presence session at a time.
 * Transient read/heartbeat failure is NOT a reason to POST another join.
 */
export class CollaborationCoordinator {
  private sessionId: string | undefined;
  private lastSession: PresenceSession | undefined;
  private joinPending: Promise<PresenceSession> | undefined;
  private heartbeatPending: Promise<void> | undefined;
  private modelId = "";
  private timer: ReturnType<typeof setInterval> | undefined;
  private disposed = false;

  constructor(
    private readonly client: Pick<
      KideApiClient,
      "joinPresence" | "heartbeatPresence" | "leavePresence" | "listPresence"
    >,
    readonly projectId: string,
    private readonly events: CollaborationEvents,
    private readonly heartbeatMs = 20_000
  ) {}

  get isActive(): boolean {
    // An in-flight join is owned by this coordinator, too. Callers must
    // await it rather than replacing it with a second parallel join.
    return !this.disposed && Boolean(this.sessionId || this.joinPending);
  }

  async connect(existingSessionId?: string, modelId?: string): Promise<PresenceSession> {
    if (this.disposed) throw new Error("Collaboration session is disposed.");
    // Avoid setModel here: it would start a heartbeat before joining completes.
    if (modelId !== undefined) this.modelId = modelId;
    if (this.joinPending) return this.joinPending;
    if (this.lastSession && this.sessionId) return this.lastSession;

    const pending = this.join(existingSessionId);
    this.joinPending = pending;
    try {
      return await pending;
    } finally {
      if (this.joinPending === pending) this.joinPending = undefined;
    }
  }

  private async join(existingSessionId?: string): Promise<PresenceSession> {
    const joined = await this.client.joinPresence(
      this.projectId, existingSessionId, this.modelId || undefined
    );
    if (this.disposed) {
      // The old project may have been closed while the join was in flight.
      try {
        await this.client.leavePresence(this.projectId, joined.id);
      } catch {
        // Server-side expiry is the fallback.
      }
      throw new Error("Collaboration connection was superseded.");
    }
    this.sessionId = joined.id;
    this.lastSession = joined;
    this.events.onSession(joined);
    await this.refresh();
    if (this.disposed) throw new Error("Collaboration connection was superseded.");
    this.schedule();
    return joined;
  }

  /**
   * Retry the existing session before considering a new join. Only a proven
   * 404 on the heartbeat may replace an expired server-side presence session.
   */
  async recover(): Promise<void> {
    if (this.disposed) return;
    if (this.joinPending) {
      await this.joinPending;
      return;
    }
    if (!this.sessionId) {
      await this.connect();
      return;
    }
    await this.pulse();
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
    this.lastSession = undefined;
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
    if (this.heartbeatPending) return this.heartbeatPending;
    const pending = this.pulseOnce();
    this.heartbeatPending = pending;
    try {
      await pending;
    } finally {
      if (this.heartbeatPending === pending) this.heartbeatPending = undefined;
    }
  }

  private async pulseOnce(): Promise<void> {
    if (this.disposed || !this.sessionId) return;
    const currentId = this.sessionId;
    try {
      const session = await this.client.heartbeatPresence(
        this.projectId, currentId, this.modelId || undefined
      );
      if (this.disposed || this.sessionId !== currentId) return;
      this.lastSession = session;
      this.events.onSession(session);
      await this.refresh();
    } catch (error) {
      if (this.disposed || this.sessionId !== currentId) return;
      if (error instanceof ApiClientError && error.status === 404) {
        try {
          const rejoined = await this.client.joinPresence(
            this.projectId, currentId, this.modelId || undefined
          );
          if (this.disposed || this.sessionId !== currentId) {
            try {
              await this.client.leavePresence(this.projectId, rejoined.id);
            } catch {
              // Expiry fallback for obsolete sessions.
            }
            return;
          }
          this.sessionId = rejoined.id;
          this.lastSession = rejoined;
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
