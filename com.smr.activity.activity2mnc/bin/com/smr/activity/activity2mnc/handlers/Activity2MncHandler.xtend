package com.smr.activity.activity2mnc.handlers

import activityDiagramModel.ActivityDiagram
import com.google.inject.Guice
import com.google.inject.Inject
import com.mncml.dsl.MncRuntimeModule
import java.util.ArrayList
import org.eclipse.core.commands.AbstractHandler
import org.eclipse.core.commands.ExecutionEvent
import org.eclipse.core.commands.ExecutionException
import org.eclipse.core.resources.IFile
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.jface.dialogs.MessageDialog
import org.eclipse.ui.handlers.HandlerUtil
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2
import org.eclipse.xtext.parser.IParseResult
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.resource.XtextResourceSet
import org.eclipse.xtext.serializer.impl.Serializer
import org.eclipse.xtext.ui.editor.utils.EditorUtils
import org.eclipse.xtext.ui.resource.XtextLiveScopeResourceSetProvider
import org.eclipse.xtext.util.concurrent.IUnitOfWork
import org.eclipse.xtext.resource.SaveOptions
import org.eclipse.emf.common.util.URI
import com.mncml.dsl.serializer.MncCustomSerializer

class Activity2MncHandler extends AbstractHandler {

	@Inject
	XtextLiveScopeResourceSetProvider resourceSetProvider

	@Inject
	EclipseResourceFileSystemAccess2 fsa

	var public static resourceSet = null as XtextResourceSet

	override execute(ExecutionEvent event) throws ExecutionException {

		val activeEditor = EditorUtils.getActiveXtextEditor(event)
		val generate = MessageDialog.openConfirm(HandlerUtil.getActiveShell(event), "Generate MncSpec Code?",
			"Do you want to create Mnc-ML specifications file ?");
		if (!generate) {
		//	new Demo().doParse
			return null;
		}

		val file = activeEditor.getEditorInput().getAdapter(IFile) as IFile;
		if (file !== null) {
			try {
				val project = file.getProject();
				resourceSet = (resourceSetProvider.get(project) as XtextResourceSet)
				// var fileRes =  new ResourceImpl
				// fileRes.URI = URI.createURI(file.location.toOSString)
				// resourceSet = fileRes.resourceSet as XtextResourceSet
				resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
				// Checking active editor 
				if (activeEditor !== null) {

					activeEditor.document.readOnly(
						new IUnitOfWork<String, XtextResource>() {
							override exec(XtextResource xtextResource) throws Exception {
								var parseResult = xtextResource.parseResult as IParseResult
								if (parseResult !== null && parseResult.rootNode !== null) {
									var root = parseResult.rootASTElement
									val rootActivityDiagram = root as ActivityDiagram
									var mncGenerator = new GenerateMnCDesignFromActivityDiagram(rootActivityDiagram)
									var modelList = mncGenerator.mnCModels
//									var listOfResources = new ArrayList<Resource>()
									var injector = Guice.createInjector(new MncRuntimeModule);
									var serializer = injector.getInstance(Serializer);
									print(serializer)
									fsa.project = project
									fsa.outputPath = 'src-gen/code/'
									fsa.monitor = new NullProgressMonitor
									resourceSet.resources.clear
									for (m : modelList) {

										var s = serializer.serialize(m);
										print(s)
//										fsa.generateFile(m.name+".mncspec",s)
										var fileLocation = '''«project.location.toString»/src-gen/code/«m.name».mncspec'''
										var generatedRes = resourceSet.createResource(
											URI.createFileURI(fileLocation)
										) as XtextResource

										generatedRes.contents.add(m)
										generatedRes.save(null)

									}

								}
								return null
							}
						}
					)

				}
				return null
			} catch (Exception e) {
				e.printStackTrace
				return null
			}
		}
	}

}
