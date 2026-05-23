package com.model.domain.mnc.design.layout;

import org.eclipse.sirius.diagram.description.DiagramElementMapping;
import org.eclipse.sirius.diagram.ui.tools.api.layout.ordering.ViewOrdering;
import org.eclipse.sirius.diagram.ui.tools.api.layout.ordering.ViewOrderingProvider;

public class CustomViewOrderingProvider implements ViewOrderingProvider {

	@Override
	public boolean provides(DiagramElementMapping mapping) {
	
		return false;
	}

	@Override
	public ViewOrdering getViewOrdering(DiagramElementMapping mapping) {
		
		return null;
	}
	

}
