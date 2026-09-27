export interface FirebaseSignedInUser {
  uid: string;
  email: string;
}

interface FirebasePasswordResponse {
  localId: string;
  email?: string;
  idToken: string;
  refreshToken: string;
  expiresIn: string;
}

interface FirebaseRefreshResponse {
  user_id: string;
  id_token: string;
  refresh_token: string;
  expires_in: string;
}

export class FirebaseAuthError extends Error {
  constructor(message: string) {
    super(message);
    this.name = "FirebaseAuthError";
  }
}

/**
 * Minimal Firebase Authentication browser client.
 *
 * Tokens remain in memory only. The ID token is refreshed through Firebase's
 * secure-token endpoint when it is within one minute of expiry.
 */
export class FirebaseAuthClient {
  private idTokenValue = "";
  private refreshTokenValue = "";
  private expiresAt = 0;
  private userValue: FirebaseSignedInUser | undefined;

  constructor(private readonly apiKey: string) {
    if (!apiKey.trim()) {
      throw new Error("VITE_FIREBASE_API_KEY is required.");
    }
  }

  get user(): FirebaseSignedInUser | undefined {
    return this.userValue;
  }

  async signInWithEmailPassword(
    email: string,
    password: string
  ): Promise<FirebaseSignedInUser> {
    if (!email.trim() || !password) {
      throw new FirebaseAuthError("Email and password are required.");
    }

    const response = await fetch(
      `https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=${encodeURIComponent(this.apiKey)}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email: email.trim(),
          password,
          returnSecureToken: true
        })
      }
    );

    const body = await parseJson(response);
    if (!response.ok) {
      throw new FirebaseAuthError(firebaseMessage(body, "Firebase sign-in failed."));
    }

    const result = body as Partial<FirebasePasswordResponse>;
    if (
      !result.localId ||
      !result.idToken ||
      !result.refreshToken ||
      !result.expiresIn
    ) {
      throw new FirebaseAuthError("Firebase sign-in returned an incomplete session.");
    }

    this.idTokenValue = result.idToken;
    this.refreshTokenValue = result.refreshToken;
    this.expiresAt = Date.now() + seconds(result.expiresIn) * 1000;
    this.userValue = {
      uid: result.localId,
      email: result.email ?? email.trim()
    };
    return this.userValue;
  }

  async idToken(): Promise<string> {
    if (!this.userValue || !this.refreshTokenValue) {
      throw new FirebaseAuthError("Sign in to Firebase first.");
    }
    if (this.idTokenValue && Date.now() < this.expiresAt - 60_000) {
      return this.idTokenValue;
    }

    const body = new URLSearchParams({
      grant_type: "refresh_token",
      refresh_token: this.refreshTokenValue
    });
    const response = await fetch(
      `https://securetoken.googleapis.com/v1/token?key=${encodeURIComponent(this.apiKey)}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body
      }
    );

    const parsed = await parseJson(response);
    if (!response.ok) {
      this.signOut();
      throw new FirebaseAuthError(
        firebaseMessage(parsed, "Firebase session refresh failed.")
      );
    }

    const refreshed = parsed as Partial<FirebaseRefreshResponse>;
    if (
      !refreshed.id_token ||
      !refreshed.refresh_token ||
      !refreshed.expires_in ||
      !refreshed.user_id
    ) {
      this.signOut();
      throw new FirebaseAuthError("Firebase session refresh was incomplete.");
    }

    this.idTokenValue = refreshed.id_token;
    this.refreshTokenValue = refreshed.refresh_token;
    this.expiresAt = Date.now() + seconds(refreshed.expires_in) * 1000;
    this.userValue = {
      uid: refreshed.user_id,
      email: this.userValue.email
    };
    return this.idTokenValue;
  }

  signOut(): void {
    this.idTokenValue = "";
    this.refreshTokenValue = "";
    this.expiresAt = 0;
    this.userValue = undefined;
  }
}

async function parseJson(response: Response): Promise<unknown> {
  const text = await response.text();
  if (!text) return undefined;
  try {
    return JSON.parse(text);
  } catch {
    return undefined;
  }
}

function seconds(raw: string): number {
  const value = Number.parseInt(raw, 10);
  if (!Number.isFinite(value) || value <= 0) {
    throw new FirebaseAuthError("Firebase token lifetime is invalid.");
  }
  return value;
}

function firebaseMessage(body: unknown, fallback: string): string {
  if (!body || typeof body !== "object") return fallback;
  const error = (body as { error?: { message?: unknown } }).error;
  if (!error || typeof error.message !== "string") return fallback;
  switch (error.message) {
    case "EMAIL_NOT_FOUND":
    case "INVALID_PASSWORD":
    case "INVALID_LOGIN_CREDENTIALS":
      return "Email or password is incorrect.";
    case "USER_DISABLED":
      return "This Firebase account is disabled.";
    case "TOO_MANY_ATTEMPTS_TRY_LATER":
      return "Too many sign-in attempts. Try again later.";
    default:
      return fallback;
  }
}
