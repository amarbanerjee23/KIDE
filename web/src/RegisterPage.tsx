import type { FormEvent } from "react";

interface Props {
  email: string;
  password: string;
  confirmPassword: string;
  authStatus: string;
  configured: boolean;
  busy?: boolean;
  notice?: string;
  onEmail(value: string): void;
  onPassword(value: string): void;
  onConfirmPassword(value: string): void;
  onRegister(): void;
  onOpenSignIn(): void;
}

export function RegisterPage({
  email,
  password,
  confirmPassword,
  authStatus,
  configured,
  busy = false,
  notice,
  onEmail,
  onPassword,
  onConfirmPassword,
  onRegister,
  onOpenSignIn
}: Props) {
  const passwordsMatch = password === confirmPassword;
  const ready =
    configured &&
    !busy &&
    Boolean(email.trim()) &&
    password.length >= 6 &&
    passwordsMatch;

  function submit(event: FormEvent) {
    event.preventDefault();
    if (ready) onRegister();
  }

  return (
    <main className="landing-page">
      <header className="landing-nav">
        <a className="brand-lockup" href="/" aria-label="KIDE home">
          <span className="brand-mark">K</span>
          <span>
            <strong>KIDE</strong>
            <small>Engineering Environment</small>
          </span>
        </a>
        <button
          className="landing-auth-link"
          type="button"
          onClick={onOpenSignIn}
        >
          Back to sign in
        </button>
      </header>

      <section className="landing-hero registration-hero">
        <div className="landing-copy">
          <p className="landing-kicker">CREATE YOUR KIDE ACCOUNT</p>
          <h1>Register for the engineering workspace.</h1>
          <p className="landing-lead">
            Create an account with your email address and password. Your new
            Firebase session is established immediately after registration so
            you can continue into the KIDE engineering workspace.
          </p>
        </div>

        <form className="signin-card registration-card" onSubmit={submit}>
          <div className="signin-heading">
            <span className="signin-icon" aria-hidden="true">+</span>
            <div>
              <p>SECURE ACCESS</p>
              <h2>Create account</h2>
            </div>
          </div>

          <label>
            Email
            <input
              aria-label="Registration email"
              type="email"
              autoComplete="email"
              placeholder="engineer@example.com"
              value={email}
              onChange={(event) => onEmail(event.target.value)}
            />
          </label>

          <label>
            Password
            <input
              aria-label="Registration password"
              type="password"
              autoComplete="new-password"
              value={password}
              onChange={(event) => onPassword(event.target.value)}
            />
          </label>

          <label>
            Confirm password
            <input
              aria-label="Confirm registration password"
              type="password"
              autoComplete="new-password"
              value={confirmPassword}
              onChange={(event) => onConfirmPassword(event.target.value)}
            />
          </label>

          <p className="password-guidance">
            Use at least 6 characters.
            {confirmPassword && !passwordsMatch ? " Passwords do not match." : ""}
          </p>

          <button
            type="submit"
            className="signin-primary"
            disabled={!ready}
          >
            {busy ? "Creating account…" : "Create account"}
          </button>

          <button
            type="button"
            className="signin-secondary"
            onClick={onOpenSignIn}
          >
            Already have an account? Sign in
          </button>

          <p className="signin-status" aria-live="polite">{authStatus}</p>

          {!configured && (
            <p className="signin-warning">
              Firebase authentication is not configured for this deployment.
            </p>
          )}
          {notice && <p className="signin-notice" role="status">{notice}</p>}
        </form>
      </section>

      <footer className="landing-footer">
        <span>KIDE Web</span>
        <span>Email/password registration · Firebase Authentication</span>
      </footer>
    </main>
  );
}
