package com.mncml.dsl.ui.syntaxcoloring

import com.mncml.dsl.ide.highlighting.MncSemanticRegionProvider
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator

/** Eclipse adapter over the platform-neutral MNC semantic regions. */
class MncSemanticHighlightingCalculator implements ISemanticHighlightingCalculator {

    val regionProvider = new MncSemanticRegionProvider

    override provideHighlightingFor(XtextResource resource, IHighlightedPositionAcceptor acceptor) {
        for (region : regionProvider.getSemanticRegions(resource)) {
            acceptor.addPosition(region.offset, region.length, styleId(region.kind))
        }
    }

    private def String styleId(String kind) {
        switch kind {
            case MncSemanticRegionProvider.COMMAND_REFERENCE: MncHighlightingConfiguration.CRB_COMMAND
            case MncSemanticRegionProvider.EVENT_REFERENCE: MncHighlightingConfiguration.EB_EVENT
            case MncSemanticRegionProvider.ALARM_REFERENCE: MncHighlightingConfiguration.AB_ALARM
            case MncSemanticRegionProvider.DATA_REFERENCE: MncHighlightingConfiguration.DT_DATA
            case MncSemanticRegionProvider.STATE_REFERENCE: MncHighlightingConfiguration.TR_STATE
            default: kind
        }
    }
}
