package com.smr.activity.activity2mnc.handlers

import com.google.inject.Inject
import org.eclipse.xtext.testing.util.ParseHelper
import mncModel.Model
import com.mncml.dsl.MncStandaloneSetup
import org.eclipse.emf.compare.diff.DiffBuilder
import org.eclipse.emf.compare.EMFCompare
import org.eclipse.emf.compare.diff.DefaultDiffEngine
import org.eclipse.emf.compare.diff.FeatureFilter
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.util.BasicExtendedMetaData
import org.eclipse.emf.ecore.xmi.XMLResource
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.compare.scope.DefaultComparisonScope
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import org.eclipse.emf.compare.Comparison

class Demo { 
	@Inject
	ParseHelper<Model> parser

	def void doParse() {
		// When in a vanilla Java application (i.e. not within Eclipse),
		// you need to run a global setup:
		val injector = new MncStandaloneSetup().createInjectorAndDoEMFRegistration
		injector.injectMembers(this) // sets the field 'parser'
		// this is how you can use it:
		val model = parser.parse("Model Demo
     InterfaceDescription Demo {}")
		val entity = model as Model
		print(entity.name)

	}
	  
	
	def static void main(String[] args) {
		
	// register globally the Ecore Resource Factory to the ".ecore" extension
//	Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put
//	("ecore", new EcoreResourceFactoryImpl());
//	var rs = new ResourceSetImpl();
//	val extendedMetaData = new BasicExtendedMetaData(rs.getPackageRegistry());
//	rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);
//
//	var modelURI = URI.createFileURI("DML.ecore");
//	var r = rs.getResource(modelURI, true);
//	var eObject = r.getContents().get(0);
//	if (eObject instanceof EPackage) {
//		var p = eObject as EPackage;
//		rs.getPackageRegistry().put(p.getNsURI(), p);
//	}
//	
//		
//	// Register the factory
//	Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("dml", new XMIResourceFactoryImpl());
//		
//	// Load the two input models
//	var resourceSet1 = new ResourceSetImpl();
//	var resourceSet2 = new ResourceSetImpl();
//	var uri1 = URI.createFileURI("Demo_ECRE.dml");
//	var uri2 = URI.createFileURI("Deo_ECRE1.dml");
//	resourceSet1.getResource(uri1, true);
//	resourceSet2.getResource(uri2, true);
//	// Configure EMF Compare
//	var comparator = EMFCompare.builder().build();
//	// Compare the two models
//	var scope = new DefaultComparisonScope(resourceSet1, resourceSet2, null);
//	print(comparator.compare(scope));

// When in a vanilla Java application (i.e. not within Eclipse),
		// you need to run a global setup:
		new Demo().doParse
} 

}
