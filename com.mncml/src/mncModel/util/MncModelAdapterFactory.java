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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see mncModel.MncModelPackage
 * @generated
 */
public class MncModelAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static MncModelPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MncModelAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = MncModelPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MncModelSwitch<Adapter> modelSwitch =
		new MncModelSwitch<Adapter>() {
			@Override
			public Adapter caseAbstractOutcomeItems(AbstractOutcomeItems object) {
				return createAbstractOutcomeItemsAdapter();
			}
			@Override
			public Adapter caseSystem(mncModel.System object) {
				return createSystemAdapter();
			}
			@Override
			public Adapter caseBehaviorBlock(BehaviorBlock object) {
				return createBehaviorBlockAdapter();
			}
			@Override
			public Adapter caseAbstractInterfaceItems(AbstractInterfaceItems object) {
				return createAbstractInterfaceItemsAdapter();
			}
			@Override
			public Adapter caseModel(Model object) {
				return createModelAdapter();
			}
			@Override
			public Adapter caseImport(Import object) {
				return createImportAdapter();
			}
			@Override
			public Adapter caseInterfaceDescription(InterfaceDescription object) {
				return createInterfaceDescriptionAdapter();
			}
			@Override
			public Adapter casePort(Port object) {
				return createPortAdapter();
			}
			@Override
			public Adapter caseAddress(Address object) {
				return createAddressAdapter();
			}
			@Override
			public Adapter caseCommand(Command object) {
				return createCommandAdapter();
			}
			@Override
			public Adapter caseOperatingState(OperatingState object) {
				return createOperatingStateAdapter();
			}
			@Override
			public Adapter caseResponse(Response object) {
				return createResponseAdapter();
			}
			@Override
			public Adapter caseEvent(Event object) {
				return createEventAdapter();
			}
			@Override
			public Adapter caseAlarm(Alarm object) {
				return createAlarmAdapter();
			}
			@Override
			public Adapter caseDataPoint(DataPoint object) {
				return createDataPointAdapter();
			}
			@Override
			public Adapter caseSubscribableItemList(SubscribableItemList object) {
				return createSubscribableItemListAdapter();
			}
			@Override
			public Adapter caseControlNode(ControlNode object) {
				return createControlNodeAdapter();
			}
			@Override
			public Adapter caseCommandResponseBlock(CommandResponseBlock object) {
				return createCommandResponseBlockAdapter();
			}
			@Override
			public Adapter caseResponseBlock(ResponseBlock object) {
				return createResponseBlockAdapter();
			}
			@Override
			public Adapter caseResponseAggregationRule(ResponseAggregationRule object) {
				return createResponseAggregationRuleAdapter();
			}
			@Override
			public Adapter caseParameterTranslation(ParameterTranslation object) {
				return createParameterTranslationAdapter();
			}
			@Override
			public Adapter caseEventBlock(EventBlock object) {
				return createEventBlockAdapter();
			}
			@Override
			public Adapter caseAlarmBlock(AlarmBlock object) {
				return createAlarmBlockAdapter();
			}
			@Override
			public Adapter caseDataPointBlock(DataPointBlock object) {
				return createDataPointBlockAdapter();
			}
			@Override
			public Adapter caseCheckParameterCondition(CheckParameterCondition object) {
				return createCheckParameterConditionAdapter();
			}
			@Override
			public Adapter caseTransition(Transition object) {
				return createTransitionAdapter();
			}
			@Override
			public Adapter caseAction(Action object) {
				return createActionAdapter();
			}
			@Override
			public Adapter caseActionItem(ActionItem object) {
				return createActionItemAdapter();
			}
			@Override
			public Adapter caseActionParemeter(ActionParemeter object) {
				return createActionParemeterAdapter();
			}
			@Override
			public Adapter caseActionCommand(ActionCommand object) {
				return createActionCommandAdapter();
			}
			@Override
			public Adapter caseActionEvent(ActionEvent object) {
				return createActionEventAdapter();
			}
			@Override
			public Adapter caseActionAlarm(ActionAlarm object) {
				return createActionAlarmAdapter();
			}
			@Override
			public Adapter caseActionDataPoint(ActionDataPoint object) {
				return createActionDataPointAdapter();
			}
			@Override
			public Adapter caseActionOperation(ActionOperation object) {
				return createActionOperationAdapter();
			}
			@Override
			public Adapter caseValidation(Validation object) {
				return createValidationAdapter();
			}
			@Override
			public Adapter caseDataValue(DataValue object) {
				return createDataValueAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link mncModel.AbstractOutcomeItems <em>Abstract Outcome Items</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.AbstractOutcomeItems
	 * @generated
	 */
	public Adapter createAbstractOutcomeItemsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.System <em>System</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.System
	 * @generated
	 */
	public Adapter createSystemAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.BehaviorBlock <em>Behavior Block</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.BehaviorBlock
	 * @generated
	 */
	public Adapter createBehaviorBlockAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.AbstractInterfaceItems <em>Abstract Interface Items</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.AbstractInterfaceItems
	 * @generated
	 */
	public Adapter createAbstractInterfaceItemsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.Model <em>Model</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.Model
	 * @generated
	 */
	public Adapter createModelAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.Import <em>Import</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.Import
	 * @generated
	 */
	public Adapter createImportAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.InterfaceDescription <em>Interface Description</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.InterfaceDescription
	 * @generated
	 */
	public Adapter createInterfaceDescriptionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.Port <em>Port</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.Port
	 * @generated
	 */
	public Adapter createPortAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.Address <em>Address</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.Address
	 * @generated
	 */
	public Adapter createAddressAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.Command <em>Command</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.Command
	 * @generated
	 */
	public Adapter createCommandAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.OperatingState <em>Operating State</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.OperatingState
	 * @generated
	 */
	public Adapter createOperatingStateAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.Response <em>Response</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.Response
	 * @generated
	 */
	public Adapter createResponseAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.Event <em>Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.Event
	 * @generated
	 */
	public Adapter createEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.Alarm <em>Alarm</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.Alarm
	 * @generated
	 */
	public Adapter createAlarmAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.DataPoint <em>Data Point</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.DataPoint
	 * @generated
	 */
	public Adapter createDataPointAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.SubscribableItemList <em>Subscribable Item List</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.SubscribableItemList
	 * @generated
	 */
	public Adapter createSubscribableItemListAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.ControlNode <em>Control Node</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.ControlNode
	 * @generated
	 */
	public Adapter createControlNodeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.CommandResponseBlock <em>Command Response Block</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.CommandResponseBlock
	 * @generated
	 */
	public Adapter createCommandResponseBlockAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.ResponseBlock <em>Response Block</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.ResponseBlock
	 * @generated
	 */
	public Adapter createResponseBlockAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.ResponseAggregationRule <em>Response Aggregation Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.ResponseAggregationRule
	 * @generated
	 */
	public Adapter createResponseAggregationRuleAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.ParameterTranslation <em>Parameter Translation</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.ParameterTranslation
	 * @generated
	 */
	public Adapter createParameterTranslationAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.EventBlock <em>Event Block</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.EventBlock
	 * @generated
	 */
	public Adapter createEventBlockAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.AlarmBlock <em>Alarm Block</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.AlarmBlock
	 * @generated
	 */
	public Adapter createAlarmBlockAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.DataPointBlock <em>Data Point Block</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.DataPointBlock
	 * @generated
	 */
	public Adapter createDataPointBlockAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.CheckParameterCondition <em>Check Parameter Condition</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.CheckParameterCondition
	 * @generated
	 */
	public Adapter createCheckParameterConditionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.Transition <em>Transition</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.Transition
	 * @generated
	 */
	public Adapter createTransitionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.Action <em>Action</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.Action
	 * @generated
	 */
	public Adapter createActionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.ActionItem <em>Action Item</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.ActionItem
	 * @generated
	 */
	public Adapter createActionItemAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.ActionParemeter <em>Action Paremeter</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.ActionParemeter
	 * @generated
	 */
	public Adapter createActionParemeterAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.ActionCommand <em>Action Command</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.ActionCommand
	 * @generated
	 */
	public Adapter createActionCommandAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.ActionEvent <em>Action Event</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.ActionEvent
	 * @generated
	 */
	public Adapter createActionEventAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.ActionAlarm <em>Action Alarm</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.ActionAlarm
	 * @generated
	 */
	public Adapter createActionAlarmAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.ActionDataPoint <em>Action Data Point</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.ActionDataPoint
	 * @generated
	 */
	public Adapter createActionDataPointAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.ActionOperation <em>Action Operation</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.ActionOperation
	 * @generated
	 */
	public Adapter createActionOperationAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.Validation <em>Validation</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.Validation
	 * @generated
	 */
	public Adapter createValidationAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link mncModel.DataValue <em>Data Value</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see mncModel.DataValue
	 * @generated
	 */
	public Adapter createDataValueAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //MncModelAdapterFactory
