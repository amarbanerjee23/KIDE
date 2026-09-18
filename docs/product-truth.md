# KIDE Product Truth and Golden Corpus

PR12 establishes a machine-readable contract for the product that later modernization and web work must preserve.

## Authoritative language registry

`product/languages.json` is the build-time authority for the five production textual DSLs. Each entry records:

- the file extension and LSP language ID;
- the Xtext grammar;
- runtime, IDE and UI bundles;
- the exact generated `IdeSetup` used by the packaged language server; and
- valid and invalid golden fixtures.

`scripts/verify_product_truth.py` fails CI if the registry drifts from the packaged LSP registration, the desktop feature, fixture files, grammar files, bundle directories, or the generated language table in `Readme.md`.

The runtime language semantics still live in the Xtext bundles. The JSON registry does **not** duplicate parsers, scopers, linkers or validators.

## Golden language corpus

`qualification/golden/languages` contains a valid and invalid fixture for every production DSL:

1. DML;
2. Operation;
3. MNC specification;
4. Capability; and
5. Activity.

The packaged Linux language-server product is launched from its built distribution. CI opens the valid corpus in dependency order so cross-language references are resolved through the real Xtext workspace/index, and requires no LSP error diagnostics. It then opens every invalid fixture and requires an error diagnostic.

This is intentionally stronger than checking that a file extension is registered.

## Activity to MNC golden transformation

`com.smr.activity.activity2mnc.tests` is a Tycho/JUnit test bundle. It constructs a minimal Activity EMF model, invokes the production `GenerateMnCDesignFromActivityDiagram` transformer, normalizes the resulting MNC model and compares it with:

`com.smr.activity.activity2mnc.tests/golden/activity-to-mnc.snapshot`

Any intentional transformation-semantic change must update the implementation, test rationale and snapshot together. An incidental toolchain or Xtext upgrade must leave the snapshot unchanged.

## CI contract

Every PR retains the earlier KIDE gates and additionally runs:

```text
repository governance tests
  -> product truth verification
  -> full Tycho reactor including Activity->MNC golden test
  -> four-platform desktop product qualification
  -> packaged enterprise-context runtime self-check
  -> packaged five-DSL valid/invalid LSP golden corpus
```

Expected model errors remain diagnostics. Product startup, server startup or qualification must not fail with uncaught application exceptions.
