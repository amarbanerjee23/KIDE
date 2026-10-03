package com.capability.ide.hover;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ide.server.hover.HoverService;

import com.capability.ide.hover.CapabilityHoverTextProvider.HoverText;

/** LSP adapter over the same Capability hover descriptions used by Eclipse. */
public final class CapabilityLspHoverService extends HoverService {

    private final CapabilityHoverTextProvider text = new CapabilityHoverTextProvider();

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
