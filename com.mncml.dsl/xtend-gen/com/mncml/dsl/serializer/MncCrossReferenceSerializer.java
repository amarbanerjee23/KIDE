package com.mncml.dsl.serializer;

import com.google.inject.Inject;
import mncModel.OperatingState;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.serializer.diagnostic.ISerializationDiagnostic;
import org.eclipse.xtext.serializer.tokens.CrossReferenceSerializer;

@SuppressWarnings("all")
public class MncCrossReferenceSerializer extends CrossReferenceSerializer {
  @Inject
  private IQualifiedNameProvider qualifiedNameProvider;
  
  @Override
  public String serializeCrossRef(final EObject eobj, final CrossReference cref, final EObject eobj2, final INode inode, final ISerializationDiagnostic.Acceptor acceptor) {
    if ((eobj2 != null)) {
      if ((eobj2 instanceof OperatingState)) {
        return ((OperatingState) eobj2).getName();
      }
      return this.qualifiedNameProvider.getFullyQualifiedName(eobj2).toString();
    }
    return null;
  }
  
  @Override
  public boolean isValid(final EObject semanticObject, final CrossReference crossref, final EObject target, final INode node, final ISerializationDiagnostic.Acceptor errors) {
    return true;
  }
}
