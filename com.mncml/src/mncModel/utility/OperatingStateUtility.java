/**
 */
package mncModel.utility;

import mncModel.OperatingState;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Operating State Utility</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.utility.OperatingStateUtility#getOperatingStates <em>Operating States</em>}</li>
 *   <li>{@link mncModel.utility.OperatingStateUtility#getStartState <em>Start State</em>}</li>
 *   <li>{@link mncModel.utility.OperatingStateUtility#getEndState <em>End State</em>}</li>
 * </ul>
 *
 * @see mncModel.utility.UtilityPackage#getOperatingStateUtility()
 * @model
 * @generated
 */
public interface OperatingStateUtility extends EObject {
	/**
	 * Returns the value of the '<em><b>Operating States</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.OperatingState}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Operating States</em>' containment reference list.
	 * @see mncModel.utility.UtilityPackage#getOperatingStateUtility_OperatingStates()
	 * @model containment="true"
	 * @generated
	 */
	EList<OperatingState> getOperatingStates();

	/**
	 * Returns the value of the '<em><b>Start State</b></em>' reference list.
	 * The list contents are of type {@link mncModel.OperatingState}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Start State</em>' reference list.
	 * @see mncModel.utility.UtilityPackage#getOperatingStateUtility_StartState()
	 * @model
	 * @generated
	 */
	EList<OperatingState> getStartState();

	/**
	 * Returns the value of the '<em><b>End State</b></em>' reference list.
	 * The list contents are of type {@link mncModel.OperatingState}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>End State</em>' reference list.
	 * @see mncModel.utility.UtilityPackage#getOperatingStateUtility_EndState()
	 * @model
	 * @generated
	 */
	EList<OperatingState> getEndState();

} // OperatingStateUtility
