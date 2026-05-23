package com.model.domain.activity.design.services;

import CapabilityDescription.Capability;
import activityDiagramModel.Activity;
import com.model.domain.activity.design.layout.NavigateToDiagram;
import dataModelPackage.AbstractType;
import dataModelPackage.ArrayType;
import dataModelPackage.DataModel;
import dataModelPackage.Parameter;
import dataModelPackage.PrimitiveValueType;
import dataModelPackage.SimpleType;
import java.util.Collection;
import mncModel.Command;
import mncModel.CommandResponseBlock;
import mncModel.ControlNode;
import mncModel.Model;
import mncModel.ResponseBlock;
import operationsDescription.Operation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
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
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class ActivityServices {
  private NavigateToDiagram navigateToDiagram;
  
  public static String DIAGRAM_TITLE = "";
  
  public static String ACTIVITY_DESCRIPTION_DIAGRAM_TITLE_PART = "Activity Description Diagram for ";
  
  public static String ACTIVITY_DIAGRAM_EDITOR = "com.smr.activity.dsl.ActivityDiagram";
  
  public static String MNC_EDITOR = "com.mncml.dsl.Mnc";
  
  public static String DML_EDITOR = "com.dml.dsl.Dml";
  
  public ActivityServices() {
    NavigateToDiagram _navigateToDiagram = new NavigateToDiagram();
    this.navigateToDiagram = _navigateToDiagram;
  }
  
  public EList<Operation> getOperationForAnActivity(final Activity activity) {
    EList<Operation> _requiresOperation = activity.getRequiresOperation();
    boolean _tripleNotEquals = (_requiresOperation != null);
    if (_tripleNotEquals) {
      return activity.getRequiresOperation();
    }
    return null;
  }
  
  public String getNameSignatureForOperation(final Operation operation) {
    String inputParams = this.populateParametersString(operation.getInputParameters(), true);
    BasicEList<Parameter> opPars = new BasicEList<Parameter>();
    opPars.add(operation.getOutputParameters());
    String outputParams = this.populateParametersString(opPars, false);
    StringConcatenation _builder = new StringConcatenation();
    String _name = operation.getName();
    _builder.append(_name);
    _builder.append("(");
    _builder.append(inputParams);
    _builder.append("):");
    _builder.append(outputParams);
    _builder.newLineIfNotEmpty();
    _builder.append("Script Path : ");
    String _executableScript = operation.getExecutableScript();
    _builder.append(_executableScript);
    _builder.newLineIfNotEmpty();
    String operationSignature = _builder.toString();
    return operationSignature;
  }
  
  public String getCapabilityNameSignature(final Command capability) {
    String inputParameter = "";
    String outputParameter = "";
    inputParameter = this.populateParametersString(capability.getParameters(), true);
    CommandResponseBlock commandResponseBlock = this.findCommandResponseBlockForCommand(capability);
    BasicEList<Parameter> respsParList = this.getResponseParametersListForCommand(commandResponseBlock);
    outputParameter = this.populateParametersString(respsParList, false);
    String _name = capability.getName();
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(");
    _builder.append(inputParameter);
    _builder.append("):");
    _builder.append(outputParameter);
    String signature = (_name + _builder);
    return signature;
  }
  
  public BasicEList<Parameter> getResponseParametersListForCommand(final CommandResponseBlock commandResponseBlock) {
    BasicEList<Parameter> respsParList = new BasicEList<Parameter>();
    if (((((commandResponseBlock != null) && (commandResponseBlock.getResponseBlock() != null)) && (commandResponseBlock.getResponseBlock() != null)) && (commandResponseBlock.getResponseBlock().size() > 0))) {
      EList<ResponseBlock> _responseBlock = commandResponseBlock.getResponseBlock();
      for (final ResponseBlock resBl : _responseBlock) {
        respsParList.addAll(resBl.getResponse().getParameters());
      }
    }
    return respsParList;
  }
  
  public BasicEList<Parameter> getOutputParametersForACapability(final Command capability) {
    CommandResponseBlock commandResponseBlock = this.findCommandResponseBlockForCommand(capability);
    BasicEList<Parameter> respsParList = this.getResponseParametersListForCommand(commandResponseBlock);
    return respsParList;
  }
  
  public String populateParametersString(final EList<Parameter> parameters, final boolean isInput) {
    String string = "";
    if (((parameters != null) && (parameters.size() > 0))) {
      for (final Parameter par : parameters) {
        {
          if (isInput) {
            String _parameterName = ActivityServices.getParameterName(par);
            String _plus = (string + _parameterName);
            String _plus_1 = (_plus + ":");
            String _parameterType = ActivityServices.getParameterType(par);
            String _plus_2 = (_plus_1 + _parameterType);
            string = _plus_2;
          } else {
            String _parameterType_1 = ActivityServices.getParameterType(par);
            String _plus_3 = (string + _parameterType_1);
            string = _plus_3;
          }
          boolean _equals = par.equals(IterableExtensions.<Parameter>last(parameters));
          boolean _not = (!_equals);
          if (_not) {
            string = (string + ",");
          }
        }
      }
    } else {
      string = "Void";
    }
    InputOutput.<String>println(string);
    return string;
  }
  
  public static String getParameterName(final Parameter parameter) {
    if ((parameter != null)) {
      if ((parameter instanceof SimpleType)) {
        return ((SimpleType)parameter).getName();
      }
      if ((parameter instanceof ArrayType)) {
        return ((ArrayType)parameter).getName();
      }
      if ((parameter instanceof AbstractType)) {
        return ((AbstractType)parameter).getName();
      }
    }
    return null;
  }
  
  public static String getParameterType(final Parameter parameter) {
    if ((parameter != null)) {
      if ((parameter instanceof SimpleType)) {
        return ((SimpleType)parameter).getType().getName();
      }
      if ((parameter instanceof ArrayType)) {
        DataModel _dataModelType = ((ArrayType)parameter).getDataModelType();
        boolean _tripleNotEquals = (_dataModelType != null);
        if (_tripleNotEquals) {
          String _name = ((ArrayType)parameter).getDataModelType().getName();
          return (_name + "[]");
        }
        PrimitiveValueType _primitiveType = ((ArrayType)parameter).getPrimitiveType();
        boolean _tripleNotEquals_1 = (_primitiveType != null);
        if (_tripleNotEquals_1) {
          return ((ArrayType)parameter).getPrimitiveType().getName();
        }
      }
      if ((parameter instanceof AbstractType)) {
        return ((AbstractType)parameter).getType().getName();
      }
    }
    return null;
  }
  
  public boolean singleActivityRepresentationCheckForActivity(final EObject any) {
    ActivityServices.DIAGRAM_TITLE = this.getActivityTitle(any);
    final Session session = SessionManager.INSTANCE.getSession(any);
    final Collection<DRepresentation> representations = DialectManager.INSTANCE.getAllRepresentations(session);
    for (final DRepresentation representation : representations) {
      if ((representation instanceof DSemanticDiagram)) {
        final DSemanticDiagram diagram = ((DSemanticDiagram) representation);
        boolean _equals = ActivityServices.DIAGRAM_TITLE.equals(diagram.getName());
        if (_equals) {
          return false;
        }
      }
    }
    return true;
  }
  
  public String getActivityTitle(final EObject eObject) {
    String title = "";
    if ((eObject instanceof Activity)) {
      String _name = ((Activity)eObject).getName();
      String _plus = (ActivityServices.ACTIVITY_DESCRIPTION_DIAGRAM_TITLE_PART + _name);
      title = _plus;
    }
    return title;
  }
  
  public void openActivityDescriptionDiagramForActivity(final Activity element) {
    this.navigateToDiagram.createActivityDescriptionDiagram(element);
  }
  
  public EObject openTextEditor(final EObject any) {
    InputOutput.<String>println("Here");
    if ((((any != null) && (any.eResource() instanceof XtextResource)) && (any.eResource().getURI() != null))) {
      String fileURI = any.eResource().getURI().toPlatformString(true);
      IWorkspaceRoot _root = ResourcesPlugin.getWorkspace().getRoot();
      Path _path = new Path(fileURI);
      IFile workspaceFile = _root.getFile(_path);
      if ((workspaceFile != null)) {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        try {
          InputOutput.<IFile>println(workspaceFile);
          String editorType = this.getEditorType(workspaceFile);
          IEditorPart openEditor = IDE.openEditor(page, workspaceFile, editorType, true);
          if ((openEditor instanceof AbstractTextEditor)) {
            ICompositeNode node = NodeModelUtils.findActualNodeFor(any);
            if ((node != null)) {
              int offset = node.getOffset();
              int _totalEndOffset = node.getTotalEndOffset();
              int length = (_totalEndOffset - offset);
              ((AbstractTextEditor) openEditor).selectAndReveal(offset, length);
            }
          }
        } catch (final Throwable _t) {
          if (_t instanceof PartInitException) {
            final PartInitException e = (PartInitException)_t;
            System.out.println(("" + e));
          } else {
            throw Exceptions.sneakyThrow(_t);
          }
        }
      }
    }
    return any;
  }
  
  public String getEditorType(final IFile file) {
    String fileExtension = file.getName();
    InputOutput.<String>print((fileExtension + "Yehi hai right choice !!"));
    boolean _endsWith = fileExtension.endsWith("mncspec");
    if (_endsWith) {
      return ActivityServices.MNC_EDITOR;
    }
    boolean _endsWith_1 = fileExtension.endsWith("activity");
    if (_endsWith_1) {
      return ActivityServices.ACTIVITY_DIAGRAM_EDITOR;
    }
    boolean _endsWith_2 = fileExtension.endsWith("dml");
    if (_endsWith_2) {
      return ActivityServices.DML_EDITOR;
    }
    return null;
  }
  
  public Model getTheCapabilityResourceModel(final Activity activity) {
    Capability _bindCapability = activity.getBindCapability();
    boolean _tripleNotEquals = (_bindCapability != null);
    if (_tripleNotEquals) {
      Model model = EcoreUtil2.<Model>getContainerOfType(activity.getBindCapability(), Model.class);
      return model;
    }
    return null;
  }
  
  public CommandResponseBlock findCommandResponseBlockForCommand(final Command command) {
    Model model = EcoreUtil2.<Model>getContainerOfType(command, Model.class);
    if ((((model != null) && (model.getSystems() != null)) && (model.getSystems().size() > 1))) {
      mncModel.System _last = IterableExtensions.<mncModel.System>last(model.getSystems());
      ControlNode cn = ((ControlNode) _last);
      if (((((cn != null) && (cn.getCommandResponseBlocks() != null)) && (cn.getCommandResponseBlocks() != null)) && (cn.getCommandResponseBlocks().size() > 0))) {
        EList<CommandResponseBlock> _commandResponseBlocks = cn.getCommandResponseBlocks();
        for (final CommandResponseBlock crb : _commandResponseBlocks) {
          boolean _equals = crb.getCommand().equals(command);
          if (_equals) {
            return crb;
          }
        }
      }
    }
    return null;
  }
}
