package com.kide.enterprise.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

/**
 * Layered enterprise configuration with provenance and reference-only secrets.
 * Precedence is defaults &lt; installation &lt; user &lt; workspace &lt; project.
 */
public final class EnterpriseConfiguration {
    private static final String DEFAULTS_RESOURCE = "/defaults/enterprise.properties";
    private static final Set<String> SENSITIVE_SUFFIXES = Set.of(
            "secret", "password", "passwd", "token", "credential", "credentials",
            "apikey", "api_key", "privatekey", "private_key", "clientsecret",
            "client_secret", "accesskey", "access_key");

    private final Map<String, ConfigurationValue> values;
    private final SecretResolverRegistry secretResolvers;

    private EnterpriseConfiguration(Map<String, ConfigurationValue> values, SecretResolverRegistry secretResolvers) {
        this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
        this.secretResolvers = Objects.requireNonNull(secretResolvers, "secretResolvers");
    }

    /** Loads the standard Eclipse installation/user/workspace/project layers. */
    public static EnterpriseConfiguration system(Path projectRoot) {
        Map<String, String> defaults = loadBundledDefaults();
        Path install = areaPath("osgi.install.area")
                .map(path -> path.resolve("configuration").resolve("kide").resolve("enterprise.properties"))
                .orElse(null);
        Path user = propertyPath("user.home")
                .map(path -> path.resolve(".kide").resolve("enterprise.properties"))
                .orElse(null);
        Path workspace = areaPath("osgi.instance.area")
                .map(path -> path.resolve(".metadata").resolve(".plugins")
                        .resolve("com.kide.enterprise.configuration").resolve("enterprise.properties"))
                .orElse(null);
        Path project = projectRoot == null ? null : projectRoot.toAbsolutePath().normalize()
                .resolve(".kide").resolve("enterprise.properties");
        return load(defaults, install, user, workspace, project, SecretResolverRegistry.systemDefault());
    }

    /** Public deterministic loader used by integrations and qualification tests. */
    public static EnterpriseConfiguration load(
            Map<String, String> defaults,
            Path installationConfig,
            Path userConfig,
            Path workspaceConfig,
            Path projectConfig,
            SecretResolverRegistry secretResolvers) {
        Map<String, ConfigurationValue> merged = new LinkedHashMap<>();
        mergeMap(merged, defaults == null ? Collections.emptyMap() : defaults, ConfigurationLayer.DEFAULTS, null);
        mergeFile(merged, installationConfig, ConfigurationLayer.INSTALLATION);
        mergeFile(merged, userConfig, ConfigurationLayer.USER);
        mergeFile(merged, workspaceConfig, ConfigurationLayer.WORKSPACE);
        mergeFile(merged, projectConfig, ConfigurationLayer.PROJECT);
        return new EnterpriseConfiguration(merged, secretResolvers);
    }

    public Optional<ConfigurationValue> find(String key) {
        return Optional.ofNullable(values.get(requireKey(key)));
    }

    /** Returns only non-secret material. Secret references must use getSecret(). */
    public String get(String key, String defaultValue) {
        ConfigurationValue value = values.get(requireKey(key));
        if (value == null) {
            return defaultValue;
        }
        if (SecretReference.isReference(value.value())) {
            throw new ConfigurationException("Configuration key references secret material; use getSecret(): " + key);
        }
        return value.value();
    }

    /** Resolves a secret reference into a short-lived, wipeable value. */
    public SecretValue getSecret(String key) {
        ConfigurationValue value = values.get(requireKey(key));
        if (value == null) {
            throw new ConfigurationException("Missing secret configuration key: " + key);
        }
        final SecretReference reference;
        try {
            reference = SecretReference.parse(value.value());
        } catch (ConfigurationException e) {
            throw new ConfigurationException("Configuration key must contain a secret reference: " + key, e);
        }
        return secretResolvers.resolve(reference);
    }

    /** Safe snapshot: values may contain secret references but never resolved secret material. */
    public Map<String, ConfigurationValue> snapshot() {
        return values;
    }

    private static void mergeFile(Map<String, ConfigurationValue> merged, Path file, ConfigurationLayer layer) {
        if (file == null || !Files.exists(file)) {
            return;
        }
        if (!Files.isRegularFile(file) || !Files.isReadable(file)) {
            throw new ConfigurationException("Configuration source is not a readable regular file: " + file);
        }
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(file)) {
            properties.load(input);
        } catch (IOException e) {
            throw new ConfigurationException("Unable to read configuration source: " + file, e);
        }
        Map<String, String> source = new LinkedHashMap<>();
        for (String key : properties.stringPropertyNames()) {
            source.put(key, properties.getProperty(key));
        }
        mergeMap(merged, source, layer, file.toAbsolutePath().normalize());
    }

    private static void mergeMap(Map<String, ConfigurationValue> merged, Map<String, String> source,
            ConfigurationLayer layer, Path path) {
        for (Map.Entry<String, String> entry : source.entrySet()) {
            String key = requireKey(entry.getKey());
            String value = Objects.requireNonNull(entry.getValue(), "Configuration value for " + key).trim();
            validateSecretPolicy(key, value, layer, path);
            merged.put(key, new ConfigurationValue(key, value, layer, path));
        }
    }

    private static void validateSecretPolicy(String key, String value, ConfigurationLayer layer, Path path) {
        if (!isSensitiveKey(key)) {
            return;
        }
        if (!SecretReference.isReference(value)) {
            String source = path == null ? layer.name().toLowerCase(Locale.ROOT) : path.toString();
            throw new ConfigurationException("Sensitive configuration key must use secret:// reference: "
                    + key + " (source=" + source + ")");
        }
    }

    static boolean isSensitiveKey(String key) {
        String normalized = key.trim().toLowerCase(Locale.ROOT);
        for (String suffix : SENSITIVE_SUFFIXES) {
            if (normalized.equals(suffix) || normalized.endsWith("." + suffix)
                    || normalized.endsWith("-" + suffix) || normalized.endsWith("_" + suffix)) {
                return true;
            }
        }
        return false;
    }

    private static String requireKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new ConfigurationException("Configuration key must not be empty");
        }
        return key.trim();
    }

    private static Map<String, String> loadBundledDefaults() {
        Properties properties = new Properties();
        try (InputStream input = EnterpriseConfiguration.class.getResourceAsStream(DEFAULTS_RESOURCE)) {
            if (input == null) {
                throw new ConfigurationException("Bundled enterprise configuration defaults are missing");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new ConfigurationException("Unable to load bundled enterprise configuration defaults", e);
        }
        Map<String, String> defaults = new LinkedHashMap<>();
        for (String key : properties.stringPropertyNames()) {
            defaults.put(key, properties.getProperty(key));
        }
        return defaults;
    }

    private static Optional<Path> propertyPath(String property) {
        String raw = System.getProperty(property);
        return raw == null || raw.trim().isEmpty() ? Optional.empty() : Optional.of(Paths.get(raw).toAbsolutePath().normalize());
    }

    private static Optional<Path> areaPath(String property) {
        String raw = System.getProperty(property);
        if (raw == null || raw.trim().isEmpty()) {
            return Optional.empty();
        }
        String trimmed = raw.trim();
        try {
            URI uri = new URI(trimmed);
            if (uri.getScheme() == null) {
                return Optional.of(Paths.get(trimmed).toAbsolutePath().normalize());
            }
            if (!"file".equalsIgnoreCase(uri.getScheme())) {
                throw new ConfigurationException("Unsupported Eclipse area URI scheme for " + property);
            }
            return Optional.of(Paths.get(uri).toAbsolutePath().normalize());
        } catch (URISyntaxException | IllegalArgumentException e) {
            throw new ConfigurationException("Invalid Eclipse area for " + property, e);
        }
    }
}
