# Changelog

All notable changes to KIDE are recorded here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the project uses
[semantic versioning](https://semver.org/).

## [Unreleased]

## [1.0.0] — 2026-09-15

First packaged product release.

### Added

- **KIDE Modelling Studio**: a standalone application for Windows, Linux and
  macOS with its own splash screen, icons, About dialog and `kide` launcher.
- **KIDE Modelling perspective**, opened by default: models on the left, editors
  in the middle, outline and guided tours on the right, problems below.
- **New > KIDE Modelling Project** wizard: creates a project with a data model,
  a component interface, a capability and an activity flow that already
  validate, and opens the first model for you.
- **KIDE Light and KIDE Dark** workbench themes using the KIDE palette.
- **First-run defaults**: line numbers, UTF-8, two-space indentation, autobuild
  and Sirius reload behaviour, all overridable.
- **Welcome page** with three guided tours and a one-click project action.
- **Reproducible Tycho build** producing a p2 update site and platform archives,
  from a pinned target platform.
- **GitHub Actions**: build on every push; on a tag, build, checksum, optionally
  sign, publish the release and the update site.
- **Enterprise documentation**: installation, user guide, administrator guide,
  architecture, release process, contributing, support, security, notices.

### Changed

- The product is now feature-based, so p2 can install, update and uninstall it.
- Every plug-in targets JavaSE-11 consistently.
- Validation messages across the capability and activity languages are written
  in plain language and paired with quick fixes.
- Sirius diagrams use one palette: capability blue, activity amber, data green,
  structure slate.

### Fixed

- The product referenced an undefined id (`com.kide.product.kide`); the Welcome
  page now binds to the real product.
- Two different activity validation problems shared one issue code, so the wrong
  quick fix could be offered.

### Known limitations

- `com.system.knowledge.plugin` is excluded from the build: it depends on NeoEMF,
  which is not in the target platform.
- `.mncspec` has no grammar in this repository, so those files open as plain text
  and are not validated.
