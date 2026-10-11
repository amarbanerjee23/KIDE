# PR98 — Model-open intent and unsaved Monaco draft protection

## Confirmed lifecycle gaps

PR97 prevents old *projects* from applying stale API results, but it does not distinguish two model-open requests inside the same project. A delayed lazy GET for file A can reselect file A even after the engineer explicitly selects file B. Similarly, `MonacoWorkspace.ensure()` synchronizes server text into an existing Monaco model. Re-loading a file with unsaved edits can silently replace those edits if it was not an explicit conflict-reload operation.

## Changes

- Publish new model listing metadata to the synchronous entries ref before scheduling React rendering; a model fetch must not misclassify a newly inserted starter-model summary as a competing update. Track a monotonic **model-open intent** independently from project scope. Selecting any file or tab, closing a tab, and resetting a project invalidates earlier lazy model requests.
- Capture the requested file's current Monaco text and server ETag *before* fetching. On response, compare against the live text and ETag; if either changed during the request, discard the server response.
- Reject a background/navigation reload if the file is dirty or has an autosave **pending or in flight**. An idle coordinator after a successful save is not considered a pending write. Never dispose an active autosave or synchronize over the local draft.
- An explicit **Reload server revision** action is the only way to discard an already-dirty draft; it first backs up local text to `sessionStorage` using the existing project-specific conflict key. Even an explicit reload refuses to overwrite text that changed while its GET was running.
- Only announce a successful conflict/graphical reload when the model was genuinely refreshed, not when an outdated result was discarded.

## Acceptance

- `web/tests/modelLoadPolicy.test.ts`: seven cases for clean model loading, dirty and pending-save prevention, deliberate server reload, mid-flight local edits, newer ETags, and a model created by another hydration request.
- `web/e2e/model-open-intent.spec.ts`: mocked authenticated project with three DSL models; hold beta's GET, open gamma, then release beta and require gamma to remain selected without beta appearing as a late tab.
- All Web unit/browser, Eclipse and Cloud Run build workflows must be green before merge.

## Safety scope

The browser mock proves client state handling and does not certify a live Firebase/Cloud Run deployment. The server remains the source of truth for revision checks and authorization. An already-sent autosave write cannot be canceled by a model selection policy.
