package com.smr.activity.dsl.ide.hover;

import org.eclipse.emf.ecore.EObject;

import activityDiagramModel.Activity;
import activityDiagramModel.ActivityDiagram;

/** Client-neutral Activity hover descriptions shared by Eclipse and LSP. */
public final class ActivityDiagramHoverTextProvider {

    public HoverText describe(EObject element) {
        if (element instanceof Activity activity) {
            StringBuilder detail = new StringBuilder();
            if (activity.getDescription() != null && !activity.getDescription().isBlank()) {
                detail.append(activity.getDescription());
            }
            if (activity.getBindCapability() != null) {
                appendLine(detail, "Requires capability: "
                        + nullSafe(activity.getBindCapability().getName()));
            }
            if (activity.getUnit() != null) {
                appendLine(detail, "Takes " + activity.getTime() + " " + activity.getUnit());
            }
            if (activity.getNextActivity() != null) {
                appendLine(detail, "Hands over to: "
                        + nullSafe(activity.getNextActivity().getName()));
            }
            return new HoverText(
                    "Activity " + nullSafe(activity.getName()),
                    detail.toString());
        }
        if (element instanceof ActivityDiagram diagram) {
            int count = diagram.getActivities() == null ? 0 : diagram.getActivities().size();
            return new HoverText(
                    "Activity diagram " + nullSafe(diagram.getName()),
                    "Contains " + count + " activity/activities.");
        }
        return null;
    }

    private static void appendLine(StringBuilder text, String value) {
        if (text.length() > 0) {
            text.append('\n');
        }
        text.append(value);
    }

    private static String nullSafe(String value) {
        return value == null ? "<unnamed>" : value;
    }

    public record HoverText(String title, String detail) {
    }
}
