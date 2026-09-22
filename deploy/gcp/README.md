# KIDE on Google Cloud Run

This deployment packages the KIDE browser workspace and the three headless
runtime boundaries into one Cloud Run service:

- Nginx serves the built React/Vite workspace.
- `/api/*` proxies to the enterprise HTTP API.
- `/lsp` proxies WebSocket traffic to the secure Xtext/LSP gateway.
- `/glsp` proxies WebSocket traffic to the secure GLSP gateway.
- The Java services bind only to loopback; only Nginx listens on Cloud Run's
  public `PORT`.
- Project/workspace state is mounted at `/data` from a Cloud Storage bucket.

The deployment intentionally sets one warm instance and a maximum of one
instance because the current file-backed model, knowledge, review and presence
stores are not a multi-writer distributed database. This is a hosted
qualification topology, not the final horizontally scaled enterprise topology.

## Prerequisites

Install and authenticate the Google Cloud CLI, select a billing-enabled project,
and create an OAuth2/OIDC provider that exposes an RFC 7662-compatible token
introspection endpoint. Store only the OIDC client secret in Secret Manager.

The principal supplied in `KIDE_PRIMARY_PRINCIPAL` must equal the principal ID
returned by KIDE's OIDC introspection mapping. The bootstrap grants that
principal the KIDE `ADMINISTRATOR` role for the persistent project. For
multi-user deployments, set `KIDE_ROLE_BINDINGS` explicitly instead.

## Deploy

Set the required values:

```bash
export PROJECT_ID="your-gcp-project"
export KIDE_OIDC_INTROSPECTION_URL="https://idp.example.com/oauth2/introspect"
export KIDE_OIDC_CLIENT_ID="kide"
export KIDE_OIDC_ISSUER="https://idp.example.com/"
export KIDE_OIDC_AUDIENCE="kide"
export KIDE_PRIMARY_PRINCIPAL="your-principal-id"
export KIDE_OIDC_SECRET_NAME="kide-oidc-client-secret"

# Optional:
export REGION="asia-south1"
export SERVICE_NAME="kide"

./deploy/gcp/deploy-cloud-run.sh
```

The script enables the required APIs, creates an Artifact Registry repository,
creates a dedicated runtime service account, creates the data bucket when
needed, grants least-purpose storage and Secret Manager access, builds the
container with Cloud Build, deploys Cloud Run, and then pins browser origins to
the service URL.

The secret itself is deliberately not created by the script. Create it once:

```bash
printf '%s' 'YOUR_OIDC_CLIENT_SECRET' | \
  gcloud secrets create kide-oidc-client-secret --data-file=-
```

If the secret already exists, add a new version instead:

```bash
printf '%s' 'YOUR_OIDC_CLIENT_SECRET' | \
  gcloud secrets versions add kide-oidc-client-secret --data-file=-
```

## Runtime notes

Cloud Run terminates public TLS. Nginx is the only process exposed by the
container and forwards `X-Forwarded-Proto: https` to loopback-only KIDE
services. KIDE continues to enforce its secure-transport guard and trusts only
the local proxy addresses.

The service uses a 60-minute request timeout and session affinity for the
WebSocket IDE channels. Browser clients must still tolerate WebSocket
reconnection because Cloud Run connections can be closed at the configured
request timeout.

For a horizontally scaled enterprise deployment, replace the file-backed
persistence and in-memory collaboration state with shared transactional
services before increasing `--max` above 1.


## Cloud Build custom service account logging

The build config explicitly uses `CLOUD_LOGGING_ONLY`. Google Cloud requires
an explicit user-owned log destination whenever a user-specified build service
account is used; otherwise build creation is rejected before any Docker step
starts.

If your project uses a restricted custom build service account, grant that
service account `roles/logging.logWriter` and
`roles/artifactregistry.writer`. The repository intentionally does not fall
back to Google-owned legacy log buckets.
