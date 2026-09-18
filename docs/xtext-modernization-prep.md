# Xtext modernization preparation

PR15 removes KIDE-owned dependencies on legacy Xtext UI/web infrastructure without changing the Xtext, Eclipse, Java or Sirius platform versions.

## Generation contract

The machine-readable contract is `product/xtext-generation.json`. The exact generator-owned byte baseline is `product/xtext-generated-baseline.json`.

For all five production DSLs:

- MWE2 generation inputs are UTF-8 with LF output;
- `src/` is hand-written code;
- `src-gen/` and `xtend-gen/` are generator-owned snapshots;
- Eclipse/PDE `bin/` output is not source and is not committed;
- UI activators are KIDE-owned source in `src/`, not generator-owned source;
- KIDE bundles do not directly import Log4j 1;
- the Activity DSL no longer requests Xtext-Web/Ace/servlet generation.

This PR intentionally keeps Xtext 2.25.0, Java 11, Tycho 4.0.8 and the current Sirius line. PR16 owns the platform upgrade.

## Regeneration procedure

Run the existing aggregate MWE2 workflow `com.dsep.dsl.generator/src/GenerateDsls.mwe2` from the pinned PR15 target platform, then run:

```bash
python3 scripts/normalize_xtext_generation.py
python3 scripts/verify_xtext_modernization.py
python3 scripts/verify_product_truth.py
python3 scripts/verify_lsp_parity_matrix.py
mvn -B -ntp clean verify
```

The normalizer removes only generator-owned UI activators, legacy Log4j-1 metadata and local Eclipse `bin/` output. It does not modify parsers, linkers, scopers, validators, serializers, formatters, IDE services or hand-written semantic code.

The PR12 golden corpus, PR13 LSP parity checks and PR14 schema migration checks remain cumulative. If a deliberate regeneration changes generator-owned bytes, review the semantic diff and update `product/xtext-generated-baseline.json` in the same PR only after those gates remain green.

## Why the activators are hand-written

The legacy generator template emits Log4j-1-based lifecycle error logging. KIDE needs the lifecycle class but not that logging API. The activators retain the same injector and lifecycle behavior while logging failures through Eclipse `ILog`/`Status`.

No parser, linker, scoper, validator, formatter, serializer or language-server behavior is reimplemented here.
