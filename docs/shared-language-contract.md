# Shared Xtext language contract

KIDE supports more than one client, but each DSL has exactly one semantic implementation.
The Eclipse RCP workbench is the current desktop client; the browser client will use an
Xtext language server and Eclipse Theia. Both clients must consume the same runtime and
IDE-layer services.

## Layering rule

The dependency direction is:

```text
model/runtime -> *.dsl runtime -> *.dsl.ide -> client adapters
                                      |            |-- Eclipse *.ui
                                      |            `-- LSP/Theia
                                      `-- reusable IDE semantics
```

`*.dsl` and `*.dsl.ide` bundles are shared language layers. They must not import or
require SWT, JFace, Eclipse UI, or `org.eclipse.xtext.ui`. Runtime DSL bundles must not
depend upward on IDE bundles. Eclipse `*.ui` bundles may depend on the shared layers and
translate their client-neutral results into workbench-specific APIs.

This rule keeps parsing, linking, validation, scoping, completion, navigation,
formatting, refactoring, semantic-region calculation, generation and other language
meaning reusable by desktop and web clients.

## W01 extraction

Capability and Activity semantic highlighting previously traversed EMF/Xtext models in
the Eclipse UI bundles. W01 moves that traversal into exported IDE-layer services:

- `com.capability.ide.highlighting.CapabilitySemanticRegionProvider`
- `com.smr.activity.dsl.ide.highlighting.ActivityDiagramSemanticRegionProvider`

The existing Eclipse semantic-highlighting calculators are now adapters over those
services. Their style IDs are also sourced from the shared providers, preventing Eclipse
and future LSP semantic-token identifiers from drifting apart.

## Enforcement

Run locally:

```bash
python3 -m unittest discover -s scripts/tests -p 'test_*.py'
python3 scripts/verify_language_layering.py
mvn -B -ntp clean verify
```

`verify_language_layering.py` discovers top-level `*.dsl` and `*.dsl.ide` bundles and
fails on client-UI imports/dependencies or reverse runtime-to-IDE dependencies. The same
checks run before the existing Maven/Tycho product build in CI.

## Acceptance criteria

A W01 change is acceptable only when:

1. Eclipse behavior remains intact.
2. reusable language semantics are callable without SWT/JFace/Eclipse UI classes.
3. shared runtime/IDE bundles satisfy the automated layering check.
4. the existing full Tycho build and all portable-product qualification gates remain
   green.

W02 will build the headless KIDE language-server process on this contract rather than
reimplementing any DSL behavior for the browser.
