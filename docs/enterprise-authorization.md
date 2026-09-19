# Enterprise least-privilege authorization

PR18 adds KIDE's centralized RBAC authorization boundary over the stable E04
organization, portfolio, project and workspace identities.

## Security model

Authorization is deny-by-default. A caller receives access only when an explicit
role binding grants the requested permission on the target stable ID or on an
ancestor stable ID in the same E04 lineage.

Display names and filesystem paths are never authorization identities. Two projects
with the same visible name remain isolated because their E04 project IDs differ.

Explicit DENY bindings override inherited ALLOW bindings. This supports emergency
revocation and narrow exclusions without weakening the default-deny model.

## Roles

The built-in roles are:

- ADMINISTRATOR — all current permissions, including authorization administration
  and audit read;
- ENGINEER — model/workspace read-write, validation, synthesis, evidence and
  LSP/API access, but no authorization administration;
- REVIEWER — read, validation, evidence and connection permissions, no model write;
- VIEWER — read-only project/model/evidence/API access;
- SERVICE_OPERATOR — bounded machine-oriented model/workspace/API/LSP operations,
  with no authorization administration.

Roles are permission sets; the scope of a role is determined by its RoleBinding.
An organization-level binding can inherit to its descendants in that exact lineage.
A project-level binding cannot grant access to a sibling project.

## Central enforcement

AuthorizationService evaluates policy. AuthorizationEnforcer combines an active
AuthenticatedSession with that policy and throws AccessDeniedException when access
is not allowed.

ServerAuthorizationGate is the transport-neutral server boundary for project API,
WebSocket workspace and LSP workspace operations. Future REST/WebSocket/LSP
front ends must invoke this gate before exposing a protected operation.

AuthorizationAffordances projects the same decisions into UI visibility/enabled
state. UI hiding is convenience only and never replaces server enforcement.

The current packaged Xtext language server is a local stdio process and has no
remote authenticated transport. PR22's authenticated WebSocket gateway must call
ServerAuthorizationGate; it must not create an independent browser-side permission
model.

## Policy changes

Authorization policies are revisioned. InMemoryAuthorizationPolicyStore provides
the initial implementation with compare-and-replace semantics. A stale policy
revision fails rather than overwriting a newer policy.

Authorization decisions read the current snapshot on every check. Removing a
binding therefore takes effect for an existing session without forcing re-login.

A durable/server-backed policy store belongs with the versioned service/model
repository boundaries in PR20/PR21. The authorization semantics introduced here
must remain unchanged when storage changes.

## Qualification

com.kide.enterprise.authorization.tests verifies:

- the role/permission matrix;
- deny-by-default;
- organization-to-descendant inheritance;
- explicit-deny precedence;
- same-name/different-ID cross-project isolation;
- live privilege revocation and stale-policy rejection;
- denied REST/WebSocket/LSP boundary access; and
- UI affordance filtering plus independent server enforcement.

The packaged desktop product also executes
com.kide.enterprise.authorization.selfcheck in CI, proving that the shipped
authorization bundle resolves and enforces privilege revocation from customer
product bytes.
