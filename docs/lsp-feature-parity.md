# LSP and Eclipse editor-service parity contract

KIDE qualifies the browser editor against the same semantic services used by the
Eclipse product. The browser is a separate client: Monaco renders the editor,
while Xtext/KIDE services continue to own parsing, linking, validation, content
assist, hover, navigation, refactoring, formatting and semantic classification.

## Current Xtext 2.44 baseline

`product/lsp-capabilities.json` is the machine-readable contract for every
production DSL in `product/languages.json`.

The packaged language-server product must qualify:

- diagnostics and cross-file linking;
- content assist, including the semantic candidate filtering used by the Eclipse
  Capability, MNC and Activity editors;
- hover, including the custom Capability and Activity descriptions shared with
  Eclipse;
- definition/hyperlink navigation where applicable;
- references and document occurrences/highlights;
- document and workspace symbols/outline;
- document and range formatting;
- rename refactoring, including cross-language edits;
- folding;
- semantic tokens for languages with custom Eclipse semantic highlighting; and
- code actions for every active Eclipse quick fix.

The parity harness does not consider a capability complete merely because the
server advertises it. It runs functional protocol probes against the packaged
language-server product.

## Shared Eclipse/Web services

Eclipse-only semantic implementations are not duplicated in TypeScript.

- Capability and Activity semantic highlighting use platform-neutral region
  providers consumed by both the Eclipse highlighter and LSP semantic-token
  adapter.
- MNC semantic highlighting has been moved to the same shared-region pattern.
- Capability and Activity custom hover text is produced by shared providers,
  with Eclipse HTML and LSP Markdown adapters.
- Capability, MNC and Activity semantic content-assist filtering is available in
  the headless `.ide` bundles used by the language server.
- MNC, Capability and Activity Eclipse quick fixes are exposed as LSP code
  actions with the same validator issue codes and edits.
- DML, Operation and KRL have no separate custom Eclipse semantic highlighter or
  custom quick-fix service; they continue to use the shared/default language
  services.

The Operation editor's executable-script chooser is an Eclipse UI affordance
(SWT resource dialog), not a language-semantic service. KIDE Web provides the
equivalent workspace-file picker and inserts the selected quoted path into the
Operation editor.

## Explicitly not-applicable LSP concepts

Complete parity does not mean inventing services that the Eclipse KIDE editors do
not have. The matrix marks these as `not_applicable` with a reason:

- snippet service: KIDE has no separate Eclipse snippet semantic service;
  snippet-shaped completion items remain supported through completion;
- declaration navigation: KIDE uses definition/hyperlink navigation;
- type-definition navigation: KIDE model elements do not have a separate
  Eclipse type-definition service;
- implementation navigation: KIDE model elements do not have a separate
  Eclipse implementation service; and
- signature help: KIDE's DSL editors do not ship a separate parameter-signature
  service, and Xtext's default service is intentionally no-op.

These entries are not allowed to remain vaguely `deferred` on the complete
parity baseline.

## Qualification

The permanent packaged parity flow verifies:

1. every required capability is advertised by the current Xtext 2.44 server;
2. every production DSL passes completion, hover, navigation, references,
   occurrences, symbols, formatting, rename and folding probes;
3. semantic-token streams are structurally valid and non-empty where KIDE has
   custom semantic highlighting;
4. Capability/MNC/Activity completion excludes known out-of-scope candidates;
5. the real validator diagnostic is published before a quick fix is requested;
6. the expected Eclipse quick-fix title and workspace edit are returned;
7. cross-language navigation/refactoring targets the correct project files; and
8. the packaged server starts and shuts down cleanly.

The browser client has independent JSON-RPC unit coverage for every mapped LSP
method, while Build KIDE retains the desktop product qualification. A change
cannot call itself editor-service parity if it only changes browser UI.
