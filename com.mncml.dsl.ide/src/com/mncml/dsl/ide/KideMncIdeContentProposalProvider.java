package com.mncml.dsl.ide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.inject.Inject;

import mncModel.Alarm;
import mncModel.AlarmBlock;
import mncModel.Command;
import mncModel.CommandResponseBlock;
import mncModel.ControlNode;
import mncModel.DataPoint;
import mncModel.DataPointBlock;
import mncModel.Event;
import mncModel.EventBlock;
import mncModel.InterfaceDescription;
import mncModel.OperatingState;
import mncModel.Transition;

/**
 * Headless equivalent of the model-aware MNC Eclipse content-assist filters.
 *
 * <p>The provider limits block creation to interface items that are actually
 * available to the associated interface and have not already been represented,
 * and limits transition states to the associated interface's declared states.</p>
 */
public final class KideMncIdeContentProposalProvider
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

        Collection<? extends EObject> candidates = candidates(model, feature.getName());
        return candidates == null
                ? base
                : Predicates.and(base, allowed(candidates));
    }

    private static Collection<? extends EObject> candidates(
            EObject model,
            String feature) {
        ControlNode node = EcoreUtil2.getContainerOfType(model, ControlNode.class);
        if (node == null || node.getInterfaceDescription() == null) {
            return null;
        }
        InterfaceDescription descriptor = node.getInterfaceDescription();

        if (model instanceof CommandResponseBlock
                && "command".equals(feature)) {
            List<Command> values = commands(descriptor);
            for (CommandResponseBlock block : node.getCommandResponseBlocks()) {
                if (block.getCommand() != null && block != model) {
                    values.remove(block.getCommand());
                }
            }
            return values;
        }

        if (model instanceof EventBlock && "event".equals(feature)) {
            List<Event> values = events(descriptor);
            for (EventBlock block : node.getEventBlocks()) {
                if (block.getEvent() != null && block != model) {
                    values.remove(block.getEvent());
                }
            }
            return values;
        }

        if (model instanceof AlarmBlock && "alarm".equals(feature)) {
            List<Alarm> values = alarms(descriptor);
            for (AlarmBlock block : node.getAlarmBlocks()) {
                if (block.getAlarm() != null && block != model) {
                    values.remove(block.getAlarm());
                }
            }
            return values;
        }

        if (model instanceof DataPointBlock && "dataPoint".equals(feature)) {
            List<DataPoint> values = dataPoints(descriptor);
            for (DataPointBlock block : node.getDataPointBlocks()) {
                if (block == model) {
                    continue;
                }
                values.removeAll(block.getDataPoint());
            }
            return values;
        }

        if (model instanceof Transition
                && ("currentState".equals(feature) || "nextState".equals(feature))) {
            if (descriptor.getOperatingStatesUtility() == null) {
                return List.of();
            }
            return new ArrayList<OperatingState>(
                    descriptor.getOperatingStatesUtility().getOperatingStates());
        }

        return null;
    }

    private static List<Command> commands(InterfaceDescription descriptor) {
        List<Command> values = new ArrayList<>(descriptor.getCommands());
        for (InterfaceDescription used : descriptor.getUses()) {
            values.addAll(used.getCommands());
        }
        return values;
    }

    private static List<Event> events(InterfaceDescription descriptor) {
        List<Event> values = new ArrayList<>(descriptor.getEvents());
        if (descriptor.getSubscribedItems() != null) {
            values.addAll(descriptor.getSubscribedItems().getSubscribedEvents());
        }
        for (InterfaceDescription used : descriptor.getUses()) {
            values.addAll(used.getEvents());
        }
        return values;
    }

    private static List<Alarm> alarms(InterfaceDescription descriptor) {
        List<Alarm> values = new ArrayList<>(descriptor.getAlarms());
        if (descriptor.getSubscribedItems() != null) {
            values.addAll(descriptor.getSubscribedItems().getSubscribedAlarms());
        }
        for (InterfaceDescription used : descriptor.getUses()) {
            values.addAll(used.getAlarms());
        }
        return values;
    }

    private static List<DataPoint> dataPoints(InterfaceDescription descriptor) {
        List<DataPoint> values = new ArrayList<>(descriptor.getDataPoints());
        if (descriptor.getSubscribedItems() != null) {
            values.addAll(descriptor.getSubscribedItems().getSubscribedDataPoints());
        }
        for (InterfaceDescription used : descriptor.getUses()) {
            values.addAll(used.getDataPoints());
        }
        return values;
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
