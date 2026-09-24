# KIDE Consolidated Enterprise Release Plan

Status: authoritative implementation sequence after GitHub PR44 allocation, 2026-09-24.

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
| **PR39** | Google Cloud Run hosted deployment | Deployment foundation inserted after PR38 | Production-oriented Cloud Run boundary with same-origin web/API/LSP/GLSP routing, persistent workspace storage, non-root runtime and deployment qualification |
| **PR40** | Cloud Build trigger logging/config repair | Deployment hardening | Bind triggers to the repository Cloud Build configuration and satisfy custom-service-account logging requirements |
| **PR41** | Cloud Build trigger-safe image naming | Deployment hardening | Self-contained, non-empty Artifact Registry image naming for trigger and manual builds |
| **PR42** | Tycho launcher branding path repair | Desktop packaging hardening | Resolve Windows/Linux/macOS launcher branding assets deterministically and enforce them in governance tests |
| **PR43** | Artifact Registry bootstrap for Cloud Build | Deployment hardening | Create/verify the Artifact Registry repository, apply least-privilege writer/logging roles and fail early on missing deployment prerequisites |
| **PR44** | Platform desktop launchers and standalone web deployer | Desktop/web execution separation | Explicit Windows/Linux/macOS desktop entry points plus an independently deployable frontend-only web image and deployer |
| **PR45** | Requirements, digital thread, baselines and evidence reports | Former planned PR39; former roadmap PR41 + PR42 + PR47 | Requirement entities, immutable baselines, impact/trace graph and deterministic evidence-pack exports |
| **PR46** | Simulation and formal verification | Former planned PR40; former roadmap PR43 + PR44 | Deterministic sandbox/record-replay and solver-neutral verification/counterexample mapping |
| **PR47** | Engineering interoperability, Git lifecycle and connector SDK | Former planned PR41; former roadmap PR45 + PR46 + PR52 | ReqIF/SysML boundary, OSLC/external adapters, Git/PR traceability and one versioned extension SDK |
| **PR48** | Governed AI, policy, approvals and bounded agents | Former planned PR42; former roadmap PR48 + PR49 + PR50 + PR51 | Provider-neutral model gateway, authorized context/provenance, policy-as-code, approvals and sandboxed tools |
| **PR49** | Security, sustainability and operational evidence | Former planned PR43; former roadmap PR53 + PR54 + PR55 | Shared evidence/telemetry pipeline for SAST/SCA/SBOM, C3-ECO measurements, health/tracing/support bundles |
| **PR50** | Production shared-service HA/on-prem deployment and native installers | Remaining scope from former planned PR44; former roadmap PR56 + PR57 | HA/on-prem/air-gap containers/Helm, backup/restore and signed Windows/macOS/Linux installers/update path, building on PR39 and PR44 deployment foundations |
| **PR51** | Product UX, accessibility and onboarding | Former planned PR45; former roadmap PR58 | Unified thesis-flow experience, keyboard/WCAG checks, first-run health and guided sample projects on desktop+web |
| **PR52** | Enterprise qualification and GA release | Former planned PR46; former roadmap PR59 + PR60 | Soak/load/failover/upgrade/offline/browser/desktop matrix followed by signed downloadable release from immutable CI outputs |

## Why these PRs are clubbed

The grouping follows shared code boundaries rather than feature labels:

- PR30 changes the same web shell, project state and REST client needed for project/files/history.
- PR32 changes the same GLSP server/model mapping for read, edit and visual parity.
- PR34 changes the same ontology/repository schema for acquisition, validation and provenance.
- PR36 changes the same deterministic synthesis graph for matching, composition and contract validation.
- PR38 changes the same language-to-generator/toolchain boundary.
- PR39-PR44 are allocated to the deployment, Cloud Build and launcher hardening work that was actually merged/opened under those GitHub IDs.
- PR45 changes the same trace/baseline data model used by requirements and reports.
- PR48 changes the same governed execution boundary for AI, policy, approvals and tools.
- PR49 changes the same evidence/telemetry plumbing for security, sustainability and operations.
- PR50 changes the remaining HA/on-prem/air-gap runtime, installer and update assets after the PR39/PR44 deployment foundations.
- PR52 uses one final qualification evidence set to gate the GA release.

## Current execution point

PR22 through PR43 are merged on `main`. GitHub PR39-PR43 were used for
deployment and packaging hardening rather than the older reserved roadmap scopes.

**PR44 is the active Draft PR: platform desktop launchers and standalone web deployer.**
It separates the desktop execution surface (`KIDE.exe`, `KIDE.sh`, `KIDE.command`)
from the independently deployable browser frontend while preserving the shared backend
services and qualification gates.

After PR44 merges, the remaining implementation sequence is reserved as:

- **PR45 — Requirements, digital thread, baselines and evidence reports**
- **PR46 — Simulation and formal verification**
- **PR47 — Engineering interoperability, Git lifecycle and connector SDK**
- **PR48 — Governed AI, policy, approvals and bounded agents**
- **PR49 — Security, sustainability and operational evidence**
- **PR50 — Production shared-service HA/on-prem deployment and native installers**
- **PR51 — Product UX, accessibility and onboarding**
- **PR52 — Enterprise qualification and GA release**

These IDs supersede the pre-deployment planned PR39-PR46 labels and must not be reused.
