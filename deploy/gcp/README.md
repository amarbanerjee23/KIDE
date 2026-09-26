# KIDE on Google Cloud Run

## Cloud Shell quick start

From a fresh Google Cloud Shell session, you no longer need to know any
repository-relative paths.

After cloning the repository once:

```bash
git clone https://github.com/amarbanerjee23/KIDE.git
cd KIDE
./deploy-kide-gcp.sh status
```

The `status` command works even before the first deployment and reports each
service independently as `NOT DEPLOYED`.

Useful commands:

```bash
./deploy-kide-gcp.sh doctor
./deploy-kide-gcp.sh status
./deploy-kide-gcp.sh bootstrap
./deploy-kide-gcp.sh deploy
./deploy-kide-gcp.sh configure-trigger
```

For the **first deployment**, use:

```bash
./deploy-kide-gcp.sh bootstrap
```

The bootstrap flow:

1. detects the active GCP project and region;
2. discovers the existing Cloud Build trigger automatically;
3. prompts only for missing OIDC configuration;
4. reads the OIDC client secret with hidden terminal input;
5. creates the Secret Manager secret or adds a new version without placing the
   secret value in the command line or shell history;
6. provisions the Artifact Registry, persistent bucket, runtime service
   accounts and IAM through the existing bootstrap;
7. keeps the selected trigger on the canonical root `cloudbuild.yaml` and
   enables `_KIDE_RELEASE_ENABLED=true`;
8. runs the trigger against `main`;
9. waits for both `kide` and `kide-web` to exist and pass health checks;
10. prints the final public web and backend URLs.

After the first deployment, `deploy` refreshes a dedicated cached checkout of
`main` and invokes the same unified release/deployment pipeline directly.

For non-interactive automation, provide the documented OIDC environment
variables, point `KIDE_OIDC_CLIENT_SECRET_FILE` at a protected local file,
and use `KIDE_GITHUB_RELEASE_TOKEN_FILE` when the GitHub release token
Secret Manager secret has not yet been created.

KIDE uses two Cloud Run services when deployment is enabled:

- `kide`: backend/gateway service for the enterprise API, LSP and GLSP.
- `kide-web`: standalone React/Vite browser application.

The backend remains single-writer because current persistent and collaborative
state is not horizontally multi-writer safe. The static web tier may scale
independently.

## Cloud Build configuration

KIDE uses one canonical unified Cloud Build contract. The following files are
kept identical:

- `cloudbuild.yaml`;
- `deploy/gcp/cloudbuild.yaml`;
- `deploy/gcp/cloudbuild-release.yaml`.

The canonical config defaults `_KIDE_RELEASE_ENABLED=true`, so a normal main
Cloud Build is expected to build the Eclipse products, deploy the hosted KIDE
services, verify them, and publish the hosted URL to GitHub Releases.

If required OIDC or release configuration is missing, Step 0 fails immediately
with the missing prerequisite instead of silently degrading to a build-only
run.

### Unified release/deployment flow

After one-time configuration it:

1. validates the release/deployment substitutions;
2. builds the Eclipse/Tycho desktop products;
3. stages and qualifies Windows, Linux, macOS Intel and macOS Apple Silicon
   runnable bundles;
4. builds and pushes the backend image;
5. deploys the `kide` Cloud Run service and reads its live URL;
6. builds the standalone web image against the live backend URL;
7. pushes and deploys `kide-web`;
8. updates backend allowed origins;
9. verifies both live `/healthz` endpoints and the web root;
10. writes `KIDE-HOSTED-URL.txt` and `gcp-deployment-manifest.json`;
11. creates/updates a commit-specific GitHub pre-release containing the
    desktop bundles and deployment metadata.

The GitHub Release notes include the clickable hosted web URL and backend URL.

The deployment preflight intentionally does not call Storage or Secret Manager
`describe` operations. Resource creation/validation is performed by the
operator bootstrap so the Cloud Build service account does not need broad
Storage Viewer or Secret Manager Viewer roles merely for preflight checks.

## Deployment activation and prerequisites

`configure-auto-deploy.sh` keeps the trigger on the canonical root
`cloudbuild.yaml` and writes the required non-secret substitutions, including
`_KIDE_RELEASE_ENABLED=true`.

The root config itself also defaults hosted release mode to `true`. Therefore
a main Cloud Build never reports a misleading successful build while silently
skipping the hosted deployment. If the required deployment configuration has
not been provisioned, the build fails in Step 0 with an actionable error.

## Canonical main-branch release ownership

The root `cloudbuild.yaml`, `deploy/gcp/cloudbuild.yaml`, and
`deploy/gcp/cloudbuild-release.yaml` now describe the same unified pipeline.

The canonical config defaults `_KIDE_RELEASE_ENABLED=true`. Bootstrap ensures
the trigger also carries that value together with the required OIDC,
Secret Manager, service-account and GitHub release settings. The root build
then deploys `kide` and `kide-web`, verifies the live URLs, and creates the
canonical `gcp-<SHORT_SHA>` GitHub pre-release.

The GitHub Actions artifact workflow no longer creates desktop-only Releases on
`main`; it still builds and uploads Actions artifacts. This prevents a
desktop-only Release from appearing before a hosted URL exists.

## GitHub Release publication

The unified Cloud Build pipeline publishes a pre-release tagged:

```text
gcp-<SHORT_SHA>
```

The release contains:

- Windows x86_64 Eclipse bundle;
- Linux x86_64 Eclipse bundle;
- macOS x86_64 Eclipse bundle;
- macOS Apple Silicon Eclipse bundle;
- `release-manifest.json`;
- `SHA256SUMS.txt`;
- `KIDE-HOSTED-URL.txt`;
- `gcp-deployment-manifest.json`.

The release notes also contain the live `kide-web` and backend URLs.

Cloud Build reads the GitHub publishing credential only from Secret Manager.
The default secret name is:

```text
kide-github-release-token
```

Use a fine-grained GitHub token scoped only to
`amarbanerjee23/KIDE` with **Contents: Read and write**. The bootstrap reads
the token with hidden input and stores it in Secret Manager; it is never
written to the build YAML or trigger substitutions.

## First-deployment bootstrap

The first-deployment wizard is implemented in
`deploy/gcp/bootstrap-first-deployment.sh` and is normally invoked through the
top-level command:

```bash
./deploy-kide-gcp.sh bootstrap
```

If more than one Cloud Build trigger exists, the wizard displays the matching
trigger candidates and asks which one to use. In non-interactive mode, set
`TRIGGER_NAME` and `TRIGGER_REGION` explicitly.

The client secret is handled separately from the non-secret OIDC configuration.
Interactive input uses a hidden prompt and a temporary file created with
restrictive permissions. The temporary file is removed on success or failure.

The bootstrap runs the configured trigger with:

```text
branch: main
release/deployment config: deploy/gcp/cloudbuild-release.yaml
```

and completes only after:

```text
KIDE FIRST DEPLOYMENT COMPLETE
Web: https://...run.app
Backend: https://...run.app
```

## One-time automatic deployment configuration

Before enabling deployment, create the OIDC client secret in Secret Manager if
it does not already exist:

```bash
printf '%s' 'YOUR_OIDC_CLIENT_SECRET' | \
  gcloud secrets create kide-oidc-client-secret \
  --project=kide-eclipse \
  --data-file=-
```

Then configure the trigger:

```bash
export PROJECT_ID="kide-eclipse"
export TRIGGER_NAME="YOUR_CLOUD_BUILD_TRIGGER_NAME"
export REGION="asia-south1"
# TRIGGER_REGION defaults to REGION. Override only if the trigger differs.

export KIDE_OIDC_INTROSPECTION_URL="https://idp.example.com/oauth2/introspect"
export KIDE_OIDC_CLIENT_ID="kide"
export KIDE_OIDC_ISSUER="https://idp.example.com/"
export KIDE_OIDC_AUDIENCE="kide"
export KIDE_PRIMARY_PRINCIPAL="your-principal-id"
export KIDE_OIDC_SECRET_NAME="kide-oidc-client-secret"

bash deploy/gcp/configure-auto-deploy.sh
```

The configuration script provisions or validates:

- Artifact Registry repository;
- persistent Cloud Storage bucket;
- `kide-runtime` backend service account;
- `kide-web-runtime` frontend service account;
- backend bucket access;
- backend Secret Manager access;
- Cloud Build Artifact Registry Writer and Logs Writer;
- Cloud Build Cloud Run Admin;
- Service Account User on only the two KIDE runtime identities.

It keeps the selected Cloud Build trigger on the canonical root
`cloudbuild.yaml`, enables `_KIDE_RELEASE_ENABLED=true`, and supplies the
required non-secret substitutions. It also grants the Cloud Build service account Secret Manager
access only to the dedicated GitHub release-token secret.

The OIDC client secret and GitHub release token remain only in Secret Manager.

## Manual deployment

The manual entry point uses the same unified release/deployment configuration:

```bash
export PROJECT_ID="kide-eclipse"
export REGION="asia-south1"
export KIDE_OIDC_INTROSPECTION_URL="..."
export KIDE_OIDC_CLIENT_ID="..."
export KIDE_OIDC_ISSUER="..."
export KIDE_OIDC_AUDIENCE="..."
export KIDE_PRIMARY_PRINCIPAL="..."
export KIDE_OIDC_SECRET_NAME="kide-oidc-client-secret"

bash deploy/gcp/deploy-cloud-run.sh
```

## Find the live URLs

After a successful deployment:

```bash
export PROJECT_ID="kide-eclipse"
export REGION="asia-south1"
bash deploy/gcp/deployment-status.sh
```

The deployment Cloud Build also ends with:

```text
KIDE DEPLOYMENT COMPLETE
Web: https://...run.app
Backend: https://...run.app
```

## Runtime topology

The backend deployment uses:

- minimum instances: 1;
- maximum instances: 1;
- session affinity;
- 60-minute request timeout;
- Cloud Storage mounted at `/data`;
- dedicated `kide-runtime` identity.

The frontend deployment uses:

- minimum instances: 0;
- maximum instances: 10;
- no persistent volume;
- no OIDC client secret;
- dedicated `kide-web-runtime` identity.

Cloud Storage mounting uses Cloud Run's supported single-container
`--add-volume mount-path=...,type=cloud-storage,...` syntax.

## Security

Cloud Run terminates public TLS. The backend Nginx proxy is the only public
container listener and forwards requests to loopback-only API/LSP/GLSP
processes.

The Cloud Build identity receives only the deployment and release-publication
permissions provisioned by the bootstrap. Runtime data and OIDC secret access
remain on the backend runtime identity rather than the build identity.

For horizontally scaled backend operation, first replace file-backed
persistence and process-local collaboration state with shared transactional
services.
