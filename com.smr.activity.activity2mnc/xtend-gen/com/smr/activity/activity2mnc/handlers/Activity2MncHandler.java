package com.smr.activity.activity2mnc.handlers;

import activityDiagramModel.ActivityDiagram;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.mncml.dsl.MncRuntimeModule;
import com.kide.synthesis.activity2mnc.GenerateMnCDesignFromActivityDiagram;
import java.util.List;
import mncModel.Model;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.serializer.impl.Serializer;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.ui.resource.XtextLiveScopeResourceSetProvider;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;

@SuppressWarnings("all")
public class Activity2MncHandler extends AbstractHandler {
  @Inject
  private XtextLiveScopeResourceSetProvider resourceSetProvider;
  
  @Inject
  private EclipseResourceFileSystemAccess2 fsa;
  
  public static XtextResourceSet resourceSet = ((XtextResourceSet) null);
  
  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {
    final XtextEditor activeEditor = EditorUtils.getActiveXtextEditor(event);
    final boolean generate = MessageDialog.openConfirm(HandlerUtil.getActiveShell(event), "Generate MncSpec Code?", 
      "Do you want to create Mnc-ML specifications file ?");
    if ((!generate)) {
      return null;
    }
    IFile _adapter = activeEditor.getEditorInput().<IFile>getAdapter(IFile.class);
    final IFile file = ((IFile) _adapter);
    if ((file != null)) {
      try {
        final IProject project = file.getProject();
        ResourceSet _get = this.resourceSetProvider.get(project);
        Activity2MncHandler.resourceSet = ((XtextResourceSet) _get);
        Activity2MncHandler.resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
        if ((activeEditor != null)) {
          IXtextDocument _document = activeEditor.getDocument();
          _document.<String>readOnly(
            new IUnitOfWork<String, XtextResource>() {
              @Override
              public String exec(final XtextResource xtextResource) throws Exception {
                IParseResult _parseResult = xtextResource.getParseResult();
                IParseResult parseResult = ((IParseResult) _parseResult);
                if (((parseResult != null) && (parseResult.getRootNode() != null))) {
                  EObject root = parseResult.getRootASTElement();
                  final ActivityDiagram rootActivityDiagram = ((ActivityDiagram) root);
                  GenerateMnCDesignFromActivityDiagram mncGenerator = new GenerateMnCDesignFromActivityDiagram(rootActivityDiagram);
                  List<Model> modelList = mncGenerator.getMnCModels();
                  MncRuntimeModule _mncRuntimeModule = new MncRuntimeModule();
                  Injector injector = Guice.createInjector(_mncRuntimeModule);
                  Serializer serializer = injector.<Serializer>getInstance(Serializer.class);
                  InputOutput.<Serializer>print(serializer);
                  Activity2MncHandler.this.fsa.setProject(project);
                  Activity2MncHandler.this.fsa.setOutputPath("src-gen/code/");
                  NullProgressMonitor _nullProgressMonitor = new NullProgressMonitor();
                  Activity2MncHandler.this.fsa.setMonitor(_nullProgressMonitor);
                  Activity2MncHandler.resourceSet.getResources().clear();
                  for (final Model m : modelList) {
                    {
                      String s = serializer.serialize(m);
                      InputOutput.<String>print(s);
                      StringConcatenation _builder = new StringConcatenation();
                      String _string = project.getLocation().toString();
                      _builder.append(_string);
                      _builder.append("/src-gen/code/");
                      String _name = m.getName();
                      _builder.append(_name);
                      _builder.append(".mncspec");
                      String fileLocation = _builder.toString();
                      Resource _createResource = Activity2MncHandler.resourceSet.createResource(
                        URI.createFileURI(fileLocation));
                      XtextResource generatedRes = ((XtextResource) _createResource);
                      generatedRes.getContents().add(m);
                      generatedRes.save(null);
                    }
                  }
                }
                return null;
              }
            });
        }
        return null;
      } catch (final Throwable _t) {
        if (_t instanceof Exception) {
          final Exception e = (Exception)_t;
          e.printStackTrace();
          return null;
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
    }
    return null;
  }
}
