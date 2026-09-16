package com.kide.enterprise.configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

/** Resolves and manages secret://secure/ALIAS using Equinox encrypted secure storage. */
public final class SecureStorageSecretResolver implements SecretResolver {
    private static final String NODE = "com.kide.enterprise.configuration/secrets";
    private static final Pattern ALIAS = Pattern.compile("[A-Za-z0-9._-]{1,128}");

    private final ISecurePreferences root;

    public SecureStorageSecretResolver() {
        this(SecurePreferencesFactory.getDefault());
    }

    SecureStorageSecretResolver(ISecurePreferences root) {
        this.root = root;
    }

    @Override
    public String provider() {
        return "secure";
    }

    public void put(String alias, char[] secret) {
        validateAlias(alias);
        if (secret == null || secret.length == 0) {
            throw new ConfigurationException("Secret material must not be empty");
        }
        String materialized = new String(secret);
        try {
            root.node(NODE).put(alias, materialized, true);
            root.node(NODE).flush();
        } catch (StorageException | IOException e) {
            throw new ConfigurationException("Unable to store encrypted secret alias: " + alias, e);
        } finally {
            materialized = null;
        }
    }

    public void remove(String alias) {
        validateAlias(alias);
        try {
            if (root.nodeExists(NODE)) {
                ISecurePreferences node = root.node(NODE);
                node.remove(alias);
                node.flush();
            }
        } catch (IOException e) {
            throw new ConfigurationException("Unable to remove encrypted secret alias: " + alias, e);
        }
    }

    @Override
    public SecretValue resolve(String alias) {
        validateAlias(alias);
        try {
            if (!root.nodeExists(NODE)) {
                throw new ConfigurationException("Secure secret alias is unavailable: " + alias);
            }
            String stored = root.node(NODE).get(alias, null);
            if (stored == null) {
                throw new ConfigurationException("Secure secret alias is unavailable: " + alias);
            }
            char[] chars = stored.toCharArray();
            try {
                return new SecretValue(chars);
            } finally {
                Arrays.fill(chars, '\0');
                stored = null;
            }
        } catch (StorageException e) {
            throw new ConfigurationException("Unable to read encrypted secret alias: " + alias, e);
        }
    }

    private static void validateAlias(String alias) {
        if (alias == null || !ALIAS.matcher(alias).matches()) {
            throw new ConfigurationException("Invalid secure secret alias");
        }
    }
}
