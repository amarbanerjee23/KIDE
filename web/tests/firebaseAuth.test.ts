import { afterEach, describe, expect, it, vi } from "vitest";
import { FirebaseAuthClient } from "../src/firebaseAuth";

afterEach(() => {
  vi.unstubAllGlobals();
});

describe("FirebaseAuthClient", () => {
  it("registers with email and password and establishes a session", async () => {
    const fetchMock = vi.fn(async () =>
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
});
