# KIDE knowledge fabric core

PR34 replaces the release dependency on the obsolete NeoEMF research plug-in with the
versioned `com.kide.knowledge` boundary. The legacy plug-in remains source material only
and is not reintroduced into the Tycho reactor.

## Canonical contract

Knowledge is represented as deterministic RDF triples plus explicit dataset metadata:
schema version, dataset identity, organization/project scope and import provenance.
The embedded repository persists that state under `.kide/knowledge`; engineering DSL
files remain canonical project truth and knowledge metadata never replaces them.

The repository uses SHA-256 ETags and optimistic replacement. Stale writers fail rather
than merging or overwriting a newer graph. State writes are bounded, atomic and reject
symbolic-link storage boundaries.

## Standards-facing interchange

The core supports a deliberately bounded, deterministic profile of:

- Turtle/N-Triples-compatible absolute-IRI statements;
- RDF/XML `rdf:Description` graphs;
- expanded JSON-LD `@graph` objects;
- OWL vocabulary as ordinary RDF/OWL IRIs;
- SHACL Core `NodeShape` property constraints for `targetClass`, `path`,
  `minCount` and `maxCount`.

The built-in KIDE shapes require one `rdfs:label` for Capability, Interface, Behavior,
Interaction, Workflow and Device nodes. Imports are validated before repository commit.

## Enterprise adapter

`SparqlEndpointAdapter` provides the external SPARQL 1.1 HTTP boundary. Remote endpoints
must use HTTPS; insecure HTTP is accepted only for loopback development. Bearer material is
supplied at call time and is not persisted by the adapter. Endpoint failures are bounded
and sanitized as knowledge-repository errors.

PR35 builds deterministic retrieval, catalogue UX and stable knowledge-to-model trace links
over this repository. It must not duplicate RDF or ontology meaning in browser code.

## Migration

`LegacyKnowledgeMigrator` is a fail-closed streaming XMI migration boundary for supported
research fixtures. It preserves the thesis concept classes Capability, Interface, Behavior,
Interaction, Workflow and Device with deterministic legacy URNs and provenance. The old
NeoEMF runtime is not a production dependency.
