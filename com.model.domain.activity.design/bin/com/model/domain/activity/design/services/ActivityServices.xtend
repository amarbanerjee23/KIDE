package com.model.domain.activity.design.services

import activityDiagramModel.Activity
import com.model.domain.activity.design.layout.NavigateToDiagram
import dataModelPackage.AbstractType
import dataModelPackage.ArrayType
import dataModelPackage.Parameter
import dataModelPackage.SimpleType
import java.util.Collection
import mncModel.Command
import mncModel.CommandResponseBlock
import mncModel.ControlNode
import mncModel.Model
import org.eclipse.core.resources.IFile
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.runtime.Path
import org.eclipse.emf.common.util.BasicEList
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EObject
import org.eclipse.sirius.business.api.dialect.DialectManager
import org.eclipse.sirius.business.api.session.Session
import org.eclipse.sirius.business.api.session.SessionManager
import org.eclipse.sirius.diagram.DSemanticDiagram
import org.eclipse.sirius.viewpoint.DRepresentation
import org.eclipse.ui.PartInitException
import org.eclipse.ui.PlatformUI
import org.eclipse.ui.ide.IDE
import org.eclipse.ui.texteditor.AbstractTextEditor
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.nodemodel.util.NodeModelUtils
import org.eclipse.xtext.resource.XtextResource
import operationsDescription.Operation

class ActivityServices {

	var NavigateToDiagram navigateToDiagram;
	public static String DIAGRAM_TITLE = ""
	public static String ACTIVITY_DESCRIPTION_DIAGRAM_TITLE_PART = "Activity Description Diagram for "
	public static String ACTIVITY_DIAGRAM_EDITOR = "com.smr.activity.dsl.ActivityDiagram"
	public static String MNC_EDITOR = "com.mncml.dsl.Mnc"
	public static String DML_EDITOR = "com.dml.dsl.Dml"

	new() {
		navigateToDiagram = new NavigateToDiagram();
	}  
	
	def getOperationForAnActivity(Activity activity){
		if(activity.requiresOperation!==null)
			return activity.requiresOperation
	}
	
def getNameSignatureForOperation(Operation operation){
	var inputParams = populateParametersString(operation.inputParameters,true)
	
	var opPars = new BasicEList<Parameter>() 
	opPars.add(operation.outputParameters)
	var outputParams = populateParametersString(opPars,false)
	var operationSignature = '''
	«operation.name»(«inputParams»):«outputParams»
	Script Path : «operation.executableScript»
	'''
	return operationSignature
	
}

	def String getCapabilityNameSignature(Command capability) {
		
		
		var inputParameter = ""
		var outputParameter  = ""
		inputParameter = populateParametersString(capability.parameters,true) 
		var commandResponseBlock = findCommandResponseBlockForCommand(capability)
		var respsParList = getResponseParametersListForCommand(commandResponseBlock)
			
		outputParameter = populateParametersString(respsParList,false)
		
		
		
		var signature = capability.name + '''(«inputParameter»):«outputParameter»'''
		return signature
	}
	
	def getResponseParametersListForCommand(CommandResponseBlock commandResponseBlock){
		var respsParList = new BasicEList<Parameter>()
		if(commandResponseBlock!==null && commandResponseBlock.responseBlock!==null 
			&& commandResponseBlock.responseBlock!==null &&  commandResponseBlock.responseBlock.size>0)
			{
				for(resBl : commandResponseBlock.responseBlock){
					respsParList.addAll(resBl.response.parameters)
				}
			}
		return respsParList	
	}
	
	
	def getOutputParametersForACapability(Command capability){
		var commandResponseBlock = findCommandResponseBlockForCommand(capability)
		var respsParList = getResponseParametersListForCommand(commandResponseBlock)
		return respsParList 
	}


	
	def populateParametersString(EList<Parameter> parameters,boolean isInput){
		var string = ""
		if (parameters !== null && parameters.size>0) {
			for (par : parameters) {
				if(isInput)
				{
					string = string + getParameterName(par)+':'+getParameterType(par)
				}else{
					string = string + getParameterType(par)
				}
				if (!par.equals(parameters.last)) {
					string = string + ','
				}
			} 
			
		}else{
			string =  'Void'
		}
		println(string)
		return string
	}
	
	
	def static String getParameterName(Parameter parameter) {
		if (parameter !== null) {
			if (parameter instanceof SimpleType) {
				return parameter.name
			}
			if (parameter instanceof ArrayType) {
				return parameter.name
			}
			if (parameter instanceof AbstractType) {
				return parameter.name
			}
		}
	}

	def static String getParameterType(Parameter parameter) {
		if (parameter !== null) {
			if (parameter instanceof SimpleType) {
				return parameter.type.getName()
			}
			if (parameter instanceof ArrayType) {
				if (parameter.dataModelType !== null) {
					return parameter.dataModelType.name+'[]'
				}
				
				if (parameter.primitiveType !== null) {
					return parameter.primitiveType.getName()
				}
			}
			if (parameter instanceof AbstractType) {
				return parameter.type.name
			}
		}
	}

	// precondition do restrict generation of many diagram by same name for same
	// model
	def boolean singleActivityRepresentationCheckForActivity(EObject any) {
		DIAGRAM_TITLE = getActivityTitle(any)
		val Session session = SessionManager.INSTANCE.getSession(any)
		val Collection<DRepresentation> representations = DialectManager.INSTANCE.getAllRepresentations(session)
		for (DRepresentation representation : representations) {
			if (representation instanceof DSemanticDiagram) {
				val DSemanticDiagram diagram = (representation as DSemanticDiagram)
				if (DIAGRAM_TITLE.equals(diagram.getName())) {
					return false
				}
			}
		}
		return true
	}

	def String getActivityTitle(EObject eObject) {
		var String title = ""
		if (eObject instanceof Activity)
			title = ACTIVITY_DESCRIPTION_DIAGRAM_TITLE_PART + eObject.name

		return title
	}

	def openActivityDescriptionDiagramForActivity(Activity element) {
		navigateToDiagram.createActivityDescriptionDiagram(element)
	}

	// navigating from diagram to code
	def EObject openTextEditor(EObject any) {
		println('Here')
		if (any !== null && any.eResource() instanceof XtextResource && any.eResource().getURI() !== null) {
			var fileURI = any.eResource().getURI().toPlatformString(true);
			var workspaceFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileURI));
			if (workspaceFile !== null) {
				var page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					println(workspaceFile)
					var editorType = getEditorType(workspaceFile)
					var openEditor = IDE.openEditor(page, workspaceFile, editorType, true);
					if ((openEditor instanceof AbstractTextEditor)) {

						var node = NodeModelUtils.findActualNodeFor(any);
						if (node !== null) {
							var offset = node.getOffset();
							var length = node.getTotalEndOffset() - offset;
							(openEditor as AbstractTextEditor).selectAndReveal(offset, length);
						}
					}
				// editorInput.
				} catch (PartInitException e) {
					System.out.println("" + e);
				// Put your exception handler here if you wish to.
				}
			}
		}
		return any;
	}

	def getEditorType(IFile file) {

		var fileExtension = file.name
		print(fileExtension + "Yehi hai right choice !!")
		if (fileExtension.endsWith('mncspec'))
			return MNC_EDITOR
		if (fileExtension.endsWith('activity'))
			return ACTIVITY_DIAGRAM_EDITOR
		if (fileExtension.endsWith('dml'))
			return DML_EDITOR
	}

	def Model getTheCapabilityResourceModel(Activity activity) {
		if (activity.bindCapability !== null) {
			var model = EcoreUtil2.getContainerOfType(activity.bindCapability, Model)
			return model
		}
	}


def findCommandResponseBlockForCommand(Command command){
	var model = EcoreUtil2.getContainerOfType(command,Model)
	if(model!==null   && model.systems!==null && model.systems.size>1){
		var cn = model.systems.last as ControlNode
		if(cn!==null && cn.commandResponseBlocks!==null && cn.commandResponseBlocks!==null 
			&& cn.commandResponseBlocks.size>0
		){
			for(crb : cn.commandResponseBlocks){
				if(crb.command.equals(command)){
					return crb
				}
			}
		}
	}
	
}






}
