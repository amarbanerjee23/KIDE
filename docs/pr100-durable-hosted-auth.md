# PR100 — Durable Firebase authorization and automatic Web IDE recovery

## Incident addressed

On 2026-10-11 the hosted Web IDE reported API v1 connected and compatible but returned
HTTP 403 while listing projects for the signed-in `firebase:kide-eclipse#<uid>`.
Xtext LSP and Collaboration were waiting for an authorized project. Request IDs are
for server-log correlation, not authentication credentials.

This is **not** a broken API URL or WebSocket outage. The legacy hosted backend
requires an explicit project-scoped ENGINEER or ADMINISTRATOR grant. An unapproved
Firebase login must remain denied. A browser-side fallback or universal grant
would cross tenant/security boundaries.

## Operator recovery (legacy single project)

Run the following **locally as an authorized GCP operator** from the repository
root. Use the actual GCP project, Cloud Build trigger, and Firebase UID shown in
**Settings > KIDE authorization identity**. Never provide Firebase ID tokens.

```bash
export PROJECT_ID="<gcp-project-id>"
export KIDE_FIREBASE_PROJECT_ID="kide-eclipse"
export TRIGGER_NAME="<deployment-trigger-name>"
export REGION="asia-south1"
# If the trigger lives in global instead of asia-south1:
# export TRIGGER_REGION="global"
bash deploy/gcp/grant-firebase-engineer.sh "<firebase-uid>"
```

The script checks Cloud Run's Firebase project identity and rejects a different
tenant or registry-backed mode. It validates the UID and both existing engineer
allowlists, merges without dropping other engineers, updates the Cloud Build
trigger, verifies the trigger change, updates Cloud Run, and verifies the
runtime environment. It is idempotent for an already authorized UID.

**This does not grant access merely because someone signs into Firebase.**
The operator must independently approve the requested user's engineering
access and execute the command with GCP permissions for Cloud Build triggers
and Cloud Run services. Role grants are not committed to the public repo.

If the script stops after updating the trigger but before updating Cloud Run,
investigate the error and rerun it (the merge is idempotent). If the deployed
service uses `KIDE_HOSTED_PROJECTS_ROOT`, do not use this script: create a
per-project ENGINEER role binding through the approved registry admin process.

## No more accidental grant loss on deployments

All four Cloud Build deployment definitions now check the currently deployed
Firebase project and engineer UIDs **before** replacing the Cloud Run backend
revision. If a proposed release omits any already granted UID, the build fails
rather than erasing project access. Likewise, a Cloud Run inspection failure
other than a true not-found service blocks deployment. First deployments still
work with only the bootstrap ADMINISTRATOR identity.

`configure-auto-deploy.sh` now preserves the existing trigger allowlist when
the engineer variable is *unset*, rather than treating it as an instruction to
clear all engineers. The manual deploy path adopts the current Cloud Run list
when omitted. Explicit changes and revocations must be deliberate, coordinated
between the trigger and runtime, and separately audited. A recurring 403 should
never be "fixed" by granting wildcard access.

## Browser self-recovery

On a real HTTP 403 for project listing, KIDE keeps API reachability distinct
from authorization and polls again approximately every 30 seconds **while the
same Firebase session remains signed in**. After an approved grant is rolled out,
successful discovery clears the retry flag and a single available project
automatically opens the normal authenticated Xtext/Collaboration connections.
Manual **Reconnect API** remains available for immediate re-checks.

The retry neither grants project access nor simulates Online status. It stops
after successful discovery or sign-out. Existing Xtext/Collaboration recovery
continues to apply after an authorized project is opened.

## Validation and release acceptance

Automated coverage:
- `python3 -m unittest scripts.tests.test_firebase_access_persistence`
- `python3 -m unittest scripts.tests.test_hosted_connectivity`
- Web `npm run build`, `npm test`, and Playwright `npm run test:e2e`
- Existing desktop and Cloud Build CI

Live staging/production acceptance **after an approved operator run**:
1. Before grant, the UID receives project-list 403 and Xtext/Collaboration
   remain awaiting authorization.
2. After grant, refresh occurs automatically or by Reconnect API; listProjects
   returns an authorized project.
3. Open a model; verify real Xtext initialize/completion/diagnostics, GLSP,
   Collaboration join/heartbeat, synthesis and generation.
4. Deploy a later commit through the configured trigger. The same UID must
   remain authorized; deliberately omitting an existing UID must fail the
   deployment protection.
5. A different, unlisted UID must still receive 403. Repeat after sign-out
   and a fresh Firebase login to ensure no stale session crosses accounts.
6. Check Cloud Run and Cloud Build audit logs, rollout status, and request ID
   correlation. An HTTP `/health` response alone does **not** prove LSP,
   GLSP, Collaboration, or project authorization is working.

No code-only PR can update production GCP IAM/Cloud Run settings or guarantee
100% service uptime. The operator-run step and live acceptance remain necessary.
