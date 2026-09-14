# KIDE Modelling Studio product

`kide.product` describes a self-contained Eclipse application that ships the KIDE
language editors, the Sirius design projects and the Welcome page with the three
guided tours, so an evaluator does not have to install and wire an Eclipse
Modeling Tools workspace by hand.

## Export from the IDE

1. Open `kide.product` in Eclipse (PDE product editor).
2. Set the target platform to Eclipse Modeling Tools 2023-12 or newer.
3. Press **Eclipse Product export wizard**, tick *Generate p2 repository*, export to `demo/product/`.

## What the user gets

- a `kide` launcher, no plug-in installation required,
- the example workspace preloaded at first start (`demo/example-workspace`),
- the Welcome page with the three guided tours opened automatically,
- the Sirius viewpoints already registered.
