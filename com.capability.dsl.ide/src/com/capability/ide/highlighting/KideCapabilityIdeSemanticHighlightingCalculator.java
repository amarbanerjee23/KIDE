package com.capability.ide.highlighting;

import org.eclipse.xtext.ide.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;

import com.capability.ide.highlighting.CapabilitySemanticRegionProvider.SemanticRegion;

/** Headless/LSP adapter over the same semantic regions used by Eclipse. */
public final class KideCapabilityIdeSemanticHighlightingCalculator
        implements ISemanticHighlightingCalculator {

    private final CapabilitySemanticRegionProvider regions =
            new CapabilitySemanticRegionProvider();

    @Override
    public void provideHighlightingFor(
            XtextResource resource,
            IHighlightedPositionAcceptor acceptor,
            CancelIndicator cancelIndicator) {
        for (SemanticRegion region : regions.getSemanticRegions(resource)) {
            if (cancelIndicator.isCanceled()) {
                return;
            }
            acceptor.addPosition(
                    region.getOffset(),
                    region.getLength(),
                    region.getKind());
        }
    }
}
