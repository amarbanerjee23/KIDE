# KIDE web deployment

This directory deploys the browser workspace independently of the Eclipse
desktop product.

The frontend is a static Vite build served by Nginx on Cloud Run. It does not
embed the KIDE backend. Set `KIDE_BACKEND_ORIGIN` to the separately deployed
KIDE API/LSP/GLSP service.

## Deploy

```bash
export PROJECT_ID="kide-eclipse"
export KIDE_BACKEND_ORIGIN="https://YOUR-KIDE-BACKEND-URL"
export REGION="asia-south1"

bash deploy/web/deploy-web.sh
```

Optional overrides:

- `SERVICE_NAME` (default: `kide-web`)
- `BACKEND_SERVICE_NAME` (default: `kide`)
- `AR_REPOSITORY` (default: `kide`)
- `IMAGE_TAG` (default: current Git commit)

The deployer provisions Artifact Registry through the existing GCP bootstrap,
builds a frontend-only image, deploys it to Cloud Run, and updates the KIDE
backend's allowed origins when that backend service is present in the same
project and region.

The browser build receives the backend origin through both
`VITE_KIDE_API_ORIGIN` and `VITE_KIDE_LSP_ORIGIN`. GLSP follows the same
gateway origin used by the browser workspace.
