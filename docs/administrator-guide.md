# Administrator guide

For rolling KIDE out to a team or a controlled environment.

## Deployment options

| Option | When | How |
| --- | --- | --- |
| Unpacked studio per user | Default. No admin rights needed. | Copy the archive to a share; users unpack it locally. |
| Shared read-only install | Locked-down desktops. | Unpack once to a read-only path; give each user their own configuration and workspace area. |
| Plug-ins into a managed Eclipse | You already manage an Eclipse estate. | Mirror the p2 repository and add it to the managed install. |

### Shared read-only installation

The installation directory must never be written to by users. Give each user
private areas in `kide.ini`:

```
-configuration
@user.home/.kide/configuration
-data
@user.home/kide-workspace
```

## Mirroring the update site

Air-gapped or bandwidth-limited sites should mirror rather than reach GitHub
from every desktop:

```
eclipse -application org.eclipse.equinox.p2.artifact.repository.mirrorApplication \
  -source https://amarbanerjee23.github.io/KIDE/updates \
  -destination file:/srv/p2/kide
eclipse -application org.eclipse.equinox.p2.metadata.repository.mirrorApplication \
  -source https://amarbanerjee23.github.io/KIDE/updates \
  -destination file:/srv/p2/kide
```

Publish `/srv/p2/kide` over HTTP internally and point clients at it.

## Silent installation

```
eclipse -application org.eclipse.equinox.p2.director -noSplash \
  -repository https://your.internal/p2/kide \
  -installIU com.kide.feature.feature.group
```

Uninstall with `-uninstallIU com.kide.feature.feature.group`.

## Standardising settings

Everything in `com.kide.branding/plugin_customization.ini` is a *default*,
applied on first start only. To standardise afterwards:

1. Configure one machine the way you want it.
2. **File > Export > General > Preferences**, save `kide-team.epf`.
3. Distribute it for **File > Import > General > Preferences**, or start with
   `kide -pluginCustomization /path/to/team.ini`.

## Controlling updates

The studio does not check for updates on start-up. To offer in-place updates,
add your mirrored site under **Window > Preferences > Install/Update > Available
Software Sites** and let users run **Help > Check for Updates**; otherwise push
new archives through your normal software distribution.

To block updates entirely, remove write permission on the installation
directory — p2 then reports that it cannot modify the install.

## Memory and performance

Defaults live in `kide.ini` (`-Xms512m -Xmx2048m`). For large workspaces:

```
-Xmx4096m
-XX:MaxMetaspaceSize=1024m
```

Sirius diagram refresh is the usual cause of slowness on big models; refresh
behaviour is under **Window > Preferences > Sirius**.

## Logs and diagnostics

| What | Where |
| --- | --- |
| Workbench log | `<workspace>/.metadata/.log` |
| Error Log view | **Window > Show View > Error Log** |
| Installed content and versions | **Help > About > Installation Details** |

Start with `kide -clean` after any change to installed plug-ins, and
`kide -consoleLog -debug` when reproducing a start-up problem.

## Security posture

- Artefacts carry SHA-256 checksums; verify before distribution.
- `-Declipse.p2.unsignedPolicy=allow` is set so that unsigned development builds
  start. Remove it from `kide.ini` in regulated environments and distribute
  signed builds only.
- KIDE makes no outbound network calls of its own; network access happens only
  when a user explicitly checks for updates.
