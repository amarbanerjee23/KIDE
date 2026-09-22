package com.kide.synthesis;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DeterministicReconfigurationPlanner {
    private final DeterministicCapabilityMatcher matcher =
            new DeterministicCapabilityMatcher();

    public ReconfigurationResult reconfigure(
            SynthesisPlan previousPlan,
            List<CapabilityRequirement> requirements,
            List<SynthesisResource> resources,
            ReconfigurationCause cause) {
        if (previousPlan == null) {
            throw new IllegalArgumentException("previousPlan is required");
        }
        if (cause == null) {
            throw new IllegalArgumentException("cause is required");
        }

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

        Map<String, CapabilityRequirement> requirementsById = new LinkedHashMap<>();
        orderedRequirements.forEach(r -> requirementsById.put(r.id(), r));
        Map<String, SynthesisResource> resourcesById = new LinkedHashMap<>();
        orderedResources.forEach(r -> resourcesById.put(r.id(), r));
        Map<String, ResourceSelection> previousByRequirement = new LinkedHashMap<>();
        previousPlan.selections().stream()
                .sorted(Comparator.comparing(ResourceSelection::requirementId))
                .forEach(s -> previousByRequirement.put(s.requirementId(), s));

        List<ResourceSelection> preserved = new ArrayList<>();
        List<CapabilityRequirement> affected = new ArrayList<>();
        List<SynthesisDiagnostic> diagnostics = new ArrayList<>();
        Map<String, Integer> reservedBindings = new HashMap<>();
        Set<String> selectedIds = new LinkedHashSet<>();

        for (CapabilityRequirement requirement : orderedRequirements) {
            ResourceSelection old = previousByRequirement.get(requirement.id());
            if (old == null) {
                affected.add(requirement);
                continue;
            }

            SynthesisResource resource = resourcesById.get(old.resourceId());
            String invalidReason = invalidReason(
                    requirement, resource, reservedBindings, selectedIds, orderedResources);
            if (invalidReason != null) {
                affected.add(requirement);
                diagnostics.add(SynthesisDiagnostic.warning(
                        "BINDING_INVALIDATED",
                        invalidReason,
                        requirement.id(),
                        List.of(old.resourceId())));
                continue;
            }

            preserved.add(new ResourceSelection(
                    requirement.id(),
                    requirement.activityName(),
                    requirement.capabilityName(),
                    resource.id(),
                    "preserved previous binding; " + old.rationale()));
            reservedBindings.merge(resource.id(), 1, Integer::sum);
            selectedIds.add(resource.id());
        }

        SynthesisPlan replanned = matcher.match(
                affected, orderedResources, reservedBindings, selectedIds);
        diagnostics.addAll(replanned.diagnostics());

        List<ResourceSelection> combined = new ArrayList<>(preserved);
        combined.addAll(replanned.selections());
        combined.sort(Comparator.comparing(ResourceSelection::requirementId));

        boolean feasible = diagnostics.stream()
                .noneMatch(d -> d.severity() == SynthesisDiagnosticSeverity.ERROR)
                && combined.size() == orderedRequirements.size();
        SynthesisPlan plan = new SynthesisPlan(feasible, combined, diagnostics);
        List<StateMigrationInstruction> migrations = migrations(
                previousByRequirement, requirementsById, combined, feasible, cause);

        ReconfigurationStatus status;
        if (!feasible) {
            status = ReconfigurationStatus.NO_SOLUTION;
        } else if (sameBindings(previousPlan.selections(), combined)
                && sameRequirements(previousByRequirement, requirementsById)) {
            status = ReconfigurationStatus.UNCHANGED;
        } else {
            status = ReconfigurationStatus.RECONFIGURED;
        }

        return new ReconfigurationResult(
                ReconfigurationResult.VERSION,
                status,
                cause,
                fingerprint(cause, orderedRequirements, orderedResources, combined, migrations),
                plan,
                migrations,
                diagnostics);
    }

    private static String invalidReason(
            CapabilityRequirement requirement,
            SynthesisResource resource,
            Map<String, Integer> bindings,
            Set<String> selectedIds,
            List<SynthesisResource> resources) {
        if (resource == null) {
            return "Previously bound resource is no longer present.";
        }
        if (!resource.supports(requirement)) {
            return "Previously bound resource no longer satisfies the current capability contract.";
        }
        if (bindings.getOrDefault(resource.id(), 0) >= resource.maxBindings()) {
            return "Previously bound resource no longer has binding capacity.";
        }
        if (conflicts(resource, selectedIds, resources)) {
            return "Previously bound resource now conflicts with another preserved binding.";
        }
        return null;
    }

    private static boolean conflicts(
            SynthesisResource candidate,
            Set<String> selectedIds,
            List<SynthesisResource> resources) {
        if (candidate.conflictsWith().stream().anyMatch(selectedIds::contains)) return true;
        for (SynthesisResource selected : resources) {
            if (selectedIds.contains(selected.id())
                    && selected.conflictsWith().contains(candidate.id())) {
                return true;
            }
        }
        return false;
    }

    private static List<StateMigrationInstruction> migrations(
            Map<String, ResourceSelection> previous,
            Map<String, CapabilityRequirement> currentRequirements,
            List<ResourceSelection> currentSelections,
            boolean feasible,
            ReconfigurationCause cause) {
        Map<String, ResourceSelection> current = new LinkedHashMap<>();
        currentSelections.forEach(s -> current.put(s.requirementId(), s));
        List<StateMigrationInstruction> result = new ArrayList<>();

        Set<String> all = new java.util.TreeSet<>();
        all.addAll(previous.keySet());
        all.addAll(currentRequirements.keySet());

        for (String requirementId : all) {
            ResourceSelection old = previous.get(requirementId);
            CapabilityRequirement requirement = currentRequirements.get(requirementId);
            ResourceSelection next = current.get(requirementId);

            if (requirement == null && old != null) {
                result.add(new StateMigrationInstruction(
                        requirementId,
                        StateMigrationPolicy.RETIRE,
                        old.resourceId(),
                        "",
                        "Requirement was removed; retire its supervisory state."));
                continue;
            }
            if (old == null && next != null) {
                result.add(new StateMigrationInstruction(
                        requirementId,
                        StateMigrationPolicy.INITIALIZE,
                        "",
                        next.resourceId(),
                        "New requirement requires initialized supervisory state."));
                continue;
            }
            if (old != null && next == null) {
                result.add(new StateMigrationInstruction(
                        requirementId,
                        StateMigrationPolicy.SAFE_FALLBACK,
                        old.resourceId(),
                        "",
                        feasible
                                ? "Binding was removed; enter safe fallback."
                                : "No feasible replacement exists; retain safe fallback and do not continue automatic control."));
                continue;
            }
            if (old == null) continue;

            if (cause == ReconfigurationCause.REQUIREMENT_CHANGE
                    || cause == ReconfigurationCause.CAPABILITY_CHANGE
                    || !old.capabilityName().equals(requirement.capabilityName())) {
                result.add(new StateMigrationInstruction(
                        requirementId,
                        StateMigrationPolicy.RESET,
                        old.resourceId(),
                        next.resourceId(),
                        "Capability contract changed; reset requirement-specific supervisory state."));
            } else if (old.resourceId().equals(next.resourceId())) {
                result.add(new StateMigrationInstruction(
                        requirementId,
                        StateMigrationPolicy.PRESERVE,
                        old.resourceId(),
                        next.resourceId(),
                        "Binding remains valid; preserve supervisory state."));
            } else {
                result.add(new StateMigrationInstruction(
                        requirementId,
                        StateMigrationPolicy.MIGRATE,
                        old.resourceId(),
                        next.resourceId(),
                        "Equivalent capability binding changed; migrate supervisory state to the replacement resource."));
            }
        }
        return List.copyOf(result);
    }

    private static boolean sameBindings(
            List<ResourceSelection> previous,
            List<ResourceSelection> current) {
        Map<String, String> a = new java.util.TreeMap<>();
        previous.forEach(s -> a.put(s.requirementId(), s.resourceId()));
        Map<String, String> b = new java.util.TreeMap<>();
        current.forEach(s -> b.put(s.requirementId(), s.resourceId()));
        return a.equals(b);
    }

    private static boolean sameRequirements(
            Map<String, ResourceSelection> previous,
            Map<String, CapabilityRequirement> current) {
        if (!previous.keySet().equals(current.keySet())) return false;
        for (var entry : current.entrySet()) {
            ResourceSelection old = previous.get(entry.getKey());
            if (old == null || !old.capabilityName().equals(entry.getValue().capabilityName())) {
                return false;
            }
        }
        return true;
    }

    private static String fingerprint(
            ReconfigurationCause cause,
            List<CapabilityRequirement> requirements,
            List<SynthesisResource> resources,
            List<ResourceSelection> selections,
            List<StateMigrationInstruction> migrations) {
        StringBuilder canonical = new StringBuilder("kide-reconfiguration-v1|")
                .append(cause).append('\n');
        requirements.forEach(r -> canonical.append("req|")
                .append(r.id()).append('|').append(r.capabilityName()).append('|')
                .append(r.requiredInterfaces()).append('|')
                .append(r.requiredOperations()).append('\n'));
        resources.forEach(r -> canonical.append("res|")
                .append(r.id()).append('|').append(r.available()).append('|')
                .append(r.capabilities()).append('|').append(r.interfaces()).append('|')
                .append(r.operations()).append('|').append(r.priority()).append('|')
                .append(r.latencyMillis()).append('|').append(r.energyMilliJoules()).append('|')
                .append(r.maxBindings()).append('|').append(r.conflictsWith()).append('\n'));
        selections.forEach(s -> canonical.append("sel|")
                .append(s.requirementId()).append('|').append(s.resourceId()).append('\n'));
        migrations.forEach(m -> canonical.append("mig|")
                .append(m.requirementId()).append('|').append(m.policy()).append('|')
                .append(m.fromResourceId()).append('|').append(m.toResourceId()).append('\n'));
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(
                            canonical.toString().getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
