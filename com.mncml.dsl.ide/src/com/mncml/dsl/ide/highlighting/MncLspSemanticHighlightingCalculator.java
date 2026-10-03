package com.mncml.dsl.ide.highlighting;

import org.eclipse.xtext.ide.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;

import com.mncml.dsl.ide.highlighting.MncSemanticRegionProvider.SemanticRegion;

/**
 * LSP semantic-token adapter over the same MNC regions consumed by the Eclipse
 * editor.
 */
public final class MncLspSemanticHighlightingCalculator
        implements ISemanticHighlightingCalculator {

    private final MncSemanticRegionProvider regions = new MncSemanticRegionProvider();

    @Override
    public void provideHighlightingFor(
            XtextResource resource,
            IHighlightedPositionAcceptor acceptor,
            CancelIndicator cancelIndicator) {
        for (SemanticRegion region : regions.getSemanticRegions(resource)) {
            if (cancelIndicator != null && cancelIndicator.isCanceled()) {
                return;
            }
            acceptor.addPosition(
                    region.getOffset(),
                    region.getLength(),
                    tokenType(region.getKind()));
        }
    }

    private static String tokenType(String kind) {
        if (MncSemanticRegionProvider.COMMAND_REFERENCE.equals(kind)) {
            return "method";
        }
        if (MncSemanticRegionProvider.EVENT_REFERENCE.equals(kind)
                || MncSemanticRegionProvider.ALARM_REFERENCE.equals(kind)) {
            return "event";
        }
        if (MncSemanticRegionProvider.DATA_REFERENCE.equals(kind)) {
            return "property";
        }
        if (MncSemanticRegionProvider.STATE_REFERENCE.equals(kind)) {
            return "enumMember";
        }
        return "variable";
    }
}
