# PR93 — Single latest stable GitHub Release

## Policy

Keep exactly one published GitHub Release after a successful release cycle: the newest stable, signed KIDE desktop build (vMAJOR.MINOR.PATCH). Routine pull requests, branch builds and manually dispatched CI packaging jobs do not publish preview/unsigned GitHub Releases. Temporary build packages remain in GitHub Actions artifacts only for their configured retention.

Cloud Run and GHCR images are separately qualified; this policy does not delete containers, Git tags, history, commits or prior workflow logs.

## Existing inventory

At PR93 implementation, GitHub returned **20 public Releases, all marked prerelease**, with the newest dated September 27, 2026. There was **no published stable Release** and the GitHub latest-stable endpoint returned HTTP 404.

Cleanup must not delete the old 20 until a newly signed and qualified stable Release is published; otherwise the Releases page would be empty. This code-only change does NOT claim that those 20 Releases have already been deleted.

## Trusted publishing sequence

1. Verify stable vX.Y.Z tag or manual main dispatch. The source commit must equal current main HEAD, not merely be an ancestor. Serialize releases with one concurrency group.
2. Verify both Build KIDE and KIDE Enterprise CI Pipeline completed successfully for the exact same main commit SHA; a newer failed or pending rerun blocks publication.
3. Reject prerelease suffixes, stale versions, downgrades and existing stable release tags.
4. Require the protected trusted-release environment and production signing credentials. Build Eclipse signed JARs, Authenticode sign Windows, Developer ID sign and notarize both macOS architectures, validate artifacts and recompute final SHA-256, release manifest, CycloneDX SBOM and provenance attestations.
5. Publish the Eclipse update site, then create the GitHub stable latest Release.
6. Only after new release creation succeeds, run retain_latest_stable_release.py. It verifies the GitHub latest-stable API points to the new tag, its source commit matches the qualified SHA and all expected nonempty signed packages and evidence assets are uploaded. It paginates over the full release inventory and refuses cleanup if an equal/newer stable version is present.
7. Delete all other GitHub Release records, including old previews. Preserve historical Git tags and source commits. Report a failed cleanup if any verification or deletion fails.

The GitHub API cannot atomically create one release while deleting all others: brief overlap is possible while cleanup runs; a failure leaves older releases visible until a retry.

## Authorized operator runbook

Protect main and configure the trusted-release GitHub environment with required reviewers, secret access and signing credentials. Choose a new vX.Y.Z version and verify Build KIDE and enterprise CI are green on the current main SHA. Start the Release KIDE workflow manually from main or push the corresponding stable version tag pointed to current main. All signing, notarization, attestation and cleanup jobs must pass.

For an optional **dry run**, use a fine-grained GitHub token restricted to this repository, supplied as the GH_TOKEN environment variable, and execute:

    python3 scripts/retain_latest_stable_release.py --repository amarbanerjee23/KIDE --keep-tag v1.0.0 --expected-sha FULL_40_CHARACTER_SOURCE_SHA

No deletion takes place by default. The actual workflow passes --apply only after publication. If cleanup fails due to an API error, an authorized operator can rerun with --apply after reconfirming the newest stable keeper has all required assets. Never include a token in a command argument or commit it to the repository.

A passing PR, an unsigned desktop ZIP, preview tag or image push is **not** a signed stable product release. The pipeline fails closed if signing requirements are incomplete. It also does not independently certify deployed Firebase/LSP/GLSP services or disaster recovery.
