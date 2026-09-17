# KIDE Enterprise Eclipse Roadmap

This roadmap governs the transformation of the Eclipse-based KIDE product into
an enterprise-grade engineering application. The web application is not part of
this roadmap unless an Eclipse capability explicitly requires a shared service.

## Delivery rules

Every phase is delivered through a focused pull request. A later phase must not
weaken tests or controls introduced by an earlier phase. Enterprise capabilities
must be usable from the Eclipse product and must support Windows, Linux and
macOS unless the capability is explicitly server-side.

A phase is complete only when its implementation, automated tests, migration or
compatibility considerations, operator/developer documentation, and CI evidence
are present. Documentation alone does not complete a phase.

## Release gates

Every production candidate must eventually prove:

1. reproducible build inputs and dependency provenance;
2. standalone Windows/Linux/macOS packaging with a tested embedded runtime;
3. native signing/notarisation where the operating system supports it;
4. upgrade, rollback and workspace compatibility;
5. least-privilege identity and authorization;
6. protected secrets and configuration;
7. tamper-evident audit/provenance evidence;
8. policy enforcement with fail-closed behavior for mandatory controls;
9. bounded and auditable AI/model/tool execution;
10. security scanning, SBOM and dependency/license evidence;
11. telemetry, diagnostics and supportability without source-code disclosure;
12. backup/recovery for shared state and graceful degradation of remote services;
13. performance, concurrency and long-running stability qualification; and
14. offline/air-gapped behavior for capabilities advertised as offline capable.

## Implementation sequence

| Phase | Scope | Primary enterprise outcome |
| --- | --- | --- |
| E01 | Portable desktop distribution | Self-contained Windows x64, Linux x64, macOS x64/ARM64 products with embedded runtime and release qualification |
| E02 | Trusted release supply chain | Pinned GitHub Actions, SBOM/provenance, Windows signing, macOS signing/notarisation, release attestations |
| E03 | Enterprise configuration and secrets | Layered configuration, secure secret references, no credentials in workspace/project files |
| E04 | Organization/project/workspace model | Stable organization → portfolio → project → workspace identities and metadata |
| E05 | Identity and authorization | OIDC/SAML-ready identity abstraction, RBAC then policy-aware authorization |
| E06 | Audit and engineering event model | Append-oriented actor/action/resource/outcome evidence with correlation IDs |
| E07 | Service/API boundary | Versioned contracts between Eclipse and optional shared enterprise services |
| E08 | Model gateway | Governed provider/model routing, classification, quotas, redaction and retention controls |
| E09 | Knowledge fabric | Project/enterprise knowledge graph, semantic retrieval, lineage and source authority |
| E10 | AI context and provenance | Exact source/context/model/tool provenance for generated engineering changes |
| E11 | Policy as code | Organization/project policies enforced in Eclipse and CI with explainable decisions |
| E12 | Agent/tool execution | Capability registry, sandbox/bounds, timeouts, cancellation and auditable tool calls |
| E13 | Human approvals | Risk-tiered approval gates for source, infrastructure and production-affecting actions |
| E14 | Git/PR lifecycle | Traceable branches, commits, reviews and evidence links from Eclipse workflows |
| E15 | Security evidence | SAST/SCA/secrets/IaC/container inputs, SBOMs, vulnerability reachability and evidence graph |
| E16 | Sustainability evidence | Resource/carbon measurement, regression budgets and C3-ECO-aligned evidence |
| E17 | Workflow execution | Durable workflow/state-machine execution with retries, idempotency and recovery |
| E18 | Observability/support | OpenTelemetry, structured diagnostics, health checks and privacy-safe support bundles |
| E19 | Shared-service deployment | HA/on-prem/air-gap deployment model for enterprise services used by Eclipse |
| E20 | Connector and extension SDK | Stable versioned APIs for SCM, work management, security and organization extensions |
| E21 | Enterprise qualification | Upgrade/failover/load/security/offline/compatibility qualification and release acceptance suite |

## Completed baseline: E01-E03

E01 established the non-negotiable distribution baseline:

- Tycho materializes native Eclipse launchers for Windows x64, Linux x64,
  macOS Intel and macOS Apple silicon.
- The product embeds a pinned JustJ/Adoptium Java runtime so end users do not
  install or configure Java separately.
- CI rejects archives missing the launcher, embedded Java, Eclipse
  configuration, plug-ins or executable Unix file modes.
- Release artifacts use stable customer-facing names and include SHA-256
  checksums plus a machine-readable release manifest.

E02 made those bytes trustworthy and independently verifiable:

- every external GitHub Action is pinned to a full immutable commit SHA and CI
  rejects mutable action tags;
- the Java/Eclipse bundle signing keystore is required for production builds;
- the Windows launcher must pass Authenticode signing and verification;
- both macOS distributions must pass Developer ID signing, Apple notarisation,
  ticket stapling and verification;
- release publication fails closed when any signing credential is unavailable;
- checksums and the release manifest are regenerated from the final signed and
  notarised bytes, never from the unsigned staging archives;
- a CycloneDX SBOM inventories the Eclipse plug-ins shipped in the final product;
- GitHub build-provenance attestations bind the published artifacts to the
  release workflow identity.

E03 introduced the shared configuration/security contract:

- deterministic precedence is defaults → installation → user → workspace →
  project, with provenance retained for every winning value;
- sensitive configuration keys must contain `secret://` references rather than
  credential literals;
- `secret://secure/<alias>` stores encrypted values through Equinox secure
  storage and `secret://env/<NAME>` supports externally provisioned runtime
  credentials;
- normal configuration APIs never return resolved secret material;
- resolved secrets are short-lived, redacted and wipe their internal character
  arrays when closed; and
- the Eclipse preferences UI provisions encrypted aliases without writing secret
  material to project/workspace files.

## Current phase: E04

E04 establishes stable organization → portfolio → project → workspace context:

- canonical IDs are typed `kide:<scope>:<uuid>` values independent of paths and
  display names;
- organization/portfolio/project IDs travel with the project descriptor while
  each Eclipse workspace receives its own stable workspace ID;
- workspace bindings carry their organization/portfolio/project ancestry and
  mismatches fail closed rather than silently rebinding;
- each node carries a bounded descriptive metadata map for later governance,
  policy and integration use;
- malformed, missing, partial, oversized or unsupported metadata returns typed
  diagnostics instead of uncaught workbench exceptions;
- persistence rejects unsafe symbolic-link paths, performs atomic replacement
  and rolls the project descriptor back when the paired workspace write fails;
- the feature is lazy-loaded and does not parse enterprise context merely because
  KIDE starts;
- the Eclipse preference page provides explicit initialization/update without
  changing IDs on rename; and
- CI runs both Tycho E04 failure-path tests and a self-check from the actual
  packaged Linux desktop product, rejecting non-zero exits and runtime
  exception/error signatures.

E05 must preserve E01-E04 controls and add an OIDC/SAML-ready principal abstraction,
least-privilege RBAC and authorization decisions keyed to the stable E04 enterprise
IDs rather than filesystem paths or display names.
