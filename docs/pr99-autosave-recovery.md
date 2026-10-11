# PR99 — Recovery for interrupted Web IDE autosaves

## Root cause

An engineer can edit a hosted DSL file and switch projects or sign out before the 800-ms autosave debounce sends its request, or while a write is in flight. The Web IDE disposes autosave coordinators and the Monaco models during teardown (correctly suppressing stale callbacks since PR97), but that also discards pending text that was never acknowledged by the server. PR98 protects against unsolicited model reload overwrites; it does not recover pending edits from a disposed workspace.

## Implementation

- Persist remote draft text in **tab-scoped `sessionStorage`** synchronously on each Monaco edit *before* scheduling autosave. Draft key includes Firebase **UID, project ID and full model path**, encoded with unambiguous separators. Do not store ID tokens, refresh tokens, passwords or authorization headers.
- Each record carries the original **ETag**, last-updated time, schema version, and a 2 MiB UTF-8 size ceiling. Invalid JSON, wrong schema/scope, or corrupt drafts are ignored. Storage quota failures are reported to the user rather than being described as protected.
- At teardown capture still-dirty hosted models before disposing their editors; protect unsaved text on account switching and sign-out without allowing another UID to see it.
- When an autosave succeeds, delete the saved draft **only when the live text matches the acknowledged server content**. If the engineer typed more while the earlier write was outstanding, retain the latest text with the newly acknowledged ETag.
- On a later authorized model open, display an **Unsaved browser draft available** region; never silently replace canonical server text. A **Restore draft** action is available only when the server's current ETag matches the draft's base ETag and no other unsaved edit is pending. Recovery enters the normal revision-checked autosave pipeline and clears the draft upon server acknowledgement.
- If ETags differ, recovery is view/copy-only in a read-only textarea, with no server overwrite. The engineer can use the existing review/merge processes to reconcile the text. An explicit **Discard draft** removes only that scoped recovery record.
- Existing PR97 project-session fencing, PR98 model-open intent checks, and optimistic concurrency remain authoritative.

## Verification

`web/tests/recoveryDraft.test.ts` exercises seven cases: exact scope, UID isolation, project/path isolation, limits, quota failure, corrupt/cross-scoped data and targeted removal.

`web/e2e/recovery-draft.spec.ts` provides two authenticated mocked browser flows: a matching-revision draft is explicitly restored via a single revision-checked PUT and removed after acknowledgement; a mismatching revision leaves the draft readable but prevents any PUT.

CI also runs full Web Vitest/Playwright, TypeScript/Vite, Eclipse desktop products, Enterprise backend and Cloud Run packaging/boot checks.

## Limitations and acceptance

`sessionStorage` is per browser tab/session and can be unavailable or cleared by browser policy. It is **not** durable backup, multi-device sync, or a substitute for confirmed backend saving. The user is explicitly warned when storage fails. Client/browser tests use mocked Firebase and KIDE APIs; live staging acceptance and role permissions are separate deployment gates. An already submitted server write cannot be unsent.
