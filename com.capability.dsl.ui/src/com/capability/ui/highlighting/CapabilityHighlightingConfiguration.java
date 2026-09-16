package com.capability.ui.highlighting;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

import com.capability.ide.highlighting.CapabilitySemanticRegionProvider;

/**
 * Colours the capability editor with the same palette the KIDE diagrams use:
 * blue for capabilities, amber for interface items, green for data.
 */
public class CapabilityHighlightingConfiguration extends DefaultHighlightingConfiguration {

    public static final String CAPABILITY_NAME_ID = CapabilitySemanticRegionProvider.CAPABILITY_NAME;
    public static final String INTERFACE_ITEM_ID = CapabilitySemanticRegionProvider.INTERFACE_ITEM;
    public static final String STRUCTURAL_KEYWORD_ID = CapabilitySemanticRegionProvider.STRUCTURAL_KEYWORD;

    @Override
    public void configure(IHighlightingConfigurationAcceptor acceptor) {
        super.configure(acceptor);
        acceptor.acceptDefaultHighlighting(CAPABILITY_NAME_ID, "Capability name", capabilityNameTextStyle());
        acceptor.acceptDefaultHighlighting(INTERFACE_ITEM_ID, "Interface item reference", interfaceItemTextStyle());
        acceptor.acceptDefaultHighlighting(STRUCTURAL_KEYWORD_ID, "Structural keyword", structuralKeywordTextStyle());
    }

    public TextStyle capabilityNameTextStyle() {
        TextStyle style = defaultTextStyle().copy();
        style.setColor(new RGB(82, 132, 226));
        style.setStyle(SWT.BOLD);
        return style;
    }

    public TextStyle interfaceItemTextStyle() {
        TextStyle style = defaultTextStyle().copy();
        style.setColor(new RGB(176, 112, 20));
        return style;
    }

    public TextStyle structuralKeywordTextStyle() {
        TextStyle style = defaultTextStyle().copy();
        style.setColor(new RGB(96, 110, 133));
        style.setStyle(SWT.BOLD);
        return style;
    }
}
