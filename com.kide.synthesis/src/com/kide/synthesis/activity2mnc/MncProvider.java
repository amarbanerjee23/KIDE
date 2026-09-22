package com.kide.synthesis.activity2mnc;

import CapabilityDescription.Capability;
import CapabilityDescription.ControlCapabilities;
import activityDiagramModel.Activity;
import activityDiagramModel.ActivityDiagram;
import activityDiagramModel.ConditionalActivity;
import activityDiagramModel.Outcome;
import com.google.common.collect.Iterables;
import com.kide.synthesis.activity2mnc.GenerateMnCDesignFromActivityDiagram;
import dataModelPackage.AbstractType;
import dataModelPackage.ArrayType;
import dataModelPackage.DataModelFactory;
import dataModelPackage.Parameter;
import dataModelPackage.SimpleType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import mncModel.AbstractInterfaceItems;
import mncModel.AbstractOutcomeItems;
import mncModel.Action;
import mncModel.ActionAlarm;
import mncModel.ActionCommand;
import mncModel.ActionDataPoint;
import mncModel.ActionEvent;
import mncModel.ActionOperation;
import mncModel.Alarm;
import mncModel.AlarmBlock;
import mncModel.CheckParameterCondition;
import mncModel.Command;
import mncModel.CommandResponseBlock;
import mncModel.ControlNode;
import mncModel.DataPoint;
import mncModel.DataPointBlock;
import mncModel.Event;
import mncModel.EventBlock;
import mncModel.InterfaceDescription;
import mncModel.MncModelFactory;
import mncModel.Model;
import mncModel.OperatingState;
import mncModel.Response;
import mncModel.ResponseBlock;
import mncModel.Transition;
import mncModel.Validation;
import operationsDescription.Operation;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class MncProvider {
  private final HashSet<InterfaceDescription> usedInterfaces = new LinkedHashSet<InterfaceDescription>();
  
  private final HashSet<Action> initExecutableActions = new LinkedHashSet<Action>();
  
  private final HashSet<ControlNode> childNodes = new LinkedHashSet<ControlNode>();
  
  private final HashSet<OperatingState> operatingState = new LinkedHashSet<OperatingState>();
  
  private final HashSet<CommandResponseBlock> commandResponseBlocks = new LinkedHashSet<CommandResponseBlock>();
  
  private final HashSet<EventBlock> eventBlocks = new LinkedHashSet<EventBlock>();
  
  private final HashSet<DataPointBlock> dataPointBlocks = new LinkedHashSet<DataPointBlock>();
  
  private final HashSet<AlarmBlock> alarmBlocks = new LinkedHashSet<AlarmBlock>();
  
  public HashMap<String, Set> getParseInfo() {
    HashMap<String, Set> parseInfor = new HashMap<String, Set>();
    parseInfor.put("childNodes", this.childNodes);
    parseInfor.put("uses", this.usedInterfaces);
    parseInfor.put("initActions", this.initExecutableActions);
    parseInfor.put("eventBlocks", this.eventBlocks);
    parseInfor.put("alarmBlocks", this.alarmBlocks);
    parseInfor.put("operatingStates", this.operatingState);
    parseInfor.put("commandResponseBlocks", this.commandResponseBlocks);
    return parseInfor;
  }
  
  public void parseActivityDiagram(final ActivityDiagram activityDiagram) {
    if (((activityDiagram.getActivities() != null) && (activityDiagram.getActivities().size() > 0))) {
      EList<Activity> _activities = activityDiagram.getActivities();
      for (final Activity activity : _activities) {
        {
          Capability _bindCapability = activity.getBindCapability();
          boolean _tripleNotEquals = (_bindCapability != null);
          if (_tripleNotEquals) {
            BasicEList<AbstractInterfaceItems> capabilityItems = this.capabilitiesAbstractInterfaceItems(activity);
            for (final AbstractInterfaceItems item : capabilityItems) {
              {
                boolean actionItemExists = this.checkIfActionContains(item);
                if ((item instanceof Command)) {
                  String _name = ((Command)item).getName();
                  String _plus = ("###" + _name);
                  InputOutput.<String>println(_plus);
                  this.getCapabilityCommandResponseBlock(((Command)item), activity);
                }
                if ((item instanceof Event)) {
                  if ((!actionItemExists)) {
                    EList<ActionEvent> _publishEvent = this.ensureInitAction().getPublishEvent();
                    ActionEvent _createActionEvent = MncModelFactory.eINSTANCE.createActionEvent();
                    final Procedure1<ActionEvent> _function = new Procedure1<ActionEvent>() {
                      @Override
                      public void apply(final ActionEvent it) {
                        it.setEvent(((Event) item));
                      }
                    };
                    ActionEvent _doubleArrow = ObjectExtensions.<ActionEvent>operator_doubleArrow(_createActionEvent, _function);
                    _publishEvent.add(_doubleArrow);
                  }
                  if ((activity != null)) {
                    this.getCapabilityEventBlock(((Event)item), activity);
                  }
                }
                if ((item instanceof Alarm)) {
                  if ((!actionItemExists)) {
                    EList<ActionAlarm> _raiseAlarm = this.ensureInitAction().getRaiseAlarm();
                    ActionAlarm _createActionAlarm = MncModelFactory.eINSTANCE.createActionAlarm();
                    final Procedure1<ActionAlarm> _function_1 = new Procedure1<ActionAlarm>() {
                      @Override
                      public void apply(final ActionAlarm it) {
                        it.setAlarm(((Alarm) item));
                      }
                    };
                    ActionAlarm _doubleArrow_1 = ObjectExtensions.<ActionAlarm>operator_doubleArrow(_createActionAlarm, _function_1);
                    _raiseAlarm.add(_doubleArrow_1);
                  }
                  if ((activity != null)) {
                    this.getCapabilityAlarmBlock(((Alarm)item), activity);
                  }
                }
                if ((item instanceof DataPoint)) {
                  if ((!actionItemExists)) {
                    EList<ActionDataPoint> _triggerDataPoint = this.ensureInitAction().getTriggerDataPoint();
                    ActionDataPoint _createActionDataPoint = MncModelFactory.eINSTANCE.createActionDataPoint();
                    final Procedure1<ActionDataPoint> _function_2 = new Procedure1<ActionDataPoint>() {
                      @Override
                      public void apply(final ActionDataPoint it) {
                        it.setDataPoint(((DataPoint) item));
                      }
                    };
                    ActionDataPoint _doubleArrow_2 = ObjectExtensions.<ActionDataPoint>operator_doubleArrow(_createActionDataPoint, _function_2);
                    _triggerDataPoint.add(_doubleArrow_2);
                  }
                }
              }
            }
          }
          EList<Operation> _requiresOperation = activity.getRequiresOperation();
          boolean _tripleNotEquals_1 = (_requiresOperation != null);
          if (_tripleNotEquals_1) {
          }
          ActivityDiagram _childActivityDiagram = activity.getChildActivityDiagram();
          boolean _tripleNotEquals_2 = (_childActivityDiagram != null);
          if (_tripleNotEquals_2) {
            Model childModel = new GenerateMnCDesignFromActivityDiagram().parseActivityDiagramToGenerateAnMncModel(activity.getChildActivityDiagram());
            if ((childModel != null)) {
              mncModel.System _get = childModel.getSystems().get(1);
              ControlNode childController = ((ControlNode) _get);
              this.childNodes.add(childController);
            }
          }
        }
      }
    }
  }
  
  private Action ensureInitAction() {
    Action existing = IterableExtensions.<Action>head(this.initExecutableActions);
    if (existing != null) {
      return existing;
    }
    Action created = MncModelFactory.eINSTANCE.createAction();
    this.initExecutableActions.add(created);
    return created;
  }
  
  public boolean getCapabilityCommandResponseBlock(final Command com, final Activity activity) {
    boolean _xblockexpression = false;
    {
      String _name = com.getName();
      String _plus = ("***" + _name);
      InputOutput.<String>println(_plus);
      final Function1<CommandResponseBlock, Boolean> _function = new Function1<CommandResponseBlock, Boolean>() {
        @Override
        public Boolean apply(final CommandResponseBlock it) {
          return Boolean.valueOf(it.getCommand().getName().equals(com.getName()));
        }
      };
      CommandResponseBlock commandResponseBlock = IterableExtensions.<CommandResponseBlock>head(IterableExtensions.<CommandResponseBlock>filter(IterableExtensions.<CommandResponseBlock>filterNull(this.commandResponseBlocks), _function));
      if ((commandResponseBlock == null)) {
        CommandResponseBlock _createCommandResponseBlock = MncModelFactory.eINSTANCE.createCommandResponseBlock();
        final Procedure1<CommandResponseBlock> _function_1 = new Procedure1<CommandResponseBlock>() {
          @Override
          public void apply(final CommandResponseBlock it) {
            Command _createCommand = MncModelFactory.eINSTANCE.createCommand();
            final Procedure1<Command> _function = new Procedure1<Command>() {
              @Override
              public void apply(final Command it) {
                it.setName(com.getName());
                EList<Parameter> _parameters = com.getParameters();
                boolean _tripleNotEquals = (_parameters != null);
                if (_tripleNotEquals) {
                  EList<Parameter> _parameters_1 = com.getParameters();
                  for (final Parameter par : _parameters_1) {
                    if ((par instanceof SimpleType)) {
                      EList<Parameter> _parameters_2 = it.getParameters();
                      SimpleType _createSimpleType = DataModelFactory.eINSTANCE.createSimpleType();
                      final Procedure1<SimpleType> _function = new Procedure1<SimpleType>() {
                        @Override
                        public void apply(final SimpleType it) {
                          it.setName(((SimpleType)par).getName());
                          it.setType(((SimpleType)par).getType());
                          it.setValue(((SimpleType)par).getValue());
                        }
                      };
                      SimpleType _doubleArrow = ObjectExtensions.<SimpleType>operator_doubleArrow(_createSimpleType, _function);
                      _parameters_2.add(_doubleArrow);
                    }
                  }
                }
              }
            };
            Command _doubleArrow = ObjectExtensions.<Command>operator_doubleArrow(_createCommand, _function);
            it.setCommand(_doubleArrow);
          }
        };
        CommandResponseBlock _doubleArrow = ObjectExtensions.<CommandResponseBlock>operator_doubleArrow(_createCommandResponseBlock, _function_1);
        commandResponseBlock = _doubleArrow;
        Action _createAction = MncModelFactory.eINSTANCE.createAction();
        final Procedure1<Action> _function_2 = new Procedure1<Action>() {
          @Override
          public void apply(final Action it) {
            EList<ActionCommand> _fireCommand = it.getFireCommand();
            ActionCommand _createActionCommand = MncModelFactory.eINSTANCE.createActionCommand();
            final Procedure1<ActionCommand> _function = new Procedure1<ActionCommand>() {
              @Override
              public void apply(final ActionCommand it) {
                it.setCommand(com);
              }
            };
            ActionCommand _doubleArrow = ObjectExtensions.<ActionCommand>operator_doubleArrow(_createActionCommand, _function);
            _fireCommand.add(_doubleArrow);
          }
        };
        Action _doubleArrow_1 = ObjectExtensions.<Action>operator_doubleArrow(_createAction, _function_2);
        commandResponseBlock.setAction(_doubleArrow_1);
      } else {
        this.commandResponseBlocks.remove(commandResponseBlock);
      }
      EList<ConditionalActivity> _conditionalActivity = activity.getConditionalActivity();
      boolean _tripleNotEquals = (_conditionalActivity != null);
      if (_tripleNotEquals) {
        EList<ConditionalActivity> _conditionalActivity_1 = activity.getConditionalActivity();
        for (final ConditionalActivity cond : _conditionalActivity_1) {
          EList<Outcome> _outcome = cond.getOutcome();
          boolean _tripleNotEquals_1 = (_outcome != null);
          if (_tripleNotEquals_1) {
            EList<Outcome> _outcome_1 = cond.getOutcome();
            for (final Outcome outcome : _outcome_1) {
              {
                final AbstractOutcomeItems item = outcome.getCapabilityOutcome();
                if ((item instanceof Response)) {
                  final Validation validation = this.getValidationForCondition(cond);
                  if (((commandResponseBlock.getAction() != null) && 
                    (commandResponseBlock.getAction().getFireCommand() != null))) {
                    final Function1<ActionCommand, Boolean> _function_3 = new Function1<ActionCommand, Boolean>() {
                      @Override
                      public Boolean apply(final ActionCommand it) {
                        return Boolean.valueOf(it.getCommand().equals(com));
                      }
                    };
                    ActionCommand fireCommand = IterableExtensions.<ActionCommand>head(IterableExtensions.<ActionCommand>filter(IterableExtensions.<ActionCommand>filterNull(commandResponseBlock.getAction().getFireCommand()), _function_3));
                    if ((fireCommand != null)) {
                      EList<ActionCommand> _fireCommand = commandResponseBlock.getAction().getFireCommand();
                      _fireCommand.remove(fireCommand);
                      EList<ResponseBlock> _responseHandling = fireCommand.getResponseHandling();
                      ResponseBlock _createResponseBlock = MncModelFactory.eINSTANCE.createResponseBlock();
                      final Procedure1<ResponseBlock> _function_4 = new Procedure1<ResponseBlock>() {
                        @Override
                        public void apply(final ResponseBlock it) {
                          AbstractOutcomeItems _capabilityOutcome = outcome.getCapabilityOutcome();
                          it.setResponse(((Response) _capabilityOutcome));
                          if ((validation != null)) {
                            EList<Validation> _validationRules = it.getValidationRules();
                            _validationRules.add(validation);
                          }
                        }
                      };
                      ResponseBlock _doubleArrow_2 = ObjectExtensions.<ResponseBlock>operator_doubleArrow(_createResponseBlock, _function_4);
                      _responseHandling.add(_doubleArrow_2);
                      EList<ActionCommand> _fireCommand_1 = commandResponseBlock.getAction().getFireCommand();
                      _fireCommand_1.add(fireCommand);
                    }
                  }
                } else {
                  this.getBlocksForConditionOutcomes(cond);
                }
              }
            }
          }
        }
      }
      _xblockexpression = this.commandResponseBlocks.add(commandResponseBlock);
    }
    return _xblockexpression;
  }
  
  public void getBlocksForConditionOutcomes(final ConditionalActivity cond) {
    EList<Outcome> _outcome = cond.getOutcome();
    boolean _tripleNotEquals = (_outcome != null);
    if (_tripleNotEquals) {
      final Validation validation = this.getValidationForCondition(cond);
      EList<Outcome> _outcome_1 = cond.getOutcome();
      for (final Outcome outcome : _outcome_1) {
        {
          final AbstractOutcomeItems item = outcome.getCapabilityOutcome();
          if ((item instanceof Event)) {
            final Function1<EventBlock, Boolean> _function = new Function1<EventBlock, Boolean>() {
              @Override
              public Boolean apply(final EventBlock it) {
                return Boolean.valueOf(it.getEvent().equals(item));
              }
            };
            EventBlock evblock = IterableExtensions.<EventBlock>head(IterableExtensions.<EventBlock>filter(IterableExtensions.<EventBlock>filterNull(this.eventBlocks), _function));
            if ((evblock == null)) {
              EventBlock _createEventBlock = MncModelFactory.eINSTANCE.createEventBlock();
              final Procedure1<EventBlock> _function_1 = new Procedure1<EventBlock>() {
                @Override
                public void apply(final EventBlock it) {
                  it.setEvent(((Event)item));
                }
              };
              EventBlock _doubleArrow = ObjectExtensions.<EventBlock>operator_doubleArrow(_createEventBlock, _function_1);
              evblock = _doubleArrow;
            } else {
              this.eventBlocks.remove(evblock);
            }
            EList<Validation> _validationRules = evblock.getValidationRules();
            _validationRules.add(validation);
            this.eventBlocks.add(evblock);
          }
          if ((item instanceof Alarm)) {
            final Function1<AlarmBlock, Boolean> _function_2 = new Function1<AlarmBlock, Boolean>() {
              @Override
              public Boolean apply(final AlarmBlock it) {
                return Boolean.valueOf(it.getAlarm().equals(item));
              }
            };
            AlarmBlock alblock = IterableExtensions.<AlarmBlock>head(IterableExtensions.<AlarmBlock>filter(IterableExtensions.<AlarmBlock>filterNull(this.alarmBlocks), _function_2));
            if ((alblock == null)) {
              AlarmBlock _createAlarmBlock = MncModelFactory.eINSTANCE.createAlarmBlock();
              final Procedure1<AlarmBlock> _function_3 = new Procedure1<AlarmBlock>() {
                @Override
                public void apply(final AlarmBlock it) {
                  it.setAlarm(((Alarm)item));
                }
              };
              AlarmBlock _doubleArrow_1 = ObjectExtensions.<AlarmBlock>operator_doubleArrow(_createAlarmBlock, _function_3);
              alblock = _doubleArrow_1;
            } else {
              this.alarmBlocks.remove(alblock);
            }
            Action _onSuccessAction = validation.getOnSuccessAction();
            boolean _tripleNotEquals_1 = (_onSuccessAction != null);
            if (_tripleNotEquals_1) {
              EList<ActionAlarm> _raiseAlarm = validation.getOnSuccessAction().getRaiseAlarm();
              ActionAlarm _createActionAlarm = MncModelFactory.eINSTANCE.createActionAlarm();
              final Procedure1<ActionAlarm> _function_4 = new Procedure1<ActionAlarm>() {
                @Override
                public void apply(final ActionAlarm it) {
                  Alarm _createAlarm = MncModelFactory.eINSTANCE.createAlarm();
                  final Procedure1<Alarm> _function = new Procedure1<Alarm>() {
                    @Override
                    public void apply(final Alarm it) {
                      it.setName("Aborted");
                    }
                  };
                  Alarm _doubleArrow = ObjectExtensions.<Alarm>operator_doubleArrow(_createAlarm, _function);
                  it.setAlarm(_doubleArrow);
                }
              };
              ActionAlarm _doubleArrow_2 = ObjectExtensions.<ActionAlarm>operator_doubleArrow(_createActionAlarm, _function_4);
              _raiseAlarm.add(_doubleArrow_2);
            }
            EList<Validation> _validationRules_1 = alblock.getValidationRules();
            _validationRules_1.add(validation);
            this.alarmBlocks.add(alblock);
          }
        }
      }
    }
  }
  
  public void getCapabilityEventBlock(final Event evt, final Activity activity) {
    EventBlock block = ((EventBlock) null);
    final Function1<EventBlock, Boolean> _function = new Function1<EventBlock, Boolean>() {
      @Override
      public Boolean apply(final EventBlock it) {
        return Boolean.valueOf(it.getEvent().equals(evt));
      }
    };
    Iterable<EventBlock> eventsBlockList = IterableExtensions.<EventBlock>filter(IterableExtensions.<EventBlock>filterNull(this.eventBlocks), _function);
    int _size = IterableExtensions.size(eventsBlockList);
    boolean _tripleEquals = (_size == 0);
    if (_tripleEquals) {
      EventBlock _createEventBlock = MncModelFactory.eINSTANCE.createEventBlock();
      final Procedure1<EventBlock> _function_1 = new Procedure1<EventBlock>() {
        @Override
        public void apply(final EventBlock it) {
          it.setEvent(evt);
        }
      };
      EventBlock _doubleArrow = ObjectExtensions.<EventBlock>operator_doubleArrow(_createEventBlock, _function_1);
      block = _doubleArrow;
    } else {
      block = IterableExtensions.<EventBlock>head(eventsBlockList);
      this.eventBlocks.remove(block);
    }
    if (((activity != null) && (activity.getConditionalActivity() != null))) {
      EList<ConditionalActivity> _conditionalActivity = activity.getConditionalActivity();
      boolean _tripleNotEquals = (_conditionalActivity != null);
      if (_tripleNotEquals) {
        EList<ConditionalActivity> _conditionalActivity_1 = activity.getConditionalActivity();
        for (final ConditionalActivity cond : _conditionalActivity_1) {
          this.getBlocksForConditionOutcomes(cond);
        }
      }
    }
  }
  
  public void getCapabilityAlarmBlock(final Alarm item, final Activity activity) {
    AlarmBlock block = ((AlarmBlock) null);
    final Function1<AlarmBlock, Boolean> _function = new Function1<AlarmBlock, Boolean>() {
      @Override
      public Boolean apply(final AlarmBlock it) {
        return Boolean.valueOf(it.getAlarm().equals(item));
      }
    };
    Iterable<AlarmBlock> alarmsBlockList = IterableExtensions.<AlarmBlock>filter(IterableExtensions.<AlarmBlock>filterNull(this.alarmBlocks), _function);
    int _size = IterableExtensions.size(alarmsBlockList);
    boolean _tripleEquals = (_size == 0);
    if (_tripleEquals) {
      AlarmBlock _createAlarmBlock = MncModelFactory.eINSTANCE.createAlarmBlock();
      final Procedure1<AlarmBlock> _function_1 = new Procedure1<AlarmBlock>() {
        @Override
        public void apply(final AlarmBlock it) {
          it.setAlarm(item);
        }
      };
      AlarmBlock _doubleArrow = ObjectExtensions.<AlarmBlock>operator_doubleArrow(_createAlarmBlock, _function_1);
      block = _doubleArrow;
    } else {
      block = IterableExtensions.<AlarmBlock>head(alarmsBlockList);
      this.alarmBlocks.remove(block);
    }
    EList<ConditionalActivity> _conditionalActivity = activity.getConditionalActivity();
    boolean _tripleNotEquals = (_conditionalActivity != null);
    if (_tripleNotEquals) {
      EList<ConditionalActivity> _conditionalActivity_1 = activity.getConditionalActivity();
      for (final ConditionalActivity cond : _conditionalActivity_1) {
        this.getBlocksForConditionOutcomes(cond);
      }
    }
  }
  
  public Transition getTransitionsFromConditionalActivity(final ConditionalActivity condition) {
    final OperatingState currST = this.getStateExistsFromName(EcoreUtil2.<Activity>getContainerOfType(condition, Activity.class).getName());
    OperatingState nxtST = ((OperatingState) null);
    Activity _onTrueNextActivity = condition.getOnTrueNextActivity();
    boolean _tripleNotEquals = (_onTrueNextActivity != null);
    if (_tripleNotEquals) {
      nxtST = this.getStateExistsFromName(condition.getOnTrueNextActivity().getName());
    }
    Parameter _onTrueFinalResult = condition.getOnTrueFinalResult();
    boolean _tripleNotEquals_1 = (_onTrueFinalResult != null);
    if (_tripleNotEquals_1) {
      nxtST = this.getStateExistsFromName(this.parameterName(condition.getOnTrueFinalResult()));
    }
    Transition _createTransition = MncModelFactory.eINSTANCE.createTransition();
    final Procedure1<Transition> _function = new Procedure1<Transition>() {
      @Override
      public void apply(final Transition it) {
      }
    };
    Transition transition = ObjectExtensions.<Transition>operator_doubleArrow(_createTransition, _function);
    EList<OperatingState> _currentState = transition.getCurrentState();
    _currentState.add(currST);
    transition.setNextState(nxtST);
    return transition;
  }
  
  public String parameterName(final Parameter par) {
    if ((par instanceof SimpleType)) {
      return ((SimpleType)par).getName();
    }
    if ((par instanceof ArrayType)) {
      return ((ArrayType)par).getName();
    }
    if ((par instanceof AbstractType)) {
      return ((AbstractType)par).getName();
    }
    return null;
  }
  
  public Validation getValidationForCondition(final ConditionalActivity conditionalActivity) {
    Validation _createValidation = MncModelFactory.eINSTANCE.createValidation();
    final Procedure1<Validation> _function = new Procedure1<Validation>() {
      @Override
      public void apply(final Validation it) {
      }
    };
    Validation validation = ObjectExtensions.<Validation>operator_doubleArrow(_createValidation, _function);
    EList<Outcome> _outcome = conditionalActivity.getOutcome();
    boolean _tripleNotEquals = (_outcome != null);
    if (_tripleNotEquals) {
      EList<Outcome> _outcome_1 = conditionalActivity.getOutcome();
      for (final Outcome outcome : _outcome_1) {
        {
          Iterable<CheckParameterCondition> checkParemeterConditions = IterableExtensions.<CheckParameterCondition>filterNull(outcome.getOutcomeValidation());
          if ((checkParemeterConditions != null)) {
            EList<CheckParameterCondition> _parametersValidationRules = validation.getParametersValidationRules();
            List<CheckParameterCondition> _list = IterableExtensions.<CheckParameterCondition>toList(checkParemeterConditions);
            Iterables.<CheckParameterCondition>addAll(_parametersValidationRules, _list);
          }
          Activity _onTrueNextActivity = conditionalActivity.getOnTrueNextActivity();
          boolean _tripleNotEquals_1 = (_onTrueNextActivity != null);
          if (_tripleNotEquals_1) {
            Action _createAction = MncModelFactory.eINSTANCE.createAction();
            final Procedure1<Action> _function_1 = new Procedure1<Action>() {
              @Override
              public void apply(final Action it) {
                Activity nextActivity = conditionalActivity.getOnTrueNextActivity();
                if ((nextActivity != null)) {
                  final AbstractInterfaceItems capabilityInterfaceItem = IterableExtensions.<AbstractInterfaceItems>head(MncProvider.this.capabilitiesAbstractInterfaceItems(nextActivity));
                  if ((capabilityInterfaceItem instanceof Command)) {
                    EList<ActionCommand> _fireCommand = it.getFireCommand();
                    ActionCommand _createActionCommand = MncModelFactory.eINSTANCE.createActionCommand();
                    final Procedure1<ActionCommand> _function = new Procedure1<ActionCommand>() {
                      @Override
                      public void apply(final ActionCommand it) {
                        Command _createCommand = MncModelFactory.eINSTANCE.createCommand();
                        final Procedure1<Command> _function = new Procedure1<Command>() {
                          @Override
                          public void apply(final Command it) {
                            it.setName(((Command)capabilityInterfaceItem).getName());
                          }
                        };
                        Command _doubleArrow = ObjectExtensions.<Command>operator_doubleArrow(_createCommand, _function);
                        it.setCommand(_doubleArrow);
                      }
                    };
                    ActionCommand _doubleArrow = ObjectExtensions.<ActionCommand>operator_doubleArrow(_createActionCommand, _function);
                    _fireCommand.add(_doubleArrow);
                  }
                }
              }
            };
            Action _doubleArrow = ObjectExtensions.<Action>operator_doubleArrow(_createAction, _function_1);
            validation.setOnSuccessAction(_doubleArrow);
            Transition transition = this.getTransitionsFromConditionalActivity(conditionalActivity);
            EList<Transition> _transitionStates = validation.getOnSuccessAction().getTransitionStates();
            _transitionStates.add(transition);
          }
          Parameter _onTrueFinalResult = conditionalActivity.getOnTrueFinalResult();
          boolean _tripleNotEquals_2 = (_onTrueFinalResult != null);
          if (_tripleNotEquals_2) {
            final Transition transition_1 = this.getTransitionsFromConditionalActivity(conditionalActivity);
            Action _createAction_1 = MncModelFactory.eINSTANCE.createAction();
            final Procedure1<Action> _function_2 = new Procedure1<Action>() {
              @Override
              public void apply(final Action it) {
                EList<Transition> _transitionStates = it.getTransitionStates();
                _transitionStates.add(transition_1);
              }
            };
            Action _doubleArrow_1 = ObjectExtensions.<Action>operator_doubleArrow(_createAction_1, _function_2);
            validation.setOnSuccessAction(_doubleArrow_1);
          }
        }
      }
    }
    return validation;
  }
  
  public OperatingState getStateExistsFromName(final String string) {
    boolean flag = false;
    OperatingState opState = ((OperatingState) null);
    for (final OperatingState state : this.operatingState) {
      boolean _equalsIgnoreCase = state.getName().equalsIgnoreCase(string);
      if (_equalsIgnoreCase) {
        flag = true;
        return state;
      }
    }
    if ((!flag)) {
      OperatingState _createOperatingState = MncModelFactory.eINSTANCE.createOperatingState();
      final Procedure1<OperatingState> _function = new Procedure1<OperatingState>() {
        @Override
        public void apply(final OperatingState it) {
          it.setName(string);
        }
      };
      OperatingState _doubleArrow = ObjectExtensions.<OperatingState>operator_doubleArrow(_createOperatingState, _function);
      opState = _doubleArrow;
      this.operatingState.add(opState);
    }
    return opState;
  }
  
  public boolean checkIfActionContains(final AbstractInterfaceItems item) {
    for (final Action action : this.initExecutableActions) {
      {
        if (((item instanceof Alarm) && (action.getRaiseAlarm() != null))) {
          final Function1<ActionAlarm, Boolean> _function = new Function1<ActionAlarm, Boolean>() {
            @Override
            public Boolean apply(final ActionAlarm it) {
              return Boolean.valueOf(it.getAlarm().equals(item));
            }
          };
          int _size = IterableExtensions.size(IterableExtensions.<ActionAlarm>filter(IterableExtensions.<ActionAlarm>filterNull(action.getRaiseAlarm()), _function));
          return (_size > 0);
        }
        if (((item instanceof Event) && (action.getPublishEvent() != null))) {
          final Function1<ActionEvent, Boolean> _function_1 = new Function1<ActionEvent, Boolean>() {
            @Override
            public Boolean apply(final ActionEvent it) {
              return Boolean.valueOf(it.getEvent().equals(item));
            }
          };
          int _size_1 = IterableExtensions.size(IterableExtensions.<ActionEvent>filter(IterableExtensions.<ActionEvent>filterNull(action.getPublishEvent()), _function_1));
          return (_size_1 > 0);
        }
        if (((item instanceof DataPoint) && (action.getTriggerDataPoint() != null))) {
          final Function1<ActionDataPoint, Boolean> _function_2 = new Function1<ActionDataPoint, Boolean>() {
            @Override
            public Boolean apply(final ActionDataPoint it) {
              return Boolean.valueOf(it.getDataPoint().equals(item));
            }
          };
          int _size_2 = IterableExtensions.size(IterableExtensions.<ActionDataPoint>filter(IterableExtensions.<ActionDataPoint>filterNull(action.getTriggerDataPoint()), _function_2));
          return (_size_2 > 0);
        }
        if (((item instanceof Command) && (action.getFireCommand() != null))) {
          final Function1<ActionCommand, Boolean> _function_3 = new Function1<ActionCommand, Boolean>() {
            @Override
            public Boolean apply(final ActionCommand it) {
              return Boolean.valueOf(it.getCommand().equals(item));
            }
          };
          int _size_3 = IterableExtensions.size(IterableExtensions.<ActionCommand>filter(IterableExtensions.<ActionCommand>filterNull(action.getFireCommand()), _function_3));
          return (_size_3 > 0);
        }
        if (((item instanceof Operation) && action.getExecuteOperation().contains(item))) {
          final Function1<ActionOperation, Boolean> _function_4 = new Function1<ActionOperation, Boolean>() {
            @Override
            public Boolean apply(final ActionOperation it) {
              return Boolean.valueOf(it.getOperation().equals(item));
            }
          };
          int _size_4 = IterableExtensions.size(IterableExtensions.<ActionOperation>filter(IterableExtensions.<ActionOperation>filterNull(action.getExecuteOperation()), _function_4));
          return (_size_4 > 0);
        }
      }
    }
    return false;
  }
  
  public BasicEList<AbstractInterfaceItems> capabilitiesAbstractInterfaceItems(final Activity activity) {
    BasicEList<AbstractInterfaceItems> controlCapabilitiesAbsractItems = new BasicEList<AbstractInterfaceItems>();
    Capability _bindCapability = activity.getBindCapability();
    boolean _tripleNotEquals = (_bindCapability != null);
    if (_tripleNotEquals) {
      final Capability capability = activity.getBindCapability();
      EList<InterfaceDescription> _componentInterface = capability.getComponentInterface();
      boolean _tripleNotEquals_1 = (_componentInterface != null);
      if (_tripleNotEquals_1) {
        EList<InterfaceDescription> capIds = capability.getComponentInterface();
        Iterables.<InterfaceDescription>addAll(this.usedInterfaces, capIds);
        Action _requiredINITProcess = capability.getRequiredINITProcess();
        boolean _tripleNotEquals_2 = (_requiredINITProcess != null);
        if (_tripleNotEquals_2) {
          Action _requiredINITProcess_1 = capability.getRequiredINITProcess();
          this.initExecutableActions.add(_requiredINITProcess_1);
        }
        ControlCapabilities capabilityControlFromCapabilityDescription = capability.getProvidesControlCapabilities();
        EList<AbstractInterfaceItems> capabilityControlFromActivityCapabilityLink = activity.getUseControlCapabilities();
        if (((capabilityControlFromActivityCapabilityLink != null) && 
          (capabilityControlFromActivityCapabilityLink.size() > 0))) {
          Iterables.<AbstractInterfaceItems>addAll(controlCapabilitiesAbsractItems, capabilityControlFromActivityCapabilityLink);
        } else {
          if ((capabilityControlFromCapabilityDescription != null)) {
            EList<Command> _commands = capabilityControlFromCapabilityDescription.getCommands();
            boolean _tripleNotEquals_3 = (_commands != null);
            if (_tripleNotEquals_3) {
              EList<Command> _commands_1 = capabilityControlFromCapabilityDescription.getCommands();
              Iterables.<AbstractInterfaceItems>addAll(controlCapabilitiesAbsractItems, _commands_1);
            }
            EList<Event> _events = capabilityControlFromCapabilityDescription.getEvents();
            boolean _tripleNotEquals_4 = (_events != null);
            if (_tripleNotEquals_4) {
              EList<Event> _events_1 = capabilityControlFromCapabilityDescription.getEvents();
              Iterables.<AbstractInterfaceItems>addAll(controlCapabilitiesAbsractItems, _events_1);
            }
            EList<Alarm> _alarms = capabilityControlFromCapabilityDescription.getAlarms();
            boolean _tripleNotEquals_5 = (_alarms != null);
            if (_tripleNotEquals_5) {
              EList<Alarm> _alarms_1 = capabilityControlFromCapabilityDescription.getAlarms();
              Iterables.<AbstractInterfaceItems>addAll(controlCapabilitiesAbsractItems, _alarms_1);
            }
            EList<DataPoint> _dataPoints = capabilityControlFromCapabilityDescription.getDataPoints();
            boolean _tripleNotEquals_6 = (_dataPoints != null);
            if (_tripleNotEquals_6) {
              EList<DataPoint> _dataPoints_1 = capabilityControlFromCapabilityDescription.getDataPoints();
              Iterables.<AbstractInterfaceItems>addAll(controlCapabilitiesAbsractItems, _dataPoints_1);
            }
          }
        }
      }
    }
    return controlCapabilitiesAbsractItems;
  }
}
