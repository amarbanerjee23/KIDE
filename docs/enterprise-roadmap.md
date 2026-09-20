# KIDE Enterprise Roadmap

This roadmap records the enterprise capability sequence established through E01-E21.
The current implementation numbering and consolidation decisions are authoritative in
[`consolidated-release-plan.md`](consolidated-release-plan.md). The broader capability
requirements, desktop/web parity, thesis-flow requirements and GA acceptance remain
defined in [`world-class-systems-engineering-roadmap.md`](world-class-systems-engineering-roadmap.md).

KIDE has two first-class product clients: the Eclipse desktop product and the web product.
They must share the same language/model/knowledge/synthesis semantics. Browser textual
editing uses the shared Xtext language server through LSP; browser graphical modelling uses
a protocol/server boundary (planned GLSP) over the same source models. A separate web
parser, validator, synthesis engine or model meaning is explicitly prohibited.

## Delivery rules

Every phase is delivered through a focused pull request. A later phase must not weaken tests
or controls introduced by an earlier phase. Enterprise capabilities must work through shared
client-neutral contracts so that desktop and web can consume the same behavior unless a
capability is explicitly platform-specific.

A phase is complete only when its implementation, automated tests, migration or compatibility
considerations, operator/developer documentation, packaged-runtime evidence and CI evidence
are present. Documentation alone does not complete a phase.

## Release gates

Every production candidate must eventually prove:

1. reproducible build inputs and dependency provenance;
2. standalone Windows/Linux/macOS packaging with a tested embedded runtime;
3. a tested browser deployment that consumes shared LSP/model services;
4. native signing/notarisation where the operating system supports it;
5. upgrade, rollback and workspace/project compatibility;
6. least-privilege identity and authorization;
7. protected secrets and configuration;
8. tamper-evident audit/provenance evidence;
9. policy enforcement with fail-closed behavior for mandatory controls;
10. bounded and auditable AI/model/tool execution;
11. security scanning, SBOM and dependency/license evidence;
12. telemetry, diagnostics and supportability without source-code disclosure;
13. backup/recovery for shared state and graceful degradation of remote services;
14. performance, concurrency and long-running stability qualification; and
15. offline/air-gapped behavior for capabilities advertised as offline capable.

## Implementation sequence

| Phase | Scope | Primary enterprise outcome | Integrated roadmap mapping |
| --- | --- | --- | --- |
| E01 | Portable desktop distribution | Self-contained Windows x64, Linux x64, macOS x64/ARM64 products with embedded runtime and release qualification | Completed |
| E02 | Trusted release supply chain | Pinned GitHub Actions, SBOM/provenance, Windows signing, macOS signing/notarisation, release attestations | Completed |
| E03 | Enterprise configuration and secrets | Layered configuration, secure secret references, no credentials in workspace/project files | Completed |
| E04 | Organization/project/workspace model | Stable organization → portfolio → project → workspace identities and metadata | Completed |
| E05 | Identity and authorization | OIDC-ready identity abstraction and least-privilege authorization | PR17-PR18 |
| E06 | Audit and engineering event model | Append-oriented actor/action/resource/outcome evidence with correlation IDs | PR19 |
| E07 | Service/API boundary | Versioned contracts between clients and shared enterprise services | PR20-PR21, PR26-PR28 |
| E08 | Model gateway | Governed provider/model routing, classification, quotas, redaction and retention controls | PR41 |
| E09 | Knowledge fabric | Project/enterprise knowledge graph, semantic retrieval, lineage and source authority | PR33-PR34 |
| E10 | AI context and provenance | Exact source/context/model/tool provenance for generated engineering changes | PR41 |
| E11 | Policy as code | Organization/project policies enforced in clients, services and CI with explainable decisions | PR41 |
| E12 | Agent/tool execution | Capability registry, sandbox/bounds, timeouts, cancellation and auditable tool calls | PR41 |
| E13 | Human approvals | Risk-tiered approval gates for engineering and production-affecting actions | PR41 |
| E14 | Git/PR lifecycle | Traceable branches, commits, reviews and evidence links | PR40 |
| E15 | Security evidence | SAST/SCA/secrets/IaC/container inputs, SBOMs, vulnerability evidence | PR42 |
| E16 | Sustainability evidence | Resource/carbon measurement, regression budgets and C3-ECO-aligned evidence | PR42 |
| E17 | Workflow execution | Durable/sandboxed execution, retries, idempotency and recovery | PR39, PR41 and PR43 |
| E18 | Observability/support | Structured diagnostics, health checks, tracing and privacy-safe support bundles | PR42 |
| E19 | Shared-service deployment | HA/on-prem/air-gap deployment model used by desktop and web | PR43 |
| E20 | Connector and extension SDK | Stable versioned APIs for SCM, ALM/PLM, simulation, security and organization extensions | PR40 |
| E21 | Enterprise qualification | Upgrade/failover/load/security/offline/compatibility qualification and release acceptance | PR45 |

## Completed baseline: E01-E04

E01 established the distribution baseline:

- Tycho materializes native Eclipse launchers for Windows x64, Linux x64, macOS Intel and macOS Apple silicon.
- The product embeds a pinned Java runtime so end users do not install or configure Java separately.
- CI rejects archives missing the launcher, embedded Java, Eclipse configuration, plug-ins or executable Unix file modes.
- Release engineering defines stable customer-facing names, SHA-256 checksums and a machine-readable release manifest.

E02 made those bytes trustworthy and independently verifiable:

- every external GitHub Action is pinned to a full immutable commit SHA and CI rejects mutable action tags;
- production Java/Eclipse, Windows and macOS signing paths fail closed;
- macOS distributions require Developer ID signing, notarisation, stapling and verification;
- checksums and release evidence are regenerated from final signed/notarised bytes;
- CycloneDX SBOM and GitHub build-provenance attestations bind artefacts to the release workflow.

E03 introduced the shared configuration/security contract:

- deterministic precedence is defaults → installation → user → workspace → project, with provenance retained for every winning value;
- sensitive configuration keys contain `secret://` references rather than credential literals;
- encrypted Equinox secure storage and environment-backed secret providers are supported;
- normal configuration APIs never expose resolved secret material; and
- resolved secrets are short-lived, redacted and wipe internal character arrays when closed.

E04 established stable organization → portfolio → project → workspace context:

- canonical IDs are typed `kide:<scope>:<uuid>` values independent of paths and display names;
- organization/portfolio/project IDs travel with the project descriptor while each workspace has its own stable workspace ID;
- workspace ancestry mismatches fail closed rather than silently rebinding;
- malformed, missing, partial, oversized or unsupported metadata returns typed diagnostics rather than uncaught workbench exceptions;
- persistence rejects unsafe symbolic-link paths, performs atomic replacement and rolls back paired writes on failure;
- the feature is lazy-loaded; and
- CI executes E04 tests plus a self-check from the actual packaged desktop product.

## Next execution point

PR22 through PR27 are merged. PR27 restored the Jetty HTTP OSGi dependency and
per-language rename compatibility, but it was merged before the exact-head full
build finished. The post-merge build then failed the secure WebSocket packaged
qualification.

**PR28 is active as a strict runtime stabilization PR.** The gateway failure is
a lifecycle race in embedded Xtext sessions: Xtext's default shutdown/exit handler
calls `System.exit`, so a valid client `shutdown` + `exit` can terminate the
shared gateway JVM before reconnect qualification. PR28 binds the no-op handler
explicitly for in-process sessions.

The same full run also proved PR27's rename correction was incomplete. Older
generated setups can overwrite Xtext's global language registry with runtime-only
providers for transitive dependencies; `RenameService2` is language-injector
scoped and consults that global registry. PR28 therefore republishes the final
IDE-aware providers after every generated setup completes and fails startup if
either the local or global provider lacks rename support.

PR29 starts the separate React/TypeScript web workspace only after PR28's exact
head is fully green across desktop packaging, enterprise runtime, ordinary LSP,
secure WebSocket, HTTP API and LSP parity qualification.
