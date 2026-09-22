# KIDE Enterprise API v1 Contract

PR20 defines the stable client-neutral service boundary for KIDE. It does not
introduce a network server; it defines the contract that later REST/WebSocket
services must implement.

## Versioning

All v1 routes are rooted at `/api/v1`. The v1 contract covers:

- projects;
- models;
- knowledge queries;
- deterministic synthesis and supervisory reconfiguration;
- evidence; and
- health.

Operation IDs, HTTP methods, route templates, request schemas and response schemas
are registered centrally in `ApiContractRegistry`.

## Request identity

Every request receives a UUID request ID through `ApiRequestContext`. Services
must propagate that ID into structured errors, audit events and later observability
traces. Clients may display the request ID for support, but it is not an
authorization credential.

## Structured errors

Client-visible failures use `ApiErrorEnvelope`:

- API version;
- request ID;
- typed `ApiErrorCode`;
- bounded client-safe message; and
- bounded structured details.

Raw exception messages, stack traces, token/secret/password/credential fields and
implementation class names are not client contracts. `ApiExceptionMapper` maps
known failure categories to stable error codes and generic safe text.

Unknown response fields are ignored by the v1 decoder, allowing additive evolution.

## Compatibility policy

Within v1, the following are treated as breaking changes:

- removing an existing operation;
- changing an existing operation's method, path, request schema or response schema;
- removing an existing schema or field; or
- making a previously optional field required.

Adding a new operation, adding a new schema, or adding an optional field is
compatible. Breaking contract changes require a new API version.

PR31 adds optional `workspaceId` to the `Project` response. It is the stable E04
workspace identity used by authorized clients to select the corresponding LSP
gateway workspace. It is not a filesystem path or authentication credential.

## OpenAPI

`OpenApiV1.generateJson()` produces a deterministic OpenAPI 3.1 document from the
same route and schema registries used by the runtime. Shared paths are grouped,
path-template parameters are declared, request/response schema references are
validated, and all object schemas allow unknown fields for forward compatibility.

`OpenApiV1.validateContract()` fails on duplicate operation IDs/routes, missing
schema references, routes outside `/api/v1`, malformed path parameter declarations
or malformed generated contract structure.

## Qualification

The PR20 test bundle verifies the six required domain surfaces, deterministic
OpenAPI generation, shared-path grouping, templated path parameters, schema
references, request-ID propagation, safe structured errors, authorization failure
mapping, unknown-field compatibility and non-breaking schema/route evolution.

The packaged desktop product executes
`com.kide.enterprise.api.selfcheck` in CI so the contract bundle is qualified from
the same customer product bytes that will be released.

## PR37 reconfiguration

`POST /api/v1/projects/{projectId}/reconfiguration` uses the same authenticated
`MODEL_SYNTHESIZE` boundary as synthesis. The request contains the current Activity model
ID/ETag, a reconfiguration cause, and previous requirement/resource IDs only. Current
requirements, resources, contracts and migration decisions are server-derived.
