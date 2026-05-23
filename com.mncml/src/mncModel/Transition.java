/**
 */
package mncModel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Transition</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mncModel.Transition#getCurrentState <em>Current State</em>}</li>
 *   <li>{@link mncModel.Transition#getNextState <em>Next State</em>}</li>
 *   <li>{@link mncModel.Transition#getEntryAction <em>Entry Action</em>}</li>
 *   <li>{@link mncModel.Transition#getExitAction <em>Exit Action</em>}</li>
 * </ul>
 *
 * @see mncModel.MncModelPackage#getTransition()
 * @model
 * @generated
 */
public interface Transition extends EObject {
	/**
	 * Returns the value of the '<em><b>Current State</b></em>' reference list.
	 * The list contents are of type {@link mncModel.OperatingState}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Current State</em>' reference list.
	 * @see mncModel.MncModelPackage#getTransition_CurrentState()
	 * @model
	 * @generated
	 */
	EList<OperatingState> getCurrentState();

	/**
	 * Returns the value of the '<em><b>Next State</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Next State</em>' reference.
	 * @see #setNextState(OperatingState)
	 * @see mncModel.MncModelPackage#getTransition_NextState()
	 * @model required="true"
	 * @generated
	 */
	OperatingState getNextState();

	/**
	 * Sets the value of the '{@link mncModel.Transition#getNextState <em>Next State</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Next State</em>' reference.
	 * @see #getNextState()
	 * @generated
	 */
	void setNextState(OperatingState value);

	/**
	 * Returns the value of the '<em><b>Entry Action</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Entry Action</em>' containment reference.
	 * @see #setEntryAction(Action)
	 * @see mncModel.MncModelPackage#getTransition_EntryAction()
	 * @model containment="true"
	 * @generated
	 */
	Action getEntryAction();

	/**
	 * Sets the value of the '{@link mncModel.Transition#getEntryAction <em>Entry Action</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Entry Action</em>' containment reference.
	 * @see #getEntryAction()
	 * @generated
	 */
	void setEntryAction(Action value);

	/**
	 * Returns the value of the '<em><b>Exit Action</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Exit Action</em>' containment reference.
	 * @see #setExitAction(Action)
	 * @see mncModel.MncModelPackage#getTransition_ExitAction()
	 * @model containment="true"
	 * @generated
	 */
	Action getExitAction();

	/**
	 * Sets the value of the '{@link mncModel.Transition#getExitAction <em>Exit Action</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Exit Action</em>' containment reference.
	 * @see #getExitAction()
	 * @generated
	 */
	void setExitAction(Action value);

} // Transition
