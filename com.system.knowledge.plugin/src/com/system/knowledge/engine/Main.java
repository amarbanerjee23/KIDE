/*
 * Copyright (c) 2013 Atlanmod.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which accompanies
 * this distribution, and is available at https://www.eclipse.org/legal/epl-2.0/
 */

package com.system.knowledge.engine;

import java.io.IOException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import fr.inria.atlanmod.neoemf.config.BaseConfig;
import fr.inria.atlanmod.neoemf.data.blueprints.neo4j.config.BlueprintsNeo4jConfig;
import fr.inria.atlanmod.neoemf.data.blueprints.util.BlueprintsUriFactory;
import mncModel.ControlNode;
import mncModel.InterfaceDescription;
import mncModel.MncModelFactory;
import mncModel.Model;


public class Main {   

 
    public static Resource createBlueprintsResource() throws IOException {
        Resource resource = new ResourceSetImpl()
        		.createResource(new BlueprintsUriFactory()
        				.createLocalUri("ne04j/databases/mncml.graphdb"));
        resource.save(new BlueprintsNeo4jConfig().toMap());
        return resource;
    }

  
    public static void read(Resource resource) throws IOException {
        resource.load(new BaseConfig().toMap());
      EList<EObject> graph = resource.getContents();
      
      for(EObject e: graph) { 
    	  Model g = (Model) e;
    	  System.out.println(g.getName());
          System.out.println(((InterfaceDescription)g.getSystems().get(0)).getName());
          System.out.println(((ControlNode)g.getSystems().get(1)).getName());  
      }
    }


   
    public static void write(Resource resource) throws IOException {
    	 BaseConfig baseConfig = new BaseConfig();
		  MncModelFactory factory = MncModelFactory.eINSTANCE; 
			  Model graph =  factory.createModel();
			  graph.setName("ModelA");
			InterfaceDescription v1 = factory.createInterfaceDescription();
			v1.setName("InterfaceDescription "  + "a");
		  
		  ControlNode v2 = factory.createControlNode(); 
		  v2.setName("ControlNode "  + "b");
		  
		  v2.setInterfaceDescription(v1);
		  
		  graph.getSystems().add(v1);
		  graph.getSystems().add(v2); 
		  
		  resource.getContents().add(graph); 
		  resource.save(baseConfig.toMap());
    }

  
    public static void main(String[] args) throws IOException {
        Resource resource = createBlueprintsResource();
            write(resource);
            read(resource);
            
            
    }
}
