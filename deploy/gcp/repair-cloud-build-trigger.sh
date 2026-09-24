#!/usr/bin/env bash
set -euo pipefail

echo "KIDE Cloud Build triggers now deploy both backend and web services."
echo "Applying the full continuous-deployment trigger configuration..."
exec bash deploy/gcp/configure-auto-deploy.sh
