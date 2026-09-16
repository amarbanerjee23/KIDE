package com.smr.activity.dsl.ui.highlighting;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

import com.smr.activity.dsl.ide.highlighting.ActivityDiagramSemanticRegionProvider;

/**
 * Colours the activity editor with the KIDE palette: amber for activities,
 * blue for the capabilities they require, green for data.
 */
public class ActivityDiagramHighlightingConfiguration extends DefaultHighlightingConfiguration {

    public static final String ACTIVITY_NAME_ID = ActivityDiagramSemanticRegionProvider.ACTIVITY_NAME;
    public static final String CAPABILITY_REFERENCE_ID = ActivityDiagramSemanticRegionProvider.CAPABILITY_REFERENCE;
    public static final String DATA_REFERENCE_ID = ActivityDiagramSemanticRegionProvider.DATA_REFERENCE;

    @Override
    public void configure(IHighlightingConfigurationAcceptor acceptor) {
        super.configure(acceptor);
        acceptor.acceptDefaultHighlighting(ACTIVITY_NAME_ID, "Activity name", activityNameTextStyle());
        acceptor.acceptDefaultHighlighting(CAPABILITY_REFERENCE_ID, "Required capability", capabilityTextStyle());
        acceptor.acceptDefaultHighlighting(DATA_REFERENCE_ID, "Data parameter", dataTextStyle());
    }

    public TextStyle activityNameTextStyle() {
        TextStyle style = defaultTextStyle().copy();
        style.setColor(new RGB(176, 112, 20));
        style.setStyle(SWT.BOLD);
        return style;
    }

    public TextStyle capabilityTextStyle() {
        TextStyle style = defaultTextStyle().copy();
        style.setColor(new RGB(82, 132, 226));
        return style;
    }

    public TextStyle dataTextStyle() {
        TextStyle style = defaultTextStyle().copy();
        style.setColor(new RGB(46, 138, 96));
        return style;
    }
}
