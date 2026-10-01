package com.mncml.dsl.ui.syntaxcoloring

import com.mncml.dsl.ide.highlighting.MncSemanticRegionProvider
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator

/** Eclipse adapter over the platform-neutral MNC semantic regions. */
class MncSemanticHighlightingCalculator implements ISemanticHighlightingCalculator {

    val MncSemanticRegionProvider regionProvider = new MncSemanticRegionProvider

    override provideHighlightingFor(XtextResource resource, IHighlightedPositionAcceptor acceptor) {
        for (region : regionProvider.getSemanticRegions(resource)) {
            acceptor.addPosition(region.offset, region.length, region.kind)
        }
    }
}
