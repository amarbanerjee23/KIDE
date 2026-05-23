package com.mncml.dsl.serializer

import com.google.inject.Inject
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.CrossReference
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.xtext.nodemodel.INode
import org.eclipse.xtext.serializer.diagnostic.ISerializationDiagnostic.Acceptor
import org.eclipse.xtext.serializer.tokens.CrossReferenceSerializer
import mncModel.OperatingState

class MncCrossReferenceSerializer extends CrossReferenceSerializer {
	
	@Inject
	IQualifiedNameProvider qualifiedNameProvider
	
	override serializeCrossRef(EObject eobj, CrossReference cref, EObject eobj2, INode inode, Acceptor acceptor) {
		if(eobj2!==null){
			if(eobj2 instanceof OperatingState)
			{
				return (eobj2 as OperatingState).name
			}
			return qualifiedNameProvider.getFullyQualifiedName(eobj2).toString
		}
	}
 
	override boolean isValid(EObject semanticObject, CrossReference crossref, EObject target, INode node,
		Acceptor errors) {
		return true 
	}
}
