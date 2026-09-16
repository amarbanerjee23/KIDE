package com.kide.enterprise.configuration;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** Explicit registry of approved secret providers. Unknown providers fail closed. */
public final class SecretResolverRegistry {
    private final Map<String, SecretResolver> resolvers = new LinkedHashMap<>();

    public SecretResolverRegistry() {
    }

    public static SecretResolverRegistry systemDefault() {
        return new SecretResolverRegistry()
                .register(new SecureStorageSecretResolver())
                .register(new EnvironmentSecretResolver());
    }

    public synchronized SecretResolverRegistry register(SecretResolver resolver) {
        Objects.requireNonNull(resolver, "resolver");
        String provider = Objects.requireNonNull(resolver.provider(), "provider").toLowerCase(Locale.ROOT);
        SecretResolver previous = resolvers.putIfAbsent(provider, resolver);
        if (previous != null) {
            throw new ConfigurationException("Duplicate secret provider: " + provider);
        }
        return this;
    }

    public synchronized SecretValue resolve(SecretReference reference) {
        Objects.requireNonNull(reference, "reference");
        SecretResolver resolver = resolvers.get(reference.provider());
        if (resolver == null) {
            throw new ConfigurationException("Unsupported secret provider: " + reference.provider());
        }
        return resolver.resolve(reference.key());
    }
}
