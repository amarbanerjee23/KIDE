package com.kide.glsp;

import java.util.Optional;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.glsp.graph.GEdge;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.glsp.server.emf.EMFOperationHandler;
import org.eclipse.glsp.server.emf.model.notation.NotationElement;
import org.eclipse.glsp.server.emf.model.notation.NotationPackage;
import org.eclipse.glsp.server.emf.notation.EMFNotationModelState;
import org.eclipse.glsp.server.operations.DeleteOperation;

import activityDiagramModel.Activity;
import activityDiagramModel.ActivityDiagramModelPackage;
import mncModel.ControlNode;
import mncModel.InterfaceDescription;
import mncModel.MncModelPackage;

public final class KideDeleteHandler extends EMFOperationHandler<DeleteOperation> {
    @Override
    public Optional<Command> createCommand(DeleteOperation operation) {
        CompoundCommand commands = new CompoundCommand("Delete graphical elements");
        for (String id : operation.getElementIds()) {
            GModelElement graphical = modelState.getIndex().get(id).orElse(null);
            if (graphical instanceof GEdge edge) {
                appendDeleteEdge(commands, edge);
            } else {
                appendDeleteNode(commands, id);
            }
        }
        return commands.isEmpty() ? doNothing() : Optional.of(commands);
    }

    private void appendDeleteNode(CompoundCommand commands, String id) {
        EObject semantic = modelState.getIndex().getEObject(id).orElse(null);
        if (semantic == null || semantic.eContainer() == null) return;
        if (modelState instanceof EMFNotationModelState notationState) {
            NotationElement notation = notationState.getIndex().getNotation(semantic).orElse(null);
            if (notation != null && notation.eContainer() != null) {
                commands.append(RemoveCommand.create(
                        modelState.getEditingDomain(), notation));
            }
        }
        commands.append(RemoveCommand.create(modelState.getEditingDomain(), semantic));
    }

    private void appendDeleteEdge(CompoundCommand commands, GEdge edge) {
        EObject source = modelState.getIndex().getEObject(edge.getSourceId()).orElse(null);
        EObject target = modelState.getIndex().getEObject(edge.getTargetId()).orElse(null);
        if (source == null || target == null) return;

        if (KideDiagramTypes.ACTIVITY_NEXT.equals(edge.getType())
                && source instanceof Activity activity && activity.getNextActivity() == target) {
            commands.append(SetCommand.create(
                    modelState.getEditingDomain(), activity,
                    ActivityDiagramModelPackage.Literals.ACTIVITY__NEXT_ACTIVITY, null));
        } else if (KideDiagramTypes.MNC_USES.equals(edge.getType())
                && source instanceof InterfaceDescription iface
                && target instanceof InterfaceDescription used) {
            commands.append(RemoveCommand.create(
                    modelState.getEditingDomain(), iface,
                    MncModelPackage.Literals.INTERFACE_DESCRIPTION__USES, used));
        } else if (KideDiagramTypes.MNC_IMPLEMENTS.equals(edge.getType())
                && source instanceof ControlNode node && node.getInterfaceDescription() == target) {
            commands.append(SetCommand.create(
                    modelState.getEditingDomain(), node,
                    MncModelPackage.Literals.CONTROL_NODE__INTERFACE_DESCRIPTION, null));
        } else if (KideDiagramTypes.MNC_CHILD.equals(edge.getType())
                && source instanceof ControlNode parent && target instanceof ControlNode child) {
            commands.append(RemoveCommand.create(
                    modelState.getEditingDomain(), parent,
                    MncModelPackage.Literals.CONTROL_NODE__CHILD_NODES, child));
        }
    }
}
