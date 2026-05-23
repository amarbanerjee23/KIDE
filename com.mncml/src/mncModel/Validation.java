/**
 */
package mncModel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Validation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.Validation#getOnSuccessAction <em>On Success Action</em>}</li>
 *   <li>{@link mncModel.Validation#getOnFailedAction <em>On Failed Action</em>}</li>
 *   <li>{@link mncModel.Validation#getParametersValidationRules <em>Parameters Validation Rules</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getValidation()
 * @model
 * @generated
 */
public interface Validation extends EObject {
	/**
	 * Returns the value of the '<em><b>On Success Action</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>On Success Action</em>' containment reference.
	 * @see #setOnSuccessAction(Action)
	 * @see mncModel.MncModelPackage#getValidation_OnSuccessAction()
	 * @model containment="true"
	 * @generated
	 */
	Action getOnSuccessAction();

	/**
	 * Sets the value of the '{@link mncModel.Validation#getOnSuccessAction <em>On Success Action</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>On Success Action</em>' containment reference.
	 * @see #getOnSuccessAction()
	 * @generated
	 */
	void setOnSuccessAction(Action value);

	/**
	 * Returns the value of the '<em><b>On Failed Action</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>On Failed Action</em>' containment reference.
	 * @see #setOnFailedAction(Action)
	 * @see mncModel.MncModelPackage#getValidation_OnFailedAction()
	 * @model containment="true"
	 * @generated
	 */
	Action getOnFailedAction();

	/**
	 * Sets the value of the '{@link mncModel.Validation#getOnFailedAction <em>On Failed Action</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>On Failed Action</em>' containment reference.
	 * @see #getOnFailedAction()
	 * @generated
	 */
	void setOnFailedAction(Action value);

	/**
	 * Returns the value of the '<em><b>Parameters Validation Rules</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.CheckParameterCondition}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameters Validation Rules</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getValidation_ParametersValidationRules()
	 * @model containment="true"
	 * @generated
	 */
	EList<CheckParameterCondition> getParametersValidationRules();

} // Validation
