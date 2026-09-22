# KIDE deterministic synthesis, controller composition and design contracts

PR36 promotes the research-backed Activity-to-MNC transformation into a headless,
client-neutral synthesis service and combines it with deterministic knowledge-backed
resource matching and cross-model contract validation.

## Semantic ownership

The canonical semantic inputs remain:

- the existing Activity EMF/Xtext model for workflow intent;
- the existing Capability EMF/Xtext model for resolved control semantics;
- the PR34/PR35 knowledge repository for candidate resources and declared properties;
- the existing MNC EMF/Xtext model for synthesized supervisory controllers.

Desktop and browser clients do not implement matching, ranking, contracts or controller
generation. The desktop transformation command and the enterprise HTTP service both call
the same Java synthesis boundary.

## Deterministic resource-capability matching

Each Activity that declares a bound Capability or textual required capability becomes a
stable synthesis requirement. A bound Capability contributes its compatible component
interfaces and operations; Activity-required operations are also enforced.

Candidate resources are Device nodes from the project knowledge graph. PR36 recognizes
explicit synthesis properties under the KIDE ontology namespace:

- `providesCapability`
- `providesInterface`
- `providesOperation`
- `available`
- `priority`
- `latencyMillis`
- `energyMilliJoules`
- `maxBindings`
- `conflictsWith`

Only resources satisfying the complete capability/interface/operation contract are
eligible. Ranking is deterministic and transparent:

1. reuse an already selected compatible resource where possible;
2. higher declared priority;
3. lower declared latency;
4. lower declared energy;
5. lexicographically smaller stable resource IRI as the final tie-break.

If multiple candidates are equal on the declared criteria, synthesis emits an
`AMBIGUOUS_MATCH` warning and documents the stable-ID tie-break. If no resource is
feasible, synthesis emits `NO_FEASIBLE_RESOURCE` and no controller model is created.

## Design contracts

Before controller generation, the contract engine checks the existing EMF relationships
rather than a parallel schema. Current PR36 contracts include:

- stable and unique Activity names;
- no simultaneous textual and bound capability declaration;
- workflow references must remain inside the same Activity diagram;
- no self-interruption relationship;
- explicitly selected control interface items must belong to the bound Capability;
- every selected resource must still satisfy the full requirement;
- selected resources must not declare incompatible composition;
- textual capability matching alone is insufficient to invent controller semantics.

The last rule is deliberate: a textual `requiredCapability` may identify a feasible
resource, but controller generation requires an actual resolved Capability DSL object.
Such a case returns `CONTRACT_VIOLATION` with `UNBOUND_CAPABILITY_MODEL`.

Valid cyclic Activity workflows, including a self-looping `nextActivity`, remain
supported because they are already part of KIDE's valid language fixtures.

## Controller composition

The existing Activity-to-MNC semantic implementation was moved from the legacy Eclipse UI
plug-in into `com.kide.synthesis`. The old desktop handler is now only a client of this
headless core. The historical golden Activity-to-MNC snapshot remains unchanged and is
tested directly against the new core boundary.

A controller is generated only after matching and contract validation succeed. No-solution
and contract-invalid runs never emit MNC output.

## Read-only project synthesis

`ProjectSynthesisEngine` uses the registered Xtext runtimes for DML, Operation, MNC,
Capability and Activity. It loads canonical project DSL resources into one resource set,
resolves links, rejects parser/linker errors, then synthesizes entirely in memory.

The enterprise API requires the caller's current Activity model ETag. The server verifies
that ETag before synthesis and re-reads it afterward. If the Activity changes during the
run, the result is discarded with the normal revision-conflict response.

Synthesis never saves or rewrites the Activity, Capability, MNC, DML, Operation or
knowledge source files.

## Result evidence

Successful and failed runs return a deterministic evidence structure containing:

- service version and result ID;
- source model version/ETag;
- knowledge revision/ETag;
- deterministic fingerprint;
- selected resource bindings and human-readable ranking rationale;
- structured diagnostics;
- generated MNC text only on successful runs.

The browser workbench sends only the Activity model ID and current ETag and renders this
server response. It does not submit candidate resources, ranking decisions or generated
controller content.

## Qualification

PR36 permanently gates:

- successful bound-capability synthesis;
- repeatability of selections and fingerprints;
- deterministic ambiguity handling;
- no-solution behavior;
- unavailable-resource exclusion;
- refusal to invent unbound Capability semantics;
- the existing Activity-to-MNC golden snapshot;
- packaged headless synthesis;
- enterprise API authorization, stale-revision rejection, repeatability and source
  non-mutation;
- browser API-contract and Playwright synthesis flows;
- all previous desktop, knowledge, GLSP, LSP, security and enterprise release checks.
