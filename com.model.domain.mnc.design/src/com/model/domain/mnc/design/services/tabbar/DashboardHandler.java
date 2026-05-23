package com.model.domain.mnc.design.services.tabbar;

import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.model.domain.mnc.design.services.diagramnavigation.NavigateToDiagram;
import com.model.domain.mnc.design.services.models.AllModelFinder;

import mncModel.InterfaceDescription;
import mncModel.Model;

public class DashboardHandler extends AbstractHandler {

	NavigateToDiagram navigateToDiagram;
	public static final String DASHBOARD_REPRESENTATION = "DashBoard";

	// It executes when dashboard button clicked form tabbar
	@Override 
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Model model = null;
		System.out.println("DashboardHadnler called");
		final boolean generate = MessageDialog.openConfirm(HandlerUtil.getActiveShell(event), "Generate Graphical View",
				"Do you want to Graphical View for the MnC Specifications ?");
		if ((!generate)) {
			return null; 
		}
		final IEditorPart activeEditor = HandlerUtil.getActiveEditor(event);
		IFile _adapter = activeEditor.getEditorInput().<IFile> getAdapter(IFile.class);
		final IFile file = ((IFile) _adapter);
		if ((file != null)) {
			final IProject project = file.getProject();
	 		try {
				navigateToDiagram = new NavigateToDiagram();

				AllModelFinder allModelFinder = new AllModelFinder(project.getName());
				HashMap<InterfaceDescription, Model> modelData = allModelFinder.getAllModels();
				for (Entry<InterfaceDescription, Model> entry : modelData.entrySet()) {
					model = entry.getValue();
					break;
				}

				navigateToDiagram.createRepresentation(model, DASHBOARD_REPRESENTATION);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return model;
	}

}
