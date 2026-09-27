# KIDE on Google Cloud Run

KIDE's hosted runtime uses Firebase Authentication for browser users and one
canonical Google Cloud Build pipeline for Eclipse desktop builds, Cloud Run
deployment, live verification and GitHub Release publication.

## Hosted authentication

The hosted API, LSP gateway and GLSP gateway accept Firebase Authentication ID
tokens.

Backend configuration:

- `KIDE_FIREBASE_PROJECT_ID`

The expected token issuer and audience are derived from that project ID:

```text
issuer   = https://securetoken.google.com/<project-id>
audience = <project-id>
```

There is no hosted OIDC introspection endpoint, OAuth client ID or OAuth client
secret. Google/Firebase signing certificates are fetched over HTTPS and cached
according to their `Cache-Control` lifetime.

The web application uses Firebase email/password authentication through the
Firebase Authentication REST API and keeps the ID/refresh tokens in browser
memory only.

Web build configuration:

- `VITE_FIREBASE_API_KEY`
- `VITE_FIREBASE_PROJECT_ID`

The Firebase Web API key is normal Firebase web configuration, not a backend
credential.

## Firebase prerequisites

Before the first KIDE deployment:

1. add/enable Firebase for the GCP project;
2. enable **Email/Password** in Firebase Authentication;
3. create the first KIDE user;
4. copy the Firebase **Web API key** from the Firebase web-app configuration;
5. copy the first user's Firebase **UID** from Authentication > Users.

The initial administrator principal is derived as:

```text
firebase:<project-id>#<firebase-uid>
```

The UID is used for authorization; email is only a display/safe identity claim.

## Cloud Shell quick start

```bash
git clone https://github.com/amarbanerjee23/KIDE.git
cd KIDE

export PROJECT_ID=kide-eclipse
export REGION=asia-south1

./deploy-kide-gcp.sh bootstrap
```

The bootstrap discovers the Cloud Build trigger and prompts for:

```text
Firebase Web API key
Firebase UID for the initial KIDE administrator
GitHub release token (hidden, only if its Secret Manager secret does not exist)
```

The GitHub release token remains in Secret Manager. Firebase Authentication does
not require a KIDE backend client secret.

## Non-interactive bootstrap

```bash
export PROJECT_ID=kide-eclipse
export REGION=asia-south1
export KIDE_FIREBASE_PROJECT_ID=kide-eclipse
export KIDE_FIREBASE_API_KEY='...'
export KIDE_FIREBASE_ADMIN_UID='...'
export TRIGGER_NAME='...'
export KIDE_GITHUB_RELEASE_TOKEN_FILE=/path/to/protected/github-token

./deploy-kide-gcp.sh bootstrap
```

After the GitHub release-token secret exists,
`KIDE_GITHUB_RELEASE_TOKEN_FILE` can be omitted.

## Cloud Build configuration

These canonical configs are kept identical:

- `cloudbuild.yaml`
- `deploy/gcp/cloudbuild.yaml`
- `deploy/gcp/cloudbuild-release.yaml`

The release pipeline:

1. verifies Firebase and release substitutions;
2. builds the Eclipse/Tycho desktop products;
3. stages Windows, Linux, macOS Intel and macOS Apple Silicon bundles;
4. builds and pushes the KIDE backend image;
5. deploys `kide`;
6. discovers the live backend URL;
7. builds the standalone web image with:
   - live backend API/LSP origin;
   - Firebase Web API key;
   - Firebase project ID;
8. deploys `kide-web`;
9. updates backend allowed origins;
10. verifies both live health endpoints and the web root;
11. writes `KIDE-HOSTED-URL.txt` and
    `gcp-deployment-manifest.json`;
12. creates/updates `gcp-<SHORT_SHA>` in GitHub Releases.

The GitHub Release notes and `KIDE-HOSTED-URL.txt` contain the live web and
backend URLs.

## Trigger substitutions

The hosted release trigger uses:

```text
_KIDE_RELEASE_ENABLED=true
_KIDE_FIREBASE_PROJECT_ID=<firebase/gcp-project-id>
_KIDE_FIREBASE_API_KEY=<firebase-web-api-key>
_KIDE_FIREBASE_ADMIN_UID=<initial-admin-firebase-uid>
_KIDE_GITHUB_REPOSITORY=amarbanerjee23/KIDE
_KIDE_GITHUB_TOKEN_SECRET=kide-github-release-token
```

No `_KIDE_OIDC_*` substitutions are used by the hosted runtime.

## Runtime identities

The deployment maintains two Cloud Run service accounts:

- `kide-runtime`: backend API/LSP/GLSP service;
- `kide-web-runtime`: static web service.

The backend remains single-writer:

- minimum instances: 1;
- maximum instances: 1;
- session affinity enabled;
- persistent Cloud Storage mounted at `/data`.

The static web tier may scale independently.

Cloud Build requires:

- Artifact Registry Writer;
- Logs Writer;
- Cloud Run Admin;
- Service Account User on the two KIDE runtime identities;
- Secret Manager Secret Accessor only on the GitHub release-token secret.

The backend no longer needs Secret Manager access for an OIDC client secret.

## Manual deployment

```bash
export PROJECT_ID=kide-eclipse
export REGION=asia-south1
export KIDE_FIREBASE_PROJECT_ID=kide-eclipse
export KIDE_FIREBASE_API_KEY='...'
export KIDE_FIREBASE_ADMIN_UID='...'

bash deploy/gcp/deploy-cloud-run.sh
```

## Status and live URLs

```bash
export PROJECT_ID=kide-eclipse
export REGION=asia-south1

./deploy-kide-gcp.sh status
```

or:

```bash
bash deploy/gcp/deployment-status.sh
```

A successful unified build ends with:

```text
KIDE DEPLOYMENT COMPLETE
Web: https://...run.app
Backend: https://...run.app
```

## Desktop authentication

The generic OIDC PKCE/device/client-credentials code in
`com.kide.enterprise.identity` remains available for the Eclipse desktop
product and enterprise integrations. PR57 changes the **hosted** server trust
boundary only.
