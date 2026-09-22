package com.kide.glsp;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.glsp.graph.GModelRoot;
import org.eclipse.glsp.graph.builder.impl.GEdgeBuilder;
import org.eclipse.glsp.graph.builder.impl.GLabelBuilder;
import org.eclipse.glsp.graph.builder.impl.GNodeBuilder;
import org.eclipse.glsp.graph.util.GraphUtil;
import org.eclipse.glsp.server.emf.model.notation.Diagram;
import org.eclipse.glsp.server.emf.model.notation.NotationFactory;
import org.eclipse.glsp.server.emf.model.notation.SemanticElementReference;
import org.eclipse.glsp.server.emf.model.notation.Shape;
import org.eclipse.glsp.server.emf.notation.EMFNotationGModelFactory;
import org.eclipse.glsp.server.emf.notation.util.NotationUtil;

import activityDiagramModel.Activity;
import activityDiagramModel.ActivityDiagram;

public final class ActivityGModelFactory extends EMFNotationGModelFactory {
    @Override
    protected void fillRootElement(
            EObject semanticModel, Diagram notationModel, GModelRoot newRoot) {
        if (!(semanticModel instanceof ActivityDiagram diagram)) {
            throw new IllegalStateException("Expected ActivityDiagram semantic root");
        }
        int index = 0;
        for (Activity activity : diagram.getActivities()) {
            String id = idGenerator.getOrCreateId(activity);
            GNodeBuilder node = new GNodeBuilder(KideDiagramTypes.ACTIVITY)
                    .id(id)
                    .addCssClass("kide-activity")
                    .add(new GLabelBuilder("label")
                            .id(id + "_label")
                            .text(safe(activity.getName(), "Activity"))
                            .build());
            Shape shape = shapeFor(activity, notationModel, KideDiagramTypes.ACTIVITY, index++);
            NotationUtil.applyShapeData(shape, node);
            newRoot.getChildren().add(node.build());
        }
        for (Activity activity : diagram.getActivities()) {
            Activity next = activity.getNextActivity();
            if (next == null) continue;
            String source = idGenerator.getOrCreateId(activity);
            String target = idGenerator.getOrCreateId(next);
            newRoot.getChildren().add(new GEdgeBuilder(KideDiagramTypes.ACTIVITY_NEXT)
                    .id("edge:" + source + ":next:" + target)
                    .sourceId(source)
                    .targetId(target)
                    .addCssClass("kide-activity-next")
                    .build());
        }
    }

    private Shape shapeFor(EObject semantic, Diagram notation, String type, int index) {
        Shape existing = modelState.getIndex().getNotation(semantic, Shape.class).orElse(null);
        if (existing != null) return existing;

        Shape shape = NotationFactory.eINSTANCE.createShape();
        shape.setType(type);
        SemanticElementReference ref =
                NotationFactory.eINSTANCE.createSemanticElementReference();
        ref.setResolvedSemanticElement(semantic);
        ref.setElementId(idGenerator.getOrCreateId(semantic));
        shape.setSemanticElement(ref);
        shape.setPosition(GraphUtil.point(40 + (index % 4) * 220, 40 + (index / 4) * 120));
        notation.getElements().add(shape);
        return shape;
    }

    private static String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
