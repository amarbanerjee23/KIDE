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

`com.kide.languageserver` is a headless Equinox application. JSON-RPC/LSP uses standard input and standard output, which is the transport expected by a future Theia language client.

The product still materializes the normal Eclipse native launchers, but the supported server/container entrypoint on Linux is `kide-languageserver-headless`. That wrapper starts Equinox directly through the packaged `org.eclipse.equinox.launcher` JAR using KIDE's embedded JustJ Java runtime. It deliberately bypasses the GTK native launcher, so it does not require X11, Wayland, Xvfb, or a desktop session. CI removes `DISPLAY` and `WAYLAND_DISPLAY` before starting it to enforce that contract.

The native Windows launcher remains `kide-languageserver.exe`. Native launchers can still be used on interactive desktop hosts; automation on Linux should use the display-free wrapper. With no explicit application argument the wrapper starts `com.kide.languageserver.application`. When an explicit `-application` is supplied, the wrapper preserves it instead of prepending the default; packaged gateway/API self-checks and future headless service applications therefore execute through the same embedded-JRE entrypoint.

Xtext's server implementation owns document synchronization, diagnostics, workspace folders, completion/navigation services exposed by each language, and the LSP shutdown/exit lifecycle. KIDE adds deterministic multi-language registration and distribution packaging around that implementation.

## Qualification

Pull-request CI materializes the language-server product independently from the desktop KIDE product and then performs a protocol smoke test. The smoke test:

1. requires the packaged Linux `kide-languageserver-headless` wrapper to exist and be executable;
2. removes X11/Wayland display variables and starts the server through its embedded Java runtime;
3. initializes it with a workspace folder;
4. opens an intentionally invalid document for every production extension;
5. requires non-empty diagnostics from every language provider; and
6. performs a clean LSP `shutdown` / `exit` sequence.

This gate proves that the packaged product can serve every production DSL without Eclipse UI bundles or a graphical display. W03 will add deeper Eclipse-versus-LSP feature parity tests.
