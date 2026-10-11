# PR103 — Actual desktop/Web editor and graphical service parity

## Engineering scope

KIDE ships six DSLs: DML, Operation, MNC, Capability, Activity and KRL.
The desktop product runs the shared Eclipse/Xtext services directly, while
the Web IDE consumes those same services via authenticated Xtext LSP and
the GLSP/EMF gateway. The browser must never duplicate the source grammar,
cross-reference resolution, scoping, synthesis or code-generation semantics.

The packaged LSP capability matrix in `product/lsp-capabilities.json` and
Sirius/GLSP mappings in `product/diagram-semantics.json` already qualify
the Eclipse/headless service boundary. Previous PR89 staging acceptance,
however, imported only four DSLs and performed basic LSP/GLSP handshake
checks without real textual editor semantic operations or a populated GLSP
model response.

PR103 **extends the existing operator-approved staging acceptance**, not
the mocked CI suite:

1. Imports **seven** models across all six production DSLs into a real
   isolated hosted project. DML/Operation and a separate KRL golden source
   are read from the exact canonical language fixtures used by packaged
   Eclipse qualification; the existing four-model MNC/Capability/Activity/
   bindings.krl engineering synthesis corpus remains untouched.
2. Reads all seven models back from the actual REST repository, requiring
   byte-for-byte equality, model IDs and SHA-256 revision/ETag identity.
3. Opens an authenticated production-protocol Xtext WebSocket and checks
   the mandatory server capabilities from the canonical parity manifest.
   Sends `didOpen` for all six model URIs in one authorized workspace;
   exercises real document symbols, hover, references, document highlights,
   folding, formatting, semantic token responses and per-language completion
   probes derived from the packaged Eclipse matrix. Tests real protocol
   results rather than counting method names or considering a handshake
   successful editor parity.
4. Opens two authenticated GLSP WebSockets for the **Activity flow** and
   **MNC state-machine** diagram types; initializes an ephemeral session,
   requests an actual diagram model, verifies a nonempty EMF-derived GModel
   and queries the real edit shape/edge hints. No diagram mutations are sent.
5. Signs into the deployed Web UI and opens **all seven model files across the six DSLs**
   through the real project tree into an active Monaco editor tab, verifying
   the selected path, breadcrumbs and visible editor before returning to
   Activity synthesis. The UI KRL input is pinned to the original\n   bindings.krl for code generation. This is a browser action on actual persisted models,
   not a network-replayed or hand-constructed Monaco instance.
6. Preserves the PR89 actual synthesis/code generation, repeatability and
   artifact integrity tests and a second-user negative tenant-access check.
   The browser still logs in with actual Firebase credentials and uses the
   real Monaco and graphical renderers for the engineering workflow.
6. No source, model semantics or copy of the Xtext parsers is added in
   TypeScript. The parity helper is a **test-only** protocol client.

## How to run

The full acceptance continues to run through the existing protected
`Live Staging Engineering Acceptance` workflow, which requires approved
staging Web/API hostnames, two isolated Firebase principals and explicit
`writes_approved=staging-only`. A staging target must support real atomic
project creation as qualified by PR102. Running against production is
forbidden.

Operator action:

```bash
cd web
npm ci --no-audit --no-fund
npx playwright install --with-deps chromium
npm run test:live
```

Supply the existing `KIDE_LIVE_*` variables and credentials from the
protected staging environment; do not put Firebase tokens in the command
line or URLs. Tracing, screenshots, retries and video are disabled so
real bearer credentials cannot be archived. The acceptance suite creates
an isolated project and consumes a creator quota slot. The operator must
manage staging retention/reset separately.

Ordinary PR CI continues to run the packaged cross-adapter semantic
qualification, the LSP and Sirius/GLSP matrices, Web unit tests and
mocked functional editor tests. It also enforces
`scripts/verify_pr103_live_editor_parity.py` and merely discovers
the live Playwright suite **without executing it**.

## Signoff and failure handling

- A failed completion, hover, missing required capability, empty document
  symbol, mismatched model byte, diagram model absence or source-to-target
  authorization denial **fails** staging acceptance.
- Successful CI + browser E2E discovery alone are **not** proof that
  deployed Xtext/GLSP parity works; the separately protected real staging
  run must pass against a build-matched Web/API pair.
- The protocol tests do not currently assert interactive user-driven
  rename/save/undo/redo or per-keystroke latency through real browser UIs.
  Those need human/editor acceptance and PR104 concurrency/recovery.
- GLSP type-hint and GModel responses establish that graphical semantics
  can be obtained from the real server; they do not establish all graphical
  edit/undo/save operations have been exercised end-to-end.
- A real MNC/Activity diagram must contain actual model elements. A
  successful session `initialize` without `setModel` is insufficient.
- Existing desktop packaged tests, storage mode/recovery restrictions,
  Firebase authorization, trusted release policy and compatibility checks
  remain blocking gates.

No production Cloud Run deployment, staging credential provisioning or
Firebase role assignment is performed by this PR.
