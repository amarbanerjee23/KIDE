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

## Deliberate boundary

PR87 does **not** enable `POST /api/v1/projects`. The Enterprise REST server
still owns one configured context/repository, so enabling creation at this stage
would produce projects the API cannot correctly route.

The next infrastructure step is to make the REST/service layer registry-aware:
resolve each request by project ID, provision a new slot atomically, persist the
creator's server-side role binding, and then expose project creation in Web.
