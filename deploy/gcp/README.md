# KIDE on Google Cloud Run

KIDE uses two Cloud Run services when deployment is enabled:

- `kide`: backend/gateway service for the enterprise API, LSP and GLSP.
- `kide-web`: standalone React/Vite browser application.

The backend remains single-writer because current persistent and collaborative
state is not horizontally multi-writer safe. The static web tier may scale
independently.

## Cloud Build configurations

KIDE deliberately keeps **build** and **deployment** as separate Cloud Build
contracts.

### Build-safe trigger

The repository root `cloudbuild.yaml` and
`deploy/gcp/cloudbuild.yaml` are build-only configurations.

They:

1. verify that the Artifact Registry repository exists;
2. build the KIDE backend image;
3. push the image to Artifact Registry.

They do **not** require OIDC deployment settings and do not deploy Cloud Run.
This preserves the behavior of the original working Google Cloud Build trigger
and keeps ordinary main-branch builds green before deployment has been
explicitly configured.

### Deployment trigger

`deploy/gcp/cloudbuild-deploy.yaml` is the continuous-deployment
configuration.

After one-time configuration it:

1. checks that required non-secret deployment substitutions are present;
2. builds and pushes the backend image;
3. deploys the `kide` Cloud Run service;
4. reads its live URL;
5. builds the standalone web image with that URL in
   `VITE_KIDE_API_ORIGIN` and `VITE_KIDE_LSP_ORIGIN`;
6. pushes and deploys `kide-web`;
7. updates backend allowed origins with the live backend and web URLs;
8. verifies both live `/healthz` endpoints and the web root.

The deployment preflight intentionally does not call Storage or Secret Manager
`describe` operations. Resource creation/validation is performed by the
operator bootstrap so the Cloud Build service account does not need broad
Storage Viewer or Secret Manager Viewer roles merely for preflight checks.

## Why there are two configs

PR46 changed the existing root trigger file from build/push to mandatory
deployment while the new OIDC substitutions defaulted to empty values. An
existing, previously successful trigger therefore failed immediately at its
first deployment-preflight step.

The corrected model is explicit:

- a normal trigger can always use `cloudbuild.yaml`;
- automatic deployment is enabled only after infrastructure and IAM setup is
  complete;
- `configure-auto-deploy.sh` then switches that trigger to
  `deploy/gcp/cloudbuild-deploy.yaml`.

This prevents repository changes from silently converting a build trigger into
an infrastructure deployment trigger.

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

It then changes the selected Cloud Build trigger to use
`deploy/gcp/cloudbuild-deploy.yaml` and supplies the required non-secret
substitutions.

The OIDC client secret itself remains only in Secret Manager.

## Manual deployment

The manual entry point uses the same deployment configuration:

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

The Cloud Build identity receives deployment rights only when automatic
deployment is explicitly enabled. Runtime data and secret access remain on the
backend runtime identity rather than the build identity.

For horizontally scaled backend operation, first replace file-backed
persistence and process-local collaboration state with shared transactional
services.
