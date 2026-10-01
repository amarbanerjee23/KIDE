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
- discover canonical project models through the revision-safe repository index;
- automatically open a production DSL and expose Monaco/Xtext editor actions;
- create the six canonical starter DSL models in an empty hosted project;
- edit Activity/MNC diagrams through the shared GLSP boundary;
- run synthesis, reconfiguration and semantic generation through the shared API;
- autosave server-backed text models with the last observed ETag;
- stop on HTTP 409 revision conflicts without silently overwriting another edit;
- import a bounded ZIP of canonical project files into a local browser workspace;
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

Project-level '.kide' files are preserved because project schema and enterprise
identity metadata are canonical project content.

## Qualification

The web job in Build KIDE runs TypeScript/Vitest tests, a production Vite build,
and Playwright Chromium qualification. Unit coverage proves archive round-trip/path
guards, API authentication/error handling, authoritative DSL-extension parity and
revision-aware autosave conflict behavior. Browser qualification verifies automatic
project-model discovery, Monaco/Xtext command visibility, synthesis, generation,
reconfiguration and GLSP editing against the real '/api/v1', '/lsp' and '/glsp'
client boundaries.
