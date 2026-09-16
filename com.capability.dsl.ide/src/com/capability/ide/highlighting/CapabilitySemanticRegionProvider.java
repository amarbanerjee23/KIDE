package com.capability.ide.highlighting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import CapabilityDescription.CapabilitiesOutcome;
import CapabilityDescription.Capability;
import CapabilityDescription.ControlCapabilities;

/**
 * Platform-neutral semantic regions for the Capability DSL.
 *
 * The Eclipse UI and the future language-server client consume this same
 * representation so semantic meaning is not reimplemented per client.
 */
public final class CapabilitySemanticRegionProvider {

    public static final String CAPABILITY_NAME = "capabilityName";
    public static final String INTERFACE_ITEM = "interfaceItem";
    public static final String STRUCTURAL_KEYWORD = "structuralKeyword";

    public List<SemanticRegion> getSemanticRegions(XtextResource resource) {
        if (resource == null || resource.getParseResult() == null) {
            return Collections.emptyList();
        }

        List<SemanticRegion> regions = new ArrayList<>();
        Iterator<EObject> contents = resource.getAllContents();
        while (contents.hasNext()) {
            EObject element = contents.next();
            if (element instanceof Capability) {
                addFeatureRegions(element, "name", CAPABILITY_NAME, regions);
            } else if (element instanceof ControlCapabilities || element instanceof CapabilitiesOutcome) {
                addNodeRegion(NodeModelUtils.findActualNodeFor(element), STRUCTURAL_KEYWORD, 1, regions);
            }
        }
        return Collections.unmodifiableList(regions);
    }

    private void addFeatureRegions(EObject element, String featureName, String kind, List<SemanticRegion> regions) {
        EStructuralFeature feature = element.eClass().getEStructuralFeature(featureName);
        if (feature == null) {
            return;
        }
        for (INode node : NodeModelUtils.findNodesForFeature(element, feature)) {
            regions.add(new SemanticRegion(node.getOffset(), node.getLength(), kind));
        }
    }

    private void addNodeRegion(INode node, String kind, int requestedLength, List<SemanticRegion> regions) {
        if (node != null) {
            regions.add(new SemanticRegion(node.getOffset(), Math.min(requestedLength, node.getLength()), kind));
        }
    }

    /** Immutable client-neutral semantic range. */
    public static final class SemanticRegion {
        private final int offset;
        private final int length;
        private final String kind;

        public SemanticRegion(int offset, int length, String kind) {
            this.offset = offset;
            this.length = length;
            this.kind = kind;
        }

        public int getOffset() {
            return offset;
        }

        public int getLength() {
            return length;
        }

        public String getKind() {
            return kind;
        }
    }
}
