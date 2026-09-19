# Enterprise audit and engineering event model

PR19 introduces KIDE's client-neutral append-oriented audit event contract.

## Event schema

Audit schema version 1 records:

- monotonic sequence number;
- immutable event UUID and timestamp;
- actor principal ID;
- client identity;
- action;
- resource type and resource ID;
- outcome (SUCCESS, FAILURE or DENIED);
- correlation ID and optional causation ID;
- stable E04 project ID;
- project/model revision;
- structured attributes;
- previous-record hash; and
- current SHA-256 hash.

The schema version is explicit. Unknown future schema versions fail closed rather
than being silently interpreted as version 1.

## Redaction

AuditRedactor is applied both when a draft is created and when the immutable
AuditEvent is constructed. Keys indicating tokens, passwords, secrets, credentials,
authorization headers, cookies, API keys, client secrets, private keys, access keys
or session IDs are stored as [REDACTED].

Free-form exception objects and stack traces are not part of the audit schema.
Failure events carry bounded reason codes instead. This avoids turning an audit
trail into an accidental credential or sensitive diagnostic store.

## Append-only integrity

InMemoryAuditLedger is the first append-only implementation. Appends are serialized
under one lock, giving every committed event exactly one sequence number even when
multiple threads append concurrently.

Each record hash covers the prior hash plus a deterministic length-prefixed
canonical representation of all semantically relevant event fields. Reordering,
insertion, removal or mutation therefore breaks verification. The genesis record
uses a fixed all-zero SHA-256 predecessor.

The public ledger surface provides append, immutable snapshot and verification.
There is intentionally no update or delete operation.

A durable server/file/database implementation may replace the in-memory store in
the later service/repository work, but it must preserve this schema, sequencing,
redaction and integrity contract.

## Correlation and causation

A correlation UUID groups operations belonging to one engineering/request flow.
Causation optionally points to the UUID of the event that directly triggered the
current operation. This is intentionally separate from project revision: revision
answers which engineering state was operated on; correlation/causation answer how
the operation arose.

## Qualification

com.kide.enterprise.audit.tests covers:

- schema compatibility;
- actor/client/project/revision capture;
- correlation and causation;
- redaction/no-secret behavior;
- SHA-256 tamper detection;
- concurrent append ordering;
- failure-event reason codes; and
- field-size/format bounds.

The packaged desktop product executes com.kide.enterprise.audit.selfcheck in CI.
This proves the shipped audit bundle starts and performs hash-chain, redaction and
failure-event checks from customer product bytes.
