/**
 */
package mncModel;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see mncModel.MncModelPackage
 * @generated
 */
public interface MncModelFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	MncModelFactory eINSTANCE = mncModel.impl.MncModelFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Abstract Outcome Items</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Abstract Outcome Items</em>'.
	 * @generated
	 */
	AbstractOutcomeItems createAbstractOutcomeItems();

	/**
	 * Returns a new object of class '<em>System</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>System</em>'.
	 * @generated
	 */
	System createSystem();

	/**
	 * Returns a new object of class '<em>Behavior Block</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Behavior Block</em>'.
	 * @generated
	 */
	BehaviorBlock createBehaviorBlock();

	/**
	 * Returns a new object of class '<em>Abstract Interface Items</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Abstract Interface Items</em>'.
	 * @generated
	 */
	AbstractInterfaceItems createAbstractInterfaceItems();

	/**
	 * Returns a new object of class '<em>Model</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Model</em>'.
	 * @generated
	 */
	Model createModel();

	/**
	 * Returns a new object of class '<em>Import</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Import</em>'.
	 * @generated
	 */
	Import createImport();

	/**
	 * Returns a new object of class '<em>Interface Description</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Interface Description</em>'.
	 * @generated
	 */
	InterfaceDescription createInterfaceDescription();

	/**
	 * Returns a new object of class '<em>Port</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Port</em>'.
	 * @generated
	 */
	Port createPort();

	/**
	 * Returns a new object of class '<em>Address</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Address</em>'.
	 * @generated
	 */
	Address createAddress();

	/**
	 * Returns a new object of class '<em>Command</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Command</em>'.
	 * @generated
	 */
	Command createCommand();

	/**
	 * Returns a new object of class '<em>Operating State</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Operating State</em>'.
	 * @generated
	 */
	OperatingState createOperatingState();

	/**
	 * Returns a new object of class '<em>Response</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Response</em>'.
	 * @generated
	 */
	Response createResponse();

	/**
	 * Returns a new object of class '<em>Event</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Event</em>'.
	 * @generated
	 */
	Event createEvent();

	/**
	 * Returns a new object of class '<em>Alarm</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Alarm</em>'.
	 * @generated
	 */
	Alarm createAlarm();

	/**
	 * Returns a new object of class '<em>Data Point</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Data Point</em>'.
	 * @generated
	 */
	DataPoint createDataPoint();

	/**
	 * Returns a new object of class '<em>Subscribable Item List</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Subscribable Item List</em>'.
	 * @generated
	 */
	SubscribableItemList createSubscribableItemList();

	/**
	 * Returns a new object of class '<em>Control Node</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Control Node</em>'.
	 * @generated
	 */
	ControlNode createControlNode();

	/**
	 * Returns a new object of class '<em>Command Response Block</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Command Response Block</em>'.
	 * @generated
	 */
	CommandResponseBlock createCommandResponseBlock();

	/**
	 * Returns a new object of class '<em>Response Block</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Response Block</em>'.
	 * @generated
	 */
	ResponseBlock createResponseBlock();

	/**
	 * Returns a new object of class '<em>Response Aggregation Rule</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Response Aggregation Rule</em>'.
	 * @generated
	 */
	ResponseAggregationRule createResponseAggregationRule();

	/**
	 * Returns a new object of class '<em>Parameter Translation</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Parameter Translation</em>'.
	 * @generated
	 */
	ParameterTranslation createParameterTranslation();

	/**
	 * Returns a new object of class '<em>Event Block</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Event Block</em>'.
	 * @generated
	 */
	EventBlock createEventBlock();

	/**
	 * Returns a new object of class '<em>Alarm Block</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Alarm Block</em>'.
	 * @generated
	 */
	AlarmBlock createAlarmBlock();

	/**
	 * Returns a new object of class '<em>Data Point Block</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Data Point Block</em>'.
	 * @generated
	 */
	DataPointBlock createDataPointBlock();

	/**
	 * Returns a new object of class '<em>Check Parameter Condition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Check Parameter Condition</em>'.
	 * @generated
	 */
	CheckParameterCondition createCheckParameterCondition();

	/**
	 * Returns a new object of class '<em>Transition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Transition</em>'.
	 * @generated
	 */
	Transition createTransition();

	/**
	 * Returns a new object of class '<em>Action</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Action</em>'.
	 * @generated
	 */
	Action createAction();

	/**
	 * Returns a new object of class '<em>Action Item</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Action Item</em>'.
	 * @generated
	 */
	ActionItem createActionItem();

	/**
	 * Returns a new object of class '<em>Action Paremeter</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Action Paremeter</em>'.
	 * @generated
	 */
	ActionParemeter createActionParemeter();

	/**
	 * Returns a new object of class '<em>Action Command</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Action Command</em>'.
	 * @generated
	 */
	ActionCommand createActionCommand();

	/**
	 * Returns a new object of class '<em>Action Event</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Action Event</em>'.
	 * @generated
	 */
	ActionEvent createActionEvent();

	/**
	 * Returns a new object of class '<em>Action Alarm</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Action Alarm</em>'.
	 * @generated
	 */
	ActionAlarm createActionAlarm();

	/**
	 * Returns a new object of class '<em>Action Data Point</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Action Data Point</em>'.
	 * @generated
	 */
	ActionDataPoint createActionDataPoint();

	/**
	 * Returns a new object of class '<em>Action Operation</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Action Operation</em>'.
	 * @generated
	 */
	ActionOperation createActionOperation();

	/**
	 * Returns a new object of class '<em>Validation</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Validation</em>'.
	 * @generated
	 */
	Validation createValidation();

	/**
	 * Returns a new object of class '<em>Data Value</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Data Value</em>'.
	 * @generated
	 */
	DataValue createDataValue();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	MncModelPackage getMncModelPackage();

} //MncModelFactory
