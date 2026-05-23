/**
 */
package CapabilityDescription;

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
 * @see CapabilityDescription.CapabilityDescriptionFactory
 * @model kind="package"
 * @generated
 */
public interface CapabilityDescriptionPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "CapabilityDescription";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://CapabilityDescription/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "CapabilityDescription1.0";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	CapabilityDescriptionPackage eINSTANCE = CapabilityDescription.impl.CapabilityDescriptionPackageImpl.init();

	/**
	 * The meta object id for the '{@link CapabilityDescription.impl.CapabilityImpl <em>Capability</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see CapabilityDescription.impl.CapabilityImpl
	 * @see CapabilityDescription.impl.CapabilityDescriptionPackageImpl#getCapability()
	 * @generated
	 */
	int CAPABILITY = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CAPABILITY__NAME = 0;

	/**
	 * The feature id for the '<em><b>Component Interface</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CAPABILITY__COMPONENT_INTERFACE = 1;

	/**
	 * The feature id for the '<em><b>Required INIT Process</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CAPABILITY__REQUIRED_INIT_PROCESS = 2;

	/**
	 * The feature id for the '<em><b>Provides Control Capabilities</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CAPABILITY__PROVIDES_CONTROL_CAPABILITIES = 3;

	/**
	 * The feature id for the '<em><b>Provides Outcomes</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CAPABILITY__PROVIDES_OUTCOMES = 4;

	/**
	 * The number of structural features of the '<em>Capability</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CAPABILITY_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Capability</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CAPABILITY_OPERATION_COUNT = 0;


	/**
	 * The meta object id for the '{@link CapabilityDescription.impl.ControlCapabilitiesImpl <em>Control Capabilities</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see CapabilityDescription.impl.ControlCapabilitiesImpl
	 * @see CapabilityDescription.impl.CapabilityDescriptionPackageImpl#getControlCapabilities()
	 * @generated
	 */
	int CONTROL_CAPABILITIES = 1;

	/**
	 * The feature id for the '<em><b>Commands</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_CAPABILITIES__COMMANDS = 0;

	/**
	 * The feature id for the '<em><b>Events</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_CAPABILITIES__EVENTS = 1;

	/**
	 * The feature id for the '<em><b>Alarms</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_CAPABILITIES__ALARMS = 2;

	/**
	 * The feature id for the '<em><b>Data Points</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_CAPABILITIES__DATA_POINTS = 3;

	/**
	 * The feature id for the '<em><b>Operation</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_CAPABILITIES__OPERATION = 4;

	/**
	 * The number of structural features of the '<em>Control Capabilities</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_CAPABILITIES_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Control Capabilities</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_CAPABILITIES_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link CapabilityDescription.impl.CapabilitiesOutcomeImpl <em>Capabilities Outcome</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see CapabilityDescription.impl.CapabilitiesOutcomeImpl
	 * @see CapabilityDescription.impl.CapabilityDescriptionPackageImpl#getCapabilitiesOutcome()
	 * @generated
	 */
	int CAPABILITIES_OUTCOME = 2;

	/**
	 * The feature id for the '<em><b>Responses</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CAPABILITIES_OUTCOME__RESPONSES = 0;

	/**
	 * The feature id for the '<em><b>Events</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CAPABILITIES_OUTCOME__EVENTS = 1;

	/**
	 * The feature id for the '<em><b>Alarms</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CAPABILITIES_OUTCOME__ALARMS = 2;

	/**
	 * The feature id for the '<em><b>Data Points</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CAPABILITIES_OUTCOME__DATA_POINTS = 3;

	/**
	 * The number of structural features of the '<em>Capabilities Outcome</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CAPABILITIES_OUTCOME_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Capabilities Outcome</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CAPABILITIES_OUTCOME_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link CapabilityDescription.Capability <em>Capability</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Capability</em>'.
	 * @see CapabilityDescription.Capability
	 * @generated
	 */
	EClass getCapability();

	/**
	 * Returns the meta object for the attribute '{@link CapabilityDescription.Capability#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see CapabilityDescription.Capability#getName()
	 * @see #getCapability()
	 * @generated
	 */
	EAttribute getCapability_Name();

	/**
	 * Returns the meta object for the reference list '{@link CapabilityDescription.Capability#getComponentInterface <em>Component Interface</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Component Interface</em>'.
	 * @see CapabilityDescription.Capability#getComponentInterface()
	 * @see #getCapability()
	 * @generated
	 */
	EReference getCapability_ComponentInterface();

	/**
	 * Returns the meta object for the containment reference '{@link CapabilityDescription.Capability#getRequiredINITProcess <em>Required INIT Process</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Required INIT Process</em>'.
	 * @see CapabilityDescription.Capability#getRequiredINITProcess()
	 * @see #getCapability()
	 * @generated
	 */
	EReference getCapability_RequiredINITProcess();

	/**
	 * Returns the meta object for the containment reference '{@link CapabilityDescription.Capability#getProvidesControlCapabilities <em>Provides Control Capabilities</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Provides Control Capabilities</em>'.
	 * @see CapabilityDescription.Capability#getProvidesControlCapabilities()
	 * @see #getCapability()
	 * @generated
	 */
	EReference getCapability_ProvidesControlCapabilities();

	/**
	 * Returns the meta object for the containment reference '{@link CapabilityDescription.Capability#getProvidesOutcomes <em>Provides Outcomes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Provides Outcomes</em>'.
	 * @see CapabilityDescription.Capability#getProvidesOutcomes()
	 * @see #getCapability()
	 * @generated
	 */
	EReference getCapability_ProvidesOutcomes();

	/**
	 * Returns the meta object for class '{@link CapabilityDescription.ControlCapabilities <em>Control Capabilities</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Control Capabilities</em>'.
	 * @see CapabilityDescription.ControlCapabilities
	 * @generated
	 */
	EClass getControlCapabilities();

	/**
	 * Returns the meta object for the reference list '{@link CapabilityDescription.ControlCapabilities#getCommands <em>Commands</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Commands</em>'.
	 * @see CapabilityDescription.ControlCapabilities#getCommands()
	 * @see #getControlCapabilities()
	 * @generated
	 */
	EReference getControlCapabilities_Commands();

	/**
	 * Returns the meta object for the reference list '{@link CapabilityDescription.ControlCapabilities#getEvents <em>Events</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Events</em>'.
	 * @see CapabilityDescription.ControlCapabilities#getEvents()
	 * @see #getControlCapabilities()
	 * @generated
	 */
	EReference getControlCapabilities_Events();

	/**
	 * Returns the meta object for the reference list '{@link CapabilityDescription.ControlCapabilities#getAlarms <em>Alarms</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Alarms</em>'.
	 * @see CapabilityDescription.ControlCapabilities#getAlarms()
	 * @see #getControlCapabilities()
	 * @generated
	 */
	EReference getControlCapabilities_Alarms();

	/**
	 * Returns the meta object for the reference list '{@link CapabilityDescription.ControlCapabilities#getDataPoints <em>Data Points</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Data Points</em>'.
	 * @see CapabilityDescription.ControlCapabilities#getDataPoints()
	 * @see #getControlCapabilities()
	 * @generated
	 */
	EReference getControlCapabilities_DataPoints();

	/**
	 * Returns the meta object for the containment reference list '{@link CapabilityDescription.ControlCapabilities#getOperation <em>Operation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Operation</em>'.
	 * @see CapabilityDescription.ControlCapabilities#getOperation()
	 * @see #getControlCapabilities()
	 * @generated
	 */
	EReference getControlCapabilities_Operation();

	/**
	 * Returns the meta object for class '{@link CapabilityDescription.CapabilitiesOutcome <em>Capabilities Outcome</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Capabilities Outcome</em>'.
	 * @see CapabilityDescription.CapabilitiesOutcome
	 * @generated
	 */
	EClass getCapabilitiesOutcome();

	/**
	 * Returns the meta object for the reference list '{@link CapabilityDescription.CapabilitiesOutcome#getResponses <em>Responses</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Responses</em>'.
	 * @see CapabilityDescription.CapabilitiesOutcome#getResponses()
	 * @see #getCapabilitiesOutcome()
	 * @generated
	 */
	EReference getCapabilitiesOutcome_Responses();

	/**
	 * Returns the meta object for the reference list '{@link CapabilityDescription.CapabilitiesOutcome#getEvents <em>Events</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Events</em>'.
	 * @see CapabilityDescription.CapabilitiesOutcome#getEvents()
	 * @see #getCapabilitiesOutcome()
	 * @generated
	 */
	EReference getCapabilitiesOutcome_Events();

	/**
	 * Returns the meta object for the reference list '{@link CapabilityDescription.CapabilitiesOutcome#getAlarms <em>Alarms</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Alarms</em>'.
	 * @see CapabilityDescription.CapabilitiesOutcome#getAlarms()
	 * @see #getCapabilitiesOutcome()
	 * @generated
	 */
	EReference getCapabilitiesOutcome_Alarms();

	/**
	 * Returns the meta object for the reference list '{@link CapabilityDescription.CapabilitiesOutcome#getDataPoints <em>Data Points</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Data Points</em>'.
	 * @see CapabilityDescription.CapabilitiesOutcome#getDataPoints()
	 * @see #getCapabilitiesOutcome()
	 * @generated
	 */
	EReference getCapabilitiesOutcome_DataPoints();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	CapabilityDescriptionFactory getCapabilityDescriptionFactory();

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
		 * The meta object literal for the '{@link CapabilityDescription.impl.CapabilityImpl <em>Capability</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see CapabilityDescription.impl.CapabilityImpl
		 * @see CapabilityDescription.impl.CapabilityDescriptionPackageImpl#getCapability()
		 * @generated
		 */
		EClass CAPABILITY = eINSTANCE.getCapability();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CAPABILITY__NAME = eINSTANCE.getCapability_Name();

		/**
		 * The meta object literal for the '<em><b>Component Interface</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CAPABILITY__COMPONENT_INTERFACE = eINSTANCE.getCapability_ComponentInterface();

		/**
		 * The meta object literal for the '<em><b>Required INIT Process</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CAPABILITY__REQUIRED_INIT_PROCESS = eINSTANCE.getCapability_RequiredINITProcess();

		/**
		 * The meta object literal for the '<em><b>Provides Control Capabilities</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CAPABILITY__PROVIDES_CONTROL_CAPABILITIES = eINSTANCE.getCapability_ProvidesControlCapabilities();

		/**
		 * The meta object literal for the '<em><b>Provides Outcomes</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CAPABILITY__PROVIDES_OUTCOMES = eINSTANCE.getCapability_ProvidesOutcomes();

		/**
		 * The meta object literal for the '{@link CapabilityDescription.impl.ControlCapabilitiesImpl <em>Control Capabilities</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see CapabilityDescription.impl.ControlCapabilitiesImpl
		 * @see CapabilityDescription.impl.CapabilityDescriptionPackageImpl#getControlCapabilities()
		 * @generated
		 */
		EClass CONTROL_CAPABILITIES = eINSTANCE.getControlCapabilities();

		/**
		 * The meta object literal for the '<em><b>Commands</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTROL_CAPABILITIES__COMMANDS = eINSTANCE.getControlCapabilities_Commands();

		/**
		 * The meta object literal for the '<em><b>Events</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTROL_CAPABILITIES__EVENTS = eINSTANCE.getControlCapabilities_Events();

		/**
		 * The meta object literal for the '<em><b>Alarms</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTROL_CAPABILITIES__ALARMS = eINSTANCE.getControlCapabilities_Alarms();

		/**
		 * The meta object literal for the '<em><b>Data Points</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTROL_CAPABILITIES__DATA_POINTS = eINSTANCE.getControlCapabilities_DataPoints();

		/**
		 * The meta object literal for the '<em><b>Operation</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTROL_CAPABILITIES__OPERATION = eINSTANCE.getControlCapabilities_Operation();

		/**
		 * The meta object literal for the '{@link CapabilityDescription.impl.CapabilitiesOutcomeImpl <em>Capabilities Outcome</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see CapabilityDescription.impl.CapabilitiesOutcomeImpl
		 * @see CapabilityDescription.impl.CapabilityDescriptionPackageImpl#getCapabilitiesOutcome()
		 * @generated
		 */
		EClass CAPABILITIES_OUTCOME = eINSTANCE.getCapabilitiesOutcome();

		/**
		 * The meta object literal for the '<em><b>Responses</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CAPABILITIES_OUTCOME__RESPONSES = eINSTANCE.getCapabilitiesOutcome_Responses();

		/**
		 * The meta object literal for the '<em><b>Events</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CAPABILITIES_OUTCOME__EVENTS = eINSTANCE.getCapabilitiesOutcome_Events();

		/**
		 * The meta object literal for the '<em><b>Alarms</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CAPABILITIES_OUTCOME__ALARMS = eINSTANCE.getCapabilitiesOutcome_Alarms();

		/**
		 * The meta object literal for the '<em><b>Data Points</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CAPABILITIES_OUTCOME__DATA_POINTS = eINSTANCE.getCapabilitiesOutcome_DataPoints();

	}

} //CapabilityDescriptionPackage
