package com.mncml.dsl.ui.hyperlink;

import com.google.inject.Inject;
import com.google.inject.Provider;
import java.rmi.server.Operation;
import mncModel.MncModelPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.Region;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.hyperlinking.HyperlinkHelper;
import org.eclipse.xtext.ui.editor.hyperlinking.IHyperlinkAcceptor;
import org.eclipse.xtext.ui.editor.hyperlinking.XtextHyperlink;

@SuppressWarnings("all")
public class MncHyperlinkHelper extends HyperlinkHelper {
  @Inject
  private Provider<XtextHyperlink> hyperlinkProvider;
  
  @Inject
  private EObjectAtOffsetHelper eObjectAtOffsetHelper;
  
  @Override
  public void createHyperlinksByOffset(final XtextResource resource, final int offset, final IHyperlinkAcceptor acceptor) {
    super.createHyperlinksByOffset(resource, offset, acceptor);
    EObject eObject = this.eObjectAtOffsetHelper.resolveElementAt(resource, offset);
    if ((eObject instanceof Operation)) {
      Operation operation = ((Operation)eObject);
      INode node = NodeModelUtils.findNodesForFeature(((EObject)operation), MncModelPackage.Literals.ACTION_OPERATION__OPERATION).get(0);
      XtextHyperlink hyperlink = this.hyperlinkProvider.get();
      int _offset = node.getOffset();
      int _length = node.getLength();
      Region _region = new Region(_offset, _length);
      hyperlink.setHyperlinkRegion(_region);
      hyperlink.setHyperlinkText("Open included file");
      URI uri = URI.createURI(operation.getOperation());
      hyperlink.setURI(uri);
      acceptor.accept(hyperlink);
    }
  }
}
