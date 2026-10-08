# Release process

A production release is a tag or an explicitly dispatched version. The trusted
release workflow is fail-closed: if any required signing or notarisation
credential is unavailable, no production release is published.

## Versioning

Semantic versioning: `MAJOR.MINOR.PATCH`.

- **MAJOR** — a model written in the previous version no longer loads or
  validates unchanged.
- **MINOR** — new language constructs or tooling, backwards compatible.
- **PATCH** — fixes only.

OSGi versions in `MANIFEST.MF` and `feature.xml` use
`MAJOR.MINOR.PATCH.qualifier`; Tycho fills the qualifier from the git commit
timestamp.

## Steps

1. **Freeze.** Confirm the `Build KIDE` workflow is green on `main`. The build
   also enforces immutable commit-SHA pins for every external GitHub Action.
2. **Set versions.**

   ```bash
   mvn org.eclipse.tycho:tycho-versions-plugin:4.0.8:set-version -DnewVersion=1.1.0-SNAPSHOT
   ```

   This updates every manifest, feature, product and pom together. Check the
   diff; hand edits are a common source of mismatched versions.
3. **Write the changelog.** Move the *Unreleased* entries under the new version
   with today's date. Every entry describes what a user notices.
4. **Build locally once.**

   ```bash
   mvn clean verify
   ```

   Then actually start the product from
   `releng/com.kide.repository/target/products/` and run the smoke test.
5. **Confirm protected release credentials.** All E02 secrets in the table below
   must be configured in GitHub before a production tag is pushed.
6. **Commit and tag.**

   ```bash
   git commit -am "Release 1.1.0"
   git tag -a v1.1.0 -m "KIDE 1.1.0"
   git push && git push --tags
   ```
7. **Watch the release workflow.** It signs Eclipse bundles, Authenticode-signs
   the Windows launcher, Developer-ID-signs and notarises both macOS products,
   staples the Apple tickets, recomputes evidence from the final bytes, emits a
   CycloneDX SBOM, creates GitHub build-provenance attestations, publishes the
   immutable release, and updates the p2 update site.
8. **Verify the published evidence.** Check `SHA256SUMS.txt`,
   `release-manifest.json`, and the CycloneDX SBOM against the downloaded files.
   Verify GitHub artifact attestations with GitHub's attestation tooling before
   mirroring artifacts to another repository.
9. **Reopen development.** Set the next `-SNAPSHOT` version and add an empty
   *Unreleased* section.

## Smoke test before every release

On at least one platform, ideally all three:

1. The product starts with no errors in `.metadata/.log`.
2. Splash and About show the right version and build id.
3. The KIDE Modelling perspective opens by default.
4. **New > KIDE Modelling Project** creates a project, opens `Loading.cap` and
   shows no problems.
5. `Ctrl+Space` offers the block templates in a `.cap` file.
6. Breaking a command name produces a readable problem, and `Ctrl+1` offers a fix.
7. Enabling the viewpoints opens a diagram with the KIDE palette.
8. The activity-to-MNC transformation runs on the example activity model.
9. **Help > Welcome** shows the three tours and each one opens.
10. Installing `kide-p2-repository.zip` into a clean Eclipse succeeds.

## Changing the target platform

Pinned versions in `releng/com.kide.target/com.kide.target.target` are a release
decision, never a drive-by change:

- Xtext must still export `org.apache.log4j`, or the generated UI bundles have to
  be regenerated against the newer Xtext first.
- Sirius must match the Eclipse stream.
- Bump it in its own pull request, run the full smoke test, and note it in the
  changelog under *Changed*.

## Protected release credentials

The production workflow will not downgrade to an unsigned release. Store these
as protected GitHub Actions secrets and restrict who can trigger the release
environment.

| Secret | Meaning |
| --- | --- |
| `KIDE_JAR_KEYSTORE_BASE64` | base64-encoded Java signing keystore used by Maven jarsigner |
| `KIDE_KEY_ALIAS` | Java signing key alias |
| `KIDE_KEYSTORE_PASSWORD` | Java signing keystore password |
| `WINDOWS_CERTIFICATE_BASE64` | base64-encoded Authenticode PFX/P12 certificate |
| `WINDOWS_CERTIFICATE_PASSWORD` | Windows code-signing certificate password |
| `APPLE_SIGNING_CERT_BASE64` | base64-encoded Developer ID Application P12 certificate |
| `APPLE_SIGNING_CERT_PASSWORD` | Developer ID certificate password |
| `APPLE_SIGNING_IDENTITY` | exact `Developer ID Application: ...` codesign identity |
| `APPLE_ID` | Apple account used by `notarytool` |
| `APPLE_APP_PASSWORD` | Apple app-specific password for notarisation |
| `APPLE_TEAM_ID` | Apple Developer team identifier |

Certificate material is decoded only into runner-temporary storage. It is never
written to the repository, workspace metadata, release archives, or logs.
