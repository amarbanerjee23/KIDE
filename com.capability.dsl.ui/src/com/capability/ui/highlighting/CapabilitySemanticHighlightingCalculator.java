package com.capability.ui.highlighting;

import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;

import CapabilityDescription.Capability;
import CapabilityDescription.ControlCapabilities;
import CapabilityDescription.CapabilitiesOutcome;

/**
 * Highlights the elements that carry meaning while reading a capability:
 * the capability name, and the control capability / outcome blocks.
 *
 * This implementation deliberately uses the two-argument
 * ISemanticHighlightingCalculator contract provided by the Xtext 2.25 target
 * platform used by KIDE. Cancellation-aware highlighting was introduced through
 * a newer API and must not leak into bundles that are compiled against 2.25.
 */
public class CapabilitySemanticHighlightingCalculator implements ISemanticHighlightingCalculator {

	@Override
	public void provideHighlightingFor(XtextResource resource, IHighlightedPositionAcceptor acceptor) {
		if (resource == null || resource.getParseResult() == null) {
			return;
		}
		Iterator<EObject> contents = resource.getAllContents();
		while (contents.hasNext()) {
			EObject element = contents.next();
			if (element instanceof Capability) {
				highlightFeature(element, "name", CapabilityHighlightingConfiguration.CAPABILITY_NAME_ID, acceptor);
			} else if (element instanceof ControlCapabilities || element instanceof CapabilitiesOutcome) {
				highlightNode(NodeModelUtils.findActualNodeFor(element),
						CapabilityHighlightingConfiguration.STRUCTURAL_KEYWORD_ID, acceptor, 1);
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

	private void highlightNode(INode node, String styleId, IHighlightedPositionAcceptor acceptor, int length) {
		if (node != null) {
			acceptor.addPosition(node.getOffset(), Math.min(length, node.getLength()), styleId);
		}
	}
}
