package com.mncml.dsl.ui.syntaxcoloring

import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor
import org.eclipse.xtext.ui.editor.utils.TextStyle
import org.eclipse.swt.graphics.RGB
import org.eclipse.swt.SWT
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration

class MncHighlightingConfiguration extends DefaultHighlightingConfiguration implements IHighlightingConfiguration {
	
	 public final static String CRB_COMMAND = "CRB_command"
	 public final static String EB_EVENT = "EB_EVENT"
	 public final static String AB_ALARM = "AB_ALARM"
	 public final static String DT_DATA = "DT_DATA"
	 public final static String TR_STATE = "TR_STATE"
	 
	override configure(IHighlightingConfigurationAcceptor acceptor) {
		println("MncHC")
		super.configure(acceptor)
		acceptor.acceptDefaultHighlighting(CRB_COMMAND, "CRB_command", CRB_CommandTextStyle)
		acceptor.acceptDefaultHighlighting(EB_EVENT , "EB_EVENT", EB_EVENTTextStyle)
		acceptor.acceptDefaultHighlighting(AB_ALARM ,"AB_ALARM", AB_ALARMTextStyle)
		acceptor.acceptDefaultHighlighting(DT_DATA , "DT_DATA", DT_DATATextStyle)
		acceptor.acceptDefaultHighlighting(TR_STATE , "TR_STATE", TR_STATETextStyle)
	}
	
	def CRB_CommandTextStyle() {
		var textStyle = new TextStyle
		textStyle.color = new RGB(00,255,00)
		textStyle.style = SWT.ITALIC
		textStyle
	}
	def EB_EVENTTextStyle() {
		var textStyle = new TextStyle
		textStyle.color = new RGB(0,0,255)
		textStyle.style = SWT.ITALIC
		textStyle
	}
	def AB_ALARMTextStyle() {
		var textStyle = new TextStyle
		textStyle.color = new RGB(255,00,00)
		textStyle.style = SWT.ITALIC
		textStyle
	}
	def DT_DATATextStyle() {
		var textStyle = new TextStyle
		textStyle.color = new RGB(00,00,255)
		textStyle.style = SWT.ITALIC
		textStyle
	}
	
	def TR_STATETextStyle() {
		var textStyle = new TextStyle
		textStyle.color = new RGB(128,128,128)
		textStyle.style = SWT.ITALIC
		textStyle.style = SWT.BOLD
		textStyle
	}
	
}