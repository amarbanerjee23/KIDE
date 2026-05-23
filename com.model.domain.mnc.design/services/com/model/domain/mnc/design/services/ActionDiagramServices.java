package com.model.domain.mnc.design.services;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.business.api.dialect.DialectManager;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.diagram.DSemanticDiagram;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import mncModel.ActionAlarm;
import mncModel.ActionCommand;
import mncModel.ActionEvent;
import mncModel.Alarm;
import mncModel.Command;
import mncModel.CommandResponseBlock;
import mncModel.ControlNode;
import mncModel.Event;
import mncModel.InterfaceDescription;
import mncModel.Model;
import mncModel.OperatingState;
import mncModel.Transition;
import mncModel.utility.OperatingStateUtility;

public class ActionDiagramServices {
	
	//The diagram title is useful for generating new diagram programatically.
	public static String DIAGRAM_TITLE="";

	public ActionDiagramServices() {
		super();
	}
	
	//precondition do restrict generation of many diagram by same name for same model
	public boolean singleActionRepresentationCheck(EObject any) {		
		DIAGRAM_TITLE=getActionTitle(any);
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
	
	//Setting title for diagram
	public String getActionTitle(EObject eObject)
	{
		String title="Action diagram for ";
		Model model=(Model)eObject;
		for (EObject id : model.getSystems()) {
			if(id instanceof InterfaceDescription)
			{
				title+=""+((InterfaceDescription)id).getName();
			}
		}
		return title;
	}
	
	//restriction diagram generation from any other Project element than Model
	public boolean getActionDiagramGenerationPrecondition(EObject any)
	{
		if(any instanceof Model)
			return true;
		return false;
	}

	
	//Diagram to code navigation
	public EObject openTextEditor(EObject any) {
		if (any != null && any.eResource() instanceof XtextResource
				&& any.eResource().getURI() != null) {
			String fileURI = any.eResource().getURI().toPlatformString(true);
			IFile workspaceFile = ResourcesPlugin.getWorkspace().getRoot()
					.getFile(new Path(fileURI));
			if (workspaceFile != null) {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				try {
					
					
					
					
					
					IEditorPart openEditor = IDE.openEditor(page,workspaceFile,"com.mncml.dsl.Mnc",true);
					if ((openEditor instanceof AbstractTextEditor) ){
						ICompositeNode node = NodeModelUtils
								.findActualNodeFor(any);
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
		return any;
	}
	
	
	// Entry Action for each state
	public List<Transition> getEntryActionTransition(OperatingState operatingState)
	{
		
		List<Transition> transtionList=new BasicEList<Transition>();
		Model model=(Model)operatingState.eContainer().eContainer().eContainer();
		for (mncModel.System system : model.getSystems()) {
			if(system instanceof ControlNode)
			{
				for (CommandResponseBlock commandResponseBlock : ((ControlNode)system).getCommandResponseBlocks()) {
					if(commandResponseBlock.getAction()!=null && commandResponseBlock.getAction().getTransitionStates()!=null)
					{
						for (Transition transition : commandResponseBlock.getAction().getTransitionStates()) {
							if(((OperatingState)transition.getNextState()).equals(operatingState))
							{	
								if (transition.getEntryAction()!=null) {
										if (transition.getEntryAction().getRaiseAlarm().size()>0 || transition.getEntryAction().getFireCommand().size()>0 || transition.getEntryAction().getPublishEvent().size()>0) {
										transtionList.add(transition);
									}
								}
															
							}
						}
					}
					
				}
			}
		}
		return transtionList;
	}
	
	// Exit Action for each state
	public List<Transition> getExitActionTransition(OperatingState operatingState)
	{
		
		List<Transition> transtionList=new BasicEList<Transition>();
		Model model=(Model)operatingState.eContainer().eContainer().eContainer();
		for (mncModel.System system : model.getSystems()) {
			if(system instanceof ControlNode)
			{
				for (CommandResponseBlock commandResponseBlock : ((ControlNode)system).getCommandResponseBlocks()) {
					
					if(commandResponseBlock.getAction()!=null && commandResponseBlock.getAction().getTransitionStates()!=null)
					{
						for (Transition transition : commandResponseBlock.getAction().getTransitionStates()) {
							if(((OperatingState)transition.getCurrentState()).equals(operatingState))
							{
								if (transition.getExitAction()!=null){
										if (transition.getExitAction().getRaiseAlarm().size()>0 || transition.getExitAction().getFireCommand().size()>0 || transition.getExitAction().getPublishEvent().size()>0) {
										transtionList.add(transition);
									}
								}
								
							}
						}
					}
					
				}
			}
		}
		return transtionList;
	}
	
	// Entry Action name for each EntryAction
	public String getEntryActionName(Transition transition)
	{		
		return ""+getEntryActionCommandName(transition)+getEntryActionEventName(transition)+getEntryActionAlarmName(transition);
	}
	
	// Exit Action name for each EntryAction
	public String getExitActionName(Transition transition)
	{		
		return ""+getExitActionCommandName(transition)+getExitActionEventName(transition)+getExitActionAlarmName(transition);
	}
	
	
	private String getExitActionAlarmName(Transition transition) {
		String alarmName="";
		if(transition.getExitAction().getRaiseAlarm().size()>0)
		{
			for (ActionAlarm alarm : transition.getExitAction().getRaiseAlarm()) {
				alarmName+=alarm.getAlarm().getName()+" ";
			}
			alarmName+="\n";
			return "\n  Alarms: "+alarmName;
		}
		return alarmName;
	}


	private String getExitActionCommandName(Transition transition) {
		String commandName="";
		if(transition.getExitAction().getFireCommand().size()>0)
		{
			for (ActionCommand command : transition.getExitAction().getFireCommand()) {
				commandName+=command.getCommand().getName()+" ";
			}
			commandName+="\n";
			return "\n  Commands: "+commandName;
		}
		return commandName;
	}


	private String getExitActionEventName(Transition transition) {
		String eventName="";
		if(transition.getExitAction().getPublishEvent().size()>0)
		{
			for (ActionEvent event : transition.getExitAction().getPublishEvent()) {
				eventName+=event.getEvent().getName()+" ";
			}
			eventName+="\n";
			return "\n  Events: "+eventName;
		}
		return eventName;	
	}


	private String getEntryActionAlarmName(Transition transition) {
		String alarmName="";
		if(transition.getEntryAction().getRaiseAlarm().size()>0)
		{
			for (ActionAlarm alarm : transition.getEntryAction().getRaiseAlarm()) {
				alarmName+=alarm.getAlarm().getName()+" ";
			}
			alarmName+="\n";
			return "\n  Alarms: "+alarmName;
		}
		return alarmName;		
	}


	private String getEntryActionEventName(Transition transition) {
		String eventName="";
		if(transition.getEntryAction().getPublishEvent().size()>0)
		{
			for (ActionEvent event : transition.getEntryAction().getPublishEvent()) {
				eventName+=event.getEvent().getName()+" ";
			}
			eventName+="\n";
			return "\n  Events: "+eventName;
		}
		return eventName;	
	}


	private String getEntryActionCommandName(Transition transition) {
		String commandName="";
		if(transition.getEntryAction().getFireCommand().size()>0)
		{
			for (ActionCommand command : transition.getEntryAction().getFireCommand()) {
				commandName+=command.getCommand().getName()+" ";
			}
			commandName+="\n";
			return "\n  Commands: "+commandName;
		}
		return commandName;
	}


	public OperatingStateUtility getNewOperatingStateContext(Model model)
	{
		System.out.println("In getNewOperatingStateContext");
		for (mncModel.System system : model.getSystems()) {
			if(system instanceof InterfaceDescription)
			{
				return ((InterfaceDescription)system).getOperatingStatesUtility();
			}
		}
		return null;
	}
	
	
/*	public TransitionUtility getNewTransitionContext(OperatingState operatingState)
	{
		System.out.println("In getNew transition Context");
		
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
		
		Model model=(Model) operatingState.eContainer().eContainer().eContainer();
		Vector<String> commands=new Vector<String>();
		for (mncModel.System system : model.getSystems()) {
			if(system instanceof ControlNode)
			{
				for (CommandResponseBlock commandResponseBlock : ((ControlNode)system).getCommandResponseBlocks().getCommandResponseBlocks()) {
					commands.add(commandResponseBlock.getCommand().getName());
				}	
			}
		}		
		NewTransitionInformation newTransitionInformation=new NewTransitionInformation();
		newTransitionInformation.addCommands(commands);
		newTransitionInformation.showCommandDialog();
		
		
		
		while (NewTransitionInformation.APPROVE_COMMAND) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		NewTransitionInformation.APPROVE_COMMAND=true;
		
		String selectedCommand=newTransitionInformation.getSelectedCommandName();
		System.out.println("Selected Command="+selectedCommand);	
		
		//Model model=(Model) operatingState.eContainer().eContainer().eContainer();
		for (mncModel.System system : model.getSystems()) {
			if(system instanceof ControlNode)
			{
				for (CommandResponseBlock commandResponseBlock : ((ControlNode)system).getCommandResponseBlocks().getCommandResponseBlocks()) {
					if(commandResponseBlock.getCommand().getName().equals(selectedCommand))
					{
						System.out.println("In Context="+(TransitionUtility)commandResponseBlock.getTransition());
						return (TransitionUtility)commandResponseBlock.getTransition();
					}
				}	
			}
		}
		return null;
	}
	*/
	
	
}
