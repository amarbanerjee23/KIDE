package com.mncml.dsl.ide.highlighting;

import org.eclipse.xtext.ide.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;

import com.mncml.dsl.ide.highlighting.MncSemanticRegionProvider.SemanticRegion;

/** Headless/LSP adapter over the same semantic regions used by Eclipse. */
public final class KideMncIdeSemanticHighlightingCalculator
        implements ISemanticHighlightingCalculator {

    private final MncSemanticRegionProvider regions =
            new MncSemanticRegionProvider();

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
