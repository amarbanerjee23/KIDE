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
  private epoch = 0;
  private refreshPending: Promise<string> | undefined;

  constructor(private readonly apiKey: string) {}

  get user(): FirebaseSignedInUser | undefined {
    return this.userValue;
  }

  /** Changes whenever sign-in, registration or sign-out replaces an identity. */
  get sessionVersion(): number {
    return this.epoch;
  }

  async registerWithEmailPassword(
    email: string,
    password: string
  ): Promise<FirebaseSignedInUser> {
    this.requireCredentials(email, password);
    if (password.length < 6) {
      throw new FirebaseAuthError("Password must be at least 6 characters.");
    }
    const epoch = this.beginAuthentication();
    const response = await fetch(
      `https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=${encodeURIComponent(this.apiKey)}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email: email.trim(), password, returnSecureToken: true
        })
      }
    );
    const body = await parseJson(response);
    return this.acceptPasswordResponse(epoch, response, body, email.trim(),
      "Firebase registration failed.", "Firebase registration returned an incomplete session.");
  }

  async signInWithEmailPassword(
    email: string,
    password: string
  ): Promise<FirebaseSignedInUser> {
    this.requireCredentials(email, password);
    const epoch = this.beginAuthentication();
    const response = await fetch(
      `https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=${encodeURIComponent(this.apiKey)}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email: email.trim(), password, returnSecureToken: true
        })
      }
    );
    const body = await parseJson(response);
    return this.acceptPasswordResponse(epoch, response, body, email.trim(),
      "Firebase sign-in failed.", "Firebase sign-in returned an incomplete session.");
  }

  async idToken(): Promise<string> {
    if (!this.apiKey.trim()) {
      throw new FirebaseAuthError("Firebase Web API key is not configured.");
    }
    if (!this.userValue || !this.refreshTokenValue) {
      throw new FirebaseAuthError("Sign in to Firebase first.");
    }
    if (this.idTokenValue && Date.now() < this.expiresAt - 60_000) {
      return this.idTokenValue;
    }

    // Firebase rotates refresh tokens. Two simultaneous refreshes could
    // invalidate each other and wrongly sign a healthy engineer out.
    if (this.refreshPending) return this.refreshPending;
    const epoch = this.epoch;
    const uid = this.userValue.uid;
    const refreshToken = this.refreshTokenValue;
    const pending = this.refreshIdentity(epoch, uid, refreshToken);
    this.refreshPending = pending;
    try {
      return await pending;
    } finally {
      if (this.refreshPending === pending) this.refreshPending = undefined;
    }
  }

  signOut(): void {
    ++this.epoch;
    this.clearCredentials();
    this.refreshPending = undefined;
  }

  private requireCredentials(email: string, password: string): void {
    if (!this.apiKey.trim()) {
      throw new FirebaseAuthError("Firebase Web API key is not configured.");
    }
    if (!email.trim() || !password) {
      throw new FirebaseAuthError("Email and password are required.");
    }
  }

  private beginAuthentication(): number {
    // Clear any prior identity immediately. A late response from an older
    // sign-in, registration or refresh is never allowed to restore it.
    this.signOut();
    return this.epoch;
  }

  private clearCredentials(): void {
    this.idTokenValue = "";
    this.refreshTokenValue = "";
    this.expiresAt = 0;
    this.userValue = undefined;
  }

  private assertCurrent(epoch: number): void {
    if (epoch !== this.epoch) {
      throw new FirebaseAuthError("Authentication request was superseded.");
    }
  }

  private acceptPasswordResponse(
    epoch: number,
    response: Response,
    body: unknown,
    email: string,
    failureMessage: string,
    incompleteMessage: string
  ): FirebaseSignedInUser {
    this.assertCurrent(epoch);
    if (!response.ok) {
      throw new FirebaseAuthError(firebaseMessage(body, failureMessage));
    }
    const result = body as Partial<FirebasePasswordResponse> | undefined;
    if (!result?.localId || !result.idToken ||
        !result.refreshToken || !result.expiresIn) {
      throw new FirebaseAuthError(incompleteMessage);
    }
    const lifetime = seconds(result.expiresIn);
    this.assertCurrent(epoch);
    this.idTokenValue = result.idToken;
    this.refreshTokenValue = result.refreshToken;
    this.expiresAt = Date.now() + lifetime * 1000;
    this.userValue = {
      uid: result.localId,
      email: result.email ?? email
    };
    return this.userValue;
  }

  private async refreshIdentity(
    epoch: number, uid: string, refreshToken: string
  ): Promise<string> {
    const current = () =>
      epoch === this.epoch && this.userValue?.uid === uid &&
      this.refreshTokenValue === refreshToken;
    const body = new URLSearchParams({
      grant_type: "refresh_token", refresh_token: refreshToken
    });
    let response: Response;
    let parsed: unknown;
    try {
      response = await fetch(
        `https://securetoken.googleapis.com/v1/token?key=${encodeURIComponent(this.apiKey)}`,
        {
          method: "POST",
          headers: { "Content-Type": "application/x-www-form-urlencoded" },
          body
        }
      );
      parsed = await parseJson(response);
    } catch {
      if (!current()) throw new FirebaseAuthError("Authentication request was superseded.");
      // Transient transport errors do not erase the identity; the already
      // expired token cannot be used and the next request can retry refresh.
      throw new FirebaseAuthError("Firebase session refresh could not reach the provider.");
    }
    if (!current()) throw new FirebaseAuthError("Authentication request was superseded.");
    if (!response.ok) {
      if (response.status === 429 || response.status >= 500) {
        // Provider throttling/outage is not credential revocation. Keep the
        // identity, but never return an expired ID token as successful auth.
        throw new FirebaseAuthError("Firebase session refresh is temporarily unavailable.");
      }
      this.signOut();
      throw new FirebaseAuthError(firebaseMessage(parsed, "Firebase session refresh failed."));
    }
    const refreshed = parsed as Partial<FirebaseRefreshResponse> | undefined;
    if (!refreshed?.id_token || !refreshed.refresh_token ||
        !refreshed.expires_in || !refreshed.user_id ||
        refreshed.user_id !== uid) {
      this.signOut();
      throw new FirebaseAuthError("Firebase session refresh was incomplete or changed identity.");
    }
    let lifetime: number;
    try {
      lifetime = seconds(refreshed.expires_in);
    } catch {
      this.signOut();
      throw new FirebaseAuthError("Firebase session refresh returned an invalid lifetime.");
    }
    if (!current()) throw new FirebaseAuthError("Authentication request was superseded.");
    const email = this.userValue!.email;
    this.idTokenValue = refreshed.id_token;
    this.refreshTokenValue = refreshed.refresh_token;
    this.expiresAt = Date.now() + lifetime * 1000;
    this.userValue = { uid, email };
    return this.idTokenValue;
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
    case "EMAIL_EXISTS":
      return "An account already exists for this email.";
    case "WEAK_PASSWORD":
      return "Password must be at least 6 characters.";
    case "INVALID_EMAIL":
      return "Enter a valid email address.";
    case "USER_DISABLED":
      return "This Firebase account is disabled.";
    case "TOO_MANY_ATTEMPTS_TRY_LATER":
      return "Too many sign-in attempts. Try again later.";
    default:
      return fallback;
  }
}
