package com.capability.ide.highlighting;

import org.eclipse.xtext.ide.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;

import com.capability.ide.highlighting.CapabilitySemanticRegionProvider.SemanticRegion;

/**
 * LSP semantic-token adapter over the same Capability regions consumed by the
 * Eclipse editor.
 */
public final class CapabilityLspSemanticHighlightingCalculator
        implements ISemanticHighlightingCalculator {

    private final CapabilitySemanticRegionProvider regions =
            new CapabilitySemanticRegionProvider();

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
        if (CapabilitySemanticRegionProvider.CAPABILITY_NAME.equals(kind)) {
            return "class";
        }
        if (CapabilitySemanticRegionProvider.INTERFACE_ITEM.equals(kind)) {
            return "property";
        }
        if (CapabilitySemanticRegionProvider.STRUCTURAL_KEYWORD.equals(kind)) {
            return "keyword";
        }
        return "variable";
    }
}
