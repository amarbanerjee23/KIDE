package com.smr.activity.dsl.ui.highlighting;

import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;

import activityDiagramModel.Activity;
import activityDiagramModel.ActivityDiagram;

/**
 * Highlights activity names and the capability each activity requires.
 *
 * KIDE is built against Xtext 2.25, whose semantic-highlighting contract uses
 * the two-argument provideHighlightingFor method. Cancellation-aware variants
 * belong to newer Xtext APIs and must not be compiled into this bundle.
 */
public class ActivityDiagramSemanticHighlightingCalculator implements ISemanticHighlightingCalculator {

	@Override
	public void provideHighlightingFor(XtextResource resource, IHighlightedPositionAcceptor acceptor) {
		if (resource == null || resource.getParseResult() == null) {
			return;
		}
		Iterator<EObject> contents = resource.getAllContents();
		while (contents.hasNext()) {
			EObject element = contents.next();
			if (element instanceof Activity || element instanceof ActivityDiagram) {
				highlightFeature(element, "name", ActivityDiagramHighlightingConfiguration.ACTIVITY_NAME_ID, acceptor);
			}
			if (element instanceof Activity) {
				highlightFeature(element, "bindCapability",
						ActivityDiagramHighlightingConfiguration.CAPABILITY_REFERENCE_ID, acceptor);
				highlightFeature(element, "inputParameters",
						ActivityDiagramHighlightingConfiguration.DATA_REFERENCE_ID, acceptor);
			}
		}
	}

	private void highlightFeature(EObject element, String featureName, String styleId,
			IHighlightedPositionAcceptor acceptor) {
		EStructuralFeature feature = element.eClass().getEStructuralFeature(featureName);
		if (feature == null) {
			return;
		}
		for (INode node : NodeModelUtils.findNodesForFeature(element, feature)) {
			acceptor.addPosition(node.getOffset(), node.getLength(), styleId);
		}
	}
}
