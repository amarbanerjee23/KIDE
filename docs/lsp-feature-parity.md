# LSP Feature Parity Contract

PR13 freezes KIDE's client-neutral textual editor behavior before generated-code cleanup and the Xtext/Eclipse/Java modernization.

The contract is intentionally tested against the **packaged** KIDE language-server product. It does not compare screenshots and it does not duplicate parsing, scoping, validation, completion, refactoring or formatting logic in a second client.

## Qualified baseline

`product/lsp-capabilities.json` is the machine-readable qualification matrix. It covers every production DSL from `product/languages.json`.

The legacy Xtext 2.25 baseline must retain:

- diagnostics and clean cross-file linking;
- root completion proposals;
- hover request handling;
- definition navigation where the fixture contains a semantic cross-reference;
- references;
- document symbols and workspace symbols;
- document formatting;
- rename refactoring, including cross-language edits for MNC -> Capability and Capability -> Activity; and
- folding.

The matrix also records features that are intentionally **deferred/unqualified** on the legacy baseline: snippets, code actions, semantic tokens, declaration, type definition and implementation navigation. A feature may be promoted only by changing the matrix and adding a functional probe in the same PR.

## Why the matrix is explicit

A language server can advertise a capability while returning useless or structurally broken responses. PR13 therefore tests both layers:

1. the server must advertise every required capability;
2. every production DSL must pass a functional protocol probe;
3. cross-language definition and rename must target the correct files;
4. the server must start from packaged bytes and shut down cleanly.

This contract is the safety net for PR15 generated-code cleanup and PR16 platform modernization.

## CI

The permanent Build KIDE flow is:

```text
repository governance
-> product truth
-> LSP parity matrix verification
-> full Tycho build
-> four desktop product qualification
-> packaged enterprise-context self-check
-> five-DSL valid/invalid diagnostic smoke
-> PR13 full LSP feature parity
```

Expected model errors remain diagnostics. Uncaught application exceptions, malformed protocol responses, missing required capabilities, broken cross-file navigation/refactoring or non-zero server shutdown fail the build.
