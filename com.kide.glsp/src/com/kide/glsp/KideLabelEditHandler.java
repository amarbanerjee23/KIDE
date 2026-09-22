package com.kide.glsp;

import java.util.Optional;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.glsp.server.emf.EMFOperationHandler;
import org.eclipse.glsp.server.features.directediting.ApplyLabelEditOperation;

public final class KideLabelEditHandler extends EMFOperationHandler<ApplyLabelEditOperation> {
    @Override
    public Optional<Command> createCommand(ApplyLabelEditOperation operation) {
        String labelId = operation.getLabelId();
        if (labelId == null || !labelId.endsWith("_label")) return doNothing();
        String semanticId = labelId.substring(0, labelId.length() - "_label".length());
        EObject semantic = modelState.getIndex().getEObject(semanticId).orElse(null);
        if (semantic == null) return doNothing();
        EStructuralFeature name = semantic.eClass().getEStructuralFeature("name");
        if (name == null || !name.isChangeable()) return doNothing();
        String text = operation.getText() == null ? "" : operation.getText().trim();
        if (text.isEmpty()) return doNothing();
        return Optional.of(SetCommand.create(
                modelState.getEditingDomain(), semantic, name, text));
    }
}
