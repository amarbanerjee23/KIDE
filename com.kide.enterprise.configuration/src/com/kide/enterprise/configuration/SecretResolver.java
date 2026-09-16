package com.kide.enterprise.configuration;

/** Resolves one provider namespace without exposing secrets through configuration snapshots. */
public interface SecretResolver {
    String provider();

    SecretValue resolve(String key);
}
