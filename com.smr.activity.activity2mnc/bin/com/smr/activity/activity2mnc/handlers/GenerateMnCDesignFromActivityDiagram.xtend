package com.smr.activity.activity2mnc.handlers

import activityDiagramModel.ActivityDiagram
import com.smr.activity.activity2mnc.methods.MncProvider
import dataModelPackage.DataModelFactory
import dataModelPackage.Parameter
import dataModelPackage.SimpleType
import java.util.HashSet
import java.util.List
import java.util.Set
import mncModel.Action
import mncModel.ActionOperation
import mncModel.Alarm
import mncModel.AlarmBlock
import mncModel.Command
import mncModel.CommandResponseBlock
import mncModel.ControlNode
import mncModel.DataPoint
import mncModel.DataPointBlock
import mncModel.Event
import mncModel.EventBlock
import mncModel.InterfaceDescription
import mncModel.MncModelFactory
import mncModel.Model
import mncModel.OperatingState
import mncModel.Response
import mncModel.ResponseBlock
import mncModel.utility.UtilityFactory
import org.eclipse.emf.common.util.BasicEList
import org.eclipse.emf.common.util.EList

class GenerateMnCDesignFromActivityDiagram {

	var mncModels = new BasicEList<Model>()
	public var static defaultAlarmBlocks = new BasicEList<AlarmBlock>

	def List<Model> getMnCModels() {
		return mncModels
	}

	new(ActivityDiagram activityDiagram) {
//		this.resourceSet = resourceSet
		if (activityDiagram !== null) {
			parseActivityDiagramToGenerateAnMncModel(activityDiagram)
		}
	}

	new() {
	}

	def Model parseActivityDiagramToGenerateAnMncModel(ActivityDiagram activityDiagram) {

		// Generating Supervisory Model
		var model = MncModelFactory.eINSTANCE.createModel => [name = activityDiagram.name]

		// Generating Supervisory Interface
		val interfaceDescription = MncModelFactory.eINSTANCE.createInterfaceDescription => [
			name = activityDiagram.name
		]
		// Generating Supervisory Control Node
		val controlNodeDescription = MncModelFactory.eINSTANCE.createControlNode => [name = activityDiagram.name]

		// Generating Default
		val defaultCommandBlocks = defaultCommandsForController
		val defaultResponseBlocks = defaultResponsesForController
		val defaultOperatingStates = getDefaultOperatingStatesForController(activityDiagram.results)
		val defaultEventBlocks = defaultEventsForController

		// Adding Activity Outcomes as Alarms
		defaultAlarmBlocks = activityDiagram.results.defaultAlarmsForController

// Parse the activity diagram
		var mncProvider = new MncProvider
		mncProvider.parseActivityDiagram(activityDiagram)
		var parseInfo = mncProvider.parseInfo
// Extract all necessary details for the Mnc Model
		var initExecutableActions = parseInfo.get('initActions') as Set<Action>
		var childNodes = parseInfo.get('childNodes') as Set<ControlNode>
		var usedInterfaces = parseInfo.get('uses') as Set<InterfaceDescription>
		var eventBlocksToBeImplemented = parseInfo.get('eventBlocks') as Set<EventBlock>
		var commandBlocksToBeImplemented = parseInfo.get('commandResponseBlocks') as Set<CommandResponseBlock>
		var alarmBlocksToBeImplemented = parseInfo.get('alarmBlocks') as Set<AlarmBlock>
		var dataPointBlocksToBeImplemented = parseInfo.get('dataPointBlocks') as Set<DataPointBlock>
		var operatingStateFromActivity = parseInfo.get('operatingStates') as Set<OperatingState>

// Set subscribed items
		val toBeSubscribedEvents = initExecutableActions.toBeSubscribedEvents
		val toBeSubscribedAlarms = initExecutableActions.toBeSubscribedAlarms
		val toBeSubscribedDataPoints = initExecutableActions.toBeSubscribedDataPoints
		val toBeFiredCommands = initExecutableActions.toBeFiredCommands
		val toBeReceivedResponses = initExecutableActions.toBeReceivedResponse
		val toBeExecutedOperations = initExecutableActions.toBeExecutedOperations
// Set the rquired information
		var subscribedItems = MncModelFactory.eINSTANCE.createSubscribableItemList => [
			subscribedAlarms += toBeSubscribedAlarms
			subscribedEvents += toBeSubscribedEvents
			subscribedDataPoints += toBeSubscribedDataPoints
		]

		// Add interfaces
		interfaceDescription.uses += usedInterfaces
		// Add states
		interfaceDescription.operatingStatesUtility = defaultOperatingStates
		interfaceDescription.operatingStatesUtility.operatingStates += operatingStateFromActivity

		// Add events
		defaultEventBlocks.forall[eb|interfaceDescription.events += eb.event]

		// Add alarms
		defaultAlarmBlocks.forall[ab|interfaceDescription.alarms += ab.alarm]
		// Add init commands and operations to INIT command
		defaultCommandBlocks.forall [ crb |
			if (crb.command.name.equalsIgnoreCase('INIT')) {
				crb.action = MncModelFactory.eINSTANCE.createAction => [
					for (com : toBeFiredCommands) {
						fireCommand += MncModelFactory.eINSTANCE.createActionCommand => [
							command = com
						]
					}
					for (resps : toBeReceivedResponses) {
						crb.responseBlock += defaultResponseBlocks
					}
					executeOperation += toBeExecutedOperations
				]
				true
			} else {
				false
			}
		]
		
		// Add commands
		defaultCommandBlocks.filterNull.forall[crb|interfaceDescription.commands += crb.command]
		// Add response
		for (crb : defaultCommandBlocks.filterNull) {
			if (crb.responseBlock !== null) {
				for (rspBlk : crb.responseBlock) {
					interfaceDescription.responses += rspBlk.response
				}
			}

		}
		
		// Add commands
		commandBlocksToBeImplemented.filterNull.forall[crb|interfaceDescription.commands += crb.command]
		// Add response
		for (crb : commandBlocksToBeImplemented.filterNull) {
			if (crb.responseBlock !== null) {
				for (rspBlk : crb.responseBlock) {
					interfaceDescription.responses += rspBlk.response
				}
			}

		}
		

// Add subscribed Items
		interfaceDescription.subscribedItems = subscribedItems
// Add ID to model
		model.systems += interfaceDescription
// Add ID To CN
		controlNodeDescription.interfaceDescription = interfaceDescription
// Add child nodes to CN
		controlNodeDescription.childNodes += childNodes.filterNull
// Add CommandResponse Blocks to CN
		if (defaultCommandBlocks !== null)
			controlNodeDescription.commandResponseBlocks += defaultCommandBlocks.filterNull
		if (defaultResponseBlocks !== null)
			controlNodeDescription.commandResponseBlocks += defaultCommandBlocks.filterNull
		if (commandBlocksToBeImplemented !== null)
			controlNodeDescription.commandResponseBlocks += commandBlocksToBeImplemented.filterNull
// Add EventBlocks to CN
		if (defaultEventBlocks !== null)
			controlNodeDescription.eventBlocks += defaultEventBlocks
		if (eventBlocksToBeImplemented !== null)
			controlNodeDescription.eventBlocks += eventBlocksToBeImplemented.filterNull
//Add DataPoint Blocks to CN
		if (dataPointBlocksToBeImplemented !== null)
			controlNodeDescription.dataPointBlocks += dataPointBlocksToBeImplemented.filterNull
// Add Alarm Blocks to CN	
		if (defaultAlarmBlocks !== null)
			controlNodeDescription.alarmBlocks += defaultAlarmBlocks
		if (alarmBlocksToBeImplemented !== null)
			controlNodeDescription.alarmBlocks += alarmBlocksToBeImplemented.filterNull
// Add CN To Model
		model.systems += controlNodeDescription
		mncModels += model
		return model

	}

	def getDefaultAlarmsForController(EList<Parameter> result) {
		var alarmBlocks = new BasicEList<AlarmBlock>
		alarmBlocks.add(MncModelFactory.eINSTANCE.createAlarmBlock => [
			alarm = MncModelFactory.eINSTANCE.createAlarm => [
				name = 'Aborted'
				for (par : result) {
					if (par instanceof SimpleType) {
						parameters += DataModelFactory.eINSTANCE.createSimpleType => [
							name = par.name
							type = par.type
						]
					}

				}
			]
		])
		return alarmBlocks
	}

	def toBeExecutedOperations(Set<Action> list) {
		var tobeExecutedOps = new HashSet<ActionOperation>
		for (action : list) {
			if (action.executeOperation !== null) {
				tobeExecutedOps += action.executeOperation
			}
		}
		return tobeExecutedOps
	}

	def toBeFiredCommands(Set<Action> list) {
		var tobeFiredCommands = new HashSet<Command>
		for (action : list) {
			if (action.fireCommand !== null) {
				for (coms : action.fireCommand) {
					if (coms.command !== null) {
						tobeFiredCommands += coms.command
					}

				}
			}
		}
		return tobeFiredCommands
	}

	def getToBeReceivedResponse(Set<Action> actions) {
		var tobeReceivedResponses = new HashSet<Response>
		for (action : actions) {
			if (action.fireCommand !== null) {
				for (coms : action.fireCommand) {
					if (coms.command !== null && coms.responseHandling !== null) {
						for (resp : coms.responseHandling) {
							tobeReceivedResponses += resp.response
						}
					}
				}
			}
		}
		return tobeReceivedResponses
	}

	def toBeSubscribedDataPoints(Set<Action> list) {
		var subscribedDataPoints = new HashSet<DataPoint>
		for (action : list) {
			if (action.triggerDataPoint !== null) {
				for (dp : action.triggerDataPoint) {
					subscribedDataPoints += dp.dataPoint
				}

			}
		}
		return subscribedDataPoints
	}

	def toBeSubscribedAlarms(Set<Action> list) {
		var subscribedAlarms = new HashSet<Alarm>
		for (action : list) {
			if (action.raiseAlarm !== null) {
				for (alarm : action.raiseAlarm) {
					subscribedAlarms += alarm.alarm
				}

			}
		}
		return subscribedAlarms
	}

	def toBeSubscribedEvents(Set<Action> list) {
		var subscribedEvents = new HashSet<Event>
		for (action : list) {
			if (action.publishEvent !== null) {
				for (event : action.publishEvent) {
					subscribedEvents += event.event
				}
			}
		}
		return subscribedEvents
	}

	def getDefaultEventsForController() {
		val startEvent = MncModelFactory.eINSTANCE.createEvent => [
			name = 'Started'
		]
		val readyEvent = MncModelFactory.eINSTANCE.createEvent => [
			name = 'Ready'
		]
		val stopEvent = MncModelFactory.eINSTANCE.createEvent => [
			name = 'Stopped'
		]
		var eventBlock = new BasicEList<EventBlock>()
		eventBlock.add(MncModelFactory.eINSTANCE.createEventBlock => [
			event = readyEvent
		])
		eventBlock.add(MncModelFactory.eINSTANCE.createEventBlock => [
			event = startEvent
		])
		eventBlock.add(MncModelFactory.eINSTANCE.createEventBlock => [
			event = stopEvent
		])
		return eventBlock
	}

	def getDefaultCommandsForController() {
		var commandBlock = new BasicEList<CommandResponseBlock>
		val initCommand = MncModelFactory.eINSTANCE.createCommand => [
			name = 'INIT'

		]
		var initCommandBlock = MncModelFactory.eINSTANCE.createCommandResponseBlock => [
			command = initCommand
		]
		commandBlock.add(initCommandBlock)
		return commandBlock
	}

	def getDefaultResponsesForController() {
		var responseBlock = new BasicEList<ResponseBlock>
		val initResponse = MncModelFactory.eINSTANCE.createResponse => [
			name = 'INIT_RES'

		]
		var initResponseBlock = MncModelFactory.eINSTANCE.createResponseBlock => [
			response = initResponse
		]
		responseBlock.add(initResponseBlock)
		return responseBlock
	}

	def getDefaultOperatingStatesForController(EList<Parameter> results) {
		var stateUtility = UtilityFactory.eINSTANCE.createOperatingStateUtility => []

		var initState = MncModelFactory.eINSTANCE.createOperatingState => [
			name = 'INITIALIZED'
		]
		var readyState = MncModelFactory.eINSTANCE.createOperatingState => [
			name = 'READY'
		]

		stateUtility.operatingStates.add(initState)
		stateUtility.operatingStates.add(readyState)

		for (par : results) {
			var endState = MncModelFactory.eINSTANCE.createOperatingState => [
				name = ECREGeneratorUtils.getParameterName(par)
			]
			stateUtility.operatingStates.add(endState)
			stateUtility.endState += endState
		}
//		stateUtility.startState += readyState
		return stateUtility
	}

	def getControlNode(ActivityDiagram diagram, InterfaceDescription description) {
		var cn = MncModelFactory.eINSTANCE.createControlNode => [
			name = diagram.name
			interfaceDescription = description
		]
		return cn
	}

	def getInterfaceDescription(ActivityDiagram diagram) {

		val commandList = new BasicEList<Command>()

		var id = MncModelFactory.eINSTANCE.createInterfaceDescription => [
			name = diagram.name
			commands.addAll(commandList)
		]
		return id
	}

}
