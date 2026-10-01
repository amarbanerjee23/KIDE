import type { FormEvent } from "react";

interface Props {
  email: string;
  password: string;
  authStatus: string;
  signedInEmail?: string;
  configured: boolean;
  busy?: boolean;
  notice?: string;
  onEmail(value: string): void;
  onPassword(value: string): void;
  onSignIn(): void;
  onSignOut(): void;
  onOpenWorkspace(): void;
}

export function LandingPage({
  email,
  password,
  authStatus,
  signedInEmail,
  configured,
  busy = false,
  notice,
  onEmail,
  onPassword,
  onSignIn,
  onSignOut,
  onOpenWorkspace
}: Props) {
  function submit(event: FormEvent) {
    event.preventDefault();
    if (!busy && configured && !signedInEmail) onSignIn();
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
          type="button"
          className="landing-workspace-link"
          onClick={onOpenWorkspace}
        >
          Open Engineering Workspace
          <span aria-hidden="true">→</span>
        </button>
      </header>

      <section className="landing-hero">
        <div className="landing-copy">
          <p className="landing-kicker">MODEL-DRIVEN CONTROL SOFTWARE ENGINEERING</p>
          <h1>Engineer control software from models to generated artifacts.</h1>
          <p className="landing-lead">
            KIDE Web is the browser workbench for the same shared Xtext, GLSP,
            synthesis, knowledge and generation services used by the Eclipse
            product.
          </p>

          <div className="landing-capabilities" aria-label="Workspace capabilities">
            <article>
              <strong>6 DSLs</strong>
              <span>Textual engineering with Xtext language intelligence</span>
            </article>
            <article>
              <strong>Graphical modelling</strong>
              <span>Activity and MNC diagrams backed by Eclipse GLSP</span>
            </article>
            <article>
              <strong>Engineering pipeline</strong>
              <span>Synthesis, reconfiguration and deterministic generation</span>
            </article>
          </div>

          <button
            type="button"
            className="hero-workspace-button"
            onClick={onOpenWorkspace}
          >
            Enter the Engineering Workspace
            <span aria-hidden="true">→</span>
          </button>
          <p className="landing-link-note">
            The workspace can be opened directly. Server engineering actions
            require an authenticated Firebase session.
          </p>
        </div>

        <form className="signin-card" onSubmit={submit}>
          <div className="signin-heading">
            <span className="signin-icon" aria-hidden="true">↳</span>
            <div>
              <p>SECURE ACCESS</p>
              <h2>{signedInEmail ? "Session ready" : "Sign in to KIDE"}</h2>
            </div>
          </div>

          {signedInEmail ? (
            <>
              <div className="signed-in-account">
                <span className="account-avatar">
                  {signedInEmail.slice(0, 1).toUpperCase()}
                </span>
                <div>
                  <small>Signed in as</small>
                  <strong>{signedInEmail}</strong>
                </div>
              </div>
              <button
                type="button"
                className="signin-primary"
                onClick={onOpenWorkspace}
              >
                Open Engineering Workspace
              </button>
              <button
                type="button"
                className="signin-secondary"
                onClick={onSignOut}
              >
                Sign out
              </button>
            </>
          ) : (
            <>
              <label>
                Email
                <input
                  aria-label="Firebase email"
                  type="email"
                  autoComplete="username"
                  placeholder="engineer@example.com"
                  value={email}
                  onChange={(event) => onEmail(event.target.value)}
                />
              </label>
              <label>
                Password
                <input
                  aria-label="Firebase password"
                  type="password"
                  autoComplete="current-password"
                  value={password}
                  onChange={(event) => onPassword(event.target.value)}
                />
              </label>
              <button
                type="submit"
                className="signin-primary"
                disabled={!configured || busy}
              >
                {busy ? "Signing in…" : "Sign in with Firebase"}
              </button>
              <p className="signin-status" aria-live="polite">{authStatus}</p>
            </>
          )}

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
        <span>Browser client · shared Eclipse semantics</span>
      </footer>
    </main>
  );
}
