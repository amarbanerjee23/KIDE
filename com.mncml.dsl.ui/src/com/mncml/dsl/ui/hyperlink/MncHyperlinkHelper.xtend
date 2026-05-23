package com.mncml.dsl.ui.hyperlink

import com.google.inject.Inject
import com.google.inject.Provider
import org.eclipse.xtext.resource.EObjectAtOffsetHelper
import org.eclipse.xtext.ui.editor.hyperlinking.HyperlinkHelper
import org.eclipse.xtext.ui.editor.hyperlinking.XtextHyperlink
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.ui.editor.hyperlinking.IHyperlinkAcceptor
import java.rmi.server.Operation
import org.eclipse.xtext.nodemodel.util.NodeModelUtils
import org.eclipse.jface.text.Region
import org.eclipse.emf.common.util.URI
import mncModel.MncModelPackage

class MncHyperlinkHelper extends HyperlinkHelper {
	 @Inject
     private Provider<XtextHyperlink> hyperlinkProvider;
        
     @Inject 
     private EObjectAtOffsetHelper eObjectAtOffsetHelper;
         
	override createHyperlinksByOffset(XtextResource resource, int offset, IHyperlinkAcceptor acceptor) 
	{
		super.createHyperlinksByOffset(resource, offset, acceptor)
	    var eObject = eObjectAtOffsetHelper.resolveElementAt(resource,offset);
	    if(eObject instanceof Operation)
	    {
	    	var operation = eObject
	    	var node = NodeModelUtils.findNodesForFeature(operation, MncModelPackage.Literals.ACTION_OPERATION__OPERATION).get(0)
	    	var hyperlink = hyperlinkProvider.get
        	hyperlink.setHyperlinkRegion(new Region(node.offset,node.length));
        	hyperlink.setHyperlinkText("Open included file");
        	var uri = URI.createURI(operation.operation)
        	hyperlink.setURI(uri)
        	acceptor.accept(hyperlink)
	    }
    }
        
}