# PR101 — Authenticated full-stack hosted-service readiness

## Why this is needed

A live `/health` success is insufficient to establish that a real engineering
user can actually open a project and use Xtext LSP or Collaboration. PR100
secured persistent operator-approved Firebase grants, but it still requires a
real GCP operator rollout. PR101 adds a separate **read-only**, opt-in scheduled
staging probe that observes that authorization and the hosted service
boundaries continue to work together.

## Read-only probe coverage

`web/e2e/live-connectivity.spec.ts` uses **real** staging Firebase and
Cloud Run endpoints and one previously approved engineering project. Nothing is
mocked. Its checks are:

1. Read Web/API health and assert coherent frontend/backend build IDs.
2. Sign in a dedicated approved Firebase engineer through Identity Toolkit.
3. Confirm anonymous project discovery returns 401 and authenticated project
   discovery returns 200, including a precise known project/workspace identity.
4. Read project metadata, model list, and Collaboration presence listing.
5. Establish an actual authenticated Xtext WebSocket using the browser-origin
   header and production bearer subprotocol; require protocol negotiation,
   LSP initialization and completion/document-symbol capabilities.
6. Establish an actual GLSP WebSocket and initialize an **ephemeral** client
   session, with no model request/mutation actions.

It deliberately does **not** create projects, import archives, join persistent
collaboration presence, alter models, run synthesis/generation, or consume data
from unrelated projects. PR89's operator-approved, write-enabled complete
engineering acceptance remains a separate and more comprehensive qualification.

A failure returns a failed GitHub Actions job. A 403 is not automatically
recovered by changing role grants; the report instead requires an operator
to identify whether the monitor UID is authorized and the deployed project
and workspace exist.

## Operator setup — isolated staging only

1. Provision independent staging Web/API Cloud Run origins and Firebase project.
   Do not use production credentials or customer projects.
2. Create a **dedicated** staging Firebase account with a stable approved
   ENGINEER grant on an existing disposable, read-only synthetic engineering
   project; record its canonical `kide:project:<uuid>` ID.
3. Create GitHub environment `staging-monitor` with least-privilege access.
   Configure repository/environment variables:
   - `KIDE_MONITOR_API_ORIGIN`, `KIDE_MONITOR_WEB_ORIGIN` — independent HTTPS
     service origins, with no path, query, fragment or credentials
   - `KIDE_MONITOR_APPROVED_API_HOST`,
     `KIDE_MONITOR_APPROVED_WEB_HOST` — independently authorized exact hosts
   - `KIDE_MONITOR_EXPECTED_PROJECT_ID` — exact synthetic project ID
4. Configure **environment secrets**:
   `KIDE_MONITOR_FIREBASE_API_KEY`,
   `KIDE_MONITOR_ENGINEER_EMAIL`,
   `KIDE_MONITOR_ENGINEER_PASSWORD`.
   Never place these in repository files, CLI arguments, PR logs, or artifacts.
5. After the setup is reviewed, set repository variable
   `KIDE_MONITOR_AUTH_ENABLED=true`. This enables the authenticated-services
   job in the existing `Hosted Runtime Health Watch` workflow. It runs on the
   existing half-hour schedule or by manual workflow dispatch.
6. Review the job summary and exceptions. A skipped/disabled job is clearly
   reported as **NOT CONFIGURED / NOT VERIFIED**, never as proof of service
   availability.

To run the same read-only test from an isolated staging operator shell, provide
the exact `KIDE_MONITOR_*` variables in its protected environment and execute:

```bash
cd web
npm ci --no-audit --no-fund
npx playwright install --with-deps chromium
npm run test:connectivity
```

Playwright uses a dedicated config with trace, screenshots, video and retries
disabled. Project IDs and server URLs are not passed as query-string tokens.
The Firebase ID token is transmitted only in the Authorization header or the
same authenticated subprotocol used by the shipping Web client. No token or
password is printed. Actual secret retention and log policies remain an
operator responsibility.

## CI and release gates

- Local/PR CI checks `connectivityPolicy.test.ts`, TypeScript, and the
  existence/discovery of the live read-only suite; it does **not** have or
  pretend to use deployed staging credentials.
- `scripts/verify_authenticated_monitor.py` is mandatory repository
  governance. It rejects removing host authorization, removing any service
  check, adding mocked routes or write actions, or moving this suite into
  ordinary browser CI.
- Existing normal browser tests, PR89 write-enabled staging acceptance,
  hosted synthetic recovery tests, desktop/semantic parity qualification and
  trusted release rules are retained.

## Known limitations

Passing the readiness probe is a point-in-time service check, **not** a
guarantee of continuous connectivity. An authorized production UID and project
can still be incorrectly configured; production deployment permission and
revocation remain operator-controlled. Cloud Storage FUSE atomic rename is
still unqualified for registry-based project creation and restore. Real
independent alert routing/on-call, automatic rollback, load/soak, backup
restores and signed GA release qualification remain separate launch gates.
