package com.kide.synthesis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DeterministicCapabilityMatcher {
    public SynthesisPlan match(
            List<CapabilityRequirement> requirements,
            List<SynthesisResource> resources) {
        List<CapabilityRequirement> orderedRequirements = requirements == null
                ? List.of()
                : requirements.stream()
                        .sorted(Comparator.comparing(CapabilityRequirement::id))
                        .toList();
        List<SynthesisResource> orderedResources = resources == null
                ? List.of()
                : resources.stream()
                        .sorted(Comparator.comparing(SynthesisResource::id))
                        .toList();

        List<ResourceSelection> selections = new ArrayList<>();
        List<SynthesisDiagnostic> diagnostics = new ArrayList<>();
        Map<String, Integer> bindings = new HashMap<>();
        Set<String> selectedIds = new LinkedHashSet<>();

        for (CapabilityRequirement requirement : orderedRequirements) {
            List<SynthesisResource> candidates = orderedResources.stream()
                    .filter(resource -> resource.supports(requirement))
                    .filter(resource -> bindings.getOrDefault(resource.id(), 0) < resource.maxBindings())
                    .filter(resource -> compatible(resource, selectedIds, orderedResources))
                    .sorted(comparator(selectedIds))
                    .toList();

            if (candidates.isEmpty()) {
                diagnostics.add(SynthesisDiagnostic.error(
                        "NO_FEASIBLE_RESOURCE",
                        "No available resource satisfies capability '" + requirement.capabilityName()
                                + "' with its declared interface/operation contracts.",
                        requirement.id(),
                        List.of()));
                continue;
            }

            SynthesisResource selected = candidates.get(0);
            List<SynthesisResource> tied = candidates.stream()
                    .filter(candidate -> sameDeclaredRank(selected, candidate, selectedIds))
                    .toList();
            if (tied.size() > 1) {
                diagnostics.add(SynthesisDiagnostic.warning(
                        "AMBIGUOUS_MATCH",
                        "Multiple resources have the same declared ranking criteria; "
                                + "the lexicographically smallest resource ID is used as the deterministic tie-break.",
                        requirement.id(),
                        tied.stream().map(SynthesisResource::id).toList()));
            }

            selections.add(new ResourceSelection(
                    requirement.id(),
                    requirement.activityName(),
                    requirement.capabilityName(),
                    selected.id(),
                    rationale(selected, selectedIds.contains(selected.id()))));
            bindings.merge(selected.id(), 1, Integer::sum);
            selectedIds.add(selected.id());
        }

        boolean feasible = diagnostics.stream()
                .noneMatch(d -> d.severity() == SynthesisDiagnosticSeverity.ERROR);
        return new SynthesisPlan(feasible, selections, diagnostics);
    }

    private static Comparator<SynthesisResource> comparator(Set<String> selectedIds) {
        return Comparator
                .comparing((SynthesisResource r) -> !selectedIds.contains(r.id()))
                .thenComparing(Comparator.comparingInt(SynthesisResource::priority).reversed())
                .thenComparingLong(SynthesisResource::latencyMillis)
                .thenComparingLong(SynthesisResource::energyMilliJoules)
                .thenComparing(SynthesisResource::id);
    }

    private static boolean sameDeclaredRank(
            SynthesisResource a,
            SynthesisResource b,
            Set<String> selectedIds) {
        return selectedIds.contains(a.id()) == selectedIds.contains(b.id())
                && a.priority() == b.priority()
                && a.latencyMillis() == b.latencyMillis()
                && a.energyMilliJoules() == b.energyMilliJoules();
    }

    private static boolean compatible(
            SynthesisResource candidate,
            Set<String> selectedIds,
            List<SynthesisResource> resources) {
        if (candidate.conflictsWith().stream().anyMatch(selectedIds::contains)) return false;
        for (SynthesisResource selected : resources) {
            if (selectedIds.contains(selected.id())
                    && selected.conflictsWith().contains(candidate.id())) {
                return false;
            }
        }
        return true;
    }

    private static String rationale(SynthesisResource resource, boolean reused) {
        return (reused ? "reused selected resource; " : "")
                + "priority=" + resource.priority()
                + ", latencyMillis=" + resource.latencyMillis()
                + ", energyMilliJoules=" + resource.energyMilliJoules()
                + ", deterministicTieBreak=" + resource.id();
    }
}
