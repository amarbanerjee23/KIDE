package com.kide.enterprise.configuration;

/** Resolves secret://env/NAME from the process environment. */
public final class EnvironmentSecretResolver implements SecretResolver {
    @Override
    public String provider() {
        return "env";
    }

    @Override
    public SecretValue resolve(String key) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            throw new ConfigurationException("Environment secret is unavailable: " + key);
        }
        return new SecretValue(value.toCharArray());
    }
}
