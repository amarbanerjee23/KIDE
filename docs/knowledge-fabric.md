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

PR35 adds deterministic retrieval, catalogue UX and stable knowledge-to-model trace links
over this repository. Catalogue indexes are rebuilt only when the knowledge ETag changes;
desktop reads the shared Java service directly while the browser consumes the authenticated
enterprise API. Neither client duplicates RDF parsing or ontology meaning.

Trace links are persisted separately under `.kide/knowledge/traces.json`. They carry stable
UUID identity, server-derived knowledge/model revision evidence, source authority and
provenance. A model move or rename uses an explicit rebind that preserves the trace ID.
Concurrent trace mutation requires the current trace-store ETag, and impact validation
reports broken model targets, removed knowledge resources and stale knowledge bindings.

## Migration

`LegacyKnowledgeMigrator` is a fail-closed streaming XMI migration boundary for supported
research fixtures. It preserves the thesis concept classes Capability, Interface, Behavior,
Interaction, Workflow and Device with deterministic legacy URNs and provenance. The old
NeoEMF runtime is not a production dependency.
