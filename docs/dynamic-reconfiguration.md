# KIDE dynamic reconfiguration and thesis-scale qualification

PR37 extends the PR36 deterministic synthesis boundary with supervisory re-planning when
resources, capabilities or requirements change. Reconfiguration remains read-only and does
not execute device commands or place ontology reasoning in a hard real-time control loop.

## Deterministic minimal-disruption planning

`DeterministicReconfigurationPlanner` accepts the previous binding plan, current Activity
requirements and current knowledge-backed resources.

The planner:

1. orders requirements and resources by stable IDs;
2. preserves every previous binding that still satisfies the current capability,
   interface, operation, availability, capacity and conflict constraints;
3. reserves preserved capacity before considering replacement candidates;
4. re-plans only invalidated or new requirements through the PR36 deterministic matcher;
5. emits explicit diagnostics when a binding was invalidated or no replacement exists;
6. produces a deterministic SHA-256 fingerprint over cause, requirements, resources,
   resulting selections and migration instructions.

If a resource's maximum binding count shrinks, the lexicographically earliest requirements
retain their valid bindings and later requirements are replanned. This makes capacity
degradation deterministic.

## State migration rules

Every previous/current requirement pair receives an explicit supervisory migration policy:

- `PRESERVE` — same valid resource binding; keep supervisory state.
- `MIGRATE` — equivalent capability moves to a different resource.
- `RESET` — requirement/capability semantics changed; do not carry requirement-specific state.
- `INITIALIZE` — a new requirement needs fresh state.
- `SAFE_FALLBACK` — no valid automatic binding or design contract remains.
- `RETIRE` — the requirement no longer exists.

Requirement/capability-change causes are deliberately conservative and force `RESET`.
If current design contracts fail, all non-retirement automatic migration instructions are
converted to `SAFE_FALLBACK`.

These are planning instructions only. PR37 does not perform external device side effects.

## Server-owned semantics

The enterprise reconfiguration endpoint is:

`POST /api/v1/projects/{projectId}/reconfiguration`

It uses the same `MODEL_SYNTHESIZE` authorization boundary as deterministic synthesis.

The client supplies only:

- current Activity model ID;
- current model ETag;
- a declared reconfiguration cause; and
- historical `requirementId -> resourceId` bindings.

Clients do not submit Activity names, capability semantics, candidate resources, rankings,
contracts or migration decisions. Current requirements are re-derived from the canonical
Xtext Activity dependency closure and current candidates are re-derived from the current
project knowledge snapshot.

Historical bindings are evidence only. The server revalidates them against the current
semantic model and knowledge data. Duplicate historical requirement IDs and oversized
binding sets are rejected.

The Activity ETag is verified before and after planning. If the model changes while
reconfiguration is running, the response is discarded as a revision conflict.

## Browser boundary

The browser reuses the previous successful synthesis result only to provide stable binding
IDs. It sends no matching or ontology semantics. Users choose the cause and the browser
renders the server-returned selections, migration instructions and diagnostics.

A dirty/unsaved Activity model cannot be reconfigured from the browser.

## Thesis-scale evidence

The packaged headless product runs synthetic resource sets at exactly:

- 10 devices;
- 50 devices;
- 100 devices;
- 500 devices; and
- 1000 devices.

For each size, the scenario creates a deterministic initial binding plan, removes the
highest-ranked primary device, re-plans, reruns the same reconfiguration, and asserts:

- the plan remains feasible;
- exactly one binding migrates;
- all unaffected bindings remain preserved;
- the repeated fingerprint, selections and migration instructions are identical.

The benchmark records:

- device count;
- requirement count;
- initial matching wall-clock nanoseconds;
- reconfiguration wall-clock nanoseconds;
- current-thread CPU nanoseconds;
- preserved/migrated binding counts; and
- deterministic fingerprint.

The JSON evidence uses schema
`kide-pr37-reconfiguration-benchmark-v1` and is retained by GitHub Actions as a CI
artifact for trend analysis.

## Timing policy

Hosted CI runners are not stable performance laboratories. PR37 therefore makes semantic
correctness, deterministic re-planning and scale completion hard gates, while timing values
are evidence-only on hosted runners.

A future dedicated stable qualification runner may add calibrated latency thresholds
without changing the benchmark schema. KIDE must not claim a soft-real-time bound from
uncontrolled hosted-runner measurements.

## Regression scenarios

Permanent tests include:

- vehicle gate: loss of the primary detection device migrates only the affected binding;
- robotics: a capability-contract change forces supervisory state reset;
- smart room: loss with no feasible replacement produces `NO_SOLUTION` and
  `SAFE_FALLBACK`;
- unchanged inputs return `UNCHANGED` with all state preserved;
- capacity reduction deterministically preserves the earliest valid binding and replans
  only the excess binding.

All PR36 synthesis, design-contract, desktop, browser, knowledge, LSP, GLSP, authorization,
audit and packaged-runtime gates remain mandatory.
