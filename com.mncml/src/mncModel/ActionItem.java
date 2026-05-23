/**
 */
package mncModel;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Action Item</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.ActionItem#getActionParemeter <em>Action Paremeter</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getActionItem()
 * @model
 * @generated
 */
public interface ActionItem extends EObject {
	/**
	 * Returns the value of the '<em><b>Action Paremeter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Action Paremeter</em>' containment reference.
	 * @see #setActionParemeter(ActionParemeter)
	 * @see mncModel.MncModelPackage#getActionItem_ActionParemeter()
	 * @model containment="true"
	 * @generated
	 */
	ActionParemeter getActionParemeter();

	/**
	 * Sets the value of the '{@link mncModel.ActionItem#getActionParemeter <em>Action Paremeter</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Action Paremeter</em>' containment reference.
	 * @see #getActionParemeter()
	 * @generated
	 */
	void setActionParemeter(ActionParemeter value);

} // ActionItem
