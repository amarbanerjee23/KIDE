package com.model.domain.mnc.design.services.models;

import java.util.HashMap;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;

import mncModel.InterfaceDescription;
import mncModel.Model;

public class AllModelFinder {

	HashMap<InterfaceDescription,Model> modelData=null;
	String path;
	public AllModelFinder(String path) {		
		this.path = path;
	}
	
	public AllModelFinder(){
		
	}
	
	//Getting all models from project
	public HashMap<InterfaceDescription,Model> getAllModels()
	{
		modelData=new HashMap<InterfaceDescription, Model>();
		Model tempModel=null;		
		try 
		{
			URI sessionResourceURI = URI.createPlatformResourceURI(this.path+"/representations.aird", true);
			Session createdSession = SessionManager.INSTANCE.getExistingSession(sessionResourceURI);
			for (Resource element : createdSession.getSemanticResources()) {
				TreeIterator<EObject> eAllContents = element.getAllContents();
				while (eAllContents.hasNext()) {
				      EObject next = eAllContents.next();
				      if(next instanceof Model)
				      {
				    	  tempModel=(Model) next;
				      }
				    	  	
				      if(next instanceof InterfaceDescription)
				      {
				    	 modelData.put((InterfaceDescription)next,tempModel);
				      }				    	  
				}
			}
		}
		catch(Exception e)
		{
			System.out.println(""+e);
		}
					
		return modelData;
	}

}
