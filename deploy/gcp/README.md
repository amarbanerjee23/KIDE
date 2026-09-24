# KIDE on Google Cloud Run

KIDE is deployed to Google Cloud as two independent Cloud Run services:

- **`kide` backend/gateway** — enterprise API plus secure LSP and GLSP
  WebSocket gateways behind one Nginx ingress port.
- **`kide-web` frontend** — the standalone React/Vite browser application
  served by Nginx.

The frontend is built only after the backend is deployed so its
`VITE_KIDE_API_ORIGIN` and `VITE_KIDE_LSP_ORIGIN` values are compiled
against the actual live backend URL. The backend then receives the final web
URL in `KIDE_ALLOWED_ORIGINS`.

The backend Java services bind only to loopback. Nginx is the only backend
process exposed on Cloud Run's public `PORT` and routes:

- `/api/*` to the enterprise HTTP API;
- `/lsp` to the secure Xtext/LSP WebSocket gateway;
- `/glsp` to the secure GLSP WebSocket gateway;
- `/healthz` to the enterprise API health endpoint.

The backend image deliberately does **not** contain the browser application.
The browser is deployed only through the separate `kide-web` service.

## Continuous deployment

The root `cloudbuild.yaml` and `deploy/gcp/cloudbuild.yaml` are identical.
A successful configured Cloud Build trigger now performs the complete
deployment:

1. validates Artifact Registry, Cloud Storage, runtime service accounts and the
   configured OIDC secret;
2. builds and pushes the KIDE backend image;
3. deploys the `kide` Cloud Run service;
4. reads the live backend URL;
5. builds the standalone web image against that backend URL;
6. pushes and deploys `kide-web`;
7. updates backend allowed origins with both live service URLs;
8. checks `/healthz` on both deployed services and verifies the web root.

A build is successful only after both live services pass those checks.

## One-time trigger configuration

The actual OIDC client secret remains in Secret Manager. The trigger stores
only non-secret deployment configuration and the Secret Manager secret name.

Before the first automatic deployment, run:

```bash
export PROJECT_ID="kide-eclipse"
export TRIGGER_NAME="YOUR_CLOUD_BUILD_TRIGGER_NAME"
export TRIGGER_REGION="global"       # or the trigger's actual region
export REGION="asia-south1"

export KIDE_OIDC_INTROSPECTION_URL="https://idp.example.com/oauth2/introspect"
export KIDE_OIDC_CLIENT_ID="kide"
export KIDE_OIDC_ISSUER="https://idp.example.com/"
export KIDE_OIDC_AUDIENCE="kide"
export KIDE_PRIMARY_PRINCIPAL="your-principal-id"
export KIDE_OIDC_SECRET_NAME="kide-oidc-client-secret"

bash deploy/gcp/configure-auto-deploy.sh
```

For a manual Cloud Build trigger, also set:

```bash
export TRIGGER_KIND="manual"
```

The configuration script:

- enables the required Google Cloud APIs;
- creates the Artifact Registry repository if needed;
- creates the persistent Cloud Storage bucket if needed;
- creates dedicated `kide-runtime` and `kide-web-runtime` service accounts;
- grants storage and Secret Manager access only to the backend runtime;
- grants the Cloud Build service account Artifact Registry Writer, Logs Writer
  and Cloud Run Admin;
- grants the build service account Service Account User only on the two KIDE
  runtime identities;
- writes the deployment substitutions into the existing trigger.

The web runtime identity receives no storage or secret privileges.

## Secret Manager

Create the OIDC client secret once:

```bash
printf '%s' 'YOUR_OIDC_CLIENT_SECRET' | \
  gcloud secrets create kide-oidc-client-secret --data-file=-
```

If it already exists, add a version instead:

```bash
printf '%s' 'YOUR_OIDC_CLIENT_SECRET' | \
  gcloud secrets versions add kide-oidc-client-secret --data-file=-
```

The backend runtime service account receives
`roles/secretmanager.secretAccessor` on this secret. The build service
account does not receive access to the secret payload.

## Find the live URLs

After a successful trigger build:

```bash
export PROJECT_ID="kide-eclipse"
export REGION="asia-south1"

bash deploy/gcp/deployment-status.sh
```

This prints:

```text
KIDE web: https://...
KIDE backend: https://...
Web health: https://.../healthz
Backend health: https://.../healthz
```

The Cloud Build log also ends with:

```text
KIDE DEPLOYMENT COMPLETE
Web: https://...
Backend: https://...
```

## Runtime topology

The backend intentionally remains single-writer:

- `--min 1`
- `--max 1`
- session affinity enabled
- 60-minute request timeout for WebSocket IDE channels
- persistent project/workspace state mounted from Cloud Storage at `/data`

This is required while the model, knowledge, review and collaboration state is
file-backed or process-local. Do not increase backend horizontal scale until
those stores have shared transactional persistence.

The static `kide-web` service is independently scalable:

- `--min 0`
- `--max 10`
- no persistent volume
- no Secret Manager access
- dedicated unprivileged runtime service account

## Cloud Build service account

KIDE uses `CLOUD_LOGGING_ONLY` for Cloud Build so custom build service
accounts satisfy Google's explicit logging-destination requirement.

The build identity requires deployment privileges because Cloud Build now
deploys the already-built Artifact Registry images to Cloud Run. Runtime
privileges stay on the runtime identities and are not inherited by the build
service account.

## Existing trigger repair

`deploy/gcp/repair-cloud-build-trigger.sh` is retained as a compatibility
entry point. It now delegates to `configure-auto-deploy.sh`, because a
build-only trigger configuration is no longer sufficient.

The trigger must use **Cloud Build configuration file** mode with
`cloudbuild.yaml`, not Dockerfile or inline configuration mode.

## Manual deployment scripts

`deploy/gcp/deploy-cloud-run.sh` remains available for manual backend
deployment and `deploy/web/deploy-web.sh` remains available for manual
frontend deployment. The Cloud Build trigger is the recommended path because
it wires the live backend URL into the frontend automatically and validates
both live services in one transaction.

## Security notes

Cloud Run terminates public TLS. The backend proxy supplies
`X-Forwarded-Proto: https` only to loopback KIDE services, and KIDE trusts
only configured local proxy addresses.

The public Cloud Run services are intentionally unauthenticated at the Google
Cloud ingress layer so a browser can reach the UI and API endpoints. KIDE's
OIDC token validation and role bindings continue to protect application API,
LSP and GLSP operations.

For a horizontally scaled enterprise backend, replace file-backed persistence
and in-memory collaboration state with shared transactional services before
raising the backend maximum instance count.
