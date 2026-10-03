# Shared Eclipse/Web service kernel

KIDE has one engineering implementation and two product adapters:

- the downloadable Eclipse product;
- the hosted/headless runtime used by Web.

The adapters may differ in presentation and transport, but they must not implement
separate engineering semantics.

## Kernel ownership

The canonical shared kernel is declared in
`product/shared-service-kernel.json`.

| Concern | Shared owner | Desktop adapter | Web/backend adapter |
| --- | --- | --- | --- |
| API contract | `com.kide.enterprise.api` | Eclipse callers use the same DTO/contract semantics | HTTP `/api/v1` |
| Model persistence | `com.kide.enterprise.modelrepo` | workspace/project integration | HTTP/GLSP adapters |
| Knowledge catalogue/traces | `com.kide.knowledge` | Knowledge Catalogue view | HTTP knowledge endpoints |
| Synthesis/reconfiguration | `com.kide.synthesis` | Eclipse engineering actions | HTTP synthesis/reconfiguration endpoints |
| Semantic generation | `com.kide.codegen` | Eclipse generation actions | HTTP generation endpoint |
| Text language semantics | Xtext runtime + `.ide` bundles | Eclipse editor adapters | LSP/Monaco |
| Graphical semantics | EMF/domain services | Sirius/Eclipse diagrams | GLSP |

## Adapter rule

UI and transport bundles are adapters only.

They may:

- authenticate and authorize;
- map protocol requests to shared service calls;
- render results;
- translate shared exceptions into UI or protocol errors;
- emit audit events.

They must not:

- instantiate synthesis engines directly;
- instantiate code-generation engines directly;
- recreate knowledge catalogue semantics;
- parse or interpret KIDE DSLs independently;
- maintain separate browser-only engineering rules.

Project-level orchestration therefore lives with the owning semantic bundle:

- `com.kide.synthesis.ProjectSynthesisService`
- `com.kide.codegen.ProjectGenerationService`
- `com.kide.knowledge.ProjectKnowledgeService`

`com.kide.enterprise.server` depends on these services and exposes them over HTTP.

## Product synchronization

Both `releng/com.kide.feature` and
`releng/com.kide.languageserver.feature` must explicitly ship every bundle in
`product/shared-service-kernel.json`.

`scripts/verify_shared_service_kernel.py` enforces this in CI and rejects
server-owned copies of the shared project services or direct construction of
the underlying semantic engines.

This makes the downloadable Eclipse product and hosted backend derive their
engineering behavior from the same commit, bundles, and service implementations.

## Cross-adapter semantic qualification

PR80 makes the next parity layer executable and fail-closed.

The packaged enterprise self-check now exercises the same canonical project through
the shared project services directly and through the authenticated HTTP adapter,
then compares normalized engineering semantics rather than presentation:

- knowledge revision/ETag and catalogue identity;
- synthesis status, selections, diagnostics, fingerprint and generated MNC;
- generation fingerprint, manifest structure and artifact path/hash/target/content;
- reconfiguration status, selections and state-migration policy/resources.

Transport-only values such as request IDs, timestamps, HTTP headers, JSON object
ordering and UI wording are explicitly excluded from parity.

The contract is declared in
`product/cross-adapter-semantic-qualification.json` and verified by
`scripts/verify_cross_adapter_semantics.py`.

The same PR80 contract also requires the existing adapter-specific parity gates to
remain active:

1. Eclipse Xtext services versus packaged Web LSP
   (`verify_lsp_parity_matrix.py` + `qualify_lsp_parity.py`);
2. Eclipse Sirius semantics versus Web GLSP
   (`verify_diagram_parity.py` + packaged GLSP smoke qualification).

A change that removes any of these gates, bypasses the shared project services, or
changes a normalized semantic result on only one adapter fails CI.
