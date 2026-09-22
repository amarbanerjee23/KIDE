# KRL and semantic code generation

PR38 makes KIDE Knowledge Representation Language (KRL) the sixth production Xtext DSL
and binds it to a versioned, deterministic semantic code-generation service.

## Language ownership

KRL is implemented as a normal KIDE Xtext language:

- `.krl` is registered in `product/languages.json`;
- the same runtime/IDE injector serves Eclipse desktop and the packaged LSP;
- the browser remains a language client and does not contain a KRL parser, validator,
  query engine, template engine or generator;
- valid and invalid KRL fixtures participate in the packaged golden-language corpus and
  full LSP parity qualification.

KRL models contain namespaces, facts, typed queries, templates and generation targets.
Generation target IDs are extensible, while parameter/binding value types remain closed and
typed: string, integer, decimal, boolean and IRI.

## Sandboxed query and template semantics

KRL generation is deliberately non-Turing-complete.

The query engine evaluates bounded triple patterns over:

1. the current versioned project knowledge snapshot; and
2. KRL-local facts.

Queries are deterministic: triples and result rows are canonically ordered, intermediate
result counts are bounded, selected variables must be bound, and query-backed generation
bindings must resolve to exactly one row. Ambiguous or empty bindings fail closed.

Templates support placeholder substitution only. They cannot execute Java, JavaScript,
shell commands, filesystem reads, processes, network requests, reflection or arbitrary
expressions. Missing or malformed placeholders fail generation.

## Versioned target SDK

`GenerationTarget` is the target plug-in contract. Each maintained target declares a
stable ID and version and converts one validated rendered template into a
`GeneratedArtifact`.

PR38 ships one maintained reference target:

- target ID: `java`;
- target version: `1`;
- media type: Java source;
- output is compile-qualified in packaged CI with `javac`.

Additional targets must register through `GenerationTargetRegistry`; they do not change
KRL parser semantics.

## Reproducibility and provenance

Every generation run produces a deterministic manifest containing:

- toolchain and manifest schema versions;
- source model ID, revision and ETag;
- knowledge revision and ETag;
- deterministic synthesis fingerprint;
- KRL model ID, revision and ETag;
- KRL knowledge-model name;
- every registered target version;
- generated artifact path, SHA-256, target ID/version, target name and template name; and
- one deterministic generation fingerprint.

Identical semantic inputs produce byte-identical generated artifacts and manifest content.

## Revision-safe enterprise service

`POST /api/v1/projects/{projectId}/generation` accepts only:

- source Activity model ID and current ETag;
- KRL model ID and current ETag; and
- the synthesis fingerprint previously returned by deterministic synthesis.

The server reruns synthesis and requires the supplied fingerprint to still match the current
source model and current knowledge revision. It verifies the KRL ETag before generation and
rechecks source, KRL and knowledge revisions after generation. Any stale input fails with a
conflict instead of returning stale generated evidence.

The endpoint returns generated artifact bytes as bounded base64 payloads plus SHA-256 and the
canonical provenance manifest. Generation is read-only: it does not rewrite Activity, KRL or
knowledge files.

## Filesystem containment

The standalone generation writer accepts an explicit staging root. Generated paths must be
portable relative paths, cannot traverse above the root or enter reserved `.kide` state,
and cannot cross or overwrite symbolic links. Duplicate output paths are rejected.

The enterprise API does not write generated artifacts into the project automatically; it
returns generated evidence to the client. This prevents generation from silently becoming
canonical engineering truth.

## Browser boundary

The browser generation workbench is a thin client. After successful synthesis it supplies
the current Activity ETag, a selected KRL model ETag and the synthesis fingerprint. It
renders the server-returned artifact list and manifest. No browser-side query/template or
generation semantics exist.

## Qualification

PR38 permanently gates:

- KRL valid/invalid packaged diagnostics;
- full KRL LSP completion, hover, definition, references, symbols, formatting and rename;
- secure WebSocket gateway KRL diagnostics;
- deterministic repeated semantic generation;
- ambiguous query refusal;
- malformed placeholder containment;
- path traversal and symlink escape rejection;
- invalid KRL refusal;
- packaged semantic generation from the built headless product;
- compilation of the maintained generated Java reference artifact;
- enterprise generation authorization, revision/fingerprint checks, repeatability and
  source non-mutation;
- browser generation API and Playwright flows; and
- all previous desktop, GLSP, knowledge, synthesis, reconfiguration, authorization and
  release gates.
