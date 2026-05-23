//package com.model.domain.mnc.design.services;
//
//import java.util.Collection;
//import java.util.List;
//
//import org.eclipse.core.resources.IFile;
//import org.eclipse.core.resources.ResourcesPlugin;
//import org.eclipse.core.runtime.Path;
//import org.eclipse.emf.common.util.BasicEList;
//import org.eclipse.emf.ecore.EObject;
//import org.eclipse.jface.wizard.WizardDialog;
//import org.eclipse.sirius.business.api.dialect.DialectManager;
//import org.eclipse.sirius.business.api.session.Session;
//import org.eclipse.sirius.business.api.session.SessionManager;
//import org.eclipse.sirius.diagram.DSemanticDiagram;
//import org.eclipse.sirius.viewpoint.DRepresentation;
//import org.eclipse.swt.widgets.Shell;
//import org.eclipse.ui.IEditorPart;
//import org.eclipse.ui.IWorkbenchPage;
//import org.eclipse.ui.PartInitException;
//import org.eclipse.ui.PlatformUI;
//import org.eclipse.ui.ide.IDE;
//import org.eclipse.ui.texteditor.AbstractTextEditor;
//import org.eclipse.xtext.nodemodel.ICompositeNode;
//import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
//import org.eclipse.xtext.resource.XtextResource;
////import static  org.eclipse.xtext.EcoreUtil2.*;
//
//import com.model.domain.mnc.design.ui.TransitionInformationWizard;
//
//import mncModel.CommandResponseBlock;
//import mncModel.ControlNode;
//import mncModel.InterfaceDescription;
//import mncModel.Model;
//import mncModel.OperatingState;
//import mncModel.Transition;
//import mncModel.utility.StateUtility;
//import mncModel.utility.TransitionUtility;
//
//public class StateDiagramServices {
//
//	
//	public static String DIAGRAM_TITLE="";
//	
//	public StateDiagramServices() {
//		super();
//		
//		// TODO Auto-generated constructor stub
//	}
//	
//	//checking for single representation
//	public boolean singleStateRepresentationCheck(EObject any) {		
//		DIAGRAM_TITLE=getStateDiagramTitle(any);
//		final Session session = SessionManager.INSTANCE.getSession(any);
//		final Collection<DRepresentation> representations = DialectManager.INSTANCE
//				.getAllRepresentations(session);
//		for (final DRepresentation representation : representations) {
//			if (representation instanceof DSemanticDiagram) {
//				final DSemanticDiagram diagram = (DSemanticDiagram)representation;
//				if (DIAGRAM_TITLE.equals(diagram.getName())) {
//					return false;
//				}
//			}
//		}
//		return true;
//	}
//	
//	//title of diagram
//	public String getStateDiagramTitle(EObject eObject)
//	{
//		String title="State diagram for ";
//		Model model=(Model)eObject;
//		for (EObject id : model.getSystems()) {
//			if(id instanceof InterfaceDescription)
//			{
//				title+=""+((InterfaceDescription)id).getName();
//			}
//		}
//		return title;
//	}
//	
//	
//	//navigating from diagram to code
//	public EObject openTextEditor(EObject any) {
//		if (any != null && any.eResource() instanceof XtextResource
//				&& any.eResource().getURI() != null) {	
//			String fileURI = any.eResource().getURI().toPlatformString(true);
//			IFile workspaceFile = ResourcesPlugin.getWorkspace().getRoot()
//					.getFile(new Path(fileURI));
//			if (workspaceFile != null) {
//				IWorkbenchPage page = PlatformUI.getWorkbench()
//						.getActiveWorkbenchWindow().getActivePage();
//				try {	
//					IEditorPart openEditor = IDE.openEditor(page,workspaceFile,"com.mncml.dsl.Mnc",true);
//					if ((openEditor instanceof AbstractTextEditor) ){
//						
//						ICompositeNode node = NodeModelUtils
//								.findActualNodeFor(any);
//						if (node != null) {
//							int offset = node.getOffset();
//							int length = node.getTotalEndOffset() - offset;
//							((AbstractTextEditor) openEditor).selectAndReveal(
//									offset, length);						
//						}
//					}
//					// editorInput.
//				} catch (PartInitException e) {
//					System.out.println(""+e);
//					// Put your exception handler here if you wish to.
//				}
//			}
//		}		
//		return any;
//	}
//	
//	
//	//Moved in Action DiagramServices
//	public List<Transition> getEntryActionTransition(OperatingState operatingState)
//	{
//		
//		List<Transition> transtionList=new BasicEList<Transition>();
//		Model model=(Model)operatingState.eContainer().eContainer().eContainer();
//		for (mncModel.System system : model.getSystems()) {
//			if(system instanceof ControlNode)
//			{
//				for (CommandResponseBlock commandResponseBlock : ((ControlNode)system).getCommandResponseBlocks().getCommandResponseBlocks()) {
//					for (Transition transition : commandResponseBlock.getTransition().getTransitions()) {
//						if(((OperatingState)transition.getNextState()).equals(operatingState))
//						{
//							transtionList.add(transition);
//						}
//					}
//				}
//			}
//		}
//		return transtionList;
//	}
//	
//	//Moved in Action DiagramServices
//	public List<Transition> getExitActionTransition(OperatingState operatingState)
//	{
//		
//		List<Transition> transtionList=new BasicEList<Transition>();
//		Model model=(Model)operatingState.eContainer().eContainer().eContainer();
//		for (mncModel.System system : model.getSystems()) {
//			if(system instanceof ControlNode)
//			{
//				for (CommandResponseBlock commandResponseBlock : ((ControlNode)system).getCommandResponseBlocks().getCommandResponseBlocks()) {
//					for (Transition transition : commandResponseBlock.getTransition().getTransitions()) {
//						if(((OperatingState)transition.getCurrentState()).equals(operatingState))
//						{
//							transtionList.add(transition);
//						}
//					}
//				}
//			}
//		}
//		return transtionList;
//	}
//	
//	
//	//getting context of new state (New state creation from palette)
//	public StateUtility getNewOperatingStateContext(Model model)
//	{
//		for (mncModel.System system : model.getSystems()) {
//			if(system instanceof InterfaceDescription)
//			{
//				return ((InterfaceDescription)system).getOperatingStates();
//			}
//		}
//		return null;
//	}
//	
//	//getting context of new transition (New transition creation from palette)
//	//It shows one wizard for selecting Command response block to adding new transition
//	public TransitionUtility getNewTransitionContext(OperatingState operatingState)
//	{
//		
//		
//		Shell shell=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
//		TransitionInformationWizard transitionInformationWizard=new TransitionInformationWizard();
//		transitionInformationWizard.setCurrentContextObject(operatingState);
//		WizardDialog dialog = new WizardDialog(shell, transitionInformationWizard);
//		dialog.open();
//		
//		return transitionInformationWizard.getSelectedTransitionContext();
//		
//		
//		
//		
//		/*Model model=(Model) operatingState.eContainer().eContainer().eContainer();
//		Vector<String> commands=new Vector<String>();
//		for (mncModel.System system : model.getSystems()) {
//			if(system instanceof ControlNode)
//			{
//				for (CommandResponseBlock commandResponseBlock : ((ControlNode)system).getCommandResponseBlocks().getCommandResponseBlocks()) {
//					commands.add(commandResponseBlock.getCommand().getName());
//				}	
//			}
//		}		
//		NewTransitionInformation newTransitionInformation=new NewTransitionInformation();
//		newTransitionInformation.addCommands(commands);
//		newTransitionInformation.showCommandDialog();
//		
//		
//		
//		while (NewTransitionInformation.APPROVE_COMMAND) {
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		NewTransitionInformation.APPROVE_COMMAND=true;
//		
//		String selectedCommand=newTransitionInformation.getSelectedCommandName();
//		System.out.println("Selected Command="+selectedCommand);	
//		
//		//Model model=(Model) operatingState.eContainer().eContainer().eContainer();
//		for (mncModel.System system : model.getSystems()) {
//			if(system instanceof ControlNode)
//			{
//				for (CommandResponseBlock commandResponseBlock : ((ControlNode)system).getCommandResponseBlocks().getCommandResponseBlocks()) {
//					if(commandResponseBlock.getCommand().getName().equals(selectedCommand))
//					{
//						System.out.println("In Context="+(TransitionUtility)commandResponseBlock.getTransition());
//						return (TransitionUtility)commandResponseBlock.getTransition();
//					}
//				}	
//			}
//		}
//		return null;*/
//	}
//
//}
