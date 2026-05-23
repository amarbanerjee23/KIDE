/**
 */
package mncModel.impl;

import dataModelPackage.DataModelPackage;

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

import mncModel.utility.UtilityPackage;

import mncModel.utility.impl.UtilityPackageImpl;

import operationsDescription.OperationsDescriptionPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class MncModelPackageImpl extends EPackageImpl implements MncModelPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass abstractOutcomeItemsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass systemEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass behaviorBlockEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass abstractInterfaceItemsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass modelEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass importEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass interfaceDescriptionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass portEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass addressEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass commandEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass operatingStateEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass responseEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass alarmEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dataPointEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass subscribableItemListEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass controlNodeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass commandResponseBlockEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass responseBlockEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass responseAggregationRuleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass parameterTranslationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eventBlockEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass alarmBlockEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dataPointBlockEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass checkParameterConditionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass transitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass actionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass actionItemEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass actionParemeterEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass actionCommandEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass actionEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass actionAlarmEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass actionDataPointEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass actionOperationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass validationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dataValueEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see mncModel.MncModelPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private MncModelPackageImpl() {
		super(eNS_URI, MncModelFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link MncModelPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static MncModelPackage init() {
		if (isInited) return (MncModelPackage)EPackage.Registry.INSTANCE.getEPackage(MncModelPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredMncModelPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		MncModelPackageImpl theMncModelPackage = registeredMncModelPackage instanceof MncModelPackageImpl ? (MncModelPackageImpl)registeredMncModelPackage : new MncModelPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		DataModelPackage.eINSTANCE.eClass();
		OperationsDescriptionPackage.eINSTANCE.eClass();

		// Obtain or create and register interdependencies
		Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(UtilityPackage.eNS_URI);
		UtilityPackageImpl theUtilityPackage = (UtilityPackageImpl)(registeredPackage instanceof UtilityPackageImpl ? registeredPackage : UtilityPackage.eINSTANCE);

		// Create package meta-data objects
		theMncModelPackage.createPackageContents();
		theUtilityPackage.createPackageContents();

		// Initialize created meta-data
		theMncModelPackage.initializePackageContents();
		theUtilityPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theMncModelPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(MncModelPackage.eNS_URI, theMncModelPackage);
		return theMncModelPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAbstractOutcomeItems() {
		return abstractOutcomeItemsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSystem() {
		return systemEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getBehaviorBlock() {
		return behaviorBlockEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getBehaviorBlock_ValidationRules() {
		return (EReference)behaviorBlockEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getBehaviorBlock_Action() {
		return (EReference)behaviorBlockEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAbstractInterfaceItems() {
		return abstractInterfaceItemsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAbstractInterfaceItems_Name() {
		return (EAttribute)abstractInterfaceItemsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAbstractInterfaceItems_Parameters() {
		return (EReference)abstractInterfaceItemsEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getModel() {
		return modelEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getModel_Systems() {
		return (EReference)modelEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getModel_Name() {
		return (EAttribute)modelEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getModel_ImportSection() {
		return (EReference)modelEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getImport() {
		return importEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getImport_ImportedNamespace() {
		return (EAttribute)importEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getInterfaceDescription() {
		return interfaceDescriptionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getInterfaceDescription_Name() {
		return (EAttribute)interfaceDescriptionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInterfaceDescription_Ipaddress() {
		return (EReference)interfaceDescriptionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInterfaceDescription_Port() {
		return (EReference)interfaceDescriptionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInterfaceDescription_DataPoints() {
		return (EReference)interfaceDescriptionEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInterfaceDescription_Alarms() {
		return (EReference)interfaceDescriptionEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInterfaceDescription_Commands() {
		return (EReference)interfaceDescriptionEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInterfaceDescription_Events() {
		return (EReference)interfaceDescriptionEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInterfaceDescription_Responses() {
		return (EReference)interfaceDescriptionEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInterfaceDescription_OperatingStatesUtility() {
		return (EReference)interfaceDescriptionEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInterfaceDescription_SubscribedItems() {
		return (EReference)interfaceDescriptionEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInterfaceDescription_Uses() {
		return (EReference)interfaceDescriptionEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPort() {
		return portEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPort_Name() {
		return (EAttribute)portEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPort_Value() {
		return (EAttribute)portEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAddress() {
		return addressEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAddress_Ipaddress() {
		return (EAttribute)addressEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCommand() {
		return commandEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCommand_Asynch() {
		return (EAttribute)commandEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getOperatingState() {
		return operatingStateEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOperatingState_Name() {
		return (EAttribute)operatingStateEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOperatingState_Parameters() {
		return (EReference)operatingStateEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getResponse() {
		return responseEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEvent() {
		return eventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEvent_Publish() {
		return (EAttribute)eventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAlarm() {
		return alarmEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAlarm_Type() {
		return (EAttribute)alarmEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAlarm_Level() {
		return (EAttribute)alarmEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAlarm_Publish() {
		return (EAttribute)alarmEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDataPoint() {
		return dataPointEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDataPoint_Type() {
		return (EAttribute)dataPointEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDataPoint_Value() {
		return (EReference)dataPointEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDataPoint_Publish() {
		return (EAttribute)dataPointEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSubscribableItemList() {
		return subscribableItemListEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSubscribableItemList_SubscribedEvents() {
		return (EReference)subscribableItemListEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSubscribableItemList_SubscribedAlarms() {
		return (EReference)subscribableItemListEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSubscribableItemList_SubscribedDataPoints() {
		return (EReference)subscribableItemListEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getControlNode() {
		return controlNodeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getControlNode_Name() {
		return (EAttribute)controlNodeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getControlNode_InterfaceDescription() {
		return (EReference)controlNodeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getControlNode_ChildNodes() {
		return (EReference)controlNodeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getControlNode_ParentNode() {
		return (EReference)controlNodeEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getControlNode_CommandResponseBlocks() {
		return (EReference)controlNodeEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getControlNode_EventBlocks() {
		return (EReference)controlNodeEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getControlNode_AlarmBlocks() {
		return (EReference)controlNodeEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getControlNode_DataPointBlocks() {
		return (EReference)controlNodeEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCommandResponseBlock() {
		return commandResponseBlockEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCommandResponseBlock_Command() {
		return (EReference)commandResponseBlockEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCommandResponseBlock_ResponseBlock() {
		return (EReference)commandResponseBlockEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getResponseBlock() {
		return responseBlockEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResponseBlock_Response() {
		return (EReference)responseBlockEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResponseBlock_ResponseAggregationRules() {
		return (EReference)responseBlockEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getResponseAggregationRule() {
		return responseAggregationRuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResponseAggregationRule_InputResponses() {
		return (EReference)responseAggregationRuleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResponseAggregationRule_ParameterTranslations() {
		return (EReference)responseAggregationRuleEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getParameterTranslation() {
		return parameterTranslationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getParameterTranslation_InputParameters() {
		return (EReference)parameterTranslationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getParameterTranslation_TransformationFunction() {
		return (EReference)parameterTranslationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getParameterTranslation_TranslatedParameters() {
		return (EReference)parameterTranslationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEventBlock() {
		return eventBlockEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEventBlock_Event() {
		return (EReference)eventBlockEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAlarmBlock() {
		return alarmBlockEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAlarmBlock_Alarm() {
		return (EReference)alarmBlockEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDataPointBlock() {
		return dataPointBlockEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDataPointBlock_DataPoint() {
		return (EReference)dataPointBlockEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCheckParameterCondition() {
		return checkParameterConditionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCheckParameterCondition_Parameter() {
		return (EReference)checkParameterConditionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCheckParameterCondition_CheckValues() {
		return (EReference)checkParameterConditionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCheckParameterCondition_CheckMaxValue() {
		return (EReference)checkParameterConditionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCheckParameterCondition_CheckMinValue() {
		return (EReference)checkParameterConditionEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCheckParameterCondition_Operations() {
		return (EReference)checkParameterConditionEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTransition() {
		return transitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTransition_CurrentState() {
		return (EReference)transitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTransition_NextState() {
		return (EReference)transitionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTransition_EntryAction() {
		return (EReference)transitionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTransition_ExitAction() {
		return (EReference)transitionEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAction() {
		return actionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAction_FireCommand() {
		return (EReference)actionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAction_PublishEvent() {
		return (EReference)actionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAction_RaiseAlarm() {
		return (EReference)actionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAction_TriggerDataPoint() {
		return (EReference)actionEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAction_ExecuteOperation() {
		return (EReference)actionEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAction_TransitionStates() {
		return (EReference)actionEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getActionItem() {
		return actionItemEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActionItem_ActionParemeter() {
		return (EReference)actionItemEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getActionParemeter() {
		return actionParemeterEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActionParemeter_ParameterMappings() {
		return (EReference)actionParemeterEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActionParemeter_ParameterValues() {
		return (EReference)actionParemeterEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getActionCommand() {
		return actionCommandEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActionCommand_Command() {
		return (EReference)actionCommandEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActionCommand_ResponseHandling() {
		return (EReference)actionCommandEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getActionEvent() {
		return actionEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActionEvent_Event() {
		return (EReference)actionEventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getActionAlarm() {
		return actionAlarmEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActionAlarm_Alarm() {
		return (EReference)actionAlarmEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getActionDataPoint() {
		return actionDataPointEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActionDataPoint_DataPoint() {
		return (EReference)actionDataPointEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getActionOperation() {
		return actionOperationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActionOperation_Operation() {
		return (EReference)actionOperationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getValidation() {
		return validationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getValidation_OnSuccessAction() {
		return (EReference)validationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getValidation_OnFailedAction() {
		return (EReference)validationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getValidation_ParametersValidationRules() {
		return (EReference)validationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDataValue() {
		return dataValueEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MncModelFactory getMncModelFactory() {
		return (MncModelFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		abstractOutcomeItemsEClass = createEClass(ABSTRACT_OUTCOME_ITEMS);

		systemEClass = createEClass(SYSTEM);

		behaviorBlockEClass = createEClass(BEHAVIOR_BLOCK);
		createEReference(behaviorBlockEClass, BEHAVIOR_BLOCK__VALIDATION_RULES);
		createEReference(behaviorBlockEClass, BEHAVIOR_BLOCK__ACTION);

		abstractInterfaceItemsEClass = createEClass(ABSTRACT_INTERFACE_ITEMS);
		createEAttribute(abstractInterfaceItemsEClass, ABSTRACT_INTERFACE_ITEMS__NAME);
		createEReference(abstractInterfaceItemsEClass, ABSTRACT_INTERFACE_ITEMS__PARAMETERS);

		modelEClass = createEClass(MODEL);
		createEReference(modelEClass, MODEL__SYSTEMS);
		createEAttribute(modelEClass, MODEL__NAME);
		createEReference(modelEClass, MODEL__IMPORT_SECTION);

		importEClass = createEClass(IMPORT);
		createEAttribute(importEClass, IMPORT__IMPORTED_NAMESPACE);

		interfaceDescriptionEClass = createEClass(INTERFACE_DESCRIPTION);
		createEAttribute(interfaceDescriptionEClass, INTERFACE_DESCRIPTION__NAME);
		createEReference(interfaceDescriptionEClass, INTERFACE_DESCRIPTION__IPADDRESS);
		createEReference(interfaceDescriptionEClass, INTERFACE_DESCRIPTION__PORT);
		createEReference(interfaceDescriptionEClass, INTERFACE_DESCRIPTION__DATA_POINTS);
		createEReference(interfaceDescriptionEClass, INTERFACE_DESCRIPTION__ALARMS);
		createEReference(interfaceDescriptionEClass, INTERFACE_DESCRIPTION__COMMANDS);
		createEReference(interfaceDescriptionEClass, INTERFACE_DESCRIPTION__EVENTS);
		createEReference(interfaceDescriptionEClass, INTERFACE_DESCRIPTION__RESPONSES);
		createEReference(interfaceDescriptionEClass, INTERFACE_DESCRIPTION__OPERATING_STATES_UTILITY);
		createEReference(interfaceDescriptionEClass, INTERFACE_DESCRIPTION__SUBSCRIBED_ITEMS);
		createEReference(interfaceDescriptionEClass, INTERFACE_DESCRIPTION__USES);

		portEClass = createEClass(PORT);
		createEAttribute(portEClass, PORT__NAME);
		createEAttribute(portEClass, PORT__VALUE);

		addressEClass = createEClass(ADDRESS);
		createEAttribute(addressEClass, ADDRESS__IPADDRESS);

		commandEClass = createEClass(COMMAND);
		createEAttribute(commandEClass, COMMAND__ASYNCH);

		operatingStateEClass = createEClass(OPERATING_STATE);
		createEAttribute(operatingStateEClass, OPERATING_STATE__NAME);
		createEReference(operatingStateEClass, OPERATING_STATE__PARAMETERS);

		responseEClass = createEClass(RESPONSE);

		eventEClass = createEClass(EVENT);
		createEAttribute(eventEClass, EVENT__PUBLISH);

		alarmEClass = createEClass(ALARM);
		createEAttribute(alarmEClass, ALARM__TYPE);
		createEAttribute(alarmEClass, ALARM__LEVEL);
		createEAttribute(alarmEClass, ALARM__PUBLISH);

		dataPointEClass = createEClass(DATA_POINT);
		createEAttribute(dataPointEClass, DATA_POINT__TYPE);
		createEReference(dataPointEClass, DATA_POINT__VALUE);
		createEAttribute(dataPointEClass, DATA_POINT__PUBLISH);

		subscribableItemListEClass = createEClass(SUBSCRIBABLE_ITEM_LIST);
		createEReference(subscribableItemListEClass, SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_EVENTS);
		createEReference(subscribableItemListEClass, SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_ALARMS);
		createEReference(subscribableItemListEClass, SUBSCRIBABLE_ITEM_LIST__SUBSCRIBED_DATA_POINTS);

		controlNodeEClass = createEClass(CONTROL_NODE);
		createEAttribute(controlNodeEClass, CONTROL_NODE__NAME);
		createEReference(controlNodeEClass, CONTROL_NODE__INTERFACE_DESCRIPTION);
		createEReference(controlNodeEClass, CONTROL_NODE__CHILD_NODES);
		createEReference(controlNodeEClass, CONTROL_NODE__PARENT_NODE);
		createEReference(controlNodeEClass, CONTROL_NODE__COMMAND_RESPONSE_BLOCKS);
		createEReference(controlNodeEClass, CONTROL_NODE__EVENT_BLOCKS);
		createEReference(controlNodeEClass, CONTROL_NODE__ALARM_BLOCKS);
		createEReference(controlNodeEClass, CONTROL_NODE__DATA_POINT_BLOCKS);

		commandResponseBlockEClass = createEClass(COMMAND_RESPONSE_BLOCK);
		createEReference(commandResponseBlockEClass, COMMAND_RESPONSE_BLOCK__COMMAND);
		createEReference(commandResponseBlockEClass, COMMAND_RESPONSE_BLOCK__RESPONSE_BLOCK);

		responseBlockEClass = createEClass(RESPONSE_BLOCK);
		createEReference(responseBlockEClass, RESPONSE_BLOCK__RESPONSE);
		createEReference(responseBlockEClass, RESPONSE_BLOCK__RESPONSE_AGGREGATION_RULES);

		responseAggregationRuleEClass = createEClass(RESPONSE_AGGREGATION_RULE);
		createEReference(responseAggregationRuleEClass, RESPONSE_AGGREGATION_RULE__INPUT_RESPONSES);
		createEReference(responseAggregationRuleEClass, RESPONSE_AGGREGATION_RULE__PARAMETER_TRANSLATIONS);

		parameterTranslationEClass = createEClass(PARAMETER_TRANSLATION);
		createEReference(parameterTranslationEClass, PARAMETER_TRANSLATION__INPUT_PARAMETERS);
		createEReference(parameterTranslationEClass, PARAMETER_TRANSLATION__TRANSFORMATION_FUNCTION);
		createEReference(parameterTranslationEClass, PARAMETER_TRANSLATION__TRANSLATED_PARAMETERS);

		eventBlockEClass = createEClass(EVENT_BLOCK);
		createEReference(eventBlockEClass, EVENT_BLOCK__EVENT);

		alarmBlockEClass = createEClass(ALARM_BLOCK);
		createEReference(alarmBlockEClass, ALARM_BLOCK__ALARM);

		dataPointBlockEClass = createEClass(DATA_POINT_BLOCK);
		createEReference(dataPointBlockEClass, DATA_POINT_BLOCK__DATA_POINT);

		checkParameterConditionEClass = createEClass(CHECK_PARAMETER_CONDITION);
		createEReference(checkParameterConditionEClass, CHECK_PARAMETER_CONDITION__PARAMETER);
		createEReference(checkParameterConditionEClass, CHECK_PARAMETER_CONDITION__CHECK_VALUES);
		createEReference(checkParameterConditionEClass, CHECK_PARAMETER_CONDITION__CHECK_MAX_VALUE);
		createEReference(checkParameterConditionEClass, CHECK_PARAMETER_CONDITION__CHECK_MIN_VALUE);
		createEReference(checkParameterConditionEClass, CHECK_PARAMETER_CONDITION__OPERATIONS);

		transitionEClass = createEClass(TRANSITION);
		createEReference(transitionEClass, TRANSITION__CURRENT_STATE);
		createEReference(transitionEClass, TRANSITION__NEXT_STATE);
		createEReference(transitionEClass, TRANSITION__ENTRY_ACTION);
		createEReference(transitionEClass, TRANSITION__EXIT_ACTION);

		actionEClass = createEClass(ACTION);
		createEReference(actionEClass, ACTION__FIRE_COMMAND);
		createEReference(actionEClass, ACTION__PUBLISH_EVENT);
		createEReference(actionEClass, ACTION__RAISE_ALARM);
		createEReference(actionEClass, ACTION__TRIGGER_DATA_POINT);
		createEReference(actionEClass, ACTION__EXECUTE_OPERATION);
		createEReference(actionEClass, ACTION__TRANSITION_STATES);

		actionItemEClass = createEClass(ACTION_ITEM);
		createEReference(actionItemEClass, ACTION_ITEM__ACTION_PAREMETER);

		actionParemeterEClass = createEClass(ACTION_PAREMETER);
		createEReference(actionParemeterEClass, ACTION_PAREMETER__PARAMETER_MAPPINGS);
		createEReference(actionParemeterEClass, ACTION_PAREMETER__PARAMETER_VALUES);

		actionCommandEClass = createEClass(ACTION_COMMAND);
		createEReference(actionCommandEClass, ACTION_COMMAND__COMMAND);
		createEReference(actionCommandEClass, ACTION_COMMAND__RESPONSE_HANDLING);

		actionEventEClass = createEClass(ACTION_EVENT);
		createEReference(actionEventEClass, ACTION_EVENT__EVENT);

		actionAlarmEClass = createEClass(ACTION_ALARM);
		createEReference(actionAlarmEClass, ACTION_ALARM__ALARM);

		actionDataPointEClass = createEClass(ACTION_DATA_POINT);
		createEReference(actionDataPointEClass, ACTION_DATA_POINT__DATA_POINT);

		actionOperationEClass = createEClass(ACTION_OPERATION);
		createEReference(actionOperationEClass, ACTION_OPERATION__OPERATION);

		validationEClass = createEClass(VALIDATION);
		createEReference(validationEClass, VALIDATION__ON_SUCCESS_ACTION);
		createEReference(validationEClass, VALIDATION__ON_FAILED_ACTION);
		createEReference(validationEClass, VALIDATION__PARAMETERS_VALIDATION_RULES);

		dataValueEClass = createEClass(DATA_VALUE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		UtilityPackage theUtilityPackage = (UtilityPackage)EPackage.Registry.INSTANCE.getEPackage(UtilityPackage.eNS_URI);
		DataModelPackage theDataModelPackage = (DataModelPackage)EPackage.Registry.INSTANCE.getEPackage(DataModelPackage.eNS_URI);
		OperationsDescriptionPackage theOperationsDescriptionPackage = (OperationsDescriptionPackage)EPackage.Registry.INSTANCE.getEPackage(OperationsDescriptionPackage.eNS_URI);

		// Add subpackages
		getESubpackages().add(theUtilityPackage);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		interfaceDescriptionEClass.getESuperTypes().add(this.getSystem());
		commandEClass.getESuperTypes().add(this.getAbstractInterfaceItems());
		responseEClass.getESuperTypes().add(this.getAbstractInterfaceItems());
		responseEClass.getESuperTypes().add(this.getAbstractOutcomeItems());
		eventEClass.getESuperTypes().add(this.getAbstractInterfaceItems());
		eventEClass.getESuperTypes().add(this.getAbstractOutcomeItems());
		alarmEClass.getESuperTypes().add(this.getAbstractInterfaceItems());
		alarmEClass.getESuperTypes().add(this.getAbstractOutcomeItems());
		dataPointEClass.getESuperTypes().add(this.getAbstractInterfaceItems());
		dataPointEClass.getESuperTypes().add(this.getAbstractOutcomeItems());
		controlNodeEClass.getESuperTypes().add(this.getSystem());
		commandResponseBlockEClass.getESuperTypes().add(this.getBehaviorBlock());
		responseBlockEClass.getESuperTypes().add(this.getBehaviorBlock());
		eventBlockEClass.getESuperTypes().add(this.getBehaviorBlock());
		alarmBlockEClass.getESuperTypes().add(this.getBehaviorBlock());
		dataPointBlockEClass.getESuperTypes().add(this.getBehaviorBlock());
		actionCommandEClass.getESuperTypes().add(this.getActionItem());
		actionEventEClass.getESuperTypes().add(this.getActionItem());
		actionAlarmEClass.getESuperTypes().add(this.getActionItem());
		actionDataPointEClass.getESuperTypes().add(this.getActionItem());
		actionOperationEClass.getESuperTypes().add(this.getActionItem());

		// Initialize classes, features, and operations; add parameters
		initEClass(abstractOutcomeItemsEClass, AbstractOutcomeItems.class, "AbstractOutcomeItems", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(systemEClass, mncModel.System.class, "System", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(behaviorBlockEClass, BehaviorBlock.class, "BehaviorBlock", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getBehaviorBlock_ValidationRules(), this.getValidation(), null, "validationRules", null, 0, -1, BehaviorBlock.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getBehaviorBlock_Action(), this.getAction(), null, "action", null, 0, 1, BehaviorBlock.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(abstractInterfaceItemsEClass, AbstractInterfaceItems.class, "AbstractInterfaceItems", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAbstractInterfaceItems_Name(), ecorePackage.getEString(), "name", null, 0, 1, AbstractInterfaceItems.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAbstractInterfaceItems_Parameters(), theDataModelPackage.getParameter(), null, "parameters", null, 0, -1, AbstractInterfaceItems.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(modelEClass, Model.class, "Model", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getModel_Systems(), this.getSystem(), null, "systems", null, 0, -1, Model.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getModel_Name(), ecorePackage.getEString(), "name", null, 0, 1, Model.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getModel_ImportSection(), this.getImport(), null, "importSection", null, 0, -1, Model.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(importEClass, Import.class, "Import", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getImport_ImportedNamespace(), ecorePackage.getEString(), "importedNamespace", null, 0, 1, Import.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(interfaceDescriptionEClass, InterfaceDescription.class, "InterfaceDescription", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getInterfaceDescription_Name(), ecorePackage.getEString(), "name", null, 0, 1, InterfaceDescription.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getInterfaceDescription_Ipaddress(), this.getAddress(), null, "ipaddress", null, 0, 1, InterfaceDescription.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getInterfaceDescription_Port(), this.getPort(), null, "port", null, 0, 1, InterfaceDescription.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getInterfaceDescription_DataPoints(), this.getDataPoint(), null, "dataPoints", null, 0, -1, InterfaceDescription.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getInterfaceDescription_Alarms(), this.getAlarm(), null, "alarms", null, 0, -1, InterfaceDescription.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getInterfaceDescription_Commands(), this.getCommand(), null, "commands", null, 0, -1, InterfaceDescription.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getInterfaceDescription_Events(), this.getEvent(), null, "events", null, 0, -1, InterfaceDescription.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getInterfaceDescription_Responses(), this.getResponse(), null, "responses", null, 0, -1, InterfaceDescription.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getInterfaceDescription_OperatingStatesUtility(), theUtilityPackage.getOperatingStateUtility(), null, "operatingStatesUtility", null, 0, 1, InterfaceDescription.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getInterfaceDescription_SubscribedItems(), this.getSubscribableItemList(), null, "subscribedItems", null, 0, 1, InterfaceDescription.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getInterfaceDescription_Uses(), this.getInterfaceDescription(), null, "uses", null, 0, -1, InterfaceDescription.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(portEClass, Port.class, "Port", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPort_Name(), ecorePackage.getEString(), "name", null, 0, 1, Port.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPort_Value(), ecorePackage.getEInt(), "value", null, 0, 1, Port.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(addressEClass, Address.class, "Address", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAddress_Ipaddress(), ecorePackage.getEString(), "ipaddress", null, 0, 1, Address.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(commandEClass, Command.class, "Command", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCommand_Asynch(), ecorePackage.getEBoolean(), "asynch", null, 0, 1, Command.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(operatingStateEClass, OperatingState.class, "OperatingState", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getOperatingState_Name(), ecorePackage.getEString(), "name", null, 0, 1, OperatingState.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOperatingState_Parameters(), theDataModelPackage.getParameter(), null, "parameters", null, 0, -1, OperatingState.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(responseEClass, Response.class, "Response", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(eventEClass, Event.class, "Event", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEvent_Publish(), ecorePackage.getEBoolean(), "publish", null, 0, 1, Event.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(alarmEClass, Alarm.class, "Alarm", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAlarm_Type(), ecorePackage.getEString(), "type", null, 0, 1, Alarm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAlarm_Level(), ecorePackage.getEInt(), "level", null, 0, 1, Alarm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAlarm_Publish(), ecorePackage.getEBoolean(), "publish", null, 0, 1, Alarm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dataPointEClass, DataPoint.class, "DataPoint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDataPoint_Type(), theDataModelPackage.getPrimitiveValueType(), "type", null, 0, 1, DataPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDataPoint_Value(), theDataModelPackage.getPrimitiveValue(), null, "value", null, 0, 1, DataPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDataPoint_Publish(), ecorePackage.getEBoolean(), "publish", null, 0, 1, DataPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(subscribableItemListEClass, SubscribableItemList.class, "SubscribableItemList", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getSubscribableItemList_SubscribedEvents(), this.getEvent(), null, "subscribedEvents", null, 0, -1, SubscribableItemList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSubscribableItemList_SubscribedAlarms(), this.getAlarm(), null, "subscribedAlarms", null, 0, -1, SubscribableItemList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSubscribableItemList_SubscribedDataPoints(), this.getDataPoint(), null, "subscribedDataPoints", null, 0, -1, SubscribableItemList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(controlNodeEClass, ControlNode.class, "ControlNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getControlNode_Name(), ecorePackage.getEString(), "name", null, 0, 1, ControlNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getControlNode_InterfaceDescription(), this.getInterfaceDescription(), null, "interfaceDescription", null, 0, 1, ControlNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getControlNode_ChildNodes(), this.getControlNode(), this.getControlNode_ParentNode(), "childNodes", null, 0, -1, ControlNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getControlNode_ParentNode(), this.getControlNode(), this.getControlNode_ChildNodes(), "parentNode", null, 0, 1, ControlNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getControlNode_CommandResponseBlocks(), this.getCommandResponseBlock(), null, "commandResponseBlocks", null, 0, -1, ControlNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getControlNode_EventBlocks(), this.getEventBlock(), null, "eventBlocks", null, 0, -1, ControlNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getControlNode_AlarmBlocks(), this.getAlarmBlock(), null, "alarmBlocks", null, 0, -1, ControlNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getControlNode_DataPointBlocks(), this.getDataPointBlock(), null, "dataPointBlocks", null, 0, -1, ControlNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(commandResponseBlockEClass, CommandResponseBlock.class, "CommandResponseBlock", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCommandResponseBlock_Command(), this.getCommand(), null, "command", null, 0, 1, CommandResponseBlock.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCommandResponseBlock_ResponseBlock(), this.getResponseBlock(), null, "responseBlock", null, 0, -1, CommandResponseBlock.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(responseBlockEClass, ResponseBlock.class, "ResponseBlock", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getResponseBlock_Response(), this.getResponse(), null, "response", null, 0, 1, ResponseBlock.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getResponseBlock_ResponseAggregationRules(), this.getResponseAggregationRule(), null, "responseAggregationRules", null, 0, -1, ResponseBlock.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(responseAggregationRuleEClass, ResponseAggregationRule.class, "ResponseAggregationRule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getResponseAggregationRule_InputResponses(), this.getResponse(), null, "inputResponses", null, 0, -1, ResponseAggregationRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getResponseAggregationRule_ParameterTranslations(), this.getParameterTranslation(), null, "parameterTranslations", null, 0, -1, ResponseAggregationRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(parameterTranslationEClass, ParameterTranslation.class, "ParameterTranslation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getParameterTranslation_InputParameters(), theDataModelPackage.getParameter(), null, "inputParameters", null, 0, -1, ParameterTranslation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getParameterTranslation_TransformationFunction(), theOperationsDescriptionPackage.getOperation(), null, "transformationFunction", null, 0, 1, ParameterTranslation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getParameterTranslation_TranslatedParameters(), theDataModelPackage.getParameter(), null, "translatedParameters", null, 0, 1, ParameterTranslation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eventBlockEClass, EventBlock.class, "EventBlock", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getEventBlock_Event(), this.getEvent(), null, "event", null, 0, 1, EventBlock.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(alarmBlockEClass, AlarmBlock.class, "AlarmBlock", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getAlarmBlock_Alarm(), this.getAlarm(), null, "alarm", null, 0, 1, AlarmBlock.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dataPointBlockEClass, DataPointBlock.class, "DataPointBlock", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDataPointBlock_DataPoint(), this.getDataPoint(), null, "dataPoint", null, 0, -1, DataPointBlock.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(checkParameterConditionEClass, CheckParameterCondition.class, "CheckParameterCondition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCheckParameterCondition_Parameter(), theDataModelPackage.getParameter(), null, "parameter", null, 0, -1, CheckParameterCondition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCheckParameterCondition_CheckValues(), theDataModelPackage.getPrimitiveValue(), null, "checkValues", null, 0, -1, CheckParameterCondition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCheckParameterCondition_CheckMaxValue(), theDataModelPackage.getPrimitiveValue(), null, "checkMaxValue", null, 0, 1, CheckParameterCondition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCheckParameterCondition_CheckMinValue(), theDataModelPackage.getPrimitiveValue(), null, "checkMinValue", null, 0, 1, CheckParameterCondition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCheckParameterCondition_Operations(), theOperationsDescriptionPackage.getOperation(), null, "operations", null, 0, -1, CheckParameterCondition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(transitionEClass, Transition.class, "Transition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getTransition_CurrentState(), this.getOperatingState(), null, "currentState", null, 0, -1, Transition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getTransition_NextState(), this.getOperatingState(), null, "nextState", null, 1, 1, Transition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getTransition_EntryAction(), this.getAction(), null, "entryAction", null, 0, 1, Transition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getTransition_ExitAction(), this.getAction(), null, "exitAction", null, 0, 1, Transition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(actionEClass, Action.class, "Action", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getAction_FireCommand(), this.getActionCommand(), null, "fireCommand", null, 0, -1, Action.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAction_PublishEvent(), this.getActionEvent(), null, "publishEvent", null, 0, -1, Action.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAction_RaiseAlarm(), this.getActionAlarm(), null, "raiseAlarm", null, 0, -1, Action.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAction_TriggerDataPoint(), this.getActionDataPoint(), null, "triggerDataPoint", null, 0, -1, Action.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAction_ExecuteOperation(), this.getActionOperation(), null, "executeOperation", null, 0, -1, Action.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAction_TransitionStates(), this.getTransition(), null, "transitionStates", null, 0, -1, Action.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(actionItemEClass, ActionItem.class, "ActionItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getActionItem_ActionParemeter(), this.getActionParemeter(), null, "actionParemeter", null, 0, 1, ActionItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(actionParemeterEClass, ActionParemeter.class, "ActionParemeter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getActionParemeter_ParameterMappings(), this.getParameterTranslation(), null, "parameterMappings", null, 0, -1, ActionParemeter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getActionParemeter_ParameterValues(), theDataModelPackage.getPrimitiveValue(), null, "parameterValues", null, 0, -1, ActionParemeter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(actionCommandEClass, ActionCommand.class, "ActionCommand", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getActionCommand_Command(), this.getCommand(), null, "command", null, 0, 1, ActionCommand.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getActionCommand_ResponseHandling(), this.getResponseBlock(), null, "responseHandling", null, 0, -1, ActionCommand.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(actionEventEClass, ActionEvent.class, "ActionEvent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getActionEvent_Event(), this.getEvent(), null, "event", null, 0, 1, ActionEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(actionAlarmEClass, ActionAlarm.class, "ActionAlarm", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getActionAlarm_Alarm(), this.getAlarm(), null, "alarm", null, 0, 1, ActionAlarm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(actionDataPointEClass, ActionDataPoint.class, "ActionDataPoint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getActionDataPoint_DataPoint(), this.getDataPoint(), null, "dataPoint", null, 0, 1, ActionDataPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(actionOperationEClass, ActionOperation.class, "ActionOperation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getActionOperation_Operation(), theOperationsDescriptionPackage.getOperation(), null, "operation", null, 0, 1, ActionOperation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(validationEClass, Validation.class, "Validation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getValidation_OnSuccessAction(), this.getAction(), null, "onSuccessAction", null, 0, 1, Validation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getValidation_OnFailedAction(), this.getAction(), null, "onFailedAction", null, 0, 1, Validation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getValidation_ParametersValidationRules(), this.getCheckParameterCondition(), null, "parametersValidationRules", null, 0, -1, Validation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dataValueEClass, DataValue.class, "DataValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //MncModelPackageImpl
