# Release process

A release is a tag. Everything else is automated.

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

1. **Freeze.** Confirm the `Build KIDE` workflow is green on `main`.
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
5. **Commit and tag.**

   ```bash
   git commit -am "Release 1.1.0"
   git tag -a v1.1.0 -m "KIDE 1.1.0"
   git push && git push --tags
   ```
6. **Watch the release workflow.** It builds, signs when signing secrets exist,
   uploads archives with checksums and publishes the update site to GitHub Pages
   under `/updates`.
7. **Reopen development.** Set the next `-SNAPSHOT` version and add an empty
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

## Signing

| Secret | Meaning |
| --- | --- |
| `KIDE_KEYSTORE_PATH` | path to the keystore on the runner |
| `KIDE_KEY_ALIAS` | key alias |
| `KIDE_KEYSTORE_PASSWORD` | keystore password |

Without them the release is still produced, clearly marked as unsigned.
