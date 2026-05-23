/**
 */
package mncModel.impl;

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
import mncModel.MncModelFactory;
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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class MncModelFactoryImpl extends EFactoryImpl implements MncModelFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static MncModelFactory init() {
		try {
			MncModelFactory theMncModelFactory = (MncModelFactory)EPackage.Registry.INSTANCE.getEFactory(MncModelPackage.eNS_URI);
			if (theMncModelFactory != null) {
				return theMncModelFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new MncModelFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MncModelFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case MncModelPackage.ABSTRACT_OUTCOME_ITEMS: return createAbstractOutcomeItems();
			case MncModelPackage.SYSTEM: return createSystem();
			case MncModelPackage.BEHAVIOR_BLOCK: return createBehaviorBlock();
			case MncModelPackage.ABSTRACT_INTERFACE_ITEMS: return createAbstractInterfaceItems();
			case MncModelPackage.MODEL: return createModel();
			case MncModelPackage.IMPORT: return createImport();
			case MncModelPackage.INTERFACE_DESCRIPTION: return createInterfaceDescription();
			case MncModelPackage.PORT: return createPort();
			case MncModelPackage.ADDRESS: return createAddress();
			case MncModelPackage.COMMAND: return createCommand();
			case MncModelPackage.OPERATING_STATE: return createOperatingState();
			case MncModelPackage.RESPONSE: return createResponse();
			case MncModelPackage.EVENT: return createEvent();
			case MncModelPackage.ALARM: return createAlarm();
			case MncModelPackage.DATA_POINT: return createDataPoint();
			case MncModelPackage.SUBSCRIBABLE_ITEM_LIST: return createSubscribableItemList();
			case MncModelPackage.CONTROL_NODE: return createControlNode();
			case MncModelPackage.COMMAND_RESPONSE_BLOCK: return createCommandResponseBlock();
			case MncModelPackage.RESPONSE_BLOCK: return createResponseBlock();
			case MncModelPackage.RESPONSE_AGGREGATION_RULE: return createResponseAggregationRule();
			case MncModelPackage.PARAMETER_TRANSLATION: return createParameterTranslation();
			case MncModelPackage.EVENT_BLOCK: return createEventBlock();
			case MncModelPackage.ALARM_BLOCK: return createAlarmBlock();
			case MncModelPackage.DATA_POINT_BLOCK: return createDataPointBlock();
			case MncModelPackage.CHECK_PARAMETER_CONDITION: return createCheckParameterCondition();
			case MncModelPackage.TRANSITION: return createTransition();
			case MncModelPackage.ACTION: return createAction();
			case MncModelPackage.ACTION_ITEM: return createActionItem();
			case MncModelPackage.ACTION_PAREMETER: return createActionParemeter();
			case MncModelPackage.ACTION_COMMAND: return createActionCommand();
			case MncModelPackage.ACTION_EVENT: return createActionEvent();
			case MncModelPackage.ACTION_ALARM: return createActionAlarm();
			case MncModelPackage.ACTION_DATA_POINT: return createActionDataPoint();
			case MncModelPackage.ACTION_OPERATION: return createActionOperation();
			case MncModelPackage.VALIDATION: return createValidation();
			case MncModelPackage.DATA_VALUE: return createDataValue();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AbstractOutcomeItems createAbstractOutcomeItems() {
		AbstractOutcomeItemsImpl abstractOutcomeItems = new AbstractOutcomeItemsImpl();
		return abstractOutcomeItems;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public mncModel.System createSystem() {
		SystemImpl system = new SystemImpl();
		return system;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BehaviorBlock createBehaviorBlock() {
		BehaviorBlockImpl behaviorBlock = new BehaviorBlockImpl();
		return behaviorBlock;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AbstractInterfaceItems createAbstractInterfaceItems() {
		AbstractInterfaceItemsImpl abstractInterfaceItems = new AbstractInterfaceItemsImpl();
		return abstractInterfaceItems;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Model createModel() {
		ModelImpl model = new ModelImpl();
		return model;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Import createImport() {
		ImportImpl import_ = new ImportImpl();
		return import_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public InterfaceDescription createInterfaceDescription() {
		InterfaceDescriptionImpl interfaceDescription = new InterfaceDescriptionImpl();
		return interfaceDescription;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Port createPort() {
		PortImpl port = new PortImpl();
		return port;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Address createAddress() {
		AddressImpl address = new AddressImpl();
		return address;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Command createCommand() {
		CommandImpl command = new CommandImpl();
		return command;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public OperatingState createOperatingState() {
		OperatingStateImpl operatingState = new OperatingStateImpl();
		return operatingState;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Response createResponse() {
		ResponseImpl response = new ResponseImpl();
		return response;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Event createEvent() {
		EventImpl event = new EventImpl();
		return event;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Alarm createAlarm() {
		AlarmImpl alarm = new AlarmImpl();
		return alarm;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DataPoint createDataPoint() {
		DataPointImpl dataPoint = new DataPointImpl();
		return dataPoint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SubscribableItemList createSubscribableItemList() {
		SubscribableItemListImpl subscribableItemList = new SubscribableItemListImpl();
		return subscribableItemList;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ControlNode createControlNode() {
		ControlNodeImpl controlNode = new ControlNodeImpl();
		return controlNode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public CommandResponseBlock createCommandResponseBlock() {
		CommandResponseBlockImpl commandResponseBlock = new CommandResponseBlockImpl();
		return commandResponseBlock;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ResponseBlock createResponseBlock() {
		ResponseBlockImpl responseBlock = new ResponseBlockImpl();
		return responseBlock;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ResponseAggregationRule createResponseAggregationRule() {
		ResponseAggregationRuleImpl responseAggregationRule = new ResponseAggregationRuleImpl();
		return responseAggregationRule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ParameterTranslation createParameterTranslation() {
		ParameterTranslationImpl parameterTranslation = new ParameterTranslationImpl();
		return parameterTranslation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EventBlock createEventBlock() {
		EventBlockImpl eventBlock = new EventBlockImpl();
		return eventBlock;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AlarmBlock createAlarmBlock() {
		AlarmBlockImpl alarmBlock = new AlarmBlockImpl();
		return alarmBlock;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DataPointBlock createDataPointBlock() {
		DataPointBlockImpl dataPointBlock = new DataPointBlockImpl();
		return dataPointBlock;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public CheckParameterCondition createCheckParameterCondition() {
		CheckParameterConditionImpl checkParameterCondition = new CheckParameterConditionImpl();
		return checkParameterCondition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Transition createTransition() {
		TransitionImpl transition = new TransitionImpl();
		return transition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Action createAction() {
		ActionImpl action = new ActionImpl();
		return action;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ActionItem createActionItem() {
		ActionItemImpl actionItem = new ActionItemImpl();
		return actionItem;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ActionParemeter createActionParemeter() {
		ActionParemeterImpl actionParemeter = new ActionParemeterImpl();
		return actionParemeter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ActionCommand createActionCommand() {
		ActionCommandImpl actionCommand = new ActionCommandImpl();
		return actionCommand;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ActionEvent createActionEvent() {
		ActionEventImpl actionEvent = new ActionEventImpl();
		return actionEvent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ActionAlarm createActionAlarm() {
		ActionAlarmImpl actionAlarm = new ActionAlarmImpl();
		return actionAlarm;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ActionDataPoint createActionDataPoint() {
		ActionDataPointImpl actionDataPoint = new ActionDataPointImpl();
		return actionDataPoint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ActionOperation createActionOperation() {
		ActionOperationImpl actionOperation = new ActionOperationImpl();
		return actionOperation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Validation createValidation() {
		ValidationImpl validation = new ValidationImpl();
		return validation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DataValue createDataValue() {
		DataValueImpl dataValue = new DataValueImpl();
		return dataValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MncModelPackage getMncModelPackage() {
		return (MncModelPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static MncModelPackage getPackage() {
		return MncModelPackage.eINSTANCE;
	}

} //MncModelFactoryImpl
