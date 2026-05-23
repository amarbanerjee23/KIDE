/**
 */
package activityDiagramModel.impl;

import CapabilityDescription.CapabilityDescriptionPackage;

import activityDiagramModel.Activity;
import activityDiagramModel.ActivityDiagram;
import activityDiagramModel.ActivityDiagramModelFactory;
import activityDiagramModel.ActivityDiagramModelPackage;
import activityDiagramModel.ConditionalActivity;
import activityDiagramModel.Outcome;
import activityDiagramModel.UnitTime;

import dataModelPackage.DataModelPackage;

import mncModel.MncModelPackage;

import operationsDescription.OperationsDescriptionPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ActivityDiagramModelPackageImpl extends EPackageImpl implements ActivityDiagramModelPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass activityDiagramEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass activityEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass conditionalActivityEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass outcomeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum unitTimeEEnum = null;

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
	 * @see activityDiagramModel.ActivityDiagramModelPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ActivityDiagramModelPackageImpl() {
		super(eNS_URI, ActivityDiagramModelFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link ActivityDiagramModelPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ActivityDiagramModelPackage init() {
		if (isInited) return (ActivityDiagramModelPackage)EPackage.Registry.INSTANCE.getEPackage(ActivityDiagramModelPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredActivityDiagramModelPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		ActivityDiagramModelPackageImpl theActivityDiagramModelPackage = registeredActivityDiagramModelPackage instanceof ActivityDiagramModelPackageImpl ? (ActivityDiagramModelPackageImpl)registeredActivityDiagramModelPackage : new ActivityDiagramModelPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		CapabilityDescriptionPackage.eINSTANCE.eClass();
		DataModelPackage.eINSTANCE.eClass();
		MncModelPackage.eINSTANCE.eClass();
		OperationsDescriptionPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theActivityDiagramModelPackage.createPackageContents();

		// Initialize created meta-data
		theActivityDiagramModelPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theActivityDiagramModelPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(ActivityDiagramModelPackage.eNS_URI, theActivityDiagramModelPackage);
		return theActivityDiagramModelPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getActivityDiagram() {
		return activityDiagramEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getActivityDiagram_Name() {
		return (EAttribute)activityDiagramEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivityDiagram_Activities() {
		return (EReference)activityDiagramEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivityDiagram_DataObjects() {
		return (EReference)activityDiagramEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivityDiagram_Results() {
		return (EReference)activityDiagramEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivityDiagram_ContextDataModel() {
		return (EReference)activityDiagramEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getActivityDiagram_PhysicalContext() {
		return (EAttribute)activityDiagramEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getActivity() {
		return activityEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getActivity_Name() {
		return (EAttribute)activityEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivity_BindCapability() {
		return (EReference)activityEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getActivity_Description() {
		return (EAttribute)activityEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivity_ConditionalActivity() {
		return (EReference)activityEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivity_UseControlCapabilities() {
		return (EReference)activityEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivity_RequiresOperation() {
		return (EReference)activityEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivity_ChildActivityDiagram() {
		return (EReference)activityEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivity_InputParameters() {
		return (EReference)activityEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivity_InterruptedBy() {
		return (EReference)activityEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivity_Interrupts() {
		return (EReference)activityEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getActivity_Time() {
		return (EAttribute)activityEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getActivity_Unit() {
		return (EAttribute)activityEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivity_NextActivity() {
		return (EReference)activityEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivity_NextActivityDiagram() {
		return (EReference)activityEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getActivity_RequiredCapability() {
		return (EAttribute)activityEClass.getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getConditionalActivity() {
		return conditionalActivityEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getConditionalActivity_OnTrueNextActivity() {
		return (EReference)conditionalActivityEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getConditionalActivity_OnFalseNextActivity() {
		return (EReference)conditionalActivityEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getConditionalActivity_OnTrueFinalResult() {
		return (EReference)conditionalActivityEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getConditionalActivity_OnFalseFinalResult() {
		return (EReference)conditionalActivityEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getConditionalActivity_Outcome() {
		return (EReference)conditionalActivityEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getConditionalActivity_BOp() {
		return (EAttribute)conditionalActivityEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getOutcome() {
		return outcomeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOutcome_Name() {
		return (EAttribute)outcomeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOutcome_CapabilityOutcome() {
		return (EReference)outcomeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOutcome_OutcomeValidation() {
		return (EReference)outcomeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getUnitTime() {
		return unitTimeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ActivityDiagramModelFactory getActivityDiagramModelFactory() {
		return (ActivityDiagramModelFactory)getEFactoryInstance();
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
		activityDiagramEClass = createEClass(ACTIVITY_DIAGRAM);
		createEAttribute(activityDiagramEClass, ACTIVITY_DIAGRAM__NAME);
		createEReference(activityDiagramEClass, ACTIVITY_DIAGRAM__ACTIVITIES);
		createEReference(activityDiagramEClass, ACTIVITY_DIAGRAM__DATA_OBJECTS);
		createEReference(activityDiagramEClass, ACTIVITY_DIAGRAM__RESULTS);
		createEReference(activityDiagramEClass, ACTIVITY_DIAGRAM__CONTEXT_DATA_MODEL);
		createEAttribute(activityDiagramEClass, ACTIVITY_DIAGRAM__PHYSICAL_CONTEXT);

		activityEClass = createEClass(ACTIVITY);
		createEAttribute(activityEClass, ACTIVITY__NAME);
		createEReference(activityEClass, ACTIVITY__BIND_CAPABILITY);
		createEAttribute(activityEClass, ACTIVITY__DESCRIPTION);
		createEReference(activityEClass, ACTIVITY__CONDITIONAL_ACTIVITY);
		createEReference(activityEClass, ACTIVITY__USE_CONTROL_CAPABILITIES);
		createEReference(activityEClass, ACTIVITY__REQUIRES_OPERATION);
		createEReference(activityEClass, ACTIVITY__CHILD_ACTIVITY_DIAGRAM);
		createEReference(activityEClass, ACTIVITY__INPUT_PARAMETERS);
		createEReference(activityEClass, ACTIVITY__INTERRUPTED_BY);
		createEReference(activityEClass, ACTIVITY__INTERRUPTS);
		createEAttribute(activityEClass, ACTIVITY__TIME);
		createEAttribute(activityEClass, ACTIVITY__UNIT);
		createEReference(activityEClass, ACTIVITY__NEXT_ACTIVITY);
		createEReference(activityEClass, ACTIVITY__NEXT_ACTIVITY_DIAGRAM);
		createEAttribute(activityEClass, ACTIVITY__REQUIRED_CAPABILITY);

		conditionalActivityEClass = createEClass(CONDITIONAL_ACTIVITY);
		createEReference(conditionalActivityEClass, CONDITIONAL_ACTIVITY__ON_TRUE_NEXT_ACTIVITY);
		createEReference(conditionalActivityEClass, CONDITIONAL_ACTIVITY__ON_FALSE_NEXT_ACTIVITY);
		createEReference(conditionalActivityEClass, CONDITIONAL_ACTIVITY__ON_TRUE_FINAL_RESULT);
		createEReference(conditionalActivityEClass, CONDITIONAL_ACTIVITY__ON_FALSE_FINAL_RESULT);
		createEReference(conditionalActivityEClass, CONDITIONAL_ACTIVITY__OUTCOME);
		createEAttribute(conditionalActivityEClass, CONDITIONAL_ACTIVITY__BOP);

		outcomeEClass = createEClass(OUTCOME);
		createEAttribute(outcomeEClass, OUTCOME__NAME);
		createEReference(outcomeEClass, OUTCOME__CAPABILITY_OUTCOME);
		createEReference(outcomeEClass, OUTCOME__OUTCOME_VALIDATION);

		// Create enums
		unitTimeEEnum = createEEnum(UNIT_TIME);
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
		DataModelPackage theDataModelPackage = (DataModelPackage)EPackage.Registry.INSTANCE.getEPackage(DataModelPackage.eNS_URI);
		CapabilityDescriptionPackage theCapabilityDescriptionPackage = (CapabilityDescriptionPackage)EPackage.Registry.INSTANCE.getEPackage(CapabilityDescriptionPackage.eNS_URI);
		MncModelPackage theMncModelPackage = (MncModelPackage)EPackage.Registry.INSTANCE.getEPackage(MncModelPackage.eNS_URI);
		OperationsDescriptionPackage theOperationsDescriptionPackage = (OperationsDescriptionPackage)EPackage.Registry.INSTANCE.getEPackage(OperationsDescriptionPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes, features, and operations; add parameters
		initEClass(activityDiagramEClass, ActivityDiagram.class, "ActivityDiagram", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getActivityDiagram_Name(), ecorePackage.getEString(), "name", null, 0, 1, ActivityDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getActivityDiagram_Activities(), this.getActivity(), null, "activities", null, 0, -1, ActivityDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getActivityDiagram_DataObjects(), theDataModelPackage.getParameter(), null, "dataObjects", null, 0, -1, ActivityDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getActivityDiagram_Results(), theDataModelPackage.getParameter(), null, "results", null, 0, -1, ActivityDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getActivityDiagram_ContextDataModel(), theDataModelPackage.getParameter(), null, "contextDataModel", null, 0, -1, ActivityDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getActivityDiagram_PhysicalContext(), ecorePackage.getEString(), "physicalContext", null, 0, -1, ActivityDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(activityEClass, Activity.class, "Activity", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getActivity_Name(), ecorePackage.getEString(), "name", null, 0, 1, Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getActivity_BindCapability(), theCapabilityDescriptionPackage.getCapability(), null, "bindCapability", null, 0, 1, Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getActivity_Description(), ecorePackage.getEString(), "description", null, 0, 1, Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getActivity_ConditionalActivity(), this.getConditionalActivity(), null, "conditionalActivity", null, 0, -1, Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getActivity_UseControlCapabilities(), theMncModelPackage.getAbstractInterfaceItems(), null, "useControlCapabilities", null, 0, -1, Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getActivity_RequiresOperation(), theOperationsDescriptionPackage.getOperation(), null, "requiresOperation", null, 0, -1, Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getActivity_ChildActivityDiagram(), this.getActivityDiagram(), null, "childActivityDiagram", null, 0, 1, Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getActivity_InputParameters(), theDataModelPackage.getParameter(), null, "inputParameters", null, 0, -1, Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getActivity_InterruptedBy(), this.getActivity(), null, "interruptedBy", null, 0, -1, Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getActivity_Interrupts(), this.getActivity(), null, "interrupts", null, 0, -1, Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getActivity_Time(), ecorePackage.getEFloat(), "time", null, 0, 1, Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getActivity_Unit(), this.getUnitTime(), "unit", null, 0, 1, Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getActivity_NextActivity(), this.getActivity(), null, "nextActivity", null, 0, 1, Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getActivity_NextActivityDiagram(), this.getActivityDiagram(), null, "nextActivityDiagram", null, 0, 1, Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getActivity_RequiredCapability(), ecorePackage.getEString(), "requiredCapability", null, 0, 1, Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(conditionalActivityEClass, ConditionalActivity.class, "ConditionalActivity", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getConditionalActivity_OnTrueNextActivity(), this.getActivity(), null, "onTrueNextActivity", null, 0, 1, ConditionalActivity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getConditionalActivity_OnFalseNextActivity(), this.getActivity(), null, "onFalseNextActivity", null, 0, 1, ConditionalActivity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getConditionalActivity_OnTrueFinalResult(), theDataModelPackage.getParameter(), null, "onTrueFinalResult", null, 0, 1, ConditionalActivity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getConditionalActivity_OnFalseFinalResult(), theDataModelPackage.getParameter(), null, "onFalseFinalResult", null, 0, 1, ConditionalActivity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getConditionalActivity_Outcome(), this.getOutcome(), null, "outcome", null, 0, -1, ConditionalActivity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getConditionalActivity_BOp(), ecorePackage.getEString(), "bOp", null, 0, -1, ConditionalActivity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(outcomeEClass, Outcome.class, "Outcome", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getOutcome_Name(), ecorePackage.getEString(), "name", null, 0, 1, Outcome.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOutcome_CapabilityOutcome(), theMncModelPackage.getAbstractOutcomeItems(), null, "capabilityOutcome", null, 0, 1, Outcome.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOutcome_OutcomeValidation(), theMncModelPackage.getCheckParameterCondition(), null, "outcomeValidation", null, 0, -1, Outcome.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(unitTimeEEnum, UnitTime.class, "UnitTime");
		addEEnumLiteral(unitTimeEEnum, UnitTime.SECS);
		addEEnumLiteral(unitTimeEEnum, UnitTime.MINS);
		addEEnumLiteral(unitTimeEEnum, UnitTime.HRS);
		addEEnumLiteral(unitTimeEEnum, UnitTime.DAYS);

		// Create resource
		createResource(eNS_URI);
	}

} //ActivityDiagramModelPackageImpl
