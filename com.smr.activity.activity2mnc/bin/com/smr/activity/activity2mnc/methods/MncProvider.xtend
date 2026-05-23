package com.smr.activity.activity2mnc.methods

import activityDiagramModel.Activity
import activityDiagramModel.ActivityDiagram
import activityDiagramModel.ConditionalActivity
import com.smr.activity.activity2mnc.handlers.GenerateMnCDesignFromActivityDiagram
import dataModelPackage.AbstractType
import dataModelPackage.ArrayType
import dataModelPackage.DataModelFactory
import dataModelPackage.Parameter
import dataModelPackage.SimpleType
import java.util.HashMap
import java.util.HashSet
import java.util.Set
import mncModel.AbstractInterfaceItems
import mncModel.Action
import mncModel.Alarm
import mncModel.AlarmBlock
import mncModel.CheckParameterCondition
import mncModel.Command
import mncModel.CommandResponseBlock
import mncModel.ControlNode
import mncModel.DataPoint
import mncModel.DataPointBlock
import mncModel.Event
import mncModel.EventBlock
import mncModel.InterfaceDescription
import mncModel.MncModelFactory
import mncModel.OperatingState
import mncModel.Response
import mncModel.Transition
import mncModel.Validation
import operationsDescription.Operation
import org.eclipse.emf.common.util.BasicEList
import org.eclipse.emf.common.util.EList
import org.eclipse.xtext.EcoreUtil2
import mncModel.ActionCommand

class MncProvider {

	val usedInterfaces = new HashSet<InterfaceDescription>
	val initExecutableActions = new HashSet<Action>
	val childNodes = new HashSet<ControlNode>
	val operatingState = new HashSet<OperatingState>
	val commandResponseBlocks = new HashSet<CommandResponseBlock>
	val eventBlocks = new HashSet<EventBlock>
	val dataPointBlocks = new HashSet<DataPointBlock>
	val alarmBlocks = new HashSet<AlarmBlock>

	def getParseInfo() {
		var parseInfor = new HashMap<String, Set>
		parseInfor.put('childNodes', childNodes)
		parseInfor.put('uses', usedInterfaces)
		parseInfor.put('initActions', initExecutableActions)
		parseInfor.put('eventBlocks', eventBlocks)
		parseInfor.put('alarmBlocks', alarmBlocks)
		parseInfor.put('operatingStates', operatingState)
		parseInfor.put('commandResponseBlocks', commandResponseBlocks)
		return parseInfor
	}

	def parseActivityDiagram(ActivityDiagram activityDiagram) {

		if (activityDiagram.activities !== null && activityDiagram.activities.size > 0) {
			// Iterate through all activities in activity diagram
			for (activity : activityDiagram.activities) {

				if (activity.bindCapability !== null) {
					// When capability is firing a command
					var capabilityItems = activity.capabilitiesAbstractInterfaceItems
					for (item : capabilityItems) {
						var actionItemExists = item.checkIfActionContains
						if (item instanceof Command) {
							println('###' + item.name)
							item.getCapabilityCommandResponseBlock(activity)
						}

// When capability is getting an event
						if (item instanceof Event) {
							if (!actionItemExists) {
								initExecutableActions.head.publishEvent.add(
									MncModelFactory.eINSTANCE.createActionEvent => [event = item as Event]
								)
							}
							if(activity !== null) item.getCapabilityEventBlock(activity)
						}
// When capability is getting an alarm
						if (item instanceof Alarm) {
							if (!actionItemExists) {
								initExecutableActions.head.raiseAlarm.add(
									MncModelFactory.eINSTANCE.createActionAlarm => [alarm = item as Alarm]
								)
							}
							if(activity !== null) item.getCapabilityAlarmBlock(activity)

						}
// When capability is getting a datapoint
						if (item instanceof DataPoint) {
							if (!actionItemExists) {
								initExecutableActions.head.triggerDataPoint.add(
									MncModelFactory.eINSTANCE.createActionDataPoint => [
										dataPoint = item as DataPoint
									])

							}
						}
					}
				}
				if (activity.requiresOperation !== null) {
				}
				if (activity.childActivityDiagram !== null) {
// If activity has associated child activity diagram, then again generate mnc for child activity diagram
					var childModel = new GenerateMnCDesignFromActivityDiagram().
						parseActivityDiagramToGenerateAnMncModel(activity.childActivityDiagram)
					if (childModel !== null) {
						var childController = childModel.systems.get(1) as ControlNode
						childNodes.add(childController)
					}
				}

			}
		}

	}

	def getCapabilityCommandResponseBlock(Command com, Activity activity) {
		println('***' + com.name)
		var commandResponseBlock = commandResponseBlocks.filterNull.filter[it.command.name.equals(com.name)].head
		if (commandResponseBlock === null) {
			commandResponseBlock = MncModelFactory.eINSTANCE.createCommandResponseBlock => [
				command = MncModelFactory.eINSTANCE.createCommand => [
					name = com.name
					if (com.parameters !== null) {
						for (par : com.parameters) {
							if (par instanceof SimpleType) {
								parameters += DataModelFactory.eINSTANCE.createSimpleType => [
									name = par.name
									type = par.type
									value = par.value
								]
							}

						}
					}
				]

			]
			// Add actions to the command respose block
			commandResponseBlock.action = MncModelFactory.eINSTANCE.createAction => [
// Add capability command to the fired command in the action, so that
// When the Wrapper command is invoked it can invoke the device capability command
				fireCommand += MncModelFactory.eINSTANCE.createActionCommand => [
					command = com
				]
			]
		} else {
			commandResponseBlocks -= commandResponseBlock
		}

		// Add conditions on the fire command (check response and take some actions)
		// If CRB exists then add behaviour to the CRB
		if (activity.conditionalActivity !== null) {
			for (cond : activity.conditionalActivity) {
				if (cond.outcome !== null) {
					for (outcome : cond.outcome) {
						val item = outcome.capabilityOutcome
						if (item instanceof Response) {
							val validation = getValidationForCondition(cond)
							if (commandResponseBlock.action !== null &&
								commandResponseBlock.action.fireCommand !== null) {
								var fireCommand = commandResponseBlock.action.fireCommand.filterNull.filter [
									it.command.equals(com)
								].head
								if (fireCommand !== null) {
									commandResponseBlock.action.fireCommand -= fireCommand
									fireCommand.responseHandling += MncModelFactory.eINSTANCE.createResponseBlock => [
										response = outcome.capabilityOutcome as Response
										if (validation !== null) {
											validationRules += validation
										}
									]
									commandResponseBlock.action.fireCommand += fireCommand
								}

								
							}

						} else {
							cond.getBlocksForConditionOutcomes
						}
					} 
				}

			}

		}
		commandResponseBlocks += commandResponseBlock
	}

	def getBlocksForConditionOutcomes(ConditionalActivity cond) {
		if (cond.outcome !== null) {
			val validation = getValidationForCondition(cond)
			for (outcome : cond.outcome) {
				val item = outcome.capabilityOutcome

				if (item instanceof Event) {
					var evblock = eventBlocks.filterNull.filter[it.event.equals(item)].head
					if (evblock === null) {
						evblock = MncModelFactory.eINSTANCE.createEventBlock => [event = item]
					} else {
						eventBlocks -= evblock
					}
					evblock.validationRules += validation
					eventBlocks += evblock
				}

				if (item instanceof Alarm) {
					var alblock = alarmBlocks.filterNull.filter[it.alarm.equals(item)].head
					if (alblock === null) {
						alblock = MncModelFactory.eINSTANCE.createAlarmBlock => [alarm = item]
					} else {
						alarmBlocks -= alblock
					}
					if (validation.onSuccessAction !== null) {
						validation.onSuccessAction.raiseAlarm += MncModelFactory.eINSTANCE.createActionAlarm => [
							alarm = MncModelFactory.eINSTANCE.createAlarm => [
								name = 'Aborted'
							]
						]
					}

					alblock.validationRules += validation
					alarmBlocks += alblock
				}
			}

		}

	}

	def getCapabilityEventBlock(
		Event evt,
		Activity activity
	) {
		var block = null as EventBlock
		var eventsBlockList = eventBlocks.filterNull.filter[it.event.equals(evt)]
		if (eventsBlockList.size === 0) {
			block = MncModelFactory.eINSTANCE.createEventBlock => [event = evt]
		} else {
			block = eventsBlockList.head
			eventBlocks -= block
		}
		if (activity !== null && activity.conditionalActivity !== null) {
			if (activity.conditionalActivity !== null) {
				for (cond : activity.conditionalActivity) {
					cond.getBlocksForConditionOutcomes
				}
			}
		}
	}

	def getCapabilityAlarmBlock(
		Alarm item,
		Activity activity
	) {
		var block = null as AlarmBlock
		var alarmsBlockList = alarmBlocks.filterNull.filter[it.alarm.equals(item)]
		if (alarmsBlockList.size === 0) {
			block = MncModelFactory.eINSTANCE.createAlarmBlock => [alarm = item]
		} else {
			block = alarmsBlockList.head
			alarmBlocks -= block
		}

		if (activity.conditionalActivity !== null) {
			for (cond : activity.conditionalActivity) {
				cond.getBlocksForConditionOutcomes

			}
		}
	}

	def Transition getTransitionsFromConditionalActivity(ConditionalActivity condition) {

		val currST = EcoreUtil2.getContainerOfType(condition, Activity).name.getStateExistsFromName
		var nxtST = null as OperatingState
		if (condition.onTrueNextActivity !== null) {
			nxtST = condition.onTrueNextActivity.name.stateExistsFromName
		}
		if (condition.onTrueFinalResult !== null) {
			nxtST = condition.onTrueFinalResult.parameterName.stateExistsFromName
		}
		var transition = MncModelFactory.eINSTANCE.createTransition => []
		transition.currentState += currST
		transition.nextState = nxtST
		return transition

	}

	def parameterName(Parameter par) {
		if (par instanceof SimpleType)
			return par.name
		if (par instanceof ArrayType)
			return par.name
		if (par instanceof AbstractType)
			return par.name
	}

	def Validation getValidationForCondition(ConditionalActivity conditionalActivity) {
		var validation = MncModelFactory.eINSTANCE.createValidation => []
		if (conditionalActivity.outcome !== null) {
		for(outcome: conditionalActivity.outcome){
			var checkParemeterConditions = outcome.outcomeValidation.filterNull
			if (checkParemeterConditions !== null) {
				validation.parametersValidationRules += checkParemeterConditions.toList
			}
			if (conditionalActivity.onTrueNextActivity !== null) {
				validation.onSuccessAction = MncModelFactory.eINSTANCE.createAction => [
					var nextActivity = conditionalActivity.onTrueNextActivity
					if (nextActivity !== null) {
						val capabilityInterfaceItem = nextActivity.capabilitiesAbstractInterfaceItems.head
						if (capabilityInterfaceItem instanceof Command) {
							// Fire command of the supervisory controller 
							fireCommand += MncModelFactory.eINSTANCE.createActionCommand => [
								command = MncModelFactory.eINSTANCE.createCommand => [
									name = capabilityInterfaceItem.name
								]
							]

						}
					}
				]
				var transition = getTransitionsFromConditionalActivity(conditionalActivity)
				validation.onSuccessAction.transitionStates += transition
			}
			if (conditionalActivity.onTrueFinalResult !== null) {
				val transition = getTransitionsFromConditionalActivity(conditionalActivity)
				validation.onSuccessAction = MncModelFactory.eINSTANCE.createAction => [
					transitionStates += transition

				]
			}
		}
			

		}
		return validation
	}

	def getStateExistsFromName(String string) {
		var flag = false
		var opState = null as OperatingState
		for (state : operatingState) {
			if (state.name.equalsIgnoreCase(string)) {
				flag = true
				return state
			}
		}
		if (!flag) {
			opState = MncModelFactory.eINSTANCE.createOperatingState => [name = string]
			operatingState += opState
		}
		return opState
	}

	def checkIfActionContains(AbstractInterfaceItems item) {
		for (action : initExecutableActions) {
			if (item instanceof Alarm && action.raiseAlarm !== null) {
				return action.raiseAlarm.filterNull.filter[it.alarm.equals(item)].size > 0
			}
			if (item instanceof Event && action.publishEvent !== null) {
				return action.publishEvent.filterNull.filter[it.event.equals(item)].size > 0
			}
			if (item instanceof DataPoint && action.triggerDataPoint !== null) {
				return action.triggerDataPoint.filterNull.filter[it.dataPoint.equals(item)].size > 0
			}
			if (item instanceof Command && action.fireCommand !== null) {
				return action.fireCommand.filterNull.filter[it.command.equals(item)].size > 0
			}
			if (item instanceof Operation && action.executeOperation.contains(item)) {
				return action.executeOperation.filterNull.filter[it.operation.equals(item)].size > 0
			}
		}
		return false
	}

	def capabilitiesAbstractInterfaceItems(Activity activity) {
		var controlCapabilitiesAbsractItems = new BasicEList<AbstractInterfaceItems>
		// Check if activity has a capability
		if (activity.bindCapability !== null) {
			// Get Activity Capability
			val capability = activity.bindCapability
			// Check if the capability has an interface
			if (capability.componentInterface !== null) {
				// Add uses interface
				var capIds = capability.componentInterface
				usedInterfaces += capIds
				// Add init process
				if (capability.requiredINITProcess !== null) {
					initExecutableActions += capability.requiredINITProcess
				}

				var capabilityControlFromCapabilityDescription = capability.providesControlCapabilities
				var capabilityControlFromActivityCapabilityLink = activity.useControlCapabilities

// Check if capability control list is available from either activity or capability
				if (capabilityControlFromActivityCapabilityLink !== null &&
					capabilityControlFromActivityCapabilityLink.size > 0) {
					controlCapabilitiesAbsractItems += capabilityControlFromActivityCapabilityLink
				} else if (capabilityControlFromCapabilityDescription !== null) {
					if (capabilityControlFromCapabilityDescription.commands !== null)
						controlCapabilitiesAbsractItems += capabilityControlFromCapabilityDescription.commands

					if (capabilityControlFromCapabilityDescription.events !== null)
						controlCapabilitiesAbsractItems += capabilityControlFromCapabilityDescription.events

					if (capabilityControlFromCapabilityDescription.alarms !== null)
						controlCapabilitiesAbsractItems += capabilityControlFromCapabilityDescription.alarms

					if (capabilityControlFromCapabilityDescription.dataPoints !== null)
						controlCapabilitiesAbsractItems += capabilityControlFromCapabilityDescription.dataPoints
				}

			}

		}
		return controlCapabilitiesAbsractItems
	}

}
