package com.kide.glsp;

import java.util.Optional;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.glsp.server.emf.EMFCreateOperationHandler;
import org.eclipse.glsp.server.operations.CreateNodeOperation;

import mncModel.InterfaceDescription;
import mncModel.MncModelFactory;
import mncModel.MncModelPackage;
import mncModel.Model;

public final class CreateMncInterfaceHandler extends EMFCreateOperationHandler<CreateNodeOperation> {
    public CreateMncInterfaceHandler() { super(KideDiagramTypes.MNC_INTERFACE); }

    @Override
    public Optional<Command> createCommand(CreateNodeOperation operation) {
        if (!(modelState.getSemanticModel() instanceof Model root)) return doNothing();
        InterfaceDescription iface = MncModelFactory.eINSTANCE.createInterfaceDescription();
        String requested = operation.getArgs().get("name");
        iface.setName(requested == null || requested.isBlank()
                ? "Interface" + (root.getSystems().size() + 1) : requested.trim());
        return Optional.of(AddCommand.create(
                modelState.getEditingDomain(),
                root,
                MncModelPackage.Literals.MODEL__SYSTEMS,
                iface));
    }
}
