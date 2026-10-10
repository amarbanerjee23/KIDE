# PR89 — Real deployed engineering acceptance

This is a **separate, manual, non-mocked** staging gate. The existing `web/e2e/golden-journey.spec.ts` remains useful as a repeatable UI/contract test, but its mocked Firebase, REST, synthesis and LSP responses do not prove that a hosted deployment works.

## What the real gate checks

`web/e2e/live-hosted.spec.ts` runs against actual staging URLs and performs:

1. Backend health, project-creation capability, API runtime version, and **matching Web/backend build IDs**.
2. Email/password sign-in with real Firebase Authentication for **two distinct staging principals**.
3. Unauthenticated access denial; creation of a new isolated hosted project; second-principal list filtering and direct project/model denial.
4. Real archive import of a four-DSL corpus (MNC, Capability, Activity, KRL), with model bytes and revision checks.
5. Direct authenticated Xtext LSP and Eclipse GLSP WebSocket subprotocol/initialization over the deployed backend.
6. Deterministic REST synthesis and semantic Java code generation; repeated-call fingerprint stability; KRL revision conflict, source non-mutation, generation manifest provenance, target/version agreement, and SHA-256 of **actual decoded artifact bytes**.
7. Actual deployed Web sign-in, project opening, Monaco/Xtext status, engineering-flow synthesis and generation, Activity graphical view, and MNC state-machine view.

No WebSocket or REST test response is intercepted/replayed.

## Required isolated staging environment

**Never use the production Cloud Run services or a production Firebase account.** Use separate `kide-staging` Web/API Cloud Run services, a separate staging Firebase project, and a **persistent staging filesystem supporting atomic directory rename** for `KIDE_HOSTED_PROJECTS_ROOT`. The current production Cloud Storage FUSE setup has not been qualified for PR88's atomic project creation. Set `KIDE_API_BOOTSTRAP_STARTER_KNOWLEDGE=true` to seed the deterministic synthesis knowledge corpus on newly discovered projects.

The PR88 registry has a quota of **10 projects per creator** and no remote deletion endpoint. Every invocation creates a uniquely named, persistent staging project and consumes one slot. Reset staging data securely or rotate dedicated staging test principals before exhausting the quota. Do not use customer data or clean up via unaudited direct deletion on a live deployment.

Configure a GitHub Actions **environment named `staging`**, preferably with required reviewers and restricted deployment branches. Add these **environment-scoped secrets**, not repository-wide or production secrets:

- `KIDE_STAGING_FIREBASE_API_KEY` — public Firebase Web API key for the *staging* Firebase project
- `KIDE_STAGING_ENGINEER_EMAIL`, `KIDE_STAGING_ENGINEER_PASSWORD` — first staging user
- `KIDE_STAGING_OTHER_ENGINEER_EMAIL`, `KIDE_STAGING_OTHER_ENGINEER_PASSWORD` — second distinct staging user

Both Firebase users must exist. The first must be able to create a registry project; in PR88 registry mode the creator is granted scoped ADMINISTRATOR rights automatically. The second must not have a global/static admin binding granting access to the first user's projects.

Run **Actions → Live Staging Engineering Acceptance → Run workflow** from the branch containing PR89. Supply both deployed HTTPS service origins, explicitly approved Web and API hostnames, and enter the literal `staging-only` for write authorization. Both hostname values must match exactly; credentials and tokens must not appear in URL query parameters. The GitHub workflow refuses missing secrets and refuses any other approval value.

Alternatively, in a secured staging-only shell with the same environment variable values, run:

```bash
cd web
npm install --no-audit --no-fund
npx playwright install chromium
npm run test:live
```

Use `KIDE_LIVE_WEB_URL`, `KIDE_LIVE_API_ORIGIN`, `KIDE_LIVE_APPROVED_WEB_HOST`, `KIDE_LIVE_APPROVED_API_HOST`, `KIDE_LIVE_ALLOW_WRITES=staging-only`, `KIDE_LIVE_FIREBASE_API_KEY`, `KIDE_LIVE_ENGINEER_EMAIL`, `KIDE_LIVE_ENGINEER_PASSWORD`, `KIDE_LIVE_OTHER_ENGINEER_EMAIL`, and `KIDE_LIVE_OTHER_ENGINEER_PASSWORD`. Never commit these credentials. Playwright tracing, screenshots and video are **disabled** in the live config to avoid archiving real bearer tokens.

## CI and release decisions

Pull-request CI runs the normal mocked browser tests, the Python governance checks, and `playwright test --config playwright.live.config.ts --list` to validate **test discovery only**. These checks **do not run the staging gate** and must not be presented as evidence of live deployment qualification. The separate manual workflow result is required before a Web beta readiness sign-off.

A PR89 merge does not deploy services, provision staging infrastructure, configure Firebase, or validate the live hosted environment automatically. Failed acceptance should block promotion; capture sanitized errors and debug staging without uploading token-bearing browser traces. PR90 covers operational durability, recovery and load assurance.
