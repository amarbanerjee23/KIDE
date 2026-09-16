# Architecture

KIDE is an Eclipse product built from four groups of plug-ins.

```text
                    com.kide.branding          product, perspective,
                    com.kide.welcome           wizard, themes, tours
                              |
   ---------------------------------------------------------------
   |             |               |                |              |
 com.capability com.dml      com.mncml      com.operation   com.smr.activity     metamodels (EMF)
   |             |               |                |              |
 *.dsl / *.dsl.ide / *.dsl.ui                                                   languages (Xtext)
   |                                                              |
 com.model.domain.mnc.design                     com.smr.activity.activity2mnc   diagrams and
 com.model.domain.activity.design                                                transformation (Sirius)
```

## Layers

| Layer | Projects | Responsibility |
| --- | --- | --- |
| Metamodels | `com.capability`, `com.dml`, `com.mncml`, `com.operation`, `com.smr.activity` | The EMF models, generated from Ecore. Nothing here knows about the UI. |
| Languages | `*.dsl`, `*.dsl.ide`, `*.dsl.ui` | Grammar, parser, scoping, validation, quick fixes, highlighting, hovers, templates. |
| Diagrams | `com.model.domain.*.design` | Sirius viewpoint specifications (`*.odesign`). |
| Transformation | `com.smr.activity.activity2mnc` | Turns a validated activity model into an MNC model. |
| Product | `com.kide.branding`, `com.kide.welcome` | Identity, perspective, wizard, themes, first-run defaults, onboarding. |

## Release engineering

| Path | What it is |
| --- | --- |
| `pom.xml`, `.mvn/extensions.xml` | Tycho pomless build; bundle poms derive from the manifests. |
| `releng/com.kide.target` | The pinned target platform — the only place third-party versions are declared. |
| `releng/com.kide.feature` | What a user installs: every runtime plug-in. |
| `releng/com.kide.sdk.feature` | Optional MWE2 generator for people changing the grammars. |
| `releng/com.kide.repository` | The p2 update site and the product definition (`kide.product`). |
| `.github/workflows` | Build on every push; build, checksum, sign and publish on a tag. |

## Decisions worth knowing

**One visual language.** Capability blue, activity amber, data green, structure
slate — in the diagrams, the syntax colouring, the icons and the workbench
theme. `tools/apply_visual_language.py` applies the palette to the `.odesign`
files; it rewrites them textually because Sirius XMI is namespace-prefix
sensitive and a generic XML writer corrupts it.

**Feature-based product.** The product lists features, not plug-ins, so p2 can
install, update and uninstall it. A new plug-in needs an entry in a feature *and*
in `<modules>`; forgetting either means it silently does not ship.

**Pinned platform.** Xtext is held at 2.25 because the generated UI bundles
import `org.apache.log4j`, which later Xtext releases no longer export. Moving
that pin means regenerating the language artefacts.

**Excluded from the product.** `com.system.knowledge.plugin` needs NeoEMF
(`fr.inria.atlanmod.*`), which is not in the target platform, and `.mncspec` has
no grammar in this repository, so those files open as plain text. Both are
recorded in `NOTICE.md` and the changelog rather than quietly dropped.

**Optional views are placeholders.** The KIDE perspective adds Sirius and console
views as placeholders, so it still opens cleanly where those components are
missing.
