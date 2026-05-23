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
4. Open one of the modeling entrypoints:
   - `com.mncml/representations.aird`
   - `com.model.domain.activity.design/representations.aird`
5. Use the textual editors and transformations from the DSL projects.

## Notes

- This repository already includes generated Java/classes for multiple modules.
- The demonstrator script creates a distribution-oriented view (zip + manifest + quickstart) to make the workspace usable as a product handoff artifact.
