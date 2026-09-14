package com.capability.ui.hover;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider;

import CapabilityDescription.Capability;

/**
 * Explains, on hover, what an element means, so that a reader does not have to
 * jump to the interface description to understand it.
 */
public class CapabilityEObjectHoverProvider extends DefaultEObjectHoverProvider {

	@Override
	protected String getFirstLine(EObject element) {
		if (element instanceof Capability) {
			return "<b>Capability</b> " + nullSafe(((Capability) element).getName());
		}
		String kind = readableKind(element);
		String name = readName(element);
		if (kind != null && name != null) {
			return "<b>" + kind + "</b> " + name;
		}
		return super.getFirstLine(element);
	}

	@Override
	protected String getDocumentation(EObject element) {
		if (element instanceof Capability) {
			Capability capability = (Capability) element;
			StringBuilder text = new StringBuilder();
			text.append("Declared against ");
			text.append(capability.getComponentInterface() == null ? 0 : capability.getComponentInterface().size());
			text.append(" component interface(s).");
			if (capability.getProvidesControlCapabilities() != null) {
				text.append("<br/>Provides control capabilities: the commands, events, alarms and data points a plan may use.");
			}
			if (capability.getProvidesOutcomes() != null) {
				text.append("<br/>Provides outcomes: the results a plan may branch on.");
			}
			return text.toString();
		}
		return super.getDocumentation(element);
	}

	/** Turns a metamodel class name such as DataPoint into "Data point". */
	private String readableKind(EObject element) {
		if (element == null) {
			return null;
		}
		String raw = element.eClass().getName();
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

	private String readName(EObject element) {
		EStructuralFeature feature = element.eClass().getEStructuralFeature("name");
		if (feature == null) {
			return null;
		}
		Object value = element.eGet(feature);
		return value == null ? null : String.valueOf(value);
	}

	private String nullSafe(String value) {
		return value == null ? "&lt;unnamed&gt;" : value;
	}
}
