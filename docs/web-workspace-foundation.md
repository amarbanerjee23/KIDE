# Web engineering workspace foundation

PR30 introduces KIDE's browser client as a separate product surface. It does not
replace the Eclipse desktop application and it does not move DSL semantics into
TypeScript.

## Architecture boundary

The browser consumes the existing shared service boundaries:

- '/api/v1' for authenticated project/model operations;
- the secure '/lsp' WebSocket gateway for Xtext language services in PR31; and
- the future GLSP service boundary for graphical editing in PR32.

PR30 deliberately does **not** implement a TypeScript parser, validator, scoper,
completion engine, model index or synthesis engine. Monaco is present only as the
editor foundation in this PR. Textual semantic parity is wired to the existing
Xtext LSP in PR31.

## Workspace behavior

The browser can:

- connect to the enterprise HTTP runtime and list/open authorized projects;
- load a known model ID through the revision-safe model API;
- autosave server-backed text models with the last observed ETag;
- stop on HTTP 409 revision conflicts without silently overwriting another edit;
- import a bounded ZIP of canonical project files into a local browser workspace;
- preserve binary/non-text files for export while editing supported text files;
- export the current local project files back to ZIP; and
- retain local/conflict drafts only in browser session storage.

The access token is held in React memory only. It is not written to local storage,
project files, URLs or logs.

The server intentionally does not yet provide model indexing or project creation.
The web UI exposes those states honestly instead of inventing parallel browser
semantics.

## Archive safety

Project ZIP import rejects compressed uploads over 10 MiB, more than 1,000 files,
individual expanded files over 2 MiB, total expanded content over 20 MiB, absolute
or drive-qualified paths, empty segments, '..' traversal, backslash paths, and
Eclipse '.metadata' workspace state.

Project-level '.kide' files are preserved because project schema and enterprise
identity metadata are canonical project content.

## Qualification

The web job in Build KIDE runs TypeScript/Vitest tests, a production Vite build,
and a Playwright Chromium smoke test. Unit coverage proves archive round-trip/path
guards, API authentication/error handling and revision-aware autosave conflict
behavior. The browser smoke verifies project open and model load against mocked
HTTP responses at the real '/api/v1' boundary.
