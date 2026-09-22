package com.kide.glsp;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.glsp.server.emf.EMFModelState;
import org.eclipse.glsp.server.features.validation.Marker;
import org.eclipse.glsp.server.features.validation.MarkerKind;
import org.eclipse.glsp.server.features.validation.ModelValidator;

import com.google.inject.Inject;

import activityDiagramModel.Activity;
import mncModel.ControlNode;
import mncModel.InterfaceDescription;

public final class KideGlspModelValidator implements ModelValidator {
    @Inject
    protected EMFModelState modelState;

    @Override
    public List<Marker> doLiveValidation(GModelElement element) {
        List<Marker> markers = new ArrayList<>();
        EObject semantic = modelState.getIndex().getEObject(element.getId()).orElse(null);
        if (semantic instanceof Activity activity
                && (activity.getName() == null || activity.getName().isBlank())) {
            markers.add(error("Unnamed activity",
                    "Activities require a name.", element.getId()));
        } else if (semantic instanceof InterfaceDescription iface
                && (iface.getName() == null || iface.getName().isBlank())) {
            markers.add(error("Unnamed interface",
                    "MNC interfaces require a name.", element.getId()));
        } else if (semantic instanceof ControlNode control) {
            if (control.getName() == null || control.getName().isBlank()) {
                markers.add(error("Unnamed control node",
                        "Control nodes require a name.", element.getId()));
            }
            if (control.getInterfaceDescription() == null) {
                markers.add(error("Missing implemented interface",
                        "A control node must implement an interface.", element.getId()));
            }
        }
        return markers;
    }

    @Override
    public List<Marker> doBatchValidation(GModelElement element) {
        return doLiveValidation(element);
    }

    private static Marker error(String label, String description, String elementId) {
        return new Marker(label, description, elementId, MarkerKind.ERROR);
    }
}
