/**
 */
package mncModel.util;

import mncModel.AbstractInterfaceItems;
import mncModel.AbstractOutcomeItems;
import mncModel.Action;
import mncModel.ActionAlarm;
import mncModel.ActionCommand;
import mncModel.ActionDataPoint;
import mncModel.ActionEvent;
import mncModel.ActionItem;
import mncModel.ActionOperation;
import mncModel.ActionParemeter;
import mncModel.Address;
import mncModel.Alarm;
import mncModel.AlarmBlock;
import mncModel.BehaviorBlock;
import mncModel.CheckParameterCondition;
import mncModel.Command;
import mncModel.CommandResponseBlock;
import mncModel.ControlNode;
import mncModel.DataPoint;
import mncModel.DataPointBlock;
import mncModel.DataValue;
import mncModel.Event;
import mncModel.EventBlock;
import mncModel.Import;
import mncModel.InterfaceDescription;
import mncModel.MncModelPackage;
import mncModel.Model;
import mncModel.OperatingState;
import mncModel.ParameterTranslation;
import mncModel.Port;
import mncModel.Response;
import mncModel.ResponseAggregationRule;
import mncModel.ResponseBlock;
import mncModel.SubscribableItemList;
import mncModel.Transition;
import mncModel.Validation;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see mncModel.MncModelPackage
 * @generated
 */
public class MncModelSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static MncModelPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MncModelSwitch() {
		if (modelPackage == null) {
			modelPackage = MncModelPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case MncModelPackage.ABSTRACT_OUTCOME_ITEMS: {
				AbstractOutcomeItems abstractOutcomeItems = (AbstractOutcomeItems)theEObject;
				T result = caseAbstractOutcomeItems(abstractOutcomeItems);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.SYSTEM: {
				mncModel.System system = (mncModel.System)theEObject;
				T result = caseSystem(system);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.BEHAVIOR_BLOCK: {
				BehaviorBlock behaviorBlock = (BehaviorBlock)theEObject;
				T result = caseBehaviorBlock(behaviorBlock);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.ABSTRACT_INTERFACE_ITEMS: {
				AbstractInterfaceItems abstractInterfaceItems = (AbstractInterfaceItems)theEObject;
				T result = caseAbstractInterfaceItems(abstractInterfaceItems);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.MODEL: {
				Model model = (Model)theEObject;
				T result = caseModel(model);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.IMPORT: {
				Import import_ = (Import)theEObject;
				T result = caseImport(import_);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.INTERFACE_DESCRIPTION: {
				InterfaceDescription interfaceDescription = (InterfaceDescription)theEObject;
				T result = caseInterfaceDescription(interfaceDescription);
				if (result == null) result = caseSystem(interfaceDescription);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.PORT: {
				Port port = (Port)theEObject;
				T result = casePort(port);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.ADDRESS: {
				Address address = (Address)theEObject;
				T result = caseAddress(address);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.COMMAND: {
				Command command = (Command)theEObject;
				T result = caseCommand(command);
				if (result == null) result = caseAbstractInterfaceItems(command);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.OPERATING_STATE: {
				OperatingState operatingState = (OperatingState)theEObject;
				T result = caseOperatingState(operatingState);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.RESPONSE: {
				Response response = (Response)theEObject;
				T result = caseResponse(response);
				if (result == null) result = caseAbstractInterfaceItems(response);
				if (result == null) result = caseAbstractOutcomeItems(response);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.EVENT: {
				Event event = (Event)theEObject;
				T result = caseEvent(event);
				if (result == null) result = caseAbstractInterfaceItems(event);
				if (result == null) result = caseAbstractOutcomeItems(event);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.ALARM: {
				Alarm alarm = (Alarm)theEObject;
				T result = caseAlarm(alarm);
				if (result == null) result = caseAbstractInterfaceItems(alarm);
				if (result == null) result = caseAbstractOutcomeItems(alarm);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.DATA_POINT: {
				DataPoint dataPoint = (DataPoint)theEObject;
				T result = caseDataPoint(dataPoint);
				if (result == null) result = caseAbstractInterfaceItems(dataPoint);
				if (result == null) result = caseAbstractOutcomeItems(dataPoint);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.SUBSCRIBABLE_ITEM_LIST: {
				SubscribableItemList subscribableItemList = (SubscribableItemList)theEObject;
				T result = caseSubscribableItemList(subscribableItemList);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.CONTROL_NODE: {
				ControlNode controlNode = (ControlNode)theEObject;
				T result = caseControlNode(controlNode);
				if (result == null) result = caseSystem(controlNode);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.COMMAND_RESPONSE_BLOCK: {
				CommandResponseBlock commandResponseBlock = (CommandResponseBlock)theEObject;
				T result = caseCommandResponseBlock(commandResponseBlock);
				if (result == null) result = caseBehaviorBlock(commandResponseBlock);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.RESPONSE_BLOCK: {
				ResponseBlock responseBlock = (ResponseBlock)theEObject;
				T result = caseResponseBlock(responseBlock);
				if (result == null) result = caseBehaviorBlock(responseBlock);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.RESPONSE_AGGREGATION_RULE: {
				ResponseAggregationRule responseAggregationRule = (ResponseAggregationRule)theEObject;
				T result = caseResponseAggregationRule(responseAggregationRule);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.PARAMETER_TRANSLATION: {
				ParameterTranslation parameterTranslation = (ParameterTranslation)theEObject;
				T result = caseParameterTranslation(parameterTranslation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.EVENT_BLOCK: {
				EventBlock eventBlock = (EventBlock)theEObject;
				T result = caseEventBlock(eventBlock);
				if (result == null) result = caseBehaviorBlock(eventBlock);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.ALARM_BLOCK: {
				AlarmBlock alarmBlock = (AlarmBlock)theEObject;
				T result = caseAlarmBlock(alarmBlock);
				if (result == null) result = caseBehaviorBlock(alarmBlock);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.DATA_POINT_BLOCK: {
				DataPointBlock dataPointBlock = (DataPointBlock)theEObject;
				T result = caseDataPointBlock(dataPointBlock);
				if (result == null) result = caseBehaviorBlock(dataPointBlock);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.CHECK_PARAMETER_CONDITION: {
				CheckParameterCondition checkParameterCondition = (CheckParameterCondition)theEObject;
				T result = caseCheckParameterCondition(checkParameterCondition);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.TRANSITION: {
				Transition transition = (Transition)theEObject;
				T result = caseTransition(transition);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.ACTION: {
				Action action = (Action)theEObject;
				T result = caseAction(action);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.ACTION_ITEM: {
				ActionItem actionItem = (ActionItem)theEObject;
				T result = caseActionItem(actionItem);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.ACTION_PAREMETER: {
				ActionParemeter actionParemeter = (ActionParemeter)theEObject;
				T result = caseActionParemeter(actionParemeter);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.ACTION_COMMAND: {
				ActionCommand actionCommand = (ActionCommand)theEObject;
				T result = caseActionCommand(actionCommand);
				if (result == null) result = caseActionItem(actionCommand);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.ACTION_EVENT: {
				ActionEvent actionEvent = (ActionEvent)theEObject;
				T result = caseActionEvent(actionEvent);
				if (result == null) result = caseActionItem(actionEvent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.ACTION_ALARM: {
				ActionAlarm actionAlarm = (ActionAlarm)theEObject;
				T result = caseActionAlarm(actionAlarm);
				if (result == null) result = caseActionItem(actionAlarm);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.ACTION_DATA_POINT: {
				ActionDataPoint actionDataPoint = (ActionDataPoint)theEObject;
				T result = caseActionDataPoint(actionDataPoint);
				if (result == null) result = caseActionItem(actionDataPoint);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.ACTION_OPERATION: {
				ActionOperation actionOperation = (ActionOperation)theEObject;
				T result = caseActionOperation(actionOperation);
				if (result == null) result = caseActionItem(actionOperation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.VALIDATION: {
				Validation validation = (Validation)theEObject;
				T result = caseValidation(validation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MncModelPackage.DATA_VALUE: {
				DataValue dataValue = (DataValue)theEObject;
				T result = caseDataValue(dataValue);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Abstract Outcome Items</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Abstract Outcome Items</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAbstractOutcomeItems(AbstractOutcomeItems object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>System</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>System</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSystem(mncModel.System object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Behavior Block</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Behavior Block</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBehaviorBlock(BehaviorBlock object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Abstract Interface Items</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Abstract Interface Items</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAbstractInterfaceItems(AbstractInterfaceItems object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Model</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Model</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseModel(Model object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Import</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Import</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseImport(Import object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Interface Description</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Interface Description</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseInterfaceDescription(InterfaceDescription object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Port</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Port</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePort(Port object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Address</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Address</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAddress(Address object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Command</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Command</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCommand(Command object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Operating State</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Operating State</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseOperatingState(OperatingState object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Response</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Response</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseResponse(Response object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEvent(Event object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Alarm</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Alarm</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAlarm(Alarm object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Data Point</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Data Point</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDataPoint(DataPoint object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Subscribable Item List</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Subscribable Item List</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSubscribableItemList(SubscribableItemList object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Control Node</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Control Node</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseControlNode(ControlNode object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Command Response Block</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Command Response Block</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCommandResponseBlock(CommandResponseBlock object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Response Block</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Response Block</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseResponseBlock(ResponseBlock object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Response Aggregation Rule</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Response Aggregation Rule</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseResponseAggregationRule(ResponseAggregationRule object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Parameter Translation</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parameter Translation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseParameterTranslation(ParameterTranslation object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Event Block</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Event Block</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEventBlock(EventBlock object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Alarm Block</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Alarm Block</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAlarmBlock(AlarmBlock object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Data Point Block</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Data Point Block</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDataPointBlock(DataPointBlock object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Check Parameter Condition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Check Parameter Condition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCheckParameterCondition(CheckParameterCondition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Transition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Transition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTransition(Transition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Action</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Action</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAction(Action object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Action Item</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Action Item</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseActionItem(ActionItem object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Action Paremeter</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Action Paremeter</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseActionParemeter(ActionParemeter object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Action Command</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Action Command</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseActionCommand(ActionCommand object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Action Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Action Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseActionEvent(ActionEvent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Action Alarm</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Action Alarm</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseActionAlarm(ActionAlarm object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Action Data Point</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Action Data Point</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseActionDataPoint(ActionDataPoint object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Action Operation</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Action Operation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseActionOperation(ActionOperation object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Validation</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Validation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseValidation(Validation object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Data Value</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Data Value</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDataValue(DataValue object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

} //MncModelSwitch
