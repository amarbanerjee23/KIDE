package com.smr.activity.dsl.ui.hover;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider;

import activityDiagramModel.Activity;
import activityDiagramModel.ActivityDiagram;

/**
 * Shows what an activity does, how long it takes and what follows it, without
 * leaving the editor.
 */
public class ActivityDiagramEObjectHoverProvider extends DefaultEObjectHoverProvider {

	@Override
	protected String getFirstLine(EObject element) {
		if (element instanceof Activity) {
			return "<b>Activity</b> " + nullSafe(((Activity) element).getName());
		}
		if (element instanceof ActivityDiagram) {
			return "<b>Activity diagram</b> " + nullSafe(((ActivityDiagram) element).getName());
		}
		return super.getFirstLine(element);
	}

	@Override
	protected String getDocumentation(EObject element) {
		if (element instanceof Activity) {
			Activity activity = (Activity) element;
			StringBuilder text = new StringBuilder();
			if (activity.getDescription() != null) {
				text.append(activity.getDescription());
			}
			if (activity.getBindCapability() != null) {
				text.append("<br/>Requires capability: ").append(nullSafe(activity.getBindCapability().getName()));
			}
			if (activity.getUnit() != null) {
				text.append("<br/>Takes ").append(activity.getTime()).append(' ').append(activity.getUnit());
			}
			if (activity.getNextActivity() != null) {
				text.append("<br/>Hands over to: ").append(nullSafe(activity.getNextActivity().getName()));
			}
			return text.length() == 0 ? super.getDocumentation(element) : text.toString();
		}
		if (element instanceof ActivityDiagram) {
			ActivityDiagram diagram = (ActivityDiagram) element;
			int count = diagram.getActivities() == null ? 0 : diagram.getActivities().size();
			return "Contains " + count + " activity/activities.";
		}
		return super.getDocumentation(element);
	}

	private String nullSafe(String value) {
		return value == null ? "&lt;unnamed&gt;" : value;
	}
}
