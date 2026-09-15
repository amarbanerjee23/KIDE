# Security policy

## Supported versions

| Version | Supported |
| --- | --- |
| 1.0.x | yes |
| earlier / unreleased snapshots | no |

Fixes are made on the latest release line only.

## Reporting a vulnerability

Please do **not** open a public issue.

1. Use the repository's **Security** tab > *Report a vulnerability* (private
   disclosure), or
2. e-mail the maintainer — contact details are in [SUPPORT.md](SUPPORT.md).

Include what you did, what happened, and the version from **Help > About**.

You will get an acknowledgement, an assessment, and a fix or an explanation of
why the report is out of scope. KIDE is maintained alongside research work, so
please allow reasonable time before public disclosure.

## Scope

In scope: the KIDE plug-ins, the product configuration, and the release
pipeline in `.github/workflows`.

Out of scope: vulnerabilities in the Eclipse Platform, Xtext or Sirius
themselves — report those to their projects; we will update the pinned target
platform once a fix is published.

## Supply chain

- Every release carries SHA-256 checksums.
- The build is reproducible from the pinned target platform; no dependency is
  resolved from a floating version.
- Release artefacts are signed when signing secrets are configured; unsigned
  builds are labelled as such in the release notes.
