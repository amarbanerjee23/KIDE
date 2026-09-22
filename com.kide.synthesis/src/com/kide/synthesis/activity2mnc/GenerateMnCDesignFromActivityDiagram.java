package com.kide.synthesis.activity2mnc;

import activityDiagramModel.ActivityDiagram;
import com.google.common.collect.Iterables;
import com.kide.synthesis.activity2mnc.ECREGeneratorUtils;
import com.kide.synthesis.activity2mnc.MncProvider;
import dataModelPackage.DataModelFactory;
import dataModelPackage.Parameter;
import dataModelPackage.SimpleType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import mncModel.Action;
import mncModel.ActionAlarm;
import mncModel.ActionCommand;
import mncModel.ActionDataPoint;
import mncModel.ActionEvent;
import mncModel.ActionOperation;
import mncModel.Alarm;
import mncModel.AlarmBlock;
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
import mncModel.SubscribableItemList;
import mncModel.utility.OperatingStateUtility;
import mncModel.utility.UtilityFactory;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class GenerateMnCDesignFromActivityDiagram {
  private BasicEList<Model> mncModels = new BasicEList<Model>();
  
  private BasicEList<AlarmBlock> defaultAlarmBlocks = new BasicEList<AlarmBlock>();
  
  public List<Model> getMnCModels() {
    return this.mncModels;
  }
  
  public GenerateMnCDesignFromActivityDiagram(final ActivityDiagram activityDiagram) {
    if ((activityDiagram != null)) {
      this.parseActivityDiagramToGenerateAnMncModel(activityDiagram);
    }
  }
  
  public GenerateMnCDesignFromActivityDiagram() {
  }
  
  public Model parseActivityDiagramToGenerateAnMncModel(final ActivityDiagram activityDiagram) {
    Model _createModel = MncModelFactory.eINSTANCE.createModel();
    final Procedure1<Model> _function = new Procedure1<Model>() {
      @Override
      public void apply(final Model it) {
        it.setName(activityDiagram.getName());
      }
    };
    Model model = ObjectExtensions.<Model>operator_doubleArrow(_createModel, _function);
    InterfaceDescription _createInterfaceDescription = MncModelFactory.eINSTANCE.createInterfaceDescription();
    final Procedure1<InterfaceDescription> _function_1 = new Procedure1<InterfaceDescription>() {
      @Override
      public void apply(final InterfaceDescription it) {
        it.setName(activityDiagram.getName());
      }
    };
    final InterfaceDescription interfaceDescription = ObjectExtensions.<InterfaceDescription>operator_doubleArrow(_createInterfaceDescription, _function_1);
    ControlNode _createControlNode = MncModelFactory.eINSTANCE.createControlNode();
    final Procedure1<ControlNode> _function_2 = new Procedure1<ControlNode>() {
      @Override
      public void apply(final ControlNode it) {
        it.setName(activityDiagram.getName());
      }
    };
    final ControlNode controlNodeDescription = ObjectExtensions.<ControlNode>operator_doubleArrow(_createControlNode, _function_2);
    final BasicEList<CommandResponseBlock> defaultCommandBlocks = this.getDefaultCommandsForController();
    final BasicEList<ResponseBlock> defaultResponseBlocks = this.getDefaultResponsesForController();
    final OperatingStateUtility defaultOperatingStates = this.getDefaultOperatingStatesForController(activityDiagram.getResults());
    final BasicEList<EventBlock> defaultEventBlocks = this.getDefaultEventsForController();
    this.defaultAlarmBlocks = this.getDefaultAlarmsForController(activityDiagram.getResults());
    MncProvider mncProvider = new MncProvider();
    mncProvider.parseActivityDiagram(activityDiagram);
    HashMap<String, Set> parseInfo = mncProvider.getParseInfo();
    Set _get = parseInfo.get("initActions");
    Set<Action> initExecutableActions = ((Set<Action>) _get);
    Set _get_1 = parseInfo.get("childNodes");
    Set<ControlNode> childNodes = ((Set<ControlNode>) _get_1);
    Set _get_2 = parseInfo.get("uses");
    Set<InterfaceDescription> usedInterfaces = ((Set<InterfaceDescription>) _get_2);
    Set _get_3 = parseInfo.get("eventBlocks");
    Set<EventBlock> eventBlocksToBeImplemented = ((Set<EventBlock>) _get_3);
    Set _get_4 = parseInfo.get("commandResponseBlocks");
    Set<CommandResponseBlock> commandBlocksToBeImplemented = ((Set<CommandResponseBlock>) _get_4);
    Set _get_5 = parseInfo.get("alarmBlocks");
    Set<AlarmBlock> alarmBlocksToBeImplemented = ((Set<AlarmBlock>) _get_5);
    Set _get_6 = parseInfo.get("dataPointBlocks");
    Set<DataPointBlock> dataPointBlocksToBeImplemented = ((Set<DataPointBlock>) _get_6);
    Set _get_7 = parseInfo.get("operatingStates");
    Set<OperatingState> operatingStateFromActivity = ((Set<OperatingState>) _get_7);
    final HashSet<Event> toBeSubscribedEvents = this.toBeSubscribedEvents(initExecutableActions);
    final HashSet<Alarm> toBeSubscribedAlarms = this.toBeSubscribedAlarms(initExecutableActions);
    final HashSet<DataPoint> toBeSubscribedDataPoints = this.toBeSubscribedDataPoints(initExecutableActions);
    final HashSet<Command> toBeFiredCommands = this.toBeFiredCommands(initExecutableActions);
    final HashSet<Response> toBeReceivedResponses = this.getToBeReceivedResponse(initExecutableActions);
    final HashSet<ActionOperation> toBeExecutedOperations = this.toBeExecutedOperations(initExecutableActions);
    SubscribableItemList _createSubscribableItemList = MncModelFactory.eINSTANCE.createSubscribableItemList();
    final Procedure1<SubscribableItemList> _function_3 = new Procedure1<SubscribableItemList>() {
      @Override
      public void apply(final SubscribableItemList it) {
        EList<Alarm> _subscribedAlarms = it.getSubscribedAlarms();
        Iterables.<Alarm>addAll(_subscribedAlarms, toBeSubscribedAlarms);
        EList<Event> _subscribedEvents = it.getSubscribedEvents();
        Iterables.<Event>addAll(_subscribedEvents, toBeSubscribedEvents);
        EList<DataPoint> _subscribedDataPoints = it.getSubscribedDataPoints();
        Iterables.<DataPoint>addAll(_subscribedDataPoints, toBeSubscribedDataPoints);
      }
    };
    SubscribableItemList subscribedItems = ObjectExtensions.<SubscribableItemList>operator_doubleArrow(_createSubscribableItemList, _function_3);
    EList<InterfaceDescription> _uses = interfaceDescription.getUses();
    Iterables.<InterfaceDescription>addAll(_uses, usedInterfaces);
    interfaceDescription.setOperatingStatesUtility(defaultOperatingStates);
    EList<OperatingState> _operatingStates = interfaceDescription.getOperatingStatesUtility().getOperatingStates();
    Iterables.<OperatingState>addAll(_operatingStates, operatingStateFromActivity);
    final Function1<EventBlock, Boolean> _function_4 = new Function1<EventBlock, Boolean>() {
      @Override
      public Boolean apply(final EventBlock eb) {
        EList<Event> _events = interfaceDescription.getEvents();
        Event _event = eb.getEvent();
        return Boolean.valueOf(_events.add(_event));
      }
    };
    IterableExtensions.<EventBlock>forall(defaultEventBlocks, _function_4);
    final Function1<AlarmBlock, Boolean> _function_5 = new Function1<AlarmBlock, Boolean>() {
      @Override
      public Boolean apply(final AlarmBlock ab) {
        EList<Alarm> _alarms = interfaceDescription.getAlarms();
        Alarm _alarm = ab.getAlarm();
        return Boolean.valueOf(_alarms.add(_alarm));
      }
    };
    IterableExtensions.<AlarmBlock>forall(this.defaultAlarmBlocks, _function_5);
    final Function1<CommandResponseBlock, Boolean> _function_6 = new Function1<CommandResponseBlock, Boolean>() {
      @Override
      public Boolean apply(final CommandResponseBlock crb) {
        boolean _xifexpression = false;
        boolean _equalsIgnoreCase = crb.getCommand().getName().equalsIgnoreCase("INIT");
        if (_equalsIgnoreCase) {
          boolean _xblockexpression = false;
          {
            Action _createAction = MncModelFactory.eINSTANCE.createAction();
            final Procedure1<Action> _function = new Procedure1<Action>() {
              @Override
              public void apply(final Action it) {
                for (final Command com : toBeFiredCommands) {
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
                for (final Response resps : toBeReceivedResponses) {
                  EList<ResponseBlock> _responseBlock = crb.getResponseBlock();
                  Iterables.<ResponseBlock>addAll(_responseBlock, defaultResponseBlocks);
                }
                EList<ActionOperation> _executeOperation = it.getExecuteOperation();
                Iterables.<ActionOperation>addAll(_executeOperation, toBeExecutedOperations);
              }
            };
            Action _doubleArrow = ObjectExtensions.<Action>operator_doubleArrow(_createAction, _function);
            crb.setAction(_doubleArrow);
            _xblockexpression = true;
          }
          _xifexpression = _xblockexpression;
        } else {
          _xifexpression = false;
        }
        return Boolean.valueOf(_xifexpression);
      }
    };
    IterableExtensions.<CommandResponseBlock>forall(defaultCommandBlocks, _function_6);
    final Function1<CommandResponseBlock, Boolean> _function_7 = new Function1<CommandResponseBlock, Boolean>() {
      @Override
      public Boolean apply(final CommandResponseBlock crb) {
        EList<Command> _commands = interfaceDescription.getCommands();
        Command _command = crb.getCommand();
        return Boolean.valueOf(_commands.add(_command));
      }
    };
    IterableExtensions.<CommandResponseBlock>forall(IterableExtensions.<CommandResponseBlock>filterNull(defaultCommandBlocks), _function_7);
    Iterable<CommandResponseBlock> _filterNull = IterableExtensions.<CommandResponseBlock>filterNull(defaultCommandBlocks);
    for (final CommandResponseBlock crb : _filterNull) {
      EList<ResponseBlock> _responseBlock = crb.getResponseBlock();
      boolean _tripleNotEquals = (_responseBlock != null);
      if (_tripleNotEquals) {
        EList<ResponseBlock> _responseBlock_1 = crb.getResponseBlock();
        for (final ResponseBlock rspBlk : _responseBlock_1) {
          EList<Response> _responses = interfaceDescription.getResponses();
          Response _response = rspBlk.getResponse();
          _responses.add(_response);
        }
      }
    }
    final Function1<CommandResponseBlock, Boolean> _function_8 = new Function1<CommandResponseBlock, Boolean>() {
      @Override
      public Boolean apply(final CommandResponseBlock crb) {
        EList<Command> _commands = interfaceDescription.getCommands();
        Command _command = crb.getCommand();
        return Boolean.valueOf(_commands.add(_command));
      }
    };
    IterableExtensions.<CommandResponseBlock>forall(IterableExtensions.<CommandResponseBlock>filterNull(commandBlocksToBeImplemented), _function_8);
    Iterable<CommandResponseBlock> _filterNull_1 = IterableExtensions.<CommandResponseBlock>filterNull(commandBlocksToBeImplemented);
    for (final CommandResponseBlock crb_1 : _filterNull_1) {
      EList<ResponseBlock> _responseBlock_2 = crb_1.getResponseBlock();
      boolean _tripleNotEquals_1 = (_responseBlock_2 != null);
      if (_tripleNotEquals_1) {
        EList<ResponseBlock> _responseBlock_3 = crb_1.getResponseBlock();
        for (final ResponseBlock rspBlk_1 : _responseBlock_3) {
          EList<Response> _responses_1 = interfaceDescription.getResponses();
          Response _response_1 = rspBlk_1.getResponse();
          _responses_1.add(_response_1);
        }
      }
    }
    interfaceDescription.setSubscribedItems(subscribedItems);
    EList<mncModel.System> _systems = model.getSystems();
    _systems.add(interfaceDescription);
    controlNodeDescription.setInterfaceDescription(interfaceDescription);
    EList<ControlNode> _childNodes = controlNodeDescription.getChildNodes();
    Iterable<ControlNode> _filterNull_2 = IterableExtensions.<ControlNode>filterNull(childNodes);
    Iterables.<ControlNode>addAll(_childNodes, _filterNull_2);
    if ((defaultCommandBlocks != null)) {
      EList<CommandResponseBlock> _commandResponseBlocks = controlNodeDescription.getCommandResponseBlocks();
      Iterable<CommandResponseBlock> _filterNull_3 = IterableExtensions.<CommandResponseBlock>filterNull(defaultCommandBlocks);
      Iterables.<CommandResponseBlock>addAll(_commandResponseBlocks, _filterNull_3);
    }
    if ((defaultResponseBlocks != null)) {
      EList<CommandResponseBlock> _commandResponseBlocks_1 = controlNodeDescription.getCommandResponseBlocks();
      Iterable<CommandResponseBlock> _filterNull_4 = IterableExtensions.<CommandResponseBlock>filterNull(defaultCommandBlocks);
      Iterables.<CommandResponseBlock>addAll(_commandResponseBlocks_1, _filterNull_4);
    }
    if ((commandBlocksToBeImplemented != null)) {
      EList<CommandResponseBlock> _commandResponseBlocks_2 = controlNodeDescription.getCommandResponseBlocks();
      Iterable<CommandResponseBlock> _filterNull_5 = IterableExtensions.<CommandResponseBlock>filterNull(commandBlocksToBeImplemented);
      Iterables.<CommandResponseBlock>addAll(_commandResponseBlocks_2, _filterNull_5);
    }
    if ((defaultEventBlocks != null)) {
      EList<EventBlock> _eventBlocks = controlNodeDescription.getEventBlocks();
      Iterables.<EventBlock>addAll(_eventBlocks, defaultEventBlocks);
    }
    if ((eventBlocksToBeImplemented != null)) {
      EList<EventBlock> _eventBlocks_1 = controlNodeDescription.getEventBlocks();
      Iterable<EventBlock> _filterNull_6 = IterableExtensions.<EventBlock>filterNull(eventBlocksToBeImplemented);
      Iterables.<EventBlock>addAll(_eventBlocks_1, _filterNull_6);
    }
    if ((dataPointBlocksToBeImplemented != null)) {
      EList<DataPointBlock> _dataPointBlocks = controlNodeDescription.getDataPointBlocks();
      Iterable<DataPointBlock> _filterNull_7 = IterableExtensions.<DataPointBlock>filterNull(dataPointBlocksToBeImplemented);
      Iterables.<DataPointBlock>addAll(_dataPointBlocks, _filterNull_7);
    }
    if ((this.defaultAlarmBlocks != null)) {
      EList<AlarmBlock> _alarmBlocks = controlNodeDescription.getAlarmBlocks();
      Iterables.<AlarmBlock>addAll(_alarmBlocks, this.defaultAlarmBlocks);
    }
    if ((alarmBlocksToBeImplemented != null)) {
      EList<AlarmBlock> _alarmBlocks_1 = controlNodeDescription.getAlarmBlocks();
      Iterable<AlarmBlock> _filterNull_8 = IterableExtensions.<AlarmBlock>filterNull(alarmBlocksToBeImplemented);
      Iterables.<AlarmBlock>addAll(_alarmBlocks_1, _filterNull_8);
    }
    EList<mncModel.System> _systems_1 = model.getSystems();
    _systems_1.add(controlNodeDescription);
    this.mncModels.add(model);
    return model;
  }
  
  public BasicEList<AlarmBlock> getDefaultAlarmsForController(final EList<Parameter> result) {
    BasicEList<AlarmBlock> alarmBlocks = new BasicEList<AlarmBlock>();
    AlarmBlock _createAlarmBlock = MncModelFactory.eINSTANCE.createAlarmBlock();
    final Procedure1<AlarmBlock> _function = new Procedure1<AlarmBlock>() {
      @Override
      public void apply(final AlarmBlock it) {
        Alarm _createAlarm = MncModelFactory.eINSTANCE.createAlarm();
        final Procedure1<Alarm> _function = new Procedure1<Alarm>() {
          @Override
          public void apply(final Alarm it) {
            it.setName("Aborted");
            for (final Parameter par : result) {
              if ((par instanceof SimpleType)) {
                EList<Parameter> _parameters = it.getParameters();
                SimpleType _createSimpleType = DataModelFactory.eINSTANCE.createSimpleType();
                final Procedure1<SimpleType> _function = new Procedure1<SimpleType>() {
                  @Override
                  public void apply(final SimpleType it) {
                    it.setName(((SimpleType)par).getName());
                    it.setType(((SimpleType)par).getType());
                  }
                };
                SimpleType _doubleArrow = ObjectExtensions.<SimpleType>operator_doubleArrow(_createSimpleType, _function);
                _parameters.add(_doubleArrow);
              }
            }
          }
        };
        Alarm _doubleArrow = ObjectExtensions.<Alarm>operator_doubleArrow(_createAlarm, _function);
        it.setAlarm(_doubleArrow);
      }
    };
    AlarmBlock _doubleArrow = ObjectExtensions.<AlarmBlock>operator_doubleArrow(_createAlarmBlock, _function);
    alarmBlocks.add(_doubleArrow);
    return alarmBlocks;
  }
  
  public HashSet<ActionOperation> toBeExecutedOperations(final Set<Action> list) {
    HashSet<ActionOperation> tobeExecutedOps = new LinkedHashSet<ActionOperation>();
    for (final Action action : list) {
      EList<ActionOperation> _executeOperation = action.getExecuteOperation();
      boolean _tripleNotEquals = (_executeOperation != null);
      if (_tripleNotEquals) {
        EList<ActionOperation> _executeOperation_1 = action.getExecuteOperation();
        Iterables.<ActionOperation>addAll(tobeExecutedOps, _executeOperation_1);
      }
    }
    return tobeExecutedOps;
  }
  
  public HashSet<Command> toBeFiredCommands(final Set<Action> list) {
    HashSet<Command> tobeFiredCommands = new LinkedHashSet<Command>();
    for (final Action action : list) {
      EList<ActionCommand> _fireCommand = action.getFireCommand();
      boolean _tripleNotEquals = (_fireCommand != null);
      if (_tripleNotEquals) {
        EList<ActionCommand> _fireCommand_1 = action.getFireCommand();
        for (final ActionCommand coms : _fireCommand_1) {
          Command _command = coms.getCommand();
          boolean _tripleNotEquals_1 = (_command != null);
          if (_tripleNotEquals_1) {
            Command _command_1 = coms.getCommand();
            tobeFiredCommands.add(_command_1);
          }
        }
      }
    }
    return tobeFiredCommands;
  }
  
  public HashSet<Response> getToBeReceivedResponse(final Set<Action> actions) {
    HashSet<Response> tobeReceivedResponses = new LinkedHashSet<Response>();
    for (final Action action : actions) {
      EList<ActionCommand> _fireCommand = action.getFireCommand();
      boolean _tripleNotEquals = (_fireCommand != null);
      if (_tripleNotEquals) {
        EList<ActionCommand> _fireCommand_1 = action.getFireCommand();
        for (final ActionCommand coms : _fireCommand_1) {
          if (((coms.getCommand() != null) && (coms.getResponseHandling() != null))) {
            EList<ResponseBlock> _responseHandling = coms.getResponseHandling();
            for (final ResponseBlock resp : _responseHandling) {
              Response _response = resp.getResponse();
              tobeReceivedResponses.add(_response);
            }
          }
        }
      }
    }
    return tobeReceivedResponses;
  }
  
  public HashSet<DataPoint> toBeSubscribedDataPoints(final Set<Action> list) {
    HashSet<DataPoint> subscribedDataPoints = new LinkedHashSet<DataPoint>();
    for (final Action action : list) {
      EList<ActionDataPoint> _triggerDataPoint = action.getTriggerDataPoint();
      boolean _tripleNotEquals = (_triggerDataPoint != null);
      if (_tripleNotEquals) {
        EList<ActionDataPoint> _triggerDataPoint_1 = action.getTriggerDataPoint();
        for (final ActionDataPoint dp : _triggerDataPoint_1) {
          DataPoint _dataPoint = dp.getDataPoint();
          subscribedDataPoints.add(_dataPoint);
        }
      }
    }
    return subscribedDataPoints;
  }
  
  public HashSet<Alarm> toBeSubscribedAlarms(final Set<Action> list) {
    HashSet<Alarm> subscribedAlarms = new LinkedHashSet<Alarm>();
    for (final Action action : list) {
      EList<ActionAlarm> _raiseAlarm = action.getRaiseAlarm();
      boolean _tripleNotEquals = (_raiseAlarm != null);
      if (_tripleNotEquals) {
        EList<ActionAlarm> _raiseAlarm_1 = action.getRaiseAlarm();
        for (final ActionAlarm alarm : _raiseAlarm_1) {
          Alarm _alarm = alarm.getAlarm();
          subscribedAlarms.add(_alarm);
        }
      }
    }
    return subscribedAlarms;
  }
  
  public HashSet<Event> toBeSubscribedEvents(final Set<Action> list) {
    HashSet<Event> subscribedEvents = new LinkedHashSet<Event>();
    for (final Action action : list) {
      EList<ActionEvent> _publishEvent = action.getPublishEvent();
      boolean _tripleNotEquals = (_publishEvent != null);
      if (_tripleNotEquals) {
        EList<ActionEvent> _publishEvent_1 = action.getPublishEvent();
        for (final ActionEvent event : _publishEvent_1) {
          Event _event = event.getEvent();
          subscribedEvents.add(_event);
        }
      }
    }
    return subscribedEvents;
  }
  
  public BasicEList<EventBlock> getDefaultEventsForController() {
    Event _createEvent = MncModelFactory.eINSTANCE.createEvent();
    final Procedure1<Event> _function = new Procedure1<Event>() {
      @Override
      public void apply(final Event it) {
        it.setName("Started");
      }
    };
    final Event startEvent = ObjectExtensions.<Event>operator_doubleArrow(_createEvent, _function);
    Event _createEvent_1 = MncModelFactory.eINSTANCE.createEvent();
    final Procedure1<Event> _function_1 = new Procedure1<Event>() {
      @Override
      public void apply(final Event it) {
        it.setName("Ready");
      }
    };
    final Event readyEvent = ObjectExtensions.<Event>operator_doubleArrow(_createEvent_1, _function_1);
    Event _createEvent_2 = MncModelFactory.eINSTANCE.createEvent();
    final Procedure1<Event> _function_2 = new Procedure1<Event>() {
      @Override
      public void apply(final Event it) {
        it.setName("Stopped");
      }
    };
    final Event stopEvent = ObjectExtensions.<Event>operator_doubleArrow(_createEvent_2, _function_2);
    BasicEList<EventBlock> eventBlock = new BasicEList<EventBlock>();
    EventBlock _createEventBlock = MncModelFactory.eINSTANCE.createEventBlock();
    final Procedure1<EventBlock> _function_3 = new Procedure1<EventBlock>() {
      @Override
      public void apply(final EventBlock it) {
        it.setEvent(readyEvent);
      }
    };
    EventBlock _doubleArrow = ObjectExtensions.<EventBlock>operator_doubleArrow(_createEventBlock, _function_3);
    eventBlock.add(_doubleArrow);
    EventBlock _createEventBlock_1 = MncModelFactory.eINSTANCE.createEventBlock();
    final Procedure1<EventBlock> _function_4 = new Procedure1<EventBlock>() {
      @Override
      public void apply(final EventBlock it) {
        it.setEvent(startEvent);
      }
    };
    EventBlock _doubleArrow_1 = ObjectExtensions.<EventBlock>operator_doubleArrow(_createEventBlock_1, _function_4);
    eventBlock.add(_doubleArrow_1);
    EventBlock _createEventBlock_2 = MncModelFactory.eINSTANCE.createEventBlock();
    final Procedure1<EventBlock> _function_5 = new Procedure1<EventBlock>() {
      @Override
      public void apply(final EventBlock it) {
        it.setEvent(stopEvent);
      }
    };
    EventBlock _doubleArrow_2 = ObjectExtensions.<EventBlock>operator_doubleArrow(_createEventBlock_2, _function_5);
    eventBlock.add(_doubleArrow_2);
    return eventBlock;
  }
  
  public BasicEList<CommandResponseBlock> getDefaultCommandsForController() {
    BasicEList<CommandResponseBlock> commandBlock = new BasicEList<CommandResponseBlock>();
    Command _createCommand = MncModelFactory.eINSTANCE.createCommand();
    final Procedure1<Command> _function = new Procedure1<Command>() {
      @Override
      public void apply(final Command it) {
        it.setName("INIT");
      }
    };
    final Command initCommand = ObjectExtensions.<Command>operator_doubleArrow(_createCommand, _function);
    CommandResponseBlock _createCommandResponseBlock = MncModelFactory.eINSTANCE.createCommandResponseBlock();
    final Procedure1<CommandResponseBlock> _function_1 = new Procedure1<CommandResponseBlock>() {
      @Override
      public void apply(final CommandResponseBlock it) {
        it.setCommand(initCommand);
      }
    };
    CommandResponseBlock initCommandBlock = ObjectExtensions.<CommandResponseBlock>operator_doubleArrow(_createCommandResponseBlock, _function_1);
    commandBlock.add(initCommandBlock);
    return commandBlock;
  }
  
  public BasicEList<ResponseBlock> getDefaultResponsesForController() {
    BasicEList<ResponseBlock> responseBlock = new BasicEList<ResponseBlock>();
    Response _createResponse = MncModelFactory.eINSTANCE.createResponse();
    final Procedure1<Response> _function = new Procedure1<Response>() {
      @Override
      public void apply(final Response it) {
        it.setName("INIT_RES");
      }
    };
    final Response initResponse = ObjectExtensions.<Response>operator_doubleArrow(_createResponse, _function);
    ResponseBlock _createResponseBlock = MncModelFactory.eINSTANCE.createResponseBlock();
    final Procedure1<ResponseBlock> _function_1 = new Procedure1<ResponseBlock>() {
      @Override
      public void apply(final ResponseBlock it) {
        it.setResponse(initResponse);
      }
    };
    ResponseBlock initResponseBlock = ObjectExtensions.<ResponseBlock>operator_doubleArrow(_createResponseBlock, _function_1);
    responseBlock.add(initResponseBlock);
    return responseBlock;
  }
  
  public OperatingStateUtility getDefaultOperatingStatesForController(final EList<Parameter> results) {
    OperatingStateUtility _createOperatingStateUtility = UtilityFactory.eINSTANCE.createOperatingStateUtility();
    final Procedure1<OperatingStateUtility> _function = new Procedure1<OperatingStateUtility>() {
      @Override
      public void apply(final OperatingStateUtility it) {
      }
    };
    OperatingStateUtility stateUtility = ObjectExtensions.<OperatingStateUtility>operator_doubleArrow(_createOperatingStateUtility, _function);
    OperatingState _createOperatingState = MncModelFactory.eINSTANCE.createOperatingState();
    final Procedure1<OperatingState> _function_1 = new Procedure1<OperatingState>() {
      @Override
      public void apply(final OperatingState it) {
        it.setName("INITIALIZED");
      }
    };
    OperatingState initState = ObjectExtensions.<OperatingState>operator_doubleArrow(_createOperatingState, _function_1);
    OperatingState _createOperatingState_1 = MncModelFactory.eINSTANCE.createOperatingState();
    final Procedure1<OperatingState> _function_2 = new Procedure1<OperatingState>() {
      @Override
      public void apply(final OperatingState it) {
        it.setName("READY");
      }
    };
    OperatingState readyState = ObjectExtensions.<OperatingState>operator_doubleArrow(_createOperatingState_1, _function_2);
    stateUtility.getOperatingStates().add(initState);
    stateUtility.getOperatingStates().add(readyState);
    for (final Parameter par : results) {
      {
        OperatingState _createOperatingState_2 = MncModelFactory.eINSTANCE.createOperatingState();
        final Procedure1<OperatingState> _function_3 = new Procedure1<OperatingState>() {
          @Override
          public void apply(final OperatingState it) {
            it.setName(ECREGeneratorUtils.getParameterName(par));
          }
        };
        OperatingState endState = ObjectExtensions.<OperatingState>operator_doubleArrow(_createOperatingState_2, _function_3);
        stateUtility.getOperatingStates().add(endState);
        EList<OperatingState> _endState = stateUtility.getEndState();
        _endState.add(endState);
      }
    }
    return stateUtility;
  }
  
  public ControlNode getControlNode(final ActivityDiagram diagram, final InterfaceDescription description) {
    ControlNode _createControlNode = MncModelFactory.eINSTANCE.createControlNode();
    final Procedure1<ControlNode> _function = new Procedure1<ControlNode>() {
      @Override
      public void apply(final ControlNode it) {
        it.setName(diagram.getName());
        it.setInterfaceDescription(description);
      }
    };
    ControlNode cn = ObjectExtensions.<ControlNode>operator_doubleArrow(_createControlNode, _function);
    return cn;
  }
  
  public InterfaceDescription getInterfaceDescription(final ActivityDiagram diagram) {
    final BasicEList<Command> commandList = new BasicEList<Command>();
    InterfaceDescription _createInterfaceDescription = MncModelFactory.eINSTANCE.createInterfaceDescription();
    final Procedure1<InterfaceDescription> _function = new Procedure1<InterfaceDescription>() {
      @Override
      public void apply(final InterfaceDescription it) {
        it.setName(diagram.getName());
        it.getCommands().addAll(commandList);
      }
    };
    InterfaceDescription id = ObjectExtensions.<InterfaceDescription>operator_doubleArrow(_createInterfaceDescription, _function);
    return id;
  }
}
