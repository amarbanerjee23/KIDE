# KIDE World-Class Systems Engineering Product Roadmap

Status: implementation plan after merged PR20 (versioned service/API boundary), 2026-09-19.

This document is the authoritative implementation plan for evolving KIDE from a research-backed Eclipse modelling product into a world-class, enterprise-grade systems engineering platform with first-class desktop and web clients.

The plan deliberately preserves the research thesis rather than replacing it with a generic IDE. KIDE remains a knowledge-driven control/system engineering environment whose differentiator is the transformation of domain knowledge and high-level intent into validated, traceable, synthesized and executable engineering artefacts.

## 1. Product thesis and non-negotiable engineering flow

The product must preserve the thesis pipeline:

```text
Human objective / requirement
        |
        v
Knowledge acquisition + Capability Ontology / knowledge graph
        |
        v
Knowledge retrieval + provenance
        |
        v
Data / interface / operation / capability modelling
(DML + M&CML + Operation + Capability DSL)
        |
        v
Activity / supervisory workflow modelling
        |
        v
Deterministic capability matching + controller/design synthesis
        |
        v
Validation + verification + visualization
        |
        v
Semantic code generation / KRL target mapping
        |
        v
Simulation / orchestration / deployment / dynamic reconfiguration
        |
        v
Evidence, traceability, versioning and engineering feedback
```

The KDA layers from the thesis remain visible in the product architecture:

1. knowledge storage and acquisition;
2. knowledge retrieval;
3. knowledge usage;
4. knowledge representation.

No future UI, AI feature, cloud service or editor may bypass the deterministic language, model, knowledge, synthesis or validation layers. AI may propose changes, but deterministic KIDE services validate them before they become engineering artefacts.

## 2. Current product status

### Strong foundations already delivered

- Standalone Eclipse products are built for Windows x64, Linux x64, macOS x64 and macOS ARM64 with an embedded runtime.
- Release engineering already contains immutable GitHub Action pins, signing/notarisation controls, checksums, SBOM and provenance generation.
- Enterprise configuration supports deterministic layering and secret references; secrets are not stored as project literals.
- Stable organization -> portfolio -> project -> workspace identities are implemented with failure-safe persistence and packaged-runtime qualification.
- The five production Xtext language layers have a shared semantic contract and are prevented from depending on SWT/JFace/Eclipse UI.
- A packaged headless multi-DSL LSP product already registers DML, Capability, MNC, Operation and Activity from the existing generated Xtext IDE setups.
- CI already proves all five languages produce diagnostics through the packaged language server and can shut down cleanly.
- Sirius desktop diagrams and the Activity -> MNC transformation provide an existing graphical/transformation foundation.
- Onboarding, templates, validation, quick fixes and guided tours exist in the Eclipse product.

### Material gaps before KIDE can be called a world-class systems engineering platform

- The current target still pins Xtext 2.25. Xtext 2.44 removed the legacy Xtext-Web/GWT path, so future browser editing must use LSP rather than old Xtext Web APIs. A guarded modernization to current Xtext/Eclipse/Java is required.
- There is no first-class web product on `main`; web must become another client of the same semantics, not a separate implementation.
- Desktop diagrams are Sirius-specific; browser graphical modelling needs a modern protocol-based client without duplicating the EMF domain model.
- The research knowledge plug-in is excluded from the release because of obsolete NeoEMF dependencies. The thesis Capability Ontology/knowledge-graph path therefore is not yet a production-supported subsystem.
- KRL/semantic code generation is not yet a release-qualified first-class language/toolchain.
- The repository contains an Activity -> MNC transformation, but the full ontology-driven capability matching, controller synthesis, reconfiguration and thesis-scale qualification need to be promoted into supported product services.
- Requirements management, end-to-end traceability, baselines, change impact, formal verification, simulation and engineering evidence are not yet integrated product capabilities.
- Enterprise principal identity, authorization, audit, policy, shared services, HA, backup/restore and operational observability remain to be implemented.
- There is currently no published GitHub Release. Buildable archives are not enough: users need signed, tested, downloadable installers/packages and a browser deployment that require no source checkout or local build.
- Documentation has drifted from reality in places; product capability declarations must become test-derived so documentation cannot claim missing features or omit shipped ones.

## 3. Target architecture

### 3.1 One semantic core, multiple clients

```text
                             +---------------------------+
                             | Shared model repository   |
                             | revision/transactions     |
                             +-------------+-------------+
                                           |
          +--------------------------------+--------------------------------+
          |                                |                                |
+---------v----------+          +----------v---------+          +-----------v-----------+
| Xtext runtime/IDE  |          | Knowledge services |          | Synthesis/verification |
| shared semantics   |          | ontology/query     |          | deterministic engines  |
+---------+----------+          +----------+---------+          +-----------+-----------+
          |                                |                                |
          +--------------------+-----------+--------------------------------+
                               |
             +-----------------+------------------+
             |                                    |
   +---------v---------+                 +--------v---------+
   | LSP service       |                 | GLSP service     |
   | text semantics    |                 | diagrams/models  |
   +---------+---------+                 +--------+---------+
             |                                    |
      +------+-------+                     +------+-------+
      |              |                     |              |
+-----v-----+  +-----v------+        +-----v-----+  +-----v------+
| Eclipse   |  | Monaco Web |        | Eclipse   |  | Web GLSP   |
| desktop   |  | client     |        | client    |  | client     |
+-----------+  +------------+        +-----------+  +------------+
```

Rules:

- The `*.dsl` and `*.dsl.ide` bundles remain the single implementation of textual language meaning.
- Eclipse `*.ui` and Monaco are adapters/clients only.
- LSP is the supported browser text-language boundary. No new Xtext-Web/GWT implementation is allowed.
- Browser transport uses authenticated WebSocket/JSON-RPC to the Java/Xtext language server. The Java server stays server-side; do not rewrite the DSLs in TypeScript just to run them in a browser worker.
- Monaco uses `monaco-languageclient`/VS Code-compatible services, TextMate grammars for lexical highlighting, and LSP semantic tokens for semantic highlighting.
- Graphical editing uses a protocol/server boundary. The recommended direction is Eclipse GLSP with EMF source models because it can support web, Eclipse, VS Code/Theia integrations while keeping graphical language logic server-side. Existing Sirius diagrams remain supported until GLSP parity is proven.
- Project files remain portable and Git-friendly. A shared server may index/cache them, but desktop/offline use must not require a cloud database.
- Knowledge graph state carries provenance and can be regenerated/imported from versioned project knowledge artefacts; hidden server-only state must not become the only copy of engineering truth.

### 3.2 Web product shape

The primary web client should be a KIDE-branded React/TypeScript application using Monaco directly rather than forcing users into a generic web IDE shell. The architecture must nevertheless keep the editor integration thin enough that a future Theia or VS Code client can consume the same LSP/GLSP/services.

The web workspace must expose the engineering flow, not a folder-first developer UI:

```text
1 Requirements / Objectives
2 Knowledge & Data
3 Capabilities & Operations
4 Supervisory Workflow
5 Synthesis
6 Verification & Simulation
7 Generate / Deploy
8 Traceability & Evidence
```

Code/text, diagrams, properties, problems, traceability, simulation and generated outputs are views of one engineering project, not disconnected tools.

## 4. Product-wide non-breaking rules

Every implementation PR after this roadmap must satisfy these rules.

1. Existing valid KIDE projects must continue to open. Any format change requires an explicit version and tested migration.
2. Stable E04 organization/portfolio/project/workspace IDs must never be regenerated because of a rename, path move or client change.
3. A desktop project edited through web must remain loadable by desktop once both clients advertise the same model/schema compatibility level.
4. No parser, linker, scoper, validator, formatter, completion engine, rename/refactoring implementation or synthesis algorithm may be duplicated for web.
5. No feature may introduce an eager workbench activator that can crash startup. Remote services must degrade to typed unavailable states.
6. Expected user/model validation errors are diagnostics, not uncaught exceptions.
7. CI must reject uncaught exceptions, `ERROR`/`SEVERE` startup failures, non-zero smoke-test exits and unresolved OSGi bundles in qualified product flows.
8. Shared services use versioned contracts and structured error codes; clients never parse server exception strings.
9. Mandatory security/policy checks fail closed. Optional remote capabilities fail safely without corrupting local projects.
10. Project data changes are transactional or recoverable. Autosave never silently overwrites a newer revision.
11. The product must be usable with keyboard-only navigation and must not rely on colour alone to communicate semantic state.
12. Every new feature requires user documentation, administrator documentation where relevant, migration notes and automated evidence in CI.

## 5. CI/CD constitution

### 5.1 Permanent gates on every PR

These gates are cumulative; later PRs may add gates but must not weaken earlier ones.

- repository governance tests;
- immutable GitHub Action SHA enforcement;
- shared Xtext layering enforcement;
- full Maven/Tycho reactor build;
- all DSL unit tests and golden semantic fixtures;
- E03 configuration/secrets tests;
- E04 context failure-path tests;
- packaged desktop runtime self-check;
- Windows/Linux/macOS desktop archive/product qualification;
- packaged five-DSL LSP smoke test;
- model backward-compatibility/migration fixtures once introduced;
- dependency/SBOM/security checks once introduced;
- web typecheck/unit/build once the web client exists;
- web browser E2E once the web shell exists;
- GLSP/model parity once graphical web editing exists;
- installer clean-machine tests once native installers exist.

A component that is expected to exist must never silently skip CI because its directory disappeared. Detection jobs may skip only explicitly optional components.

### 5.2 Runtime-safety gate

Each packaged client/service gets a smoke flow that:

- launches from packaged bytes rather than Maven/IDE classpaths;
- exercises a valid project and representative invalid input;
- verifies expected diagnostics;
- performs clean shutdown;
- scans application logs for uncaught exceptions, OSGi resolution errors and unexpected `ERROR`/`SEVERE` records;
- verifies no project identity, descriptor or model corruption after restart.

### 5.3 Semantic golden corpus

Maintain versioned fixtures for all production languages and thesis use cases. Golden evidence includes:

- parse/link/validation results;
- completion/hover/navigation/rename/code-action expectations;
- serialized model identity;
- Activity -> MNC transformation outputs;
- synthesis plans;
- generated-code snapshots where deterministic;
- traceability links;
- expected diagrams/GModel semantic structure;
- migration from every still-supported project format.

## 6. Detailed implementation PR plan

PR11 is this planning PR. After it merges, implement PR12-PR60 in order unless a dependency is explicitly shown as parallel-safe. A PR is not merge-ready until its listed tests are part of CI and green.

### Phase A — Freeze semantics, then modernize safely

| PR | Scope | Required implementation | PR-specific tests / CI acceptance |
| --- | --- | --- | --- |
| **PR12** | Product truth + golden engineering corpus | Build machine-readable registry of the five DSLs/extensions/features; add representative valid/invalid thesis fixtures; make docs derive/check against the registry; correct stale README/install claims. | Five-DSL parse/link/validate fixtures; Activity->MNC snapshot; packaged desktop + LSP smoke; CI fails if documented language set differs from registered/shipped set. |
| **PR13** | Full LSP feature-parity harness | Define client-neutral capability matrix for diagnostics, completion/snippets, hover, definition/declaration/type/implementation where applicable, references, symbols, code actions, formatting, rename, folding and semantic tokens; capabilities absent from the legacy packaged server are explicitly recorded as deferred and promoted only when later platform work makes them real. Compare LSP behavior with shared IDE services rather than UI screenshots. | `AbstractLanguageServerTest`-style tests for every applicable feature x every DSL; no UI dependencies in shared layers; current five-DSL protocol smoke retained. |
| **PR14** | Model/schema versioning and migration | Add explicit project/model schema compatibility metadata, migration service, backup-before-migrate, dry-run/report API and forward-version rejection. | Fixtures from pre-PR14 projects open unchanged; migration round-trip/idempotence; corrupt/unknown versions return diagnostics, never crash; stable IDs unchanged. |
| **PR15** | Xtext modernization preparation | Regenerate/clean language artefacts so legacy Log4j/Xtext-2.25 UI coupling is removed; make generation reproducible; separate generated and hand-written semantic code. Do not change platform version yet. | Golden corpus identical; generated-code reproducibility; no Log4j-1-only dependency; full desktop/LSP products green. |
| **PR16** | Current platform upgrade | Move to Java 21, Tycho 5, Eclipse 2025-12-or-newer compatible stream, Xtext 2.44.x and a compatible pinned Sirius release. Remove all old Xtext-Web/GWT assumptions. | Every PR12-15 semantic/parity/migration test unchanged; four desktop products + LSP package launch; OSGi resolution scan; clean workspace and migrated workspace startup. |

### Phase B — Enterprise identity, authorization and shared model contracts

| PR | Scope | Required implementation | PR-specific tests / CI acceptance |
| --- | --- | --- | --- |
| **PR17** | Principal identity / authentication | OIDC-ready principal model, PKCE/device-flow-capable desktop adapter, browser auth adapter, service-account abstraction, local/offline principal. SAML is handled through an OIDC/SAML enterprise IdP boundary rather than custom SAML parsing in clients. | Token expiry/refresh/logout; offline mode; malformed/expired token fail closed; no token/secret in logs or project files; mocked IdP integration tests. |
| **PR18** | Least-privilege authorization | RBAC permissions over E04 org/portfolio/project/workspace IDs; centralized authorization service; server-side enforcement plus UI affordance hiding; deny-by-default. | Role matrix, inheritance, cross-project isolation, path/name spoofing, unauthorized REST/WebSocket/LSP workspace access, privilege-change tests. |
| **PR19** | Audit and engineering event model | Append-oriented actor/action/resource/outcome events, correlation/causation IDs, project revision, client identity, immutable event schema and redaction rules. | Schema compatibility, redaction/no-secret tests, tamper-chain/hash verification where used, concurrent ordering/correlation, failure-event tests. |
| **PR20** | Versioned service/API boundary | Define `/api/v1` contracts for projects, models, knowledge, synthesis, evidence and health; typed error envelope; request IDs; OpenAPI generation; contract compatibility policy. | OpenAPI schema validation, consumer contract tests, unknown-field compatibility, structured-error tests, no raw stack traces to clients. |
| **PR21** | Model repository + revision transactions | Introduce a shared model repository abstraction with revision/ETag, transactional reads/writes, optimistic concurrency, file-backed local implementation and server implementation. Keep project files canonical. | Lost-update tests, crash/restart recovery, transaction rollback, stale revision conflict, desktop/web round-trip, symlink/path traversal and large-file bounds. |

### Phase C — First-class web product using LSP + Monaco + GLSP

| PR | Scope | Required implementation | PR-specific tests / CI acceptance |
| --- | --- | --- | --- |
| **PR22** | Secure LSP WebSocket gateway | Adapt the existing packaged Java/Xtext LSP stdio process to authenticated WebSocket sessions; one bounded workspace/session context; lifecycle, cancellation, quotas, idle timeout, TLS/reverse-proxy awareness. | WebSocket initialize/open/change/diagnostics/shutdown for all five DSLs; auth denial; reconnect; cancellation; oversized message/rate-limit tests; no orphan LS processes. |
| **PR23** | KIDE web shell + Monaco | Add supported React/TypeScript web app with Monaco and `monaco-languageclient`; engineering-stage navigation, project explorer, editor tabs, Problems/Outline/Properties panes and responsive layout. | TypeScript strict check, unit tests, production build, Playwright Chromium/Firefox/WebKit launch, keyboard navigation baseline, browser console error scan. |
| **PR24** | Monaco/LSP production parity | TextMate lexical grammars plus semantic tokens; snippets, hover, navigation, references, symbols, code actions, formatting and rename wired to PR13 contracts. | Run the PR13 semantic corpus through browser LSP; screenshot only for visual regressions, semantic assertions for correctness; no custom duplicate validator/completion logic in TypeScript. |
| **PR25** | Web workspace/files/history | Project create/open/import/export, autosave, dirty state, revisions, undo/redo boundaries, recovery after dropped connection, browser download/upload without source checkout. | Autosave race tests, stale-revision conflict UX, reconnect/recovery, import malicious archive/path traversal tests, desktop-web-desktop round-trip. |
| **PR26** | GLSP read-only graphical views | Introduce Java GLSP server adapters over the existing EMF source models for Activity/MNC representations. Keep Sirius desktop unchanged. | Same source model produces expected semantic node/edge GModel; large-model render smoke; no mutation in read-only mode; desktop Sirius still green. |
| **PR27** | GLSP graphical editing | Add create/delete/connect/label/property operations, validation markers, undo/redo, layout and navigation; edits commit through PR21 model transactions. | Operation-to-source-model round trips; invalid edit rejection; concurrent revision conflicts; undo/redo; reopen in Eclipse/Sirius after web edits. |
| **PR28** | Visual parity and shared graphical semantics | Extract client-neutral diagram mapping/style rules where feasible; maintain one concept palette; prove Sirius and GLSP represent equivalent engineering elements/relationships. | Model-to-representation semantic parity fixtures; palette/accessibility checks; navigation source<->diagram; no model semantic divergence. |
| **PR29** | Collaborative web engineering | Presence, project sessions, optimistic concurrency/conflict review, comments/annotations and review-ready change sets. Do not implement unsafe character-level merging for semantic model files without model-aware conflict handling. | Two-user concurrent edit scenarios, conflict detection/resolution, disconnect/rejoin, authorization, audit coverage, no silent overwrite. |

### Phase D — Promote the thesis knowledge/synthesis chain into production

| PR | Scope | Required implementation | PR-specific tests / CI acceptance |
| --- | --- | --- | --- |
| **PR30** | Capability Ontology v1 + knowledge repository | Replace release dependency on obsolete NeoEMF with a versioned `KnowledgeRepository` abstraction. Provide an embedded RDF/OWL store for standalone/offline use and an external SPARQL endpoint adapter for enterprise deployment. Preserve thesis capability/interface/behavior/interaction/workflow concepts. | Ontology schema fixtures, offline persistence, restart, import/export, malformed ontology isolation, backward migration from supported research fixtures. |
| **PR31** | Knowledge acquisition, evolution and provenance | RDF/OWL/JSON-LD import/export, provenance/source authority, versioned ontology changes, SHACL validation, controlled vocabulary and project/org knowledge scopes. | SHACL positive/negative corpus, provenance retention, ontology version migration, duplicate/merge conflict tests, cross-project isolation. |
| **PR32** | Knowledge retrieval + capability catalogue | SPARQL/query service, indexes/cache, capability/device search, dependency/role/property views and desktop/web catalogue UI. | Thesis SPARQL-equivalent query fixtures, cache invalidation, deterministic query results, permissions, 80/500/1000-device datasets. |
| **PR33** | Knowledge-to-model trace links | Connect requirements/objectives, retrieved knowledge, selected devices/capabilities, DSL declarations and activities through stable typed links with source authority. | Broken-link detection, rename/move stability, provenance chain assertions, impact-query tests. |
| **PR34** | Deterministic resource-capability synthesis | Productionize the capability matching algorithm as a client-neutral service: objective/workflow -> required capabilities -> candidate resources -> ranked/justified feasible composition. Ranking must be deterministic from declared criteria, not opaque AI. | Thesis boom barrier/robot/smart-room fixtures; no-solution and ambiguous-solution explanations; deterministic repeatability; scale corpus. |
| **PR35** | Controller composition + transformation consolidation | Implement higher-order controller/state-machine composition and consolidate the existing Activity->MNC transformation behind a versioned synthesis service. | Existing transformation snapshots unchanged unless versioned migration approved; state/transition invariants; invalid workflow rejection; deterministic output. |
| **PR36** | Design validation + contract/conflict engine | Cross-model constraints, pre/postconditions, capability contracts, incompatible sessions/resources and concurrent composition conflict diagnostics. | Negative contract corpus, explanation quality, no false mutation on failed validation, incremental validation after one-element changes. |
| **PR37** | Dynamic reconfiguration planner | Recompute valid designs when devices/capabilities/requirements change. Keep semantic reasoning at supervisory/reconfiguration level; do not put heavy ontology reasoning into hard real-time control loops. | Device-loss/replace/capability-change scenarios, safe fallback/no-solution state, state migration rules, reconfiguration determinism and latency benchmark. |
| **PR38** | KRL as a first-class language | Add the thesis Knowledge Representation Language with grammar, parser, validator, formatter, LSP registration, templates/queries and typed target bindings. | KRL parse/validation/golden fixtures, LSP parity, sandboxed query/template execution, malformed-template containment. |
| **PR39** | Semantic code generation + target SDK | Versioned generation API, KRL template engine, target plug-in contract, deterministic outputs, manifest of source model/revision/knowledge/template/toolchain used. Add at least one maintained reference target before broad connector growth. | Golden generated artefacts, reproducibility, invalid input refusal, path/sandbox tests, provenance manifest, generated-project compile/test for reference target. |
| **PR40** | Thesis regression + performance qualification | Turn the thesis use cases and synthetic 10/50/100/500/1000-device scenarios into automated benchmark/evidence packs. Preserve the thesis soft-real-time acceptance criteria where environment permits. | Functional regression for vehicle gate, robotics and smart room; benchmark latency/reconfiguration/CPU collection; dedicated stable runner for threshold enforcement; CI trend artifact on normal PRs. |

### Phase E — Systems engineering lifecycle capabilities

| PR | Scope | Required implementation | PR-specific tests / CI acceptance |
| --- | --- | --- | --- |
| **PR41** | Requirements/objectives engineering | First-class requirement/objective entities with IDs, hierarchy, rationale, verification method, status and links into knowledge/workflow/capability/synthesis. Provide desktop and web UX. | Requirement CRUD/versioning, link integrity, import/export round-trip, authorization, audit, deleted-target diagnostics. |
| **PR42** | Digital thread, baselines and change impact | End-to-end trace graph requirement -> knowledge -> model -> synthesis -> generated artefact -> test/evidence; immutable baselines/configurations; compare/impact analysis. | Baseline reproducibility, diff/impact fixtures, moved/renamed element identity, orphan detection, cross-revision trace queries. |
| **PR43** | Simulation / execution sandbox | Deterministic state-machine/workflow runner with step, pause, breakpoints, mock device adapters, time/event controls, record/replay and fault injection. | Deterministic replay, invalid transition protection, cancellation/timeouts, fault scenarios, no external side effects without explicit adapter. |
| **PR44** | Formal verification adapter framework | Solver-neutral contracts for LTL/SMT/assume-guarantee/model checking; start with a small maintained reference verifier; map counterexamples to model elements. | Known satisfiable/unsatisfiable fixtures, timeout/cancel, counterexample mapping, unsupported-feature diagnostic, verifier version provenance. |
| **PR45** | Engineering interoperability I | ReqIF import/export for requirements and a SysML v2 mapping boundary/API for selected KIDE concepts without replacing KIDE's thesis DSLs. Preserve external IDs/provenance. | Round-trip fixtures, unknown-extension preservation/reporting, mapping-loss diagnostics, version compatibility. |
| **PR46** | Engineering interoperability II | OSLC link/provider boundary plus simulation/deployment connector contracts for FMI/AutomationML/OPC UA/ROS 2 or equivalent domain adapters. Implement only tested reference adapters; keep others SDK-level until maintained. | Contract conformance, mocked external systems, timeout/retry/idempotence, no credential leakage, adapter isolation. |
| **PR47** | Engineering reports and evidence packs | Configurable traceability matrix, requirements coverage, validation/verification report, synthesis rationale, generated-artifact provenance and audit package export. | Deterministic report data, access filtering, no secrets, baseline-linked evidence, PDF/HTML/JSON generation smoke where supported. |

### Phase F — Governed AI, policy and engineering automation

| PR | Scope | Required implementation | PR-specific tests / CI acceptance |
| --- | --- | --- | --- |
| **PR48** | Governed model gateway | Provider-neutral AI gateway with project/org policy, model allowlists, data classification, redaction, quotas, timeout/fallback and retention controls. | Provider mocks, redaction, quota, retry/fallback, denied-classification tests, no direct client provider keys. |
| **PR49** | AI engineering context + provenance | Retrieval only from authorized project/knowledge/trace sources; exact citations to model/revision; prompt/model/tool provenance; proposed edits represented as reviewable patches. | Access isolation, citation validity, stale-context detection, prompt-injection fixture set, deterministic validation of proposed edits. |
| **PR50** | Policy as code + approvals | Organization/project policies for model changes, generation, deployment, AI/tool use and safety/security rules; explainable decisions; risk-tiered human approvals. | Allow/deny/approval matrix, policy versioning, fail-closed unavailable-policy behavior for mandatory gates, approval replay/expiry. |
| **PR51** | Bounded agent/tool execution | Capability registry, explicit scopes, sandbox, timeouts, cancellation, resource budgets, side-effect classification and complete audit for AI/automation tools. | Unauthorized tool denial, sandbox escape/path tests, timeout/cancel, idempotency, approval-required side effects, audit completeness. |
| **PR52** | Git/PR engineering lifecycle + extension SDK | Branch/commit/review/evidence links from project revisions; connector SDK for SCM/ALM/PLM/security tools with stable versioned APIs. | Git round-trip, dirty/conflict states, detached/offline behavior, connector compatibility test kit, plugin isolation/version negotiation. |

### Phase G — Security, sustainability, operations, installers and GA qualification

| PR | Scope | Required implementation | PR-specific tests / CI acceptance |
| --- | --- | --- | --- |
| **PR53** | Security evidence and hardening | SAST, SCA, secret scanning, container/IaC scanning, dependency/license policy, SBOM reachability/enrichment, secure defaults and threat-model evidence. | Known-vulnerable fixture detection, false-positive suppression governance, secrets fixture, OWASP-style API/web tests, signed SBOM/provenance verification. |
| **PR54** | Sustainability / C3-ECO evidence | Measure build/runtime resource use, AI/token/vector costs where applicable, benchmark regressions, workload boundary and evidence export aligned with the certification work. | Reproducible measurement schema, no-telemetry/offline behavior, benchmark comparison, evidence integrity, configurable budgets. |
| **PR55** | Observability and supportability | Structured logs, metrics/traces, health/readiness, correlation IDs from browser->gateway->LSP/model/synthesis, privacy-safe support bundle and diagnostic UI. | Trace propagation, PII/secret redaction, support bundle reproducibility, degraded dependency health, log rotation/size bounds. |
| **PR56** | Shared-service production deployment | Containerized services, PostgreSQL/knowledge-store choices where required, HA topology, backup/restore, migration jobs, air-gapped images/charts and graceful desktop offline degradation. | Fresh install, rolling upgrade, backup/restore, failed migration rollback, node/service loss, no-network desktop tests. |
| **PR57** | Native end-user installers + updates | Windows signed MSI/EXE installer plus portable ZIP; notarized macOS app in DMG/PKG for ARM64/x64 as applicable; Linux AppImage plus portable tar and optionally deb/rpm. Embedded runtime, file associations, Start/Menu/Desktop integration where appropriate, safe uninstall, signed update channels. | Clean VM install/start/uninstall on each OS, no Java prerequisite, file association, spaces/non-ASCII paths, upgrade/rollback, workspace preservation, signature/notarization verification. |
| **PR58** | World-class UX/accessibility/onboarding | Unified thesis-flow project home, progressive disclosure, command palette, searchable help, empty/error/loading states, keyboard navigation, WCAG-oriented contrast/focus/labels, guided sample projects and first-run health check on desktop+web. | axe/accessibility automation on web, keyboard E2E, first-run flows, invalid/offline/degraded-state screenshots and assertions, usability telemetry opt-in only. |
| **PR59** | Enterprise qualification suite | Long-running soak, large models, concurrent users, restart/recovery, failover, upgrade from supported versions, offline/air-gap, authorization isolation, installer matrix and performance regression gates. | 24h-or-equivalent scheduled soak, load profiles, chaos/failure scenarios, migration matrix, browser matrix, four desktop architectures, zero uncaught qualified-flow exceptions. |
| **PR60** | GA release and downloadable product | Publish the first fully qualified signed GitHub Release and web deployment bundle from immutable CI outputs. Generate checksums, SBOM, provenance, release notes, admin/user docs and compatibility matrix. No source build required for end users. | Download every published artefact into clean environments and execute it; verify signatures/checksums/provenance; run first-five-minutes desktop and web journeys; release workflow must fail closed on missing evidence/signing. |

## 7. Definition of world-class completion at PR60

KIDE may call the release world-class/enterprise-ready only when all of the following are true.

### Engineering workflow

- Human objectives/requirements are first-class and traceable.
- Capability ontology and knowledge retrieval are production supported.
- All five current DSLs plus KRL are supported through the shared semantic layer and LSP.
- Automated capability matching, controller synthesis, validation, visualization, code generation and reconfiguration are product features, not research-only code.
- Requirements -> knowledge -> model -> synthesis -> generated code -> verification evidence is queryable as a digital thread.
- Simulation and formal-verification hooks exist without coupling KIDE to one solver/runtime.

### Desktop and web parity

- Windows, Linux and macOS users can download and run KIDE without source code, Maven or a separately installed JDK.
- Browser users can open the web product and model with Monaco + LSP and GLSP diagrams.
- Desktop and web operate on the same versioned project semantics and can round-trip supported projects.
- VS Code/Theia integrations remain possible because language/diagram intelligence lives behind LSP/GLSP rather than in the KIDE web UI.

### Enterprise readiness

- OIDC-based enterprise identity, RBAC, audit, policy and human approval are enforced server-side.
- Secrets never become project literals or client-visible provider credentials.
- Shared services have health, backup/restore, migration, HA and air-gap stories.
- Supply chain is signed, pinned, SBOM'd and provenance-attested.
- Security, sustainability and operational evidence are generated continuously.

### Reliability

- Qualified workflows produce zero uncaught application exceptions.
- Corrupt/missing/unsupported state becomes typed diagnostics or recovery flows, not crashes.
- Every persistence mutation is transactional, revision-aware or explicitly recoverable.
- Upgrade and rollback preserve user workspaces and stable identities.
- Thesis benchmark scenarios remain part of regression qualification.

### Usability

- The UI follows the engineering process instead of exposing implementation modules.
- A new user can create/open a sample, understand the current stage, identify errors, synthesize, inspect the design and generate an artefact without reading source code or installing developer tooling.
- Expert users retain direct textual/graphical access, keyboard workflows, Git integration, traceability and extension APIs.

## 8. Release artefacts required at GA

A production tag must publish immutable customer-consumable artefacts, not only CI intermediates:

- `KIDE-<version>-windows-x64-setup.exe` or `.msi` (signed);
- `KIDE-<version>-windows-x64-portable.zip`;
- `KIDE-<version>-macos-arm64.dmg` and supported Intel/universal equivalent (Developer ID signed + notarized);
- `KIDE-<version>-linux-x86_64.AppImage` plus portable `.tar.gz` and enterprise package(s) where maintained;
- headless KIDE language-server package/container;
- KIDE web/service OCI images by immutable digest;
- Helm chart and/or supported Docker Compose bundle for self-hosting;
- offline/air-gap dependency/image bundle if advertised;
- update-site archive;
- `SHA256SUMS.txt`, release manifest, CycloneDX SBOM, provenance attestations and compatibility matrix.

End users must not need Git, Maven, Node, a JDK, Xtext, Eclipse SDK or the repository source to use a production release.

## 9. External technology direction

- Xtext 2.44 removed Xtext-Web/GWT. KIDE therefore standardizes on LSP for browser textual language support: https://eclipse.dev/Xtext/releasenotes.html
- Xtext documents LSP support and its language feature surface, including diagnostics, completion, hover, navigation, references, code actions, formatting, rename, folding and semantic tokens: https://eclipse.dev/Xtext/documentation/340_lsp_support.html
- Monaco's language-client ecosystem supports connecting Monaco to external LSP processes over WebSocket: https://github.com/TypeFox/monaco-languageclient
- Eclipse GLSP provides the analogous protocol/server architecture for modern graphical editors and supports web, Eclipse, Theia and VS Code integration with EMF source models: https://eclipse.dev/glsp/

These are architectural boundaries, not excuses to outsource KIDE's domain semantics. KIDE's thesis models, ontology, synthesis, validation and evidence remain KIDE-owned services.

## 10. PR execution discipline

For every implementation PR:

1. start from fresh `main` after the previous PR is merged;
2. state exactly which roadmap acceptance criteria are being implemented;
3. add tests before/with the implementation and wire them into required CI;
4. preserve all earlier required checks;
5. run the packaged product/service, not only unit tests;
6. investigate the exact first failing command when CI fails and make the smallest strict fix;
7. never solve a failure by skipping tests, loosening error detection, disabling security or suppressing unresolved runtime problems;
8. update this roadmap's status table only after the PR is genuinely green and merged.

The target after PR60 is not merely a feature-rich research IDE. It is a downloadable, browser-capable, versioned, secure, traceable, interoperable and operationally supportable systems engineering product whose differentiating engineering flow remains the knowledge-driven synthesis approach established by the thesis.
