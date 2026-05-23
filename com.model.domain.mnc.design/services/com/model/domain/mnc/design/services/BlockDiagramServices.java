package com.model.domain.mnc.design.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sirius.business.api.dialect.DialectManager;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.diagram.DSemanticDiagram;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com.model.domain.mnc.design.services.diagramnavigation.NavigateToDiagram;
import com.model.domain.mnc.design.ui.ModelInformationWizard;

import mncModel.InterfaceDescription;
import mncModel.Model;

public class BlockDiagramServices {

	//Diagram names to generate programatically
	private static final String InternalBlockDiagram_REPRESENTATION = "InternalBlockDiagram";
	private static final String DataPointDiagram_REPRESENTATION = "DataPointDiagram";
	private static final String AlarmDiagram_REPRESENTATION = "AlarmDiagram";
	private static final String CommandDiagram_REPRESENTATION = "CommandDiagram";
	private static final String EventDiagram_REPRESENTATION = "EventDiagram";
	private static final String ResponseDiagram_REPRESENTATION = "ResponseDiagram";
	private static final String OperatingStateDiagram_REPRESENTATION = "OperatingStateDiagram";
	private static final String CommandResponseBlocksDiagram_REPRESENTATION = "CommandResponseBlocksDiagram";
	private static final String EventBlocksDiagram_REPRESENTATION = "EventBlocksDiagram";
	private static final String AlarmBlocksDiagram_REPRESENTATION = "AlarmBlocksDiagram";
	private static final String DataPointBlocksDiagram_REPRESENTATION = "DataPointBlocksDiagram";
	private static final String BLOCK_DIAGRAM_REPRESENTATION = "BlockDiagram";
	NavigateToDiagram navigateToDiagram;
	public static BasicEList semanticModels;
	
	public static void setSemanticCandidates(BasicEList models)
	{
		semanticModels=models;
	}
	
	
	public static BasicEList getSemanticCandidates(EObject eObject)
	{
	
		return semanticModels;
	}
	
	
	
	public static String DIAGRAM_TITLE="";
	
	//restrict to generate many diagrams with single name for one model for Block Diagram
	public boolean singleBlockRepresentationCheck(EObject any) {		
		DIAGRAM_TITLE=getBlockDiagramTitle(any);
		final Session session = SessionManager.INSTANCE.getSession(any);
		final Collection<DRepresentation> representations = DialectManager.INSTANCE
				.getAllRepresentations(session);
		for (final DRepresentation representation : representations) {
			if (representation instanceof DSemanticDiagram) {
				final DSemanticDiagram diagram = (DSemanticDiagram)representation;
				if (DIAGRAM_TITLE.equals(diagram.getName())) {
					return false;
				}
			}
		}
		return true;
	}
	
	//restrict to generate many diagrams with single name for one model for Internal Block Diagram
	public boolean singleRepresentationCheck(EObject any) {		
		DIAGRAM_TITLE=getTitle(any);
		final Session session = SessionManager.INSTANCE.getSession(any);
		final Collection<DRepresentation> representations = DialectManager.INSTANCE
				.getAllRepresentations(session);
		for (final DRepresentation representation : representations) {
			if (representation instanceof DSemanticDiagram) {
				final DSemanticDiagram diagram = (DSemanticDiagram)representation;
				if (DIAGRAM_TITLE.equals(diagram.getName())) {
					return false;
				}
			}
		}
		return true;
	}
	
	//Title for Internal Block Diagram
	public String getTitle(EObject eObject)
	{
		String title="Internal block diagram for ";
		Model model=(Model)eObject;
		for (EObject id : model.getSystems()) {
			if(id instanceof InterfaceDescription)
			{
				title+=""+((InterfaceDescription)id).getName();
			}
		}
		return title;
	}
	
	//Title for Block Diagram
	public String getBlockDiagramTitle(EObject eObject)
	{
		String title="Block diagram for ";
		Model model=(Model)eObject;
		for (EObject id : model.getSystems()) {
			if(id instanceof InterfaceDescription)
			{
				title+=""+((InterfaceDescription)id).getName();
			}
		}
		return title;
	}
	
	public BlockDiagramServices() {
		navigateToDiagram=new NavigateToDiagram();
	}	
	
	
	//Creating Sub diagrams programatically when double clicked
	public void createInternalBlockDiagram(EObject element) {
		navigateToDiagram.createRepresentation(element, InternalBlockDiagram_REPRESENTATION);
	}
//	public void createDataPointsDiagram(DataPointUtility element) {
//		navigateToDiagram.createRepresentation(element, DataPointDiagram_REPRESENTATION);
//	}
//	
//	public void createAlarmDiagram(AlarmUtility element) {
//		navigateToDiagram.createRepresentation(element, AlarmDiagram_REPRESENTATION);
//	}
//	
//	public void createCommandDiagram(CommandUtility element) {
//		navigateToDiagram.createRepresentation(element, CommandDiagram_REPRESENTATION);
//	}
//	
//	public void createEventDiagram(EventUtility element) {
//		navigateToDiagram.createRepresentation(element, EventDiagram_REPRESENTATION);
//	}
//	
//	public void createResponseDiagram(ResponseUtility element) {
//		navigateToDiagram.createRepresentation(element, ResponseDiagram_REPRESENTATION);
//	}
//	
//	public void createOperatingStateDiagram(StateUtility element) {
//		navigateToDiagram.createRepresentation(element, OperatingStateDiagram_REPRESENTATION);
//	}
//	
//	public void createCommandResponseBlocksDiagram(CommandResponseBlockUtility element) {
//		navigateToDiagram.createRepresentation(element, CommandResponseBlocksDiagram_REPRESENTATION);
//	}
//	
//	public void createEventBlocksDiagram(EventBlockUtility element) {
//		navigateToDiagram.createRepresentation(element, EventBlocksDiagram_REPRESENTATION);
//	}
//	
//	public void createAlarmBlocksDiagram(AlarmBlockUtility element) {
//		navigateToDiagram.createRepresentation(element, AlarmBlocksDiagram_REPRESENTATION);
//	}
//	
//	public void createDataPointBlocksDiagram(DataPointBlockUtility element) {
//		navigateToDiagram.createRepresentation(element, DataPointBlocksDiagram_REPRESENTATION);
//	}
//	
//	public void createBlocksDiagram(DataPointBlockUtility element) {
//		navigateToDiagram.createRepresentation(element, BLOCK_DIAGRAM_REPRESENTATION);
//	}
	
	public static void main(String[] args) {
		new BlockDiagramServices().showDialog(null);
	}
	
	
	//Opens Model Selection Wizard for generating Selective Component Representation 
	public EObject showDialog(EObject anyObject)
	{
		
		Shell shell=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		ModelInformationWizard modelInformationWizard=new ModelInformationWizard();
		modelInformationWizard.setCurrentContextObject(anyObject);
		WizardDialog dialog = new WizardDialog(shell, modelInformationWizard);
		dialog.open();
		
		
		return anyObject;
	}
	
	// File renaming If InterfaceDescription renamed
	public String renameFile(InterfaceDescription anyObject)
	{
		try{
//		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
//		IFile ifile = (IFile) anyObject.eResource();
//		InterfaceDescription newID = (InterfaceDescription) anyObject;
//		File file = new File(anyObject.eResource().getURI().toPlatformString(false));
//		
////		if(ifile.exists())
////			file = ifile.getFullPath().toFile();
//		IWorkspace workspace= ResourcesPlugin.getWorkspace();    
//		IFile ifile= workspace.getRoot().getFileForLocation(Path.fromOSString(file.getAbsolutePath()));
//		
//		
//		File newFile = new File(ifile.getFullPath().removeLastSegments(1).append(newID.getName()+".mncspec").toString());
//		boolean bool = file.renameTo(newFile);
//		
			
			
		IWorkspace workspace = ResourcesPlugin.getWorkspace();		
		File file=new File(""+workspace.getRoot().getLocation()+"/"+anyObject.eResource().getURI().toString().substring(19, anyObject.eResource().getURI().toString().length()));
		File toRenameFile=new File(""+workspace.getRoot().getLocation()+"/"+anyObject.eResource().getURI().toString().substring(19,anyObject.eResource().getURI().toString().lastIndexOf('/'))+"/"+((InterfaceDescription)anyObject).getName()+".mncspec");
		boolean bool = file.renameTo(toRenameFile);
		
		// Refreshing current project
/*		for(IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
		{
					if(anyObject.eResource().getURI().toString().substring(19,anyObject.eResource().getURI().toString().lastIndexOf('/')).equals(""+project.getName()))
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
				   
		}*/
		
	
		
//		if(file.exists())
//		{
//			System.out.println("Source Available");
//			try 
//			{
//				copyFileUsingStream(file, toRenameFile);
//				file.delete();
			} 
			catch (Exception e) {
				
				e.printStackTrace();
			}
//		}
		return "";
	}
	
	public void copyFileUsingStream(File source, File dest) throws IOException {
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } finally {	    	
	        is.close();
	        os.close();
	    }
	}
	
	//Diagram to code navigation
	public EObject openTextEditor(EObject anyObject) {
		if (anyObject != null && anyObject.eResource() instanceof XtextResource
				&& anyObject.eResource().getURI() != null) {	
			String fileURI = anyObject.eResource().getURI().toPlatformString(true);
			IFile workspaceFile = ResourcesPlugin.getWorkspace().getRoot()
					.getFile(new Path(fileURI));
			if (workspaceFile != null) {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				try {
									
					
					IEditorPart openEditor = IDE.openEditor(page,workspaceFile,"com.mncml.dsl.Mnc",true);
					if ((openEditor instanceof AbstractTextEditor) ){
						
						ICompositeNode node = NodeModelUtils
								.findActualNodeFor(anyObject);
						if (node != null) {
							int offset = node.getOffset();
							int length = node.getTotalEndOffset() - offset;
							((AbstractTextEditor) openEditor).selectAndReveal(
									offset, length);							
						}
					}
					// editorInput.
				} catch (PartInitException e) {
					System.out.println(""+e);
					// Put your exception handler here if you wish to.
				}
			}
		}		
		return anyObject;
	}
	

}
