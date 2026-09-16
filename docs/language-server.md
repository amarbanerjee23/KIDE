# KIDE Language Server

W02 introduces one headless Language Server Protocol process for the production KIDE Xtext languages. It does not create a second parser, validator, linker, scope provider, completion engine, or generator.

## Production language set

The current Maven/Tycho reactor contains five language IDE bundles, and W02 registers exactly those five:

| Language | Extension | Existing Xtext setup |
| --- | --- | --- |
| DML | `.dml` | `com.dml.dsl.ide.DmlIdeSetup` |
| Capability | `.cap` | `com.capability.ide.CapabilityIdeSetup` |
| MNC | `.mncspec` | `com.mncml.dsl.ide.MncIdeSetup` |
| Operation | `.op` | `com.operation.dsl.ide.OperationIdeSetup` |
| Activity | `.activity` | `com.smr.activity.dsl.ide.ActivityDiagramIdeSetup` |

The registry is constructed explicitly from these generated `IdeSetup` classes because ordinary Java `ServiceLoader` discovery is not a reliable cross-bundle contract in an Equinox product. Each setup still creates the existing generated runtime/IDE injector; the language server only aggregates them.

## Runtime contract

`com.kide.languageserver` is a headless Equinox application. The packaged launcher is `kide-languageserver` (`kide-languageserver.exe` on Windows). JSON-RPC/LSP uses standard input and standard output, which is the transport expected by a future Theia language client.

Xtext's server implementation owns document synchronization, diagnostics, workspace folders, completion/navigation services exposed by each language, and the LSP shutdown/exit lifecycle. KIDE adds deterministic multi-language registration and distribution packaging around that implementation.

## Qualification

Pull-request CI materializes the language-server product independently from the desktop KIDE product and then performs a protocol smoke test. The smoke test:

1. starts the packaged Linux launcher;
2. initializes it with a workspace folder;
3. opens an intentionally invalid document for every production extension;
4. requires non-empty diagnostics from every language provider; and
5. performs a clean LSP `shutdown` / `exit` sequence.

This gate proves that the packaged product can serve every production DSL without Eclipse UI bundles. W03 will add deeper Eclipse-versus-LSP feature parity tests.
