package com.mncml.dsl.ui.syntaxcoloring;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;
import org.eclipse.xtext.xbase.lib.InputOutput;

@SuppressWarnings("all")
public class MncHighlightingConfiguration extends DefaultHighlightingConfiguration implements IHighlightingConfiguration {
  public static final String CRB_COMMAND = "CRB_command";
  
  public static final String EB_EVENT = "EB_EVENT";
  
  public static final String AB_ALARM = "AB_ALARM";
  
  public static final String DT_DATA = "DT_DATA";
  
  public static final String TR_STATE = "TR_STATE";
  
  @Override
  public void configure(final IHighlightingConfigurationAcceptor acceptor) {
    InputOutput.<String>println("MncHC");
    super.configure(acceptor);
    acceptor.acceptDefaultHighlighting(MncHighlightingConfiguration.CRB_COMMAND, "CRB_command", this.CRB_CommandTextStyle());
    acceptor.acceptDefaultHighlighting(MncHighlightingConfiguration.EB_EVENT, "EB_EVENT", this.EB_EVENTTextStyle());
    acceptor.acceptDefaultHighlighting(MncHighlightingConfiguration.AB_ALARM, "AB_ALARM", this.AB_ALARMTextStyle());
    acceptor.acceptDefaultHighlighting(MncHighlightingConfiguration.DT_DATA, "DT_DATA", this.DT_DATATextStyle());
    acceptor.acceptDefaultHighlighting(MncHighlightingConfiguration.TR_STATE, "TR_STATE", this.TR_STATETextStyle());
  }
  
  public TextStyle CRB_CommandTextStyle() {
    TextStyle _xblockexpression = null;
    {
      TextStyle textStyle = new TextStyle();
      RGB _rGB = new RGB(0, 255, 0);
      textStyle.setColor(_rGB);
      textStyle.setStyle(SWT.ITALIC);
      _xblockexpression = textStyle;
    }
    return _xblockexpression;
  }
  
  public TextStyle EB_EVENTTextStyle() {
    TextStyle _xblockexpression = null;
    {
      TextStyle textStyle = new TextStyle();
      RGB _rGB = new RGB(0, 0, 255);
      textStyle.setColor(_rGB);
      textStyle.setStyle(SWT.ITALIC);
      _xblockexpression = textStyle;
    }
    return _xblockexpression;
  }
  
  public TextStyle AB_ALARMTextStyle() {
    TextStyle _xblockexpression = null;
    {
      TextStyle textStyle = new TextStyle();
      RGB _rGB = new RGB(255, 0, 0);
      textStyle.setColor(_rGB);
      textStyle.setStyle(SWT.ITALIC);
      _xblockexpression = textStyle;
    }
    return _xblockexpression;
  }
  
  public TextStyle DT_DATATextStyle() {
    TextStyle _xblockexpression = null;
    {
      TextStyle textStyle = new TextStyle();
      RGB _rGB = new RGB(0, 0, 255);
      textStyle.setColor(_rGB);
      textStyle.setStyle(SWT.ITALIC);
      _xblockexpression = textStyle;
    }
    return _xblockexpression;
  }
  
  public TextStyle TR_STATETextStyle() {
    TextStyle _xblockexpression = null;
    {
      TextStyle textStyle = new TextStyle();
      RGB _rGB = new RGB(128, 128, 128);
      textStyle.setColor(_rGB);
      textStyle.setStyle(SWT.ITALIC);
      textStyle.setStyle(SWT.BOLD);
      _xblockexpression = textStyle;
    }
    return _xblockexpression;
  }
}
