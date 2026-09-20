# KIDE Consolidated Enterprise Release Plan

Status: authoritative implementation sequence after merged PR26, 2026-09-20.

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
| **PR27** | PR26 HTTP runtime stabilization | Post-merge qualification correction | Declare the direct Jetty HTTP OSGi dependency required by the PR26 server; restore strict Tycho/package qualification without weakening compiler or CI gates |
| **PR28** | Web engineering workspace foundation | Former PR23 + PR25 | React/TypeScript shell, Monaco base, API client, project open/import/export, autosave/revision/conflict UX, browser build/E2E |
| **PR29** | Browser textual-language production parity | Former PR24 | Monaco language client, TextMate + semantic tokens, completion/hover/navigation/references/actions/format/rename using PR13 contracts |
| **PR30** | GLSP graphical modelling and parity | Former PR26 + PR27 + PR28 | One EMF-backed GLSP mapping supports read/edit/undo/validation and semantic parity with Sirius |
| **PR31** | Collaborative engineering and review | Former PR29 | Concurrent sessions, presence/comments, revision-aware conflict review, no silent overwrite |
| **PR32** | Knowledge fabric core | Former PR30 + PR31 | Versioned ontology/repository, embedded + SPARQL adapters, RDF/OWL/JSON-LD, SHACL, provenance and migration |
| **PR33** | Knowledge retrieval, catalogue and trace links | Former PR32 + PR33 | Deterministic retrieval/cache, desktop+web catalogue and stable knowledge-to-model provenance links |
| **PR34** | Deterministic synthesis, controller composition and design contracts | Former PR34 + PR35 + PR36 | Capability matching, Activity->MNC consolidation, controller composition, cross-model contracts/conflict diagnostics |
| **PR35** | Dynamic reconfiguration and thesis-scale qualification | Former PR37 + PR40 | Deterministic re-planning plus 10/50/100/500/1000-device regression/performance evidence |
| **PR36** | KRL and semantic code-generation toolchain | Former PR38 + PR39 | KRL grammar/LSP, versioned generator, target SDK, reproducible outputs and provenance manifests |
| **PR37** | Requirements, digital thread, baselines and evidence reports | Former PR41 + PR42 + PR47 | Requirement entities, immutable baselines, impact/trace graph and deterministic evidence-pack exports |
| **PR38** | Simulation and formal verification | Former PR43 + PR44 | Deterministic sandbox/record-replay and solver-neutral verification/counterexample mapping |
| **PR39** | Engineering interoperability, Git lifecycle and connector SDK | Former PR45 + PR46 + PR52 | ReqIF/SysML boundary, OSLC/external adapters, Git/PR traceability and one versioned extension SDK |
| **PR40** | Governed AI, policy, approvals and bounded agents | Former PR48 + PR49 + PR50 + PR51 | Provider-neutral model gateway, authorized context/provenance, policy-as-code, approvals and sandboxed tools |
| **PR41** | Security, sustainability and operational evidence | Former PR53 + PR54 + PR55 | Shared evidence/telemetry pipeline for SAST/SCA/SBOM, C3-ECO measurements, health/tracing/support bundles |
| **PR42** | Production shared-service deployment and native installers | Former PR56 + PR57 | HA/on-prem/air-gap containers/Helm, backup/restore and signed Windows/macOS/Linux installers/update path |
| **PR43** | Product UX, accessibility and onboarding | Former PR58 | Unified thesis-flow experience, keyboard/WCAG checks, first-run health and guided sample projects on desktop+web |
| **PR44** | Enterprise qualification and GA release | Former PR59 + PR60 | Soak/load/failover/upgrade/offline/browser/desktop matrix followed by signed downloadable release from immutable CI outputs |

## Why these PRs are clubbed

The grouping follows shared code boundaries rather than feature labels:

- PR28 changes the same web shell, project state and REST client needed for project/files/history.
- PR30 changes the same GLSP server/model mapping for read, edit and visual parity.
- PR32 changes the same ontology/repository schema for acquisition, validation and provenance.
- PR34 changes the same deterministic synthesis graph for matching, composition and contract validation.
- PR36 changes the same language-to-generator/toolchain boundary.
- PR37 changes the same trace/baseline data model used by requirements and reports.
- PR40 changes the same governed execution boundary for AI, policy, approvals and tools.
- PR41 changes the same evidence/telemetry plumbing for security, sustainability and operations.
- PR42 changes the same deployable runtime/package/update assets.
- PR44 uses one final qualification evidence set to gate the GA release.

## Current execution point

PR22 and stabilization PR23-PR25 are merged. PR26 is also merged, but its first
post-merge Tycho run exposed one strict OSGi dependency defect: the new HTTP
runtime directly consumes Jetty HTTP header/URI APIs without declaring
`org.eclipse.jetty.http` in its bundle manifest.

**PR27 is the active stabilization PR.** It contains only that dependency repair
plus this numbering correction. The full packaged build and PR26 HTTP self-check
must be green before PR28 begins the separate React/TypeScript web workspace.
