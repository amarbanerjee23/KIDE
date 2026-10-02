package com.capability.ide;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.ide.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalProvider;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.capability.validation.CapabilityValidator;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.inject.Inject;

import CapabilityDescription.Capability;
import mncModel.AbstractInterfaceItems;
import mncModel.ActionAlarm;
import mncModel.ActionCommand;
import mncModel.ActionDataPoint;
import mncModel.ActionEvent;
import mncModel.Alarm;
import mncModel.Command;
import mncModel.DataPoint;
import mncModel.Event;

/**
 * Headless equivalent of the semantic filtering performed by the Eclipse
 * Capability proposal provider.
 */
public final class KideCapabilityIdeContentProposalProvider
        extends IdeContentProposalProvider {

    @Inject
    private IQualifiedNameProvider names;

    @Override
    protected Predicate<IEObjectDescription> getCrossrefFilter(
            CrossReference reference,
            ContentAssistContext context) {
        Predicate<IEObjectDescription> base = super.getCrossrefFilter(reference, context);
        EObject model = model(context);
        if (model == null) {
            return base;
        }
        EReference feature = GrammarUtil.getReference(reference, model.eClass());
        if (feature == null) {
            return base;
        }
        Capability capability = EcoreUtil2.getContainerOfType(model, Capability.class);
        if (capability == null || capability.getComponentInterface() == null) {
            return base;
        }

        Collection<? extends EObject> allowed = switch (feature.getName()) {
            case "command" -> model instanceof ActionCommand
                    ? CapabilityValidator.getCommandsFromAllInterfaces(
                            capability.getComponentInterface())
                    : null;
            case "alarm" -> model instanceof ActionAlarm
                    ? CapabilityValidator.getAlarmsFromAllInterfaces(
                            capability.getComponentInterface())
                    : null;
            case "event" -> model instanceof ActionEvent
                    ? CapabilityValidator.getEventsFromAllInterfaces(
                            capability.getComponentInterface())
                    : null;
            case "dataPoint" -> model instanceof ActionDataPoint
                    ? CapabilityValidator.getDataPointsFromAllInterfaces(
                            capability.getComponentInterface())
                    : null;
            default -> null;
        };
        return allowed == null
                ? base
                : Predicates.and(base, allowed(allowed));
    }

    private Predicate<IEObjectDescription> allowed(Collection<? extends EObject> values) {
        Set<QualifiedName> allowed = new HashSet<>();
        for (EObject value : values) {
            QualifiedName name = names.getFullyQualifiedName(value);
            if (name != null) {
                allowed.add(name);
            }
        }
        return candidate -> allowed.contains(candidate.getQualifiedName());
    }

    private static EObject model(ContentAssistContext context) {
        return context.getCurrentModel() != null
                ? context.getCurrentModel()
                : context.getPreviousModel();
    }
}
