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

The access token is held in React memory only. It is not written to local storage,
project files, URLs or logs.

The hosted runtime provides bounded model enumeration and starter-model creation.
It still does not duplicate Eclipse workspace metadata or desktop UI contributions:
the browser remains a separate client over shared model, LSP, GLSP, synthesis and
generation semantics.

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
