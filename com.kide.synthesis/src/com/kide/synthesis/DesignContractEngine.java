package com.kide.synthesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import CapabilityDescription.Capability;
import activityDiagramModel.Activity;
import activityDiagramModel.ActivityDiagram;
import mncModel.AbstractInterfaceItems;

public final class DesignContractEngine {
    public DesignContractReport validate(
            ActivityDiagram diagram,
            List<CapabilityRequirement> requirements,
            SynthesisPlan plan,
            List<SynthesisResource> resources) {
        List<SynthesisDiagnostic> diagnostics = new ArrayList<>();
        if (diagram == null) {
            diagnostics.add(SynthesisDiagnostic.error(
                    "MISSING_ACTIVITY_DIAGRAM",
                    "Activity diagram is required.",
                    "",
                    List.of()));
            return new DesignContractReport(false, diagnostics);
        }

        Set<String> names = new HashSet<>();
        Set<Activity> contained = Set.copyOf(diagram.getActivities());
        for (Activity activity : diagram.getActivities()) {
            String name = activity.getName() == null ? "" : activity.getName().trim();
            if (name.isEmpty()) {
                diagnostics.add(SynthesisDiagnostic.error(
                        "ACTIVITY_NAME_REQUIRED",
                        "Every synthesized activity must have a stable name.",
                        "",
                        List.of()));
            } else if (!names.add(name)) {
                diagnostics.add(SynthesisDiagnostic.error(
                        "DUPLICATE_ACTIVITY_NAME",
                        "Activity names must be unique for deterministic synthesis.",
                        "activity:" + name,
                        List.of()));
            }

            if (activity.getBindCapability() != null
                    && activity.getRequiredCapability() != null
                    && !activity.getRequiredCapability().isBlank()) {
                diagnostics.add(SynthesisDiagnostic.error(
                        "AMBIGUOUS_CAPABILITY_DECLARATION",
                        "An activity cannot use both a bound Capability and textual requiredCapability.",
                        "activity:" + name,
                        List.of()));
            }

            if (activity.getNextActivity() != null
                    && !contained.contains(activity.getNextActivity())) {
                diagnostics.add(SynthesisDiagnostic.error(
                        "INVALID_NEXT_ACTIVITY",
                        "nextActivity must reference an activity in the same diagram.",
                        "activity:" + name,
                        List.of()));
            }

            if (activity.getInterruptedBy().contains(activity)
                    || activity.getInterrupts().contains(activity)) {
                diagnostics.add(SynthesisDiagnostic.error(
                        "SELF_INTERRUPTION",
                        "An activity cannot interrupt itself.",
                        "activity:" + name,
                        List.of()));
            }

            Capability bound = activity.getBindCapability();
            if (bound != null && !activity.getUseControlCapabilities().isEmpty()) {
                Set<AbstractInterfaceItems> allowed = allowedItems(bound);
                for (AbstractInterfaceItems item : activity.getUseControlCapabilities()) {
                    if (!allowed.contains(item)) {
                        diagnostics.add(SynthesisDiagnostic.error(
                                "CONTROL_CAPABILITY_OUTSIDE_BINDING",
                                "Activity uses an interface item not declared by its bound Capability.",
                                "activity:" + name,
                                List.of()));
                    }
                }
            }
        }

        Map<String, SynthesisResource> byId = new HashMap<>();
        for (SynthesisResource resource : resources) byId.put(resource.id(), resource);
        Map<String, CapabilityRequirement> requirementById = new HashMap<>();
        for (CapabilityRequirement requirement : requirements) {
            requirementById.put(requirement.id(), requirement);
            if (!requirement.boundToCapabilityModel()) {
                diagnostics.add(SynthesisDiagnostic.error(
                        "UNBOUND_CAPABILITY_MODEL",
                        "Resource matching succeeded only against a textual capability name; "
                                + "controller synthesis requires a resolved Capability DSL object.",
                        requirement.id(),
                        List.of(requirement.capabilityName())));
            }
        }

        for (ResourceSelection selection : plan.selections()) {
            CapabilityRequirement requirement = requirementById.get(selection.requirementId());
            SynthesisResource resource = byId.get(selection.resourceId());
            if (requirement == null || resource == null || !resource.supports(requirement)) {
                diagnostics.add(SynthesisDiagnostic.error(
                        "INVALID_RESOURCE_SELECTION",
                        "Selected resource no longer satisfies its capability contract.",
                        selection.requirementId(),
                        List.of(selection.resourceId())));
            }
        }

        for (ResourceSelection left : plan.selections()) {
            SynthesisResource a = byId.get(left.resourceId());
            if (a == null) continue;
            for (ResourceSelection right : plan.selections()) {
                if (left == right) continue;
                SynthesisResource b = byId.get(right.resourceId());
                if (b != null && (a.conflictsWith().contains(b.id())
                        || b.conflictsWith().contains(a.id()))) {
                    diagnostics.add(SynthesisDiagnostic.error(
                            "RESOURCE_COMPOSITION_CONFLICT",
                            "Selected resources declare an incompatible composition.",
                            left.requirementId(),
                            List.of(a.id(), b.id())));
                }
            }
        }

        diagnostics.addAll(plan.diagnostics());
        boolean valid = diagnostics.stream()
                .noneMatch(d -> d.severity() == SynthesisDiagnosticSeverity.ERROR);
        return new DesignContractReport(valid, diagnostics);
    }

    private static Set<AbstractInterfaceItems> allowedItems(Capability capability) {
        Set<AbstractInterfaceItems> allowed = new HashSet<>();
        if (capability.getProvidesControlCapabilities() != null) {
            allowed.addAll(capability.getProvidesControlCapabilities().getCommands());
            allowed.addAll(capability.getProvidesControlCapabilities().getEvents());
            allowed.addAll(capability.getProvidesControlCapabilities().getAlarms());
            allowed.addAll(capability.getProvidesControlCapabilities().getDataPoints());
        }
        return allowed;
    }
}
