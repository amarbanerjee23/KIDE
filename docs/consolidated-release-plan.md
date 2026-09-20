# KIDE Consolidated Enterprise Release Plan

Status: authoritative implementation sequence after merged PR28, 2026-09-20.

This plan supersedes the old one-feature-per-PR numbering in
`world-class-systems-engineering-roadmap.md`. The engineering outcomes remain
unchanged; only code-cohesive work is grouped so one subsystem is changed,
qualified and documented once.

## Permanent rules

- Eclipse desktop and the web product are separate clients of one semantic core.
- Web code must not duplicate KIDE parsers, validators, scoping, synthesis or model meaning.
- Existing project/schema compatibility, stable enterprise IDs and packaged desktop/LSP tests remain permanent gates.
- A runtime feature is delivered together with its packaged smoke/qualification path; stabilization is not intentionally deferred to a follow-up PR.
- Mandatory authentication, authorization, policy and security checks fail closed.
- No expected component may silently skip CI. Ubuntu/server deployment and Kubernetes qualification become mandatory when the shared-service deployment is introduced.
- Project files remain canonical and Git-friendly; shared services add revisions/indexes without becoming the only copy of engineering truth.

## Re-evaluated PR sequence

| PR | Consolidated scope | Clubbed former roadmap work | Merge gate |
| --- | --- | --- | --- |
| **PR26** | Shared enterprise HTTP service runtime | Missing prerequisite between former PR20-22 and web work | Secure `/api/v1` runtime, shared OIDC introspection, RBAC, audit, revision-safe model access, structured unavailable states, packaged headless self-check |
| **PR27** | Post-merge runtime stabilization | PR26 HTTP OSGi correction + PR25 LSP rename regression | Declare the direct Jetty HTTP dependency and restore rename at the per-language resource-service boundary; remove the invalid server-global rename binding; restore strict packaged qualification without weakening CI gates |
| **PR28** | Post-merge gateway stabilization | PR27 packaged WebSocket race correction | Keep embedded LSP shutdown/exit session-local so valid client exit cannot terminate the shared gateway JVM; deterministic stage evidence; restore gateway qualification |
| **PR29** | Rename registry stabilization | PR27 incomplete rename correction | Republish final IDE-aware providers after all generated setups finish so transitive standalone registration cannot downgrade the global Xtext registry; share one process-wide registry provider; require rename in local and global providers |
| **PR30** | Web engineering workspace foundation | Former PR23 + PR25 | React/TypeScript shell, Monaco base, API client, project open/import/export, autosave/revision/conflict UX, browser build/E2E |
| **PR31** | Browser textual-language production parity | Former PR24 | Monaco language client, TextMate + semantic tokens, completion/hover/navigation/references/actions/format/rename using PR13 contracts |
| **PR32** | GLSP graphical modelling and parity | Former PR26 + PR27 + PR28 | One EMF-backed GLSP mapping supports read/edit/undo/validation and semantic parity with Sirius |
| **PR33** | Collaborative engineering and review | Former PR29 | Concurrent sessions, presence/comments, revision-aware conflict review, no silent overwrite |
| **PR34** | Knowledge fabric core | Former PR30 + PR31 | Versioned ontology/repository, embedded + SPARQL adapters, RDF/OWL/JSON-LD, SHACL, provenance and migration |
| **PR35** | Knowledge retrieval, catalogue and trace links | Former PR32 + PR33 | Deterministic retrieval/cache, desktop+web catalogue and stable knowledge-to-model provenance links |
| **PR36** | Deterministic synthesis, controller composition and design contracts | Former PR34 + PR35 + PR36 | Capability matching, Activity->MNC consolidation, controller composition, cross-model contracts/conflict diagnostics |
| **PR37** | Dynamic reconfiguration and thesis-scale qualification | Former PR37 + PR40 | Deterministic re-planning plus 10/50/100/500/1000-device regression/performance evidence |
| **PR38** | KRL and semantic code-generation toolchain | Former PR38 + PR39 | KRL grammar/LSP, versioned generator, target SDK, reproducible outputs and provenance manifests |
| **PR39** | Requirements, digital thread, baselines and evidence reports | Former PR41 + PR42 + PR47 | Requirement entities, immutable baselines, impact/trace graph and deterministic evidence-pack exports |
| **PR40** | Simulation and formal verification | Former PR43 + PR44 | Deterministic sandbox/record-replay and solver-neutral verification/counterexample mapping |
| **PR41** | Engineering interoperability, Git lifecycle and connector SDK | Former PR45 + PR46 + PR52 | ReqIF/SysML boundary, OSLC/external adapters, Git/PR traceability and one versioned extension SDK |
| **PR42** | Governed AI, policy, approvals and bounded agents | Former PR48 + PR49 + PR50 + PR51 | Provider-neutral model gateway, authorized context/provenance, policy-as-code, approvals and sandboxed tools |
| **PR43** | Security, sustainability and operational evidence | Former PR53 + PR54 + PR55 | Shared evidence/telemetry pipeline for SAST/SCA/SBOM, C3-ECO measurements, health/tracing/support bundles |
| **PR44** | Production shared-service deployment and native installers | Former PR56 + PR57 | HA/on-prem/air-gap containers/Helm, backup/restore and signed Windows/macOS/Linux installers/update path |
| **PR45** | Product UX, accessibility and onboarding | Former PR58 | Unified thesis-flow experience, keyboard/WCAG checks, first-run health and guided sample projects on desktop+web |
| **PR46** | Enterprise qualification and GA release | Former PR59 + PR60 | Soak/load/failover/upgrade/offline/browser/desktop matrix followed by signed downloadable release from immutable CI outputs |

## Why these PRs are clubbed

The grouping follows shared code boundaries rather than feature labels:

- PR30 changes the same web shell, project state and REST client needed for project/files/history.
- PR32 changes the same GLSP server/model mapping for read, edit and visual parity.
- PR34 changes the same ontology/repository schema for acquisition, validation and provenance.
- PR36 changes the same deterministic synthesis graph for matching, composition and contract validation.
- PR38 changes the same language-to-generator/toolchain boundary.
- PR39 changes the same trace/baseline data model used by requirements and reports.
- PR42 changes the same governed execution boundary for AI, policy, approvals and tools.
- PR43 changes the same evidence/telemetry plumbing for security, sustainability and operations.
- PR44 changes the same deployable runtime/package/update assets.
- PR46 uses one final qualification evidence set to gate the GA release.

## Current execution point

PR22 through PR29 are merged. PR29 restored deterministic Xtext rename and symbol
parity across the packaged five-language LSP while preserving the desktop product.

**PR30 is the active implementation PR.** It creates a separate React/TypeScript
browser workspace over the existing enterprise HTTP boundary, adds a locally
bundled Monaco editor foundation, bounded canonical-project ZIP import/export,
revision-aware autosave/conflict handling, and browser build/unit/E2E gates.

PR30 does not duplicate KIDE language semantics in TypeScript. Browser textual
language intelligence remains owned by the existing Xtext LSP and is connected in
PR31; graphical editing remains owned by the EMF/GLSP work in PR32.
