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

import mncModel.ControlNode;
import mncModel.InterfaceDescription;
import mncModel.Model;

public final class MncGModelFactory extends EMFNotationGModelFactory {
    @Override
    protected void fillRootElement(
            EObject semanticModel, Diagram notationModel, GModelRoot newRoot) {
        if (!(semanticModel instanceof Model model)) {
            throw new IllegalStateException("Expected MNC Model semantic root");
        }
        int index = 0;
        for (mncModel.System system : model.getSystems()) {
            if (system instanceof InterfaceDescription iface) {
                addNode(iface, KideDiagramTypes.MNC_INTERFACE,
                        safe(iface.getName(), "Interface"), notationModel, newRoot, index++);
            } else if (system instanceof ControlNode control) {
                addNode(control, KideDiagramTypes.MNC_CONTROL_NODE,
                        safe(control.getName(), "ControlNode"), notationModel, newRoot, index++);
            }
        }
        for (mncModel.System system : model.getSystems()) {
            if (system instanceof InterfaceDescription iface) {
                String source = idGenerator.getOrCreateId(iface);
                for (InterfaceDescription used : iface.getUses()) {
                    addEdge(newRoot, KideDiagramTypes.MNC_USES, source,
                            idGenerator.getOrCreateId(used), "uses");
                }
            } else if (system instanceof ControlNode control) {
                String source = idGenerator.getOrCreateId(control);
                if (control.getInterfaceDescription() != null) {
                    addEdge(newRoot, KideDiagramTypes.MNC_IMPLEMENTS, source,
                            idGenerator.getOrCreateId(control.getInterfaceDescription()), "implements");
                }
                for (ControlNode child : control.getChildNodes()) {
                    addEdge(newRoot, KideDiagramTypes.MNC_CHILD, source,
                            idGenerator.getOrCreateId(child), "child");
                }
            }
        }
    }

    private void addNode(
            EObject semantic, String type, String label, Diagram notation,
            GModelRoot root, int index) {
        String id = idGenerator.getOrCreateId(semantic);
        GNodeBuilder node = new GNodeBuilder(type)
                .id(id).addCssClass(type.replace(':', '-'))
                .add(new GLabelBuilder("label").id(id + "_label").text(label).build());
        Shape shape = shapeFor(semantic, notation, type, index);
        NotationUtil.applyShapeData(shape, node);
        root.getChildren().add(node.build());
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
        shape.setPosition(GraphUtil.point(40 + (index % 4) * 240, 40 + (index / 4) * 130));
        notation.getElements().add(shape);
        return shape;
    }

    private static void addEdge(
            GModelRoot root, String type, String source, String target, String relation) {
        root.getChildren().add(new GEdgeBuilder(type)
                .id("edge:" + source + ":" + relation + ":" + target)
                .sourceId(source).targetId(target).build());
    }

    private static String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
