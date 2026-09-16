# Enterprise configuration and secrets

KIDE enterprise configuration is provided by the `com.kide.enterprise.configuration`
bundle. The contract deliberately separates configuration from secret material so a
workspace or project can be copied, committed, backed up, or inspected without
carrying credentials with it.

## Layer order

Configuration is merged deterministically from lowest to highest precedence:

1. bundled defaults: `defaults/enterprise.properties`;
2. installation: `<KIDE install>/configuration/kide/enterprise.properties`;
3. user: `~/.kide/enterprise.properties`;
4. workspace: `<workspace>/.metadata/.plugins/com.kide.enterprise.configuration/enterprise.properties`;
5. project: `<project>/.kide/enterprise.properties`.

A higher layer replaces a lower-layer value with the same key. `ConfigurationValue`
retains the winning layer and source path so diagnostics and future policy/audit
features can explain where a setting came from.

The resolver uses the Eclipse `osgi.install.area` and `osgi.instance.area` locations.
Only local file locations are accepted; non-file Eclipse area URIs fail closed.

## Secret references

Secret material is not a configuration value. Sensitive keys such as keys ending in
`password`, `token`, `apiKey`, `clientSecret`, `privateKey`, `accessKey`,
`credential`, or `secret` must contain a reference of this form:

```properties
provider.apiKey=secret://secure/primary-provider
service.password=secret://env/KIDE_SERVICE_PASSWORD
```

Two providers are enabled by default:

- `secret://secure/<alias>` uses Eclipse/Equinox secure storage and requests encrypted
  persistence. This is the default for secrets entered or provisioned on a desktop.
- `secret://env/<NAME>` reads a process environment variable. This supports managed
  launchers, CI, containers and enterprise endpoint-management systems without
  writing the value to a project or workspace.

Unknown providers fail closed. References cannot contain user information,
passwords, query strings, fragments, ports, path traversal or multi-segment keys.
Aliases are restricted to `A-Z`, `a-z`, `0-9`, `.`, `_` and `-`.

## Provisioning encrypted aliases in Eclipse

Open **Window → Preferences → KIDE Enterprise Secrets**. Enter an alias and the
secret value, then choose **Save encrypted secret**. The value is passed to Equinox
secure storage with encryption enabled and the password field is cleared immediately
after provisioning. Project/workspace configuration should reference only the alias,
for example `secret://secure/primary-provider`.

The same page can remove an alias. It intentionally does not display existing secret
values. For centrally managed credentials, operators can avoid local provisioning
entirely and launch KIDE with an environment variable referenced through
`secret://env/<NAME>`.

## API contract

Use `EnterpriseConfiguration.system(projectRoot)` for normal Eclipse runtime use.
`get(key, defaultValue)` returns only ordinary configuration. It refuses any
`secret://` reference so callers cannot accidentally treat a credential as a normal
string. Use `getSecret(key)` instead; it returns a `SecretValue`, which is
`AutoCloseable`, renders as `[REDACTED]`, and overwrites its internal character array
when closed.

Consumers should use try-with-resources and close a secret immediately after the
credential has been passed to the target client/API. Do not log, persist, cache, add
to exceptions, or convert secret values to application configuration.

`SecureStorageSecretResolver.put(alias, char[])` and `remove(alias)` provide the
same provisioning primitive used by the preference page. Equinox secure storage
itself accepts strings, so the resolver necessarily materializes a short-lived string
at that boundary; KIDE does not retain it or expose it through snapshots.

## Failure behavior

KIDE fails configuration loading when:

- a configured source exists but is not a readable regular file;
- a sensitive key contains a literal instead of a valid secret reference;
- a secret provider is unsupported;
- the requested secure alias/environment variable is unavailable; or
- an Eclipse area uses an unsupported URI scheme.

Error messages identify the configuration key, provider/alias, layer or source path
needed to diagnose the problem, but never include resolved secret material or a
rejected literal value.

## Qualification

`com.kide.enterprise.configuration.tests` runs as an Eclipse test plug-in during the
normal Tycho `verify` reactor. It covers precedence/provenance, literal-secret
rejection without value disclosure, secret-only API resolution, redaction/close
behavior and strict reference parsing. E01 portable-product qualification and E02
workflow/release controls remain mandatory and unchanged.
