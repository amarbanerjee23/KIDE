# KIDE deployed product comprehensive qualification audit

**Evidence date:** 18 October 2026 (UTC+05:30)  
**Inspected exact merged source:** `4418d9c7e42e0be454de9fcabb0c3b3b5ba17dd3` (PR107 merge).  
**Product under test:** KIDE Web engineering workbench, Cloud Run backend, Xtext LSP, GLSP, collaboration, Firebase identity, Eclipse desktop and delivery pipeline.

## Executive result

**GA / production signoff: BLOCKED — deployed engineering functionality is not verified.**

We inspected real GitHub workflow jobs, individual step conclusions,
test logs, registered live-test suites, signed release inventory and hosted
monitor coverage. We did not substitute successful CI for evidence that
the deployed Cloud Run endpoints work. No authenticated GCP browser, REST
or WebSocket test ran in this review: the actual deployment URLs and
a suitably scoped test Firebase identity are not available to this
execution environment. GitHub reports the application image *built and
booted inside CI*, but that container is not a GCP revision.

### Evidence actually executed

| Automated evidence | Result | Limit |
| --- | --- | --- |
| [Build KIDE main](https://github.com/amarbanerjee23/KIDE/actions/runs/38604671992) | **PASS** Eclipse/OSGi Maven compile, native package qualification and over 30 verification/smoke steps | GitHub runner; not deployed GCP runtime |
| [Web browser foundation](https://github.com/amarbanerjee23/KIDE/actions/runs/38604671992) | **PASS** 87 Vitest unit tests, 14 mocked browser Playwright tests; TypeScript and Vite build | Playwright mocks service calls; real staging tests were **listed**, not executed |
| [Cloud Run image build/boot](https://github.com/amarbanerjee23/KIDE/actions/runs/38604672000) | **PASS** build, boot, local health checks and immutable GitHub Container Registry push | No evidence of deployed Cloud Run rollout or live credentials |
| [Enterprise CI main](https://github.com/amarbanerjee23/KIDE/actions/runs/38604672073) | Workflow **GREEN**, but **the old Python backend Pytest and split frontend TypeScript/Vite steps were SKIPPED** | False-positive job naming; corrected in PR109 against actual source paths |
| [Latest hosted health watch](https://github.com/amarbanerjee23/KIDE/actions/runs/39059785490) | Report-only step **PASS**; public Web/API and authenticated REST/Xtext/GLSP/collaboration jobs **SKIPPED** | Health, latency, routing, auth and session availability **not established** |
| [Releases](https://github.com/amarbanerjee23/KIDE/releases) | Old previews exist; `/releases/latest` returned 404 | No qualified signed stable release evidenced |

## Per-function acceptance matrix

Statuses: **P** = tested and passed in isolated CI (only); **U** = **not tested against deployed GCP**; **B** = blocked/misleading operational gate. A P does **not** imply live acceptance.

| # | Product item / acceptance condition | CI evidence | Deployed GCP |
| ---: | --- | --- | --- |
| 1 | Landing page renders independently of workbench, links to engineering workspace | Browser E2E **P** | **U** |
| 2 | Firebase sign-in/sign-out/session token refresh, invalid token rejected | Identity + Web unit tests **P** | **U** |
| 3 | API public health, runtime version and Web/backend build identity coherence | Cloud Run container boot + policy **P** | **B:** monitoring skipped |
| 4 | Project authorization across all REST, LSP, GLSP and collaboration channels | Auth/JWT/adapter tests **P** | **U** |
| 5 | Private project boundaries, missing role 403 and explicit DENY, other-user isolation | Security and browser mocked tests **P** | **U** |
| 6 | Optional shared Firebase engineer mode (deliberate operator opt-in, no accidental cross-tenant editing) | PR105 contract/tests **P** | **U** |
| 7 | Multi-project discovery, create, select, switch, quota and persistence | Hosted registry and E2E tests **P** | **U** |
| 8 | Promote local ZIP/archive to empty hosted project, rejection on nonempty target | Archive and browser E2E tests **P** | **U** |
| 9 | Canonical source storage, model bytes, ETags, revisions, CAS conflict and role integrity | Model repository + concurrency **P** | **U** |
| 10 | Monaco editor loads every authorized model, switches tabs, quick-open and keyboard shortcuts | Browser E2E **P** | **U** |
| 11 | DML editor syntax/validation/completion/hover/symbols/references | Packaged Xtext + matrix **P** | **U** |
| 12 | Operation DSL syntax/validation/completion/hover/symbols/references | Packaged Xtext + matrix **P** | **U** |
| 13 | MNC DSL syntax/validation/completion/hover/symbols/references | Packaged Xtext + matrix **P** | **U** |
| 14 | Capability DSL syntax/validation/completion/hover/symbols/references | Packaged Xtext + matrix **P** | **U** |
| 15 | Activity DSL syntax/validation/completion/hover/symbols/references | Packaged Xtext + matrix **P** | **U** |
| 16 | KRL DSL syntax/validation/completion/hover/symbols/references | Packaged Xtext + matrix **P** | **U** |
| 17 | Xtext WebSocket authenticated lifecycle, reconnect, cross-workspace cancellation and workspace URIs | LSP gateway + browser tests **P** | **U** |
| 18 | Xtext diagnostics, completion, hover, references, formatting, code actions, semantic tokens/folding | LSP parity contract/packed test **P** | **U** |
| 19 | Graphical Activity diagram real GLSP model, semantic edits/save, layout/hints | Packaged GLSP integration + browser tests **P** | **U** |
| 20 | Graphical MNC state diagram real GLSP model, semantic edits/save, layout/hints | Packaged GLSP integration + browser tests **P** | **U** |
| 21 | Collaboration handshake, authenticated project scoping, concurrent editors and update delivery | Collaboration unit tests **P** | **U** |
| 22 | Deterministic synthesis and Activity→MNC reconfiguration with knowledge trace | JVM smoke/golden tests **P** | **U** |
| 23 | Semantic code generation, deterministic hashes and actual generated-byte integrity | JVM smoke and browser mocked tests **P** | **U** |
| 24 | Knowledge fabric, catalog indexing, provenance and trace navigation | JVM smoke and unit tests **P** | **U** |
| 25 | Autosave, offline recovery draft, aborted GET protection, refresh/reconnect | Web unit + browser E2E **P** | **U** |
| 26 | Concurrent conditional saves, resilience under 2 engineers, 409/412 conflict handling | PR104 contracts/unit **P** | **U** |
| 27 | Registry POSIX atomicity, backup/restore, journal recovery, project quota under mounted GCP storage | Static/storage qualification controls **P** | **U / critical** |
| 28 | Cloud Run revision serving, backend container split, REST and Websocket forwarding, CORS and headers | Local container smoke/security contracts **P** | **U** |
| 29 | Deployment route revisions, rollback, same build SHA and health before/after rollout | Deploy shell/contracts **P** | **U / critical** |
| 30 | Observability: configured Web/API probe, authenticated probes, alarm delivery and latency | Monitoring workflow is present **P** | **B:** both probes skipped |
| 31 | Browser accessibility, first-run onboarding, keyboard and responsive design | PR106 + browser tests **P** | **U:** real browser/screen reader |
| 32 | Desktop Windows x64 native launcher, runtime, installation and Xtext/Sirius parity | Package built/qualified **P** | **U:** native Windows launch |
| 33 | Desktop Linux x64 native launcher, runtime, installation and Xtext/Sirius parity | Package built/qualified **P** | **U:** native Linux launch |
| 34 | Desktop macOS Intel signed/notarized launcher and Eclipse functionality | Package produced **P** | **U:** signing and native launch |
| 35 | Desktop macOS ARM signed/notarized launcher and Eclipse functionality | Package produced **P** | **U:** signing and native launch |
| 36 | Signing, SBOM, SHA256, provenance, immutability and latest-stable-only cleanup | Release code/governance **P** | **B:** no latest stable release |
| 37 | Production authorization, incident/alert routing, audit redaction and least-privileged account boundaries | Auth and governance **P** | **U / critical** |

### Two confirmed engineering process defects

1. **Monitoring false-green:** The hosted health-watch workflow currently has
   no effective configured Web/API or authenticated probe; only its
   reporting job ran. The overall green workflow does **not** demonstrate
   online hosted engineering services.
2. **Legacy enterprise false-green:** The `ci.yml` job gates attempted to
   detect `kide-enterprise/frontend` and `kide-enterprise/backend`, neither
   of which is the actual current product code. The workflow went green
   with critical steps skipped. PR109 directly executes the real
   `web/` Vitest/TS/Vite suite and Python backend/API/security contracts.

## Mandatory safe live testing to complete the user request

The deployed production URLs and authorized synthetic-test identity are
required to execute genuine GCP acceptance. **Do not enter passwords,
bearer tokens or production data into issues.** A read-only public probe
may test GET `/health`, `/api/v1/health`, `/api/v1/version`,
`/kide-version.json`, response headers, Web routing, latency and
version coherence, without modifying customer data.

For authenticated end-to-end project creation, import, autosave,
collaboration, synthesis, generation, and backup/restore, provision a
**separate approved staging project** running the identical image digest
and real Firebase staging accounts. The existing
[`Live Staging Engineering Acceptance`](https://github.com/amarbanerjee23/KIDE/actions/workflows/live-staging-acceptance.yml)
supports this and must be operator-dispatched with isolated test data.
Operations/recovery testing must be performed only on staging/offline
storage, not on live production tenant volumes.

**Pass criteria**: each required live health and authenticated probe has a
non-skipped successful result; all DSL, GLSP, collaboration, generation,
permissions and concurrency acceptance passes on the same SHA/deployed image;
operator signs off rollback/storage recovery and four native desktop
platforms; only then can the protected stable-release workflow execute.

**Current release decision: NO-GO for asserting end-to-end GCP functionality.**
This is an evidence gap, not proof the deployed product is broken.
