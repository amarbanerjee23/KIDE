package com.smr.activity.dsl.ui.highlighting;

import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;

import com.smr.activity.dsl.ide.highlighting.ActivityDiagramSemanticRegionProvider;
import com.smr.activity.dsl.ide.highlighting.ActivityDiagramSemanticRegionProvider.SemanticRegion;

/** Eclipse adapter over the platform-neutral Activity semantic regions. */
public class ActivityDiagramSemanticHighlightingCalculator implements ISemanticHighlightingCalculator {

    private final ActivityDiagramSemanticRegionProvider regionProvider = new ActivityDiagramSemanticRegionProvider();

    @Override
    public void provideHighlightingFor(XtextResource resource, IHighlightedPositionAcceptor acceptor) {
        for (SemanticRegion region : regionProvider.getSemanticRegions(resource)) {
            acceptor.addPosition(region.getOffset(), region.getLength(), region.getKind());
        }
    }
}
