//package com.model.domain.mnc.design.services;
//
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Set;
//
//import org.eclipse.emf.common.util.BasicEList;
//import org.eclipse.emf.common.util.EList;
//import org.eclipse.emf.ecore.EObject;
//import org.eclipse.sirius.business.api.dialect.DialectManager;
//import org.eclipse.sirius.business.api.session.Session;
//import org.eclipse.sirius.business.api.session.SessionManager;
//import org.eclipse.sirius.diagram.DSemanticDiagram;
//import org.eclipse.sirius.viewpoint.DRepresentation;
//
//import com.google.common.collect.ImmutableSet;
//import com.google.common.collect.Sets;
//
//import mncModel.Command;
//import mncModel.CommandResponseBlock;
//import mncModel.ControlNode;
//import mncModel.InterfaceDescription;
//import mncModel.Model;
//import mncModel.Response;
//import mncModel.ResponseBlock;
//
//
//
//public class SequenceDiagramServices {
//
//	public static String DIAGRAM_TITLE="";
//	public SequenceDiagramServices() {
//		// TODO Auto-generated constructor stub
//	}
//	
//	//Single representation checking
//	public boolean singleSequenceRepresentationCheck(EObject any) {		
//		DIAGRAM_TITLE=getSequenceTitle(any);
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
//	//Title to diagram
//	public String getSequenceTitle(EObject eObject)
//	{
//		String title="Sequence diagram for ";
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
//	//Getting list of instance roles by order
//	public List<ControlNode> getInstanceRoleOrdering(Model model)
//	{
//		List<ControlNode> instanceRoleList=new BasicEList<ControlNode>();
//		
//		for (mncModel.System system : model.getSystems()) {
//			if(system instanceof ControlNode)
//			{
//				
//				instanceRoleList.add((ControlNode) system);
//				if(((ControlNode) system).getChildNodes().size()>0)
//				instanceRoleList.add(((ControlNode) system).getChildNodes().get(0));
//				return instanceRoleList;
//			}
//		}
//		return instanceRoleList;
//	}
//	
//	//Only ControlNodes can be an instance role
//	public List<ControlNode> getInstanceSemanticCandidate(ControlNode controlNode)
//	{
//		List<ControlNode> instanceList=new BasicEList<ControlNode>();
//		instanceList.add(controlNode);
//		instanceList.add(controlNode.getChildNodes().get(0));
//		return instanceList;
//		
//	}
//	
//	public List<ControlNode> getInstanceSemanticCandidate(EObject eObject)
//	{		
//		List<ControlNode> instanceList=getInstanceRoleOrdering((Model)eObject);
//		return instanceList;		
//	}
//	
//	
//	//Ends ordering list to diagram by sequence
//	public Set<EObject> getEndsOrdering(Model model)
//	{
//		List<EObject> endsOrderingList=new BasicEList<>();
//		
//		HashMap<Command, Response>[] controlNodeCommandResponse=(HashMap<Command, Response>[]) getCommandResponseMapping(model);		
//			
//		for (mncModel.System system : model.getSystems()) 
//		{
//			if(system instanceof ControlNode)
//			{
//				for (CommandResponseBlock commandResponseBlock : ((mncModel.impl.ControlNodeImpl)system).getCommandResponseBlocks().getCommandResponseBlocks()) 
//				{
//					endsOrderingList.add(commandResponseBlock.getCommand());
//					endsOrderingList.add(commandResponseBlock.getAction().getFireCommand().get(0));
//					endsOrderingList.add(controlNodeCommandResponse[1].get(commandResponseBlock.getAction().getFireCommand().get(0)));
//					endsOrderingList.add(controlNodeCommandResponse[0].get(commandResponseBlock.getCommand()));
//				}
//			}
//		}
//		
//		final Set<EObject> eventEndsSet = ImmutableSet.copyOf(endsOrderingList);
//		final Sets.SetView<EObject> intersection = Sets.intersection(eventEndsSet,eventEndsSet);
//		return intersection;		
//	}
//	
//	// getting response message sender and receiver
//	public Response getResponseMessageSender(CommandResponseBlock commandResponseBlock)
//	{
//		return commandResponseBlock.getResponseBlock().getResponseBlocks().get(0).getResponse();
//	}
//	
//	
//	public Response getResponseMessageReceiver(CommandResponseBlock commandResponseBlock)
//	{
//		
//		Model model=(Model)((ControlNode)commandResponseBlock.eContainer().eContainer()).getParentNode().eContainer();
//		for (mncModel.System system : model.getSystems()) {
//			if (system instanceof ControlNode) 
//			{
//				EList<ControlNode> childControlNode=((mncModel.impl.ControlNodeImpl)system).getChildNodes();	
//				for (CommandResponseBlock commandResponseBlocktemp : ((mncModel.impl.ControlNodeImpl)system).getCommandResponseBlocks().getCommandResponseBlocks()) {
//					if(commandResponseBlocktemp.getCommandTranslation().getTranslatedCommands().get(0).equals(commandResponseBlock.getCommand()))
//					{
//						return commandResponseBlocktemp.getResponseBlock().getResponseBlocks().get(0).getResponse();
//					}					
//				}
//			}
//		}
//		
//		
//		return null;	
//	}
//	
//	//getting Command message sender and receiver
//	public Command getCommandMessageSender(CommandResponseBlock commandResponseBlock)
//	{
//		return commandResponseBlock.getCommand();
//	}
//	
//	public Command getCommandMessageReceiver(CommandResponseBlock commandResponseBlock)
//	{
//		return (Command)commandResponseBlock.getCommandTranslation().getTranslatedCommands().get(0);
//	}
//		
//	//getting mapping of command and response for mentaining sequence 
//	public HashMap<Command,Response>[] getCommandResponseMapping(Model model)
//	{
//		HashMap<Command,Response> hashMap[]=new HashMap[2];
//		hashMap[0]=new HashMap<Command, Response>();
//		hashMap[1]=new HashMap<Command, Response>();
//		
//		try 
//		{
//			for (mncModel.System system : model.getSystems()) {
//				if (system.getClass().getName().equals("mncModel.impl.ControlNodeImpl")) 
//				{
//					//parent ControlNode
//					for (CommandResponseBlock commandResponseBlock : ((mncModel.impl.ControlNodeImpl)system).getCommandResponseBlocks().getCommandResponseBlocks()) {
//						
//						EList<ResponseBlock> responseBlocks=commandResponseBlock.getResponseBlock().getResponseBlocks();
//						hashMap[0].put(commandResponseBlock.getCommand(),responseBlocks.get(0).getResponse());
//					//	System.out.println("Parent : "+commandResponseBlock.getCommand().getName()+"  =>  "+responseBlocks.get(0).getResponse().getName());
//					}
//					
//					
//					EList<ControlNode> childControlNode=((mncModel.impl.ControlNodeImpl)system).getChildNodes();					
//					for (CommandResponseBlock commandResponseBlock : ((mncModel.impl.ControlNodeImpl)childControlNode.get(0)).getCommandResponseBlocks().getCommandResponseBlocks()) {
//						
//						EList<ResponseBlock> responseBlocks=commandResponseBlock.getResponseBlock().getResponseBlocks();
//						hashMap[1].put(commandResponseBlock.getCommand(),responseBlocks.get(0).getResponse());
//					//	System.out.println("Child : "+commandResponseBlock.getCommand().getName()+"  =>  "+responseBlocks.get(0).getResponse().getName());
//					}
//				}
//				
//				if (system.getClass().getName().equals("mncModel.impl.InterfaceDescriptionImpl")) {					
//					//Interface desciption related code here
//				}	
//			}
//			
//		} 
//		catch (Exception e) {
//			System.out.println(""+e);
//		}
//		return hashMap;
//	}
//	
//	//getting exection start and stop location
//	public Command getExecutionStart(CommandResponseBlock commandResponseBlock)
//	{
//		return commandResponseBlock.getCommand();
//	}
//	
//	public Response getExecutionEnd(CommandResponseBlock commandResponseBlock)
//	{
//		return (Response)commandResponseBlock.getResponseBlock().getResponseBlocks().get(0).getResponse();
//	}	
//	
//	
//	public List<EObject> getExecutionAssociation(CommandResponseBlock commandResponseBlock)
//	{
//		List<EObject> associationList=new BasicEList<>();
//		associationList.add(commandResponseBlock.getCommand());
//		associationList.add(commandResponseBlock.getResponseBlock().getResponseBlocks().get(0).getResponse());
//		return null;
//	}
//	
//	// getting instance roles list
//	public List<EObject> getInstanceRoles(Model model)
//	{
//		List<EObject> instanceRoles=new BasicEList<EObject>();
//		for (mncModel.System system : model.getSystems()) 
//		{				
//			if (system instanceof ControlNode) 
//			{							
//				instanceRoles.add(system);
//			}
//		}
//		return instanceRoles;
//	}
//	
//	public CommandResponseBlock getSourceOfCommandMessage(CommandResponseBlock commandResponseBlock)
//	{
//		return commandResponseBlock;
//	}
//	
//	
//	public CommandResponseBlock getTargetOfCommandMessage(CommandResponseBlock commandResponseBlock)
//	{
//		
//		Model model=(Model)((ControlNode)commandResponseBlock.eContainer().eContainer()).getChildNodes().get(0).eContainer();
//		for (mncModel.System system : model.getSystems()) {
//			if (system instanceof ControlNode) 
//			{					
//				for (CommandResponseBlock commandResponseBlocktemp : ((mncModel.impl.ControlNodeImpl)system).getCommandResponseBlocks().getCommandResponseBlocks()) {
//					if(commandResponseBlock.getCommandTranslation().getTranslatedCommands().get(0).equals(commandResponseBlocktemp.getCommand()))
//					{
//						return commandResponseBlocktemp;
//					}					
//				}
//			}
//		}
//		return null;
//	}
//	
//	public CommandResponseBlock getSourceOfResponseMessage(CommandResponseBlock commandResponseBlock)
//	{
//		return commandResponseBlock;
//	}
//	
//	public CommandResponseBlock getTargetOfResponseMessage(CommandResponseBlock commandResponseBlock)
//	{
//		Model model=(Model)((ControlNode)commandResponseBlock.eContainer().eContainer()).getParentNode().eContainer();
//		for (mncModel.System system : model.getSystems()) {
//			if (system instanceof ControlNode) 
//			{
//				EList<ControlNode> childControlNode=((mncModel.impl.ControlNodeImpl)system).getChildNodes();	
//				for (CommandResponseBlock commandResponseBlocktemp : ((mncModel.impl.ControlNodeImpl)system).getCommandResponseBlocks().getCommandResponseBlocks()) {
//					if(commandResponseBlocktemp.getCommandTranslation().getTranslatedCommands().get(0).equals(commandResponseBlock.getCommand()))
//					{					
//						return commandResponseBlocktemp;
//					}					
//				}
//			}
//		}
//		
//		return null;
//	}
//	
//	
//	
//}
