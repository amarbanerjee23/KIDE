package com.kide.glsp;

import java.util.Optional;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.glsp.server.emf.EMFCreateOperationHandler;
import org.eclipse.glsp.server.emf.notation.EMFNotationModelState;
import org.eclipse.glsp.server.operations.CreateNodeOperation;

import mncModel.ControlNode;
import mncModel.InterfaceDescription;
import mncModel.MncModelFactory;
import mncModel.MncModelPackage;
import mncModel.Model;

public final class CreateMncControlNodeHandler extends EMFCreateOperationHandler<CreateNodeOperation> {
    public CreateMncControlNodeHandler() { super(KideDiagramTypes.MNC_CONTROL_NODE); }

    @Override
    public Optional<Command> createCommand(CreateNodeOperation operation) {
        if (!(modelState instanceof EMFNotationModelState notationState)
                || !(notationState.getSemanticModel() instanceof Model root)) return doNothing();

        // The canonical grammar permits at most one top-level ControlNode.
        if (root.getSystems().stream().anyMatch(ControlNode.class::isInstance)) {
            return doNothing();
        }

        InterfaceDescription iface = root.getSystems().stream()
                .filter(InterfaceDescription.class::isInstance)
                .map(InterfaceDescription.class::cast)
                .findFirst().orElse(null);
        if (iface == null) return doNothing();

        ControlNode node = MncModelFactory.eINSTANCE.createControlNode();
        String requested = operation.getArgs().get("name");
        node.setName(requested == null || requested.isBlank()
                ? "ControlNode" + (root.getSystems().size() + 1) : requested.trim());

        CompoundCommand commands = new CompoundCommand("Create MNC control node");
        commands.append(AddCommand.create(
                modelState.getEditingDomain(), root,
                MncModelPackage.Literals.MODEL__SYSTEMS, node));
        commands.append(SetCommand.create(
                modelState.getEditingDomain(), node,
                MncModelPackage.Literals.CONTROL_NODE__INTERFACE_DESCRIPTION, iface));
        return Optional.of(commands);
    }
}
