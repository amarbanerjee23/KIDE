/**
 */
package mncModel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Behavior Block</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.BehaviorBlock#getValidationRules <em>Validation Rules</em>}</li>
 *   <li>{@link mncModel.BehaviorBlock#getAction <em>Action</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getBehaviorBlock()
 * @model
 * @generated
 */
public interface BehaviorBlock extends EObject {
	/**
	 * Returns the value of the '<em><b>Validation Rules</b></em>' containment reference list.
	 * The list contents are of type {@link mncModel.Validation}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Validation Rules</em>' containment reference list.
	 * @see mncModel.MncModelPackage#getBehaviorBlock_ValidationRules()
	 * @model containment="true"
	 * @generated
	 */
	EList<Validation> getValidationRules();

	/**
	 * Returns the value of the '<em><b>Action</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Action</em>' containment reference.
	 * @see #setAction(Action)
	 * @see mncModel.MncModelPackage#getBehaviorBlock_Action()
	 * @model containment="true"
	 * @generated
	 */
	Action getAction();

	/**
	 * Sets the value of the '{@link mncModel.BehaviorBlock#getAction <em>Action</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Action</em>' containment reference.
	 * @see #getAction()
	 * @generated
	 */
	void setAction(Action value);

} // BehaviorBlock
