package com.smr.activity.dsl.ui.hover;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider;

import com.smr.activity.dsl.ide.hover.ActivityDiagramHoverTextProvider;
import com.smr.activity.dsl.ide.hover.ActivityDiagramHoverTextProvider.HoverText;

/** Eclipse adapter over the client-neutral Activity hover descriptions. */
public class ActivityDiagramEObjectHoverProvider extends DefaultEObjectHoverProvider {

    private final ActivityDiagramHoverTextProvider text =
            new ActivityDiagramHoverTextProvider();

    @Override
    protected String getFirstLine(EObject element) {
        HoverText hover = text.describe(element);
        return hover == null ? super.getFirstLine(element) : html(hover.title());
    }

    @Override
    protected String getDocumentation(EObject element) {
        HoverText hover = text.describe(element);
        if (hover == null || hover.detail() == null || hover.detail().isBlank()) {
            return super.getDocumentation(element);
        }
        return html(hover.detail()).replace("\n", "<br/>");
    }

    private static String html(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
