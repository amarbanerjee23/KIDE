package com.model.domain.mnc.design.ui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import swt.SWTResourceManager;

public class ModelInformationWizard extends Wizard{
	
	SimpleWizardPage simpleWizardPage;	
	public static EObject currentContextObject;
	
	public ModelInformationWizard() {
		setWindowTitle("Add More Elements");
		simpleWizardPage =new SimpleWizardPage();
	}
	
	public void setCurrentContextObject(EObject object) {
		currentContextObject=object;
		simpleWizardPage.setCurrentContextObject(object);
	}

	@Override
	public void addPages() {			
		addPage(simpleWizardPage);
	}

	@Override
	public boolean performFinish() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		int noOfFiles=Integer.parseInt(simpleWizardPage.text.getText());
		for (int i = 0; i < noOfFiles; i++) 
		{
			
			try 
			{	
				File fileExistancecheck;
				do{
					fileExistancecheck=new File(""+workspace.getRoot().getLocation()+"/"+currentContextObject.eResource().getURI().toString().substring(19,currentContextObject.eResource().getURI().toString().lastIndexOf('/'))+"/ID"+(i+1)+".mncspec");
					i++; noOfFiles++;
				} while (fileExistancecheck.exists());
				i--;
				noOfFiles--;
				
				FileWriter file = new FileWriter(""+workspace.getRoot().getLocation()+"/"+currentContextObject.eResource().getURI().toString().substring(19,currentContextObject.eResource().getURI().toString().lastIndexOf('/'))+"/ID"+(i+1)+".mncspec");
				putFileContent(file,(i+1),simpleWizardPage.text_1.getText());						
				file.close();
				
				for(IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
				{
							if(currentContextObject.eResource().getURI().toString().substring(19,currentContextObject.eResource().getURI().toString().lastIndexOf('/')).equals(""+project.getName()))
							{
								System.out.println("Refresing project");
								 try 
								 {
									project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
								 } 
								 catch (CoreException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								 break;
							}
						   
				}
				
			} 
			catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
		}
		
		return true;
	}
	
	private void putFileContent(FileWriter file, int index,String modelName) {
		// TODO Auto-generated method stub
		String s="Model "+modelName+"\n"+
				"InterfaceDescription ID"+index+"\n" +
				"{\n\t"+
			"dataPoints{\n\n"+

			"\t}\n\n\t"+
			"alarms{\n\n"+

			"\t}\n\n\t"+
			"commands{\n\n"+

			"\t}\n\n\t"+
			"events{\n\n"+

			"\t}\n\n\t"+
			"responses{\n\n"+

			"\t}\n\n\t"+
			"operatingStates{\n\n"+

			"\t}\n\n\t"+
			"SubscribableItemList{\n\n"+

			"\t}\n\n"+

				"}\n"+
		"ControlNode CN"+index+"\n"+
		"{\n"+
		"	Associated Interface Description:ID"+index+"\n"+
		"	AlarmBlock{		\n\n"+

		"\t}\n\n\t"+
		"CommandResponseBlock{\n\n"+

		"\t}\n\n\t"+
		"DataPointBlock{\n\n"+

		"\t}\n\n\t"+
		"EventBlock{\n\n"+

		"\t}\n\n"+
		"}\n";
		try {
			file.write(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
	}

}

class SimpleWizardPage extends WizardPage {
	public Text text;
	public Text text_1;
	public static EObject currentContextObject;

	/**
	 * Create the wizard.
	 */
	public SimpleWizardPage() {
		super("wizardPage");		
		setTitle("Add More Elements");
		setDescription("Enter the information of model to add more elements to project");
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
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setBounds(10, 10, 421, 192);
		
		Label label = new Label(composite, SWT.NONE);
		label.setText("How many elements you want to add ?");
		label.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		label.setFont(SWTResourceManager.getFont("Verdana", 12, SWT.NORMAL));
		label.setBounds(26, 10, 340, 29);
		
		text = new Text(composite, SWT.BORDER);
		text.setFont(SWTResourceManager.getFont("Verdana", 12, SWT.NORMAL));
		text.setBounds(258, 45, 76, 29);
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setText("Enter Model Name");
		label_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		label_1.setFont(SWTResourceManager.getFont("Verdana", 12, SWT.NORMAL));
		label_1.setBounds(26, 100, 166, 29);
		
		text_1 = new Text(composite, SWT.BORDER);
		text_1.setFont(SWTResourceManager.getFont("Verdana", 12, SWT.NORMAL));
		text_1.setBounds(195, 100, 156, 29);
	}

}
