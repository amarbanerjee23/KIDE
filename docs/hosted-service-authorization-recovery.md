# PR92 — Restoring authorized Web IDE service connectivity

## What the report means

"API connected · authorization required" means HTTPS and the API compatibility handshake succeeded, but the Firebase UID that signed in did **not** match a KIDE project role binding. Xtext and collaboration require a selected authorized workspace and therefore cannot truthfully show Online yet. Continuous availability is a monitoring/recovery goal, not an authorization bypass.

The legacy Cloud Run service uses one canonical project under /data/project and one workspace under /data/workspace. Initial bootstrap grants ADMINISTRATOR to the Firebase UID from KIDE_FIREBASE_ADMIN_UID. Creating a second Firebase account is **not** equivalent to granting it an engineering role.

## Approved resolution for existing kide-eclipse deployment

1. Confirm the account's exact Firebase UID on the Web IDE **Settings > Account > KIDE principal**. The project ID must equal the backend Firebase project (kide-eclipse in the report). Do not paste ID tokens or passwords into support tickets.
2. With deployment operator authorization, configure the existing Cloud Build trigger using the repository's configure-auto-deploy.sh and its required current Firebase/admin settings, adding:

   `export KIDE_FIREBASE_ENGINEER_UIDS='<approved-uid>'`

   For more than one approved engineer, use a colon-separated list (maximum 32 UIDs). Do not add emails, wildcards, separators other than colon, or Firebase passwords. This list is a **project-level ENGINEER grant only**, not an ADMINISTRATOR grant. Other Firebase accounts remain denied.
3. The approved operator must update the trigger substitutions (including _KIDE_FIREBASE_ENGINEER_UIDS) and **redeploy the backend** from a reviewed commit. Simply changing the browser settings, refreshing Firebase tokens, or merging this PR will not update already deployed Cloud Run environment variables.
4. The backend entrypoint reads the allowlist and constructs explicit scoped role bindings using the same persisted project identity. API, Xtext LSP and GLSP gateways receive the identical binding set at boot. They will not expose a project to an unlisted UID.
5. Sign in, open Engineering Workspace. With exactly one authorized server project, the Web app loads it automatically and connects LSP/collaboration. For multiple projects, select a project explicitly. If authorization has just been updated, use **Settings > Reconnect API**; previously opened sessions can re-establish following interruption.
6. Verify **API Online**, **Xtext Online** and **Collaboration Online** and load an actual DSL file. Test completion, diagnostics, graphical GLSP open, synthesis, and code generation with an authorized account. Test a second unlisted Firebase UID still returns 403 and cannot establish an engineering session.

An operator can also use KIDE_ROLE_BINDINGS explicitly (principal|ROLE|projectId); do **not** combine a custom KIDE_ROLE_BINDINGS value with KIDE_FIREBASE_ENGINEER_UIDS, because the startup validator rejects ambiguous policies.

## Hosted multi-project mode

KIDE_HOSTED_PROJECTS_ROOT uses per-project creator ownership metadata. The legacy allowlist is deliberately **not** applied to registry-backed projects, which must retain tenant isolation. Authorized project owners should use the registered per-project grants; new users may create their own projects only after an approved POSIX registry deployment qualifies atomic rename/restore. **Cloud Storage FUSE atomic rename is not qualified** and should not be switched into self-service registry creation merely to avoid 403 errors.

## Service recovery

The Web client checks the actual Xtext socket state, and retries disconnected Xtext and degraded collaboration approximately every 30 seconds while a signed-in authorized project is open. Status labels distinguish API reachable but unauthorized from a fully opened engineering workspace. No user should ever see "Online" solely because a WebSocket mock or an unauthenticated health check responds.

## Evidence and remaining operational tasks

PR92 includes executable negative allowlist tests, an end-to-end browser test that transitions from a real 403-shaped denied role to an operator-approved project grant, plus the normal Eclipse product, browser and Cloud Run CI jobs. The browser test uses mocked backend responses and does not claim an actual production grant. Run PR89 live staging acceptance (real Firebase/REST/LSP/GLSP, generation), and PR90 staged restore checks before GA. Deploying/changing Cloud Run production configuration requires separate operator approval and cannot be performed from a code-only PR.
