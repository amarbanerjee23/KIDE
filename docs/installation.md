# Installing KIDE

KIDE is distributed both as a self-contained desktop application and as an
installable Eclipse update site. For most users, use the desktop application.

## 1. KIDE Modelling Studio (recommended)

The desktop archives contain the Eclipse launcher, KIDE plug-ins and a pinned
JustJ/Adoptium Java runtime. **A system-wide Java installation is not required.**
Release CI rejects any desktop archive that is missing its native launcher or
embedded runtime.

| Platform | Release file | Start KIDE |
| --- | --- | --- |
| Windows x64 | `KIDE-<version>-windows-x86_64.zip` | `kide.exe` |
| Linux x64 | `KIDE-<version>-linux-x86_64.tar.gz` | `./kide` |
| macOS Intel | `KIDE-<version>-macos-x86_64.tar.gz` | `KIDE.app` |
| macOS Apple silicon | `KIDE-<version>-macos-aarch64.tar.gz` | `KIDE.app` |

1. For a published production release, download the archive for your platform from the
   [Releases page](https://github.com/amarbanerjee23/KIDE/releases). If no release is
   listed yet, the repository has not reached a publishable signed release gate; do not
   treat intermediate CI products as a supported customer release.
2. Download `SHA256SUMS.txt` from the same release and verify the archive.
3. Unpack the archive to a user-writable application directory.
4. Start the native launcher shown above.

The first start creates a workspace at `~/kide-workspace`. Pass
`-data /path/to/workspace` to use a different workspace.

### Download integrity

Every release contains:

- `SHA256SUMS.txt` with a SHA-256 digest for every standalone archive;
- `release-manifest.json` recording the platform, launcher, bundled runtime and
  artifact digest; and
- `kide-p2-repository.zip` for existing Eclipse installations.

Linux/macOS users can verify all downloaded files with:

```sh
sha256sum -c SHA256SUMS.txt
```

On Windows, PowerShell can verify an individual archive with:

```powershell
Get-FileHash .\KIDE-<version>-windows-x86_64.zip -Algorithm SHA256
```

Compare the result with the matching entry in `SHA256SUMS.txt`.

### System requirements

- Windows x64, Linux x64, macOS x64, or macOS Apple silicon as listed above.
- 4 GB RAM minimum; 8 GB or more recommended for larger models/workspaces.
- At least 2 GB free disk space plus workspace capacity.
- No separately installed JDK/JRE is required for the standalone application.

### macOS signing status

KIDE's release pipeline distinguishes portable packaging from platform trust.
Production macOS releases must be Developer ID signed, notarised and stapled;
Windows releases must be Authenticode signed. The release workflow fails closed
when required signing credentials or evidence are unavailable, so unsupported
unsigned customer releases are not published. Do not disable Gatekeeper globally
to run KIDE.

## 2. Install into an existing Eclipse

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

Delete the unpacked application folder, or use **Help > About > Installation
Details > Uninstall…** for the plug-in installation. The workspace is stored
separately and is never deleted automatically.
