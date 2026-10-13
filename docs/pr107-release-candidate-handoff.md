# PR107 — Final release-candidate qualification and stable-only handoff

PR107 **does not declare KIDE generally available**. A code merge cannot
establish real storage integrity, production authorization, page delivery,
desktop OS trust or operator rollback readiness. This gate only enables a
stable GitHub Release **after** reviewers perform, record and approve real
qualification on the *exact commit* being published.

## Blockers this PR closes

1. The signed-bundle release job previously read its own output via
   `needs.build-release.outputs.tag`. That output is not available inside the
   job that produces it. The version now comes from `steps.version.outputs.tag`.
   A version older than or equal to an already published stable is rejected.
2. Existing `Build KIDE` and Enterprise CI checks did not require **live**
   Firebase project authorization, Xtext LSP, GLSP, collaboration, synthesis
   and code generation. They remain mandatory, but are not substitutes for
   a successful `Live Staging Engineering Acceptance` workflow.
3. Even a real browser staging run does not qualify NFS transaction recovery,
   migration/rollback or signed Windows, Linux and both macOS desktop launch
   journeys. The new **KIDE Release Candidate Operational Qualification**
   workflow records reviewed evidence under a protected GitHub environment.
4. A stable-only GitHub Release was already **published only after native
   signing and attestations**, and only then were older GitHub Release records
   retired by `retain_latest_stable_release.py`. PR107 retains that process:
   never delete tags, Git history or a previous stable before a fully
   published, verified replacement exists.

## Qualified source and sequence (same SHA throughout)

1. Review and merge PR107 and obtain successful required main-branch CI:
   `Build KIDE`, `KIDE Enterprise CI Pipeline`. Observe any Cloud Run and
   package image workflows that ran for this SHA. Verify the published
   staging Web/API image build IDs against the intended exact main SHA.
2. Provision an **isolated staging deployment** (not production Firebase,
   not customer NFS data) with an approved POSIX-compatible
   `KIDE_HOSTED_PROJECTS_ROOT`. Do not turn on unsupported Cloud Storage
   FUSE self-service registry mode. Test a private registry's creator-only
   authorization, or explicitly test the approved trusted single-tenant
   shared Firebase mode; never infer access from a green anonymous health
   response.
3. Run **Actions → Live Staging Engineering Acceptance** on `main` for
   this exact commit. Approve the `staging` environment, use two distinct
   real staging Firebase principals, approved staging origins, and explicit
   `staging-only` write permission. Its real REST/Xtext/GLSP/Monaco/
   synthesis/generation checks must all **pass**. Record the workflow run
   ID. A skipped, older-commit, PR, failed or merely mocked run is invalid.
4. In controlled staging, separately exercise: (a) same-ETag concurrent
   editor writes and the PR104 conflict path; (b) NFS writer guard and
   interrupted-journal recovery with exclusive offline ownership;
   (c) a checksum-verified backup restored onto a clean path with
   unchanged model bytes, revisions, project IDs, ownership and role
   isolation; (d) rollout of the exact proposed backend build and a
   documented rollback to a known-good image plus restoration/reconnection.
   Record actual RPO/RTO and incident IDs. Do **not** paste credentials,
   token-bearing URLs or user data into GitHub Actions inputs.
5. On a real Windows x64, Linux x64, macOS Intel and macOS Apple silicon
   machine, launch the **platform-signed or notarized candidate**, open
   the sample project, exercise diagnostics and DSL completion plus
   Sirius diagram, synthesis and code generation. Verify the release
   signatures/notarization and archives, including SHA256 and SBOM.
   Record any browser/assistive-technology exceptions explicitly.
6. Create GitHub Actions **environment `release-qualification`** with
   required independent human reviewers and restrict deployments to
   protected `main`. Then use **Actions → KIDE Release Candidate
   Operational Qualification** on `main`, giving:
   - The successful live staging **run ID** for the exact main SHA
   - The restored staging snapshot's 64-character lowercase SHA-256
   - Traceable, nonsecret backup/restore and hosted rollout/rollback
     evidence IDs
   - The exact word `verified` for all four human-tested desktop platforms
   - `I_APPROVE_THIS_SHA_FOR_PRODUCTION_RELEASE` only after reviewing
     the evidence and accepting the measured limitations

   This workflow verifies the real GitHub staging run's exact SHA,
   dispatch origin, success result **and required real acceptance job**.
   It does not generate fictitious evidence. The protected environment
   makes the human review explicit and auditable; the supplied evidence
   IDs must be backed by operator-held drill records.
7. Finally, after both the staging and subsequent operational review
   workflows have succeeded on the **same latest main SHA**, use
   **Actions → Release KIDE** on `main` with the intended new semver.
   The release refuses a skipped, failed, stale or unapproved run. The
   existing protected `trusted-release` environment still requires
   signing credentials for JARs, Windows Authenticode, Apple Developer ID
   notarization and stapling.
8. Signed desktop archives, the Eclipse p2 site, CycloneDX SBOM, integrity
   checksums and GitHub provenance attestations must be present before
   publication. A fully published stable is marked Latest; **only after**
   verifying its complete asset inventory are older release records retired.
   All version tags and Git commits remain as rollback/source evidence.

## Explicit security and availability limitations

- A success status for an Actions job alone does not attest that real
  NFS backup/restoration was performed; the operational reviewer must
  check the stored evidence IDs, outputs, objective measurements and
  environment before entering approval.
- If staging infrastructure, authorized Firebase credentials, POSIX
  filesystem or signing certificates are unavailable, **do not** run
  the operational qualification or publish any stable GitHub Release.
- A release is a desktop artifact. It does not automatically promote
  the hosted Web/API deployment. The approved Cloud Run promotion still
  requires service identity, role, migration, health and rollback checks.
- Staging test projects are disposable but persistent; respect the
  registry's per-principal quota. Never run live acceptance against
  production models.
- Fix any real failure within the responsible PR, repeat the required
  qualification against the newly proposed SHA, and obtain fresh
  human signoff. New commits invalidate earlier evidence.
