package com.model.domain.mnc.design.services;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sirius.business.api.dialect.DialectManager;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.diagram.DSemanticDiagram;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.model.domain.mnc.design.services.diagramnavigation.NavigateToDiagram;
import com.model.domain.mnc.design.services.models.AllModelFinder;
import com.model.domain.mnc.design.ui.ModelInformationWizard;
import com.model.domain.mnc.design.ui.ModelSelectionWizard;

import mncModel.InterfaceDescription;
import mncModel.Model;

public class DashBoardServices {
	NavigateToDiagram navigateToDiagram;
	AllModelFinder allModelFinder;
	public static final String DASHBOARD_DIAGRAM_DESCRIPTION_ID="DashBoard";
	
	public DashBoardServices() {
		navigateToDiagram=new NavigateToDiagram();
	}
	
	//Check for Dashboard previously exit or not
	public boolean doesDashboardNotExist(EObject any) {
		final Session session = SessionManager.INSTANCE.getSession(any);
		final Collection<DRepresentation> representations = DialectManager.INSTANCE
				.getAllRepresentations(session);
		for (final DRepresentation representation : representations) {
			if (representation instanceof DSemanticDiagram) {
				final DSemanticDiagram diagram = (DSemanticDiagram)representation;
				if (DASHBOARD_DIAGRAM_DESCRIPTION_ID.equals(diagram.getDescription().getName())) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	//Open a wizard window to Add More elements
	public void addMoreElements(Model element) {

		Shell shell=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		ModelInformationWizard modelInformationWizard=new ModelInformationWizard();
		modelInformationWizard.setCurrentContextObject(element);
		WizardDialog dialog = new WizardDialog(shell, modelInformationWizard);
		dialog.open();
	}
	
	//Open a wizard window to select elements for generating selective Component representation
	public void createSelectedElementsBlockDiagram(Model element) {
		allModelFinder=new AllModelFinder();
		
		
		Shell shell=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		ModelSelectionWizard modelInformationWizard=new ModelSelectionWizard();
		modelInformationWizard.setCurrentContextObject(element);
		WizardDialog dialog = new WizardDialog(shell, modelInformationWizard);
		dialog.open();
	}
	
	//creating block diagram with all Components
	public void createBlockDiagram(Model element) {
		
		allModelFinder=new AllModelFinder();
		HashMap<InterfaceDescription, Model> modelData=allModelFinder.getAllModels();
		BasicEList<Model> list=new BasicEList<Model>();
		
		for (Entry<InterfaceDescription, Model> entry : modelData.entrySet()) {
		    list.add(entry.getValue());	
		  }	
		BlockDiagramServices.setSemanticCandidates(list);
		
		navigateToDiagram.createBlockDiagram(element);
	}
	
	public void createInternalBlockDiagram(Model element) {
		navigateToDiagram.createInternalBlockDiagram(element);
	}
	
	public void createStateDiagram(Model element) {
		navigateToDiagram.createStateDiagram(element);		
	}

}
