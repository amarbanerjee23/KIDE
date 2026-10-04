# Runtime compatibility handshake

KIDE has one engineering implementation consumed by the Eclipse desktop product
and the Web/backend product. PR82 adds an explicit compatibility handshake so a
client cannot silently use engineering services from an incompatible runtime.

## Endpoint

The enterprise backend exposes unauthenticated:

`GET /api/v1/version`

The response contains:

- `apiVersion`;
- `engineeringCompatibilityLevel`;
- `projectSchemaVersion`;
- `sharedKernelSchemaVersion`;
- `productLine`;
- `productVersion`;
- `buildId`.

Health and compatibility are deliberately separate. A runtime can be reachable
and healthy while still being incompatible with the current client.

## Fail-closed fields

The canonical policy is `product/runtime-compatibility.json`.

Web requires an exact match for:

1. API version;
2. engineering compatibility level;
3. project schema version;
4. shared service-kernel schema version.

If any field differs, Web does not request the project list and therefore does
not attach hosted Xtext LSP, GLSP, collaboration, synthesis, reconfiguration or
generation services. An already-open hosted project is disconnected. A local
browser ZIP archive remains available because it does not use the hosted
engineering runtime.

`productVersion` and `buildId` are diagnostic/provenance fields. They may
differ between compatible patch builds without blocking a connection.

## Build identity

Packaged Eclipse products derive `productVersion` from the API bundle version.
Cloud Run also exposes its Docker build qualifier as `KIDE_BUILD_ID`, allowing
the settings panel and deployment qualification to identify the exact backend
build being used.

## Qualification

PR82 is fail-closed across all product surfaces:

- the packaged desktop API self-check validates the shared compatibility metadata;
- the packaged enterprise backend self-check validates the unauthenticated
  `/api/v1/version` response;
- Web unit tests validate exact-match behavior;
- Playwright proves an incompatible runtime causes zero project-discovery
  requests;
- Cloud Run qualification calls the live `/api/v1/version` endpoint;
- `scripts/verify_runtime_compatibility.py` ensures Java constants, Web
  expectations, project schema version, shared-kernel schema version, packaged
  smoke markers and Cloud Run wiring remain aligned.

Changing one of the exact-match compatibility levels is therefore an explicit
product compatibility decision and must update the canonical contract and all
corresponding migration/adapter qualifications in the same change.
