/**
 */
package CapabilityDescription.impl;

import CapabilityDescription.CapabilitiesOutcome;
import CapabilityDescription.Capability;
import CapabilityDescription.CapabilityDescriptionFactory;
import CapabilityDescription.CapabilityDescriptionPackage;
import CapabilityDescription.ControlCapabilities;
import dataModelPackage.DataModelPackage;

import mncModel.MncModelPackage;

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
public class CapabilityDescriptionPackageImpl extends EPackageImpl implements CapabilityDescriptionPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass capabilityEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass controlCapabilitiesEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass capabilitiesOutcomeEClass = null;

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
	 * @see CapabilityDescription.CapabilityDescriptionPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private CapabilityDescriptionPackageImpl() {
		super(eNS_URI, CapabilityDescriptionFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link CapabilityDescriptionPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static CapabilityDescriptionPackage init() {
		if (isInited) return (CapabilityDescriptionPackage)EPackage.Registry.INSTANCE.getEPackage(CapabilityDescriptionPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredCapabilityDescriptionPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		CapabilityDescriptionPackageImpl theCapabilityDescriptionPackage = registeredCapabilityDescriptionPackage instanceof CapabilityDescriptionPackageImpl ? (CapabilityDescriptionPackageImpl)registeredCapabilityDescriptionPackage : new CapabilityDescriptionPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		DataModelPackage.eINSTANCE.eClass();
		MncModelPackage.eINSTANCE.eClass();
		OperationsDescriptionPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theCapabilityDescriptionPackage.createPackageContents();

		// Initialize created meta-data
		theCapabilityDescriptionPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theCapabilityDescriptionPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(CapabilityDescriptionPackage.eNS_URI, theCapabilityDescriptionPackage);
		return theCapabilityDescriptionPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getCapability() {
		return capabilityEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCapability_Name() {
		return (EAttribute)capabilityEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getCapability_ComponentInterface() {
		return (EReference)capabilityEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getCapability_RequiredINITProcess() {
		return (EReference)capabilityEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getCapability_ProvidesControlCapabilities() {
		return (EReference)capabilityEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getCapability_ProvidesOutcomes() {
		return (EReference)capabilityEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getControlCapabilities() {
		return controlCapabilitiesEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getControlCapabilities_Commands() {
		return (EReference)controlCapabilitiesEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getControlCapabilities_Events() {
		return (EReference)controlCapabilitiesEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getControlCapabilities_Alarms() {
		return (EReference)controlCapabilitiesEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getControlCapabilities_DataPoints() {
		return (EReference)controlCapabilitiesEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getControlCapabilities_Operation() {
		return (EReference)controlCapabilitiesEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getCapabilitiesOutcome() {
		return capabilitiesOutcomeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getCapabilitiesOutcome_Responses() {
		return (EReference)capabilitiesOutcomeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getCapabilitiesOutcome_Events() {
		return (EReference)capabilitiesOutcomeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getCapabilitiesOutcome_Alarms() {
		return (EReference)capabilitiesOutcomeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getCapabilitiesOutcome_DataPoints() {
		return (EReference)capabilitiesOutcomeEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CapabilityDescriptionFactory getCapabilityDescriptionFactory() {
		return (CapabilityDescriptionFactory)getEFactoryInstance();
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
		capabilityEClass = createEClass(CAPABILITY);
		createEAttribute(capabilityEClass, CAPABILITY__NAME);
		createEReference(capabilityEClass, CAPABILITY__COMPONENT_INTERFACE);
		createEReference(capabilityEClass, CAPABILITY__REQUIRED_INIT_PROCESS);
		createEReference(capabilityEClass, CAPABILITY__PROVIDES_CONTROL_CAPABILITIES);
		createEReference(capabilityEClass, CAPABILITY__PROVIDES_OUTCOMES);

		controlCapabilitiesEClass = createEClass(CONTROL_CAPABILITIES);
		createEReference(controlCapabilitiesEClass, CONTROL_CAPABILITIES__COMMANDS);
		createEReference(controlCapabilitiesEClass, CONTROL_CAPABILITIES__EVENTS);
		createEReference(controlCapabilitiesEClass, CONTROL_CAPABILITIES__ALARMS);
		createEReference(controlCapabilitiesEClass, CONTROL_CAPABILITIES__DATA_POINTS);
		createEReference(controlCapabilitiesEClass, CONTROL_CAPABILITIES__OPERATION);

		capabilitiesOutcomeEClass = createEClass(CAPABILITIES_OUTCOME);
		createEReference(capabilitiesOutcomeEClass, CAPABILITIES_OUTCOME__RESPONSES);
		createEReference(capabilitiesOutcomeEClass, CAPABILITIES_OUTCOME__EVENTS);
		createEReference(capabilitiesOutcomeEClass, CAPABILITIES_OUTCOME__ALARMS);
		createEReference(capabilitiesOutcomeEClass, CAPABILITIES_OUTCOME__DATA_POINTS);
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
		MncModelPackage theMncModelPackage = (MncModelPackage)EPackage.Registry.INSTANCE.getEPackage(MncModelPackage.eNS_URI);
		OperationsDescriptionPackage theOperationsDescriptionPackage = (OperationsDescriptionPackage)EPackage.Registry.INSTANCE.getEPackage(OperationsDescriptionPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes, features, and operations; add parameters
		initEClass(capabilityEClass, Capability.class, "Capability", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCapability_Name(), ecorePackage.getEString(), "name", null, 0, 1, Capability.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCapability_ComponentInterface(), theMncModelPackage.getInterfaceDescription(), null, "componentInterface", null, 0, -1, Capability.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCapability_RequiredINITProcess(), theMncModelPackage.getAction(), null, "requiredINITProcess", null, 0, 1, Capability.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCapability_ProvidesControlCapabilities(), this.getControlCapabilities(), null, "providesControlCapabilities", null, 0, 1, Capability.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCapability_ProvidesOutcomes(), this.getCapabilitiesOutcome(), null, "providesOutcomes", null, 0, 1, Capability.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(controlCapabilitiesEClass, ControlCapabilities.class, "ControlCapabilities", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getControlCapabilities_Commands(), theMncModelPackage.getCommand(), null, "commands", null, 0, -1, ControlCapabilities.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getControlCapabilities_Events(), theMncModelPackage.getEvent(), null, "events", null, 0, -1, ControlCapabilities.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getControlCapabilities_Alarms(), theMncModelPackage.getAlarm(), null, "alarms", null, 0, -1, ControlCapabilities.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getControlCapabilities_DataPoints(), theMncModelPackage.getDataPoint(), null, "dataPoints", null, 0, -1, ControlCapabilities.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getControlCapabilities_Operation(), theOperationsDescriptionPackage.getOperation(), null, "operation", null, 0, -1, ControlCapabilities.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(capabilitiesOutcomeEClass, CapabilitiesOutcome.class, "CapabilitiesOutcome", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCapabilitiesOutcome_Responses(), theMncModelPackage.getResponse(), null, "responses", null, 0, -1, CapabilitiesOutcome.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCapabilitiesOutcome_Events(), theMncModelPackage.getEvent(), null, "events", null, 0, -1, CapabilitiesOutcome.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCapabilitiesOutcome_Alarms(), theMncModelPackage.getAlarm(), null, "alarms", null, 0, -1, CapabilitiesOutcome.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCapabilitiesOutcome_DataPoints(), theMncModelPackage.getDataPoint(), null, "dataPoints", null, 0, -1, CapabilitiesOutcome.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //CapabilityDescriptionPackageImpl
