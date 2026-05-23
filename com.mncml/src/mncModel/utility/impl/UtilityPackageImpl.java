/**
 */
package mncModel.utility.impl;

import dataModelPackage.DataModelPackage;

import mncModel.MncModelPackage;

import mncModel.impl.MncModelPackageImpl;

import mncModel.utility.OperatingStateUtility;
import mncModel.utility.UtilityFactory;
import mncModel.utility.UtilityPackage;

import operationsDescription.OperationsDescriptionPackage;

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
public class UtilityPackageImpl extends EPackageImpl implements UtilityPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass operatingStateUtilityEClass = null;

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
	 * @see mncModel.utility.UtilityPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private UtilityPackageImpl() {
		super(eNS_URI, UtilityFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link UtilityPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static UtilityPackage init() {
		if (isInited) return (UtilityPackage)EPackage.Registry.INSTANCE.getEPackage(UtilityPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredUtilityPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		UtilityPackageImpl theUtilityPackage = registeredUtilityPackage instanceof UtilityPackageImpl ? (UtilityPackageImpl)registeredUtilityPackage : new UtilityPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		DataModelPackage.eINSTANCE.eClass();
		OperationsDescriptionPackage.eINSTANCE.eClass();

		// Obtain or create and register interdependencies
		Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(MncModelPackage.eNS_URI);
		MncModelPackageImpl theMncModelPackage = (MncModelPackageImpl)(registeredPackage instanceof MncModelPackageImpl ? registeredPackage : MncModelPackage.eINSTANCE);

		// Create package meta-data objects
		theUtilityPackage.createPackageContents();
		theMncModelPackage.createPackageContents();

		// Initialize created meta-data
		theUtilityPackage.initializePackageContents();
		theMncModelPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theUtilityPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(UtilityPackage.eNS_URI, theUtilityPackage);
		return theUtilityPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getOperatingStateUtility() {
		return operatingStateUtilityEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOperatingStateUtility_OperatingStates() {
		return (EReference)operatingStateUtilityEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOperatingStateUtility_StartState() {
		return (EReference)operatingStateUtilityEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOperatingStateUtility_EndState() {
		return (EReference)operatingStateUtilityEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public UtilityFactory getUtilityFactory() {
		return (UtilityFactory)getEFactoryInstance();
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
		operatingStateUtilityEClass = createEClass(OPERATING_STATE_UTILITY);
		createEReference(operatingStateUtilityEClass, OPERATING_STATE_UTILITY__OPERATING_STATES);
		createEReference(operatingStateUtilityEClass, OPERATING_STATE_UTILITY__START_STATE);
		createEReference(operatingStateUtilityEClass, OPERATING_STATE_UTILITY__END_STATE);
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

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes, features, and operations; add parameters
		initEClass(operatingStateUtilityEClass, OperatingStateUtility.class, "OperatingStateUtility", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getOperatingStateUtility_OperatingStates(), theMncModelPackage.getOperatingState(), null, "operatingStates", null, 0, -1, OperatingStateUtility.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOperatingStateUtility_StartState(), theMncModelPackage.getOperatingState(), null, "startState", null, 0, -1, OperatingStateUtility.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOperatingStateUtility_EndState(), theMncModelPackage.getOperatingState(), null, "endState", null, 0, -1, OperatingStateUtility.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
	}

} //UtilityPackageImpl
