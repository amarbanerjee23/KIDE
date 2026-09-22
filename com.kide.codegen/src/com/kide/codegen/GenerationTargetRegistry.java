package com.kide.codegen;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class GenerationTargetRegistry {
    private final Map<String, GenerationTarget> targets;

    public GenerationTargetRegistry(List<GenerationTarget> targets) {
        Map<String, GenerationTarget> values = new LinkedHashMap<>();
        for (GenerationTarget target : Objects.requireNonNull(targets, "targets")) {
            GenerationTarget previous = values.put(target.id(), target);
            if (previous != null) {
                throw new IllegalArgumentException("duplicate generation target " + target.id());
            }
        }
        this.targets = Map.copyOf(values);
    }

    public static GenerationTargetRegistry defaults() {
        return new GenerationTargetRegistry(List.of(new JavaGenerationTarget()));
    }

    public GenerationTarget require(String id) {
        GenerationTarget target = targets.get(id);
        if (target == null) {
            throw new GenerationException("unsupported generation target: " + id);
        }
        return target;
    }

    public Map<String, String> versions() {
        Map<String, String> result = new java.util.TreeMap<>();
        targets.forEach((id, target) -> result.put(id, target.version()));
        return Map.copyOf(result);
    }
}
