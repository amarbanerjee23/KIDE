package com.kide.glsp;

import java.util.Optional;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.glsp.server.emf.EMFCreateOperationHandler;
import org.eclipse.glsp.server.operations.CreateEdgeOperation;

import activityDiagramModel.Activity;
import activityDiagramModel.ActivityDiagramModelPackage;
import mncModel.ControlNode;
import mncModel.InterfaceDescription;
import mncModel.MncModelPackage;

public final class KideCreateEdgeHandler extends EMFCreateOperationHandler<CreateEdgeOperation> {
    public KideCreateEdgeHandler() {
        super(KideDiagramTypes.ACTIVITY_NEXT, KideDiagramTypes.MNC_USES,
                KideDiagramTypes.MNC_IMPLEMENTS, KideDiagramTypes.MNC_CHILD);
    }

    @Override
    public Optional<Command> createCommand(CreateEdgeOperation operation) {
        EObject source = modelState.getIndex().getEObject(operation.getSourceElementId()).orElse(null);
        EObject target = modelState.getIndex().getEObject(operation.getTargetElementId()).orElse(null);
        if (source == null || target == null) return doNothing();

        String type = operation.getElementTypeId();
        if (KideDiagramTypes.ACTIVITY_NEXT.equals(type)
                && source instanceof Activity from && target instanceof Activity to) {
            return Optional.of(SetCommand.create(
                    modelState.getEditingDomain(), from,
                    ActivityDiagramModelPackage.Literals.ACTIVITY__NEXT_ACTIVITY, to));
        }
        if (KideDiagramTypes.MNC_USES.equals(type)
                && source instanceof InterfaceDescription from
                && target instanceof InterfaceDescription to) {
            if (from.getUses().contains(to)) return doNothing();
            return Optional.of(AddCommand.create(
                    modelState.getEditingDomain(), from,
                    MncModelPackage.Literals.INTERFACE_DESCRIPTION__USES, to));
        }
        if (KideDiagramTypes.MNC_IMPLEMENTS.equals(type)
                && source instanceof ControlNode from
                && target instanceof InterfaceDescription to) {
            return Optional.of(SetCommand.create(
                    modelState.getEditingDomain(), from,
                    MncModelPackage.Literals.CONTROL_NODE__INTERFACE_DESCRIPTION, to));
        }
        if (KideDiagramTypes.MNC_CHILD.equals(type)
                && source instanceof ControlNode from && target instanceof ControlNode to
                && from != to && !from.getChildNodes().contains(to)) {
            return Optional.of(AddCommand.create(
                    modelState.getEditingDomain(), from,
                    MncModelPackage.Literals.CONTROL_NODE__CHILD_NODES, to));
        }
        return doNothing();
    }
}
