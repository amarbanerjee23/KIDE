package com.kide.glsp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.glsp.server.actions.TriggerEdgeCreationAction;
import org.eclipse.glsp.server.actions.TriggerNodeCreationAction;
import org.eclipse.glsp.server.features.toolpalette.PaletteItem;
import org.eclipse.glsp.server.features.toolpalette.ToolPaletteItemProvider;

public final class KideToolPaletteItemProvider implements ToolPaletteItemProvider {
    @Override
    public List<PaletteItem> getItems(Map<String, String> args) {
        List<PaletteItem> nodes = new ArrayList<>();
        List<PaletteItem> relations = new ArrayList<>();
        String diagramType = args == null ? "" : args.getOrDefault("diagramType", "");
        if (KideDiagramTypes.ACTIVITY_DIAGRAM.equals(diagramType)) {
            nodes.add(node(KideDiagramTypes.ACTIVITY, "Activity"));
            relations.add(edge(KideDiagramTypes.ACTIVITY_NEXT, "Next activity"));
        } else {
            // An MNC file already contains its required singleton InterfaceDescription.
            // The only grammar-valid top-level node creation is its optional ControlNode.
            nodes.add(node(KideDiagramTypes.MNC_CONTROL_NODE, "Control node"));
            relations.add(edge(KideDiagramTypes.MNC_USES, "Uses"));
            relations.add(edge(KideDiagramTypes.MNC_IMPLEMENTS, "Implements"));
            relations.add(edge(KideDiagramTypes.MNC_CHILD, "Child node"));
        }
        return List.of(
                PaletteItem.createPaletteGroup("kide.nodes", "Nodes", nodes, "symbol-class"),
                PaletteItem.createPaletteGroup("kide.relations", "Relations", relations, "symbol-method"));
    }

    private static PaletteItem node(String type, String label) {
        return new PaletteItem(type, label, new TriggerNodeCreationAction(type));
    }

    private static PaletteItem edge(String type, String label) {
        return new PaletteItem(type, label, new TriggerEdgeCreationAction(type));
    }
}
