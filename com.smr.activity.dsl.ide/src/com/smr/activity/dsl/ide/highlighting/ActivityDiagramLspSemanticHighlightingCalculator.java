package com.smr.activity.dsl.ide.highlighting;

import org.eclipse.xtext.ide.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;

import com.smr.activity.dsl.ide.highlighting.ActivityDiagramSemanticRegionProvider.SemanticRegion;

/**
 * LSP semantic-token adapter over the same Activity regions consumed by the
 * Eclipse editor.
 */
public final class ActivityDiagramLspSemanticHighlightingCalculator
        implements ISemanticHighlightingCalculator {

    private final ActivityDiagramSemanticRegionProvider regions =
            new ActivityDiagramSemanticRegionProvider();

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
        if (ActivityDiagramSemanticRegionProvider.ACTIVITY_NAME.equals(kind)) {
            return "function";
        }
        if (ActivityDiagramSemanticRegionProvider.CAPABILITY_REFERENCE.equals(kind)) {
            return "class";
        }
        if (ActivityDiagramSemanticRegionProvider.DATA_REFERENCE.equals(kind)) {
            return "variable";
        }
        return "variable";
    }
}
