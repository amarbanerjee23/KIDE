# KIDE — Knowledge-Integrated DSL Engineering Workspace

This repository contains the implementation artifacts accompanying the PhD work (DSLs, metamodels, editors, model transformations, and Sirius-based modeling environments).

## What is in this repository

The codebase is organized as an Eclipse/Xtext/Sirius multi-project workspace, including:

- `com.mncml` — EMF metamodel implementation for MNC domain objects.
- `com.mncml.dsl` / `com.mncml.dsl.ui` — textual DSL and Eclipse UI integration for MNC modeling.
- `com.smr.activity.dsl` — activity DSL components.
- `com.smr.activity.activity2mnc` — transformation logic from activity models to MNC models.
- `com.model.domain.*.design` — Sirius viewpoint/design projects for graphical modeling.
- Additional support projects (`com.capability.dsl.*`, `com.dml`, etc.).

## Getting started in one step

Three ways in, from least to most setup:

| I want to… | Do this |
| --- | --- |
| See what KIDE does, in a browser, with nothing installed | `cd kide-web-demo && npm install && npm run dev` |
| Watch my own models render as I edit them | `node tools/kide-live-preview/server.js demo/example-workspace` |
| Use the full Eclipse tooling | Build the KIDE Modelling Studio product (below) |

### KIDE Modelling Studio

`releng/com.kide.product/kide.product` defines a ready-made Eclipse product with
every KIDE plug-in, Xtext, EMF and Sirius already inside. Open it in Eclipse and
choose **Launch an Eclipse application**, or export it to get a standalone
application. It starts on a Welcome page with three guided tours
(**Help → Welcome**, or **Help → Cheat Sheets → KIDE guided tours**):

1. Write your first capability and activity model.
2. Transform an activity model into an MNC model.
3. Open and read the Sirius diagrams.

`demo/example-workspace` holds a small worked example — a loading interface, the
capability built on it, and a mission-planning activity diagram — that the tours
walk through.

### Browser demo

`kide-web-demo/` is a self-contained web page: type a KIDE-style model on the
left and the diagram builds itself on the right, with plain-language validation
underneath. Useful for showing the idea to someone who will not install Eclipse.

### Live preview for the real editors

`tools/kide-live-preview/` watches a folder of `.cap` and `.activity` files and
serves an auto-refreshing diagram. See its README for details.

## The KIDE visual language

One colour per concept, in the Sirius diagrams, the editors, the browser demo
and the live preview:

| Concept | Colour |
| --- | --- |
| Capability | blue `#5284e2` |
| Activity | amber `#e2982e` |
| Data and outcomes | green `#3ea676` |
| Structure and containers | slate `#606e85` |

`tools/apply_visual_language.py` installs that palette into the Sirius design
files. Re-run it after editing a `.odesign` with the Sirius specification editor
if new system colours creep back in.

## Editor experience

The capability and activity editors now offer syntax and semantic colouring in
the palette above, hover documentation explaining each element in plain words,
content-assist templates (`capability`, `controlCapabilities`, `init`,
`activityDiagram`, `activity`, `conditionalActivity` — press Ctrl+Space), and
quick fixes on the problems the validators report. Validation messages say what
is wrong and what to do about it, rather than restating the rule.

## Demonstrable product build

A reproducible demonstrator packaging step is provided:

```bash
python scripts/build_demo_product.py
```

This generates:

- `demo/kide-demonstrator.zip` — a portable demonstrator package.
- `demo/QUICKSTART.md` — operator instructions to run the tooling in Eclipse.
- `demo/product/manifest.json` — discovered plugin/bundle inventory for the demonstrator.

## How to run the demonstrator

1. Install **Eclipse Modeling Tools** (2023-12 or newer) with Xtext and Sirius.
2. Import all folders in this repository as existing Eclipse projects.
3. Run `Project -> Build All`.
4. Or skip steps 1-3 entirely and build the KIDE Modelling Studio product described above.
5. Open one of the modeling entrypoints:
   - `com.mncml/representations.aird`
   - `com.model.domain.activity.design/representations.aird`
5. Use the textual editors and transformations from the DSL projects.

## Notes

- This repository already includes generated Java/classes for multiple modules.
- The demonstrator script creates a distribution-oriented view (zip + manifest + quickstart) to make the workspace usable as a product handoff artifact.
