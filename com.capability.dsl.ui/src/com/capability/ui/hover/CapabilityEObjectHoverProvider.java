package com.capability.ui.hover;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider;

import com.capability.ide.hover.CapabilityHoverTextProvider;
import com.capability.ide.hover.CapabilityHoverTextProvider.HoverText;

/** Eclipse adapter over the client-neutral Capability hover descriptions. */
public class CapabilityEObjectHoverProvider extends DefaultEObjectHoverProvider {

    private final CapabilityHoverTextProvider text = new CapabilityHoverTextProvider();

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
