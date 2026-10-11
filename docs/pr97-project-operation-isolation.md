# PR97 — Project-scoped asynchronous operations and disposed autosaves

## Problem

The Web IDE's PR94–PR96 fixes fenced Firebase authentication, Xtext and collaboration lifecycles, but many other asynchronous UI callbacks were still allowed to commit state after the engineer switched projects, signed out, or reopened the same project. Notable examples: review listings and change-set details, model refreshes, lazy Monaco model hydration, synthesis/reconfiguration/generation results, knowledge queries and cross-file search results.

The autosave coordinator cleared its debounce timer on disposal but did not guard an in-flight write's response. The old response could notify a now-closed editor about a saved revision or conflict. A network write already sent cannot be retracted; the server-side revision guard remains authoritative.

## Changes

- **Project-scoped response guards:** Capture the current project ID, project-open attempt, monotonically advancing project-workspace scope, and Firebase authentication session version before API operations. Only the matching live context may apply results or surface errors.
- **No post-switch dependent mutations:** Where an action fetches metadata before a subsequent remote mutation (e.g. generation KRL revision, knowledge trace ETag), return before issuing the dependent request if its initiating context became obsolete.
- **Review isolation:** Refresh and open review details only in the originating context; increment the review request generation so an older review detail cannot overwrite a more recent selection.
- **Workspace/outline search isolation:** Stale asynchronous model hydration, full-project matches and LSP symbol results are discarded on switch/reopen, including when the same path exists in a different project.
- **Teardown ordering:** Invalidate project scope and dispose autosave callbacks before awaiting old collaboration presence DELETE. A slower overlapping teardown cannot reset a newer workspace.
- **Autosave disposal:** Suppress stale success, conflict and transport-error callbacks from an in-flight write, clear pending changes, and avoid rescheduling after disposal.

## Tests

- `web/tests/autosave.test.ts`: delayed successful save, 409 conflict, transport failure after disposal; no callbacks or superseded follow-up.
- `web/e2e/project-switch-race.spec.ts`: after activating the second project, hold its review refresh, switch again, then release the old review response. The old review must not appear under the newly active project. Also preserves PR94's stalled Xtext and PR95's exactly-one-join assertions.
- All normal Web Vitest/Playwright, TypeScript, Eclipse desktop build and Cloud Run CI workflows.

## Operational limits

This is client-side state isolation, not cancellation of an already-accepted server mutation. Server permission checks, optimistic concurrency and audit trails remain required. CI browser tests use mocked authorized endpoints; live Firebase/Cloud Run authorization still requires an operator-run acceptance test.
