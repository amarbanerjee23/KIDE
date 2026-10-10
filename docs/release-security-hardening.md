# PR91 — Security and release hardening

This PR strengthens hosted Web/API/LSP/GLSP response policies and release CI. It is not a penetration test or proof of production GA security.

## CI least-privilege boundary

Before PR91, the artifact workflow granted repository contents and package write rights at workflow level, including pull-request jobs that only needed to build and qualify.

After PR91:
- The workflow defaults to contents: read.
- The desktop and Cloud Run qualification jobs explicitly retain read-only tokens and cannot publish releases or container images.
- A separate publish-cloud-run job has package-write rights and is gated to push/main or manual main dispatch, after Cloud Run qualification completes. The published image is rebuilt from the same source commit, but bytewise reproducibility has not been independently attested.
- A separate publish-desktop job has content-write rights and runs only after a successful manually dispatched main build. Desktop prereleases use immutable tags and never overwrite existing release assets.
- All third-party actions remain pinned to immutable commits.

## Trusted signed desktop releases

The trusted release pipeline now starts with a secret-free verify-release-source job. Manual release runs must originate from main, version tags must have a valid vX.Y.Z version shape, and the source commit must be reachable from main.

Production signing and publishing jobs use the trusted-release GitHub environment. Repository administrators MUST configure it with required reviewers, branch restrictions and controlled secrets; referencing an environment in YAML does not automatically activate protective rules.

Manual release version input is passed as an environment variable, not interpolated into shell source. Existing Windows signing, macOS notarization, Eclipse signing and provenance checks remain required.

## Real Nginx/Web browser protections

The backend and Web Nginx endpoints now send no-sniff, deny-framing, no-referrer and restrictive browser device-permission headers. The Web also sets Cross-Origin-Opener-Policy to same-origin; the backend marks API responses as no-store. Web shell/version responses are revalidated between deployments. The Web production build does not ship JavaScript source maps.

The Cloud Run container qualification checks the actual backend and Web HTTP headers, Web build version caching and absence of generated source maps.

A comprehensive Content Security Policy is NOT enabled in this PR. Firebase, Monaco workers, Xtext and GLSP assets require a measured and tested CSP allowlist. CSP is a remaining release-security task.

## Regression gates

The Build KIDE PR workflow runs scripts/verify_release_security.py. Its tests, scripts/tests/test_release_security.py, deliberately simulate dangerous changes: reintroducing a PR write token, removing source verification, shell interpolation of manual inputs, immutable release override, security header removal and source-map exposure.

These tests do not independently audit all dependencies or substitute for a live security assessment.

## Required operational release review

1. Protect main, signed tags, and the trusted-release environment. Restrict workflow edits and manual dispatch privileges.
2. Execute PR89 staging Firebase identity and two-user tenant isolation checks on actual REST, Xtext LSP and GLSP endpoints. Verify session expiry, reconnect, role revocation and tenant write denial.
3. Verify PR90 staged restore and data isolation. Cloud Storage FUSE atomic rename remains unqualified.
4. Qualify credential rotation, dependency/container vulnerability scanning, SBOM results, token leakage, authentication rate limits and incident logs.
5. Establish production TLS proxy trust, CSP enforcement, patch cadence, observability/on-call escalation, rollback and signed artifact provenance.
6. Complete PR92 Web/editor UX and GA acceptance before declaring commercial release readiness.

Green CI means the PR91 controls compile and qualification gates pass; it does not mean the entire deployment has passed a penetration test.
