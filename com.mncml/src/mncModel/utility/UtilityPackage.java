/**
 */
package mncModel.utility;

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
 * @see mncModel.utility.UtilityFactory
 * @model kind="package"
 * @generated
 */
public interface UtilityPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "utility";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://mncModel/utility/2.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "MncUtility";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	UtilityPackage eINSTANCE = mncModel.utility.impl.UtilityPackageImpl.init();

	/**
	 * The meta object id for the '{@link mncModel.utility.impl.OperatingStateUtilityImpl <em>Operating State Utility</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mncModel.utility.impl.OperatingStateUtilityImpl
	 * @see mncModel.utility.impl.UtilityPackageImpl#getOperatingStateUtility()
	 * @generated
	 */
	int OPERATING_STATE_UTILITY = 0;

	/**
	 * The feature id for the '<em><b>Operating States</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATING_STATE_UTILITY__OPERATING_STATES = 0;

	/**
	 * The feature id for the '<em><b>Start State</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATING_STATE_UTILITY__START_STATE = 1;

	/**
	 * The feature id for the '<em><b>End State</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATING_STATE_UTILITY__END_STATE = 2;

	/**
	 * The number of structural features of the '<em>Operating State Utility</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATING_STATE_UTILITY_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Operating State Utility</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATING_STATE_UTILITY_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link mncModel.utility.OperatingStateUtility <em>Operating State Utility</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Operating State Utility</em>'.
	 * @see mncModel.utility.OperatingStateUtility
	 * @generated
	 */
	EClass getOperatingStateUtility();

	/**
	 * Returns the meta object for the containment reference list '{@link mncModel.utility.OperatingStateUtility#getOperatingStates <em>Operating States</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Operating States</em>'.
	 * @see mncModel.utility.OperatingStateUtility#getOperatingStates()
	 * @see #getOperatingStateUtility()
	 * @generated
	 */
	EReference getOperatingStateUtility_OperatingStates();

	/**
	 * Returns the meta object for the reference list '{@link mncModel.utility.OperatingStateUtility#getStartState <em>Start State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Start State</em>'.
	 * @see mncModel.utility.OperatingStateUtility#getStartState()
	 * @see #getOperatingStateUtility()
	 * @generated
	 */
	EReference getOperatingStateUtility_StartState();

	/**
	 * Returns the meta object for the reference list '{@link mncModel.utility.OperatingStateUtility#getEndState <em>End State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>End State</em>'.
	 * @see mncModel.utility.OperatingStateUtility#getEndState()
	 * @see #getOperatingStateUtility()
	 * @generated
	 */
	EReference getOperatingStateUtility_EndState();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	UtilityFactory getUtilityFactory();

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
		 * The meta object literal for the '{@link mncModel.utility.impl.OperatingStateUtilityImpl <em>Operating State Utility</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mncModel.utility.impl.OperatingStateUtilityImpl
		 * @see mncModel.utility.impl.UtilityPackageImpl#getOperatingStateUtility()
		 * @generated
		 */
		EClass OPERATING_STATE_UTILITY = eINSTANCE.getOperatingStateUtility();

		/**
		 * The meta object literal for the '<em><b>Operating States</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPERATING_STATE_UTILITY__OPERATING_STATES = eINSTANCE.getOperatingStateUtility_OperatingStates();

		/**
		 * The meta object literal for the '<em><b>Start State</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPERATING_STATE_UTILITY__START_STATE = eINSTANCE.getOperatingStateUtility_StartState();

		/**
		 * The meta object literal for the '<em><b>End State</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPERATING_STATE_UTILITY__END_STATE = eINSTANCE.getOperatingStateUtility_EndState();

	}

} //UtilityPackage
