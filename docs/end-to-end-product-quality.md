# End-to-end product quality journey

PR84 establishes a permanent product-level qualification contract for the hosted engineering flow.

The golden journey proves that an authenticated engineer can open a project, load the shared semantic model, run deterministic synthesis, and generate a concrete target artifact. The browser accepts generated output only after checking request provenance, the generation manifest, safe artifact paths, SHA-256 evidence, and the actual decoded bytes.

CI must retain all of these independent layers:

- repository governance checks;
- packaged deterministic synthesis;
- packaged semantic code generation, including manifest/hash validation and Java compilation;
- generated Web API transport verification against packaged OpenAPI;
- Web unit tests for generation-integrity failure modes;
- Playwright golden engineering journey;
- the PR84 repository verifier, which fails closed if any mandatory layer is removed or skipped.

A green UI response alone is not sufficient evidence that code generation works. Qualified output must have internally consistent model/KRL/synthesis provenance and artifact bytes matching their declared SHA-256 evidence.
