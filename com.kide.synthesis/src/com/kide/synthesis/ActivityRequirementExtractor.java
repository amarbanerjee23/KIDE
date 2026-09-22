package com.kide.synthesis;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import CapabilityDescription.Capability;
import activityDiagramModel.Activity;
import activityDiagramModel.ActivityDiagram;
import mncModel.InterfaceDescription;
import operationsDescription.Operation;

public final class ActivityRequirementExtractor {
    public List<CapabilityRequirement> extract(ActivityDiagram diagram) {
        if (diagram == null) throw new IllegalArgumentException("activity diagram is required");
        return diagram.getActivities().stream()
                .filter(activity -> activity.getBindCapability() != null
                        || nonBlank(activity.getRequiredCapability()))
                .map(this::requirement)
                .sorted(java.util.Comparator.comparing(CapabilityRequirement::id))
                .toList();
    }

    private CapabilityRequirement requirement(Activity activity) {
        Capability bound = activity.getBindCapability();
        String capability = bound != null ? bound.getName() : activity.getRequiredCapability();
        Set<String> interfaces = new LinkedHashSet<>();
        Set<String> operations = new LinkedHashSet<>();
        if (bound != null) {
            for (InterfaceDescription descriptor : bound.getComponentInterface()) {
                if (nonBlank(descriptor.getName())) interfaces.add(descriptor.getName().trim());
            }
            if (bound.getProvidesControlCapabilities() != null) {
                for (Operation operation : bound.getProvidesControlCapabilities().getOperation()) {
                    if (nonBlank(operation.getName())) operations.add(operation.getName().trim());
                }
            }
        }
        for (Operation operation : activity.getRequiresOperation()) {
            if (nonBlank(operation.getName())) operations.add(operation.getName().trim());
        }
        String activityName = nonBlank(activity.getName()) ? activity.getName().trim() : "<unnamed>";
        return new CapabilityRequirement(
                "activity:" + activityName,
                activityName,
                capability,
                interfaces,
                operations,
                bound != null);
    }

    private static boolean nonBlank(String value) {
        return value != null && !value.isBlank();
    }
}
