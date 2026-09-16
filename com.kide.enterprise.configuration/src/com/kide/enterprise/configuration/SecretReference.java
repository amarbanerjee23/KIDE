package com.kide.enterprise.configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/** Immutable reference to secret material held outside project/workspace files. */
public final class SecretReference {
    public static final String SCHEME = "secret";
    private static final Pattern PROVIDER = Pattern.compile("[a-z][a-z0-9-]{0,31}");
    private static final Pattern KEY = Pattern.compile("[A-Za-z0-9._-]{1,128}");

    private final String provider;
    private final String key;

    private SecretReference(String provider, String key) {
        this.provider = provider;
        this.key = key;
    }

    public static SecretReference parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        final URI uri;
        try {
            uri = new URI(raw.trim());
        } catch (URISyntaxException e) {
            throw new ConfigurationException("Invalid secret reference syntax", e);
        }
        if (!SCHEME.equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null) {
            throw new ConfigurationException("Secret references must use secret://<provider>/<key>");
        }
        if (uri.getQuery() != null || uri.getFragment() != null || uri.getUserInfo() != null || uri.getPort() != -1) {
            throw new ConfigurationException("Secret references cannot contain credentials, query strings, fragments or ports");
        }
        String provider = uri.getHost().toLowerCase(Locale.ROOT);
        String path = uri.getPath();
        String key = path == null ? "" : path.replaceFirst("^/", "");
        if (!PROVIDER.matcher(provider).matches() || !KEY.matcher(key).matches()) {
            throw new ConfigurationException("Invalid secret provider or key");
        }
        return new SecretReference(provider, key);
    }

    public static boolean isReference(String raw) {
        if (raw == null || !raw.trim().toLowerCase(Locale.ROOT).startsWith(SCHEME + "://")) {
            return false;
        }
        try {
            parse(raw);
            return true;
        } catch (ConfigurationException e) {
            return false;
        }
    }

    public String provider() {
        return provider;
    }

    public String key() {
        return key;
    }

    @Override
    public String toString() {
        return SCHEME + "://" + provider + "/" + key;
    }
}
