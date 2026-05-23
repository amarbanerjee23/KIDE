package com.model.domain.mnc.design.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

import com.model.domain.mnc.design.services.BlockDiagramServices;
import com.model.domain.mnc.design.services.diagramnavigation.NavigateToDiagram;
import com.model.domain.mnc.design.services.models.AllModelFinder;

import mncModel.InterfaceDescription;
import mncModel.Model;
import swt.SWTResourceManager;;

public class ModelSelectionWizard extends Wizard {
	
	public static EObject currentContextObject;
	ModelSelectionWizardPage modelSelectionWizardPage;
	
	
	public ModelSelectionWizard() {
		setWindowTitle("Selective Component representation");
	}
	public void setCurrentContextObject(EObject object) {
		currentContextObject=object;
	}

	@Override
	public void addPages() {
		modelSelectionWizardPage=new ModelSelectionWizardPage();
		modelSelectionWizardPage.setCurrentContextObject(currentContextObject);
		addPage(modelSelectionWizardPage);
	}

	@Override
	public boolean performFinish() {
		BlockDiagramServices.setSemanticCandidates(modelSelectionWizardPage.getSelectedModels());
		NavigateToDiagram navigateToDiagram=new NavigateToDiagram();
		navigateToDiagram.createBlockDiagram((Model)currentContextObject);
		return true;
	}

}

class ModelSelectionWizardPage extends WizardPage {

	/**
	 * Create the wizard.
	 */
	List list;
	public static EObject currentContextObject;
	AllModelFinder allModelFinder;
	
	public ModelSelectionWizardPage() {
		super("Selective component wizard page");
		setTitle("Select component from list");
		setDescription("Select components from list, You can select more than one component as well");
	}
	public void setCurrentContextObject(EObject object) {
		currentContextObject=object;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	
	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		list = new List(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		for (String modelName : getAllModels()) {
			list.add(modelName);
		}	
		list.select(0);
		list.setBounds(218, 10, 240, 194);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblNewLabel.setFont(SWTResourceManager.getFont("Verdana", 11, SWT.NORMAL));
		lblNewLabel.setBounds(10, 41, 202, 39);
		lblNewLabel.setText("Select Elements from List");
	}
	
	public ArrayList<String> getAllModels()
	{
		allModelFinder=new AllModelFinder();
		HashMap<InterfaceDescription, Model> modelData=allModelFinder.getAllModels();
		ArrayList<String> list=new ArrayList<String>();
		
		for (Entry<InterfaceDescription, Model> entry : modelData.entrySet()) {
		    InterfaceDescription key = entry.getKey();
		    list.add(""+key.getName());
		}	
		return list;
	}
	
	public void getAllProjectModels(EObject currentContext)
	{
		
	}
	
	public BasicEList<Model> getSelectedModels()
	{
		getAllProjectModels(currentContextObject);	
		HashMap<InterfaceDescription, Model> modelData;
		BasicEList<Model> semanticList=new BasicEList<Model>();
		
		int count[]=list.getSelectionIndices();
		for (int i = 0; i < count.length; i++) {
			
			
			modelData=allModelFinder.getAllModels();			
			for (Entry<InterfaceDescription, Model> entry : modelData.entrySet()) {
			    InterfaceDescription key = entry.getKey();
			    if(key.getName().equals(list.getItem(count[i])))
			    {
			    	semanticList.add(modelData.get(key));
			    }
			}
		}
		return semanticList;
	}
	
}


