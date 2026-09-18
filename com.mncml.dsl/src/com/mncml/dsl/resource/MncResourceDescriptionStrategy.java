package com.mncml.dsl.resource;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionStrategy;
import org.eclipse.xtext.util.IAcceptor;

import mncModel.AbstractInterfaceItems;
import mncModel.InterfaceDescription;

/**
 * Exports stable simple-name aliases for the MNC symbols that KIDE's Capability
 * and Activity DSLs intentionally reference without a model/interface prefix.
 *
 * <p>The default Xtext strategy is retained, so fully qualified names such as
 * {@code Golden.Device.Start} remain available for disambiguation. This class
 * only adds aliases such as {@code Device} and {@code Start}; it never replaces
 * the canonical qualified export.</p>
 */
public final class MncResourceDescriptionStrategy extends DefaultResourceDescriptionStrategy {

    @Override
    public boolean createEObjectDescriptions(
            EObject eObject,
            IAcceptor<IEObjectDescription> acceptor) {
        boolean descend = super.createEObjectDescriptions(eObject, acceptor);

        if (isCrossLanguageSymbol(eObject)) {
            QualifiedName qualified = getQualifiedNameProvider().getFullyQualifiedName(eObject);
            if (qualified != null && qualified.getSegmentCount() > 1) {
                QualifiedName simple = QualifiedName.create(qualified.getLastSegment());
                acceptor.accept(EObjectDescription.create(simple, eObject));
            }
        }

        return descend;
    }

    private boolean isCrossLanguageSymbol(EObject eObject) {
        return eObject instanceof InterfaceDescription
                || eObject instanceof AbstractInterfaceItems;
    }
}
