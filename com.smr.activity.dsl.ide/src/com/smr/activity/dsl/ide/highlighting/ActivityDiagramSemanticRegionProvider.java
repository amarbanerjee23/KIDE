package com.smr.activity.dsl.ide.highlighting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import activityDiagramModel.Activity;
import activityDiagramModel.ActivityDiagram;

/**
 * Platform-neutral semantic regions for the Activity DSL.
 *
 * Eclipse highlighting and language-server semantic tokens must be derived
 * from this service rather than duplicating model traversal in each client.
 */
public final class ActivityDiagramSemanticRegionProvider {

    public static final String ACTIVITY_NAME = "activityName";
    public static final String CAPABILITY_REFERENCE = "capabilityReference";
    public static final String DATA_REFERENCE = "dataReference";

    public List<SemanticRegion> getSemanticRegions(XtextResource resource) {
        if (resource == null || resource.getParseResult() == null) {
            return Collections.emptyList();
        }

        List<SemanticRegion> regions = new ArrayList<>();
        Iterator<EObject> contents = resource.getAllContents();
        while (contents.hasNext()) {
            EObject element = contents.next();
            if (element instanceof Activity || element instanceof ActivityDiagram) {
                addFeatureRegions(element, "name", ACTIVITY_NAME, regions);
            }
            if (element instanceof Activity) {
                addFeatureRegions(element, "bindCapability", CAPABILITY_REFERENCE, regions);
                addFeatureRegions(element, "inputParameters", DATA_REFERENCE, regions);
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
