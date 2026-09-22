package com.kide.glsp;

import java.util.List;
import org.eclipse.glsp.server.diagram.BaseDiagramConfiguration;
import org.eclipse.glsp.server.types.EdgeTypeHint;
import org.eclipse.glsp.server.types.ShapeTypeHint;

public final class ActivityDiagramConfiguration extends BaseDiagramConfiguration {
    @Override
    public List<ShapeTypeHint> getShapeTypeHints() {
        return List.of(new ShapeTypeHint(
                KideDiagramTypes.ACTIVITY, true, true, true, false));
    }

    @Override
    public List<EdgeTypeHint> getEdgeTypeHints() {
        return List.of(new EdgeTypeHint(
                KideDiagramTypes.ACTIVITY_NEXT, false, true, false,
                List.of(KideDiagramTypes.ACTIVITY),
                List.of(KideDiagramTypes.ACTIVITY)));
    }
    @Override
    public boolean needsClientLayout() {
        // KIDE persists graphical bounds in the shared .notation sidecar.
        // Browser clients therefore do not compute semantic model bounds.
        return false;
    }

}
