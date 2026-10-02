# Browser textual-language production parity

PR31 connects the separate KIDE browser product to the same five-language Xtext
language server already qualified for desktop/headless use. No parser, validator,
scope provider, linker, rename engine or other model semantics are implemented in
TypeScript.

## Browser/server URI boundary

Browsers use project-relative URIs of the form:

`kide-workspace:/path/to/model.dml`

The secure WebSocket gateway translates these URIs to the configured authorized
project root before forwarding JSON-RPC to Xtext, applies the existing canonical
path/symlink boundary, and translates server file URIs back before responses cross
the WebSocket. Server filesystem paths therefore are not a browser contract.

The enterprise project response now includes the stable E04 `workspaceId` as an
additive optional v1 field. The browser uses that ID only to select the authorized
LSP workspace.

## Monaco language services

The browser registers Monaco providers only for capabilities advertised by the
server. The client maps:

- diagnostics;
- completion, including snippet-shaped Xtext completion items;
- hover;
- definition and references;
- document occurrences/highlights;
- document symbols and workspace-symbol search;
- document and range formatting;
- rename, including multi-document text edits;
- folding ranges;
- code actions/quick fixes; and
- semantic tokens.

The authoritative capability matrix distinguishes required, required-where-
applicable and explicitly not-applicable services. The browser does not invent
semantic quick fixes, candidate filtering or semantic classification in
TypeScript: those implementations live in the shared/runtime or `.ide` Java
layers used by the packaged language server.

## Lexical highlighting

TextMate lexical assets are generated from the production Xtext grammar files and
`product/languages.json`. The generated asset file is verified in CI by
`scripts/generate_web_language_assets.py --check`. This gives Monaco a local,
fast lexical fallback without creating a second hand-maintained language
definition. Xtext 2.44 semantic tokens overlay that lexical baseline for DSLs with custom
semantic regions. Capability, MNC and Activity use the same platform-neutral
region providers as the Eclipse semantic highlighters.

## Authentication and transport

The browser WebSocket uses the existing `kide.lsp.v1` subprotocol and passes the
OIDC access token only in the credential subprotocol defined by PR22. The token is
not placed in the URL, project files or persistent browser storage.

## Qualification

PR31 adds:

- Java tests for browser/server URI virtualization and path-escape rejection;
- browser JSON-RPC unit tests covering every mapped LSP method;
- generated-language-asset drift checks;
- Playwright WebSocket qualification proving initialization at
  `kide-workspace:/`, project model `didOpen`, and workspace-symbol round trip;
- all existing packaged Xtext parity, gateway, HTTP, desktop and enterprise gates.

The browser uses the same Xtext service contracts as the packaged LSP parity
matrix; the web client does not weaken or replace those tests.
