# KIDE — Knowledge-Integrated DSL Engineering Workspace

KIDE is a modelling studio for capability, activity, data and MNC models:
textual languages with live validation, generated Sirius diagrams, and a
transformation from activity models to MNC models.

KIDE is built as a self-contained application for Windows, Linux and macOS, and as
a p2 update site for existing Eclipse installations. Signed public release artefacts
are published only by the fail-closed release workflow once release credentials and
evidence are available.

## Install

When a production release is published, download the archive for your platform from
the [Releases page](https://github.com/amarbanerjee23/KIDE/releases), unpack it and
run `kide`. The standalone product includes its own pinned Java runtime; no separate
JDK/JRE installation is required.

Full instructions, including installing into an existing Eclipse:
[`docs/installation.md`](docs/installation.md).

## First five minutes

1. **File > New > KIDE Modelling Project** — you get a project with a data
   model, a component interface, a capability and an activity flow that already
   validate.
2. **Help > Welcome** — three guided tours walk through those same files.
3. Edit `models/Loading.cap`. `Ctrl+Space` for block templates, hover anything
   for a plain-language explanation, `Ctrl+1` to fix a problem.

[`docs/user-guide.md`](docs/user-guide.md) covers the languages, the diagrams and
the transformation.

## The languages

<!-- KIDE-LANGUAGES:START -->
| Extension | Language | What you describe |
| --- | --- | --- |
| `.dml` | Data Model Language | primitive, composite and array data exchanged by engineered systems |
| `.op` | Operation Language | reusable executable operations and their typed inputs and outputs |
| `.mncspec` | MNC Specification Language | component interfaces, commands, events, alarms, responses and control nodes |
| `.cap` | Capability Language | capabilities bound to component interfaces and their controllable outcomes |
| `.activity` | Activity Language | supervisory workflows that consume capabilities and operations |
<!-- KIDE-LANGUAGES:END -->

## The KIDE visual language

One colour per concept, in the diagrams, the editors and the workbench theme:

| Concept | Colour |
| --- | --- |
| Capability | blue `#5284E2` |
| Activity | amber `#E2982E` |
| Data and outcomes | green `#3EA676` |
| Structure and containers | slate `#606E85` |

`tools/apply_visual_language.py` installs that palette into the Sirius design
files. Re-run it after editing a `.odesign` with the Sirius specification editor
if system colours creep back in.

## Building from source

JDK 17 and Maven 3.9+:

```bash
mvn clean verify
```

Produces:

- `releng/com.kide.repository/target/repository` — the p2 update site.
- `releng/com.kide.repository/target/products/` — the platform archives.

The build is Tycho pomless: plug-in and feature projects build from their
manifests. Third-party versions are declared in exactly one place,
`releng/com.kide.target/com.kide.target.target`.

## Repository layout

| Path | Contents |
| --- | --- |
| `com.capability`, `com.dml`, `com.mncml`, `com.operation`, `com.smr.activity` | EMF metamodels |
| `*.dsl`, `*.dsl.ide`, `*.dsl.ui` | Xtext grammars, validation, quick fixes, editors |
| `com.model.domain.*.design` | Sirius viewpoints |
| `com.smr.activity.activity2mnc` | activity-to-MNC transformation |
| `com.kide.branding`, `com.kide.welcome` | product identity, perspective, wizard, themes, tours |
| `releng/` | target platform, features, product, update site |
| `docs/` | installation, user, administrator, architecture, release docs |
| `demo/example-workspace` | the worked example the tours use |
| `tools/kide-live-preview` | lightweight diagram preview outside Eclipse |

## Documentation

- [Installation](docs/installation.md)
- [User guide](docs/user-guide.md)
- [Administrator guide](docs/administrator-guide.md)
- [Architecture](docs/architecture.md)
- [Release process](docs/release-process.md)
- [Contributing](CONTRIBUTING.md) · [Support](SUPPORT.md) · [Security](SECURITY.md) · [Changelog](CHANGELOG.md)

## Known limitations

- `com.system.knowledge.plugin` depends on NeoEMF, which is not in the target
  platform; it is research code and is not part of the release build.

## Licence

EPL-2.0. See [LICENSE](LICENSE) and [NOTICE.md](NOTICE.md).

This repository accompanies PhD work; the languages, metamodels and
transformations are the research contribution, packaged here as a product.
