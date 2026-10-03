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

## Next qualification layer

The next parity gate should execute the same golden project through:

1. the shared Java service directly;
2. the Eclipse adapter;
3. the HTTP/LSP/GLSP adapter as applicable;

and compare normalized semantic outputs such as diagnostics, selections,
fingerprints, generated artifacts and trace IDs.
