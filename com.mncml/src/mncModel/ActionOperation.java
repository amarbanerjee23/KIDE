/**
 */
package mncModel;

import operationsDescription.Operation;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Action Operation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.ActionOperation#getOperation <em>Operation</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getActionOperation()
 * @model
 * @generated
 */
public interface ActionOperation extends ActionItem {
	/**
	 * Returns the value of the '<em><b>Operation</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Operation</em>' reference.
	 * @see #setOperation(Operation)
	 * @see mncModel.MncModelPackage#getActionOperation_Operation()
	 * @model
	 * @generated
	 */
	Operation getOperation();

	/**
	 * Sets the value of the '{@link mncModel.ActionOperation#getOperation <em>Operation</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Operation</em>' reference.
	 * @see #getOperation()
	 * @generated
	 */
	void setOperation(Operation value);

} // ActionOperation
