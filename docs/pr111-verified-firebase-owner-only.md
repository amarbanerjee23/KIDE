# PR111 — Verified Firebase email, privately owned KIDE projects

## Decision

- Project scope: **user-owned projects only**. Never enable the PR105 shared
  all-project ENGINEER mode on this deployment.
- Eligibility: **all Firebase Authentication email users**, irrespective of
  email domain, **only after Firebase verifies the email address**.
- Role selection: new projects have a durable **creator owner** binding,
  already implemented by PR87/88; no one-time operator allowlist is required
  for a verified owner to create their own hosted project.
- No change to explicit DENY precedence: `AuthorizationService` checks
  project-scoped denies before returning an allow decision.
- No outside identity validator: REST, Xtext and GLSP all validate the same
  Google-signed Firebase ID token, project issuer/audience, expiration and
  `email_verified: true`. Unverified or missing-email tokens fail with
  authentication denial before accessing any project.
- The private project registry is the source of authorization truth. Existing
  model state never moves into the browser or a client-maintained UID list.

## Configuration

The API, LSP and GLSP processes share these environment variables:

```ini
KIDE_FIREBASE_PROJECT_ID=kide-eclipse
KIDE_FIREBASE_OWNER_ONLY_ENGINEERS=true
KIDE_SHARED_FIREBASE_ENGINEERS=false
KIDE_STORAGE_MODE=registry-posix
KIDE_HOSTED_PROJECTS_ROOT=/data/hosted-projects
```

The **one and only** owner-only switch is
`KIDE_FIREBASE_OWNER_ONLY_ENGINEERS`. When enabled, the backend fails startup
if the registry is missing, not at the qualified fixed path, or shared mode
is enabled. Existing private legacy installs keep the new switch disabled by
default; no permission is silently broadened.

**Cloud Run storage caveat.** The existing normal Git-push pipeline mounts
Cloud Storage FUSE, which is not a qualified POSIX registry. An automatic
rollout may not safely create privately owned projects on that storage.
This PR therefore passes the setting to all four Cloud Build definitions
but explicitly **refuses an owner-only legacy deployment before `gcloud run
deploy`**. It also prevents `configure-auto-deploy.sh` and the manual
legacy deploy script from silently enabling the unsafe option. An old
`kide` production service is **not** migrated or reconfigured by merging.

## Qualified isolated staging (next operational gate)

1. Provision a separate reviewed Cloud Run staging backend on **NFS/POSIX**
   with a stable `/data/hosted-projects` root. Run the documented offline
   atomic rename, no-replace, write/snapshot restore and recovery preflight.
   Never use the legacy GCS FUSE volume as the new registry.
2. On an isolated candidate service and dedicated Firebase staging project,
   run `deploy/gcp/deploy-registry-staging.sh` with
   `DEPLOY_APPROVAL=isolated-staging-only` and
   `STORAGE_PREFLIGHT_APPROVAL=staging-only-offline`.
   The script now enables owner-only/verified-email and disables shared
   engineering mode, and deploys **no traffic** to the candidate.
3. Verify real emails A and B (two distinct Firebase UID values) on the
   approved staging backend. Before verification, both must receive 401
   from REST, LSP and GLSP and be unable to create a project. After
   verification, obtain **fresh** ID tokens; each user should see an empty
   list, be able to create an own project, open it, save/read it, synthesize
   and generate. Each must be unable to list, fetch, connect to, or edit the
   other user's project (including guessing IDs and workspace IDs).
4. Repeat with a valid unverified email, another Firebase project,
   expired token, fabricated token, and explicitly denied project grant.
   Review creation quota, simultaneous writes, ETag conflicts, GLSP edits
   and collaboration; check authorizations after restart/redeploy.
5. For an approved **Git-push staging trigger**, select the new
   `deploy/gcp/cloudbuild-owner-registry-staging.yaml` manifest. Its
   substitutions default to `disabled`/empty. Configure the dedicated
   staging service ending `-registry-staging`, isolated Firebase project,
   private VPC/NFS, approved web origin, dedicated runtime service identity,
   and **both** reviewed approvals. The trigger refuses incomplete inputs,
   builds a source-SHA-tagged image, resolves an immutable Artifact Registry
   SHA-256 digest and invokes the same no-traffic registry staging script.
6. Test restored snapshots, no old-writer overlap, bounded concurrency,
   rejected writes and rollback with the real NFS backend. Only after
   signoff configure a **separate production registry rollout** with
   traffic migration, explicit rollback approval and observed authorization
   for both test accounts. Never overwrite the existing legacy `kide`
   service via its single-project Git-push trigger.

The deployment trigger name/ID was **not provided**. Accordingly, this PR
does not edit the live trigger, IAM grants, Firebase users or production
Cloud Run revisions. The old trigger is deliberately limited to legacy mode
until the dedicated POSIX registry deployment is accepted.

## Web first-use behavior

- On Firebase registration, KIDE requests a verification email via the
  official Firebase Identity Toolkit API. In the signed-in landing UI the
  user can send it again and force-refresh their Firebase ID token after
  clicking the email verification link.
- Email verification **does not** grant access to other owners' projects.
  Once verified and on the qualified registry backend, project discovery
  and creation work without a preconfigured engineer UID, while the normal
  automatic API and Xtext reconnection behavior from PR110 continues.
- Tokens are memory-only; the verification mail request does not expose
  token data to URLs, console output or error messages.

## Automated coverage

- Signed test JWTs: approved email, unverified false, missing claim,
  phone-only/missing email, wrong Firebase audience, expired token and
  mismatched subject are denied as appropriate.
- Registry + protocol access tests: creator-only REST/LSP/collaboration,
  cross-user denials, and explicit DENY precedence.
- Web Vitest: email-verification REST request, no anonymous send and forced
  token refresh for the same user.
- `scripts/verify_pr111_owner_only.py` and adversarial mutation tests
  require all three transport apps, storage guards, Cloud Build safety,
  explicit denies, and Web verification actions to be present.

**Repository CI passing is not the same as live GCP staging qualification.**
