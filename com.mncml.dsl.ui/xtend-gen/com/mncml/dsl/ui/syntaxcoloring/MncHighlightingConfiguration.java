package com.mncml.dsl.ui.syntaxcoloring;

import com.mncml.dsl.ide.highlighting.MncSemanticRegionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

@SuppressWarnings("all")
public class MncHighlightingConfiguration extends DefaultHighlightingConfiguration implements IHighlightingConfiguration {
  public static final String CRB_COMMAND = MncSemanticRegionProvider.COMMAND_REFERENCE;
  public static final String EB_EVENT = MncSemanticRegionProvider.EVENT_REFERENCE;
  public static final String AB_ALARM = MncSemanticRegionProvider.ALARM_REFERENCE;
  public static final String DT_DATA = MncSemanticRegionProvider.DATA_REFERENCE;
  public static final String TR_STATE = MncSemanticRegionProvider.STATE_REFERENCE;

  @Override
  public void configure(final IHighlightingConfigurationAcceptor acceptor) {
    super.configure(acceptor);
    acceptor.acceptDefaultHighlighting(CRB_COMMAND, "CRB_command", CRB_CommandTextStyle());
    acceptor.acceptDefaultHighlighting(EB_EVENT, "EB_EVENT", EB_EVENTTextStyle());
    acceptor.acceptDefaultHighlighting(AB_ALARM, "AB_ALARM", AB_ALARMTextStyle());
    acceptor.acceptDefaultHighlighting(DT_DATA, "DT_DATA", DT_DATATextStyle());
    acceptor.acceptDefaultHighlighting(TR_STATE, "TR_STATE", TR_STATETextStyle());
  }

  public TextStyle CRB_CommandTextStyle() {
    TextStyle textStyle = new TextStyle();
    textStyle.setColor(new RGB(0, 255, 0));
    textStyle.setStyle(SWT.ITALIC);
    return textStyle;
  }

  public TextStyle EB_EVENTTextStyle() {
    TextStyle textStyle = new TextStyle();
    textStyle.setColor(new RGB(0, 0, 255));
    textStyle.setStyle(SWT.ITALIC);
    return textStyle;
  }

  public TextStyle AB_ALARMTextStyle() {
    TextStyle textStyle = new TextStyle();
    textStyle.setColor(new RGB(255, 0, 0));
    textStyle.setStyle(SWT.ITALIC);
    return textStyle;
  }

  public TextStyle DT_DATATextStyle() {
    TextStyle textStyle = new TextStyle();
    textStyle.setColor(new RGB(0, 0, 255));
    textStyle.setStyle(SWT.ITALIC);
    return textStyle;
  }

  public TextStyle TR_STATETextStyle() {
    TextStyle textStyle = new TextStyle();
    textStyle.setColor(new RGB(128, 128, 128));
    textStyle.setStyle(SWT.BOLD);
    return textStyle;
  }
}
