# KIDE knowledge catalogue and trace links

PR35 promotes the PR34 knowledge fabric into deterministic engineering retrieval and
traceability surfaces without introducing a second semantic implementation in either
client.

## Deterministic catalogue

`KnowledgeCatalogueService` derives its searchable catalogue from the current
`KnowledgeSnapshot`. The index is keyed by the repository ETag: repeated queries against
the same graph reuse the cache, while any committed knowledge revision invalidates it.

Queries use server-owned token matching and optional concept-type filtering. Results are
deterministically ordered by label and IRI and include types, properties, source authority
and provenance. Capability, Device, Workflow, Interface, Behavior and Interaction filters
resolve to the canonical PR34 vocabulary in Java.

The browser sends only query text/type/limit and renders the returned contract. The Eclipse
Knowledge Catalogue view uses the same Java catalogue service against the selected project.
Neither UI parses RDF, evaluates SHACL, or recreates ontology meaning.

## Stable knowledge-to-model traces

Trace state is project metadata under `.kide/knowledge/traces.json`; canonical DSL source
files remain engineering truth. Each link records:

- a stable UUID;
- the knowledge IRI and typed relation;
- project-relative model path and optional semantic fragment;
- source authority and provenance;
- creator/updater identity and timestamps;
- the knowledge graph ETag and model ETag observed when the link was bound.

Creation validates the knowledge resource and current model on the server. Clients cannot
supply the knowledge or model ETag. Model moves/renames use explicit rebind and retain the
same trace UUID.

Trace-store writes are atomic, bounded and optimistic. Create, rebind and delete require the
current trace-store ETag; stale operations fail with the normal HTTP 409 conflict boundary
instead of silently overwriting another user's trace changes.

## Impact and integrity

Impact queries can start from a knowledge IRI or model path. Validation reports:

- `BROKEN_KNOWLEDGE` when the referenced knowledge resource no longer exists;
- `BROKEN_MODEL` when the project model no longer exists;
- `STALE_MODEL` when the model still exists but its current ETag differs from the bound ETag;
- `STALE_KNOWLEDGE` when the knowledge graph has changed since the trace was bound.

These findings are evidence only; validation does not mutate or auto-merge engineering
content.

## Authorization

`KNOWLEDGE_READ` is granted to Viewer, Reviewer, Engineer and Service Operator roles.
`KNOWLEDGE_TRACE_WRITE` is limited to Engineer, Service Operator and Administrator roles.
All routes still pass through the existing authenticated project API boundary.

## Qualification

Permanent qualification covers deterministic query order, cache reuse/invalidation, stable
trace identity across rebinds, stale trace-store conflict rejection, broken/stale link
detection, role separation, browser contract/E2E behavior, the Eclipse catalogue bundle,
packaged knowledge self-checks and packaged enterprise API checks.
