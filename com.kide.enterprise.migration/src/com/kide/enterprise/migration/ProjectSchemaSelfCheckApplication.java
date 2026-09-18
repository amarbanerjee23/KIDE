package com.kide.enterprise.migration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Properties;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public final class ProjectSchemaSelfCheckApplication implements IApplication {
    @Override
    public Object start(IApplicationContext context) throws Exception {
        Path root = Files.createTempDirectory("kide-pr14-selfcheck-");
        try {
            ProjectSchemaService service = new ProjectSchemaService();
            require(service.inspect(root).status() == ProjectSchemaStatus.LEGACY, "legacy inspection");
            require(service.migrate(root, true).status() == ProjectSchemaStatus.MIGRATION_REQUIRED, "dry run");
            require(service.inspect(root).status() == ProjectSchemaStatus.LEGACY, "dry run must not write");
            require(service.migrate(root, false).status() == ProjectSchemaStatus.CURRENT, "migration");
            require(service.inspect(root).status() == ProjectSchemaStatus.CURRENT, "current inspection");
            require(service.migrate(root, false).status() == ProjectSchemaStatus.CURRENT, "idempotence");

            Properties future = new Properties();
            future.setProperty("schema.version", "999");
            Path manifest = root.resolve(ProjectSchemaService.MANIFEST);
            try (java.io.BufferedWriter writer = Files.newBufferedWriter(manifest)) {
                future.store(writer, "future");
            }
            require(service.inspect(root).status() == ProjectSchemaStatus.FORWARD_INCOMPATIBLE,
                    "future version rejection");

            System.out.println("KIDE PR14 SCHEMA SELF-CHECK OK");
            return IApplication.EXIT_OK;
        } finally {
            if (Files.exists(root)) {
                try (java.util.stream.Stream<Path> stream = Files.walk(root)) {
                    stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                        try { Files.deleteIfExists(path); } catch (Exception ignored) { }
                    });
                }
            }
        }
    }

    private static void require(boolean condition, String label) {
        if (!condition) throw new IllegalStateException("PR14 self-check failed: " + label);
    }

    @Override
    public void stop() {
    }
}
