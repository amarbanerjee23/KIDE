package com.capability.ui.highlighting;

import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;

import com.capability.ide.highlighting.CapabilitySemanticRegionProvider;
import com.capability.ide.highlighting.CapabilitySemanticRegionProvider.SemanticRegion;

/** Eclipse adapter over the platform-neutral Capability semantic regions. */
public class CapabilitySemanticHighlightingCalculator implements ISemanticHighlightingCalculator {

    private final CapabilitySemanticRegionProvider regionProvider = new CapabilitySemanticRegionProvider();

    @Override
    public void provideHighlightingFor(XtextResource resource, IHighlightedPositionAcceptor acceptor) {
        for (SemanticRegion region : regionProvider.getSemanticRegions(resource)) {
            acceptor.addPosition(region.getOffset(), region.getLength(), region.getKind());
        }
    }
}
