package com.kide.enterprise.api;

import java.io.InputStream;
import java.util.jar.Manifest;

public final class RuntimeCompatibility {
    public static final String API_VERSION = "v1";
    public static final int ENGINEERING_COMPATIBILITY_LEVEL = 1;
    public static final int PROJECT_SCHEMA_VERSION = 1;
    public static final int SHARED_KERNEL_SCHEMA_VERSION = 1;
    public static final String PRODUCT_LINE = "1.0";

    private RuntimeCompatibility() { }

    public static Info current() {
        String productVersion = productVersion();
        return new Info(
                API_VERSION,
                ENGINEERING_COMPATIBILITY_LEVEL,
                PROJECT_SCHEMA_VERSION,
                SHARED_KERNEL_SCHEMA_VERSION,
                PRODUCT_LINE,
                productVersion,
                buildId(productVersion));
    }

    private static String productVersion() {
        String explicit = firstNonBlank(
                systemProperty("kide.product.version"),
                environment("KIDE_PRODUCT_VERSION"));
        if (explicit != null) return explicit;

        try (InputStream input =
                RuntimeCompatibility.class.getResourceAsStream("/META-INF/MANIFEST.MF")) {
            if (input != null) {
                String version = new Manifest(input)
                        .getMainAttributes()
                        .getValue("Bundle-Version");
                if (version != null && !version.isBlank()) return version.trim();
            }
        } catch (Exception ignored) {
            // A missing manifest is valid in source-level/unit execution.
        }
        return PRODUCT_LINE + ".0";
    }

    private static String buildId(String productVersion) {
        String explicit = firstNonBlank(
                systemProperty("kide.build.id"),
                environment("KIDE_BUILD_ID"),
                environment("GITHUB_SHA"));
        return explicit == null ? productVersion : explicit;
    }

    private static String systemProperty(String name) {
        try {
            return System.getProperty(name);
        } catch (SecurityException ignored) {
            return null;
        }
    }

    private static String environment(String name) {
        try {
            return System.getenv(name);
        } catch (SecurityException ignored) {
            return null;
        }
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) return value.trim();
        }
        return null;
    }

    public record Info(
            String apiVersion,
            int engineeringCompatibilityLevel,
            int projectSchemaVersion,
            int sharedKernelSchemaVersion,
            String productLine,
            String productVersion,
            String buildId) { }
}
