package com.mncml.dsl.ide.highlighting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import mncModel.AlarmBlock;
import mncModel.CommandResponseBlock;
import mncModel.DataPointBlock;
import mncModel.EventBlock;
import mncModel.Transition;

/**
 * Platform-neutral semantic regions for the MNC DSL.
 *
 * Eclipse highlighting and language-server semantic tokens consume this same
 * representation so command/event/alarm/data/state references are classified
 * identically in both clients.
 */
public final class MncSemanticRegionProvider {

    public static final String COMMAND_REFERENCE = "CRB_command";
    public static final String EVENT_REFERENCE = "EB_EVENT";
    public static final String ALARM_REFERENCE = "AB_ALARM";
    public static final String DATA_REFERENCE = "DT_DATA";
    public static final String STATE_REFERENCE = "TR_STATE";

    public List<SemanticRegion> getSemanticRegions(XtextResource resource) {
        if (resource == null || resource.getParseResult() == null) {
            return Collections.emptyList();
        }

        List<SemanticRegion> regions = new ArrayList<>();
        Iterator<EObject> contents = resource.getAllContents();
        while (contents.hasNext()) {
            EObject element = contents.next();
            if (element instanceof CommandResponseBlock) {
                addCrossReferenceRegions(element, COMMAND_REFERENCE, regions);
            } else if (element instanceof EventBlock) {
                addCrossReferenceRegions(element, EVENT_REFERENCE, regions);
            } else if (element instanceof AlarmBlock) {
                addCrossReferenceRegions(element, ALARM_REFERENCE, regions);
            } else if (element instanceof DataPointBlock) {
                addCrossReferenceRegions(element, DATA_REFERENCE, regions);
            } else if (element instanceof Transition) {
                addCrossReferenceRegions(element, STATE_REFERENCE, regions);
            }
        }
        return Collections.unmodifiableList(regions);
    }

    private static void addCrossReferenceRegions(
            EObject semanticElement,
            String kind,
            List<SemanticRegion> regions) {
        INode root = NodeModelUtils.getNode(semanticElement);
        if (root == null) {
            return;
        }
        for (INode node : root.getAsTreeIterable()) {
            if (node.getGrammarElement() instanceof CrossReference
                    && node.getSemanticElement() == semanticElement
                    && node.getLength() > 0) {
                regions.add(new SemanticRegion(node.getOffset(), node.getLength(), kind));
            }
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
