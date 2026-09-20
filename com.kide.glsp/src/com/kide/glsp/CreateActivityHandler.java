package com.kide.glsp;

import java.util.Optional;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.glsp.server.emf.EMFCreateOperationHandler;
import org.eclipse.glsp.server.operations.CreateNodeOperation;

import activityDiagramModel.Activity;
import activityDiagramModel.ActivityDiagram;
import activityDiagramModel.ActivityDiagramModelFactory;
import activityDiagramModel.ActivityDiagramModelPackage;

public final class CreateActivityHandler extends EMFCreateOperationHandler<CreateNodeOperation> {
    public CreateActivityHandler() { super(KideDiagramTypes.ACTIVITY); }

    @Override
    public Optional<Command> createCommand(CreateNodeOperation operation) {
        if (!(modelState.getSemanticModel() instanceof ActivityDiagram root)) return doNothing();
        Activity activity = ActivityDiagramModelFactory.eINSTANCE.createActivity();
        String requested = operation.getArgs().get("name");
        activity.setName(requested == null || requested.isBlank()
                ? "Activity" + (root.getActivities().size() + 1) : requested.trim());
        // Keep a newly-created Activity serializable by the existing Xtext grammar
        // without inventing a browser-only semantic default.
        activity.setRequiredCapability("Unbound");
        activity.setNextActivity(activity);
        return Optional.of(AddCommand.create(
                modelState.getEditingDomain(),
                root,
                ActivityDiagramModelPackage.Literals.ACTIVITY_DIAGRAM__ACTIVITIES,
                activity));
    }
}
