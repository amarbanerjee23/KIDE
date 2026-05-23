/**
 */
package mncModel;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see mncModel.MncModelFactory
 * @model kind="package"
 * @generated
 */
public interface MncModelPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "mncModel";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://mncModel/4.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "mncModel";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	MncModelPackage eINSTANCE = mncModel.impl.MncModelPackageImpl.init();

	/**
	 * The meta object id for the '{@link mncModel.impl.AbstractOutcomeItemsImpl <em>Abstract Outcome Items</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.AbstractOutcomeItemsImpl
	 * @see mncModel.impl.MncModelPackageImpl#getAbstractOutcomeItems()
	 * @generated
	 */
	int ABSTRACT_OUTCOME_ITEMS = 0;

	/**
	 * The number of structural features of the '<em>Abstract Outcome Items</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_OUTCOME_ITEMS_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>Abstract Outcome Items</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_OUTCOME_ITEMS_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.SystemImpl <em>System</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.SystemImpl
	 * @see mncModel.impl.MncModelPackageImpl#getSystem()
	 * @generated
	 */
	int SYSTEM = 1;

	/**
	 * The number of structural features of the '<em>System</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SYSTEM_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>System</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SYSTEM_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.BehaviorBlockImpl <em>Behavior Block</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.BehaviorBlockImpl
	 * @see mncModel.impl.MncModelPackageImpl#getBehaviorBlock()
	 * @generated
	 */
	int BEHAVIOR_BLOCK = 2;

	/**
	 * The feature id for the '<em><b>Validation Rules</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BEHAVIOR_BLOCK__VALIDATION_RULES = 0;

	/**
	 * The feature id for the '<em><b>Action</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BEHAVIOR_BLOCK__ACTION = 1;

	/**
	 * The number of structural features of the '<em>Behavior Block</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BEHAVIOR_BLOCK_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Behavior Block</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BEHAVIOR_BLOCK_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.AbstractInterfaceItemsImpl <em>Abstract Interface Items</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.AbstractInterfaceItemsImpl
	 * @see mncModel.impl.MncModelPackageImpl#getAbstractInterfaceItems()
	 * @generated
	 */
	int ABSTRACT_INTERFACE_ITEMS = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_INTERFACE_ITEMS__NAME = 0;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_INTERFACE_ITEMS__PARAMETERS = 1;

	/**
	 * The number of structural features of the '<em>Abstract Interface Items</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_INTERFACE_ITEMS_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Abstract Interface Items</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_INTERFACE_ITEMS_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.ModelImpl <em>Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.ModelImpl
	 * @see mncModel.impl.MncModelPackageImpl#getModel()
	 * @generated
	 */
	int MODEL = 4;

	/**
	 * The feature id for the '<em><b>Systems</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL__SYSTEMS = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL__NAME = 1;

	/**
	 * The feature id for the '<em><b>Import Section</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL__IMPORT_SECTION = 2;

	/**
	 * The number of structural features of the '<em>Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.ImportImpl <em>Import</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.ImportImpl
	 * @see mncModel.impl.MncModelPackageImpl#getImport()
	 * @generated
	 */
	int IMPORT = 5;

	/**
	 * The feature id for the '<em><b>Imported Namespace</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPORT__IMPORTED_NAMESPACE = 0;

	/**
	 * The number of structural features of the '<em>Import</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPORT_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Import</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPORT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.InterfaceDescriptionImpl <em>Interface Description</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.InterfaceDescriptionImpl
	 * @see mncModel.impl.MncModelPackageImpl#getInterfaceDescription()
	 * @generated
	 */
	int INTERFACE_DESCRIPTION = 6;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_DESCRIPTION__NAME = SYSTEM_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Ipaddress</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_DESCRIPTION__IPADDRESS = SYSTEM_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Port</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_DESCRIPTION__PORT = SYSTEM_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Data Points</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_DESCRIPTION__DATA_POINTS = SYSTEM_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Alarms</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_DESCRIPTION__ALARMS = SYSTEM_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Commands</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_DESCRIPTION__COMMANDS = SYSTEM_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Events</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_DESCRIPTION__EVENTS = SYSTEM_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Responses</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_DESCRIPTION__RESPONSES = SYSTEM_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Operating States Utility</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_DESCRIPTION__OPERATING_STATES_UTILITY = SYSTEM_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Subscribed Items</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_DESCRIPTION__SUBSCRIBED_ITEMS = SYSTEM_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Uses</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_DESCRIPTION__USES = SYSTEM_FEATURE_COUNT + 10;

	/**
	 * The number of structural features of the '<em>Interface Description</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_DESCRIPTION_FEATURE_COUNT = SYSTEM_FEATURE_COUNT + 11;

	/**
	 * The number of operations of the '<em>Interface Description</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_DESCRIPTION_OPERATION_COUNT = SYSTEM_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.PortImpl <em>Port</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.PortImpl
	 * @see mncModel.impl.MncModelPackageImpl#getPort()
	 * @generated
	 */
	int PORT = 7;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PORT__NAME = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PORT__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Port</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PORT_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Port</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PORT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.AddressImpl <em>Address</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.AddressImpl
	 * @see mncModel.impl.MncModelPackageImpl#getAddress()
	 * @generated
	 */
	int ADDRESS = 8;

	/**
	 * The feature id for the '<em><b>Ipaddress</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDRESS__IPADDRESS = 0;

	/**
	 * The number of structural features of the '<em>Address</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDRESS_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Address</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADDRESS_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.CommandImpl <em>Command</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.CommandImpl
	 * @see mncModel.impl.MncModelPackageImpl#getCommand()
	 * @generated
	 */
	int COMMAND = 9;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMAND__NAME = ABSTRACT_INTERFACE_ITEMS__NAME;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMAND__PARAMETERS = ABSTRACT_INTERFACE_ITEMS__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Asynch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMAND__ASYNCH = ABSTRACT_INTERFACE_ITEMS_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Command</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMAND_FEATURE_COUNT = ABSTRACT_INTERFACE_ITEMS_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Command</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMAND_OPERATION_COUNT = ABSTRACT_INTERFACE_ITEMS_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.OperatingStateImpl <em>Operating State</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.OperatingStateImpl
	 * @see mncModel.impl.MncModelPackageImpl#getOperatingState()
	 * @generated
	 */
	int OPERATING_STATE = 10;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATING_STATE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATING_STATE__PARAMETERS = 1;

	/**
	 * The number of structural features of the '<em>Operating State</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATING_STATE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Operating State</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATING_STATE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.ResponseImpl <em>Response</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.ResponseImpl
	 * @see mncModel.impl.MncModelPackageImpl#getResponse()
	 * @generated
	 */
	int RESPONSE = 11;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__NAME = ABSTRACT_INTERFACE_ITEMS__NAME;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__PARAMETERS = ABSTRACT_INTERFACE_ITEMS__PARAMETERS;

	/**
	 * The number of structural features of the '<em>Response</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_FEATURE_COUNT = ABSTRACT_INTERFACE_ITEMS_FEATURE_COUNT + 0;

	/**
	 * The number of operations of the '<em>Response</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_OPERATION_COUNT = ABSTRACT_INTERFACE_ITEMS_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.EventImpl <em>Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.EventImpl
	 * @see mncModel.impl.MncModelPackageImpl#getEvent()
	 * @generated
	 */
	int EVENT = 12;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT__NAME = ABSTRACT_INTERFACE_ITEMS__NAME;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT__PARAMETERS = ABSTRACT_INTERFACE_ITEMS__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Publish</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT__PUBLISH = ABSTRACT_INTERFACE_ITEMS_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_FEATURE_COUNT = ABSTRACT_INTERFACE_ITEMS_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_OPERATION_COUNT = ABSTRACT_INTERFACE_ITEMS_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.AlarmImpl <em>Alarm</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.AlarmImpl
	 * @see mncModel.impl.MncModelPackageImpl#getAlarm()
	 * @generated
	 */
	int ALARM = 13;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALARM__NAME = ABSTRACT_INTERFACE_ITEMS__NAME;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALARM__PARAMETERS = ABSTRACT_INTERFACE_ITEMS__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALARM__TYPE = ABSTRACT_INTERFACE_ITEMS_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Level</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALARM__LEVEL = ABSTRACT_INTERFACE_ITEMS_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Publish</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALARM__PUBLISH = ABSTRACT_INTERFACE_ITEMS_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Alarm</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALARM_FEATURE_COUNT = ABSTRACT_INTERFACE_ITEMS_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Alarm</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALARM_OPERATION_COUNT = ABSTRACT_INTERFACE_ITEMS_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.DataPointImpl <em>Data Point</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.DataPointImpl
	 * @see mncModel.impl.MncModelPackageImpl#getDataPoint()
	 * @generated
	 */
	int DATA_POINT = 14;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_POINT__NAME = ABSTRACT_INTERFACE_ITEMS__NAME;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_POINT__PARAMETERS = ABSTRACT_INTERFACE_ITEMS__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_POINT__TYPE = ABSTRACT_INTERFACE_ITEMS_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_POINT__VALUE = ABSTRACT_INTERFACE_ITEMS_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Publish</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_POINT__PUBLISH = ABSTRACT_INTERFACE_ITEMS_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Data Point</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_POINT_FEATURE_COUNT = ABSTRACT_INTERFACE_ITEMS_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Data Point</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_POINT_OPERATION_COUNT = ABSTRACT_INTERFACE_ITEMS_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.SubscribableItemListImpl <em>Subscribable Item List</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.SubscribableItemListImpl
	 * @see mncModel.impl.MncModelPackageImpl#getSubscribableItemList()
	 * @generated
	 */
	int SUBSCRIBABLE_ITEM_LIST = 15;

	/**
	 * The feature id for the '<em><b>Subscribed Events</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_EVENTS = 0;

	/**
	 * The feature id for the '<em><b>Subscribed Alarms</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_ALARMS = 1;

	/**
	 * The feature id for the '<em><b>Subscribed Data Points</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_DATA_POINTS = 2;

	/**
	 * The number of structural features of the '<em>Subscribable Item List</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBSCRIBABLE_ITEM_LIST_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Subscribable Item List</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBSCRIBABLE_ITEM_LIST_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.ControlNodeImpl <em>Control Node</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.ControlNodeImpl
	 * @see mncModel.impl.MncModelPackageImpl#getControlNode()
	 * @generated
	 */
	int CONTROL_NODE = 16;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_NODE__NAME = SYSTEM_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Interface Description</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_NODE__INTERFACE_DESCRIPTION = SYSTEM_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Child Nodes</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_NODE__CHILD_NODES = SYSTEM_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Parent Node</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_NODE__PARENT_NODE = SYSTEM_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Command Response Blocks</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_NODE__COMMAND_RESPONSE_BLOCKS = SYSTEM_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Event Blocks</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_NODE__EVENT_BLOCKS = SYSTEM_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Alarm Blocks</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_NODE__ALARM_BLOCKS = SYSTEM_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Data Point Blocks</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_NODE__DATA_POINT_BLOCKS = SYSTEM_FEATURE_COUNT + 7;

	/**
	 * The number of structural features of the '<em>Control Node</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_NODE_FEATURE_COUNT = SYSTEM_FEATURE_COUNT + 8;

	/**
	 * The number of operations of the '<em>Control Node</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_NODE_OPERATION_COUNT = SYSTEM_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.CommandResponseBlockImpl <em>Command Response Block</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.CommandResponseBlockImpl
	 * @see mncModel.impl.MncModelPackageImpl#getCommandResponseBlock()
	 * @generated
	 */
	int COMMAND_RESPONSE_BLOCK = 17;

	/**
	 * The feature id for the '<em><b>Validation Rules</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMAND_RESPONSE_BLOCK__VALIDATION_RULES = BEHAVIOR_BLOCK__VALIDATION_RULES;

	/**
	 * The feature id for the '<em><b>Action</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMAND_RESPONSE_BLOCK__ACTION = BEHAVIOR_BLOCK__ACTION;

	/**
	 * The feature id for the '<em><b>Command</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMAND_RESPONSE_BLOCK__COMMAND = BEHAVIOR_BLOCK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Response Block</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMAND_RESPONSE_BLOCK__RESPONSE_BLOCK = BEHAVIOR_BLOCK_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Command Response Block</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMAND_RESPONSE_BLOCK_FEATURE_COUNT = BEHAVIOR_BLOCK_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Command Response Block</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMAND_RESPONSE_BLOCK_OPERATION_COUNT = BEHAVIOR_BLOCK_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.ResponseBlockImpl <em>Response Block</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.ResponseBlockImpl
	 * @see mncModel.impl.MncModelPackageImpl#getResponseBlock()
	 * @generated
	 */
	int RESPONSE_BLOCK = 18;

	/**
	 * The feature id for the '<em><b>Validation Rules</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_BLOCK__VALIDATION_RULES = BEHAVIOR_BLOCK__VALIDATION_RULES;

	/**
	 * The feature id for the '<em><b>Action</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_BLOCK__ACTION = BEHAVIOR_BLOCK__ACTION;

	/**
	 * The feature id for the '<em><b>Response</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_BLOCK__RESPONSE = BEHAVIOR_BLOCK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Response Aggregation Rules</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_BLOCK__RESPONSE_AGGREGATION_RULES = BEHAVIOR_BLOCK_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Response Block</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_BLOCK_FEATURE_COUNT = BEHAVIOR_BLOCK_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Response Block</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_BLOCK_OPERATION_COUNT = BEHAVIOR_BLOCK_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.ResponseAggregationRuleImpl <em>Response Aggregation Rule</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.ResponseAggregationRuleImpl
	 * @see mncModel.impl.MncModelPackageImpl#getResponseAggregationRule()
	 * @generated
	 */
	int RESPONSE_AGGREGATION_RULE = 19;

	/**
	 * The feature id for the '<em><b>Input Responses</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_AGGREGATION_RULE__INPUT_RESPONSES = 0;

	/**
	 * The feature id for the '<em><b>Parameter Translations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_AGGREGATION_RULE__PARAMETER_TRANSLATIONS = 1;

	/**
	 * The number of structural features of the '<em>Response Aggregation Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_AGGREGATION_RULE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Response Aggregation Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_AGGREGATION_RULE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.ParameterTranslationImpl <em>Parameter Translation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.ParameterTranslationImpl
	 * @see mncModel.impl.MncModelPackageImpl#getParameterTranslation()
	 * @generated
	 */
	int PARAMETER_TRANSLATION = 20;

	/**
	 * The feature id for the '<em><b>Input Parameters</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_TRANSLATION__INPUT_PARAMETERS = 0;

	/**
	 * The feature id for the '<em><b>Transformation Function</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_TRANSLATION__TRANSFORMATION_FUNCTION = 1;

	/**
	 * The feature id for the '<em><b>Translated Parameters</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_TRANSLATION__TRANSLATED_PARAMETERS = 2;

	/**
	 * The number of structural features of the '<em>Parameter Translation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_TRANSLATION_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Parameter Translation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_TRANSLATION_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.EventBlockImpl <em>Event Block</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.EventBlockImpl
	 * @see mncModel.impl.MncModelPackageImpl#getEventBlock()
	 * @generated
	 */
	int EVENT_BLOCK = 21;

	/**
	 * The feature id for the '<em><b>Validation Rules</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BLOCK__VALIDATION_RULES = BEHAVIOR_BLOCK__VALIDATION_RULES;

	/**
	 * The feature id for the '<em><b>Action</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BLOCK__ACTION = BEHAVIOR_BLOCK__ACTION;

	/**
	 * The feature id for the '<em><b>Event</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BLOCK__EVENT = BEHAVIOR_BLOCK_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Event Block</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BLOCK_FEATURE_COUNT = BEHAVIOR_BLOCK_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Event Block</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BLOCK_OPERATION_COUNT = BEHAVIOR_BLOCK_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.AlarmBlockImpl <em>Alarm Block</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.AlarmBlockImpl
	 * @see mncModel.impl.MncModelPackageImpl#getAlarmBlock()
	 * @generated
	 */
	int ALARM_BLOCK = 22;

	/**
	 * The feature id for the '<em><b>Validation Rules</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALARM_BLOCK__VALIDATION_RULES = BEHAVIOR_BLOCK__VALIDATION_RULES;

	/**
	 * The feature id for the '<em><b>Action</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALARM_BLOCK__ACTION = BEHAVIOR_BLOCK__ACTION;

	/**
	 * The feature id for the '<em><b>Alarm</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALARM_BLOCK__ALARM = BEHAVIOR_BLOCK_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Alarm Block</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALARM_BLOCK_FEATURE_COUNT = BEHAVIOR_BLOCK_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Alarm Block</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ALARM_BLOCK_OPERATION_COUNT = BEHAVIOR_BLOCK_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.DataPointBlockImpl <em>Data Point Block</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.DataPointBlockImpl
	 * @see mncModel.impl.MncModelPackageImpl#getDataPointBlock()
	 * @generated
	 */
	int DATA_POINT_BLOCK = 23;

	/**
	 * The feature id for the '<em><b>Validation Rules</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_POINT_BLOCK__VALIDATION_RULES = BEHAVIOR_BLOCK__VALIDATION_RULES;

	/**
	 * The feature id for the '<em><b>Action</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_POINT_BLOCK__ACTION = BEHAVIOR_BLOCK__ACTION;

	/**
	 * The feature id for the '<em><b>Data Point</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_POINT_BLOCK__DATA_POINT = BEHAVIOR_BLOCK_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Data Point Block</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_POINT_BLOCK_FEATURE_COUNT = BEHAVIOR_BLOCK_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Data Point Block</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_POINT_BLOCK_OPERATION_COUNT = BEHAVIOR_BLOCK_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.CheckParameterConditionImpl <em>Check Parameter Condition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.CheckParameterConditionImpl
	 * @see mncModel.impl.MncModelPackageImpl#getCheckParameterCondition()
	 * @generated
	 */
	int CHECK_PARAMETER_CONDITION = 24;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHECK_PARAMETER_CONDITION__PARAMETER = 0;

	/**
	 * The feature id for the '<em><b>Check Values</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHECK_PARAMETER_CONDITION__CHECK_VALUES = 1;

	/**
	 * The feature id for the '<em><b>Check Max Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHECK_PARAMETER_CONDITION__CHECK_MAX_VALUE = 2;

	/**
	 * The feature id for the '<em><b>Check Min Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHECK_PARAMETER_CONDITION__CHECK_MIN_VALUE = 3;

	/**
	 * The feature id for the '<em><b>Operations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHECK_PARAMETER_CONDITION__OPERATIONS = 4;

	/**
	 * The number of structural features of the '<em>Check Parameter Condition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHECK_PARAMETER_CONDITION_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Check Parameter Condition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHECK_PARAMETER_CONDITION_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.TransitionImpl <em>Transition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.TransitionImpl
	 * @see mncModel.impl.MncModelPackageImpl#getTransition()
	 * @generated
	 */
	int TRANSITION = 25;

	/**
	 * The feature id for the '<em><b>Current State</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSITION__CURRENT_STATE = 0;

	/**
	 * The feature id for the '<em><b>Next State</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSITION__NEXT_STATE = 1;

	/**
	 * The feature id for the '<em><b>Entry Action</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSITION__ENTRY_ACTION = 2;

	/**
	 * The feature id for the '<em><b>Exit Action</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSITION__EXIT_ACTION = 3;

	/**
	 * The number of structural features of the '<em>Transition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSITION_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Transition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSITION_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.ActionImpl <em>Action</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.ActionImpl
	 * @see mncModel.impl.MncModelPackageImpl#getAction()
	 * @generated
	 */
	int ACTION = 26;

	/**
	 * The feature id for the '<em><b>Fire Command</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION__FIRE_COMMAND = 0;

	/**
	 * The feature id for the '<em><b>Publish Event</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION__PUBLISH_EVENT = 1;

	/**
	 * The feature id for the '<em><b>Raise Alarm</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION__RAISE_ALARM = 2;

	/**
	 * The feature id for the '<em><b>Trigger Data Point</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION__TRIGGER_DATA_POINT = 3;

	/**
	 * The feature id for the '<em><b>Execute Operation</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION__EXECUTE_OPERATION = 4;

	/**
	 * The feature id for the '<em><b>Transition States</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION__TRANSITION_STATES = 5;

	/**
	 * The number of structural features of the '<em>Action</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_FEATURE_COUNT = 6;

	/**
	 * The number of operations of the '<em>Action</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.ActionItemImpl <em>Action Item</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.ActionItemImpl
	 * @see mncModel.impl.MncModelPackageImpl#getActionItem()
	 * @generated
	 */
	int ACTION_ITEM = 27;

	/**
	 * The feature id for the '<em><b>Action Paremeter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_ITEM__ACTION_PAREMETER = 0;

	/**
	 * The number of structural features of the '<em>Action Item</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_ITEM_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Action Item</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_ITEM_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.ActionParemeterImpl <em>Action Paremeter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.ActionParemeterImpl
	 * @see mncModel.impl.MncModelPackageImpl#getActionParemeter()
	 * @generated
	 */
	int ACTION_PAREMETER = 28;

	/**
	 * The feature id for the '<em><b>Parameter Mappings</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_PAREMETER__PARAMETER_MAPPINGS = 0;

	/**
	 * The feature id for the '<em><b>Parameter Values</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_PAREMETER__PARAMETER_VALUES = 1;

	/**
	 * The number of structural features of the '<em>Action Paremeter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_PAREMETER_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Action Paremeter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_PAREMETER_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.ActionCommandImpl <em>Action Command</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.ActionCommandImpl
	 * @see mncModel.impl.MncModelPackageImpl#getActionCommand()
	 * @generated
	 */
	int ACTION_COMMAND = 29;

	/**
	 * The feature id for the '<em><b>Action Paremeter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_COMMAND__ACTION_PAREMETER = ACTION_ITEM__ACTION_PAREMETER;

	/**
	 * The feature id for the '<em><b>Command</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_COMMAND__COMMAND = ACTION_ITEM_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Response Handling</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_COMMAND__RESPONSE_HANDLING = ACTION_ITEM_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Action Command</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_COMMAND_FEATURE_COUNT = ACTION_ITEM_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Action Command</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_COMMAND_OPERATION_COUNT = ACTION_ITEM_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.ActionEventImpl <em>Action Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.ActionEventImpl
	 * @see mncModel.impl.MncModelPackageImpl#getActionEvent()
	 * @generated
	 */
	int ACTION_EVENT = 30;

	/**
	 * The feature id for the '<em><b>Action Paremeter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_EVENT__ACTION_PAREMETER = ACTION_ITEM__ACTION_PAREMETER;

	/**
	 * The feature id for the '<em><b>Event</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_EVENT__EVENT = ACTION_ITEM_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Action Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_EVENT_FEATURE_COUNT = ACTION_ITEM_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Action Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_EVENT_OPERATION_COUNT = ACTION_ITEM_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.ActionAlarmImpl <em>Action Alarm</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.ActionAlarmImpl
	 * @see mncModel.impl.MncModelPackageImpl#getActionAlarm()
	 * @generated
	 */
	int ACTION_ALARM = 31;

	/**
	 * The feature id for the '<em><b>Action Paremeter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_ALARM__ACTION_PAREMETER = ACTION_ITEM__ACTION_PAREMETER;

	/**
	 * The feature id for the '<em><b>Alarm</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_ALARM__ALARM = ACTION_ITEM_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Action Alarm</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_ALARM_FEATURE_COUNT = ACTION_ITEM_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Action Alarm</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_ALARM_OPERATION_COUNT = ACTION_ITEM_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.ActionDataPointImpl <em>Action Data Point</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.ActionDataPointImpl
	 * @see mncModel.impl.MncModelPackageImpl#getActionDataPoint()
	 * @generated
	 */
	int ACTION_DATA_POINT = 32;

	/**
	 * The feature id for the '<em><b>Action Paremeter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_DATA_POINT__ACTION_PAREMETER = ACTION_ITEM__ACTION_PAREMETER;

	/**
	 * The feature id for the '<em><b>Data Point</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_DATA_POINT__DATA_POINT = ACTION_ITEM_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Action Data Point</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_DATA_POINT_FEATURE_COUNT = ACTION_ITEM_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Action Data Point</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_DATA_POINT_OPERATION_COUNT = ACTION_ITEM_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.ActionOperationImpl <em>Action Operation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.ActionOperationImpl
	 * @see mncModel.impl.MncModelPackageImpl#getActionOperation()
	 * @generated
	 */
	int ACTION_OPERATION = 33;

	/**
	 * The feature id for the '<em><b>Action Paremeter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_OPERATION__ACTION_PAREMETER = ACTION_ITEM__ACTION_PAREMETER;

	/**
	 * The feature id for the '<em><b>Operation</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_OPERATION__OPERATION = ACTION_ITEM_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Action Operation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_OPERATION_FEATURE_COUNT = ACTION_ITEM_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Action Operation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_OPERATION_OPERATION_COUNT = ACTION_ITEM_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.ValidationImpl <em>Validation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.ValidationImpl
	 * @see mncModel.impl.MncModelPackageImpl#getValidation()
	 * @generated
	 */
	int VALIDATION = 34;

	/**
	 * The feature id for the '<em><b>On Success Action</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATION__ON_SUCCESS_ACTION = 0;

	/**
	 * The feature id for the '<em><b>On Failed Action</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATION__ON_FAILED_ACTION = 1;

	/**
	 * The feature id for the '<em><b>Parameters Validation Rules</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATION__PARAMETERS_VALIDATION_RULES = 2;

	/**
	 * The number of structural features of the '<em>Validation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATION_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Validation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATION_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link mncModel.impl.DataValueImpl <em>Data Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.impl.DataValueImpl
	 * @see mncModel.impl.MncModelPackageImpl#getDataValue()
	 * @generated
	 */
	int DATA_VALUE = 35;

	/**
	 * The number of structural features of the '<em>Data Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_VALUE_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>Data Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_VALUE_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link mncModel.AbstractOutcomeItems <em>Abstract Outcome Items</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Abstract Outcome Items</em>'.
	 * @see mncModel.AbstractOutcomeItems
	 * @generated
	 */
	EClass getAbstractOutcomeItems();

	/**
	 * Returns the meta object for class '{@link mncModel.System <em>System</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>System</em>'.
	 * @see mncModel.System
	 * @generated
	 */
	EClass getSystem();

	/**
	 * Returns the meta object for class '{@link mncModel.BehaviorBlock <em>Behavior Block</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Behavior Block</em>'.
	 * @see mncModel.BehaviorBlock
	 * @generated
	 */
	EClass getBehaviorBlock();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.BehaviorBlock#getValidationRules <em>Validation Rules</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Validation Rules</em>'.
	 * @see mncModel.BehaviorBlock#getValidationRules()
	 * @see #getBehaviorBlock()
	 * @generated
	 */
	EReference getBehaviorBlock_ValidationRules();

	/**
	 * Returns the meta object for the containment reference '{@link mncModel.BehaviorBlock#getAction <em>Action</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Action</em>'.
	 * @see mncModel.BehaviorBlock#getAction()
	 * @see #getBehaviorBlock()
	 * @generated
	 */
	EReference getBehaviorBlock_Action();

	/**
	 * Returns the meta object for class '{@link mncModel.AbstractInterfaceItems <em>Abstract Interface Items</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Abstract Interface Items</em>'.
	 * @see mncModel.AbstractInterfaceItems
	 * @generated
	 */
	EClass getAbstractInterfaceItems();

	/**
	 * Returns the meta object for the attribute '{@link mncModel.AbstractInterfaceItems#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see mncModel.AbstractInterfaceItems#getName()
	 * @see #getAbstractInterfaceItems()
	 * @generated
	 */
	EAttribute getAbstractInterfaceItems_Name();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.AbstractInterfaceItems#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameters</em>'.
	 * @see mncModel.AbstractInterfaceItems#getParameters()
	 * @see #getAbstractInterfaceItems()
	 * @generated
	 */
	EReference getAbstractInterfaceItems_Parameters();

	/**
	 * Returns the meta object for class '{@link mncModel.Model <em>Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Model</em>'.
	 * @see mncModel.Model
	 * @generated
	 */
	EClass getModel();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.Model#getSystems <em>Systems</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Systems</em>'.
	 * @see mncModel.Model#getSystems()
	 * @see #getModel()
	 * @generated
	 */
	EReference getModel_Systems();

	/**
	 * Returns the meta object for the attribute '{@link mncModel.Model#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see mncModel.Model#getName()
	 * @see #getModel()
	 * @generated
	 */
	EAttribute getModel_Name();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.Model#getImportSection <em>Import Section</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Import Section</em>'.
	 * @see mncModel.Model#getImportSection()
	 * @see #getModel()
	 * @generated
	 */
	EReference getModel_ImportSection();

	/**
	 * Returns the meta object for class '{@link mncModel.Import <em>Import</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Import</em>'.
	 * @see mncModel.Import
	 * @generated
	 */
	EClass getImport();

	/**
	 * Returns the meta object for the attribute '{@link mncModel.Import#getImportedNamespace <em>Imported Namespace</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Imported Namespace</em>'.
	 * @see mncModel.Import#getImportedNamespace()
	 * @see #getImport()
	 * @generated
	 */
	EAttribute getImport_ImportedNamespace();

	/**
	 * Returns the meta object for class '{@link mncModel.InterfaceDescription <em>Interface Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Interface Description</em>'.
	 * @see mncModel.InterfaceDescription
	 * @generated
	 */
	EClass getInterfaceDescription();

	/**
	 * Returns the meta object for the attribute '{@link mncModel.InterfaceDescription#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see mncModel.InterfaceDescription#getName()
	 * @see #getInterfaceDescription()
	 * @generated
	 */
	EAttribute getInterfaceDescription_Name();

	/**
	 * Returns the meta object for the containment reference '{@link mncModel.InterfaceDescription#getIpaddress <em>Ipaddress</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Ipaddress</em>'.
	 * @see mncModel.InterfaceDescription#getIpaddress()
	 * @see #getInterfaceDescription()
	 * @generated
	 */
	EReference getInterfaceDescription_Ipaddress();

	/**
	 * Returns the meta object for the containment reference '{@link mncModel.InterfaceDescription#getPort <em>Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Port</em>'.
	 * @see mncModel.InterfaceDescription#getPort()
	 * @see #getInterfaceDescription()
	 * @generated
	 */
	EReference getInterfaceDescription_Port();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.InterfaceDescription#getDataPoints <em>Data Points</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Data Points</em>'.
	 * @see mncModel.InterfaceDescription#getDataPoints()
	 * @see #getInterfaceDescription()
	 * @generated
	 */
	EReference getInterfaceDescription_DataPoints();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.InterfaceDescription#getAlarms <em>Alarms</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Alarms</em>'.
	 * @see mncModel.InterfaceDescription#getAlarms()
	 * @see #getInterfaceDescription()
	 * @generated
	 */
	EReference getInterfaceDescription_Alarms();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.InterfaceDescription#getCommands <em>Commands</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Commands</em>'.
	 * @see mncModel.InterfaceDescription#getCommands()
	 * @see #getInterfaceDescription()
	 * @generated
	 */
	EReference getInterfaceDescription_Commands();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.InterfaceDescription#getEvents <em>Events</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Events</em>'.
	 * @see mncModel.InterfaceDescription#getEvents()
	 * @see #getInterfaceDescription()
	 * @generated
	 */
	EReference getInterfaceDescription_Events();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.InterfaceDescription#getResponses <em>Responses</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Responses</em>'.
	 * @see mncModel.InterfaceDescription#getResponses()
	 * @see #getInterfaceDescription()
	 * @generated
	 */
	EReference getInterfaceDescription_Responses();

	/**
	 * Returns the meta object for the containment reference '{@link mncModel.InterfaceDescription#getOperatingStatesUtility <em>Operating States Utility</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Operating States Utility</em>'.
	 * @see mncModel.InterfaceDescription#getOperatingStatesUtility()
	 * @see #getInterfaceDescription()
	 * @generated
	 */
	EReference getInterfaceDescription_OperatingStatesUtility();

	/**
	 * Returns the meta object for the containment reference '{@link mncModel.InterfaceDescription#getSubscribedItems <em>Subscribed Items</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Subscribed Items</em>'.
	 * @see mncModel.InterfaceDescription#getSubscribedItems()
	 * @see #getInterfaceDescription()
	 * @generated
	 */
	EReference getInterfaceDescription_SubscribedItems();

	/**
	 * Returns the meta object for the reference list '{@link mncModel.InterfaceDescription#getUses <em>Uses</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Uses</em>'.
	 * @see mncModel.InterfaceDescription#getUses()
	 * @see #getInterfaceDescription()
	 * @generated
	 */
	EReference getInterfaceDescription_Uses();

	/**
	 * Returns the meta object for class '{@link mncModel.Port <em>Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Port</em>'.
	 * @see mncModel.Port
	 * @generated
	 */
	EClass getPort();

	/**
	 * Returns the meta object for the attribute '{@link mncModel.Port#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see mncModel.Port#getName()
	 * @see #getPort()
	 * @generated
	 */
	EAttribute getPort_Name();

	/**
	 * Returns the meta object for the attribute '{@link mncModel.Port#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see mncModel.Port#getValue()
	 * @see #getPort()
	 * @generated
	 */
	EAttribute getPort_Value();

	/**
	 * Returns the meta object for class '{@link mncModel.Address <em>Address</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Address</em>'.
	 * @see mncModel.Address
	 * @generated
	 */
	EClass getAddress();

	/**
	 * Returns the meta object for the attribute '{@link mncModel.Address#getIpaddress <em>Ipaddress</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ipaddress</em>'.
	 * @see mncModel.Address#getIpaddress()
	 * @see #getAddress()
	 * @generated
	 */
	EAttribute getAddress_Ipaddress();

	/**
	 * Returns the meta object for class '{@link mncModel.Command <em>Command</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Command</em>'.
	 * @see mncModel.Command
	 * @generated
	 */
	EClass getCommand();

	/**
	 * Returns the meta object for the attribute '{@link mncModel.Command#isAsynch <em>Asynch</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Asynch</em>'.
	 * @see mncModel.Command#isAsynch()
	 * @see #getCommand()
	 * @generated
	 */
	EAttribute getCommand_Asynch();

	/**
	 * Returns the meta object for class '{@link mncModel.OperatingState <em>Operating State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Operating State</em>'.
	 * @see mncModel.OperatingState
	 * @generated
	 */
	EClass getOperatingState();

	/**
	 * Returns the meta object for the attribute '{@link mncModel.OperatingState#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see mncModel.OperatingState#getName()
	 * @see #getOperatingState()
	 * @generated
	 */
	EAttribute getOperatingState_Name();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.OperatingState#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameters</em>'.
	 * @see mncModel.OperatingState#getParameters()
	 * @see #getOperatingState()
	 * @generated
	 */
	EReference getOperatingState_Parameters();

	/**
	 * Returns the meta object for class '{@link mncModel.Response <em>Response</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Response</em>'.
	 * @see mncModel.Response
	 * @generated
	 */
	EClass getResponse();

	/**
	 * Returns the meta object for class '{@link mncModel.Event <em>Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Event</em>'.
	 * @see mncModel.Event
	 * @generated
	 */
	EClass getEvent();

	/**
	 * Returns the meta object for the attribute '{@link mncModel.Event#isPublish <em>Publish</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Publish</em>'.
	 * @see mncModel.Event#isPublish()
	 * @see #getEvent()
	 * @generated
	 */
	EAttribute getEvent_Publish();

	/**
	 * Returns the meta object for class '{@link mncModel.Alarm <em>Alarm</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Alarm</em>'.
	 * @see mncModel.Alarm
	 * @generated
	 */
	EClass getAlarm();

	/**
	 * Returns the meta object for the attribute '{@link mncModel.Alarm#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see mncModel.Alarm#getType()
	 * @see #getAlarm()
	 * @generated
	 */
	EAttribute getAlarm_Type();

	/**
	 * Returns the meta object for the attribute '{@link mncModel.Alarm#getLevel <em>Level</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Level</em>'.
	 * @see mncModel.Alarm#getLevel()
	 * @see #getAlarm()
	 * @generated
	 */
	EAttribute getAlarm_Level();

	/**
	 * Returns the meta object for the attribute '{@link mncModel.Alarm#isPublish <em>Publish</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Publish</em>'.
	 * @see mncModel.Alarm#isPublish()
	 * @see #getAlarm()
	 * @generated
	 */
	EAttribute getAlarm_Publish();

	/**
	 * Returns the meta object for class '{@link mncModel.DataPoint <em>Data Point</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Data Point</em>'.
	 * @see mncModel.DataPoint
	 * @generated
	 */
	EClass getDataPoint();

	/**
	 * Returns the meta object for the attribute '{@link mncModel.DataPoint#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see mncModel.DataPoint#getType()
	 * @see #getDataPoint()
	 * @generated
	 */
	EAttribute getDataPoint_Type();

	/**
	 * Returns the meta object for the containment reference '{@link mncModel.DataPoint#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see mncModel.DataPoint#getValue()
	 * @see #getDataPoint()
	 * @generated
	 */
	EReference getDataPoint_Value();

	/**
	 * Returns the meta object for the attribute '{@link mncModel.DataPoint#isPublish <em>Publish</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Publish</em>'.
	 * @see mncModel.DataPoint#isPublish()
	 * @see #getDataPoint()
	 * @generated
	 */
	EAttribute getDataPoint_Publish();

	/**
	 * Returns the meta object for class '{@link mncModel.SubscribableItemList <em>Subscribable Item List</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Subscribable Item List</em>'.
	 * @see mncModel.SubscribableItemList
	 * @generated
	 */
	EClass getSubscribableItemList();

	/**
	 * Returns the meta object for the reference list '{@link mncModel.SubscribableItemList#getSubscribedEvents <em>Subscribed Events</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Subscribed Events</em>'.
	 * @see mncModel.SubscribableItemList#getSubscribedEvents()
	 * @see #getSubscribableItemList()
	 * @generated
	 */
	EReference getSubscribableItemList_SubscribedEvents();

	/**
	 * Returns the meta object for the reference list '{@link mncModel.SubscribableItemList#getSubscribedAlarms <em>Subscribed Alarms</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Subscribed Alarms</em>'.
	 * @see mncModel.SubscribableItemList#getSubscribedAlarms()
	 * @see #getSubscribableItemList()
	 * @generated
	 */
	EReference getSubscribableItemList_SubscribedAlarms();

	/**
	 * Returns the meta object for the reference list '{@link mncModel.SubscribableItemList#getSubscribedDataPoints <em>Subscribed Data Points</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Subscribed Data Points</em>'.
	 * @see mncModel.SubscribableItemList#getSubscribedDataPoints()
	 * @see #getSubscribableItemList()
	 * @generated
	 */
	EReference getSubscribableItemList_SubscribedDataPoints();

	/**
	 * Returns the meta object for class '{@link mncModel.ControlNode <em>Control Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Control Node</em>'.
	 * @see mncModel.ControlNode
	 * @generated
	 */
	EClass getControlNode();

	/**
	 * Returns the meta object for the attribute '{@link mncModel.ControlNode#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see mncModel.ControlNode#getName()
	 * @see #getControlNode()
	 * @generated
	 */
	EAttribute getControlNode_Name();

	/**
	 * Returns the meta object for the reference '{@link mncModel.ControlNode#getInterfaceDescription <em>Interface Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Interface Description</em>'.
	 * @see mncModel.ControlNode#getInterfaceDescription()
	 * @see #getControlNode()
	 * @generated
	 */
	EReference getControlNode_InterfaceDescription();

	/**
	 * Returns the meta object for the reference list '{@link mncModel.ControlNode#getChildNodes <em>Child Nodes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Child Nodes</em>'.
	 * @see mncModel.ControlNode#getChildNodes()
	 * @see #getControlNode()
	 * @generated
	 */
	EReference getControlNode_ChildNodes();

	/**
	 * Returns the meta object for the reference '{@link mncModel.ControlNode#getParentNode <em>Parent Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Parent Node</em>'.
	 * @see mncModel.ControlNode#getParentNode()
	 * @see #getControlNode()
	 * @generated
	 */
	EReference getControlNode_ParentNode();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.ControlNode#getCommandResponseBlocks <em>Command Response Blocks</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Command Response Blocks</em>'.
	 * @see mncModel.ControlNode#getCommandResponseBlocks()
	 * @see #getControlNode()
	 * @generated
	 */
	EReference getControlNode_CommandResponseBlocks();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.ControlNode#getEventBlocks <em>Event Blocks</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Event Blocks</em>'.
	 * @see mncModel.ControlNode#getEventBlocks()
	 * @see #getControlNode()
	 * @generated
	 */
	EReference getControlNode_EventBlocks();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.ControlNode#getAlarmBlocks <em>Alarm Blocks</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Alarm Blocks</em>'.
	 * @see mncModel.ControlNode#getAlarmBlocks()
	 * @see #getControlNode()
	 * @generated
	 */
	EReference getControlNode_AlarmBlocks();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.ControlNode#getDataPointBlocks <em>Data Point Blocks</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Data Point Blocks</em>'.
	 * @see mncModel.ControlNode#getDataPointBlocks()
	 * @see #getControlNode()
	 * @generated
	 */
	EReference getControlNode_DataPointBlocks();

	/**
	 * Returns the meta object for class '{@link mncModel.CommandResponseBlock <em>Command Response Block</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Command Response Block</em>'.
	 * @see mncModel.CommandResponseBlock
	 * @generated
	 */
	EClass getCommandResponseBlock();

	/**
	 * Returns the meta object for the reference '{@link mncModel.CommandResponseBlock#getCommand <em>Command</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Command</em>'.
	 * @see mncModel.CommandResponseBlock#getCommand()
	 * @see #getCommandResponseBlock()
	 * @generated
	 */
	EReference getCommandResponseBlock_Command();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.CommandResponseBlock#getResponseBlock <em>Response Block</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Response Block</em>'.
	 * @see mncModel.CommandResponseBlock#getResponseBlock()
	 * @see #getCommandResponseBlock()
	 * @generated
	 */
	EReference getCommandResponseBlock_ResponseBlock();

	/**
	 * Returns the meta object for class '{@link mncModel.ResponseBlock <em>Response Block</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Response Block</em>'.
	 * @see mncModel.ResponseBlock
	 * @generated
	 */
	EClass getResponseBlock();

	/**
	 * Returns the meta object for the reference '{@link mncModel.ResponseBlock#getResponse <em>Response</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Response</em>'.
	 * @see mncModel.ResponseBlock#getResponse()
	 * @see #getResponseBlock()
	 * @generated
	 */
	EReference getResponseBlock_Response();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.ResponseBlock#getResponseAggregationRules <em>Response Aggregation Rules</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Response Aggregation Rules</em>'.
	 * @see mncModel.ResponseBlock#getResponseAggregationRules()
	 * @see #getResponseBlock()
	 * @generated
	 */
	EReference getResponseBlock_ResponseAggregationRules();

	/**
	 * Returns the meta object for class '{@link mncModel.ResponseAggregationRule <em>Response Aggregation Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Response Aggregation Rule</em>'.
	 * @see mncModel.ResponseAggregationRule
	 * @generated
	 */
	EClass getResponseAggregationRule();

	/**
	 * Returns the meta object for the reference list '{@link mncModel.ResponseAggregationRule#getInputResponses <em>Input Responses</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Input Responses</em>'.
	 * @see mncModel.ResponseAggregationRule#getInputResponses()
	 * @see #getResponseAggregationRule()
	 * @generated
	 */
	EReference getResponseAggregationRule_InputResponses();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.ResponseAggregationRule#getParameterTranslations <em>Parameter Translations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameter Translations</em>'.
	 * @see mncModel.ResponseAggregationRule#getParameterTranslations()
	 * @see #getResponseAggregationRule()
	 * @generated
	 */
	EReference getResponseAggregationRule_ParameterTranslations();

	/**
	 * Returns the meta object for class '{@link mncModel.ParameterTranslation <em>Parameter Translation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter Translation</em>'.
	 * @see mncModel.ParameterTranslation
	 * @generated
	 */
	EClass getParameterTranslation();

	/**
	 * Returns the meta object for the reference list '{@link mncModel.ParameterTranslation#getInputParameters <em>Input Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Input Parameters</em>'.
	 * @see mncModel.ParameterTranslation#getInputParameters()
	 * @see #getParameterTranslation()
	 * @generated
	 */
	EReference getParameterTranslation_InputParameters();

	/**
	 * Returns the meta object for the reference '{@link mncModel.ParameterTranslation#getTransformationFunction <em>Transformation Function</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Transformation Function</em>'.
	 * @see mncModel.ParameterTranslation#getTransformationFunction()
	 * @see #getParameterTranslation()
	 * @generated
	 */
	EReference getParameterTranslation_TransformationFunction();

	/**
	 * Returns the meta object for the reference '{@link mncModel.ParameterTranslation#getTranslatedParameters <em>Translated Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Translated Parameters</em>'.
	 * @see mncModel.ParameterTranslation#getTranslatedParameters()
	 * @see #getParameterTranslation()
	 * @generated
	 */
	EReference getParameterTranslation_TranslatedParameters();

	/**
	 * Returns the meta object for class '{@link mncModel.EventBlock <em>Event Block</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Event Block</em>'.
	 * @see mncModel.EventBlock
	 * @generated
	 */
	EClass getEventBlock();

	/**
	 * Returns the meta object for the reference '{@link mncModel.EventBlock#getEvent <em>Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Event</em>'.
	 * @see mncModel.EventBlock#getEvent()
	 * @see #getEventBlock()
	 * @generated
	 */
	EReference getEventBlock_Event();

	/**
	 * Returns the meta object for class '{@link mncModel.AlarmBlock <em>Alarm Block</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Alarm Block</em>'.
	 * @see mncModel.AlarmBlock
	 * @generated
	 */
	EClass getAlarmBlock();

	/**
	 * Returns the meta object for the reference '{@link mncModel.AlarmBlock#getAlarm <em>Alarm</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Alarm</em>'.
	 * @see mncModel.AlarmBlock#getAlarm()
	 * @see #getAlarmBlock()
	 * @generated
	 */
	EReference getAlarmBlock_Alarm();

	/**
	 * Returns the meta object for class '{@link mncModel.DataPointBlock <em>Data Point Block</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Data Point Block</em>'.
	 * @see mncModel.DataPointBlock
	 * @generated
	 */
	EClass getDataPointBlock();

	/**
	 * Returns the meta object for the reference list '{@link mncModel.DataPointBlock#getDataPoint <em>Data Point</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Data Point</em>'.
	 * @see mncModel.DataPointBlock#getDataPoint()
	 * @see #getDataPointBlock()
	 * @generated
	 */
	EReference getDataPointBlock_DataPoint();

	/**
	 * Returns the meta object for class '{@link mncModel.CheckParameterCondition <em>Check Parameter Condition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Check Parameter Condition</em>'.
	 * @see mncModel.CheckParameterCondition
	 * @generated
	 */
	EClass getCheckParameterCondition();

	/**
	 * Returns the meta object for the reference list '{@link mncModel.CheckParameterCondition#getParameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Parameter</em>'.
	 * @see mncModel.CheckParameterCondition#getParameter()
	 * @see #getCheckParameterCondition()
	 * @generated
	 */
	EReference getCheckParameterCondition_Parameter();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.CheckParameterCondition#getCheckValues <em>Check Values</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Check Values</em>'.
	 * @see mncModel.CheckParameterCondition#getCheckValues()
	 * @see #getCheckParameterCondition()
	 * @generated
	 */
	EReference getCheckParameterCondition_CheckValues();

	/**
	 * Returns the meta object for the containment reference '{@link mncModel.CheckParameterCondition#getCheckMaxValue <em>Check Max Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Check Max Value</em>'.
	 * @see mncModel.CheckParameterCondition#getCheckMaxValue()
	 * @see #getCheckParameterCondition()
	 * @generated
	 */
	EReference getCheckParameterCondition_CheckMaxValue();

	/**
	 * Returns the meta object for the containment reference '{@link mncModel.CheckParameterCondition#getCheckMinValue <em>Check Min Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Check Min Value</em>'.
	 * @see mncModel.CheckParameterCondition#getCheckMinValue()
	 * @see #getCheckParameterCondition()
	 * @generated
	 */
	EReference getCheckParameterCondition_CheckMinValue();

	/**
	 * Returns the meta object for the reference list '{@link mncModel.CheckParameterCondition#getOperations <em>Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Operations</em>'.
	 * @see mncModel.CheckParameterCondition#getOperations()
	 * @see #getCheckParameterCondition()
	 * @generated
	 */
	EReference getCheckParameterCondition_Operations();

	/**
	 * Returns the meta object for class '{@link mncModel.Transition <em>Transition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Transition</em>'.
	 * @see mncModel.Transition
	 * @generated
	 */
	EClass getTransition();

	/**
	 * Returns the meta object for the reference list '{@link mncModel.Transition#getCurrentState <em>Current State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Current State</em>'.
	 * @see mncModel.Transition#getCurrentState()
	 * @see #getTransition()
	 * @generated
	 */
	EReference getTransition_CurrentState();

	/**
	 * Returns the meta object for the reference '{@link mncModel.Transition#getNextState <em>Next State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Next State</em>'.
	 * @see mncModel.Transition#getNextState()
	 * @see #getTransition()
	 * @generated
	 */
	EReference getTransition_NextState();

	/**
	 * Returns the meta object for the containment reference '{@link mncModel.Transition#getEntryAction <em>Entry Action</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Entry Action</em>'.
	 * @see mncModel.Transition#getEntryAction()
	 * @see #getTransition()
	 * @generated
	 */
	EReference getTransition_EntryAction();

	/**
	 * Returns the meta object for the containment reference '{@link mncModel.Transition#getExitAction <em>Exit Action</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Exit Action</em>'.
	 * @see mncModel.Transition#getExitAction()
	 * @see #getTransition()
	 * @generated
	 */
	EReference getTransition_ExitAction();

	/**
	 * Returns the meta object for class '{@link mncModel.Action <em>Action</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Action</em>'.
	 * @see mncModel.Action
	 * @generated
	 */
	EClass getAction();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.Action#getFireCommand <em>Fire Command</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Fire Command</em>'.
	 * @see mncModel.Action#getFireCommand()
	 * @see #getAction()
	 * @generated
	 */
	EReference getAction_FireCommand();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.Action#getPublishEvent <em>Publish Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Publish Event</em>'.
	 * @see mncModel.Action#getPublishEvent()
	 * @see #getAction()
	 * @generated
	 */
	EReference getAction_PublishEvent();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.Action#getRaiseAlarm <em>Raise Alarm</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Raise Alarm</em>'.
	 * @see mncModel.Action#getRaiseAlarm()
	 * @see #getAction()
	 * @generated
	 */
	EReference getAction_RaiseAlarm();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.Action#getTriggerDataPoint <em>Trigger Data Point</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Trigger Data Point</em>'.
	 * @see mncModel.Action#getTriggerDataPoint()
	 * @see #getAction()
	 * @generated
	 */
	EReference getAction_TriggerDataPoint();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.Action#getExecuteOperation <em>Execute Operation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Execute Operation</em>'.
	 * @see mncModel.Action#getExecuteOperation()
	 * @see #getAction()
	 * @generated
	 */
	EReference getAction_ExecuteOperation();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.Action#getTransitionStates <em>Transition States</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Transition States</em>'.
	 * @see mncModel.Action#getTransitionStates()
	 * @see #getAction()
	 * @generated
	 */
	EReference getAction_TransitionStates();

	/**
	 * Returns the meta object for class '{@link mncModel.ActionItem <em>Action Item</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Action Item</em>'.
	 * @see mncModel.ActionItem
	 * @generated
	 */
	EClass getActionItem();

	/**
	 * Returns the meta object for the containment reference '{@link mncModel.ActionItem#getActionParemeter <em>Action Paremeter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Action Paremeter</em>'.
	 * @see mncModel.ActionItem#getActionParemeter()
	 * @see #getActionItem()
	 * @generated
	 */
	EReference getActionItem_ActionParemeter();

	/**
	 * Returns the meta object for class '{@link mncModel.ActionParemeter <em>Action Paremeter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Action Paremeter</em>'.
	 * @see mncModel.ActionParemeter
	 * @generated
	 */
	EClass getActionParemeter();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.ActionParemeter#getParameterMappings <em>Parameter Mappings</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameter Mappings</em>'.
	 * @see mncModel.ActionParemeter#getParameterMappings()
	 * @see #getActionParemeter()
	 * @generated
	 */
	EReference getActionParemeter_ParameterMappings();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.ActionParemeter#getParameterValues <em>Parameter Values</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameter Values</em>'.
	 * @see mncModel.ActionParemeter#getParameterValues()
	 * @see #getActionParemeter()
	 * @generated
	 */
	EReference getActionParemeter_ParameterValues();

	/**
	 * Returns the meta object for class '{@link mncModel.ActionCommand <em>Action Command</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Action Command</em>'.
	 * @see mncModel.ActionCommand
	 * @generated
	 */
	EClass getActionCommand();

	/**
	 * Returns the meta object for the reference '{@link mncModel.ActionCommand#getCommand <em>Command</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Command</em>'.
	 * @see mncModel.ActionCommand#getCommand()
	 * @see #getActionCommand()
	 * @generated
	 */
	EReference getActionCommand_Command();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.ActionCommand#getResponseHandling <em>Response Handling</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Response Handling</em>'.
	 * @see mncModel.ActionCommand#getResponseHandling()
	 * @see #getActionCommand()
	 * @generated
	 */
	EReference getActionCommand_ResponseHandling();

	/**
	 * Returns the meta object for class '{@link mncModel.ActionEvent <em>Action Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Action Event</em>'.
	 * @see mncModel.ActionEvent
	 * @generated
	 */
	EClass getActionEvent();

	/**
	 * Returns the meta object for the reference '{@link mncModel.ActionEvent#getEvent <em>Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Event</em>'.
	 * @see mncModel.ActionEvent#getEvent()
	 * @see #getActionEvent()
	 * @generated
	 */
	EReference getActionEvent_Event();

	/**
	 * Returns the meta object for class '{@link mncModel.ActionAlarm <em>Action Alarm</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Action Alarm</em>'.
	 * @see mncModel.ActionAlarm
	 * @generated
	 */
	EClass getActionAlarm();

	/**
	 * Returns the meta object for the reference '{@link mncModel.ActionAlarm#getAlarm <em>Alarm</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Alarm</em>'.
	 * @see mncModel.ActionAlarm#getAlarm()
	 * @see #getActionAlarm()
	 * @generated
	 */
	EReference getActionAlarm_Alarm();

	/**
	 * Returns the meta object for class '{@link mncModel.ActionDataPoint <em>Action Data Point</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Action Data Point</em>'.
	 * @see mncModel.ActionDataPoint
	 * @generated
	 */
	EClass getActionDataPoint();

	/**
	 * Returns the meta object for the reference '{@link mncModel.ActionDataPoint#getDataPoint <em>Data Point</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Data Point</em>'.
	 * @see mncModel.ActionDataPoint#getDataPoint()
	 * @see #getActionDataPoint()
	 * @generated
	 */
	EReference getActionDataPoint_DataPoint();

	/**
	 * Returns the meta object for class '{@link mncModel.ActionOperation <em>Action Operation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Action Operation</em>'.
	 * @see mncModel.ActionOperation
	 * @generated
	 */
	EClass getActionOperation();

	/**
	 * Returns the meta object for the reference '{@link mncModel.ActionOperation#getOperation <em>Operation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Operation</em>'.
	 * @see mncModel.ActionOperation#getOperation()
	 * @see #getActionOperation()
	 * @generated
	 */
	EReference getActionOperation_Operation();

	/**
	 * Returns the meta object for class '{@link mncModel.Validation <em>Validation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Validation</em>'.
	 * @see mncModel.Validation
	 * @generated
	 */
	EClass getValidation();

	/**
	 * Returns the meta object for the containment reference '{@link mncModel.Validation#getOnSuccessAction <em>On Success Action</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>On Success Action</em>'.
	 * @see mncModel.Validation#getOnSuccessAction()
	 * @see #getValidation()
	 * @generated
	 */
	EReference getValidation_OnSuccessAction();

	/**
	 * Returns the meta object for the containment reference '{@link mncModel.Validation#getOnFailedAction <em>On Failed Action</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>On Failed Action</em>'.
	 * @see mncModel.Validation#getOnFailedAction()
	 * @see #getValidation()
	 * @generated
	 */
	EReference getValidation_OnFailedAction();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.Validation#getParametersValidationRules <em>Parameters Validation Rules</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameters Validation Rules</em>'.
	 * @see mncModel.Validation#getParametersValidationRules()
	 * @see #getValidation()
	 * @generated
	 */
	EReference getValidation_ParametersValidationRules();

	/**
	 * Returns the meta object for class '{@link mncModel.DataValue <em>Data Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Data Value</em>'.
	 * @see mncModel.DataValue
	 * @generated
	 */
	EClass getDataValue();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	MncModelFactory getMncModelFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link mncModel.impl.AbstractOutcomeItemsImpl <em>Abstract Outcome Items</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.AbstractOutcomeItemsImpl
		 * @see mncModel.impl.MncModelPackageImpl#getAbstractOutcomeItems()
		 * @generated
		 */
		EClass ABSTRACT_OUTCOME_ITEMS = eINSTANCE.getAbstractOutcomeItems();

		/**
		 * The meta object literal for the '{@link mncModel.impl.SystemImpl <em>System</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.SystemImpl
		 * @see mncModel.impl.MncModelPackageImpl#getSystem()
		 * @generated
		 */
		EClass SYSTEM = eINSTANCE.getSystem();

		/**
		 * The meta object literal for the '{@link mncModel.impl.BehaviorBlockImpl <em>Behavior Block</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.BehaviorBlockImpl
		 * @see mncModel.impl.MncModelPackageImpl#getBehaviorBlock()
		 * @generated
		 */
		EClass BEHAVIOR_BLOCK = eINSTANCE.getBehaviorBlock();

		/**
		 * The meta object literal for the '<em><b>Validation Rules</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BEHAVIOR_BLOCK__VALIDATION_RULES = eINSTANCE.getBehaviorBlock_ValidationRules();

		/**
		 * The meta object literal for the '<em><b>Action</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BEHAVIOR_BLOCK__ACTION = eINSTANCE.getBehaviorBlock_Action();

		/**
		 * The meta object literal for the '{@link mncModel.impl.AbstractInterfaceItemsImpl <em>Abstract Interface Items</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.AbstractInterfaceItemsImpl
		 * @see mncModel.impl.MncModelPackageImpl#getAbstractInterfaceItems()
		 * @generated
		 */
		EClass ABSTRACT_INTERFACE_ITEMS = eINSTANCE.getAbstractInterfaceItems();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ABSTRACT_INTERFACE_ITEMS__NAME = eINSTANCE.getAbstractInterfaceItems_Name();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ABSTRACT_INTERFACE_ITEMS__PARAMETERS = eINSTANCE.getAbstractInterfaceItems_Parameters();

		/**
		 * The meta object literal for the '{@link mncModel.impl.ModelImpl <em>Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.ModelImpl
		 * @see mncModel.impl.MncModelPackageImpl#getModel()
		 * @generated
		 */
		EClass MODEL = eINSTANCE.getModel();

		/**
		 * The meta object literal for the '<em><b>Systems</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MODEL__SYSTEMS = eINSTANCE.getModel_Systems();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODEL__NAME = eINSTANCE.getModel_Name();

		/**
		 * The meta object literal for the '<em><b>Import Section</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MODEL__IMPORT_SECTION = eINSTANCE.getModel_ImportSection();

		/**
		 * The meta object literal for the '{@link mncModel.impl.ImportImpl <em>Import</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.ImportImpl
		 * @see mncModel.impl.MncModelPackageImpl#getImport()
		 * @generated
		 */
		EClass IMPORT = eINSTANCE.getImport();

		/**
		 * The meta object literal for the '<em><b>Imported Namespace</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMPORT__IMPORTED_NAMESPACE = eINSTANCE.getImport_ImportedNamespace();

		/**
		 * The meta object literal for the '{@link mncModel.impl.InterfaceDescriptionImpl <em>Interface Description</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.InterfaceDescriptionImpl
		 * @see mncModel.impl.MncModelPackageImpl#getInterfaceDescription()
		 * @generated
		 */
		EClass INTERFACE_DESCRIPTION = eINSTANCE.getInterfaceDescription();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INTERFACE_DESCRIPTION__NAME = eINSTANCE.getInterfaceDescription_Name();

		/**
		 * The meta object literal for the '<em><b>Ipaddress</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTERFACE_DESCRIPTION__IPADDRESS = eINSTANCE.getInterfaceDescription_Ipaddress();

		/**
		 * The meta object literal for the '<em><b>Port</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTERFACE_DESCRIPTION__PORT = eINSTANCE.getInterfaceDescription_Port();

		/**
		 * The meta object literal for the '<em><b>Data Points</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTERFACE_DESCRIPTION__DATA_POINTS = eINSTANCE.getInterfaceDescription_DataPoints();

		/**
		 * The meta object literal for the '<em><b>Alarms</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTERFACE_DESCRIPTION__ALARMS = eINSTANCE.getInterfaceDescription_Alarms();

		/**
		 * The meta object literal for the '<em><b>Commands</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTERFACE_DESCRIPTION__COMMANDS = eINSTANCE.getInterfaceDescription_Commands();

		/**
		 * The meta object literal for the '<em><b>Events</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTERFACE_DESCRIPTION__EVENTS = eINSTANCE.getInterfaceDescription_Events();

		/**
		 * The meta object literal for the '<em><b>Responses</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTERFACE_DESCRIPTION__RESPONSES = eINSTANCE.getInterfaceDescription_Responses();

		/**
		 * The meta object literal for the '<em><b>Operating States Utility</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTERFACE_DESCRIPTION__OPERATING_STATES_UTILITY = eINSTANCE.getInterfaceDescription_OperatingStatesUtility();

		/**
		 * The meta object literal for the '<em><b>Subscribed Items</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTERFACE_DESCRIPTION__SUBSCRIBED_ITEMS = eINSTANCE.getInterfaceDescription_SubscribedItems();

		/**
		 * The meta object literal for the '<em><b>Uses</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTERFACE_DESCRIPTION__USES = eINSTANCE.getInterfaceDescription_Uses();

		/**
		 * The meta object literal for the '{@link mncModel.impl.PortImpl <em>Port</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.PortImpl
		 * @see mncModel.impl.MncModelPackageImpl#getPort()
		 * @generated
		 */
		EClass PORT = eINSTANCE.getPort();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PORT__NAME = eINSTANCE.getPort_Name();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PORT__VALUE = eINSTANCE.getPort_Value();

		/**
		 * The meta object literal for the '{@link mncModel.impl.AddressImpl <em>Address</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.AddressImpl
		 * @see mncModel.impl.MncModelPackageImpl#getAddress()
		 * @generated
		 */
		EClass ADDRESS = eINSTANCE.getAddress();

		/**
		 * The meta object literal for the '<em><b>Ipaddress</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ADDRESS__IPADDRESS = eINSTANCE.getAddress_Ipaddress();

		/**
		 * The meta object literal for the '{@link mncModel.impl.CommandImpl <em>Command</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.CommandImpl
		 * @see mncModel.impl.MncModelPackageImpl#getCommand()
		 * @generated
		 */
		EClass COMMAND = eINSTANCE.getCommand();

		/**
		 * The meta object literal for the '<em><b>Asynch</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COMMAND__ASYNCH = eINSTANCE.getCommand_Asynch();

		/**
		 * The meta object literal for the '{@link mncModel.impl.OperatingStateImpl <em>Operating State</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.OperatingStateImpl
		 * @see mncModel.impl.MncModelPackageImpl#getOperatingState()
		 * @generated
		 */
		EClass OPERATING_STATE = eINSTANCE.getOperatingState();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OPERATING_STATE__NAME = eINSTANCE.getOperatingState_Name();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPERATING_STATE__PARAMETERS = eINSTANCE.getOperatingState_Parameters();

		/**
		 * The meta object literal for the '{@link mncModel.impl.ResponseImpl <em>Response</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.ResponseImpl
		 * @see mncModel.impl.MncModelPackageImpl#getResponse()
		 * @generated
		 */
		EClass RESPONSE = eINSTANCE.getResponse();

		/**
		 * The meta object literal for the '{@link mncModel.impl.EventImpl <em>Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.EventImpl
		 * @see mncModel.impl.MncModelPackageImpl#getEvent()
		 * @generated
		 */
		EClass EVENT = eINSTANCE.getEvent();

		/**
		 * The meta object literal for the '<em><b>Publish</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVENT__PUBLISH = eINSTANCE.getEvent_Publish();

		/**
		 * The meta object literal for the '{@link mncModel.impl.AlarmImpl <em>Alarm</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.AlarmImpl
		 * @see mncModel.impl.MncModelPackageImpl#getAlarm()
		 * @generated
		 */
		EClass ALARM = eINSTANCE.getAlarm();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ALARM__TYPE = eINSTANCE.getAlarm_Type();

		/**
		 * The meta object literal for the '<em><b>Level</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ALARM__LEVEL = eINSTANCE.getAlarm_Level();

		/**
		 * The meta object literal for the '<em><b>Publish</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ALARM__PUBLISH = eINSTANCE.getAlarm_Publish();

		/**
		 * The meta object literal for the '{@link mncModel.impl.DataPointImpl <em>Data Point</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.DataPointImpl
		 * @see mncModel.impl.MncModelPackageImpl#getDataPoint()
		 * @generated
		 */
		EClass DATA_POINT = eINSTANCE.getDataPoint();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATA_POINT__TYPE = eINSTANCE.getDataPoint_Type();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATA_POINT__VALUE = eINSTANCE.getDataPoint_Value();

		/**
		 * The meta object literal for the '<em><b>Publish</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATA_POINT__PUBLISH = eINSTANCE.getDataPoint_Publish();

		/**
		 * The meta object literal for the '{@link mncModel.impl.SubscribableItemListImpl <em>Subscribable Item List</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.SubscribableItemListImpl
		 * @see mncModel.impl.MncModelPackageImpl#getSubscribableItemList()
		 * @generated
		 */
		EClass SUBSCRIBABLE_ITEM_LIST = eINSTANCE.getSubscribableItemList();

		/**
		 * The meta object literal for the '<em><b>Subscribed Events</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_EVENTS = eINSTANCE.getSubscribableItemList_SubscribedEvents();

		/**
		 * The meta object literal for the '<em><b>Subscribed Alarms</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_ALARMS = eINSTANCE.getSubscribableItemList_SubscribedAlarms();

		/**
		 * The meta object literal for the '<em><b>Subscribed Data Points</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_DATA_POINTS = eINSTANCE.getSubscribableItemList_SubscribedDataPoints();

		/**
		 * The meta object literal for the '{@link mncModel.impl.ControlNodeImpl <em>Control Node</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.ControlNodeImpl
		 * @see mncModel.impl.MncModelPackageImpl#getControlNode()
		 * @generated
		 */
		EClass CONTROL_NODE = eINSTANCE.getControlNode();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTROL_NODE__NAME = eINSTANCE.getControlNode_Name();

		/**
		 * The meta object literal for the '<em><b>Interface Description</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTROL_NODE__INTERFACE_DESCRIPTION = eINSTANCE.getControlNode_InterfaceDescription();

		/**
		 * The meta object literal for the '<em><b>Child Nodes</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTROL_NODE__CHILD_NODES = eINSTANCE.getControlNode_ChildNodes();

		/**
		 * The meta object literal for the '<em><b>Parent Node</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTROL_NODE__PARENT_NODE = eINSTANCE.getControlNode_ParentNode();

		/**
		 * The meta object literal for the '<em><b>Command Response Blocks</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTROL_NODE__COMMAND_RESPONSE_BLOCKS = eINSTANCE.getControlNode_CommandResponseBlocks();

		/**
		 * The meta object literal for the '<em><b>Event Blocks</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTROL_NODE__EVENT_BLOCKS = eINSTANCE.getControlNode_EventBlocks();

		/**
		 * The meta object literal for the '<em><b>Alarm Blocks</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTROL_NODE__ALARM_BLOCKS = eINSTANCE.getControlNode_AlarmBlocks();

		/**
		 * The meta object literal for the '<em><b>Data Point Blocks</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTROL_NODE__DATA_POINT_BLOCKS = eINSTANCE.getControlNode_DataPointBlocks();

		/**
		 * The meta object literal for the '{@link mncModel.impl.CommandResponseBlockImpl <em>Command Response Block</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.CommandResponseBlockImpl
		 * @see mncModel.impl.MncModelPackageImpl#getCommandResponseBlock()
		 * @generated
		 */
		EClass COMMAND_RESPONSE_BLOCK = eINSTANCE.getCommandResponseBlock();

		/**
		 * The meta object literal for the '<em><b>Command</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMMAND_RESPONSE_BLOCK__COMMAND = eINSTANCE.getCommandResponseBlock_Command();

		/**
		 * The meta object literal for the '<em><b>Response Block</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMMAND_RESPONSE_BLOCK__RESPONSE_BLOCK = eINSTANCE.getCommandResponseBlock_ResponseBlock();

		/**
		 * The meta object literal for the '{@link mncModel.impl.ResponseBlockImpl <em>Response Block</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.ResponseBlockImpl
		 * @see mncModel.impl.MncModelPackageImpl#getResponseBlock()
		 * @generated
		 */
		EClass RESPONSE_BLOCK = eINSTANCE.getResponseBlock();

		/**
		 * The meta object literal for the '<em><b>Response</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESPONSE_BLOCK__RESPONSE = eINSTANCE.getResponseBlock_Response();

		/**
		 * The meta object literal for the '<em><b>Response Aggregation Rules</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESPONSE_BLOCK__RESPONSE_AGGREGATION_RULES = eINSTANCE.getResponseBlock_ResponseAggregationRules();

		/**
		 * The meta object literal for the '{@link mncModel.impl.ResponseAggregationRuleImpl <em>Response Aggregation Rule</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.ResponseAggregationRuleImpl
		 * @see mncModel.impl.MncModelPackageImpl#getResponseAggregationRule()
		 * @generated
		 */
		EClass RESPONSE_AGGREGATION_RULE = eINSTANCE.getResponseAggregationRule();

		/**
		 * The meta object literal for the '<em><b>Input Responses</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESPONSE_AGGREGATION_RULE__INPUT_RESPONSES = eINSTANCE.getResponseAggregationRule_InputResponses();

		/**
		 * The meta object literal for the '<em><b>Parameter Translations</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESPONSE_AGGREGATION_RULE__PARAMETER_TRANSLATIONS = eINSTANCE.getResponseAggregationRule_ParameterTranslations();

		/**
		 * The meta object literal for the '{@link mncModel.impl.ParameterTranslationImpl <em>Parameter Translation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.ParameterTranslationImpl
		 * @see mncModel.impl.MncModelPackageImpl#getParameterTranslation()
		 * @generated
		 */
		EClass PARAMETER_TRANSLATION = eINSTANCE.getParameterTranslation();

		/**
		 * The meta object literal for the '<em><b>Input Parameters</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PARAMETER_TRANSLATION__INPUT_PARAMETERS = eINSTANCE.getParameterTranslation_InputParameters();

		/**
		 * The meta object literal for the '<em><b>Transformation Function</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PARAMETER_TRANSLATION__TRANSFORMATION_FUNCTION = eINSTANCE.getParameterTranslation_TransformationFunction();

		/**
		 * The meta object literal for the '<em><b>Translated Parameters</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PARAMETER_TRANSLATION__TRANSLATED_PARAMETERS = eINSTANCE.getParameterTranslation_TranslatedParameters();

		/**
		 * The meta object literal for the '{@link mncModel.impl.EventBlockImpl <em>Event Block</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.EventBlockImpl
		 * @see mncModel.impl.MncModelPackageImpl#getEventBlock()
		 * @generated
		 */
		EClass EVENT_BLOCK = eINSTANCE.getEventBlock();

		/**
		 * The meta object literal for the '<em><b>Event</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EVENT_BLOCK__EVENT = eINSTANCE.getEventBlock_Event();

		/**
		 * The meta object literal for the '{@link mncModel.impl.AlarmBlockImpl <em>Alarm Block</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.AlarmBlockImpl
		 * @see mncModel.impl.MncModelPackageImpl#getAlarmBlock()
		 * @generated
		 */
		EClass ALARM_BLOCK = eINSTANCE.getAlarmBlock();

		/**
		 * The meta object literal for the '<em><b>Alarm</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ALARM_BLOCK__ALARM = eINSTANCE.getAlarmBlock_Alarm();

		/**
		 * The meta object literal for the '{@link mncModel.impl.DataPointBlockImpl <em>Data Point Block</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.DataPointBlockImpl
		 * @see mncModel.impl.MncModelPackageImpl#getDataPointBlock()
		 * @generated
		 */
		EClass DATA_POINT_BLOCK = eINSTANCE.getDataPointBlock();

		/**
		 * The meta object literal for the '<em><b>Data Point</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATA_POINT_BLOCK__DATA_POINT = eINSTANCE.getDataPointBlock_DataPoint();

		/**
		 * The meta object literal for the '{@link mncModel.impl.CheckParameterConditionImpl <em>Check Parameter Condition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.CheckParameterConditionImpl
		 * @see mncModel.impl.MncModelPackageImpl#getCheckParameterCondition()
		 * @generated
		 */
		EClass CHECK_PARAMETER_CONDITION = eINSTANCE.getCheckParameterCondition();

		/**
		 * The meta object literal for the '<em><b>Parameter</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CHECK_PARAMETER_CONDITION__PARAMETER = eINSTANCE.getCheckParameterCondition_Parameter();

		/**
		 * The meta object literal for the '<em><b>Check Values</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CHECK_PARAMETER_CONDITION__CHECK_VALUES = eINSTANCE.getCheckParameterCondition_CheckValues();

		/**
		 * The meta object literal for the '<em><b>Check Max Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CHECK_PARAMETER_CONDITION__CHECK_MAX_VALUE = eINSTANCE.getCheckParameterCondition_CheckMaxValue();

		/**
		 * The meta object literal for the '<em><b>Check Min Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CHECK_PARAMETER_CONDITION__CHECK_MIN_VALUE = eINSTANCE.getCheckParameterCondition_CheckMinValue();

		/**
		 * The meta object literal for the '<em><b>Operations</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CHECK_PARAMETER_CONDITION__OPERATIONS = eINSTANCE.getCheckParameterCondition_Operations();

		/**
		 * The meta object literal for the '{@link mncModel.impl.TransitionImpl <em>Transition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.TransitionImpl
		 * @see mncModel.impl.MncModelPackageImpl#getTransition()
		 * @generated
		 */
		EClass TRANSITION = eINSTANCE.getTransition();

		/**
		 * The meta object literal for the '<em><b>Current State</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TRANSITION__CURRENT_STATE = eINSTANCE.getTransition_CurrentState();

		/**
		 * The meta object literal for the '<em><b>Next State</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TRANSITION__NEXT_STATE = eINSTANCE.getTransition_NextState();

		/**
		 * The meta object literal for the '<em><b>Entry Action</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TRANSITION__ENTRY_ACTION = eINSTANCE.getTransition_EntryAction();

		/**
		 * The meta object literal for the '<em><b>Exit Action</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TRANSITION__EXIT_ACTION = eINSTANCE.getTransition_ExitAction();

		/**
		 * The meta object literal for the '{@link mncModel.impl.ActionImpl <em>Action</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.ActionImpl
		 * @see mncModel.impl.MncModelPackageImpl#getAction()
		 * @generated
		 */
		EClass ACTION = eINSTANCE.getAction();

		/**
		 * The meta object literal for the '<em><b>Fire Command</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTION__FIRE_COMMAND = eINSTANCE.getAction_FireCommand();

		/**
		 * The meta object literal for the '<em><b>Publish Event</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTION__PUBLISH_EVENT = eINSTANCE.getAction_PublishEvent();

		/**
		 * The meta object literal for the '<em><b>Raise Alarm</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTION__RAISE_ALARM = eINSTANCE.getAction_RaiseAlarm();

		/**
		 * The meta object literal for the '<em><b>Trigger Data Point</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTION__TRIGGER_DATA_POINT = eINSTANCE.getAction_TriggerDataPoint();

		/**
		 * The meta object literal for the '<em><b>Execute Operation</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTION__EXECUTE_OPERATION = eINSTANCE.getAction_ExecuteOperation();

		/**
		 * The meta object literal for the '<em><b>Transition States</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTION__TRANSITION_STATES = eINSTANCE.getAction_TransitionStates();

		/**
		 * The meta object literal for the '{@link mncModel.impl.ActionItemImpl <em>Action Item</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.ActionItemImpl
		 * @see mncModel.impl.MncModelPackageImpl#getActionItem()
		 * @generated
		 */
		EClass ACTION_ITEM = eINSTANCE.getActionItem();

		/**
		 * The meta object literal for the '<em><b>Action Paremeter</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTION_ITEM__ACTION_PAREMETER = eINSTANCE.getActionItem_ActionParemeter();

		/**
		 * The meta object literal for the '{@link mncModel.impl.ActionParemeterImpl <em>Action Paremeter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.ActionParemeterImpl
		 * @see mncModel.impl.MncModelPackageImpl#getActionParemeter()
		 * @generated
		 */
		EClass ACTION_PAREMETER = eINSTANCE.getActionParemeter();

		/**
		 * The meta object literal for the '<em><b>Parameter Mappings</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTION_PAREMETER__PARAMETER_MAPPINGS = eINSTANCE.getActionParemeter_ParameterMappings();

		/**
		 * The meta object literal for the '<em><b>Parameter Values</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTION_PAREMETER__PARAMETER_VALUES = eINSTANCE.getActionParemeter_ParameterValues();

		/**
		 * The meta object literal for the '{@link mncModel.impl.ActionCommandImpl <em>Action Command</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.ActionCommandImpl
		 * @see mncModel.impl.MncModelPackageImpl#getActionCommand()
		 * @generated
		 */
		EClass ACTION_COMMAND = eINSTANCE.getActionCommand();

		/**
		 * The meta object literal for the '<em><b>Command</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTION_COMMAND__COMMAND = eINSTANCE.getActionCommand_Command();

		/**
		 * The meta object literal for the '<em><b>Response Handling</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTION_COMMAND__RESPONSE_HANDLING = eINSTANCE.getActionCommand_ResponseHandling();

		/**
		 * The meta object literal for the '{@link mncModel.impl.ActionEventImpl <em>Action Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.ActionEventImpl
		 * @see mncModel.impl.MncModelPackageImpl#getActionEvent()
		 * @generated
		 */
		EClass ACTION_EVENT = eINSTANCE.getActionEvent();

		/**
		 * The meta object literal for the '<em><b>Event</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTION_EVENT__EVENT = eINSTANCE.getActionEvent_Event();

		/**
		 * The meta object literal for the '{@link mncModel.impl.ActionAlarmImpl <em>Action Alarm</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.ActionAlarmImpl
		 * @see mncModel.impl.MncModelPackageImpl#getActionAlarm()
		 * @generated
		 */
		EClass ACTION_ALARM = eINSTANCE.getActionAlarm();

		/**
		 * The meta object literal for the '<em><b>Alarm</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTION_ALARM__ALARM = eINSTANCE.getActionAlarm_Alarm();

		/**
		 * The meta object literal for the '{@link mncModel.impl.ActionDataPointImpl <em>Action Data Point</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.ActionDataPointImpl
		 * @see mncModel.impl.MncModelPackageImpl#getActionDataPoint()
		 * @generated
		 */
		EClass ACTION_DATA_POINT = eINSTANCE.getActionDataPoint();

		/**
		 * The meta object literal for the '<em><b>Data Point</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTION_DATA_POINT__DATA_POINT = eINSTANCE.getActionDataPoint_DataPoint();

		/**
		 * The meta object literal for the '{@link mncModel.impl.ActionOperationImpl <em>Action Operation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.ActionOperationImpl
		 * @see mncModel.impl.MncModelPackageImpl#getActionOperation()
		 * @generated
		 */
		EClass ACTION_OPERATION = eINSTANCE.getActionOperation();

		/**
		 * The meta object literal for the '<em><b>Operation</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTION_OPERATION__OPERATION = eINSTANCE.getActionOperation_Operation();

		/**
		 * The meta object literal for the '{@link mncModel.impl.ValidationImpl <em>Validation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.ValidationImpl
		 * @see mncModel.impl.MncModelPackageImpl#getValidation()
		 * @generated
		 */
		EClass VALIDATION = eINSTANCE.getValidation();

		/**
		 * The meta object literal for the '<em><b>On Success Action</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VALIDATION__ON_SUCCESS_ACTION = eINSTANCE.getValidation_OnSuccessAction();

		/**
		 * The meta object literal for the '<em><b>On Failed Action</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VALIDATION__ON_FAILED_ACTION = eINSTANCE.getValidation_OnFailedAction();

		/**
		 * The meta object literal for the '<em><b>Parameters Validation Rules</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VALIDATION__PARAMETERS_VALIDATION_RULES = eINSTANCE.getValidation_ParametersValidationRules();

		/**
		 * The meta object literal for the '{@link mncModel.impl.DataValueImpl <em>Data Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.impl.DataValueImpl
		 * @see mncModel.impl.MncModelPackageImpl#getDataValue()
		 * @generated
		 */
		EClass DATA_VALUE = eINSTANCE.getDataValue();

	}

} //MncModelPackage
