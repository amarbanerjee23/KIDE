package com.kide.glsp;

import java.util.List;
import org.eclipse.glsp.server.diagram.BaseDiagramConfiguration;
import org.eclipse.glsp.server.types.EdgeTypeHint;
import org.eclipse.glsp.server.types.ShapeTypeHint;

public final class MncDiagramConfiguration extends BaseDiagramConfiguration {
    @Override
    public List<ShapeTypeHint> getShapeTypeHints() {
        return List.of(
                new ShapeTypeHint(KideDiagramTypes.MNC_INTERFACE, true, true, true, false),
                new ShapeTypeHint(KideDiagramTypes.MNC_CONTROL_NODE, true, true, true, false));
    }

    @Override
    public List<EdgeTypeHint> getEdgeTypeHints() {
        return List.of(
                new EdgeTypeHint(KideDiagramTypes.MNC_USES, false, true, false,
                        List.of(KideDiagramTypes.MNC_INTERFACE),
                        List.of(KideDiagramTypes.MNC_INTERFACE)),
                new EdgeTypeHint(KideDiagramTypes.MNC_IMPLEMENTS, false, true, false,
                        List.of(KideDiagramTypes.MNC_CONTROL_NODE),
                        List.of(KideDiagramTypes.MNC_INTERFACE)),
                new EdgeTypeHint(KideDiagramTypes.MNC_CHILD, false, true, false,
                        List.of(KideDiagramTypes.MNC_CONTROL_NODE),
                        List.of(KideDiagramTypes.MNC_CONTROL_NODE)));
    }
}
