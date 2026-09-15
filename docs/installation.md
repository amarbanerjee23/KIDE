# Installing KIDE

Two ways in. Pick the first unless you already live in Eclipse.

## 1. The KIDE Modelling Studio (recommended)

A complete application. Nothing else to install except Java.

| Platform | File |
| --- | --- |
| Windows x64 | `KIDE-1.0.0-win32.win32.x86_64.zip` |
| Linux x64 | `KIDE-1.0.0-linux.gtk.x86_64.tar.gz` |
| macOS Intel | `KIDE-1.0.0-macosx.cocoa.x86_64.tar.gz` |
| macOS Apple silicon | `KIDE-1.0.0-macosx.cocoa.aarch64.tar.gz` |

1. Download the archive for your platform from the
   [Releases page](https://github.com/amarbanerjee23/KIDE/releases).
2. Check it: `sha256sum -c SHA256SUMS.txt`.
3. Unpack it somewhere you can write to — not `C:\Program Files` unless you
   intend to run as an administrator.
4. Start `kide` (`kide.exe` on Windows, `KIDE.app` on macOS).

The first start creates a workspace at `~/kide-workspace`. Use
`kide -data /path/to/workspace` to choose another one.

### Prerequisites

- Java 11 or newer (17 recommended). `java -version` should print 11 or above.
- 4 GB RAM minimum, 8 GB for large models.
- 2 GB free disk space.

If the launcher cannot find Java, point at it explicitly:

```
kide -vm /usr/lib/jvm/temurin-17/bin/java
```

### macOS: unsigned builds

Unless the release notes say the build is notarised, macOS refuses the first
launch. Right-click **KIDE.app > Open** and confirm, or run:

```
xattr -dr com.apple.quarantine /Applications/KIDE.app
```

## 2. Into an existing Eclipse

Requires **Eclipse IDE for Java and DSL Developers** 2021-12 or newer with
Sirius installed.

1. **Help > Install New Software…**
2. **Add… > Archive…**, choose `kide-p2-repository.zip` from the release, or use
   the update site URL published with the release.
3. Tick **KIDE Modelling Tools**. Add **KIDE Language Development Kit** only if
   you intend to change the languages themselves.
4. Finish, accept the licence, restart.
5. **Window > Perspective > Open Perspective > Other… > KIDE Modelling**.

## First model

**File > New > KIDE Modelling Project**. The project already contains a data
model, a component interface, a capability and an activity flow that validate.
Open `models/Loading.cap` and start editing; **Help > Welcome** has three guided
tours through the same files.

## Uninstalling

Delete the unpacked folder, or use **Help > About > Installation Details >
Uninstall…** for the plug-in install. Your workspace is a separate folder and is
never deleted for you.
