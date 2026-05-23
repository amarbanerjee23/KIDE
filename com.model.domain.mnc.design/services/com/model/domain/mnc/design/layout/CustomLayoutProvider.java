package com.model.domain.mnc.design.layout;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.services.layout.AbstractLayoutEditPartProvider;
import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.LayoutConstraint;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.diagram.DDiagram;
import org.eclipse.sirius.diagram.DDiagramElement;
import org.eclipse.sirius.diagram.description.DiagramDescription;
import org.eclipse.sirius.diagram.ui.edit.api.part.AbstractDDiagramEditPart;
import org.eclipse.sirius.diagram.ui.tools.api.layout.provider.AbstractLayoutProvider;
import org.eclipse.sirius.diagram.ui.tools.api.layout.provider.LayoutProvider;

import com.google.common.collect.Iterables;

public class CustomLayoutProvider implements LayoutProvider {
	

	public CustomLayoutProvider() {
	}

	/** The GMF layout provider. */
	private AbstractLayoutEditPartProvider layoutProvider;

	@Override
	public AbstractLayoutEditPartProvider getLayoutNodeProvider(IGraphicalEditPart container) {
		if (isProcessDiagram(container)) {
			if (this.layoutProvider == null) {
				this.layoutProvider = new CustomLayout();
			}
		}

		return this.layoutProvider;
	}

	@Override
	public boolean isDiagramLayoutProvider() {
		return false;
	}

	@Override
	public boolean provides(IGraphicalEditPart container) {
		return isProcessDiagram(container);
	}

	//restricting custom layout for all diagrams
	private boolean isProcessDiagram(IGraphicalEditPart container) {
	if (container instanceof AbstractDDiagramEditPart) {
			AbstractDDiagramEditPart editPart = (AbstractDDiagramEditPart) container;
			if (editPart.resolveSemanticElement() instanceof DDiagram) {
				DDiagram diagram = (DDiagram) editPart.resolveSemanticElement();
				if (diagram.getDescription() != null) {
					DiagramDescription diagramDescription = diagram.getDescription();
					String name = diagramDescription.getName();
					//here state Diagram1 is diagram name
					if (name.equals("State Diagram1")) {
						return true;
					}
				}
			}
		}
		return false;
	}

	//setting bounds of each component
	class CustomLayout extends AbstractLayoutProvider {
		@Override
		public Command layoutEditParts(List selectedObjects, IAdaptable layoutHint) {
			if (selectedObjects.isEmpty()) {
	                  return UnexecutableCommand.INSTANCE;
	                }

			int index = 0;
			for (IGraphicalEditPart graphicalEditPart : Iterables.filter(selectedObjects, IGraphicalEditPart.class)) {

				EObject semanticElement = graphicalEditPart.resolveSemanticElement();

				if (semanticElement instanceof DDiagramElement) {
					View notationView = graphicalEditPart.getNotationView();
					if (notationView instanceof Node) {
						final Node node = (Node) notationView;
						final LayoutConstraint layoutConstraint = node.getLayoutConstraint();
						if (layoutConstraint instanceof Bounds) {
							final Bounds bounds = (Bounds) layoutConstraint;
							bounds.setHeight(50);
							bounds.setWidth(130);
							bounds.setX(index * 100);
							bounds.setY(index * 100);
						}
					}				
					
				}
				index++;
				// refresh for size of nodes
				graphicalEditPart.refresh();
			}

			return new CompoundCommand();
		}
	}

}