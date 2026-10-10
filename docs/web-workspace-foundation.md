# Web engineering workspace foundation

PR30 introduces KIDE's browser client as a separate product surface. It does not
replace the Eclipse desktop application and it does not move DSL semantics into
TypeScript.

## Architecture boundary

The browser consumes the existing shared service boundaries:

- '/api/v1' for authenticated project/model operations;
- the secure '/lsp' WebSocket gateway for shared Xtext language services; and
- the secure '/glsp' boundary for Activity/MNC graphical editing.

The browser deliberately does **not** implement a TypeScript parser, validator,
scoper, completion engine, synthesis engine or graphical semantic model. Monaco
delegates textual semantics to the packaged Xtext LSP, GLSP delegates graphical
semantics to the Java service, and engineering actions use the versioned
enterprise API.

## Browser routes and authentication

KIDE Web deliberately separates authentication from engineering work:

- `/` is the landing page. It contains product context and Firebase sign-in.
- `/register` is the email/password account-registration page. Registration establishes a Firebase session; KIDE engineering authorization still comes from server-side role bindings.
- `/workspace` is the engineering workbench. It contains project/model actions,
  Monaco/Xtext editing, GLSP diagrams, knowledge, synthesis, reconfiguration,
  generation and collaboration.
- the landing page contains a direct **Open Engineering Workspace** link;
- SPA navigation preserves the in-memory Firebase session when moving from the
  landing page into the workspace; and
- direct access to `/workspace` is allowed, but server-backed engineering remains
  unavailable until the user signs in from Home.

Firebase email/password inputs do not appear inside the engineering workbench.
The token remains held in React memory only and is not persisted to local storage,
project files, URLs or logs.

## Workspace behavior

The browser can:

- connect to the enterprise HTTP runtime and list/open authorized projects;
- create an independent authorized hosted project in opt-in registry mode (PR88);
- discover canonical project models through the revision-safe repository index;
- automatically open a production DSL and expose Monaco/Xtext editor actions;
- create the six canonical starter DSL models in an empty hosted project;
- edit Activity/MNC diagrams through the shared GLSP boundary;
- run synthesis, reconfiguration and semantic generation through the shared API;
- autosave server-backed text models with the last observed ETag;
- stop on HTTP 409 revision conflicts without silently overwriting another edit;
- import a bounded ZIP of canonical project files into a local browser workspace;
- promote a local ZIP workspace atomically into an existing authorized empty hosted project;
- reopen the promoted project through the normal hosted Xtext LSP, GLSP, collaboration,
  synthesis, reconfiguration and generation boundaries;
- preserve binary/non-text files for export while editing supported text files;
- export the current local project files back to ZIP; and
- retain local/conflict drafts only in browser session storage.

The hosted runtime provides bounded model enumeration and starter-model creation.
It still does not duplicate Eclipse workspace metadata or desktop UI contributions:
the browser remains a separate client over shared model, LSP, GLSP, synthesis and
generation semantics.

## Browser IDE workbench

The browser shell follows the established workbench conventions used by modern
web IDEs rather than exposing service diagnostics as the primary page. It provides:

- a compact title/command bar;
- Explorer, Search, Engineering, Collaboration and Settings activity views;
- a collapsible hierarchical project tree;
- multiple open-editor tabs and breadcrumbs;
- Quick Open and a keyboard-first Command Palette;
- project-wide text search plus LSP workspace-symbol search;
- Monaco minimap, sticky scroll, folding, bracket-pair guides, CodeLens,
  inline suggestions, parameter hints, multi-cursor editing and validation
  decorations;
- a Problems panel driven directly by Monaco/LSP markers;
- a workspace Output panel;
- dark/light workbench themes; and
- a labeled status bar for API, Xtext LSP, collaboration, language mode,
  save state, diagnostics and cursor position.

Connection endpoints live under Settings, while Firebase credentials live only
on the landing page. Offline state is explicit in the workbench status bar (for
example, "API Offline", "Xtext Offline" or "Collaboration Offline") instead of
being rendered as anonymous "Not connected" badges in the application header.

The browser does not advertise a local terminal or debugger because KIDE Web has
no browser-side shell/debug runtime. Those capabilities require an explicit
remote execution/debug service; the UI does not fake them.

## Archive safety

Project ZIP import rejects compressed uploads over 10 MiB, more than 1,000 files,
individual expanded files over 2 MiB, total expanded content over 20 MiB, absolute
or drive-qualified paths, empty segments, '..' traversal, backslash paths, and
Eclipse '.metadata' workspace state.

Project-level '.kide' files are preserved for local ZIP round-trip because they
may be canonical project metadata. They are deliberately **not** accepted by
hosted promotion: the hosted runtime owns its own enterprise identity, schema,
repository and collaboration metadata. Promotion therefore fails before upload
when a local archive contains '.kide/**'; it never silently drops or overwrites
those files.

## Local ZIP to hosted project promotion

PR84 bridges the former local-only archive mode into the shared engineering
runtime without introducing a second project store.

The user imports a ZIP locally, remains signed in, and chooses **Promote here**
beside an authorized hosted target. The target must be empty. The browser
preflights the target and sends one binary-safe base64 import request; the server
validates every project-relative path and commits the complete archive in one
`ModelRepository` transaction with a commit-time `requireEmpty()` precondition.

Promotion is bounded to 256 files, 2 MiB per file and 10 MiB total expanded
content. The HTTP request boundary is 16 MiB to accommodate base64/JSON overhead.
If any path, size, authorization or empty-target precondition fails, no archive
file is committed.

After a successful commit the browser reopens the same hosted project and uses
its stable workspace ID. From that point the promoted files are ordinary remote
project entries: Monaco/Xtext, GLSP, collaboration, synthesis, reconfiguration
and generation use the same shared Java services as every other hosted project.

PR88 enables `POST /api/v1/projects` only when REST/LSP/GLSP use the shared
persistent `KIDE_HOSTED_PROJECTS_ROOT` registry. Each project is published in
one atomic rename with a persisted owner grant and separate model/knowledge/
collaboration services. The existing single-context Cloud Run deployment does
not implicitly enable creation or migrate its legacy project. See
`docs/hosted-project-registry.md` for atomic-storage limitations and staging
requirements.

## Qualification

The web job in Build KIDE runs TypeScript/Vitest tests, a production Vite build,
and Playwright Chromium qualification. Unit coverage proves archive round-trip/path
guards, API authentication/error handling, authoritative DSL-extension parity and
revision-aware autosave conflict behavior. Browser qualification verifies automatic
project-model discovery, Monaco/Xtext command visibility, synthesis, generation,
reconfiguration and GLSP editing against the real '/api/v1', '/lsp' and '/glsp'
client boundaries.
