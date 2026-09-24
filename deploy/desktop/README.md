# KIDE desktop launchers

KIDE desktop releases contain a platform-native Eclipse product with an embedded
Java runtime. Users do not need to install Java separately.

The release entry points are:

- **Windows x86_64:** `KIDE.exe` — the real Tycho/Eclipse native executable.
- **Linux x86_64:** `KIDE.sh` — executable wrapper that launches the native
  `kide` Eclipse binary beside it.
- **macOS Intel / Apple silicon:** `KIDE.command` — double-clickable executable
  wrapper beside `KIDE.app`, launching the native app binary.

The wrappers do not replace the platform-native Eclipse launchers. They provide
stable, obvious user-facing entry points while preserving the signed/notarized
desktop application underneath.

`scripts/prepare_desktop_release.py` injects the Linux/macOS wrappers into
staged release archives with mode `0755` and stages the native Windows
launcher as `KIDE.exe`. The release manifest records the selected
`entryPoint` for each platform.
