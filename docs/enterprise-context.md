# Enterprise organization, portfolio, project and workspace context

KIDE E04 introduces stable enterprise identities for later authorization, audit,
policy, AI-governance and shared-service features. Filesystem paths and display
names are explicitly **not** security identities.

## Hierarchy

Each initialized KIDE working context has four typed identities:

1. organization;
2. portfolio;
3. project; and
4. workspace.

Canonical IDs use `kide:<scope>:<uuid>`, for example
`kide:project:550e8400-e29b-41d4-a716-446655440000`. IDs are generated once and
retained when names or filesystem locations change.

Organization, portfolio and project identity is stored with the project in
`.kide/enterprise-context.properties`, allowing the same project identity to
survive a clone, move or import into another Eclipse workspace. The workspace ID
is local to the Eclipse workspace and is stored under
`.metadata/.plugins/com.kide.enterprise.context/workspace.properties`. The
workspace descriptor also records the organization/portfolio/project IDs it is
bound to; mismatched bindings are rejected rather than silently reinterpreted.

## Metadata

Every hierarchy node has:

- a stable typed ID;
- a human-readable display name; and
- a bounded metadata map.

Metadata keys are limited to 64 characters using letters, digits, `.`, `_` and
`-`; each node is limited to 64 metadata entries and values are limited to 512
characters. These bounds prevent enterprise context files from becoming an
unbounded storage or parsing surface.

The `EnterpriseContextStore.updateMetadata(...)` API preserves IDs and display
names while changing one node's metadata. Later E05+ authorization/audit/policy
features should consume the stable IDs and may use metadata only as descriptive
or policy input, never as an identity substitute.

## Runtime safety contract

E04 is deliberately lazy: there is no bundle activator and no enterprise-context
file is read merely because KIDE starts. The Eclipse page is available at
**Window -> Preferences -> KIDE Enterprise Context** and reads the selected
project only when the page is opened.

`EnterpriseContextStore` does not propagate malformed-file, validation or normal
filesystem failures into the workbench. Public runtime operations return an
`EnterpriseContextResult` with one of:

- `READY`;
- `UNINITIALIZED`;
- `INVALID`; or
- `IO_ERROR`.

Sanitized diagnostic codes/messages explain the state. A corrupt or unsupported
identity descriptor is never silently regenerated, because doing so could change
future RBAC/audit identities. The UI reports the diagnostic and leaves existing
files unchanged.

Persistence additionally:

- rejects identity descriptor symlinks and unsafe managed paths;
- caps each descriptor at 64 KiB before parsing;
- uses UTF-8 properties and schema version `1`;
- writes via a temporary file plus atomic replace where the filesystem supports it;
- rolls back the project descriptor if the paired workspace write fails; and
- never uses project/workspace paths or display names as stable IDs.

## Provisioning and rebinding

Use the Preferences page to select an Eclipse project and initialize the four
names. On first initialization KIDE creates organization, portfolio, project and
workspace IDs. Updating names preserves all IDs.

If the project descriptor is imported into a different Eclipse workspace, KIDE
preserves the organization/portfolio/project IDs and creates a new workspace ID
for the new workspace. This models a new working environment under the same
enterprise project without changing the project's enterprise identity.

## Qualification

E04 is qualified at two levels:

1. `com.kide.enterprise.context.tests` executes malformed/missing/partial state,
   rename, path-move, workspace-rebind, metadata and persistence cases in the
   normal Tycho reactor; and
2. `scripts/smoke_enterprise_context.py` extracts the actual packaged Linux KIDE
   desktop archive and runs `com.kide.enterprise.context.selfcheck` through the
   embedded Java/Equinox runtime. CI requires exit code zero, the explicit success
   marker, and no exception/error signatures in runtime output.

All E01-E03 build, package, release, configuration and secret-handling controls
remain mandatory.
