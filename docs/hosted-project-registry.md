# Hosted project registry

PR87 introduces the persistent discovery boundary required before KIDE can
safely provision more than one hosted engineering project.

## Layout

When `KIDE_HOSTED_PROJECTS_ROOT` is configured, the LSP and GLSP gateways
discover projects from:

```
<registry-root>/<project-uuid>/project/
<registry-root>/<project-uuid>/workspace/
```

The existing enterprise context descriptors inside the project/workspace roots
remain authoritative. The directory name is only a stable registry slot and
must equal the UUID portion of the persisted project identity.

Registry entries are direct real directories only. Symbolic links are refused.
Project and workspace identities must be unique, malformed contexts fail
closed, and the registry is capped at 512 projects.

## Dynamic gateway behavior

The registry-backed gateway catalog resolves the requested workspace on every
handshake. A project slot that is atomically moved into the registry therefore
becomes available to both Xtext LSP and GLSP without restarting either process.

Gateway role bindings are validated against every enterprise context currently
present in the registry. A binding whose scope does not belong to a registered
context is rejected.

## Backward compatibility

If `KIDE_HOSTED_PROJECTS_ROOT` is absent, LSP and GLSP keep using the current
single-project `KIDE_*_WORKSPACE_ROOT` / `KIDE_*_PROJECT_ROOT` configuration.
PR87 intentionally does not migrate the current Cloud Run data layout.

## PR88: registry-aware REST and owned project creation

When `KIDE_HOSTED_PROJECTS_ROOT` is set on the REST API, LSP and GLSP gateway
processes, all three discover the same persistent project registrations.
REST resolves every `/api/v1/projects/{projectId}/...` operation through a
per-project service runtime: canonical model repository, collaboration,
knowledge, synthesis and generation all use the selected project's own
directory. A project ID never selects the legacy fallback repository in
registry mode.

Authenticated `GET /api/v1/projects` returns only authorized projects.
Authenticated `POST /api/v1/projects` accepts `{"displayName":"..."}`
(maximum 128 characters) and creates a project with stable project/workspace
identities. Each creator is limited to 10 hosted projects; the registry has
a global limit of 512. The creator receives a project-scoped ADMINISTRATOR grant
persisted under `project/.kide/enterprise-context.properties` as
`project.meta.hosted.ownerPrincipalId`. LSP, GLSP and REST re-evaluate
those persisted grants on each authorized request, including after restart.
The browser exposes creation only if the backend health contract advertises
`projectCreationEnabled=true`.

Creation first writes enterprise project and workspace descriptors in a sibling
staging directory, then atomically renames the complete slot into the live
registry. On a filesystem without `ATOMIC_MOVE`, creation fails closed;
a partially initialized slot is never published. An unregistered sibling
staging directory left by a crash may require operator cleanup.

## Production deployment boundary

**Do not enable project creation on Cloud Run's existing Cloud Storage FUSE
mount without proving atomic rename support and restore guarantees.**
Current deployment remains in legacy single-project mode unless
`KIDE_HOSTED_PROJECTS_ROOT` is explicitly set. No filesystem migration is
performed implicitly; the legacy project must be migrated separately if it
needs to appear in registry mode. Registry-enabled gateways can start with
zero projects and then discover projects created by REST.

In registry mode the deployed LSP and GLSP gateway processes must not be
given the legacy project's static `KIDE_*_ROLE_BINDINGS` unless that identity
has been registered in the new catalog. Creator grants are durable and
read directly from registered project metadata.

This change is not an operational HA, backup/restore, or production capacity
qualification. It should be deployed first on a staging filesystem that
supports atomic rename; use the live staging acceptance tests before a pilot.
