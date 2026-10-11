import { afterEach, describe, expect, it, vi } from "vitest";
import { FirebaseAuthClient } from "../src/firebaseAuth";

afterEach(() => {
  vi.unstubAllGlobals();
});

describe("FirebaseAuthClient", () => {
  it("registers with email and password and establishes a session", async () => {
    const fetchMock = vi.fn(async (_input: RequestInfo | URL, _init?: RequestInit) =>
      new Response(
        JSON.stringify({
          localId: "uid-new",
          email: "new@example.test",
          idToken: "id-token-new",
          refreshToken: "refresh-new",
          expiresIn: "3600"
        }),
        { status: 200 }
      )
    );
    vi.stubGlobal("fetch", fetchMock);

    const auth = new FirebaseAuthClient("web-api-key");
    const user = await auth.registerWithEmailPassword(
      "new@example.test",
      "secret1"
    );

    expect(user).toEqual({ uid: "uid-new", email: "new@example.test" });
    expect(await auth.idToken()).toBe("id-token-new");
    expect(String(fetchMock.mock.calls[0]?.[0])).toContain(
      "identitytoolkit.googleapis.com/v1/accounts:signUp"
    );
  });

  it("maps duplicate registration to a safe user-facing error", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn(async () =>
        new Response(
          JSON.stringify({ error: { message: "EMAIL_EXISTS" } }),
          { status: 400 }
        )
      )
    );

    const auth = new FirebaseAuthClient("web-api-key");
    await expect(
      auth.registerWithEmailPassword("existing@example.test", "secret1")
    ).rejects.toThrow("An account already exists for this email.");
  });

  it("signs in and reuses a current ID token", async () => {
    const fetchMock = vi.fn(async () =>
      new Response(
        JSON.stringify({
          localId: "uid-1",
          email: "user@example.test",
          idToken: "id-token-1",
          refreshToken: "refresh-1",
          expiresIn: "3600"
        }),
        { status: 200 }
      )
    );
    vi.stubGlobal("fetch", fetchMock);

    const auth = new FirebaseAuthClient("web-api-key");
    const user = await auth.signInWithEmailPassword(
      "user@example.test",
      "password"
    );

    expect(user.uid).toBe("uid-1");
    expect(await auth.idToken()).toBe("id-token-1");
    expect(fetchMock).toHaveBeenCalledTimes(1);
  });

  it("refreshes an ID token before expiry", async () => {
    const fetchMock = vi
      .fn()
      .mockResolvedValueOnce(
        new Response(
          JSON.stringify({
            localId: "uid-1",
            email: "user@example.test",
            idToken: "short-token",
            refreshToken: "refresh-1",
            expiresIn: "1"
          }),
          { status: 200 }
        )
      )
      .mockResolvedValueOnce(
        new Response(
          JSON.stringify({
            user_id: "uid-1",
            id_token: "refreshed-token",
            refresh_token: "refresh-2",
            expires_in: "3600"
          }),
          { status: 200 }
        )
      );
    vi.stubGlobal("fetch", fetchMock);

    const auth = new FirebaseAuthClient("web-api-key");
    await auth.signInWithEmailPassword("user@example.test", "password");

    expect(await auth.idToken()).toBe("refreshed-token");
    expect(fetchMock).toHaveBeenCalledTimes(2);
    expect(String(fetchMock.mock.calls[1]?.[0])).toContain(
      "securetoken.googleapis.com/v1/token"
    );
  });

  it("does not expose Firebase provider diagnostics for bad credentials", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn(async () =>
        new Response(
          JSON.stringify({ error: { message: "INVALID_LOGIN_CREDENTIALS" } }),
          { status: 400 }
        )
      )
    );

    const auth = new FirebaseAuthClient("web-api-key");
    await expect(
      auth.signInWithEmailPassword("user@example.test", "wrong")
    ).rejects.toThrow("Email or password is incorrect.");
  });
  it("shares one rotating refresh token request across concurrent API consumers", async () => {
    let finishRefresh!: (response: Response) => void;
    const deferred = new Promise<Response>((resolve) => { finishRefresh = resolve; });
    const fetchMock = vi.fn()
      .mockResolvedValueOnce(new Response(JSON.stringify({
        localId: "uid-1", email: "engineer@example.test", idToken: "expired",
        refreshToken: "refresh-1", expiresIn: "1"
      }), { status: 200 }))
      .mockReturnValueOnce(deferred);
    vi.stubGlobal("fetch", fetchMock);
    const auth = new FirebaseAuthClient("api-key");
    await auth.signInWithEmailPassword("engineer@example.test", "password");
    const first = auth.idToken();
    const second = auth.idToken();
    expect(fetchMock).toHaveBeenCalledTimes(2);
    finishRefresh(new Response(JSON.stringify({
      user_id: "uid-1", id_token: "new-id", refresh_token: "rotated-2",
      expires_in: "3600"
    }), { status: 200 }));
    await expect(first).resolves.toBe("new-id");
    await expect(second).resolves.toBe("new-id");
    expect(fetchMock).toHaveBeenCalledTimes(2);
    expect(await auth.idToken()).toBe("new-id");
  });

  it("never restores a signed-out identity from a delayed successful refresh", async () => {
    let finishRefresh!: (response: Response) => void;
    const deferred = new Promise<Response>((resolve) => { finishRefresh = resolve; });
    vi.stubGlobal("fetch", vi.fn()
      .mockResolvedValueOnce(new Response(JSON.stringify({
        localId: "uid-1", email: "first@example.test", idToken: "expired",
        refreshToken: "refresh-1", expiresIn: "1"
      }), { status: 200 }))
      .mockReturnValueOnce(deferred));
    const auth = new FirebaseAuthClient("api-key");
    await auth.signInWithEmailPassword("first@example.test", "password");
    const pending = auth.idToken();
    auth.signOut();
    finishRefresh(new Response(JSON.stringify({
      user_id: "uid-1", id_token: "revived", refresh_token: "rotated",
      expires_in: "3600"
    }), { status: 200 }));
    await expect(pending).rejects.toThrow("superseded");
    expect(auth.user).toBeUndefined();
    await expect(auth.idToken()).rejects.toThrow("Sign in");
  });

  it("keeps the new account when an old refresh fails after account switch", async () => {
    let finishOldRefresh!: (response: Response) => void;
    const deferred = new Promise<Response>((resolve) => { finishOldRefresh = resolve; });
    vi.stubGlobal("fetch", vi.fn()
      .mockResolvedValueOnce(new Response(JSON.stringify({
        localId: "uid-A", email: "a@example.test", idToken: "expired-A",
        refreshToken: "refresh-A", expiresIn: "1"
      }), { status: 200 }))
      .mockReturnValueOnce(deferred)
      .mockResolvedValueOnce(new Response(JSON.stringify({
        localId: "uid-B", email: "b@example.test", idToken: "token-B",
        refreshToken: "refresh-B", expiresIn: "3600"
      }), { status: 200 })));
    const auth = new FirebaseAuthClient("api-key");
    await auth.signInWithEmailPassword("a@example.test", "password");
    const old = auth.idToken();
    await auth.signInWithEmailPassword("b@example.test", "password");
    finishOldRefresh(new Response(JSON.stringify({ error: { message: "TOKEN_EXPIRED" } }),
      { status: 400 }));
    await expect(old).rejects.toThrow("superseded");
    expect(auth.user?.uid).toBe("uid-B");
    expect(await auth.idToken()).toBe("token-B");
  });

  it("rejects a late sign-in response after explicit sign-out", async () => {
    let finishSignIn!: (response: Response) => void;
    const delayed = new Promise<Response>((resolve) => { finishSignIn = resolve; });
    vi.stubGlobal("fetch", vi.fn().mockReturnValue(delayed));
    const auth = new FirebaseAuthClient("api-key");
    const pending = auth.signInWithEmailPassword("a@example.test", "password");
    auth.signOut();
    finishSignIn(new Response(JSON.stringify({
      localId: "uid-A", email: "a@example.test", idToken: "token-A",
      refreshToken: "refresh-A", expiresIn: "3600"
    }), { status: 200 }));
    await expect(pending).rejects.toThrow("superseded");
    expect(auth.user).toBeUndefined();
  });

  it("rejects an older sign-in completing after a newer account signs in", async () => {
    let finishFirst!: (response: Response) => void;
    const delayed = new Promise<Response>((resolve) => { finishFirst = resolve; });
    vi.stubGlobal("fetch", vi.fn()
      .mockReturnValueOnce(delayed)
      .mockResolvedValueOnce(new Response(JSON.stringify({
        localId: "uid-B", email: "b@example.test", idToken: "token-B",
        refreshToken: "refresh-B", expiresIn: "3600"
      }), { status: 200 })));
    const auth = new FirebaseAuthClient("api-key");
    const old = auth.signInWithEmailPassword("a@example.test", "password");
    await auth.signInWithEmailPassword("b@example.test", "password");
    finishFirst(new Response(JSON.stringify({
      localId: "uid-A", email: "a@example.test", idToken: "token-A",
      refreshToken: "refresh-A", expiresIn: "3600"
    }), { status: 200 }));
    await expect(old).rejects.toThrow("superseded");
    expect(auth.user?.uid).toBe("uid-B");
    expect(await auth.idToken()).toBe("token-B");
  });

  it("fails closed if Firebase refresh returns a different user ID", async () => {
    vi.stubGlobal("fetch", vi.fn()
      .mockResolvedValueOnce(new Response(JSON.stringify({
        localId: "uid-A", email: "a@example.test", idToken: "expired",
        refreshToken: "refresh-A", expiresIn: "1"
      }), { status: 200 }))
      .mockResolvedValueOnce(new Response(JSON.stringify({
        user_id: "uid-B", id_token: "foreign-token",
        refresh_token: "foreign-refresh", expires_in: "3600"
      }), { status: 200 })));
    const auth = new FirebaseAuthClient("api-key");
    await auth.signInWithEmailPassword("a@example.test", "password");
    await expect(auth.idToken()).rejects.toThrow("changed identity");
    expect(auth.user).toBeUndefined();
  });

  it("retries Firebase 503 without clearing identity or returning stale credentials", async () => {
    const fetchMock = vi.fn()
      .mockResolvedValueOnce(new Response(JSON.stringify({
        localId: "uid-A", email: "a@example.test", idToken: "expired",
        refreshToken: "refresh-A", expiresIn: "1"
      }), { status: 200 }))
      .mockResolvedValueOnce(new Response(JSON.stringify({
        error: { message: "SERVICE_UNAVAILABLE" }
      }), { status: 503 }))
      .mockResolvedValueOnce(new Response(JSON.stringify({
        user_id: "uid-A", id_token: "recovered",
        refresh_token: "rotated", expires_in: "3600"
      }), { status: 200 }));
    vi.stubGlobal("fetch", fetchMock);
    const auth = new FirebaseAuthClient("api-key");
    await auth.signInWithEmailPassword("a@example.test", "password");
    await expect(auth.idToken()).rejects.toThrow("temporarily unavailable");
    expect(auth.user?.uid).toBe("uid-A");
    await expect(auth.idToken()).resolves.toBe("recovered");
    expect(fetchMock).toHaveBeenCalledTimes(3);
  });

  it("retains the identity across a transient Firebase refresh outage", async () => {
    const fetchMock = vi.fn()
      .mockResolvedValueOnce(new Response(JSON.stringify({
        localId: "uid-A", email: "a@example.test", idToken: "expired",
        refreshToken: "refresh-A", expiresIn: "1"
      }), { status: 200 }))
      .mockRejectedValueOnce(new Error("temporary network failure"))
      .mockResolvedValueOnce(new Response(JSON.stringify({
        user_id: "uid-A", id_token: "recovered",
        refresh_token: "rotated", expires_in: "3600"
      }), { status: 200 }));
    vi.stubGlobal("fetch", fetchMock);
    const auth = new FirebaseAuthClient("api-key");
    await auth.signInWithEmailPassword("a@example.test", "password");
    await expect(auth.idToken()).rejects.toThrow("could not reach");
    expect(auth.user?.uid).toBe("uid-A");
    await expect(auth.idToken()).resolves.toBe("recovered");
    expect(fetchMock).toHaveBeenCalledTimes(3);
  });

});
