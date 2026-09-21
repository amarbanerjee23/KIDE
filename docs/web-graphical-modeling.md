# Browser graphical modelling: GLSP/Sirius parity

PR32 adds graphical modelling to the browser without turning the browser into a second modelling engine.

## Semantic ownership

The canonical semantic artifacts remain the existing Xtext files: `.activity` and `.mncspec`. Both Eclipse Sirius desktop and the browser GLSP path load the same generated EMF packages. The browser receives a GLSP GModel and sends standard GLSP operations; it does not parse, scope, validate, or interpret Activity or MNC semantics.

`product/diagram-semantics.json` is the machine-checked bridge between the GLSP element IDs and the current Sirius design mappings. `scripts/verify_diagram_parity.py` fails CI if a declared Sirius mapping, generated EMF class/feature, Java GLSP type, or browser diagram ID drifts.

## Persistence and conflicts

Semantic edits and GLSP notation are saved together through the PR21 `FileModelRepository` transaction boundary. Each resource is written against the ETag observed when the diagram was loaded. A stale graphical session therefore fails with a revision conflict instead of overwriting a textual edit.

GLSP `.notation` files contain browser diagram layout only. They do not replace or shadow the canonical Xtext model.

## Transport boundary

The browser connects to `/glsp?workspaceId=...` using the same memory-only bearer-token WebSocket subprotocol pattern as `/lsp`. The gateway validates the enterprise workspace identity, origin/TLS policy, session quota, model read permission, and workspace/model write permission before opening the editing session.

Browser file references use `kide-workspace:/<project-relative-path>`. The gateway translates that virtual URI only after validating it against the authorized project root. Server filesystem paths are never a browser API contract.

## Client separation

The Eclipse desktop product continues to use Sirius and its existing `.odesign` definitions. The browser uses GLSP. They are separate presentation clients over the same EMF/Xtext model and repository, which keeps desktop compatibility while allowing a production browser experience.
