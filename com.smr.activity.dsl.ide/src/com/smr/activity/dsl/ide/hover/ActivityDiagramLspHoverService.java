package com.smr.activity.dsl.ide.hover;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ide.server.hover.HoverService;

import com.smr.activity.dsl.ide.hover.ActivityDiagramHoverTextProvider.HoverText;

/** LSP adapter over the same Activity hover descriptions used by Eclipse. */
public final class ActivityDiagramLspHoverService extends HoverService {

    private final ActivityDiagramHoverTextProvider text =
            new ActivityDiagramHoverTextProvider();

    @Override
    public String getContents(EObject element) {
        HoverText hover = text.describe(element);
        if (hover == null) {
            return super.getContents(element);
        }
        StringBuilder markdown = new StringBuilder("**")
                .append(hover.title())
                .append("**");
        if (hover.detail() != null && !hover.detail().isBlank()) {
            markdown.append("\n\n").append(hover.detail());
        }
        return markdown.toString();
    }
}
