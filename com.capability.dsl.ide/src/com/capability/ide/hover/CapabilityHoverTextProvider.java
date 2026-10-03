package com.capability.ide.hover;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import CapabilityDescription.Capability;

/** Client-neutral Capability hover descriptions shared by Eclipse and LSP. */
public final class CapabilityHoverTextProvider {

    public HoverText describe(EObject element) {
        if (element == null) {
            return null;
        }
        if (element instanceof Capability capability) {
            StringBuilder detail = new StringBuilder();
            detail.append("Declared against ")
                    .append(capability.getComponentInterface() == null
                            ? 0 : capability.getComponentInterface().size())
                    .append(" component interface(s).");
            if (capability.getProvidesControlCapabilities() != null) {
                detail.append("\nProvides control capabilities: the commands, events, alarms and data points a plan may use.");
            }
            if (capability.getProvidesOutcomes() != null) {
                detail.append("\nProvides outcomes: the results a plan may branch on.");
            }
            return new HoverText(
                    "Capability " + nullSafe(capability.getName()),
                    detail.toString());
        }

        String kind = readableKind(element);
        String name = readName(element);
        if (kind != null && name != null) {
            return new HoverText(kind + " " + name, "");
        }
        return null;
    }

    private static String readableKind(EObject element) {
        String raw = element.eClass().getName();
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (i > 0 && Character.isUpperCase(c)) {
                out.append(' ').append(Character.toLowerCase(c));
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    private static String readName(EObject element) {
        EStructuralFeature feature = element.eClass().getEStructuralFeature("name");
        if (feature == null) {
            return null;
        }
        Object value = element.eGet(feature);
        return value == null ? null : String.valueOf(value);
    }

    private static String nullSafe(String value) {
        return value == null ? "<unnamed>" : value;
    }

    public record HoverText(String title, String detail) {
    }
}
