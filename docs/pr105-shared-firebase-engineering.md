# PR105 — Automatic Firebase authorization across KIDE services (shared-workspace mode)

## Goal and boundary

**One backend policy** authorizes a signed-in user consistently across REST,
Xtext LSP, GLSP and collaboration; the Web UI does not maintain separate
API/LSP/GLSP allowlists. For deployments operated as a **single trusted team**,
PR105 introduces an *explicitly opted-in* shared-project authorization mode:

```text
KIDE_SHARED_FIREBASE_ENGINEERS=true
```

When true, each correctly signed and unexpired **Firebase ID token from the
configured `KIDE_FIREBASE_PROJECT_ID`** automatically receives KIDE **ENGINEER**
permissions for **every hosted project**, existing and subsequently created.
The same request-time policy is constructed in the API, LSP and GLSP processes;
collaboration uses the API policy. No per-UID allowlist or one-time
`Reconnect API` authorization intervention is required after rollout.
A Firebase sign-in alone is not an ADMINISTRATOR grant: ownership, role
management and review approval still need separately scoped grants.

**This is intentionally broad access. Anyone permitted to sign up or sign in
to the configured Firebase Authentication project will be able to read,
edit, synthesize, generate and collaborate on every KIDE project.** Do not
enable this mode for a SaaS/multitenant service, public email registration,
customers with isolated projects, or users who must not see one another's
models. In private mode (unset/false) KIDE retains owner-only registry grants
and legacy explicit role allowlists. Explicit project/organization DENY
bindings override the shared grant. Invalid flag values cause server startup
failure rather than a permissive fallback; tokens from another Firebase
project, OIDC tokens and offline principals do not qualify.

## Activate on the already deployed kide-eclipse team workspace

First merge this PR after all required CI checks, then deploy a backend
image containing the shared authorization implementation. A service
environment variable does **not** add code to an older deployed image.

On a deployment-operator Google Cloud Shell checkout of that approved
commit, with Google Cloud Run Admin and Cloud Build trigger editing rights:

```bash
export PROJECT_ID=kide-eclipse
export REGION=asia-south1
export KIDE_FIREBASE_PROJECT_ID=kide-eclipse

gcloud builds triggers list --project "${PROJECT_ID}" \
  --region "${REGION}" --format='table(name,id)'

export TRIGGER_NAME='ACTUAL_KIDE_DEPLOYMENT_TRIGGER_NAME'
export KIDE_SHARED_ACCESS_ACK=I_UNDERSTAND_ALL_FIREBASE_USERS_CAN_EDIT_ALL_PROJECTS

bash deploy/gcp/enable-shared-firebase-access.sh
```

The script checks that both the existing Cloud Run service and the Cloud
Build trigger target exactly `kide-eclipse`, updates the trigger **first**
to keep the policy in subsequent deployments, then updates only the
`kide` backend's shared-access environment variable, and verifies both.
It does not change `kide-web`, Firebase Authentication users, customer data
or project ownership. The canonical release/build manifests carry the
`_KIDE_SHARED_FIREBASE_ENGINEERS` substitution into Cloud Run. For manual
releases the current setting is inherited, or an explicit `true` requires
the same acknowledgement. `configure-auto-deploy.sh` retains the existing
trigger value and requires the same acknowledgement for shared access.

After the deployment, sign into KIDE Web as a valid user in the same Firebase
project and click **Settings → Reconnect API**. Open any existing authorized
project and verify **API Online, Xtext Online and Collaboration Online**,
real content assist, graphical GLSP open and synthesis/generation. A second
trusted Firebase user must be able to access the same project; a Firebase
user from another project, an unauthenticated request, and explicit scoped
denies must all continue to fail.

To return to private mode, update the reviewed trigger substitution
`_KIDE_SHARED_FIREBASE_ENGINEERS=false` and Cloud Run backend environment
`KIDE_SHARED_FIREBASE_ENGINEERS=false` together, then verify the deployed
backend no longer exposes ungranted projects. Disabling it does not grant
revoked access and does not rewrite any project owner metadata.

## CI coverage and limitations

- Authorization service JUnit tests qualify two distinct project contexts,
  API, LSP, collaboration and model writes for multiple signed Firebase
  principals, reject forged/foreign tokens and explicit denies, and prove
  the safe default remains private.
- PR CI checks the three canonical Cloud Build configs and standalone deploy
  manifest propagate the same setting and fail closed when the setting is
  not deliberately enabled by a deployment operator.
- Real staging must exercise Web sign-in, API project listing, Xtext, GLSP
  and collaboration for two separate Firebase identities; no production
  authorization or Cloud Run configuration is modified by this code PR.
