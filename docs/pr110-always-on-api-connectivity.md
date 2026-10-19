# PR110 — Always-on API availability and automatic project-access recovery

## Why the "Reconnect API" button kept appearing

The reported **403 FORBIDDEN** is issued by the KIDE project authorization
boundary *after* Cloud Run HTTPS, API v1 and the runtime version handshake
have already succeeded. Firebase Authentication confirms the user's identity;
it is **not** equivalent to a KIDE ENGINEER/ADMINISTRATOR project role.
A browser retry cannot supply an operator role grant, and bypassing that
server check would expose other users' models.

## Web behavior after this PR

1. On initial page load (even signed out), use **unauthenticated** read-only
   `GET /api/v1/health` and `GET /api/v1/version` to identify a real
   compatible and healthy API. A healthy result shows **API Online**.
2. Continue the read-only API heartbeat **every 30 seconds** while the Web
   page is open, including while a project is open. Retry as soon as the
   browser comes back **online** or becomes **visible**. An unhealthy,
   unavailable or incompatible service never masquerades as online.
3. When signed in with no open project, repeat authenticated project
   discovery every 30 seconds, recovering from denied-role grants and
   temporary transport outages without touching existing local drafts.
   Exactly one authorized hosted project opens itself; real Xtext/GLSP
   and collaboration are then established by the existing project flow.
4. The footer distinguishes **API Online** from **Project access required**.
   Until server authorization succeeds, do **not** mark a project or
   engineering services online and do not grant privileges from the
   browser. Avoid flashing the same 403 notice on each silent retry.
5. Settings exposes optional **Check project access now** for diagnostics.
   There is **no required manual reconnect step** in normal operation.
   Firebase ID tokens remain memory-only and automatically refresh while
   the current signed-in browser session is open. Closing/reloading the
   browser requires a new sign-in; the public API remains probeable.

## What the GCP deployment operator must still do — once per approved UID

For the legacy single-project deployment (no
`KIDE_HOSTED_PROJECTS_ROOT`), an authorized operator can run:

```bash
export PROJECT_ID="<actual-gcp-project-id>"
export KIDE_FIREBASE_PROJECT_ID="kide-eclipse"
export TRIGGER_NAME="<deployed-cloud-build-trigger>"
export REGION="asia-south1"
bash deploy/gcp/grant-firebase-engineer.sh "<approved-firebase-uid>"
```

The UID shown by **Settings → KIDE principal** must be independently
approved. The script preserves other engineers' grants, validates the
Firebase tenant, updates Cloud Build and Cloud Run, and verifies the runtime
environment. Do not paste a password or Firebase ID token into issues.

For registry-backed multi-project deployments, grant the specific project
`ENGINEER` role using the approved registry-admin mechanism. **Do not
use the legacy environment variable in registry mode**. A trusted single-team
opt-in shared policy is separately defined in PR105; it broadens access to
all projects and **must never be enabled automatically** to make the
button disappear.

## Acceptance

- Mocked browser tests: anonymous health probing and online recovery work
  without any Firebase credentials; denied 403 shows API Online alongside
  Project access required; denied users cannot start Xtext/collaboration;
  post-grant discovery automatically opens the authorized project and
  starts real (mocked in CI) engineering sessions.
- Hostile cases: offline/503 health never reports online, stale attempts
  cannot revive an account that signed out or switched, and no browser path
  installs a KIDE role grant.
- **Live GCP acceptance still required:** verify unauthenticated Web/API
  health; before grant verify exact 403; after authorized operator grant
  verify automatic project discovery, Xtext/GLSP/collaboration, code
  generation, and second-user denial. Code merge alone does not modify
  current Cloud Run env vars; deploy this frontend revision.

See [PR100](pr100-durable-hosted-auth.md),
[operator recovery](hosted-service-authorization-recovery.md) and
[PR105 shared-policy boundaries](pr105-shared-firebase-engineering.md).
