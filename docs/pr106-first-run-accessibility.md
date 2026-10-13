# PR106 — First-run engineering and keyboard-accessible Web workbench

## Scope

This PR brings the **Web** first-run journey closer to the existing
**Eclipse desktop** onboarding, without changing desktop project layout or
duplicating Xtext/GLSP semantics. The first screen after opening
`/workspace` derives an explicit five-stage checklist from the *live* app
state: Firebase sign-in, compatible REST API, an authorized project,
production DSL source models and real Xtext + collaboration connections.

Only the next incomplete step has an action. The actions execute the
existing **Home sign-in, Reconnect API, Explorer project list/create,
server-side starter-model creation, Quick Open and authenticated service
reconnection**. There is no mock "connected" status and no implicit role
grant. The guide cannot mark model or language readiness until a project
is actually open. The server remains the authorization source of truth;
the guide does not automatically create or overwrite any project/model.

The shortcut **Skip to editor** is the first keyboard-focusable workbench
link and targets an explicitly focusable editor panel. The guide uses a
named region, ordered list, textual Ready/Next step/Not yet ready states,
native buttons, visible focus indicators, forced-color borders and a
responsive compact layout. It does not rely only on color or icons.
`web/tests/firstRun.test.ts` covers denial, empty registries, starter
models and partial service readiness; `web/e2e/workspace.spec.ts`
exercises the actual workbench DOM/keyboard link.

## Recommended first-run Web acceptance

1. Open **Home**, sign in using a real Firebase user, then **Open
   Engineering Workspace**. Use Tab before other controls to see **Skip to
   editor** and activate it with Enter. The target editor panel must
   receive keyboard focus.
2. With no access, sign-in may be Ready but the project/LSP/collaboration
   steps **must not** be marked Ready. Grant a project-scoped role or
   explicitly enable a reviewed single-tenant shared-access deployment.
   Reconnect API must re-evaluate real backend access.
3. In an approved POSIX registry staging deployment, select **Create
   project in Explorer**, create a hosted project, open it, then
   **Create starter engineering models**. The hosted API must persist
   canonical files and expose them to REST/Xtext/GLSP; the browser never
   treats locally imported ZIP content as a hosted project.
4. Use Quick Open (Ctrl/⌘+P), F1 Command Palette, diagnostics, graphical
   Activity/MNC view and synthesis/generation on actual hosted sources.
   Verify that Xtext and collaboration only become Ready when both
   sessions connect to the currently selected authorized workspace.
5. Test Chrome, Firefox and WebKit with keyboard-only navigation,
   200% zoom, high contrast, screen-reader names and narrow viewport.
   Functional mocked Playwright CI does **not** replace real live
   Firebase REST/LSP/GLSP authorization checks.

## Eclipse desktop first run (unchanged shipping workflow)

Use the standalone package for the target OS, verify the release hash
and launcher, launch into a user-writable workspace, select **File → New
→ KIDE Modelling Project**, open `models/Loading.cap`, and follow **Help
→ Welcome**. Verify Xtext validation/completion, Sirius diagrams and
model synthesis within that Eclipse workbench. The desktop product does
not consume browser Firebase roles; its local and enterprise identity
configuration remains separate.

**Production launch is not signed off** by a static onboarding checklist.
A human must complete the web and desktop platform matrix, including
assistive technology and an operator-provisioned live staging project.
Unqualified Cloud Storage FUSE registry creation remains disabled.
