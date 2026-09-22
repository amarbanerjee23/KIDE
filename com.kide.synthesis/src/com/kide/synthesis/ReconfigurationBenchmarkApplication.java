package com.kide.synthesis;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public final class ReconfigurationBenchmarkApplication implements IApplication {
    private static final int[] DEVICE_COUNTS = {10, 50, 100, 500, 1000};

    @Override
    public Object start(IApplicationContext context) {
        try {
            Path output = outputPath(context);
            String evidence = benchmark();
            Files.createDirectories(output.toAbsolutePath().normalize().getParent());
            Files.writeString(output, evidence, StandardCharsets.UTF_8);
            System.out.println("KIDE PR37 RECONFIGURATION SCALE QUALIFIED: "
                    + output.toAbsolutePath().normalize());
            return IApplication.EXIT_OK;
        } catch (Throwable failure) {
            String detail = failure.getMessage();
            if (detail == null || detail.isBlank()) detail = "no detail";
            detail = detail.replace('\r', ' ').replace('\n', ' ');
            System.err.println("KIDE PR37 reconfiguration benchmark failed: "
                    + failure.getClass().getSimpleName() + " - " + detail);
            return Integer.valueOf(2);
        }
    }

    @Override
    public void stop() { }

    private static String benchmark() {
        DeterministicCapabilityMatcher matcher = new DeterministicCapabilityMatcher();
        DeterministicReconfigurationPlanner planner =
                new DeterministicReconfigurationPlanner();
        ThreadMXBean cpu = ManagementFactory.getThreadMXBean();
        if (cpu.isThreadCpuTimeSupported() && !cpu.isThreadCpuTimeEnabled()) {
            cpu.setThreadCpuTimeEnabled(true);
        }

        List<BenchmarkRow> rows = new ArrayList<>();
        for (int deviceCount : DEVICE_COUNTS) {
            int requirementCount = Math.max(1, deviceCount / 10);
            List<CapabilityRequirement> requirements = requirements(requirementCount);
            List<SynthesisResource> resources = resources(deviceCount, true);

            long matchStart = System.nanoTime();
            SynthesisPlan initial = matcher.match(requirements, resources);
            long matchNanos = System.nanoTime() - matchStart;
            if (!initial.feasible() || initial.selections().size() != requirementCount) {
                throw new AssertionError("initial scale plan is infeasible for " + deviceCount);
            }

            List<SynthesisResource> afterLoss = resources(deviceCount, false);
            long cpuStart = currentCpu(cpu);
            long start = System.nanoTime();
            ReconfigurationResult result = planner.reconfigure(
                    initial,
                    requirements,
                    afterLoss,
                    ReconfigurationCause.RESOURCE_LOSS);
            long reconfigurationNanos = System.nanoTime() - start;
            long cpuNanos = Math.max(0L, currentCpu(cpu) - cpuStart);

            ReconfigurationResult repeated = planner.reconfigure(
                    initial,
                    requirements,
                    afterLoss,
                    ReconfigurationCause.RESOURCE_LOSS);

            if (result.status() != ReconfigurationStatus.RECONFIGURED
                    || !result.plan().feasible()
                    || result.plan().selections().size() != requirementCount) {
                throw new AssertionError(
                        "scale reconfiguration is infeasible for " + deviceCount);
            }
            if (!result.fingerprint().equals(repeated.fingerprint())
                    || !result.plan().selections().equals(repeated.plan().selections())
                    || !result.migrations().equals(repeated.migrations())) {
                throw new AssertionError(
                        "scale reconfiguration is not deterministic for " + deviceCount);
            }

            long preserved = result.migrations().stream()
                    .filter(m -> m.policy() == StateMigrationPolicy.PRESERVE)
                    .count();
            long migrated = result.migrations().stream()
                    .filter(m -> m.policy() == StateMigrationPolicy.MIGRATE)
                    .count();
            if (migrated != 1L || preserved != requirementCount - 1L) {
                throw new AssertionError(
                        "scale reconfiguration changed more bindings than necessary for "
                                + deviceCount);
            }

            rows.add(new BenchmarkRow(
                    deviceCount,
                    requirementCount,
                    matchNanos,
                    reconfigurationNanos,
                    cpuNanos,
                    preserved,
                    migrated,
                    result.fingerprint()));
        }
        return json(rows);
    }

    private static List<CapabilityRequirement> requirements(int count) {
        List<CapabilityRequirement> requirements = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String suffix = String.format(Locale.ROOT, "%04d", i);
            requirements.add(new CapabilityRequirement(
                    "activity:R" + suffix,
                    "R" + suffix,
                    "Observe",
                    Set.of("Sensor"),
                    Set.of(),
                    true));
        }
        return List.copyOf(requirements);
    }

    private static List<SynthesisResource> resources(int count, boolean primaryAvailable) {
        List<SynthesisResource> resources = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String suffix = String.format(Locale.ROOT, "%04d", i);
            resources.add(new SynthesisResource(
                    "urn:kide:device:" + suffix,
                    "Device " + suffix,
                    Set.of("Observe"),
                    Set.of("Sensor"),
                    Set.of(),
                    Set.of(),
                    i != 0 || primaryAvailable,
                    count - i,
                    5L + i,
                    20L + i,
                    1));
        }
        return List.copyOf(resources);
    }

    private static long currentCpu(ThreadMXBean bean) {
        if (!bean.isCurrentThreadCpuTimeSupported() || !bean.isThreadCpuTimeEnabled()) {
            return 0L;
        }
        return Math.max(0L, bean.getCurrentThreadCpuTime());
    }

    private static Path outputPath(IApplicationContext context) {
        Object raw = context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
        String[] args = raw instanceof String[] values ? values : new String[0];
        for (int i = 0; i < args.length; i++) {
            if ("--output".equals(args[i]) && i + 1 < args.length) {
                return Path.of(args[i + 1]);
            }
        }
        return Path.of("pr37-reconfiguration-benchmark.json");
    }

    private static String json(List<BenchmarkRow> rows) throws IOException {
        StringBuilder out = new StringBuilder();
        out.append("{\n")
                .append("  \"schema\": \"kide-pr37-reconfiguration-benchmark-v1\",\n")
                .append("  \"timingPolicy\": \"evidence-only-on-hosted-runner\",\n")
                .append("  \"javaVersion\": \"")
                .append(escape(System.getProperty("java.version", "unknown")))
                .append("\",\n")
                .append("  \"availableProcessors\": ")
                .append(Runtime.getRuntime().availableProcessors())
                .append(",\n")
                .append("  \"scenarios\": [\n");
        for (int i = 0; i < rows.size(); i++) {
            BenchmarkRow row = rows.get(i);
            out.append("    {")
                    .append("\"deviceCount\":").append(row.deviceCount)
                    .append(",\"requirementCount\":").append(row.requirementCount)
                    .append(",\"initialMatchNanos\":").append(row.initialMatchNanos)
                    .append(",\"reconfigurationNanos\":").append(row.reconfigurationNanos)
                    .append(",\"threadCpuNanos\":").append(row.threadCpuNanos)
                    .append(",\"preservedBindings\":").append(row.preservedBindings)
                    .append(",\"migratedBindings\":").append(row.migratedBindings)
                    .append(",\"fingerprint\":\"").append(row.fingerprint).append("\"}");
            if (i + 1 < rows.size()) out.append(',');
            out.append('\n');
        }
        out.append("  ]\n}\n");
        return out.toString();
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private record BenchmarkRow(
            int deviceCount,
            int requirementCount,
            long initialMatchNanos,
            long reconfigurationNanos,
            long threadCpuNanos,
            long preservedBindings,
            long migratedBindings,
            String fingerprint) { }
}
