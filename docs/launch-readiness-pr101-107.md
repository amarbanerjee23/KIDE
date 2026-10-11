# KIDE — remaining product-launch PR plan (reviewed 2026-10-11)

This is an updated **release-readiness implementation estimate**, not a
replacement for the full historical enterprise feature roadmap. PR1–PR100 are
not re-opened or renumbered; PR100 has merged. The plans below intentionally
group changes that affect the same code/service boundaries. Treat the count
as provisional: real staging acceptance may expose new defects.

## Estimate: seven focused PRs, PR101–PR107 inclusive

| PR | Grouped deliverable | Exit / no-merge gate |
| --- | --- | --- |
| **101** | Authenticated read-only REST/project/Xtext/GLSP/collaboration monitor, separate from browser mocks, opt-in staging schedule and governance | CI browser/test discovery + real provisioned staging probe; a skipped monitor is **not** proof of working services |
| **102** | Hosted storage and deployment reliability: safe multi-project POSIX-capable registry option, explicit backend/role migrations, Cloud Run deployment/rollback guarantees, staging provisioning | Registry creation/restore atomicity on actual target storage, authenticated project creation/authorization across reboot and deployment, rollback rehearsal |
| **103** | Editor engineering functionality and desktop/Web parity in one matrix: all DSLs, Xtext semantic services, graphical GLSP/Sirius parity, synthesis/reconfiguration/generation and opening/saving models | Real desktop packaged tests and real browser/API/LSP/GLSP golden workflows with identical semantic evidence; no mocked-only acceptance |
| **104** | Durability under parallel engineers: same-project concurrency, interrupted saves, conflict/reviews, load/soak, backup/restore and recovery | Measured staging load/restart and complete restore, ETag/owner preservation, no lost or cross-tenant source models |
| **105** | Security + operations launch hardening: RBAC negative tests, audit/log redaction, secrets scanning, vulnerability/SBOM evidence, quotas, structured health, alerts and incident/runbook coverage | Independently assessed role boundaries, observable fail-closed behavior, deployed alert delivery/on-call and penetration/regression review |
| **106** | Product experience and onboarding for Web + desktop: editor usability, actionable diagnostics, accessible keyboard navigation, first-run samples and status/support UX | End-to-end human acceptance, accessibility and first-run journey on desktop platforms and major supported browsers |
| **107** | Release-candidate qualification and production handoff: upgrade/migration/rollback matrix, signed desktop builds, verified hosted deployment, release gates and reproducible provenance | No failed required CI checks; actual staging and deployment sign-offs; only latest fully qualified stable GitHub Release is published |

**Count:** 7 proposed PRs after PR100, including PR101. After PR101 merges,
the estimate becomes **6 remaining PRs** if no additional regressions are
discovered. Operational work (staging resources, approvals, account grants,
secrets, platform signing keys, live runbooks) is **not itself completed** by
merging the code PRs.

## Ordering and dependencies

PR102 and PR103 can be reviewed in parallel after PR101, but live tenant
creation tests in PR103 require qualifying the PR102 filesystem. PR104 depends
on that storage architecture. PR105 may be developed alongside PR104,
but cannot be signed off until staging monitoring, durability and auth
acceptance complete. PR106 is UX-facing and can overlap later backend
qualification. PR107 is strictly last.

## Critical gaps today

- PR100's merged deployment scripts do not prove that the deployed GCP service
  has actually granted an engineer's UID. The operator approval/run remains
  required. Authentication cannot be bypassed to fabricate availability.
- `/api/v1/health` cannot prove project authorization, Xtext, GLSP, or
  Collaboration; the optional authenticated PR101 stage must be **configured
  and observed** before relying on it.
- The current Cloud Storage FUSE volume cannot be assumed to support the
  POSIX atomic rename and restore requirements of registry mode.
- PR89's real staging test is deliberately separate from normal CI and has
  not been evidenced as passed for the current live deployment.
- PR90's offline snapshots and probes do not establish recovery objectives,
  off-site backups, split-brain avoidance or production HA.
- Successful CI on GitHub is necessary but does not prove that signed native
  installers work on all supported operating systems or that a real user
  can complete the full engineering workflow.

## Reassessment policy

Recount the remaining PRs after each real full-stack acceptance and after
every new production-blocking finding. Fix failed tests inside the same PR,
rather than generating another PR for the same subsystem. Do not merge merely
because the anonymous API shows Connected.
